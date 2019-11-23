/**********************************************
 * Greed, a chess engine written in java.
 * Copyright (C) 2019 Ahmet Ba�ar
 * 
 * This file is part of Greed.
 * 
 * Greed is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Greed is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Greed.  If not, see <https://www.gnu.org/licenses/>.
 **********************************************/
package chess.engine;

import chess.engine.EngineConstants;
import chess.engine.TranspositionTable;

public class BoardV6 implements IBoard {
	
	private long[] bitboard;
	private byte[] pieces;
//	private long zobristKey;
	
	private int[] pushDiffs = { 8, 64 - 8 };
	private int[][] castlingRookSources = { { 0, 7 }, { 56, 63 } };
	private int[][] castlingRookTargets = { { 3, 5 }, { 59, 61 } };
	private byte[] kingPositions = { 4, 60 };
	private byte[][] rookPositions = { { 0, 7 }, { 56, 63 } };
	private int[] epSquareDiff = {-8, 8};
	
	//
	private static final int TEMP_BOARD_SIZE = 100;
	private static final int HALF_OF_TEMP_BOARD_SIZE = 50;
	
	private byte[] capturedPieces = new byte[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private int[] epTs = new int[TEMP_BOARD_SIZE]; // 25 ply? wtf!.
	private byte[][][] castlingRightss = new byte[TEMP_BOARD_SIZE][2][2]; // 25 ply? wtf!...
	private long[] zobristKeys = new long[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private long[] pawnZobristKeys = new long[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private int[] fiftyMoveCounters = new int[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private int[] nullMoveCounters = new int[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private int[][] moveLists = new int[TEMP_BOARD_SIZE][EngineConstants.MOVE_LIST_SIZE];
	//
	
	public byte capturedPiece;
	public int epT = 64; // 64 means no epTarget. because we assume 1L<<64 = 0
	public byte[][] castlingRights = new byte[][] { { 1, 1 }, { 1, 1 } };
	public long zobristKey;
	public long pawnZobristKey;
	public int fiftyMoveCounter;
	public int nullMoveCounter;
	public int[] moveList;
	
	public int depth;
	
	public int getEpTarget(int depth) {
		return epT;
	}
	
	public int[] getMoveList(int depth) {
		depth = convertToInternalDepth(depth);
		return moveLists[depth];
	}
	
	public BoardV6(long[] bitboard, byte[] pieces, int epT, int depth, byte[][] castlingRights, long zobristKey, int fiftyMoveCounter, long pawnZobristKey) {
		depth = convertToInternalDepth(depth);
		this.depth = depth;
		this.bitboard = bitboard;
		this.pieces = pieces;
		this.epT = epT;
		this.castlingRights[0][0] = castlingRights[0][0];
		this.castlingRights[0][1] = castlingRights[0][1];
		this.castlingRights[1][0] = castlingRights[1][0];
		this.castlingRights[1][1] = castlingRights[1][1];
		this.zobristKey = zobristKey;
		this.pawnZobristKey = pawnZobristKey;
		this.fiftyMoveCounter = fiftyMoveCounter;
		this.nullMoveCounter = fiftyMoveCounter; // Initially equals.
	}
	
	public void doNullMove(int depth, int side, int R) {
		
		storeCurrentValues();
		this.depth -= R;
		
		//Transposition Table//
		zobristKey = zobristKey ^ TranspositionTable.zobristBlackMove;
		
		if (epT != 64 && (EngineConstants.PAWN_ATTACK_LOOKUP[side ^ 1][epT] & bitboard[(side) | EngineConstants.PAWN]) != 0) {
			zobristKey = zobristKey ^ TranspositionTable.zobristEnPassantArray[epT];
		}

		fiftyMoveCounter++;
		nullMoveCounter = 0;
		
		epT = 64;
	}
	
	public void undoNullMove(int depth, int R) {
		this.depth += R;
		fetchPreviousValues();
	}
	
	private void storeCurrentValues() {
		capturedPieces[depth] = capturedPiece;
		epTs[depth] = epT;
		castlingRightss[depth][0][0] = castlingRights[0][0];
		castlingRightss[depth][0][1] = castlingRights[0][1];
		castlingRightss[depth][1][0] = castlingRights[1][0];
		castlingRightss[depth][1][1] = castlingRights[1][1];
		zobristKeys[depth] = zobristKey;
		pawnZobristKeys[depth] = pawnZobristKey;
		fiftyMoveCounters[depth] = fiftyMoveCounter;
		nullMoveCounters[depth] = nullMoveCounter;
		depth--;
	}
	
	private void fetchPreviousValues() {
		depth++;
		capturedPiece = capturedPieces[depth];
		epT = epTs[depth];
		castlingRights[0][0] = castlingRightss[depth][0][0];
		castlingRights[0][1] = castlingRightss[depth][0][1];
		castlingRights[1][0] = castlingRightss[depth][1][0];
		castlingRights[1][1] = castlingRightss[depth][1][1];
		zobristKey = zobristKeys[depth];
		pawnZobristKey = pawnZobristKeys[depth];
		fiftyMoveCounter = fiftyMoveCounters[depth];
		nullMoveCounter = nullMoveCounters[depth];
	}
	
	public void doMove(int move, int side, int opSide, int depth) {
		
		storeCurrentValues();
		
		int moveType = move & 0x00070000;
		int to = (move & 0x0000ff00) >>> 8;
		int from = move & 0x000000ff;
		byte fromPiece = pieces[from];
		
		boolean isRookOrKingMove = (fromPiece == (side | EngineConstants.KING) || (fromPiece == (side | EngineConstants.ROOK))) 
				  || (pieces[to] == ((opSide) | EngineConstants.ROOK)) || (pieces[to] == ((opSide) | EngineConstants.KING));
		
		//Transposition Table//
		zobristKey = zobristKey ^ TranspositionTable.zobristBlackMove;
		if (epT != 64 && (EngineConstants.PAWN_ATTACK_LOOKUP[side ^ 1][epT] & bitboard[(side) | EngineConstants.PAWN]) != 0) {
			zobristKey = zobristKey ^ TranspositionTable.zobristEnPassantArray[epT];
		}
		//
		
		switch (moveType) {
		case 0:
			
			epT = 64;
			capturedPiece = pieces[to];
			
			//Transposition Table//
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			if(capturedPiece > 0){
				zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[capturedPiece][to];
			}
			//
			
			byte fromPieceWc = (byte)(fromPiece & 0XFE);
			if (fromPieceWc == EngineConstants.PAWN) {
				pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
				pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			}
			
			byte capturedPieceWc = (byte)(capturedPiece & 0XFE);
			if (capturedPieceWc == EngineConstants.PAWN) {
				pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[capturedPiece][to];
			}
			
			pieces[from] = 0;
			pieces[to] = fromPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			bitboard[capturedPiece] &= ~(1L << to);
			break;
		case EngineConstants.DOUBLE_PUSH_SHIFTED:
			capturedPiece = 0;
			int diff = pushDiffs[side];
			epT = Long.numberOfTrailingZeros(((1L << to) >>> diff) | ((1L << to) << (64 - diff)));
			
			//Transposition Table//
			if ((EngineConstants.PAWN_ATTACK_LOOKUP[side][epT] & bitboard[(side ^ 1) | EngineConstants.PAWN]) != 0) {
				zobristKey = zobristKey ^ TranspositionTable.zobristEnPassantArray[epT];
			}
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			//
			
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			
			pieces[from] = 0;
			pieces[to] = fromPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			break;
		case EngineConstants.EP_CAPTURE_SHIFTED:
			
			if (epT == 64) {
				System.out.println("epT = " + epT);
			}
			
			int epS = to + epSquareDiff[side];
			capturedPiece = pieces[epS];
			
			//Transposition Table//
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[capturedPiece][epS];
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			//
			
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[capturedPiece][epS];
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			
			pieces[epS] = 0;
			pieces[from] = 0;
			pieces[to] = fromPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			bitboard[capturedPiece] &= ~(1L << epS);
			epT = 64;
			break;
		case EngineConstants.PROMOTION_SHIFTED:
			
			epT = 64;
			capturedPiece = pieces[to];
			byte promotedPiece = Move.getPromotedPiece(move);
			
			//Transposition Table//
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[promotedPiece][to];
			if(capturedPiece > 0) {
				zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[capturedPiece][to];
			}
			//
			
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			
			pieces[from] = 0;
			pieces[to] = promotedPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[promotedPiece] |= (1L << to);
			bitboard[capturedPiece] &= ~(1L << to);
			break;
		default:
			capturedPiece = 0;
			epT = 64;
			int castlingSide = (move & 0x00010000) >>> 16;
			byte sideToRook = (byte) (side | EngineConstants.ROOK);
			
			int castlingRookFrom = castlingRookSources[side][castlingSide];
			int castlingRookTo = castlingRookTargets[side][castlingSide];
			
			//Transposition Table//
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[sideToRook][castlingRookFrom];
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[sideToRook][castlingRookTo];
			//

			pieces[from] = 0;
			pieces[to] = fromPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);

			pieces[castlingRookFrom] = 0;
			pieces[castlingRookTo] = sideToRook;
			bitboard[sideToRook] &= ~(1L << castlingRookFrom);
			bitboard[sideToRook] |= (1L << castlingRookTo);
			break;
		
		}

		if (isRookOrKingMove) {
			
			//
			//Transposition Table//
			byte whiteQueenSideCastlingRight = castlingRights[0][0];
			byte whiteKingSideCastlingRight = castlingRights[0][1];
			byte blackQueenSideCastlingRight = castlingRights[1][0];
			byte blackKingSideCastlingRight = castlingRights[1][1];
			//
			
			long tmpBitboard = bitboard[(side) | EngineConstants.KING];
			castlingRights[side][0] = (byte) (castlingRights[side][0] & (tmpBitboard >>> kingPositions[side]));
			castlingRights[side][1] = (byte) (castlingRights[side][1] & (tmpBitboard >>> kingPositions[side]));

			tmpBitboard = bitboard[(side) | EngineConstants.ROOK];
			castlingRights[side][0] = (byte) (castlingRights[side][0] & (tmpBitboard >>> rookPositions[side][0]));
			castlingRights[side][1] = (byte) (castlingRights[side][1] & (tmpBitboard >>> rookPositions[side][1]));
			
			tmpBitboard = bitboard[(opSide) | EngineConstants.KING];
			castlingRights[opSide][0] = (byte) (castlingRights[opSide][0] & (tmpBitboard >>> kingPositions[opSide]));
			castlingRights[opSide][1] = (byte) (castlingRights[opSide][1] & (tmpBitboard >>> kingPositions[opSide]));

			tmpBitboard = bitboard[(opSide) | EngineConstants.ROOK];
			castlingRights[opSide][0] = (byte) (castlingRights[opSide][0] & (tmpBitboard >>> rookPositions[opSide][0]));
			castlingRights[opSide][1] = (byte) (castlingRights[opSide][1] & (tmpBitboard >>> rookPositions[opSide][1]));
			
			//
			//Transposition Table//
			if(whiteQueenSideCastlingRight != castlingRights[0][0]){
				zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[0][0];
			}
			
			if(whiteKingSideCastlingRight != castlingRights[0][1]){
				zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[0][1];
			}
			
			if(blackQueenSideCastlingRight != castlingRights[1][0]){
				zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[1][0];
			}
			
			if(blackKingSideCastlingRight != castlingRights[1][1]){
				zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[1][1];
			}
			//
		}
		
		if (capturedPiece != 0 || fromPiece == (side | EngineConstants.PAWN)) {
			fiftyMoveCounter = 0;
		} else {
			fiftyMoveCounter ++;
		}
		
		nullMoveCounter ++;
	}
	
	public void undoMove(int move, int side, int opSide, int depth) {
		
		int moveType = move & 0x00070000;
		int to = (move & 0x0000ff00) >>> 8;
		int from = move & 0x000000ff;
		byte fromPiece = pieces[to];
		
		switch (moveType) {
		case 0:
			
			pieces[from] = fromPiece;
			pieces[to] = capturedPiece;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			bitboard[capturedPiece] |= (1L << to); // capturedPiece may be zero here.
			break;
		case EngineConstants.DOUBLE_PUSH_SHIFTED:
			
			pieces[from] = fromPiece;
			pieces[to] = 0;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			break;
		case EngineConstants.EP_CAPTURE_SHIFTED:
			
			int epS = to + epSquareDiff[side];
			
			pieces[epS] = capturedPiece;
			pieces[from] = fromPiece;
			pieces[to] = 0;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			bitboard[capturedPiece] |= (1L << epS);
			break;
		case EngineConstants.PROMOTION_SHIFTED:
			
			fromPiece = (byte)(side | EngineConstants.PAWN);
			pieces[from] = fromPiece;
			pieces[to] = capturedPiece;
			bitboard[fromPiece] |= (1L << from);
			bitboard[Move.getPromotedPiece(move)] &= ~(1L << to);
			bitboard[capturedPiece] |= (1L << to); // capturedPiece may be zero here. 
			break;
		default:
			
			pieces[from] = fromPiece;
			pieces[to] = 0;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			
			byte sideToRook = (byte) (side | EngineConstants.ROOK);
			int castlingSide = Move.getCastlingSide(move);
			int castlingRookFrom = castlingRookSources[side][castlingSide];
			int castlingRookTo = castlingRookTargets[side][castlingSide];
			
			pieces[castlingRookFrom] = sideToRook;
			pieces[castlingRookTo] = 0;
			bitboard[sideToRook] |= (1L << castlingRookFrom);
			bitboard[sideToRook] &= ~(1L << castlingRookTo);
			break;
		}
		
		fetchPreviousValues();
	}
	
	public void doMoveWithoutZobrist(int move, int side, int opSide, int depth) {
		
		storeCurrentValues();
		
		int moveType = move & 0x00070000;
		int to = (move & 0x0000ff00) >>> 8;
		int from = move & 0x000000ff;
		byte fromPiece = pieces[from];
		
		boolean isRookOrKingMove = (fromPiece == (side | EngineConstants.KING) || (fromPiece == (side | EngineConstants.ROOK))) 
				  || (pieces[to] == ((opSide) | EngineConstants.ROOK)) || (pieces[to] == ((opSide) | EngineConstants.KING));
		
		
		switch (moveType) {
		case 0:
			
			epT = 64;
			capturedPiece = pieces[to];
			
			byte fromPieceWc = (byte)(fromPiece & 0XFE);
			if (fromPieceWc == EngineConstants.PAWN) {
				pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
				pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			}
			
			byte capturedPieceWc = (byte)(capturedPiece & 0XFE);
			if (capturedPieceWc == EngineConstants.PAWN) {
				pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[capturedPiece][to];
			}
			
			pieces[from] = 0;
			pieces[to] = fromPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			bitboard[capturedPiece] &= ~(1L << to);
			break;
		case EngineConstants.DOUBLE_PUSH_SHIFTED:
			capturedPiece = 0;
			int diff = pushDiffs[side];
			epT = Long.numberOfTrailingZeros(((1L << to) >>> diff) | ((1L << to) << (64 - diff)));
			
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			
			pieces[from] = 0;
			pieces[to] = fromPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			break;
		case EngineConstants.EP_CAPTURE_SHIFTED:
			
			int epS = to + epSquareDiff[side];
			capturedPiece = pieces[epS];
			
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[capturedPiece][epS];
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			
			pieces[epS] = 0;
			pieces[from] = 0;
			pieces[to] = fromPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			bitboard[capturedPiece] &= ~(1L << epS);
			epT = 64;
			break;
		case EngineConstants.PROMOTION_SHIFTED:
			
			epT = 64;
			capturedPiece = pieces[to];
			byte promotedPiece = Move.getPromotedPiece(move);
			
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			
			pieces[from] = 0;
			pieces[to] = promotedPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[promotedPiece] |= (1L << to);
			bitboard[capturedPiece] &= ~(1L << to);
			break;
		default:
			capturedPiece = 0;
			epT = 64;
			int castlingSide = (move & 0x00010000) >>> 16;
			byte sideToRook = (byte) (side | EngineConstants.ROOK);
			
			int castlingRookFrom = castlingRookSources[side][castlingSide];
			int castlingRookTo = castlingRookTargets[side][castlingSide];
			
			pieces[from] = 0;
			pieces[to] = fromPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);

			pieces[castlingRookFrom] = 0;
			pieces[castlingRookTo] = sideToRook;
			bitboard[sideToRook] &= ~(1L << castlingRookFrom);
			bitboard[sideToRook] |= (1L << castlingRookTo);
			break;
		
		}

		if (isRookOrKingMove) {
			long tmpBitboard = bitboard[(side) | EngineConstants.KING];
			castlingRights[side][0] = (byte) (castlingRights[side][0] & (tmpBitboard >>> kingPositions[side]));
			castlingRights[side][1] = (byte) (castlingRights[side][1] & (tmpBitboard >>> kingPositions[side]));

			tmpBitboard = bitboard[(side) | EngineConstants.ROOK];
			castlingRights[side][0] = (byte) (castlingRights[side][0] & (tmpBitboard >>> rookPositions[side][0]));
			castlingRights[side][1] = (byte) (castlingRights[side][1] & (tmpBitboard >>> rookPositions[side][1]));
			
			tmpBitboard = bitboard[(opSide) | EngineConstants.KING];
			castlingRights[opSide][0] = (byte) (castlingRights[opSide][0] & (tmpBitboard >>> kingPositions[opSide]));
			castlingRights[opSide][1] = (byte) (castlingRights[opSide][1] & (tmpBitboard >>> kingPositions[opSide]));

			tmpBitboard = bitboard[(opSide) | EngineConstants.ROOK];
			castlingRights[opSide][0] = (byte) (castlingRights[opSide][0] & (tmpBitboard >>> rookPositions[opSide][0]));
			castlingRights[opSide][1] = (byte) (castlingRights[opSide][1] & (tmpBitboard >>> rookPositions[opSide][1]));
		}
		
		/****/
		// No need to increment fifty-move-counter here because this method is invoked only from quiescence search. 
		/****/
	}
	
	public void undoMoveWithoutZobrist(int move, int side, int opSide, int depth) {
		
		
		int moveType = move & 0x00070000;
		int to = (move & 0x0000ff00) >>> 8;
		int from = move & 0x000000ff;
		byte fromPiece = pieces[to];
		
		switch (moveType) {
		case 0:
			
			pieces[from] = fromPiece;
			pieces[to] = capturedPiece;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			bitboard[capturedPiece] |= (1L << to); // capturedPiece may be zero here.
			break;
		case EngineConstants.DOUBLE_PUSH_SHIFTED:
			
			pieces[from] = fromPiece;
			pieces[to] = 0;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			break;
		case EngineConstants.EP_CAPTURE_SHIFTED:
			
			int epS = to + epSquareDiff[side];
			
			pieces[epS] = capturedPiece;
			pieces[from] = fromPiece;
			pieces[to] = 0;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			bitboard[capturedPiece] |= (1L << epS);
			break;
		case EngineConstants.PROMOTION_SHIFTED:
			
			fromPiece = (byte)(side | EngineConstants.PAWN);
			pieces[from] = fromPiece;
			pieces[to] = capturedPiece;
			bitboard[fromPiece] |= (1L << from);
			bitboard[Move.getPromotedPiece(move)] &= ~(1L << to);
			bitboard[capturedPiece] |= (1L << to); // capturedPiece may be zero here. 
			break;
		default:
			
			pieces[from] = fromPiece;
			pieces[to] = 0;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			
			byte sideToRook = (byte) (side | EngineConstants.ROOK);
			int castlingSide = Move.getCastlingSide(move);
			int castlingRookFrom = castlingRookSources[side][castlingSide];
			int castlingRookTo = castlingRookTargets[side][castlingSide];
			
			pieces[castlingRookFrom] = sideToRook;
			pieces[castlingRookTo] = 0;
			bitboard[sideToRook] |= (1L << castlingRookFrom);
			bitboard[sideToRook] &= ~(1L << castlingRookTo);
			break;
		}
		
		/****/
		// No need to decrement fifty-move-counter here because this method is invoked only from quiescence search. 
		/****/
		
		fetchPreviousValues();
					
	}
	

	public byte[][] getCastlingRights(int depth) {
		return castlingRights;
	}
	
	public byte getCapturedPiece(int depth) {
		return capturedPiece;
	}

	public long[] getBitboard() {
		return bitboard;
	}

	public byte[] getPieces() {
		return pieces;
	}
	
	private int convertToInternalDepth(int depth) {
		return depth + HALF_OF_TEMP_BOARD_SIZE;
	}

	public long getZobristKey(int depth) {
		return zobristKey;
	}
	
	public long getPawnZobristKey(int depth) {
		return pawnZobristKey;
	}
	
	public int getFiftyMoveCounter(int depth) {
		return fiftyMoveCounter;
	}
	
	public int getNullMoveCounter(int depth) {
		return nullMoveCounter;
	}
	
	public boolean hasRepeated(long zobristKey, int depth) {
		depth = convertToInternalDepth(depth);
		
		// TODO Check (as well as fiftyMoveCounter > 99) whether the king is in check or not because of the FIDE rules.
		if (fiftyMoveCounter > 99) {
			return true;
		}
		
		int upperBound = Math.min(fiftyMoveCounter, nullMoveCounter);
		if (upperBound < 4) {
			return false;
		}
		
		upperBound += depth;
		
		for (int i = depth + 4; i <= upperBound && i < TEMP_BOARD_SIZE; i = i + 2) {
			if (zobristKeys[i] == zobristKey) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void deepDive(int depth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deeperDive(int depth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getEpSquare(int depth) {
		// TODO Auto-generated method stub
		return 0;
	}
}
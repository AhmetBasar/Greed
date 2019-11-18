/**********************************************
 * Greed, a chess engine written in java.
 * Copyright (C) 2019 Ahmet Baþar
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

public class BoardV5 implements IBoard {
	
	private long[] bitboard;
	private byte[] pieces;
//	private long zobristKey;
	
	private int[] pushDiffs = { 8, 64 - 8 };
	private int[][] castlingRookSources = { { 0, 7 }, { 56, 63 } };
	private int[][] castlingRookTargets = { { 3, 5 }, { 59, 61 } };
	private byte[] kingPositions = { 4, 60 };
	private byte[][] rookPositions = { { 0, 7 }, { 56, 63 } };
	
	//
	private static final int TEMP_BOARD_SIZE = 100;
	private static final int HALF_OF_TEMP_BOARD_SIZE = 50;
	private byte[] capturedPieces = new byte[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private int[] castlingRookFroms = new int[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private int[] castlingRookTos = new int[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private byte[] sideToRooks = new byte[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private int[] epTs = new int[TEMP_BOARD_SIZE]; // 25 ply? wtf!.
	private int[] epSs = new int[TEMP_BOARD_SIZE]; // 25 ply? wtf!.
	private byte[][][] castlingRightss = new byte[TEMP_BOARD_SIZE][2][2]; // 25 ply? wtf!...
	private long[] zobristKeys = new long[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private long[] pawnZobristKeys = new long[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private int[] fiftyMoveCounters = new int[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private int[] nullMoveCounters = new int[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private int[][] moveList = new int[TEMP_BOARD_SIZE][EngineConstants.MOVE_LIST_SIZE];
	//
	
	{
		for (int i = 0; i < TEMP_BOARD_SIZE; i++) {
			// 64 means no epTarget. because we assume 1L<<64 = 0
			// -1 means no epSquare exists.
			epTs[i] = 64;
			epSs[i] = -1;
		}
	}
	
	public int getEpTarget(int depth) {
		depth = convertToInternalDepth(depth);
		return epTs[depth];
	}
	
	public int getEpSquare(int depth) {
		depth = convertToInternalDepth(depth);
		return epSs[depth];
	}
	
	public int[] getMoveList(int depth) {
		depth = convertToInternalDepth(depth);
		return moveList[depth];
	}
	
	public BoardV5(long[] bitboard, byte[] pieces, int epT, int epS, int depth, byte[][] castlingRights, long zobristKey, int fiftyMoveCounter, long pawnZobristKey) {
		depth = convertToInternalDepth(depth);
		int depthPlusOne = depth + 1;
		this.bitboard = bitboard;
		this.pieces = pieces;
//		this.zobristKey = zobristKey;
		epTs[depthPlusOne] = epT;
		epSs[depthPlusOne] = epS;
		castlingRightss[depthPlusOne][0][0] = castlingRights[0][0];
		castlingRightss[depthPlusOne][0][1] = castlingRights[0][1];
		castlingRightss[depthPlusOne][1][0] = castlingRights[1][0];
		castlingRightss[depthPlusOne][1][1] = castlingRights[1][1];
		zobristKeys[depthPlusOne] = zobristKey;
		pawnZobristKeys[depthPlusOne] = pawnZobristKey;
		fiftyMoveCounters[depthPlusOne] = fiftyMoveCounter;
		nullMoveCounters[depthPlusOne] = fiftyMoveCounter; // Initially equals.
	}
	
	public void doNullMove(int depth, int side) {
		depth = convertToInternalDepth(depth);
		int depthPlusOne = depth + 1;
		//Transposition Table//
		long zz = zobristKeys[depthPlusOne];
		zz = zz ^ TranspositionTable.zobristBlackMove;
		
		int ept = epTs[depthPlusOne];
		if (ept != 64 && (EngineConstants.PAWN_ATTACK_LOOKUP[side ^ 1][ept] & bitboard[(side) | EngineConstants.PAWN]) != 0) {
			zz = zz ^ TranspositionTable.zobristEnPassantArray[ept];
		}
		zobristKeys[depth] = zz;

		int fiftyMoveCounter = fiftyMoveCounters[depthPlusOne];
		fiftyMoveCounter++;
		fiftyMoveCounters[depth] = fiftyMoveCounter;
		nullMoveCounters[depth] = 0;
	}
	
	public void undoNullMove(int depth) {
		depth = convertToInternalDepth(depth);
		int depthPlusOne = depth + 1;
		zobristKeys[depth] = zobristKeys[depthPlusOne];
		
		fiftyMoveCounters[depth] = fiftyMoveCounters[depthPlusOne];
		nullMoveCounters[depth] = nullMoveCounters[depthPlusOne];
	}
	
	public void doMove(int move, int side, int opSide, int depth) {
		depth = convertToInternalDepth(depth);
		int depthPlusOne = depth + 1;
		int diff = pushDiffs[side];
		int castlingSide = 0;
		int castlingRookFrom = 0;
		int castlingRookTo = 0;
		byte sideToRook = 0;
		long tmpBitboard;
		int moveType = move & 0x00070000;
		int to = (move & 0x0000ff00) >>> 8;
		int from = move & 0x000000ff;
		byte fromPiece = pieces[from];
		byte capturedPiece = 0;
		
		
		boolean isRookOrKingMove = (fromPiece == (side | EngineConstants.KING) || (fromPiece == (side | EngineConstants.ROOK))) 
				  || (pieces[to] == ((opSide) | EngineConstants.ROOK)) || (pieces[to] == ((opSide) | EngineConstants.KING));
		
		int epTarget = epTs[depthPlusOne];
		int epSquare = epSs[depthPlusOne];
		
		long pawnZobristKey = pawnZobristKeys[depthPlusOne];
		
		//Transposition Table//
		long zobristKey = zobristKeys[depthPlusOne];
		zobristKey = zobristKey ^ TranspositionTable.zobristBlackMove;
		if (epTarget != 64 && (EngineConstants.PAWN_ATTACK_LOOKUP[side ^ 1][epTarget] & bitboard[(side) | EngineConstants.PAWN]) != 0) {
			zobristKey = zobristKey ^ TranspositionTable.zobristEnPassantArray[epTarget];
		}
		//
		
		switch (moveType) {
		case 0:
			
			epTarget = 64;
			epSquare = -1;
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
			epTarget = Long.numberOfTrailingZeros(((1L << to) >>> diff) | ((1L << to) << (64 - diff)));
			epSquare = to;
			
			//Transposition Table//
			if ((EngineConstants.PAWN_ATTACK_LOOKUP[side][epTarget] & bitboard[(side ^ 1) | EngineConstants.PAWN]) != 0) {
				zobristKey = zobristKey ^ TranspositionTable.zobristEnPassantArray[epTarget];
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
			
			capturedPiece = pieces[epSquare];
			
			//Transposition Table//
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[capturedPiece][epSquare];
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			//
			
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[capturedPiece][epSquare];
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			
			pieces[epSquare] = 0;
			pieces[from] = 0;
			pieces[to] = fromPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			bitboard[capturedPiece] &= ~(1L << epSquare);
			epTarget = 64;
			epSquare = -1;
			break;
		case EngineConstants.PROMOTION_SHIFTED:
			
			epTarget = 64;
			epSquare = -1;
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
			epTarget = 64;
			epSquare = -1;
			castlingSide = (move & 0x00010000) >>> 16;
			sideToRook = (byte) (side | EngineConstants.ROOK);
			
			castlingRookFrom = castlingRookSources[side][castlingSide];
			castlingRookTo = castlingRookTargets[side][castlingSide];
			
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

		capturedPieces[depth] = capturedPiece;
		castlingRookFroms[depth] = castlingRookFrom;
		castlingRookTos[depth] = castlingRookTo;
		sideToRooks[depth] = sideToRook;
		epTs[depth] = epTarget;
		epSs[depth] = epSquare;
		
		
		if (isRookOrKingMove) {
			
			//
			//Transposition Table//
			byte whiteQueenSideCastlingRight = castlingRightss[depth][0][0];
			byte whiteKingSideCastlingRight = castlingRightss[depth][0][1];
			byte blackQueenSideCastlingRight = castlingRightss[depth][1][0];
			byte blackKingSideCastlingRight = castlingRightss[depth][1][1];
			//
			
			byte[][] castlingRights = castlingRightss[depth];
			tmpBitboard = bitboard[(side) | EngineConstants.KING];
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
		
		zobristKeys[depth] = zobristKey;
		
		pawnZobristKeys[depth] = pawnZobristKey;
		
		int fiftyMoveCounter = fiftyMoveCounters[depthPlusOne];
		if (capturedPiece != 0 || fromPiece == (side | EngineConstants.PAWN)) {
			fiftyMoveCounter = 0;
		} else {
			fiftyMoveCounter ++;
		}
		fiftyMoveCounters[depth] = fiftyMoveCounter;
		
		
		int nullMoveCounter = nullMoveCounters[depthPlusOne];
		nullMoveCounter ++;
		nullMoveCounters[depth] = nullMoveCounter;
	}
	
	public void undoMove(int move, int side, int opSide, int depth) {
		depth = convertToInternalDepth(depth);
		int depthPlusOne = depth + 1;
		int moveType = move & 0x00070000;
		int to = (move & 0x0000ff00) >>> 8;
		int from = move & 0x000000ff;
		byte fromPiece = pieces[to];
		byte capturedPiece = capturedPieces[depth];
		int castlingRookFrom = castlingRookFroms[depth];
		int castlingRookTo = castlingRookTos[depth];
		byte sideToRook = sideToRooks[depth];
		int previousEpSquare = epSs[depthPlusOne];
		
		zobristKeys[depth] = zobristKeys[depthPlusOne];
		fiftyMoveCounters[depth] = fiftyMoveCounters[depthPlusOne];
		nullMoveCounters[depth] = nullMoveCounters[depthPlusOne];
		
		pawnZobristKeys[depth] = pawnZobristKeys[depthPlusOne];
		
		boolean isRookOrKingMove = (fromPiece == (side | EngineConstants.KING) || (fromPiece == (side | EngineConstants.ROOK))) 
				  || (capturedPiece == ((opSide) | EngineConstants.ROOK)) || (capturedPiece == ((opSide) | EngineConstants.KING));
		
		
		// update castling rights
		if (isRookOrKingMove) {
			
			//
			castlingRightss[depth][0][0] = castlingRightss[depthPlusOne][0][0];
			castlingRightss[depth][0][1] = castlingRightss[depthPlusOne][0][1];
			castlingRightss[depth][1][0] = castlingRightss[depthPlusOne][1][0];
			castlingRightss[depth][1][1] = castlingRightss[depthPlusOne][1][1];
			
			//
		}
		
		//
		// NOT INCORRECT BUT ALSO NOT NECESSARY FOR CURRENT IMPLEMENTATION.
//		epTs[depth] = epTs[depthPlusOne];
//		epSs[depth] = epSs[depthPlusOne];
		//
		
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
			
			pieces[previousEpSquare] = capturedPiece;
			pieces[from] = fromPiece;
			pieces[to] = 0;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			bitboard[capturedPiece] |= (1L << previousEpSquare);
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
			
			pieces[castlingRookFrom] = sideToRook;
			pieces[castlingRookTo] = 0;
			bitboard[sideToRook] |= (1L << castlingRookFrom);
			bitboard[sideToRook] &= ~(1L << castlingRookTo);
			break;
		}
		
	}
	
	public void doMoveWithoutZobrist(int move, int side, int opSide, int depth) {
		depth = convertToInternalDepth(depth);
		int depthPlusOne = depth + 1;
		int diff = pushDiffs[side];
		int castlingSide = 0;
		int castlingRookFrom = 0;
		int castlingRookTo = 0;
		byte sideToRook = 0;
		long tmpBitboard;
		int moveType = move & 0x00070000;
		int to = (move & 0x0000ff00) >>> 8;
		int from = move & 0x000000ff;
		byte fromPiece = pieces[from];
		byte capturedPiece = 0;
		
		
		boolean isRookOrKingMove = (fromPiece == (side | EngineConstants.KING) || (fromPiece == (side | EngineConstants.ROOK))) 
				  || (pieces[to] == ((opSide) | EngineConstants.ROOK)) || (pieces[to] == ((opSide) | EngineConstants.KING));
		
		int epTarget = epTs[depthPlusOne];
		int epSquare = epSs[depthPlusOne];
		
		long pawnZobristKey = pawnZobristKeys[depthPlusOne];
		
		switch (moveType) {
		case 0:
			
			epTarget = 64;
			epSquare = -1;
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
			epTarget = Long.numberOfTrailingZeros(((1L << to) >>> diff) | ((1L << to) << (64 - diff)));
			epSquare = to;
			
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			
			pieces[from] = 0;
			pieces[to] = fromPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			break;
		case EngineConstants.EP_CAPTURE_SHIFTED:
			
			capturedPiece = pieces[epSquare];
			
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[capturedPiece][epSquare];
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
			
			pieces[epSquare] = 0;
			pieces[from] = 0;
			pieces[to] = fromPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			bitboard[capturedPiece] &= ~(1L << epSquare);
			epTarget = 64;
			epSquare = -1;
			break;
		case EngineConstants.PROMOTION_SHIFTED:
			
			epTarget = 64;
			epSquare = -1;
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
			epTarget = 64;
			epSquare = -1;
			castlingSide = (move & 0x00010000) >>> 16;
			sideToRook = (byte) (side | EngineConstants.ROOK);
			
			castlingRookFrom = castlingRookSources[side][castlingSide];
			castlingRookTo = castlingRookTargets[side][castlingSide];
			
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

		capturedPieces[depth] = capturedPiece;
		castlingRookFroms[depth] = castlingRookFrom;
		castlingRookTos[depth] = castlingRookTo;
		sideToRooks[depth] = sideToRook;
		epTs[depth] = epTarget;
		epSs[depth] = epSquare;
		pawnZobristKeys[depth] = pawnZobristKey;
		
		
		if (isRookOrKingMove) {
			
			//
			byte[][] castlingRights = castlingRightss[depth];
			tmpBitboard = bitboard[(side) | EngineConstants.KING];
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
		}
		
		/****/
		// No need to increment fifty-move-counter here because this method is invoked only from quiescence search. 
		/****/
	}
	
	public void undoMoveWithoutZobrist(int move, int side, int opSide, int depth) {
		depth = convertToInternalDepth(depth);
		int depthPlusOne = depth + 1;
		int moveType = move & 0x00070000;
		int to = (move & 0x0000ff00) >>> 8;
		int from = move & 0x000000ff;
		byte fromPiece = pieces[to];
		byte capturedPiece = capturedPieces[depth];
		int castlingRookFrom = castlingRookFroms[depth];
		int castlingRookTo = castlingRookTos[depth];
		byte sideToRook = sideToRooks[depth];
		int previousEpSquare = epSs[depthPlusOne];
		
		pawnZobristKeys[depth] = pawnZobristKeys[depthPlusOne];
		
		boolean isRookOrKingMove = (fromPiece == (side | EngineConstants.KING) || (fromPiece == (side | EngineConstants.ROOK))) 
				  || (capturedPiece == ((opSide) | EngineConstants.ROOK)) || (capturedPiece == ((opSide) | EngineConstants.KING));
		
		
		// update castling rights
		if (isRookOrKingMove) {
			
			//
			castlingRightss[depth][0][0] = castlingRightss[depthPlusOne][0][0];
			castlingRightss[depth][0][1] = castlingRightss[depthPlusOne][0][1];
			castlingRightss[depth][1][0] = castlingRightss[depthPlusOne][1][0];
			castlingRightss[depth][1][1] = castlingRightss[depthPlusOne][1][1];
			
			//
		}
		
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
			
			pieces[previousEpSquare] = capturedPiece;
			pieces[from] = fromPiece;
			pieces[to] = 0;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			bitboard[capturedPiece] |= (1L << previousEpSquare);
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
			
			pieces[castlingRookFrom] = sideToRook;
			pieces[castlingRookTo] = 0;
			bitboard[sideToRook] |= (1L << castlingRookFrom);
			bitboard[sideToRook] &= ~(1L << castlingRookTo);
			break;
		}
		
		/****/
		// No need to decrement fifty-move-counter here because this method is invoked only from quiescence search. 
		/****/
					
	}
	
	public void deepDive(int depth) {
		depth = convertToInternalDepth(depth);
		int depthPlusOne = depth + 1;
		castlingRightss[depth][0][0] = castlingRightss[depthPlusOne][0][0];
		castlingRightss[depth][0][1] = castlingRightss[depthPlusOne][0][1];
		castlingRightss[depth][1][0] = castlingRightss[depthPlusOne][1][0];
		castlingRightss[depth][1][1] = castlingRightss[depthPlusOne][1][1];
		zobristKeys[depth] = zobristKeys[depthPlusOne];
		pawnZobristKeys[depth] = pawnZobristKeys[depthPlusOne];
		fiftyMoveCounters[depth] = fiftyMoveCounters[depthPlusOne];
		nullMoveCounters[depth] = nullMoveCounters[depthPlusOne];
	}
	
	public void deeperDive(int depth) {
		depth = convertToInternalDepth(depth);
		int depthPlusOne = depth + 1;
		castlingRightss[depth][0][0] = castlingRightss[depthPlusOne][0][0];
		castlingRightss[depth][0][1] = castlingRightss[depthPlusOne][0][1];
		castlingRightss[depth][1][0] = castlingRightss[depthPlusOne][1][0];
		castlingRightss[depth][1][1] = castlingRightss[depthPlusOne][1][1];
		zobristKeys[depth] = zobristKeys[depthPlusOne];
		pawnZobristKeys[depth] = pawnZobristKeys[depthPlusOne];
		fiftyMoveCounters[depth] = fiftyMoveCounters[depthPlusOne];
		nullMoveCounters[depth] = nullMoveCounters[depthPlusOne];
		
		epTs[depth] = 64;
		epSs[depth] = -1;
	}

	public byte[][] getCastlingRights(int depth) {
		depth = convertToInternalDepth(depth);
		return castlingRightss[depth];
	}
	
	public byte getCapturedPiece(int depth) {
		depth = convertToInternalDepth(depth);
		return capturedPieces[depth];
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
		depth = convertToInternalDepth(depth);
		return zobristKeys[depth];
	}
	
	public long getPawnZobristKey(int depth) {
		depth = convertToInternalDepth(depth);
		return pawnZobristKeys[depth];
	}
	
	public int getFiftyMoveCounter(int depth) {
		depth = convertToInternalDepth(depth);
		return fiftyMoveCounters[depth];
	}
	
	public int getNullMoveCounter(int depth) {
		depth = convertToInternalDepth(depth);
		return nullMoveCounters[depth];
	}
	
	public boolean hasRepeated(long zobristKey, int depth) {
		depth = convertToInternalDepth(depth);
		int fiftyMoveCounter = fiftyMoveCounters[depth];
		
		// TODO Check (as well as fiftyMoveCounter > 99) whether the king is in check or not because of the FIDE rules.
		if (fiftyMoveCounter > 99) {
			return true;
		}
		
		int nullMoveCounter = nullMoveCounters[depth];
		int upperBound = Math.min(fiftyMoveCounter, nullMoveCounter);
		if (upperBound < 4) {
			return false;
		}
		
		// go back to last doMove iteration.
		depth++;
		
		upperBound += depth;
		
		for (int i = depth + 4; i <= upperBound && i < TEMP_BOARD_SIZE; i = i + 2) {
			if (zobristKeys[i] == zobristKey) {
				return true;
			}
		}
		return false;
	}
}

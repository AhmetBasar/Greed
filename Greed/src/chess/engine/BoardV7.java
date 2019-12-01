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

import java.util.List;

import chess.engine.test.Assertion;

public class BoardV7 implements IBoard, EngineConstants {
	
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
	private static final int TEMP_BOARD_SIZE = 150;
	
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
	
	public int moveIndex = 0;
	private int side;
	private int opSide;
	
	public int materialKey;
	public long occupiedSquares;
	public long emptySquares;
	public long[] occupiedSquaresBySide = new long[2];
	
	public int getEpTarget() {
		return epT;
	}
	
	public int[] getMoveList() {
		return moveLists[this.moveIndex];
	}
	
	public BoardV7(long[] bitboard, byte[] pieces, int epT, byte[][] castlingRights, int fiftyMoveCounter, List<Long> zobristKeyHistory, int side) {
		this.bitboard = bitboard;
		this.pieces = pieces;
		this.epT = epT;
		this.castlingRights[0][0] = castlingRights[0][0];
		this.castlingRights[0][1] = castlingRights[0][1];
		this.castlingRights[1][0] = castlingRights[1][0];
		this.castlingRights[1][1] = castlingRights[1][1];
		this.fiftyMoveCounter = fiftyMoveCounter;
		this.nullMoveCounter = fiftyMoveCounter; // Initially equals.
		this.side = side;
		this.opSide = side ^ 1;
		
		int k = zobristKeyHistory.size() - fiftyMoveCounter;
		moveIndex = fiftyMoveCounter;
		for (int j = 0; j < fiftyMoveCounter; j++, k++) {
			zobristKeys[j] = zobristKeyHistory.get(k);
		}
		
		zobristKey = TranspositionTable.getZobristKey(bitboard, epT, castlingRights, side);
		pawnZobristKey = TranspositionTable.getPawnZobristKey(bitboard);
		materialKey = Material.getMaterialKey(bitboard);
		
		occupiedSquaresBySide[WHITE] = bitboard[WHITE_PAWN] | bitboard[WHITE_KNIGHT] | bitboard[WHITE_BISHOP] | bitboard[WHITE_ROOK] | bitboard[WHITE_QUEEN] | bitboard[WHITE_KING];
		occupiedSquaresBySide[BLACK] = bitboard[BLACK_PAWN] | bitboard[BLACK_KNIGHT] | bitboard[BLACK_BISHOP] | bitboard[BLACK_ROOK] | bitboard[BLACK_QUEEN] | bitboard[BLACK_KING];
		occupiedSquares = occupiedSquaresBySide[WHITE] | occupiedSquaresBySide[BLACK];
		emptySquares = ~occupiedSquares;
	}
	
	public void doNullMove() {
		
		storeCurrentValues();
		
		//Transposition Table//
		zobristKey = zobristKey ^ TranspositionTable.zobristBlackMove;
		
		if (epT != 64 && (EngineConstants.PAWN_ATTACK_LOOKUP[side ^ 1][epT] & bitboard[(side) | EngineConstants.PAWN]) != 0) {
			zobristKey = zobristKey ^ TranspositionTable.zobristEnPassantArray[epT];
		}

		fiftyMoveCounter++;
		nullMoveCounter = 0;
		
		epT = 64;
		
		changeSideToMove();
		
		if (CompileTimeConstants.ENABLE_ASSERTION) {
			checkConsistency();
		}
	}
	
	public void undoNullMove() {
		changeSideToMove();
		fetchPreviousValues();
		
		if (CompileTimeConstants.ENABLE_ASSERTION) {
			checkConsistency();
		}
	}
	
	private void storeCurrentValues() {
		capturedPieces[moveIndex] = capturedPiece;
		epTs[moveIndex] = epT;
		castlingRightss[moveIndex][0][0] = castlingRights[0][0];
		castlingRightss[moveIndex][0][1] = castlingRights[0][1];
		castlingRightss[moveIndex][1][0] = castlingRights[1][0];
		castlingRightss[moveIndex][1][1] = castlingRights[1][1];
		zobristKeys[moveIndex] = zobristKey;
		pawnZobristKeys[moveIndex] = pawnZobristKey;
		fiftyMoveCounters[moveIndex] = fiftyMoveCounter;
		nullMoveCounters[moveIndex] = nullMoveCounter;
		moveIndex++;
	}
	
	private void fetchPreviousValues() {
		moveIndex--;
		capturedPiece = capturedPieces[moveIndex];
		epT = epTs[moveIndex];
		castlingRights[0][0] = castlingRightss[moveIndex][0][0];
		castlingRights[0][1] = castlingRightss[moveIndex][0][1];
		castlingRights[1][0] = castlingRightss[moveIndex][1][0];
		castlingRights[1][1] = castlingRightss[moveIndex][1][1];
		zobristKey = zobristKeys[moveIndex];
		pawnZobristKey = pawnZobristKeys[moveIndex];
		fiftyMoveCounter = fiftyMoveCounters[moveIndex];
		nullMoveCounter = nullMoveCounters[moveIndex];
	}
	
	public void doMove(int move) {
		
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
				materialKey -= Material.PIECE_VALUES[capturedPiece];
				
				bitboard[capturedPiece] &= ~(1L << to);
				occupiedSquaresBySide[opSide] &= ~(1L << to);
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
			
			//
			occupiedSquaresBySide[side] &= ~(1L << from);
			occupiedSquaresBySide[side] |= (1L << to);
			//
			
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
			
			//
			occupiedSquaresBySide[side] &= ~(1L << from);
			occupiedSquaresBySide[side] |= (1L << to);
			//
			break;
		case EngineConstants.EP_CAPTURE_SHIFTED:
			int epS = to + epSquareDiff[side];
			capturedPiece = pieces[epS];
			
			materialKey -= Material.PIECE_VALUES[capturedPiece];
			
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
			
			//
			occupiedSquaresBySide[side] &= ~(1L << from);
			occupiedSquaresBySide[side] |= (1L << to);
			occupiedSquaresBySide[opSide] &= ~(1L << epS);
			//
			break;
		case EngineConstants.PROMOTION_SHIFTED:
			
			epT = 64;
			capturedPiece = pieces[to];
			byte promotedPiece = Move.getPromotedPiece(move);
			
			materialKey += Material.PIECE_VALUES[promotedPiece] - Material.PIECE_VALUES[fromPiece];
			
			//Transposition Table//
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[promotedPiece][to];
			if(capturedPiece > 0) {
				zobristKey = zobristKey ^ TranspositionTable.zobristPositionArray[capturedPiece][to];
				materialKey -= Material.PIECE_VALUES[capturedPiece];
				
				bitboard[capturedPiece] &= ~(1L << to);
				occupiedSquaresBySide[opSide] &= ~(1L << to);
			}
			//
			
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			
			pieces[from] = 0;
			pieces[to] = promotedPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[promotedPiece] |= (1L << to);
			
			//
			occupiedSquaresBySide[side] &= ~(1L << from);
			occupiedSquaresBySide[side] |= (1L << to);
			//
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
			
			//
			occupiedSquaresBySide[side] &= ~(1L << from);
			occupiedSquaresBySide[side] |= (1L << to);
			occupiedSquaresBySide[side] &= ~(1L << castlingRookFrom);
			occupiedSquaresBySide[side] |= (1L << castlingRookTo);
			//
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
		
		changeSideToMove();
		
		occupiedSquares = occupiedSquaresBySide[WHITE] | occupiedSquaresBySide[BLACK];
		emptySquares = ~occupiedSquares;
		
		if (CompileTimeConstants.ENABLE_ASSERTION) {
			checkConsistency();
		}
	}
	
	public void undoMove(int move) {
		
		changeSideToMove();
		
		int moveType = move & 0x00070000;
		int to = (move & 0x0000ff00) >>> 8;
		int from = move & 0x000000ff;
		byte fromPiece = pieces[to];
		
		switch (moveType) {
		case 0:
			
			if (capturedPiece > 0) {
				materialKey += Material.PIECE_VALUES[capturedPiece];
				
				bitboard[capturedPiece] |= (1L << to); // capturedPiece may be zero here.
				occupiedSquaresBySide[opSide] |= (1L << to); // capturedPiece may be zero here.
			}
			
			pieces[from] = fromPiece;
			pieces[to] = capturedPiece;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			
			//
			occupiedSquaresBySide[side] |= (1L << from);
			occupiedSquaresBySide[side] &= ~(1L << to);
			//
			
			break;
		case EngineConstants.DOUBLE_PUSH_SHIFTED:
			
			pieces[from] = fromPiece;
			pieces[to] = 0;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			
			//
			occupiedSquaresBySide[side] |= (1L << from);
			occupiedSquaresBySide[side] &= ~(1L << to);
			//
			break;
		case EngineConstants.EP_CAPTURE_SHIFTED:
			
			int epS = to + epSquareDiff[side];
			
			materialKey += Material.PIECE_VALUES[capturedPiece];
			
			pieces[epS] = capturedPiece;
			pieces[from] = fromPiece;
			pieces[to] = 0;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			bitboard[capturedPiece] |= (1L << epS);
			
			//
			occupiedSquaresBySide[side] |= (1L << from);
			occupiedSquaresBySide[side] &= ~(1L << to);
			occupiedSquaresBySide[opSide] |= (1L << epS);
			//
			break;
		case EngineConstants.PROMOTION_SHIFTED:
			
			fromPiece = (byte)(side | EngineConstants.PAWN);
			byte promotedPiece = Move.getPromotedPiece(move);
			materialKey += Material.PIECE_VALUES[fromPiece] - Material.PIECE_VALUES[promotedPiece];
			if (capturedPiece > 0) {
				materialKey += Material.PIECE_VALUES[capturedPiece];
				
				bitboard[capturedPiece] |= (1L << to); // capturedPiece may be zero here. 
				occupiedSquaresBySide[opSide] |= (1L << to); // capturedPiece may be zero here.
			}
			pieces[from] = fromPiece;
			pieces[to] = capturedPiece;
			bitboard[fromPiece] |= (1L << from);
			bitboard[promotedPiece] &= ~(1L << to);
			
			//
			occupiedSquaresBySide[side] |= (1L << from);
			occupiedSquaresBySide[side] &= ~(1L << to);
			//
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
			
			//
			occupiedSquaresBySide[side] |= (1L << from);
			occupiedSquaresBySide[side] &= ~(1L << to);
			occupiedSquaresBySide[side] |= (1L << castlingRookFrom);
			occupiedSquaresBySide[side] &= ~(1L << castlingRookTo);
			//
			break;
		}
		
		fetchPreviousValues();
		
		occupiedSquares = occupiedSquaresBySide[WHITE] | occupiedSquaresBySide[BLACK];
		emptySquares = ~occupiedSquares;
		
		if (CompileTimeConstants.ENABLE_ASSERTION) {
			checkConsistency();
		}
	}
	
	public void doMoveWithoutZobrist(int move) {
		
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
			
			if (capturedPiece > 0) {
				materialKey -= Material.PIECE_VALUES[capturedPiece];
				
				bitboard[capturedPiece] &= ~(1L << to);
				occupiedSquaresBySide[opSide] &= ~(1L << to);
			}
			
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
			
			//
			occupiedSquaresBySide[side] &= ~(1L << from);
			occupiedSquaresBySide[side] |= (1L << to);
			//
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
			
			//
			occupiedSquaresBySide[side] &= ~(1L << from);
			occupiedSquaresBySide[side] |= (1L << to);
			//
			break;
		case EngineConstants.EP_CAPTURE_SHIFTED:
			
			int epS = to + epSquareDiff[side];
			capturedPiece = pieces[epS];
			
			materialKey -= Material.PIECE_VALUES[capturedPiece];
			
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
			
			//
			occupiedSquaresBySide[side] &= ~(1L << from);
			occupiedSquaresBySide[side] |= (1L << to);
			occupiedSquaresBySide[opSide] &= ~(1L << epS);
			//
			break;
		case EngineConstants.PROMOTION_SHIFTED:
			
			epT = 64;
			capturedPiece = pieces[to];
			byte promotedPiece = Move.getPromotedPiece(move);
			
			materialKey += Material.PIECE_VALUES[promotedPiece] - Material.PIECE_VALUES[fromPiece];
			if(capturedPiece > 0) {
				materialKey -= Material.PIECE_VALUES[capturedPiece];
				
				bitboard[capturedPiece] &= ~(1L << to);
				occupiedSquaresBySide[opSide] &= ~(1L << to);
			}
			
			pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
			
			pieces[from] = 0;
			pieces[to] = promotedPiece;
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[promotedPiece] |= (1L << to);
			
			//
			occupiedSquaresBySide[side] &= ~(1L << from);
			occupiedSquaresBySide[side] |= (1L << to);
			//
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
			
			//
			occupiedSquaresBySide[side] &= ~(1L << from);
			occupiedSquaresBySide[side] |= (1L << to);
			occupiedSquaresBySide[side] &= ~(1L << castlingRookFrom);
			occupiedSquaresBySide[side] |= (1L << castlingRookTo);
			//
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
		
		changeSideToMove();
		
		occupiedSquares = occupiedSquaresBySide[WHITE] | occupiedSquaresBySide[BLACK];
		emptySquares = ~occupiedSquares;
	}
	
	public void undoMoveWithoutZobrist(int move) {
		
		changeSideToMove();
		
		int moveType = move & 0x00070000;
		int to = (move & 0x0000ff00) >>> 8;
		int from = move & 0x000000ff;
		byte fromPiece = pieces[to];
		
		switch (moveType) {
		case 0:
			
			if (capturedPiece > 0) {
				materialKey += Material.PIECE_VALUES[capturedPiece];
				
				bitboard[capturedPiece] |= (1L << to); // capturedPiece may be zero here.
				occupiedSquaresBySide[opSide] |= (1L << to); // capturedPiece may be zero here.
			}
			
			pieces[from] = fromPiece;
			pieces[to] = capturedPiece;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			
			//
			occupiedSquaresBySide[side] |= (1L << from);
			occupiedSquaresBySide[side] &= ~(1L << to);
			//
			break;
		case EngineConstants.DOUBLE_PUSH_SHIFTED:
			
			pieces[from] = fromPiece;
			pieces[to] = 0;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			
			//
			occupiedSquaresBySide[side] |= (1L << from);
			occupiedSquaresBySide[side] &= ~(1L << to);
			//
			break;
		case EngineConstants.EP_CAPTURE_SHIFTED:
			
			materialKey += Material.PIECE_VALUES[capturedPiece];
			
			int epS = to + epSquareDiff[side];
			
			pieces[epS] = capturedPiece;
			pieces[from] = fromPiece;
			pieces[to] = 0;
			bitboard[fromPiece] |= (1L << from);
			bitboard[fromPiece] &= ~(1L << to);
			bitboard[capturedPiece] |= (1L << epS);
			
			//
			occupiedSquaresBySide[side] |= (1L << from);
			occupiedSquaresBySide[side] &= ~(1L << to);
			occupiedSquaresBySide[opSide] |= (1L << epS);
			//
			break;
		case EngineConstants.PROMOTION_SHIFTED:
			byte promotedPiece = Move.getPromotedPiece(move);
			fromPiece = (byte)(side | EngineConstants.PAWN);
			
			materialKey += Material.PIECE_VALUES[fromPiece] - Material.PIECE_VALUES[promotedPiece];
			if (capturedPiece > 0) {
				materialKey += Material.PIECE_VALUES[capturedPiece];	
				bitboard[capturedPiece] |= (1L << to); // capturedPiece may be zero here.
				occupiedSquaresBySide[opSide] |= (1L << to); // capturedPiece may be zero here.
			}
			
			pieces[from] = fromPiece;
			pieces[to] = capturedPiece;
			bitboard[fromPiece] |= (1L << from);
			bitboard[promotedPiece] &= ~(1L << to);
			
			//
			occupiedSquaresBySide[side] |= (1L << from);
			occupiedSquaresBySide[side] &= ~(1L << to);
			//
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
			
			//
			occupiedSquaresBySide[side] |= (1L << from);
			occupiedSquaresBySide[side] &= ~(1L << to);
			occupiedSquaresBySide[side] |= (1L << castlingRookFrom);
			occupiedSquaresBySide[side] &= ~(1L << castlingRookTo);
			//
			break;
		}
		
		/****/
		// No need to decrement fifty-move-counter here because this method is invoked only from quiescence search. 
		/****/
		
		fetchPreviousValues();
		
		occupiedSquares = occupiedSquaresBySide[WHITE] | occupiedSquaresBySide[BLACK];
		emptySquares = ~occupiedSquares;
	}
	

	public byte[][] getCastlingRights() {
		return castlingRights;
	}
	
	public byte getCapturedPiece() {
		return capturedPiece;
	}

	public long[] getBitboard() {
		return bitboard;
	}

	public byte[] getPieces() {
		return pieces;
	}
	
	public long getZobristKey() {
		return zobristKey;
	}
	
	public long getPawnZobristKey() {
		return pawnZobristKey;
	}
	
	public int getFiftyMoveCounter() {
		return fiftyMoveCounter;
	}
	
	public int getNullMoveCounter() {
		return nullMoveCounter;
	}
	
	public boolean hasRepeated(long zobristKey) {
		// TODO Check (as well as fiftyMoveCounter > 99) whether the king is in check or not because of the FIDE rules.
		if (fiftyMoveCounter > 99) {
			return true;
		}
		
		int upperBound = Math.min(fiftyMoveCounter, nullMoveCounter);
		if (upperBound < 4) {
			return false;
		}
		
		final int lowerBound = Math.max(0, moveIndex - upperBound);
		
		for (int i = moveIndex - 4; i >= lowerBound; i = i - 2) {
			if (zobristKeys[i] == zobristKey) {
				return true;
			}
		}
		return false;
	}
	
	public void changeSideToMove() {
		side = opSide;
		opSide = side ^ 1;
	}
	
	public int getSide() {
		return side;
	}
	
	public int getOpSide() {
		return opSide;
	}

	private void checkConsistency() {
		
		// The king can not be captured.
		Assertion.assertTrue((capturedPiece & 0XFE) != EngineConstants.KING);
		
		// fifty move counter can not be less than move index. (UI related issues.)
		Assertion.assertTrue(moveIndex >= fiftyMoveCounter);

		// check zobrist key.
		Assertion.assertTrue(zobristKey == TranspositionTable.getZobristKey(bitboard, epT, castlingRights, side));
		
		// check pawn zobrist key.
		Assertion.assertTrue(pawnZobristKey == TranspositionTable.getPawnZobristKey(bitboard));
		
		// check material key.
		Assertion.assertTrue(materialKey == Material.getMaterialKey(bitboard));

		// There must be one king per side.
		Assertion.assertTrue(1 == Long.bitCount(bitboard[EngineConstants.WHITE_KING]));

		// There must be one king per side.
		Assertion.assertTrue(1 == Long.bitCount(bitboard[EngineConstants.BLACK_KING]));

		// check white occupancy.
		Assertion.assertTrue(occupiedSquaresBySide[WHITE] == (bitboard[WHITE_PAWN] | bitboard[WHITE_KNIGHT] | bitboard[WHITE_BISHOP] | bitboard[WHITE_ROOK] | bitboard[WHITE_QUEEN] | bitboard[WHITE_KING]));
		
		// check black occupancy.
		Assertion.assertTrue(occupiedSquaresBySide[BLACK] == (bitboard[BLACK_PAWN] | bitboard[BLACK_KNIGHT] | bitboard[BLACK_BISHOP] | bitboard[BLACK_ROOK] | bitboard[BLACK_QUEEN] | bitboard[BLACK_KING]));
		
	}

	public long getOccupiedSquares() {
		return occupiedSquares;
	}

	public long getEmptySquares() {
		return emptySquares;
	}

	public long[] getOccupiedSquaresBySide() {
		return occupiedSquaresBySide;
	}
	
}

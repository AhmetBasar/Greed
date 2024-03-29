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

import java.util.Arrays;
import java.util.List;

import chess.engine.test.Assertion;
import chess.movegen.MagicBitboard;
import chess.util.Utility;

public class BoardV7 implements IBoard, EngineConstants {
	
	private long[] bitboard;
	private byte[] pieces;
//	private long zobristKey;
	
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
	private long[] checkerss = new long[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private long[] pinnedPiecess = new long[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	private long[] discoveredPiecess = new long[TEMP_BOARD_SIZE]; // 25 ply? wtf!...
	//
	
	public byte capturedPiece;
	public int epT = 64; // 64 means no epTarget. because we assume 1L<<64 = 0
	public byte[][] castlingRights = new byte[][] { { 1, 1 }, { 1, 1 } };
	public long zobristKey;
	public long pawnZobristKey;
	public int fiftyMoveCounter;
	public int nullMoveCounter;
	
	public int moveIndex = 0;
	private int side;
	private int opSide;
	
	public int materialKey;
	public long occupiedSquares;
	public long emptySquares;
	public long[] occupiedSquaresBySide = new long[2];
	
	public int[] kingSquares = new int[2];
	public long checkers;
	public long pinnedPieces;
	public long discoveredPieces;
	
	public int getEpTarget() {
		return epT;
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
		
		kingSquares[WHITE] = Long.numberOfTrailingZeros(bitboard[WHITE_KING]);
		kingSquares[BLACK] = Long.numberOfTrailingZeros(bitboard[BLACK_KING]);
		
		checkers = Check.getCheckers(this);
		
		setPinnedAndDiscoveredPieces();
		
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
		
		capturedPiece = 0;
		
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
		checkerss[moveIndex] = checkers;
		pinnedPiecess[moveIndex] = pinnedPieces;
		discoveredPiecess[moveIndex] = discoveredPieces;
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
		checkers = checkerss[moveIndex];
		pinnedPieces = pinnedPiecess[moveIndex];
		discoveredPieces = discoveredPiecess[moveIndex];
	}
	
	public void doMove(int move) {
		
		storeCurrentValues();
		
		int moveType = Move.getMoveType(move);
		int to = Move.getTo(move);
		int from = Move.getFrom(move);
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
			switch (fromPieceWc) {
			case EngineConstants.PAWN:
				pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
				pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
				break;
			case EngineConstants.KING:
				kingSquares[side] = to;
				break;
			default:
				break;
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
			epT = EngineConstants.EPT_LOOKUP[side][to];
			
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
			int castlingSide = Move.getCastlingSide(move);
			byte sideToRook = (byte) (side | EngineConstants.ROOK);
			
			int castlingRookFrom = castlingRookSources[side][castlingSide];
			int castlingRookTo = castlingRookTargets[side][castlingSide];
			kingSquares[side] = to;
			
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
		
		if (CompileTimeConstants.ENABLE_ASSERTION) {
			int fPiece = Move.getFromPiece(move) | side;
			int cPiece = Move.getCapturedPiece(move);
			cPiece = cPiece == 0 ? cPiece : cPiece | opSide;
			Assertion.assertTrue(fPiece == fromPiece);
			Assertion.assertTrue(cPiece == capturedPiece);
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
		
		checkers = Check.getCheckers(this);
		
		setPinnedAndDiscoveredPieces();
		
		if (CompileTimeConstants.ENABLE_ASSERTION) {
			checkConsistency();
		}
	}
	
	public void undoMove(int move) {
		
		changeSideToMove();
		
		int moveType = Move.getMoveType(move);
		int to = Move.getTo(move);
		int from = Move.getFrom(move);
		byte fromPiece = pieces[to];
		
		switch (moveType) {
		case 0:
			
			if (capturedPiece > 0) {
				materialKey += Material.PIECE_VALUES[capturedPiece];
				
				bitboard[capturedPiece] |= (1L << to); // capturedPiece may be zero here.
				occupiedSquaresBySide[opSide] |= (1L << to); // capturedPiece may be zero here.
			}
			
			byte fromPieceWc = (byte)(fromPiece & 0XFE);
			switch (fromPieceWc) {
			case EngineConstants.KING:
				kingSquares[side] = from;
				break;
			default:
				break;
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
			kingSquares[side] = from;
			
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
		
		if (CompileTimeConstants.ENABLE_ASSERTION) {
			int fPiece = Move.getFromPiece(move) | side;
			int cPiece = Move.getCapturedPiece(move);
			cPiece = cPiece == 0 ? cPiece : cPiece | opSide;
			Assertion.assertTrue(fPiece == fromPiece);
			Assertion.assertTrue(cPiece == capturedPiece);
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
		
		int moveType = Move.getMoveType(move);
		int to = Move.getTo(move);
		int from = Move.getFrom(move);
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
			switch (fromPieceWc) {
			case EngineConstants.PAWN:
				pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][from];
				pawnZobristKey = pawnZobristKey ^ TranspositionTable.zobristPositionArray[fromPiece][to];
				break;
			case EngineConstants.KING:
				kingSquares[side] = to;
				break;
			default:
				break;
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
			epT = EngineConstants.EPT_LOOKUP[side][to];
			
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
			int castlingSide = Move.getCastlingSide(move);
			byte sideToRook = (byte) (side | EngineConstants.ROOK);
			
			int castlingRookFrom = castlingRookSources[side][castlingSide];
			int castlingRookTo = castlingRookTargets[side][castlingSide];
			kingSquares[side] = to;
			
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
		
		checkers = Check.getCheckers(this);
		
		setPinnedAndDiscoveredPieces();
	}
	
	public void undoMoveWithoutZobrist(int move) {
		
		changeSideToMove();
		
		int moveType = Move.getMoveType(move);
		int to = Move.getTo(move);
		int from = Move.getFrom(move);
		byte fromPiece = pieces[to];
		
		switch (moveType) {
		case 0:
			
			if (capturedPiece > 0) {
				materialKey += Material.PIECE_VALUES[capturedPiece];
				
				bitboard[capturedPiece] |= (1L << to); // capturedPiece may be zero here.
				occupiedSquaresBySide[opSide] |= (1L << to); // capturedPiece may be zero here.
			}
			
			byte fromPieceWc = (byte)(fromPiece & 0XFE);
			switch (fromPieceWc) {
			case EngineConstants.KING:
				kingSquares[side] = from;
				break;
			default:
				break;
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
			kingSquares[side] = from;
			
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
	
	public boolean hasRepeated(long zobristKey, SearchResult searchResult) {
		// TODO Check (as well as fiftyMoveCounter > 99) whether the king is in check or not because of the FIDE rules.
		if (fiftyMoveCounter > 99) {
			if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
				searchResult.incrementRepetitionCount();
			}
			return true;
		}
		
		int upperBound = Math.min(fiftyMoveCounter, nullMoveCounter);
		if (upperBound < 4) {
			return false;
		}
		
		final int lowerBound = Math.max(0, moveIndex - upperBound);
		
		for (int i = moveIndex - 4; i >= lowerBound; i = i - 2) {
			if (zobristKeys[i] == zobristKey) {
				if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
					searchResult.incrementRepetitionCount();
				}
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
		
		// Friendly piece cannot be Captured
		Assertion.assertTrue(capturedPiece == 0 || (capturedPiece & (byte) 1) == side);
		
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
		
		// check board occupancy.
		Assertion.assertTrue(occupiedSquares == (occupiedSquaresBySide[BLACK] | occupiedSquaresBySide[WHITE]));
		
		// check empty squares.
		Assertion.assertTrue(emptySquares == ~occupiedSquares);
		
		// check white king square
		Assertion.assertTrue(kingSquares[WHITE] == Long.numberOfTrailingZeros(bitboard[WHITE_KING]));
		
		// check black king square
		Assertion.assertTrue(kingSquares[BLACK] == Long.numberOfTrailingZeros(bitboard[BLACK_KING]));
		
		// check board consistency
		Assertion.assertTrue(Arrays.equals(Transformer.getByteArrayStyl(bitboard), pieces));
		
		// check checkers
		Assertion.assertTrue(checkers == Check.getCheckers(this));
		
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

	public int[] getKingSquares() {
		return kingSquares;
	}

	public long getCheckers() {
		return checkers;
	}
	
	// https://github.com/sandermvdb/chess22k
	public void setPinnedAndDiscoveredPieces() {
		pinnedPieces = 0;
		discoveredPieces = 0;
		
		for (int side = WHITE; side <= BLACK; side++) {
			int opSide = side ^ 1;
			if (!Material.hasSlidingPiece(materialKey, opSide)) {
				continue;
			}
			int kingSquare = kingSquares[side];
			long enemySlidingPiece = ((bitboard[opSide | EngineConstants.BISHOP] | bitboard[opSide | EngineConstants.QUEEN]) & MagicBitboard.getBishopMovesWithoutBlocker(kingSquare))
					| ((bitboard[opSide | EngineConstants.ROOK] | bitboard[opSide | EngineConstants.QUEEN]) & MagicBitboard.getRookMovesWithoutBlocker(kingSquare));
			
			while (enemySlidingPiece != 0) {
				long candidates = Utility.LINE[Long.numberOfTrailingZeros(enemySlidingPiece)][kingSquare] & occupiedSquares;
				if (Long.bitCount(candidates) == 1) {
					pinnedPieces |= candidates & occupiedSquaresBySide[side];
					discoveredPieces |= candidates & occupiedSquaresBySide[opSide];
				}
				enemySlidingPiece &= (enemySlidingPiece - 1);
			}
		}
	}

	public long getPinnedPieces() {
		return pinnedPieces;
	}

	public long getDiscoveredPieces() {
		return discoveredPieces;
	}

	public int getMaterialKey() {
		return materialKey;
	}
	
	// https://github.com/sandermvdb/chess22k
	public boolean isLegal(int move) {
		int from = Move.getFrom(move);
		int to = Move.getTo(move);
		int moveType = Move.getMoveType(move);
		if ((pieces[from] & 0XFE) == EngineConstants.KING) {
			return !Check.isKingIncheckIncludingKing(Material.hasMajorPiece(materialKey, opSide), to, bitboard, opSide, side, occupiedSquares ^ Utility.SINGLE_BIT[from]);
		}
		
		if (pieces[to] != 0) {
			return true;
		}
		
		if (moveType == EP_CAPTURE_SHIFTED) {
			return isLegalEpCapture(to, from);
		}
		
		return true;
	}
	
	private boolean isLegalEpCapture(int to, int from) {
		
		byte fromPiece = pieces[from];
		int epS = to + epSquareDiff[side];
		
		long occ = occupiedSquares;
		byte capturedPiece = pieces[epS];
		
		bitboard[fromPiece] &= ~(1L << from);
		bitboard[fromPiece] |= (1L << to);
		bitboard[capturedPiece] &= ~(1L << epS);
		occ &= ~(1L << from);
		occ |= (1L << to);
		occ &= ~(1L << epS);
		
		boolean isInCheck = Check.getCheckers(bitboard, side, opSide, kingSquares[side], occ) != 0;
		
		bitboard[fromPiece] |= (1L << from);
		bitboard[fromPiece] &= ~(1L << to);
		bitboard[capturedPiece] |= (1L << epS);
		
		return !isInCheck;
	}
	
	public boolean isDiscoveredMove(int from) {
		if (discoveredPieces == 0) {
			return false;
		}
		return (discoveredPieces & (1L << from)) != 0;
	}

	@Override
	public boolean isValid(int move) {
		int to = Move.getTo(move);
		long toBb = Utility.SINGLE_BIT[to];
		int from = Move.getFrom(move);
		long fromBb = Utility.SINGLE_BIT[from];
		int moveType = Move.getMoveType(move);
		
		byte moveFromPieceWC = Move.getFromPiece(move);
		byte moveFromPiece = (byte)(moveFromPieceWC | side);
		byte moveCapturedPieceWc = Move.getCapturedPiece(move);
		byte moveCapturedPiece = moveCapturedPieceWc == 0 ? moveCapturedPieceWc : (byte)(moveCapturedPieceWc | opSide);
				
		if (moveFromPiece != pieces[from]) {
			return false;
		}
		
		if (moveCapturedPiece != pieces[to] && moveType != EngineConstants.EP_CAPTURE_SHIFTED) {
			return false;
		}
		
		switch (moveFromPieceWC) {
		case EngineConstants.PAWN:
			if (moveType == EngineConstants.EP_CAPTURE_SHIFTED) {
				if (epT != to) {
					return false;
				}
				return isLegalEpCapture(to, from);
			} else {
				switch(side) {
				case EngineConstants.WHITE: {
					if (from > to) {
						return false;
					}
					if (to - from == 16 && (occupiedSquares & Utility.SINGLE_BIT[from + 8]) != 0L) {
						return false;
					}
					break;
				}
				case EngineConstants.BLACK: {
					if (to > from) {
						return false;
					}
					if (from - to == 16 && (occupiedSquares & Utility.SINGLE_BIT[from - 8]) != 0L) {
						return false;
					}
					break;
				}
				}
			}
		case EngineConstants.KNIGHT:
			// No obstacle for knight moves.
			break;
		case EngineConstants.BISHOP:
		case EngineConstants.ROOK:
		case EngineConstants.QUEEN:
			if ((Utility.LINE[from][to] & occupiedSquares) != 0) {
				return false;
			}
			break;
		case EngineConstants.KING:
			if (Move.isCastling(move)) {
				return isValidCastlingMove(move, moveType);
			}
			return !Check.isKingIncheckIncludingKing(Material.hasMajorPiece(materialKey, opSide), to, bitboard, opSide, side, occupiedSquares ^ fromBb);
		}
		
		if ((fromBb & pinnedPieces) != 0 && (Utility.PINNED_MOVEMENT[from][kingSquares[side]] & toBb) == 0) {
			return false;
		}
		
		if (checkers != 0) {
			if (moveCapturedPiece > 0) {
				if (Long.bitCount(checkers) == 2) {
					return false;
				}
				return (toBb & checkers) != 0;
			} else {
				return !Check.isKingIncheck(kingSquares[side], bitboard, opSide, side, occupiedSquares ^ fromBb ^ toBb);
			}
		}
		
		return true;
	}
	
	private boolean isValidCastlingMove(int move, int moveType) {
		if (checkers != 0) {
			return false;	
		}

		switch (moveType) {
		case EngineConstants.QUEEN_SIDE_CASTLING_SHIFTED:
			if (castlingRights[side][0] == 1 && (EngineConstants.CASTLING_EMPTY_SQUARES[side][0] & occupiedSquares) == 0
			&& !Check.isKingIncheckIncludingKing(MoveGenerationConstants.betweenKingAndRook[side][0], bitboard, opSide, side, occupiedSquares)
			&& !Check.isKingIncheckIncludingKing(MoveGenerationConstants.castlingTarget[side][0], bitboard, opSide, side, occupiedSquares)
					) {
				return true;
			}
			return false;
		case EngineConstants.KING_SIDE_CASTLING_SHIFTED:
			if (castlingRights[side][1] == 1 && (EngineConstants.CASTLING_EMPTY_SQUARES[side][1] & occupiedSquares) == 0
			&& !Check.isKingIncheckIncludingKing(MoveGenerationConstants.betweenKingAndRook[side][1], bitboard, opSide, side, occupiedSquares)
			&& !Check.isKingIncheckIncludingKing(MoveGenerationConstants.castlingTarget[side][1], bitboard, opSide, side, occupiedSquares)
					) {
				return true;
			}
			return false;
		default:
			throw new RuntimeException("Internal Error");
		}
	}

}

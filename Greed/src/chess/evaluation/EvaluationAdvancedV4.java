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
package chess.evaluation;

import chess.engine.EngineConstants;
import chess.engine.PawnHashTable;
import chess.engine.PawnTranspositionElement;
import chess.engine.TranspositionTable;
import chess.gui.GuiConstants;

public class EvaluationAdvancedV4 {
	
	private static final int BONUS_BISHOP_PAIR = 20;
	private static final int BONUS_CASTLING_RIGHT = 10;
	private static final int PENALTY_DOUBLED_PAWN = 10;
	private static final int PENALTY_ISOLATED_PAWN = 20;
	private static final int PENALTY_BACKWARD_PAWN = 20;
	private static final int BONUS_PASSED_PAWN = 20;
	private static final int BONUS_ROOK_ON_SEMI_OPEN_FILE = 10;
//	private static final int BONUS_ROOK_ON_OPEN_FILE = 10;
	private static final int BONUS_ROOK_BATTERY = 10;
	
	private static final boolean usePsqt = true;
	private static final boolean useBishopPair = true;
	
	public static int evaluate(long[] bitboard, byte[][] castlingRights, int side, long pawnZobristKey, PawnHashTable pawnHashTable){
		
		// TODO COMMENT OUT HERE.
		// TODO COMMENT OUT HERE.
		// TODO COMMENT OUT HERE.
		// TODO COMMENT OUT HERE.
		if (pawnZobristKey != TranspositionTable.getPawnZobristKey(bitboard)) {
			throw new RuntimeException("PATLADI");
		}
		
		int eval = 0;
		
		long wp = bitboard[EngineConstants.WHITE_PAWN];
		long bp = bitboard[EngineConstants.BLACK_PAWN];
		
		PawnTranspositionElement ttElement = pawnHashTable.probe(pawnZobristKey);
		if(ttElement != null && ttElement.zobristKey == pawnZobristKey){
			eval += ttElement.score;
		} else {
			int pawnScore = evalPawn(bitboard, wp, bp);
			eval += pawnScore;
			pawnHashTable.recordTranspositionTable(pawnZobristKey, pawnScore);
		}
		
		int whiteTotalPieceValue = 0;
		int blackTotalPieceValue = 0;
		
		
		int wkCount = Long.bitCount(bitboard[EngineConstants.WHITE_KNIGHT]);
		int wbCount = Long.bitCount(bitboard[EngineConstants.WHITE_BISHOP]);
		int wrCount = Long.bitCount(bitboard[EngineConstants.WHITE_ROOK]);
		int wqCount = Long.bitCount(bitboard[EngineConstants.WHITE_QUEEN]);
		
		int bkCount = Long.bitCount(bitboard[EngineConstants.BLACK_KNIGHT]);
		int bbCount = Long.bitCount(bitboard[EngineConstants.BLACK_BISHOP]);
		int brCount = Long.bitCount(bitboard[EngineConstants.BLACK_ROOK]);
		int bqCount = Long.bitCount(bitboard[EngineConstants.BLACK_QUEEN]);


		
		whiteTotalPieceValue = wkCount * EngineConstants.WHITE_KNIGHT_V + wbCount * EngineConstants.WHITE_BISHOP_V + wrCount * EngineConstants.WHITE_ROOK_V + wqCount * EngineConstants.WHITE_QUEEN_V;
		blackTotalPieceValue = bkCount * EngineConstants.BLACK_KNIGHT_V + bbCount * EngineConstants.BLACK_BISHOP_V + brCount * EngineConstants.BLACK_ROOK_V + bqCount * EngineConstants.BLACK_QUEEN_V;
		
		long whitePawns = bitboard[EngineConstants.WHITE_PAWN];
		long blackPawns = bitboard[EngineConstants.BLACK_PAWN];
		
		int trailingZeros;
		long fromBitboard;

		
		fromBitboard=bitboard[EngineConstants.WHITE_KNIGHT];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			if (usePsqt) {
				eval += PieceSquareTable.positionalValue[EngineConstants.WHITE_KNIGHT][trailingZeros];
			}
			fromBitboard &= (fromBitboard - 1);
		}
		
		
		fromBitboard=bitboard[EngineConstants.WHITE_BISHOP];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			if (usePsqt) {
				eval += PieceSquareTable.positionalValue[EngineConstants.WHITE_BISHOP][trailingZeros];
			}
			fromBitboard &= (fromBitboard - 1);
		}

		
		fromBitboard=bitboard[EngineConstants.WHITE_ROOK];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			/**
			 * Bonus For Rook On Semi Open File.
			 * */
			//
			int fileIndex = (int) (trailingZeros & 0x7L);
			long file = EngineConstants.FILE[fileIndex];
			eval = eval - BONUS_ROOK_ON_SEMI_OPEN_FILE * Long.bitCount(file & whitePawns);
			//
			
			if (usePsqt) {
				eval += PieceSquareTable.positionalValue[EngineConstants.WHITE_ROOK][trailingZeros];
			}
			fromBitboard &= (fromBitboard - 1);
		}
		
		fromBitboard=bitboard[EngineConstants.WHITE_QUEEN];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			if (usePsqt) {
				eval += PieceSquareTable.positionalValue[EngineConstants.WHITE_QUEEN][trailingZeros];
			}
			fromBitboard &= (fromBitboard - 1);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_KNIGHT];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			if (usePsqt) {
				eval += PieceSquareTable.positionalValue[EngineConstants.BLACK_KNIGHT][trailingZeros];
			}
			fromBitboard &= (fromBitboard - 1);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_BISHOP];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			if (usePsqt) {
				eval += PieceSquareTable.positionalValue[EngineConstants.BLACK_BISHOP][trailingZeros];
			}
			fromBitboard &= (fromBitboard - 1);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_ROOK];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			/**
			 * Bonus For Rook On Semi Open File.
			 * */
			//
			int fileIndex = (int) (trailingZeros & 0x7L);
			long file = EngineConstants.FILE[fileIndex];
			eval = eval + BONUS_ROOK_ON_SEMI_OPEN_FILE * Long.bitCount(file & blackPawns);
			//
			
			if (usePsqt) {
				eval += PieceSquareTable.positionalValue[EngineConstants.BLACK_ROOK][trailingZeros];
			}
			fromBitboard &= (fromBitboard - 1);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_QUEEN];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			if (usePsqt) {
				eval += PieceSquareTable.positionalValue[EngineConstants.BLACK_QUEEN][trailingZeros];
			}
			fromBitboard &= (fromBitboard - 1);
		}
		
		
		boolean isEndgame = false;
		if (side == GuiConstants.WHITES_TURN) {
			if (blackTotalPieceValue >= -EngineConstants.END_GAME_THRESHOLD) {
				isEndgame = true;
			}
		} else {
			if (whiteTotalPieceValue <= EngineConstants.END_GAME_THRESHOLD) {
				isEndgame = true;
			}
			
		}
		
		if (isEndgame) {
			fromBitboard=bitboard[EngineConstants.BLACK_KING];
			while (fromBitboard != 0) {
				trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
				
				if (usePsqt) {
					eval += PieceSquareTable.positionalValueKingEnding[EngineConstants.BLACK][trailingZeros];
				}
				fromBitboard &= (fromBitboard - 1);
			}
			
			fromBitboard=bitboard[EngineConstants.WHITE_KING];
			while (fromBitboard != 0) {
				trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
				
				if (usePsqt) {
					eval += PieceSquareTable.positionalValueKingEnding[EngineConstants.WHITE][trailingZeros];
				}
				fromBitboard &= (fromBitboard - 1);
			}
		} else {
			fromBitboard=bitboard[EngineConstants.BLACK_KING];
			while (fromBitboard != 0) {
				trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
				
				if (usePsqt) {
					eval += PieceSquareTable.positionalValue[EngineConstants.BLACK_KING][trailingZeros];
				}
				fromBitboard &= (fromBitboard - 1);
			}
			
			fromBitboard=bitboard[EngineConstants.WHITE_KING];
			while (fromBitboard != 0) {
				trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
				
				if (usePsqt) {
					eval += PieceSquareTable.positionalValue[EngineConstants.WHITE_KING][trailingZeros];
				}
				fromBitboard &= (fromBitboard - 1);
			}
		}
		
		
		eval += castlingRights[GuiConstants.WHITES_TURN][0] * BONUS_CASTLING_RIGHT;
		eval += castlingRights[GuiConstants.WHITES_TURN][1] * BONUS_CASTLING_RIGHT;
		
		eval -= castlingRights[GuiConstants.BLACKS_TURN][0] * BONUS_CASTLING_RIGHT;
		eval -= castlingRights[GuiConstants.BLACKS_TURN][1] * BONUS_CASTLING_RIGHT;
		
		if (useBishopPair) {
			if (wbCount == 2) {
				eval += BONUS_BISHOP_PAIR;
			}
			if (bbCount == 2) {
				eval -= BONUS_BISHOP_PAIR;
			}
		}
		

		

		return  eval + EngineConstants.WHITE_KNIGHT_V 	* (wkCount - bkCount) +
				   		EngineConstants.WHITE_BISHOP_V 	* (wbCount - bbCount) +
				   		EngineConstants.WHITE_ROOK_V 	* (wrCount - brCount) +
				   		EngineConstants.WHITE_QUEEN_V 	* (wqCount - bqCount);
	}
	
	public static int evalPawn(long[] bitboard, long wp, long bp) {
		int wpCount = Long.bitCount(wp);
		int bpCount = Long.bitCount(bp);
		int eval = 0;
		
		/**
		 * Passed pawn bonus.
		 **/
		eval += (Long.bitCount(PassedPawn.whitePassedPawns(wp, bp)) - Long.bitCount(PassedPawn.blackPassedPawns(bp, wp))) * BONUS_PASSED_PAWN;
		
		/**
		 * Penalty backward pawns 
		 **/
		eval -= (Long.bitCount(BackwardPawn.whiteBackwardPawns(wp, bp)) - Long.bitCount(BackwardPawn.blackBackwardPawns(bp, wp))) * PENALTY_BACKWARD_PAWN;
		
		int trailingZeros;
		long fromBitboard=bitboard[EngineConstants.WHITE_PAWN];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			
			// Isolated Pawns
			if ((EngineConstants.neighborFiles[trailingZeros] & wp) == 0) {
				eval -= PENALTY_ISOLATED_PAWN;
			}
			
			if (usePsqt) {
				eval += PieceSquareTable.positionalValue[EngineConstants.WHITE_PAWN][trailingZeros];
			}
			fromBitboard &= (fromBitboard - 1);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_PAWN];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			
			
			// Isolated Pawns
			if ((EngineConstants.neighborFiles[trailingZeros] & bp) == 0) {
				eval += PENALTY_ISOLATED_PAWN;
			}
			
			if (usePsqt) {
				eval += PieceSquareTable.positionalValue[EngineConstants.BLACK_PAWN][trailingZeros];
			}
			fromBitboard &= (fromBitboard - 1);
		}
		
		int pawnCount = Long.bitCount(wp & EngineConstants.FILE_A);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(wp & EngineConstants.FILE_B);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(wp & EngineConstants.FILE_C);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(wp & EngineConstants.FILE_D);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(wp & EngineConstants.FILE_E);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(wp & EngineConstants.FILE_F);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(wp & EngineConstants.FILE_G);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(wp & EngineConstants.FILE_H);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		
		pawnCount = Long.bitCount(bp & EngineConstants.FILE_A);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(bp & EngineConstants.FILE_B);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(bp & EngineConstants.FILE_C);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(bp & EngineConstants.FILE_D);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(bp & EngineConstants.FILE_E);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(bp & EngineConstants.FILE_F);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(bp & EngineConstants.FILE_G);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(bp & EngineConstants.FILE_H);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		
		eval += (EngineConstants.WHITE_PAWN_V 	* (wpCount - bpCount)) ;
		
		return eval;
	}
	
}

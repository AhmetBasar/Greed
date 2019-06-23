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

import chess.gui.GuiConstants;

public class EvaluationAdvancedV4 {
	
	private static final int BONUS_BISHOP_PAIR = 20;
	private static final int BONUS_CASTLING_RIGHT = 10;
	private static final int PENALTY_DOUBLED_PAWN = 10;
	private static final int PENALTY_ISOLATED_PAWN = 20;
	private static final int BONUS_ROOK_ON_SEMI_OPEN_FILE = 10;
//	private static final int BONUS_ROOK_ON_OPEN_FILE = 10;
	private static final int BONUS_ROOK_BATTERY = 10;
	
	public static int evaluate(long[] bitboard, byte[][] castlingRights, int side){
		int eval = 0;
		
		int whiteTotalPieceValue = 0;
		int blackTotalPieceValue = 0;
		
		int wpCount = Long.bitCount(bitboard[EngineConstants.WHITE_PAWN]);
		int wkCount = Long.bitCount(bitboard[EngineConstants.WHITE_KNIGHT]);
		int wbCount = Long.bitCount(bitboard[EngineConstants.WHITE_BISHOP]);
		int wrCount = Long.bitCount(bitboard[EngineConstants.WHITE_ROOK]);
		int wqCount = Long.bitCount(bitboard[EngineConstants.WHITE_QUEEN]);
		
		int bpCount = Long.bitCount(bitboard[EngineConstants.BLACK_PAWN]);
		int bkCount = Long.bitCount(bitboard[EngineConstants.BLACK_KNIGHT]);
		int bbCount = Long.bitCount(bitboard[EngineConstants.BLACK_BISHOP]);
		int brCount = Long.bitCount(bitboard[EngineConstants.BLACK_ROOK]);
		int bqCount = Long.bitCount(bitboard[EngineConstants.BLACK_QUEEN]);
		
		whiteTotalPieceValue = wkCount * EngineConstants.WHITE_KNIGHT_V + wbCount * EngineConstants.WHITE_BISHOP_V + wrCount * EngineConstants.WHITE_ROOK_V + wqCount * EngineConstants.WHITE_QUEEN_V;
		blackTotalPieceValue = bkCount * EngineConstants.BLACK_KNIGHT_V + bbCount * EngineConstants.BLACK_BISHOP_V + brCount * EngineConstants.BLACK_ROOK_V + bqCount * EngineConstants.BLACK_QUEEN_V;
		
		long whitePawns = bitboard[EngineConstants.WHITE_PAWN];
		long blackPawns = bitboard[EngineConstants.BLACK_PAWN];
		
		int trailingZeros;
		long fromBitboard=bitboard[EngineConstants.WHITE_PAWN];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			
			// Isolated Pawns
			if ((EngineConstants.neighborFiles[trailingZeros] & whitePawns) == 0) {
				eval -= PENALTY_ISOLATED_PAWN;
			}
			
			eval += positionalValue[EngineConstants.WHITE_PAWN][trailingZeros];
			fromBitboard &= (fromBitboard - 1);
		}
		
		fromBitboard=bitboard[EngineConstants.WHITE_KNIGHT];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			eval += positionalValue[EngineConstants.WHITE_KNIGHT][trailingZeros];
			fromBitboard &= (fromBitboard - 1);
		}
		
		
		fromBitboard=bitboard[EngineConstants.WHITE_BISHOP];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			eval += positionalValue[EngineConstants.WHITE_BISHOP][trailingZeros];
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
			
			eval += positionalValue[EngineConstants.WHITE_ROOK][trailingZeros];
			fromBitboard &= (fromBitboard - 1);
		}
		
		fromBitboard=bitboard[EngineConstants.WHITE_QUEEN];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			eval += positionalValue[EngineConstants.WHITE_QUEEN][trailingZeros];
			fromBitboard &= (fromBitboard - 1);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_PAWN];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			
			
			// Isolated Pawns
			if ((EngineConstants.neighborFiles[trailingZeros] & blackPawns) == 0) {
				eval += PENALTY_ISOLATED_PAWN;
			}
			
			eval += positionalValue[EngineConstants.BLACK_PAWN][trailingZeros];
			fromBitboard &= (fromBitboard - 1);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_KNIGHT];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			eval += positionalValue[EngineConstants.BLACK_KNIGHT][trailingZeros];
			fromBitboard &= (fromBitboard - 1);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_BISHOP];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			eval += positionalValue[EngineConstants.BLACK_BISHOP][trailingZeros];
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
			
			eval += positionalValue[EngineConstants.BLACK_ROOK][trailingZeros];
			fromBitboard &= (fromBitboard - 1);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_QUEEN];
		while (fromBitboard != 0) {
			trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
			eval += positionalValue[EngineConstants.BLACK_QUEEN][trailingZeros];
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
				
				eval += positionalValueKingEnding[EngineConstants.BLACK][trailingZeros];
				fromBitboard &= (fromBitboard - 1);
			}
			
			fromBitboard=bitboard[EngineConstants.WHITE_KING];
			while (fromBitboard != 0) {
				trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
				
				eval += positionalValueKingEnding[EngineConstants.WHITE][trailingZeros];
				fromBitboard &= (fromBitboard - 1);
			}
		} else {
			fromBitboard=bitboard[EngineConstants.BLACK_KING];
			while (fromBitboard != 0) {
				trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
				
				eval += positionalValue[EngineConstants.BLACK_KING][trailingZeros];
				fromBitboard &= (fromBitboard - 1);
			}
			
			fromBitboard=bitboard[EngineConstants.WHITE_KING];
			while (fromBitboard != 0) {
				trailingZeros = Long.numberOfTrailingZeros(fromBitboard);
				
				eval += positionalValue[EngineConstants.WHITE_KING][trailingZeros];
				fromBitboard &= (fromBitboard - 1);
			}
		}
		
		
		eval += castlingRights[GuiConstants.WHITES_TURN][0] * BONUS_CASTLING_RIGHT;
		eval += castlingRights[GuiConstants.WHITES_TURN][1] * BONUS_CASTLING_RIGHT;
		
		eval -= castlingRights[GuiConstants.BLACKS_TURN][0] * BONUS_CASTLING_RIGHT;
		eval -= castlingRights[GuiConstants.BLACKS_TURN][1] * BONUS_CASTLING_RIGHT;
		
		if (wbCount == 2) {
			eval += BONUS_BISHOP_PAIR;
		}
		if (bbCount == 2) {
			eval -= BONUS_BISHOP_PAIR;
		}
		
		int pawnCount = Long.bitCount(whitePawns & EngineConstants.FILE_A);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(whitePawns & EngineConstants.FILE_B);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(whitePawns & EngineConstants.FILE_C);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(whitePawns & EngineConstants.FILE_D);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(whitePawns & EngineConstants.FILE_E);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(whitePawns & EngineConstants.FILE_F);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(whitePawns & EngineConstants.FILE_G);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(whitePawns & EngineConstants.FILE_H);
		eval -= (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		
		pawnCount = Long.bitCount(blackPawns & EngineConstants.FILE_A);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(blackPawns & EngineConstants.FILE_B);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(blackPawns & EngineConstants.FILE_C);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(blackPawns & EngineConstants.FILE_D);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(blackPawns & EngineConstants.FILE_E);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(blackPawns & EngineConstants.FILE_F);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(blackPawns & EngineConstants.FILE_G);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		pawnCount = Long.bitCount(blackPawns & EngineConstants.FILE_H);
		eval += (pawnCount - 1) * PENALTY_DOUBLED_PAWN;
		

		return  eval + EngineConstants.WHITE_PAWN_V 	* (wpCount - bpCount) + 
				   		EngineConstants.WHITE_KNIGHT_V 	* (wkCount - bkCount) +
				   		EngineConstants.WHITE_BISHOP_V 	* (wbCount - bbCount) +
				   		EngineConstants.WHITE_ROOK_V 	* (wrCount - brCount) +
				   		EngineConstants.WHITE_QUEEN_V 	* (wqCount - bqCount);
	}

	// https://www.chessprogramming.org/Simplified_Evaluation_Function
	public static final int[][] positionalValue={
			
			{
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0
			},
			{
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0,
				0,  0,  0,  0,  0,  0,  0,  0
			},
			{
				0,  0,  0,  0,  0,  0,  0,  0,
				5, 10, 10,-20,-20, 10, 10,  5,
				5, -5,-10,  0,  0,-10, -5,  5,
				0,  0,  0, 20, 20,  0,  0,  0,
				5,  5, 10, 25, 25, 10,  5,  5,
				10, 10, 20, 30, 30, 20, 10, 10,
				50, 50, 50, 50, 50, 50, 50, 50,
				0,  0,  0,  0,  0,  0,  0,  0
			},
			{
				0,  0,  0,  0,  0,  0,  0,  0,
				-50,-50,-50,-50,-50,-50,-50,-50,
				-10,-10,-20,-30,-30,-20,-10,-10,
				-5, -5,-10,-25,-25,-10, -5, -5,
				0,  0,  0,-20,-20,  0,  0,  0,
				-5,  5, 10,  0,  0, 10,  5, -5,
				-5,-10,-10, 20, 20,-10,-10, -5,
				0,  0,  0,  0,  0,  0,  0,  0
			},
			{
				-50,-40,-30,-30,-30,-30,-40,-50,
				-40,-20,  0,  5,  5,  0,-20,-40,
				-30,  5, 10, 15, 15, 10,  5,-30,
				-30,  0, 15, 20, 20, 15,  0,-30,
				-30,  5, 15, 20, 20, 15,  5,-30,
				-30,  0, 10, 15, 15, 10,  0,-30,
				-40,-20,  0,  0,  0,  0,-20,-40,
				-50,-40,-30,-30,-30,-30,-40,-50
			},
			
			{
				50, 40, 30, 30, 30, 30, 40, 50,
				40, 20,  0,  0,  0,  0, 20, 40,
				30,  0,-10,-15,-15,-10,  0, 30,
				30, -5,-15,-20,-20,-15, -5, 30,
				30,  0,-15,-20,-20,-15,  0, 30,
				30, -5,-10,-15,-15,-10, -5, 30,
				40, 20,  0, -5, -5,  0, 20, 40,
				50, 40, 30, 30, 30, 30, 40, 50
			},
			{
				-20,-10,-10,-10,-10,-10,-10,-20,
				-10,  5,  0,  0,  0,  0,  5,-10,
				-10, 10, 10, 10, 10, 10, 10,-10,
				-10,  0, 10, 10, 10, 10,  0,-10,
				-10,  5,  5, 10, 10,  5,  5,-10,
				-10,  0,  5, 10, 10,  5,  0,-10,
				-10,  0,  0,  0,  0,  0,  0,-10,
				-20,-10,-10,-10,-10,-10,-10,-20
			},
			{
				20, 10, 10, 10, 10, 10, 10, 20,
				10,  0,  0,  0,  0,  0,  0, 10,
				10,  0, -5,-10,-10, -5,  0, 10,
				10, -5, -5,-10,-10, -5, -5, 10,
				10,  0,-10,-10,-10,-10,  0, 10,
				10,-10,-10,-10,-10,-10,-10, 10,
				10, -5,  0,  0,  0,  0, -5, 10,
				20, 10, 10, 10, 10, 10, 10, 20
			},
			{
				0,  0,  0,  5,  5,  0,  0,  0,
				-5,  0,  0,  0,  0,  0,  0, -5,
				-5,  0,  0,  0,  0,  0,  0, -5,
				-5,  0,  0,  0,  0,  0,  0, -5,
				-5,  0,  0,  0,  0,  0,  0, -5,
				-5,  0,  0,  0,  0,  0,  0, -5,
				5, 10, 10, 10, 10, 10, 10,  5,
				0,  0,  0,  0,  0,  0,  0,  0
			},
			{
				0,  0,  0,  0,  0,  0,  0,  0,
				-5,-10,-10,-10,-10,-10,-10, -5,
				5,  0,  0,  0,  0,  0,  0,  5,
				5,  0,  0,  0,  0,  0,  0,  5,
				5,  0,  0,  0,  0,  0,  0,  5,
				5,  0,  0,  0,  0,  0,  0,  5,
				5,  0,  0,  0,  0,  0,  0,  5,
				0,  0,  0, -5, -5,  0,  0,  0
			},
			{
				-20,-10,-10, -5, -5,-10,-10,-20,
				-10,  0,  5,  0,  0,  0,  0,-10,
				-10,  5,  5,  5,  5,  5,  0,-10,
				0,  0,  5,  5,  5,  5,  0, -5,
				-5,  0,  5,  5,  5,  5,  0, -5,
				-10,  0,  5,  5,  5,  5,  0,-10,
				-10,  0,  0,  0,  0,  0,  0,-10,
				-20,-10,-10, -5, -5,-10,-10,-20
			},
			{
				20, 10, 10,  5,  5, 10, 10, 20,
				10,  0,  0,  0,  0,  0,  0, 10,
				10,  0, -5, -5, -5, -5,  0, 10,
				5,  0, -5, -5, -5, -5,  0,  5,
				0,  0, -5, -5, -5, -5,  0,  5,
				10, -5, -5, -5, -5, -5,  0, 10,
				10,  0, -5,  0,  0,  0,  0, 10,
				20, 10, 10,  5,  5, 10, 10, 20
			},
			{
				20, 30, 10,  0,  0, 10, 30, 20,
				20, 20,  0,  0,  0,  0, 20, 20,
				-10,-20,-20,-20,-20,-20,-20,-10,
				-20,-30,-30,-40,-40,-30,-30,-20,
				-30,-40,-40,-50,-50,-40,-40,-30,
				-30,-40,-40,-50,-50,-40,-40,-30,
				-30,-40,-40,-50,-50,-40,-40,-30,
				-30,-40,-40,-50,-50,-40,-40,-30
			},
			{
				30, 40, 40, 50, 50, 40, 40, 30,
				30, 40, 40, 50, 50, 40, 40, 30,
				30, 40, 40, 50, 50, 40, 40, 30,
				30, 40, 40, 50, 50, 40, 40, 30,
				20, 30, 30, 40, 40, 30, 30, 20,
				10, 20, 20, 20, 20, 20, 20, 10,
				-20,-20,  0,  0,  0,  0,-20,-20,
				-20,-30,-10,  0,  0,-10,-30,-20
			}
	};
	
	// https://www.chessprogramming.org/Simplified_Evaluation_Function
	public static final int[][] positionalValueKingEnding={
			{
			  -50,-30,-30,-30,-30,-30,-30,-50,
			  -30,-30,  0,  0,  0,  0,-30,-30,
			  -30,-10, 20, 30, 30, 20,-10,-30,
			  -30,-10, 30, 40, 40, 30,-10,-30,
			  -30,-10, 30, 40, 40, 30,-10,-30,
			  -30,-10, 20, 30, 30, 20,-10,-30,
			  -30,-20,-10,  0,  0,-10,-20,-30,
			  -50,-40,-30,-20,-20,-30,-40,-50,
			},
			{
			   50, 40, 30, 20, 20, 30, 40, 50,
			   30, 20, 10,  0,  0, 10, 20, 30,
			   30, 10,-20,-30,-30,-20, 10, 30,
			   30, 10,-30,-40,-40,-30, 10, 30,
			   30, 10,-30,-40,-40,-30, 10, 30,
			   30, 10,-20,-30,-30,-20, 10, 30,
			   30, 30,  0,  0,  0,  0, 30, 30,
			   50, 30, 30, 30, 30, 30, 30, 50,
			},
	};
}

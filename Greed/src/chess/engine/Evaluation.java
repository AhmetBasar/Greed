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

public class Evaluation {
	
	public static int evaluate(long[] bitboard){
		int eval = 0;
		
		int trailingZeros;
		long fromBitboard=bitboard[EngineConstants.WHITE_PAWN];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			eval += positionalValue[EngineConstants.WHITE_PAWN][trailingZeros];
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}
		
		fromBitboard=bitboard[EngineConstants.WHITE_KNIGHT];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			eval += positionalValue[EngineConstants.WHITE_KNIGHT][trailingZeros];
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}
		
		fromBitboard=bitboard[EngineConstants.WHITE_BISHOP];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			eval += positionalValue[EngineConstants.WHITE_BISHOP][trailingZeros];
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}
		
		fromBitboard=bitboard[EngineConstants.WHITE_ROOK];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			eval += positionalValue[EngineConstants.WHITE_ROOK][trailingZeros];
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}
		
		fromBitboard=bitboard[EngineConstants.WHITE_QUEEN];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			eval += positionalValue[EngineConstants.WHITE_QUEEN][trailingZeros];
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}
		
		fromBitboard=bitboard[EngineConstants.WHITE_KING];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			eval += positionalValue[EngineConstants.WHITE_KING][trailingZeros];
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_PAWN];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			eval += positionalValue[EngineConstants.BLACK_PAWN][trailingZeros];
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_KNIGHT];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			eval += positionalValue[EngineConstants.BLACK_KNIGHT][trailingZeros];
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_BISHOP];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			eval += positionalValue[EngineConstants.BLACK_BISHOP][trailingZeros];
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_ROOK];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			eval += positionalValue[EngineConstants.BLACK_ROOK][trailingZeros];
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_QUEEN];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			eval += positionalValue[EngineConstants.BLACK_QUEEN][trailingZeros];
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_KING];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			eval += positionalValue[EngineConstants.BLACK_KING][trailingZeros];
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}

		return  eval + EngineConstants.WHITE_PAWN_V 	* (Long.bitCount(bitboard[EngineConstants.WHITE_PAWN]) 		- Long.bitCount(bitboard[EngineConstants.BLACK_PAWN])) 		+ 
				   		EngineConstants.WHITE_KNIGHT_V 	* (Long.bitCount(bitboard[EngineConstants.WHITE_KNIGHT]) 	- Long.bitCount(bitboard[EngineConstants.BLACK_KNIGHT]))	+
				   		EngineConstants.WHITE_BISHOP_V 	* (Long.bitCount(bitboard[EngineConstants.WHITE_BISHOP]) 	- Long.bitCount(bitboard[EngineConstants.BLACK_BISHOP]))  	+
				   		EngineConstants.WHITE_ROOK_V 	* (Long.bitCount(bitboard[EngineConstants.WHITE_ROOK]) 		- Long.bitCount(bitboard[EngineConstants.BLACK_ROOK]))  	+
				   		EngineConstants.WHITE_QUEEN_V 	* (Long.bitCount(bitboard[EngineConstants.WHITE_QUEEN]) 	- Long.bitCount(bitboard[EngineConstants.BLACK_QUEEN]))  	+
				   		EngineConstants.WHITE_KING_V 	* (Long.bitCount(bitboard[EngineConstants.WHITE_KING]) 		- Long.bitCount(bitboard[EngineConstants.BLACK_KING]))  	;
		
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
}

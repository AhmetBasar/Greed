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

import chess.movegen.MagicBitboard;

public class LegalityV4 {
	public boolean isKingInCheck(long[] bitboard, int side) {
		int opSide = side ^ 1;
		long toBitboard;
		long fromBitboard;
		long opSideBitBoard;
		int from;
		long occupiedSquares = bitboard[EngineConstants.WHITE_PAWN] | bitboard[EngineConstants.WHITE_KNIGHT]
				| bitboard[EngineConstants.WHITE_BISHOP] | bitboard[EngineConstants.WHITE_ROOK]
				| bitboard[EngineConstants.WHITE_QUEEN] | bitboard[EngineConstants.WHITE_KING]
				| bitboard[EngineConstants.BLACK_PAWN] | bitboard[EngineConstants.BLACK_KNIGHT]
				| bitboard[EngineConstants.BLACK_BISHOP] | bitboard[EngineConstants.BLACK_ROOK]
				| bitboard[EngineConstants.BLACK_QUEEN] | bitboard[EngineConstants.BLACK_KING];

		// PAWNS
		fromBitboard = bitboard[side | EngineConstants.KING];
		from = Long.numberOfTrailingZeros(fromBitboard);
		opSideBitBoard = bitboard[opSide | EngineConstants.PAWN];

		// PAWN ATTACKS
		if ((EngineConstants.PAWN_ATTACK_LOOKUP[side][from] & opSideBitBoard) != 0) {
			return true;
		}
		
		// KNIGHT ATTACKS.
		opSideBitBoard = bitboard[opSide | EngineConstants.KNIGHT];
		//TODO : will be cleaned by deleting.
//		if(from == 64){
//			System.out.println("INSANE ERROR");
//		}
		toBitboard = EngineConstants.KNIGHT_LOOKUP[from]
				// and opSideBitBoard..
				& opSideBitBoard;
		if (toBitboard != 0) {
			return true;
		}

		// KING ATTACKS.
		opSideBitBoard = bitboard[opSide | EngineConstants.KING];
		toBitboard = EngineConstants.KING_LOOKUP[from]
				// and opSideBitBoard..
				& opSideBitBoard;
		if (toBitboard != 0) {
			return true;
		}

		// ROOK ATTACKS.
		opSideBitBoard = bitboard[opSide | EngineConstants.ROOK];
		
		toBitboard = MagicBitboard.generateRookMoves(from, occupiedSquares) & opSideBitBoard;

		if (toBitboard != 0) {
			return true;
		}

		// BISHOP ATTACKS.
		opSideBitBoard = bitboard[opSide | EngineConstants.BISHOP];
		
		toBitboard = MagicBitboard.generateBishopMoves(from, occupiedSquares) & opSideBitBoard;

		if (toBitboard != 0) {
			return true;
		}

		// QUEEN ATTACKS.
		
		opSideBitBoard = bitboard[opSide | EngineConstants.QUEEN];

		toBitboard = MagicBitboard.generateQueenMoves(from, occupiedSquares) & opSideBitBoard;
		
		if (toBitboard != 0) {
			return true;
		}

		return false;
	}
}

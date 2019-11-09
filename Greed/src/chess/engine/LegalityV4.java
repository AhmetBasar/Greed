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

		long rightMoves, leftMoves, upMoves, downMoves, moves_45, moves_135, moves_225, moves_315, lookup;
		
		// ROOK ATTACKS.
		opSideBitBoard = bitboard[opSide | EngineConstants.ROOK];

		lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.RIGHT];
		rightMoves = lookup & occupiedSquares;
		rightMoves = (rightMoves << 1) | 
				     (rightMoves << 2) | 
				     (rightMoves << 3) | 
				     (rightMoves << 4) | 
				     (rightMoves << 5) | 
				     (rightMoves << 6);
		rightMoves = (rightMoves & lookup) ^ lookup;

		lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.LEFT];
		leftMoves = lookup & occupiedSquares;
		leftMoves = (leftMoves >>> 1) | 
				    (leftMoves >>> 2) | 
				    (leftMoves >>> 3) | 
				    (leftMoves >>> 4) | 
				    (leftMoves >>> 5) | 
				    (leftMoves >>> 6);
		leftMoves = (leftMoves & lookup) ^ lookup;

		lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.UP];
		upMoves = lookup & occupiedSquares;
		upMoves = (upMoves << 8)  | 
				  (upMoves << 16) | 
				  (upMoves << 24) | 
				  (upMoves << 32) | 
				  (upMoves << 40) | 
				  (upMoves << 48);
		upMoves = (upMoves & lookup) ^ lookup;

		lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.DOWN];
		downMoves = lookup & occupiedSquares;
		downMoves = (downMoves >>> 8)  | 
				    (downMoves >>> 16) | 
				    (downMoves >>> 24) | 
				    (downMoves >>> 32) | 
				    (downMoves >>> 40) | 
				    (downMoves >>> 48);
		downMoves = (downMoves & lookup) ^ lookup;

		toBitboard = (rightMoves | leftMoves | upMoves | downMoves)
				// and opSideBitBoard..
				& opSideBitBoard;
		if (toBitboard != 0) {
			return true;
		}

		// BISHOP ATTACKS.
		opSideBitBoard = bitboard[opSide | EngineConstants.BISHOP];

		lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_45];
		moves_45 = lookup & occupiedSquares;
		moves_45 = (moves_45 << 9)  | 
				   (moves_45 << 18) | 
				   (moves_45 << 27) | 
				   (moves_45 << 36) | 
				   (moves_45 << 45) | 
				   (moves_45 << 54);
		moves_45 = (moves_45 & lookup) ^ lookup;

		lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_135];
		moves_135 = lookup & occupiedSquares;
		moves_135 = (moves_135 << 7)  | 
				    (moves_135 << 14) | 
				    (moves_135 << 21) | 
				    (moves_135 << 28) | 
				    (moves_135 << 35) | 
				    (moves_135 << 42);
		moves_135 = (moves_135 & lookup) ^ lookup;

		lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_225];
		moves_225 = lookup & occupiedSquares;
		moves_225 = (moves_225 >>> 9)  | 
				    (moves_225 >>> 18) | 
				    (moves_225 >>> 27) | 
				    (moves_225 >>> 36) | 
				    (moves_225 >>> 45) | 
				    (moves_225 >>> 54);
		moves_225 = (moves_225 & lookup) ^ lookup;

		lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_315];
		moves_315 = lookup & occupiedSquares;
		moves_315 = (moves_315 >>> 7)  | 
				    (moves_315 >>> 14) | 
				    (moves_315 >>> 21) | 
				    (moves_315 >>> 28) | 
				    (moves_315 >>> 35) | 
				    (moves_315 >>> 42);
		moves_315 = (moves_315 & lookup) ^ lookup;

		toBitboard = (moves_45 | moves_135 | moves_225 | moves_315)
				// and opSideBitBoard..
				& opSideBitBoard;
		if (toBitboard != 0) {
			return true;
		}

		// QUEEN ATTACKS.
		
		opSideBitBoard = bitboard[opSide | EngineConstants.QUEEN];

		lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.RIGHT];
		rightMoves = lookup & occupiedSquares;
		rightMoves = (rightMoves << 1) | 
				     (rightMoves << 2) | 
				     (rightMoves << 3) | 
				     (rightMoves << 4) | 
				     (rightMoves << 5) | 
				     (rightMoves << 6);
		rightMoves = (rightMoves & lookup) ^ lookup;

		lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.LEFT];
		leftMoves = lookup & occupiedSquares;
		leftMoves = (leftMoves >>> 1) | 
				    (leftMoves >>> 2) | 
				    (leftMoves >>> 3) | 
				    (leftMoves >>> 4) | 
				    (leftMoves >>> 5) | 
				    (leftMoves >>> 6);
		leftMoves = (leftMoves & lookup) ^ lookup;

		lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.UP];
		upMoves = lookup & occupiedSquares;
		upMoves = (upMoves << 8)  | 
				  (upMoves << 16) | 
				  (upMoves << 24) | 
				  (upMoves << 32) | 
				  (upMoves << 40) | 
				  (upMoves << 48);
		upMoves = (upMoves & lookup) ^ lookup;

		lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.DOWN];
		downMoves = lookup & occupiedSquares;
		downMoves = (downMoves >>> 8)  | 
				    (downMoves >>> 16) | 
				    (downMoves >>> 24) | 
				    (downMoves >>> 32) | 
				    (downMoves >>> 40) | 
				    (downMoves >>> 48);
		downMoves = (downMoves & lookup) ^ lookup;

		lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_45];
		moves_45 = lookup & occupiedSquares;
		moves_45 = (moves_45 << 9)  | 
				   (moves_45 << 18) | 
				   (moves_45 << 27) | 
				   (moves_45 << 36) | 
				   (moves_45 << 45) | 
				   (moves_45 << 54);
		moves_45 = (moves_45 & lookup) ^ lookup;

		lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_135];
		moves_135 = lookup & occupiedSquares;
		moves_135 = (moves_135 << 7)  | 
				    (moves_135 << 14) | 
				    (moves_135 << 21) | 
				    (moves_135 << 28) | 
				    (moves_135 << 35) | 
				    (moves_135 << 42);
		moves_135 = (moves_135 & lookup) ^ lookup;

		lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_225];
		moves_225 = lookup & occupiedSquares;
		moves_225 = (moves_225 >>> 9)  | 
				    (moves_225 >>> 18) | 
				    (moves_225 >>> 27) | 
				    (moves_225 >>> 36) | 
				    (moves_225 >>> 45) | 
				    (moves_225 >>> 54);
		moves_225 = (moves_225 & lookup) ^ lookup;

		lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_315];
		moves_315 = lookup & occupiedSquares;
		moves_315 = (moves_315 >>> 7)  | 
				    (moves_315 >>> 14) | 
				    (moves_315 >>> 21) | 
				    (moves_315 >>> 28) | 
				    (moves_315 >>> 35) | 
				    (moves_315 >>> 42);
		moves_315 = (moves_315 & lookup) ^ lookup;

		toBitboard = ((moves_45 | moves_135 | moves_225 | moves_315) 
				// and opSideBitBoard..
				& opSideBitBoard)
				| ((rightMoves | leftMoves | upMoves | downMoves) 
				// and opSideBitBoard..
				& opSideBitBoard);
		if (toBitboard != 0) {
			return true;
		}

		return false;
	}
}

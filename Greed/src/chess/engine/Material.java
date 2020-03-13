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

public class Material implements EngineConstants {
	//BQ, BQ, BQ, WQ, WQ, WQ, BR, BR, BR, WR, WR, WR, BB, BB, BB, WB, WB, WB, BK, BK, BK, WK, WK, WK, BP, BP, BP, BP, WP, WP, WP, WP.
	public static final int[] PIECE_VALUES = {0, 0, 1 << 0, 1 << 4, 1 << 8, 1 << 11, 1 << 14, 1 << 17, 1 << 20, 1 << 23, 1 << 26, 1 << 29 };
	
	public static final int[] PIECE_MASKS = {0, 0, 15 << 0, 15 << 4, 7 << 8, 7 << 11, 7 << 14, 7 << 17, 7 << 20, 7 << 23, 7 << 26, 7 << 29 };
	
	public static final int[] PIECE_SHIFTS = {0, 0, 0, 4, 8, 11, 14, 17, 20, 23, 26, 29 };
	
	public static final int[] SLIDING_PIECE_MASKS = {PIECE_MASKS[WHITE_BISHOP] | PIECE_MASKS[WHITE_ROOK] | PIECE_MASKS[WHITE_QUEEN], 
			PIECE_MASKS[BLACK_BISHOP] | PIECE_MASKS[BLACK_ROOK] | PIECE_MASKS[BLACK_QUEEN]};
	
	public static final int[] MAJOR_PIECE_MASKS = {PIECE_MASKS[WHITE_BISHOP] | PIECE_MASKS[WHITE_ROOK] | PIECE_MASKS[WHITE_QUEEN] | PIECE_MASKS[WHITE_KNIGHT] , 
			PIECE_MASKS[BLACK_BISHOP] | PIECE_MASKS[BLACK_ROOK] | PIECE_MASKS[BLACK_QUEEN] | PIECE_MASKS[BLACK_KNIGHT]};

	public static int getMaterialKey(long[] bb) {
		int materialKey = 0;
		for (int j = EngineConstants.WHITE_PAWN ; j <= EngineConstants.BLACK_QUEEN ; j++) {
			materialKey += Long.bitCount(bb[j]) * PIECE_VALUES[j];
		}
		return materialKey;
	}
	
	public static int getPieceCount(int materialKey, int piece) {
		return (materialKey & PIECE_MASKS[piece]) >> PIECE_SHIFTS[piece];
	}
	
	public static boolean hasSlidingPiece(int materialKey, int side) {
		return (materialKey & SLIDING_PIECE_MASKS[side]) != 0;
	}
	
	public static boolean hasMajorPiece(int materialKey, int side) {
		return (materialKey & MAJOR_PIECE_MASKS[side]) != 0;
	}
	
}

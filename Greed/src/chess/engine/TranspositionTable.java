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

// http://hardy.uhasselt.be/Toga/book_format.html
public class TranspositionTable {

	private static final int PIECE_COUNT = 14;
	private static final int SQUARE_COUNT = 64;
	private static final int FILE_COUNT = 8;
	private static final int RANK_COUNT = 8;
	private static final int CASTLING_TYPE = 2;
	private static final int CASTLING_SIDE = 2;

	public static final long[][] zobristPositionArray = new long[PIECE_COUNT][SQUARE_COUNT];
	public static final long[] zobristEnPassantArray = new long[FILE_COUNT * RANK_COUNT];
	public static final long[][] zobristCastlingArray = new long[CASTLING_SIDE][CASTLING_TYPE];
	public static final long zobristBlackMove;

	// TODO Change This later.
	// TODO Change This later.
	// private static HardCodedSecureRandom secureRandom = new
	// HardCodedSecureRandom();
	private static final PolyGlotRandom secureRandom = new PolyGlotRandom();
	// private static SecureRandom secureRandom = new SecureRandom();
	// TODO Change This later.
	// TODO Change This later.

	static {
		for (int piece = 2; piece < PIECE_COUNT; piece++) {
			for (int square = 0; square < SQUARE_COUNT; square++) {
				zobristPositionArray[piece ^ 1][square] = secureRandom.nextLong();
			}
		}

		for (int castlingSide = 0; castlingSide < CASTLING_SIDE; castlingSide++) {
			for (int castlingType = CASTLING_TYPE - 1; castlingType >= 0; castlingType--) {
				zobristCastlingArray[castlingSide][castlingType] = secureRandom.nextLong();
			}
		}

		for (int file = 0; file < FILE_COUNT; file++) {
			long zobristKeyForFile = secureRandom.nextLong();
			for (int rank = 0; rank < RANK_COUNT; rank++) {
				zobristEnPassantArray[file + (rank * FILE_COUNT)] = zobristKeyForFile;
			}
		}

		zobristBlackMove = secureRandom.nextLong();
	}

	public static void fillZobristArrays() {
	}

	public static long getZobristKey(long[] bitboard, int epT, byte[][] castlingRights, int side) {
		long zobristKey = 0L;

		int trailingZeros;
		for (byte i = 2; i < bitboard.length; i++) {
			long bb = bitboard[i];
			while (bb != 0) {
				trailingZeros = Long.numberOfTrailingZeros(bb);
				zobristKey ^= zobristPositionArray[i][trailingZeros];
				bb = bb & ~(1L << trailingZeros);
			}
		}

		if (epT != 64 && (EngineConstants.PAWN_ATTACK_LOOKUP[side ^ 1][epT] & bitboard[(side) | EngineConstants.PAWN]) != 0) {
			zobristKey ^= zobristEnPassantArray[epT];
		}

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if (castlingRights[i][j] == 1) {
					zobristKey ^= zobristCastlingArray[i][j];
				}
			}
		}

		if (side == GuiConstants.WHITES_TURN) {
			zobristKey ^= zobristBlackMove;
		}

		return zobristKey;
	}

}

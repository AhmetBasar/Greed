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

// https://www.chessprogramming.org/Isolated_Pawns_(Bitboards)
public class IsolatedPawn {

	private static final int PENALTY_ISOLATED_PAWN = 10;

	public static int evaluateIsolatedPawnsSquareCentric(long[] bitboard) {

		int eval = 0;

		long whitePawns = bitboard[EngineConstants.WHITE_PAWN];
		long blackPawns = bitboard[EngineConstants.BLACK_PAWN];

		int trailingZeros;
		long fromBitboard = bitboard[EngineConstants.WHITE_PAWN];
		while ((trailingZeros = Long.numberOfTrailingZeros(fromBitboard)) != 64) {

			if ((EngineConstants.neighborFiles[trailingZeros] & whitePawns) == 0) {
				eval -= PENALTY_ISOLATED_PAWN;
			}

			fromBitboard = fromBitboard & ~(1L << trailingZeros);
		}

		fromBitboard = bitboard[EngineConstants.BLACK_PAWN];
		while ((trailingZeros = Long.numberOfTrailingZeros(fromBitboard)) != 64) {

			// Isolated Pawns
			if ((EngineConstants.neighborFiles[trailingZeros] & blackPawns) == 0) {
				eval += PENALTY_ISOLATED_PAWN;
			}

			fromBitboard = fromBitboard & ~(1L << trailingZeros);
		}

		return eval;

	}

	public static int evaluateIsolatedPawnsSetWise(long whitePawns, long blackPawns) {
		int eval = 0;
		eval += (Long.bitCount(getIsolatedPawnCount(blackPawns)) - Long.bitCount(getIsolatedPawnCount(whitePawns)))
				* PENALTY_ISOLATED_PAWN;
		return eval;
	}

	public static long getIsolatedPawnCount(long bb) {
		return noNeighborOnEastFile(bb) & noNeighborOnWestFile(bb);
	}

	public static long noNeighborOnEastFile(long bb) {
		return bb & ~westAttackFileFill(bb);
	}

	public static long noNeighborOnWestFile(long bb) {
		return bb & ~eastAttackFileFill(bb);
	}

	public static long westAttackFileFill(long bb) {
		return BitboardUtility.westOne(BitboardUtility.fileFill(bb));
	}

	public static long eastAttackFileFill(long bb) {
		return BitboardUtility.eastOne(BitboardUtility.fileFill(bb));
	}
}

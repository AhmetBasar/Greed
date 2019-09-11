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

/**
 * https://www.chessprogramming.org/Backward_Pawns_(Bitboards)
 **/
public class BackwardPawn {

	public static long whiteBackwardPawns(long wp, long bp) {
		long stops = wp << 8;
		long wAttackSpans = BitboardUtility.wEastAttackFrontSpans(wp)
				| BitboardUtility.wWestAttackFrontSpans(wp);
		long bAttacks = BitboardUtility.bPawnEastAttacks(bp) | BitboardUtility.bPawnWestAttacks(bp);
		return (stops & bAttacks & ~wAttackSpans) >>> 8;
	}

	public static long blackBackwardPawns(long bp, long wp) {
		long stops = bp >>> 8;
		long bAttackSpans = BitboardUtility.bEastAttackFrontSpans(bp)
				| BitboardUtility.bWestAttackFrontSpans(bp);
		long wAttacks = BitboardUtility.wPawnEastAttacks(wp) | BitboardUtility.wPawnWestAttacks(wp);
		return (stops & wAttacks & ~bAttackSpans) << 8;
	}

}
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
 * https://www.chessprogramming.org/Passed_Pawns_(Bitboards)
 **/
public class PassedPawn {

	public static long whitePassedPawns(long wp, long bp) {
		long allFrontSpans = BitboardUtility.bFrontSpans(bp);
		allFrontSpans |= BitboardUtility.eastOne(allFrontSpans) | BitboardUtility.westOne(allFrontSpans);
		return wp & ~allFrontSpans;
	}

	public static long blackPassedPawns(long bp, long wp) {
		long allFrontSpans = BitboardUtility.wFrontSpans(wp);
		allFrontSpans |= BitboardUtility.eastOne(allFrontSpans) | BitboardUtility.westOne(allFrontSpans);
		return bp & ~allFrontSpans;
	}

}

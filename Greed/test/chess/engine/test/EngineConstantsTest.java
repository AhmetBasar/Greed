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
package chess.engine.test;

import chess.engine.EngineConstants;

public class EngineConstantsTest {

	public static void main(String[] args) {
		testAll();
	}

	public static void testAll() {
		testEptLookup();
	}

	public static void testEptLookup() {
		int[] pushDiffs = { 8, 64 - 8 };
		for (int side = EngineConstants.WHITE; side <= EngineConstants.BLACK; side++) {
			for (int to = 0; to < 64; to++) {
				int diff = pushDiffs[side];
				int epT = Long.numberOfTrailingZeros(((1L << to) >>> diff) | ((1L << to) << (64 - diff)));
				if (EngineConstants.EPT_LOOKUP[side][to] != epT) {
					throw new RuntimeException("Failed.");
				}
			}
		}
	}
}

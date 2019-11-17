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

import java.util.Arrays;

import chess.movegen.MagicBitboard;
import chess.util.Utility;

// https://github.com/jswaff/chess4j
public class MagicBitboardTest {

	public static void main(String[] args) {
		testAll();
	}

	public static void testAll() {
		testBishopMagicNumbers();
		testRookMagicNumbers();
	}

	private static void testBishopMagicNumbers() {
		boolean dejavu[] = new boolean[512];
		for (int s = 0; s < 64; s++) {
			Arrays.fill(dejavu, false);
			long magicNumber = MagicBitboard.bishopMagicNumbers[s];
			int variationCount = (int) Utility.SINGLE_BIT[Long.bitCount(MagicBitboard.bishopMasks[s])];
			for (int i = 0; i < variationCount; i++) {
				long occ = MagicBitboard.bishopOccVariations[s][i];
				int index = (int) ((occ * magicNumber) >>> MagicBitboard.bishopShifts[s]);
				
				if (!(index <= variationCount && index <= 512)) {
					throw new RuntimeException("Failed.");
				}

				if (dejavu[index]) {
					throw new RuntimeException("Failed.");
				}
				
				dejavu[index] = true;
			}
		}
	}

	private static void testRookMagicNumbers() {
		boolean dejavu[] = new boolean[4096];
		for (int s = 0; s < 64; s++) {
			Arrays.fill(dejavu, false);
			long magicNumber = MagicBitboard.rookMagicNumbers[s];
			int variationCount = (int) Utility.SINGLE_BIT[Long.bitCount(MagicBitboard.rookMasks[s])];
			for (int i = 0; i < variationCount; i++) {
				long occ = MagicBitboard.rookOccVariations[s][i];
				int index = (int) ((occ * magicNumber) >>> MagicBitboard.rookShifts[s]);
				
				if (!(index <= variationCount && index <= 4096)) {
					throw new RuntimeException("Failed.");
				}

				if (dejavu[index]) {
					throw new RuntimeException("Failed.");
				}
				
				dejavu[index] = true;
			}
		}
	}

}

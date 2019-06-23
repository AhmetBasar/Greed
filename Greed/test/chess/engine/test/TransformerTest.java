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

import chess.debug.DebugUtility;
import chess.engine.Transformer;

public class TransformerTest {

	public static void main(String[] args) throws Exception {
		testAll();
	}

	public static void testAll() {
		testRandomBoard();
		testRIndexMap();
		testIndexMap();
	}

	public static void testRandomBoard() {
		for (int i = 0; i < 9999; i++) {
			byte[][] randomBoard = DebugUtility.generateRandomBoard();
			long[] bitboard = Transformer.getBitboardStyl(randomBoard);
			byte[] oneDimByteArray = Transformer.getByteArrayStyl(bitboard);
			long[] bitboard2 = Transformer.getBitboardStyl(oneDimByteArray);
			byte[][] randomBoard2 = Transformer.getTwoDimByteArrayStyl(bitboard2);
			if (!Arrays.equals(bitboard, bitboard2)) {
				throw new RuntimeException("Failed.");
			}
			if (!Arrays.deepEquals(randomBoard, randomBoard2)) {
				throw new RuntimeException("Failed.");
			}
		}
	}

	public static void testRIndexMap() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (Transformer.rIndexMap[i][j] != ((7 - i) * 8) + j) {
					throw new RuntimeException("Failed.");
				}
			}
		}
	}

	public static void testIndexMap() {
		for (int i = 0; i < 64; i++) {
			if (Transformer.indexMap[i][0] % 8 != i % 8) {
				throw new RuntimeException("Failed.");
			}
			if (Transformer.indexMap[i][1] != 7 - (i / 8)) {
				throw new RuntimeException("Failed.");
			}
		}
	}

}
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
import chess.engine.EngineConstants;
import chess.engine.Transformer;

public class DebugUtilityTest {

	public static void main(String[] args) {
		testAll();
	}

	public static void testAll() {
		testReverseCastlingRights();

		testReverseBitboard();

		testDeepCloneMultiDimensionalArray();
		
		testGetItem();

	}

	public static void testReverseCastlingRights() {
		for (byte i = 0; i < 16; i++) {
			byte firstBit = (byte) (i & 1);
			byte secondBit = (byte) (i & 2);
			byte thirdBit = (byte) (i & 4);
			byte fourthBit = (byte) (i & 8);
			byte[][] orig = new byte[][] { { fourthBit, thirdBit }, { secondBit, firstBit } };
			byte[][] reversed = DebugUtility.reverseCastlingRights(orig);
			byte[][] expectedReversed = new byte[][] { { secondBit, firstBit }, { fourthBit, thirdBit } };
			if (!Arrays.deepEquals(reversed, expectedReversed)) {
				throw new RuntimeException("Failed.");
			}
		}
	}

	public static void testReverseBitboard() {
		long[] defaultBitboard = Transformer.getBitboardStyl(DebugUtility.getDefaultBoard());
		long[] byteReversedDefaultBitboard = DebugUtility.reverseBitboard(defaultBitboard, false);
		if (!Arrays.equals(defaultBitboard, byteReversedDefaultBitboard)) {
			throw new RuntimeException("Failed.");
		}

		long[] bitboard2 = Transformer.getBitboardStyl(DebugUtility.getBoard2());
		long[] byteReversedBitboard2 = DebugUtility.reverseBitboard(bitboard2, false);
		if (Arrays.equals(bitboard2, byteReversedBitboard2)) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testDeepCloneMultiDimensionalArray() {
		byte[][] defaultBoard = DebugUtility.getDefaultBoard();
		byte[][] clonnedBoard = DebugUtility.deepCloneMultiDimensionalArray(defaultBoard);
		if (!Arrays.deepEquals(defaultBoard, clonnedBoard)) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testGetItem() {
		long[] defaultBitboard = Transformer.getBitboardStyl(DebugUtility.getDefaultBoard());
		byte[] pieces = Transformer.getByteArrayStyl(defaultBitboard);
		for (int i = 0; i < 64; i++) {
			if (DebugUtility.getItem(defaultBitboard, i) != EngineConstants.PIECE_VALUES[pieces[i]]) {
				throw new RuntimeException("Failed");
			}
		}
	}

}

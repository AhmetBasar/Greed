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

import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.EvaluationAdvancedV4;
import chess.engine.Transformer;
import chess.gui.FenOperations;

public class EvaluationTest {

	public static void main(String[] args) throws Exception {
		testAll();
	}
	
	public static void testAll() throws Exception{
		test();
	}

	private static void test() throws Exception {
		String datazz[] = TestPositions.TEST_POSITIONS.split("//");
		for (int i = 0; i < datazz.length; i++) {
			String fenData = datazz[i].split(";")[0];
			FenOperations fenOperations = new FenOperations();
			fenOperations.setFenString(fenData);

			long bitboard[] = Transformer.getBitboardStyl(fenOperations.getBoard());
			long[] bitReversedBitboard = DebugUtility.reverseBitboard(bitboard, true);
			long[] byteReversedBitboard = DebugUtility.reverseBitboard(bitboard, false);

			byte[][] castlingRights = fenOperations.getCastlingRights();
			byte[][] reversedCastlinRights = DebugUtility.reverseCastlingRights(castlingRights);

			int whiteScore = EvaluationAdvancedV4.evaluate(bitboard, castlingRights, EngineConstants.WHITE);

			int bitReversedBlackScore = EvaluationAdvancedV4.evaluate(bitReversedBitboard, reversedCastlinRights, EngineConstants.BLACK);
			int byteReversedBlackScore = EvaluationAdvancedV4.evaluate(byteReversedBitboard, reversedCastlinRights, EngineConstants.BLACK);

			if (whiteScore + bitReversedBlackScore != 0) {
				System.out.println("Failed");
				throw new RuntimeException("whiteScore = " + whiteScore + " blackScore = " + bitReversedBlackScore);
			}

			if (bitReversedBlackScore != byteReversedBlackScore) {
				System.out.println("Failed");
				throw new RuntimeException("blackScore = " + bitReversedBlackScore + " blackScore2 = " + byteReversedBlackScore);
			}
		}
	}
}

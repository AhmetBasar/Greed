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

import chess.engine.Transformer;
import chess.engine.ZobristHashingPolyGlot;
import chess.gui.FenOperations;
import chess.util.Utility;

public class ZobristHashingPolyGlotTest {
	
	public static void main(String[] args) {
		testAll();
	}

	public static void testAll() {
		testGetZobristKey();

		System.out.println("Successful.");
	}

	public static void testGetZobristKey() {

		String testDatas = Utility.readFile("test/chess/engine/test/suites/POLYGLOT");

		String[] arrTestDatas = testDatas.split("\n");

		for (String testData : arrTestDatas) {
			if (testData.startsWith(TestConstants.COMMENT_INDICATOR) || testData.trim().length() == 0) {
				continue;
			}

			String[] parts = testData.split(";");
			String fenString = parts[0];
			long expectedZobristKey = Long.parseUnsignedLong(parts[1].trim(), 16);

			FenOperations fenOperations = new FenOperations();
			fenOperations.setFenString(fenString);
			
			long foundZobristKey = ZobristHashingPolyGlot.getZobristKey(Transformer.getBitboardStyl(fenOperations.getBoard()), fenOperations.getEpTarget(), fenOperations.getCastlingRights(), fenOperations.getSide());
			
			if (foundZobristKey != expectedZobristKey) {
				throw new RuntimeException("Failed.");
			}
		}
	}

}

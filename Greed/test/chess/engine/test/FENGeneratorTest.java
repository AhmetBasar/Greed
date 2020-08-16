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

import chess.engine.IBoard;
import chess.engine.test.suites.FenGenerator;
import chess.gui.FenOperations;

public class FENGeneratorTest {

	public static void main(String[] args) {
		testAll();
	}

	public static void testAll() {
		String datazz[] = TestPositions.FEN_STRINGS.split("//");
		for (int i = 0; i < datazz.length; i++) {
			String fenData = datazz[i];
			FenOperations fenOperations = new FenOperations();
			fenOperations.setFenString(fenData);
			IBoard board = fenOperations.getIBoard();

			String fenStr = FenGenerator.getFenString(board);
			if (!fenStr.equals(fenData)) {
				throw new RuntimeException("Failed.");
			}
		}
	}

}

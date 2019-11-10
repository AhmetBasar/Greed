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
package chess.bot.test;

import java.util.Arrays;

import javax.swing.SwingUtilities;

import chess.bot.BoardScanner;
import chess.bot.Utility;
import chess.bot.image.impl.CruelChessImageCache;
import chess.debug.DebugUtility;
import chess.gui.BaseGui;

public class BoardScannerTest implements Runnable {
	
	private BaseGui base;
	private int iterationCount;

	public BoardScannerTest(BaseGui base) {
		this.base = base;
	}
	
	public void runTest() {
		CruelChessImageCache cache = new CruelChessImageCache();
		cache.refresh();
		BoardScanner boardScanner = new BoardScanner(null, cache);
		while (true) {
			iterationCount++;
			try {

				byte[][] randomBoard = DebugUtility.generateRandomBoard();

				// DebugUtility.throwBoard(randomBoard);

				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						try {
							base.setBoard(randomBoard);
						} catch (Exception e) {
							e.printStackTrace();
							System.exit(1);
						}
					}
				});

				Utility.sleep(111);
				byte[][] guessedBoard = boardScanner.scan();

				if (Arrays.deepEquals(randomBoard, guessedBoard)) {
					System.out.println("OKEYY" + iterationCount);
				} else {
					System.out.println("NOOOOOOOOOOOOOOOOOOOO");
					throw new Error();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		try {
			runTest();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}

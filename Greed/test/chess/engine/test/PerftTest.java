/**********************************************
 * Greed, a chess engine written in java.
 * Copyright (C) 2019 Ahmet Ba�ar
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

import java.util.ArrayList;
import java.util.List;

import chess.debug.BoardV5;
import chess.debug.PerformanceTestingSingleThreadedWithBoardInfrastructureV2;
import chess.engine.Transformer;
import chess.gui.FenOperations;

public class PerftTest {
	
	public static void main(String[] args) {
		testAll();
	}

	public static void testAll() {

		List<Runnable> executables = new ArrayList<Runnable>();

		String datazz[] = TestingFramework.QUICK_TEST ? TestPositions.TEST_POSITIONS_FAST.split("//") : TestPositions.TEST_POSITIONS.split("//");
		for (int i = 0; i < datazz.length; i++) {
			String[] datas = datazz[i].split(";");
			String fenData = datas[0];
			Runnable executable = new Runnable() {

				@Override
				public void run() {
					
					PerformanceTestingSingleThreadedWithBoardInfrastructureV2 engine = new PerformanceTestingSingleThreadedWithBoardInfrastructureV2();
					
					for (int k = 1; k < datas.length; k++) {
						String result = datas[k].trim();
						int depth = Character.getNumericValue(result.split(" ")[0].charAt(1));
						int expectedCount = Integer.parseInt(result.split(" ")[1]);
						FenOperations fenOperations = new FenOperations();
						fenOperations.setFenString(fenData);

						BoardV5 board = new BoardV5(Transformer.getBitboardStyl(fenOperations.getBoard()),
								Transformer.getByteArrayStyl(Transformer.getBitboardStyl(fenOperations.getBoard())),
								fenOperations.getEpTarget(), fenOperations.getEpSquare(), depth,
								fenOperations.getCastlingRights(), 0L, 0);

						engine.getPerftResult().resetCounters();
						engine.perft(depth, board, fenOperations.getSide() ^ 1);
						long resultMoveCount = engine.getPerftResult().getNodeCount();
						if (expectedCount != resultMoveCount) {
							System.out.println("fenData = " + fenData);
							System.out.println("depth = " + depth);
							System.out.println("expectedCount = " + expectedCount);
							System.out.println("resultMoveCount = " + resultMoveCount);
							throw new RuntimeException("Failed.");
						}
					}
				}
			};
			executables.add(executable);
		}
		ThreadPool.getInstance().execute(executables, true);
		
		System.out.println("Successful.");
	}

}
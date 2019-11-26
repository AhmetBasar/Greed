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

import java.util.ArrayList;
import java.util.List;

import chess.debug.PerformanceTestingSingleThreadedWithBoardInfrastructureV2;
import chess.engine.BoardFactory;
import chess.engine.IBoard;
import chess.engine.Transformer;
import chess.gui.FenOperations;
import chess.util.Utility;

public class PerftDetailedTest {

	public static void main(String[] args) {
		testAll();
	}

	public static void testAll() {
		testPerft();
	}

	public static void testPerft() {

		List<Runnable> executables = new ArrayList<Runnable>();

		String testDatas = Utility.readFile("test/chess/engine/test/perft/PERFT");

		String[] arrTestDatas = testDatas.split("\n");

		for (String perftString : arrTestDatas) {
			if (perftString.startsWith(TestConstants.COMMENT_INDICATOR)) {
				continue;
			}

			String[] parts = perftString.split(":");
			String fenString = parts[0];
			for (int i = 1; i < parts.length; i++) {

				String part = parts[i].trim();

				Runnable executable = new Runnable() {

					@Override
					public void run() {
						
						try {
							FenOperations fenOperations = new FenOperations();
							fenOperations.setFenString(fenString);
							String[] detailedInfos = part.split(";");
							
							int depth = Integer.parseInt(detailedInfos[0]);
							long nodeCount = Long.parseLong(detailedInfos[1]);
							
							if (TestingFramework.QUICK_TEST && nodeCount > 100000000) {
								return;
							}
							
							int captureCount = -1;
							int epCount = -1;
							int castlingCount = -1;
							int promotionCount = -1;
							int checkCount = -1;
							int checkMateCount = -1;
							
							if (detailedInfos.length > 2) {
								captureCount = Integer.parseInt(detailedInfos[2]);
								epCount = Integer.parseInt(detailedInfos[3]);
								castlingCount = Integer.parseInt(detailedInfos[4]);
								promotionCount = Integer.parseInt(detailedInfos[5]);
								checkCount = Integer.parseInt(detailedInfos[6]);
								if (detailedInfos.length == 10) {
									checkMateCount = Integer.parseInt(detailedInfos[9]);
								}
							}
							IBoard board = BoardFactory.getInstance(Transformer.getBitboardStyl(fenOperations.getBoard())
									, Transformer.getByteArrayStyl(Transformer.getBitboardStyl(fenOperations.getBoard()))
									, fenOperations.getEpTarget()
									, fenOperations.getCastlingRights(), 0L, 0, 0L, null);
							PerformanceTestingSingleThreadedWithBoardInfrastructureV2 engine = new PerformanceTestingSingleThreadedWithBoardInfrastructureV2();
							engine.getPerftResult().resetCounters();
							engine.perft(depth, board, fenOperations.getSide() ^ 1);
							
							if (nodeCount != engine.getPerftResult().getNodeCount()
									|| (captureCount != -1 && captureCount != engine.getPerftResult().getCaptureCount())
									|| (epCount != -1 && epCount != engine.getPerftResult().getEpCount())
									|| (castlingCount != -1 && castlingCount != engine.getPerftResult().getCastlingCount())
									|| (promotionCount != -1 && promotionCount != engine.getPerftResult().getPromotionCount())
									|| (checkCount != -1 && checkCount != engine.getPerftResult().getCheckCount())
									|| (checkMateCount != -1 && checkMateCount != engine.getPerftResult().getCheckMateCount())) {
								throw new RuntimeException("Failed.");
							}
						} catch (Exception e) {
							e.printStackTrace();
							System.exit(1);
						}
					}
				};
				executables.add(executable);
			}
		}
		ThreadPool.getInstance().execute(executables, true);
	}
}

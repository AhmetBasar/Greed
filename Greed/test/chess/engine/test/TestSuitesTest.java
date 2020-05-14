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
import java.util.Arrays;
import java.util.List;

import chess.engine.EngineConstants;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.engine.Transformer;
import chess.engine.test.suites.EpdOperations;
import chess.engine.test.suites.SanGenerator;
import chess.engine.test.suites.TestSuiteResult;
import chess.util.Utility;

public class TestSuitesTest {

	public static void main(String[] args) {
		testAll();
	}

	public static void testAll() {
		//TODO test other suites.
		testWAC();
	}

	private static void testWAC() {
		List<Runnable> executables = new ArrayList<Runnable>();
		TestSuiteResult testSuiteResult = new TestSuiteResult();

		String testDatas = Utility.readFile("test/chess/engine/test/suites/WACNEW.epd");
		
		String[] arrTestDatas = testDatas.split("\n");
		List<String> listAll = Arrays.asList(arrTestDatas);
		List<List<String>> splitted = Utility.splitList(listAll, listAll.size() / ThreadPool.POOL_SIZE);

		for (int i = 0; i < splitted.size(); i++) {
			final List<String> part = splitted.get(i);

			Runnable executable = new Runnable() {

				@Override
				public void run() {
					try {
						chess.fhv2.SearchEngineFifty11 engine = chess.fhv2.SearchEngineFifty11.getNewInstance();
						
						for (String epdString : part) {
							if (epdString.startsWith(TestConstants.COMMENT_INDICATOR)) {
								continue;
							}
							EpdOperations eo = new EpdOperations();
							eo.setEpdString(epdString.trim());
							
							SearchParameters params = new SearchParameters();
							params.setDepth(1);
							params.setEpT(eo.getFenOperations().getEpTarget());
							params.setEpS(eo.getFenOperations().getEpSquare());
							params.setBitboard(Transformer.getBitboardStyl(eo.getFenOperations().getBoard()));
							params.setPieces(Transformer
									.getByteArrayStyl(Transformer.getBitboardStyl(eo.getFenOperations().getBoard())));
							params.setCastlingRights(eo.getFenOperations().getCastlingRights());
							params.setSide(eo.getFenOperations().getSide());
							params.setUiZobristKey(eo.getFenOperations().getZobristKey());
							params.setUiPawnZobristKey(eo.getFenOperations().getPawnZobristKey());
							params.setTimeLimit(TestingFramework.QUICK_TEST ? 500 : 5000);
							params.setFiftyMoveCounter(0);
							params.setEngineMode(EngineConstants.EngineMode.NON_FIXED_DEPTH);
							params.setBookName(null);
							params.setZobristKeyHistory(new ArrayList<>());
							
							SearchResult searchResult = engine.search(params);
							
							String sanMove = SanGenerator.convertSanMove(searchResult.getBestMove(), params.getPieces(),
									params.getBitboard(), params.getSide(), params.getEpT(), params.getEpS(),
									params.getCastlingRights());
							
							if (eo.getOperations().containsKey(EpdOperations.OPCODE_BEST_MOVES)) {
								List<String> bestMoves = eo.getOperations().get(EpdOperations.OPCODE_BEST_MOVES);
								if (bestMoves.contains(sanMove)) {
									testSuiteResult.incrementSolvedSuiteCount();
								} else {
//								System.err.println("sanMove = " + sanMove + " but bestMoves = " + bestMoves);
								}
							} else {
								List<String> avoidMoves = eo.getOperations().get(EpdOperations.OPCODE_AVOID_MOVES);
								if (avoidMoves.contains(sanMove)) {
//								System.err.println("sanMove = " + sanMove + " but avoidMoves = " + avoidMoves);
								} else {
									testSuiteResult.incrementSolvedSuiteCount();
								}
							}
							testSuiteResult.incrementTotalSuiteCount();
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			};
			executables.add(executable);
		}
		ThreadPool.getInstance().execute(executables, true);
		
		if (testSuiteResult.getSolvedSuiteCount() < (TestingFramework.QUICK_TEST ? 240 : 260)) {
			System.err.println(testSuiteResult);
			throw new RuntimeException("Failed.");
		}
		System.out.println(testSuiteResult);
	}

	@SuppressWarnings("unused")
	private static void testOtherSuites() {
		// (20190605) Depth = 1, TL = 5000, Total / Success = 24 / 12
		// (20190606) (After SanMoveParser BugFix) Depth = 1, TL = 5000, Total / Success = 24 / 14
//		 String testDatas = Utility.readFile("test/chess/engine/test/suites/BRATKO-KOPEC.epd");
		
		// (20190605) Depth = 1, TL = 5000, Total / Success = 25 / 13
		// (20190606) (After SanMoveParser BugFix) Depth = 1, TL = 5000, Total / Success = 25 / 16
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/CCR-ONE-HOUR.epd");
		
		// (20190605) Depth = 1, TL = 5000, Total / Success = 25 / 18
		// (20190606) (After SanMoveParser BugFix) Depth = 1, TL = 5000, Total / Success = 25 / 18
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/KAUFMAN.epd");
		
		// (20190605) Depth = 1, TL = 5000, Total / Success = 35 / 5
		// (20190606) (After SanMoveParser BugFix) Depth = 1, TL = 5000, Total / Success = 35 / 5
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/LCTII.epd");
		
		// (20190605) Depth = 1, TL = 5000, Total / Success = 11 / 0
		// (20190606) (After SanMoveParser BugFix) Depth = 1, TL = 5000, Total / Success = 11 / 0
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/NOLOT.epd");
		
		// (20190605) Depth = 1, TL = 5000, Total / Success = 5 / 1
		// (20190606) (After SanMoveParser BugFix) Depth = 1, TL = 5000, Total / Success = 5 / 1
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/NULLMOVE.epd");
		
		// (20190605) Depth = 1, TL = 5000, Total / Success = 134 / 73
		// (20190606) (After SanMoveParser BugFix) Depth = 1, TL = 5000, Total / Success = 133 / 70
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/SILENT_BUT_DEADLY.epd");
		
		// (20190605) Depth = 1, TL = 5000, Total / Success = 300 / 174
		// (20190606) (After SanMoveParser BugFix) Depth = 1, TL = 5000, Total / Success = 300 / 276
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/WACNEW.epd");
		
		// (20190605) Depth = 1, TL = 5000, Total / Success = 30 / 8
		// (20190606) (After SanMoveParser BugFix) Depth = 1, TL = 5000, Total / Success = 30 / 11
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/ZPTS.epd");
		
		// (20190605) Depth = 1, TL = 5000, Total / Success = 100 / 40
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS1.epd");
		
		// (20190605) Depth = 1, TL = 5000, Total / Success = 100 / 47
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS10.epd");
		
		// (20190605) Depth = 1, TL = 5000, Total / Success = 100 / 42
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS11.epd");
		
		// (20190605) Depth = 1, TL = 5000, Total / Success = 100 / 47
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS12.epd");
		
		// (20190605) Depth = 1, TL = 5000,	Total / Success = 100 / 55
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS13.epd");
		
		// (20190605) Depth = 1, TL = 5000,	Total / Success = 100 / 53
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS14.epd");
		
		// (20190605) Depth = 1, TL = 5000,	Total / Success = 100 / 24
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS15.epd");
		
		// (20190605) Depth = 1, TL = 5000,	Total / Success = 100 / 33
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS2.epd");
		
		// (20190605) Depth = 1, TL = 5000,	Total / Success = 100 / 47
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS3.epd");
		
		// (20190605) Depth = 1, TL = 5000,	Total / Success = 100 / 41
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS4.epd");
		
		// (20190605) Depth = 1, TL = 5000,	Total / Success = 100 / 59
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS5.epd");
		
		// (20190605) Depth = 1, TL = 5000,	Total / Success = 100 / 55
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS6.epd");
		
		// (20190605) Depth = 1, TL = 5000,	Total / Success = 100 / 40
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS7.epd");
		
		// (20190605) Depth = 1, TL = 5000,	Total / Success = 100 / 30
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS8.epd");
		
		// (20190605) Depth = 1, TL = 5000,	Total / Success = 100 / 26
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/sts/STS9.epd");
		
		// (20190605) Depth = 1, TL = 5000,	Total / Success = 11 / 1
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/CCC-I.epd");
		
		// (20190605) Depth = 1, TL = 5000,	Total / Success = 24 / 2
//		String testDatas = Utility.readFile("test/chess/engine/test/suites/PASSED-PAWN.epd");
	}

}

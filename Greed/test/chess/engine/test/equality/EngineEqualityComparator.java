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
package chess.engine.test.equality;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import chess.engine.EngineConstants;
import chess.engine.ISearchableV2;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.engine.test.ThreadPool;
import chess.engine.test.tournament.ChessBoard;
import chess.engine.test.tournament.ChessBoard.GameState;
import chess.fhv2.SearchEngineFifty10;
import chess.fhv2.SearchEngineFifty12;

public class EngineEqualityComparator implements Runnable {
	
	private static final int depth = 6;
	private static final int timeLimit = 5;

	@Override
	public void run() {
		try {
			ChessBoard board = new ChessBoard();
			
			ISearchableV2 engine1 = SearchEngineFifty10.getNewInstance();
			ISearchableV2 engine2 = SearchEngineFifty12.getNewInstance();
			
			engine1.setBoardStateHistory(board.getBoardStateHistory());
			engine2.setBoardStateHistory(board.getBoardStateHistory());
			
			ISearchableV2 engineWhite = engine1;
			ISearchableV2 engineBlack = engine2;
			
			while (true) {
				SearchParameters params = getSearchParameters(board);
				SearchResult srw = engineWhite.search(params);
				SearchResult srb = engineBlack.search(params);
				
				increment(mapTimeConsumeds, engineWhite.getClass(), srw.getTimeConsumed());
				increment(mapTimeConsumeds, engineBlack.getClass(), srb.getTimeConsumed());
				
				if (srb.isBookMove() != srw.isBookMove()) {
					throw new RuntimeException("Failed.");
				}
				
				if (!srw.isBookMove()) {
					if (!srw.equals(srb)) {
						throw new RuntimeException("Failed.");
					}
				}
				
				board.doMove(srw.getBestMove());
				
				GameState gameState = board.getGameState();
				if (gameState != GameState.PLAYING) {
					
					engineWhite.resetTT();
					engineBlack.resetTT();
					
					if (gameState == GameState.WHITE_WINS) {
						increment(positionCountWhite, board.getZobristKey());
					} else if (gameState == GameState.BLACK_WINS) {
						increment(positionCountBlack, board.getZobristKey());
					} else {
						increment(positionCountDraw, board.getZobristKey());
					}
					board.resetAll();
					
					StringBuilder sb = new StringBuilder();
					sb.append("positionCountDraw = " + convertToCountBasedMap(positionCountDraw) + "\n");
					sb.append("positionCountWhite = " + convertToCountBasedMap(positionCountWhite) + "\n");
					sb.append("positionCountBlack = " + convertToCountBasedMap(positionCountBlack) + "\n");
					sb.append("mapTimeConsumeds = " + mapTimeConsumeds + "\n");
					
					long engine1Time = mapTimeConsumeds.get(engine1.getClass());
					long engine2Time = mapTimeConsumeds.get(engine2.getClass());
					double totalTime = engine1Time + engine2Time;
					double engine1TimePercentage = (100 * engine1Time) / totalTime;
					double engine2TimePercentage = (100 * engine2Time) / totalTime;
					sb.append("engine1TimePercentage = " + engine1TimePercentage + "\n");
					sb.append("engine2TimePercentage = " + engine2TimePercentage + "\n");
					
				print(sb.toString());
					
					ISearchableV2 tempEngine = engineWhite;
					engineWhite = engineBlack;
					engineBlack = tempEngine;
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
			// TODO: handle exception
		}
	}

	private SearchParameters getSearchParameters(ChessBoard board) {
		SearchParameters params = new SearchParameters();
		params.setDepth(depth);
		params.setEpT(board.getEpTarget());
		params.setEpS(board.getEpSquare());
		params.setBitboard(board.getBitboard());
		params.setPieces(board.getPieces());
		params.setCastlingRights(board.getCastlingRights());
		params.setSide(board.getSide());
		params.setUiZobristKey(board.getZobristKey());
		params.setTimeLimit(timeLimit);
		params.setFiftyMoveCounter(board.getFiftyMoveCounter());
		params.setEngineMode(EngineConstants.EngineMode.FIXED_DEPTH);
		return params;
	}
	
	private synchronized static void increment(Map<Long, Long> map, Long key) {
		Long count = map.get(key);
		map.put(key, count != null ? count.longValue() + 1 : 1);
	}
	
	private synchronized static void increment(Map<Class<?>, Long> map, Class<?> key, long v) {
		Long count = map.get(key);
		map.put(key, count != null ? count.longValue() + v : v);
	}

	private synchronized static TreeMap<Long, Long> convertToCountBasedMap(Map<Long, Long> map) {
		TreeMap<Long, Long> retMap = new TreeMap<>();
		for (Map.Entry<Long, Long> entry : map.entrySet()) {
			Long count = entry.getValue();
			Long countOfCount = retMap.get(count);
			retMap.put(count, countOfCount != null ? countOfCount.longValue() + 1 : 1);
		}
		return retMap;
	}

	private static synchronized void print(String s) {
		System.out.println(s);
	}
	

	private static final Map<Long, Long> positionCountDraw = Collections.synchronizedMap(new HashMap<>());
	private static final Map<Long, Long> positionCountWhite = Collections.synchronizedMap(new HashMap<>());
	private static final Map<Long, Long> positionCountBlack = Collections.synchronizedMap(new HashMap<>());
	private static final Map<Class<?>, Long> mapTimeConsumeds = Collections.synchronizedMap(new HashMap<>());
	
	public static void main(String[] args) {
		List<Runnable> runnables = new ArrayList<>();
		for (int i = 0; i < ThreadPool.POOL_SIZE; i++) {
			runnables.add(new EngineEqualityComparator());
		}
		ThreadPool.getInstance().execute(runnables, true);
	}
	
	public EngineEqualityComparator() {
	}
}

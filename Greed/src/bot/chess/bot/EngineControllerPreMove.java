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
package chess.bot;

import chess.engine.EngineConstants;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.fhv2.SearchEngineFifty_PREMOVEFINDER;

public class EngineControllerPreMove implements Runnable {

	private SearchEngineFifty_PREMOVEFINDER engine;
	private volatile boolean suspended = true;
	private IGameController gameController;
	private ICallBackPreMove callbackPreMove;
	
	public static final int DEFAULT_DEPTH_LIMIT = 6;
	private int depth = DEFAULT_DEPTH_LIMIT;
//	public static int timeLimit = 500;

	public EngineControllerPreMove(IGameController gameController) {
		this.gameController = gameController;
		engine = SearchEngineFifty_PREMOVEFINDER.getInstance();
	}

	public void setCallbackPreMove(ICallBackPreMove callbackPreMove) {
		this.callbackPreMove = callbackPreMove;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				waitIfSuspended();
				long start = System.currentTimeMillis();
				
				SearchParameters params = new SearchParameters();
				params.setDepth(depth);
				params.setEpT(gameController.getEpTarget());
				params.setEpS(gameController.getEpSquare());
				params.setBitboard(gameController.getBitboard());
				params.setPieces(gameController.getPieces());
				params.setCastlingRights(gameController.getCastlingRights());
				params.setSide(gameController.getSide());
				params.setUiZobristKey(gameController.getZobristKey());
				params.setUiPawnZobristKey(gameController.getPawnZobristKey());
				params.setTimeLimit(-1L);
				params.setFiftyMoveCounter(gameController.getFiftyMoveCounter());
				params.setEngineMode(EngineConstants.EngineMode.FIXED_DEPTH);
				params.setFirstMove(gameController.getLastMove());
				
				SearchResult searchResult = engine.search(params);
				
				long e = System.currentTimeMillis();
				System.out.println("fark PREMOVE = " + (e - start));
				callbackPreMove.execute(searchResult.getPreMove());
				suspend();
			} catch (Exception e) {
				e.printStackTrace();
				suspend();
				gameController.resetAll();
				gameController.resetBot();
			}
		}
	}
	
	private void waitIfSuspended() {
		synchronized (this) {
			while (suspended) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void suspend() {
		suspended = true;
	}

	public synchronized void resume() {
		suspended = false;
		notify();
	}
	
	public void reset() {
		engine.resetTT();
		depth = DEFAULT_DEPTH_LIMIT;
	}

}

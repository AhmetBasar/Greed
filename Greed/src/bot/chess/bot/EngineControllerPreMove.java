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

import chess.engine.SearchResult;
import chess.fhv2.SearchEngineFifty_PREMOVEFINDER;

public class EngineControllerPreMove implements Runnable {

	private SearchEngineFifty_PREMOVEFINDER engine;
	private volatile boolean suspended = true;
	private IGameController gameController;
	private ICallBackPreMove callbackPreMove;
	
	public static int DEFAULT_DEPTH_LIMIT = 6;
	public static int depth = DEFAULT_DEPTH_LIMIT;
//	public static int timeLimit = 500;

	public EngineControllerPreMove(IGameController gameController) {
		this.gameController = gameController;
		engine = new SearchEngineFifty_PREMOVEFINDER();
		engine.setBoardStateHistory(gameController.getBoardStateHistory());
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
				SearchResult searchResult = engine.search(depth, gameController.getEpTarget(), gameController.getEpSquare(),
						gameController.getBitboard(), gameController.getPieces(), gameController.getCastlingRights(),
						gameController.getSide(), gameController.getZobristKey(), gameController.getLastMove(), -1L, gameController.getFiftyMoveCounter());
				
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

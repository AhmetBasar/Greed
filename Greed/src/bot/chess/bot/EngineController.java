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

import java.util.ArrayList;

import chess.engine.EngineConstants;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.fhv2.SearchEngineFifty10;

public class EngineController implements Runnable {

	private SearchEngineFifty10 engine;
	private volatile boolean suspended = true;
	private IGameController gameController;
	private ICallBack callback;
	
	public static final int DEFAULT_DEPTH_LIMIT = 7;
	private int depthLimit = DEFAULT_DEPTH_LIMIT;
	private int timeLimit = 2000;

	public EngineController(IGameController gameController) {
		this.gameController = gameController;
		engine = SearchEngineFifty10.getInstance();
		engine.setBoardStateHistory(gameController.getBoardStateHistory());
	}

	public void setCallback(ICallBack callback) {
		this.callback = callback;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				waitIfSuspended();
				int tl = timeLimit;
				int dl = depthLimit;
				
				// 1 move fast, 1 move slow, human like thinking.
				if (gameController.getMoveCount() % 2 == 0 && Utility.generateStrongRandomNumber(1, 10) > 3) {
					tl = tl / 2;
				} else {
					tl = tl * 2;
				}
				
				if (isFast()) {
					// Fast Moves, first 7 move, if lastmove is king move, if last move is capture.
					if (Utility.doProbability(81)) {
						tl = Utility.generateStrongRandomNumber(50, 300);
					}
				} else {
					int htl = (tl * 2) / 3;
					tl = tl + Utility.generateStrongRandomNumber(-htl, htl);
				}
				
				if (Utility.doProbability(5)) {
					// % 5 probablity very fast move. (AKA Jackpot.)
					tl = Utility.generateStrongRandomNumber(70, 250);
				}
				
				if (Utility.doProbability(10)) {
					// % 10 probablity very slow move. (AKA Reverse - Jackpot.)
					java.awt.Toolkit.getDefaultToolkit().beep();
					tl = Utility.generateStrongRandomNumber(15000, 25000);
				}
				
				if (gameController.getMoveCount() <= 1) {
					dl = 5;
					tl = Utility.generateStrongRandomNumber(70, 500);
				}
				
				System.out.println("izin verilen = " + tl);
				
				long start = System.currentTimeMillis();
				
				SearchParameters params = new SearchParameters();
				params.setDepth(dl);
				params.setEpT(gameController.getEpTarget());
				params.setEpS(gameController.getEpSquare());
				params.setBitboard(gameController.getBitboard());
				params.setPieces(gameController.getPieces());
				params.setCastlingRights(gameController.getCastlingRights());
				params.setSide(gameController.getSide());
				params.setUiZobristKey(gameController.getZobristKey());
				params.setTimeLimit(tl);
				params.setFiftyMoveCounter(gameController.getFiftyMoveCounter());
				params.setEngineMode(EngineConstants.EngineMode.NON_FIXED_DEPTH);
				
				SearchResult searchResult = engine.search(params);
				
				if (searchResult.getBestMove() == 0) {
					System.out.println("MOVE SIFIR.");
				}
				
				long e = System.currentTimeMillis();
				System.out.println("fark = " + (e - start));
				callback.execute(searchResult.getBestMove());
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
		depthLimit = DEFAULT_DEPTH_LIMIT;
	}
	
	private boolean isFast() {
		ArrayList<BotGamePlayMove> moveHistory = gameController.getMoveHistory();
		if (moveHistory.size() > 2) {
			BotGamePlayMove currentMove = moveHistory.get(moveHistory.size() - 1);
			BotGamePlayMove previousMove = moveHistory.get(moveHistory.size() - 2);
			if (previousMove.getCapturedPiece() > 0 && currentMove.getCapturedPiece() > 0 && Utility.doProbability(80)) {
				return true;
			}
			
			if ((currentMove.getFromPiece() == EngineConstants.WHITE_KING || currentMove.getFromPiece() == EngineConstants.BLACK_KING) && Utility.doProbability(70)) {
				return true;
			}
			
			if (gameController.getMoveCount() < Utility.generateStrongRandomNumber(5, 10) && Utility.doProbability(85)) {
				return true;
			}
			
			return false;
		} else {
			return true;
		}
	}

	public void setDepthLimit(int depthLimit) {
		this.depthLimit = depthLimit;
	}
	
	public void incrementDepthLimit() {
		this.depthLimit++;
	}
	
	public void decrementDepthLimit() {
		this.depthLimit--;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public int getDepthLimit() {
		return depthLimit;
	}

	public int getTimeLimit() {
		return timeLimit;
	}
	
	
	
	

}

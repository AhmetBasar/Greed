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
package chess.engine.test.tournament;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import chess.engine.EngineConstants;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.engine.Transformer;
import chess.engine.test.tournament.ChessBoard.GameState;
import chess.fhv2.SearchEngineFifty8;
import chess.gui.BaseGui;
import chess.gui.GuiConstants;

public class TournamentManager implements Runnable {

	public static void main(String[] args) {
		new TournamentManager(null).run();
	}

	private BaseGui base;

	public TournamentManager(BaseGui base) {
		this.base = base;
	}

	@Override
	public void run() {

		if (base != null) {
			// WARNING: High frequency Hard Drive usage may be harmful.
			return;
		}

		HashMap<Long, Long> positionCountDraw = new HashMap<Long, Long>();
		HashMap<Long, Long> positionCountWhite = new HashMap<Long, Long>();
		HashMap<Long, Long> positionCountBlack = new HashMap<Long, Long>();

		// TODO Auto-generated method stub
		ChessBoard board = new ChessBoard();

		SearchEngineFifty8 engineW = SearchEngineFifty8.getNewInstance();
		SearchEngineFifty8 engineB = SearchEngineFifty8.getNewInstance();

		int drawCount = 0;
		int whiteWinCount = 0;
		int blackWinCount = 0;

		while (true) {
			if (board.getSide() == GuiConstants.WHITES_TURN) {
				engineW.setBoardStateHistory(board.getBoardStateHistory());

				SearchParameters params = new SearchParameters();
				params.setDepth(5);
				params.setEpT(board.getEpTarget());
				params.setEpS(board.getEpSquare());
				params.setBitboard(board.getBitboard());
				params.setPieces(board.getPieces());
				params.setCastlingRights(board.getCastlingRights());
				params.setSide(board.getSide());
				params.setUiZobristKey(board.getZobristKey());
				params.setTimeLimit(1);
				params.setFiftyMoveCounter(board.getFiftyMoveCounter());
				params.setEngineMode(EngineConstants.EngineMode.NON_FIXED_DEPTH);

				SearchResult searchResult = engineW.search(params);
				board.doMove(searchResult.getBestMove());

			} else {
				engineB.setBoardStateHistory(board.getBoardStateHistory());

				SearchParameters params = new SearchParameters();
				params.setDepth(5);
				params.setEpT(board.getEpTarget());
				params.setEpS(board.getEpSquare());
				params.setBitboard(board.getBitboard());
				params.setPieces(board.getPieces());
				params.setCastlingRights(board.getCastlingRights());
				params.setSide(board.getSide());
				params.setUiZobristKey(board.getZobristKey());
				params.setTimeLimit(1);
				params.setFiftyMoveCounter(board.getFiftyMoveCounter());
				params.setEngineMode(EngineConstants.EngineMode.NON_FIXED_DEPTH);

				SearchResult searchResult = engineB.search(params);
				board.doMove(searchResult.getBestMove());
			}

			if (base != null) {
				base.setBoard(Transformer.getTwoDimByteArrayStyl(board.getBitboard()));
			}

			GameState gameState = board.getGameState();
			if (gameState != GameState.PLAYING) {
				engineB.resetTT();
				engineW.resetTT();
				if (gameState == GameState.WHITE_WINS) {
					whiteWinCount++;
					increment(positionCountWhite, board.getZobristKey());
				} else if (gameState == GameState.BLACK_WINS) {
					blackWinCount++;
					increment(positionCountBlack, board.getZobristKey());
				} else {
					drawCount++;
					increment(positionCountDraw, board.getZobristKey());
				}
				System.out.println("drawCount = " + drawCount);
				System.out.println("whiteWinCount = " + whiteWinCount);
				System.out.println("blackWinCount = " + blackWinCount);
				board.resetAll();
				System.out.println("positionCountDraw = " + convertToCountBasedMap(positionCountDraw));
				System.out.println("positionCountWhite = " + convertToCountBasedMap(positionCountWhite));
				System.out.println("positionCountBlack = " + convertToCountBasedMap(positionCountBlack));
			}

		}
	}

	public void increment(HashMap<Long, Long> map, Long key) {
		Long count = map.get(key);
		map.put(key, count != null ? count.longValue() + 1 : 1);
	}

	public TreeMap<Long, Long> convertToCountBasedMap(HashMap<Long, Long> map) {
		TreeMap<Long, Long> retMap = new TreeMap<>();
		for (Map.Entry<Long, Long> entry : map.entrySet()) {
			Long count = entry.getValue();
			Long countOfCount = retMap.get(count);
			retMap.put(count, countOfCount != null ? countOfCount.longValue() + 1 : 1);
		}
		return retMap;
	}
}

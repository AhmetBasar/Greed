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
import chess.engine.ISearchableV2;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.engine.Transformer;
import chess.engine.test.tournament.ChessBoard.GameState;
import chess.fhv2.SearchEngineFifty8;
import chess.gui.BaseGui;
import chess.gui.GuiConstants;

public class TournamentManagerEngineBased implements Runnable {

	public static void main(String[] args) {
		new TournamentManagerEngineBased(null).run();
	}

	private BaseGui base;

	public TournamentManagerEngineBased(BaseGui base) {
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

		ISearchableV2 engine1 = SearchEngineFifty8.getNewInstance();
		ISearchableV2 engine2 = SearchEngineFifty8.getNewInstance();

		double engine1Score = 0d;
		double engine2Score = 0d;
		
		ISearchableV2 engineWhite = engine1;
		ISearchableV2 engineBlack = engine2;

		while (true) {
			if (board.getSide() == GuiConstants.WHITES_TURN) {
				engineWhite.setBoardStateHistory(board.getBoardStateHistory());

				SearchParameters params = new SearchParameters();
				params.setDepth(1);
				params.setEpT(board.getEpTarget());
				params.setEpS(board.getEpSquare());
				params.setBitboard(board.getBitboard());
				params.setPieces(board.getPieces());
				params.setCastlingRights(board.getCastlingRights());
				params.setSide(board.getSide());
				params.setUiZobristKey(board.getZobristKey());
				params.setTimeLimit(3);
				params.setFiftyMoveCounter(board.getFiftyMoveCounter());
				params.setEngineMode(EngineConstants.EngineMode.NON_FIXED_DEPTH);

				SearchResult searchResult = engineWhite.search(params);
				board.doMove(searchResult.getBestMove());

			} else {
				engineBlack.setBoardStateHistory(board.getBoardStateHistory());

				SearchParameters params = new SearchParameters();
				params.setDepth(1);
				params.setEpT(board.getEpTarget());
				params.setEpS(board.getEpSquare());
				params.setBitboard(board.getBitboard());
				params.setPieces(board.getPieces());
				params.setCastlingRights(board.getCastlingRights());
				params.setSide(board.getSide());
				params.setUiZobristKey(board.getZobristKey());
				params.setTimeLimit(3);
				params.setFiftyMoveCounter(board.getFiftyMoveCounter());
				params.setEngineMode(EngineConstants.EngineMode.NON_FIXED_DEPTH);

				SearchResult searchResult = engineBlack.search(params);
				board.doMove(searchResult.getBestMove());
			}

			if (base != null) {
				base.setBoard(Transformer.getTwoDimByteArrayStyl(board.getBitboard()));
			}

			GameState gameState = board.getGameState();
			if (gameState != GameState.PLAYING) {
				
				engineWhite.resetTT();
				engineBlack.resetTT();
				if (gameState == GameState.WHITE_WINS) {
					increment(positionCountWhite, board.getZobristKey());
					if (engineWhite == engine1) {
						engine1Score = engine1Score + 1;
					} else {
						engine2Score = engine2Score + 1;
					}
				} else if (gameState == GameState.BLACK_WINS) {
					increment(positionCountBlack, board.getZobristKey());
					if (engineBlack == engine1) {
						engine1Score = engine1Score + 1;
					} else {
						engine2Score = engine2Score + 1;
					}
				} else {
					increment(positionCountDraw, board.getZobristKey());
					engine1Score = engine1Score + 0.5d;
					engine2Score = engine2Score + 0.5d;
				}
				board.resetAll();
				System.out.println("positionCountDraw = " + convertToCountBasedMap(positionCountDraw));
				System.out.println("positionCountWhite = " + convertToCountBasedMap(positionCountWhite));
				System.out.println("positionCountBlack = " + convertToCountBasedMap(positionCountBlack));
				
				System.out.println("engine1Score = " + engine1Score);
				System.out.println("engine2Score = " + engine2Score);
				
				ISearchableV2 tempEngine = engineWhite;
				engineWhite = engineBlack;
				engineBlack = tempEngine;
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

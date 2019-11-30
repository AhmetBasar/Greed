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
import java.util.List;
import java.util.Map;

import chess.bot.interpreting.BotMove;

public interface IGameController {

	byte[][] getBoard();

	boolean doMove(BotMove botMove);
	
	boolean doMove(BotMove botMove, boolean throwExceptionIfFailure);
	
	void doMove(int move);
	
	void undoMove();

	void suggestMoveForUs();
	
	void suggestPreMoveForUs();
	
	int getEpTarget();

	int getEpSquare();

	long[] getBitboard();

	byte[] getPieces();

	byte[][] getCastlingRights();

	int getSide();
	
	int getPerspective();
	
	Map<Long, Integer> getBoardStateHistory();
	
	long getZobristKey();
	
	long getPawnZobristKey();
	
	ArrayList<BotGamePlayMove> getMoveHistory();
	
	boolean isLastMoveCapture();
	
	int getLastMove();
	
	void resetAll();
	
	void resetBot();
	
	int getFiftyMoveCounter();
	
	int getMoveCount();
	
	List<Long> getZobristKeyHistory();
	
}

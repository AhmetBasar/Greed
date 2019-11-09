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
package chess.engine;

import java.util.HashMap;
import java.util.Map;

public class SearchResult {
	
	private Map<Integer, Integer> possibleMoves = new HashMap<>();
	private int bestMove;
	private long evaluatedLeafNodeCount;
	private int preMove;
	private boolean isBookMove;
	private long timeConsumed;
	
	@Override
	public boolean equals(Object obj) {
		SearchResult toBeComparedObj = (SearchResult) obj;
		return 1 ==1 
				&& possibleMoves.equals(toBeComparedObj.possibleMoves)
				&& bestMove == toBeComparedObj.bestMove
				&& evaluatedLeafNodeCount == toBeComparedObj.evaluatedLeafNodeCount
				&& preMove == toBeComparedObj.preMove;
	}
	
	@Override
	public String toString() {
		return " bestMove = " + bestMove + " evaluatedLeafNodeCount = " + evaluatedLeafNodeCount + " preMove = " + preMove + " possibleMoves = " + possibleMoves + " isBookMove = " + isBookMove + " timeConsumed = " + timeConsumed ;
	}
	
	public void reset() {
		possibleMoves.clear();
		bestMove = 0;
		evaluatedLeafNodeCount = 0;
		preMove = 0;
		isBookMove = false;
		timeConsumed = 0;
	}

	public Map<Integer, Integer> getPossibleMoves() {
		return possibleMoves;
	}

	public void setPossibleMoves(Map<Integer, Integer> possibleMoves) {
		this.possibleMoves = possibleMoves;
	}

	public int getBestMove() {
		return bestMove;
	}

	public void setBestMove(int bestMove) {
		this.bestMove = bestMove;
	}

	public long getEvaluatedLeafNodeCount() {
		return evaluatedLeafNodeCount;
	}

	// WANING: There is NO thread safety.
	public void incrementEvaluatedLeafNodeCount() {
		evaluatedLeafNodeCount++;
	}

	public int getPreMove() {
		return preMove;
	}

	public void setPreMove(int preMove) {
		this.preMove = preMove;
	}

	public boolean isBookMove() {
		return isBookMove;
	}

	public void setBookMove(boolean isBookMove) {
		this.isBookMove = isBookMove;
	}

	public long getTimeConsumed() {
		return timeConsumed;
	}

	public void setTimeConsumed(long timeConsumed) {
		this.timeConsumed = timeConsumed;
	}
	
	
}
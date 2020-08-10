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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import chess.util.Defaults;
import chess.util.Utility;

public class SearchResult {
	
	private Map<Integer, Integer> possibleMoves = new HashMap<>();
	private int bestMove;
	private int preMove;
	private boolean isBookMove;
	private long timeConsumed;
	private long betaCutoffCount;
	private long nullMoveHitCount;
	private long nullMoveMissCount;
	private long ttHitCount;
	private long ttMissCount;
	private long checkMateCount;
	private long staleMateCount;
	private long negamaxNodeCount;
	private long quiescenceNodeCount;
	private long evaluatedNodeCount;
	private long pawnHashTableHitCount;
	private long pawnHashTableMissCount;
	private long repetitionCount;
	private long staticNullMovePruningCount;
	
	@Override
	public boolean equals(Object obj) {
		Field[] allFields = SearchResult.class.getDeclaredFields();
		for (Field field : allFields) {
			if (field.getName().equals("timeConsumed")) {
				continue;
			}
			field.setAccessible(true);
			try {
				Object value1 = field.get(this);
				Object value2 = field.get(obj);
				if (!value1.equals(value2)) {
					return false;
				}
			} catch (Exception e) {
				RuntimeException re = new RuntimeException(e);
				re.setStackTrace(e.getStackTrace());
				throw re;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return  "\n"
	            + " |================== GENERAL ==================|" + "\n"
				+ " |  bestMove              = " + Utility.leftSpacePad(String.valueOf(bestMove), 17) + "  |" + "\n"
				+ " |  preMove               = " + Utility.leftSpacePad(String.valueOf(preMove), 17) + "  |" + "\n"
				+ " |  possibleMoves         = " + Utility.leftSpacePad(String.valueOf(possibleMoves), 17) + "  |" + "\n"
				+ " |  isBookMove            = " + Utility.leftSpacePad(String.valueOf(isBookMove), 17) + "  |" + "\n"
				+ " |  timeConsumed          = " + Utility.leftSpacePad(String.valueOf(timeConsumed), 17) + "  |" + "\n"
				+ " |================== PRUNING ==================|" + "\n"
				+ " |  betaCutoffCount       = " + Utility.leftSpacePad(String.valueOf(betaCutoffCount), 17) + "  |" + "\n"
				+ " |  Null Move Hit Rate    = " + Utility.leftSpacePad(getHitRate(nullMoveHitCount, nullMoveMissCount), 17) + "  |" + "\n"
				+ " |  staticNullMove        = " + Utility.leftSpacePad(String.valueOf(staticNullMovePruningCount), 17) + "  |" + "\n"
				+ " |================== CACHE   ==================|" + "\n"
				+ " |  TT Hit Rate           = " + Utility.leftSpacePad(getHitRate(ttHitCount, ttMissCount), 17) + "  |" + "\n"
				+ " |  Pawn Hash Hit Rate    = " + Utility.leftSpacePad(getHitRate(pawnHashTableHitCount, pawnHashTableMissCount), 17) + "  |" + "\n"
				+ " |================== STATE   ==================|" + "\n"
				+ " |  checkMateCount        = " + Utility.leftSpacePad(String.valueOf(checkMateCount), 17) + "  |" + "\n"
				+ " |  staleMateCount        = " + Utility.leftSpacePad(String.valueOf(staleMateCount), 17) + "  |" + "\n"
				+ " |  repetitionCount       = " + Utility.leftSpacePad(String.valueOf(repetitionCount), 17) + "  |" + "\n"
				+ " |================== NODE    ==================|" + "\n"
				+ " |  negamaxNodeCount      = " + Utility.leftSpacePad(String.valueOf(negamaxNodeCount), 17) + "  |" + "\n"
				+ " |  quiescenceNodeCount   = " + Utility.leftSpacePad(String.valueOf(quiescenceNodeCount), 17) + "  |" + "\n"
				+ " |  evaluatedNodeCount    = " + Utility.leftSpacePad(String.valueOf(evaluatedNodeCount), 17) + "  |" + "\n"
				+ " |_____________________________________________| "
				;
	}
	
	private String getHitRate(long hitCount, long missCount) {
		return "%" + ((hitCount + missCount) != 0 ? String.valueOf(((100 * hitCount) / (hitCount + missCount))) : "0");
	}
	
	public void reset() {
		try {
			Field[] allFields = SearchResult.class.getDeclaredFields();
			for (Field field : allFields) {
				if (field.getType().isPrimitive()) {
					field.set(this, Defaults.getDefaultValue(field.getType()));
				} else {
					Object obj = field.get(this);
					Method method = obj.getClass().getMethod("clear");
					method.invoke(obj);
				}
			}
		} catch (Exception e) {
			RuntimeException re = new RuntimeException(e);
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
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

	public void incrementBetaCutoffCount() {
		this.betaCutoffCount++;
	}

	public void incrementNullMoveHitCount() {
		this.nullMoveHitCount++;
	}
	
	public void incrementNullMoveMissCount() {
		this.nullMoveMissCount++;
	}
	
	public void incrementTtHitCount() {
		this.ttHitCount++;
	}
	
	public void incrementTtMissCount() {
		this.ttMissCount++;
	}
	
	public void incrementCheckMateCount() {
		this.checkMateCount++;
	}
	
	public void incrementStaleMateCount() {
		this.staleMateCount++;
	}
	
	// WANING: There is NO thread safety.
	public void incrementEvaluatedNodeCount() {
		evaluatedNodeCount++;
	}
	
	public void incrementNegamaxNodeCount() {
		this.negamaxNodeCount++;
	}
	
	public void incrementQuiescenceNodeCount() {
		this.quiescenceNodeCount++;
	}
	
	public void incrementPawnHashTableHitCount() {
		this.pawnHashTableHitCount++;
	}
	
	public void incrementPawnHashTableMissCount() {
		this.pawnHashTableMissCount++;
	}
	
	public void incrementRepetitionCount() {
		this.repetitionCount++;
	}
	
	public void incrementStaticNullMovePruningCount() {
		this.staticNullMovePruningCount++;
	}
}
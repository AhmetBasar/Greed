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
package chess.engine.test.suites;

public class TestSuiteResult {
	
	private int totalSuiteCount;
	
	private int solvedSuiteCount;

	public int getTotalSuiteCount() {
		return totalSuiteCount;
	}

	public int getSolvedSuiteCount() {
		return solvedSuiteCount;
	}
	
	public synchronized void incrementTotalSuiteCount() {
		totalSuiteCount++;
	}
	
	public synchronized void incrementSolvedSuiteCount() {
		solvedSuiteCount++;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("totalSuiteCount  = " + totalSuiteCount);
		sb.append("\n");
		sb.append("solvedSuiteCount = " + solvedSuiteCount);
		return sb.toString();
	}

}

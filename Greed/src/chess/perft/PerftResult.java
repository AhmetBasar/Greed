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
package chess.perft;

import java.util.List;

public class PerftResult {

	private long nodeCount;
	private int castlingCount;
	private int epCount;
	private int captureCount;
	private int checkMateCount;
	private int promotionCount;
	private int checkCount;
	private long timeConsumed;
	
	public void resetCounters() {
		nodeCount = 0;
		castlingCount = 0;
		epCount = 0;
		captureCount = 0;
		checkMateCount = 0;
		promotionCount = 0;
		checkCount = 0;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("nodeCount      = " + nodeCount);
		sb.append("\n");
		sb.append("captureCount   = " + captureCount);
		sb.append("\n");
		sb.append("epCount        = " + epCount);
		sb.append("\n");
		sb.append("castlingCount  = " + castlingCount);
		sb.append("\n");
		sb.append("promotionCount = " + promotionCount);
		sb.append("\n");
		sb.append("checkCount     = " + checkCount);
		sb.append("\n");
		sb.append("checkMateCount = " + checkMateCount);
		sb.append("\n");
		sb.append("timeConsumed = " + timeConsumed);
		return sb.toString();
	}

	public long getNodeCount() {
		return nodeCount;
	}

	public int getCastlingCount() {
		return castlingCount;
	}

	public int getEpCount() {
		return epCount;
	}

	public int getCaptureCount() {
		return captureCount;
	}

	public int getCheckMateCount() {
		return checkMateCount;
	}

	public int getPromotionCount() {
		return promotionCount;
	}

	public int getCheckCount() {
		return checkCount;
	}

	public void incrementNodeCount() {
		nodeCount++;
	}

	public void incrementCastlingCount() {
		castlingCount++;
	}

	public void incrementEpCount() {
		epCount++;
	}

	public void incrementCaptureCount() {
		captureCount++;
	}

	public void incrementCheckMateCount() {
		checkMateCount++;
	}

	public void incrementPromotionCount() {
		promotionCount++;
	}

	public void incrementCheckCount() {
		checkCount++;
	}

	public long getTimeConsumed() {
		return timeConsumed;
	}

	public void setTimeConsumed(long timeConsumed) {
		this.timeConsumed = timeConsumed;
	}
	
	public void combinePerftResults(List<PerftResult> perftResults, int depth) {
		resetCounters();
		for (PerftResult perftResult : perftResults) {
			nodeCount += perftResult.getNodeCount();
			castlingCount += perftResult.getCastlingCount();
			epCount += perftResult.getEpCount();
			captureCount += perftResult.getCaptureCount();
			checkMateCount += perftResult.getCheckMateCount();
			promotionCount += perftResult.getPromotionCount();
			checkCount += perftResult.getCheckCount();
			timeConsumed += perftResult.getTimeConsumed();
		}
		
		if (depth == 0) {
			nodeCount = 1;
		}
	}

}

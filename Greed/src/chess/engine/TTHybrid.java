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

import chess.util.Utility;

public class TTHybrid implements ITranspositionTable {

	private static final int TT_P = 24;
	private static final int TT_F = 64 - TT_P;
	private static final int TT_SIZE = (int) Utility.SINGLE_BIT[TT_P] + 3;
	// private static final int TT_SIZE = 1048576 + 3;
	private TranspositionElement[] hashTable = new TranspositionElement[TT_SIZE];

	public TranspositionElement probe(long zobristKey) {
		int key = (int)(zobristKey >>> TT_F);
		for (int i = key; i < key + 4; i++) {
			TranspositionElement e = hashTable[i];
			if (e != null && e.zobristKey == zobristKey) {
				return e;
			}
		}

		return null;
	}

	public void recordTranspositionTable(long zobristKey, int value, int bestMove, int depth, int hashType,
			boolean isTimeout) {
		if (isTimeout) {
			return;
		}

		int address = -1;
		int lowestDepth = Integer.MAX_VALUE;

		int key = (int)(zobristKey >>> TT_F);
		for (int i = key; i < key + 3; i++) {

			TranspositionElement e = hashTable[i];
			if (e == null) {
				address = i;
				break;
			}

			if (e.zobristKey == zobristKey) {
				if (e.depth > depth) {
					return;
				}
				address = i;
				break;
			}

			if (e.depth < lowestDepth) {
				lowestDepth = e.depth;
				address = i;
			}
		}
		TranspositionElement ttElement = hashTable[address];
		if (ttElement != null) {
			ttElement.zobristKey = zobristKey;
			ttElement.score = value;
			ttElement.depth = depth;
			ttElement.bestMove = bestMove;
			ttElement.hashType = hashType;
		} else {
			ttElement = new TranspositionElement();
			ttElement.zobristKey = zobristKey;
			ttElement.score = value;
			ttElement.depth = depth;
			ttElement.bestMove = bestMove;
			ttElement.hashType = hashType;
			hashTable[address] = ttElement;
		}
	}

	public void resetTT() {
		hashTable = new TranspositionElement[TT_SIZE];
	}

}

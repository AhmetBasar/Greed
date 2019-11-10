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

public class PawnHashTable {
	
	private static final int TT_SIZE = 1048583;
	private PawnTranspositionElement[] hashTable = new PawnTranspositionElement[TT_SIZE];
	
	public void resetTT() {
		hashTable = new PawnTranspositionElement[TT_SIZE];
	}
	
	public PawnTranspositionElement probe(long pawnZobristKey) {
		return hashTable[(int)Math.abs(pawnZobristKey % TT_SIZE)];
	}
	
	public void recordTranspositionTable(long pawnZobristKey, int value){
		int index = (int)Math.abs(pawnZobristKey % hashTable.length);
		PawnTranspositionElement ttElement = hashTable[index];
		if (ttElement != null) {
			ttElement.zobristKey = pawnZobristKey;
			ttElement.score = value;
		} else {
			ttElement = new PawnTranspositionElement(); // Maybe when engine initiates, instantiate all Transposition object. in order to reduce new object cost.
			ttElement.zobristKey = pawnZobristKey;
			ttElement.score = value;
			hashTable[index] = ttElement;
		}
	}

}

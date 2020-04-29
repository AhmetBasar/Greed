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

public class TT implements ITranspositionTable {
	
	private static final int TT_SIZE = 1048583;
	private TranspositionElement[] hashTable = new TranspositionElement[TT_SIZE];
	
	public void resetTT() {
		hashTable = new TranspositionElement[TT_SIZE];
	}
	
	public TranspositionElement probe(long zobristKey) {
		return hashTable[(int)Math.abs(zobristKey % TT_SIZE)];
	}
	
	public void recordTranspositionTable(long zobristKey, int value, int bestMove, int depth, int hashType, boolean isTimeout){
		if (isTimeout) {
			return;
		}
		int index = (int)Math.abs(zobristKey % hashTable.length);
		TranspositionElement ttElement = hashTable[index];
		if(ttElement != null){
//			if(ttElement.depth <= depth){ // try only greater than...
				ttElement.zobristKey = zobristKey;
				ttElement.score = value;
				ttElement.depth = depth;
				ttElement.bestMove = bestMove;
				ttElement.hashType = hashType;
//			}
		} else {
			ttElement = new TranspositionElement(); // Maybe when engine initiates, instantiate all Transposition object. in order to reduce new object cost.
			ttElement.zobristKey = zobristKey;
			ttElement.score = value;
			ttElement.depth = depth;
			ttElement.bestMove = bestMove;
			ttElement.hashType = hashType;
			hashTable[index] = ttElement;
		}
	}
	
	public int adjustEval(int hashType, int eval, int ttScore) {
		if (hashType == EngineConstants.HASH_EXACT || (hashType == EngineConstants.HASH_ALPHA && eval > ttScore) || (hashType == EngineConstants.HASH_BETA && eval < ttScore)) {
			return ttScore;
		}
		return eval;
	}

}

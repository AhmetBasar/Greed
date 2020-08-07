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
package chess.engine.test;

import java.util.concurrent.ThreadLocalRandom;

import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.PawnHashTable;
import chess.engine.Transformer;
import chess.engine.TranspositionTable;

public class PawnHashTableTest implements EngineConstants {
	
	public static void main(String[] args) {
		testAll();
	}
	
	public static void testAll() {
		testResetTT();
		testProbe();
		testRecordTranspositionTable();
	}
	
	private static void testResetTT() {
		for (int i = 0; i < 100; i++) {
			PawnHashTable pht = new PawnHashTable();
			long[] bb = Transformer.getBitboardStyl(DebugUtility.generateRealisticRandomBoard());
			long pawnZobristKey = TranspositionTable.getPawnZobristKey(bb);
			
			int rndVal = ThreadLocalRandom.current().nextInt(0, 13);
			pht.recordTranspositionTable(pawnZobristKey, rndVal);
			
			if (pht.probe(pawnZobristKey) == null) {
				throw new RuntimeException("Failed.");
			}
			
			pht.resetTT();
			
			if (pht.probe(pawnZobristKey) != null) {
				throw new RuntimeException("Failed.");
			}
		}
	}
	
	private static void testRecordTranspositionTable() {
		for (int i = 0; i < 1000; i++) {
			PawnHashTable pht = new PawnHashTable();
			long[] bb = Transformer.getBitboardStyl(DebugUtility.generateRealisticRandomBoard());
			long pawnZobristKey = TranspositionTable.getPawnZobristKey(bb);
			
			int rndVal = ThreadLocalRandom.current().nextInt(0, 13);
			
			if (pht.probe(pawnZobristKey) != null) {
				throw new RuntimeException("Failed.");
			}
			
			pht.recordTranspositionTable(pawnZobristKey, rndVal);
			
			if (pht.probe(pawnZobristKey) == null) {
				throw new RuntimeException("Failed.");
			}
		}
	}
	
	private static void testProbe() {
		for (int i = 0; i < 1000; i++) {
			PawnHashTable pht = new PawnHashTable();
			long[] bb = Transformer.getBitboardStyl(DebugUtility.generateRealisticRandomBoard());
			long pawnZobristKey = TranspositionTable.getPawnZobristKey(bb);
			
			int rndVal = ThreadLocalRandom.current().nextInt(0, 13);
			pht.recordTranspositionTable(pawnZobristKey, rndVal);
			
			if (pht.probe(pawnZobristKey).score != rndVal) {
				throw new RuntimeException("Failed.");
			}
		}
	}
	
	
	
}

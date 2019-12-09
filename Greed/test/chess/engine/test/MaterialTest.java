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

import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.Material;
import chess.engine.Transformer;

public class MaterialTest implements EngineConstants {
	
	public static void main(String[] args) {
		testAll();
	}
	
	public static void testAll() {
		testGetMaterialKey();
		testHasSlidingPiece();
	}
	
	private static void testGetMaterialKey() {
		for (int i = 0; i < 1000000; i++) {
			long[] bb = Transformer.getBitboardStyl(DebugUtility.generateRealisticRandomBoard());
			int materialKey = Material.getMaterialKey(bb);
			
			for (int j = EngineConstants.WHITE_PAWN ; j <= EngineConstants.BLACK_QUEEN ; j++) {
				if (Long.bitCount(bb[j]) != Material.getPieceCount(materialKey, j)) {
					throw new RuntimeException("Failed.");
				}
			}			
		}
	}
	
	private static void testHasSlidingPiece() {
		for (int i = 0; i < 1000000; i++) {
			long[] bb = Transformer.getBitboardStyl(DebugUtility.generateRealisticWeightedRandomBoard());
			int materialKey = Material.getMaterialKey(bb);
			for (int side = EngineConstants.WHITE ; side <= EngineConstants.BLACK ; side++) {
				boolean hasSlidingPiece = Material.hasSlidingPiece(materialKey, side);
				if (hasSlidingPiece != ((bb[side | BISHOP] | bb[side | ROOK] | bb[side | QUEEN]) != 0)) {
					DebugUtility.throwBoard(bb);
					throw new RuntimeException("Failed.");
				}
			}			
		}
	}
	
}

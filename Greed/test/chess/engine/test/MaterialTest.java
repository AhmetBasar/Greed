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

import java.util.HashMap;
import java.util.Map;

import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.IBoard;
import chess.engine.Material;
import chess.engine.Transformer;
import chess.gui.FenOperations;

public class MaterialTest implements EngineConstants {
	
	public static void main(String[] args) {
		testAll();
	}
	
	public static void testAll() {
		testGetMaterialKey();
		testHasSlidingPiece();
		testHasMajorPiece();
		testConstants();
		testIsInsufficientMaterial();
	}
	
	private static void testGetMaterialKey() {
		for (int i = 0; i < 100000; i++) {
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
		for (int i = 0; i < 100000; i++) {
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
	
	private static void testHasMajorPiece() {
		for (int i = 0; i < 100000; i++) {
			long[] bb = Transformer.getBitboardStyl(DebugUtility.generateRealisticWeightedRandomBoard());
			int materialKey = Material.getMaterialKey(bb);
			for (int side = EngineConstants.WHITE ; side <= EngineConstants.BLACK ; side++) {
				boolean hasSlidingPiece = Material.hasMajorPiece(materialKey, side);
				if (hasSlidingPiece != ((bb[side | BISHOP] | bb[side | ROOK] | bb[side | QUEEN] | bb[side | KNIGHT]) != 0)) {
					DebugUtility.throwBoard(bb);
					throw new RuntimeException("Failed.");
				}
			}			
		}
	}
	
	private static void testConstants() {
		if (Material.KK != 0) {
			throw new RuntimeException("Failed.");
		}
		
		if (Material.KNK != Material.PIECE_VALUES[WHITE_KNIGHT]) {
			throw new RuntimeException("Failed.");
		}
		
		if (Material.KKN != Material.PIECE_VALUES[BLACK_KNIGHT]) {
			throw new RuntimeException("Failed.");
		}
		
		if (Material.KNNK != 2 * Material.PIECE_VALUES[WHITE_KNIGHT]) {
			throw new RuntimeException("Failed.");
		}
		
		if (Material.KKNN != 2 * Material.PIECE_VALUES[BLACK_KNIGHT]) {
			throw new RuntimeException("Failed.");
		}
		
		if (Material.KNKN != Material.PIECE_VALUES[WHITE_KNIGHT] + Material.PIECE_VALUES[BLACK_KNIGHT]) {
			throw new RuntimeException("Failed.");
		}
		
		if (Material.KBK != Material.PIECE_VALUES[WHITE_BISHOP]) {
			throw new RuntimeException("Failed.");
		}
		
		if (Material.KKB != Material.PIECE_VALUES[BLACK_BISHOP]) {
			throw new RuntimeException("Failed.");
		}
		
		if (Material.KNKB != Material.PIECE_VALUES[WHITE_KNIGHT] + Material.PIECE_VALUES[BLACK_BISHOP]) {
			throw new RuntimeException("Failed.");
		}
		
		if (Material.KBKN != Material.PIECE_VALUES[WHITE_BISHOP] + Material.PIECE_VALUES[BLACK_KNIGHT]) {
			throw new RuntimeException("Failed.");
		}
		
		if (Material.KBKB != Material.PIECE_VALUES[WHITE_BISHOP] + Material.PIECE_VALUES[BLACK_BISHOP]) {
			throw new RuntimeException("Failed.");
		}
	}
	
	private static void testIsInsufficientMaterial() {
		Map<String, Boolean> testPositions = new HashMap<String, Boolean>();
		testPositions.put("1nb1kbn1/8/8/8/8/8/8/1NB1KBN1 w KQkq -", false);
		testPositions.put("4k3/8/8/8/8/8/8/4K3 w KQkq -", true);
		testPositions.put("4k3/8/8/8/8/8/8/4K1N1 w KQkq -", true);
		testPositions.put("4k1n1/8/8/8/8/8/8/4K3 w KQkq -", true);
		testPositions.put("4k3/8/8/8/8/8/8/1N2K1N1 w KQkq -", true);
		testPositions.put("1n2k1n1/8/8/8/8/8/8/4K3 w KQkq -", true);
		testPositions.put("1n2k3/8/8/8/8/8/8/1N2K3 w KQkq -", true);
		testPositions.put("4k3/8/8/8/8/8/8/2B1K3 w KQkq -", true);
		testPositions.put("2b1k3/8/8/8/8/8/8/4K3 w KQkq -", true);
		testPositions.put("2b1k3/8/8/8/8/8/8/1N2K3 w KQkq -", true);
		testPositions.put("1n2k3/8/8/8/8/8/8/2B1K3 w KQkq -", true);
		testPositions.put("2b1k3/8/8/8/8/8/8/2B1K3 w KQkq -", true);
		testPositions.put("2b1k3/8/8/8/8/8/3P4/2B1K3 w KQkq -", false);
		testPositions.put("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -", false);
		
		for (Map.Entry<String, Boolean> entry : testPositions.entrySet()) {
			String fenStr = entry.getKey();
			Boolean isInsufficientMaterial = entry.getValue();
			
			FenOperations fenOperations = new FenOperations();
			fenOperations.setFenString(fenStr);
			IBoard board = fenOperations.getIBoard();
			if (Material.isInsufficientMaterial(board.getMaterialKey()) != isInsufficientMaterial) {
				throw new RuntimeException("Failed.");	
			}
		}
	}
	
}

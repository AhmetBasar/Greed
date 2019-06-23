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

import chess.engine.EngineConstants;
import chess.engine.Transformer;
import chess.evaluation.PassedPawn;
import chess.gui.FenOperations;

/**
 * https://www.chessprogramming.org/Passed_Pawns_(Bitboards)
 **/
public class PassedPawnTest {

	public static void main(String[] args) {
		testAll();
	}

	public static void testAll() {
		testPassedPawnPositions();
	}

	// http://en.wikipedia.org/wiki/Passed_pawn
	public static void testPassedPawnPositions() {

		FenOperations fenOperations = new FenOperations();
		fenOperations.setFenString("7k/8/7p/1P2Pp1P/2Pp1PP1/8/8/7K w - - 0 0");
		long[] bb = Transformer.getBitboardStyl(fenOperations.getBoard());

		long whitePassedPawns = PassedPawn.whitePassedPawns(bb[EngineConstants.WHITE_PAWN],
				bb[EngineConstants.BLACK_PAWN]);
		long blackPassedPawns = PassedPawn.blackPassedPawns(bb[EngineConstants.BLACK_PAWN],
				bb[EngineConstants.WHITE_PAWN]);

		if (whitePassedPawns != 77376520192L) {
			throw new RuntimeException("Failed.");
		}

		if (blackPassedPawns != 134217728L) {
			throw new RuntimeException("Failed.");
		}

	}

}

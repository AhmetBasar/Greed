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
import chess.engine.Transformer;
import chess.evaluation.IsolatedPawn;

public class IsolatedPawnTest {

	public static void main(String[] args) {
		testAll();
	}

	public static void testAll() {
		for (int i = 0; i < 1000000; i++) {
			byte[][] sourceBoard = DebugUtility.generateRandomBoard();
			long[] bitboard = Transformer.getBitboardStyl(sourceBoard);
			long whitePawns = bitboard[EngineConstants.WHITE_PAWN];
			long blackPawns = bitboard[EngineConstants.BLACK_PAWN];
			int evalWithSquareCentric = IsolatedPawn.evaluateIsolatedPawnsSquareCentric(bitboard);
			int evalWithSetWise = IsolatedPawn.evaluateIsolatedPawnsSetWise(whitePawns, blackPawns);
			if (evalWithSquareCentric != evalWithSetWise) {
				throw new RuntimeException("Not Equal.");
			}
		}
	}

}

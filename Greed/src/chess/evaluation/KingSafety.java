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
package chess.evaluation;

import chess.engine.EngineConstants;
import chess.util.Utility;

public class KingSafety implements EngineConstants {
	
	private static final int PENALTY_PAWN_SHIELD_ONE = 5;
	private static final int PENALTY_PAWN_SHIELD_TWO = 10;
	private static final int PENALTY_PAWN_SHIELD_GT_TWO = 15;
	
	// TODO : decrease this penalty score
	// TODO : decrease this penalty score
	// TODO : decrease this penalty score
	private static final int PENALTY_KING_ON_OPEN_FILE = 5;
	// TODO : decrease this penalty score
	// TODO : decrease this penalty score
	// TODO : decrease this penalty score
	
	public static int evaluateWhiteKingPawnShield(int whiteKingSquare, long wp, long bp, int blackTotalPieceValue) {
		int score = 0;
		int kingFile = Utility.getFile(whiteKingSquare);
		if (kingFile < 3) {
			score -= (evaluatePawnShield(wp, A2, A3, A4));
			score -= evaluatePawnShield(wp, B2, B3, B4);
			score -= (evaluatePawnShield(wp, C2, C3, C4));
		} else if (kingFile > 4) {
			score -= (evaluatePawnShield(wp, F2, F3, F4));
			score -= evaluatePawnShield(wp, G2, G3, G4);
			score -= (evaluatePawnShield(wp, H2, H3, H4));
		} else if (((wp | bp) & EngineConstants.FILE[kingFile]) == 0) {
			score -= PENALTY_KING_ON_OPEN_FILE;
		}
		return (score * blackTotalPieceValue) / EngineConstants.ALL_PIECES_V;
	}
	
	public static int evaluateBlackKingPawnShield(int blackKingSquare, long bp, long wp, int whiteTotalPieceValue) {
		int score = 0;
		int kingFile = Utility.getFile(blackKingSquare);
		if (kingFile < 3) {
			score += evaluatePawnShield(bp, A7, A6, A5);
			score += evaluatePawnShield(bp, B7, B6, B5);
			score += evaluatePawnShield(bp, C7, C6, C5);
		} else if (kingFile > 4) {
			score += evaluatePawnShield(bp, F7, F6, F5);
			score += evaluatePawnShield(bp, G7, G6, G5);
			score += evaluatePawnShield(bp, H7, H6, H5);
		} else if (((bp | wp) & EngineConstants.FILE[kingFile]) == 0) {
			score += PENALTY_KING_ON_OPEN_FILE;
		}
		return (score * whiteTotalPieceValue) / EngineConstants.ALL_PIECES_V;
	}
	
	private static int evaluatePawnShield(long p, int sqOrig, int sqOne, int sqTwo) {
		if ((p & Utility.SINGLE_BIT[sqOrig]) != 0) {
			// no penalty.
			return 0;
		} else if ((p & Utility.SINGLE_BIT[sqOne]) != 0) {
			return PENALTY_PAWN_SHIELD_ONE;
		} else if ((p & Utility.SINGLE_BIT[sqTwo]) != 0) {
			return PENALTY_PAWN_SHIELD_TWO;
		} else {
			return PENALTY_PAWN_SHIELD_GT_TWO;
		}
	}

}

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
package chess.engine.test.suites;

import chess.engine.IBoard;
import chess.engine.Transformer;
import chess.util.Utility;

public class FenGenerator {

	private static final char[] PIECES = new char[] { '.', '.', 'P', 'p', 'N', 'n', 'B', 'b', 'R', 'r', 'Q', 'q', 'K', 'k' };

	public static final String[] FILES = new String[] { "a", "b", "c", "d", "e", "f", "g", "h" };

	private static final char[] SIDE_TO_MOVE = new char[] { 'w', 'b' };

	public static String getFenString(IBoard b) {
		StringBuilder sbFenString = new StringBuilder();
		byte[][] board = Transformer.getTwoDimByteArrayStyl(b.getBitboard());
		for (int rank = 0; rank < 8; rank++) {
			StringBuilder sbRankString = new StringBuilder();
			int emptySquareCount = 0;
			for (int file = 0; file < 8; file++) {
				int pieceIndex = board[file][rank];
				if (pieceIndex == 0) {
					emptySquareCount++;
				} else {
					if (emptySquareCount > 0) {
						sbRankString.append(emptySquareCount);
						emptySquareCount = 0;
					}
					sbRankString.append(PIECES[pieceIndex]);
				}
			}
			if (emptySquareCount > 0) {
				sbRankString.append(emptySquareCount);
			}
			sbFenString.append(sbRankString);
			if (rank != 7) {
				sbFenString.append("/");
			}
		}

		sbFenString.append(" ");

		sbFenString.append(SIDE_TO_MOVE[b.getSide()]);

		sbFenString.append(" ");

		byte[][] castlingRights = b.getCastlingRights();
		if (castlingRights[0][1] == 1) {
			sbFenString.append('K');
		}
		if (castlingRights[0][0] == 1) {
			sbFenString.append('Q');
		}
		if (castlingRights[1][1] == 1) {
			sbFenString.append('k');
		}
		if (castlingRights[1][0] == 1) {
			sbFenString.append('q');
		}
		if (castlingRights[0][0] == 0 && castlingRights[0][1] == 0 && castlingRights[1][0] == 0
				&& castlingRights[1][1] == 0) {
			sbFenString.append('-');
		}

		sbFenString.append(" ");

		if (b.getEpTarget() == 64) {
			sbFenString.append('-');
		} else {
			sbFenString.append(FILES[Utility.getFile(b.getEpTarget())]);
			sbFenString.append(Utility.getRank(b.getEpTarget()));
		}

		return sbFenString.toString();
	}
}

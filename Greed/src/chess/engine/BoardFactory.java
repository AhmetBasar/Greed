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

import java.util.List;

public class BoardFactory {
	
	public static IBoard getInstance2(long[] bitboard, byte[] pieces, int epT, byte[][] castlingRights,
			int fiftyMoveCounter, List<Long> zobristKeyHistory, int side) {
		return new BoardV7(bitboard, pieces, epT, castlingRights, fiftyMoveCounter, zobristKeyHistory, side);
	}
	
	public static IBoard getInstance2(SearchParameters sp) {
		return new BoardV7(sp.getBitboard(), sp.getPieces(), sp.getEpT(),
				sp.getCastlingRights(), sp.getFiftyMoveCounter(),
				sp.getZobristKeyHistory(), sp.getSide());
	}
}

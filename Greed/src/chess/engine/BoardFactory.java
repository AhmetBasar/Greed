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

public class BoardFactory {
	
	public static IBoard getInstance(long[] bitboard, byte[] pieces, int epT, int epS, int depth, byte[][] castlingRights, long zobristKey, int fiftyMoveCounter, long pawnZobristKey) {
		return new BoardV5(bitboard, pieces, epT, epS, depth, castlingRights, zobristKey, fiftyMoveCounter, pawnZobristKey);
	}
	
	public static IBoard getInstance6(long[] bitboard, byte[] pieces, int epT, int epS, int depth, byte[][] castlingRights, long zobristKey, int fiftyMoveCounter, long pawnZobristKey) {
		return new BoardV6(bitboard, pieces, epT, depth, castlingRights, zobristKey, fiftyMoveCounter, pawnZobristKey);
	}

	public static IBoard getInstance7(long[] bitboard, byte[] pieces, int epT, int epS, int depth, byte[][] castlingRights, long zobristKey, int fiftyMoveCounter, long pawnZobristKey) {
		return new BoardV7(bitboard, pieces, epT, depth, castlingRights, zobristKey, fiftyMoveCounter, pawnZobristKey);
	}
	
}

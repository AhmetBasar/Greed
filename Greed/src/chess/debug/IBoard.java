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
package chess.debug;

public interface IBoard {
	
	long[] getBitboard();
	
	byte[] getPieces();
	
	int getEpTarget(int depth);
	
	byte[][] getCastlingRights(int depth);
	
	int[] getMoveList(int depth);
	
	long getZobristKey(int depth);

	void deepDive(int depth);
	
	void deeperDive(int depth);
	
	void doMove(int move, int side, int opSide, int depth);
	
	boolean hasRepeated(long zobristKey, int depth);
	
	void undoMove(int move, int side, int opSide, int depth);
	
	void doMoveWithoutZobrist(int move, int side, int opSide, int depth);
	
	void undoMoveWithoutZobrist(int move, int side, int opSide, int depth);
	
	void doNullMove(int depth, int side);
	
	void undoNullMove(int depth);
	
}

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

public interface IBoard {
	
	int getEpTarget();
	
	void doNullMove();
	
	void undoNullMove();
	
	void doMove(int move);
	
	void undoMove(int move);
	
	void doMoveWithoutZobrist(int move);
	
	void undoMoveWithoutZobrist(int move);
	
	byte[][] getCastlingRights();
	
	byte getCapturedPiece();

	long[] getBitboard();

	byte[] getPieces();
	
	long getZobristKey();
	
	long getPawnZobristKey();
	
	int getFiftyMoveCounter();
	
	int getNullMoveCounter();
	
	boolean hasRepeated(long zobristKey, SearchResult searchResult);
	
	int getSide();
	
	int getOpSide();
	
	long getOccupiedSquares();

	long getEmptySquares();

	long[] getOccupiedSquaresBySide();
	
	int[] getKingSquares();
	
	long getCheckers();
	
	long getPinnedPieces();

	long getDiscoveredPieces();
	
	int getMaterialKey();
	
	boolean isLegal(int move);

	boolean isDiscoveredMove(int from);
	
	boolean isValid(int move);
	
}

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

public interface MoveGenerationConstants {

	int[] pushDiffs = { 8, 64 - 8 };
	long[] promotionMask = { EngineConstants.ROW_8, EngineConstants.ROW_1 };
	long[] doublePushMask = { EngineConstants.ROW_3, EngineConstants.ROW_6 };
	long[] fileMask = { ~EngineConstants.FILE_H, ~EngineConstants.FILE_A };
	int[][] attackDiffs = { { 7, 64 - 9 }, { 9, 64 - 7 } };
	int[][][] castlingShift = { { {1, 2, 3} , {5, 6} } , { {57, 58, 59} , {61, 62} } };
	int[][] castlingTarget = {{2, 6}, {58, 62}};
	int[][] betweenKingAndRook = {{3, 5}, {59, 61}};
	byte[] kingPositions = { 4, 60 };
	
	public static final int MAX_PLIES = 64;
	
}

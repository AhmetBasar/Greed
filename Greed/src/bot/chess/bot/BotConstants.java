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
package chess.bot;

public class BotConstants {
	
	public static final int[][] ONE_DIMENSION_INDEXES = new int[][] {
		{56, 48, 40, 32, 24, 16,  8,  0},
		{57, 49, 41, 33, 25, 17,  9,  1},
		{58, 50, 42, 34, 26, 18, 10,  2},
		{59, 51, 43, 35, 27, 19, 11,  3},
		{60, 52, 44, 36, 28, 20, 12,  4},
		{61, 53, 45, 37, 29, 21, 13,  5},
		{62, 54, 46, 38, 30, 22, 14,  6},
		{63, 55, 47, 39, 31, 23, 15,  7},
	};
	
	public static final int[][] TWO_DIMENSION_INDEXES = new int[][] {
		     {0, 7}, {1, 7}, {2, 7}, {3, 7}, {4, 7}, {5, 7}, {6, 7}, {7, 7},
			 {0, 6}, {1, 6}, {2, 6}, {3, 6}, {4, 6}, {5, 6}, {6, 6}, {7, 6},
			 {0, 5}, {1, 5}, {2, 5}, {3, 5}, {4, 5}, {5, 5}, {6, 5}, {7, 5},
			 {0, 4}, {1, 4}, {2, 4}, {3, 4}, {4, 4}, {5, 4}, {6, 4}, {7, 4},
			 {0, 3}, {1, 3}, {2, 3}, {3, 3}, {4, 3}, {5, 3}, {6, 3}, {7, 3},
			 {0, 2}, {1, 2}, {2, 2}, {3, 2}, {4, 2}, {5, 2}, {6, 2}, {7, 2},
			 {0, 1}, {1, 1}, {2, 1}, {3, 1}, {4, 1}, {5, 1}, {6, 1}, {7, 1},
			 {0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}};

}

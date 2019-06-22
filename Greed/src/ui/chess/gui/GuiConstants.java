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
package chess.gui;

import java.awt.Color;

public interface GuiConstants {
	
	public static final String EFFECT_THREAD_NAME = "Piece Effects";
    public static final Color COLOR_WHITE = new Color(255, 255, 255);
    public static final Color COLOR_BLACK = new Color(122, 177, 222);
    public static final Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
    public static final int WHITE_PERSPECTIVE = 0;
    public static final int BLACK_PERSPECTIVE = 1;
    public static final int CELL_WIDTH = 50;
    public static final int CELL_HEIGHT = 50;
    public static final int LAYER_STATIC_PIECE = 0;
    public static final int LAYER_MOVING_PIECE = 1;
	public static final int WHITES_TURN = 0;
	public static final int BLACKS_TURN = 1;
    
   
   public static final int[][][] PERSPECTIVE_BOARD_MAPPING ={
		   												{{0, 7}, {1, 7}, {2, 7}, {3, 7}, {4, 7}, {5, 7}, {6, 7}, {7, 7},
			   											 {0, 6}, {1, 6}, {2, 6}, {3, 6}, {4, 6}, {5, 6}, {6, 6}, {7, 6},
			   											 {0, 5}, {1, 5}, {2, 5}, {3, 5}, {4, 5}, {5, 5}, {6, 5}, {7, 5},
			   											 {0, 4}, {1, 4}, {2, 4}, {3, 4}, {4, 4}, {5, 4}, {6, 4}, {7, 4},
			   											 {0, 3}, {1, 3}, {2, 3}, {3, 3}, {4, 3}, {5, 3}, {6, 3}, {7, 3},
			   											 {0, 2}, {1, 2}, {2, 2}, {3, 2}, {4, 2}, {5, 2}, {6, 2}, {7, 2},
			   											 {0, 1}, {1, 1}, {2, 1}, {3, 1}, {4, 1}, {5, 1}, {6, 1}, {7, 1},
			   											 {0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}}
		   											   ,
		   											   	{{7, 0}, {6, 0}, {5, 0}, {4, 0}, {3, 0}, {2, 0}, {1, 0}, {0, 0},
		   												 {7, 1}, {6, 1}, {5, 1}, {4, 1}, {3, 1}, {2, 1}, {1, 1}, {0, 1},
		   												 {7, 2}, {6, 2}, {5, 2}, {4, 2}, {3, 2}, {2, 2}, {1, 2}, {0, 2},
		   												 {7, 3}, {6, 3}, {5, 3}, {4, 3}, {3, 3}, {2, 3}, {1, 3}, {0, 3},
		   												 {7, 4}, {6, 4}, {5, 4}, {4, 4}, {3, 4}, {2, 4}, {1, 4}, {0, 4},
		   												 {7, 5}, {6, 5}, {5, 5}, {4, 5}, {3, 5}, {2, 5}, {1, 5}, {0, 5},
		   												 {7, 6}, {6, 6}, {5, 6}, {4, 6}, {3, 6}, {2, 6}, {1, 6}, {0, 6},
		   												 {7, 7}, {6, 7}, {5, 7}, {4, 7}, {3, 7}, {2, 7}, {1, 7}, {0, 7}}
			   										   };
   
   public static final int[][] PERSPECTIVE_CELL_MAPPING = { {0,  63}, {1,  62}, {2,  61}, {3,  60}, {4,  59}, {5,  58}, {6,  57}, {7,  56},
   															{8,  55}, {9,  54}, {10, 53}, {11, 52}, {12, 51}, {13, 50}, {14, 49}, {15, 48},
   															{16, 47}, {17, 46}, {18, 45}, {19, 44}, {20, 43}, {21, 42}, {22, 41}, {23, 40},
   															{24, 39}, {25, 38}, {26, 37}, {27, 36}, {28, 35}, {29, 34}, {30, 33}, {31, 32},
   															{32, 31}, {33, 30}, {34, 29}, {35, 28}, {36, 27}, {37, 26}, {38, 25}, {39, 24},
   															{40, 23}, {41, 22}, {42, 21}, {43, 20}, {44, 19}, {45, 18}, {46, 17}, {47, 16},
   															{48, 15}, {49, 14}, {50, 13}, {51, 12}, {52, 11}, {53, 10}, {54,  9}, {55,  8},
   															{56,  7}, {57,  6}, {58,  5}, {59,  4}, {60,  3}, {61,  2}, {62,  1}, {63,  0}};
   
   
   
}

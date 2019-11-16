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
package chess.movegen;

import chess.debug.DebugUtility;
import chess.util.Utility;

//https://github.com/sandermvdb/chess22k
public class MagicBitboard {
	
	public static void main(String[] args) {
	}
	
	private static long[] rookMasks = new long[64];
	
	static {
		generateRookMask();
	}
	
	private static void generateRookMask() {
		
		for (int s = 0 ; s < 64 ; s++) {
			long rookMask = 0L;
			for (int right = s + 1 ; right <= ((s >>> 3) * 8) + 6 ; right++) {
				rookMask |= Utility.SINGLE_BIT[right];
			}
			
			for (int left = s - 1 ; left >= ((s >>> 3) * 8) + 1 ; left--) {
				rookMask |= Utility.SINGLE_BIT[left];
			}
			
			for (int up = s + 8 ; up <= 55 ; up += 8) {
				rookMask |= Utility.SINGLE_BIT[up];
			}
			
			for (int down = s - 8 ; down >= 8 ; down -= 8) {
				rookMask |= Utility.SINGLE_BIT[down];
			}
			
			rookMasks[s] = rookMask;
		}
	}
}
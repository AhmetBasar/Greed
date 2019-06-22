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
package chess.bot.test;

import chess.bot.Utility;

public class UtilityTest {
	
	public static void main(String[] args) {
		testConvertOneDimensionIndex();
		testConvertTwoDimensionIndex();
		System.out.println("Test was successfull.");
	}
	
	public static void testConvertOneDimensionIndex(){
		if (Utility.convertOneDimensionIndex(0, 0) != 56) {
			throw new RuntimeException();
		}
		
		// in horizontal order, it must be decremental by eight.
		int prev = -1;
		int current = -1;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				current = Utility.convertOneDimensionIndex(i, j);
				if (prev != -1) {
					if (current != prev - 8) {
						throw new RuntimeException();
					}
				}
				prev = j == 7 ? -1 : current;
			}
		}
		
		// in vertical order, it must be incremental by one.
		prev = -1;
		current = -1;
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				current = Utility.convertOneDimensionIndex(i, j);
				if (prev != -1) {
					if (current != prev + 1) {
						throw new RuntimeException();
					}
				}
				prev = i == 7 ? -1 : current;
			}
		}
	}
	
	public static void testConvertTwoDimensionIndex(){
		if (Utility.convertTwoDimensionIndex(56)[0] != 0 || Utility.convertTwoDimensionIndex(56)[1] != 0) {
			throw new RuntimeException();
		}
		
		for (int i = 0; i < 64; i++) {
			int[] arr = Utility.convertTwoDimensionIndex(i);
			if (Utility.convertOneDimensionIndex(arr[0], arr[1]) != i) {
				throw new RuntimeException();
			}
		}
		
	}

}

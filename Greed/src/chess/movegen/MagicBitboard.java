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

import chess.util.Utility;

//https://github.com/sandermvdb/chess22k
public class MagicBitboard {
	
	public static void main(String[] args) {
	}
	
	private static long[] rookMasks = new long[64];
	private static long[] bishopMasks = new long[64];
	private static long[] rookShifts = new long[64];
	private static long[] bishopShifts = new long[64];
	
	static {
		generateRookMasks();
		generateBishopMasks();
		generateShifts();
		long[][] bishopOccVariations = generateOccupancyVariations(bishopMasks);
		long[][] rookOccVariations = generateOccupancyVariations(rookMasks);
	}
	
	private static void generateRookMasks() {
		
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
	
	private static void generateBishopMasks() {
		for (int s = 0 ; s < 64 ; s++) {
			long bishopMask = 0L;
			
			for (int northEast = s + 9 ; northEast <= 55 && Utility.getFile(northEast) <= 6 && Utility.getFile(s) <= 6 ; northEast += 9) {
				bishopMask |= Utility.SINGLE_BIT[northEast];
			}
			
			for (int northWest = s + 7 ; northWest <= 55 && Utility.getFile(northWest) >= 1 && Utility.getFile(s) >= 1 ; northWest += 7) {
				bishopMask |= Utility.SINGLE_BIT[northWest];
			}
			
			for (int southWest = s - 9 ; southWest >= 8 && Utility.getFile(southWest) >= 1 && Utility.getFile(s) >= 1 ; southWest -= 9) {
				bishopMask |= Utility.SINGLE_BIT[southWest];
			}
			
			for (int southEast = s - 7 ; southEast >= 8 && Utility.getFile(southEast) <= 6 && Utility.getFile(s) <= 6 ; southEast -= 7) {
				bishopMask |= Utility.SINGLE_BIT[southEast];
			}
			
			bishopMasks[s] = bishopMask;
			
		}
	}
	
	private static void generateShifts() {
		for (int i = 0; i < 64; i++) {
			rookShifts[i] = 64 - Long.bitCount(rookMasks[i]);
			bishopShifts[i] = 64 - Long.bitCount(bishopMasks[i]);
		}
	}
	
	private static long[][] generateOccupancyVariations(long[] masks) {
		long[][] occVariations = new long[64][];
		
		for (int s = 0 ; s < 64 ; s++) {
			int variationCount = (int)Utility.SINGLE_BIT[Long.bitCount(masks[s])];
			occVariations[s] = new long[variationCount];
			
			for (int varIndex = 1 ; varIndex < variationCount ; varIndex++) {
				long curMask = masks[s];
				for (int i = 0 ; i < 32 - Integer.numberOfLeadingZeros(varIndex) ; i++) {
					if ((Utility.SINGLE_BIT[i] & varIndex) != 0) {
						occVariations[s][varIndex] |= Long.lowestOneBit(curMask);
					}
				}
				curMask &= (curMask - 1);
			}
		}
		
		return occVariations;
	}
	
}
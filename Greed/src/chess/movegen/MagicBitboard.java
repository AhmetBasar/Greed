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

// https://github.com/sandermvdb/chess22k
public class MagicBitboard {
	
	public static void main(String[] args) {
	}
	
	public static final long[] rookMasks = new long[64];
	public static final long[] bishopMasks = new long[64];
	
	public static final long[] rookShifts = new long[64];
	public static final long[] bishopShifts = new long[64];
	
	public static final long[][] rookMoves = new long[64][];
	public static final long[][] bishopMoves = new long[64][];
	
	public static final long[][] rookOccVariations;
	public static final long[][] bishopOccVariations;
	
	public static final long[] rookMagicNumbers = { 0xa180022080400230L, 0x40100040022000L, 0x80088020001002L, 0x80080280841000L, 0x4200042010460008L,
			0x4800a0003040080L, 0x400110082041008L, 0x8000a041000880L, 0x10138001a080c010L, 0x804008200480L, 0x10011012000c0L, 0x22004128102200L,
			0x200081201200cL, 0x202a001048460004L, 0x81000100420004L, 0x4000800380004500L, 0x208002904001L, 0x90004040026008L, 0x208808010002001L,
			0x2002020020704940L, 0x8048010008110005L, 0x6820808004002200L, 0xa80040008023011L, 0xb1460000811044L, 0x4204400080008ea0L, 0xb002400180200184L,
			0x2020200080100380L, 0x10080080100080L, 0x2204080080800400L, 0xa40080360080L, 0x2040604002810b1L, 0x8c218600004104L, 0x8180004000402000L,
			0x488c402000401001L, 0x4018a00080801004L, 0x1230002105001008L, 0x8904800800800400L, 0x42000c42003810L, 0x8408110400b012L, 0x18086182000401L,
			0x2240088020c28000L, 0x1001201040c004L, 0xa02008010420020L, 0x10003009010060L, 0x4008008008014L, 0x80020004008080L, 0x282020001008080L,
			0x50000181204a0004L, 0x102042111804200L, 0x40002010004001c0L, 0x19220045508200L, 0x20030010060a900L, 0x8018028040080L, 0x88240002008080L,
			0x10301802830400L, 0x332a4081140200L, 0x8080010a601241L, 0x1008010400021L, 0x4082001007241L, 0x211009001200509L, 0x8015001002441801L,
			0x801000804000603L, 0xc0900220024a401L, 0x1000200608243L };
	public static final long[] bishopMagicNumbers = { 0x2910054208004104L, 0x2100630a7020180L, 0x5822022042000000L, 0x2ca804a100200020L, 0x204042200000900L,
			0x2002121024000002L, 0x80404104202000e8L, 0x812a020205010840L, 0x8005181184080048L, 0x1001c20208010101L, 0x1001080204002100L, 0x1810080489021800L,
			0x62040420010a00L, 0x5028043004300020L, 0xc0080a4402605002L, 0x8a00a0104220200L, 0x940000410821212L, 0x1808024a280210L, 0x40c0422080a0598L,
			0x4228020082004050L, 0x200800400e00100L, 0x20b001230021040L, 0x90a0201900c00L, 0x4940120a0a0108L, 0x20208050a42180L, 0x1004804b280200L,
			0x2048020024040010L, 0x102c04004010200L, 0x20408204c002010L, 0x2411100020080c1L, 0x102a008084042100L, 0x941030000a09846L, 0x244100800400200L,
			0x4000901010080696L, 0x280404180020L, 0x800042008240100L, 0x220008400088020L, 0x4020182000904c9L, 0x23010400020600L, 0x41040020110302L,
			0x412101004020818L, 0x8022080a09404208L, 0x1401210240484800L, 0x22244208010080L, 0x1105040104000210L, 0x2040088800c40081L, 0x8184810252000400L,
			0x4004610041002200L, 0x40201a444400810L, 0x4611010802020008L, 0x80000b0401040402L, 0x20004821880a00L, 0x8200002022440100L, 0x9431801010068L,
			0x1040c20806108040L, 0x804901403022a40L, 0x2400202602104000L, 0x208520209440204L, 0x40c000022013020L, 0x2000104000420600L, 0x400000260142410L,
			0x800633408100500L, 0x2404080a1410L, 0x138200122002900L };
	
	static {
		generateRookMasks();
		generateBishopMasks();
		generateShifts();
		rookOccVariations = generateOccupancyVariations(rookMasks);
		bishopOccVariations = generateOccupancyVariations(bishopMasks);
		generateRookMoves(rookOccVariations);
		generateBishopMoves(bishopOccVariations);
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
					curMask &= (curMask - 1);
				}
			}
		}
		
		return occVariations;
	}
	
	private static void generateRookMoves(long[][] rookOccVariations) {
		for (int s = 0 ; s < 64 ; s++) {
			rookMoves[s] = new long[rookOccVariations[s].length];
			for (int varIndex = 0 ; varIndex < rookOccVariations[s].length ; varIndex++) {
				long moves = 0;
				int magicIndex = (int)((rookOccVariations[s][varIndex] * rookMagicNumbers[s]) >>> rookShifts[s]);
				
				for (int up = s + 8 ; up <= 63 ; up += 8) {
					moves |= Utility.SINGLE_BIT[up];
					if ((rookOccVariations[s][varIndex] & Utility.SINGLE_BIT[up]) != 0) {
						break;
					}
				}
				
				for (int down = s - 8 ; down >= 0 ; down -= 8) {
					moves |= Utility.SINGLE_BIT[down];
					if ((rookOccVariations[s][varIndex] & Utility.SINGLE_BIT[down]) != 0) {
						break;
					}
				}
				
				for (int right = s + 1 ; right % 8 != 0 ; right++) {
					moves |= Utility.SINGLE_BIT[right];
					if ((rookOccVariations[s][varIndex] & Utility.SINGLE_BIT[right]) != 0) {
						break;
					}
				}
				
				for (int left = s - 1 ; left % 8 != 7 && left >= 0 ; left--) {
					moves |= Utility.SINGLE_BIT[left];
					if ((rookOccVariations[s][varIndex] & Utility.SINGLE_BIT[left]) != 0) {
						break;
					}
				}
				
				rookMoves[s][magicIndex] = moves;
			}
		}
	}
	
	private static void generateBishopMoves(long[][] bishopVariations) {
		for (int s = 0 ; s < 64 ; s++) {
			bishopMoves[s] = new long[bishopVariations[s].length];
			for (int varIndex = 0 ; varIndex < bishopVariations[s].length ; varIndex++) {
				long moves = 0;
				int magicIndex = (int)((bishopVariations[s][varIndex] * bishopMagicNumbers[s]) >>> bishopShifts[s]);
				
				for (int northEast = s + 9 ; northEast % 8 != 0 && northEast <= 63; northEast += 9) {
					moves |= Utility.SINGLE_BIT[northEast];
					if ((bishopVariations[s][varIndex] & Utility.SINGLE_BIT[northEast]) != 0) {
						break;
					}
				}
				
				for (int northWest = s + 7 ; northWest % 8 != 7 && northWest <= 63; northWest += 7) {
					moves |= Utility.SINGLE_BIT[northWest];
					if ((bishopVariations[s][varIndex] & Utility.SINGLE_BIT[northWest]) != 0) {
						break;
					}
				}
				
				for (int southWest = s - 9 ; southWest % 8 != 7 && southWest >= 0; southWest -= 9) {
					moves |= Utility.SINGLE_BIT[southWest];
					if ((bishopVariations[s][varIndex] & Utility.SINGLE_BIT[southWest]) != 0) {
						break;
					}
				}
				
				for (int southEast = s - 7 ; southEast % 8 != 0 && southEast >= 0 ; southEast -= 7) {
					moves |= Utility.SINGLE_BIT[southEast];
					if ((bishopVariations[s][varIndex] & Utility.SINGLE_BIT[southEast]) != 0) {
						break;
					}
				}
				
				bishopMoves[s][magicIndex] = moves;
			}
		}
	}
	
	public static long generateRookMoves(int from, long occ) {
		return rookMoves[from][(int) ((occ & rookMasks[from]) * rookMagicNumbers[from] >>> rookShifts[from])];
	}
	
	public static long generateBishopMoves(int from, long occ) {
		return bishopMoves[from][(int) ((occ & bishopMasks[from]) * bishopMagicNumbers[from] >>> bishopShifts[from])];
	}
	
	public static long generateQueenMoves(int from, long occ) {
		return rookMoves[from][(int) ((occ & rookMasks[from]) * rookMagicNumbers[from] >>> rookShifts[from])]
				| bishopMoves[from][(int) ((occ & bishopMasks[from]) * bishopMagicNumbers[from] >>> bishopShifts[from])];
	}
	
	public static void initialize() {
		
	}
	
}
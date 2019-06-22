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

public class Transformer {
	
	public static byte[][] rIndexMap={
			   {56,57,58,59,60,61,62,63},
			   {48,49,50,51,52,53,54,55},
			   {40,41,42,43,44,45,46,47},
			   {32,33,34,35,36,37,38,39},
			   {24,25,26,27,28,29,30,31},
			   {16,17,18,19,20,21,22,23},
			   { 8, 9,10,11,12,13,14,15},
			   { 0, 1, 2, 3, 4, 5, 6, 7},
			   };
	
	public static byte[][] indexMap={
			   {0,7},{1,7},{2,7},{3,7},{4,7},{5,7},{6,7},{7,7},
			   {0,6},{1,6},{2,6},{3,6},{4,6},{5,6},{6,6},{7,6},
			   {0,5},{1,5},{2,5},{3,5},{4,5},{5,5},{6,5},{7,5},
			   {0,4},{1,4},{2,4},{3,4},{4,4},{5,4},{6,4},{7,4},
			   {0,3},{1,3},{2,3},{3,3},{4,3},{5,3},{6,3},{7,3},
			   {0,2},{1,2},{2,2},{3,2},{4,2},{5,2},{6,2},{7,2},
			   {0,1},{1,1},{2,1},{3,1},{4,1},{5,1},{6,1},{7,1},
			   {0,0},{1,0},{2,0},{3,0},{4,0},{5,0},{6,0},{7,0},
			   };
	
	public static long[] getBitboardStyl(byte[][] board) {
		long[] bitboard = new long[14];
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if ((bitboard[board[x][y]] & (1L << rIndexMap[y][x])) == 1) {
					throw new RuntimeException();
				}
				if (board[x][y] != EngineConstants.BLANK) {
					bitboard[board[x][y]] = bitboard[board[x][y]] | (1L << rIndexMap[y][x]);
				}
			}
		}
		return bitboard;
	}

	public static byte[] getByteArrayStyl(long[] bitboard) {
		byte[] board = new byte[64];

		for (byte i = 2; i < bitboard.length; i++) {
			long bb = bitboard[i];
			while (bb != 0) {
				int trailingZeros = Long.numberOfTrailingZeros(bb);
				if (board[trailingZeros] != 0) {
					throw new RuntimeException();
				}
				board[trailingZeros] = i;
				bb = bb & ~(1L << trailingZeros);
			}
		}
		return board;
	}

	public static byte[][] getTwoDimByteArrayStyl(long[] bitboard) {
		byte board[][] = new byte[8][8];

		for (byte i = 2; i < bitboard.length; i++) {
			long bb = bitboard[i];
			while (bb != 0) {
				int trailingZeros = Long.numberOfTrailingZeros(bb);
				if (board[indexMap[trailingZeros][0]][indexMap[trailingZeros][1]] != 0) {
					throw new RuntimeException();
				}
				board[indexMap[trailingZeros][0]][indexMap[trailingZeros][1]] = i;
				bb = bb & ~(1L << trailingZeros);
			}
		}

		return board;
	}
	
	public static long[] getBitboardStyl(byte[] board) {
		long[] bitboard = new long[14];
		for (int x = 0; x < 64; x++) {
			if ((bitboard[board[x]] & (1L << x)) == 1) {
				throw new RuntimeException();
			}
			if (board[x] != EngineConstants.BLANK) {
				bitboard[board[x]] = bitboard[board[x]] | (1L << x);
			}
		}
		return bitboard;
	}
}

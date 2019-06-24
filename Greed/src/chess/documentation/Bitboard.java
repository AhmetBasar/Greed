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
package chess.documentation;

import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.Transformer;
import chess.evaluation.BitboardUtility;
import chess.util.Utility;

public class Bitboard {
	
//	56 57 58 59 60 61 62 63
//	48 49 50 51 52 53 54 55
//	40 41 42 43 44 45 46 47
//	32 33 34 35 36 37 38 39
//	24 25 26 27 28 29 30 31
//	16 17 18 19 20 21 22 23
//	 8  9 10 11 12 13 14 15
//	 0  1  2  3  4  5  6  7
	
	// File To Rank. (With Anti Diagonal)
	public static void main1(String[] args) {
		byte [][] EMPTY_BOARD1={
				   {     2,     0,     0,     0,     0,     0,     0,     0},
				   {     2,     0,     0,     0,     0,     0,     0,     0},
				   {     2,     0,     0,     0,     0,     0,     0,     0},
				   {     2,     0,     0,     0,     0,     0,     0,     0},
				   {     2,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     2,     0,     0,     0,     0,     0,     0,     0},
				   {     2,     0,     0,     0,     0,     0,     0,     0}
		};
		
		byte [][] EMPTY_BOARD2={
				   {     0,     0,     0,     0,     0,     0,     0,     2},
				   {     0,     0,     0,     0,     0,     0,     2,     0},
				   {     0,     0,     0,     0,     0,     2,     0,     0},
				   {     0,     0,     0,     0,     2,     0,     0,     0},
				   {     0,     0,     0,     2,     0,     0,     0,     0},
				   {     0,     0,     2,     0,     0,     0,     0,     0},
				   {     0,     2,     0,     0,     0,     0,     0,     0},
				   {     2,     0,     0,     0,     0,     0,     0,     0}
		};
		
		byte[][] board1 = new byte[8][8];
		for(int x=0;x<=7;x++){
			for(int y=0;y<=7;y++){
				board1[x][y]=EMPTY_BOARD1[y][x];
			}
		}
		
		byte[][] board2 = new byte[8][8];
		for(int x=0;x<=7;x++){
			for(int y=0;y<=7;y++){
				board2[x][y]=EMPTY_BOARD2[y][x];
			}
		}
		
		long[] emptyBb1 = Transformer.getBitboardStyl(board1);
		long[] emptyBb2 = Transformer.getBitboardStyl(board2);
		
		DebugUtility.throwBoard(emptyBb1);
		DebugUtility.throwBoard(emptyBb2);
		emptyBb1[EngineConstants.WHITE_PAWN] = (emptyBb2[EngineConstants.WHITE_PAWN] * emptyBb1[EngineConstants.WHITE_PAWN]) >>> 56;
		DebugUtility.throwBoard(emptyBb1);
		
	}
	
	// Rank to File. (With Anti Diagonal)
	public static void main11(String[] args) {
		
		byte [][] EMPTY_BOARD1={
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     2,     2,     2,     0,     2,     2,     2,     0}
		};
		
		byte [][] EMPTY_BOARD2={
				   {     0,     0,     0,     0,     0,     0,     0,     2},
				   {     0,     0,     0,     0,     0,     0,     2,     0},
				   {     0,     0,     0,     0,     0,     2,     0,     0},
				   {     0,     0,     0,     0,     2,     0,     0,     0},
				   {     0,     0,     0,     2,     0,     0,     0,     0},
				   {     0,     0,     2,     0,     0,     0,     0,     0},
				   {     0,     2,     0,     0,     0,     0,     0,     0},
				   {     2,     0,     0,     0,     0,     0,     0,     0}
		};
		
		
		byte[][] board1 = new byte[8][8];
		for(int x=0;x<=7;x++){
			for(int y=0;y<=7;y++){
				board1[x][y]=EMPTY_BOARD1[y][x];
			}
		}
		
		byte[][] board2 = new byte[8][8];
		for(int x=0;x<=7;x++){
			for(int y=0;y<=7;y++){
				board2[x][y]=EMPTY_BOARD2[y][x];
			}
		}
		
		long[] emptyBb1 = Transformer.getBitboardStyl(board1);
		long[] emptyBb2 = Transformer.getBitboardStyl(board2);
		
		DebugUtility.throwBoard(emptyBb1);
		DebugUtility.throwBoard(emptyBb2);
//		emptyBb1[EngineConstants.WHITE_PAWN] = ((emptyBb2[EngineConstants.WHITE_PAWN] * emptyBb1[EngineConstants.WHITE_PAWN]) >>> 7) & EngineConstants.FILE_A;
		emptyBb1[EngineConstants.WHITE_PAWN] = ((EngineConstants.DIAG_MASK[0] * emptyBb1[EngineConstants.WHITE_PAWN]) >>> 7) & EngineConstants.FILE_A;
		DebugUtility.throwBoard(emptyBb1);
	}
	
	// File To Rank. (With Diagonal)
	public static void main33(String[] args) {
		
		byte [][] EMPTY_BOARD1={
				   {     0,     0,     0,     0,     0,     0,     0,     2},
				   {     0,     0,     0,     0,     0,     0,     0,     2},
				   {     0,     0,     0,     0,     0,     0,     0,     2},
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     2},
				   {     0,     0,     0,     0,     0,     0,     0,     2},
				   {     0,     0,     0,     0,     0,     0,     0,     2},
				   {     0,     0,     0,     0,     0,     0,     0,     2}
		};
		
		byte [][] EMPTY_BOARD2={
				   {     2,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     2,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     2,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     2,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     2,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     2,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     2,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     2}
		};
		
		
		byte[][] board1 = new byte[8][8];
		for(int x=0;x<=7;x++){
			for(int y=0;y<=7;y++){
				board1[x][y]=EMPTY_BOARD1[y][x];
			}
		}
		
		byte[][] board2 = new byte[8][8];
		for(int x=0;x<=7;x++){
			for(int y=0;y<=7;y++){
				board2[x][y]=EMPTY_BOARD2[y][x];
			}
		}
		
		long[] emptyBb1 = Transformer.getBitboardStyl(board1);
		long[] emptyBb2 = Transformer.getBitboardStyl(board2);
		
		DebugUtility.throwBoard(emptyBb1);
		DebugUtility.throwBoard(emptyBb2);
		emptyBb1[EngineConstants.WHITE_PAWN] = (emptyBb2[EngineConstants.WHITE_PAWN] * emptyBb1[EngineConstants.WHITE_PAWN]) >>> 56;
		DebugUtility.throwBoard(emptyBb1);
		
	}
	
	// Diagonals to Ranks
	public static void main23(String[] args) {
		
		byte [][] EMPTY_BOARD={
				   {     2,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     2,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     2,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     2,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     2,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     2,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     2}
		};
		
		byte[][] board = new byte[8][8];
		for(int x=0;x<=7;x++){
			for(int y=0;y<=7;y++){
				board[x][y]=EMPTY_BOARD[y][x];
			}
		}
		
		long[] emptyBb = Transformer.getBitboardStyl(board);
		
		DebugUtility.throwBoard(emptyBb);
		
		emptyBb[EngineConstants.WHITE_PAWN] = (EngineConstants.FILE_A * emptyBb[EngineConstants.WHITE_PAWN]) >>> 56;
		
		DebugUtility.throwBoard(emptyBb);
	}
	

	
	public static void main223(String[] args) {
		for (int i = 56; i < 64; i++) {
			DebugUtility.throwBoardAsWhitePawn(EngineConstants.DIAG_MASK[i]);
		}
		
		for (int i = 0; i < 8; i++) {
			DebugUtility.throwBoardAsWhitePawn(EngineConstants.DIAG_MASK[i]);
		}
	}
	
	public static void mainss(String[] args) {
		for (int i = 56; i < 64; i++) {
			DebugUtility.throwBoardAsWhitePawn(EngineConstants.ADIAG_MASK[i]);
		}
		
		for (int i = 0; i < 8; i++) {
			DebugUtility.throwBoardAsWhitePawn(EngineConstants.ADIAG_MASK[i]);
		}
	}
	
	public static void main(String[] args) {
		generatePreCalculatedPawnAttacks();
	}
	
	public static void generatePreCalculatedPawnAttacks() {
		
		long[] fileMask = { ~EngineConstants.FILE_H, ~EngineConstants.FILE_A };
		int[][] attackDiffs = { { 7, 64 - 9 }, { 9, 64 - 7 } };
		
		System.out.println("{");
		for (int side = 0; side < 2; side++) {
			System.out.println("{");
			for (int i = 0; i < 64; i++) {
				long fromBitboard = Utility.SINGLE_BIT[i];
				int diff0;
				int diff1;
				long toBitboard0;
				long toBitboard1;
				long toBitboard;
				
				// PAWN ATTACKS
				diff0 = attackDiffs[0][side];
				
				// Pure Pawn Attacks
				toBitboard0 = ((fromBitboard << diff0) | (fromBitboard >>> (64 - diff0))) & fileMask[0];
				
				
				diff1 = attackDiffs[1][side];
				
				// Pure Pawn Attacks
				toBitboard1 = ((fromBitboard << diff1) | (fromBitboard >>> (64 - diff1))) & fileMask[1];
				
				if ((side == 1 && i < 8) || (side == 0 && i > 55)) {
					toBitboard = 0;
				} else {
					toBitboard = toBitboard0 | toBitboard1;
				}

				if (i % 4 == 3) {
					if(i == 63){
						System.out.print(Utility.toFormattedHexString(toBitboard) + "L");
					} else {
						System.out.print(Utility.toFormattedHexString(toBitboard) + "L,");
					}
					System.out.println("");
				} else {
					System.out.print(Utility.toFormattedHexString(toBitboard) + "L, ");
				}
			}
			System.out.println("}, ");
		}
		System.out.println("};");
	}
	
	public static void generatePreCalculatedFrontSpans() {
		System.out.println("{");
		for (int side = 0; side < 2; side++) {
			System.out.println("{");
			for (int i = 0; i < 64; i++) {
				long allFrontSpans = -1L;
				if (side == EngineConstants.WHITE) {
					allFrontSpans = BitboardUtility.wFrontSpans(Utility.SINGLE_BIT[i]);
					allFrontSpans |= BitboardUtility.eastOne(allFrontSpans) | BitboardUtility.westOne(allFrontSpans);
				} else {
					allFrontSpans = BitboardUtility.bFrontSpans(Utility.SINGLE_BIT[i]);
					allFrontSpans |= BitboardUtility.eastOne(allFrontSpans) | BitboardUtility.westOne(allFrontSpans);
				}

				if (i % 4 == 3) {
					if (i == 63) {
						System.out.print(Utility.toFormattedHexString(allFrontSpans) + "L");
					} else {
						System.out.print(Utility.toFormattedHexString(allFrontSpans) + "L,");
					}
					System.out.println("");
				} else {
					System.out.print(Utility.toFormattedHexString(allFrontSpans) + "L, ");
				}
			}
			System.out.println("}, ");
		}
		System.out.println("};");

	}
	
}

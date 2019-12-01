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
package chess.debug;

import java.util.concurrent.ThreadLocalRandom;

import chess.engine.EngineConstants;
import chess.engine.Transformer;

public class DebugUtility {
	private static final byte [][] DEFAULT_BOARD={
			   {     9,     5,     7,    11,    13,     7,     5,     9},
			   {     3,     3,     3,     3,     3,     3,     3,     3},
			   {     0,     0,     0,     0,     0,     0,     0,     0},
			   {     0,     0,     0,     0,     0,     0,     0,     0},
			   {     0,     0,     0,     0,     0,     0,     0,     0},
			   {     0,     0,     0,     0,     0,     0,     0,     0},
			   {     2,     2,     2,     2,     2,     2,     2,     2},
			   {     8,     4,     6,    10,    12,     6,     4,     8}
			   };
	
	private static final byte [][] EMPTY_BOARD={
			   {     0,     0,     0,     0,     0,     0,     0,     0},
			   {     0,     0,     0,     0,     0,     0,     0,     0},
			   {     0,     0,     0,     0,     0,     0,     0,     0},
			   {     0,     0,     0,     0,     0,     0,     0,     0},
			   {     0,     0,     0,     0,     0,     0,     0,     0},
			   {     0,     0,     0,     0,     0,     0,     0,     0},
			   {     0,     0,     0,     0,     0,     0,     0,     0},
			   {     0,     0,     0,     0,     0,     0,     0,     0}
	};
	
	private static final byte [][] BOARD_2={
			   {     9,     0,     0,     0,    13,     0,     0,     9},
			   {     3,     0,     3,     3,    11,     3,     7,     0},
			   {     7,     5,     0,     0,     3,     5,     3,     0},
			   {     0,     0,     0,     2,     4,     0,     0,     0},
			   {     0,     3,     0,     0,     2,     0,     0,     0},
			   {     0,     0,     4,     0,     0,    10,     0,     3},
			   {     2,     2,     2,     6,     6,     2,     2,     2},
			   {     8,     0,     0,     0,    12,     0,     0,     8}
	};
	
	public static byte[][] getDefaultBoard(){
		byte[][] board = new byte[8][8];
		for(int x=0;x<=7;x++){
			for(int y=0;y<=7;y++){
				board[x][y]=DEFAULT_BOARD[y][x];
			}
		}
		return board;
	}
	
	public static byte[][] getEmptyBoard(){
		byte[][] board = new byte[8][8];
		for(int x=0;x<=7;x++){
			for(int y=0;y<=7;y++){
				board[x][y]=EMPTY_BOARD[y][x];
			}
		}
		return board;
	}
	
	public static byte[][] getAsymmetricBoard(){
		byte[][] board = new byte[8][8];
		for(int x=0;x<=7;x++){
			for(int y=0;y<=7;y++){
				board[x][y]=BOARD_2[y][x];
			}
		}
		return board;
	}
	
	public static byte[][] deepCloneMultiDimensionalArray(byte[][] sourceArray) {
		int firstDimSize = sourceArray.length;
		byte[][] targetArray = new byte[firstDimSize][];
	    for (int i = 0; i < firstDimSize; i++) {
	        targetArray[i] = sourceArray[i].clone();
	    }
	    return targetArray;
	}
	
	public static void throwBoard(byte[][] board){
		String tempStr="";
		int tempItem;
		for(int y=0;y<=7;y++){
			for(int x=0;x<=7;x++){
				tempItem=board[x][y];
				tempStr= tempItem+"";
				if(tempStr.length()==1){
					tempStr= "     " + tempItem;
				} else if(tempStr.length()==2){
					tempStr= "    " + tempItem;
				} else if(tempStr.length()==3){
					tempStr= "   " + tempItem;
				} else if(tempStr.length()==4){
					tempStr= "  " + tempItem;
				} else if(tempStr.length()==5){
					tempStr= " " + tempItem;
				}
//				System.out.print("--tempStr.leng = "+tempStr.length()+"--");
				System.out.print(tempStr + ",");
			}
			System.out.println(" ");
		}
		System.out.println("-----------------");
	}

	public static void throwBoard(byte[] board){
		String tempStr="";
		int tempItem;
		for(int y=63;y>=0;y--){
			tempItem=board[y];
			tempStr= tempItem+"";
			if(tempStr.length()==1){
				tempStr= "     " + tempItem;
			} else if(tempStr.length()==2){
				tempStr= "    " + tempItem;
			} else if(tempStr.length()==3){
				tempStr= "   " + tempItem;
			} else if(tempStr.length()==4){
				tempStr= "  " + tempItem;
			} else if(tempStr.length()==5){
				tempStr= " " + tempItem;
			}
			System.out.print(tempStr + ",");
			if(y%8==0){
				System.out.println(" ");
			}
		}
		System.out.println("-----------------");
	}

	public static byte[][] getFixedBoard(byte[][] b){
		byte[][] k=new byte[8][8];
		int diff=7;
		for(int y=0;y<=7;y++){
			for(int x=0;x<=7;x++){
				k[x][y+diff]=b[x][y];
			}
			diff=diff-2;
		}
		return k;
	}
	
	public static void throwBoard(long bitBoard[]){
		int tempItem;
		String tempStr;
		for(int rank = 7 ; rank >= 0 ; rank--){
			for(int file = 0 ; file < 8 ; file++){
				int index = (rank * 8) + file;
//				System.out.println("index = "+ index);
				tempItem = getItem(bitBoard, index);
				tempStr= tempItem + "";
				if(tempStr.length()==1){
					tempStr= "     " + tempItem;
				} else if(tempStr.length()==2){
					tempStr= "    " + tempItem;
				} else if(tempStr.length()==3){
					tempStr= "   " + tempItem;
				} else if(tempStr.length()==4){
					tempStr= "  " + tempItem;
				} else if(tempStr.length()==5){
					tempStr= " " + tempItem;
				} else if(tempStr.length()==6){
					tempStr= "" + tempItem;
				}
				System.out.print(tempStr + ",");
			}
			System.out.println(" ");
		}
		System.out.println("-----------------");
	}
	
	public static int getItem(long bitboard[], int index){
		int item = 0;
		if((bitboard[EngineConstants.WHITE_PAWN] & (1L << index)) >>> index == 1L){
			item = EngineConstants.WHITE_PAWN_V;
		} else if((bitboard[EngineConstants.WHITE_KNIGHT] & (1L << index)) >>> index == 1L){
			item = EngineConstants.WHITE_KNIGHT_V;
		} else if((bitboard[EngineConstants.WHITE_BISHOP] & (1L << index)) >>> index == 1L){
			item = EngineConstants.WHITE_BISHOP_V;
		} else if((bitboard[EngineConstants.WHITE_ROOK] & (1L << index)) >>> index == 1L){
			item = EngineConstants.WHITE_ROOK_V;
		} else if((bitboard[EngineConstants.WHITE_QUEEN] & (1L << index)) >>> index == 1L){
			item = EngineConstants.WHITE_QUEEN_V;
		} else if((bitboard[EngineConstants.WHITE_KING] & (1L << index)) >>> index == 1L){
			item = EngineConstants.WHITE_KING_V;
		} else if((bitboard[EngineConstants.BLACK_PAWN] & (1L << index)) >>> index == 1L){
			item = EngineConstants.BLACK_PAWN_V;
		} else if((bitboard[EngineConstants.BLACK_KNIGHT] & (1L << index)) >>> index == 1L){
			item = EngineConstants.BLACK_KNIGHT_V;
		} else if((bitboard[EngineConstants.BLACK_BISHOP] & (1L << index)) >>> index == 1L){
			item = EngineConstants.BLACK_BISHOP_V;
		} else if((bitboard[EngineConstants.BLACK_ROOK] & (1L << index)) >>> index == 1L){
			item = EngineConstants.BLACK_ROOK_V;
		} else if((bitboard[EngineConstants.BLACK_QUEEN] & (1L << index)) >>> index == 1L){
			item = EngineConstants.BLACK_QUEEN_V;
		} else if((bitboard[EngineConstants.BLACK_KING] & (1L << index)) >>> index == 1L){
			item = EngineConstants.BLACK_KING_V;
		}
		return item;
	}
	
	public static byte[][] generateRandomBoard() {
		byte[][] board = new byte[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				byte[] possibleValues = new byte[] {
						/***/
						EngineConstants.WHITE_PAWN,
						/***/
						EngineConstants.WHITE_KNIGHT,
						/***/
						EngineConstants.WHITE_BISHOP,
						/***/
						EngineConstants.WHITE_ROOK,
						/***/
						EngineConstants.WHITE_QUEEN,
						/***/
						EngineConstants.WHITE_KING,
						/***/
						EngineConstants.BLACK_PAWN,
						/***/
						EngineConstants.BLACK_KNIGHT,
						/***/
						EngineConstants.BLACK_BISHOP,
						/***/
						EngineConstants.BLACK_ROOK,
						/***/
						EngineConstants.BLACK_QUEEN,
						/***/
						EngineConstants.BLACK_KING,
						/***/
						EngineConstants.BLANK,
						/***/
				};
				int randomIndex = ThreadLocalRandom.current().nextInt(0, 13);
				board[i][j] = possibleValues[randomIndex];
			}
		}
		return board;
	}
	
	public static byte[][] generateRealisticRandomBoard() {
		
		int[] counters = new int[14];
		int[] limits = new int[]{4, 3, 3, 3, 3, 1, 4, 3, 3, 3, 3, 1, 99};
		
		byte[][] board = new byte[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				byte[] possibleValues = new byte[] {
						/***/
						EngineConstants.WHITE_PAWN,
						/***/
						EngineConstants.WHITE_KNIGHT,
						/***/
						EngineConstants.WHITE_BISHOP,
						/***/
						EngineConstants.WHITE_ROOK,
						/***/
						EngineConstants.WHITE_QUEEN,
						/***/
						EngineConstants.WHITE_KING,
						/***/
						EngineConstants.BLACK_PAWN,
						/***/
						EngineConstants.BLACK_KNIGHT,
						/***/
						EngineConstants.BLACK_BISHOP,
						/***/
						EngineConstants.BLACK_ROOK,
						/***/
						EngineConstants.BLACK_QUEEN,
						/***/
						EngineConstants.BLACK_KING,
						/***/
						EngineConstants.BLANK,
						/***/
				};
				int randomIndex = ThreadLocalRandom.current().nextInt(0, 13);
				counters[randomIndex] = counters[randomIndex] + 1;
				if (counters[randomIndex] <= limits[randomIndex]) {
					board[i][j] = possibleValues[randomIndex];
				}
			}
		}
		return board;
	}
	
	public static void throwBoardAsWhitePawn(long whitePawns) {
		long[] emptyBb = Transformer.getBitboardStyl(getEmptyBoard());
		emptyBb[EngineConstants.WHITE_PAWN] = whitePawns;
		DebugUtility.throwBoard(emptyBb);
	}
	
	public static long[] convertToBitboard(long board, final byte pieceType) {
		long[] emptyBb = Transformer.getBitboardStyl(getEmptyBoard());
		emptyBb[pieceType] = board;
		return emptyBb;
	}
	
	public static long[] reverseBitboard(long[] bitboard, boolean reverseBits) {
		long[] retBitboard = new long[14];
		for (byte i = 2; i < retBitboard.length; i = (byte) (i + 2)) {
			retBitboard[i] = bitboard[i | EngineConstants.BLACK];
			retBitboard[i | EngineConstants.BLACK] = bitboard[i];
		}
		if (reverseBits) {
			return reverseOrderBits(retBitboard);
		} else {
			return reverseOrderBytes(retBitboard);
		}
	}
	
	private static long[] reverseOrderBits(long[] bitboard) {
		long[] retBitboard = new long[14];
		for (int i = 2; i < retBitboard.length; i++) {
			retBitboard[i] = Long.reverse(bitboard[i]);
		}
		return retBitboard;
	}
	
	private static long[] reverseOrderBytes(long[] bitboard) {
		long[] retBitboard = new long[14];
		for (int i = 2; i < retBitboard.length; i++) {
			retBitboard[i] = Long.reverseBytes(bitboard[i]);
		}
		return retBitboard;
	}
	
	public static byte[][] reverseCastlingRights(byte[][] castlingRights) {
		byte[][] retCastlingRights = new byte[2][2];
		retCastlingRights[0][0] = castlingRights[1][0];
		retCastlingRights[0][1] = castlingRights[1][1];
		retCastlingRights[1][0] = castlingRights[0][0];
		retCastlingRights[1][1] = castlingRights[0][1];
		return retCastlingRights;
	}
	
}

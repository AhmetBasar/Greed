/**********************************************
 * Greed, a chess engine written in java.
 * Copyright (C) 2019 Ahmet Ba�ar
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

public interface EngineConstants {
	
	// I am still here.
	
	public static final int MOVE_LIST_SIZE = 256;
	
	public static final byte PAWN = 2, KNIGHT = 4, BISHOP = 6, ROOK = 8, QUEEN = 10, KING = 12;
	
	public static final int WHITE_PAWN_V=100, WHITE_ROOK_V=500, WHITE_KNIGHT_V=320, WHITE_BISHOP_V=330, WHITE_QUEEN_V=900, WHITE_KING_V=20000, 
			               BLACK_PAWN_V=-100, BLACK_ROOK_V=-500, BLACK_KNIGHT_V=-320, BLACK_BISHOP_V=-330, BLACK_QUEEN_V=-900, BLACK_KING_V=-20000;
	
	public static final int ALL_PIECES_V = WHITE_QUEEN_V + (2 * WHITE_ROOK_V) + (2 * WHITE_KNIGHT_V) + (2 * WHITE_BISHOP_V);
	
	public static final int END_GAME_THRESHOLD = 1230;

	public static final byte WHITE = 0, BLACK = 1;

	public static final byte WHITE_PAWN = 2, WHITE_KNIGHT = 4, WHITE_BISHOP = 6, WHITE_ROOK = 8, WHITE_QUEEN = 10,
			WHITE_KING = 12, BLACK_PAWN = 3, BLACK_KNIGHT = 5, BLACK_BISHOP = 7, BLACK_ROOK = 9, BLACK_QUEEN = 11,
			BLACK_KING = 13, BLANK = 0;
	
	public static final int[] PIECE_VALUES = {0, 0, WHITE_PAWN_V, BLACK_PAWN_V, WHITE_KNIGHT_V, BLACK_KNIGHT_V, WHITE_BISHOP_V, BLACK_BISHOP_V, 
			                                          WHITE_ROOK_V, BLACK_ROOK_V, WHITE_QUEEN_V,  BLACK_QUEEN_V,  WHITE_KING_V, BLACK_KING_V};
	
	public static final int[] PIECE_VALUES_POSITIVE = {0, 0, WHITE_PAWN_V, WHITE_PAWN_V, WHITE_KNIGHT_V, WHITE_KNIGHT_V, WHITE_BISHOP_V, WHITE_BISHOP_V, 
            WHITE_ROOK_V, WHITE_ROOK_V, WHITE_QUEEN_V, WHITE_QUEEN_V, WHITE_KING_V, WHITE_KING_V};
	
	public static final int[] PIECE_WITHOUT_SIDE = {0, 0, PAWN, PAWN, KNIGHT, KNIGHT, BISHOP, BISHOP, 
			ROOK, ROOK, QUEEN, QUEEN, KING, KING};
	
//	public static final int[] PIECE_VALUES_MVVLVA = {0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6};
	public static final int[] PIECE_VALUES_MVVLVA = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
//	public static final int PAWN_MVVLVA = 1, KNIGHT_MVVLVA = 2, BISHOP_MVVLVA = 3, ROOK_MVVLVA = 4, QUEEN_MVVLVA = 5, KING_MVVLVA = 6;
	public static final int PAWN_MVVLVA = 6, KNIGHT_MVVLVA = 5, BISHOP_MVVLVA = 4, ROOK_MVVLVA = 3, QUEEN_MVVLVA = 2, KING_MVVLVA = 1;
	
	public static final int[] SIDE_COLOR = new int[]{1, -1};
	
	public static final int DOUBLE_PUSH 			= 0b00000001;
	public static final int EP_CAPTURE 			 	= 0b00000010;
	public static final int PROMOTION		 		= 0b00000011;
	public static final int QUEEN_SIDE_CASTLING		= 0b00000100;
	public static final int KING_SIDE_CASTLING		= 0b00000101;
	public static final int CASTLING				= 0b00000100;
	
	public static final int DOUBLE_PUSH_SHIFTED			= DOUBLE_PUSH << 16;
	public static final int EP_CAPTURE_SHIFTED			= EP_CAPTURE << 16;
	public static final int PROMOTION_SHIFTED			= PROMOTION << 16;
	public static final int QUEEN_SIDE_CASTLING_SHIFTED	= QUEEN_SIDE_CASTLING << 16;
	public static final int KING_SIDE_CASTLING_SHIFTED	= KING_SIDE_CASTLING << 16;
	public static final int CASTLING_SHIFTED			= CASTLING << 16;
	
	public static final int HASH_EXACT = 1;
	public static final int HASH_ALPHA = 2;
	public static final int HASH_BETA = 3;	

	public static final long
	   ROW_1=0x00000000000000ffL,
	   ROW_2=0x000000000000ff00L,
	   ROW_3=0x0000000000ff0000L,
	   ROW_4=0x00000000ff000000L,
	   ROW_5=0x000000ff00000000L,
	   ROW_6=0x0000ff0000000000L,
	   ROW_7=0x00ff000000000000L,
	   ROW_8=0xff00000000000000L;
	
	public static final long[] ROW_MASK = {
	   ROW_1, ROW_1, ROW_1, ROW_1, ROW_1, ROW_1, ROW_1, ROW_1,
	   ROW_2, ROW_2, ROW_2, ROW_2, ROW_2, ROW_2, ROW_2, ROW_2,
	   ROW_3, ROW_3, ROW_3, ROW_3, ROW_3, ROW_3, ROW_3, ROW_3,
	   ROW_4, ROW_4, ROW_4, ROW_4, ROW_4, ROW_4, ROW_4, ROW_4,
	   ROW_5, ROW_5, ROW_5, ROW_5, ROW_5, ROW_5, ROW_5, ROW_5,
	   ROW_6, ROW_6, ROW_6, ROW_6, ROW_6, ROW_6, ROW_6, ROW_6,
	   ROW_7, ROW_7, ROW_7, ROW_7, ROW_7, ROW_7, ROW_7, ROW_7,
	   ROW_8, ROW_8, ROW_8, ROW_8, ROW_8, ROW_8, ROW_8, ROW_8};
	
	public static final long ROW_MASK_23456 = ROW_2 | ROW_3 | ROW_4 | ROW_5 | ROW_6;
	
	public static final long ROW_MASK_34567 = ROW_3 | ROW_4 | ROW_5 | ROW_6 | ROW_7;
	
	public static final long[] RANK_PROMOTION = new long[] {ROW_7, ROW_2};
	
	public static final long[] RANK_NON_PROMOTION = new long[] {~ROW_7, ~ROW_2};
	
	public static final long
	   FILE_A=0x0101010101010101L<<0,
	   FILE_B=0x0101010101010101L<<1,
	   FILE_C=0x0101010101010101L<<2,
	   FILE_D=0x0101010101010101L<<3,
	   FILE_E=0x0101010101010101L<<4,
	   FILE_F=0x0101010101010101L<<5,
	   FILE_G=0x0101010101010101L<<6,
	   FILE_H=0x0101010101010101L<<7;
	
	public static final long NOT_FILE_A = ~FILE_A;
	public static final long NOT_FILE_H = ~FILE_H;
	
	public static final long[] FILE = {
			FILE_A,
			FILE_B,
			FILE_C,
			FILE_D,
			FILE_E,
			FILE_F,
			FILE_G,
			FILE_H
	};
	
	public static final long[] neighborFiles = {
			FILE_B, FILE_A | FILE_C, FILE_B | FILE_D, FILE_C | FILE_E, FILE_D | FILE_F, FILE_E | FILE_G, FILE_F | FILE_H, FILE_G,
			FILE_B, FILE_A | FILE_C, FILE_B | FILE_D, FILE_C | FILE_E, FILE_D | FILE_F, FILE_E | FILE_G, FILE_F | FILE_H, FILE_G,
			FILE_B, FILE_A | FILE_C, FILE_B | FILE_D, FILE_C | FILE_E, FILE_D | FILE_F, FILE_E | FILE_G, FILE_F | FILE_H, FILE_G,
			FILE_B, FILE_A | FILE_C, FILE_B | FILE_D, FILE_C | FILE_E, FILE_D | FILE_F, FILE_E | FILE_G, FILE_F | FILE_H, FILE_G,
			FILE_B, FILE_A | FILE_C, FILE_B | FILE_D, FILE_C | FILE_E, FILE_D | FILE_F, FILE_E | FILE_G, FILE_F | FILE_H, FILE_G,
			FILE_B, FILE_A | FILE_C, FILE_B | FILE_D, FILE_C | FILE_E, FILE_D | FILE_F, FILE_E | FILE_G, FILE_F | FILE_H, FILE_G,
			FILE_B, FILE_A | FILE_C, FILE_B | FILE_D, FILE_C | FILE_E, FILE_D | FILE_F, FILE_E | FILE_G, FILE_F | FILE_H, FILE_G,
			FILE_B, FILE_A | FILE_C, FILE_B | FILE_D, FILE_C | FILE_E, FILE_D | FILE_F, FILE_E | FILE_G, FILE_F | FILE_H, FILE_G,
	};
	
	public static final long
		DIAG_1  = 72057594037927936L,
		DIAG_2  = 144396663052566528L,
		DIAG_3  = 288794425616760832L,
		DIAG_4  = 577588855528488960L,
		DIAG_5  = 1155177711073755136L,
		DIAG_6  = 2310355422147575808L,
		DIAG_7  = 4620710844295151872L,
		DIAG_8  = -9205322385119247871L,
		DIAG_9  = 36099303471055874L,
		DIAG_10 = 141012904183812L,
		DIAG_11 = 550831656968L,
		DIAG_12 = 2151686160L,
		DIAG_13 = 8405024L,
		DIAG_14 = 32832L,
		DIAG_15 = 128L;
	
	public static final long[] DIAG_MASK = {
		 DIAG_8,  DIAG_9,  DIAG_10, DIAG_11, DIAG_12, DIAG_13, DIAG_14, DIAG_15,
		 DIAG_7,  DIAG_8,  DIAG_9,  DIAG_10, DIAG_11, DIAG_12, DIAG_13, DIAG_14,
		 DIAG_6,  DIAG_7,  DIAG_8,  DIAG_9,  DIAG_10, DIAG_11, DIAG_12, DIAG_13,
		 DIAG_5,  DIAG_6,  DIAG_7,  DIAG_8,  DIAG_9,  DIAG_10, DIAG_11, DIAG_12,
		 DIAG_4,  DIAG_5,  DIAG_6,  DIAG_7,  DIAG_8,  DIAG_9,  DIAG_10, DIAG_11,
		 DIAG_3,  DIAG_4,  DIAG_5,  DIAG_6,  DIAG_7,  DIAG_8,  DIAG_9,  DIAG_10,
		 DIAG_2,  DIAG_3,  DIAG_4,  DIAG_5,  DIAG_6,  DIAG_7,  DIAG_8,  DIAG_9,
		 DIAG_1,  DIAG_2,  DIAG_3,  DIAG_4,  DIAG_5,  DIAG_6,  DIAG_7,  DIAG_8};
	
	public static final long
		ADIAG_1  = 1L,
		ADIAG_2  = 258L,
		ADIAG_3  = 66052L,
		ADIAG_4  = 16909320L,
		ADIAG_5  = 4328785936L,
		ADIAG_6  = 1108169199648L,
		ADIAG_7  = 283691315109952L,
		ADIAG_8  = 72624976668147840L,
		ADIAG_9  = 145249953336295424L,
		ADIAG_10 = 290499906672525312L,
		ADIAG_11 = 580999813328273408L,
		ADIAG_12 = 1161999622361579520L,
		ADIAG_13 = 2323998145211531264L,
		ADIAG_14 = 4647714815446351872L,
		ADIAG_15 = -9223372036854775808L;
	
	public static final long[] ADIAG_MASK = {
		ADIAG_1,  ADIAG_2,  ADIAG_3,  ADIAG_4,  ADIAG_5,  ADIAG_6,  ADIAG_7,  ADIAG_8,
		ADIAG_2,  ADIAG_3,  ADIAG_4,  ADIAG_5,  ADIAG_6,  ADIAG_7,  ADIAG_8,  ADIAG_9,
		ADIAG_3,  ADIAG_4,  ADIAG_5,  ADIAG_6,  ADIAG_7,  ADIAG_8,  ADIAG_9,  ADIAG_10,
		ADIAG_4,  ADIAG_5,  ADIAG_6,  ADIAG_7,  ADIAG_8,  ADIAG_9,  ADIAG_10, ADIAG_11,
		ADIAG_5,  ADIAG_6,  ADIAG_7,  ADIAG_8,  ADIAG_9,  ADIAG_10, ADIAG_11, ADIAG_12,
		ADIAG_6,  ADIAG_7,  ADIAG_8,  ADIAG_9,  ADIAG_10, ADIAG_11, ADIAG_12, ADIAG_13,
		ADIAG_7,  ADIAG_8,  ADIAG_9,  ADIAG_10, ADIAG_11, ADIAG_12, ADIAG_13, ADIAG_14,
		ADIAG_8,  ADIAG_9,  ADIAG_10, ADIAG_11, ADIAG_12, ADIAG_13, ADIAG_14, ADIAG_15};
	
	public static final long DIAG_C2H7 = 36099303471055872L; 
	
	enum EngineMode {
		FIXED_DEPTH, NON_FIXED_DEPTH
	}
	
	   // This PreCalculated KNIGHT_LOOKUP array was verified with the +3000 ELO Engine. (At 20190526 14:51)
	   public static final long[] KNIGHT_LOOKUP={
			      0x0000000000020400L, 0x0000000000050800L, 0x00000000000a1100L, 0x0000000000142200L,
			      0x0000000000284400L, 0x0000000000508800L, 0x0000000000a01000L, 0x0000000000402000L,
			      0x0000000002040004L, 0x0000000005080008L, 0x000000000a110011L, 0x0000000014220022L,
			      0x0000000028440044L, 0x0000000050880088L, 0x00000000a0100010L, 0x0000000040200020L,
			      0x0000000204000402L, 0x0000000508000805L, 0x0000000a1100110aL, 0x0000001422002214L,
			      0x0000002844004428L, 0x0000005088008850L, 0x000000a0100010a0L, 0x0000004020002040L,
			      0x0000020400040200L, 0x0000050800080500L, 0x00000a1100110a00L, 0x0000142200221400L,
			      0x0000284400442800L, 0x0000508800885000L, 0x0000a0100010a000L, 0x0000402000204000L,
			      0x0002040004020000L, 0x0005080008050000L, 0x000a1100110a0000L, 0x0014220022140000L,
			      0x0028440044280000L, 0x0050880088500000L, 0x00a0100010a00000L, 0x0040200020400000L,
			      0x0204000402000000L, 0x0508000805000000L, 0x0a1100110a000000L, 0x1422002214000000L,
			      0x2844004428000000L, 0x5088008850000000L, 0xa0100010a0000000L, 0x4020002040000000L,
			      0x0400040200000000L, 0x0800080500000000L, 0x1100110a00000000L, 0x2200221400000000L,
			      0x4400442800000000L, 0x8800885000000000L, 0x100010a000000000L, 0x2000204000000000L,
			      0x0004020000000000L, 0x0008050000000000L, 0x00110a0000000000L, 0x0022140000000000L,
			      0x0044280000000000L, 0x0088500000000000L, 0x0010a00000000000L, 0x0020400000000000L
	   };

	   // This PreCalculated KING_LOOKUP array was verified with the +3000 ELO Engine. (At 20190526 14:51)
	   public static final long[] KING_LOOKUP={
			      0x0000000000000302L, 0x0000000000000705L, 0x0000000000000e0aL, 0x0000000000001c14L,
			      0x0000000000003828L, 0x0000000000007050L, 0x000000000000e0a0L, 0x000000000000c040L,
			      0x0000000000030203L, 0x0000000000070507L, 0x00000000000e0a0eL, 0x00000000001c141cL,
			      0x0000000000382838L, 0x0000000000705070L, 0x0000000000e0a0e0L, 0x0000000000c040c0L,
			      0x0000000003020300L, 0x0000000007050700L, 0x000000000e0a0e00L, 0x000000001c141c00L,
			      0x0000000038283800L, 0x0000000070507000L, 0x00000000e0a0e000L, 0x00000000c040c000L,
			      0x0000000302030000L, 0x0000000705070000L, 0x0000000e0a0e0000L, 0x0000001c141c0000L,
			      0x0000003828380000L, 0x0000007050700000L, 0x000000e0a0e00000L, 0x000000c040c00000L,
			      0x0000030203000000L, 0x0000070507000000L, 0x00000e0a0e000000L, 0x00001c141c000000L,
			      0x0000382838000000L, 0x0000705070000000L, 0x0000e0a0e0000000L, 0x0000c040c0000000L,
			      0x0003020300000000L, 0x0007050700000000L, 0x000e0a0e00000000L, 0x001c141c00000000L,
			      0x0038283800000000L, 0x0070507000000000L, 0x00e0a0e000000000L, 0x00c040c000000000L,
			      0x0302030000000000L, 0x0705070000000000L, 0x0e0a0e0000000000L, 0x1c141c0000000000L,
			      0x3828380000000000L, 0x7050700000000000L, 0xe0a0e00000000000L, 0xc040c00000000000L,
			      0x0203000000000000L, 0x0507000000000000L, 0x0a0e000000000000L, 0x141c000000000000L,
			      0x2838000000000000L, 0x5070000000000000L, 0xa0e0000000000000L, 0x40c0000000000000L
	   };
	   
	   // This PreCalculated PAWN_ATTACK_LOOKUP array was verified with the +3000 ELO Engine. (At 20190526 15:40)
	   public static final long[][] PAWN_ATTACK_LOOKUP = {
			   {
			   0x0000000000000200L, 0x0000000000000500L, 0x0000000000000a00L, 0x0000000000001400L,
			   0x0000000000002800L, 0x0000000000005000L, 0x000000000000a000L, 0x0000000000004000L,
			   0x0000000000020000L, 0x0000000000050000L, 0x00000000000a0000L, 0x0000000000140000L,
			   0x0000000000280000L, 0x0000000000500000L, 0x0000000000a00000L, 0x0000000000400000L,
			   0x0000000002000000L, 0x0000000005000000L, 0x000000000a000000L, 0x0000000014000000L,
			   0x0000000028000000L, 0x0000000050000000L, 0x00000000a0000000L, 0x0000000040000000L,
			   0x0000000200000000L, 0x0000000500000000L, 0x0000000a00000000L, 0x0000001400000000L,
			   0x0000002800000000L, 0x0000005000000000L, 0x000000a000000000L, 0x0000004000000000L,
			   0x0000020000000000L, 0x0000050000000000L, 0x00000a0000000000L, 0x0000140000000000L,
			   0x0000280000000000L, 0x0000500000000000L, 0x0000a00000000000L, 0x0000400000000000L,
			   0x0002000000000000L, 0x0005000000000000L, 0x000a000000000000L, 0x0014000000000000L,
			   0x0028000000000000L, 0x0050000000000000L, 0x00a0000000000000L, 0x0040000000000000L,
			   0x0200000000000000L, 0x0500000000000000L, 0x0a00000000000000L, 0x1400000000000000L,
			   0x2800000000000000L, 0x5000000000000000L, 0xa000000000000000L, 0x4000000000000000L,
			   0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
			   0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L
			   }, 
			   {
			   0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
			   0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
			   0x0000000000000002L, 0x0000000000000005L, 0x000000000000000aL, 0x0000000000000014L,
			   0x0000000000000028L, 0x0000000000000050L, 0x00000000000000a0L, 0x0000000000000040L,
			   0x0000000000000200L, 0x0000000000000500L, 0x0000000000000a00L, 0x0000000000001400L,
			   0x0000000000002800L, 0x0000000000005000L, 0x000000000000a000L, 0x0000000000004000L,
			   0x0000000000020000L, 0x0000000000050000L, 0x00000000000a0000L, 0x0000000000140000L,
			   0x0000000000280000L, 0x0000000000500000L, 0x0000000000a00000L, 0x0000000000400000L,
			   0x0000000002000000L, 0x0000000005000000L, 0x000000000a000000L, 0x0000000014000000L,
			   0x0000000028000000L, 0x0000000050000000L, 0x00000000a0000000L, 0x0000000040000000L,
			   0x0000000200000000L, 0x0000000500000000L, 0x0000000a00000000L, 0x0000001400000000L,
			   0x0000002800000000L, 0x0000005000000000L, 0x000000a000000000L, 0x0000004000000000L,
			   0x0000020000000000L, 0x0000050000000000L, 0x00000a0000000000L, 0x0000140000000000L,
			   0x0000280000000000L, 0x0000500000000000L, 0x0000a00000000000L, 0x0000400000000000L,
			   0x0002000000000000L, 0x0005000000000000L, 0x000a000000000000L, 0x0014000000000000L,
			   0x0028000000000000L, 0x0050000000000000L, 0x00a0000000000000L, 0x0040000000000000L
			   }
			   };

	   public static final int[][] EPT_LOOKUP = {
			   {56, 57, 58, 59, 60, 61, 62, 63, 
			   0, 1, 2, 3, 4, 5, 6, 7, 
			   8, 9, 10, 11, 12, 13, 14, 15, 
			   16, 17, 18, 19, 20, 21, 22, 23, 
			   24, 25, 26, 27, 28, 29, 30, 31, 
			   32, 33, 34, 35, 36, 37, 38, 39, 
			   40, 41, 42, 43, 44, 45, 46, 47, 
			   48, 49, 50, 51, 52, 53, 54, 55
			   },
			   {8, 9, 10, 11, 12, 13, 14, 15, 
			   16, 17, 18, 19, 20, 21, 22, 23, 
			   24, 25, 26, 27, 28, 29, 30, 31, 
			   32, 33, 34, 35, 36, 37, 38, 39, 
			   40, 41, 42, 43, 44, 45, 46, 47, 
			   48, 49, 50, 51, 52, 53, 54, 55, 
			   56, 57, 58, 59, 60, 61, 62, 63, 
			   0, 1, 2, 3, 4, 5, 6, 7
			   }};
	   
	   public static final long[][] FRONT_SPAN_LOOKUP = {
		   {
		   0x0303030303030300L, 0x0707070707070700L, 0x0e0e0e0e0e0e0e00L, 0x1c1c1c1c1c1c1c00L,
		   0x3838383838383800L, 0x7070707070707000L, 0xe0e0e0e0e0e0e000L, 0xc0c0c0c0c0c0c000L,
		   0x0303030303030000L, 0x0707070707070000L, 0x0e0e0e0e0e0e0000L, 0x1c1c1c1c1c1c0000L,
		   0x3838383838380000L, 0x7070707070700000L, 0xe0e0e0e0e0e00000L, 0xc0c0c0c0c0c00000L,
		   0x0303030303000000L, 0x0707070707000000L, 0x0e0e0e0e0e000000L, 0x1c1c1c1c1c000000L,
		   0x3838383838000000L, 0x7070707070000000L, 0xe0e0e0e0e0000000L, 0xc0c0c0c0c0000000L,
		   0x0303030300000000L, 0x0707070700000000L, 0x0e0e0e0e00000000L, 0x1c1c1c1c00000000L,
		   0x3838383800000000L, 0x7070707000000000L, 0xe0e0e0e000000000L, 0xc0c0c0c000000000L,
		   0x0303030000000000L, 0x0707070000000000L, 0x0e0e0e0000000000L, 0x1c1c1c0000000000L,
		   0x3838380000000000L, 0x7070700000000000L, 0xe0e0e00000000000L, 0xc0c0c00000000000L,
		   0x0303000000000000L, 0x0707000000000000L, 0x0e0e000000000000L, 0x1c1c000000000000L,
		   0x3838000000000000L, 0x7070000000000000L, 0xe0e0000000000000L, 0xc0c0000000000000L,
		   0x0300000000000000L, 0x0700000000000000L, 0x0e00000000000000L, 0x1c00000000000000L,
		   0x3800000000000000L, 0x7000000000000000L, 0xe000000000000000L, 0xc000000000000000L,
		   0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
		   0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L
		   },
		   {
		   0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
		   0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
		   0x0000000000000003L, 0x0000000000000007L, 0x000000000000000eL, 0x000000000000001cL,
		   0x0000000000000038L, 0x0000000000000070L, 0x00000000000000e0L, 0x00000000000000c0L,
		   0x0000000000000303L, 0x0000000000000707L, 0x0000000000000e0eL, 0x0000000000001c1cL,
		   0x0000000000003838L, 0x0000000000007070L, 0x000000000000e0e0L, 0x000000000000c0c0L,
		   0x0000000000030303L, 0x0000000000070707L, 0x00000000000e0e0eL, 0x00000000001c1c1cL,
		   0x0000000000383838L, 0x0000000000707070L, 0x0000000000e0e0e0L, 0x0000000000c0c0c0L,
		   0x0000000003030303L, 0x0000000007070707L, 0x000000000e0e0e0eL, 0x000000001c1c1c1cL,
		   0x0000000038383838L, 0x0000000070707070L, 0x00000000e0e0e0e0L, 0x00000000c0c0c0c0L,
		   0x0000000303030303L, 0x0000000707070707L, 0x0000000e0e0e0e0eL, 0x0000001c1c1c1c1cL,
		   0x0000003838383838L, 0x0000007070707070L, 0x000000e0e0e0e0e0L, 0x000000c0c0c0c0c0L,
		   0x0000030303030303L, 0x0000070707070707L, 0x00000e0e0e0e0e0eL, 0x00001c1c1c1c1c1cL,
		   0x0000383838383838L, 0x0000707070707070L, 0x0000e0e0e0e0e0e0L, 0x0000c0c0c0c0c0c0L,
		   0x0003030303030303L, 0x0007070707070707L, 0x000e0e0e0e0e0e0eL, 0x001c1c1c1c1c1c1cL,
		   0x0038383838383838L, 0x0070707070707070L, 0x00e0e0e0e0e0e0e0L, 0x00c0c0c0c0c0c0c0L
		   }
		   };
	   
	   public static final byte
	   DEGREE_45 =0,
	   DEGREE_135=1,
	   DEGREE_225=2,
	   DEGREE_315=3;
	   
	   public static final byte 
	   RIGHT=0,
	   DOWN =1,
	   LEFT =2,
	   UP   =3;
	   
	   public static final long[][] BISHOP_LOOKUP={
			      // ROW 1
			      // square 0
			      {
			         0x8040201008040200L,
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0000000000000000L
			      },
			      // square 1
			      {
			         0x0080402010080400L,
			         0x0000000000000100L,
			         0x0000000000000000L,
			         0x0000000000000000L
			      },
			      // square 2
			      {
			         0x0000804020100800L,
			         0x0000000000010200L,
			         0x0000000000000000L,
			         0x0000000000000000L
			      },
			      // square 3
			      {
			         0x0000008040201000L,
			         0x0000000001020400L,
			         0x0000000000000000L,
			         0x0000000000000000L
			      },
			      // square 4
			      {
			         0x0000000080402000L,
			         0x0000000102040800L,
			         0x0000000000000000L,
			         0x0000000000000000L
			      },
			      // square 5
			      {
			         0x0000000000804000L,
			         0x0000010204081000L,
			         0x0000000000000000L,
			         0x0000000000000000L
			      },
			      // square 6
			      {
			         0x0000000000008000L,
			         0x0001020408102000L,
			         0x0000000000000000L,
			         0x0000000000000000L
			      },
			      // square 7
			      {
			         0x0000000000000000L,
			         0x0102040810204000L,
			         0x0000000000000000L,
			         0x0000000000000000L
			      },

			      // ROW 2
			      // square 8
			      {
			         0x4020100804020000L,
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0000000000000002L
			      },
			      // square 9
			      {
			         0x8040201008040000L,
			         0x0000000000010000L,
			         0x0000000000000001L,
			         0x0000000000000004L
			      },
			      // square 10
			      {
			         0x0080402010080000L,
			         0x0000000001020000L,
			         0x0000000000000002L,
			         0x0000000000000008L
			      },
			      // square 11
			      {
			         0x0000804020100000L,
			         0x0000000102040000L,
			         0x0000000000000004L,
			         0x0000000000000010L
			      },
			      // square 12
			      {
			         0x0000008040200000L,
			         0x0000010204080000L,
			         0x0000000000000008L,
			         0x0000000000000020L
			      },
			      // square 13
			      {
			         0x0000000080400000L,
			         0x0001020408100000L,
			         0x0000000000000010L,
			         0x0000000000000040L
			      },
			      // square 14
			      {
			         0x0000000000800000L,
			         0x0102040810200000L,
			         0x0000000000000020L,
			         0x0000000000000080L
			      },
			      // square 15
			      {
			         0x0000000000000000L,
			         0x0204081020400000L,
			         0x0000000000000040L,
			         0x0000000000000000L
			      },

			      // ROW 3
			      // square 16
			      {
			         0x2010080402000000L,
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0000000000000204L
			      },
			      // square 17
			      {
			         0x4020100804000000L,
			         0x0000000001000000L,
			         0x0000000000000100L,
			         0x0000000000000408L
			      },
			      // square 18
			      {
			         0x8040201008000000L,
			         0x0000000102000000L,
			         0x0000000000000201L,
			         0x0000000000000810L
			      },
			      // square 19
			      {
			         0x0080402010000000L,
			         0x0000010204000000L,
			         0x0000000000000402L,
			         0x0000000000001020L
			      },
			      // square 20
			      {
			         0x0000804020000000L,
			         0x0001020408000000L,
			         0x0000000000000804L,
			         0x0000000000002040L
			      },
			      // square 21
			      {
			         0x0000008040000000L,
			         0x0102040810000000L,
			         0x0000000000001008L,
			         0x0000000000004080L
			      },
			      // square 22
			      {
			         0x0000000080000000L,
			         0x0204081020000000L,
			         0x0000000000002010L,
			         0x0000000000008000L
			      },
			      // square 23
			      {
			         0x0000000000000000L,
			         0x0408102040000000L,
			         0x0000000000004020L,
			         0x0000000000000000L
			      },

			      // ROW 4
			      // square 24
			      {
			         0x1008040200000000L,
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0000000000020408L
			      },
			      // square 25
			      {
			         0x2010080400000000L,
			         0x0000000100000000L,
			         0x0000000000010000L,
			         0x0000000000040810L
			      },
			      // square 26
			      {
			         0x4020100800000000L,
			         0x0000010200000000L,
			         0x0000000000020100L,
			         0x0000000000081020L
			      },
			      // square 27
			      {
			         0x8040201000000000L,
			         0x0001020400000000L,
			         0x0000000000040201L,
			         0x0000000000102040L
			      },
			      // square 28
			      {
			         0x0080402000000000L,
			         0x0102040800000000L,
			         0x0000000000080402L,
			         0x0000000000204080L
			      },
			      // square 29
			      {
			         0x0000804000000000L,
			         0x0204081000000000L,
			         0x0000000000100804L,
			         0x0000000000408000L
			      },
			      // square 30
			      {
			         0x0000008000000000L,
			         0x0408102000000000L,
			         0x0000000000201008L,
			         0x0000000000800000L
			      },
			      // square 31
			      {
			         0x0000000000000000L,
			         0x0810204000000000L,
			         0x0000000000402010L,
			         0x0000000000000000L
			      },

			      // ROW 5
			      // square 32
			      {
			         0x0804020000000000L,
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0000000002040810L
			      },
			      // square 33
			      {
			         0x1008040000000000L,
			         0x0000010000000000L,
			         0x0000000001000000L,
			         0x0000000004081020L
			      },
			      // square 34
			      {
			         0x2010080000000000L,
			         0x0001020000000000L,
			         0x0000000002010000L,
			         0x0000000008102040L
			      },
			      // square 35
			      {
			         0x4020100000000000L,
			         0x0102040000000000L,
			         0x0000000004020100L,
			         0x0000000010204080L
			      },
			      // square 36
			      {
			         0x8040200000000000L,
			         0x0204080000000000L,
			         0x0000000008040201L,
			         0x0000000020408000L
			      },
			      // square 37
			      {
			         0x0080400000000000L,
			         0x0408100000000000L,
			         0x0000000010080402L,
			         0x0000000040800000L
			      },
			      // square 38
			      {
			         0x0000800000000000L,
			         0x0810200000000000L,
			         0x0000000020100804L,
			         0x0000000080000000L
			      },
			      // square 39
			      {
			         0x0000000000000000L,
			         0x1020400000000000L,
			         0x0000000040201008L,
			         0x0000000000000000L
			      },

			      // ROW 6
			      // square 40
			      {
			         0x0402000000000000L,
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0000000204081020L
			      },
			      // square 41
			      {
			         0x0804000000000000L,
			         0x0001000000000000L,
			         0x0000000100000000L,
			         0x0000000408102040L
			      },
			      // square 42
			      {
			         0x1008000000000000L,
			         0x0102000000000000L,
			         0x0000000201000000L,
			         0x0000000810204080L
			      },
			      // square 43
			      {
			         0x2010000000000000L,
			         0x0204000000000000L,
			         0x0000000402010000L,
			         0x0000001020408000L
			      },
			      // square 44
			      {
			         0x4020000000000000L,
			         0x0408000000000000L,
			         0x0000000804020100L,
			         0x0000002040800000L
			      },
			      // square 45
			      {
			         0x8040000000000000L,
			         0x0810000000000000L,
			         0x0000001008040201L,
			         0x0000004080000000L
			      },
			      // square 46
			      {
			         0x0080000000000000L,
			         0x1020000000000000L,
			         0x0000002010080402L,
			         0x0000008000000000L
			      },
			      // square 47
			      {
			         0x0000000000000000L,
			         0x2040000000000000L,
			         0x0000004020100804L,
			         0x0000000000000000L
			      },

			      // ROW 7
			      // square 48
			      {
			         0x0200000000000000L,
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0000020408102040L
			      },
			      // square 49
			      {
			         0x0400000000000000L,
			         0x0100000000000000L,
			         0x0000010000000000L,
			         0x0000040810204080L
			      },
			      // square 50
			      {
			         0x0800000000000000L,
			         0x0200000000000000L,
			         0x0000020100000000L,
			         0x0000081020408000L
			      },
			      // square 51
			      {
			         0x1000000000000000L,
			         0x0400000000000000L,
			         0x0000040201000000L,
			         0x0000102040800000L
			      },
			      // square 52
			      {
			         0x2000000000000000L,
			         0x0800000000000000L,
			         0x0000080402010000L,
			         0x0000204080000000L
			      },
			      // square 53
			      {
			         0x4000000000000000L,
			         0x1000000000000000L,
			         0x0000100804020100L,
			         0x0000408000000000L
			      },
			      // square 54
			      {
			         0x8000000000000000L,
			         0x2000000000000000L,
			         0x0000201008040201L,
			         0x0000800000000000L
			      },
			      // square 55
			      {
			         0x0000000000000000L,
			         0x4000000000000000L,
			         0x0000402010080402L,
			         0x0000000000000000L
			      },

			      // ROW 8
			      // square 56
			      {
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0002040810204080L
			      },
			      // square 57
			      {
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0001000000000000L,
			         0x0004081020408000L
			      },
			      // square 58
			      {
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0002010000000000L,
			         0x0008102040800000L
			      },
			      // square 59
			      {
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0004020100000000L,
			         0x0010204080000000L
			      },
			      // square 60
			      {
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0008040201000000L,
			         0x0020408000000000L
			      },
			      // square 61
			      {
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0010080402010000L,
			         0x0040800000000000L
			      },
			      // square 62
			      {
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0020100804020100L,
			         0x0080000000000000L
			      },
			      // square 63
			      {
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0040201008040201L,
			         0x0000000000000000L
			      }
			   };
	   

	   
	   public static final long[][] ROOK_LOOKUP={
			      // ROW 1
			      // square 0
			      {
			         0x00000000000000feL,
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x0101010101010100L
			      },
			      // square 1
			      {
			         0x00000000000000fcL,
			         0x0000000000000000L,
			         0x0000000000000001L,
			         0x0202020202020200L
			      },
			      // square 2
			      {
			         0x00000000000000f8L,
			         0x0000000000000000L,
			         0x0000000000000003L,
			         0x0404040404040400L
			      },
			      // square 3
			      {
			         0x00000000000000f0L,
			         0x0000000000000000L,
			         0x0000000000000007L,
			         0x0808080808080800L
			      },
			      // square 4
			      {
			         0x00000000000000e0L,
			         0x0000000000000000L,
			         0x000000000000000fL,
			         0x1010101010101000L
			      },
			      // square 5
			      {
			         0x00000000000000c0L,
			         0x0000000000000000L,
			         0x000000000000001fL,
			         0x2020202020202000L
			      },
			      // square 6
			      {
			         0x0000000000000080L,
			         0x0000000000000000L,
			         0x000000000000003fL,
			         0x4040404040404000L
			      },
			      // square 7
			      {
			         0x0000000000000000L,
			         0x0000000000000000L,
			         0x000000000000007fL,
			         0x8080808080808000L
			      },
			      
			      // ROW 2
			      // square 8
			      {
			         0x000000000000fe00L,
			         0x0000000000000001L,
			         0x0000000000000000L,
			         0x0101010101010000L
			      },
			      // square 9
			      {
			         0x000000000000fc00L,
			         0x0000000000000002L,
			         0x0000000000000100L,
			         0x0202020202020000L
			      },
			      // square 10
			      {
			         0x000000000000f800L,
			         0x0000000000000004L,
			         0x0000000000000300L,
			         0x0404040404040000L
			      },
			      // square 11
			      {
			         0x000000000000f000L,
			         0x0000000000000008L,
			         0x0000000000000700L,
			         0x0808080808080000L
			      },
			      // square 12
			      {
			         0x000000000000e000L,
			         0x0000000000000010L,
			         0x0000000000000f00L,
			         0x1010101010100000L
			      },
			      // square 13
			      {
			         0x000000000000c000L,
			         0x0000000000000020L,
			         0x0000000000001f00L,
			         0x2020202020200000L
			      },
			      // square 14
			      {
			         0x0000000000008000L,
			         0x0000000000000040L,
			         0x0000000000003f00L,
			         0x4040404040400000L
			      },
			      // square 15
			      {
			         0x0000000000000000L,
			         0x0000000000000080L,
			         0x0000000000007f00L,
			         0x8080808080800000L
			      },

			      // ROW 3
			      // square 16
			      {
			         0x0000000000fe0000L,
			         0x0000000000000101L,
			         0x0000000000000000L,
			         0x0101010101000000L
			      },
			      // square 17
			      {
			         0x0000000000fc0000L,
			         0x0000000000000202L,
			         0x0000000000010000L,
			         0x0202020202000000L
			      },
			      // square 18
			      {
			         0x0000000000f80000L,
			         0x0000000000000404L,
			         0x0000000000030000L,
			         0x0404040404000000L
			      },
			      // square 19
			      {
			         0x0000000000f00000L,
			         0x0000000000000808L,
			         0x0000000000070000L,
			         0x0808080808000000L
			      },
			      // square 20
			      {
			         0x0000000000e00000L,
			         0x0000000000001010L,
			         0x00000000000f0000L,
			         0x1010101010000000L
			      },
			      // square 21
			      {
			         0x0000000000c00000L,
			         0x0000000000002020L,
			         0x00000000001f0000L,
			         0x2020202020000000L
			      },
			      // square 22
			      {
			         0x0000000000800000L,
			         0x0000000000004040L,
			         0x00000000003f0000L,
			         0x4040404040000000L
			      },
			      // square 23
			      {
			         0x0000000000000000L,
			         0x0000000000008080L,
			         0x00000000007f0000L,
			         0x8080808080000000L
			      },

			      // ROW 4
			      // square 24
			      {
			         0x00000000fe000000L,
			         0x0000000000010101L,
			         0x0000000000000000L,
			         0x0101010100000000L
			      },
			      // square 25
			      {
			         0x00000000fc000000L,
			         0x0000000000020202L,
			         0x0000000001000000L,
			         0x0202020200000000L
			      },
			      // square 26
			      {
			         0x00000000f8000000L,
			         0x0000000000040404L,
			         0x0000000003000000L,
			         0x0404040400000000L
			      },
			      // square 27
			      {
			         0x00000000f0000000L,
			         0x0000000000080808L,
			         0x0000000007000000L,
			         0x0808080800000000L
			      },
			      // square 28
			      {
			         0x00000000e0000000L,
			         0x0000000000101010L,
			         0x000000000f000000L,
			         0x1010101000000000L
			      },
			      // square 29
			      {
			         0x00000000c0000000L,
			         0x0000000000202020L,
			         0x000000001f000000L,
			         0x2020202000000000L
			      },
			      // square 30
			      {
			         0x0000000080000000L,
			         0x0000000000404040L,
			         0x000000003f000000L,
			         0x4040404000000000L
			      },
			      // square 31
			      {
			         0x0000000000000000L,
			         0x0000000000808080L,
			         0x000000007f000000L,
			         0x8080808000000000L
			      },

			      // ROW 5
			      // square 32
			      {
			         0x000000fe00000000L,
			         0x0000000001010101L,
			         0x0000000000000000L,
			         0x0101010000000000L
			      },
			      // square 33
			      {
			         0x000000fc00000000L,
			         0x0000000002020202L,
			         0x0000000100000000L,
			         0x0202020000000000L
			      },
			      // square 34
			      {
			         0x000000f800000000L,
			         0x0000000004040404L,
			         0x0000000300000000L,
			         0x0404040000000000L
			      },
			      // square 35
			      {
			         0x000000f000000000L,
			         0x0000000008080808L,
			         0x0000000700000000L,
			         0x0808080000000000L
			      },
			      // square 36
			      {
			         0x000000e000000000L,
			         0x0000000010101010L,
			         0x0000000f00000000L,
			         0x1010100000000000L
			      },
			      // square 37
			      {
			         0x000000c000000000L,
			         0x0000000020202020L,
			         0x0000001f00000000L,
			         0x2020200000000000L
			      },
			      // square 38
			      {
			         0x0000008000000000L,
			         0x0000000040404040L,
			         0x0000003f00000000L,
			         0x4040400000000000L
			      },
			      // square 39
			      {
			         0x0000000000000000L,
			         0x0000000080808080L,
			         0x0000007f00000000L,
			         0x8080800000000000L
			      },

			      // ROW 6
			      // square 40
			      {
			         0x0000fe0000000000L,
			         0x0000000101010101L,
			         0x0000000000000000L,
			         0x0101000000000000L
			      },
			      // square 41
			      {
			         0x0000fc0000000000L,
			         0x0000000202020202L,
			         0x0000010000000000L,
			         0x0202000000000000L
			      },
			      // square 42
			      {
			         0x0000f80000000000L,
			         0x0000000404040404L,
			         0x0000030000000000L,
			         0x0404000000000000L
			      },
			      // square 43
			      {
			         0x0000f00000000000L,
			         0x0000000808080808L,
			         0x0000070000000000L,
			         0x0808000000000000L
			      },
			      // square 44
			      {
			         0x0000e00000000000L,
			         0x0000001010101010L,
			         0x00000f0000000000L,
			         0x1010000000000000L
			      },
			      // square 45
			      {
			         0x0000c00000000000L,
			         0x0000002020202020L,
			         0x00001f0000000000L,
			         0x2020000000000000L
			      },
			      // square 46
			      {
			         0x0000800000000000L,
			         0x0000004040404040L,
			         0x00003f0000000000L,
			         0x4040000000000000L
			      },
			      // square 47
			      {
			         0x0000000000000000L,
			         0x0000008080808080L,
			         0x00007f0000000000L,
			         0x8080000000000000L
			      },

			      // ROW 7
			      // square 48
			      {
			         0x00fe000000000000L,
			         0x0000010101010101L,
			         0x0000000000000000L,
			         0x0100000000000000L
			      },
			      // square 49
			      {
			         0x00fc000000000000L,
			         0x0000020202020202L,
			         0x0001000000000000L,
			         0x0200000000000000L
			      },
			      // square 50
			      {
			         0x00f8000000000000L,
			         0x0000040404040404L,
			         0x0003000000000000L,
			         0x0400000000000000L
			      },
			      // square 51
			      {
			         0x00f0000000000000L,
			         0x0000080808080808L,
			         0x0007000000000000L,
			         0x0800000000000000L
			      },
			      // square 52
			      {
			         0x00e0000000000000L,
			         0x0000101010101010L,
			         0x000f000000000000L,
			         0x1000000000000000L
			      },
			      // square 53
			      {
			         0x00c0000000000000L,
			         0x0000202020202020L,
			         0x001f000000000000L,
			         0x2000000000000000L
			      },
			      // square 54
			      {
			         0x0080000000000000L,
			         0x0000404040404040L,
			         0x003f000000000000L,
			         0x4000000000000000L
			      },
			      // square 55
			      {
			         0x0000000000000000L,
			         0x0000808080808080L,
			         0x007f000000000000L,
			         0x8000000000000000L
			      },

			      // ROW 8
			      // square 56
			      {
			         0xfe00000000000000L,
			         0x0001010101010101L,
			         0x0000000000000000L,
			         0x0000000000000000L
			      },
			      // square 57
			      {
			         0xfc00000000000000L,
			         0x0002020202020202L,
			         0x0100000000000000L,
			         0x0000000000000000L
			      },
			      // square 58
			      {
			         0xf800000000000000L,
			         0x0004040404040404L,
			         0x0300000000000000L,
			         0x0000000000000000L
			      },
			      // square 59
			      {
			         0xf000000000000000L,
			         0x0008080808080808L,
			         0x0700000000000000L,
			         0x0000000000000000L
			      },
			      // square 60
			      {
			         0xe000000000000000L,
			         0x0010101010101010L,
			         0x0f00000000000000L,
			         0x0000000000000000L
			      },
			      // square 61
			      {
			         0xc000000000000000L,
			         0x0020202020202020L,
			         0x1f00000000000000L,
			         0x0000000000000000L
			      },
			      // square 62
			      {
			         0x8000000000000000L,
			         0x0040404040404040L,
			         0x3f00000000000000L,
			         0x0000000000000000L
			      },
			      // square 63
			      {
			         0x0000000000000000L,
			         0x0080808080808080L,
			         0x7f00000000000000L,
			         0x0000000000000000L
			      }
			   };

		public static final int A1 = 0;
		public static final int B1 = 1;
		public static final int C1 = 2;
		public static final int D1 = 3;
		public static final int E1 = 4;
		public static final int F1 = 5;
		public static final int G1 = 6;
		public static final int H1 = 7;
		
		public static final int A2 = 8;
		public static final int B2 = 9;
		public static final int C2 = 10;
		public static final int D2 = 11;
		public static final int E2 = 12;
		public static final int F2 = 13;
		public static final int G2 = 14;
		public static final int H2 = 15;
		
		public static final int A3 = 16;
		public static final int B3 = 17;
		public static final int C3 = 18;
		public static final int D3 = 19;
		public static final int E3 = 20;
		public static final int F3 = 21;
		public static final int G3 = 22;
		public static final int H3 = 23;
		
		public static final int A4 = 24;
		public static final int B4 = 25;
		public static final int C4 = 26;
		public static final int D4 = 27;
		public static final int E4 = 28;
		public static final int F4 = 29;
		public static final int G4 = 30;
		public static final int H4 = 31;
		
		public static final int A5 = 32;
		public static final int B5 = 33;
		public static final int C5 = 34;
		public static final int D5 = 35;
		public static final int E5 = 36;
		public static final int F5 = 37;
		public static final int G5 = 38;
		public static final int H5 = 39;
		
		public static final int A6 = 40;
		public static final int B6 = 41;
		public static final int C6 = 42;
		public static final int D6 = 43;
		public static final int E6 = 44;
		public static final int F6 = 45;
		public static final int G6 = 46;
		public static final int H6 = 47;
		
		public static final int A7 = 48;
		public static final int B7 = 49;
		public static final int C7 = 50;
		public static final int D7 = 51;
		public static final int E7 = 52;
		public static final int F7 = 53;
		public static final int G7 = 54;
		public static final int H7 = 55;
		
		public static final int A8 = 56;
		public static final int B8 = 57;
		public static final int C8 = 58;
		public static final int D8 = 59;
		public static final int E8 = 60;
		public static final int F8 = 61;
		public static final int G8 = 62;
		public static final int H8 = 63;
		
		public static final long b1 = 1L << B1;
		public static final long c1 = 1L << C1;
		public static final long d1 = 1L << D1;
		public static final long f1 = 1L << F1;
		public static final long g1 = 1L << G1;
		
		public static final long b8 = 1L << B8;
		public static final long c8 = 1L << C8;
		public static final long d8 = 1L << D8;
		public static final long f8 = 1L << F8;
		public static final long g8 = 1L << G8;
		
		public static final long b1_c1_d1 = b1 | c1 | d1;
		public static final long f1_g1 = f1 | g1;
		public static final long b8_c8_d8 = b8 | c8 | d8;
		public static final long f8_g8 = f8 | g8;
		public static final long[][] CASTLING_EMPTY_SQUARES = new long[][] {{b1_c1_d1, f1_g1}, {b8_c8_d8, f8_g8}};
}

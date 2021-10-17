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

public class Move {
	
	public static void main(String[] args) {
		int move = 6503476;
		System.out.println("move = " + move);
		System.out.println("move & 0x0000_00FF = " + (move & 0x0000_00FF));
		System.out.println("(move & 0x0000_FF00) >>> 8 = " + ((move & 0x0000_FF00) >>> 8));
		System.out.println("(move & 0x000F_0000) >>> 16 = " + ((move & 0x000F_0000) >>> 16));
		System.out.println("(move & 0x0700_0000) >>> 24 = " + ((move & 0x0700_0000) >>> 24));
		System.out.println("(move & 0x7800_0000) >>> 27 = " + ((move & 0x7800_0000) >>> 27));
	}
	
	public static byte getPromotedPiece(int move) {
		return (byte)((move & 0x00f00000) >>> 20);		
	}
	
	public static byte getFromPiece(int move) {
		return (byte)((move & 0x0F000000) >>> 24);		
	}
	
	public static byte getCapturedPiece(int move) {
		return (byte)((move & 0x70000000) >>> 27);		
	}
	
	public static boolean isPromotion(int move) {
		return (move & 0x00070000) == EngineConstants.PROMOTION_SHIFTED;
	}
	
	public static boolean isCastling(int move) {
		return (move & 0x00040000) == EngineConstants.CASTLING_SHIFTED;
	}
	
	public static int getTo(int move) {
		return (move & 0x0000ff00) >>> 8;
	}
	
	public static int getFrom(int move) {
		return move & 0x000000ff;
	}
	
	public static int getMoveType(int move) {
		return move & 0x00070000;
	}
	
	public static int getCastlingSide(int move) {
		return (move & 0x00010000) >>> 16;
	}
	
	public static int encodeMove(int from, int to, int fromPiece) {
		return from | (to << 8) | (fromPiece << 24);
	}
	
	public static int encodeAttackMove(int from, int to, int capturedPiece, int fromPiece) {
		return from | (to << 8) | (fromPiece << 24) | (capturedPiece << 27);
	}
	
	public static int encodeSpecialMove(int from, int to, int moveType, int fromPiece) {
		return from | (to << 8) | (moveType << 16) | (fromPiece << 24);
	}
	
	public static int encodeSpecialAttackMove(int from, int to, int moveType, int capturedPiece, int fromPiece) {
		return from | (to << 8) | (moveType << 16) | (fromPiece << 24) | (capturedPiece << 27);
	}
	
	public static int encodePromotionMove(int from, int to, int moveType, int promotedPiece) {
		return from | (to << 8) | (moveType << 16) | (promotedPiece << 20) | (EngineConstants.PAWN << 24);
	}
	
	public static int encodePromotionAttackMove(int from, int to, int moveType, int promotedPiece, int capturedPiece) {
		return from | (to << 8) | (moveType << 16) | (promotedPiece << 20) | (EngineConstants.PAWN << 24) | (capturedPiece << 27);
	}
	
	public static void print(int move) {
		System.out.println("From = " + getFrom(move) + " To = " + getTo(move));
	}
	
}

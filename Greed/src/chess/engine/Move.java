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
	
	public static byte getPromotedPiece(int move) {
		return (byte)((move & 0x00f00000) >>> 20);		
	}
	
	public static boolean isPromotion(int move) {
		return (move & 0x00070000) == EngineConstants.PROMOTION_SHIFTED;
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
	
	public static int encodeMove(int from, int to) {
		return from | (to << 8);
	}
	
	public static int encodeAttackMove(int from, int to, int capturedPiece) {
//		return from | (to << 8) | (capturedPiece << 27);
		return from | (to << 8);
	}
	
	public static int encodeMove(int from, int to, int moveType) {
		return from | (to << 8) | (moveType << 16);
	}
	
	public static int encodeAttackMove(int from, int to, int moveType, int capturedPiece) {
//		return from | (to << 8) | (moveType << 16) | (capturedPiece << 27);
		return from | (to << 8) | (moveType << 16);
	}
	
	public static int encodeMove(int from, int to, int moveType, int promotedPiece) {
		return from | (to << 8) | (moveType << 16) | (promotedPiece << 20);
	}
	
	public static int encodeAttackMove(int from, int to, int moveType, int promotedPiece, int capturedPiece) {
//		return from | (to << 8) | (moveType << 16) | (promotedPiece << 20) | (capturedPiece << 27);
		return from | (to << 8) | (moveType << 16) | (promotedPiece << 20);
	}
	
}

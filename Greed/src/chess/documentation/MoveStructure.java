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

public class MoveStructure {
	
	/**
	 * move structure explanation.
	 * 
	 * move is 32 bit.
	 * 
	 * .... .... .... .... / .... .... .... ....
	 * 
	 * from = range 0 to 63, in binary representation, 00 0000 to 11 1111
	 * from is not shifted and stands in first 6 bit.
	 * .... .... .... .... / .... .... ..(from)(from) (from)(from)(from)(from)
	 * 
	 * 
	 * to = range 0 to 63, in binary representation, 00 0000 to 11 1111
	 * to is shifted 8 bits. maybe 6 bit is necessary... think about it later.
	 * .... .... .... .... / ..(to)(to) (to)(to)(to)(to) ..(from)(from) (from)(from)(from)(from)
	 * 
	 * 
	 * moveType = range 1 to 5, in binary representation, 0001 to 101
	 * moveType is shifted 16 bits.
	 * .... .... .... .(moveType)(moveType)(moveType) / ..(to)(to) (to)(to)(to)(to) ..(from)(from) (from)(from)(from)(from)
	 * 
	 * 
	 * promotionType = range 4 to 10, in binary representation , 0100 to 1010
	 * promotionType is shifted 20 bits.
	 * .... .... (promotionType)(promotionType)(promotionType)(promotionType) .(moveType)(moveType)(moveType) / ..(to)(to) (to)(to)(to)(to) ..(from)(from) (from)(from)(from)(from)
	 * 
	 * 
	 * fromPieceValue = range 1 to 6, in binary representation , 001 to 110
	 * fromPieceValue is shifted transparently by 24 bits.
	 * .... .(fromPieceValue)(fromPieceValue)(fromPieceValue) (promotionType)(promotionType)(promotionType)(promotionType) .(moveType)(moveType)(moveType) / ..(to)(to) (to)(to)(to)(to) ..(from)(from) (from)(from)(from)(from)
	 * 
	 * 
	 * captured piece = range 0 to 13 in binary representation 0000 to 1101
	 * captured piece is shifted 27 bits. why not shift by 28 bit. maybe two's complements can cause negative numbers. and prevent ordering moves?
	 * .(capt)(capt)(capt) (capt)(fromPieceValue)(fromPieceValue)(fromPieceValue) (promotionType)(promotionType)(promotionType)(promotionType) .(moveType)(moveType)(moveType) / ..(to)(to) (to)(to)(to)(to) ..(from)(from) (from)(from)(from)(from)
	 * 
	 * 
	 * In the MVV LVA implementation. it is important to know from piece value. pawn capture queen must be evaluated first.
	 * Magic Number For 
	 * Pawn Moves   = 100663296    0000 0110 0000 0000 / 0000 0000 0000 0000
	 * Knight Moves =  83886080    0000 0101 0000 0000 / 0000 0000 0000 0000
	 * Bishop Atck  =  67108864    0000 0100 0000 0000 / 0000 0000 0000 0000
	 * Rook Moves   =  50331648    0000 0011 0000 0000 / 0000 0000 0000 0000
	 * Queen Attack =  33554432    0000 0010 0000 0000 / 0000 0000 0000 0000
	 * King Attack  =  16777216    0000 0001 0000 0000 / 0000 0000 0000 0000
	 * 
	 * */

}

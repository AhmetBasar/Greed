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

import chess.movegen.MagicBitboard;

//https://github.com/sandermvdb/chess22k
public class Check {
	
	public static long getCheckers(long[] bb, int side, int opSide, int kingSquare, long occ) {
		
		return EngineConstants.PAWN_ATTACK_LOOKUP[side][kingSquare] & bb[opSide | EngineConstants.PAWN]
		 | EngineConstants.KNIGHT_LOOKUP[kingSquare] & bb[opSide | EngineConstants.KNIGHT]
		 | (bb[opSide | EngineConstants.BISHOP] | bb[opSide | EngineConstants.QUEEN]) & MagicBitboard.generateBishopMoves(kingSquare, occ)
		 | (bb[opSide | EngineConstants.ROOK] | bb[opSide | EngineConstants.QUEEN]) & MagicBitboard.generateRookMoves(kingSquare, occ)
				 ;
	}
	
	public static long getCheckers(IBoard board) {
		return getCheckers(board.getBitboard(), board.getSide(), board.getOpSide(), board.getKingSquares()[board.getSide()], board.getOccupiedSquares());
	}
	
	public static boolean isKingIncheckIncludingKing(boolean hasEnemyMajorPieces, int kingSquare, long[] bitboard, int opSide, int side, long occupiedSquares) {
		if (!hasEnemyMajorPieces) {
			return ((bitboard[opSide | EngineConstants.PAWN] & EngineConstants.PAWN_ATTACK_LOOKUP[side][kingSquare]
			    | bitboard[opSide | EngineConstants.KING] & EngineConstants.KING_LOOKUP[kingSquare]) != 0);
		}
		
		return (bitboard[opSide | EngineConstants.KNIGHT] & EngineConstants.KNIGHT_LOOKUP[kingSquare]
			  | (bitboard[opSide | EngineConstants.ROOK] | bitboard[opSide | EngineConstants.QUEEN]) & MagicBitboard.generateRookMoves(kingSquare, occupiedSquares)
			  | (bitboard[opSide | EngineConstants.BISHOP] | bitboard[opSide | EngineConstants.QUEEN]) & MagicBitboard.generateBishopMoves(kingSquare, occupiedSquares)
		      | bitboard[opSide | EngineConstants.PAWN] & EngineConstants.PAWN_ATTACK_LOOKUP[side][kingSquare]
			  | bitboard[opSide | EngineConstants.KING] & EngineConstants.KING_LOOKUP[kingSquare]) != 0;
	}
	
	public static boolean isKingIncheckIncludingKing(int kingSquare, long[] bitboard, int opSide, int side, long occupiedSquares) {
		return (bitboard[opSide | EngineConstants.KNIGHT] & EngineConstants.KNIGHT_LOOKUP[kingSquare]
			  | (bitboard[opSide | EngineConstants.ROOK] | bitboard[opSide | EngineConstants.QUEEN]) & MagicBitboard.generateRookMoves(kingSquare, occupiedSquares)
			  | (bitboard[opSide | EngineConstants.BISHOP] | bitboard[opSide | EngineConstants.QUEEN]) & MagicBitboard.generateBishopMoves(kingSquare, occupiedSquares)
		      | bitboard[opSide | EngineConstants.PAWN] & EngineConstants.PAWN_ATTACK_LOOKUP[side][kingSquare]
			  | bitboard[opSide | EngineConstants.KING] & EngineConstants.KING_LOOKUP[kingSquare]) != 0;
	}
	
	public static boolean isKingIncheck(int kingSquare, long[] bitboard, int opSide, int side, long occupiedSquares) {
		return (bitboard[opSide | EngineConstants.KNIGHT] & EngineConstants.KNIGHT_LOOKUP[kingSquare]
			  | (bitboard[opSide | EngineConstants.ROOK] | bitboard[opSide | EngineConstants.QUEEN]) & MagicBitboard.generateRookMoves(kingSquare, occupiedSquares)
			  | (bitboard[opSide | EngineConstants.BISHOP] | bitboard[opSide | EngineConstants.QUEEN]) & MagicBitboard.generateBishopMoves(kingSquare, occupiedSquares)
		      | bitboard[opSide | EngineConstants.PAWN] & EngineConstants.PAWN_ATTACK_LOOKUP[side][kingSquare]) != 0;
	}
	
}

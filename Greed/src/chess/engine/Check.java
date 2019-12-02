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

public class Check {
	
	public static long getCheckers(IBoard board) {
		int side = board.getSide();
		int opSide = board.getOpSide();
		int kingSquare = board.getKingSquares()[board.getSide()];
		
		return EngineConstants.PAWN_ATTACK_LOOKUP[side][kingSquare] & board.getBitboard()[opSide | EngineConstants.PAWN]
		 | EngineConstants.KNIGHT_LOOKUP[kingSquare] & board.getBitboard()[opSide | EngineConstants.KNIGHT]
		 | (board.getBitboard()[opSide | EngineConstants.BISHOP] | board.getBitboard()[opSide | EngineConstants.QUEEN]) & MagicBitboard.generateBishopMoves(kingSquare, board.getOccupiedSquares())
		 | (board.getBitboard()[opSide | EngineConstants.ROOK] | board.getBitboard()[opSide | EngineConstants.QUEEN]) & MagicBitboard.generateRookMoves(kingSquare, board.getOccupiedSquares())
				 ;
	}
	
}

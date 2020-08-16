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
import chess.util.Utility;

//https://github.com/sandermvdb/chess22k
public class StaticExchangeEvaluation {
	
	public static int getSeeCaptureScore(int move, IBoard board, int side) {
		long allPieces = board.getOccupiedSquares();
		
		int from = Move.getFrom(move);
		int to = Move.getTo(move);
		byte fromPiece = board.getPieces()[from];
		
		allPieces &= ~(Utility.SINGLE_BIT[from]);
		long slidingMask = MagicBitboard.getQueenMovesWithoutBlocker(to) & allPieces;
		
		int moveType = Move.getMoveType(move);
		if (moveType == EngineConstants.PROMOTION_SHIFTED) {
			byte promotedPiece = Move.getPromotedPiece(move);
			return EngineConstants.PIECE_VALUES_POSITIVE[promotedPiece] + EngineConstants.PIECE_VALUES_POSITIVE[board.getPieces()[to]] - getSeeScore(to, allPieces, slidingMask, fromPiece, board, side ^ 1);
		} else {
			byte capturedPiece;
			if (moveType == EngineConstants.EP_CAPTURE_SHIFTED) {
				capturedPiece = EngineConstants.PAWN;
			} else {
				capturedPiece = board.getPieces()[to];
			}
			
			return EngineConstants.PIECE_VALUES_POSITIVE[capturedPiece] - getSeeScore(to, allPieces, slidingMask, fromPiece, board, side ^ 1);
		}
		
	}
	
	private static int getSeeScore(int to, long allPieces, long slidingMask, byte toBeCapturedPiece, IBoard board, int side) {
		
		int leastValuableAttackerIndex = getLeastValuableAttacker(to, allPieces, slidingMask, board, side);
		
		if (leastValuableAttackerIndex == -1) {
			return 0;
		}
		
		if ((toBeCapturedPiece & 0XFE) == EngineConstants.KING) {
			return EngineConstants.WHITE_KING_V;
		}
		
		allPieces &= ~(Utility.SINGLE_BIT[leastValuableAttackerIndex]);
		slidingMask &= allPieces;
		
		return Math.max(0, EngineConstants.PIECE_VALUES_POSITIVE[toBeCapturedPiece] - getSeeScore(to, allPieces, slidingMask, board.getPieces()[leastValuableAttackerIndex], board, side ^ 1));
	}
	
	private static int getLeastValuableAttacker(int to, long allPieces, long slidingMask, IBoard board, int side) {
		
		long pawnAttacks = EngineConstants.PAWN_ATTACK_LOOKUP[side ^ 1][to] & board.getBitboard()[side | EngineConstants.PAWN] & allPieces;
		if (pawnAttacks != 0) {
			return Long.numberOfTrailingZeros(pawnAttacks);
		}
		
		long knightAttacks = EngineConstants.KNIGHT_LOOKUP[to] & board.getBitboard()[side | EngineConstants.KNIGHT] & allPieces;
		if (knightAttacks != 0) {
			return Long.numberOfTrailingZeros(knightAttacks);
		}
		
		if ((board.getBitboard()[side | EngineConstants.BISHOP] & slidingMask) != 0) {
			long bishopAttacks = board.getBitboard()[side | EngineConstants.BISHOP] & MagicBitboard.generateBishopMoves(to, allPieces) & allPieces;
			if (bishopAttacks != 0) {
				return Long.numberOfTrailingZeros(bishopAttacks);
			}
		}
		
		if ((board.getBitboard()[side | EngineConstants.ROOK] & slidingMask) != 0) {
			long rookAttacks = board.getBitboard()[side | EngineConstants.ROOK] & MagicBitboard.generateRookMoves(to, allPieces) & allPieces;
			if (rookAttacks != 0) {
				return Long.numberOfTrailingZeros(rookAttacks);
			}
		}
		
		if ((board.getBitboard()[side | EngineConstants.QUEEN] & slidingMask) != 0) {
			long queenAttacks = board.getBitboard()[side | EngineConstants.QUEEN] & MagicBitboard.generateQueenMoves(to, allPieces) & allPieces;
			if (queenAttacks != 0) {
				return Long.numberOfTrailingZeros(queenAttacks);
			}
		}
		
		long kingAttacks = EngineConstants.KING_LOOKUP[to] & board.getBitboard()[side | EngineConstants.KING] & allPieces;
		if (kingAttacks != 0) {
			return Long.numberOfTrailingZeros(kingAttacks);
		}
		
		return -1;
	}

}

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

public class MoveGenerationOrderedOnlyQueenPromotions_SBIV2 implements MoveGenerationConstants {

	private LegalityV4 legality = new LegalityV4();

	public void generateMoves(IBoard board, int depthPlusOne, int depth) {
		generateMoves(board.getBitboard(), board.getPieces(), board.getSide(), board.getEpTarget(), board.getCastlingRights(), board, depth);
	}
	
	//TODO : try direct access ep target and side instead of pass parameter. and compare performances..
	public void generateMoves(long[] bitboard, byte[] pieces, int side, int epTarget, byte[][] castlingRights, IBoard board, int depth) {
		int opSide = side ^ 1;
		int[] moveList = board.getMoveList();
		for ( int i = EngineConstants.MOVE_LIST_SIZE - 1  ; moveList[i] != 0 ; i--) {
			moveList[i] = 0;
		}
		
		int diff;
		long toBitboard;
		long fromBitboard;
		long singlePushes;
		int to;
		int from;
		int move;
		int idx = -1;
		long occupiedSquares = bitboard[EngineConstants.WHITE_PAWN] | bitboard[EngineConstants.WHITE_KNIGHT]
				| bitboard[EngineConstants.WHITE_BISHOP] | bitboard[EngineConstants.WHITE_ROOK]
				| bitboard[EngineConstants.WHITE_QUEEN] | bitboard[EngineConstants.WHITE_KING]
				| bitboard[EngineConstants.BLACK_PAWN] | bitboard[EngineConstants.BLACK_KNIGHT]
				| bitboard[EngineConstants.BLACK_BISHOP] | bitboard[EngineConstants.BLACK_ROOK]
				| bitboard[EngineConstants.BLACK_QUEEN] | bitboard[EngineConstants.BLACK_KING];
		long emptySquares = ~occupiedSquares;
		long enemySquares = bitboard[opSide | EngineConstants.PAWN] | bitboard[opSide | EngineConstants.KNIGHT]
				| bitboard[opSide | EngineConstants.BISHOP] | bitboard[opSide | EngineConstants.ROOK]
				| bitboard[opSide | EngineConstants.QUEEN] | bitboard[opSide | EngineConstants.KING];
		long enemyAndEmptySquares = emptySquares | enemySquares;

		// PAWNS
		fromBitboard = bitboard[side | EngineConstants.PAWN];

		// PAWN PUSHES

		// Single Pushes
		diff = pushDiffs[side];
		toBitboard = ((fromBitboard << diff) | (fromBitboard >>> (64 - diff))) & emptySquares;
		singlePushes = toBitboard; // will be reused.
		toBitboard &= ~promotionMask[side];
		while (toBitboard != 0) {
			to = Long.numberOfTrailingZeros(toBitboard);
			
			from = (((to - diff) % 64) + 64) % 64;
			move = from | (to << 8);
			moveList[++idx] = move;
			toBitboard &= (toBitboard - 1);
		}

		// Double Pushes
		toBitboard = (((singlePushes & doublePushMask[side]) << diff)
				| ((singlePushes & doublePushMask[side]) >>> (64 - diff))) & emptySquares;
		while (toBitboard != 0) {
			to = Long.numberOfTrailingZeros(toBitboard);
			
			from = (((to - diff - diff) % 64) + 64) % 64;
			move = from | (to << 8) | (EngineConstants.DOUBLE_PUSH << 16);
			moveList[++idx] = move;
			toBitboard &= (toBitboard - 1);
		}
		
		// TODO : Maybe promotions should be handled before pawn pushes in order to reduce branching factor by alpha-beta cutoffs. 
		// Pawn Promotions
		toBitboard=singlePushes&promotionMask[side];
		while (toBitboard != 0) {
			to = Long.numberOfTrailingZeros(toBitboard);
			
			from=(((to-diff)%64)+64)%64;

			//Queen Promotions
			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.QUEEN) 	<< 20);
			moveList[++idx] = move;

			toBitboard &= (toBitboard - 1);
		}

		// PAWN ATTACKS
		for (int dir = 0; dir < 2; dir++) {
			diff = attackDiffs[dir][side];

			// Pure Pawn Attacks
			toBitboard = ((fromBitboard << diff) | (fromBitboard >>> (64 - diff))) & fileMask[dir] & enemySquares & (~promotionMask[side]);
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				
				from = (((to - diff) % 64) + 64) % 64;
				move = from | (to << 8) | 100663296 | (((int)pieces[to]) << 27);
				moveList[++idx] = move;
				toBitboard &= (toBitboard - 1);
			}
			
			// En-Passant Capture
			if(epTarget != 64){
				toBitboard=((fromBitboard<<diff) | (fromBitboard>>>(64-diff))) & fileMask[dir] & ( 1L << epTarget );
				while (toBitboard != 0) {
					to = Long.numberOfTrailingZeros(toBitboard);
					
					from=(((to-diff)%64)+64)%64;
					move = from | (to << 8) | (EngineConstants.EP_CAPTURE << 16) | 100663296 | (((int)(EngineConstants.PAWN | opSide)) << 27);
					moveList[++idx] = move;
					toBitboard &= (toBitboard - 1);
				}
			}
			
			// Promotion Attacks
			toBitboard= ((fromBitboard << diff) | (fromBitboard >>> (64 - diff))) & fileMask[dir] & enemySquares & promotionMask[side];
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				
				from=(((to-diff)%64)+64)%64;
				
				//Queen Promotions
				move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.QUEEN) 	<< 20) | 100663296 | (((int)pieces[to]) << 27);
				moveList[++idx] = move;
				
				toBitboard &= (toBitboard - 1);
			}
		}

		// KNIGHT ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.KNIGHT];
		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			
			toBitboard = EngineConstants.KNIGHT_LOOKUP[from] & enemyAndEmptySquares;
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				
				move = from | (to << 8) | 83886080 | (((int)pieces[to]) << 27);
				moveList[++idx] = move;
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}

		// KING ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.KING];
		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			
			toBitboard = EngineConstants.KING_LOOKUP[from] & enemyAndEmptySquares;
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				
				move = from | (to << 8) | 16777216 | (((int)pieces[to]) << 27);
				moveList[++idx] = move;
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}

		// ROOK ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.ROOK];

		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);

			toBitboard = MagicBitboard.generateRookMoves(from, occupiedSquares) & enemyAndEmptySquares;
			
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				
				move = from | (to << 8) | 50331648 | (((int)pieces[to]) << 27);
				moveList[++idx] = move;
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}

		// BISHOP ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.BISHOP];
		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);

			toBitboard = MagicBitboard.generateBishopMoves(from, occupiedSquares) & enemyAndEmptySquares;
			
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				
				move = from | (to << 8) | 67108864 | (((int)pieces[to]) << 27);
				moveList[++idx] = move;
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}

		// QUEEN ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.QUEEN];

		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);

			toBitboard = MagicBitboard.generateQueenMoves(from, occupiedSquares) & enemyAndEmptySquares;
			
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				
				move = from | (to << 8) | 33554432 | (((int)pieces[to]) << 27);
				moveList[++idx] = move;
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}
		
		
		// Castling Queen Side
		fromBitboard = bitboard[side | EngineConstants.KING];
		if ((from = Long.numberOfTrailingZeros(fromBitboard)) != 64) {
			toBitboard = (castlingRights[side][0] & (emptySquares >>> castlingShift[side][0][0]) 
					                              & (emptySquares >>> castlingShift[side][0][1])
					                              & (emptySquares >>> castlingShift[side][0][2])) << castlingTarget[side][0];
			if ((to = Long.numberOfTrailingZeros(toBitboard)) != 64) {
				if(!legality.isKingInCheck(bitboard, side)){
					byte sideToKing = (byte)(side| EngineConstants.KING);
					int kingOriginalPos = kingPositions[side];
					int squareBetweenKingAndRook = betweenKingAndRook[side][0];
					bitboard[sideToKing] &= ~(1L << kingOriginalPos);
					bitboard[sideToKing] |= (1L << squareBetweenKingAndRook);
					if(!legality.isKingInCheck(bitboard, side)){
						move = from | (to << 8) | (EngineConstants.QUEEN_SIDE_CASTLING << 16);
						moveList[++idx] = move;
					}
					bitboard[sideToKing] &= ~(1L << squareBetweenKingAndRook);
					bitboard[sideToKing] |= (1L << kingOriginalPos);
				}
			}
		}
		
		// Castling King Side 
		fromBitboard = bitboard[side | EngineConstants.KING];
		if ((from = Long.numberOfTrailingZeros(fromBitboard)) != 64) {
			toBitboard = (castlingRights[side][1] & (emptySquares >>> castlingShift[side][1][0]) 
					                              & (emptySquares >>> castlingShift[side][1][1])) << castlingTarget[side][1];
			if ((to = Long.numberOfTrailingZeros(toBitboard)) != 64) {
				if(!legality.isKingInCheck(bitboard, side)){
					byte sideToKing = (byte)(side| EngineConstants.KING);
					int kingOriginalPos = kingPositions[side];
					int squareBetweenKingAndRook = betweenKingAndRook[side][1];
					bitboard[sideToKing] &= ~(1L << kingOriginalPos);
					bitboard[sideToKing] |= (1L << squareBetweenKingAndRook);
					if(!legality.isKingInCheck(bitboard, side)){
						move = from | (to << 8) | (EngineConstants.KING_SIDE_CASTLING << 16);
						moveList[++idx] = move;
					}
					bitboard[sideToKing] &= ~(1L << squareBetweenKingAndRook);
					bitboard[sideToKing] |= (1L << kingOriginalPos);
				}
			}
		}
	}

}
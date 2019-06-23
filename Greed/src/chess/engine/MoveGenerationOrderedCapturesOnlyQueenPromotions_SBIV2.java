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

public class MoveGenerationOrderedCapturesOnlyQueenPromotions_SBIV2 {
	private int[] pushDiffs = { 8, 64 - 8 };
	private long[] promotionMask = { EngineConstants.ROW_8, EngineConstants.ROW_1 };
	private long[] fileMask = { ~EngineConstants.FILE_H, ~EngineConstants.FILE_A };
	private int[][] attackDiffs = { { 7, 64 - 9 }, { 9, 64 - 7 } };
	private long rightMoves, leftMoves, upMoves, downMoves, moves_45, moves_135, moves_225, moves_315, lookup;

	public void generateMoves(IBoard board, int side, int depthPlusOne, int depth) {
		generateMoves(board.getBitboard(), board.getPieces(), side, board.getEpTarget(depthPlusOne), board.getCastlingRights(depthPlusOne), board, depth);
	}
	
	//TODO : try direct access ep target and side instead of pass parameter. and compare performances..
	public void generateMoves(long[] bitboard, byte[] pieces, int side, int epTarget, byte[][] castlingRights, IBoard board, int depth) {
		int opSide = side ^ 1;
		int[] moveList = board.getMoveList(depth);
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

		// TODO : Maybe promotions should be handled before pawn pushes in order to reduce branching factor by alpha-beta cutoffs.
		// PAWNS
		// Pawn Promotions
		fromBitboard = bitboard[side | EngineConstants.PAWN];
		diff = pushDiffs[side];
		singlePushes = ((fromBitboard << diff) | (fromBitboard >>> (64 - diff))) & emptySquares;
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
			
			toBitboard = EngineConstants.KNIGHT_LOOKUP[from] & enemySquares;
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
			
			toBitboard = EngineConstants.KING_LOOKUP[from] & enemySquares;
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

			lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.RIGHT];
			rightMoves = lookup & occupiedSquares;
			rightMoves = (rightMoves << 1) | 
					     (rightMoves << 2) | 
					     (rightMoves << 3) | 
					     (rightMoves << 4) | 
					     (rightMoves << 5) | 
					     (rightMoves << 6);
			rightMoves = (rightMoves & lookup) ^ lookup;

			lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.LEFT];
			leftMoves = lookup & occupiedSquares;
			leftMoves = (leftMoves >>> 1) | 
					    (leftMoves >>> 2) | 
					    (leftMoves >>> 3) | 
					    (leftMoves >>> 4) | 
					    (leftMoves >>> 5) | 
					    (leftMoves >>> 6);
			leftMoves = (leftMoves & lookup) ^ lookup;

			lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.UP];
			upMoves = lookup & occupiedSquares;
			upMoves = (upMoves << 8)  | 
					  (upMoves << 16) | 
					  (upMoves << 24) | 
					  (upMoves << 32) | 
					  (upMoves << 40) | 
					  (upMoves << 48);
			upMoves = (upMoves & lookup) ^ lookup;

			lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.DOWN];
			downMoves = lookup & occupiedSquares;
			downMoves = (downMoves >>> 8)  | 
					    (downMoves >>> 16) | 
					    (downMoves >>> 24) | 
					    (downMoves >>> 32) | 
					    (downMoves >>> 40) | 
					    (downMoves >>> 48);
			downMoves = (downMoves & lookup) ^ lookup;

			toBitboard = (rightMoves | leftMoves | upMoves | downMoves) & enemySquares;
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

			lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_45];
			moves_45 = lookup & occupiedSquares;
			moves_45 = (moves_45 << 9)  | 
					   (moves_45 << 18) | 
					   (moves_45 << 27) | 
					   (moves_45 << 36) | 
					   (moves_45 << 45) | 
					   (moves_45 << 54);
			moves_45 = (moves_45 & lookup) ^ lookup;

			lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_135];
			moves_135 = lookup & occupiedSquares;
			moves_135 = (moves_135 << 7)  | 
					    (moves_135 << 14) | 
					    (moves_135 << 21) | 
					    (moves_135 << 28) | 
					    (moves_135 << 35) | 
					    (moves_135 << 42);
			moves_135 = (moves_135 & lookup) ^ lookup;

			lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_225];
			moves_225 = lookup & occupiedSquares;
			moves_225 = (moves_225 >>> 9)  | 
					    (moves_225 >>> 18) | 
					    (moves_225 >>> 27) | 
					    (moves_225 >>> 36) | 
					    (moves_225 >>> 45) | 
					    (moves_225 >>> 54);
			moves_225 = (moves_225 & lookup) ^ lookup;

			lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_315];
			moves_315 = lookup & occupiedSquares;
			moves_315 = (moves_315 >>> 7)  | 
					    (moves_315 >>> 14) | 
					    (moves_315 >>> 21) | 
					    (moves_315 >>> 28) | 
					    (moves_315 >>> 35) | 
					    (moves_315 >>> 42);
			moves_315 = (moves_315 & lookup) ^ lookup;

			toBitboard = (moves_45 | moves_135 | moves_225 | moves_315) & enemySquares;
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

			lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.RIGHT];
			rightMoves = lookup & occupiedSquares;
			rightMoves = (rightMoves << 1) | 
					     (rightMoves << 2) | 
					     (rightMoves << 3) | 
					     (rightMoves << 4) | 
					     (rightMoves << 5) | 
					     (rightMoves << 6);
			rightMoves = (rightMoves & lookup) ^ lookup;

			lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.LEFT];
			leftMoves = lookup & occupiedSquares;
			leftMoves = (leftMoves >>> 1) | 
					    (leftMoves >>> 2) | 
					    (leftMoves >>> 3) | 
					    (leftMoves >>> 4) | 
					    (leftMoves >>> 5) | 
					    (leftMoves >>> 6);
			leftMoves = (leftMoves & lookup) ^ lookup;

			lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.UP];
			upMoves = lookup & occupiedSquares;
			upMoves = (upMoves << 8)  | 
					  (upMoves << 16) | 
					  (upMoves << 24) | 
					  (upMoves << 32) | 
					  (upMoves << 40) | 
					  (upMoves << 48);
			upMoves = (upMoves & lookup) ^ lookup;

			lookup = EngineConstants.ROOK_LOOKUP[from][EngineConstants.DOWN];
			downMoves = lookup & occupiedSquares;
			downMoves = (downMoves >>> 8)  | 
					    (downMoves >>> 16) | 
					    (downMoves >>> 24) | 
					    (downMoves >>> 32) | 
					    (downMoves >>> 40) | 
					    (downMoves >>> 48);
			downMoves = (downMoves & lookup) ^ lookup;

			lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_45];
			moves_45 = lookup & occupiedSquares;
			moves_45 = (moves_45 << 9)  | 
					   (moves_45 << 18) | 
					   (moves_45 << 27) | 
					   (moves_45 << 36) | 
					   (moves_45 << 45) | 
					   (moves_45 << 54);
			moves_45 = (moves_45 & lookup) ^ lookup;

			lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_135];
			moves_135 = lookup & occupiedSquares;
			moves_135 = (moves_135 << 7)  | 
					    (moves_135 << 14) | 
					    (moves_135 << 21) | 
					    (moves_135 << 28) | 
					    (moves_135 << 35) | 
					    (moves_135 << 42);
			moves_135 = (moves_135 & lookup) ^ lookup;

			lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_225];
			moves_225 = lookup & occupiedSquares;
			moves_225 = (moves_225 >>> 9)  | 
					    (moves_225 >>> 18) | 
					    (moves_225 >>> 27) | 
					    (moves_225 >>> 36) | 
					    (moves_225 >>> 45) | 
					    (moves_225 >>> 54);
			moves_225 = (moves_225 & lookup) ^ lookup;

			lookup = EngineConstants.BISHOP_LOOKUP[from][EngineConstants.DEGREE_315];
			moves_315 = lookup & occupiedSquares;
			moves_315 = (moves_315 >>> 7)  | 
					    (moves_315 >>> 14) | 
					    (moves_315 >>> 21) | 
					    (moves_315 >>> 28) | 
					    (moves_315 >>> 35) | 
					    (moves_315 >>> 42);
			moves_315 = (moves_315 & lookup) ^ lookup;

			toBitboard = ((moves_45 | moves_135 | moves_225 | moves_315) & enemySquares)
					| ((rightMoves | leftMoves | upMoves | downMoves) & enemySquares);
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				
				move = from | (to << 8) | 33554432 | (((int)pieces[to]) << 27);
				moveList[++idx] = move;
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}
	}

}
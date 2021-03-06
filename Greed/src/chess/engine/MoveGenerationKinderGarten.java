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

// https://www.chessprogramming.org/Kindergarten_Bitboards
public class MoveGenerationKinderGarten implements MoveGenerationConstants {
	private LegalityV4 legality = new LegalityV4();
	
	//TODO : try direct access ep target and side instead of pass parameter. and compare performances..
	public int[] generateMoves(long[] bitboard, int side, int epTarget, byte[][] castlingRights) {
		int opSide = side ^ 1;
		int[] moveList = new int[EngineConstants.MOVE_LIST_SIZE];
		int diff;
		long toBitboard;
		long fromBitboard;
		long singlePushes;
		int to;
		int from;
		int move;
		int idx = -1;
		int file;
		long occ;
		long rankAttacks;
		long fileAttacks;
		long diagAttacks;
		long adiagAttacks;
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
		while ((to = Long.numberOfTrailingZeros(toBitboard)) != 64) {
			from = (((to - diff) % 64) + 64) % 64;
			move = from | (to << 8);
			moveList[++idx] = move;
			toBitboard = toBitboard & ~(1L << to);
		}

		// Double Pushes
		toBitboard = (((singlePushes & doublePushMask[side]) << diff)
				| ((singlePushes & doublePushMask[side]) >>> (64 - diff))) & emptySquares;
		while ((to = Long.numberOfTrailingZeros(toBitboard)) != 64) {
			from = (((to - diff - diff) % 64) + 64) % 64;
			move = from | (to << 8) | (EngineConstants.DOUBLE_PUSH << 16);
			moveList[++idx] = move;
			toBitboard = toBitboard & ~(1L << to);
		}
		
		// TODO : Maybe promotions should be handled before pawn pushes in order to reduce branching factor by alpha-beta cutoffs. 
		// Pawn Promotions
		toBitboard=singlePushes&promotionMask[side];
		while((to=Long.numberOfTrailingZeros(toBitboard))!=64){
			from=(((to-diff)%64)+64)%64;

			//Queen Promotions
			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.QUEEN) 	<< 20);
			moveList[++idx] = move;
			//Rook Promotions
			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.ROOK) 	<< 20);
			moveList[++idx] = move;
			//Bishop Promotions
			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.BISHOP) 	<< 20);
			moveList[++idx] = move;
			//Knight Promotions
			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.KNIGHT) 	<< 20);
			moveList[++idx] = move;

			toBitboard=toBitboard & ~(1L << to);
		}

		// PAWN ATTACKS
		for (int dir = 0; dir < 2; dir++) {
			diff = attackDiffs[dir][side];

			// Pure Pawn Attacks
			toBitboard = ((fromBitboard << diff) | (fromBitboard >>> (64 - diff))) & fileMask[dir] & enemySquares & (~promotionMask[side]);
			while ((to = Long.numberOfTrailingZeros(toBitboard)) != 64) {
				from = (((to - diff) % 64) + 64) % 64;
				move = from | (to << 8);
				moveList[++idx] = move;
				toBitboard = toBitboard & ~(1L << to);
			}
			
			// En-Passant Capture
			if(epTarget != 64){
				toBitboard=((fromBitboard<<diff) | (fromBitboard>>>(64-diff))) & fileMask[dir] & ( 1L << epTarget );
				while((to=Long.numberOfTrailingZeros(toBitboard))!=64){
					from=(((to-diff)%64)+64)%64;
					move = from | (to << 8) | (EngineConstants.EP_CAPTURE << 16);
					moveList[++idx] = move;
					toBitboard=toBitboard & ~(1L << to);
				}
			}
			
			// Promotion Attacks
			toBitboard= ((fromBitboard << diff) | (fromBitboard >>> (64 - diff))) & fileMask[dir] & enemySquares & promotionMask[side];
			while((to=Long.numberOfTrailingZeros(toBitboard))!=64){
				from=(((to-diff)%64)+64)%64;
				
				//Queen Promotions
				move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.QUEEN) 	<< 20);
				moveList[++idx] = move;
				//Rook Promotions
				move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.ROOK) 	<< 20);
				moveList[++idx] = move;
				//Bishop Promotions
				move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.BISHOP) 	<< 20);
				moveList[++idx] = move;
				//Knight Promotions
				move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.KNIGHT) 	<< 20);
				moveList[++idx] = move;
				
				toBitboard=toBitboard & ~(1L << to);
			}
		}

		// KNIGHT ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.KNIGHT];
		while ((from = Long.numberOfTrailingZeros(fromBitboard)) != 64) {
			toBitboard = EngineConstants.KNIGHT_LOOKUP[from] & enemyAndEmptySquares;
			while ((to = Long.numberOfTrailingZeros(toBitboard)) != 64) {
				move = from | (to << 8);
				moveList[++idx] = move;
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// KING ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.KING];
		while ((from = Long.numberOfTrailingZeros(fromBitboard)) != 64) {
			toBitboard = EngineConstants.KING_LOOKUP[from] & enemyAndEmptySquares;
			while ((to = Long.numberOfTrailingZeros(toBitboard)) != 64) {
				move = from | (to << 8);
				moveList[++idx] = move;
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// ROOK ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.ROOK];

		while ((from = Long.numberOfTrailingZeros(fromBitboard)) != 64) {

			
			/**
			 * File Attacks are hard to understand compared to rank and diagonal attacks.
			 * first of all occupancy of the corresponding file is shifted to FILE_A and mask with FILE_A to set other bits to zero.
			 * then multiply with c2h7 file. this multiplication has a special meaning. it is shifted one bit to right (it is not C3 H8 DIAG watch out!), so rightmost bit is ignored. and it is not b1h7 so leftmost bit
			 * is ignored. and normal operation shift right 58 bit to obtain occupancy index. FILE_ATTACKS is PreCalculated file AttackSet of A file. it is
			 * indexed by rank index and occupancy value. Finally, attack set is left shift file index times. because FILE_ATTACKS is for A_FILE only.
			 * */
			
			//
			file = from & 7;
			occ = EngineConstants.FILE_A & (occupiedSquares >>> file);
			occ = (EngineConstants.DIAG_C2H7 * occ) >>> 58;
			fileAttacks = PrecalculatedAttackTables.FILE_ATTACKS[from >>> 3][(int)occ] << file;
			//
			
			//
			occ = occupiedSquares & EngineConstants.ROW_MASK[from];
			occ = (occ * EngineConstants.FILE_B) >>> 58;
			rankAttacks = EngineConstants.ROW_MASK[from] & PrecalculatedAttackTables.RANK_ATTACKS[file][(int)occ];
			//

			toBitboard = (rankAttacks | fileAttacks) & enemyAndEmptySquares;
			while ((to = Long.numberOfTrailingZeros(toBitboard)) != 64) {
				move = from | (to << 8);
				moveList[++idx] = move;
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// BISHOP ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.BISHOP];
		while ((from = Long.numberOfTrailingZeros(fromBitboard)) != 64) {

			file = from & 7;
			//
			/**
			 * from is 0-63 square index. A1 = 0, H8 = 63
			 * DIAG_MASK[from] corresponding diagonal mask for particular square index(from)
			 * OCC * FILE_B is a NortFill multiplication. But not FILE_A because we don't need rightmost occupancy bit.
			 * >>> 58 is final operation. But not 57 because we don't need left most occupancy bit.
			 * RANK_ATTACKS is PreCalculated rank attack set. it is indexed by file index and occupancy value.
			 * Furthermore, it is 8x duplicated for per rank.
			 * Final operation is to AND with DIAG_MASK again to obtain diagonal attack set of particular bishop.
			 * */
			occ = occupiedSquares & EngineConstants.DIAG_MASK[from];
			occ = (occ * EngineConstants.FILE_B) >>> 58;
			diagAttacks = EngineConstants.DIAG_MASK[from] & PrecalculatedAttackTables.RANK_ATTACKS[file][(int)occ];
			
			occ = occupiedSquares & EngineConstants.ADIAG_MASK[from];
			occ = (occ * EngineConstants.FILE_B) >>> 58;
			adiagAttacks = EngineConstants.ADIAG_MASK[from] & PrecalculatedAttackTables.RANK_ATTACKS[file][(int)occ];
			//

			toBitboard = (diagAttacks | adiagAttacks) & enemyAndEmptySquares;
			while ((to = Long.numberOfTrailingZeros(toBitboard)) != 64) {
				move = from | (to << 8);
				moveList[++idx] = move;
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// QUEEN ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.QUEEN];

		while ((from = Long.numberOfTrailingZeros(fromBitboard)) != 64) {
			
			//
			file = from & 7;
			occ = EngineConstants.FILE_A & (occupiedSquares >>> file);
			occ = (EngineConstants.DIAG_C2H7 * occ) >>> 58;
			fileAttacks = PrecalculatedAttackTables.FILE_ATTACKS[from >>> 3][(int)occ] << file;
			//
			
			//
			occ = occupiedSquares & EngineConstants.ROW_MASK[from];
			occ = (occ * EngineConstants.FILE_B) >>> 58;
			rankAttacks = EngineConstants.ROW_MASK[from] & PrecalculatedAttackTables.RANK_ATTACKS[file][(int)occ];
			//
			
			//
			occ = occupiedSquares & EngineConstants.DIAG_MASK[from];
			occ = (occ * EngineConstants.FILE_B) >>> 58;
			diagAttacks = EngineConstants.DIAG_MASK[from] & PrecalculatedAttackTables.RANK_ATTACKS[file][(int)occ];
			
			occ = occupiedSquares & EngineConstants.ADIAG_MASK[from];
			occ = (occ * EngineConstants.FILE_B) >>> 58;
			adiagAttacks = EngineConstants.ADIAG_MASK[from] & PrecalculatedAttackTables.RANK_ATTACKS[file][(int)occ];
			//

			toBitboard = ((diagAttacks | adiagAttacks) & enemyAndEmptySquares)
					| ((rankAttacks | fileAttacks) & enemyAndEmptySquares);
			while ((to = Long.numberOfTrailingZeros(toBitboard)) != 64) {
				move = from | (to << 8);
				moveList[++idx] = move;
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
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

		return moveList;
	}

}

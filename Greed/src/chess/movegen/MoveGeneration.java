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
package chess.movegen;

import java.util.HashSet;
import java.util.Set;

import chess.engine.Check;
import chess.engine.EngineConstants;
import chess.engine.IBoard;
import chess.engine.Move;
import chess.engine.MoveGenerationConstants;
import chess.util.Utility;

// https://github.com/sandermvdb/chess22k
public class MoveGeneration implements MoveGenerationConstants {
	
	private int[] moves = new int[1500];
	private final int[] moveScores = new int[1500];
	private int[] nextToGenerate = new int[MAX_PLIES * 2];
	private int[] nextToMove = new int[MAX_PLIES * 2];
	
	private int currentPly;
	private boolean allowUnderPromotion = false;
	
	public MoveGeneration(boolean allowUnderPromotion) {
		this.allowUnderPromotion = allowUnderPromotion;
	}
	
	public void startPly() {
		nextToGenerate[currentPly + 1] = nextToGenerate[currentPly];
		nextToMove[currentPly + 1] = nextToGenerate[currentPly];
		currentPly++;
	}
	
	public void endPly() {
		currentPly--;
	}
	
	public int next() {
		return moves[nextToMove[currentPly]++];
	}

	public int previous() {
		return moves[nextToMove[currentPly] - 1];
	}
	
	public boolean hasNext() {
		return nextToGenerate[currentPly] != nextToMove[currentPly];
	}
	
	public void sort() {
		int left = nextToMove[currentPly];
		for (int i = left, j = i; i < nextToGenerate[currentPly] - 1; j = ++i) {
			int score = moveScores[i + 1];
			int move = moves[i + 1];
			while (score > moveScores[j]) {
				moveScores[j + 1] = moveScores[j];
				moves[j + 1] = moves[j];
				if (j-- == left) {
					break;
				}
			}
			moveScores[j + 1] = score;
			moves[j + 1] = move;
		}
	}
	
	public Set<Integer> getMoveSet(){
		Set<Integer> moveSet = new HashSet<Integer>();
		int left = nextToMove[currentPly];
		for (int i = left; i < nextToGenerate[currentPly]; ++i) {
			int move = moves[i];
			moveSet.add(move);
		}
		return moveSet;
	}
	
	public void addMove(int move){
		moves[nextToGenerate[currentPly]++] = move;
	}
	
	public void setMvvLvaScores() {
		for (int i = nextToMove[currentPly]; i < nextToGenerate[currentPly]; i++) {
			int move = moves[i];
			int fromPiece = Move.getFromPiece(move);
			int capturedPiece = Move.getCapturedPiece(move);
			moveScores[i] = capturedPiece * 6 - fromPiece;
			if (Move.getMoveType(move) == EngineConstants.PROMOTION_SHIFTED) {
				moveScores[i] += EngineConstants.QUEEN * 6;
			}
		}
	}
	
	public void generateMoves(IBoard board) {
		if (board.getCheckers() == 0) {
			generateNotInCheckQuietMoves(board);
		} else if (Long.bitCount(board.getCheckers()) == 1) {
			if (board.getPieces()[Long.numberOfTrailingZeros(board.getCheckers())] <= EngineConstants.BLACK_KNIGHT) {
				generateKingQuietMoves(board);
			} else {
				generateOutOfSlidingCheckQuietMoves(board);
			}
		} else {
			generateKingQuietMoves(board);
		}
	}
	
	public void generateAttacks(IBoard board) {
		if (board.getCheckers() == 0) {
			generateNotInCheckAttacks(board);
		} else if (Long.bitCount(board.getCheckers()) == 1) {
			generateOutOfCheckAttacks(board);
		} else {
			generateKingAttacks(board);
		}
	}
	
	private void generateNotInCheckQuietMoves(IBoard board) {
		
		// non-pinned pieces
		long nonPinnedPieces = ~board.getPinnedPieces();
		generateKingQuietMoves(board);
		generateQueenMoves(board.getBitboard()[board.getSide() | EngineConstants.QUEEN] & nonPinnedPieces, board.getOccupiedSquares(), board.getEmptySquares());
		generateRookMoves(board.getBitboard()[board.getSide() | EngineConstants.ROOK] & nonPinnedPieces, board.getOccupiedSquares(), board.getEmptySquares());
		generateBishopMoves(board.getBitboard()[board.getSide() | EngineConstants.BISHOP] & nonPinnedPieces, board.getOccupiedSquares(), board.getEmptySquares());
		generateKnightMoves(board.getBitboard()[board.getSide() | EngineConstants.KNIGHT] & nonPinnedPieces, board.getEmptySquares());
		generatePawnPushes(board.getBitboard()[board.getSide() | EngineConstants.PAWN] & nonPinnedPieces, board.getSide(), board.getEmptySquares(), board.getEmptySquares());
		
		// pinned pieces
		long pinnedPieces = board.getOccupiedSquaresBySide()[board.getSide()] & board.getPinnedPieces();
		while (pinnedPieces != 0) {
			int pinnedPieceSquare = Long.numberOfTrailingZeros(pinnedPieces);
			byte pieceWc = (byte)(board.getPieces()[pinnedPieceSquare] & 0XFE);
			switch (pieceWc) {
			case EngineConstants.PAWN:
				generatePawnPushes(Long.lowestOneBit(pinnedPieces), board.getSide(), board.getEmptySquares() & Utility.PINNED_MOVEMENT[pinnedPieceSquare][board.getKingSquares()[board.getSide()]], board.getEmptySquares());
				break;
			case EngineConstants.BISHOP:
				generateBishopMoves(Long.lowestOneBit(pinnedPieces), board.getOccupiedSquares(), board.getEmptySquares() & Utility.PINNED_MOVEMENT[pinnedPieceSquare][board.getKingSquares()[board.getSide()]]);
				break;
			case EngineConstants.ROOK:
				generateRookMoves(Long.lowestOneBit(pinnedPieces), board.getOccupiedSquares(), board.getEmptySquares() & Utility.PINNED_MOVEMENT[pinnedPieceSquare][board.getKingSquares()[board.getSide()]]);
				break;
			case EngineConstants.QUEEN:
				generateQueenMoves(Long.lowestOneBit(pinnedPieces), board.getOccupiedSquares(), board.getEmptySquares() & Utility.PINNED_MOVEMENT[pinnedPieceSquare][board.getKingSquares()[board.getSide()]]);
				break;
			}
			pinnedPieces &= (pinnedPieces - 1);
		}
	}
	
	private void generateKingAttacks(IBoard board) {
		int from = board.getKingSquares()[board.getSide()];
		long toBitboard = EngineConstants.KING_LOOKUP[from] & board.getOccupiedSquaresBySide()[board.getOpSide()];
		while (toBitboard != 0) {
			int to = Long.numberOfTrailingZeros(toBitboard);
			addMove(Move.encodeAttackMove(from, to, EngineConstants.PIECE_WITHOUT_SIDE[board.getPieces()[to]], EngineConstants.KING));
			toBitboard &= (toBitboard - 1);
		}
	}
	
	private void generateKingQuietMoves(IBoard board) {
		int from = board.getKingSquares()[board.getSide()];
		long toBitboard = EngineConstants.KING_LOOKUP[from] & board.getEmptySquares();
		while (toBitboard != 0) {
			addMove(Move.encodeMove(from, Long.numberOfTrailingZeros(toBitboard), EngineConstants.KING));
			toBitboard &= (toBitboard - 1);
		}
		
		int side = board.getSide();
		int opSide = board.getOpSide();
		long[] bitboard = board.getBitboard();
		byte[][] castlingRights = board.getCastlingRights();
		long occ = board.getOccupiedSquares();
		
		// Castling Queen Side
		if (board.getCheckers() == 0) {
			if (castlingRights[side][0] == 1 && (EngineConstants.CASTLING_EMPTY_SQUARES[side][0] & occ) == 0) {
				if (!Check.isKingIncheckIncludingKing(betweenKingAndRook[side][0], bitboard, opSide, side, occ)) {
					addMove(Move.encodeSpecialMove(from, castlingTarget[side][0], EngineConstants.QUEEN_SIDE_CASTLING, EngineConstants.KING));
				}
			}
			
			// Castling King Side 
			if (castlingRights[side][1] == 1 && (EngineConstants.CASTLING_EMPTY_SQUARES[side][1] & occ) == 0) {
				if (!Check.isKingIncheckIncludingKing(betweenKingAndRook[side][1], bitboard, opSide, side, occ)) {
					addMove(Move.encodeSpecialMove(from, castlingTarget[side][1], EngineConstants.KING_SIDE_CASTLING, EngineConstants.KING));
				}
			}
		}
	}
	
	private void generateQueenMoves(long fromBitboard, long occupiedSquares, long possibleSquares) {
		while (fromBitboard != 0) {
			int from = Long.numberOfTrailingZeros(fromBitboard);
			long toBitboard = MagicBitboard.generateQueenMoves(from, occupiedSquares) & possibleSquares;
			
			while (toBitboard != 0) {
				addMove(Move.encodeMove(from, Long.numberOfTrailingZeros(toBitboard), EngineConstants.QUEEN));
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}
	}
	
	private void generateQueenAttacks(long fromBitboard, long occupiedSquares, long possibleSquares, byte[] pieces) {
		while (fromBitboard != 0) {
			int from = Long.numberOfTrailingZeros(fromBitboard);
			long toBitboard = MagicBitboard.generateQueenMoves(from, occupiedSquares) & possibleSquares;
			
			while (toBitboard != 0) {
				int to = Long.numberOfTrailingZeros(toBitboard);
				addMove(Move.encodeAttackMove(from, to, EngineConstants.PIECE_WITHOUT_SIDE[pieces[to]], EngineConstants.QUEEN));
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}
	}
	
	private void generateRookMoves(long fromBitboard, long occupiedSquares, long possibleSquares) {
		while (fromBitboard != 0) {
			int from = Long.numberOfTrailingZeros(fromBitboard);
			long toBitboard = MagicBitboard.generateRookMoves(from, occupiedSquares) & possibleSquares;
			while (toBitboard != 0) {
				addMove(Move.encodeMove(from, Long.numberOfTrailingZeros(toBitboard), EngineConstants.ROOK));
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}
	}
	
	private void generateRookAttacks(long fromBitboard, long occupiedSquares, long possibleSquares, byte[] pieces) {
		while (fromBitboard != 0) {
			int from = Long.numberOfTrailingZeros(fromBitboard);
			long toBitboard = MagicBitboard.generateRookMoves(from, occupiedSquares) & possibleSquares;
			while (toBitboard != 0) {
				int to = Long.numberOfTrailingZeros(toBitboard);
				addMove(Move.encodeAttackMove(from, to, EngineConstants.PIECE_WITHOUT_SIDE[pieces[to]], EngineConstants.ROOK));
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}
	}
	
	private void generateBishopMoves(long fromBitboard, long occupiedSquares, long possibleSquares) {
		while (fromBitboard != 0) {
			int from = Long.numberOfTrailingZeros(fromBitboard);
			long toBitboard = MagicBitboard.generateBishopMoves(from, occupiedSquares) & possibleSquares;
			while (toBitboard != 0) {
				addMove(Move.encodeMove(from, Long.numberOfTrailingZeros(toBitboard), EngineConstants.BISHOP));
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}
	}
	
	private void generateBishopAttacks(long fromBitboard, long occupiedSquares, long possibleSquares, byte[] pieces) {
		while (fromBitboard != 0) {
			int from = Long.numberOfTrailingZeros(fromBitboard);
			long toBitboard = MagicBitboard.generateBishopMoves(from, occupiedSquares) & possibleSquares;
			while (toBitboard != 0) {
				int to = Long.numberOfTrailingZeros(toBitboard);
				addMove(Move.encodeAttackMove(from, to, EngineConstants.PIECE_WITHOUT_SIDE[pieces[to]], EngineConstants.BISHOP));
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}
	}
	
	private void generateKnightMoves(long fromBitboard, long possibleSquares) {
		while (fromBitboard != 0) {
			int from = Long.numberOfTrailingZeros(fromBitboard);
			long toBitboard = EngineConstants.KNIGHT_LOOKUP[from] & possibleSquares;
			while (toBitboard != 0) {
				addMove(Move.encodeMove(from, Long.numberOfTrailingZeros(toBitboard), EngineConstants.KNIGHT));
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}
	}
	
	private void generateKnightAttacks(long fromBitboard, long possibleSquares, byte[] pieces) {
		while (fromBitboard != 0) {
			int from = Long.numberOfTrailingZeros(fromBitboard);
			long toBitboard = EngineConstants.KNIGHT_LOOKUP[from] & possibleSquares;
			while (toBitboard != 0) {
				int to = Long.numberOfTrailingZeros(toBitboard);
				addMove(Move.encodeAttackMove(from, to, EngineConstants.PIECE_WITHOUT_SIDE[pieces[to]], EngineConstants.KNIGHT));
				toBitboard &= (toBitboard - 1);
			}
			fromBitboard &= (fromBitboard - 1);
		}
	}
	
	private void generatePawnPushes(long fromBitboard, int side, long possibleSquares, long emptySquares) {
		
		if (fromBitboard == 0) {
			return;
		}
		
		switch (side) {
		case EngineConstants.WHITE: {
			long toBitboard = fromBitboard & (possibleSquares >>> 8) & EngineConstants.ROW_MASK_23456;
			while (toBitboard != 0) {
				int from = Long.numberOfTrailingZeros(toBitboard);
				addMove(Move.encodeMove(from, from + 8, EngineConstants.PAWN));
				toBitboard &= (toBitboard - 1);
			}

			toBitboard = fromBitboard & (possibleSquares >>> 16) & EngineConstants.ROW_2;
			while (toBitboard != 0) {
				if ((emptySquares & (Long.lowestOneBit(toBitboard) << 8)) != 0) {
					int from = Long.numberOfTrailingZeros(toBitboard);
					addMove(Move.encodeSpecialMove(from, from + 16, EngineConstants.DOUBLE_PUSH, EngineConstants.PAWN));
				}
				toBitboard &= (toBitboard - 1);
			}
			break;
		}
		case EngineConstants.BLACK: {
			long toBitboard = fromBitboard & (possibleSquares << 8) & EngineConstants.ROW_MASK_34567;
			while (toBitboard != 0) {
				int from = Long.numberOfTrailingZeros(toBitboard);
				addMove(Move.encodeMove(from, from - 8, EngineConstants.PAWN));
				toBitboard &= (toBitboard - 1);
			}

			toBitboard = fromBitboard & (possibleSquares << 16) & EngineConstants.ROW_7;
			while (toBitboard != 0) {
				if ((emptySquares & (Long.lowestOneBit(toBitboard) >>> 8)) != 0) {
					int from = Long.numberOfTrailingZeros(toBitboard);
					addMove(Move.encodeSpecialMove(from, from - 16, EngineConstants.DOUBLE_PUSH, EngineConstants.PAWN));
				}
				toBitboard &= (toBitboard - 1);
			}
			break;
		}
		default:
			throw new IllegalArgumentException();
		}
	}
	
	private void generateOutOfSlidingCheckQuietMoves(IBoard board) {
		long possibleSquares = Utility.LINE[board.getKingSquares()[board.getSide()]][Long.numberOfTrailingZeros(board.getCheckers())];
		if (possibleSquares != 0) {
			long nonPinnedPieces = ~board.getPinnedPieces();
			generateKnightMoves(board.getBitboard()[board.getSide() | EngineConstants.KNIGHT] & nonPinnedPieces, possibleSquares);
			generateBishopMoves(board.getBitboard()[board.getSide() | EngineConstants.BISHOP] & nonPinnedPieces, board.getOccupiedSquares(), possibleSquares);
			generateRookMoves(board.getBitboard()[board.getSide() | EngineConstants.ROOK] & nonPinnedPieces, board.getOccupiedSquares(), possibleSquares);
			generateQueenMoves(board.getBitboard()[board.getSide() | EngineConstants.QUEEN] & nonPinnedPieces, board.getOccupiedSquares(), possibleSquares);
			generatePawnPushes(board.getBitboard()[board.getSide() | EngineConstants.PAWN] & nonPinnedPieces, board.getSide(), possibleSquares, board.getEmptySquares());
		}
		generateKingQuietMoves(board);
	}
	
	private void generateNotInCheckAttacks(IBoard board) {
		
		long enemySquares = board.getOccupiedSquaresBySide()[board.getOpSide()];
		long emptySquares = board.getEmptySquares();
		long nonPinnedPieces = ~board.getPinnedPieces();
		
		// non pinned pieces
		generateEpAttacks(board);
		generatePawnAttacksAndPromotions(board.getBitboard()[board.getSide() | EngineConstants.PAWN] & nonPinnedPieces, board, enemySquares, emptySquares);
		generateKnightAttacks(board.getBitboard()[board.getSide() | EngineConstants.KNIGHT] & nonPinnedPieces, enemySquares, board.getPieces());
		generateRookAttacks(board.getBitboard()[board.getSide() | EngineConstants.ROOK] & nonPinnedPieces, board.getOccupiedSquares(), enemySquares, board.getPieces());
		generateBishopAttacks(board.getBitboard()[board.getSide() | EngineConstants.BISHOP] & nonPinnedPieces, board.getOccupiedSquares(), enemySquares, board.getPieces());
		generateQueenAttacks(board.getBitboard()[board.getSide() | EngineConstants.QUEEN] & nonPinnedPieces, board.getOccupiedSquares(), enemySquares, board.getPieces());
		generateKingAttacks(board);
		
		// pinned pieces
		long fromBitboard = board.getOccupiedSquaresBySide()[board.getSide()] & board.getPinnedPieces();
		while (fromBitboard != 0) {
			int from = Long.numberOfTrailingZeros(fromBitboard);
			
			byte fromPieceWc = (byte)(board.getPieces()[from] & 0XFE);
			switch (fromPieceWc) {
			case EngineConstants.PAWN:
				generatePawnAttacksAndPromotions(Long.lowestOneBit(fromBitboard), board, enemySquares & Utility.PINNED_MOVEMENT[from][board.getKingSquares()[board.getSide()]], 0);
				break;
			case EngineConstants.BISHOP:
				generateBishopAttacks(Long.lowestOneBit(fromBitboard), board.getOccupiedSquares(), enemySquares & Utility.PINNED_MOVEMENT[from][board.getKingSquares()[board.getSide()]], board.getPieces());
				break;
			case EngineConstants.ROOK:
				generateRookAttacks(Long.lowestOneBit(fromBitboard), board.getOccupiedSquares(), enemySquares & Utility.PINNED_MOVEMENT[from][board.getKingSquares()[board.getSide()]], board.getPieces());
				break;
			case EngineConstants.QUEEN:
				generateQueenAttacks(Long.lowestOneBit(fromBitboard), board.getOccupiedSquares(), enemySquares & Utility.PINNED_MOVEMENT[from][board.getKingSquares()[board.getSide()]], board.getPieces());
				break;
			}
			fromBitboard &= (fromBitboard - 1);
		}
	}
	
	private void generateOutOfCheckAttacks(IBoard board) {
		// attack to checker
		long nonPinnedPieces = ~board.getPinnedPieces();
		
		generateEpAttacks(board);
		generatePawnAttacksAndPromotions(board.getBitboard()[board.getSide() | EngineConstants.PAWN] & nonPinnedPieces, board, board.getCheckers(), Utility.LINE[Long.numberOfTrailingZeros(board.getCheckers())][board.getKingSquares()[board.getSide()]]);
		generateKnightAttacks(board.getBitboard()[board.getSide() | EngineConstants.KNIGHT] & nonPinnedPieces, board.getCheckers(), board.getPieces());
		generateBishopAttacks(board.getBitboard()[board.getSide() | EngineConstants.BISHOP] & nonPinnedPieces, board.getOccupiedSquares(), board.getCheckers(), board.getPieces());
		generateRookAttacks(board.getBitboard()[board.getSide() | EngineConstants.ROOK] & nonPinnedPieces, board.getOccupiedSquares(), board.getCheckers(), board.getPieces());
		generateQueenAttacks(board.getBitboard()[board.getSide() | EngineConstants.QUEEN] & nonPinnedPieces, board.getOccupiedSquares(), board.getCheckers(), board.getPieces());
		generateKingAttacks(board);
	}
	
	private void generateEpAttacks(IBoard board) {
		if (board.getEpTarget() == 64) {
			return;
		}
		long fromBitboard = board.getBitboard()[board.getSide() | EngineConstants.PAWN] & EngineConstants.PAWN_ATTACK_LOOKUP[board.getOpSide()][board.getEpTarget()];
		while (fromBitboard != 0) {
			addMove(Move.encodeSpecialAttackMove(Long.numberOfTrailingZeros(fromBitboard), board.getEpTarget(), EngineConstants.EP_CAPTURE, EngineConstants.PAWN, EngineConstants.PAWN));
			fromBitboard &= (fromBitboard - 1);
		}
	}
	
	private void generatePawnAttacksAndPromotions(long pawns, IBoard board, long enemySquares, long emptySquares) {
		
		if (pawns == 0) {
			return;
		}
		
		int side = board.getSide();
		
		switch (side) {
		case EngineConstants.WHITE: {

			// non promotion.
			long fromBitboard = pawns & EngineConstants.RANK_NON_PROMOTION[EngineConstants.WHITE] & getBlackPawnAttacks(enemySquares);
			while (fromBitboard != 0) {
				int from = Long.numberOfTrailingZeros(fromBitboard);
				long toBitboard = EngineConstants.PAWN_ATTACK_LOOKUP[EngineConstants.WHITE][from] & enemySquares;
				while (toBitboard != 0) {
					int to = Long.numberOfTrailingZeros(toBitboard);
					addMove(Move.encodeAttackMove(from, to, EngineConstants.PIECE_WITHOUT_SIDE[board.getPieces()[to]], EngineConstants.PAWN));
					toBitboard &= (toBitboard - 1);
				}
				fromBitboard &= (fromBitboard - 1);
			}
			
			// promotion.
			long promotionPawns = pawns & EngineConstants.RANK_PROMOTION[EngineConstants.WHITE];
			while (promotionPawns != 0) {
				int from = Long.numberOfTrailingZeros(promotionPawns);
				
				if (((Long.lowestOneBit(promotionPawns) << 8) & emptySquares) != 0) {
					generatePromotions(from, from + 8, side);
				}
				
				// promotion attacks
				generatePromotionAttacks(from, EngineConstants.PAWN_ATTACK_LOOKUP[EngineConstants.WHITE][from] & enemySquares, side, board.getPieces());
				
				promotionPawns &= (promotionPawns - 1);
			}
			
			break;
		}
		case EngineConstants.BLACK: {
			// non promotion.
			long fromBitboard = pawns & EngineConstants.RANK_NON_PROMOTION[EngineConstants.BLACK] & getWhitePawnAttacks(enemySquares);
			while (fromBitboard != 0) {
				int from = Long.numberOfTrailingZeros(fromBitboard);
				long toBitboard = EngineConstants.PAWN_ATTACK_LOOKUP[EngineConstants.BLACK][from] & enemySquares;
				while (toBitboard != 0) {
					int to = Long.numberOfTrailingZeros(toBitboard);
					addMove(Move.encodeAttackMove(from, to, EngineConstants.PIECE_WITHOUT_SIDE[board.getPieces()[to]], EngineConstants.PAWN));
					toBitboard &= (toBitboard - 1);
				}
				fromBitboard &= (fromBitboard - 1);
			}
			
			// promotion.
			long promotionPawns = pawns & EngineConstants.RANK_PROMOTION[EngineConstants.BLACK];
			while (promotionPawns != 0) {
				int from = Long.numberOfTrailingZeros(promotionPawns);
				
				if (((Long.lowestOneBit(promotionPawns) >>> 8) & emptySquares) != 0) {
					generatePromotions(from, from - 8, side);
				}
				
				// promotion attacks
				generatePromotionAttacks(from, EngineConstants.PAWN_ATTACK_LOOKUP[EngineConstants.BLACK][from] & enemySquares, side, board.getPieces());
				
				promotionPawns &= (promotionPawns - 1);
			}
			break;
		}
		default:
			throw new IllegalArgumentException();
		}
	}
	
	private void generatePromotions(int from, int to, int side) {
		addMove(Move.encodePromotionMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.QUEEN));
		if (allowUnderPromotion) {
			addMove(Move.encodePromotionMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.KNIGHT));
			addMove(Move.encodePromotionMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.BISHOP));
			addMove(Move.encodePromotionMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.ROOK));
		}
	}
	
	private void generatePromotionAttacks(int from, long toBitboard, int side, byte[] pieces) {
		while (toBitboard != 0) {
			int to = Long.numberOfTrailingZeros(toBitboard);
			addMove(Move.encodePromotionAttackMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.QUEEN, EngineConstants.PIECE_WITHOUT_SIDE[pieces[to]]));
			if (allowUnderPromotion) {
				addMove(Move.encodePromotionAttackMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.KNIGHT, EngineConstants.PIECE_WITHOUT_SIDE[pieces[to]]));
				addMove(Move.encodePromotionAttackMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.BISHOP, EngineConstants.PIECE_WITHOUT_SIDE[pieces[to]]));
				addMove(Move.encodePromotionAttackMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.ROOK, EngineConstants.PIECE_WITHOUT_SIDE[pieces[to]]));
			}	
			toBitboard &= (toBitboard - 1);
		}
	}
	
	public static long getBlackPawnAttacks(long blackPawns) {
		return (blackPawns >>> 9 & ~EngineConstants.FILE_H) | (blackPawns >>> 7 & ~EngineConstants.FILE_A);
	}
	
	public static long getWhitePawnAttacks(long whitePawns) {
		return (whitePawns << 9 & ~EngineConstants.FILE_A) | (whitePawns << 7 & ~EngineConstants.FILE_H);
	}
	
}

package chess.movegen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chess.engine.EngineConstants;
import chess.engine.IBoard;
import chess.engine.LegalityV4;
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
		List<Integer> moveList = new ArrayList<Integer>();
		int origNextToMove = nextToMove[currentPly];
		while (hasNext()) {
			moveList.add(next());
		}
		Collections.sort(moveList);
		nextToMove[currentPly] = origNextToMove;
		
		for (Integer move : moveList) {
			moves[nextToMove[currentPly]++] = move;
		}
		
		nextToMove[currentPly] = origNextToMove;
	}
	
	public void addMove(int move){
		moves[nextToGenerate[currentPly]++] = move;
	}
	
	public void generateMoves(IBoard board) {
		switch (Long.bitCount(board.getCheckers())) {
		case 0:
			generateNotInCheckQuietMoves(board);
			break;
		case 1:
			switch ((byte)(board.getPieces()[Long.numberOfTrailingZeros(board.getCheckers())] & 0XFE)) {
			case EngineConstants.PAWN:
			case EngineConstants.KNIGHT:
				generateKingQuietMoves(board);
				break;
			default:
				generateOutOfSlidingCheckQuietMoves(board);
			}
			break;
		default:
			generateKingQuietMoves(board);
			break;
		}
	}
	
	public void generateAttacks(IBoard board) {
		switch (Long.bitCount(board.getCheckers())) {
		case 0:
			// not in-check
			generateNotInCheckAttacks(board);
			break;
		case 1:
			generateOutOfCheckAttacks(board);
			break;
		default:
			// double check, only the king can attack
			generateKingAttacks(board);
			break;
		}
	}
	
	private void generateNotInCheckQuietMoves(IBoard board) {
		
		// non-pinned pieces
		generateKingQuietMoves(board);
		generateQueenMoves(board.getBitboard()[board.getSide() | EngineConstants.QUEEN] & ~board.getPinnedPieces(), board.getOccupiedSquares(), board.getEmptySquares());
		generateRookMoves(board.getBitboard()[board.getSide() | EngineConstants.ROOK] & ~board.getPinnedPieces(), board.getOccupiedSquares(), board.getEmptySquares());
		generateBishopMoves(board.getBitboard()[board.getSide() | EngineConstants.BISHOP] & ~board.getPinnedPieces(), board.getOccupiedSquares(), board.getEmptySquares());
		generateKnightMoves(board.getBitboard()[board.getSide() | EngineConstants.KNIGHT] & ~board.getPinnedPieces(), board.getEmptySquares());
		generatePawnPushes(board.getBitboard()[board.getSide() | EngineConstants.PAWN] & ~board.getPinnedPieces(), board.getSide(), board.getEmptySquares(), board.getEmptySquares());
		
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
			addMove(Move.encodeAttackMove(from, to, board.getPieces()[to]));
			toBitboard &= (toBitboard - 1);
		}
	}
	
	private void generateKingQuietMoves(IBoard board) {
		int from = board.getKingSquares()[board.getSide()];
		long toBitboard = EngineConstants.KING_LOOKUP[from] & board.getEmptySquares();
		while (toBitboard != 0) {
			addMove(Move.encodeMove(from, Long.numberOfTrailingZeros(toBitboard)));
			toBitboard &= (toBitboard - 1);
		}
		
		int to;
		int side = board.getSide();
		long[] bitboard = board.getBitboard();
		long fromBitboard;
		long emptySquares = board.getEmptySquares();
		byte[][] castlingRights = board.getCastlingRights();
		
		// Castling Queen Side
		if (board.getCheckers() == 0) {
			fromBitboard = bitboard[side | EngineConstants.KING];
			if ((from = Long.numberOfTrailingZeros(fromBitboard)) != 64) {
				toBitboard = (castlingRights[side][0] & (emptySquares >>> castlingShift[side][0][0]) 
						& (emptySquares >>> castlingShift[side][0][1])
						& (emptySquares >>> castlingShift[side][0][2])) << castlingTarget[side][0];
				if ((to = Long.numberOfTrailingZeros(toBitboard)) != 64) {
					byte sideToKing = (byte)(side| EngineConstants.KING);
					int kingOriginalPos = kingPositions[side];
					int squareBetweenKingAndRook = betweenKingAndRook[side][0];
					bitboard[sideToKing] &= ~(1L << kingOriginalPos);
					bitboard[sideToKing] |= (1L << squareBetweenKingAndRook);
					if(!legality.isKingInCheck(bitboard, side)){
						addMove(Move.encodeMove(from, to, EngineConstants.QUEEN_SIDE_CASTLING));
					}
					bitboard[sideToKing] &= ~(1L << squareBetweenKingAndRook);
					bitboard[sideToKing] |= (1L << kingOriginalPos);
				}
			}
			
			// Castling King Side 
			fromBitboard = bitboard[side | EngineConstants.KING];
			if ((from = Long.numberOfTrailingZeros(fromBitboard)) != 64) {
				toBitboard = (castlingRights[side][1] & (emptySquares >>> castlingShift[side][1][0]) 
						& (emptySquares >>> castlingShift[side][1][1])) << castlingTarget[side][1];
				if ((to = Long.numberOfTrailingZeros(toBitboard)) != 64) {
					byte sideToKing = (byte)(side| EngineConstants.KING);
					int kingOriginalPos = kingPositions[side];
					int squareBetweenKingAndRook = betweenKingAndRook[side][1];
					bitboard[sideToKing] &= ~(1L << kingOriginalPos);
					bitboard[sideToKing] |= (1L << squareBetweenKingAndRook);
					if(!legality.isKingInCheck(bitboard, side)){
						addMove(Move.encodeMove(from, to, EngineConstants.KING_SIDE_CASTLING));
					}
					bitboard[sideToKing] &= ~(1L << squareBetweenKingAndRook);
					bitboard[sideToKing] |= (1L << kingOriginalPos);
				}
			}
		}
	}
	
	private void generateQueenMoves(long fromBitboard, long occupiedSquares, long possibleSquares) {
		while (fromBitboard != 0) {
			int from = Long.numberOfTrailingZeros(fromBitboard);
			long toBitboard = MagicBitboard.generateQueenMoves(from, occupiedSquares) & possibleSquares;
			
			while (toBitboard != 0) {
				addMove(Move.encodeMove(from, Long.numberOfTrailingZeros(toBitboard)));
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
				addMove(Move.encodeAttackMove(from, to, pieces[to]));
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
				addMove(Move.encodeMove(from, Long.numberOfTrailingZeros(toBitboard)));
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
				addMove(Move.encodeAttackMove(from, to, pieces[to]));
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
				addMove(Move.encodeMove(from, Long.numberOfTrailingZeros(toBitboard)));
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
				addMove(Move.encodeAttackMove(from, to, pieces[to]));
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
				addMove(Move.encodeMove(from, Long.numberOfTrailingZeros(toBitboard)));
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
				addMove(Move.encodeAttackMove(from, to, pieces[to]));
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
				addMove(Move.encodeMove(from, from + 8));
				toBitboard &= (toBitboard - 1);
			}

			toBitboard = fromBitboard & (possibleSquares >>> 16) & EngineConstants.ROW_2;
			while (toBitboard != 0) {
				if ((emptySquares & (Long.lowestOneBit(toBitboard) << 8)) != 0) {
					int from = Long.numberOfTrailingZeros(toBitboard);
					addMove(Move.encodeMove(from, from + 16, EngineConstants.DOUBLE_PUSH));
				}
				toBitboard &= (toBitboard - 1);
			}
			break;
		}
		case EngineConstants.BLACK: {
			long toBitboard = fromBitboard & (possibleSquares << 8) & EngineConstants.ROW_MASK_34567;
			while (toBitboard != 0) {
				int from = Long.numberOfTrailingZeros(toBitboard);
				addMove(Move.encodeMove(from, from - 8));
				toBitboard &= (toBitboard - 1);
			}

			toBitboard = fromBitboard & (possibleSquares << 16) & EngineConstants.ROW_7;
			while (toBitboard != 0) {
				if ((emptySquares & (Long.lowestOneBit(toBitboard) >>> 8)) != 0) {
					int from = Long.numberOfTrailingZeros(toBitboard);
					addMove(Move.encodeMove(from, from - 16, EngineConstants.DOUBLE_PUSH));
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
			generateKnightMoves(board.getBitboard()[board.getSide() | EngineConstants.KNIGHT] & ~board.getPinnedPieces(), possibleSquares);
			generateBishopMoves(board.getBitboard()[board.getSide() | EngineConstants.BISHOP] & ~board.getPinnedPieces(), board.getOccupiedSquares(), possibleSquares);
			generateRookMoves(board.getBitboard()[board.getSide() | EngineConstants.ROOK] & ~board.getPinnedPieces(), board.getOccupiedSquares(), possibleSquares);
			generateQueenMoves(board.getBitboard()[board.getSide() | EngineConstants.QUEEN] & ~board.getPinnedPieces(), board.getOccupiedSquares(), possibleSquares);
			generatePawnPushes(board.getBitboard()[board.getSide() | EngineConstants.PAWN] & ~board.getPinnedPieces(), board.getSide(), possibleSquares, board.getEmptySquares());
		}
		generateKingQuietMoves(board);
	}
	
	private void generateNotInCheckAttacks(IBoard board) {
		
		long enemySquares = board.getOccupiedSquaresBySide()[board.getOpSide()];
		long emptySquares = board.getEmptySquares();
		
		// non pinned pieces
		generateEpAttacks(board);
		generatePawnAttacksAndPromotions(board.getBitboard()[board.getSide() | EngineConstants.PAWN] & ~board.getPinnedPieces(), board, enemySquares, emptySquares);
		generateKnightAttacks(board.getBitboard()[board.getSide() | EngineConstants.KNIGHT] & ~board.getPinnedPieces(), enemySquares, board.getPieces());
		generateRookAttacks(board.getBitboard()[board.getSide() | EngineConstants.ROOK] & ~board.getPinnedPieces(), board.getOccupiedSquares(), enemySquares, board.getPieces());
		generateBishopAttacks(board.getBitboard()[board.getSide() | EngineConstants.BISHOP] & ~board.getPinnedPieces(), board.getOccupiedSquares(), enemySquares, board.getPieces());
		generateQueenAttacks(board.getBitboard()[board.getSide() | EngineConstants.QUEEN] & ~board.getPinnedPieces(), board.getOccupiedSquares(), enemySquares, board.getPieces());
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
		// TODO: must not be pinned. And epSquare must be checker!
		generateEpAttacks(board);
		// TODO: prevent promotion move here.
//		generatePawnAttacksAndPromotions(board.getBitboard()[board.getSide() | EngineConstants.PAWN] & ~board.getPinnedPieces(), board, board.getCheckers(), board.getEmptySquares());
//		generatePawnAttacksAndPromotions(board.getBitboard()[board.getSide() | EngineConstants.PAWN] & ~board.getPinnedPieces(), board, board.getCheckers(), 0L);
		generatePawnAttacksAndPromotions(board.getBitboard()[board.getSide() | EngineConstants.PAWN] & ~board.getPinnedPieces(), board, board.getCheckers(), Utility.LINE[Long.numberOfTrailingZeros(board.getCheckers())][board.getKingSquares()[board.getSide()]]);
		generateKnightAttacks(board.getBitboard()[board.getSide() | EngineConstants.KNIGHT] & ~board.getPinnedPieces(), board.getCheckers(), board.getPieces());
		generateBishopAttacks(board.getBitboard()[board.getSide() | EngineConstants.BISHOP] & ~board.getPinnedPieces(), board.getOccupiedSquares(), board.getCheckers(), board.getPieces());
		generateRookAttacks(board.getBitboard()[board.getSide() | EngineConstants.ROOK] & ~board.getPinnedPieces(), board.getOccupiedSquares(), board.getCheckers(), board.getPieces());
		generateQueenAttacks(board.getBitboard()[board.getSide() | EngineConstants.QUEEN] & ~board.getPinnedPieces(), board.getOccupiedSquares(), board.getCheckers(), board.getPieces());
		generateKingAttacks(board);
	}
	
	private void generateEpAttacks(IBoard board) {
		if (board.getEpTarget() == 64) {
			return;
		}
		long fromBitboard = board.getBitboard()[board.getSide() | EngineConstants.PAWN] & EngineConstants.PAWN_ATTACK_LOOKUP[board.getOpSide()][board.getEpTarget()];
		while (fromBitboard != 0) {
			addMove(Move.encodeAttackMove(Long.numberOfTrailingZeros(fromBitboard), board.getEpTarget(), EngineConstants.EP_CAPTURE, EngineConstants.PAWN | board.getOpSide()));
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
					addMove(Move.encodeAttackMove(from, to, board.getPieces()[to]));
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
					addMove(Move.encodeAttackMove(from, to, board.getPieces()[to]));
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
		addMove(Move.encodeMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.QUEEN));
		if (allowUnderPromotion) {
			addMove(Move.encodeMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.KNIGHT));
			addMove(Move.encodeMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.BISHOP));
			addMove(Move.encodeMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.ROOK));
		}
	}
	
	private void generatePromotionAttacks(int from, long toBitboard, int side, byte[] pieces) {
		while (toBitboard != 0) {
			int to = Long.numberOfTrailingZeros(toBitboard);
			addMove(Move.encodeAttackMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.QUEEN, pieces[to]));
			if (allowUnderPromotion) {
				addMove(Move.encodeAttackMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.KNIGHT, pieces[to]));
				addMove(Move.encodeAttackMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.BISHOP, pieces[to]));
				addMove(Move.encodeAttackMove(from, to, EngineConstants.PROMOTION, side | EngineConstants.ROOK, pieces[to]));
			}	
			toBitboard &= (toBitboard - 1);
		}
	}
	
	private long getBlackPawnAttacks(long blackPawns) {
		return (blackPawns >>> 9 & ~EngineConstants.FILE_H) | (blackPawns >>> 7 & ~EngineConstants.FILE_A);
	}
	
	private long getWhitePawnAttacks(long whitePawns) {
		return (whitePawns << 9 & ~EngineConstants.FILE_A) | (whitePawns << 7 & ~EngineConstants.FILE_H);
	}
	
	private LegalityV4 legality = new LegalityV4();
	
	//TODO : try direct access ep target and side instead of pass parameter. and compare performances..
	public void generateAllMoves(IBoard board) {
		
		long[] bitboard = board.getBitboard();
		int side = board.getSide();
		int epTarget = board.getEpTarget();
		byte[][] castlingRights = board.getCastlingRights();
		long occupiedSquares = board.getOccupiedSquares();
		long emptySquares = board.getEmptySquares();
		long enemySquares = board.getOccupiedSquaresBySide()[board.getOpSide()];
		long enemyAndEmptySquares = emptySquares | enemySquares;
		
		int diff;
		long toBitboard;
		long fromBitboard;
		long singlePushes;
		int to;
		int from;
		int move;

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
			addMove(move);
			toBitboard = toBitboard & ~(1L << to);
		}

		// Double Pushes
		toBitboard = (((singlePushes & doublePushMask[side]) << diff)
				| ((singlePushes & doublePushMask[side]) >>> (64 - diff))) & emptySquares;
		while (toBitboard != 0) {
			to = Long.numberOfTrailingZeros(toBitboard);
			from = (((to - diff - diff) % 64) + 64) % 64;
			move = from | (to << 8) | (EngineConstants.DOUBLE_PUSH << 16);
			addMove(move);
			toBitboard = toBitboard & ~(1L << to);
		}
		
		// TODO : Maybe promotions should be handled before pawn pushes in order to reduce branching factor by alpha-beta cutoffs. 
		// Pawn Promotions
		toBitboard=singlePushes&promotionMask[side];
		while(toBitboard != 0){
			to=Long.numberOfTrailingZeros(toBitboard);
			from=(((to-diff)%64)+64)%64;

			//Queen Promotions
			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.QUEEN) 	<< 20);
			addMove(move);
			//Rook Promotions
			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.ROOK) 	<< 20);
			addMove(move);
			//Bishop Promotions
			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.BISHOP) 	<< 20);
			addMove(move);
			//Knight Promotions
			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.KNIGHT) 	<< 20);
			addMove(move);
			
			toBitboard=toBitboard & ~(1L << to);
		}

		// PAWN ATTACKS
		for (int dir = 0; dir < 2; dir++) {
			diff = attackDiffs[dir][side];

			// Pure Pawn Attacks
			toBitboard = ((fromBitboard << diff) | (fromBitboard >>> (64 - diff))) & fileMask[dir] & enemySquares & (~promotionMask[side]);
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				from = (((to - diff) % 64) + 64) % 64;
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			
			// En-Passant Capture
			if(epTarget != 64){
				toBitboard=((fromBitboard<<diff) | (fromBitboard>>>(64-diff))) & fileMask[dir] & ( 1L << epTarget );
				while(toBitboard != 0){
					to=Long.numberOfTrailingZeros(toBitboard);
					from=(((to-diff)%64)+64)%64;
					move = from | (to << 8) | (EngineConstants.EP_CAPTURE << 16);
					addMove(move);
					toBitboard=toBitboard & ~(1L << to);
				}
			}
			
			// Promotion Attacks
			toBitboard= ((fromBitboard << diff) | (fromBitboard >>> (64 - diff))) & fileMask[dir] & enemySquares & promotionMask[side];
			while(toBitboard != 0){
				to=Long.numberOfTrailingZeros(toBitboard);
				from=(((to-diff)%64)+64)%64;
				
				//Queen Promotions
				move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.QUEEN) 	<< 20);
				addMove(move);
				//Rook Promotions
				move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.ROOK) 	<< 20);
				addMove(move);
				//Bishop Promotions
				move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.BISHOP) 	<< 20);
				addMove(move);
				//Knight Promotions
				move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.KNIGHT) 	<< 20);
				addMove(move);
				
				toBitboard=toBitboard & ~(1L << to);
			}
		}

		// KNIGHT ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.KNIGHT];
		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = EngineConstants.KNIGHT_LOOKUP[from] & enemyAndEmptySquares;
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// KING ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.KING];
		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = EngineConstants.KING_LOOKUP[from] & enemyAndEmptySquares;
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// ROOK ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.ROOK];

		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = MagicBitboard.generateRookMoves(from, occupiedSquares) & enemyAndEmptySquares;
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// BISHOP ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.BISHOP];
		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = MagicBitboard.generateBishopMoves(from, occupiedSquares) & enemyAndEmptySquares;
			
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// QUEEN ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.QUEEN];

		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = MagicBitboard.generateQueenMoves(from, occupiedSquares) & enemyAndEmptySquares;
			
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
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
						addMove(move);
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
						addMove(move);
					}
					bitboard[sideToKing] &= ~(1L << squareBetweenKingAndRook);
					bitboard[sideToKing] |= (1L << kingOriginalPos);
				}
			}
		}
	}
	
	//TODO : try direct access ep target and side instead of pass parameter. and compare performances..
	public void generateAttackMoves(IBoard board) {
		
		long[] bitboard = board.getBitboard();
		int side = board.getSide();
		int epTarget = board.getEpTarget();
		long occupiedSquares = board.getOccupiedSquares();
		long enemySquares = board.getOccupiedSquaresBySide()[board.getOpSide()];
		long emptySquares = board.getEmptySquares();
		
		int diff;
		long toBitboard;
		long fromBitboard;
		int to;
		int from;
		int move;
		
		// PAWNS
		fromBitboard = bitboard[side | EngineConstants.PAWN];
		
		diff = pushDiffs[side];
		toBitboard = ((fromBitboard << diff) | (fromBitboard >>> (64 - diff))) & emptySquares;
		long singlePushes = toBitboard; // will be reused.
		
		// TODO : Maybe promotions should be handled before pawn pushes in order to reduce branching factor by alpha-beta cutoffs. 
		// Pawn Promotions
		toBitboard=singlePushes&promotionMask[side];
		while(toBitboard != 0){
			to=Long.numberOfTrailingZeros(toBitboard);
			from=(((to-diff)%64)+64)%64;

			//Queen Promotions
			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.QUEEN) 	<< 20);
			addMove(move);
			//Rook Promotions
			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.ROOK) 	<< 20);
			addMove(move);
			//Bishop Promotions
			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.BISHOP) 	<< 20);
			addMove(move);
			//Knight Promotions
			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.KNIGHT) 	<< 20);
			addMove(move);

			toBitboard=toBitboard & ~(1L << to);
		}

		// PAWN ATTACKS
		for (int dir = 0; dir < 2; dir++) {
			diff = attackDiffs[dir][side];

			// Pure Pawn Attacks
			toBitboard = ((fromBitboard << diff) | (fromBitboard >>> (64 - diff))) & fileMask[dir] & enemySquares & (~promotionMask[side]);
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				from = (((to - diff) % 64) + 64) % 64;
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			
			// En-Passant Capture
			if(epTarget != 64){
				toBitboard=((fromBitboard<<diff) | (fromBitboard>>>(64-diff))) & fileMask[dir] & ( 1L << epTarget );
				while(toBitboard != 0){
					to=Long.numberOfTrailingZeros(toBitboard);
					from=(((to-diff)%64)+64)%64;
					move = from | (to << 8) | (EngineConstants.EP_CAPTURE << 16);
					addMove(move);
					toBitboard=toBitboard & ~(1L << to);
				}
			}
			
			// Promotion Attacks
			toBitboard= ((fromBitboard << diff) | (fromBitboard >>> (64 - diff))) & fileMask[dir] & enemySquares & promotionMask[side];
			while(toBitboard != 0){
				to=Long.numberOfTrailingZeros(toBitboard);
				from=(((to-diff)%64)+64)%64;
				
				//Queen Promotions
				move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.QUEEN) 	<< 20);
				addMove(move);
				//Rook Promotions
				move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.ROOK) 	<< 20);
				addMove(move);
				//Bishop Promotions
				move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.BISHOP) 	<< 20);
				addMove(move);
				//Knight Promotions
				move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.KNIGHT) 	<< 20);
				addMove(move);
				
				toBitboard=toBitboard & ~(1L << to);
			}
		}

		// KNIGHT ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.KNIGHT];
		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = EngineConstants.KNIGHT_LOOKUP[from] & enemySquares;
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// KING ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.KING];
		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = EngineConstants.KING_LOOKUP[from] & enemySquares;
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// ROOK ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.ROOK];

		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = MagicBitboard.generateRookMoves(from, occupiedSquares) & enemySquares;
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// BISHOP ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.BISHOP];
		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = MagicBitboard.generateBishopMoves(from, occupiedSquares) & enemySquares;
			
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// QUEEN ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.QUEEN];

		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = MagicBitboard.generateQueenMoves(from, occupiedSquares) & enemySquares;
			
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}
		
	}
	
	public void generateQuietMoves(IBoard board) {
		
		long[] bitboard = board.getBitboard();
		int side = board.getSide();
		byte[][] castlingRights = board.getCastlingRights();
		long occupiedSquares = board.getOccupiedSquares();
		long emptySquares = board.getEmptySquares();
		
		int diff;
		long toBitboard;
		long fromBitboard;
		long singlePushes;
		int to;
		int from;
		int move;

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
			addMove(move);
			toBitboard = toBitboard & ~(1L << to);
		}

		// Double Pushes
		toBitboard = (((singlePushes & doublePushMask[side]) << diff)
				| ((singlePushes & doublePushMask[side]) >>> (64 - diff))) & emptySquares;
		while (toBitboard != 0) {
			to = Long.numberOfTrailingZeros(toBitboard);
			from = (((to - diff - diff) % 64) + 64) % 64;
			move = from | (to << 8) | (EngineConstants.DOUBLE_PUSH << 16);
			addMove(move);
			toBitboard = toBitboard & ~(1L << to);
		}
		
//		// TODO : Maybe promotions should be handled before pawn pushes in order to reduce branching factor by alpha-beta cutoffs. 
//		// Pawn Promotions
//		toBitboard=singlePushes&promotionMask[side];
//		while(toBitboard != 0){
//			to=Long.numberOfTrailingZeros(toBitboard);
//			from=(((to-diff)%64)+64)%64;
//
//			//Queen Promotions
//			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.QUEEN) 	<< 20);
//			addMove(move);
//			//Rook Promotions
//			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.ROOK) 	<< 20);
//			addMove(move);
//			//Bishop Promotions
//			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.BISHOP) 	<< 20);
//			addMove(move);
//			//Knight Promotions
//			move = from | (to << 8) | (EngineConstants.PROMOTION << 16) | ((side|(int)EngineConstants.KNIGHT) 	<< 20);
//			addMove(move);
//			
//			toBitboard=toBitboard & ~(1L << to);
//		}

		// KNIGHT ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.KNIGHT];
		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = EngineConstants.KNIGHT_LOOKUP[from] & emptySquares;
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// KING ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.KING];
		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = EngineConstants.KING_LOOKUP[from] & emptySquares;
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// ROOK ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.ROOK];

		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = MagicBitboard.generateRookMoves(from, occupiedSquares) & emptySquares;
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// BISHOP ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.BISHOP];
		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = MagicBitboard.generateBishopMoves(from, occupiedSquares) & emptySquares;
			
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
				toBitboard = toBitboard & ~(1L << to);
			}
			fromBitboard = fromBitboard & ~(1L << from);
		}

		// QUEEN ATTACKS.
		fromBitboard = bitboard[side | EngineConstants.QUEEN];

		while (fromBitboard != 0) {
			from = Long.numberOfTrailingZeros(fromBitboard);
			toBitboard = MagicBitboard.generateQueenMoves(from, occupiedSquares) & emptySquares;
			
			while (toBitboard != 0) {
				to = Long.numberOfTrailingZeros(toBitboard);
				move = from | (to << 8);
				addMove(move);
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
						addMove(move);
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
						addMove(move);
					}
					bitboard[sideToKing] &= ~(1L << squareBetweenKingAndRook);
					bitboard[sideToKing] |= (1L << kingOriginalPos);
				}
			}
		}
	}

}

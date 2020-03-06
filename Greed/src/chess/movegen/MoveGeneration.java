package chess.movegen;

import chess.engine.EngineConstants;
import chess.engine.IBoard;
import chess.engine.LegalityV4;
import chess.engine.Move;
import chess.engine.MoveGenerationConstants;
import chess.engine.PrecalculatedAttackTables;
import chess.util.Utility;

// https://github.com/sandermvdb/chess22k
public class MoveGeneration implements MoveGenerationConstants {
	
	private int[] moves = new int[1500];
	private final int[] moveScores = new int[1500];
	private int[] nextToGenerate = new int[MAX_PLIES * 2];
	private int[] nextToMove = new int[MAX_PLIES * 2];
	
	private int currentPly;
	
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
	
	public void addMove(int move){
		moves[nextToGenerate[currentPly]++] = move;
	}
	
	public void generateQuietMoves(IBoard board) {
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
	
	public void generateAttackMoves(IBoard board) {
		switch (Long.bitCount(board.getCheckers())) {
		case 0:
			break;
		case 1:
			break;
		default:
			break;
		}
	}
	
	public void generateNotInCheckQuietMoves(IBoard board) {
		
		// non-pinned pieces
		generateKingQuietMoves(board);
		generateQueenMoves(board.getBitboard()[board.getSide() | EngineConstants.QUEEN] & ~board.getPinnedPieces(), board.getOccupiedSquares(), board.getEmptySquares());
		generateRookMoves(board.getBitboard()[board.getSide() | EngineConstants.ROOK] & ~board.getPinnedPieces(), board.getOccupiedSquares(), board.getEmptySquares());
		generateBishopMoves(board.getBitboard()[board.getSide() | EngineConstants.BISHOP] & ~board.getPinnedPieces(), board.getOccupiedSquares(), board.getEmptySquares());
		generateKnightMoves(board.getBitboard()[board.getSide() | EngineConstants.KNIGHT] & ~board.getPinnedPieces(), board.getEmptySquares());
		generatePawnPushes(board.getBitboard()[board.getSide() | EngineConstants.PAWN] & ~board.getPinnedPieces(), board.getSide(), board.getEmptySquares());
		
		// pinned pieces
		long pinnedPieces = board.getOccupiedSquaresBySide()[board.getSide()] & board.getPinnedPieces();
		while (pinnedPieces != 0) {
			byte pieceWc = (byte)(board.getPieces()[Long.numberOfTrailingZeros(pinnedPieces)] & 0XFE);
			switch (pieceWc) {
			case EngineConstants.PAWN:
				generatePawnPushes(Long.lowestOneBit(pinnedPieces), board.getSide(), board.getEmptySquares() & Utility.PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinnedPieces)][board.getKingSquares()[board.getSide()]]);
				break;
			case EngineConstants.BISHOP:
				generateBishopMoves(Long.lowestOneBit(pinnedPieces), board.getOccupiedSquares(), board.getEmptySquares() & Utility.PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinnedPieces)][board.getKingSquares()[board.getSide()]]);
				break;
			case EngineConstants.ROOK:
				generateRookMoves(Long.lowestOneBit(pinnedPieces), board.getOccupiedSquares(), board.getEmptySquares() & Utility.PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinnedPieces)][board.getKingSquares()[board.getSide()]]);
				break;
			case EngineConstants.QUEEN:
				generateQueenMoves(Long.lowestOneBit(pinnedPieces), board.getOccupiedSquares(), board.getEmptySquares() & Utility.PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinnedPieces)][board.getKingSquares()[board.getSide()]]);
				break;
			}
			pinnedPieces &= (pinnedPieces - 1);
		}
	}
	
	public void generateKingQuietMoves(IBoard board) {
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
	
	private void generatePawnPushes(long fromBitboard, int side, long possibleSquares) {
		
		if (fromBitboard == 0) {
			return;
		}
		
		switch (side) {
		case EngineConstants.WHITE: {
			long toBitboard = fromBitboard & (possibleSquares >>> 8) & EngineConstants.ROW_MASK_23456;
			long singlePushes = toBitboard;
			while (toBitboard != 0) {
				int from = Long.numberOfTrailingZeros(toBitboard);
				addMove(Move.encodeMove(from, from + 8));
				toBitboard &= (toBitboard - 1);
			}

			toBitboard = singlePushes & (possibleSquares >>> 16) & EngineConstants.ROW_2;
			while (toBitboard != 0) {
				int from = Long.numberOfTrailingZeros(toBitboard);
				addMove(Move.encodeMove(from, from + 16, EngineConstants.DOUBLE_PUSH));
				toBitboard &= (toBitboard - 1);
			}
			break;
		}
		case EngineConstants.BLACK: {
			long toBitboard = fromBitboard & (possibleSquares << 8) & EngineConstants.ROW_MASK_34567;
			long singlePushes = toBitboard;
			while (toBitboard != 0) {
				int from = Long.numberOfTrailingZeros(toBitboard);
				addMove(Move.encodeMove(from, from - 8));
				toBitboard &= (toBitboard - 1);
			}

			toBitboard = singlePushes & (possibleSquares << 16) & EngineConstants.ROW_7;
			while (toBitboard != 0) {
				int from = Long.numberOfTrailingZeros(toBitboard);
				addMove(Move.encodeMove(from, from - 16, EngineConstants.DOUBLE_PUSH));
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
			generatePawnPushes(board.getBitboard()[board.getSide() | EngineConstants.PAWN] & ~board.getPinnedPieces(), board.getSide(), possibleSquares);
		}
		generateKingQuietMoves(board);
	}
	
	public void generateNotInCheckAttackMoves(IBoard board) {
		
		long enemySquares = board.getOccupiedSquaresBySide()[board.getOpSide()];
		
		// non pinned pieces
		generateEpAttacks(board);
	}
	
	private void generateEpAttacks(IBoard board) {
		if (board.getEpTarget() == 64) {
			return;
		}
		long fromBitboard = board.getBitboard()[board.getSide() | EngineConstants.PAWN] & EngineConstants.PAWN_ATTACK_LOOKUP[board.getOpSide()][board.getEpTarget()];
		while (fromBitboard != 0) {
			addMove(Move.encodeMove(Long.numberOfTrailingZeros(fromBitboard), board.getEpTarget(), EngineConstants.EP_CAPTURE));
			fromBitboard &= (fromBitboard - 1);
		}
	}
	
	private void generatePawnAttacksAndPromotions(long pawns, IBoard board) {
		
		if (pawns == 0) {
			return;
		}
		
		switch (board.getSide()) {
		case EngineConstants.WHITE: {

			// non promotion.
//			long fromBitboard = pawns & EngineConstants.RANK_NON_PROMOTION & 
			
			break;
		}
		case EngineConstants.BLACK: {
			break;
		}
		default:
			throw new IllegalArgumentException();
		}
	}
	
	private long getBlackPawnAttacks(long blackPawns) {
		return (blackPawns >>> 9 & ~EngineConstants.FILE_H) | (blackPawns >>> 7 & ~EngineConstants.FILE_A);
//		SDF
	}
	
	private long getWhitePawnAttacks(long whitePawns) {
		return (whitePawns << 9 & ~EngineConstants.FILE_A) | (whitePawns << 7 & ~EngineConstants.FILE_H);
	}
	
	private LegalityV4 legality = new LegalityV4();
	
	public void generateMoves(IBoard board, int depthPlusOne) {
		generateMoves(board.getBitboard(), board.getSide(), board.getEpTarget(), board.getCastlingRights());
	}
	
	//TODO : try direct access ep target and side instead of pass parameter. and compare performances..
	public void generateMoves(long[] bitboard, int side, int epTarget, byte[][] castlingRights) {
		int opSide = side ^ 1;
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
	

}

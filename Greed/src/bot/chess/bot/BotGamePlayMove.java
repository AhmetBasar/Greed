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
package chess.bot;

import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.LegalityV4;
import chess.engine.TranspositionTable;

public class BotGamePlayMove {
	
	// EP Square = captured piece(pawn).
	// EP Target = empty target.
	
	private int move;
	private byte capturedPiece;
	private byte promotedPiece;
	private int from;
	private int to;
	private int castlingRookFrom;
	private int castlingRookTo;
	private int[] pushDiffs = { 8, 64 - 8 };
	private int side;
	private int toBeImplementedEpTarget = 64;
	private int toBeImplementedEpSquare;
	private int currentEpTarget;
	private int currentEpSquare;
	private byte[][] currentCastlingRights;
	private byte fromPiece;
	private int[][] castlingRookSources = {{0, 7}, {56, 63}};
	private int[][] castlingRookTargets = {{3, 5}, {59, 61}};
	private BotGamePlay base;
	private LegalityV4 legality = new LegalityV4();
	
	private long currentPawnZobristKey;

	public BotGamePlayMove(int move, BotGamePlay base) {
		this.base = base;
		this.move = move;
		currentEpTarget 		= base.getGamePlay().getEpTarget();
		currentEpSquare 		= base.getGamePlay().getEpSquare();
		side            		= base.getGamePlay().getSide();
		currentCastlingRights	= DebugUtility.deepCloneMultiDimensionalArray(base.getGamePlay().getCastlingRights()); // safe deep copy.
		
		currentPawnZobristKey = base.getGamePlay().getPawnZobristKey();
		
		int diff = pushDiffs[side];
		to = (move & 0x0000ff00) >>> 8;
		from = move & 0x000000ff;
		fromPiece = base.getPieces()[from];
		
		if(isSimpleMove()){
			capturedPiece = base.getPieces()[to];
		} else if (isDoublePush()) {
			toBeImplementedEpTarget = Long.numberOfTrailingZeros(((1L << to) >>> diff) | ((1L << to) << (64 - diff)));
			toBeImplementedEpSquare = to;
		} else if(isEnPassantCapture()){
			capturedPiece = base.getPieces()[currentEpSquare];
		} else if(isPromotion()){
			capturedPiece = base.getPieces()[to];
			promotedPiece = (byte)((move & 0x00f00000) >>> 20);
		} else if(isQueenSideCastling()){
			castlingRookFrom = castlingRookSources[side][0];
			castlingRookTo   = castlingRookTargets[side][0];
		} else if(isKingSideCastling()){
			castlingRookFrom = castlingRookSources[side][1];
			castlingRookTo   = castlingRookTargets[side][1];
		}
	}

	public void implement() {
		
		//Transposition Table//
		base.updateZobristKey(TranspositionTable.zobristBlackMove);
		if (currentEpTarget != 64 && (EngineConstants.PAWN_ATTACK_LOOKUP[side ^ 1][currentEpTarget] & base.getBitboard()[(side) | EngineConstants.PAWN]) != 0) {
			base.updateZobristKey(TranspositionTable.zobristEnPassantArray[currentEpTarget]);
		}
		//
		
		base.getGamePlay().resetEnPassantFlags();
		if (isSimpleMove()) {
			
			//Transposition Table//
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][from]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][to]);
			if(capturedPiece > 0){
				base.updateZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][to]);
			}
			//
			
			byte fromPieceWc = (byte)(fromPiece & 0XFE);
			if (fromPieceWc == EngineConstants.PAWN) {
				base.updatePawnZobristKey(TranspositionTable.zobristPositionArray[fromPiece][from]);
				base.updatePawnZobristKey(TranspositionTable.zobristPositionArray[fromPiece][to]);
			}
			
			byte capturedPieceWc = (byte)(capturedPiece & 0XFE);
			if (capturedPieceWc == EngineConstants.PAWN) {
				base.updatePawnZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][to]);
			}
			
			base.getPieces()[from] = 0;
			base.getPieces()[to] = fromPiece;
			base.getBitboard()[fromPiece] &= ~(1L << from);
			base.getBitboard()[fromPiece] |= (1L << to);
			base.getBitboard()[capturedPiece] &= ~(1L << to);
		} else if (isDoublePush()) {
			
			//Transposition Table//
			if ((EngineConstants.PAWN_ATTACK_LOOKUP[side][toBeImplementedEpTarget] & base.getBitboard()[(side ^ 1) | EngineConstants.PAWN]) != 0) {
				base.updateZobristKey(TranspositionTable.zobristEnPassantArray[toBeImplementedEpTarget]);
			}
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][from]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][to]);
			//
			
			base.updatePawnZobristKey(TranspositionTable.zobristPositionArray[fromPiece][from]);
			base.updatePawnZobristKey(TranspositionTable.zobristPositionArray[fromPiece][to]);
			
			base.getGamePlay().setEpTarget(toBeImplementedEpTarget);
			base.getGamePlay().setEpSquare(toBeImplementedEpSquare);
			base.getPieces()[from] = 0;
			base.getPieces()[to] = fromPiece;
			base.getBitboard()[fromPiece] &= ~(1L << from);
			base.getBitboard()[fromPiece] |= (1L << to);
		} else if(isEnPassantCapture()){
			
			//Transposition Table//
			base.updateZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][currentEpSquare]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][from]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][to]);
			//
			
			base.updatePawnZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][currentEpSquare]);
			base.updatePawnZobristKey(TranspositionTable.zobristPositionArray[fromPiece][from]);
			base.updatePawnZobristKey(TranspositionTable.zobristPositionArray[fromPiece][to]);
			
			base.getPieces()[currentEpSquare] = 0;
			base.getPieces()[from] = 0;
			base.getPieces()[to] = fromPiece;
			base.getBitboard()[fromPiece] &= ~(1L << from);
			base.getBitboard()[fromPiece] |= (1L << to);
			base.getBitboard()[capturedPiece] &= ~(1L << currentEpSquare);
		} else if(isPromotion()){
			
			//Transposition Table//
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][from]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[promotedPiece][to]);
			if(capturedPiece > 0) {
				base.updateZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][to]);
			}
			//
			
			base.updatePawnZobristKey(TranspositionTable.zobristPositionArray[fromPiece][from]);
			
			base.getPieces()[from] = 0;
			base.getPieces()[to] = promotedPiece;
			base.getBitboard()[fromPiece] &= ~(1L << from);
			base.getBitboard()[promotedPiece] |= (1L << to);
			base.getBitboard()[capturedPiece] &= ~(1L << to);
		} else if(isQueenSideCastling() || isKingSideCastling()){
			
    		//Transposition Table//
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][from]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][to]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[base.getPieces()[castlingRookFrom]][castlingRookFrom]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[base.getPieces()[castlingRookFrom]][castlingRookTo]);
			//
			
			int castlingSide = (move & 0x00010000) >>> 16;
			byte sideToRook = (byte) (side | EngineConstants.ROOK);
			castlingRookFrom = castlingRookSources[side][castlingSide];
			castlingRookTo = castlingRookTargets[side][castlingSide];
			base.getPieces()[from] = 0;
			base.getPieces()[to] = fromPiece;
			base.getBitboard()[fromPiece] &= ~(1L << from);
			base.getBitboard()[fromPiece] |= (1L << to);
			base.getPieces()[castlingRookFrom] = 0;
			base.getPieces()[castlingRookTo] = sideToRook;
			base.getBitboard()[sideToRook] &= ~(1L << castlingRookFrom);
			base.getBitboard()[sideToRook] |= (1L << castlingRookTo);
		}
	}
	
	public void unImplement() {
		
		base.getGamePlay().setPawnZobristKey(currentPawnZobristKey);
		
		//Transposition Table//
		base.updateZobristKey(TranspositionTable.zobristBlackMove);
		if(base.getGamePlay().getEpTarget() != 64 && (EngineConstants.PAWN_ATTACK_LOOKUP[side][base.getGamePlay().getEpTarget()] & base.getBitboard()[(side ^ 1) | EngineConstants.PAWN]) != 0)
			base.updateZobristKey(TranspositionTable.zobristEnPassantArray[base.getGamePlay().getEpTarget()]);
		//
		base.getGamePlay().setEpTarget(currentEpTarget);
		base.getGamePlay().setEpSquare(currentEpSquare);
		base.getGamePlay().setCastlingRights(DebugUtility.deepCloneMultiDimensionalArray(currentCastlingRights));
		if (isSimpleMove()) {
			//Transposition Table//
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][to]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][from]);
			if(capturedPiece > 0)
				base.updateZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][to]);
			//
			
			base.getPieces()[from] = fromPiece;
			base.getPieces()[to] = capturedPiece;
			base.getBitboard()[fromPiece] |= (1L << from);
			base.getBitboard()[fromPiece] &= ~(1L << to);
			base.getBitboard()[capturedPiece] |= (1L << to); // capturedPiece may be zero here.
			
		} else if (isDoublePush()) {
			//Transposition Table//
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][to]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][from]);
			//
			
			base.getPieces()[from] = fromPiece;
			base.getPieces()[to] = 0;
			base.getBitboard()[fromPiece] |= (1L << from);
			base.getBitboard()[fromPiece] &= ~(1L << to);
			
		} else if(isEnPassantCapture()){
			//Transposition Table//
			base.updateZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][currentEpSquare]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][to]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][from]);
			//
			
			base.getPieces()[currentEpSquare] = capturedPiece;
			base.getPieces()[from] = fromPiece;
			base.getPieces()[to] = 0;
			base.getBitboard()[fromPiece] |= (1L << from);
			base.getBitboard()[fromPiece] &= ~(1L << to);
			base.getBitboard()[capturedPiece] |= (1L << currentEpSquare);
			
		} else if(isPromotion()){
			//Transposition Table//
			base.updateZobristKey(TranspositionTable.zobristPositionArray[(byte)(EngineConstants.PAWN | side)][from]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[promotedPiece][to]);
			if(capturedPiece > 0)
				base.updateZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][to]);
			//
			
			fromPiece = (byte)(side | EngineConstants.PAWN);
			base.getPieces()[from] = fromPiece;
			base.getPieces()[to] = capturedPiece;
			base.getBitboard()[fromPiece] |= (1L << from);
			base.getBitboard()[promotedPiece] &= ~(1L << to);
			base.getBitboard()[capturedPiece] |= (1L << to); // capturedPiece may be zero here. 
			
		} else if(isQueenSideCastling() || isKingSideCastling()){
    		//Transposition Table//
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][to]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[fromPiece][from]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[base.getPieces()[castlingRookTo]][castlingRookTo]);
			base.updateZobristKey(TranspositionTable.zobristPositionArray[base.getPieces()[castlingRookTo]][castlingRookFrom]);
			//
            
			byte sideToRook = (byte) (side | EngineConstants.ROOK);
            
            base.getPieces()[from] = fromPiece;
            base.getPieces()[to] = 0;
            base.getBitboard()[fromPiece] |= (1L << from);
            base.getBitboard()[fromPiece] &= ~(1L << to);
			
			base.getPieces()[castlingRookFrom] = sideToRook;
			base.getPieces()[castlingRookTo] = 0;
			base.getBitboard()[sideToRook] |= (1L << castlingRookFrom);
			base.getBitboard()[sideToRook] &= ~(1L << castlingRookTo);
			
		}
		
		if(currentEpTarget != 64 && (EngineConstants.PAWN_ATTACK_LOOKUP[side ^ 1][currentEpTarget] & base.getBitboard()[(side) | EngineConstants.PAWN]) != 0)
			base.updateZobristKey(TranspositionTable.zobristEnPassantArray[currentEpTarget]);
	}

	private boolean isSimpleMove(){
		return (move & 0x00ff0000) == 0 ? true : false;
	}

	private boolean isDoublePush() {
		return (move & 0x00ff0000) == (EngineConstants.DOUBLE_PUSH << 16) ? true : false;
	}
	
	private boolean isEnPassantCapture() {
		return (move & 0x00ff0000) == (EngineConstants.EP_CAPTURE << 16) ? true : false;
	}
	
	private boolean isPromotion(){
		return (move & 0x000f0000) == (EngineConstants.PROMOTION << 16) ? true : false;
	}
	
	private boolean isQueenSideCastling(){
		return (move & 0x00ff0000) == (EngineConstants.QUEEN_SIDE_CASTLING << 16) ? true : false;
	}
	
	private boolean isKingSideCastling(){
		return (move & 0x00ff0000) == (EngineConstants.KING_SIDE_CASTLING << 16) ? true : false;
	}
	
	public boolean isCastling(){
		return isQueenSideCastling() || isKingSideCastling();
	}
	
	public boolean isKingInCheck() {
		boolean isKingInCheck = false;
		long[] bitboard = base.getBitboard().clone();
		byte fromPiece = base.getPieces()[from];
		byte toPiece = base.getPieces()[to];
		if (isSimpleMove()) {
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			bitboard[toPiece] &= ~(1L << to);
		} else if(isDoublePush()){
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
		} else if(isEnPassantCapture()){
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			bitboard[capturedPiece] &= ~(1L << currentEpSquare);
		} else if(isPromotion()){
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[promotedPiece] |= (1L << to);
			bitboard[toPiece] &= ~(1L << to);
		} else if (isCastling()) {
			int castlingSide = (move & 0x00010000) >>> 16;
			byte sideToRook = (byte) (side | EngineConstants.ROOK);
			castlingRookFrom = castlingRookSources[side][castlingSide];
			castlingRookTo = castlingRookTargets[side][castlingSide];
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			bitboard[sideToRook] &= ~(1L << castlingRookFrom);
			bitboard[sideToRook] |= (1L << castlingRookTo);
		}
		
		if (legality.isKingInCheck(bitboard, side)) {
			isKingInCheck = true;
		}
		return isKingInCheck;
	}

	public byte getCapturedPiece() {
		return capturedPiece;
	}

	public byte getFromPiece() {
		return fromPiece;
	}

	public int getMove() {
		return move;
	}

	public boolean isCaptureMove() {
		return capturedPiece != 0;
	}
	
	public boolean isPawnMove() {
		return fromPiece == EngineConstants.WHITE_PAWN || fromPiece == EngineConstants.BLACK_PAWN;
	}
	
}

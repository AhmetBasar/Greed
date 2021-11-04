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
package chess.game;

import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.LegalityV4;
import chess.engine.Move;
import chess.engine.Transformer;
import chess.engine.TranspositionTable;
import chess.gui.BaseGui;
import chess.gui.PieceEffects;

public class GamePlayMove {

	private int move;
	private byte capturedPiece;
	private byte promotedPiece;
	private byte fromPiece;
	private int from;
	private int to;
	private int castlingRookFrom;
	private int castlingRookTo;
	private BaseGui base;
	private int side;
	private int toBeImplementedEpTarget = 64;
	private int currentEpTarget;
	private int toBeImplementedEpSquare;
	private int currentEpSquare;
	private byte[][] currentCastlingRights;
	private int[][] castlingRookSources = {{0, 7}, {56, 63}};
	private int[][] castlingRookTargets = {{3, 5}, {59, 61}};
	private LegalityV4 legality = new LegalityV4();
	
	private long initialPawnZobristKey;

	public GamePlayMove(BaseGui base, int move) {
		this.base = base;
		this.move = move;
		currentEpTarget 		= base.getGamePlay().getEpTarget();
		currentEpSquare 		= base.getGamePlay().getEpSquare();
		side            		= base.getGamePlay().getSide();
		currentCastlingRights	= DebugUtility.deepCloneMultiDimensionalArray(base.getGamePlay().getCastlingRights()); // safe deep copy.
		
		to = Move.getTo(move);
		from = Move.getFrom(move);
		
		fromPiece = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(base.getChessBoardPanel().getBoard()))[from];
		
		if(Move.isSimpleMove(move)){
			capturedPiece = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(base.getChessBoardPanel().getBoard()))[to];
		} else if (Move.isDoublePush(move)) {
			toBeImplementedEpTarget = EngineConstants.EPT_LOOKUP[side][to];
			toBeImplementedEpSquare = to;
		} else if(Move.isEnPassantCapture(move)){
			capturedPiece = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(base.getChessBoardPanel().getBoard()))[currentEpSquare];
		} else if(Move.isPromotion(move)){
			capturedPiece = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(base.getChessBoardPanel().getBoard()))[to];
			promotedPiece = Move.getPromotedPiece(move);
		} else if(Move.isQueenSideCastling(move)){
			castlingRookFrom = castlingRookSources[side][0];
			castlingRookTo   = castlingRookTargets[side][0];
		} else if(Move.isKingSideCastling(move)){
			castlingRookFrom = castlingRookSources[side][1];
			castlingRookTo   = castlingRookTargets[side][1];
		}
		
		initialPawnZobristKey = base.getGamePlay().getPawnZobristKey();
	}

	public void implement() {
		
		long[] bitboard = Transformer.getBitboardStyl(base.getBoard());
		
		//Transposition Table//
		base.getGamePlay().updateZobristKey(TranspositionTable.zobristBlackMove);
		if(currentEpTarget != 64 && (EngineConstants.PAWN_ATTACK_LOOKUP[side ^ 1][currentEpTarget] & bitboard[(side) | EngineConstants.PAWN]) != 0)
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristEnPassantArray[currentEpTarget]);
		//
		base.getGamePlay().resetGameFlags();
		if (Move.isSimpleMove(move)) {
			//Transposition Table//
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][from]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][to]);
			if(capturedPiece > 0)
				base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][to]);
			//
			
			byte fromPieceWc = (byte)(fromPiece & 0XFE);
			if (fromPieceWc == EngineConstants.PAWN) {
				base.getGamePlay().updatePawnZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][from]);
				base.getGamePlay().updatePawnZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][to]);
			}
			
			byte capturedPieceWc = (byte)(capturedPiece & 0XFE);
			if (capturedPieceWc == EngineConstants.PAWN) {
				base.getGamePlay().updatePawnZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][to]);
			}
			
			PieceEffects.doEffect(base, from, to);
		} else if (Move.isDoublePush(move)) {
			//Transposition Table//
			if ((EngineConstants.PAWN_ATTACK_LOOKUP[side][toBeImplementedEpTarget] & bitboard[(side ^ 1) | EngineConstants.PAWN]) != 0) {
				base.getGamePlay().updateZobristKey(TranspositionTable.zobristEnPassantArray[toBeImplementedEpTarget]);
			}
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][from]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][to]);
			//
			
			base.getGamePlay().updatePawnZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][from]);
			base.getGamePlay().updatePawnZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][to]);
			
			base.getGamePlay().setEpTarget(toBeImplementedEpTarget);
			base.getGamePlay().setEpSquare(toBeImplementedEpSquare);
			PieceEffects.doEffect(base, from, to);
		} else if(Move.isEnPassantCapture(move)){
			//Transposition Table//
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(currentEpSquare).getItem()][currentEpSquare]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][from]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][to]);
			//
			
			base.getGamePlay().updatePawnZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(currentEpSquare).getItem()][currentEpSquare]);
			base.getGamePlay().updatePawnZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][from]);
			base.getGamePlay().updatePawnZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][to]);
			
			base.getChessBoardPanel().getCell(currentEpSquare).setItem(EngineConstants.BLANK);
			PieceEffects.doEffect(base, from, to);
		} else if(Move.isPromotion(move)){
			//Transposition Table//
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][from]);
			/** BURASI AÇIKÇA HATALI. base.getChessBoardPanel().getCell(promotedPiece).getItem() yerine sadece promotedPiece yazýlmalýydý. */
//			GamePlay.zobristKey = GamePlay.zobristKey ^ TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(promotedPiece).getItem()][to];
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[promotedPiece][to]);
			if(capturedPiece > 0)
				base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][to]);
			//
			
			base.getGamePlay().updatePawnZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][from]);
			
			base.getChessBoardPanel().getCell(from).setItem(promotedPiece);
			PieceEffects.doEffect(base, from, to);
		} else if(Move.isQueenSideCastling(move) || Move.isKingSideCastling(move)){
            byte fromItem = base.getChessBoardPanel().getCell(from).getItem();
    		//Transposition Table//
            base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][from]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(from).getItem()][to]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(castlingRookFrom).getItem()][castlingRookFrom]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(castlingRookFrom).getItem()][castlingRookTo]);
			//
            base.getChessBoardPanel().getCell(from).setItem(EngineConstants.BLANK);
            base.getChessBoardPanel().getCell(to).setItem(fromItem);
            PieceEffects.doEffect(base, castlingRookFrom, castlingRookTo);
		}
	}

	public void unImplement() {
		
		base.getGamePlay().setPawnZobristKey(initialPawnZobristKey);
		
		long[] bitboard = Transformer.getBitboardStyl(base.getBoard());
		
		//Transposition Table//
		base.getGamePlay().updateZobristKey(TranspositionTable.zobristBlackMove);
		if(base.getGamePlay().getEpTarget() != 64 && (EngineConstants.PAWN_ATTACK_LOOKUP[side][base.getGamePlay().getEpTarget()] & bitboard[(side ^ 1) | EngineConstants.PAWN]) != 0)
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristEnPassantArray[base.getGamePlay().getEpTarget()]);
		//
		base.getGamePlay().setEpTarget(currentEpTarget);
		base.getGamePlay().setEpSquare(currentEpSquare);
		base.getGamePlay().setCastlingRights(DebugUtility.deepCloneMultiDimensionalArray(currentCastlingRights));
		if (Move.isSimpleMove(move)) {
			//Transposition Table//
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(to).getItem()][to]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(to).getItem()][from]);
			if(capturedPiece > 0)
				base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][to]);
			//
			PieceEffects.doEffect(base, to, from);
			base.getChessBoardPanel().getCell(to).setItem(capturedPiece);
		} else if (Move.isDoublePush(move)) {
			//Transposition Table//
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(to).getItem()][to]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(to).getItem()][from]);
			//
			PieceEffects.doEffect(base, to, from);
		} else if(Move.isEnPassantCapture(move)){
			//Transposition Table//
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][currentEpSquare]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(to).getItem()][to]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(to).getItem()][from]);
			//
			PieceEffects.doEffect(base, to, from);
			base.getChessBoardPanel().getCell(currentEpSquare).setItem(capturedPiece);
		} else if(Move.isPromotion(move)){
			//Transposition Table//
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[(byte)(EngineConstants.PAWN | side)][from]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[promotedPiece][to]);
			if(capturedPiece > 0)
				base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[capturedPiece][to]);
			//
			base.getChessBoardPanel().getCell(to).setItem((byte)(EngineConstants.PAWN | side));
			PieceEffects.doEffect(base, to, from);
			base.getChessBoardPanel().getCell(to).setItem(capturedPiece);
		} else if(Move.isQueenSideCastling(move) || Move.isKingSideCastling(move)){
			byte fromItem = base.getChessBoardPanel().getCell(to).getItem();
    		//Transposition Table//
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(to).getItem()][to]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(to).getItem()][from]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(castlingRookTo).getItem()][castlingRookTo]);
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristPositionArray[base.getChessBoardPanel().getCell(castlingRookTo).getItem()][castlingRookFrom]);
			//
            base.getChessBoardPanel().getCell(to).setItem(EngineConstants.BLANK);
            base.getChessBoardPanel().getCell(from).setItem(fromItem);
            PieceEffects.doEffect(base, castlingRookTo, castlingRookFrom);
		}
		
		if(currentEpTarget != 64 && (EngineConstants.PAWN_ATTACK_LOOKUP[side ^ 1][currentEpTarget] & Transformer.getBitboardStyl(base.getBoard())[(side) | EngineConstants.PAWN]) != 0)
			base.getGamePlay().updateZobristKey(TranspositionTable.zobristEnPassantArray[currentEpTarget]);
	}

	public boolean isKingInCheck() {
		boolean isKingInCheck = false;
		long[] bitboard = Transformer.getBitboardStyl(base.getChessBoardPanel().getBoard());
		byte fromPiece = base.getChessBoardPanel().getCell(from).getItem();
		byte toPiece = base.getChessBoardPanel().getCell(to).getItem();
		if (Move.isSimpleMove(move)) {
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			bitboard[toPiece] &= ~(1L << to);
		} else if(Move.isDoublePush(move)){
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
		} else if(Move.isEnPassantCapture(move)){
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[fromPiece] |= (1L << to);
			bitboard[capturedPiece] &= ~(1L << currentEpSquare);
		} else if(Move.isPromotion(move)){
			bitboard[fromPiece] &= ~(1L << from);
			bitboard[promotedPiece] |= (1L << to);
			bitboard[toPiece] &= ~(1L << to);
		} else if (Move.isCastling(move)) {
			int castlingSide = Move.getCastlingSide(move);
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

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
package chess.gui;

import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.Transformer;
import chess.engine.TranspositionTable;

public class FenOperations {

	private BaseGui base = null;
	
	private byte[][] board = new byte[8][8];
	private int side;
	private byte[][] castlingRights = new byte[2][2];
	private int epTarget = 64;
	private int epSquare = -1;
	private long zobristKey;
	
	private static final char WHITE_PAWN 			= 'P';
	private static final char WHITE_KNIGHT 			= 'N';
	private static final char WHITE_BISHOP 			= 'B';
	private static final char WHITE_ROOK 			= 'R';
	private static final char WHITE_QUEEN 			= 'Q';
	private static final char WHITE_KING 			= 'K';
	
	private static final char BLACK_PAWN 			= 'p';
	private static final char BLACK_KNIGHT 			= 'n';
	private static final char BLACK_BISHOP 			= 'b';
	private static final char BLACK_ROOK 			= 'r';
	private static final char BLACK_QUEEN 			= 'q';
	private static final char BLACK_KING 			= 'k';
	
	private static final String WHITES_TURN 		= "w";
	private static final String BLACKS_TURN 		= "b";
	
	private static final String NOT_AVAILABLE 		= "-";
	private static final char WHITE_KING_CASTLE 	= 'K';
	private static final char WHITE_QUEEN_CASTLE 	= 'Q';
	private static final char BLACK_KING_CASTLE 	= 'k';
	private static final char BLACK_QUEEN_CASTLE 	= 'q';

	public FenOperations(BaseGui base) {
		this.base = base;
	}
	
	public FenOperations() {
	}

	public String getFenString() {
		String fenString = "";
		return fenString;
	}

	public void setFenString(String fenStr) {
		String[] fenArray;
		if(base != null){
			fenArray = base.getDebugPanel().getFenString().split(" ");
		}else{
			fenArray = fenStr.split(" ");
		}
		if(fenArray.length < 4){
			throw new RuntimeException("Invalid FEN Data");
		}
		setBoard(fenArray[0]);
		setSide(fenArray[1]);
		setCastlingRights(fenArray[2]);
		setEnpassantTarget(fenArray[3]);
		if(base != null){
			base.getGamePlay().recalculateZobristKey();
		}
		zobristKey = TranspositionTable.getZobristKey(Transformer.getBitboardStyl(board), epTarget, castlingRights, side);
	}
	
	private void setEnpassantTarget(String enPassant) {

		if(enPassant.equalsIgnoreCase(NOT_AVAILABLE)){
			epTarget = 64;
			epSquare = -1;
		} else {
			if(enPassant.length() != 2){
				throw new RuntimeException("Invalid Enpassant Target");
			}
			char file = enPassant.toCharArray()[0];
			int rank = Character.getNumericValue(enPassant.toCharArray()[1]);
			if(file == 'a'){
				epTarget = 0 + ((rank - 1) * 8);
			} else if(file == 'b'){
				epTarget = 1 + ((rank - 1) * 8);
			} else if(file == 'c'){
				epTarget = 2 + ((rank - 1) * 8);
			} else if(file == 'd'){
				epTarget = 3 + ((rank - 1) * 8);
			} else if(file == 'e'){
				epTarget = 4 + ((rank - 1) * 8);
			} else if(file == 'f'){
				epTarget = 5 + ((rank - 1) * 8);
			} else if(file == 'g'){
				epTarget = 6 + ((rank - 1) * 8);
			} else if(file == 'h'){
				epTarget = 7 + ((rank - 1) * 8);
			} else{
				throw new RuntimeException("Invalid Enpassant");
			}
			
			if(rank == 3){
				epSquare = epTarget + 8;
			} else if(rank == 6){
				epSquare = epTarget - 8;
			} else{
				throw new RuntimeException("Invalid Enpassant");
			}
		}
		if(base != null){
			base.getDebugPanel().setEnpassant(epTarget, epSquare);
			base.getGamePlay().setEpTarget(epTarget);
			base.getGamePlay().setEpSquare(epSquare);
		}
		
	}
	
	private void setCastlingRights(String rights) {
		
		if(rights.equalsIgnoreCase(NOT_AVAILABLE)){
			castlingRights[0][0] = 0;
			castlingRights[0][1] = 0;
			castlingRights[1][0] = 0;
			castlingRights[1][1] = 0;
		} else {
			char[] rightArray = rights.toCharArray();
			if(rightArray.length > 4){
				throw new RuntimeException("Invalid Castling Rights(count)");
			}
			for(int i = 0 ; i < rightArray.length ; i++){
				if(rightArray[i] == WHITE_KING_CASTLE){
					castlingRights[0][1] = 1;
				} else if(rightArray[i] == WHITE_QUEEN_CASTLE) {
					castlingRights[0][0] = 1;
				} else if(rightArray[i] == BLACK_KING_CASTLE) {
					castlingRights[1][1] = 1;
				} else if(rightArray[i] == BLACK_QUEEN_CASTLE) {
					castlingRights[1][0] = 1;
				} else {
					throw new RuntimeException("Invalid Castling Rights(char)");
				}
			}
		}
		if(base != null){
			base.getDebugPanel().setCastlingRights(castlingRights);
			base.getGamePlay().setCastlingRights(castlingRights);
		}
	}
	
	private void setSide(String side) {
		if(side.equalsIgnoreCase(WHITES_TURN)){
			if(base != null){
				base.getGamePlay().setSide(GuiConstants.WHITES_TURN);
			} else {
				this.side = GuiConstants.WHITES_TURN;
			}
		} else if(side.equalsIgnoreCase(BLACKS_TURN) ){
			if(base != null){
				base.getGamePlay().setSide(GuiConstants.BLACKS_TURN);
			} else {
				this.side = GuiConstants.BLACKS_TURN;
			}
		} else {
			throw new RuntimeException("Invalid Side");
		}
	}
	
	private void setBoard(String boardPosition) {
		String[] ranks = boardPosition.split("/");
		String rank;
		int rankSquareIndex = 0;
		char piece;
		if(ranks.length != 8){
			throw new RuntimeException("Invalid rank length");
		}
		for(int r = 0 ; r < ranks.length ; r++){
			rankSquareIndex = 0;
			rank = ranks[r];
			char[] pieceArray = rank.toCharArray();
			for(int p = 0 ; p < pieceArray.length ; p++){
				piece = pieceArray[p];
				if(Character.isDigit(piece)){
					rankSquareIndex = rankSquareIndex + Character.getNumericValue(piece);
				} else{
					if(piece == WHITE_PAWN){
						board[rankSquareIndex][r] = EngineConstants.WHITE_PAWN;
					} else if(piece == WHITE_KNIGHT){
						board[rankSquareIndex][r] = EngineConstants.WHITE_KNIGHT;
					} else if(piece == WHITE_BISHOP){
						board[rankSquareIndex][r] = EngineConstants.WHITE_BISHOP;
					} else if(piece == WHITE_ROOK){
						board[rankSquareIndex][r] = EngineConstants.WHITE_ROOK;
					} else if(piece == WHITE_QUEEN){
						board[rankSquareIndex][r] = EngineConstants.WHITE_QUEEN;
					} else if(piece == WHITE_KING){
						board[rankSquareIndex][r] = EngineConstants.WHITE_KING;
					} else if(piece == BLACK_PAWN){
						board[rankSquareIndex][r] = EngineConstants.BLACK_PAWN;
					} else if(piece == BLACK_KNIGHT){
						board[rankSquareIndex][r] = EngineConstants.BLACK_KNIGHT;
					} else if(piece == BLACK_BISHOP){
						board[rankSquareIndex][r] = EngineConstants.BLACK_BISHOP;
					} else if(piece == BLACK_ROOK){
						board[rankSquareIndex][r] = EngineConstants.BLACK_ROOK;
					} else if(piece == BLACK_QUEEN){
						board[rankSquareIndex][r] = EngineConstants.BLACK_QUEEN;
					} else if(piece == BLACK_KING){
						board[rankSquareIndex][r] = EngineConstants.BLACK_KING;
					}
					rankSquareIndex++;
				}
			}
			if(rankSquareIndex != 8){
				throw new RuntimeException("Invalid board position(number of piece)");
			}
		}
		if(base != null){
			base.setBoard(board);
		}
	}
	
	public byte[][] getBoard() {
		return DebugUtility.deepCloneMultiDimensionalArray(board);
	}
	
	public int getSide() {
		return side;
	}
	
	public byte[][] getCastlingRights() {
		return DebugUtility.deepCloneMultiDimensionalArray(castlingRights);
	}
	
	public int getEpTarget() {
		return epTarget;
	}
	
	public int getEpSquare() {
		return epSquare;
	}

	public long getZobristKey() {
		return zobristKey;
	}
	
	
}

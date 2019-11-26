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

import java.util.List;

import chess.engine.EngineConstants.EngineMode;

public class SearchParameters {

	private int depth;
	private int epT;
	private int epS;
	private long[] bitboard;
	private byte[] pieces;
	private byte[][] castlingRights;
	private int side;
	private long uiZobristKey;
	private long uiPawnZobristKey;
	private long timeLimit;
	private int fiftyMoveCounter;
	private EngineMode engineMode;
	private String bookName = "book_small.bin";
	private int firstMove; // for PREMOVEFINDER
	private List<Long> zobristKeyHistory;

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getEpT() {
		return epT;
	}

	public void setEpT(int epT) {
		this.epT = epT;
	}

	public int getEpS() {
		return epS;
	}

	public void setEpS(int epS) {
		this.epS = epS;
	}

	public long[] getBitboard() {
		return bitboard;
	}

	public void setBitboard(long[] bitboard) {
		this.bitboard = bitboard;
	}

	public byte[] getPieces() {
		return pieces;
	}

	public void setPieces(byte[] pieces) {
		this.pieces = pieces;
	}

	public byte[][] getCastlingRights() {
		return castlingRights;
	}

	public void setCastlingRights(byte[][] castlingRights) {
		this.castlingRights = castlingRights;
	}

	public int getSide() {
		return side;
	}

	public void setSide(int side) {
		this.side = side;
	}

	public long getUiZobristKey() {
		return uiZobristKey;
	}

	public void setUiZobristKey(long uiZobristKey) {
		this.uiZobristKey = uiZobristKey;
	}
	
	public long getUiPawnZobristKey() {
		return uiPawnZobristKey;
	}

	public void setUiPawnZobristKey(long uiPawnZobristKey) {
		this.uiPawnZobristKey = uiPawnZobristKey;
	}

	public long getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}

	public int getFiftyMoveCounter() {
		return fiftyMoveCounter;
	}

	public void setFiftyMoveCounter(int fiftyMoveCounter) {
		this.fiftyMoveCounter = fiftyMoveCounter;
	}

	public EngineMode getEngineMode() {
		return engineMode;
	}

	public void setEngineMode(EngineMode engineMode) {
		this.engineMode = engineMode;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public int getFirstMove() {
		return firstMove;
	}

	public void setFirstMove(int firstMove) {
		this.firstMove = firstMove;
	}

	public List<Long> getZobristKeyHistory() {
		return zobristKeyHistory;
	}

	public void setZobristKeyHistory(List<Long> zobristKeyHistory) {
		this.zobristKeyHistory = zobristKeyHistory;
	}
	
}

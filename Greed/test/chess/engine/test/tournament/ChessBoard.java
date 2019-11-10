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
package chess.engine.test.tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.LegalityV4;
import chess.engine.MoveGenerationOnlyQueenPromotions;
import chess.engine.Transformer;
import chess.engine.TranspositionTable;
import chess.gui.GuiConstants;

public class ChessBoard {
	private int side;
	private int epTarget;
	private int epSquare;
	private byte[][] castlingRights = { { 1, 1 }, { 1, 1 } };
	private byte[] kingPositions = { 4, 60 };
	private byte[][] rookPositions = { { 0, 7 }, { 56, 63 } };
	private MoveGenerationOnlyQueenPromotions moveGeneration = new MoveGenerationOnlyQueenPromotions();
	private long zobristKey;
	private long pawnZobristKey;
	private int fiftyMoveCounter = 0;
	private ArrayList<ChessMove> moveHistory = new ArrayList<ChessMove>();
	private GameState gameState = GameState.PLAYING;
	private LegalityV4 legality = new LegalityV4();
	
	public enum GameState{PLAYING, DRAW, WHITE_WINS, BLACK_WINS};
	
	long[] bitboard;
	private byte[] pieces;
	
	private Map<Long, Integer> boardStateHistory = new HashMap<Long, Integer>();
	
	public static void main(String[] args) throws Exception{
		new ChessBoard();
	}

	public ChessBoard() {
		resetAll();
	}

	public void doMove(int move) {
		ChessMove gamePlayMove = new ChessMove(move, this);
		incrementOrResetFiftyMoveCounterIfNecessary(gamePlayMove);
		moveHistory.add(gamePlayMove);
		gamePlayMove.implement();
		updateCastlingRights();
		reverseTurn();
		incrementBoardStateCount();
	}
	
	private void incrementOrResetFiftyMoveCounterIfNecessary(ChessMove gamePlayMove) {
		if (gamePlayMove.isCaptureMove() || gamePlayMove.isPawnMove()) {
			fiftyMoveCounter = 0;
		} else {
			fiftyMoveCounter++;
		}
	}
	
	private void decrementFiftyMoveCounterIfNecessary(ChessMove gamePlayMove) {
		if (fiftyMoveCounter > 0) {
			fiftyMoveCounter--;
		}
	}
	
	public void undoMove() {
		if (moveHistory.size() > 0) {
			ChessMove botGamePlayMove = moveHistory.remove(moveHistory.size() - 1);
			decrementFiftyMoveCounterIfNecessary(botGamePlayMove);
			decrementBoardStateCount();
			botGamePlayMove.unImplement();
			updateCastlingRights();
			reverseTurn();
		} else {
			throw new RuntimeException("There is no move to undo.");
		}
	}
	
	private void incrementBoardStateCount() {
		Integer boardStateCount = boardStateHistory.get(zobristKey);
		if (boardStateCount == null) {
			boardStateHistory.put(zobristKey, 1);
		} else {
			boardStateHistory.put(zobristKey, boardStateCount.intValue() + 1);
		}
	}
	
	private void decrementBoardStateCount() {
		Integer boardStateCount = boardStateHistory.get(zobristKey);
		boardStateHistory.put(zobristKey, boardStateCount.intValue() - 1);
	}

	public void setSide(int turn) {
		this.side = turn;
	}

	public int getSide() {
		return side;
	}

	public boolean isWhitesTurn() {
		boolean whitesTurn = false;
		if (getSide() == GuiConstants.WHITES_TURN) {
			whitesTurn = true;
		}
		return whitesTurn;
	}

	public boolean isBlacksTurn() {
		boolean blacksTurn = false;
		if (getSide() == GuiConstants.BLACKS_TURN) {
			blacksTurn = true;
		}
		return blacksTurn;
	}

	public void reverseTurn() {
		side = side ^ 1;
	}

	public void resetEnPassantFlags() {
		epTarget = 64; // 64 means no epTarget. because we assume 1L<<64 = 0
		epSquare = -1; // -1 means no ep square exists
	}

	public void setEpTarget(int epTarget) {
		this.epTarget = epTarget;
	}

	public int getEpTarget() {
		return epTarget;
	}

	public void setEpSquare(int epSquare) {
		this.epSquare = epSquare;
	}

	public int getEpSquare() {
		return epSquare;
	}

	public byte[][] getCastlingRights() {
		return castlingRights;
	}

	public void setCastlingRights(byte[][] castlingRights) {
		byte whiteQueenSideCastlingRight = this.castlingRights[0][0];
		byte whiteKingSideCastlingRight = this.castlingRights[0][1];
		byte blackQueenSideCastlingRight = this.castlingRights[1][0];
		byte blackKingSideCastlingRight = this.castlingRights[1][1];
		
		this.castlingRights = castlingRights;
		
		if(whiteQueenSideCastlingRight != this.castlingRights[0][0]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[0][0];
		}
		
		if(whiteKingSideCastlingRight != this.castlingRights[0][1]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[0][1];
		}
		
		if(blackQueenSideCastlingRight != this.castlingRights[1][0]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[1][0];
		}
		
		if(blackKingSideCastlingRight != this.castlingRights[1][1]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[1][1];
		}
		
	}
	
	private void updateCastlingRights() {
		long[] bitboard = getBitboard();
		int opSide = this.side ^ 1;
		
		byte whiteQueenSideCastlingRight = castlingRights[0][0];
		byte whiteKingSideCastlingRight = castlingRights[0][1];
		byte blackQueenSideCastlingRight = castlingRights[1][0];
		byte blackKingSideCastlingRight = castlingRights[1][1];
		
		long kings = bitboard[(side) | EngineConstants.KING];
		castlingRights[side][0] = (byte) (castlingRights[side][0] & (kings >>> kingPositions[side]));
		castlingRights[side][1] = (byte) (castlingRights[side][1] & (kings >>> kingPositions[side]));
		
		long rooks = bitboard[(side) | EngineConstants.ROOK];
		castlingRights[side][0] = (byte) (castlingRights[side][0] & (rooks >>> rookPositions[side][0]));
		castlingRights[side][1] = (byte) (castlingRights[side][1] & (rooks >>> rookPositions[side][1]));
		
		kings = bitboard[(opSide) | EngineConstants.KING];
		castlingRights[opSide][0] = (byte) (castlingRights[opSide][0] & (kings >>> kingPositions[opSide]));
		castlingRights[opSide][1] = (byte) (castlingRights[opSide][1] & (kings >>> kingPositions[opSide]));
		
		rooks = bitboard[(opSide) | EngineConstants.ROOK];
		castlingRights[opSide][0] = (byte) (castlingRights[opSide][0] & (rooks >>> rookPositions[opSide][0]));
		castlingRights[opSide][1] = (byte) (castlingRights[opSide][1] & (rooks >>> rookPositions[opSide][1]));
		
		if(whiteQueenSideCastlingRight != castlingRights[0][0]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[0][0];
		}
		
		if(whiteKingSideCastlingRight != castlingRights[0][1]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[0][1];
		}
		
		if(blackQueenSideCastlingRight != castlingRights[1][0]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[1][0];
		}
		
		if(blackKingSideCastlingRight != castlingRights[1][1]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[1][1];
		}
		
	}
	
	public ChessBoard getGamePlay() {
		return this;
	}
	
	public void resetAll() {
		gameState = GameState.PLAYING;
		setSide(GuiConstants.WHITES_TURN);
		resetEnPassantFlags();
		fiftyMoveCounter = 0;
		castlingRights = new byte[][] { { 1, 1 }, { 1, 1 } };
		bitboard = Transformer.getBitboardStyl(DebugUtility.getDefaultBoard());
		pieces = Transformer.getByteArrayStyl(bitboard);
		zobristKey = TranspositionTable.getZobristKey(getBitboard(), getEpTarget(), getCastlingRights(), getSide());
		pawnZobristKey = TranspositionTable.getPawnZobristKey(getBitboard());

		moveHistory.clear();
		boardStateHistory.clear();
	}
	
	public int getValidMove(int source, int target) {
		ArrayList<Integer> validMoveList = new ArrayList<Integer>();
		int validMove = 0;
		int move = source | (target << 8);
		int[] validMoves = moveGeneration.generateMoves(getBitboard(), side, epTarget, castlingRights);
		for (int i = 0; i < EngineConstants.MOVE_LIST_SIZE; i++) {
			if (move == (validMoves[i] & 0x0000FFFF)) {
				validMoveList.add(validMoves[i]);
			}
		}
		// check King Safety
		int validMoveListSize = validMoveList.size();
		for (int i = 0; i < validMoveListSize; i++) {
			validMove = validMoveList.get(i);
			ChessMove gamePlayMove = new ChessMove(validMove, this);
			if (gamePlayMove.isKingInCheck()) {
				return 0;
			}
		}
		// choose item to be promoted
		if (validMoveListSize > 1) {
			throw new RuntimeException("Not Yet Implemented!!!");
		}
		
		return validMove;
	}

	public byte[][] getBoard() {
		return Transformer.getTwoDimByteArrayStyl(getBitboard());
	}

	public Map<Long, Integer> getBoardStateHistory() {
		return boardStateHistory;
	}

	public long getZobristKey() {
		return zobristKey;
	}
	
	public ArrayList<ChessMove> getMoveHistory() {
		return moveHistory;
	}

	public long[] getBitboard() {
		return bitboard;
	}
	
	public byte[] getPieces() {
		return pieces;
	}

	public int getFiftyMoveCounter() {
		return fiftyMoveCounter;
	}

	public void updateZobristKey(long val) {
		zobristKey = zobristKey ^ val;
	}
	
	public void updatePawnZobristKey(long val) {
		pawnZobristKey = pawnZobristKey ^ val;
	}
	
	public GameState getGameState() {
		if (!existsValidMove()) {
			if (legality.isKingInCheck(bitboard, side)) {
				System.out.println("CHECKMATE : " + (side == EngineConstants.WHITE ? " Black won" : "White Won"));
				gameState = side == EngineConstants.WHITE ? GameState.BLACK_WINS : GameState.WHITE_WINS;
			} else {
				System.out.println("DRAW : stalemate !");
				gameState = GameState.DRAW;
			}
		} else {
			if (getFiftyMoveCounter() > 99) {
				System.out.println("DRAW : fifty move exceeded.");
				gameState = GameState.DRAW;
			} else {
				Integer boardStateHistoryCount = getBoardStateHistory().get(zobristKey);
				if(boardStateHistoryCount != null && boardStateHistoryCount.intValue() == 3){
					System.out.println("DRAW : threefold repetition.");
					gameState = GameState.DRAW;
				}
			}
			// TODO : Insufficient material.
		}
		
		return gameState;
	}
	
	public boolean existsValidMove() {
		ArrayList<Integer> pseudoLegalMoveList = new ArrayList<Integer>();
		int[] pseudoLegalMoves = moveGeneration.generateMoves(bitboard, side, epTarget,
				castlingRights);
		for (int i = 0; i < EngineConstants.MOVE_LIST_SIZE; i++) {
			if (pseudoLegalMoves[i] != 0) {
				pseudoLegalMoveList.add(pseudoLegalMoves[i]);
			} else {
				break;
			}
		}
		
		int plMoveSize = pseudoLegalMoveList.size();
		if (plMoveSize == 0) {
			return false;
		}
		
		boolean isKingInSafe = false;
		
		// check King Safety
		for (int i = 0; i < plMoveSize; i++) {
			int validMove = pseudoLegalMoveList.get(i);
			ChessMove gamePlayMove = new ChessMove(validMove, this);
			if (!gamePlayMove.isKingInCheck()) {
				isKingInSafe = true;
				break;
			}
		}
		
		return isKingInSafe;
	}

	public long getPawnZobristKey() {
		return pawnZobristKey;
	}

	public void setPawnZobristKey(long pawnZobristKey) {
		this.pawnZobristKey = pawnZobristKey;
	}
	
}
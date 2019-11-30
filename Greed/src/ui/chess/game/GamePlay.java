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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import chess.bot.KeyListenerGeneric;
import chess.bot.Utility;
import chess.database.Storage;
import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.LegalityV4;
import chess.engine.MoveGeneration;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.engine.Transformer;
import chess.engine.TranspositionTable;
import chess.gui.BaseGui;
import chess.gui.GuiConstants;
import chess.gui.PieceEffects;

public class GamePlay {
	private int side;
	private BaseGui base;
	private ArrayList<GamePlayMove> moveHistory = new ArrayList<GamePlayMove>();
	private int epTarget;
	private int epSquare;
	private byte[][] castlingRights = { { 1, 1 }, { 1, 1 } };
	private byte[] kingPositions = { 4, 60 };
	private byte[][] rookPositions = { { 0, 7 }, { 56, 63 } };
	private long zobristKey;
	private long pawnZobristKey;
	private int fiftyMoveCounter = 0;
	private Storage dbManager;
	private HashMap<String, String> preferences = new HashMap<String, String>();
	private LegalityV4 legality = new LegalityV4();
	
	private MoveGeneration moveGeneration = new MoveGeneration();
	
	private Map<Long, Integer> boardStateHistory = new HashMap<Long, Integer>();
	private List<Long> zobristKeyHistory = new ArrayList<Long>();
	
	private boolean noAnimation = false;
	
	private boolean isUnimplementMove = false;
	
	private int blackScore = 0;
	private int whiteScore = 0;

	public static void main(String[] args) throws Exception{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new GamePlay().init();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GamePlay() {
		setSide(GuiConstants.WHITES_TURN);
		resetGameFlags();
	}

	public int getValidMove(byte[][] board, int source, int target) {
		ArrayList<Integer> validMoveList = new ArrayList<Integer>();
		int validMove = 0;
		int move = source | (target << 8);
		int[] validMoves = moveGeneration.generateMoves(Transformer.getBitboardStyl(board), side, epTarget,
				castlingRights);
		for (int i = 0; i < EngineConstants.MOVE_LIST_SIZE; i++) {
			if (move == (validMoves[i] & 0x0000FFFF)) {
				validMoveList.add(validMoves[i]);
			}
		}
		// check King Safety
		int validMoveListSize = validMoveList.size();
		for (int i = 0; i < validMoveListSize; i++) {
			validMove = validMoveList.get(i);
			GamePlayMove gamePlayMove = new GamePlayMove(base, validMove);
			if (gamePlayMove.isKingInCheck()) {
				return 0;
			}
		}
		// choose item to be promoted
		if (validMoveListSize > 1) {
			base.runPopupPromotionFrame(side);
			byte toBePromotedItem = base.getPromotionPanel().getLastChoosenPromotionItem();
			for (int i = 0; i < validMoveListSize; i++) {
				validMove = validMoveList.get(i);
				if (((validMove & 0xF00000) >>> 20) == (int) toBePromotedItem) {
					break;
				}
			}
		}

		return validMove;
	}
	
	public boolean existsValidMove(byte[][] board) {
		ArrayList<Integer> pseudoLegalMoveList = new ArrayList<Integer>();
		int[] pseudoLegalMoves = moveGeneration.generateMoves(Transformer.getBitboardStyl(board), side, epTarget,
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
			GamePlayMove gamePlayMove = new GamePlayMove(base, validMove);
			if (!gamePlayMove.isKingInCheck()) {
				isKingInSafe = true;
				break;
			}
		}
		
		return isKingInSafe;
	}

	public void doMove(int move) {
		if (!PieceEffects.existsActiveTimer()) {
			incrementBoardStateCount();
			isUnimplementMove = false;
			GamePlayMove gamePlayMove = new GamePlayMove(base, move);
			incrementOrResetFiftyMoveCounterIfNecessary(gamePlayMove);
			moveHistory.add(gamePlayMove);
			gamePlayMove.implement();
			reverseTurn();

			if (noAnimation) {
				updateCastlingRights();
			}
		}
	}
	
	private void incrementOrResetFiftyMoveCounterIfNecessary(GamePlayMove gamePlayMove) {
		if (gamePlayMove.isCaptureMove() || gamePlayMove.isPawnMove()) {
			fiftyMoveCounter = 0;
		} else {
			fiftyMoveCounter++;
		}
	}
	
	private void decrementFiftyMoveCounterIfNecessary(GamePlayMove gamePlayMove) {
		if (fiftyMoveCounter > 0) {
			fiftyMoveCounter--;
		}
	}
	
	public void undoMove() {
		if ((!PieceEffects.existsActiveTimer() && moveHistory.size() > 0)) {
			isUnimplementMove = true;
			GamePlayMove gamePlayMove = moveHistory.get(moveHistory.size() - 1);
			decrementFiftyMoveCounterIfNecessary(gamePlayMove);
			moveHistory.remove(moveHistory.size() - 1);
			gamePlayMove.unImplement();
			reverseTurn();
			
			if (noAnimation) {
				updateCastlingRights();
				decrementBoardStateCount();
			}
		}
	}

	public void setSide(int turn) {
		this.side = turn;
		if(base != null){
			base.getControlPanel().refreshTurnView();
		}
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
		base.getControlPanel().refreshTurnView();
	}

	public void init() throws Exception{
		initDatabase();
		base = new BaseGui(this);
		long[] bb = Transformer.getBitboardStyl(base.getBoard());
		zobristKey = TranspositionTable.getZobristKey(bb, getEpTarget(), getCastlingRights(), getSide());
		pawnZobristKey = TranspositionTable.getPawnZobristKey(bb);
		
		//
		new KeyListenerGeneric(null);
		//
	}
	
	public synchronized void triggerThreadFinishEvent() {
		updateCastlingRights();
		if (isUnimplementMove) {
			decrementBoardStateCount();
		}
		
		zobristKey = TranspositionTable.getZobristKey(Transformer.getBitboardStyl(base.getBoard()), getEpTarget(), castlingRights, side);
		
		boolean isCheckMate = false;
		boolean isDraw = false;
		
		if (!existsValidMove(base.getBoard())) {
			if (legality.isKingInCheck(Transformer.getBitboardStyl(base.getChessBoardPanel().getBoard()), side)) {
				System.out.println("CHECKMATE : " + (side == EngineConstants.WHITE ? " Black won" : "White Won"));
				isCheckMate = true;
			} else {
				System.out.println("DRAW : stalemate !");
				isDraw = true;
			}
		} else {
			if (getFiftyMoveCounter() > 99) {
				System.out.println("DRAW : fifty move exceeded.");
				isDraw = true;
			} else {
				Integer boardStateHistoryCount = getBoardStateHistory().get(zobristKey);
				if(boardStateHistoryCount != null && boardStateHistoryCount.intValue() == 2){
					System.out.println("DRAW : threefold repetition.");
					isDraw = true;
				}
			}
			// TODO : Insufficient material.
		}
		
		if (isDraw) {
			blackScore += 1;
			whiteScore += 1;
		} else if(isCheckMate) {
			if (side == EngineConstants.WHITE) {
				blackScore += 2;
			} else {
				whiteScore += 2;
			}
		}
		
		if (isCheckMate || isDraw) {
			try {
				System.out.println("whiteScore = " + whiteScore);
				System.out.println("blackScore = " + blackScore);
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			restartGame();
			
			long s = System.currentTimeMillis();
			chess.fhv2.SearchEngineFifty10 engine = chess.fhv2.SearchEngineFifty10.getInstance();
			
			SearchParameters params = new SearchParameters();
			params.setDepth(base.getDebugPanel().getSearchDepth());
			params.setEpT(getEpTarget());
			params.setEpS(getEpSquare());
			params.setBitboard(Transformer.getBitboardStyl(base.getBoard()));
			params.setPieces(Transformer.getByteArrayStyl(Transformer.getBitboardStyl(base.getBoard())));
			params.setCastlingRights(getCastlingRights());
			params.setSide(getSide());
			params.setUiZobristKey(zobristKey);
			params.setUiPawnZobristKey(pawnZobristKey);
			params.setTimeLimit(Utility.generateStrongRandomNumber(10, 400));
			params.setFiftyMoveCounter(fiftyMoveCounter);
			params.setEngineMode(EngineConstants.EngineMode.FIXED_DEPTH);
			params.setZobristKeyHistory(zobristKeyHistory);
			
			SearchResult searchResult = engine.search(params);
			doMove(searchResult.getBestMove());
			long e = System.currentTimeMillis();
			System.out.println("V3 Engine fark = " + (e - s));
			
		} else {
			
			if(side == GuiConstants.BLACKS_TURN){
				if(base.getControlPanel().isBlackEngineEnabled()){
					
					long s = System.currentTimeMillis();
					chess.fhv2.SearchEngineFifty10 engine = chess.fhv2.SearchEngineFifty10.getInstance();
					//
					//TODO UNCOMMENT HERE.
					//TODO UNCOMMENT HERE.
					//TODO UNCOMMENT HERE.
					//TODO UNCOMMENT HERE.
//					engine.resetTT();
					//
					
					SearchParameters params = new SearchParameters();
					params.setDepth(base.getDebugPanel().getSearchDepth());
					params.setEpT(getEpTarget());
					params.setEpS(getEpSquare());
					params.setBitboard(Transformer.getBitboardStyl(base.getBoard()));
					params.setPieces(Transformer.getByteArrayStyl(Transformer.getBitboardStyl(base.getBoard())));
					params.setCastlingRights(getCastlingRights());
					params.setSide(getSide());
					params.setUiZobristKey(zobristKey);
					params.setUiPawnZobristKey(pawnZobristKey);
					params.setTimeLimit(1000);
					params.setFiftyMoveCounter(fiftyMoveCounter);
					params.setEngineMode(EngineConstants.EngineMode.NON_FIXED_DEPTH);
					params.setZobristKeyHistory(zobristKeyHistory);
					
					SearchResult searchResult = engine.search(params);
					doMove(searchResult.getBestMove());
					long e = System.currentTimeMillis();
					System.out.println("V2 Engine fark = " + (e - s));
				}
			} else {
				if(base.getControlPanel().isWhiteEngineEnabled()){
					
					long s = System.currentTimeMillis();
					chess.fhv2.SearchEngineFifty10 engine = chess.fhv2.SearchEngineFifty10.getInstance();
					//TODO UNCOMMENT HERE.
					//TODO UNCOMMENT HERE.
					//TODO UNCOMMENT HERE.
					//TODO UNCOMMENT HERE.
					//TODO UNCOMMENT HERE.
//					engine.resetTT();
					//
					
					SearchParameters params = new SearchParameters();
					params.setDepth(base.getDebugPanel().getSearchDepth());
					params.setEpT(getEpTarget());
					params.setEpS(getEpSquare());
					params.setBitboard(Transformer.getBitboardStyl(base.getBoard()));
					params.setPieces(Transformer.getByteArrayStyl(Transformer.getBitboardStyl(base.getBoard())));
					params.setCastlingRights(getCastlingRights());
					params.setSide(getSide());
					params.setUiZobristKey(zobristKey);
					params.setUiPawnZobristKey(pawnZobristKey);
					params.setTimeLimit(1000);
					params.setFiftyMoveCounter(fiftyMoveCounter);
					params.setEngineMode(EngineConstants.EngineMode.NON_FIXED_DEPTH);
					params.setZobristKeyHistory(zobristKeyHistory);
					
					SearchResult searchResult = engine.search(params);
					doMove(searchResult.getBestMove());
					long e = System.currentTimeMillis();
					System.out.println("V3 Engine fark = " + (e - s));
				}
			}
		}
		
		
	}
	
	public void restartGame() {
		base.setBoard(DebugUtility.getDefaultBoard());
		resetGameFlags();
		fiftyMoveCounter = 0;
		side = GuiConstants.WHITES_TURN;
		moveHistory.clear();
		
		castlingRights = new byte[][] { { 1, 1 }, { 1, 1 } };
		
		long[] bb = Transformer.getBitboardStyl(base.getBoard());
		zobristKey = TranspositionTable.getZobristKey(bb, getEpTarget(), getCastlingRights(), getSide());
		pawnZobristKey = TranspositionTable.getPawnZobristKey(bb);
		
		boardStateHistory.clear();
		zobristKeyHistory.clear();
		
		isUnimplementMove = false;
	}

	public void resetGameFlags() {
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
	
//	public long getZobristKey() {
//		return zobristKey;
//	}
//	
//	public void setZobristKey(long zobristKey) {
//		this.zobristKey = zobristKey;
//	}
	
	private void updateCastlingRights() {
		long[] bitboard = Transformer.getBitboardStyl(base.getBoard());
		int side = this.side ^ 1;
		int opSide = side ^ 1;
		
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
	
	private void initDatabase() throws Exception {
		dbManager = new Storage();
		preferences = dbManager.retrievePreferences();
	}
	
	public HashMap<String, String> getPreferences() {
		return preferences;
	}
	
	public Storage getDbManager() {
		return dbManager;
	}
	
	public Map<Long, Integer> getBoardStateHistory() {
		return boardStateHistory;
	}
	
	public List<Long> getZobristKeyHistory() {
		return zobristKeyHistory;
	}

	private void incrementBoardStateCount() {
		Integer boardStateCount = boardStateHistory.get(zobristKey);
		if (boardStateCount == null) {
			boardStateHistory.put(zobristKey, 1);
		} else {
			boardStateHistory.put(zobristKey, boardStateCount.intValue() + 1);
		}
		zobristKeyHistory.add(zobristKey);
	}
	
	private void decrementBoardStateCount() {
		Integer boardStateCount = boardStateHistory.get(zobristKey);
		boardStateHistory.put(zobristKey, boardStateCount.intValue() - 1);
		zobristKeyHistory.remove(zobristKeyHistory.size() - 1);
	}

	public long getZobristKey() {
		return zobristKey;
	}
	
	public void setZobristKey(long zobristKey) {
		this.zobristKey = zobristKey;
	}

	public void updateZobristKey(long val) {
		zobristKey = zobristKey ^ val;
	}
	
	public void updatePawnZobristKey(long val) {
		pawnZobristKey = pawnZobristKey ^ val;
	}
	
	public void recalculateZobristKey() {
		long[] bb = Transformer.getBitboardStyl(base.getBoard());
		zobristKey = TranspositionTable.getZobristKey(bb, getEpTarget(), castlingRights, side);
		pawnZobristKey = TranspositionTable.getPawnZobristKey(bb);
	}

	public ArrayList<GamePlayMove> getMoveHistory() {
		return moveHistory;
	}

	public int getFiftyMoveCounter() {
		return fiftyMoveCounter;
	}

	public boolean isNoAnimation() {
		return noAnimation;
	}

	public long getPawnZobristKey() {
		return pawnZobristKey;
	}

	public void setPawnZobristKey(long pawnZobristKey) {
		this.pawnZobristKey = pawnZobristKey;
	}

}
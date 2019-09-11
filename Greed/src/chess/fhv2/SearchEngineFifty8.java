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
package chess.fhv2;

import java.util.Arrays;
import java.util.Map;

import chess.engine.BoardFactory;
import chess.engine.EngineConstants;
import chess.engine.IBoard;
import chess.engine.ISearchableV2;
import chess.engine.LegalityV4;
import chess.engine.MoveGenerationOrderedCapturesOnlyQueenPromotions_SBIV2;
import chess.engine.MoveGenerationOrderedOnlyQueenPromotions_SBIV2;
import chess.engine.OpeningBook;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.engine.TranspositionElement;
import chess.engine.TranspositionTable;
import chess.evaluation.EvaluationAdvancedV4;
import chess.gui.GuiConstants;

//http://web.archive.org/web/20070707012511/http://www.brucemo.com/compchess/programming/index.htm
public class SearchEngineFifty8 implements ISearchableV2, EngineConstants {
	// if there is no castling rights, call move generator that doesn't search castling moves. 			
	// if there is no castling rights, call make move method that doesn't make castling moves. 
	
	
	// ttBestMove is tried first. but then, all possible moves are tried. do we need to try ttBestMove again??
	// ttBestMove is tried first. but then, all possible moves are tried. do we need to try ttBestMove again??
	
	private MoveGenerationOrderedOnlyQueenPromotions_SBIV2 moveGenerationOrdered = new MoveGenerationOrderedOnlyQueenPromotions_SBIV2();
	private MoveGenerationOrderedCapturesOnlyQueenPromotions_SBIV2 moveGenerationCaptures = new MoveGenerationOrderedCapturesOnlyQueenPromotions_SBIV2();
	private LegalityV4 legality = new LegalityV4();
	
	private final int MINUS_INFINITY = -99999;
	private final int PLUS_INFINITY = 99999;
	
	private static final int TT_SIZE = 1048583;
	private static final int HASH_EXACT = 1;
	private static final int HASH_ALPHA = 2;
	private static final int HASH_BETA = 3;
	
	private Map<Long, Integer> boardStateHistory;
	
	private TranspositionElement[] hashTable = new TranspositionElement[TT_SIZE];
	
	//TODO change slot count and comparen performance.
	private int[] primaryKillerss = new int[128]; // The index corresponds to the ply the killer move is located in
	private int[] secondaryKillerss = new int[128];
	
	SearchResult searchResult = new SearchResult();
	
	// This should be variable.
	private static final int R = 2;
	
	private static SearchEngineFifty8 instance;
	private static SearchEngineFifty8[] newInstances = new SearchEngineFifty8[16];
	
	private SearchEngineFifty8(){
		TranspositionTable.fillZobristArrays();
		timeControllerThread.start();
	}
	
	public static SearchEngineFifty8 getInstance() {
		if (instance == null) {
			instance = new SearchEngineFifty8();
		}
		return instance;
	}
	
	public static synchronized SearchEngineFifty8 getNewInstance() {
		for (int i = 0; i < newInstances.length; i++) {
			if (newInstances[i] == null) {
				newInstances[i] = new SearchEngineFifty8();
				return newInstances[i];
			}
		}
		System.out.println("Max instance count exceeded.");
		System.exit(-1);
		throw new RuntimeException();
	}
	
	private void reset() {
		currentDepth = 0;
		searchResult.reset();
		isTimeout = false;
		forceTimeoutRequested = false;
	}
	
	public static boolean forceTimeoutRequested = false;
	
	private boolean isTimeout = false;
	private int currentDepth;
	private long startTime;
	private long timeLimit;
	private int depth;
	
	private TimeController timeController = new TimeController();
	private Thread timeControllerThread = new Thread(timeController);
	
	private EngineMode engineMode;
	
	public SearchResult search(SearchParameters searchParameters) {
		
		this.timeLimit = searchParameters.getTimeLimit();
		this.startTime = System.currentTimeMillis();
		this.depth = searchParameters.getDepth();
		this.engineMode = searchParameters.getEngineMode();
		
		reset();
		
		boolean isFixedDepth = engineMode == EngineMode.FIXED_DEPTH;
		if (!isFixedDepth) {
			timeController.resume();
		}
		
		long zobristKey = TranspositionTable.getZobristKey(searchParameters.getBitboard(), searchParameters.getEpT(), searchParameters.getCastlingRights(), searchParameters.getSide());
		if (searchParameters.getUiZobristKey() != zobristKey) {
			throw new RuntimeException("Zobrist key is incorrect.");
		}
		
		int move = 0;
		for (int i = 1; isFixedDepth ? (i <= depth) : true; i++) {
			currentDepth = i;
			IBoard board = BoardFactory.getInstance(searchParameters.getBitboard(), searchParameters.getPieces(), searchParameters.getEpT(), searchParameters.getEpS(), i, searchParameters.getCastlingRights(), searchParameters.getUiZobristKey(), searchParameters.getFiftyMoveCounter());
			
			if (i == 1 && searchParameters.getBookName() != null) {
//				long s = System.currentTimeMillis();
				int bookMove = OpeningBook.getInstance().findBookMove(board, i + 1, searchParameters.getSide(), searchParameters.getSide() ^ 1, searchParameters.getBookName());
//				long e = System.currentTimeMillis();
//				System.out.println("Opening Book Time Consumed = " + (e - s));
				if (bookMove != 0) {
					move = bookMove;	
					break;
				}
			}
			
			int tempMove = getBestMovee(i, board, searchParameters.getSide(), 0);
			if (isTimeout) {
//				System.out.println("Derinlik ==================================== " + (i - 1));
				break;
			} else {
				move = tempMove;	
			}
			if (i == 30) {
//				System.out.println("YUHHHHHHHH DERÝNLÝÐE BAK = " + i);
				break;
			}
		}
		
//		long usedTTSize = 0;
//		for (int i = 0; i < hashTable.length; i++) {
//			if (hashTable[i] != null) {
//				usedTTSize++;
//			}
//		}
//		System.out.println("usedTTSize = " + usedTTSize);
//		System.out.println("Yüzde = % " + (100 * usedTTSize) / TT_SIZE);
		
		searchResult.setBestMove(move);
		
		return searchResult;
	}
	
	public void resetTT() {
		hashTable = new TranspositionElement[TT_SIZE];
	}
	
	public int getBestMovee(int depth, IBoard board, int side, int distance){
		
		// Board infrastructure.
		board.deepDive(depth);
		// Board infrastructure.
		
		int alpha = MINUS_INFINITY;
		int beta = PLUS_INFINITY;
		
		int bestMove = 0;
		int tempValue;
		int color = side == GuiConstants.WHITES_TURN ? 1 : -1 ;
		int opSide = side ^ 1;
		
		int hashType = HASH_ALPHA;
		
		int ttBestMove = 0;
		TranspositionElement ttElement = hashTable[(int)Math.abs(board.getZobristKey(depth) % TT_SIZE)];
		if(ttElement != null && ttElement.zobristKey == board.getZobristKey(depth)){
			ttBestMove = ttElement.bestMove;
		}
		
		//
		if(ttBestMove != 0){
			
			// Board infrastructure.
			board.doMove(ttBestMove, side, opSide, depth);
			// Board infrastructure.
			
			//
			if (!legality.isKingInCheck(board.getBitboard(), side)) {
				
				tempValue = -negamax(depth - 1, board, side, -color, -beta, -alpha, ttBestMove, ttBestMove, true, distance + 1);
				
				if(tempValue > alpha){
					hashType = HASH_EXACT;
					alpha = tempValue;
					bestMove = ttBestMove;
				}
			}
			//
			
			// Board infrastructure.
			board.undoMove(ttBestMove, side, opSide, depth);
			// Board infrastructure.
			
		}
		//
		
		moveGenerationOrdered.generateMoves(board, side, depth + 1, depth);
		int[] moveList = board.getMoveList(depth);
		Arrays.sort(moveList);
		
		int move;
		for ( int i = EngineConstants.MOVE_LIST_SIZE - 1  ; (move = moveList[i]) != 0 ; i--) {
			
			// Board infrastructure.
			board.doMove(move, side, opSide, depth);
			// Board infrastructure.
			
			//
			if (!legality.isKingInCheck(board.getBitboard(), side)) {
				
				tempValue = -negamax(depth - 1, board, side, -color, -beta, -alpha, move, move, true, distance + 1);
				
				if(tempValue > alpha){
					hashType = HASH_EXACT;
					alpha = tempValue;
					bestMove = move;
				}
			}
			//
			
			// Board infrastructure.
			board.undoMove(move, side, opSide, depth);
			// Board infrastructure.
			
		}
		
		recordTranspositionTable(board.getZobristKey(depth), alpha, bestMove, depth, hashType);
		
		return bestMove;
	}
	
	private boolean isLeadsToDraw(long zobristKey, IBoard board, int depth) {
		if (board.hasRepeated(zobristKey, depth)) {
			return true;
		}
		Integer boardStateHistoryCount = boardStateHistory.get(zobristKey);
		if(boardStateHistoryCount != null && boardStateHistoryCount.intValue() >= 1){
			return true;
		}
		return false;
	}
	
	public void addKiller(int move, int depth) {
		if (primaryKillerss[depth] != move) {
			secondaryKillerss[depth] = primaryKillerss[depth];
			primaryKillerss[depth] = move;				
		}
	}
	
	public int negamax(int depth, IBoard board, int side, int color, int alpha, int beta, int previousMove, int firstMove, boolean allowNullMove, int distance){
		
		if (isTimeout) {
			return 0;
		}
		
		int hashType = HASH_ALPHA;
		
		// Board infrastructure.
		board.deepDive(depth);
		// Board infrastructure.
		
		long zobristKey = board.getZobristKey(depth);
		
		if (isLeadsToDraw(zobristKey, board, depth)) {
			return 0;
		}
		
		//>>
		int ttBestMove = 0;
		TranspositionElement ttElement = hashTable[(int)Math.abs(zobristKey % TT_SIZE)];
		if(ttElement != null && ttElement.zobristKey == zobristKey){
			if(ttElement.depth >= depth){
				switch (ttElement.hashType) {
				case HASH_EXACT:
					return ttElement.score;
				case HASH_ALPHA:
					if (ttElement.score <= alpha) {
						return alpha;
					}
					break;
				case HASH_BETA:
					if (ttElement.score >= beta) {
						return beta;
					}
					break;
				}
			}
			ttBestMove = ttElement.bestMove;
		}
		//<<
		
		// check extension.
		boolean isKingInCheck = legality.isKingInCheck(board.getBitboard(), side ^ 1);
		if(!isKingInCheck && depth <= 0){
			searchResult.incrementEvaluatedLeafNodeCount();
			return quiescentSearch(board, side, color, alpha, beta, depth);
		}
//		
		
		int opSide = side;
		side = side ^ 1;
		int bestMove = 0;
		int tempValue;
		
		//
		boolean existsLegalMove = false;
		//
		
		//=>
		boolean foundPv = false;
		//=<
		
		//=>> NullMove Begin
		if (!isKingInCheck && allowNullMove && depth > 2) {

			board.doNullMove(depth, side);
			
			board.deeperDive(depth - 1);
			board.deeperDive(depth - 2);
			
			tempValue = -negamax(depth - 1 - R, board, side, -color, -beta, -beta + 1, ttBestMove, firstMove, false, distance + 1);
			
			board.undoNullMove(depth);
			
			
			if (tempValue >= beta) {
				return beta;
			}
		}
		//=>> NullMove End
		
		
		//
		if(ttBestMove != 0){

			// Board infrastructure.
			board.doMove(ttBestMove, side, opSide, depth);
			// Board infrastructure.
			
			if (!legality.isKingInCheck(board.getBitboard(), side)) {
				
				existsLegalMove = true;
				
				tempValue = -negamax(depth - 1, board, side, -color, -beta, -alpha, ttBestMove, firstMove, true, distance + 1);
				
				if (tempValue >= beta) {
					
					// Board infrastructure.
					board.undoMove(ttBestMove, side, opSide, depth);
					// Board infrastructure.
					
					recordTranspositionTable(zobristKey, beta, ttBestMove, depth, HASH_BETA);
					
					//TODO : hash move? maybe it will be overwritten.
					//TODO: capture moves should be handled by see, not killer heuristic.
					addKiller(ttBestMove, distance);
					
					return beta;
				}
				
				if (tempValue > alpha) {
					alpha = tempValue;
					bestMove = ttBestMove;
					hashType = HASH_EXACT;
					foundPv = true;
				}
				
			}
			
			// Board infrastructure.
			board.undoMove(ttBestMove, side, opSide, depth);
			// Board infrastructure.
			
		}
		//
		
		moveGenerationOrdered.generateMoves(board, side, depth + 1, depth);
		int[] moveList = board.getMoveList(depth);
		
		sortMoves(moveList, distance);
		
		int move;
		for ( int i = EngineConstants.MOVE_LIST_SIZE - 1  ; (move = moveList[i]) != 0 ; i--) {
			
			// Board infrastructure.
			board.doMove(move, side, opSide, depth);
			// Board infrastructure.
			
			if (!legality.isKingInCheck(board.getBitboard(), side)) {
			
				existsLegalMove = true;
				
				if (foundPv) {
					tempValue = -negamax(depth - 1, board, side, -color, -alpha - 1, -alpha, move, firstMove, true, distance + 1);
					if (tempValue > alpha) {
						tempValue = -negamax(depth - 1, board, side, -color, -beta, -alpha, move, firstMove, true, distance + 1);
					}
				} else {
					tempValue = -negamax(depth - 1, board, side, -color, -beta, -alpha, move, firstMove, true, distance + 1);
				}
				
				if (tempValue >= beta) {
					// Board infrastructure.
					board.undoMove(move, side, opSide, depth);
					// Board infrastructure.
					
					recordTranspositionTable(zobristKey, beta, move, depth, HASH_BETA);
					
					//TODO: capture moves should be handled by see, not killer heuristic.
					addKiller(move, distance);
					
					return beta;
					
				}
				
				if(tempValue > alpha){
					alpha = tempValue;
					bestMove = move;
					hashType = HASH_EXACT;
					foundPv = true;
				}
				
			}
			
			// Board infrastructure.
			board.undoMove(move, side, opSide, depth);
			// Board infrastructure.
		}
		
		if (!existsLegalMove) {
			if (legality.isKingInCheck(board.getBitboard(), side)) {
				return MINUS_INFINITY + distance;
//				return -99999;
			} else {
				// TODO return contempt factor.
				return 0;
			}
		}
		
		recordTranspositionTable(zobristKey, alpha, bestMove, depth, hashType);
		
		return alpha;
	}
	
	private int quiescentSearch(IBoard board, int side, int color, int alpha, int beta, int depth){
		
		//
//		boolean existsLegalMove = false;
		//
		
		int standPatScore =  color * EvaluationAdvancedV4.evaluate(board.getBitboard(), board.getCastlingRights(depth + 1), side ^ 1);
		
		if(standPatScore >= beta){
			return beta;
		}
		
		if(standPatScore > alpha){
			alpha = standPatScore;
		}
		
		int opSide = side;
		side = side ^ 1;
		
		// Board infrastructure.
		board.deepDive(depth);
		// Board infrastructure.
		
		boolean foundPv = false;
		
		
		moveGenerationCaptures.generateMoves(board, side, depth + 1, depth);
		int[] moveList = board.getMoveList(depth);
		
		Arrays.sort(moveList);
		int tempValue;
		int move;
		for ( int i = EngineConstants.MOVE_LIST_SIZE - 1  ; (move = moveList[i]) != 0 ; i--) {
			
			// Board infrastructure.
			board.doMoveWithoutZobrist(move, side, opSide, depth);
			// Board infrastructure.
			
			if (!legality.isKingInCheck(board.getBitboard(), side)) {
				
//				existsLegalMove = true;
				
				// No Need to check threeFold Repetition here only for now. Maybe future relases search some tactical moves (Not Only Capture Moves.)
				// No Need to check threeFold Repetition here only for now. Maybe future relases search some tactical moves (Not Only Capture Moves.)
				// No Need to check threeFold Repetition here only for now. Maybe future relases search some tactical moves (Not Only Capture Moves.)
//				if (isLeadsToThreeFoldRepetition()) {
//					tempValue = 0;
//				} else {
//					tempValue = -quiescentSearch(board, side, -color, -beta, -alpha, depth - 1);
//				}
					
					if (foundPv) {
						tempValue = -quiescentSearch(board, side, -color, -alpha - 1, -alpha, depth - 1);
						if (tempValue > alpha) {
							tempValue = -quiescentSearch(board, side, -color, -beta, -alpha, depth - 1);
						}
					} else {
						tempValue = -quiescentSearch(board, side, -color, -beta, -alpha, depth - 1);
					}
				
				if(tempValue >= beta){
					
					// Board infrastructure.
					board.undoMoveWithoutZobrist(move, side, opSide, depth);
					// Board infrastructure.
					
					return beta;
				}
				if(tempValue > alpha){
					alpha = tempValue;
					foundPv = true;
				}
			}
			
			// Board infrastructure.
			board.undoMoveWithoutZobrist(move, side, opSide, depth);
			// Board infrastructure.
		}
		
//		if (!existsLegalMove) {
////			System.out.println("Mate Detected.");
//			return color * (side == GuiConstants.WHITES_TURN ? -99999 : 99999);
//		}
		
		return alpha;
	}
	
	private void recordTranspositionTable(long zobristKey, int value, int bestMove, int depth, int hashType){
		if (isTimeout) {
			return;
		}
		int index = (int)Math.abs(zobristKey % hashTable.length);
		TranspositionElement ttElement = hashTable[index];
		if(ttElement != null){
//			if(ttElement.depth <= depth){ // try only greater than...
				ttElement.zobristKey = zobristKey;
				ttElement.score = value;
				ttElement.depth = depth;
				ttElement.bestMove = bestMove;
				ttElement.hashType = hashType;
//			}
		} else {
			ttElement = new TranspositionElement(); // Maybe when engine initiates, instantiate all Transposition object. in order to reduce new object cost.
			ttElement.zobristKey = zobristKey;
			ttElement.score = value;
			ttElement.depth = depth;
			ttElement.bestMove = bestMove;
			ttElement.hashType = hashType;
			hashTable[index] = ttElement;
		}
	}

	public void setBoardStateHistory(Map<Long, Integer> boardStateHistory) {
		this.boardStateHistory = boardStateHistory;
	}
	
	public void sortMoves(int[] moveList, int distance) {
		
		Arrays.sort(moveList);
		
		int primaryKiller = primaryKillerss[distance];
		int secondaryKiller = secondaryKillerss[distance];
		
		int sizeMinusOne = EngineConstants.MOVE_LIST_SIZE - 1;
		
		int move;
		for ( int i = sizeMinusOne  ; (move = moveList[i]) != 0 ; i--) {
			if (move == primaryKiller) {
				System.arraycopy(moveList, i + 1, moveList, i, sizeMinusOne - i);
				moveList[sizeMinusOne] = primaryKiller;
			} else if(move == secondaryKiller) {
				System.arraycopy(moveList, i + 1, moveList, i, sizeMinusOne - i);
				moveList[sizeMinusOne] = secondaryKiller;
			}
		}
	}
	
	public class TimeController implements Runnable {

		private volatile boolean suspended = true;
		
		@Override
		public void run() {
			waitIfSuspended();
			while (true) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (((System.currentTimeMillis() - startTime) >= timeLimit || forceTimeoutRequested) && currentDepth > depth) {
					isTimeout = true;
					suspend();
					waitIfSuspended();
				}
			}
		}
		
		private void waitIfSuspended() {
			synchronized (this) {
				while (suspended) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		public synchronized void suspend() {
			suspended = true;
		}

		public synchronized void resume() {
			suspended = false;
			notify();
		}
	}
}
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

import chess.engine.BoardFactory;
import chess.engine.CompileTimeConstants;
import chess.engine.EngineConstants;
import chess.engine.IBoard;
import chess.engine.ISearchableV2;
import chess.engine.Material;
import chess.engine.Move;
import chess.engine.OpeningBook;
import chess.engine.PawnHashTable;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.engine.TT;
import chess.engine.TranspositionElement;
import chess.engine.TranspositionTable;
import chess.engine.test.Assertion;
import chess.evaluation.EvaluationAdvancedV4;
import chess.movegen.MoveGeneration;

//http://web.archive.org/web/20070707012511/http://www.brucemo.com/compchess/programming/index.htm
public class SearchEngineMordering implements ISearchableV2, EngineConstants {
	private MoveGeneration moveGeneration = new MoveGeneration(false);
	private OpeningBook openingBook = OpeningBook.getNewInstance();
	
	private final int MINUS_INFINITY = -99999;
	private final int PLUS_INFINITY = 99999;
	
	private int[] primaryKillerss = new int[128]; // The index corresponds to the ply the killer move is located in
	private int[] secondaryKillerss = new int[128];
	
	SearchResult searchResult = new SearchResult();
	
	private static SearchEngineMordering instance;
	private static SearchEngineMordering[] newInstances = new SearchEngineMordering[16];
	
	private static final int MOVE_ORDERING_TT = 0;
	private static final int MOVE_ORDERING_ATTACKING = 1;
	private static final int MOVE_ORDERING_KILLER1 = 2;
	private static final int MOVE_ORDERING_KILLER2 = 3;
	private static final int MOVE_ORDERING_QUIET = 4;
	private static final int MOVE_ORDERING_UPPER_BOUND = 5;
	
	private SearchEngineMordering(){
		TranspositionTable.fillZobristArrays();
		timeControllerThread.start();
	}
	
	public static SearchEngineMordering getInstance() {
		if (instance == null) {
			instance = new SearchEngineMordering();
		}
		return instance;
	}
	
	public static synchronized SearchEngineMordering getNewInstance() {
		for (int i = 0; i < newInstances.length; i++) {
			if (newInstances[i] == null) {
				newInstances[i] = new SearchEngineMordering();
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
	
	
	private boolean isTimeout = false;
	private int currentDepth;
	private long startTime;
	private long timeLimit;
	private int depth;
	private boolean forceTimeoutRequested = false;
	
	private ICallBackTimeout callBackTimeOut = new ICallBackTimeout(){
		public void onTimeout() {
			isTimeout = true;
		}
		public boolean evaluateTimeoutCondition() {
			return ((System.currentTimeMillis() - startTime) >= timeLimit || forceTimeoutRequested) && currentDepth > depth;
		}};
		
	private TimeController timeController = new TimeController(callBackTimeOut);
	private Thread timeControllerThread = new Thread(timeController);
	
	private EngineMode engineMode;
	
	private TT tt = new TT();
	private PawnHashTable pawnHashTable = new PawnHashTable();
	
	private static final int FUTILITY_MARGIN = 200;
	
	// Margins shamelessly stolen from chess22k
	private static final int[] STATIC_NULLMOVE_MARGIN = { 0, 60, 130, 210, 300, 400, 510 };
//	private static final int[] RAZORING_MARGIN = { 0, 240, 280, 300 };
	
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
		
		long pawnZobristKey = TranspositionTable.getPawnZobristKey(searchParameters.getBitboard());
		
		if (searchParameters.getUiPawnZobristKey() != pawnZobristKey) {
			throw new RuntimeException("Pawn Zobrist key is incorrect.");
		}
		
		int move = 0;
		for (int i = 1; isFixedDepth ? (i <= depth) : true; i++) {
			currentDepth = i;
			
			IBoard board = BoardFactory.getInstance2(searchParameters);
			
			if (i == 1 && searchParameters.getBookName() != null) {
//				long s = System.currentTimeMillis();
				int bookMove = openingBook.findBookMove(board, i + 1, searchParameters.getSide(), searchParameters.getSide() ^ 1, searchParameters.getBookName());
//				long e = System.currentTimeMillis();
//				System.out.println("Opening Book Time Consumed = " + (e - s));
				if (bookMove != 0) {
					move = bookMove;
					searchResult.setBookMove(true);
					break;
				}
			}
			
			int tempMove = getBestMovee(i, board, 0);
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
		
		
		searchResult.setBestMove(move);
		
		searchResult.setTimeConsumed(System.currentTimeMillis() - startTime);
		return searchResult;
	}
	
	public void resetTT() {
		tt.resetTT();
		pawnHashTable.resetTT();
	}
	
	public int getBestMovee(int depth, IBoard board, int distance){
		
		if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
			searchResult.incrementNegamaxNodeCount();
		}
		
		int alpha = MINUS_INFINITY;
		int beta = PLUS_INFINITY;
		
		int bestMove = 0;
		int tempValue;
		
		int hashType = HASH_ALPHA;
		
		int ttBestMove = 0;
		TranspositionElement ttElement = tt.probe(board.getZobristKey());
		if(ttElement != null && ttElement.zobristKey == board.getZobristKey()){
			if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
				searchResult.incrementTtHitCount();
			}
			ttBestMove = ttElement.bestMove;
		} else {
			if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
				searchResult.incrementTtMissCount();
			}
		}
		
		if(ttBestMove != 0){
			board.doMove(ttBestMove);
			
			tempValue = -negamax(depth - 1, board, -beta, -alpha, true, distance + 1);
			
			if(tempValue > alpha){
				hashType = HASH_EXACT;
				alpha = tempValue;
				bestMove = ttBestMove;
			}
			
			board.undoMove(ttBestMove);
		}
		
		moveGeneration.startPly();
		moveGeneration.generateAttacks(board);
		moveGeneration.generateMoves(board);
		moveGeneration.setMvvLvaScores();
		moveGeneration.sort();
		
		int move;
		while (moveGeneration.hasNext()) {
			move = moveGeneration.next();
			
			if (!board.isLegal(move)) {
				continue;
			}
			
			board.doMove(move);
			tempValue = -negamax(depth - 1, board, -beta, -alpha, true, distance + 1);
			if(tempValue > alpha){
				hashType = HASH_EXACT;
				alpha = tempValue;
				bestMove = move;
			}
			board.undoMove(move);
		}
		
		moveGeneration.endPly();
		
		tt.recordTranspositionTable(board.getZobristKey(), alpha, bestMove, depth, hashType, isTimeout);
		return bestMove;
	}
	
	public void addKiller(int move, int depth) {
		if (primaryKillerss[depth] != move) {
			secondaryKillerss[depth] = primaryKillerss[depth];
			primaryKillerss[depth] = move;				
		}
	}
	
	public int negamax(int depth, IBoard board, int alpha, int beta, boolean allowNullMove, int distance){
		
		if (isTimeout) {
			return 0;
		}
		
		if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
			searchResult.incrementNegamaxNodeCount();
		}
		
		int hashType = HASH_ALPHA;
		long zobristKey = board.getZobristKey();
		if (board.hasRepeated(zobristKey, searchResult)) {
			return 0;
		}
		
		int ttBestMove = 0;
		int ttScore = 0;
		TranspositionElement ttElement = tt.probe(zobristKey);
		if(ttElement != null && ttElement.zobristKey == zobristKey){
			if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
				searchResult.incrementTtHitCount();
			}
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
			ttScore = ttElement.score;
		} else {
			if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
				searchResult.incrementTtMissCount();
			}
		}
		
		boolean isKingInCheck = board.getCheckers() != 0;
//		boolean isKingInCheck = legality.isKingInCheck(board.getBitboard(), board.getSide());
		if(!isKingInCheck && depth <= 0){
			return quiescentSearch(board, alpha, beta);
		}
		
		int bestMove = 0;
		int tempValue;
		boolean existsLegalMove = false;
		boolean foundPv = false;
		
		
		// TODO: && !isPvNode
		if (!isKingInCheck) {
			int eval =  EngineConstants.SIDE_COLOR[board.getSide()]
					* EvaluationAdvancedV4.evaluate(board.getBitboard(), board.getCastlingRights(), board.getSide(), board.getPawnZobristKey(), pawnHashTable, searchResult);
			
			// TODO: Comment out here.
			if (ttScore != 0) {
				eval = tt.adjustEval(ttElement.hashType, eval, ttScore);
			}
			
			// https://github.com/sandermvdb/chess22k
			if (CompileTimeConstants.ENABLE_STATIC_NULL_MOVE_PRUNING && depth < STATIC_NULLMOVE_MARGIN.length) {
				if (eval - STATIC_NULLMOVE_MARGIN[depth] >= beta) {
					if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
						searchResult.incrementStaticNullMovePruningCount();
					}
					return beta;
				}
			}
			
//			if (CompileTimeConstants.ENABLE_RAZORING && depth < RAZORING_MARGIN.length && Math.abs(alpha) < EngineConstants.SCORE_MATE_BOUND) {
//				if (eval + RAZORING_MARGIN[depth] < alpha) {
//					int score = quiescentSearch(board, alpha - RAZORING_MARGIN[depth], alpha - RAZORING_MARGIN[depth] + 1);
//					if (score + RAZORING_MARGIN[depth] <= alpha) {
//						return alpha;
//					}
//				}
//			}
			
			if (CompileTimeConstants.ENABLE_NULL_MOVE_PRUNING) {
				//=> NullMove Begin
				if (allowNullMove && eval >= beta && Material.hasMajorPiece(board.getMaterialKey(), board.getSide())) {
					
					board.doNullMove();
					final int reduction = depth / 4 + 3 + Math.min((eval - beta) / 80, 3);
					tempValue = depth - reduction <= 0 ? -quiescentSearch(board, -beta, -beta + 1) : -negamax(depth - reduction, board, -beta, -beta + 1, false, distance + 1);
					board.undoNullMove();
					
					if (tempValue >= beta) {
						if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
							searchResult.incrementNullMoveHitCount();
						}
						return beta;
					}
					
					if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
						searchResult.incrementNullMoveMissCount();
					}
				}
				//=> NullMove End
			}
		}
		
		//***
		moveGeneration.startPly();
		int order = MOVE_ORDERING_TT;
		int primaryKiller = 0;
		int secondaryKiller = 0;
		
		while (order < MOVE_ORDERING_UPPER_BOUND) {
			
			switch (order) {
			case MOVE_ORDERING_TT:
				if (ttBestMove != 0) {
					moveGeneration.addMove(ttBestMove);
				}
				break;
			case MOVE_ORDERING_ATTACKING:
				moveGeneration.generateAttacks(board);
				moveGeneration.setMvvLvaScores();
				moveGeneration.sort();
				break;
			case MOVE_ORDERING_KILLER1:
				primaryKiller = primaryKillerss[distance];
				if (primaryKiller != 0 && ttBestMove != primaryKiller && board.isValid(primaryKiller))  {
					if (CompileTimeConstants.ENABLE_ASSERTION) {
						Assertion.assertTrue(board.isLegal(primaryKiller));
					}
					moveGeneration.addMove(primaryKiller);
					break;
				}
				order++;
			case MOVE_ORDERING_KILLER2:
				secondaryKiller = secondaryKillerss[distance];
				if (secondaryKiller != 0 && ttBestMove != secondaryKiller && board.isValid(secondaryKiller))  {
					if (CompileTimeConstants.ENABLE_ASSERTION) {
						Assertion.assertTrue(board.isLegal(secondaryKiller));
					}
					moveGeneration.addMove(secondaryKiller);
					break;
				}
				order++;
			case MOVE_ORDERING_QUIET:
				moveGeneration.generateMoves(board);
				moveGeneration.setMvvLvaScores();
				moveGeneration.sort();
				break;
			}
			
			int move;
			while (moveGeneration.hasNext()) {
				move = moveGeneration.next();
				
				if (order == MOVE_ORDERING_QUIET) {
					if (move == ttBestMove || move == primaryKiller || move == secondaryKiller || !board.isLegal(move)) {
						continue;	
					}
				} else if (order == MOVE_ORDERING_ATTACKING) {
					if (move == ttBestMove || !board.isLegal(move)) {
						continue;	
					}
				}
				
				board.doMove(move);
				
				existsLegalMove = true;
				if (foundPv) {
					tempValue = -negamax(depth - 1, board, -alpha - 1, -alpha, true, distance + 1);
					if (tempValue > alpha) {
						tempValue = -negamax(depth - 1, board, -beta, -alpha, true, distance + 1);
					}
				} else {
					tempValue = -negamax(depth - 1, board, -beta, -alpha, true, distance + 1);
				}
				
				if (tempValue >= beta) {
					board.undoMove(move);
					tt.recordTranspositionTable(zobristKey, beta, move, depth, HASH_BETA, isTimeout);
					if (Move.getCapturedPiece(move) == 0 && !Move.isPromotion(move)) {
						addKiller(move, distance);
					}
					moveGeneration.endPly();
					if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
						searchResult.incrementBetaCutoffCount();
					}
					return beta;
				}
				
				if (tempValue > alpha) {
					alpha = tempValue;
					bestMove = move;
					hashType = HASH_EXACT;
					foundPv = true;
				}
				board.undoMove(move);
			}
			
			order++;
		}
		
		//***
		
		moveGeneration.endPly();
		
		if (!existsLegalMove) {
			if (isKingInCheck) {
				if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
					searchResult.incrementCheckMateCount();
				}
				return MINUS_INFINITY + distance;
			} else {
				if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
					searchResult.incrementStaleMateCount();
				}
				return 0;
			}
		}
		
		tt.recordTranspositionTable(zobristKey, alpha, bestMove, depth, hashType, isTimeout);
		return alpha;
	}
	
	private int quiescentSearch(IBoard board, int alpha, int beta){
		
		if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
			searchResult.incrementQuiescenceNodeCount();
		}
		
		int standPatScore =  EngineConstants.SIDE_COLOR[board.getSide()] * EvaluationAdvancedV4.evaluate(board.getBitboard(), board.getCastlingRights(), board.getSide(), board.getPawnZobristKey(), pawnHashTable, searchResult);
		
		if(standPatScore >= beta){
			return beta;
		}
		
		if(standPatScore > alpha){
			alpha = standPatScore;
		}
		
		boolean foundPv = false;
		
		moveGeneration.startPly();
		moveGeneration.generateAttacks(board);
		moveGeneration.setMvvLvaScores();
		moveGeneration.sort();
		int tempValue;
		int move;
		while (moveGeneration.hasNext()) {
			move = moveGeneration.next();
			// https://github.com/sandermvdb/chess22k
			if (CompileTimeConstants.ENABLE_QUIESCENCE_FUTILITY_PRUNING) {
				switch (Move.getMoveType(move)) {
				case EngineConstants.PROMOTION_SHIFTED:
					break;
				case EngineConstants.EP_CAPTURE_SHIFTED:
					if (standPatScore + FUTILITY_MARGIN + EngineConstants.WHITE_PAWN_V < alpha) {
						continue;
					}
					break;
				default:
					if (standPatScore + FUTILITY_MARGIN + EngineConstants.PIECE_VALUES_POSITIVE[board.getPieces()[Move.getTo(move)]] < alpha) {
						continue;
					}
					break;					
				}
			}
			
			if (!board.isLegal(move)) {
				continue;
			}
			
//			if (!board.isDiscoveredMove(Move.getFrom(move)) && StaticExchangeEvaluation.getSeeCaptureScore(move, board, board.getSide()) <= 0) {
//				continue;
//			}
			
			board.doMoveWithoutZobrist(move);
			if (foundPv) {
				tempValue = -quiescentSearch(board, -alpha - 1, -alpha);
				if (tempValue > alpha) {
					tempValue = -quiescentSearch(board, -beta, -alpha);
				}
			} else {
				tempValue = -quiescentSearch(board, -beta, -alpha);
			}
			
			if(tempValue >= beta){
				board.undoMoveWithoutZobrist(move);
				moveGeneration.endPly();
				if (CompileTimeConstants.DETAILED_SEARCH_RESULT) {
					searchResult.incrementBetaCutoffCount();
				}
				return beta;
			}
			if(tempValue > alpha){
				alpha = tempValue;
				foundPv = true;
			}
			board.undoMoveWithoutZobrist(move);
		}
		
		moveGeneration.endPly();
		
		return alpha;
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

	public void setForceTimeoutRequested(boolean forceTimeoutRequested) {
		this.forceTimeoutRequested = forceTimeoutRequested;
	}

}
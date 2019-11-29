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
import chess.engine.LegalityV4;
import chess.engine.Move;
import chess.engine.MoveGenerationOrderedCapturesOnlyQueenPromotions_SBIV2;
import chess.engine.MoveGenerationOrderedOnlyQueenPromotions_SBIV2;
import chess.engine.OpeningBook;
import chess.engine.PawnHashTable;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.engine.TT;
import chess.engine.TranspositionElement;
import chess.engine.TranspositionTable;
import chess.evaluation.EvaluationAdvancedV4;

//http://web.archive.org/web/20070707012511/http://www.brucemo.com/compchess/programming/index.htm
public class SearchEngineFifty10 implements ISearchableV2, EngineConstants {
	private MoveGenerationOrderedOnlyQueenPromotions_SBIV2 moveGenerationOrdered = new MoveGenerationOrderedOnlyQueenPromotions_SBIV2();
	private MoveGenerationOrderedCapturesOnlyQueenPromotions_SBIV2 moveGenerationCaptures = new MoveGenerationOrderedCapturesOnlyQueenPromotions_SBIV2();
	private LegalityV4 legality = new LegalityV4();
	
	private final int MINUS_INFINITY = -99999;
	private final int PLUS_INFINITY = 99999;
	
	private static final int HASH_EXACT = 1;
	private static final int HASH_ALPHA = 2;
	private static final int HASH_BETA = 3;
	
	private int[] primaryKillerss = new int[128]; // The index corresponds to the ply the killer move is located in
	private int[] secondaryKillerss = new int[128];
	
	SearchResult searchResult = new SearchResult();
	
	// This should be variable.
	private static final int R = 2;
	
	private static SearchEngineFifty10 instance;
	private static SearchEngineFifty10[] newInstances = new SearchEngineFifty10[16];
	
	private SearchEngineFifty10(){
		TranspositionTable.fillZobristArrays();
		timeControllerThread.start();
	}
	
	public static SearchEngineFifty10 getInstance() {
		if (instance == null) {
			instance = new SearchEngineFifty10();
		}
		return instance;
	}
	
	public static synchronized SearchEngineFifty10 getNewInstance() {
		for (int i = 0; i < newInstances.length; i++) {
			if (newInstances[i] == null) {
				newInstances[i] = new SearchEngineFifty10();
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
			
			IBoard board = BoardFactory.getInstance(searchParameters);
			
			if (i == 1 && searchParameters.getBookName() != null) {
//				long s = System.currentTimeMillis();
				int bookMove = OpeningBook.getInstance().findBookMove(board, i + 1, searchParameters.getSide(), searchParameters.getSide() ^ 1, searchParameters.getBookName());
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
		
		int alpha = MINUS_INFINITY;
		int beta = PLUS_INFINITY;
		
		int bestMove = 0;
		int tempValue;
		
		int hashType = HASH_ALPHA;
		
		int ttBestMove = 0;
		TranspositionElement ttElement = tt.probe(board.getZobristKey());
		if(ttElement != null && ttElement.zobristKey == board.getZobristKey()){
			ttBestMove = ttElement.bestMove;
		}
		
		if(ttBestMove != 0){
			board.doMove(ttBestMove);
			
			if (!legality.isKingInCheck(board.getBitboard(), board.getOpSide())) {
				tempValue = -negamax(depth - 1, board, -beta, -alpha, true, distance + 1);
				
				if(tempValue > alpha){
					hashType = HASH_EXACT;
					alpha = tempValue;
					bestMove = ttBestMove;
				}
			}
			
			board.undoMove(ttBestMove);
		}
		
		moveGenerationOrdered.generateMoves(board, depth + 1, depth);
		int[] moveList = board.getMoveList();
		Arrays.sort(moveList);
		
		int move;
		for ( int i = EngineConstants.MOVE_LIST_SIZE - 1  ; (move = moveList[i]) != 0 ; i--) {
			board.doMove(move);
			if (!legality.isKingInCheck(board.getBitboard(), board.getOpSide())) {
				tempValue = -negamax(depth - 1, board, -beta, -alpha, true, distance + 1);
				if(tempValue > alpha){
					hashType = HASH_EXACT;
					alpha = tempValue;
					bestMove = move;
				}
			}
			board.undoMove(move);
		}
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
		
		int hashType = HASH_ALPHA;
		long zobristKey = board.getZobristKey();
		if (board.hasRepeated(zobristKey)) {
			return 0;
		}
		
		int ttBestMove = 0;
		TranspositionElement ttElement = tt.probe(zobristKey);
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
		
		boolean isKingInCheck = legality.isKingInCheck(board.getBitboard(), board.getSide());
		if(!isKingInCheck && depth <= 0){
			searchResult.incrementEvaluatedLeafNodeCount();
			return quiescentSearch(board, alpha, beta, depth);
		}
		
		int bestMove = 0;
		int tempValue;
		boolean existsLegalMove = false;
		boolean foundPv = false;
		
		if (CompileTimeConstants.ENABLE_NULL_MOVE_PRUNING) {
			//=>> NullMove Begin
			if (!isKingInCheck && allowNullMove && depth > 2) {
				
				board.doNullMove();
				tempValue = -negamax(depth - 1 - R, board, -beta, -beta + 1, false, distance + 1);
				board.undoNullMove();
				
				if (tempValue >= beta) {
					return beta;
				}
			}
			//=>> NullMove End
		}
		
		//
		if(ttBestMove != 0){
			board.doMove(ttBestMove);
			
			if (!legality.isKingInCheck(board.getBitboard(), board.getOpSide())) {
				existsLegalMove = true;
				tempValue = -negamax(depth - 1, board, -beta, -alpha, true, distance + 1);
				
				if (tempValue >= beta) {
					board.undoMove(ttBestMove);
					tt.recordTranspositionTable(zobristKey, beta, ttBestMove, depth, HASH_BETA, isTimeout);
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
			board.undoMove(ttBestMove);
		}
		
		moveGenerationOrdered.generateMoves(board, depth + 1, depth);
		int[] moveList = board.getMoveList();
		
		sortMoves(moveList, distance);
		
		int move;
		for ( int i = EngineConstants.MOVE_LIST_SIZE - 1  ; (move = moveList[i]) != 0 ; i--) {
			board.doMove(move);
			
			if (!legality.isKingInCheck(board.getBitboard(), board.getOpSide())) {
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
			board.undoMove(move);
		}
		
		if (!existsLegalMove) {
			if (legality.isKingInCheck(board.getBitboard(), board.getSide())) {
				return MINUS_INFINITY + distance;
			} else {
				return 0;
			}
		}
		
		tt.recordTranspositionTable(zobristKey, alpha, bestMove, depth, hashType, isTimeout);
		return alpha;
	}
	
	private int quiescentSearch(IBoard board, int alpha, int beta, int depth){
		int standPatScore =  EngineConstants.SIDE_COLOR[board.getSide()] * EvaluationAdvancedV4.evaluate(board.getBitboard(), board.getCastlingRights(), board.getSide(), board.getPawnZobristKey(), pawnHashTable);
		
		if(standPatScore >= beta){
			return beta;
		}
		
		if(standPatScore > alpha){
			alpha = standPatScore;
		}
		
		boolean foundPv = false;
		
		moveGenerationCaptures.generateMoves(board, depth + 1, depth);
		int[] moveList = board.getMoveList();
		
		Arrays.sort(moveList);
		int tempValue;
		int move;
		for ( int i = EngineConstants.MOVE_LIST_SIZE - 1  ; (move = moveList[i]) != 0 ; i--) {
			
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
			
			board.doMoveWithoutZobrist(move);
			if (!legality.isKingInCheck(board.getBitboard(), board.getOpSide())) {
				if (foundPv) {
					tempValue = -quiescentSearch(board, -alpha - 1, -alpha, depth - 1);
					if (tempValue > alpha) {
						tempValue = -quiescentSearch(board, -beta, -alpha, depth - 1);
					}
				} else {
					tempValue = -quiescentSearch(board, -beta, -alpha, depth - 1);
				}
				
				if(tempValue >= beta){
					board.undoMoveWithoutZobrist(move);
					return beta;
				}
				if(tempValue > alpha){
					alpha = tempValue;
					foundPv = true;
				}
			}
			board.undoMoveWithoutZobrist(move);
		}
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
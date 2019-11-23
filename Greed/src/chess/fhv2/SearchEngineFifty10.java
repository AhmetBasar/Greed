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

import chess.engine.BoardV7;
import chess.engine.CompileTimeConstants;
import chess.engine.EngineConstants;
import chess.engine.ISearchableV2;
import chess.engine.LegalityV4;
import chess.engine.Move;
import chess.engine.MoveGenerationOrderedCapturesOnlyQueenPromotions_SBIV3;
import chess.engine.MoveGenerationOrderedOnlyQueenPromotions_SBIV3;
import chess.engine.OpeningBook2;
import chess.engine.PawnHashTable;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.engine.TT;
import chess.engine.TranspositionElement;
import chess.engine.TranspositionTable;
import chess.evaluation.EvaluationAdvancedV4;
import chess.gui.GuiConstants;

//http://web.archive.org/web/20070707012511/http://www.brucemo.com/compchess/programming/index.htm
public class SearchEngineFifty10 implements ISearchableV2, EngineConstants {
	private MoveGenerationOrderedOnlyQueenPromotions_SBIV3 moveGenerationOrdered = new MoveGenerationOrderedOnlyQueenPromotions_SBIV3();
	private MoveGenerationOrderedCapturesOnlyQueenPromotions_SBIV3 moveGenerationCaptures = new MoveGenerationOrderedCapturesOnlyQueenPromotions_SBIV3();
	private LegalityV4 legality = new LegalityV4();
	
	private final int MINUS_INFINITY = -99999;
	private final int PLUS_INFINITY = 99999;
	
	private static final int HASH_EXACT = 1;
	private static final int HASH_ALPHA = 2;
	private static final int HASH_BETA = 3;
	
	private Map<Long, Integer> boardStateHistory;
	
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
//			IBoard board = BoardFactory.getInstance7(searchParameters.getBitboard(), searchParameters.getPieces(), searchParameters.getEpT(), searchParameters.getEpS(), i, searchParameters.getCastlingRights(), searchParameters.getUiZobristKey(), searchParameters.getFiftyMoveCounter(), pawnZobristKey);
			BoardV7 board = new BoardV7(searchParameters.getBitboard(), searchParameters.getPieces(), searchParameters.getEpT(),
					searchParameters.getCastlingRights(), searchParameters.getUiZobristKey(), searchParameters.getFiftyMoveCounter(), pawnZobristKey);
			
			if (i == 1 && searchParameters.getBookName() != null) {
//				long s = System.currentTimeMillis();
				int bookMove = OpeningBook2.getInstance().findBookMove(board, i + 1, searchParameters.getSide(), searchParameters.getSide() ^ 1, searchParameters.getBookName());
//				long e = System.currentTimeMillis();
//				System.out.println("Opening Book Time Consumed = " + (e - s));
				if (bookMove != 0) {
					move = bookMove;
					searchResult.setBookMove(true);
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
		
		
		searchResult.setBestMove(move);
		
		searchResult.setTimeConsumed(System.currentTimeMillis() - startTime);
		return searchResult;
	}
	
	public void resetTT() {
		tt.resetTT();
		pawnHashTable.resetTT();
	}
	
	public int getBestMovee(int depth, BoardV7 board, int side, int distance){
		
		int alpha = MINUS_INFINITY;
		int beta = PLUS_INFINITY;
		
		int bestMove = 0;
		int tempValue;
		int color = side == GuiConstants.WHITES_TURN ? 1 : -1 ;
		int opSide = side ^ 1;
		
		int hashType = HASH_ALPHA;
		
		int ttBestMove = 0;
		TranspositionElement ttElement = tt.probe(board.getZobristKey());
		if(ttElement != null && ttElement.zobristKey == board.getZobristKey()){
			ttBestMove = ttElement.bestMove;
		}
		
		if(ttBestMove != 0){
			board.doMove(ttBestMove, side, opSide);
			
			if (!legality.isKingInCheck(board.getBitboard(), side)) {
				tempValue = -negamax(depth - 1, board, side, -color, -beta, -alpha, ttBestMove, ttBestMove, true, distance + 1);
				
				if(tempValue > alpha){
					hashType = HASH_EXACT;
					alpha = tempValue;
					bestMove = ttBestMove;
				}
			}
			
			board.undoMove(ttBestMove, side, opSide);
		}
		
		moveGenerationOrdered.generateMoves(board, side, depth + 1, depth);
		int[] moveList = board.getMoveList();
		Arrays.sort(moveList);
		
		int move;
		for ( int i = EngineConstants.MOVE_LIST_SIZE - 1  ; (move = moveList[i]) != 0 ; i--) {
			board.doMove(move, side, opSide);
			if (!legality.isKingInCheck(board.getBitboard(), side)) {
				tempValue = -negamax(depth - 1, board, side, -color, -beta, -alpha, move, move, true, distance + 1);
				if(tempValue > alpha){
					hashType = HASH_EXACT;
					alpha = tempValue;
					bestMove = move;
				}
			}
			board.undoMove(move, side, opSide);
		}
		tt.recordTranspositionTable(board.getZobristKey(), alpha, bestMove, depth, hashType, isTimeout);
		return bestMove;
	}
	
	private boolean isLeadsToDraw(long zobristKey, BoardV7 board) {
		if (board.hasRepeated(zobristKey)) {
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
	
	public int negamax(int depth, BoardV7 board, int side, int color, int alpha, int beta, int previousMove, int firstMove, boolean allowNullMove, int distance){
		
		if (isTimeout) {
			return 0;
		}
		
		int hashType = HASH_ALPHA;
		long zobristKey = board.getZobristKey();
		if (isLeadsToDraw(zobristKey, board)) {
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
		
		boolean isKingInCheck = legality.isKingInCheck(board.getBitboard(), side ^ 1);
		if(!isKingInCheck && depth <= 0){
			searchResult.incrementEvaluatedLeafNodeCount();
			return quiescentSearch(board, side, color, alpha, beta, depth);
		}
		
		int opSide = side;
		side = side ^ 1;
		int bestMove = 0;
		int tempValue;
		boolean existsLegalMove = false;
		boolean foundPv = false;
		
		if (CompileTimeConstants.ENABLE_NULL_MOVE_PRUNING) {
			//=>> NullMove Begin
			if (!isKingInCheck && allowNullMove && depth > 2) {
				
				board.doNullMove(side);
				tempValue = -negamax(depth - 1 - R, board, side, -color, -beta, -beta + 1, ttBestMove, firstMove, false, distance + 1);
				board.undoNullMove();
				
				if (tempValue >= beta) {
					return beta;
				}
			}
			//=>> NullMove End
		}
		
		//
		if(ttBestMove != 0){
			board.doMove(ttBestMove, side, opSide);
			
			if (!legality.isKingInCheck(board.getBitboard(), side)) {
				existsLegalMove = true;
				tempValue = -negamax(depth - 1, board, side, -color, -beta, -alpha, ttBestMove, firstMove, true, distance + 1);
				
				if (tempValue >= beta) {
					board.undoMove(ttBestMove, side, opSide);
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
			board.undoMove(ttBestMove, side, opSide);
		}
		
		moveGenerationOrdered.generateMoves(board, side, depth + 1, depth);
		int[] moveList = board.getMoveList();
		
		sortMoves(moveList, distance);
		
		int move;
		for ( int i = EngineConstants.MOVE_LIST_SIZE - 1  ; (move = moveList[i]) != 0 ; i--) {
			board.doMove(move, side, opSide);
			
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
					board.undoMove(move, side, opSide);
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
			board.undoMove(move, side, opSide);
		}
		
		if (!existsLegalMove) {
			if (legality.isKingInCheck(board.getBitboard(), side)) {
				return MINUS_INFINITY + distance;
			} else {
				return 0;
			}
		}
		
		tt.recordTranspositionTable(zobristKey, alpha, bestMove, depth, hashType, isTimeout);
		return alpha;
	}
	
	private int quiescentSearch(BoardV7 board, int side, int color, int alpha, int beta, int depth){
		int standPatScore =  color * EvaluationAdvancedV4.evaluate(board.getBitboard(), board.getCastlingRights(), side ^ 1, board.getPawnZobristKey(), pawnHashTable);
		
		if(standPatScore >= beta){
			return beta;
		}
		
		if(standPatScore > alpha){
			alpha = standPatScore;
		}
		
		int opSide = side;
		side = side ^ 1;
		boolean foundPv = false;
		
		moveGenerationCaptures.generateMoves(board, side, depth + 1, depth);
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
			
			board.doMoveWithoutZobrist(move, side, opSide);
			if (!legality.isKingInCheck(board.getBitboard(), side)) {
				if (foundPv) {
					tempValue = -quiescentSearch(board, side, -color, -alpha - 1, -alpha, depth - 1);
					if (tempValue > alpha) {
						tempValue = -quiescentSearch(board, side, -color, -beta, -alpha, depth - 1);
					}
				} else {
					tempValue = -quiescentSearch(board, side, -color, -beta, -alpha, depth - 1);
				}
				
				if(tempValue >= beta){
					board.undoMoveWithoutZobrist(move, side, opSide);
					return beta;
				}
				if(tempValue > alpha){
					alpha = tempValue;
					foundPv = true;
				}
			}
			board.undoMoveWithoutZobrist(move, side, opSide);
		}
		return alpha;
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

	public void setForceTimeoutRequested(boolean forceTimeoutRequested) {
		this.forceTimeoutRequested = forceTimeoutRequested;
	}

}
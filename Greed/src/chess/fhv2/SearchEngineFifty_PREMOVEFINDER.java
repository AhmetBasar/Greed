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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import chess.engine.BoardFactory;
import chess.engine.CompileTimeConstants;
import chess.engine.EngineConstants;
import chess.engine.IBoard;
import chess.engine.ISearchableV2;
import chess.engine.Move;
import chess.engine.PawnHashTable;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.engine.TT;
import chess.engine.TranspositionElement;
import chess.engine.TranspositionTable;
import chess.evaluation.EvaluationAdvancedV4;
import chess.movegen.MoveGeneration;

// http://web.archive.org/web/20070707012511/http://www.brucemo.com/compchess/programming/index.htm
public class SearchEngineFifty_PREMOVEFINDER implements ISearchableV2, EngineConstants {
	
	private MoveGeneration moveGeneration = new MoveGeneration(false);
	
	private final int MINUS_INFINITY = -99999;
	private final int PLUS_INFINITY = 99999;
	
	private Map<Integer, Map<Integer, Set<Integer>>> premoveMap = new HashMap<>();
	private Map<Integer, Map<Integer, Integer>> blackListMap = new HashMap<>();
	private int opponentsBestMove;
	
	private TT tt = new TT();
	private PawnHashTable pawnHashTable = new PawnHashTable();
	
	private static final int FUTILITY_MARGIN = 200;
	
	//TODO change slot count and comparen performance.
	private int[] primaryKillerss = new int[128]; // The index corresponds to the ply the killer move is located in
	private int[] secondaryKillerss = new int[128];
	
	SearchResult searchResult = new SearchResult();
	
	private static SearchEngineFifty_PREMOVEFINDER instance;
	private static SearchEngineFifty_PREMOVEFINDER[] newInstances = new SearchEngineFifty_PREMOVEFINDER[16];
	
	private SearchEngineFifty_PREMOVEFINDER(){
		TranspositionTable.fillZobristArrays();
	}
	
	public static SearchEngineFifty_PREMOVEFINDER getInstance() {
		if (instance == null) {
			instance = new SearchEngineFifty_PREMOVEFINDER();
		}
		return instance;
	}
	
	public static synchronized SearchEngineFifty_PREMOVEFINDER getNewInstance() {
		for (int i = 0; i < newInstances.length; i++) {
			if (newInstances[i] == null) {
				newInstances[i] = new SearchEngineFifty_PREMOVEFINDER();
				return newInstances[i];
			}
		}
		System.out.println("Max instance count exceeded.");
		System.exit(-1);
		throw new RuntimeException();
	}
	
	private void reset() {
		searchResult.reset();
		
		premoveMap = new HashMap<>();
		blackListMap = new HashMap<>();
	}
	
	public SearchResult search(SearchParameters sp) {
		
		int depth = sp.getDepth();
		long startTime = System.currentTimeMillis();
		
		reset();
		
		long zobristKey = TranspositionTable.getZobristKey(sp.getBitboard(), sp.getEpT(), sp.getCastlingRights(), sp.getSide());
		if (sp.getUiZobristKey() != zobristKey) {
			throw new RuntimeException("Zobrist key is incorrect.");
		}
		
		long pawnZobristKey = TranspositionTable.getPawnZobristKey(sp.getBitboard());
		if (sp.getUiPawnZobristKey() != pawnZobristKey) {
			throw new RuntimeException("Pawn Zobrist key is incorrect.");
		}
		
		int move = 0;
		for (int i = 1; i <= depth; i++) {
			boolean isLastIteration = i == depth;
			int preMoveDepth = depth - 1;
			IBoard board = BoardFactory.getInstance2(sp);
			move = getBestMovee(i, board, isLastIteration, preMoveDepth, 0, sp.getFirstMove());	
		}
		
		opponentsBestMove = move;
		
		doFinalPreMove(sp.getFirstMove());
		
		searchResult.setBestMove(move);
		
		searchResult.setTimeConsumed(System.currentTimeMillis() - startTime);
		
		return searchResult;
		
	}
	
	public void resetTT() {
		tt.resetTT();
		pawnHashTable.resetTT();
	}
	
	public int getBestMovee(int depth, IBoard board, boolean isLastIteration, int preMoveDepth, int distance, int firstMove){
		
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
			
			tempValue = -negamax(depth - 1, board, -beta, -alpha, ttBestMove, firstMove, preMoveDepth, distance + 1);
			
			if (isLastIteration) {
				searchResult.getPossibleMoves().put(ttBestMove, tempValue);
			}
			
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
			
			tempValue = -negamax(depth - 1, board, -beta, -alpha, move, firstMove, preMoveDepth, distance + 1);
			
			if (isLastIteration) {
				searchResult.getPossibleMoves().put(move, tempValue);
			}
			
			if(tempValue > alpha){
				hashType = HASH_EXACT;
				alpha = tempValue;
				bestMove = move;
			}
			
			board.undoMove(move);
		}
		
		moveGeneration.endPly();
		
		tt.recordTranspositionTable(board.getZobristKey(), alpha, bestMove, depth, hashType, false);
		
		return bestMove;
	}
	
	public void addKiller(int move, int depth) {
		if (primaryKillerss[depth] != move) {
			secondaryKillerss[depth] = primaryKillerss[depth];
			primaryKillerss[depth] = move;				
		}
	}
	
	public int negamax(int depth, IBoard board, int alpha, int beta, int previousMove, int firstMove, int preMoveDepth, int distance){
		
		int hashType = HASH_ALPHA;
		long zobristKey = board.getZobristKey();
		if (board.hasRepeated(zobristKey, searchResult)) {
			return 0;
		}
		
		int ttBestMove = 0;
		TranspositionElement ttElement = tt.probe(zobristKey);
		if(ttElement != null && ttElement.zobristKey == zobristKey){
			if(ttElement.depth >= depth){
				switch (ttElement.hashType) {
				case HASH_EXACT:
					if (depth == preMoveDepth) {
						decidePreMove(ttElement.bestMove, previousMove, firstMove);
					}
					return ttElement.score;
				case HASH_ALPHA:
					if (ttElement.score <= alpha) {
						if (depth == preMoveDepth) {
							decidePreMove(ttElement.bestMove, previousMove, firstMove);
						}
						return alpha;
					}
					break;
				case HASH_BETA:
					if (ttElement.score >= beta) {
						if (depth == preMoveDepth) {
							decidePreMove(ttElement.bestMove, previousMove, firstMove);
						}
						return beta;
					}
					break;
				}
			}
			ttBestMove = ttElement.bestMove;
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
		
		if(ttBestMove != 0){
			board.doMove(ttBestMove);
			
			existsLegalMove = true;
			
			tempValue = -negamax(depth - 1, board, -beta, -alpha, ttBestMove, firstMove, preMoveDepth, distance + 1);
			
			if (tempValue >= beta) {
				board.undoMove(ttBestMove);
				tt.recordTranspositionTable(zobristKey, beta, ttBestMove, depth, HASH_BETA, false);
				if (depth == preMoveDepth) {
					decidePreMove(ttBestMove, previousMove, firstMove);
				}
				addKiller(ttBestMove, distance);
				return beta;
			}
			
			if (tempValue > alpha) {
				alpha = tempValue;
				bestMove = ttBestMove;
				hashType = HASH_EXACT;
				foundPv = true;
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
			
			existsLegalMove = true;
			if (foundPv) {
				tempValue = -negamax(depth - 1, board, -alpha - 1, -alpha, move, firstMove, preMoveDepth, distance + 1);
				if (tempValue > alpha) {
					tempValue = -negamax(depth - 1, board, -beta, -alpha, move, firstMove, preMoveDepth, distance + 1);
				}
			} else {
				tempValue = -negamax(depth - 1, board, -beta, -alpha, move, firstMove, preMoveDepth, distance + 1);
			}
			
			if (tempValue >= beta) {
				board.undoMove(move);
				tt.recordTranspositionTable(zobristKey, beta, move, depth, HASH_BETA, false);
				addKiller(move, distance);
				if (depth == preMoveDepth) {
					decidePreMove(move, previousMove, firstMove);
				}
				moveGeneration.endPly();
				return beta;
			}
			
			if(tempValue > alpha){
				alpha = tempValue;
				bestMove = move;
				hashType = HASH_EXACT;
				foundPv = true;
			}
			
			board.undoMove(move);
		}
		
		moveGeneration.endPly();
		
		if (!existsLegalMove) {
			if (isKingInCheck) {
//			if (legality.isKingInCheck(board.getBitboard(), board.getSide())) {
				return MINUS_INFINITY + distance;
			} else {
				return 0;
			}
		}
		
		if (depth == preMoveDepth) {
			decidePreMove(bestMove, previousMove, firstMove);
		}
		
		tt.recordTranspositionTable(zobristKey, alpha, bestMove, depth, hashType, false);
		
		return alpha;
	}
	
	private int quiescentSearch(IBoard board, int alpha, int beta){
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
	
	private void decidePreMove(int bestMove, int previousMove, int firstMove) {
		/**
		 * if the depth is 2 below.
		 * if the previous move was a capture move.
		 * if the targets of previous capture move and current capture move are equal.
		 * Save the bestMove for candidate of PreMove on OnlineChessbot.
		 * */
		//
		// If it is an attacking move. (it may also be enPassant capture.)
		if ((previousMove & 0x78000000) != 0) {
			
			int moveType = Move.getMoveType(previousMove);
			int toPrev = -1;
			if (moveType == EngineConstants.EP_CAPTURE_SHIFTED) {
				// to(captured) square of the EnPassant capture is the to square of the previous move(double push move.)
				// We are correcting the algorithm instead of adding new facility here. if we expect our pawn is captured with normal move, but what if there is an enPassant capture?
				// EnPassant capture must also be handled here.
				
				// For Clarity. Last decision is, if there is an enPassant capture, than enPassant square will added to blacklist.
				
				toPrev = (firstMove & 0x0000ff00) >>> 8;
				Map<Integer, Integer> map = blackListMap.get(firstMove);
				if (map == null) {
					map = new HashMap<Integer, Integer>();
					blackListMap.put(firstMove, map);
				}
				map.put(toPrev, toPrev);
				
				return;
			} else {
				toPrev = (previousMove & 0x0000ff00) >>> 8;
			}
			Map<Integer, Set<Integer>> map = premoveMap.get(firstMove);
			if (map == null) {
				map = new HashMap<Integer, Set<Integer>>();
				premoveMap.put(firstMove, map);
			}
			
			Set<Integer> list = map.get(toPrev);
			if (list == null) {
				list = new HashSet<Integer>();
				map.put(toPrev, list);
			}
			list.add(bestMove);
		}
		//
	}
	
	private void doFinalPreMove(int move) {
		Map<Integer, Set<Integer>> map = premoveMap.get(move);
		Map<Integer, Integer> bList = blackListMap.get(move);
		
		if (map != null) {
			
			int to = (opponentsBestMove & 0x0000ff00) >>> 8;
		
			if (bList != null && bList.containsKey(to)) {
				return;
			}

			Set<Integer> set = map.get(to);
			if (set != null && set.size() == 1) {
				int candidatePreMove = set.iterator().next();
				int movesToSquare = (candidatePreMove & 0x0000ff00) >>> 8;
				if (to == movesToSquare) {
					searchResult.setPreMove(candidatePreMove);
				}
			}
		}
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

	@Override
	public void setForceTimeoutRequested(boolean forceTimeoutRequested) {
		// TODO Auto-generated method stub
		
	}
	
}

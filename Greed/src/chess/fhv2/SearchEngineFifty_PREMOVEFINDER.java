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

import chess.debug.IBoard;
import chess.engine.BoardFactory;
import chess.engine.EngineConstants;
import chess.engine.EvaluationAdvancedV4;
import chess.engine.ISearchablePreMove;
import chess.engine.LegalityV4;
import chess.engine.MoveGenerationOrderedCapturesOnlyQueenPromotions_SBIV2;
import chess.engine.MoveGenerationOrderedOnlyQueenPromotions_SBIV2;
import chess.engine.SearchResult;
import chess.engine.TranspositionElement;
import chess.engine.TranspositionTable;
import chess.gui.GuiConstants;

// http://web.archive.org/web/20070707012511/http://www.brucemo.com/compchess/programming/index.htm
public class SearchEngineFifty_PREMOVEFINDER implements ISearchablePreMove {
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
	
	private Map<Integer, Map<Integer, Set<Integer>>> premoveMap = new HashMap<>();
	private Map<Integer, Map<Integer, Integer>> blackListMap = new HashMap<>();
	private int opponentsBestMove;
	private Map<Long, Integer> boardStateHistory;
	
	private TranspositionElement[] hashTable = new TranspositionElement[TT_SIZE];
	
	//TODO change slot count and comparen performance.
	private int[] primaryKillerss = new int[128]; // The index corresponds to the ply the killer move is located in
	private int[] secondaryKillerss = new int[128];
	
	SearchResult searchResult = new SearchResult();
	
	public SearchEngineFifty_PREMOVEFINDER(){
		TranspositionTable.fillZobristArrays();
	}
	
	private void reset() {
		searchResult.reset();
		
		premoveMap = new HashMap<>();
		blackListMap = new HashMap<>();
	}
	
	public SearchResult search(int depth, int epT, int epS, long[] bitboard, byte[] pieces, byte[][] castlingRights, int side, long uiZobristKey, int firstMove, long timeLimit, int fiftyMoveCounter) {
		
		reset();
		
		long zobristKey = TranspositionTable.getZobristKey(bitboard, epT, castlingRights, side);
		if (uiZobristKey != zobristKey) {
			throw new RuntimeException("Zobrist key is incorrect.");
		}
		
		int move = 0;
		for (int i = 1; i <= depth; i++) {
			boolean isLastIteration = i == depth;
			int preMoveDepth = depth - 1;
			IBoard board = BoardFactory.getInstance(bitboard, pieces, epT, epS, i, castlingRights, uiZobristKey, fiftyMoveCounter);
			move = getBestMovee(i, board, side, isLastIteration, preMoveDepth, 0, firstMove);	
		}
		
		opponentsBestMove = move;
		
		doFinalPreMove(firstMove);
		
		//
		searchResult.setBestMove(move);
		//
		
//		System.out.println(searchResult);
		
		return searchResult;
		
	}
	
	public void resetTT() {
		hashTable = new TranspositionElement[TT_SIZE];
	}
	
	public int getBestMovee(int depth, IBoard board, int side, boolean isLastIteration, int preMoveDepth, int distance, int firstMove){
		
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
				
				tempValue = -negamax(depth - 1, board, side, -color, -beta, -alpha, ttBestMove, firstMove, preMoveDepth, distance + 1);
				
				//
				if (isLastIteration) {
					searchResult.getPossibleMoves().put(ttBestMove, tempValue);
				}
				//
				
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
				
				tempValue = -negamax(depth - 1, board, side, -color, -beta, -alpha, move, firstMove, preMoveDepth, distance + 1);
				
				//
				if (isLastIteration) {
					searchResult.getPossibleMoves().put(move, tempValue);
				}
				//
				
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
	
	public int negamax(int depth, IBoard board, int side, int color, int alpha, int beta, int previousMove, int firstMove, int preMoveDepth, int distance){
		
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
					//
					if (depth == preMoveDepth) {
						decidePreMove(ttElement.bestMove, previousMove, firstMove);
					}
					//
					return ttElement.score;
				case HASH_ALPHA:
					if (ttElement.score <= alpha) {
						//
						if (depth == preMoveDepth) {
							decidePreMove(ttElement.bestMove, previousMove, firstMove);
						}
						//
						return alpha;
					}
					break;
				case HASH_BETA:
					if (ttElement.score >= beta) {
						//
						if (depth == preMoveDepth) {
							decidePreMove(ttElement.bestMove, previousMove, firstMove);
						}
						//
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
		
		//
		if(ttBestMove != 0){

			// Board infrastructure.
			board.doMove(ttBestMove, side, opSide, depth);
			// Board infrastructure.
			
			if (!legality.isKingInCheck(board.getBitboard(), side)) {
				
				existsLegalMove = true;
				
				tempValue = -negamax(depth - 1, board, side, -color, -beta, -alpha, ttBestMove, firstMove, preMoveDepth, distance + 1);
				
				if (tempValue >= beta) {
					
					// Board infrastructure.
					board.undoMove(ttBestMove, side, opSide, depth);
					// Board infrastructure.
					
					recordTranspositionTable(zobristKey, beta, ttBestMove, depth, HASH_BETA);
					
					//
					if (depth == preMoveDepth) {
						decidePreMove(ttBestMove, previousMove, firstMove);
					}
					//
					
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
					tempValue = -negamax(depth - 1, board, side, -color, -alpha - 1, -alpha, move, firstMove, preMoveDepth, distance + 1);
					if (tempValue > alpha) {
						tempValue = -negamax(depth - 1, board, side, -color, -beta, -alpha, move, firstMove, preMoveDepth, distance + 1);
					}
				} else {
					tempValue = -negamax(depth - 1, board, side, -color, -beta, -alpha, move, firstMove, preMoveDepth, distance + 1);
				}
				
				if (tempValue >= beta) {
					// Board infrastructure.
					board.undoMove(move, side, opSide, depth);
					// Board infrastructure.
					
					recordTranspositionTable(zobristKey, beta, move, depth, HASH_BETA);
					
					//TODO: capture moves should be handled by see, not killer heuristic.
					addKiller(move, distance);
					
					//
					if (depth == preMoveDepth) {
						decidePreMove(move, previousMove, firstMove);
					}
					//
					
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
				return color * (side == GuiConstants.WHITES_TURN ? -99999 : 99999);
			} else {
				return 0;
			}
		}
		
		//
		if (depth == preMoveDepth) {
			decidePreMove(bestMove, previousMove, firstMove);
		}
		//
		
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
			
			int moveType = previousMove & 0x00070000;
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
	
}

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
package chess.debug;

import java.util.ArrayList;
import java.util.List;

import chess.engine.BoardFactory;
import chess.engine.EngineConstants;
import chess.engine.IBoard;
import chess.engine.Transformer;
import chess.engine.test.ThreadPool4Workers;
import chess.gui.BaseGui;
import chess.movegen.MoveGeneration;

public class PerformanceTestingMultiThreaded {
	
	private PerftResult perftResult = new PerftResult();

	public static void main(String[] args) {
		long ilk = System.currentTimeMillis();
		byte[][] sourceBoard = DebugUtility.getDefaultBoard();
		long[] bitboard = Transformer.getBitboardStyl(sourceBoard);
		byte[] pieces = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(sourceBoard));
		byte[][] castlingRights = { { 1, 1 }, { 1, 1 } };
		int depth = 5;
		IBoard board = BoardFactory.getInstance2(bitboard, pieces, 64, castlingRights, 0, new ArrayList<Long>(), 0);
		PerformanceTestingMultiThreaded obj = new PerformanceTestingMultiThreaded();
		obj.perft(depth, board, 4);
		System.out.println(obj.perftResult);
		System.out.println("time = " + (System.currentTimeMillis() - ilk));
	}
	
	public static void getAllVariations(byte[][] boardArray, int side, int depth, byte[][] castlingRights, BaseGui baseGui, int threadCount, int epTarget, int epSquare){
		long startTime = System.currentTimeMillis();
		long[] bitboard = Transformer.getBitboardStyl(boardArray);
		byte[] pieces = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(boardArray));
		IBoard board = BoardFactory.getInstance2(bitboard, pieces, epTarget, castlingRights, 0, new ArrayList<Long>(), side);
		PerformanceTestingMultiThreaded obj = new PerformanceTestingMultiThreaded();
		obj.perft(depth, board, threadCount);
		obj.perftResult.setTimeConsumed(System.currentTimeMillis() - startTime);
		baseGui.getDebugPanel().setOutputMessage(obj.perftResult.toString());
		baseGui.getDebugPanel().setEnableAll(true);
	}
		
	public void perft(int depth, IBoard board, int threadCount) {
		List<Integer> moves = new ArrayList<Integer>();
		MoveGeneration moveGeneration = new MoveGeneration(true);
		moveGeneration.startPly();
		moveGeneration.generateAttacks(board);
		moveGeneration.generateMoves(board);
		while (moveGeneration.hasNext()) {
			moves.add(moveGeneration.next());
		}
		moveGeneration.endPly();
		
		int splittedMoveListSize = (int)((double) moves.size() / (double) threadCount);
		List<List<Integer>> splittedMoveLists = splitList(moves, splittedMoveListSize);
		
		List<Runnable> executables = new ArrayList<Runnable>();
		List<PerftResult> perftResults = new ArrayList<PerftResult>();
		
		for (List<Integer> moveList : splittedMoveLists) {
			
			IBoard b = BoardFactory.getInstance2(board.getBitboard().clone(),
					Transformer.getByteArrayStyl(board.getBitboard().clone()),
					board.getEpTarget(),
					DebugUtility.deepCloneMultiDimensionalArray(board.getCastlingRights()),
					0,
					new ArrayList<Long>(),
					board.getSide());
			Worker worker = new Worker();
			perftResults.add(worker.getPerftResult());
			
			Runnable executable = new Runnable() {
				@Override
				public void run() {
					
					try {
						worker.traverseMoveList(depth, b, moveList);
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			};
			executables.add(executable);
		}
		
		ThreadPool4Workers.getInstance().execute(executables, true);
		
		perftResult.combinePerftResults(perftResults, depth);
	}
	
	private static List<List<Integer>> splitList(List<Integer> list, final int length) {
		List<List<Integer>> subLists = new ArrayList<List<Integer>>();
		int listSize = list.size();
		for (int i = 0; i < listSize; i += length) {
			subLists.add(new ArrayList<Integer>(list.subList(i, Math.min(listSize, i + length))));
		}
		return subLists;
	}

	public PerftResult getPerftResult() {
		return perftResult;
	}
	
}

class Worker {
	
	private MoveGeneration moveGeneration = new MoveGeneration(true);
	private PerftResult perftResult = new PerftResult();
	
	public void traverseMoveList(int depth, IBoard board, List<Integer> moveList) {
		
		if (depth == 0) {
			perftResult.incrementNodeCount();
			return;
		}
		
		int depthMinusOne = depth - 1;
		boolean existsLegalMove = false;
		
		int move;
		for (Integer moveWrapper : moveList) {
			move = moveWrapper.intValue();
			
			if (!board.isLegal(move)) {
				continue;
			}
			
			board.doMove(move);
			
			existsLegalMove = true;
			if (depth == 1) {
				//
				int moveType = move & 0x00070000;
				byte capturedPiece = board.getCapturedPiece();
				switch (moveType) {
				case 0:
					break;
				case EngineConstants.DOUBLE_PUSH_SHIFTED:
					break;
				case EngineConstants.EP_CAPTURE_SHIFTED:
					perftResult.incrementEpCount();
					break;
				case EngineConstants.PROMOTION_SHIFTED:
					perftResult.incrementPromotionCount();
					break;
				default:
					perftResult.incrementCastlingCount();
					break;
				}
				
				if (capturedPiece > 0) {
					perftResult.incrementCaptureCount();
				}
				
				if (board.getCheckers() != 0) {
					perftResult.incrementCheckCount();
				}
				//
			}
			
			perft(depthMinusOne, board);
			board.undoMove(move);
		}
		
		if (depth == 1 && !existsLegalMove) {
			perftResult.incrementCheckMateCount();
		}
		
	}

	public void perft(int depth, IBoard board) {

		if (depth == 0) {
			perftResult.incrementNodeCount();
			return;
		}

		int depthMinusOne = depth - 1;

		boolean existsLegalMove = false;
		int move;
		moveGeneration.startPly();
		moveGeneration.generateAttacks(board);
		moveGeneration.generateMoves(board);
		while (moveGeneration.hasNext()) {
			move = moveGeneration.next();
			
			if (!board.isLegal(move)) {
				continue;
			}
			board.doMove(move);

			existsLegalMove = true;
			if (depth == 1) {
				//
				int moveType = move & 0x00070000;
				byte capturedPiece = board.getCapturedPiece();
				switch (moveType) {
				case 0:
					break;
				case EngineConstants.DOUBLE_PUSH_SHIFTED:
					break;
				case EngineConstants.EP_CAPTURE_SHIFTED:
					perftResult.incrementEpCount();
					break;
				case EngineConstants.PROMOTION_SHIFTED:
					perftResult.incrementPromotionCount();
					break;
				default:
					perftResult.incrementCastlingCount();
					break;
				}
				
				if (capturedPiece > 0) {
					perftResult.incrementCaptureCount();
				}
				
				if (board.getCheckers() != 0) {
					perftResult.incrementCheckCount();
				}
				//
			}
			
			perft(depthMinusOne, board);

			board.undoMove(move);
		}
		
		moveGeneration.endPly();

		if (depth == 1 && !existsLegalMove) {
			perftResult.incrementCheckMateCount();
		}
		
	}

	public PerftResult getPerftResult() {
		return perftResult;
	}

}

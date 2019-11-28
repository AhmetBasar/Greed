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

import chess.engine.BoardFactory;
import chess.engine.IBoard;
import chess.engine.LegalityV4;
import chess.engine.MoveGeneration;
import chess.engine.Transformer;
import chess.gui.BaseGui;

public class PerformanceTestingSingleThreadedWithBoardInfaThreaded {
	private MoveGeneration moveGeneration = new MoveGeneration();
	private LegalityV4 legality = new LegalityV4();

	private static BaseGui base;
	
	private static long ilk = System.currentTimeMillis();
	
	private static boolean isFromScreen = false;
	
	//
	private int depth;
	private IBoard board;
	private int[] moveList;
	//
	
	public static void main1(String[] args) {
		byte[][] sourceBoard = DebugUtility.getDefaultBoard();
		long[] bitboard = Transformer.getBitboardStyl(sourceBoard);
		byte[] pieces = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(sourceBoard));
		byte[][] castlingRights = { { 1, 1 }, { 1, 1 } };
		int depth = 6;
		IBoard board = BoardFactory.getInstance(bitboard, pieces, 64, castlingRights, 0L, 0, 0L, null, 0);
		System.out.println("move count = " + new PerformanceTestingSingleThreadedWithBoardInfaThreaded().perft(depth, board));
		System.out.println("time = " + (System.currentTimeMillis() - ilk));
	}
	
	public static void getAllVariations(byte[][] boardArray, int side, int depth, byte[][] castlingRights, BaseGui baseGui, int threadCount, int epTarget, int epSquare){
		System.out.println("PerformanceTestingSingleThreadedWithBoardInfaThreaded");
		base = baseGui;
		isFromScreen = true;
		resetCounters();
		long[] bitboard = Transformer.getBitboardStyl(boardArray);
		byte[] pieces = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(boardArray));
		IBoard board = BoardFactory.getInstance(bitboard, pieces, epTarget, castlingRights, 0L, 0, 0L, null, side);
		PerformanceTestingSingleThreadedWithBoardInfaThreaded.dispatchThreads(depth, board, castlingRights, side, threadCount);
	}
	
	private synchronized static void incrementMoveCount(long moveC) {
		deadThreadCount++;
		moveCount = moveCount + moveC;
		if (deadThreadCount == aliveThreadCount) {
			if(isFromScreen){
				StringBuilder outputMessage = new StringBuilder();
				outputMessage.append("moveCount = " + moveCount + "\n");
				outputMessage.append("----------------------------" + "\n");
				outputMessage.append("active thread count = " + aliveThreadCount + "\n");
				outputMessage.append("Time Consumed = " + (System.currentTimeMillis() - ilk) + "\n");
				base.getDebugPanel().setOutputMessage(outputMessage.toString());
				base.getDebugPanel().setEnableAll(true);
			} else {
				System.out.println("moveCount = " + moveCount);
				System.out.println("----------------------------");
				System.out.println("active thread count = " + aliveThreadCount);
				System.out.println("yenisi bu Time Consumed = " + (System.currentTimeMillis() - ilk));
			}
		}
	}
	
	private Thread th = new Thread() {
		public void run() {
			try {
				incrementMoveCount(traverseMoveList(depth, board, moveList));
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	};

	public PerformanceTestingSingleThreadedWithBoardInfaThreaded() {
	}
	
	public PerformanceTestingSingleThreadedWithBoardInfaThreaded(int depth, IBoard board, int[] moveList) {
		this.depth = depth;
		this.board = board;
		this.moveList = moveList;
	}
	
	private synchronized static void dispatchThreads(int depth, IBoard board, byte[][] castlingRights, int side, int threadCount) {
		if (depth == 0)
			return;
		
		int i = 0;
		MoveGeneration moveGeneration = new MoveGeneration();
		int[] moveList = moveGeneration.generateMoves(board, depth + 1);
		// concurrency
		int moveListSize = 0;
		while (moveList[moveListSize] != 0) {
			moveListSize++;
		}
		double wholeDouble = (double) moveListSize / (double) threadCount;
		double fractionalDoubleSize = wholeDouble - (long) wholeDouble;
		int partialMoveListSize;
		if (fractionalDoubleSize == 0) {
			partialMoveListSize = (int) (moveListSize / threadCount);
		} else {
			partialMoveListSize = (int) (moveListSize / threadCount) + 1;
		}
		
		int partialMoveListIndex = 0;
		int[] partialMoveList = null;
		// concurrency
		while (moveList[i] != 0) {
			if (partialMoveListIndex == 0) {
				if (partialMoveListSize > moveListSize) {
					partialMoveListSize = moveListSize; // son kýsým.
				}
				moveListSize = moveListSize - partialMoveListSize;
				partialMoveList = new int[partialMoveListSize + 1];
			}
			partialMoveList[partialMoveListIndex] = moveList[i];
			partialMoveListIndex++;
			if (partialMoveListIndex == partialMoveListSize) {
				partialMoveListIndex = 0;
				IBoard b = BoardFactory.getInstance(board.getBitboard().clone(), Transformer.getByteArrayStyl(board.getBitboard().clone()), board.getEpTarget(), castlingRights, 0L, 0, 0L, null, side);
				new PerformanceTestingSingleThreadedWithBoardInfaThreaded(depth, b, partialMoveList).start();
				aliveThreadCount++;
			}
			i++;
		}
	}
	
	public void start() {
		th.start();
	}
	
	public long traverseMoveList(int depth, IBoard board, int[] moveList) {
		
		if (depth == 0)
			return 1;
		
		int depthMinusOne = depth - 1;
		long nodes = 0;
		int i = 0;
		
		int move;
		while (moveList[i] != 0) {
			move = moveList[i];
			
			board.doMove(move);
			
			if (!legality.isKingInCheck(board.getBitboard(), board.getOpSide())) {
				nodes += perft(depthMinusOne, board);
			}
			
			board.undoMove(move);
			
			i++;
		}
		
		return nodes;
	}

	public long perft(int depth, IBoard board) {

		if (depth == 0)
			return 1;
		
		int depthMinusOne = depth - 1;
		int depthPlusOne = depth + 1;
		long nodes = 0;
		int i = 0;
		
		int move;
		int[] moveList = moveGeneration.generateMoves(board, depthPlusOne);
		while (moveList[i] != 0) {
			move = moveList[i];
			
			board.doMove(move);
			
			if (!legality.isKingInCheck(board.getBitboard(), board.getOpSide())) {
				nodes += perft(depthMinusOne, board);
			}
			
			board.undoMove(move);

			i++;
		}
		
		return nodes;
	}
	
	private static int aliveThreadCount = 0;
	private static int deadThreadCount = 0;
	private static long moveCount = 0;
	
	private synchronized static void resetCounters(){
		aliveThreadCount = 0;
		deadThreadCount = 0;
		moveCount = 0;
		ilk = System.currentTimeMillis();
	}

}

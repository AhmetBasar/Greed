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
package chess.bot;

import java.util.Arrays;
import java.util.List;

import chess.bot.image.IImageCache;
import chess.bot.image.impl.OnlineChessImageCache;
import chess.bot.interpreting.BotMove;
import chess.bot.interpreting.CellDifference;
import chess.bot.interpreting.MoveIntepreter;
import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.Move;

public class ChessBot implements Runnable {

	byte[][] prevBoard = new byte[8][8];
	byte[][] currBoard = DebugUtility.getDefaultBoard();
	private BoardScanner boardScanner;
	private MoveIntepreter moveIntepreter;
	private IGameController controller;
	private BoardInteractionManager boardInteractionManager;
	private IImageCache imageCache;
	private volatile boolean suspended = true;
	private volatile boolean restarted = false;
	private volatile boolean preMoved = false;
	private BotMove scheduledPreMove;
	private int moveCount = 0;

	public static void main(String[] args) {
		ChessBot chessBot = new ChessBot(null);
		chessBot.suspended = false;
		Thread th = new Thread(chessBot);
		th.start();
	}

	public ChessBot(IGameController controller) {
		this.controller = controller;
		moveIntepreter = new MoveIntepreter(controller);
		// imageCache = new CruelChessImageCache();
		imageCache = new OnlineChessImageCache();
		imageCache.refresh();
		boardScanner = new BoardScanner(this, imageCache);
		boardInteractionManager = new BoardInteractionManager(boardScanner, imageCache);
	}

	@Override
	public void run() {
		outer: while (true) {
			waitIfSuspended();
			if (restarted) {
				restarted = false;
				resetAll();
				continue outer;
			}
			try {
				prevBoard = currBoard;
				inner: while (true) {
					currBoard = getStableCurrentBoardState();
					
					waitIfSuspended();
					if (restarted) {
						restarted = false;
						resetAll();
						continue outer;
					}
					
					if (!Arrays.deepEquals(prevBoard, currBoard)) {
						
						if (preMoved) {
							preMoved = false;
							
							int fromIndex[] = Utility.convertTwoDimensionIndex(scheduledPreMove.getFrom());
							int toIndex[] = Utility.convertTwoDimensionIndex(scheduledPreMove.getTo());
							
							byte prevFromItem = prevBoard[fromIndex[0]][fromIndex[1]];
							byte prevToItem = prevBoard[toIndex[0]][toIndex[1]];
							
							byte currFromItem = currBoard[fromIndex[0]][fromIndex[1]];
							byte currToItem = currBoard[toIndex[0]][toIndex[1]];
							
							// if it is too early, there is possibility to see incomplete preMove.
							// Try to detect first part of preMove (incomplete preMove) here. And ignore this incomplete change. 
							if (prevFromItem == currFromItem && prevToItem != currToItem) {
								/**
								 * in case, i am such a fast scanner. And i catch the frame that contains only first part of preMove say it is opponent's capture move.
								 * So, it is need to check target square of the scheduled preMove was changed or not. if both target and source squares was changed than there is no problem at all.
								 * What if only target square was changed? what will you do in this case?
								 * infinite loop must be executed till the second part of preMove is done.
								 * */
								// Revert changes.
								// set preMoved flag to true again.								
								currBoard = prevBoard;
								preMoved = true;
								
								System.out.println("A very very rare case occured. Our scanning algorithm must be really fast enough to catch half of the premove...");
								
								// Continue the next iteration.
								continue inner;
								
							} else {
								// preMove was completed here. 
								// Maybe it is too late to reach this point of execution. in other words, third move may also be done here... say, opponent does capture move than we recaptured
								// with the scheduled preMove. and than the opponent do moves immediately after the preMove is done. so it was too late.
								// For Clarity. Last decision is, if there is an enPassant capture, than enPassant square will added to blacklist.
								
								int capturedPieceCount = moveIntepreter.getCapturedPieceCount(prevBoard, currBoard);
								if (capturedPieceCount >= 2) {
									
									System.out.println("PreMove was successfull without any doubt.");
									
									List<CellDifference> differences = moveIntepreter.getDifferences(prevBoard, currBoard);
									if (moveIntepreter.isMoveSequence(currBoard, scheduledPreMove, differences)) {
										
										List<BotMove> moveSequence = moveIntepreter.findTripleMoveSequence(prevBoard, currBoard, scheduledPreMove, differences);
										
										controller.doMove(moveSequence.get(0));
										controller.doMove(moveSequence.get(1));
										controller.doMove(moveSequence.get(2));
										
										controller.suggestMoveForUs();
										suspend();
										waitIfSuspended();
										if (restarted) {
											restarted = false;
											resetAll();
											continue outer;
										}
										break inner;										
										
									} else {
										
										BotMove move = moveIntepreter.interpret(prevBoard, currBoard, true, scheduledPreMove);
//										System.out.println("Opponent made Move = " + move);
										controller.doMove(move);
										
										controller.doMove(scheduledPreMove.getEngineMove());
										
										// Refresh current board state here.
										currBoard = controller.getBoard();
										
										incrementMoveCount();
										break inner;
									}
								} else {
									System.out.println("PREMOVE FAILED...");
								}
							}
						}
						
						BotMove move = moveIntepreter.interpret(prevBoard, currBoard);
//						System.out.println("Opponent made Move = " + move);
						
						controller.doMove(move);
						controller.suggestMoveForUs();
						suspend();
						waitIfSuspended();
						if (restarted) {
							restarted = false;
							resetAll();
							continue outer;
						}
						break inner;
					}
					waitIfSuspended();
					if (restarted) {
						restarted = false;
						resetAll();
						continue outer;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				
//				restart();
				controller.resetAll();
				controller.resetBot();
				
//				// kill the Thread...
//				break outer;
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
	
	private void resetAll() {
		prevBoard = new byte[8][8];
		currBoard = DebugUtility.getDefaultBoard();
		moveCount = 0;
		suspend();
	}
	
	public synchronized void restart() {
		restarted = true;
	}

	public synchronized void suspend() {
		suspended = true;
	}

	public synchronized void resume() {
		suspended = false;
		notify();
	}

	/**
	 * You should be using this method instead of invoke boardScanner.scan()
	 * directly. This method guarantees that, board state is consistent.
	 */
	private byte[][] getStableCurrentBoardState() throws Exception {
		byte[][] previousBoard = boardScanner.scan();
		byte[][] currentBoard = new byte[8][8];
		while (true) {
			
			// Sleep a little, in order to prevent Fatal Error Occured. differences.size() = 1 Exception.
			Thread.sleep(20);
			currentBoard = boardScanner.scan();
			if (Arrays.deepEquals(previousBoard, currentBoard)) {
				return currentBoard;
			} else {
				previousBoard = currentBoard;
			}
		}
	}

	public void doMove(int engineMove) {
		
		BotMove move = new BotMove(engineMove);
		boardInteractionManager.doMove(move);
		
		waitAlittleTimeIfEnpassantCapture(engineMove);
		
		if (Utility.isDebug()) {
			System.out.println("Engine suggested a move for us. = " + move);
		}
		
		controller.suggestPreMoveForUs();
	}
	
	public void doPreMove(int preMove) {
		
		//
		if (preMove != 0) {
			BotMove botPreMove = new BotMove(preMove);
			scheduledPreMove = botPreMove;
			System.out.println("Bot Pre Move Detected = " + botPreMove);
			boardInteractionManager.doMove(botPreMove, false);
			preMoved = true;
		} else {
			preMoved = false;
		}
		//
		
		currBoard = controller.getBoard();
		resume();
		incrementMoveCount();
	}
	
	private void waitAlittleTimeIfEnpassantCapture(int engineMove) {
		int moveType = Move.getMoveType(engineMove);
		if (moveType == EngineConstants.EP_CAPTURE_SHIFTED) {
			System.out.println("EP capture sugessted. So I'll wait a little bit. Cuzz, ep square pawn does not disappear immediately.");
			Utility.sleep(500);
		}
	}

	public void incrementMoveCount() {
		moveCount++;
	}

	public int getMoveCount() {
		return moveCount;
	}

	public IGameController getController() {
		return controller;
	}

	public BoardInteractionManager getBoardInteractionManager() {
		return boardInteractionManager;
	}

}

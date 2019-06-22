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
package chess.bot.interpreting;

import java.util.ArrayList;
import java.util.List;

import chess.bot.IGameController;
import chess.bot.Utility;
import chess.bot.interpreting.CellDifference.CellDifferenceType;
import chess.debug.DebugUtility;
import chess.engine.EngineConstants;

public class MoveIntepreter {

	public static final int EN_PASSANT_CAPTURE_DIFF_COUNT = 3;
	public static final int CASTLING_DIFF_COUNT = 4;
	public static final int SIMPLE_MOVE_DIFF_COUNT = 2;
	
	private IGameController controller;
	
	public MoveIntepreter(IGameController controller) {
		this.controller = controller;
	}

	public BotMove interpret(byte[][] previousBoard, byte[][] currentBoard) {
		return interpret(previousBoard, currentBoard, false, null);
	}

	public BotMove interpret(byte[][] previousBoard, byte[][] currentBoard, boolean isPreMove, BotMove scheduledPreMove) {
		
		if (Utility.isDebug()) {
			System.out.println("*******************************************************************************");
			System.out.println("**************************BEGINNING OF THE INTERPRETER*************************");
			System.out.println("*******************************************************************************");
			
			System.out.println("PREV BOARD = ");
			DebugUtility.throwBoard(previousBoard);
			
			System.out.println("currentBoard BOARD = ");
			DebugUtility.throwBoard(currentBoard);
		}
		
		BotMove botMove = null;
		List<CellDifference> differences = getDifferences(previousBoard, currentBoard);

		
		if (isPreMove) {
			
//			System.out.println("isPreMove TRUEEEEEEE");
//			System.out.println("scheduledPreMove = " + scheduledPreMove);
			
			CellDifference dischargingCellDif = null;
			for (CellDifference cellDifference : differences) {
				if (cellDifference.getDiffType() == CellDifferenceType.DISCHARGE) {
					if (cellDifference.getCellCoordinate().toOneDimensionalCoordinate() != scheduledPreMove.getFrom()) {
						dischargingCellDif = cellDifference;
					}
				}
			}
			
			botMove = new BotMove(dischargingCellDif.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
			
			if (Utility.isDebug()) {
				System.out.println("========================");
				System.out.println("PreMove.");
				System.out.println("========================");
			}
			
		} else if (differences.size() == EN_PASSANT_CAPTURE_DIFF_COUNT) {
			CellDifference chargingCelDiff = null;
			CellDifference dischargingCellDif1 = null;
			CellDifference dischargingCellDif2 = null;
			for (CellDifference cellDifference : differences) {
				if (cellDifference.getDiffType() == CellDifferenceType.CHARGE) {
					chargingCelDiff = cellDifference;
				} else {
					if (dischargingCellDif1 == null) {
						dischargingCellDif1 = cellDifference;
					} else {
						dischargingCellDif2 = cellDifference;
					}
				}
			}
			int to = chargingCelDiff.getCellCoordinate().toOneDimensionalCoordinate();
			int from = -1;
			byte chargingItem = chargingCelDiff.getCurrentItem();
			if (dischargingCellDif1.getPreviousItem() == chargingItem) {
				from = dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate();
			} else {
				from = dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate();
			}

			botMove = new BotMove(from, to);
			if (Utility.isDebug()) {
				System.out.println("========================");
				System.out.println("En-Passant capture.");
				System.out.println("========================");
				System.out.println("FROM = " + from);
				System.out.println("TO = " + to);
			}

		} else if (differences.size() == CASTLING_DIFF_COUNT) {
			int to = -1;
			int from = -1;
			for (CellDifference cellDifference : differences) {
				if (cellDifference.getDiffType() == CellDifferenceType.DISCHARGE
						&& (cellDifference.getPreviousItem() == EngineConstants.WHITE_KING
								|| cellDifference.getPreviousItem() == EngineConstants.BLACK_KING)) {
					from = cellDifference.getCellCoordinate().toOneDimensionalCoordinate();
				} else if (cellDifference.getDiffType() == CellDifferenceType.CHARGE
						&& (cellDifference.getCurrentItem() == EngineConstants.WHITE_KING
								|| cellDifference.getCurrentItem() == EngineConstants.BLACK_KING)) {
					to = cellDifference.getCellCoordinate().toOneDimensionalCoordinate();
				}
			}
			botMove = new BotMove(from, to);
			if (Utility.isDebug()) {
				System.out.println("========================");
				System.out.println("Castling.");
				System.out.println("========================");
				System.out.println("FROM = " + from);
				System.out.println("TO = " + to);
			}
		} else if (differences.size() == SIMPLE_MOVE_DIFF_COUNT) {
			int to = -1;
			int from = -1;
			for (CellDifference cellDifference : differences) {
				if (cellDifference.getDiffType() == CellDifferenceType.DISCHARGE) {
					from = cellDifference.getCellCoordinate().toOneDimensionalCoordinate();
				} else {
					to = cellDifference.getCellCoordinate().toOneDimensionalCoordinate();
				}
			}
			botMove = new BotMove(from, to);
			if (Utility.isDebug()) {
				System.out.println("========================");
				System.out.println("Simple Move.");
				System.out.println("========================");
				System.out.println("FROM = " + from);
				System.out.println("TO = " + to);
			}
		} else {
			throw new RuntimeException("Fatal Error Occured. differences.size() = " + differences.size());
		}
		
		if (Utility.isDebug()) {
			System.out.println("*******************************************************************************");
			System.out.println("**************************END OF THE INTERPRETER*************************");
			System.out.println("*******************************************************************************");
		}

		return botMove;

	}
	
	public List<CellDifference> getDifferences(byte[][] previousBoard, byte[][] currentBoard) {
		List<CellDifference> differences = new ArrayList<CellDifference>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				byte prevItem = previousBoard[i][j];
				byte currentItem = currentBoard[i][j];
				if (prevItem != currentItem) {
					CellCoordinate cellCoordinate = CellCoordinateFactory.getCellCoordinate(i, j);
					CellDifference diff = new CellDifference(prevItem, currentItem, cellCoordinate);
					differences.add(diff);
				}
			}
		}
		return differences;
	}
	
	
	public boolean isMoveSequence(byte[][] currentBoard, BotMove scheduledPreMove, List<CellDifference> differences) {
		return isSourceTargetedNormalMove(currentBoard, scheduledPreMove) || differences.size() > 3;
	}
	
	private boolean isSourceTargetedNormalMove(byte[][] currentBoard, BotMove scheduledPreMove) {
		int fromIndex[] = Utility.convertTwoDimensionIndex(scheduledPreMove.getFrom());
		byte fromPieceOfPreMove = currentBoard[fromIndex[0]][fromIndex[1]];
		return fromPieceOfPreMove != 0; // opponent made normal move but its target is, premove's source square.
	}
	
	public List<BotMove> findTripleMoveSequence(byte[][] previousBoard, byte[][] currentBoard, BotMove scheduledPreMove, List<CellDifference> differences) {
		
		// 3 Moves Possibilities..
//	     => 2 change 2 capture. + 3. move(capture move)(target = preMove's target.)  = 4 change + 3 capture.
//		 => 2 change 2 capture. + 3. move(capture move)(target != preMove's target.) = 4 change + 3 capture.
//		 => 2 change 2 capture. + 3. move(normal move independent)                   = 4 change + 2 capture.
//		 => 2 change 2 capture. + 3. move(normal move but source targeted)           = 3 change + 2 capture.
//		 => 2 change 2 capture. + 3. move(normal move but OWN source targeted)       = 3 change + 2 capture.
//		 => 2 change 2 capture. + 3. move(castling move)                             = 6 change + 2 capture.
		
//	     => 3 change 2 capture. + 3. move(capture move)(target = preMove's target.)  = 4 change + 3 capture.
//		 => 3 change 2 capture. + 3. move(capture move)(target != preMove's target.) = 5 change + 3 capture.
//		 => 3 change 2 capture. + 3. move(normal move independent)                   = 5 change + 2 capture.
//		 => 3 change 2 capture. + 3. move(normal move but source targeted)           = 4 change + 2 capture.
//		 => 3 change 2 capture. + 3. move(normal move but OWN source targeted)       = 4 change + 2 capture.
//		 => 3 change 2 capture. + 3. move(castling move)                             = 7 change + 2 capture.
		
		//
		int initialPieceCount = Utility.getPieceCount(previousBoard);
		int currentPieceCount = Utility.getPieceCount(currentBoard);
		int capturedPieceCount = initialPieceCount - currentPieceCount;
		//

		//
		int fromIndex[] = Utility.convertTwoDimensionIndex(scheduledPreMove.getFrom());
		int toIndex[] = Utility.convertTwoDimensionIndex(scheduledPreMove.getTo());
		byte prevFromItem = previousBoard[fromIndex[0]][fromIndex[1]];
		byte currFromItem = currentBoard[fromIndex[0]][fromIndex[1]];
		byte currToItem = currentBoard[toIndex[0]][toIndex[1]];
		//
		
		//
		boolean isOpponentsMovesTargetEqualsPreMovesTarget = prevFromItem != currToItem;
		boolean isSourceTargeted = currFromItem != 0;
		//
		
		List<BotMove> moveSequence = new ArrayList<>();
		
//		System.out.println("Diff Size = " + differences.size());
//		System.out.println("capturedPieceCount = " + capturedPieceCount);
		
		
		if (differences.size() == 3) {
			if (isSourceTargeted) {
//			 => 2 change 2 capture. + 3. move(normal move but source targeted)           = 3 change + 2 capture.
				// there is 2 unknown (discharging.) square...
				// maximum 1 try will lead to find sequences.
				
				// 2 discharge
				// 1 change (ignore)
				
				CellDifference dischargingCellDif1 = null;
				CellDifference dischargingCellDif2 = null;
				
				for (CellDifference cellDifference : differences) {
					if (cellDifference.getDiffType() == CellDifferenceType.CHANGE) {
						// ignore
					} else if (cellDifference.getDiffType() == CellDifferenceType.DISCHARGE) {
						if (dischargingCellDif1 == null) {
							dischargingCellDif1 = cellDifference;
						} else {
							dischargingCellDif2 = cellDifference;
						}
					} else {
						throw new RuntimeException("Bad Algorithm.");
					}
				}
			
				
				BotMove firstMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
				BotMove secondMove = scheduledPreMove;
				BotMove thirdMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getFrom());
				moveSequence.add(firstMove);
				moveSequence.add(secondMove);
				moveSequence.add(thirdMove);
				if (!isValidMoveSequence(moveSequence)) {
					moveSequence.clear();
					firstMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
					thirdMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getFrom());
					moveSequence.add(firstMove);
					moveSequence.add(secondMove);
					moveSequence.add(thirdMove);
				}
				return moveSequence;
			} else {
//				 => 2 change 2 capture. + 3. move(normal move but OWN source targeted)       = 3 change + 2 capture.
				// 2 discharge (one of them is premoves source square. so exclude it.)
				// 1 change
				
				CellDifference changingCellDiff = null;
				CellDifference dischargingCellDiff = null;
				
				for (CellDifference cellDifference : differences) {
					if (cellDifference.getDiffType() == CellDifferenceType.CHANGE) {
						changingCellDiff = cellDifference;
					} else if (cellDifference.getDiffType() == CellDifferenceType.DISCHARGE) {
						if (cellDifference.getCellCoordinate().toOneDimensionalCoordinate() != scheduledPreMove.getFrom()) {
							dischargingCellDiff = cellDifference;
						}
					} else {
						throw new RuntimeException("Bad Algorithm.");
					}
				}
				
				BotMove firstMove = new BotMove(changingCellDiff.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
				BotMove secondMove = scheduledPreMove;
				BotMove thirdMove = new BotMove(dischargingCellDiff.getCellCoordinate().toOneDimensionalCoordinate(), changingCellDiff.getCellCoordinate().toOneDimensionalCoordinate());
				moveSequence.add(firstMove);
				moveSequence.add(secondMove);
				moveSequence.add(thirdMove);
				return moveSequence;
			}

		} else if (differences.size() == 4) {
			if (capturedPieceCount == 3) {
//			     => 2 change 2 capture. + 3. move(capture move)(target = preMove's target.)  = 4 change + 3 capture. (isOpponentsMovesTargetEqualsPreMovesTarget = true)  (isRecaptureWithTheSameTypeOfPiece = true)
//				 => 2 change 2 capture. + 3. move(capture move)(target != preMove's target.) = 4 change + 3 capture. (isOpponentsMovesTargetEqualsPreMovesTarget = false) (isRecaptureWithTheSameTypeOfPiece = true)
//			     => 3 change 2 capture. + 3. move(capture move)(target = preMove's target.)  = 4 change + 3 capture. (isOpponentsMovesTargetEqualsPreMovesTarget = true)  (isRecaptureWithTheSameTypeOfPiece = false)
				
				if (isOpponentsMovesTargetEqualsPreMovesTarget) {
					// 3 discharge (one of them is premoves source square. so exclude it.)
					// 1 change
					// Again there is 2 unknown square.
//					     => 2 change 2 capture. + 3. move(capture move)(target = preMove's target.)  = 4 change + 3 capture. (isOpponentsMovesTargetEqualsPreMovesTarget = true)  (isRecaptureWithTheSameTypeOfPiece = true)
					
					// or
					
					// 3 discharge (one of them is premoves source square. so exclude it.)
					// 1 change
					// Again there is 2 unknown square.
//					     => 3 change 2 capture. + 3. move(capture move)(target = preMove's target.)  = 4 change + 3 capture. (isOpponentsMovesTargetEqualsPreMovesTarget = true)  (isRecaptureWithTheSameTypeOfPiece = false)
					
					
					CellDifference dischargingCellDif1 = null;
					CellDifference dischargingCellDif2 = null;
					
					for (CellDifference cellDifference : differences) {
						if (cellDifference.getDiffType() == CellDifferenceType.DISCHARGE) {
							if (cellDifference.getCellCoordinate().toOneDimensionalCoordinate() != scheduledPreMove.getFrom()) {
								if (dischargingCellDif1 == null) {
									dischargingCellDif1 = cellDifference;
								} else {
									dischargingCellDif2 = cellDifference;
								}
							}
						} else if (cellDifference.getDiffType() == CellDifferenceType.CHANGE) {
							// ignore
						} else {
							throw new RuntimeException("Bad Algorithm.");
						}
					}
					
					BotMove firstMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
					BotMove secondMove = scheduledPreMove;
					BotMove thirdMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
					moveSequence.add(firstMove);
					moveSequence.add(secondMove);
					moveSequence.add(thirdMove);
					if (!isValidMoveSequence(moveSequence)) {
						moveSequence.clear();
						
						firstMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
						thirdMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
						
						moveSequence.add(firstMove);
						moveSequence.add(secondMove);
						moveSequence.add(thirdMove);
					}
					return moveSequence;
						
				} else {
					
//					 => 2 change 2 capture. + 3. move(capture move)(target != preMove's target.) = 4 change + 3 capture. (isOpponentsMovesTargetEqualsPreMovesTarget = false) (isRecaptureWithTheSameTypeOfPiece = true)
					// 3 discharge (one of them is premoves source square. so exclude it.)
					// 1 change.
					
					CellDifference changingCelDiff = null;
					CellDifference dischargingCellDif1 = null;
					CellDifference dischargingCellDif2 = null;
					for (CellDifference cellDifference : differences) {
						if (cellDifference.getDiffType() == CellDifferenceType.CHANGE) {
							changingCelDiff = cellDifference;
						} else if (cellDifference.getDiffType() == CellDifferenceType.DISCHARGE) {
							if (cellDifference.getCellCoordinate().toOneDimensionalCoordinate() != scheduledPreMove.getFrom()) {
								if (dischargingCellDif1 == null) {
									dischargingCellDif1 = cellDifference;
								} else {
									dischargingCellDif2 = cellDifference;
								}
							}
						} else {
							throw new RuntimeException("Bad Algorithm.");
						}
					}
						
					BotMove firstMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
					BotMove secondMove = scheduledPreMove;
					BotMove thirdMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), changingCelDiff.getCellCoordinate().toOneDimensionalCoordinate());
					moveSequence.add(firstMove);
					moveSequence.add(secondMove);
					moveSequence.add(thirdMove);
					if (!isValidMoveSequence(moveSequence)) {
						moveSequence.clear();
						
						firstMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
						thirdMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), changingCelDiff.getCellCoordinate().toOneDimensionalCoordinate());
						
						moveSequence.add(firstMove);
						moveSequence.add(secondMove);
						moveSequence.add(thirdMove);
					}
					return moveSequence;
				}
				
			} else if (capturedPieceCount == 2) {
				
				if (isSourceTargeted) {
//					 => 3 change 2 capture. + 3. move(normal move but source targeted)           = 4 change + 2 capture.
					// 2 discharge.
					// 2 change. (both of them are preMoves source and target squares.)
					
					CellDifference dischargingCellDif1 = null;
					CellDifference dischargingCellDif2 = null;
					
					for (CellDifference cellDifference : differences) {
						if (cellDifference.getDiffType() == CellDifferenceType.DISCHARGE) {
							if (cellDifference.getCellCoordinate().toOneDimensionalCoordinate() == scheduledPreMove.getFrom() || cellDifference.getCellCoordinate().toOneDimensionalCoordinate() == scheduledPreMove.getTo()) {
								throw new RuntimeException("Bad Algorithm.");								
							}
							if (dischargingCellDif1 == null) {
								dischargingCellDif1 = cellDifference;
							} else {
								dischargingCellDif2 = cellDifference;
							}
						} else if (cellDifference.getDiffType() == CellDifferenceType.CHANGE) {
							if (cellDifference.getCellCoordinate().toOneDimensionalCoordinate() != scheduledPreMove.getFrom() && cellDifference.getCellCoordinate().toOneDimensionalCoordinate() != scheduledPreMove.getTo()) {
								throw new RuntimeException("Bad Algorithm.");								
							}
							// ignore
						} else {
							throw new RuntimeException("Bad Algorithm.");
						}
					}
					
					BotMove firstMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
					BotMove secondMove = scheduledPreMove;
					BotMove thirdMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getFrom());
					moveSequence.add(firstMove);
					moveSequence.add(secondMove);
					moveSequence.add(thirdMove);
					if (!isValidMoveSequence(moveSequence)) {
						moveSequence.clear();
						firstMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
						thirdMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getFrom());
						moveSequence.add(firstMove);
						moveSequence.add(secondMove);
						moveSequence.add(thirdMove);
					}
					return moveSequence;
				} else {
					if (existsDifference(differences, CellDifferenceType.CHARGE)) {
//					 => 2 change 2 capture. + 3. move(normal move independent)                   = 4 change + 2 capture.
						// 3 discharge (one of them is premoves source square. so exclude it.)
						// 1 charge
						
						CellDifference chargingCelDiff = null;
						CellDifference dischargingCellDif1 = null;
						CellDifference dischargingCellDif2 = null;
						for (CellDifference cellDifference : differences) {
							if (cellDifference.getDiffType() == CellDifferenceType.CHARGE) {
								chargingCelDiff = cellDifference;
							} else if (cellDifference.getDiffType() == CellDifferenceType.DISCHARGE) {
								if (cellDifference.getCellCoordinate().toOneDimensionalCoordinate() != scheduledPreMove.getFrom()) {
									if (dischargingCellDif1 == null) {
										dischargingCellDif1 = cellDifference;
									} else {
										dischargingCellDif2 = cellDifference;
									}
								}
							} else {
								throw new RuntimeException("Bad Algorithm.");
							}
						}
						
						BotMove firstMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
						BotMove secondMove = scheduledPreMove;
						BotMove thirdMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), chargingCelDiff.getCellCoordinate().toOneDimensionalCoordinate());
						moveSequence.add(firstMove);
						moveSequence.add(secondMove);
						moveSequence.add(thirdMove);
						if (!isValidMoveSequence(moveSequence)) {
							moveSequence.clear();
							firstMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
							thirdMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), chargingCelDiff.getCellCoordinate().toOneDimensionalCoordinate());
							moveSequence.add(firstMove);
							moveSequence.add(secondMove);
							moveSequence.add(thirdMove);
						}
						return moveSequence;
					} else {
						
//						 => 3 change 2 capture. + 3. move(normal move but OWN source targeted)       = 4 change + 2 capture.
						 // 2 discharge (one of them is premoves source square. so exclude it.)
						 // 2 change (one of them is premoves target square. so exclude it.)
						
						CellDifference changingCelDiff = null;
						CellDifference dischargingCellDif = null;
						for (CellDifference cellDifference : differences) {
							if (cellDifference.getDiffType() == CellDifferenceType.CHANGE) {
								if (cellDifference.getCellCoordinate().toOneDimensionalCoordinate() != scheduledPreMove.getTo()) {
									changingCelDiff = cellDifference;
								}
							} else if (cellDifference.getDiffType() == CellDifferenceType.DISCHARGE) {
								if (cellDifference.getCellCoordinate().toOneDimensionalCoordinate() != scheduledPreMove.getFrom()) {
									dischargingCellDif = cellDifference;
								}
							} else {
								throw new RuntimeException("Bad Algorithm.");
							}
						}
						
						BotMove firstMove = new BotMove(changingCelDiff.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
						BotMove secondMove = scheduledPreMove;
						BotMove thirdMove = new BotMove(dischargingCellDif.getCellCoordinate().toOneDimensionalCoordinate(), changingCelDiff.getCellCoordinate().toOneDimensionalCoordinate());
						moveSequence.add(firstMove);
						moveSequence.add(secondMove);
						moveSequence.add(thirdMove);
						return moveSequence;
					}
				}
			} else {
				throw new RuntimeException("Bad Algorithm.");
			}
		} else if (differences.size() == 5) {
			if (capturedPieceCount == 2) {
//				 => 3 change 2 capture. + 3. move(normal move independent)                   = 5 change + 2 capture.
				// 3 discharge. (one of them is premoves source square. so exclude it.)
				// 1 change. (premoves target.)
				// 1 charge.
				
				CellDifference chargingCelDiff = null;
				CellDifference dischargingCellDif1 = null;
				CellDifference dischargingCellDif2 = null;
				for (CellDifference cellDifference : differences) {
					if (cellDifference.getDiffType() == CellDifferenceType.CHARGE) {
						chargingCelDiff = cellDifference;
					} else if (cellDifference.getDiffType() == CellDifferenceType.CHANGE) {
						// ignore
					} else if (cellDifference.getDiffType() == CellDifferenceType.DISCHARGE) {
						if (cellDifference.getCellCoordinate().toOneDimensionalCoordinate() != scheduledPreMove.getFrom()) {
							if (dischargingCellDif1 == null) {
								dischargingCellDif1 = cellDifference;
							} else {
								dischargingCellDif2 = cellDifference;
							}
						}
					}
				}
				
				BotMove firstMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
				BotMove secondMove = scheduledPreMove;
				BotMove thirdMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), chargingCelDiff.getCellCoordinate().toOneDimensionalCoordinate());
				moveSequence.add(firstMove);
				moveSequence.add(secondMove);
				moveSequence.add(thirdMove);
				if (!isValidMoveSequence(moveSequence)) {
					moveSequence.clear();
					firstMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
					thirdMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), chargingCelDiff.getCellCoordinate().toOneDimensionalCoordinate());
					moveSequence.add(firstMove);
					moveSequence.add(secondMove);
					moveSequence.add(thirdMove);
				}
				return moveSequence;
				
			} else if (capturedPieceCount == 3) {
//				 => 3 change 2 capture. + 3. move(capture move)(target != preMove's target.) = 5 change + 3 capture.
				// 3 discharge. (one of them is premoves source square. so exclude it.)
				// 2 change. (premoves target.) (one of them is premoves target square. so exclude it.)
				
				CellDifference changingCelDiff = null;
				CellDifference dischargingCellDif1 = null;
				CellDifference dischargingCellDif2 = null;
				for (CellDifference cellDifference : differences) {
					if (cellDifference.getDiffType() == CellDifferenceType.CHANGE) {
						if (cellDifference.getCellCoordinate().toOneDimensionalCoordinate() != scheduledPreMove.getTo()) {
							changingCelDiff = cellDifference;
						}
					} else if (cellDifference.getDiffType() == CellDifferenceType.DISCHARGE) {
						if (cellDifference.getCellCoordinate().toOneDimensionalCoordinate() != scheduledPreMove.getFrom()) {
							if (dischargingCellDif1 == null) {
								dischargingCellDif1 = cellDifference;
							} else {
								dischargingCellDif2 = cellDifference;
							}
						}
					} else {
						throw new RuntimeException("Bad Algorithm.");
					}
				}
				
				BotMove firstMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
				BotMove secondMove = scheduledPreMove;
				BotMove thirdMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), changingCelDiff.getCellCoordinate().toOneDimensionalCoordinate());
				moveSequence.add(firstMove);
				moveSequence.add(secondMove);
				moveSequence.add(thirdMove);
				if (!isValidMoveSequence(moveSequence)) {
					moveSequence.clear();
					firstMove = new BotMove(dischargingCellDif2.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
					thirdMove = new BotMove(dischargingCellDif1.getCellCoordinate().toOneDimensionalCoordinate(), changingCelDiff.getCellCoordinate().toOneDimensionalCoordinate());
					moveSequence.add(firstMove);
					moveSequence.add(secondMove);
					moveSequence.add(thirdMove);
				}
				return moveSequence;
				
			} else {
				throw new RuntimeException("Bad Algorithm.");
			}

		} else if (differences.size() >= 6) {
//			 => 2 change 2 capture. + 3. move(castling move)                             = 6 change + 2 capture.
			
			//or 
			
//			 => 3 change 2 capture. + 3. move(castling move)                             = 7 change + 2 capture.
			
			CellDifference castlingTo = null;
			CellDifference castlingFrom = null;
			CellDifference dischargingCellDif = null;
			
			for (CellDifference cellDifference : differences) {
				if (cellDifference.getDiffType() == CellDifferenceType.DISCHARGE) {
					if (cellDifference.getPreviousItem() == EngineConstants.WHITE_KING || cellDifference.getPreviousItem() == EngineConstants.BLACK_KING) {
						castlingFrom = cellDifference;
					} else if (cellDifference.getPreviousItem() == EngineConstants.WHITE_ROOK || cellDifference.getPreviousItem() == EngineConstants.BLACK_ROOK) {
						// ignore
					} else {
						dischargingCellDif = cellDifference;
					}
				} else if (cellDifference.getDiffType() == CellDifferenceType.CHARGE) {
					if (cellDifference.getCurrentItem() == EngineConstants.WHITE_KING || cellDifference.getCurrentItem() == EngineConstants.BLACK_KING) {
						castlingTo = cellDifference;
					} else if (cellDifference.getPreviousItem() == EngineConstants.WHITE_ROOK || cellDifference.getPreviousItem() == EngineConstants.BLACK_ROOK) {
						// ignore
					}
				}
				
			}
			
			BotMove firstMove = new BotMove(dischargingCellDif.getCellCoordinate().toOneDimensionalCoordinate(), scheduledPreMove.getTo());
			BotMove secondMove = scheduledPreMove;
			BotMove thirdMove = new BotMove(castlingFrom.getCellCoordinate().toOneDimensionalCoordinate(), castlingTo.getCellCoordinate().toOneDimensionalCoordinate());
			moveSequence.add(firstMove);
			moveSequence.add(secondMove);
			moveSequence.add(thirdMove);
			return moveSequence;

		} else {
			throw new RuntimeException("This is a subtle bug.");
		}
		
	}
	
	private boolean existsDifference(List<CellDifference> differences, CellDifferenceType diffType) {
		for (CellDifference cellDifference : differences) {
			if (cellDifference.getDiffType() == diffType) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isValidMoveSequence(List<BotMove> moveSequence) {
		int successfulMoveCount = 0;
		int sequenceSize = moveSequence.size();
		for (int i = 0; i < sequenceSize ; i++) {
			BotMove move = moveSequence.get(i);
			if (controller.doMove(move, false)) {
				successfulMoveCount ++;
			} else {
				break;
			}
		}
		
		for (int i = 0; i < successfulMoveCount; i++) {
			controller.undoMove();
		}
		
		return sequenceSize == successfulMoveCount;
	}
	
	public int getCapturedPieceCount(byte[][] prevBoard, byte[][] currBoard) {
		int initialPieceCount = Utility.getPieceCount(prevBoard);
		int currentPieceCount = Utility.getPieceCount(currBoard);
		int capturedPieceCount = initialPieceCount - currentPieceCount;
		return capturedPieceCount;
	}
	
}

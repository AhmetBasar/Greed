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
package chess.perft;

import java.util.ArrayList;

import chess.debug.DebugUtility;
import chess.engine.BoardFactory;
import chess.engine.EngineConstants;
import chess.engine.IBoard;
import chess.engine.Transformer;
import chess.gui.BaseGui;
import chess.movegen.MoveGeneration;

public class PerformanceTestingSingleThreaded {
	private MoveGeneration moveGeneration = new MoveGeneration(true);
	private PerftResult perftResult = new PerftResult();
	
	public static void main(String[] args) {
		long ilk = System.currentTimeMillis();
		byte[][] sourceBoard = DebugUtility.getDefaultBoard();
		long[] bitboard = Transformer.getBitboardStyl(sourceBoard);
		byte[] pieces = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(sourceBoard));
		byte[][] castlingRights = { { 1, 1 }, { 1, 1 } };
		int depth = 5;
		IBoard board = BoardFactory.getInstance2(bitboard, pieces, 64, castlingRights, 0, new ArrayList<Long>(), 0);
		PerformanceTestingSingleThreaded obj = new PerformanceTestingSingleThreaded();
		obj.perft(depth, board);
		System.out.println(obj.perftResult);
		System.out.println("time = " + (System.currentTimeMillis() - ilk));
	}
	
	public static void getAllVariations(byte[][] boardArray, int side, int depth, byte[][] castlingRights, BaseGui baseGui, int threadCount, int epTarget, int epSquare){
		long startTime = System.currentTimeMillis();
		long[] bitboard = Transformer.getBitboardStyl(boardArray);
		byte[] pieces = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(boardArray));
		IBoard board = BoardFactory.getInstance2(bitboard, pieces, epTarget, castlingRights, 0, new ArrayList<Long>(), side);
		PerformanceTestingSingleThreaded obj = new PerformanceTestingSingleThreaded();
		obj.perft(depth, board);
		obj.perftResult.setTimeConsumed(System.currentTimeMillis() - startTime);
		baseGui.getDebugPanel().setOutputMessage(obj.perftResult.toString());
		baseGui.getDebugPanel().setEnableAll(true);
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

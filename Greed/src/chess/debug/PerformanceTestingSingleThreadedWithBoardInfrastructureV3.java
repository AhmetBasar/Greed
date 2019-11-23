/**********************************************
 * Greed, a chess engine written in java.
 * Copyright (C) 2019 Ahmet Ba�ar
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

import chess.engine.BoardV6;
import chess.engine.EngineConstants;
import chess.engine.LegalityV4;
import chess.engine.MoveGeneration;
import chess.engine.Transformer;
import chess.gui.BaseGui;

public class PerformanceTestingSingleThreadedWithBoardInfrastructureV3 {
	private MoveGeneration moveGeneration = new MoveGeneration();
	private LegalityV4 legality = new LegalityV4();
	private PerftResult perftResult = new PerftResult();

	public static void main(String[] args) {
		long ilk = System.currentTimeMillis();
		byte[][] sourceBoard = DebugUtility.getDefaultBoard();
		long[] bitboard = Transformer.getBitboardStyl(sourceBoard);
		byte[] pieces = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(sourceBoard));
		byte[][] castlingRights = { { 1, 1 }, { 1, 1 } };
		int depth = 6;
		BoardV6 board = new BoardV6(bitboard, pieces, 64, depth, castlingRights, 0L, 0, 0L);
		PerformanceTestingSingleThreadedWithBoardInfrastructureV3 obj = new PerformanceTestingSingleThreadedWithBoardInfrastructureV3();
		obj.perft(depth, board, 1);
		System.out.println(obj.perftResult);
		System.out.println("time = " + (System.currentTimeMillis() - ilk));
	}
	
	public static void getAllVariations(byte[][] boardArray, int side, int depth, byte[][] castlingRights, BaseGui baseGui, int threadCount, int epTarget, int epSquare){
		long startTime = System.currentTimeMillis();
		long[] bitboard = Transformer.getBitboardStyl(boardArray);
		byte[] pieces = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(boardArray));
		BoardV6 board = new BoardV6(bitboard, pieces, epTarget, depth, castlingRights, 0L, 0, 0L);
		PerformanceTestingSingleThreadedWithBoardInfrastructureV3 obj = new PerformanceTestingSingleThreadedWithBoardInfrastructureV3();
		obj.perft(depth, board, 1);
		obj.perftResult.setTimeConsumed(System.currentTimeMillis() - startTime);
		baseGui.getDebugPanel().setOutputMessage(obj.perftResult.toString());
		baseGui.getDebugPanel().setEnableAll(true);
	}

	public void perft(int depth, BoardV6 board, int side) {

		if (depth == 0) {
			perftResult.incrementNodeCount();
			return;
		}

		int depthMinusOne = depth - 1;
		int opSide = side;
		side = side ^ 1;
		int i = 0;

		boolean existsLegalMove = false;
		int move;
		int[] moveList = moveGeneration.generateMoves(board.getBitboard(), side, board.getEpTarget(depth), board.getCastlingRights(depth));
		while (moveList[i] != 0) {
			move = moveList[i];
			board.doMove(move, side, opSide, depth);
			if (!legality.isKingInCheck(board.getBitboard(), side)) {
				existsLegalMove = true;
				if (depth == 1) {
					//
					int moveType = move & 0x00070000;
					byte capturedPiece = board.getCapturedPiece(depth);
					
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

					if (legality.isKingInCheck(board.getBitboard(), opSide)) {
						perftResult.incrementCheckCount();
					}
					//
				}

				perft(depthMinusOne, board, side);
			}

			board.undoMove(move, side, opSide, depth);

			i++;
		}

		if (depth == 1 && !existsLegalMove) {
			perftResult.incrementCheckMateCount();
		}

	}

	public PerftResult getPerftResult() {
		return perftResult;
	}

}
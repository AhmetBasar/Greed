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
import chess.engine.EngineConstants;
import chess.engine.IBoard;
import chess.engine.LegalityV4;
import chess.engine.MoveGeneration;
import chess.engine.Transformer;

public class PerformanceTestingSingleThreadedWithBoardInfrastructureV2 {
	private MoveGeneration moveGeneration = new MoveGeneration();
	private LegalityV4 legality = new LegalityV4();
	private PerftResult perftResult = new PerftResult();

	public static void main(String[] args) {
		long ilk = System.currentTimeMillis();
		byte[][] sourceBoard = DebugUtility.getDefaultBoard();
		long[] bitboard = Transformer.getBitboardStyl(sourceBoard);
		byte[] pieces = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(sourceBoard));
		byte[][] castlingRights = { { 1, 1 }, { 1, 1 } };
		int depth = 5;
		IBoard board = BoardFactory.getInstance(bitboard, pieces, 64, -1, depth, castlingRights, 0L, 0);
		PerformanceTestingSingleThreadedWithBoardInfrastructureV2 obj = new PerformanceTestingSingleThreadedWithBoardInfrastructureV2();
		obj.perft(depth, board, 1);
		System.out.println(obj.perftResult);
		System.out.println("time = " + (System.currentTimeMillis() - ilk));
	}

	public void perft(int depth, IBoard board, int side) {

		if (depth == 0) {
			perftResult.incrementNodeCount();
			return;
		}

		board.deepDive(depth);
		int depthMinusOne = depth - 1;
		int depthPlusOne = depth + 1;
		int opSide = side;
		side = side ^ 1;
		int i = 0;

		boolean existsLegalMove = false;
		int move;
		int[] moveList = moveGeneration.generateMoves(board, side, depthPlusOne);
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

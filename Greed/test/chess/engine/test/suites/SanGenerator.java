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
package chess.engine.test.suites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chess.engine.BoardFactory;
import chess.engine.EngineConstants;
import chess.engine.IBoard;
import chess.engine.Move;
import chess.movegen.MoveGeneration;
import chess.util.Utility;

public class SanGenerator {

	public static final String QUEEN_SIDE_CASTLING = "O-O-O";
	public static final String KING_SIDE_CASTLING = "O-O";
	public static final String[] FILES = new String[] { "a", "b", "c", "d", "e", "f", "g", "h" };
	public static final String[] PIECES = new String[] { "", "", "", "", "N", "N", "B", "B", "R", "R", "Q", "Q", "K", "K" };
	public static final String CAPTURE_SIGN = "x";

	public static String convertSanMove(int move, byte[] pieces, long[] bitboard, int side, int epTarget, int epSquare,
			byte[][] castlingRights) {

		String sanMove = null;

		byte promotedPiece = 0;
		int moveType = Move.getMoveType(move);
		int to = Move.getTo(move);
		int from = move & 0x000000ff;
		byte fromPiece = pieces[from];
		byte capturedPiece = 0;

		String fromFile = FILES[Utility.getFile(from)];
		String fromRank = String.valueOf(Utility.getRank(from));
		String fromSquare = fromFile + fromRank;

		String toFile = FILES[Utility.getFile(to)];
		String toRank = String.valueOf(Utility.getRank(to));
		String toSquare = toFile + toRank;

		String pieceSymbol = PIECES[fromPiece];

		IBoard board = BoardFactory.getInstance2(bitboard, pieces, epTarget, castlingRights, 0, new ArrayList<Long>(), side);
		Map<String, Map<String, List<String>>> ambiguities = getAmbiguities(1, board, move);

		switch (moveType) {
		case 0:

			capturedPiece = pieces[to];

			String strTo = String.valueOf(to);
			String strFromPiece = String.valueOf(fromPiece);
			List<String> possibleFromSquares = ambiguities.get(strTo).get(strFromPiece);
			if (possibleFromSquares.size() < 1) {
				throw new RuntimeException("Fatal Error.");
			} else if (possibleFromSquares.size() == 1) {
				switch (capturedPiece) {
				case 0:
					sanMove = pieceSymbol + toSquare;
					break;
				default:
					switch (fromPiece) {
					case EngineConstants.WHITE_PAWN:
					case EngineConstants.BLACK_PAWN:
						sanMove = fromFile /**
											 * + [fromRank? what if there is an
											 * EnPassant ambiguity] +
											 */
								+ CAPTURE_SIGN + toSquare;
						break;
					default:
						sanMove = pieceSymbol + CAPTURE_SIGN + toSquare;
						break;
					}
					break;
				}
			} else {

				Map<String, Integer> fileCounts = new HashMap<String, Integer>();
				Map<String, Integer> rankCounts = new HashMap<String, Integer>();
				for (String fsq : possibleFromSquares) {
					String strFile = String.valueOf(Utility.getFile(Integer.parseInt(fsq)));
					String strRank = String.valueOf(Utility.getRank(Integer.parseInt(fsq)));

					Integer tempFileCount = fileCounts.get(strFile);
					fileCounts.put(strFile, tempFileCount != null ? ++tempFileCount : 1);

					Integer tempRankCount = rankCounts.get(strRank);
					rankCounts.put(strRank, tempRankCount != null ? ++tempRankCount : 1);
				}

				String fromSquareIndicator = "";
				if (fileCounts.get(String.valueOf(Utility.getFile(from))).intValue() == 1) {
					fromSquareIndicator = fromFile;
				} else if (rankCounts.get(String.valueOf(fromRank)).intValue() == 1) {
					fromSquareIndicator = fromRank;
				} else {
					fromSquareIndicator = fromSquare;
				}

				switch (capturedPiece) {
				case 0:
					sanMove = pieceSymbol + fromSquareIndicator + toSquare;
					break;
				default:
					sanMove = pieceSymbol + fromSquareIndicator + CAPTURE_SIGN + toSquare;
					break;
				}
			}

			break;
		case EngineConstants.DOUBLE_PUSH_SHIFTED:
			sanMove = toSquare;
			break;
		case EngineConstants.EP_CAPTURE_SHIFTED:
			sanMove = fromFile + CAPTURE_SIGN + toSquare;
			break;
		case EngineConstants.PROMOTION_SHIFTED:
			capturedPiece = pieces[to];
			promotedPiece = Move.getPromotedPiece(move);
			String promotedPieceSymbol = PIECES[promotedPiece];
			switch (capturedPiece) {
			case 0:
				sanMove = toSquare + promotedPieceSymbol;
				break;
			default:
				sanMove = fromFile + CAPTURE_SIGN + toSquare + promotedPieceSymbol;
				break;
			}
			break;
		default:
			boolean isQueenSideCastling = (move & 0x00ff0000) == (EngineConstants.QUEEN_SIDE_CASTLING << 16);
			sanMove = isQueenSideCastling ? QUEEN_SIDE_CASTLING : KING_SIDE_CASTLING;
			break;
		}

		if (ambiguities.containsKey("isKingInCheck")) {
			sanMove += "+";
		}

		return sanMove;
	}

	private static Map<String, Map<String, List<String>>> getAmbiguities(int depth, IBoard board, int bestMove) {
		Map<String, Map<String, List<String>>> ambiguities = new HashMap<>();

		int move;
		//
		MoveGeneration moveGeneration = new MoveGeneration(false);
		moveGeneration.startPly();
		moveGeneration.generateAttacks(board);
		moveGeneration.generateMoves(board);
		
		while (moveGeneration.hasNext()) {
			move = moveGeneration.next();

			String to = String.valueOf(Move.getTo(move));
			int from = move & 0x000000ff;
			int moveType = Move.getMoveType(move);
			String fromPiece = String.valueOf(board.getPieces()[from]);

			if (board.isLegal(move)) {
				if (moveType == 0) {

					Map<String, List<String>> fromAmbiguities = ambiguities.get(to);
					if (fromAmbiguities == null) {
						fromAmbiguities = new HashMap<>();
						ambiguities.put(to, fromAmbiguities);
					}

					List<String> fromSquares = fromAmbiguities.get(fromPiece);
					if (fromSquares == null) {
						fromSquares = new ArrayList<>();
						fromAmbiguities.put(fromPiece, fromSquares);
					}

					fromSquares.add(String.valueOf(from));
				}
			}

			board.doMove(move);
			
			boolean isKingInCheck = board.getCheckers() != 0;
			
			if (move == bestMove && isKingInCheck) {
				ambiguities.put("isKingInCheck", null);
			}

			board.undoMove(move);
		}

		return ambiguities;
	}
}

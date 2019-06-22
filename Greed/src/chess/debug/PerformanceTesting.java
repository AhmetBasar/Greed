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

import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.LegalityV4;
import chess.engine.MoveGeneration;
import chess.engine.Transformer;
import chess.gui.BaseGui;

public class PerformanceTesting {
	private static int THREAD_COUNT = 5;
	private int[] pushDiffs = { 8, 64 - 8 };
	private MoveGeneration moveGeneration = new MoveGeneration();
	private LegalityV4 legality = new LegalityV4();

	private int depth;
	private int epT;
	private int epS;
	private long[] bitboard;
	private byte[] pieces;
	private int[] moveList;
	private byte[][] castlingRights;
	private int side;

	private int[][] castlingRookSources = { { 0, 7 }, { 56, 63 } };
	private int[][] castlingRookTargets = { { 3, 5 }, { 59, 61 } };
	private byte[] kingPositions = { 4, 60 };
	private byte[][] rookPositions = { { 0, 7 }, { 56, 63 } };
	
	private static long ilk = System.currentTimeMillis();
	private static boolean isFromScreen = false;
	private static BaseGui base;
	
	public static final Object lock = new Object();
	
	
	
	public static void main(String[] args) {
		isFromScreen = false;
		byte[][] sourceBoard = DebugUtility.getDefaultBoard();
		long[] bitboard = Transformer.getBitboardStyl(sourceBoard);
		byte[] pieces = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(sourceBoard));
		byte[][] castlingRights = { { 1, 1 }, { 1, 1 } };
		PerformanceTesting.dispatchThreads(1, 64, -1, bitboard, pieces, castlingRights, 0, THREAD_COUNT);
	}
	
	public static void getAllVariations(byte[][] board, int side, int depth, byte[][] castlingRights, BaseGui baseGui, int threadCount, int epTarget, int epSquare){
		base = baseGui;
		isFromScreen = true;
		resetCounters();
		long[] bitboard = Transformer.getBitboardStyl(board);
		byte[] pieces = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(board));
		PerformanceTesting.dispatchThreads(depth, epTarget, epSquare, bitboard, pieces, castlingRights, side, threadCount);
	}
	
	private synchronized static void incrementMoveCount(long moveC) {
		deadThreadCount++;
		moveCount = moveCount + moveC;
		if (deadThreadCount == aliveThreadCount) {
			if(isFromScreen){
				outputMessage = outputMessage + "moveCount = " + moveCount + "\n";
				outputMessage = outputMessage + "capture count = "+ captureCount + "\n";
				outputMessage = outputMessage + "ep count = "+ epCount + "\n";
				outputMessage = outputMessage + "castling count = "+ castlingCount + "\n";
				outputMessage = outputMessage + "promotion count = "+ promotionCount + "\n";
				outputMessage = outputMessage + "check count = "+ checkCount + "\n";
				outputMessage = outputMessage + "checkMate count = "+ checkMateCount + "\n";
				outputMessage = outputMessage + "----------------------------" + "\n";
				outputMessage = outputMessage + "active thread count = " + aliveThreadCount + "\n";
				outputMessage = outputMessage + "Time Consumed = " + (System.currentTimeMillis() - ilk) + "\n";
				base.getDebugPanel().setOutputMessage(outputMessage);
				base.getDebugPanel().setEnableAll(true);
			} else {
				System.out.println("moveCount = " + moveCount);
				System.out.println("capture count = "+ captureCount);
				System.out.println("ep count = "+ epCount);
				System.out.println("castling count = "+ castlingCount);
				System.out.println("promotion count = "+ promotionCount);
				System.out.println("check count = "+ checkCount);
				System.out.println("checkMate count = "+ checkMateCount);
				System.out.println("----------------------------");
				System.out.println("active thread count = " + aliveThreadCount);
				System.out.println("yenisi bu Time Consumed = " + (System.currentTimeMillis() - ilk));
			}
		}
	}

	private Thread th = new Thread() {
		public void run() {
			incrementMoveCount(traverseMoveList(depth, epT, epS, bitboard, pieces, moveList, castlingRights, side));
		}
	};

	public PerformanceTesting() {
	}

	public PerformanceTesting(int depth, int epT, int epS, long[] bitboard, byte[] pieces, int[] moveList,
			byte[][] castlingRights, int side) {
		this.depth = depth;
		this.epT = epT;
		this.epS = epS;
		this.bitboard = bitboard;
		this.pieces = pieces;
		this.moveList = moveList;
		this.castlingRights = castlingRights;
		this.side = side;
	}

	private synchronized static void dispatchThreads(int depth, int epT, int epS, long[] bitboard, byte[] pieces, byte[][] castlingRights, int side, int threadCount) {
		if (depth == 0)
			return;
		
		int i = 0;
		MoveGeneration moveGeneration = new MoveGeneration();
		int[] moveList = moveGeneration.generateMoves(bitboard, side, epT, castlingRights);
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
				new PerformanceTesting(depth, epT, epS, bitboard.clone(), pieces.clone(), partialMoveList,
						DebugUtility.deepCloneMultiDimensionalArray(castlingRights), side).start();
				aliveThreadCount++;
			}
			i++;
		}
	}

	public void start() {
		th.start();
	}

	private long traverseMoveList(int depth, int epT, int epS, long[] bitboard, byte[] pieces, int[] moveList,
			byte[][] castlingRights, int side) {
		if (depth == 0)
			return 1;
		int epTarget = epT;
		int epSquare = epS;

		int diff = pushDiffs[side];
		long nodes = 0;
		int i = 0;
		boolean isSimpleMove;
		boolean isDoublePush;
		boolean isEnPassantCapture;
		boolean isPromotion;

		int move;
		int to;
		int from;
		byte fromPiece;
		byte capturedPiece;
		byte promotedPiece = 0;
		
		//
		boolean isCastling;
		int castlingSide = 0;
		int castlingRookFrom = 0;
		int castlingRookTo = 0;
		byte sideToRook = 0;
		long tmpBitboard;

		byte whiteQueenSideCastlingRight = 0;
		byte whiteKingSideCastlingRight = 0;
		byte blackQueenSideCastlingRight = 0;
		byte blackKingSideCastlingRight = 0;

		boolean isRookOrKingMove;
		int opSide = side ^ 1;
		//

		while (moveList[i] != 0) {
			move = moveList[i];

			isSimpleMove = (move & 0x00ff0000) == 0 ? true : false;
			isDoublePush = (move & 0x00ff0000) == (EngineConstants.DOUBLE_PUSH << 16) ? true : false;
			isEnPassantCapture = (move & 0x00ff0000) == (EngineConstants.EP_CAPTURE << 16) ? true : false;
			isPromotion = (move & 0x000f0000) == (EngineConstants.PROMOTION << 16) ? true : false;
			
			to = (move & 0x0000ff00) >>> 8;
			from = move & 0x000000ff;
			fromPiece = pieces[from];
			capturedPiece = 0;
			
			//
			isCastling = (move & 0x00040000) == (EngineConstants.CASTLING << 16) ? true : false;
			isRookOrKingMove = (fromPiece == (side | EngineConstants.KING) || (fromPiece == (side | EngineConstants.ROOK))) 
					  || (pieces[to] == ((opSide) | EngineConstants.ROOK)) || (pieces[to] == ((opSide) | EngineConstants.KING));
			if (isRookOrKingMove) {
				whiteQueenSideCastlingRight = castlingRights[0][0];
				whiteKingSideCastlingRight = castlingRights[0][1];
				blackQueenSideCastlingRight = castlingRights[1][0];
				blackKingSideCastlingRight = castlingRights[1][1];
			}
			//

			if (isSimpleMove) {
				epTarget = 64;
				epSquare = -1;
				capturedPiece = pieces[to];
				pieces[from] = 0;
				pieces[to] = fromPiece;
				bitboard[fromPiece] &= ~(1L << from);
				bitboard[fromPiece] |= (1L << to);
				bitboard[capturedPiece] &= ~(1L << to);
			} else if (isDoublePush) {
				epTarget = Long.numberOfTrailingZeros(((1L << to) >>> diff) | ((1L << to) << (64 - diff)));
				epSquare = to;
				pieces[from] = 0;
				pieces[to] = fromPiece;
				bitboard[fromPiece] &= ~(1L << from);
				bitboard[fromPiece] |= (1L << to);
			} else if (isEnPassantCapture) {
				capturedPiece = pieces[epSquare];
				pieces[epSquare] = 0;
				pieces[from] = 0;
				pieces[to] = fromPiece;
				bitboard[fromPiece] &= ~(1L << from);
				bitboard[fromPiece] |= (1L << to);
				bitboard[capturedPiece] &= ~(1L << epSquare);
				epTarget = 64;
				epSquare = -1;
			} else if (isCastling) {
				epTarget = 64;
				epSquare = -1;
				castlingSide = (move & 0x00010000) >>> 16;
				sideToRook = (byte) (side | EngineConstants.ROOK);

				castlingRookFrom = castlingRookSources[side][castlingSide];
				castlingRookTo = castlingRookTargets[side][castlingSide];

				pieces[from] = 0;
				pieces[to] = fromPiece;
				bitboard[fromPiece] &= ~(1L << from);
				bitboard[fromPiece] |= (1L << to);

				pieces[castlingRookFrom] = 0;
				pieces[castlingRookTo] = sideToRook;
				bitboard[sideToRook] &= ~(1L << castlingRookFrom);
				bitboard[sideToRook] |= (1L << castlingRookTo);
			} else if(isPromotion){
				epTarget = 64;
				epSquare = -1;
				capturedPiece = pieces[to];
				promotedPiece = (byte)((move & 0x00f00000) >>> 20);
				
				pieces[from] = 0;
				pieces[to] = promotedPiece;
				bitboard[fromPiece] &= ~(1L << from);
				bitboard[promotedPiece] |= (1L << to);
				bitboard[capturedPiece] &= ~(1L << to);
			}
			
			// update castling rights
			if (isRookOrKingMove) {
				tmpBitboard = bitboard[(side) | EngineConstants.KING];
				castlingRights[side][0] = (byte) (castlingRights[side][0] & (tmpBitboard >>> kingPositions[side]));
				castlingRights[side][1] = (byte) (castlingRights[side][1] & (tmpBitboard >>> kingPositions[side]));

				tmpBitboard = bitboard[(side) | EngineConstants.ROOK];
				castlingRights[side][0] = (byte) (castlingRights[side][0] & (tmpBitboard >>> rookPositions[side][0]));
				castlingRights[side][1] = (byte) (castlingRights[side][1] & (tmpBitboard >>> rookPositions[side][1]));
				
				tmpBitboard = bitboard[(opSide) | EngineConstants.KING];
				castlingRights[opSide][0] = (byte) (castlingRights[opSide][0] & (tmpBitboard >>> kingPositions[opSide]));
				castlingRights[opSide][1] = (byte) (castlingRights[opSide][1] & (tmpBitboard >>> kingPositions[opSide]));

				tmpBitboard = bitboard[(opSide) | EngineConstants.ROOK];
				castlingRights[opSide][0] = (byte) (castlingRights[opSide][0] & (tmpBitboard >>> rookPositions[opSide][0]));
				castlingRights[opSide][1] = (byte) (castlingRights[opSide][1] & (tmpBitboard >>> rookPositions[opSide][1]));
			}
			//
			

			if (!legality.isKingInCheck(bitboard, side)) {
				if(depth == 1){
					if(capturedPiece != 0){
						PerformanceTesting.incrementCaptureCount();
					}
					if(isEnPassantCapture){
						PerformanceTesting.incrementEpCount();
					} else if(isCastling){
						PerformanceTesting.incrementCastlingCount();
					} else if(isPromotion){
						PerformanceTesting.incrementpromotionCount();
					}
					if(legality.isKingInCheck(bitboard, opSide)){
						incrementCheckCount();
					}
				}
				
				nodes += perft(depth - 1, epTarget, epSquare, bitboard, pieces, castlingRights, side);
			}
			
			// update castling rights
			if (isRookOrKingMove) {
				castlingRights[0][0] = whiteQueenSideCastlingRight;
				castlingRights[0][1] = whiteKingSideCastlingRight;
				castlingRights[1][0] = blackQueenSideCastlingRight;
				castlingRights[1][1] = blackKingSideCastlingRight;
			}

			epTarget = epT;
			epSquare = epS;
			if (isSimpleMove) {
				pieces[from] = fromPiece;
				pieces[to] = capturedPiece;
				bitboard[fromPiece] |= (1L << from);
				bitboard[fromPiece] &= ~(1L << to);
				bitboard[capturedPiece] |= (1L << to);
			} else if (isDoublePush) {
				pieces[from] = fromPiece;
				pieces[to] = 0;
				bitboard[fromPiece] |= (1L << from);
				bitboard[fromPiece] &= ~(1L << to);
			} else if (isEnPassantCapture) {
				pieces[epSquare] = capturedPiece;
				pieces[from] = fromPiece;
				pieces[to] = 0;
				bitboard[fromPiece] |= (1L << from);
				bitboard[fromPiece] &= ~(1L << to);
				bitboard[capturedPiece] |= (1L << epSquare);
			} else if (isCastling) {
				pieces[from] = fromPiece;
				pieces[to] = 0;
				bitboard[fromPiece] |= (1L << from);
				bitboard[fromPiece] &= ~(1L << to);

				pieces[castlingRookFrom] = sideToRook;
				pieces[castlingRookTo] = 0;
				bitboard[sideToRook] |= (1L << castlingRookFrom);
				bitboard[sideToRook] &= ~(1L << castlingRookTo);
			} else if(isPromotion){
				pieces[from] = fromPiece;
				pieces[to] = capturedPiece;
				bitboard[fromPiece] |= (1L << from);
				bitboard[promotedPiece] &= ~(1L << to);
				bitboard[capturedPiece] |= (1L << to);
			}
			
			i++;
		}
		
		if(depth == 1 && nodes == 0){
			incrementCheckMateCount();
		}
		return nodes;
	}

	private long perft(int depth, int epT, int epS, long[] bitboard, byte[] pieces, byte[][] castlingRights, int side) {
		if (depth == 0)
			return 1;
		int epTarget = epT;
		int epSquare = epS;
		int opSide = side;
		side = side ^ 1;
		int diff = pushDiffs[side];
		long nodes = 0;
		int i = 0;
		boolean isSimpleMove;
		boolean isDoublePush;
		boolean isEnPassantCapture;
		boolean isPromotion;
		
		int move;
		int to;
		int from;
		byte fromPiece;
		byte capturedPiece;
		byte promotedPiece = 0;

		//
		boolean isCastling;
		int castlingSide = 0;
		int castlingRookFrom = 0;
		int castlingRookTo = 0;
		byte sideToRook = 0;
		long tmpBitboard;

		byte whiteQueenSideCastlingRight = 0;
		byte whiteKingSideCastlingRight = 0;
		byte blackQueenSideCastlingRight = 0;
		byte blackKingSideCastlingRight = 0;

		boolean isRookOrKingMove;
		//

		int[] moveList = moveGeneration.generateMoves(bitboard, side, epTarget, castlingRights);
		while (moveList[i] != 0) {
			move = moveList[i];

			isSimpleMove = (move & 0x00ff0000) == 0 ? true : false;
			isDoublePush = (move & 0x00ff0000) == (EngineConstants.DOUBLE_PUSH << 16) ? true : false;
			isEnPassantCapture = (move & 0x00ff0000) == (EngineConstants.EP_CAPTURE << 16) ? true : false;
			isPromotion = (move & 0x000f0000) == (EngineConstants.PROMOTION << 16) ? true : false;

			to = (move & 0x0000ff00) >>> 8;
			from = move & 0x000000ff;
			fromPiece = pieces[from];
			capturedPiece = 0;
			
			//
			isCastling = (move & 0x00040000) == (EngineConstants.CASTLING << 16) ? true : false;
			isRookOrKingMove = (fromPiece == (side | EngineConstants.KING) || (fromPiece == (side | EngineConstants.ROOK))) 
					  || (pieces[to] == ((opSide) | EngineConstants.ROOK)) || (pieces[to] == ((opSide) | EngineConstants.KING));
			if (isRookOrKingMove) {
				whiteQueenSideCastlingRight = castlingRights[0][0];
				whiteKingSideCastlingRight = castlingRights[0][1];
				blackQueenSideCastlingRight = castlingRights[1][0];
				blackKingSideCastlingRight = castlingRights[1][1];
			}
			//

			if (isSimpleMove) {
				epTarget = 64;
				epSquare = -1;
				capturedPiece = pieces[to];
				pieces[from] = 0;
				pieces[to] = fromPiece;
				bitboard[fromPiece] &= ~(1L << from);
				bitboard[fromPiece] |= (1L << to);
				bitboard[capturedPiece] &= ~(1L << to);
			} else if (isDoublePush) {
				epTarget = Long.numberOfTrailingZeros(((1L << to) >>> diff) | ((1L << to) << (64 - diff)));
				epSquare = to;
				pieces[from] = 0;
				pieces[to] = fromPiece;
				bitboard[fromPiece] &= ~(1L << from);
				bitboard[fromPiece] |= (1L << to);
			} else if (isEnPassantCapture) {
				capturedPiece = pieces[epSquare];
				pieces[epSquare] = 0;
				pieces[from] = 0;
				pieces[to] = fromPiece;
				bitboard[fromPiece] &= ~(1L << from);
				bitboard[fromPiece] |= (1L << to);
				bitboard[capturedPiece] &= ~(1L << epSquare);
				epTarget = 64;
				epSquare = -1;
			} else if (isCastling) {
				epTarget = 64;
				epSquare = -1;
				castlingSide = (move & 0x00010000) >>> 16;
				sideToRook = (byte) (side | EngineConstants.ROOK);

				castlingRookFrom = castlingRookSources[side][castlingSide];
				castlingRookTo = castlingRookTargets[side][castlingSide];

				pieces[from] = 0;
				pieces[to] = fromPiece;
				bitboard[fromPiece] &= ~(1L << from);
				bitboard[fromPiece] |= (1L << to);

				pieces[castlingRookFrom] = 0;
				pieces[castlingRookTo] = sideToRook;
				bitboard[sideToRook] &= ~(1L << castlingRookFrom);
				bitboard[sideToRook] |= (1L << castlingRookTo);
			} else if(isPromotion){
				epTarget = 64;
				epSquare = -1;
				capturedPiece = pieces[to];
				promotedPiece = (byte)((move & 0x00f00000) >>> 20);
				
				pieces[from] = 0;
				pieces[to] = promotedPiece;
				bitboard[fromPiece] &= ~(1L << from);
				bitboard[promotedPiece] |= (1L << to);
				bitboard[capturedPiece] &= ~(1L << to);
			}

			// update castling rights
			if (isRookOrKingMove) {
				tmpBitboard = bitboard[(side) | EngineConstants.KING];
				castlingRights[side][0] = (byte) (castlingRights[side][0] & (tmpBitboard >>> kingPositions[side]));
				castlingRights[side][1] = (byte) (castlingRights[side][1] & (tmpBitboard >>> kingPositions[side]));

				tmpBitboard = bitboard[(side) | EngineConstants.ROOK];
				castlingRights[side][0] = (byte) (castlingRights[side][0] & (tmpBitboard >>> rookPositions[side][0]));
				castlingRights[side][1] = (byte) (castlingRights[side][1] & (tmpBitboard >>> rookPositions[side][1]));
				
				tmpBitboard = bitboard[(opSide) | EngineConstants.KING];
				castlingRights[opSide][0] = (byte) (castlingRights[opSide][0] & (tmpBitboard >>> kingPositions[opSide]));
				castlingRights[opSide][1] = (byte) (castlingRights[opSide][1] & (tmpBitboard >>> kingPositions[opSide]));

				tmpBitboard = bitboard[(opSide) | EngineConstants.ROOK];
				castlingRights[opSide][0] = (byte) (castlingRights[opSide][0] & (tmpBitboard >>> rookPositions[opSide][0]));
				castlingRights[opSide][1] = (byte) (castlingRights[opSide][1] & (tmpBitboard >>> rookPositions[opSide][1]));
			}
			//
			

			if (!legality.isKingInCheck(bitboard, side)) {
				if(depth == 1){
					if(capturedPiece != 0){
						PerformanceTesting.incrementCaptureCount();
					} 
					if(isEnPassantCapture){
						PerformanceTesting.incrementEpCount();
					} else if(isCastling){
						PerformanceTesting.incrementCastlingCount();
					} else if(isPromotion){
						PerformanceTesting.incrementpromotionCount();
					}
					if(legality.isKingInCheck(bitboard, opSide)){
						incrementCheckCount();
					}
				}
				
				nodes += perft(depth - 1, epTarget, epSquare, bitboard, pieces, castlingRights, side);
			}

			// update castling rights
			if (isRookOrKingMove) {
				castlingRights[0][0] = whiteQueenSideCastlingRight;
				castlingRights[0][1] = whiteKingSideCastlingRight;
				castlingRights[1][0] = blackQueenSideCastlingRight;
				castlingRights[1][1] = blackKingSideCastlingRight;
			}

			epTarget = epT;
			epSquare = epS;
			if (isSimpleMove) {
				pieces[from] = fromPiece;
				pieces[to] = capturedPiece;
				bitboard[fromPiece] |= (1L << from);
				bitboard[fromPiece] &= ~(1L << to);
				bitboard[capturedPiece] |= (1L << to);
			} else if (isDoublePush) {
				pieces[from] = fromPiece;
				pieces[to] = 0;
				bitboard[fromPiece] |= (1L << from);
				bitboard[fromPiece] &= ~(1L << to);
			} else if (isEnPassantCapture) {
				pieces[epSquare] = capturedPiece;
				pieces[from] = fromPiece;
				pieces[to] = 0;
				bitboard[fromPiece] |= (1L << from);
				bitboard[fromPiece] &= ~(1L << to);
				bitboard[capturedPiece] |= (1L << epSquare);
			} else if (isCastling) {
				pieces[from] = fromPiece;
				pieces[to] = 0;
				bitboard[fromPiece] |= (1L << from);
				bitboard[fromPiece] &= ~(1L << to);

				pieces[castlingRookFrom] = sideToRook;
				pieces[castlingRookTo] = 0;
				bitboard[sideToRook] |= (1L << castlingRookFrom);
				bitboard[sideToRook] &= ~(1L << castlingRookTo);
			} else if(isPromotion){
				pieces[from] = fromPiece;
				pieces[to] = capturedPiece;
				bitboard[fromPiece] |= (1L << from);
				bitboard[promotedPiece] &= ~(1L << to);
				bitboard[capturedPiece] |= (1L << to);
			}
			i++;
		}
		
		if(depth == 1 && nodes == 0){
			incrementCheckMateCount();
		}
		return nodes;
	}
	
	private static int aliveThreadCount = 0;
	private static int deadThreadCount = 0;
	private static long moveCount = 0;
	private static long castlingCount = 0;
	private static long epCount = 0;
	private static long captureCount = 0;
	private static long checkMateCount = 0;
	private static long promotionCount = 0;
	private static long checkCount = 0;
	private static String outputMessage;
	
	private synchronized static void incrementCastlingCount(){
		castlingCount++;
	}
	
	private synchronized static void incrementEpCount(){
		epCount++;
	}

	private synchronized static void incrementCaptureCount(){
		captureCount++;
	}
	
	private synchronized static void incrementCheckMateCount(){
		checkMateCount++;
	}
	
	private synchronized static void incrementpromotionCount(){
		promotionCount++;
	}
	
	private synchronized static void incrementCheckCount(){
		checkCount++;
	}
	
	private synchronized static void resetCounters(){
		aliveThreadCount = 0;
		deadThreadCount = 0;
		moveCount = 0;
		castlingCount = 0;
		epCount = 0;
		captureCount = 0;
		checkMateCount = 0;
		promotionCount = 0;
		checkCount = 0;
		outputMessage = "";
		ilk = System.currentTimeMillis();
	}

}

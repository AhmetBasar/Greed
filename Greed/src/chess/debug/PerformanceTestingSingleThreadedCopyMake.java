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

public class PerformanceTestingSingleThreadedCopyMake {
	private int[] pushDiffs = { 8, 64 - 8 };
	private MoveGeneration moveGeneration = new MoveGeneration();
	private LegalityV4 legality = new LegalityV4();

	private int[][] castlingRookSources = { { 0, 7 }, { 56, 63 } };
	private int[][] castlingRookTargets = { { 3, 5 }, { 59, 61 } };
	private byte[] kingPositions = { 4, 60 };
	private byte[][] rookPositions = { { 0, 7 }, { 56, 63 } };
	
	private int globalEpTarget;
	private int globalEpSquare;
	
	private static BaseGui base;
	
	
	private static long ilk = System.currentTimeMillis();
	
	public static void main(String[] args) {
		byte[][] sourceBoard = DebugUtility.getDefaultBoard();
		long[] bitboard = Transformer.getBitboardStyl(sourceBoard);
		byte[] pieces = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(sourceBoard));
		byte[][] castlingRights = { { 1, 1 }, { 1, 1 } };
		System.out.println("move count = " + new PerformanceTestingSingleThreadedCopyMake().perft(5, 64, -1, bitboard, pieces, castlingRights, 1));
		System.out.println("time = " + (System.currentTimeMillis() - ilk));
	}
	
	public static void getAllVariations(byte[][] board, int side, int depth, byte[][] castlingRights, BaseGui baseGui, int threadCount, int epTarget, int epSquare){
		base = baseGui;
		long[] bitboard = Transformer.getBitboardStyl(board);
		byte[] pieces = Transformer.getByteArrayStyl(Transformer.getBitboardStyl(board));
		ilk = System.currentTimeMillis();
		String outputMessage = "";
		outputMessage = outputMessage + "moveCount = " + new PerformanceTestingSingleThreadedCopyMake().perft(depth, epTarget, epSquare, bitboard, pieces, castlingRights, side ^ 1) + "\n";
		outputMessage = outputMessage + "----------------------------" + "\n";
		outputMessage = outputMessage + "Time Consumed = " + (System.currentTimeMillis() - ilk) + "\n";
		base.getDebugPanel().setOutputMessage(outputMessage);
	}

	public long perft(int depth, int epT, int epS, long[] bitboard, byte[] pieces, byte[][] castlingRights, int side) {
		if (depth == 0)
			return 1;
		side = side ^ 1;
		long nodes = 0;
		int move;

		int[] moveList = moveGeneration.generateMoves(bitboard, side, epT, castlingRights);
		
		for ( int i = 0 ; (move = moveList[i]) != 0 ; i++) {

			long[] tempBitboard = bitboard.clone();
			byte[][] tempCastlingRights = DebugUtility.deepCloneMultiDimensionalArray(castlingRights);
			byte[] tempPieces = pieces.clone();
			makeMove(move, tempBitboard, tempPieces, tempCastlingRights, epT, epS, side);

			if (!legality.isKingInCheck(tempBitboard, side)) {
				nodes += perft(depth - 1, globalEpTarget, globalEpSquare, tempBitboard, tempPieces, tempCastlingRights, side);
			}
		}
		
		return nodes;
	}
	
	private void makeMove(int move, long[] tempBitboard, byte[] tempPieces, byte[][] tempCastlingRights, int epT, int epS, int side){
		int opSide = side ^ 1;
		globalEpSquare = epS;
		globalEpTarget = epT;
		
		boolean isRookOrKingMove;
		
		int to;
		int from;
		byte fromPiece;
		byte capturedPiece;
		byte promotedPiece = 0;
		
		boolean isSimpleMove;
		boolean isDoublePush;
		boolean isEnPassantCapture;
		boolean isPromotion;
		
		boolean isCastling;
		int castlingSide = 0;
		int castlingRookFrom = 0;
		int castlingRookTo = 0;
		byte sideToRook = 0;
		long tmpBitboard;
		
		int diff = pushDiffs[side];
		
		isSimpleMove = (move & 0x00ff0000) == 0 ? true : false;
		isDoublePush = (move & 0x00ff0000) == (EngineConstants.DOUBLE_PUSH << 16) ? true : false;
		isEnPassantCapture = (move & 0x00ff0000) == (EngineConstants.EP_CAPTURE << 16) ? true : false;
		isPromotion = (move & 0x000f0000) == (EngineConstants.PROMOTION << 16) ? true : false;

		to = (move & 0x0000ff00) >>> 8;
		from = move & 0x000000ff;
		fromPiece = tempPieces[from];
		capturedPiece = 0;
		
		//
		isCastling = (move & 0x00040000) == (EngineConstants.CASTLING << 16) ? true : false;
		isRookOrKingMove = (fromPiece == (side | EngineConstants.KING) || (fromPiece == (side | EngineConstants.ROOK))) 
				  || (tempPieces[to] == ((opSide) | EngineConstants.ROOK)) || (tempPieces[to] == ((opSide) | EngineConstants.KING));
		
		if (isSimpleMove) {
			globalEpTarget = 64;
			globalEpSquare = -1;
			capturedPiece = tempPieces[to];
			tempPieces[from] = 0;
			tempPieces[to] = fromPiece;
			tempBitboard[fromPiece] &= ~(1L << from);
			tempBitboard[fromPiece] |= (1L << to);
			tempBitboard[capturedPiece] &= ~(1L << to);
		} else if (isDoublePush) {
			globalEpTarget = Long.numberOfTrailingZeros(((1L << to) >>> diff) | ((1L << to) << (64 - diff)));
			globalEpSquare = to;
			tempPieces[from] = 0;
			tempPieces[to] = fromPiece;
			tempBitboard[fromPiece] &= ~(1L << from);
			tempBitboard[fromPiece] |= (1L << to);
		} else if (isEnPassantCapture) {
			capturedPiece = tempPieces[globalEpSquare];
			tempPieces[globalEpSquare] = 0;
			tempPieces[from] = 0;
			tempPieces[to] = fromPiece;
			tempBitboard[fromPiece] &= ~(1L << from);
			tempBitboard[fromPiece] |= (1L << to);
			tempBitboard[capturedPiece] &= ~(1L << globalEpSquare);
			globalEpTarget = 64;
			globalEpSquare = -1;
		} else if (isCastling) {
			globalEpTarget = 64;
			globalEpSquare = -1;
			castlingSide = (move & 0x00010000) >>> 16;
			sideToRook = (byte) (side | EngineConstants.ROOK);

			castlingRookFrom = castlingRookSources[side][castlingSide];
			castlingRookTo = castlingRookTargets[side][castlingSide];

			tempPieces[from] = 0;
			tempPieces[to] = fromPiece;
			tempBitboard[fromPiece] &= ~(1L << from);
			tempBitboard[fromPiece] |= (1L << to);

			tempPieces[castlingRookFrom] = 0;
			tempPieces[castlingRookTo] = sideToRook;
			tempBitboard[sideToRook] &= ~(1L << castlingRookFrom);
			tempBitboard[sideToRook] |= (1L << castlingRookTo);
		} else if(isPromotion){
			globalEpTarget = 64;
			globalEpSquare = -1;
			capturedPiece = tempPieces[to];
			promotedPiece = (byte)((move & 0x00f00000) >>> 20);
			
			tempPieces[from] = 0;
			tempPieces[to] = promotedPiece;
			tempBitboard[fromPiece] &= ~(1L << from);
			tempBitboard[promotedPiece] |= (1L << to);
			tempBitboard[capturedPiece] &= ~(1L << to);
		}

		// update castling rights
		if (isRookOrKingMove) {
			tmpBitboard = tempBitboard[(side) | EngineConstants.KING];
			tempCastlingRights[side][0] = (byte) (tempCastlingRights[side][0] & (tmpBitboard >>> kingPositions[side]));
			tempCastlingRights[side][1] = (byte) (tempCastlingRights[side][1] & (tmpBitboard >>> kingPositions[side]));

			tmpBitboard = tempBitboard[(side) | EngineConstants.ROOK];
			tempCastlingRights[side][0] = (byte) (tempCastlingRights[side][0] & (tmpBitboard >>> rookPositions[side][0]));
			tempCastlingRights[side][1] = (byte) (tempCastlingRights[side][1] & (tmpBitboard >>> rookPositions[side][1]));
			
			tmpBitboard = tempBitboard[(opSide) | EngineConstants.KING];
			tempCastlingRights[opSide][0] = (byte) (tempCastlingRights[opSide][0] & (tmpBitboard >>> kingPositions[opSide]));
			tempCastlingRights[opSide][1] = (byte) (tempCastlingRights[opSide][1] & (tmpBitboard >>> kingPositions[opSide]));

			tmpBitboard = tempBitboard[(opSide) | EngineConstants.ROOK];
			tempCastlingRights[opSide][0] = (byte) (tempCastlingRights[opSide][0] & (tmpBitboard >>> rookPositions[opSide][0]));
			tempCastlingRights[opSide][1] = (byte) (tempCastlingRights[opSide][1] & (tmpBitboard >>> rookPositions[opSide][1]));
		}

	}
	

}

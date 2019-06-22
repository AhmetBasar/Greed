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
package chess.evaluation;

import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.Transformer;

// https://www.chessprogramming.org/Isolated_Pawns_(Bitboards)
public class IsolatedPawn {
	
	public static void main(String[] args) {
		
		testCorrectness();
		
		testPerformance();
		
	}
	
	public static void testPerformance() {
		
		for (int i = 0; i < 1000000; i++) {
			byte[][] sourceBoard = DebugUtility.generateRandomBoard();
			long[] bitboard = Transformer.getBitboardStyl(sourceBoard);
			evaluateIsolatedPawnsSetWiseFast(bitboard);
		}

		// PERFORMANCE
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			byte[][] sourceBoard = DebugUtility.generateRandomBoard();
			long[] bitboard = Transformer.getBitboardStyl(sourceBoard);
			evaluateIsolatedPawnsSetWise(bitboard);
		}
		long end = System.currentTimeMillis();
		System.out.println("fark setwise = " + (end - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			byte[][] sourceBoard = DebugUtility.generateRandomBoard();
			long[] bitboard = Transformer.getBitboardStyl(sourceBoard);
			evaluateIsolatedPawnsSquareCentric(bitboard);
		}
		end = System.currentTimeMillis();
		System.out.println("fark centric = " + (end - start));
		
		start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			byte[][] sourceBoard = DebugUtility.generateRandomBoard();
			long[] bitboard = Transformer.getBitboardStyl(sourceBoard);
			evaluateIsolatedPawnsSetWiseFast(bitboard);
		}
		end = System.currentTimeMillis();
		System.out.println("fark fast = " + (end - start));

	}
	
	public static void testCorrectness() {
		// CORRECTNESS
		for (int i = 0; i < 1000000; i++) {
			byte[][] sourceBoard = DebugUtility.generateRandomBoard();
			long[] bitboard = Transformer.getBitboardStyl(sourceBoard);	
			int evalWithSquareCentric = evaluateIsolatedPawnsSquareCentric(bitboard);
			int evalWithSetWise = evaluateIsolatedPawnsSetWise(bitboard);
			int fast = evaluateIsolatedPawnsSetWiseFast(bitboard);
			if (evalWithSquareCentric != evalWithSetWise || evalWithSetWise != fast) {
				throw new RuntimeException("Not Equal.");
			}
		}
	}
	
	private static final int PENALTY_ISOLATED_PAWN = 10;
	
	public static int evaluateIsolatedPawnsSquareCentric(long[] bitboard) {
		
		int eval = 0;
		
		long whitePawns = bitboard[EngineConstants.WHITE_PAWN];
		long blackPawns = bitboard[EngineConstants.BLACK_PAWN];
		
		int trailingZeros;
		long fromBitboard=bitboard[EngineConstants.WHITE_PAWN];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			
			if ((EngineConstants.neighborFiles[trailingZeros] & whitePawns) == 0) {
				eval -= PENALTY_ISOLATED_PAWN;
			}
			
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}
		
		fromBitboard=bitboard[EngineConstants.BLACK_PAWN];
		while((trailingZeros = Long.numberOfTrailingZeros(fromBitboard))!=64){
			
			// Isolated Pawns
			if ((EngineConstants.neighborFiles[trailingZeros] & blackPawns) == 0) {
				eval += PENALTY_ISOLATED_PAWN;
			}
			
			fromBitboard=fromBitboard & ~(1L << trailingZeros);
		}
		
		
		return eval;
		
	}
	
	public static int evaluateIsolatedPawnsSetWise(long[] bitboard) {
		int eval = 0;
		long whitePawns = bitboard[EngineConstants.WHITE_PAWN];
		long blackPawns = bitboard[EngineConstants.BLACK_PAWN];
		eval += (Long.bitCount(getIsolatedPawnCount(blackPawns)) - Long.bitCount(getIsolatedPawnCount(whitePawns))) * PENALTY_ISOLATED_PAWN;
		return eval;
	}
	
	public static long getIsolatedPawnCount(long bb) {
		return noNeighborOnEastFile(bb) & noNeighborOnWestFile(bb);
	}

	public static long noNeighborOnEastFile(long bb) {
		return bb & ~westAttackFileFill(bb);
	}

	public static long noNeighborOnWestFile(long bb) {
		return bb & ~eastAttackFileFill(bb);
	}

	public static long westAttackFileFill(long bb) {
		return westOne(fileFill(bb));
	}

	public static long eastAttackFileFill(long bb) {
		return eastOne(fileFill(bb));
	}

	public static long northFill(long bb) {
		bb |= (bb << 8);
		bb |= (bb << 16);
		bb |= (bb << 32);
		return bb;
	}

	public static long southFill(long bb) {
		bb |= (bb >>> 8);
		bb |= (bb >>> 16);
		bb |= (bb >>> 32);
		return bb;
	}

	public static long fileFill(long bb) {
		return northFill(bb) | southFill(bb);
	}

	public static long eastOne(long bb) {
		return (bb << 1) & ~EngineConstants.FILE_A;
	}

	public static long westOne(long bb) {
		return (bb >>> 1) & ~EngineConstants.FILE_H;
	}
	
	public static int evaluateIsolatedPawnsSetWiseFast(long[] bitboard) {
		int eval = 0;
		long whitePawns = bitboard[EngineConstants.WHITE_PAWN];
		long blackPawns = bitboard[EngineConstants.BLACK_PAWN];
		eval += (Long.bitCount(getIsolatedPawnCountFast(blackPawns)) - Long.bitCount(getIsolatedPawnCountFast(whitePawns))) * PENALTY_ISOLATED_PAWN;
		return eval;
	}
	
	public static long getIsolatedPawnCountFast(long bb) {
		long bbOrig = bb;

		// file fill
		bb |= (bb << 8);
		bb |= (bb << 16);
		bb |= (bb << 32);
		bb |= (bb >>> 8);
		bb |= (bb >>> 16);
		bb |= (bb >>> 32);
		
		long fileFill = bb;
		
		// westOne
		bb = (bb >>> 1) & ~EngineConstants.FILE_H;
		
		// noNeighborOnEastFile
		bb = bbOrig & ~bb;
		
		// eastOne
		fileFill = (fileFill << 1) & ~EngineConstants.FILE_A;
		
		// noNeighborOnWestFile
		fileFill = bbOrig & ~fileFill;
		
		// isolated pawn count
		return bb & fileFill;
	}
	
	
}

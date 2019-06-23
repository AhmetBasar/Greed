package chess.evaluation;

import chess.engine.EngineConstants;

/**
 * https://www.chessprogramming.org/Pawn_Spans
 * https://www.chessprogramming.org/General_Setwise_Operations
 **/
public class BitboardUtility {
	
	public static long wFrontSpans(long wp) {
		return northOne(northFill(wp));
	}

	public static long bFrontSpans(long bp) {
		return southOne(southFill(bp));
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

	public static long northOne(long bb) {
		return (bb << 8);
	}

	public static long southOne(long bb) {
		return (bb >>> 8);
	}

	public static long eastOne(long bb) {
		return (bb << 1) & ~EngineConstants.FILE_A;
	}

	public static long westOne(long bb) {
		return (bb >>> 1) & ~EngineConstants.FILE_H;
	}
	
	public static long fileFill(long bb) {
		return northFill(bb) | southFill(bb);
	}

}

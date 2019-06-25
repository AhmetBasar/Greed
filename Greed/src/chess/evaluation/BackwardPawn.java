package chess.evaluation;

/**
 * https://www.chessprogramming.org/Backward_Pawns_(Bitboards)
 **/
public class BackwardPawn {

	public static long whiteBackwardPawns(long wp, long bp) {
		long stops = wp << 8;
		long wAttackSpans = BitboardUtility.wEastAttackFrontSpans(wp)
				| BitboardUtility.wWestAttackFrontSpans(wp);
		long bAttacks = BitboardUtility.bPawnEastAttacks(bp) | BitboardUtility.bPawnWestAttacks(bp);
		return (stops & bAttacks & ~wAttackSpans) >> 8;
	}

	public static long blackBackwardPawns(long bp, long wp) {
		long stops = bp >>> 8;
		long bAttackSpans = BitboardUtility.bEastAttackFrontSpans(bp)
				| BitboardUtility.bWestAttackFrontSpans(bp);
		long wAttacks = BitboardUtility.wPawnEastAttacks(wp) | BitboardUtility.wPawnWestAttacks(wp);
		return (stops & wAttacks & ~bAttackSpans) << 8;
	}

}
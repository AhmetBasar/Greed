package chess.engine.test;

import chess.debug.DebugUtility;
import chess.engine.EngineConstants;
import chess.engine.Transformer;
import chess.evaluation.BackwardPawn;
import chess.evaluation.BitboardUtility;

public class BackwardPawnTest {
	
	public static void main(String[] args) {
		byte [][] EMPTY_BOARD2={
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     3,     0,     0,     0,     3,     0},
				   {     0,     0,     2,     0,     0,     0,     2,     0},
				   {     2,     2,     0,     0,     0,     2,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     0,     0,     0,     0,     0,     0,     0}
		};
		
		byte[][] board2 = new byte[8][8];
		for(int x=0;x<=7;x++){
			for(int y=0;y<=7;y++){
				board2[x][y]=EMPTY_BOARD2[y][x];
			}
		}
		
		long[] emptyBb1 = Transformer.getBitboardStyl(board2);
		
		long wp = emptyBb1[EngineConstants.WHITE_PAWN];
		long bp = emptyBb1[EngineConstants.BLACK_PAWN];
		System.out.println("wp = " + wp);
		System.out.println("bp = " + bp);
		
		
		long[] bbb = DebugUtility.convertToBitboard(BackwardPawn.whiteBackwardPawns(wp, bp), EngineConstants.WHITE_PAWN);
		System.out.println("wbp = " + bbb[EngineConstants.WHITE_PAWN]);
		DebugUtility.throwBoard(bbb);
		
		
		testAll();
	}
	
	public static void testAll() {
		testWhiteBackwardPawns();
	}
	
	public static void testWhiteBackwardPawns() {
		if (BackwardPawn.whiteBackwardPawns(68853694464L, 17592186044416L) != 134217728L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.whiteBackwardPawns(292628201472L, 74766790688768L) != 570425344L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.whiteBackwardPawns(292695310336L, 74766790688768L) != 536870912L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.whiteBackwardPawns(292628463616L, 74766790688768L) != 536870912L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.whiteBackwardPawns(292628202496L, 74766790688768L) != 536870912L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.whiteBackwardPawns(292628332544L, 74766790688768L) != 570425344L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.whiteBackwardPawns(292628201984L, 74766790688768L) != 570425344L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.whiteBackwardPawns(301218136064L, 74766790688768L) != 570425344L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.whiteBackwardPawns(292628201728L, 74766790688768L) != 536870912L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.whiteBackwardPawns(292628201472L, 74766790689024L) != 570425344L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.whiteBackwardPawns(296923168768L, 74766790688768L) != 570425344L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.whiteBackwardPawns(292644978688L, 74766790688768L) != 536870912L) {
			throw new RuntimeException("Failed.");
		}
	}

}

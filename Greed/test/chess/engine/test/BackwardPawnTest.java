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
				   {     0,     0,     0,     0,     0,     0,     0,     0},
				   {     0,     3,     0,     3,     0,     0,     3,     0},
				   {     0,     0,     3,     0,     0,     3,     0,     0},
				   {     0,     0,     2,     0,     0,     2,     0,     0},
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
		System.out.println("bp = " + bp);
		System.out.println("wp = " + wp);
		
		
		long[] bbb = DebugUtility.convertToBitboard(BackwardPawn.blackBackwardPawns(bp, wp), EngineConstants.BLACK_PAWN);
		System.out.println("wbp = " + bbb[EngineConstants.BLACK_PAWN]);
		DebugUtility.throwBoard(bbb);
		
		
		testAll();
	}
	
	public static void testAll() {
		testWhiteBackwardPawns();
		
		testBlackBackwardPawns();
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
	
	public static void testBlackBackwardPawns() {
		if (BackwardPawn.blackBackwardPawns(284071821312L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(281759048531968L, 2359296L) != 274877906944L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(1383583449088L, 2359296L) != 274877906944L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(288366788608L, 2359296L) != 274877906944L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(284088598528L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(284071886848L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(284071821568L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(563234025242624L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(2483095076864L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(284105375744L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(284071952384L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(284071821824L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(1126183978663936L, 2359296L) != 274877906944L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(4682118332416L, 2359296L) != 274877906944L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(301251690496L, 2359296L) != 274877906944L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(284071822336L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}
		
		if (BackwardPawn.blackBackwardPawns(318431559680L, 2359296L) != 317827579904L) {
			throw new RuntimeException("Failed.");
		}
		
	}

}

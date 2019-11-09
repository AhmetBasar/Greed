package chess.engine;

public interface MoveGenerationConstants {

	int[] pushDiffs = { 8, 64 - 8 };
	long[] promotionMask = { EngineConstants.ROW_8, EngineConstants.ROW_1 };
	long[] doublePushMask = { EngineConstants.ROW_3, EngineConstants.ROW_6 };
	long[] fileMask = { ~EngineConstants.FILE_H, ~EngineConstants.FILE_A };
	int[][] attackDiffs = { { 7, 64 - 9 }, { 9, 64 - 7 } };
	int[][][] castlingShift = { { {1, 2, 3} , {5, 6} } , { {57, 58, 59} , {61, 62} } };
	int[][] castlingTarget = {{2, 6}, {58, 62}};
	int[][] betweenKingAndRook = {{3, 5}, {59, 61}};
	byte[] kingPositions = { 4, 60 };
	
}

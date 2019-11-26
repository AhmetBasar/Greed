package chess.engine;

public interface IBoard {
	
	int getEpTarget();
	
	int[] getMoveList();
	
	void doNullMove(int side);
	
	void undoNullMove();
	
	void doMove(int move, int side, int opSide);
	
	void undoMove(int move, int side, int opSide);
	
	void doMoveWithoutZobrist(int move, int side, int opSide);
	
	void undoMoveWithoutZobrist(int move, int side, int opSide);
	
	byte[][] getCastlingRights();
	
	byte getCapturedPiece();

	long[] getBitboard();

	byte[] getPieces();
	
	long getZobristKey();
	
	long getPawnZobristKey();
	
	int getFiftyMoveCounter();
	
	int getNullMoveCounter();
	
	boolean hasRepeated(long zobristKey);

}

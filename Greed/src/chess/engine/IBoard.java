package chess.engine;

public interface IBoard {
	
	int getEpTarget();
	
	int[] getMoveList();
	
	void doNullMove();
	
	void undoNullMove();
	
	void doMove(int move);
	
	void undoMove(int move);
	
	void doMoveWithoutZobrist(int move);
	
	void undoMoveWithoutZobrist(int move);
	
	byte[][] getCastlingRights();
	
	byte getCapturedPiece();

	long[] getBitboard();

	byte[] getPieces();
	
	long getZobristKey();
	
	long getPawnZobristKey();
	
	int getFiftyMoveCounter();
	
	int getNullMoveCounter();
	
	boolean hasRepeated(long zobristKey);
	
	int getSide();
	
	int getOpSide();
	
	long getOccupiedSquares();

	long getEmptySquares();

	long[] getOccupiedSquaresBySide();
	
	int[] getKingSquares();
	
	long getCheckers();
	
	public long getPinnedPieces();

	public long getDiscoveredPieces();

}

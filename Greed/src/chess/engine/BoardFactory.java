package chess.engine;

import chess.debug.BoardV5;
import chess.debug.IBoard;

public class BoardFactory {
	
	public static IBoard getInstance(long[] bitboard, byte[] pieces, int epT, int epS, int depth, byte[][] castlingRights, long zobristKey, int fiftyMoveCounter) {
		return new BoardV5(bitboard, pieces, epT, epS, depth, castlingRights, zobristKey, fiftyMoveCounter);
	}

}

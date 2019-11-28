package chess.engine;

import java.util.List;

public class BoardFactory {
	
	public static IBoard getInstance(SearchParameters sp) {
		return new BoardV7(sp.getBitboard(), sp.getPieces(), sp.getEpT(),
				sp.getCastlingRights(), sp.getUiZobristKey(), sp.getFiftyMoveCounter(), sp.getUiPawnZobristKey(),
				sp.getZobristKeyHistory(), sp.getSide());
	}
	
	public static IBoard getInstance(long[] bitboard, byte[] pieces, int epT, byte[][] castlingRights,
			long zobristKey, int fiftyMoveCounter, long pawnZobristKey, List<Long> zobristKeyHistory, int side) {
		return new BoardV7(bitboard, pieces, epT, castlingRights, zobristKey, fiftyMoveCounter, pawnZobristKey, zobristKeyHistory, side);
	}
}

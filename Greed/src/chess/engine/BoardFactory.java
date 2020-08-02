package chess.engine;

import java.util.List;

public class BoardFactory {
	
	public static IBoard getInstance2(long[] bitboard, byte[] pieces, int epT, byte[][] castlingRights,
			int fiftyMoveCounter, List<Long> zobristKeyHistory, int side) {
		return new BoardV7(bitboard, pieces, epT, castlingRights, fiftyMoveCounter, zobristKeyHistory, side);
	}
	
	public static IBoard getInstance2(SearchParameters sp) {
		return new BoardV7(sp.getBitboard(), sp.getPieces(), sp.getEpT(),
				sp.getCastlingRights(), sp.getFiftyMoveCounter(),
				sp.getZobristKeyHistory(), sp.getSide());
	}
}

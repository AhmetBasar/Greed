/**********************************************
 * Greed, a chess engine written in java.
 * Copyright (C) 2019 Ahmet Baþar
 * 
 * This file is part of Greed.
 * 
 * Greed is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Greed is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Greed.  If not, see <https://www.gnu.org/licenses/>.
 **********************************************/
package chess.engine;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import chess.movegen.MoveGeneration;
import chess.util.Utility;

public class OpeningBook {
	
	private MoveGeneration moveGeneration = new MoveGeneration(false);
	private static OpeningBook[] newInstances = new OpeningBook[16];
	
	private static final SecureRandom rgn = new SecureRandom();

	private OpeningBook() {
	}

	public static synchronized OpeningBook getNewInstance() {
		for (int i = 0; i < newInstances.length; i++) {
			if (newInstances[i] == null) {
				newInstances[i] = new OpeningBook();
				return newInstances[i];
			}
		}
		System.out.println("Max instance count exceeded.");
		System.exit(-1);
		throw new RuntimeException();
	}

	public int findBookMove(IBoard board, int depth, int side, int opSide, String bookName) {
		//TODO Rewrite this method with binary search algorithm.
		List<Integer> moveList = new ArrayList<Integer>();
		List<Short> scoreList = new ArrayList<Short>();
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		long bookKey;
		short rawMove;
		short score;
		long sumOfScores = 0L;

		try {
			fis = new FileInputStream(new File(System.getProperty("user.dir") + File.separator + bookName));
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			// TODO Change it. Use already calculated one.
			// long zobristKey = board.getZobristKey(depth);
			long zobristKey = ZobristHashingPolyGlot.getZobristKey(board.getBitboard(), board.getEpTarget(),
					board.getCastlingRights(), side);
			
			if (zobristKey != board.getZobristKey()) {
				throw new RuntimeException("ZOBRIST KEY DIFFERENCE");
			}

			for (;;) {
				bookKey = dis.readLong();
				if (bookKey != zobristKey) {
					dis.skipBytes(8);
				} else {
					rawMove = dis.readShort();
					score = dis.readShort();
					dis.skipBytes(4);

					int encodedMove = encodeMove(board, depth, side, opSide, rawMove);
					if (encodedMove == 0) {
						continue;
					}

					sumOfScores += score;
					moveList.add(encodedMove);
					scoreList.add(score);
				}
			}
		} catch (EOFException e) {
			// Just be quiet.
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			Utility.closeQuietly(fis, bis, dis);
		}
		
		double random = rgn.nextDouble() * sumOfScores;
		for (int k = 0; k < moveList.size(); k++) {
			random = random - scoreList.get(k);
			if (random <= 0d) {
				return moveList.get(k);
			}
		}
		return 0;
	}

	private int encodeMove(IBoard board, int depth, int side, int opSide, int rawMove) {
		int toFile = rawMove & 0b111;
		int toRow = rawMove >>> 3 & 0b111;
		int fromFile = rawMove >>> 6 & 0b111;
		int fromRow = rawMove >>> 9 & 0b111;
		int from = 8 * fromRow + fromFile;
		int to = 8 * toRow + toFile;

		return getValidMove(from, to, board);
	}

	public int getValidMove(int source, int target, IBoard board) {
		int move = source | (target << 8);
		moveGeneration.startPly();
		moveGeneration.generateAttacks(board);
		moveGeneration.generateMoves(board);
		while (moveGeneration.hasNext()) {
			int nextMove = moveGeneration.next();
			
			if (move == (nextMove & 0x0000FFFF) && board.isLegal(nextMove)) {
				moveGeneration.endPly();
				return nextMove;
			}
		}
		moveGeneration.endPly();
		return 0;
	}
}
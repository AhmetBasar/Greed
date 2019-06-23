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
package chess.evaluation;

import chess.engine.EngineConstants;

/**
 * https://www.chessprogramming.org/Pawn_Spans
 * https://www.chessprogramming.org/General_Setwise_Operations
 **/
public class BitboardUtility {
	
	public static long wFrontSpans(long wp) {
		return northOne(northFill(wp));
	}

	public static long bFrontSpans(long bp) {
		return southOne(southFill(bp));
	}

	public static long northFill(long bb) {
		bb |= (bb << 8);
		bb |= (bb << 16);
		bb |= (bb << 32);
		return bb;
	}

	public static long southFill(long bb) {
		bb |= (bb >>> 8);
		bb |= (bb >>> 16);
		bb |= (bb >>> 32);
		return bb;
	}

	public static long northOne(long bb) {
		return (bb << 8);
	}

	public static long southOne(long bb) {
		return (bb >>> 8);
	}

	public static long eastOne(long bb) {
		return (bb << 1) & ~EngineConstants.FILE_A;
	}

	public static long westOne(long bb) {
		return (bb >>> 1) & ~EngineConstants.FILE_H;
	}
	
	public static long fileFill(long bb) {
		return northFill(bb) | southFill(bb);
	}

}

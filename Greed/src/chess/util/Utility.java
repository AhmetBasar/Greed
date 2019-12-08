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
package chess.util;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import chess.engine.EngineConstants;

public class Utility {

	public static final long[] SINGLE_BIT = new long[64];
	public static final long[][] LINE = new long[64][64];
	
	static {
		 onClassLoad();
	}
	
	private static void onClassLoad() {
		for (int i = 0; i < 64; i++) {
			SINGLE_BIT[i] = 1L << i;
		}
		
		for (int from = 0; from < 64; from++) {
			for (int to = 0; to < 64; to++) {
				if (from == to) {
					continue;
				}
				int fromRank = getRank(from);
				int toRank = getRank(to);
				int fromFile = getFile(from);
				int toFile = getFile(to);
				long toDiag  = EngineConstants.DIAG_MASK[to];
				long fromDiag = EngineConstants.DIAG_MASK[from];
				long toADiag  = EngineConstants.ADIAG_MASK[to];
				long fromADiag = EngineConstants.ADIAG_MASK[from];
				if (fromRank == toRank) {
					for (int k = Math.min(from, to) + 1; k < Math.max(from, to); k++) {
						LINE[from][to] |= SINGLE_BIT[k];
					}
				} else if (fromFile == toFile) {
					for (int k = Math.min(from, to) + 8; k < Math.max(from, to); k += 8) {
						LINE[from][to] |= SINGLE_BIT[k];
					}
				} else if (fromDiag == toDiag) {
					for (int k = Math.min(from, to) + 9; k < Math.max(from, to); k += 9) {
						LINE[from][to] |= SINGLE_BIT[k];
					}
				} else if (fromADiag == toADiag) {
					for (int k = Math.min(from, to) + 7; k < Math.max(from, to); k += 7) {
						LINE[from][to] |= SINGLE_BIT[k];
					}
				}
			}
		}
	}
	
	public static String toFormattedHexString(long val) {
		return "0x" + ("0000000000000000" + Long.toHexString(val)).substring(Long.toHexString(val).length());
	}
	
	public static int getCountOfOccurrence(String str, String occr) {
		return (str.length() - str.replace(occr, "").length()) / occr.length();
	}
	
	public static int getFile(int square) {
		return square & 7;
	}

	public static int getRank(int square) {
		return getRank(square, false);
	}
	
	public static int getRank(int square, boolean isZeroBased) {
		return isZeroBased ? square >>> 3 : (square >>> 3) + 1;
	}
	
	public static String readFile(String filePath) {
		String content = "";
		try {
			content = new String(Files.readAllBytes(Paths.get(filePath)), Charset.forName("Cp1254"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public static List<List<String>> splitList(List<String> list, final int maxLength) {
	    List<List<String>> parts = new ArrayList<List<String>>();
	    final int listSize = list.size();
	    for (int i = 0; i < listSize; i += maxLength) {
	        parts.add(new ArrayList<String>(list.subList(i, Math.min(listSize, i + maxLength))));
	    }
	    return parts;
	}
	
	public static void closeQuietly(Closeable... closeables) {
		for (Closeable closeable : closeables) {
			try {
				if (closeable != null) {
					closeable.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String leftSpacePad(String str, int spaceCount) {
		return String.format("%" + spaceCount + "s", str);
	}
	
	public static boolean isSet(long bb, int index){
		return ((bb >>> index) & 1L) == 1L;
	}
}
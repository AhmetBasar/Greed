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

public class Utility {

	public static final long[] SINGLE_BIT = new long[64];
	
	static {
		 onClassLoad();
	}
	
	private static void onClassLoad() {
		for (int i = 0; i < 64; i++) {
			SINGLE_BIT[i] = 1L << i;
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
		return isZeroBased ? square >> 3 : (square >> 3) + 1;
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
	
}
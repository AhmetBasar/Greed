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
package chess.engine.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chess.util.Utility;

public class UtilityTest {
	
	public static void main(String[] args) {
		testAll();
	}
	
	public static void testAll() {
		
		testGetCountOfOccurrence();
		
		testGetFile();
		
		testGetRank();
		
		testSplitList();
		
	}
	
	public static void testGetCountOfOccurrence() {
		String testString = "Very Very Strong Chess Engine.";
		if (Utility.getCountOfOccurrence(testString, "er") != 2) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getCountOfOccurrence(testString, "Very") != 2) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getCountOfOccurrence(testString, "Very V") != 1) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getCountOfOccurrence(testString, "E") != 1) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getCountOfOccurrence(testString, "e") != 4) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getCountOfOccurrence(testString, ".") != 1) {
			throw new RuntimeException("Failed.");
		}
	}
	
	public static void testGetFile() {
		if (Utility.getFile(63) != 7) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getFile(62) == 7) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getFile(56) != 0) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getFile(57) == 0) {
			throw new RuntimeException("Failed.");
		}
	}
	
	public static void testGetRank() {
		if (Utility.getRank(63) != 8) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getRank(62) != 8) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getRank(56) != 8) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getRank(55) == 8) {
			throw new RuntimeException("Failed.");
		}
		
		if (Utility.getRank(63, true) != 7) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getRank(62, true) != 7) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getRank(56, true) != 7) {
			throw new RuntimeException("Failed.");
		}
		if (Utility.getRank(55, true) == 7) {
			throw new RuntimeException("Failed.");
		}
	}
	
	public static void testSplitList() {
		List<String> mainList = new ArrayList<String>();
		mainList.add("a");
		mainList.add("b");
		mainList.add("b");
		mainList.add("c");
		mainList.add("c");
		mainList.add("c");
		mainList.add("d");
		mainList.add("d");
		mainList.add("d");
		mainList.add("d");
		
		List<List<String>> chopped = Utility.splitList(mainList, 3);
		if (!chopped.get(0).equals(Arrays.asList(new String[]{"a", "b", "b"}))) {
			throw new RuntimeException("Failed.");
		}
		if (!chopped.get(1).equals(Arrays.asList(new String[]{"c", "c", "c"}))) {
			throw new RuntimeException("Failed.");
		}
		if (!chopped.get(2).equals(Arrays.asList(new String[]{"d", "d", "d"}))) {
			throw new RuntimeException("Failed.");
		}
		if (!chopped.get(3).equals(Arrays.asList(new String[]{"d"}))) {
			throw new RuntimeException("Failed.");
		}
	}

}

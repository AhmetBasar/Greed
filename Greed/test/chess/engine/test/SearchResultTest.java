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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import chess.engine.SearchResult;
import chess.util.Defaults;

public class SearchResultTest {

	public static void main(String[] args) {
		testAll();
	}

	public static void testAll() {
		testResetIncerementSet();
		testEquals();
	}

	public static void testResetIncerementSet() {
		
		SearchResult searchResult = new SearchResult();
		
		Set<String> nonIncrementalFieldNames = new HashSet<String>();
		nonIncrementalFieldNames.add("possibleMoves");
		nonIncrementalFieldNames.add("bestMove");
		nonIncrementalFieldNames.add("preMove");
		nonIncrementalFieldNames.add("isBookMove");
		nonIncrementalFieldNames.add("timeConsumed");
		
		Field[] allFields = SearchResult.class.getDeclaredFields();
		for (Field field : allFields) {
			if (!nonIncrementalFieldNames.contains(field.getName())) {
				field.setAccessible(true);
				try {
					Object value = field.get(searchResult);
					if (!value.equals(Defaults.getDefaultValue(field.getType()))) {
						throw new RuntimeException("Failed.");			
					}
					
					Method method = searchResult.getClass().getMethod("increment" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
					method.invoke(searchResult);
					
					value = field.get(searchResult);
					if (value.equals(Defaults.getDefaultValue(field.getType()))) {
						throw new RuntimeException("Failed.");			
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Failed.");
				}
			}
		}
		
		if (searchResult.getBestMove() != 0) {
			throw new RuntimeException("Failed.");
		}
		searchResult.setBestMove(1);
		if (searchResult.getBestMove() == 0) {
			throw new RuntimeException("Failed.");
		}
		
		if (!searchResult.getPossibleMoves().isEmpty()) {
			throw new RuntimeException("Failed.");
		}
		searchResult.getPossibleMoves().put(1, 1);
		if (searchResult.getPossibleMoves().isEmpty()) {
			throw new RuntimeException("Failed.");
		}
		
		if (searchResult.getPreMove() != 0) {
			throw new RuntimeException("Failed.");
		}
		searchResult.setPreMove(1);
		if (searchResult.getPreMove() == 0) {
			throw new RuntimeException("Failed.");
		}
		
		if (searchResult.isBookMove()) {
			throw new RuntimeException("Failed.");
		}
		searchResult.setBookMove(true);
		if (!searchResult.isBookMove()) {
			throw new RuntimeException("Failed.");
		}
		
		if (searchResult.getTimeConsumed() != 0) {
			throw new RuntimeException("Failed.");
		}
		searchResult.setTimeConsumed(1);
		if (searchResult.getTimeConsumed() == 0) {
			throw new RuntimeException("Failed.");
		}
		
		searchResult.reset();
		
		for (Field field : allFields) {
			if (!nonIncrementalFieldNames.contains(field.getName())) {
				try {
					Object value = field.get(searchResult);
					if (!value.equals(Defaults.getDefaultValue(field.getType()))) {
						throw new RuntimeException("Failed.");			
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Failed.");
				}
			}
		}
		
		if (searchResult.getBestMove() != 0) {
			throw new RuntimeException("Failed.");
		}
		
		if (!searchResult.getPossibleMoves().isEmpty()) {
			throw new RuntimeException("Failed.");
		}
		
		if (searchResult.getPreMove() != 0) {
			throw new RuntimeException("Failed.");
		}
		
		if (searchResult.isBookMove()) {
			throw new RuntimeException("Failed.");
		}
		
		if (searchResult.getTimeConsumed() != 0) {
			throw new RuntimeException("Failed.");
		}
	}
	
	public static void testEquals() {
		SearchResult searchResult = new SearchResult();
		SearchResult searchResult2 = new SearchResult();
		
		if (!searchResult.equals(searchResult2)) {
			throw new RuntimeException("Failed.");
		}
		
		Set<String> nonIncrementalFieldNames = new HashSet<String>();
		nonIncrementalFieldNames.add("possibleMoves");
		nonIncrementalFieldNames.add("bestMove");
		nonIncrementalFieldNames.add("preMove");
		nonIncrementalFieldNames.add("isBookMove");
		nonIncrementalFieldNames.add("timeConsumed");
		
		Field[] allFields = SearchResult.class.getDeclaredFields();
		for (Field field : allFields) {
			if (!nonIncrementalFieldNames.contains(field.getName())) {
				field.setAccessible(true);
				try {
					Method method = searchResult.getClass().getMethod("increment" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
					method.invoke(searchResult);
					
					if (searchResult.equals(searchResult2)) {
						throw new RuntimeException("Failed.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Failed.");
				}
			}
		}
		
		searchResult.setBestMove(1);
		if (searchResult.equals(searchResult2)) {
			throw new RuntimeException("Failed.");
		}
		
		searchResult.getPossibleMoves().put(1, 1);
		if (searchResult.equals(searchResult2)) {
			throw new RuntimeException("Failed.");
		}
		
		searchResult.setPreMove(1);
		if (searchResult.equals(searchResult2)) {
			throw new RuntimeException("Failed.");
		}
		
		searchResult.setBookMove(true);
		if (searchResult.equals(searchResult2)) {
			throw new RuntimeException("Failed.");
		}
		
		searchResult.setTimeConsumed(1);
		if (searchResult.equals(searchResult2)) {
			throw new RuntimeException("Failed.");
		}
		
		for (Field field : allFields) {
			if (!nonIncrementalFieldNames.contains(field.getName())) {
				field.setAccessible(true);
				try {
					Method method = searchResult2.getClass().getMethod("increment" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
					method.invoke(searchResult2);
					
					if (searchResult.equals(searchResult2)) {
						throw new RuntimeException("Failed.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Failed.");
				}
			}
		}
		
		searchResult2.setBestMove(1);
		if (searchResult.equals(searchResult2)) {
			throw new RuntimeException("Failed.");
		}
		
		searchResult2.getPossibleMoves().put(1, 1);
		if (searchResult.equals(searchResult2)) {
			throw new RuntimeException("Failed.");
		}
		
		searchResult2.setPreMove(1);
		if (searchResult.equals(searchResult2)) {
			throw new RuntimeException("Failed.");
		}
		
		searchResult2.setBookMove(true);
		if (!searchResult.equals(searchResult2)) {
			throw new RuntimeException("Failed.");
		}
		
	}
	
}

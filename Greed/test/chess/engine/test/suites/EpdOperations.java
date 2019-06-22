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
package chess.engine.test.suites;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import chess.gui.FenOperations;

public class EpdOperations {
	
	public static final String OPCODE_BEST_MOVES = "bm";
	public static final String OPCODE_AVOID_MOVES = "am";
	
	FenOperations fenOperations;
	Map<String, List<String>> operations = new LinkedHashMap<String, List<String>>();
	
	public EpdOperations() {
	}
	
	public void setEpdString(String epd) {
		String fenStr = epd.substring(0, epd.contains(OPCODE_BEST_MOVES) ? epd.indexOf(OPCODE_BEST_MOVES) : epd.indexOf(OPCODE_AVOID_MOVES)).trim();
		String strOperations = epd.substring(fenStr.length());
//		System.out.println("");
		String[] arrOperations = strOperations.split(";");
		for (String operation : arrOperations) {
			operation = operation.trim();
			String[] operationParts = operation.split(" ");
			String opcode = operationParts[0].trim();
			String operands = operation.substring(opcode.length() + 1);
			operations.put(opcode, Arrays.asList(operands.split(" ")));
		}
		fenOperations = new FenOperations();
		fenOperations.setFenString(fenStr);
	}

	public FenOperations getFenOperations() {
		return fenOperations;
	}

	public Map<String, List<String>> getOperations() {
		return operations;
	}
}

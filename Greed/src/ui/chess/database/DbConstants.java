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
package chess.database;

public interface DbConstants {
	
	public interface Keys{
		String CONTRAST = "CONTRAST";
		String XLOCATION = "XLOCATION";
		String YLOCATION = "YLOCATION";
		String DEPTH = "DEPTH";
		String ENGINE_CLASS_1 = "ENGINE_CLASS_1";
		String ENGINE_CLASS_2 = "ENGINE_CLASS_2";
		String ENGINE_CLASS_3 = "ENGINE_CLASS_3";
		
		String TESTING_ENGINE_PACKAGE = "TESTING_ENGINE_PACKAGE";
		String TESTING_ENGINE_CLASS_1 = "TESTING_ENGINE_CLASS_1";
		String TESTING_ENGINE_CLASS_2 = "TESTING_ENGINE_CLASS_2";
		String TESTING_ENGINE_DEPTH = "TESTING_ENGINE_DEPTH";
	}
}

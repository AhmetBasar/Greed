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

public class Defaults {
	
	private static boolean BOOLEAN;
	private static byte BYTE;
	private static short SHORT;
	private static int INT;
	private static long LONG;
	private static float FLOAT;
	private static double DOUBLE;

	public static Object getDefaultValue(Class<?> klass) {
		if (klass.equals(boolean.class)) {
			return BOOLEAN;
		} else if (klass.equals(byte.class)) {
			return BYTE;
		} else if (klass.equals(short.class)) {
			return SHORT;
		} else if (klass.equals(int.class)) {
			return INT;
		} else if (klass.equals(long.class)) {
			return LONG;
		} else if (klass.equals(float.class)) {
			return FLOAT;
		} else if (klass.equals(double.class)) {
			return DOUBLE;
		} else {
			throw new RuntimeException("Invalid klass.");
		}
	}
}
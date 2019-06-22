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

import java.util.HashMap;

public class DatabaseManager {
	public DatabaseManager() {
	}

	public void insert(String hKey, String value) {
	}

	public void delete(String hKey) {
	}

	public void save(String hKey, String value) {
		if (getPreference(hKey) != null) {
			update(hKey, value);
		} else {
			insert(hKey, value);
		}
	}

	public void update(String hKey, String value) {
	}

	private String getPreference(String hKey) {
		return "";
	}

	public HashMap<String, String> retrievePreferences() {
		return new HashMap<String, String>();
	}
}

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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import chess.util.Utility;

public class Storage {

	private final Properties properties = new Properties();

	public Storage() {
		InputStream is = null;
		try {
			is = new FileInputStream("D:\\storage\\Storage.properties");
			properties.load(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Utility.closeQuietly(is);
		}
	}

	private void store() {
		FileOutputStream fis = null;
		try {
			fis = new FileOutputStream("D:\\storage\\Storage.properties");
			properties.store(fis, null);
			fis.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Utility.closeQuietly(fis);
		}
	}

	public void delete(String key) {
		properties.remove(key);
		store();
	}

	public void save(String key, String value) {
		properties.setProperty(key, value);
		store();
	}

	public HashMap<String, String> retrievePreferences() {
		HashMap<String, String> ret = new HashMap<String, String>();
		for (String key : properties.stringPropertyNames()) {
			String value = properties.getProperty(key);
			ret.put(key, value);
		}
		return ret;
	}
}

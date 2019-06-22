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

import java.io.File;
import java.lang.reflect.Method;

public class TestingFramework {
	private static final String TEST_CLASS_SUFFIX = "Test.java";
	private static final String TEST_FOLDER_NAME = "test";
	public static final boolean QUICK_TEST = true;

	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		String packageName = TestingFramework.class.getPackage().getName();
		final File testPackage = new File(System.getProperty("user.dir") + File.separator + TEST_FOLDER_NAME + File.separator + packageName.replace(".", "/"));
		for (File f : testPackage.listFiles()) {
			if (f.isFile() && f.getAbsolutePath().endsWith(TEST_CLASS_SUFFIX)) {
				String className = f.getName().substring(0, f.getName().lastIndexOf("."));
				Class<?> klass = Class.forName(packageName + "." + className);
				Method method = klass.getDeclaredMethod("testAll");
				method.invoke(null);
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Fark = " + (endTime - startTime));
	}
}

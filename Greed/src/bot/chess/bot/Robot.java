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
package chess.bot;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class Robot {
	
	private static java.awt.Robot robot;

	static {
		try {
			robot = new java.awt.Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public static void getInstance() {
		
	}

	public synchronized static BufferedImage takeScreenShot() {
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
		return screenFullImage;
	}

	public synchronized static void moveMouseCursor(Point to) {
		Point from = MouseInfo.getPointerInfo().getLocation();
		mouseGlide(from.x, from.y, to.x, to.y, 1, Utility.generateStrongRandomNumber(400, 600));
	}
	
	public synchronized static void moveMouseCursoUltraFast(Point to) {
		Point from = MouseInfo.getPointerInfo().getLocation();
		mouseGlide(from.x, from.y, to.x, to.y, 1, 200);
	}
	
	public synchronized static void mouseGlide(int x1, int y1, int x2, int y2, int t, int n) {
		double dx = (x2 - x1) / ((double) n);
		double dy = (y2 - y1) / ((double) n);
		double dt = t / ((double) n);
		for (int step = 1; step <= n; step++) {
			Utility.sleep((int) dt);
			robot.mouseMove((int) (x1 + dx * step), (int) (y1 + dy * step));
		}
	}
	
	public synchronized static void mouseMove(int x, int y) {
		robot.mouseMove(x, y);
	}
	
	public synchronized static void mousePress(int buttons) {
		robot.mousePress(buttons);
	}
	
	public synchronized static void mouseRelease(int buttons) {
		robot.mouseRelease(buttons);
	}
	
}

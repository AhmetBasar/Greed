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
package chess.bot.image.impl;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import chess.bot.Robot;
import chess.bot.Utility;
import chess.bot.image.AbstractImageCache;

public class OnlineChessImageCache extends AbstractImageCache {

	@Override
	protected String getImageFolder() {
		return "src/bot/";
	}
	
	@Override
	protected String getImagePath() {
		return "chess/bot/image/templateimages/onlinechessimages/";
	}

	@Override
	protected String getImageExtension() {
		return ".png";
	}

	public static void main(String[] args) throws Exception {
		OnlineChessImageCache cache = new OnlineChessImageCache();
		cache.refresh();

		BufferedImage pattern = cache.getUpperLeftCorner();
		BufferedImage screenShot = Robot.takeScreenShot();

		long startTime = System.currentTimeMillis();
		Point p = Utility.findSubImage(pattern, screenShot);

		int x = p.x;
		int y = p.y;

		x = x + 6;
		y = y - 556;

		Robot.mouseMove(x, y);

		System.out.println("Done.");

		long endTime = System.currentTimeMillis();

		System.out.println("diff = " + (endTime - startTime));

	}

	public static void main44(String[] args) {

		OnlineChessImageCache cache = new OnlineChessImageCache();
		cache.refresh();
		BufferedImage whiteBishop = cache.getWhiteBishopDarkSquare();
		BufferedImage blackBishop = cache.getBlackBishopDarkSquare();
		BufferedImage blackRook = cache.getBlackRookDarkSquare();

		long startTime = System.currentTimeMillis();
		double a = Utility.compareImage2(whiteBishop, blackBishop);
		double b = Utility.compareImage2(whiteBishop, blackRook);
		double c = Utility.compareImage2(blackBishop, blackRook);
		double d = Utility.compareImage2(blackBishop, blackBishop);

		System.out.println("a = " + a);
		System.out.println("b = " + b);
		System.out.println("c = " + c);
		System.out.println("d = " + d);

		long endTime = System.currentTimeMillis();

		System.out.println("diff = " + (endTime - startTime));

	}

	public static void main424(String[] args) {

		OnlineChessImageCache cache = new OnlineChessImageCache();
		cache.refresh();
		BufferedImage whiteBishop = cache.getWhiteBishopDarkSquare();
		BufferedImage blackBishop = cache.getBlackBishopDarkSquare();
		BufferedImage blackRook = cache.getBlackRookDarkSquare();

		long startTime = System.currentTimeMillis();
		double a = Utility.compareImage(whiteBishop, blackBishop);
		double b = Utility.compareImage(whiteBishop, blackRook);
		double c = Utility.compareImage(blackBishop, blackRook);
		double d = Utility.compareImage(blackBishop, blackBishop);

		System.out.println("a = " + a);
		System.out.println("b = " + b);
		System.out.println("c = " + c);
		System.out.println("d = " + d);

		long endTime = System.currentTimeMillis();

		System.out.println("diff = " + (endTime - startTime));

	}

	public static void main122(String[] args) throws Exception {
		OnlineChessImageCache cache = new OnlineChessImageCache();
		cache.refresh();

		BufferedImage pattern = cache.getUpperLeftCorner();
		BufferedImage screenShot = Robot.takeScreenShot();

		byte[] bmpPattern = Utility.convertImageToByteArray(pattern, Utility.COMPRESSION_ALGORITHM_BMP);
		byte[] bmpScreenShot = Utility.convertImageToByteArray(screenShot, Utility.COMPRESSION_ALGORITHM_BMP);

		long startTime = System.currentTimeMillis();

		InputStream isPattern = null;
		InputStream isScreenShot = null;
		try {
			isPattern = new ByteArrayInputStream(bmpPattern);
			isScreenShot = new ByteArrayInputStream(bmpScreenShot);

			int[][] rgbPattern = Utility.extractRgbMap(isPattern);
			int[][] rgbScreenShot = Utility.extractRgbMap(isScreenShot);

			Point p = Utility.findSubImagePoint(rgbScreenShot, rgbPattern);

			Robot.mouseMove(p.x, p.y);

			System.out.println("Done.");

		} finally {
			Utility.closeQuietly(isPattern, isScreenShot);
		}

		long endTime = System.currentTimeMillis();

		System.out.println("diff = " + (endTime - startTime));

	}

	// @Override
	// protected int getXGap() {
	// return 6;
	// }
	//
	// @Override
	// protected int getYGap() {
	// return -556;
	// }

	@Override
	protected int getXGap() {
		return 7;
	}

	@Override
	protected int getYGap() {
		return -505;
	}

	@Override
	public int getCellWidth() {
		return 64;
	}

	@Override
	public int getCellHeight() {
		return 64;
	}

	@Override
	public int getCellGap() {
		return 0;
	}

}

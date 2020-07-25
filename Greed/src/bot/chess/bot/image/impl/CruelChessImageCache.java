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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import chess.bot.Robot;
import chess.bot.Utility;
import chess.bot.image.AbstractImageCache;

public class CruelChessImageCache extends AbstractImageCache {

	@Override
	protected String getImageFolder() {
		return "src/bot/";
	}
	
	@Override
	protected String getImagePath() {
		return "chess/bot/image/templateimages/cruelchessimages/";
	}

	@Override
	protected String getImageExtension() {
		return ".png";
	}

	public static void maind(String[] args) throws Exception {
		CruelChessImageCache cache = new CruelChessImageCache();
		cache.refresh();

		BufferedImage pattern = cache.getBlackPawnDarkSquare();
		BufferedImage screenShot = Robot.takeScreenShot();

		long startTime = System.currentTimeMillis();
		Point p = Utility.findSubImage(pattern, screenShot);

		int x = p.x;
		int y = p.y;

		x = x + 14;
		y = y + 14;

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

		long startTime = System.currentTimeMillis();
		double a = Utility.compareImage2(whiteBishop, blackBishop);

		System.out.println("a = " + a);

		long endTime = System.currentTimeMillis();

		System.out.println("diff = " + (endTime - startTime));

	}
	
	public static void main(String[] args) throws IOException {

		OnlineChessImageCache cache = new OnlineChessImageCache();
		cache.refresh();
		
		
//		BufferedImage im1 = ImageIO.read(new File("C:\\Users\\Asus\\Desktop\\subimage11.png"));
//		BufferedImage im2 = ImageIO.read(new File("C:\\Users\\Asus\\Desktop\\templateImage11.png"));
//		BufferedImage im3 = ImageIO.read(new File("C:\\Users\\Asus\\Desktop\\whiteRookLightSquare.png"));
		
		BufferedImage im1 = ImageIO.read(new File("C:\\Users\\Asus\\Desktop\\image150.png"));
		BufferedImage im2 = ImageIO.read(new File("C:\\Users\\Asus\\Desktop\\whitePawnLightSquare.png"));
//		BufferedImage im3 = ImageIO.read(new File("C:\\Users\\Asus\\Desktop\\whiteRookLightSquare.png"));
		
		long startTime = System.currentTimeMillis();
		double a = Utility.compareImage2(im1, im2);

		System.out.println("a = " + a);

		long endTime = System.currentTimeMillis();

		System.out.println("diff = " + (endTime - startTime));

	}

	public static void main22(String[] args) throws IOException {

		CruelChessImageCache cache = new CruelChessImageCache();
		cache.refresh();
		BufferedImage blackpawn = cache.getBlackPawnDarkSquare();
		BufferedImage screenShot = ImageIO.read(new File("Myimageee.jpg"));

		long startTime = System.currentTimeMillis();
		double a = Utility.compareImage(blackpawn, screenShot);

		System.out.println("a = " + a);
		long endTime = System.currentTimeMillis();

		System.out.println("diff = " + (endTime - startTime));

	}

	public static void main122(String[] args) throws Exception {
		CruelChessImageCache cache = new CruelChessImageCache();
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

	@Override
	protected int getXGap() {
		return 14;
	}

	@Override
	protected int getYGap() {
		return 14;
	}

	@Override
	public int getCellWidth() {
		return 49;
	}

	@Override
	public int getCellHeight() {
		return 49;
	}

	@Override
	public int getCellGap() {
		return 1;
	}

}

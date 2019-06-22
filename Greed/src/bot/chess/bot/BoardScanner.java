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

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

import chess.bot.image.IImageCache;
import chess.bot.image.ImageType;
import chess.gui.GuiConstants;

public class BoardScanner {

	private IImageCache imageCache;
	private Point upperLeftCornerPoint;
	private ImageType[][] matchedImageTypes = new ImageType[8][8];
	private ChessBot bot;

	public BoardScanner(ChessBot bot, IImageCache imageCache) {
		this.imageCache = imageCache;
		this.bot = bot;
	}

	public byte[][] scan() throws Exception {
//		long startTime = System.currentTimeMillis();
		
		byte[][] board = new byte[8][8];
		
		BufferedImage currentScreenShot = Robot.takeScreenShot();

		if (upperLeftCornerPoint == null) {
			upperLeftCornerPoint = findUpperLeftCornerPoint(currentScreenShot);
		}

		outer: while (true) {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					int currentX = upperLeftCornerPoint.x + i * imageCache.getCellWidth() + i * imageCache.getCellGap();
					int currentY = upperLeftCornerPoint.y + j * imageCache.getCellHeight()
							+ j * imageCache.getCellGap();

					BufferedImage subimage = currentScreenShot.getSubimage(currentX, currentY,
							imageCache.getCellWidth(), imageCache.getCellHeight());

					byte piece = 0;
					try {
						piece = getPieceFromImage(subimage, i, j);
					} catch (NoMatchingSubimageException e) {
//						System.out.println("BULAMADIM !"  + System.currentTimeMillis());
						currentScreenShot = Robot.takeScreenShot();
						upperLeftCornerPoint = findUpperLeftCornerPoint(currentScreenShot);
						continue outer;
					}
					
					if (bot.getController().getPerspective() == GuiConstants.WHITE_PERSPECTIVE) {
						board[i][j] = piece;
					} else {
						board[7 - i][7 - j] = piece;
					}
				}
			}
			break outer;
		}

//		long end = System.currentTimeMillis();
//		System.out.println("fark = " + (end - startTime));
		
		return board;

	}

	private Point findUpperLeftCornerPoint(BufferedImage currentScreenShot) {
//		long startTime = System.currentTimeMillis();
		Point p = null;
		while (p == null) {
			BufferedImage pattern = imageCache.getUpperLeftCorner();
			p = Utility.findSubImage(pattern, currentScreenShot);
			if (p == null) {
				pattern = imageCache.getUpperLeftCornerSelected();
				p = Utility.findSubImage(pattern, currentScreenShot);
			}
			if (p == null) {
				pattern = imageCache.getUpperLeftCornerHighlighted();
				p = Utility.findSubImage(pattern, currentScreenShot);
			}
			if (p == null) {
				currentScreenShot = Robot.takeScreenShot();
			}
		}
//		long end = System.currentTimeMillis();
//		System.out.println("Upper left corner buluyorum = " + (end - startTime));
		return imageCache.adjustUpperLeftCornerPoint(p);
	}

	private byte getPieceFromImage(BufferedImage subimage, int x, int y) throws Exception {

		ImageType[] imageTypes = ImageType.values();

		/**
		 * implement some heuristic search here. Very likely same item with
		 * previous iteration. [THE REORDERINGS]
		 */
		ImageType prevImgageType = matchedImageTypes[x][y];
		if (prevImgageType != null) {
			imageTypes[prevImgageType.ordinal()] = imageTypes[0];
			imageTypes[0] = prevImgageType;
		}

		for (int i = 0; i < imageTypes.length; i++) {
			ImageType imageType = imageTypes[i];
			String methodName = Utility.prepareGetterMethodName(imageType.getFieldName());

			Method method = imageCache.getClass().getMethod(methodName);

			BufferedImage templateImage = (BufferedImage) method.invoke(imageCache);

			double a = Utility.compareImage2(subimage, templateImage);
//			if ((methodName.contains("Checked") && a < 1) || a == 0) {
			if (a < 500) {
				matchedImageTypes[x][y] = imageType;
				return imageType.getPieceType();
			}
		}

		throw new NoMatchingSubimageException("No matching subimage!");
	}
	
	public Point getUpperLeftCornerPoint() {
		//
		if (upperLeftCornerPoint != null) {
			return upperLeftCornerPoint;
		}
		//
		BufferedImage currentScreenShot = Robot.takeScreenShot();
		return upperLeftCornerPoint = findUpperLeftCornerPoint(currentScreenShot);
	}

	public ChessBot getBot() {
		return bot;
	}
	
	
	
}

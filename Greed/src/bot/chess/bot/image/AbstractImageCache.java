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
package chess.bot.image;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;

import javax.imageio.ImageIO;

import chess.bot.Utility;

public abstract class AbstractImageCache implements IImageCache {
	
	@Override
	public Point getPiecePoint(Point corner, int square) {
		int verticalCellCount = 7 - (square / 8);
		int horizontalCellCount = square % 8;
		int verticalGap = verticalCellCount * getCellGap();
		int horizontalGap = horizontalCellCount * getCellGap();
		
		int y = verticalCellCount * getCellHeight() + verticalGap;
		int x = horizontalCellCount * getCellWidth() + horizontalGap;
		
		int randomX = Utility.generateStrongRandomNumber(5, 50);
		int randomY = Utility.generateStrongRandomNumber(5, 50);
		
		return new Point(corner.x + x + randomX, corner.y + y + randomY);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////// UNSELECTED ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////

	private BufferedImage upperLeftCorner;
	private BufferedImage emptyDarkSquare;
	private BufferedImage emptyLightSquare;

	/**
	 * Black Pieces, Dark Squares
	 */
	private BufferedImage blackBishopDarkSquare;
	private BufferedImage blackKingDarkSquare;
	private BufferedImage blackKnightDarkSquare;
	private BufferedImage blackPawnDarkSquare;
	private BufferedImage blackQueenDarkSquare;
	private BufferedImage blackRookDarkSquare;

	/**
	 * White Pieces, Dark Squares
	 */
	private BufferedImage whiteBishopDarkSquare;
	private BufferedImage whiteKingDarkSquare;
	private BufferedImage whiteKnightDarkSquare;
	private BufferedImage whitePawnDarkSquare;
	private BufferedImage whiteQueenDarkSquare;
	private BufferedImage whiteRookDarkSquare;

	/**
	 * Black Pieces, Light Squares
	 */
	private BufferedImage blackBishopLightSquare;
	private BufferedImage blackKingLightSquare;
	private BufferedImage blackKnightLightSquare;
	private BufferedImage blackPawnLightSquare;
	private BufferedImage blackQueenLightSquare;
	private BufferedImage blackRookLightSquare;

	/**
	 * White Pieces, Light Squares
	 */
	private BufferedImage whiteBishopLightSquare;
	private BufferedImage whiteKingLightSquare;
	private BufferedImage whiteKnightLightSquare;
	private BufferedImage whitePawnLightSquare;
	private BufferedImage whiteQueenLightSquare;
	private BufferedImage whiteRookLightSquare;

	//////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////// SELECTED ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////

	private BufferedImage upperLeftCornerSelected;
	private BufferedImage emptyDarkSquareSelected;
	private BufferedImage emptyLightSquareSelected;

	/**
	 * Black Pieces, Dark Squares
	 */
	private BufferedImage blackBishopDarkSquareSelected;
	private BufferedImage blackKingDarkSquareSelected;
	private BufferedImage blackKnightDarkSquareSelected;
	private BufferedImage blackPawnDarkSquareSelected;
	private BufferedImage blackQueenDarkSquareSelected;
	private BufferedImage blackRookDarkSquareSelected;

	/**
	 * White Pieces, Dark Squares
	 */
	private BufferedImage whiteBishopDarkSquareSelected;
	private BufferedImage whiteKingDarkSquareSelected;
	private BufferedImage whiteKnightDarkSquareSelected;
	private BufferedImage whitePawnDarkSquareSelected;
	private BufferedImage whiteQueenDarkSquareSelected;
	private BufferedImage whiteRookDarkSquareSelected;

	/**
	 * Black Pieces, Light Squares
	 */
	private BufferedImage blackBishopLightSquareSelected;
	private BufferedImage blackKingLightSquareSelected;
	private BufferedImage blackKnightLightSquareSelected;
	private BufferedImage blackPawnLightSquareSelected;
	private BufferedImage blackQueenLightSquareSelected;
	private BufferedImage blackRookLightSquareSelected;

	/**
	 * White Pieces, Light Squares
	 */
	private BufferedImage whiteBishopLightSquareSelected;
	private BufferedImage whiteKingLightSquareSelected;
	private BufferedImage whiteKnightLightSquareSelected;
	private BufferedImage whitePawnLightSquareSelected;
	private BufferedImage whiteQueenLightSquareSelected;
	private BufferedImage whiteRookLightSquareSelected;
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////// HIGHLIGHED ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////

	private BufferedImage upperLeftCornerHighlighted;
	private BufferedImage emptyDarkSquareHighlighted;
	private BufferedImage emptyLightSquareHighlighted;

	/**
	 * Black Pieces, Dark Squares
	 */
	private BufferedImage blackBishopDarkSquareHighlighted;
	private BufferedImage blackKingDarkSquareHighlighted;
	private BufferedImage blackKnightDarkSquareHighlighted;
	private BufferedImage blackPawnDarkSquareHighlighted;
	private BufferedImage blackQueenDarkSquareHighlighted;
	private BufferedImage blackRookDarkSquareHighlighted;

	/**
	 * White Pieces, Dark Squares
	 */
	private BufferedImage whiteBishopDarkSquareHighlighted;
	private BufferedImage whiteKingDarkSquareHighlighted;
	private BufferedImage whiteKnightDarkSquareHighlighted;
	private BufferedImage whitePawnDarkSquareHighlighted;
	private BufferedImage whiteQueenDarkSquareHighlighted;
	private BufferedImage whiteRookDarkSquareHighlighted;

	/**
	 * Black Pieces, Light Squares
	 */
	private BufferedImage blackBishopLightSquareHighlighted;
	private BufferedImage blackKingLightSquareHighlighted;
	private BufferedImage blackKnightLightSquareHighlighted;
	private BufferedImage blackPawnLightSquareHighlighted;
	private BufferedImage blackQueenLightSquareHighlighted;
	private BufferedImage blackRookLightSquareHighlighted;

	/**
	 * White Pieces, Light Squares
	 */
	private BufferedImage whiteBishopLightSquareHighlighted;
	private BufferedImage whiteKingLightSquareHighlighted;
	private BufferedImage whiteKnightLightSquareHighlighted;
	private BufferedImage whitePawnLightSquareHighlighted;
	private BufferedImage whiteQueenLightSquareHighlighted;
	private BufferedImage whiteRookLightSquareHighlighted;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////// CHECKED ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	private BufferedImage blackKingDarkSquareChecked;
	private BufferedImage blackKingLightSquareChecked;
	
	private BufferedImage whiteKingDarkSquareChecked;
	private BufferedImage whiteKingLightSquareChecked;
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////// PREMOVED ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////

	private BufferedImage upperLeftCornerPreMoved;
	private BufferedImage emptyDarkSquarePreMoved;
	private BufferedImage emptyLightSquarePreMoved;

	/**
	 * Black Pieces, Dark Squares
	 */
	private BufferedImage blackBishopDarkSquarePreMoved;
	private BufferedImage blackKingDarkSquarePreMoved;
	private BufferedImage blackKnightDarkSquarePreMoved;
	private BufferedImage blackPawnDarkSquarePreMoved;
	private BufferedImage blackQueenDarkSquarePreMoved;
	private BufferedImage blackRookDarkSquarePreMoved;

	/**
	 * White Pieces, Dark Squares
	 */
	private BufferedImage whiteBishopDarkSquarePreMoved;
	private BufferedImage whiteKingDarkSquarePreMoved;
	private BufferedImage whiteKnightDarkSquarePreMoved;
	private BufferedImage whitePawnDarkSquarePreMoved;
	private BufferedImage whiteQueenDarkSquarePreMoved;
	private BufferedImage whiteRookDarkSquarePreMoved;

	/**
	 * Black Pieces, Light Squares
	 */
	private BufferedImage blackBishopLightSquarePreMoved;
	private BufferedImage blackKingLightSquarePreMoved;
	private BufferedImage blackKnightLightSquarePreMoved;
	private BufferedImage blackPawnLightSquarePreMoved;
	private BufferedImage blackQueenLightSquarePreMoved;
	private BufferedImage blackRookLightSquarePreMoved;

	/**
	 * White Pieces, Light Squares
	 */
	private BufferedImage whiteBishopLightSquarePreMoved;
	private BufferedImage whiteKingLightSquarePreMoved;
	private BufferedImage whiteKnightLightSquarePreMoved;
	private BufferedImage whitePawnLightSquarePreMoved;
	private BufferedImage whiteQueenLightSquarePreMoved;
	private BufferedImage whiteRookLightSquarePreMoved;

	public void refresh() {
		try {
			ImageType[] types = ImageType.values();
			int typeCount = types.length;
			for (int i = 0; i < typeCount; i++) {
				ImageType type = types[i];
				Field field = AbstractImageCache.class.getDeclaredField(type.getFieldName());
				field.setAccessible(true);
				try {
					field.set(this, ImageIO.read(new File(getImagePath() + type.getFieldName() + getImageExtension())));
				} catch (Exception e) {
					System.out.println("asdf");
					System.out.println("asdf");
					System.out.println("asdf");
					// TODO: handle exception
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Point adjustUpperLeftCornerPoint(Point p) {
		Point adjustedPoint = new Point(p.x + getXGap(), p.y + getYGap());
		return adjustedPoint;
	}

	protected abstract String getImagePath();

	protected abstract String getImageExtension();

	protected abstract int getXGap();

	protected abstract int getYGap();

	public BufferedImage getEmptyDarkSquare() {
		return emptyDarkSquare;
	}

	public void setEmptyDarkSquare(BufferedImage emptyDarkSquare) {
		this.emptyDarkSquare = emptyDarkSquare;
	}

	public BufferedImage getEmptyLightSquare() {
		return emptyLightSquare;
	}

	public void setEmptyLightSquare(BufferedImage emptyLightSquare) {
		this.emptyLightSquare = emptyLightSquare;
	}

	public BufferedImage getBlackBishopDarkSquare() {
		return blackBishopDarkSquare;
	}

	public void setBlackBishopDarkSquare(BufferedImage blackBishopDarkSquare) {
		this.blackBishopDarkSquare = blackBishopDarkSquare;
	}

	public BufferedImage getBlackKingDarkSquare() {
		return blackKingDarkSquare;
	}

	public void setBlackKingDarkSquare(BufferedImage blackKingDarkSquare) {
		this.blackKingDarkSquare = blackKingDarkSquare;
	}

	public BufferedImage getBlackKnightDarkSquare() {
		return blackKnightDarkSquare;
	}

	public void setBlackKnightDarkSquare(BufferedImage blackKnightDarkSquare) {
		this.blackKnightDarkSquare = blackKnightDarkSquare;
	}

	public BufferedImage getBlackPawnDarkSquare() {
		return blackPawnDarkSquare;
	}

	public void setBlackPawnDarkSquare(BufferedImage blackPawnDarkSquare) {
		this.blackPawnDarkSquare = blackPawnDarkSquare;
	}

	public BufferedImage getBlackQueenDarkSquare() {
		return blackQueenDarkSquare;
	}

	public void setBlackQueenDarkSquare(BufferedImage blackQueenDarkSquare) {
		this.blackQueenDarkSquare = blackQueenDarkSquare;
	}

	public BufferedImage getBlackRookDarkSquare() {
		return blackRookDarkSquare;
	}

	public void setBlackRookDarkSquare(BufferedImage blackRookDarkSquare) {
		this.blackRookDarkSquare = blackRookDarkSquare;
	}

	public BufferedImage getWhiteBishopDarkSquare() {
		return whiteBishopDarkSquare;
	}

	public void setWhiteBishopDarkSquare(BufferedImage whiteBishopDarkSquare) {
		this.whiteBishopDarkSquare = whiteBishopDarkSquare;
	}

	public BufferedImage getWhiteKingDarkSquare() {
		return whiteKingDarkSquare;
	}

	public void setWhiteKingDarkSquare(BufferedImage whiteKingDarkSquare) {
		this.whiteKingDarkSquare = whiteKingDarkSquare;
	}

	public BufferedImage getWhiteKnightDarkSquare() {
		return whiteKnightDarkSquare;
	}

	public void setWhiteKnightDarkSquare(BufferedImage whiteKnightDarkSquare) {
		this.whiteKnightDarkSquare = whiteKnightDarkSquare;
	}

	public BufferedImage getWhitePawnDarkSquare() {
		return whitePawnDarkSquare;
	}

	public void setWhitePawnDarkSquare(BufferedImage whitePawnDarkSquare) {
		this.whitePawnDarkSquare = whitePawnDarkSquare;
	}

	public BufferedImage getWhiteQueenDarkSquare() {
		return whiteQueenDarkSquare;
	}

	public void setWhiteQueenDarkSquare(BufferedImage whiteQueenDarkSquare) {
		this.whiteQueenDarkSquare = whiteQueenDarkSquare;
	}

	public BufferedImage getWhiteRookDarkSquare() {
		return whiteRookDarkSquare;
	}

	public void setWhiteRookDarkSquare(BufferedImage whiteRookDarkSquare) {
		this.whiteRookDarkSquare = whiteRookDarkSquare;
	}

	public BufferedImage getBlackBishopLightSquare() {
		return blackBishopLightSquare;
	}

	public void setBlackBishopLightSquare(BufferedImage blackBishopLightSquare) {
		this.blackBishopLightSquare = blackBishopLightSquare;
	}

	public BufferedImage getBlackKingLightSquare() {
		return blackKingLightSquare;
	}

	public void setBlackKingLightSquare(BufferedImage blackKingLightSquare) {
		this.blackKingLightSquare = blackKingLightSquare;
	}

	public BufferedImage getBlackKnightLightSquare() {
		return blackKnightLightSquare;
	}

	public void setBlackKnightLightSquare(BufferedImage blackKnightLightSquare) {
		this.blackKnightLightSquare = blackKnightLightSquare;
	}

	public BufferedImage getBlackPawnLightSquare() {
		return blackPawnLightSquare;
	}

	public void setBlackPawnLightSquare(BufferedImage blackPawnLightSquare) {
		this.blackPawnLightSquare = blackPawnLightSquare;
	}

	public BufferedImage getBlackQueenLightSquare() {
		return blackQueenLightSquare;
	}

	public void setBlackQueenLightSquare(BufferedImage blackQueenLightSquare) {
		this.blackQueenLightSquare = blackQueenLightSquare;
	}

	public BufferedImage getBlackRookLightSquare() {
		return blackRookLightSquare;
	}

	public void setBlackRookLightSquare(BufferedImage blackRookLightSquare) {
		this.blackRookLightSquare = blackRookLightSquare;
	}

	public BufferedImage getWhiteBishopLightSquare() {
		return whiteBishopLightSquare;
	}

	public void setWhiteBishopLightSquare(BufferedImage whiteBishopLightSquare) {
		this.whiteBishopLightSquare = whiteBishopLightSquare;
	}

	public BufferedImage getWhiteKingLightSquare() {
		return whiteKingLightSquare;
	}

	public void setWhiteKingLightSquare(BufferedImage whiteKingLightSquare) {
		this.whiteKingLightSquare = whiteKingLightSquare;
	}

	public BufferedImage getWhiteKnightLightSquare() {
		return whiteKnightLightSquare;
	}

	public void setWhiteKnightLightSquare(BufferedImage whiteKnightLightSquare) {
		this.whiteKnightLightSquare = whiteKnightLightSquare;
	}

	public BufferedImage getWhitePawnLightSquare() {
		return whitePawnLightSquare;
	}

	public void setWhitePawnLightSquare(BufferedImage whitePawnLightSquare) {
		this.whitePawnLightSquare = whitePawnLightSquare;
	}

	public BufferedImage getWhiteQueenLightSquare() {
		return whiteQueenLightSquare;
	}

	public void setWhiteQueenLightSquare(BufferedImage whiteQueenLightSquare) {
		this.whiteQueenLightSquare = whiteQueenLightSquare;
	}

	public BufferedImage getWhiteRookLightSquare() {
		return whiteRookLightSquare;
	}

	public void setWhiteRookLightSquare(BufferedImage whiteRookLightSquare) {
		this.whiteRookLightSquare = whiteRookLightSquare;
	}

	public BufferedImage getUpperLeftCorner() {
		return upperLeftCorner;
	}

	public void setUpperLeftCorner(BufferedImage upperLeftCorner) {
		this.upperLeftCorner = upperLeftCorner;
	}

	public BufferedImage getEmptyDarkSquareSelected() {
		return emptyDarkSquareSelected;
	}

	public void setEmptyDarkSquareSelected(BufferedImage emptyDarkSquareSelected) {
		this.emptyDarkSquareSelected = emptyDarkSquareSelected;
	}

	public BufferedImage getEmptyLightSquareSelected() {
		return emptyLightSquareSelected;
	}

	public void setEmptyLightSquareSelected(BufferedImage emptyLightSquareSelected) {
		this.emptyLightSquareSelected = emptyLightSquareSelected;
	}

	public BufferedImage getBlackBishopDarkSquareSelected() {
		return blackBishopDarkSquareSelected;
	}

	public void setBlackBishopDarkSquareSelected(BufferedImage blackBishopDarkSquareSelected) {
		this.blackBishopDarkSquareSelected = blackBishopDarkSquareSelected;
	}

	public BufferedImage getBlackKingDarkSquareSelected() {
		return blackKingDarkSquareSelected;
	}

	public void setBlackKingDarkSquareSelected(BufferedImage blackKingDarkSquareSelected) {
		this.blackKingDarkSquareSelected = blackKingDarkSquareSelected;
	}

	public BufferedImage getBlackKnightDarkSquareSelected() {
		return blackKnightDarkSquareSelected;
	}

	public void setBlackKnightDarkSquareSelected(BufferedImage blackKnightDarkSquareSelected) {
		this.blackKnightDarkSquareSelected = blackKnightDarkSquareSelected;
	}

	public BufferedImage getBlackPawnDarkSquareSelected() {
		return blackPawnDarkSquareSelected;
	}

	public void setBlackPawnDarkSquareSelected(BufferedImage blackPawnDarkSquareSelected) {
		this.blackPawnDarkSquareSelected = blackPawnDarkSquareSelected;
	}

	public BufferedImage getBlackQueenDarkSquareSelected() {
		return blackQueenDarkSquareSelected;
	}

	public void setBlackQueenDarkSquareSelected(BufferedImage blackQueenDarkSquareSelected) {
		this.blackQueenDarkSquareSelected = blackQueenDarkSquareSelected;
	}

	public BufferedImage getBlackRookDarkSquareSelected() {
		return blackRookDarkSquareSelected;
	}

	public void setBlackRookDarkSquareSelected(BufferedImage blackRookDarkSquareSelected) {
		this.blackRookDarkSquareSelected = blackRookDarkSquareSelected;
	}

	public BufferedImage getWhiteBishopDarkSquareSelected() {
		return whiteBishopDarkSquareSelected;
	}

	public void setWhiteBishopDarkSquareSelected(BufferedImage whiteBishopDarkSquareSelected) {
		this.whiteBishopDarkSquareSelected = whiteBishopDarkSquareSelected;
	}

	public BufferedImage getWhiteKingDarkSquareSelected() {
		return whiteKingDarkSquareSelected;
	}

	public void setWhiteKingDarkSquareSelected(BufferedImage whiteKingDarkSquareSelected) {
		this.whiteKingDarkSquareSelected = whiteKingDarkSquareSelected;
	}

	public BufferedImage getWhiteKnightDarkSquareSelected() {
		return whiteKnightDarkSquareSelected;
	}

	public void setWhiteKnightDarkSquareSelected(BufferedImage whiteKnightDarkSquareSelected) {
		this.whiteKnightDarkSquareSelected = whiteKnightDarkSquareSelected;
	}

	public BufferedImage getWhitePawnDarkSquareSelected() {
		return whitePawnDarkSquareSelected;
	}

	public void setWhitePawnDarkSquareSelected(BufferedImage whitePawnDarkSquareSelected) {
		this.whitePawnDarkSquareSelected = whitePawnDarkSquareSelected;
	}

	public BufferedImage getWhiteQueenDarkSquareSelected() {
		return whiteQueenDarkSquareSelected;
	}

	public void setWhiteQueenDarkSquareSelected(BufferedImage whiteQueenDarkSquareSelected) {
		this.whiteQueenDarkSquareSelected = whiteQueenDarkSquareSelected;
	}

	public BufferedImage getWhiteRookDarkSquareSelected() {
		return whiteRookDarkSquareSelected;
	}

	public void setWhiteRookDarkSquareSelected(BufferedImage whiteRookDarkSquareSelected) {
		this.whiteRookDarkSquareSelected = whiteRookDarkSquareSelected;
	}

	public BufferedImage getBlackBishopLightSquareSelected() {
		return blackBishopLightSquareSelected;
	}

	public void setBlackBishopLightSquareSelected(BufferedImage blackBishopLightSquareSelected) {
		this.blackBishopLightSquareSelected = blackBishopLightSquareSelected;
	}

	public BufferedImage getBlackKingLightSquareSelected() {
		return blackKingLightSquareSelected;
	}

	public void setBlackKingLightSquareSelected(BufferedImage blackKingLightSquareSelected) {
		this.blackKingLightSquareSelected = blackKingLightSquareSelected;
	}

	public BufferedImage getBlackKnightLightSquareSelected() {
		return blackKnightLightSquareSelected;
	}

	public void setBlackKnightLightSquareSelected(BufferedImage blackKnightLightSquareSelected) {
		this.blackKnightLightSquareSelected = blackKnightLightSquareSelected;
	}

	public BufferedImage getBlackPawnLightSquareSelected() {
		return blackPawnLightSquareSelected;
	}

	public void setBlackPawnLightSquareSelected(BufferedImage blackPawnLightSquareSelected) {
		this.blackPawnLightSquareSelected = blackPawnLightSquareSelected;
	}

	public BufferedImage getBlackQueenLightSquareSelected() {
		return blackQueenLightSquareSelected;
	}

	public void setBlackQueenLightSquareSelected(BufferedImage blackQueenLightSquareSelected) {
		this.blackQueenLightSquareSelected = blackQueenLightSquareSelected;
	}

	public BufferedImage getBlackRookLightSquareSelected() {
		return blackRookLightSquareSelected;
	}

	public void setBlackRookLightSquareSelected(BufferedImage blackRookLightSquareSelected) {
		this.blackRookLightSquareSelected = blackRookLightSquareSelected;
	}

	public BufferedImage getWhiteBishopLightSquareSelected() {
		return whiteBishopLightSquareSelected;
	}

	public void setWhiteBishopLightSquareSelected(BufferedImage whiteBishopLightSquareSelected) {
		this.whiteBishopLightSquareSelected = whiteBishopLightSquareSelected;
	}

	public BufferedImage getWhiteKingLightSquareSelected() {
		return whiteKingLightSquareSelected;
	}

	public void setWhiteKingLightSquareSelected(BufferedImage whiteKingLightSquareSelected) {
		this.whiteKingLightSquareSelected = whiteKingLightSquareSelected;
	}

	public BufferedImage getWhiteKnightLightSquareSelected() {
		return whiteKnightLightSquareSelected;
	}

	public void setWhiteKnightLightSquareSelected(BufferedImage whiteKnightLightSquareSelected) {
		this.whiteKnightLightSquareSelected = whiteKnightLightSquareSelected;
	}

	public BufferedImage getWhitePawnLightSquareSelected() {
		return whitePawnLightSquareSelected;
	}

	public void setWhitePawnLightSquareSelected(BufferedImage whitePawnLightSquareSelected) {
		this.whitePawnLightSquareSelected = whitePawnLightSquareSelected;
	}

	public BufferedImage getWhiteQueenLightSquareSelected() {
		return whiteQueenLightSquareSelected;
	}

	public void setWhiteQueenLightSquareSelected(BufferedImage whiteQueenLightSquareSelected) {
		this.whiteQueenLightSquareSelected = whiteQueenLightSquareSelected;
	}

	public BufferedImage getWhiteRookLightSquareSelected() {
		return whiteRookLightSquareSelected;
	}

	public void setWhiteRookLightSquareSelected(BufferedImage whiteRookLightSquareSelected) {
		this.whiteRookLightSquareSelected = whiteRookLightSquareSelected;
	}

	public BufferedImage getUpperLeftCornerSelected() {
		return upperLeftCornerSelected;
	}

	public void setUpperLeftCornerSelected(BufferedImage upperLeftCornerSelected) {
		this.upperLeftCornerSelected = upperLeftCornerSelected;
	}

	public BufferedImage getUpperLeftCornerHighlighted() {
		return upperLeftCornerHighlighted;
	}

	public void setUpperLeftCornerHighlighted(BufferedImage upperLeftCornerHighlighted) {
		this.upperLeftCornerHighlighted = upperLeftCornerHighlighted;
	}

	public BufferedImage getEmptyDarkSquareHighlighted() {
		return emptyDarkSquareHighlighted;
	}

	public void setEmptyDarkSquareHighlighted(BufferedImage emptyDarkSquareHighlighted) {
		this.emptyDarkSquareHighlighted = emptyDarkSquareHighlighted;
	}

	public BufferedImage getEmptyLightSquareHighlighted() {
		return emptyLightSquareHighlighted;
	}

	public void setEmptyLightSquareHighlighted(BufferedImage emptyLightSquareHighlighted) {
		this.emptyLightSquareHighlighted = emptyLightSquareHighlighted;
	}

	public BufferedImage getBlackBishopDarkSquareHighlighted() {
		return blackBishopDarkSquareHighlighted;
	}

	public void setBlackBishopDarkSquareHighlighted(BufferedImage blackBishopDarkSquareHighlighted) {
		this.blackBishopDarkSquareHighlighted = blackBishopDarkSquareHighlighted;
	}

	public BufferedImage getBlackKingDarkSquareHighlighted() {
		return blackKingDarkSquareHighlighted;
	}

	public void setBlackKingDarkSquareHighlighted(BufferedImage blackKingDarkSquareHighlighted) {
		this.blackKingDarkSquareHighlighted = blackKingDarkSquareHighlighted;
	}

	public BufferedImage getBlackKnightDarkSquareHighlighted() {
		return blackKnightDarkSquareHighlighted;
	}

	public void setBlackKnightDarkSquareHighlighted(BufferedImage blackKnightDarkSquareHighlighted) {
		this.blackKnightDarkSquareHighlighted = blackKnightDarkSquareHighlighted;
	}

	public BufferedImage getBlackPawnDarkSquareHighlighted() {
		return blackPawnDarkSquareHighlighted;
	}

	public void setBlackPawnDarkSquareHighlighted(BufferedImage blackPawnDarkSquareHighlighted) {
		this.blackPawnDarkSquareHighlighted = blackPawnDarkSquareHighlighted;
	}

	public BufferedImage getBlackQueenDarkSquareHighlighted() {
		return blackQueenDarkSquareHighlighted;
	}

	public void setBlackQueenDarkSquareHighlighted(BufferedImage blackQueenDarkSquareHighlighted) {
		this.blackQueenDarkSquareHighlighted = blackQueenDarkSquareHighlighted;
	}

	public BufferedImage getBlackRookDarkSquareHighlighted() {
		return blackRookDarkSquareHighlighted;
	}

	public void setBlackRookDarkSquareHighlighted(BufferedImage blackRookDarkSquareHighlighted) {
		this.blackRookDarkSquareHighlighted = blackRookDarkSquareHighlighted;
	}

	public BufferedImage getWhiteBishopDarkSquareHighlighted() {
		return whiteBishopDarkSquareHighlighted;
	}

	public void setWhiteBishopDarkSquareHighlighted(BufferedImage whiteBishopDarkSquareHighlighted) {
		this.whiteBishopDarkSquareHighlighted = whiteBishopDarkSquareHighlighted;
	}

	public BufferedImage getWhiteKingDarkSquareHighlighted() {
		return whiteKingDarkSquareHighlighted;
	}

	public void setWhiteKingDarkSquareHighlighted(BufferedImage whiteKingDarkSquareHighlighted) {
		this.whiteKingDarkSquareHighlighted = whiteKingDarkSquareHighlighted;
	}

	public BufferedImage getWhiteKnightDarkSquareHighlighted() {
		return whiteKnightDarkSquareHighlighted;
	}

	public void setWhiteKnightDarkSquareHighlighted(BufferedImage whiteKnightDarkSquareHighlighted) {
		this.whiteKnightDarkSquareHighlighted = whiteKnightDarkSquareHighlighted;
	}

	public BufferedImage getWhitePawnDarkSquareHighlighted() {
		return whitePawnDarkSquareHighlighted;
	}

	public void setWhitePawnDarkSquareHighlighted(BufferedImage whitePawnDarkSquareHighlighted) {
		this.whitePawnDarkSquareHighlighted = whitePawnDarkSquareHighlighted;
	}

	public BufferedImage getWhiteQueenDarkSquareHighlighted() {
		return whiteQueenDarkSquareHighlighted;
	}

	public void setWhiteQueenDarkSquareHighlighted(BufferedImage whiteQueenDarkSquareHighlighted) {
		this.whiteQueenDarkSquareHighlighted = whiteQueenDarkSquareHighlighted;
	}

	public BufferedImage getWhiteRookDarkSquareHighlighted() {
		return whiteRookDarkSquareHighlighted;
	}

	public void setWhiteRookDarkSquareHighlighted(BufferedImage whiteRookDarkSquareHighlighted) {
		this.whiteRookDarkSquareHighlighted = whiteRookDarkSquareHighlighted;
	}

	public BufferedImage getBlackBishopLightSquareHighlighted() {
		return blackBishopLightSquareHighlighted;
	}

	public void setBlackBishopLightSquareHighlighted(BufferedImage blackBishopLightSquareHighlighted) {
		this.blackBishopLightSquareHighlighted = blackBishopLightSquareHighlighted;
	}

	public BufferedImage getBlackKingLightSquareHighlighted() {
		return blackKingLightSquareHighlighted;
	}

	public void setBlackKingLightSquareHighlighted(BufferedImage blackKingLightSquareHighlighted) {
		this.blackKingLightSquareHighlighted = blackKingLightSquareHighlighted;
	}

	public BufferedImage getBlackKnightLightSquareHighlighted() {
		return blackKnightLightSquareHighlighted;
	}

	public void setBlackKnightLightSquareHighlighted(BufferedImage blackKnightLightSquareHighlighted) {
		this.blackKnightLightSquareHighlighted = blackKnightLightSquareHighlighted;
	}

	public BufferedImage getBlackPawnLightSquareHighlighted() {
		return blackPawnLightSquareHighlighted;
	}

	public void setBlackPawnLightSquareHighlighted(BufferedImage blackPawnLightSquareHighlighted) {
		this.blackPawnLightSquareHighlighted = blackPawnLightSquareHighlighted;
	}

	public BufferedImage getBlackQueenLightSquareHighlighted() {
		return blackQueenLightSquareHighlighted;
	}

	public void setBlackQueenLightSquareHighlighted(BufferedImage blackQueenLightSquareHighlighted) {
		this.blackQueenLightSquareHighlighted = blackQueenLightSquareHighlighted;
	}

	public BufferedImage getBlackRookLightSquareHighlighted() {
		return blackRookLightSquareHighlighted;
	}

	public void setBlackRookLightSquareHighlighted(BufferedImage blackRookLightSquareHighlighted) {
		this.blackRookLightSquareHighlighted = blackRookLightSquareHighlighted;
	}

	public BufferedImage getWhiteBishopLightSquareHighlighted() {
		return whiteBishopLightSquareHighlighted;
	}

	public void setWhiteBishopLightSquareHighlighted(BufferedImage whiteBishopLightSquareHighlighted) {
		this.whiteBishopLightSquareHighlighted = whiteBishopLightSquareHighlighted;
	}

	public BufferedImage getWhiteKingLightSquareHighlighted() {
		return whiteKingLightSquareHighlighted;
	}

	public void setWhiteKingLightSquareHighlighted(BufferedImage whiteKingLightSquareHighlighted) {
		this.whiteKingLightSquareHighlighted = whiteKingLightSquareHighlighted;
	}

	public BufferedImage getWhiteKnightLightSquareHighlighted() {
		return whiteKnightLightSquareHighlighted;
	}

	public void setWhiteKnightLightSquareHighlighted(BufferedImage whiteKnightLightSquareHighlighted) {
		this.whiteKnightLightSquareHighlighted = whiteKnightLightSquareHighlighted;
	}

	public BufferedImage getWhitePawnLightSquareHighlighted() {
		return whitePawnLightSquareHighlighted;
	}

	public void setWhitePawnLightSquareHighlighted(BufferedImage whitePawnLightSquareHighlighted) {
		this.whitePawnLightSquareHighlighted = whitePawnLightSquareHighlighted;
	}

	public BufferedImage getWhiteQueenLightSquareHighlighted() {
		return whiteQueenLightSquareHighlighted;
	}

	public void setWhiteQueenLightSquareHighlighted(BufferedImage whiteQueenLightSquareHighlighted) {
		this.whiteQueenLightSquareHighlighted = whiteQueenLightSquareHighlighted;
	}

	public BufferedImage getWhiteRookLightSquareHighlighted() {
		return whiteRookLightSquareHighlighted;
	}

	public void setWhiteRookLightSquareHighlighted(BufferedImage whiteRookLightSquareHighlighted) {
		this.whiteRookLightSquareHighlighted = whiteRookLightSquareHighlighted;
	}

	public BufferedImage getBlackKingDarkSquareChecked() {
		return blackKingDarkSquareChecked;
	}

	public void setBlackKingDarkSquareChecked(BufferedImage blackKingDarkSquareChecked) {
		this.blackKingDarkSquareChecked = blackKingDarkSquareChecked;
	}

	public BufferedImage getBlackKingLightSquareChecked() {
		return blackKingLightSquareChecked;
	}

	public void setBlackKingLightSquareChecked(BufferedImage blackKingLightSquareChecked) {
		this.blackKingLightSquareChecked = blackKingLightSquareChecked;
	}

	public BufferedImage getWhiteKingDarkSquareChecked() {
		return whiteKingDarkSquareChecked;
	}

	public void setWhiteKingDarkSquareChecked(BufferedImage whiteKingDarkSquareChecked) {
		this.whiteKingDarkSquareChecked = whiteKingDarkSquareChecked;
	}

	public BufferedImage getWhiteKingLightSquareChecked() {
		return whiteKingLightSquareChecked;
	}

	public void setWhiteKingLightSquareChecked(BufferedImage whiteKingLightSquareChecked) {
		this.whiteKingLightSquareChecked = whiteKingLightSquareChecked;
	}

	public BufferedImage getUpperLeftCornerPreMoved() {
		return upperLeftCornerPreMoved;
	}

	public void setUpperLeftCornerPreMoved(BufferedImage upperLeftCornerPreMoved) {
		this.upperLeftCornerPreMoved = upperLeftCornerPreMoved;
	}

	public BufferedImage getEmptyDarkSquarePreMoved() {
		return emptyDarkSquarePreMoved;
	}

	public void setEmptyDarkSquarePreMoved(BufferedImage emptyDarkSquarePreMoved) {
		this.emptyDarkSquarePreMoved = emptyDarkSquarePreMoved;
	}

	public BufferedImage getEmptyLightSquarePreMoved() {
		return emptyLightSquarePreMoved;
	}

	public void setEmptyLightSquarePreMoved(BufferedImage emptyLightSquarePreMoved) {
		this.emptyLightSquarePreMoved = emptyLightSquarePreMoved;
	}

	public BufferedImage getBlackBishopDarkSquarePreMoved() {
		return blackBishopDarkSquarePreMoved;
	}

	public void setBlackBishopDarkSquarePreMoved(BufferedImage blackBishopDarkSquarePreMoved) {
		this.blackBishopDarkSquarePreMoved = blackBishopDarkSquarePreMoved;
	}

	public BufferedImage getBlackKingDarkSquarePreMoved() {
		return blackKingDarkSquarePreMoved;
	}

	public void setBlackKingDarkSquarePreMoved(BufferedImage blackKingDarkSquarePreMoved) {
		this.blackKingDarkSquarePreMoved = blackKingDarkSquarePreMoved;
	}

	public BufferedImage getBlackKnightDarkSquarePreMoved() {
		return blackKnightDarkSquarePreMoved;
	}

	public void setBlackKnightDarkSquarePreMoved(BufferedImage blackKnightDarkSquarePreMoved) {
		this.blackKnightDarkSquarePreMoved = blackKnightDarkSquarePreMoved;
	}

	public BufferedImage getBlackPawnDarkSquarePreMoved() {
		return blackPawnDarkSquarePreMoved;
	}

	public void setBlackPawnDarkSquarePreMoved(BufferedImage blackPawnDarkSquarePreMoved) {
		this.blackPawnDarkSquarePreMoved = blackPawnDarkSquarePreMoved;
	}

	public BufferedImage getBlackQueenDarkSquarePreMoved() {
		return blackQueenDarkSquarePreMoved;
	}

	public void setBlackQueenDarkSquarePreMoved(BufferedImage blackQueenDarkSquarePreMoved) {
		this.blackQueenDarkSquarePreMoved = blackQueenDarkSquarePreMoved;
	}

	public BufferedImage getBlackRookDarkSquarePreMoved() {
		return blackRookDarkSquarePreMoved;
	}

	public void setBlackRookDarkSquarePreMoved(BufferedImage blackRookDarkSquarePreMoved) {
		this.blackRookDarkSquarePreMoved = blackRookDarkSquarePreMoved;
	}

	public BufferedImage getWhiteBishopDarkSquarePreMoved() {
		return whiteBishopDarkSquarePreMoved;
	}

	public void setWhiteBishopDarkSquarePreMoved(BufferedImage whiteBishopDarkSquarePreMoved) {
		this.whiteBishopDarkSquarePreMoved = whiteBishopDarkSquarePreMoved;
	}

	public BufferedImage getWhiteKingDarkSquarePreMoved() {
		return whiteKingDarkSquarePreMoved;
	}

	public void setWhiteKingDarkSquarePreMoved(BufferedImage whiteKingDarkSquarePreMoved) {
		this.whiteKingDarkSquarePreMoved = whiteKingDarkSquarePreMoved;
	}

	public BufferedImage getWhiteKnightDarkSquarePreMoved() {
		return whiteKnightDarkSquarePreMoved;
	}

	public void setWhiteKnightDarkSquarePreMoved(BufferedImage whiteKnightDarkSquarePreMoved) {
		this.whiteKnightDarkSquarePreMoved = whiteKnightDarkSquarePreMoved;
	}

	public BufferedImage getWhitePawnDarkSquarePreMoved() {
		return whitePawnDarkSquarePreMoved;
	}

	public void setWhitePawnDarkSquarePreMoved(BufferedImage whitePawnDarkSquarePreMoved) {
		this.whitePawnDarkSquarePreMoved = whitePawnDarkSquarePreMoved;
	}

	public BufferedImage getWhiteQueenDarkSquarePreMoved() {
		return whiteQueenDarkSquarePreMoved;
	}

	public void setWhiteQueenDarkSquarePreMoved(BufferedImage whiteQueenDarkSquarePreMoved) {
		this.whiteQueenDarkSquarePreMoved = whiteQueenDarkSquarePreMoved;
	}

	public BufferedImage getWhiteRookDarkSquarePreMoved() {
		return whiteRookDarkSquarePreMoved;
	}

	public void setWhiteRookDarkSquarePreMoved(BufferedImage whiteRookDarkSquarePreMoved) {
		this.whiteRookDarkSquarePreMoved = whiteRookDarkSquarePreMoved;
	}

	public BufferedImage getBlackBishopLightSquarePreMoved() {
		return blackBishopLightSquarePreMoved;
	}

	public void setBlackBishopLightSquarePreMoved(BufferedImage blackBishopLightSquarePreMoved) {
		this.blackBishopLightSquarePreMoved = blackBishopLightSquarePreMoved;
	}

	public BufferedImage getBlackKingLightSquarePreMoved() {
		return blackKingLightSquarePreMoved;
	}

	public void setBlackKingLightSquarePreMoved(BufferedImage blackKingLightSquarePreMoved) {
		this.blackKingLightSquarePreMoved = blackKingLightSquarePreMoved;
	}

	public BufferedImage getBlackKnightLightSquarePreMoved() {
		return blackKnightLightSquarePreMoved;
	}

	public void setBlackKnightLightSquarePreMoved(BufferedImage blackKnightLightSquarePreMoved) {
		this.blackKnightLightSquarePreMoved = blackKnightLightSquarePreMoved;
	}

	public BufferedImage getBlackPawnLightSquarePreMoved() {
		return blackPawnLightSquarePreMoved;
	}

	public void setBlackPawnLightSquarePreMoved(BufferedImage blackPawnLightSquarePreMoved) {
		this.blackPawnLightSquarePreMoved = blackPawnLightSquarePreMoved;
	}

	public BufferedImage getBlackQueenLightSquarePreMoved() {
		return blackQueenLightSquarePreMoved;
	}

	public void setBlackQueenLightSquarePreMoved(BufferedImage blackQueenLightSquarePreMoved) {
		this.blackQueenLightSquarePreMoved = blackQueenLightSquarePreMoved;
	}

	public BufferedImage getBlackRookLightSquarePreMoved() {
		return blackRookLightSquarePreMoved;
	}

	public void setBlackRookLightSquarePreMoved(BufferedImage blackRookLightSquarePreMoved) {
		this.blackRookLightSquarePreMoved = blackRookLightSquarePreMoved;
	}

	public BufferedImage getWhiteBishopLightSquarePreMoved() {
		return whiteBishopLightSquarePreMoved;
	}

	public void setWhiteBishopLightSquarePreMoved(BufferedImage whiteBishopLightSquarePreMoved) {
		this.whiteBishopLightSquarePreMoved = whiteBishopLightSquarePreMoved;
	}

	public BufferedImage getWhiteKingLightSquarePreMoved() {
		return whiteKingLightSquarePreMoved;
	}

	public void setWhiteKingLightSquarePreMoved(BufferedImage whiteKingLightSquarePreMoved) {
		this.whiteKingLightSquarePreMoved = whiteKingLightSquarePreMoved;
	}

	public BufferedImage getWhiteKnightLightSquarePreMoved() {
		return whiteKnightLightSquarePreMoved;
	}

	public void setWhiteKnightLightSquarePreMoved(BufferedImage whiteKnightLightSquarePreMoved) {
		this.whiteKnightLightSquarePreMoved = whiteKnightLightSquarePreMoved;
	}

	public BufferedImage getWhitePawnLightSquarePreMoved() {
		return whitePawnLightSquarePreMoved;
	}

	public void setWhitePawnLightSquarePreMoved(BufferedImage whitePawnLightSquarePreMoved) {
		this.whitePawnLightSquarePreMoved = whitePawnLightSquarePreMoved;
	}

	public BufferedImage getWhiteQueenLightSquarePreMoved() {
		return whiteQueenLightSquarePreMoved;
	}

	public void setWhiteQueenLightSquarePreMoved(BufferedImage whiteQueenLightSquarePreMoved) {
		this.whiteQueenLightSquarePreMoved = whiteQueenLightSquarePreMoved;
	}

	public BufferedImage getWhiteRookLightSquarePreMoved() {
		return whiteRookLightSquarePreMoved;
	}

	public void setWhiteRookLightSquarePreMoved(BufferedImage whiteRookLightSquarePreMoved) {
		this.whiteRookLightSquarePreMoved = whiteRookLightSquarePreMoved;
	}
	
	
	
	
	
	

}

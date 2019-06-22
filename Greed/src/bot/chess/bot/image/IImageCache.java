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

public interface IImageCache {
	
	void refresh();

	int getCellGap();

	int getCellWidth();

	int getCellHeight();

	Point adjustUpperLeftCornerPoint(Point p);
	
	Point getPiecePoint(Point corner, int square);

	//////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////             UNSELECTED  			       ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	BufferedImage getUpperLeftCorner();

	BufferedImage getEmptyDarkSquare();

	BufferedImage getEmptyLightSquare();

	/**
	 * Black Pieces, Dark Squares
	 */
	BufferedImage getBlackBishopDarkSquare();

	BufferedImage getBlackKingDarkSquare();

	BufferedImage getBlackKnightDarkSquare();

	BufferedImage getBlackPawnDarkSquare();

	BufferedImage getBlackQueenDarkSquare();

	BufferedImage getBlackRookDarkSquare();

	/**
	 * White Pieces, Dark Squares
	 */
	BufferedImage getWhiteBishopDarkSquare();

	BufferedImage getWhiteKingDarkSquare();

	BufferedImage getWhiteKnightDarkSquare();

	BufferedImage getWhitePawnDarkSquare();

	BufferedImage getWhiteQueenDarkSquare();

	BufferedImage getWhiteRookDarkSquare();

	/**
	 * Black Pieces, Light Squares
	 */
	BufferedImage getBlackBishopLightSquare();

	BufferedImage getBlackKingLightSquare();

	BufferedImage getBlackKnightLightSquare();

	BufferedImage getBlackPawnLightSquare();

	BufferedImage getBlackQueenLightSquare();

	BufferedImage getBlackRookLightSquare();

	/**
	 * White Pieces, Light Squares
	 */
	BufferedImage getWhiteBishopLightSquare();

	BufferedImage getWhiteKingLightSquare();

	BufferedImage getWhiteKnightLightSquare();

	BufferedImage getWhitePawnLightSquare();

	BufferedImage getWhiteQueenLightSquare();

	BufferedImage getWhiteRookLightSquare();
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////             SELECTED  			       	   ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////

	BufferedImage getUpperLeftCornerSelected();
	
	BufferedImage getEmptyDarkSquareSelected();

	BufferedImage getEmptyLightSquareSelected();

	/**
	 * Black Pieces, Dark Squares
	 */
	BufferedImage getBlackBishopDarkSquareSelected();

	BufferedImage getBlackKingDarkSquareSelected();

	BufferedImage getBlackKnightDarkSquareSelected();

	BufferedImage getBlackPawnDarkSquareSelected();

	BufferedImage getBlackQueenDarkSquareSelected();

	BufferedImage getBlackRookDarkSquareSelected();

	/**
	 * White Pieces, Dark Squares
	 */
	BufferedImage getWhiteBishopDarkSquareSelected();

	BufferedImage getWhiteKingDarkSquareSelected();

	BufferedImage getWhiteKnightDarkSquareSelected();

	BufferedImage getWhitePawnDarkSquareSelected();

	BufferedImage getWhiteQueenDarkSquareSelected();

	BufferedImage getWhiteRookDarkSquareSelected();

	/**
	 * Black Pieces, Light Squares
	 */
	BufferedImage getBlackBishopLightSquareSelected();

	BufferedImage getBlackKingLightSquareSelected();

	BufferedImage getBlackKnightLightSquareSelected();

	BufferedImage getBlackPawnLightSquareSelected();

	BufferedImage getBlackQueenLightSquareSelected();

	BufferedImage getBlackRookLightSquareSelected();

	/**
	 * White Pieces, Light Squares
	 */
	BufferedImage getWhiteBishopLightSquareSelected();

	BufferedImage getWhiteKingLightSquareSelected();

	BufferedImage getWhiteKnightLightSquareSelected();

	BufferedImage getWhitePawnLightSquareSelected();

	BufferedImage getWhiteQueenLightSquareSelected();

	BufferedImage getWhiteRookLightSquareSelected();
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////             HIGHLIGTED			       	   ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////

	BufferedImage getUpperLeftCornerHighlighted();
	
	BufferedImage getEmptyDarkSquareHighlighted();

	BufferedImage getEmptyLightSquareHighlighted();

	/**
	 * Black Pieces, Dark Squares
	 */
	BufferedImage getBlackBishopDarkSquareHighlighted();

	BufferedImage getBlackKingDarkSquareHighlighted();

	BufferedImage getBlackKnightDarkSquareHighlighted();

	BufferedImage getBlackPawnDarkSquareHighlighted();

	BufferedImage getBlackQueenDarkSquareHighlighted();

	BufferedImage getBlackRookDarkSquareHighlighted();

	/**
	 * White Pieces, Dark Squares
	 */
	BufferedImage getWhiteBishopDarkSquareHighlighted();

	BufferedImage getWhiteKingDarkSquareHighlighted();

	BufferedImage getWhiteKnightDarkSquareHighlighted();

	BufferedImage getWhitePawnDarkSquareHighlighted();

	BufferedImage getWhiteQueenDarkSquareHighlighted();

	BufferedImage getWhiteRookDarkSquareHighlighted();

	/**
	 * Black Pieces, Light Squares
	 */
	BufferedImage getBlackBishopLightSquareHighlighted();

	BufferedImage getBlackKingLightSquareHighlighted();

	BufferedImage getBlackKnightLightSquareHighlighted();

	BufferedImage getBlackPawnLightSquareHighlighted();

	BufferedImage getBlackQueenLightSquareHighlighted();

	BufferedImage getBlackRookLightSquareHighlighted();

	/**
	 * White Pieces, Light Squares
	 */
	BufferedImage getWhiteBishopLightSquareHighlighted();

	BufferedImage getWhiteKingLightSquareHighlighted();

	BufferedImage getWhiteKnightLightSquareHighlighted();

	BufferedImage getWhitePawnLightSquareHighlighted();

	BufferedImage getWhiteQueenLightSquareHighlighted();

	BufferedImage getWhiteRookLightSquareHighlighted();
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////             CHECKED			       	   ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	BufferedImage getBlackKingDarkSquareChecked();
	BufferedImage getBlackKingLightSquareChecked();
	
	BufferedImage getWhiteKingDarkSquareChecked();
	BufferedImage getWhiteKingLightSquareChecked();
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////             PREMOVED			       	   ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////

	BufferedImage getUpperLeftCornerPreMoved();
	
	BufferedImage getEmptyDarkSquarePreMoved();

	BufferedImage getEmptyLightSquarePreMoved();

	/**
	 * Black Pieces, Dark Squares
	 */
	BufferedImage getBlackBishopDarkSquarePreMoved();

	BufferedImage getBlackKingDarkSquarePreMoved();

	BufferedImage getBlackKnightDarkSquarePreMoved();

	BufferedImage getBlackPawnDarkSquarePreMoved();

	BufferedImage getBlackQueenDarkSquarePreMoved();

	BufferedImage getBlackRookDarkSquarePreMoved();

	/**
	 * White Pieces, Dark Squares
	 */
	BufferedImage getWhiteBishopDarkSquarePreMoved();

	BufferedImage getWhiteKingDarkSquarePreMoved();

	BufferedImage getWhiteKnightDarkSquarePreMoved();

	BufferedImage getWhitePawnDarkSquarePreMoved();

	BufferedImage getWhiteQueenDarkSquarePreMoved();

	BufferedImage getWhiteRookDarkSquarePreMoved();

	/**
	 * Black Pieces, Light Squares
	 */
	BufferedImage getBlackBishopLightSquarePreMoved();

	BufferedImage getBlackKingLightSquarePreMoved();

	BufferedImage getBlackKnightLightSquarePreMoved();

	BufferedImage getBlackPawnLightSquarePreMoved();

	BufferedImage getBlackQueenLightSquarePreMoved();

	BufferedImage getBlackRookLightSquarePreMoved();

	/**
	 * White Pieces, Light Squares
	 */
	BufferedImage getWhiteBishopLightSquarePreMoved();

	BufferedImage getWhiteKingLightSquarePreMoved();

	BufferedImage getWhiteKnightLightSquarePreMoved();

	BufferedImage getWhitePawnLightSquarePreMoved();

	BufferedImage getWhiteQueenLightSquarePreMoved();

	BufferedImage getWhiteRookLightSquarePreMoved();
	
}

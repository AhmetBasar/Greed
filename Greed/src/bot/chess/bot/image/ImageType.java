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

import chess.engine.EngineConstants;

public enum ImageType {

	
	//////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////             UNSELECTED  			       ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	UPPER_LEFT_CORNER("upperLeftCorner", EngineConstants.BLANK),
	/***/
	EMPTY_DARK_SQUARE("emptyDarkSquare", EngineConstants.BLANK),
	/***/
	EMPTY_LIGHT_SQUARE("emptyLightSquare", EngineConstants.BLANK),
	/***/

	/**
	 * Black Pieces, Dark Squares
	 */
	BLACK_BISHOP_DARK_SQUARE("blackBishopDarkSquare", (byte) (EngineConstants.BLACK | EngineConstants.BISHOP)),
	/***/
	BLACK_KING_DARK_SQUARE("blackKingDarkSquare", (byte) (EngineConstants.BLACK | EngineConstants.KING)),
	/***/
	BLACK_KNIGHT_DARK_SQUARE("blackKnightDarkSquare", (byte) (EngineConstants.BLACK | EngineConstants.KNIGHT)),
	/***/
	BLACK_PAWN_DARK_SQUARE("blackPawnDarkSquare", (byte) (EngineConstants.BLACK | EngineConstants.PAWN)),
	/***/
	BLACK_QUEEN_DARK_SQUARE("blackQueenDarkSquare", (byte) (EngineConstants.BLACK | EngineConstants.QUEEN)),
	/***/
	BLACK_ROOK_DARK_SQUARE("blackRookDarkSquare", (byte) (EngineConstants.BLACK | EngineConstants.ROOK)),
	/***/

	/**
	 * White Pieces, Dark Squares
	 */
	WHITE_BISHOP_DARK_SQUARE("whiteBishopDarkSquare", (byte) (EngineConstants.WHITE | EngineConstants.BISHOP)),
	/***/
	WHITE_KING_DARK_SQUARE("whiteKingDarkSquare", (byte) (EngineConstants.WHITE | EngineConstants.KING)),
	/***/
	WHITE_KNIGHT_DARK_SQUARE("whiteKnightDarkSquare", (byte) (EngineConstants.WHITE | EngineConstants.KNIGHT)),
	/***/
	WHITE_PAWN_DARK_SQUARE("whitePawnDarkSquare", (byte) (EngineConstants.WHITE | EngineConstants.PAWN)),
	/***/
	WHITE_QUEEN_DARK_SQUARE("whiteQueenDarkSquare", (byte) (EngineConstants.WHITE | EngineConstants.QUEEN)),
	/***/
	WHITE_ROOK_DARK_SQUARE("whiteRookDarkSquare", (byte) (EngineConstants.WHITE | EngineConstants.ROOK)),
	/***/

	/**
	 * Black Pieces, Light Squares
	 */
	BLACK_BISHOP_LIGHT_SQUARE("blackBishopLightSquare", (byte) (EngineConstants.BLACK | EngineConstants.BISHOP)),
	/***/
	BLACK_KING_LIGHT_SQUARE("blackKingLightSquare", (byte) (EngineConstants.BLACK | EngineConstants.KING)),
	/***/
	BLACK_KNIGHT_LIGHT_SQUARE("blackKnightLightSquare", (byte) (EngineConstants.BLACK | EngineConstants.KNIGHT)),
	/***/
	BLACK_PAWN_LIGHT_SQUARE("blackPawnLightSquare", (byte) (EngineConstants.BLACK | EngineConstants.PAWN)),
	/***/
	BLACK_QUEEN_LIGHT_SQUARE("blackQueenLightSquare", (byte) (EngineConstants.BLACK | EngineConstants.QUEEN)),
	/***/
	BLACK_ROOK_LIGHT_SQUARE("blackRookLightSquare", (byte) (EngineConstants.BLACK | EngineConstants.ROOK)),
	/***/

	/**
	 * White Pieces, Light Squares
	 */
	WHITE_BISHOP_LIGHT_SQUARE("whiteBishopLightSquare", (byte) (EngineConstants.WHITE | EngineConstants.BISHOP)),
	/***/
	WHITE_KING_LIGHT_SQUARE("whiteKingLightSquare", (byte) (EngineConstants.WHITE | EngineConstants.KING)),
	/***/
	WHITE_KNIGHT_LIGHT_SQUARE("whiteKnightLightSquare", (byte) (EngineConstants.WHITE | EngineConstants.KNIGHT)),
	/***/
	WHITE_PAWN_LIGHT_SQUARE("whitePawnLightSquare", (byte) (EngineConstants.WHITE | EngineConstants.PAWN)),
	/***/
	WHITE_QUEEN_LIGHT_SQUARE("whiteQueenLightSquare", (byte) (EngineConstants.WHITE | EngineConstants.QUEEN)),
	/***/
	WHITE_ROOK_LIGHT_SQUARE("whiteRookLightSquare", (byte) (EngineConstants.WHITE | EngineConstants.ROOK)),
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////             SELECTED  			       	   ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	UPPER_LEFT_CORNER_SELECTED("upperLeftCornerSelected", EngineConstants.BLANK),
	/***/
	EMPTY_DARK_SQUARE_SELECTED("emptyDarkSquareSelected", EngineConstants.BLANK),
	/***/
	EMPTY_LIGHT_SQUARE_SELECTED("emptyLightSquareSelected", EngineConstants.BLANK),
	/***/

	/**
	 * Black Pieces, Dark Squares
	 */
	BLACK_BISHOP_DARK_SQUARE_SELECTED("blackBishopDarkSquareSelected", (byte) (EngineConstants.BLACK | EngineConstants.BISHOP)),
	/***/
	BLACK_KING_DARK_SQUARE_SELECTED("blackKingDarkSquareSelected", (byte) (EngineConstants.BLACK | EngineConstants.KING)),
	/***/
	BLACK_KNIGHT_DARK_SQUARE_SELECTED("blackKnightDarkSquareSelected", (byte) (EngineConstants.BLACK | EngineConstants.KNIGHT)),
	/***/
	BLACK_PAWN_DARK_SQUARE_SELECTED("blackPawnDarkSquareSelected", (byte) (EngineConstants.BLACK | EngineConstants.PAWN)),
	/***/
	BLACK_QUEEN_DARK_SQUARE_SELECTED("blackQueenDarkSquareSelected", (byte) (EngineConstants.BLACK | EngineConstants.QUEEN)),
	/***/
	BLACK_ROOK_DARK_SQUARE_SELECTED("blackRookDarkSquareSelected", (byte) (EngineConstants.BLACK | EngineConstants.ROOK)),
	/***/

	/**
	 * White Pieces, Dark Squares
	 */
	WHITE_BISHOP_DARK_SQUARE_SELECTED("whiteBishopDarkSquareSelected", (byte) (EngineConstants.WHITE | EngineConstants.BISHOP)),
	/***/
	WHITE_KING_DARK_SQUARE_SELECTED("whiteKingDarkSquareSelected", (byte) (EngineConstants.WHITE | EngineConstants.KING)),
	/***/
	WHITE_KNIGHT_DARK_SQUARE_SELECTED("whiteKnightDarkSquareSelected", (byte) (EngineConstants.WHITE | EngineConstants.KNIGHT)),
	/***/
	WHITE_PAWN_DARK_SQUARE_SELECTED("whitePawnDarkSquareSelected", (byte) (EngineConstants.WHITE | EngineConstants.PAWN)),
	/***/
	WHITE_QUEEN_DARK_SQUARE_SELECTED("whiteQueenDarkSquareSelected", (byte) (EngineConstants.WHITE | EngineConstants.QUEEN)),
	/***/
	WHITE_ROOK_DARK_SQUARE_SELECTED("whiteRookDarkSquareSelected", (byte) (EngineConstants.WHITE | EngineConstants.ROOK)),
	/***/

	/**
	 * Black Pieces, Light Squares
	 */
	BLACK_BISHOP_LIGHT_SQUARE_SELECTED("blackBishopLightSquareSelected", (byte) (EngineConstants.BLACK | EngineConstants.BISHOP)),
	/***/
	BLACK_KING_LIGHT_SQUARE_SELECTED("blackKingLightSquareSelected", (byte) (EngineConstants.BLACK | EngineConstants.KING)),
	/***/
	BLACK_KNIGHT_LIGHT_SQUARE_SELECTED("blackKnightLightSquareSelected", (byte) (EngineConstants.BLACK | EngineConstants.KNIGHT)),
	/***/
	BLACK_PAWN_LIGHT_SQUARE_SELECTED("blackPawnLightSquareSelected", (byte) (EngineConstants.BLACK | EngineConstants.PAWN)),
	/***/
	BLACK_QUEEN_LIGHT_SQUARE_SELECTED("blackQueenLightSquareSelected", (byte) (EngineConstants.BLACK | EngineConstants.QUEEN)),
	/***/
	BLACK_ROOK_LIGHT_SQUARE_SELECTED("blackRookLightSquareSelected", (byte) (EngineConstants.BLACK | EngineConstants.ROOK)),
	/***/

	/**
	 * White Pieces, Light Squares
	 */
	WHITE_BISHOP_LIGHT_SQUARE_SELECTED("whiteBishopLightSquareSelected", (byte) (EngineConstants.WHITE | EngineConstants.BISHOP)),
	/***/
	WHITE_KING_LIGHT_SQUARE_SELECTED("whiteKingLightSquareSelected", (byte) (EngineConstants.WHITE | EngineConstants.KING)),
	/***/
	WHITE_KNIGHT_LIGHT_SQUARE_SELECTED("whiteKnightLightSquareSelected", (byte) (EngineConstants.WHITE | EngineConstants.KNIGHT)),
	/***/
	WHITE_PAWN_LIGHT_SQUARE_SELECTED("whitePawnLightSquareSelected", (byte) (EngineConstants.WHITE | EngineConstants.PAWN)),
	/***/
	WHITE_QUEEN_LIGHT_SQUARE_SELECTED("whiteQueenLightSquareSelected", (byte) (EngineConstants.WHITE | EngineConstants.QUEEN)),
	/***/
	WHITE_ROOK_LIGHT_SQUARE_SELECTED("whiteRookLightSquareSelected", (byte) (EngineConstants.WHITE | EngineConstants.ROOK)),
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////             HIGHLIGHTED			       	   ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	UPPER_LEFT_CORNER_HIGHLIGHTED("upperLeftCornerHighlighted", EngineConstants.BLANK),
	/***/
	EMPTY_DARK_SQUARE_HIGHLIGHTED("emptyDarkSquareHighlighted", EngineConstants.BLANK),
	/***/
	EMPTY_LIGHT_SQUARE_HIGHLIGHTED("emptyLightSquareHighlighted", EngineConstants.BLANK),
	/***/

	/**
	 * Black Pieces, Dark Squares
	 */
	BLACK_BISHOP_DARK_SQUARE_HIGHLIGHTED("blackBishopDarkSquareHighlighted", (byte) (EngineConstants.BLACK | EngineConstants.BISHOP)),
	/***/
	BLACK_KING_DARK_SQUARE_HIGHLIGHTED("blackKingDarkSquareHighlighted", (byte) (EngineConstants.BLACK | EngineConstants.KING)),
	/***/
	BLACK_KNIGHT_DARK_SQUARE_HIGHLIGHTED("blackKnightDarkSquareHighlighted", (byte) (EngineConstants.BLACK | EngineConstants.KNIGHT)),
	/***/
	BLACK_PAWN_DARK_SQUARE_HIGHLIGHTED("blackPawnDarkSquareHighlighted", (byte) (EngineConstants.BLACK | EngineConstants.PAWN)),
	/***/
	BLACK_QUEEN_DARK_SQUARE_HIGHLIGHTED("blackQueenDarkSquareHighlighted", (byte) (EngineConstants.BLACK | EngineConstants.QUEEN)),
	/***/
	BLACK_ROOK_DARK_SQUARE_HIGHLIGHTED("blackRookDarkSquareHighlighted", (byte) (EngineConstants.BLACK | EngineConstants.ROOK)),
	/***/

	/**
	 * White Pieces, Dark Squares
	 */
	WHITE_BISHOP_DARK_SQUARE_HIGHLIGHTED("whiteBishopDarkSquareHighlighted", (byte) (EngineConstants.WHITE | EngineConstants.BISHOP)),
	/***/
	WHITE_KING_DARK_SQUARE_HIGHLIGHTED("whiteKingDarkSquareHighlighted", (byte) (EngineConstants.WHITE | EngineConstants.KING)),
	/***/
	WHITE_KNIGHT_DARK_SQUARE_HIGHLIGHTED("whiteKnightDarkSquareHighlighted", (byte) (EngineConstants.WHITE | EngineConstants.KNIGHT)),
	/***/
	WHITE_PAWN_DARK_SQUARE_HIGHLIGHTED("whitePawnDarkSquareHighlighted", (byte) (EngineConstants.WHITE | EngineConstants.PAWN)),
	/***/
	WHITE_QUEEN_DARK_SQUARE_HIGHLIGHTED("whiteQueenDarkSquareHighlighted", (byte) (EngineConstants.WHITE | EngineConstants.QUEEN)),
	/***/
	WHITE_ROOK_DARK_SQUARE_HIGHLIGHTED("whiteRookDarkSquareHighlighted", (byte) (EngineConstants.WHITE | EngineConstants.ROOK)),
	/***/

	/**
	 * Black Pieces, Light Squares
	 */
	BLACK_BISHOP_LIGHT_SQUARE_HIGHLIGHTED("blackBishopLightSquareHighlighted", (byte) (EngineConstants.BLACK | EngineConstants.BISHOP)),
	/***/
	BLACK_KING_LIGHT_SQUARE_HIGHLIGHTED("blackKingLightSquareHighlighted", (byte) (EngineConstants.BLACK | EngineConstants.KING)),
	/***/
	BLACK_KNIGHT_LIGHT_SQUARE_HIGHLIGHTED("blackKnightLightSquareHighlighted", (byte) (EngineConstants.BLACK | EngineConstants.KNIGHT)),
	/***/
	BLACK_PAWN_LIGHT_SQUARE_HIGHLIGHTED("blackPawnLightSquareHighlighted", (byte) (EngineConstants.BLACK | EngineConstants.PAWN)),
	/***/
	BLACK_QUEEN_LIGHT_SQUARE_HIGHLIGHTED("blackQueenLightSquareHighlighted", (byte) (EngineConstants.BLACK | EngineConstants.QUEEN)),
	/***/
	BLACK_ROOK_LIGHT_SQUARE_HIGHLIGHTED("blackRookLightSquareHighlighted", (byte) (EngineConstants.BLACK | EngineConstants.ROOK)),
	/***/

	/**
	 * White Pieces, Light Squares
	 */
	WHITE_BISHOP_LIGHT_SQUARE_HIGHLIGHTED("whiteBishopLightSquareHighlighted", (byte) (EngineConstants.WHITE | EngineConstants.BISHOP)),
	/***/
	WHITE_KING_LIGHT_SQUARE_HIGHLIGHTED("whiteKingLightSquareHighlighted", (byte) (EngineConstants.WHITE | EngineConstants.KING)),
	/***/
	WHITE_KNIGHT_LIGHT_SQUARE_HIGHLIGHTED("whiteKnightLightSquareHighlighted", (byte) (EngineConstants.WHITE | EngineConstants.KNIGHT)),
	/***/
	WHITE_PAWN_LIGHT_SQUARE_HIGHLIGHTED("whitePawnLightSquareHighlighted", (byte) (EngineConstants.WHITE | EngineConstants.PAWN)),
	/***/
	WHITE_QUEEN_LIGHT_SQUARE_HIGHLIGHTED("whiteQueenLightSquareHighlighted", (byte) (EngineConstants.WHITE | EngineConstants.QUEEN)),
	/***/
	WHITE_ROOK_LIGHT_SQUARE_HIGHLIGHTED("whiteRookLightSquareHighlighted", (byte) (EngineConstants.WHITE | EngineConstants.ROOK)),
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////             CHECKED			       	   ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	BLACK_KING_DARK_SQUARE_CHECKED("blackKingDarkSquareChecked", (byte) (EngineConstants.BLACK | EngineConstants.KING)),
	BLACK_KING_LIGHT_SQUARE_CHECKED("blackKingLightSquareChecked", (byte) (EngineConstants.BLACK | EngineConstants.KING)),
	
	WHITE_KING_DARK_SQUARE_CHECKED("whiteKingDarkSquareChecked", (byte) (EngineConstants.WHITE | EngineConstants.KING)),
	WHITE_KING_LIGHT_SQUARE_CHECKED("whiteKingLightSquareChecked", (byte) (EngineConstants.WHITE | EngineConstants.KING)),
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////             PREMOVED			       	   ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	UPPER_LEFT_CORNER_PREMOVED("upperLeftCornerPreMoved", EngineConstants.BLANK),
	/***/
	EMPTY_DARK_SQUARE_PREMOVED("emptyDarkSquarePreMoved", EngineConstants.BLANK),
	/***/
	EMPTY_LIGHT_SQUARE_PREMOVED("emptyLightSquarePreMoved", EngineConstants.BLANK),
	/***/

	/**
	 * Black Pieces, Dark Squares
	 */
	BLACK_BISHOP_DARK_SQUARE_PREMOVED("blackBishopDarkSquarePreMoved", (byte) (EngineConstants.BLACK | EngineConstants.BISHOP)),
	/***/
	BLACK_KING_DARK_SQUARE_PREMOVED("blackKingDarkSquarePreMoved", (byte) (EngineConstants.BLACK | EngineConstants.KING)),
	/***/
	BLACK_KNIGHT_DARK_SQUARE_PREMOVED("blackKnightDarkSquarePreMoved", (byte) (EngineConstants.BLACK | EngineConstants.KNIGHT)),
	/***/
	BLACK_PAWN_DARK_SQUARE_PREMOVED("blackPawnDarkSquarePreMoved", (byte) (EngineConstants.BLACK | EngineConstants.PAWN)),
	/***/
	BLACK_QUEEN_DARK_SQUARE_PREMOVED("blackQueenDarkSquarePreMoved", (byte) (EngineConstants.BLACK | EngineConstants.QUEEN)),
	/***/
	BLACK_ROOK_DARK_SQUARE_PREMOVED("blackRookDarkSquarePreMoved", (byte) (EngineConstants.BLACK | EngineConstants.ROOK)),
	/***/

	/**
	 * White Pieces, Dark Squares
	 */
	WHITE_BISHOP_DARK_SQUARE_PREMOVED("whiteBishopDarkSquarePreMoved", (byte) (EngineConstants.WHITE | EngineConstants.BISHOP)),
	/***/
	WHITE_KING_DARK_SQUARE_PREMOVED("whiteKingDarkSquarePreMoved", (byte) (EngineConstants.WHITE | EngineConstants.KING)),
	/***/
	WHITE_KNIGHT_DARK_SQUARE_PREMOVED("whiteKnightDarkSquarePreMoved", (byte) (EngineConstants.WHITE | EngineConstants.KNIGHT)),
	/***/
	WHITE_PAWN_DARK_SQUARE_PREMOVED("whitePawnDarkSquarePreMoved", (byte) (EngineConstants.WHITE | EngineConstants.PAWN)),
	/***/
	WHITE_QUEEN_DARK_SQUARE_PREMOVED("whiteQueenDarkSquarePreMoved", (byte) (EngineConstants.WHITE | EngineConstants.QUEEN)),
	/***/
	WHITE_ROOK_DARK_SQUARE_PREMOVED("whiteRookDarkSquarePreMoved", (byte) (EngineConstants.WHITE | EngineConstants.ROOK)),
	/***/

	/**
	 * Black Pieces, Light Squares
	 */
	BLACK_BISHOP_LIGHT_SQUARE_PREMOVED("blackBishopLightSquarePreMoved", (byte) (EngineConstants.BLACK | EngineConstants.BISHOP)),
	/***/
	BLACK_KING_LIGHT_SQUARE_PREMOVED("blackKingLightSquarePreMoved", (byte) (EngineConstants.BLACK | EngineConstants.KING)),
	/***/
	BLACK_KNIGHT_LIGHT_SQUARE_PREMOVED("blackKnightLightSquarePreMoved", (byte) (EngineConstants.BLACK | EngineConstants.KNIGHT)),
	/***/
	BLACK_PAWN_LIGHT_SQUARE_PREMOVED("blackPawnLightSquarePreMoved", (byte) (EngineConstants.BLACK | EngineConstants.PAWN)),
	/***/
	BLACK_QUEEN_LIGHT_SQUARE_PREMOVED("blackQueenLightSquarePreMoved", (byte) (EngineConstants.BLACK | EngineConstants.QUEEN)),
	/***/
	BLACK_ROOK_LIGHT_SQUARE_PREMOVED("blackRookLightSquarePreMoved", (byte) (EngineConstants.BLACK | EngineConstants.ROOK)),
	/***/

	/**
	 * White Pieces, Light Squares
	 */
	WHITE_BISHOP_LIGHT_SQUARE_PREMOVED("whiteBishopLightSquarePreMoved", (byte) (EngineConstants.WHITE | EngineConstants.BISHOP)),
	/***/
	WHITE_KING_LIGHT_SQUARE_PREMOVED("whiteKingLightSquarePreMoved", (byte) (EngineConstants.WHITE | EngineConstants.KING)),
	/***/
	WHITE_KNIGHT_LIGHT_SQUARE_PREMOVED("whiteKnightLightSquarePreMoved", (byte) (EngineConstants.WHITE | EngineConstants.KNIGHT)),
	/***/
	WHITE_PAWN_LIGHT_SQUARE_PREMOVED("whitePawnLightSquarePreMoved", (byte) (EngineConstants.WHITE | EngineConstants.PAWN)),
	/***/
	WHITE_QUEEN_LIGHT_SQUARE_PREMOVED("whiteQueenLightSquarePreMoved", (byte) (EngineConstants.WHITE | EngineConstants.QUEEN)),
	/***/
	WHITE_ROOK_LIGHT_SQUARE_PREMOVED("whiteRookLightSquarePreMoved", (byte) (EngineConstants.WHITE | EngineConstants.ROOK));
	

	private String fieldName;
	private byte pieceType;

	private ImageType(String fieldName, byte pieceType) {
		this.fieldName = fieldName;
		this.pieceType = pieceType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public byte getPieceType() {
		return pieceType;
	}

}

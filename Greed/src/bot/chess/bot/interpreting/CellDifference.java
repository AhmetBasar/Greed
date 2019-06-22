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
package chess.bot.interpreting;

import chess.engine.EngineConstants;

public class CellDifference {

	private byte previousItem;
	private byte currentItem;
	private CellDifferenceType diffType;
	private CellCoordinate cellCoordinate;

	enum CellDifferenceType {
		CHARGE, DISCHARGE, CHANGE;
	}

	public CellDifference(byte previousItem, byte currentItem, CellCoordinate cellCoordinate) {
		this.previousItem = previousItem;
		this.currentItem = currentItem;
		this.cellCoordinate = cellCoordinate;
		if (previousItem == EngineConstants.BLANK) {
			diffType = CellDifferenceType.CHARGE;
		} else if (currentItem == EngineConstants.BLANK) {
			diffType = CellDifferenceType.DISCHARGE;
		} else {
			diffType = CellDifferenceType.CHANGE;
		}
	}

	public byte getPreviousItem() {
		return previousItem;
	}

	public byte getCurrentItem() {
		return currentItem;
	}

	public CellDifferenceType getDiffType() {
		return diffType;
	}

	public CellCoordinate getCellCoordinate() {
		return cellCoordinate;
	}

}

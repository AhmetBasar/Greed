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
package chess.gui;

import chess.gui.BaseGui;
import java.awt.MouseInfo;

public class BoardCell extends Cell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BaseGui base;

	public BoardCell(BaseGui base) {
		super(base);
		this.base = base;
		setExistsBorder(true);
	}

	@Override
	public void mausePressed() {
		if (base.getControlPanel().isEditMode()) {
			startEditMode();
		} else {
			if (isSelected()) {
				setSelected(false);
			} else {
				if (base.getSelectedCell() == null) {
					if (isProperToSelect()) {
						setSelected(true);
					}
				} else {
					if (isProperToSelect()) {
						base.getSelectedCell().setSelected(false);
						setSelected(true);
					} else {
						int validMove = getValidMove();
						if (validMove != 0) {
							doMove(validMove);
						}
					}
				}
			}
		}
	}

	private void doMove(int move) {
		base.getGamePlay().doMove(move);
		base.getSelectedCell().setSelected(false);
	}

	private int getValidMove() {
		return base.getGamePlay().getValidMove(base.getSelectedCell().getNumericName(),
				getNumericName());
	}

	private boolean isProperToSelect() {
		boolean properToSelect = false;
		if (!isEmptyItem() && (base.getGamePlay().isWhitesTurn() && isWhiteItem())
				|| (base.getGamePlay().isBlacksTurn() && isBlackItem())) {
			properToSelect = true;

		}
		return properToSelect;
	}

	public void startEditMode() {
		base.getEditModePanel().setBaseCell(this);
		base.getEditModeFrame().setLocation((int) MouseInfo.getPointerInfo().getLocation().getX(),
				(int) MouseInfo.getPointerInfo().getLocation().getY());
		base.getEditModeFrame().setVisible(true);
	}
}

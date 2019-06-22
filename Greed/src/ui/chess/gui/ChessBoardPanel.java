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

import chess.engine.EngineConstants;
import chess.gui.BaseGui;

import java.util.HashMap;

public class ChessBoardPanel extends javax.swing.JPanel {
	private static final long serialVersionUID = 1L;
	private HashMap<Integer, BoardCell> cellList = new HashMap<Integer, BoardCell>();
	private BaseGui base;
	private int perspective = GuiConstants.WHITE_PERSPECTIVE;

	public ChessBoardPanel(BaseGui base) {
		this.base = base;
		initComponents();
	}

	public void buildCells() {
		BoardCell cell;
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				cell = new BoardCell(base);
				cell.setNumericName(63 - ((7 - x) + (y * 8)));
				if ((x + y) % 2 == 0) {
					cell.setCellColor(GuiConstants.COLOR_WHITE);
				} else {
					cell.setCellColor(GuiConstants.COLOR_BLACK);
				}
				cell.setLocation(cell.getWidth() * x + 5, cell.getHeight() * y + 5);
				add(cell);
				cellList.put(cell.getNumericName(), cell);
			}
		}
	}

	public void startNewGame() {
		for (int x = 8; x < 16; x++) {
			((BoardCell) cellList.get(x)).setItem(EngineConstants.WHITE_PAWN);
			((BoardCell) cellList.get(x + (5 * 8))).setItem(EngineConstants.BLACK_PAWN);
		}
		for (int x = 0; x < 8; x = x + 7) {
			((BoardCell) cellList.get(x)).setItem(EngineConstants.WHITE_ROOK);
			((BoardCell) cellList.get(x + (7 * 8))).setItem(EngineConstants.BLACK_ROOK);
		}
		for (int x = 1; x < 8; x = x + 5) {
			((BoardCell) cellList.get(x)).setItem(EngineConstants.WHITE_KNIGHT);
			((BoardCell) cellList.get(x + (7 * 8))).setItem(EngineConstants.BLACK_KNIGHT);
		}
		for (int x = 2; x < 8; x = x + 3) {
			((BoardCell) cellList.get(x)).setItem(EngineConstants.WHITE_BISHOP);
			((BoardCell) cellList.get(x + (7 * 8))).setItem(EngineConstants.BLACK_BISHOP);
		}
		((BoardCell) cellList.get(3)).setItem(EngineConstants.WHITE_QUEEN);
		((BoardCell) cellList.get(59)).setItem(EngineConstants.BLACK_QUEEN);
		((BoardCell) cellList.get(4)).setItem(EngineConstants.WHITE_KING);
		((BoardCell) cellList.get(60)).setItem(EngineConstants.BLACK_KING);
	}

	public BoardCell getSelectedCell() {
		BoardCell cell = null;
		for (int x = 0; x < 64; x++) {
			cell = (BoardCell) cellList.get(x);
			if (cell.isSelected()) {
				return cell;
			}
		}
		return null;
	}

	public byte[][] getBoard() {
		byte[][] board = new byte[8][8];
		BoardCell cell;
		for (int x = 0; x < 64; x++) {
			cell = (BoardCell) cellList.get(x);
			board[GuiConstants.PERSPECTIVE_BOARD_MAPPING[perspective][x][0]][GuiConstants.PERSPECTIVE_BOARD_MAPPING[perspective][x][1]] = cell
					.getItem();
		}
		return board;
	}

	public void setBoard(byte[][] board) {
		BoardCell cell;
		for (int x = 0; x < 64; x++) {
			cell = (BoardCell) cellList.get(x);
			cell.setItem(
					board[GuiConstants.PERSPECTIVE_BOARD_MAPPING[perspective][x][0]][GuiConstants.PERSPECTIVE_BOARD_MAPPING[perspective][x][1]]);
		}
	}

	public void changePerspective() {
		if (getSelectedCell() != null) {
			getSelectedCell().setSelected(false);
		}
		byte[][] board = getBoard();
		setPerspective(Math.abs(1 - perspective));
		setBoard(board);
	}

	public void setPerspective(int perspective) {
		this.perspective = perspective;
	}

	public int getPerspective() {
		return perspective;
	}

	private void initComponents() {
		setLayout(null);
	}

	public BoardCell getCell(int idx) {
		return cellList.get(GuiConstants.PERSPECTIVE_CELL_MAPPING[idx][base.getChessBoardPanel().getPerspective()]);
	}

	public void clearBoard() {
		BoardCell cell;
		for (int x = 0; x < 64; x++) {
			cell = (BoardCell) cellList.get(x);
			cell.setItem(EngineConstants.BLANK);
		}
	}
}

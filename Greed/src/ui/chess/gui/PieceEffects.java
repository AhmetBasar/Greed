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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import chess.engine.EngineConstants;
import chess.game.GamePlay;

public class PieceEffects implements ActionListener {
	byte movingItem;
	private BaseGui base;
	Cell movingCell;
	Cell targetCell;
	int initX;
	int initY;
	float currentX;
	float currentY;
	int targetX;
	int targetY;
	final float moveSpeed = 1;
	private Timer timer;
	private static ArrayList<Timer> timerPool = new ArrayList<Timer>();

	public static void doEffect(BaseGui base, int sourceIndex, int targetIndex) {
		if (base.getGamePlay().isNoAnimation()) {
			// For Debugging Purpose.
			byte movingItem = base.getChessBoardPanel().getCell(sourceIndex).getItem();
			base.getChessBoardPanel().getCell(sourceIndex).setItem(EngineConstants.BLANK);
			base.getChessBoardPanel().getCell(targetIndex).setItem(movingItem);
			//
		} else {
			new PieceEffects(base).start(sourceIndex, targetIndex);
		}
	}

	public PieceEffects(BaseGui base) {
		this.base = base;
		movingCell = new Cell(base);
		timer = new Timer(1, this);
	}

	public void start(int sourceIndex, int targetIndex) {
		BoardCell sourceCell = base.getChessBoardPanel().getCell(sourceIndex);
		initX = sourceCell.getX();
		initY = sourceCell.getY();
		targetCell = base.getChessBoardPanel().getCell(targetIndex);
		targetX = targetCell.getX();
		targetY = targetCell.getY();

		movingItem = sourceCell.getItem();
		movingCell.setOpaque(true);
		movingCell.setBackground(GuiConstants.COLOR_TRANSPARENT);
		movingCell.setItem(movingItem);
		movingCell.setLocation(sourceCell.getLocation());
		base.getChessBoardPanel().add(movingCell, GuiConstants.LAYER_STATIC_PIECE);

		base.getChessBoardPanel().getCell(sourceIndex).setItem(EngineConstants.BLANK);

		currentX = initX;
		currentY = initY;
		timer.start();
		addTimerToPool(timer);
	}

	private void animate() {
		currentX = currentX + ((targetX - initX) / moveSpeed);
		currentY = currentY + ((targetY - initY) / moveSpeed);

		movingCell.setLocation((int) currentX, (int) currentY);
		base.getChessBoardPanel().repaint();
		base.getChessBoardPanel().getToolkit().sync();

		if (Math.abs(currentX - targetX) < 1 && Math.abs(currentY - targetY) < 1) {
			timer.stop();
			removeTimerFromPool(timer);
			targetCell.setItem(movingCell.getItem());
			base.getChessBoardPanel().remove(movingCell);
			movingCell = null;
			base.getChessBoardPanel().repaint();
			base.getChessBoardPanel().getToolkit().sync();

			/**
			 * Guarantees that paintComponent run before.
			 */
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					base.triggerTimerFinishEvent();
				}
			});

		}
	}

	public synchronized static boolean existsActiveTimer() {
		boolean existsActiveTimer = false;
		if (timerPool.size() > 0) {
			existsActiveTimer = true;
		}
		return existsActiveTimer;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		animate();
	}

	private synchronized static void addTimerToPool(Timer timer) {
		timerPool.add(timer);
	}

	private synchronized static void removeTimerFromPool(Timer timer) {
		timerPool.remove(timer);
	}
}

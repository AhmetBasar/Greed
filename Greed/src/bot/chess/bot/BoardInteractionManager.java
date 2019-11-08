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
import java.awt.event.InputEvent;

import chess.bot.image.IImageCache;
import chess.bot.interpreting.BotMove;
import chess.gui.GuiConstants;

public class BoardInteractionManager {

	private BoardScanner boardScanner;
	private IImageCache imageCache;

	public BoardInteractionManager(BoardScanner boardScanner, IImageCache imageCache) {
		this.boardScanner = boardScanner;
		this.imageCache = imageCache;
	}
	
	private boolean fast = false;
	
	public void doMove(BotMove move) {
		doMove(move, true);
	}
	
	public void doMove(BotMove move, boolean existsDelay) {
		
		boolean ff = fast;

		Point corner = boardScanner.getUpperLeftCornerPoint();

		int guiFrom = GuiConstants.PERSPECTIVE_CELL_MAPPING[move.getFrom()][boardScanner.getBot().getController()
				.getPerspective()];
		int guiTo = GuiConstants.PERSPECTIVE_CELL_MAPPING[move.getTo()][boardScanner.getBot().getController()
				.getPerspective()];

		Point fromPiecePoint = imageCache.getPiecePoint(corner, guiFrom);
		Point toPiecePoint = imageCache.getPiecePoint(corner, guiTo);

		if (ff) {
			doMouseLeftClickOnce(fromPiecePoint);
		} else {
			doMousePressed(fromPiecePoint);
		}

		if (existsDelay) {
			if (ff) {
				Utility.sleep(Utility.generateStrongRandomNumber(33, 55));
			} else {
				Utility.sleep(Utility.generateStrongRandomNumber(44, 88));
			}
		}

		if (ff) {
			doMouseLeftClickOnce(toPiecePoint);
		} else {
			doMouseReleased(toPiecePoint);
		}
	}

	public static void doMouseLeftClickOnce(Point p) {
		int mask = InputEvent.BUTTON1_DOWN_MASK;
//		Robot.moveMouseCursor(p);
		Utility.sleep(Utility.generateStrongRandomNumber(2, 12));
		Robot.getRobot().mouseMove(p.x, p.y);
		Utility.sleep(Utility.generateStrongRandomNumber(2, 12));
		Robot.getRobot().mousePress(mask);
		Utility.sleep(Utility.generateStrongRandomNumber(2, 12));
		Robot.getRobot().mouseRelease(mask);
		Utility.sleep(Utility.generateStrongRandomNumber(2, 12));
	}
	
	public static void doMousePressed(Point p) {
		int mask = InputEvent.BUTTON1_DOWN_MASK;
		Robot.moveMouseCursor(p);
		Utility.sleep(Utility.generateStrongRandomNumber(5, 27));
		Robot.getRobot().mousePress(mask);
	}
	
//	public static void doMousePressedUltraFast(Point p) {
//		int mask = InputEvent.BUTTON1_DOWN_MASK;
//		Robot.moveMouseCursoUltraFast(p);
//		Utility.sleep(Utility.generateStrongRandomNumber(7, 14));
//		Robot.getRobot().mousePress(mask);
//	}
	
	public static void doMouseReleased(Point p) {
		int mask = InputEvent.BUTTON1_DOWN_MASK;
		Robot.moveMouseCursor(p);
		Utility.sleep(Utility.generateStrongRandomNumber(11, 33));
		Robot.getRobot().mouseRelease(mask);
	}

	public boolean isFast() {
		return fast;
	}

	public void setFast(boolean fast) {
		this.fast = fast;
	}
	
//	public static void doMouseReleasedUltraFast(Point p) {
//		int mask = InputEvent.BUTTON1_DOWN_MASK;
//		Robot.moveMouseCursoUltraFast(p);
//		Utility.sleep(Utility.generateStrongRandomNumber(5, 10));
//		Robot.getRobot().mouseRelease(mask);
//	}

}

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

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;

public class GlassPane extends JComponent implements KeyListener {

	private static final long serialVersionUID = 1L;

	public GlassPane() {
		setOpaque(false);

		addMouseListener(new MouseAdapter() {
		});
		addMouseMotionListener(new MouseMotionAdapter() {
		});
		addKeyListener(this);

		setFocusTraversalKeysEnabled(false);
	}

	public void activate() {
		setVisible(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		requestFocusInWindow();
	}

	public void deactivate() {
		setCursor(null);
		setVisible(false);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		e.consume();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		e.consume();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}

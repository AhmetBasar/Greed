/**********************************************
 * Greed, a chess engine written in java.
 * Copyright (C) 2019 Ahmet Ba�ar
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JLabel;

import chess.engine.EngineConstants;

public class Cell extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Creates new form Cell
	 */
	private Image image;
	private byte item;
	private int state = 0;
	private int numericName;
	private JLabel lbl;
	private boolean blank;
	private boolean existsBorder;
	private BaseGui base;
	private int debugItemChangeCount = 0;

	public static final int CELL_SELECTED = 1, CELL_UNSELECTED = 0;

	public Cell(BaseGui base) {
		this.base = base;
		initComponents();
		setSize(GuiConstants.CELL_WIDTH, GuiConstants.CELL_HEIGHT);
	}

	public int getNumericName() {
		return GuiConstants.PERSPECTIVE_CELL_MAPPING[numericName][base.getChessBoardPanel().getPerspective()];
	}

	public void setNumericName(int numericName) {
		this.numericName = numericName;
//		lbl.setText("" + numericName);
	}

	public void setItem(byte item) {
		///DEBUG
		if(item != EngineConstants.BLANK){
			debugItemChangeCount++;
		}
		lbl.setText("" + debugItemChangeCount);
//		if(debugItemChangeCount == 0){
//			lbl.setVisible(false);
//		}else{
//			lbl.setVisible(true);
//		}
		///
		blank = false;

		this.item = item;
		
		if (item == EngineConstants.BLANK) {
			blank = true;
		} else {
			image = ChessImageCache.getInstance().get(item);
		}
		
		repaint();
	}

	public void setCellColor(Color color) {
		setBackground(color);
	}

	public byte getItem() {
		return item;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!blank) {
			g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		}
		if (existsBorder) {
			g.drawLine(0, 0, 50, 0);
			g.drawLine(0, 0, 0, 50);
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(6));
		if (state == CELL_SELECTED) {
			g2.drawRect(0, 0, 50, 50);
		}
	}

	public boolean isSelected() {
		return state == CELL_SELECTED;
	}

	public void setSelected(boolean selected) {
		if (selected) {
			state = CELL_SELECTED;
			repaint();
		} else {
			state = CELL_UNSELECTED;
			repaint();
		}
	}

	private void initComponents() {

		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				formMousePressed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));

		lbl = new JLabel();
		lbl.setSize(50, 50);
		lbl.setVisible(false);
		lbl.setLocation(10, 10);
		lbl.setText("" + debugItemChangeCount);
		add(lbl);
	}

	private void formMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMousePressed
		if(!PieceEffects.existsActiveTimer()){
			mausePressed();
		}
	}

	public void mausePressed() {
	}
	
	public void setExistsBorder(boolean existsBorder) {
		this.existsBorder = existsBorder;
	}
	
	public boolean isWhiteItem(){
		return (item & (byte) 1) == (byte) 0;
	}
	
	public boolean isBlackItem(){
		return (item & (byte) 1) == (byte) 1;
	}
	
	public boolean isEmptyItem(){
		return item == EngineConstants.BLANK;
	}
	
	public int getDebugItemChangeCount() {
		return debugItemChangeCount;
	}
	
	public void setDebugItemChangeCount(int debugItemChangeCount) {
		this.debugItemChangeCount = debugItemChangeCount;
	}
}

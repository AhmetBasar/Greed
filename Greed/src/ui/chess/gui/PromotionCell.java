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

public class PromotionCell extends Cell {

	private static final long serialVersionUID = 1L;
	BaseGui base;

    public PromotionCell(BaseGui base) {
    	super(base);
        this.base = base;
    }

    @Override
    public void mausePressed() {
    	base.getPromotionPanel().setLastChoosenPromotionItem(getItem());
    	base.getPromotionFrame().setVisible(false);
    }

}

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

import javax.swing.SwingWorker;

public abstract class BackgroundJobHandler {

	public void execute() {
		getGlassPane().activate();

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				BackgroundJobHandler.this.doInBackground();
				return null;
			}

			@Override
			protected void done() {
				getGlassPane().deactivate();
			}
		};
		worker.execute();
	}

	public abstract void doInBackground() throws Exception;

	public abstract GlassPane getGlassPane();

}

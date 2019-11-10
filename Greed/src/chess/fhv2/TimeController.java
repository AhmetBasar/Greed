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
package chess.fhv2;
public class TimeController implements Runnable {
	
	private volatile boolean suspended = true;
	private ICallBackTimeout callBack;
	private int checkPeriod = 1;
	
	public TimeController(ICallBackTimeout callBack) {
		this.callBack = callBack;
	}
	
	@Override
	public void run() {
		try {
			waitIfSuspended();
			while (true) {
				try {
					Thread.sleep(checkPeriod);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (callBack.evaluateTimeoutCondition()) {
					callBack.onTimeout();
					suspend();
					waitIfSuspended();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void waitIfSuspended() {
		synchronized (this) {
			while (suspended) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void suspend() {
		suspended = true;
	}

	public synchronized void resume() {
		suspended = false;
		notify();
	}
}
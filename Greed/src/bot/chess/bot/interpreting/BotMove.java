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

public class BotMove {

	private int from;
	private int to;
	private int engineMove;

	public BotMove(int from, int to) {
		this.from = from;
		this.to = to;
	}
	
	public BotMove(int engineMove) {
		this.engineMove = engineMove;
		this.from = engineMove & 0x000000ff;
		this.to = (engineMove & 0x0000ff00) >>> 8;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}
	
	public int getEngineMove() {
		return engineMove;
	}

	@Override
	public String toString() {
		return "from = " + from + " to = " + to; 
	}

}

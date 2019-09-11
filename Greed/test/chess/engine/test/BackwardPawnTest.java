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
package chess.engine.test;

import chess.evaluation.BackwardPawn;

public class BackwardPawnTest {

	public static void main(String[] args) {
		testAll();
	}

	public static void testAll() {
		testWhiteBackwardPawns();

		testBlackBackwardPawns();
	}

	public static void testWhiteBackwardPawns() {
		if (BackwardPawn.whiteBackwardPawns(68853694464L, 17592186044416L) != 134217728L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.whiteBackwardPawns(292628201472L, 74766790688768L) != 570425344L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.whiteBackwardPawns(292695310336L, 74766790688768L) != 536870912L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.whiteBackwardPawns(292628463616L, 74766790688768L) != 536870912L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.whiteBackwardPawns(292628202496L, 74766790688768L) != 536870912L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.whiteBackwardPawns(292628332544L, 74766790688768L) != 570425344L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.whiteBackwardPawns(292628201984L, 74766790688768L) != 570425344L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.whiteBackwardPawns(301218136064L, 74766790688768L) != 570425344L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.whiteBackwardPawns(292628201728L, 74766790688768L) != 536870912L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.whiteBackwardPawns(292628201472L, 74766790689024L) != 570425344L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.whiteBackwardPawns(296923168768L, 74766790688768L) != 570425344L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.whiteBackwardPawns(292644978688L, 74766790688768L) != 536870912L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testBlackBackwardPawns() {
		if (BackwardPawn.blackBackwardPawns(284071821312L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(281759048531968L, 2359296L) != 274877906944L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(1383583449088L, 2359296L) != 274877906944L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(288366788608L, 2359296L) != 274877906944L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(284088598528L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(284071886848L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(284071821568L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(563234025242624L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(2483095076864L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(284105375744L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(284071952384L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(284071821824L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(1126183978663936L, 2359296L) != 274877906944L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(4682118332416L, 2359296L) != 274877906944L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(301251690496L, 2359296L) != 274877906944L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(284071822336L, 2359296L) != 283467841536L) {
			throw new RuntimeException("Failed.");
		}

		if (BackwardPawn.blackBackwardPawns(318431559680L, 2359296L) != 317827579904L) {
			throw new RuntimeException("Failed.");
		}

	}

}

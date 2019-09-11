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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import chess.engine.EngineConstants;

public class ChessImageCache {

	private static volatile ChessImageCache instance;
	private final BufferedImage[] cache = new BufferedImage[14];
	private final String[] imageNames = new String[] { "", "", "White_Pawn", "Black_Pawn", "White_Knight",
			"Black_Knight", "White_Bishop", "Black_Bishop", "White_Rook", "Black_Rook", "White_Queen", "Black_Queen",
			"White_King", "Black_King" };

	public static ChessImageCache getInstance() {
		if (instance == null) {
			synchronized (ChessImageCache.class) {
				if (instance == null) {
					instance = new ChessImageCache();
				}
			}
		}
		return instance;
	}

	private ChessImageCache() {
		for (int i = EngineConstants.WHITE_PAWN; i < cache.length; i++) {
			try {
				cache[i] = ImageIO.read(new File("src/ui/images/" + imageNames[i] + ".png"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public BufferedImage get(int index) {
		if (index < EngineConstants.WHITE_PAWN) {
			throw new IllegalArgumentException();
		}
		return cache[index];
	}

}

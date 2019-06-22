package chess.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
		for (int i = 2; i < cache.length; i++) {
			try {
				cache[i] = ImageIO.read(new File("src/ui/images/" + imageNames[i] + ".png"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public BufferedImage get(int index) {
		return cache[index];
	}

}

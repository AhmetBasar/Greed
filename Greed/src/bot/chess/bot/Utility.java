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
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.security.SecureRandom;

import javax.imageio.ImageIO;

public class Utility {

	public static final String COMPRESSION_ALGORITHM_BMP = "bmp";

	public static float compareImage(BufferedImage biA, BufferedImage biB) {
		float percentage = 0;
		DataBuffer dbA = biA.getData().getDataBuffer();
		int sizeA = dbA.getSize();
		DataBuffer dbB = biB.getData().getDataBuffer();
		int sizeB = dbB.getSize();
		int count = 0;
		if (sizeA == sizeB) {
			for (int i = 0; i < sizeA; i++) {
				if (dbA.getElem(i) == dbB.getElem(i)) {
					count = count + 1;
				}
			}
			percentage = ((float) (count * 100f)) / (float) sizeA;
		} else {
			return 0;
		}
		return percentage;
	}

	// Slow method.
	public static double compareImage2(BufferedImage img1, BufferedImage img2) {
		int width1 = img1.getWidth();
		int width2 = img2.getWidth();
		int height1 = img1.getHeight();
		int height2 = img2.getHeight();
		if ((width1 != width2) || (height1 != height2)) {
			// TODO Change it to return 100.
			// TODO Change it to return 100.
			// TODO Change it to return 100.
			// TODO Change it to return 100.
			// TODO Change it to return 100.
			// TODO Change it to return 100.
			// TODO Change it to return 100.
			// TODO Change it to return 100.
			// TODO Change it to return 100.
			// TODO Change it to return 100.
			// TODO Change it to return 100.
			// TODO Change it to return 100.
			return 60000;
		}

		int diff2 = 0;

		for (int i = 0; i < height1; i++) {
			for (int j = 0; j < width1; j++) {
				int rgb1 = img1.getRGB(j, i);
				int rgb2 = img2.getRGB(j, i);
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = (rgb1) & 0xff;
				int r2 = (rgb2 >> 16) & 0xff;
				int g2 = (rgb2 >> 8) & 0xff;
				int b2 = (rgb2) & 0xff;

				diff2 += Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2);
			}
		}
		return diff2 * 1.0 / (height1 * width1);
	}

	public static byte[] convertImageToByteArray(BufferedImage image, String algorithm) throws Exception {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			ImageIO.write(image, algorithm, baos);
			baos.flush();
			byte[] bytes = baos.toByteArray();
			return bytes;
		} finally {
			if (baos != null) {
				baos.close();
			}
		}
	}

	public static Point findSubImage(BufferedImage subimage, BufferedImage image) {
		// brute force N^2 check all places in the image
		for (int i = 0; i <= image.getWidth() - subimage.getWidth(); i++) {
			check_subimage: for (int j = 0; j <= image.getHeight() - subimage.getHeight(); j++) {
				for (int ii = 0; ii < subimage.getWidth(); ii++) {
					for (int jj = 0; jj < subimage.getHeight(); jj++) {
						if (subimage.getRGB(ii, jj) != image.getRGB(i + ii, j + jj)) {
							continue check_subimage;
						}
					}
				}
				// if here, all pixels matched
				return new Point(i, j);
			}
		}
		return null;
	}

	public static void closeQuietly(Closeable... closeables) {
		for (Closeable closeable : closeables) {
			try {
				if (closeable != null) {
					closeable.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public static int[][] extractRgbMap(InputStream stream) throws Exception {
		int[][] rgbData = null;
		int bflen = 14;
		byte bf[] = new byte[bflen];
		stream.read(bf, 0, bflen);
		int bilen = 40;
		byte bi[] = new byte[bilen];
		stream.read(bi, 0, bilen);
		int nwidth = (((int) bi[7] & 0xff) << 24) | (((int) bi[6] & 0xff) << 16) | (((int) bi[5] & 0xff) << 8)
				| (int) bi[4] & 0xff;
		int nheight = (((int) bi[11] & 0xff) << 24) | (((int) bi[10] & 0xff) << 16) | (((int) bi[9] & 0xff) << 8)
				| (int) bi[8] & 0xff;
		int nbitcount = (((int) bi[15] & 0xff) << 8) | (int) bi[14] & 0xff;
		int nsizeimage = (((int) bi[23] & 0xff) << 24) | (((int) bi[22] & 0xff) << 16) | (((int) bi[21] & 0xff) << 8)
				| (int) bi[20] & 0xff;
		if (nbitcount != 24) {
			throw new RuntimeException("Use only 24bit color .bmp files");
		}
		int npad = (nsizeimage / nheight) - nwidth * 3;
		int ndata[] = new int[nheight * nwidth];
		rgbData = new int[nheight][nwidth];
		byte brgb[] = new byte[(nwidth + npad) * 3 * nheight];
		stream.read(brgb, 0, (nwidth + npad) * 3 * nheight);
		int nindex = 0;
		for (int j = 0; j < nheight; j++) {
			for (int i = 0; i < nwidth; i++) {
				int rgbValue = (255 & 0xff) << 24 | (((int) brgb[nindex + 2] & 0xff) << 16)
						| (((int) brgb[nindex + 1] & 0xff) << 8) | (int) brgb[nindex] & 0xff;
				ndata[nwidth * (nheight - j - 1) + i] = rgbValue;
				rgbData[nheight - j - 1][i] = rgbValue;
				nindex += 3;
			}
			nindex += npad;
		}
		return rgbData;
	}

	public static Point findSubImagePoint(int[][] big, int[][] small) {
		int firstElem = small[0][0];

		for (int i = 0; i < big.length - small.length + 1; i++) {
			columnscan: for (int j = 0; j < big[0].length - small[0].length; j++) {
				if (big[i][j] != firstElem)
					continue columnscan;
				for (int ii = 0; ii < small.length; ii++)
					for (int jj = 0; jj < small[0].length; jj++)
						if (big[i + ii][j + jj] != small[ii][jj])
							continue columnscan;
				Point result = new Point();
				result.y = i;
				result.x = j;
				return result;
			}
		}
		return null;
	}

	public static String prepareGetterMethodName(String fieldName) {
		return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
	}
	
	public static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static int convertOneDimensionIndex(int i, int j) {
		return BotConstants.ONE_DIMENSION_INDEXES[i][j];
	}
	
	public static int[] convertTwoDimensionIndex(int index) {
		return BotConstants.TWO_DIMENSION_INDEXES[index];
	}
	
	public static int generateStrongRandomNumber(int begin, int end) {
		SecureRandom r = new SecureRandom();
		return r.nextInt(end - begin) + begin;
	}
	
	public static boolean doProbability(int percentage) {
		return generateStrongRandomNumber(0, 100) < percentage;
	}
	
	public static void mai2n(String[] args) {
		
		System.out.println(generateStrongRandomNumber(-10, 10));
		System.out.println(generateStrongRandomNumber(-10, 10));
		System.out.println(generateStrongRandomNumber(-10, 10));
		System.out.println(generateStrongRandomNumber(-10, 10));
		System.out.println(generateStrongRandomNumber(-10, 10));
		System.out.println(generateStrongRandomNumber(-10, 10));
		System.out.println(generateStrongRandomNumber(-10, 10));
		System.out.println(generateStrongRandomNumber(-10, 10));
		System.out.println(generateStrongRandomNumber(-10, 10));
		System.out.println(generateStrongRandomNumber(-10, 10));
		System.out.println(generateStrongRandomNumber(-10, 10));
		System.out.println(generateStrongRandomNumber(-10, 10));
		System.out.println(generateStrongRandomNumber(-10, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
//		System.out.println(generateStrongRandomNumber(1, 10));
		
	}
	
	public static int getPieceCount(byte[][] board) {
		int pieceCount = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] != 0) {
					pieceCount++;
				}
			}
		}
		return pieceCount;
	}
	
	public static boolean isDebug() {
		return false;
	}

}
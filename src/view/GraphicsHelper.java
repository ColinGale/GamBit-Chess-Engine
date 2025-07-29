package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


import javax.imageio.ImageIO;

public class GraphicsHelper{
	private static final HashMap<String, BufferedImage> imageCache = new HashMap<>();
	
	public static BufferedImage getImage(String imagePath) {
		/*
		if (!imagePath.startsWith("/res")) {
	        imagePath = "/res" + imagePath;
	    }
		*/
		
	    if (imageCache.containsKey(imagePath)) {
	        return imageCache.get(imagePath);
	    }
	    try (InputStream is = GraphicsHelper.class.getResourceAsStream(imagePath)) {
	        if (is == null) {
	            System.err.println("Resource not found: " + imagePath);
	            return null;
	        }
	        BufferedImage image = ImageIO.read(is);
	        imageCache.put(imagePath, image);
	        return image;
	    } catch (IOException e) {
	        System.err.println("Could not load image: " + imagePath);
	        e.printStackTrace();
	        return null;
	    }
	}


	public static void drawRook(int row, int col, boolean toMove, Graphics2D g2) {
		drawPiece(row, col, "rook", toMove, g2);
	}
	
	public static void drawKnight(int row, int col, boolean toMove, Graphics2D g2) {
		drawPiece(row, col, "knight", toMove, g2);
	}
	
	public static void drawBishop(int row, int col, boolean toMove, Graphics2D g2) {
		drawPiece(row, col, "bishop", toMove, g2);
	}
	
	public static void drawQueen(int row, int col, boolean toMove, Graphics2D g2) {
		drawPiece(row, col, "queen", toMove, g2);
	}
	
	public static BufferedImage toDarkerGrayscale(BufferedImage img, double darknessFactor) {
	    BufferedImage grayImage = new BufferedImage(
	        img.getWidth(), img.getHeight(),
	        BufferedImage.TYPE_INT_ARGB // Preserve alpha
	    );

	    for (int y = 0; y < img.getHeight(); y++) {
	        for (int x = 0; x < img.getWidth(); x++) {
	            int argb = img.getRGB(x, y);

	            int alpha = (argb >> 24) & 0xff;
	            int red   = (argb >> 16) & 0xff;
	            int green = (argb >> 8) & 0xff;
	            int blue  = argb & 0xff;

	            // Standard grayscale conversion
	            int gray = (int)(0.299 * red + 0.587 * green + 0.114 * blue);

	            // Apply darkness factor (clamp to 0–255)
	            gray = (int)(gray * darknessFactor);
	            gray = Math.max(0, Math.min(255, gray));

	            // Rebuild ARGB with original alpha
	            int grayArgb = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
	            grayImage.setRGB(x, y, grayArgb);
	        }
	    }

	    return grayImage;
	}


	
	private static void drawPiece(int row, int col, String pType, boolean toMove, Graphics2D g2) {
		
		// get image for certain piece
		String color = toMove ? "white" : "black";
		String imagePath = "/piece/" + color + "_" + pType + ".png";
		BufferedImage image = null;
		try {
			image = GraphicsHelper.getImage(imagePath);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Couldn't find image " + imagePath);
			return;
		}
		
		// get x and y for the row/col
		int x = GraphicsHelper.getX(col);
		int y = GraphicsHelper.getY(row);
		
		g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
	}
	
	public static int getCol(int x) {
		return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
	}
	
	public static int getRow(int y) {
		return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
	}
	
	public static int getX(int col) {
		return col * Board.SQUARE_SIZE;
	}
	
	public static int getY(int row) {
		return row * Board.SQUARE_SIZE;
	}
}

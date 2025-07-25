package view;

import java.awt.Color;
import java.awt.Graphics2D;

public class Board {
	final int COLS = 8;
	final int ROWS = 8;
	public static final int SQUARE_SIZE = 100;
	// used to place pieces in middle
	public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;
	
	
	public void draw(Graphics2D g2) {
		
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				
				if ((row + col) % 2 == 1) {
					g2.setColor(new Color(118,150,86));
				}
				else {
					g2.setColor(new Color(238,238,210));
				}
				
				g2.fillRect(SQUARE_SIZE * row, SQUARE_SIZE * col, SQUARE_SIZE, SQUARE_SIZE);
			}
		}
	}
	

}

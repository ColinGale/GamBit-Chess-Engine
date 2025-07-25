package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import controller.Mouse;

public class MenuButton {
	
	private int xPos, yPos, rowIndex, index;
	private int width, height;
	private double scale;
	
	private String imageLink;
	private String[] imageLinks;
	private BufferedImage[] images;
	private Rectangle buttonHitBox;
	private String message;
	
	private boolean mouseOver, mousePressed;
	private boolean hasBeenPressed;
	
	
	public MenuButton(int xPos, int yPos, int width, int height, String imageLink) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		this.index = 0;
		this.imageLink = imageLink;
		hasBeenPressed = false;
		
		loadImgsSplit();
		initBoundsScaled();
	}
	
	public MenuButton(int xPos, int yPos, double scale, String[] imageLinks) {
		this.xPos = xPos;
		this.yPos = yPos;
		
		this.scale  = scale;
		this.index = 0;
		this.imageLinks = imageLinks;
		hasBeenPressed = false;
		
		loadImgs();
		initBounds(xPos, yPos, width, height);
	}
	
	private void initBounds(int xPos, int yPos, int width, int height) {
		buttonHitBox = new Rectangle(xPos, yPos, width, height);		
	}
	
	private void initBoundsScaled() {
	    BufferedImage img = images[0]; // Always use the base image for bounds
	    int adjustedWidth = (int) (img.getWidth() * scale);
	    int adjustedHeight = (int) (img.getHeight() * scale);
	    buttonHitBox = new Rectangle(
	        xPos - adjustedWidth / 2,
	        yPos - adjustedHeight / 2,
	        adjustedWidth,
	        adjustedHeight
	    );
	}

	
	private void loadImgs() {
		images = new BufferedImage[imageLinks.length];
		for (int i = 0; i < images.length; i++) {
			BufferedImage raw = GraphicsHelper.getImage(imageLinks[i]);
			images[i] = cropTransparentBorder(raw);
		}
	}

	private void loadImgsSplit() {
		images = new BufferedImage[2];
		BufferedImage fullImage = GraphicsHelper.getImage(imageLink);
		for (int i = 0; i < images.length; i++) {
			images[i] = fullImage.getSubimage(i * fullImage.getWidth() / 2, 0, fullImage.getWidth() / 2, fullImage.getHeight());
		}
	}
	
	public void draw(Graphics2D g2) {	
	    int adjustedWidth, adjustedHeight;
	    int drawX, drawY;

	    if (scale != 0) {
	        adjustedWidth = (int) (images[index].getWidth() * scale);
	        adjustedHeight = (int) (images[index].getHeight() * scale);
	        drawX = xPos - adjustedWidth / 2;
	        drawY = yPos - adjustedHeight / 2;
	        g2.drawImage(images[index], drawX, drawY, adjustedWidth, adjustedHeight, null);
	    } else {
	        adjustedWidth = width;
	        adjustedHeight = height;
	        drawX = xPos;
	        drawY = yPos;
	        g2.drawImage(images[index], drawX, drawY, adjustedWidth, adjustedHeight, null);
	    }

	    initBounds(drawX, drawY, adjustedWidth, adjustedHeight);

	    
	    if (message != null) {
	    	g2.setFont(new Font("Book Antiqua", Font.BOLD, 40));
	    	if (mouseOver) g2.setColor(new Color(79, 78, 78));
	    	else g2.setColor(Color.black);
	    	g2.drawString(message, xPos + 50, yPos + 10);
	    }
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void update() {
		index = 0;
		if (mouseOver) index = 1;
		if (mousePressed && !mouseOver) {
			if (images.length == 3) index = 2;
		}
	}
	

	public boolean isMouseOver(Mouse m) {
		if (m.x >= buttonHitBox.getMinX() && m.x <= buttonHitBox.getMaxX() && m.y >= buttonHitBox.getMinY() && m.y <= buttonHitBox.getMaxY()) {
			mouseOver = true;
		}
		else mouseOver = false;
		
		return mouseOver;
	}
	

	public boolean isMousePressed() {
		return mousePressed;
	}

	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}

	public void setMousePressed(boolean mousePressed) {
		this.mousePressed = mousePressed;
	}
	
	public void reset() {
		mouseOver = false;
		mousePressed = false;
		hasBeenPressed = false;
	}
	
	public static BufferedImage cropTransparentBorder(BufferedImage src) {
	    int top = 0, left = 0, right = src.getWidth() - 1, bottom = src.getHeight() - 1;

	    // Find top
	    scan:
	    for (; top < src.getHeight(); top++) {
	        for (int x = 0; x < src.getWidth(); x++) {
	            if ((src.getRGB(x, top) >> 24) != 0x00) break scan;
	        }
	    }

	    // Find bottom
	    scan:
	    for (; bottom >= top; bottom--) {
	        for (int x = 0; x < src.getWidth(); x++) {
	            if ((src.getRGB(x, bottom) >> 24) != 0x00) break scan;
	        }
	    }

	    // Find left
	    scan:
	    for (; left < src.getWidth(); left++) {
	        for (int y = top; y <= bottom; y++) {
	            if ((src.getRGB(left, y) >> 24) != 0x00) break scan;
	        }
	    }

	    // Find right
	    scan:
	    for (; right >= left; right--) {
	        for (int y = top; y <= bottom; y++) {
	            if ((src.getRGB(right, y) >> 24) != 0x00) break scan;
	        }
	    }

	    // Crop
	    int width = right - left + 1;
	    int height = bottom - top + 1;

	    return src.getSubimage(left, top, width, height);
	}


}

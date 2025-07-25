package view;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Panel extends JPanel {

	    private MenuButton evalButton;
	    private MenuButton engineButton;
	    private MenuButton playerButton;
	    private MenuButton settingButton;
	    
	    private BufferedImage logo;
	    private BufferedImage text;
	    
	    private MenuState menuState;
	    
	    private ChessMenu menu;

	    public Panel(ChessMenu menu) {
	    	this.menu = menu;
	    	
	        setPreferredSize(new Dimension(1100, 800));
	        setBackground(new Color(44, 43, 41));
	        evalButton = new MenuButton(1100 / 2 - 300, 800 / 2, 0.25, new String[] {"/menu/uncheckedbutton.png", "/menu/hovercheckbutton2.png", "/menu/checkedbutton.png"});
	        
	        engineButton = new MenuButton(1100 / 3 - 50, 800 / 2 + 150, 0.5, new String[] {"/menu/playagainstenginebutton.png", "/menu/playagainstenginebutton2.png"});
	        playerButton = new MenuButton(1100 * 2 / 3 + 50, 800 / 2 + 150, 0.5, new String[] {"/menu/playagainstyourselfbutton.png", "/menu/playagainstyourselfbutton2.png"});
	        settingButton = new MenuButton(1100 / 2, 800 / 2 + 300, 0.5, new String[] {"/menu/settingsbutton.png", "/menu/settingsbutton2.png"});
	        logo = GraphicsHelper.getImage("/menu/gambitlogo.png");
	        text = GraphicsHelper.getImage("/menu/chessenginetext.png");
	        
	        
	        menuState = MenuState.MAIN;
	    }
	    
	    
	    public MenuButton getevalButton() {
	    	return evalButton;
	    }
	    
	    
	    public MenuButton getEngineButton() {
			return engineButton;
		}


		public MenuButton getPlayerButton() {
			return playerButton;
		}
		
		public MenuButton getSettingsButton() {
			return settingButton;
		}


		public MenuState getMenuState() {
			return menuState;
		}
		
		public void setMenuState(MenuState state) {
			menuState = state;
		}

		@Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        Graphics2D g2 = (Graphics2D) g;
	        
	        int squareSize = 100;
	        Color lightColor = new Color(121, 121, 121); // Light square
	        Color darkColor = new Color(169, 169, 169);   // Dark square
	        
	        /* if I ever decide to make buttons brown and pale
	         * 
	         * Color lightColor = new Color(240, 217, 181); // Light square
	         * Color darkColor = new Color(181, 136, 99);   // Dark square
	         */
	        
	        

	        for (int row = 0; row < getHeight() / squareSize + 1; row++) {
	            for (int col = 0; col < getWidth() / squareSize + 1; col++) {
	                boolean isDark = (row + col) % 2 == 0;
	                g2.setColor(isDark ? darkColor : lightColor);
	                g2.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
	            }
	        }
	        
	        if (menuState == MenuState.MAIN) {
	        	g2.drawImage(logo, 1100 / 2 - (int) (logo.getWidth() * 0.4 / 2), 10, (int) (logo.getWidth() * 0.4), (int) (logo.getHeight() * 0.4), null);
	        	g2.drawImage(text, 1100 / 2 - (int) (logo.getWidth() * 0.4 / 2), 212, (int) (logo.getWidth() * 0.4), (int) (logo.getHeight() * 0.4), null);
	        	engineButton.draw(g2);
	        	playerButton.draw(g2);
	        	settingButton.draw(g2);
	        }
	        else if (menuState == MenuState.SETTINGS) {
	        	g2.setFont(new Font("Book Antiqua", Font.BOLD, 80));
	        	g2.setColor(Color.black);
	        	g2.drawString("Settings - Coming Soon", 150, 200);
	        	//evalButton.draw(g2);
	        }
	        else {
	        	
	        }
	    }
}

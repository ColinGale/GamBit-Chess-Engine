package view;
import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.Mouse;

public class ChessMenu extends JFrame implements Runnable {

    private Panel menuPanel;
    private Thread menuThread;
   
    private CardLayout cardLayout;
    private JPanel container;
    private BoardPanel boardPanel;
    
    private MenuButton uncheckedBox;
    private MenuButton engineButton;
    private MenuButton playerButton;
    private MenuButton settingButton;
    
    private MenuButton evalButton;
    
    private Mouse mouse;

    public ChessMenu() {
        setTitle("Chess Game Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        
        menuPanel = new Panel(this); // pass reference to ChessMenu
        container.add(menuPanel, "MENU");
        
        add(container); // Add container to frame

        pack();
        setVisible(true);
        setLocationRelativeTo(null);

        // Mouse setup
        mouse = new Mouse();
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        engineButton = menuPanel.getEngineButton();
        playerButton = menuPanel.getPlayerButton();
        settingButton = menuPanel.getSettingsButton();
        
        evalButton = menuPanel.getevalButton();
        evalButton.setMessage("Evaluation Bar");
    }

    public void launchMenu() {
        menuThread = new Thread(this);
        menuThread.start(); // start the loop
    }
    
    public void update() {
    	
    	engineButton.isMouseOver(mouse);
    	engineButton.update();
    	
    	
    	playerButton.isMouseOver(mouse);
    	playerButton.update();
    	
    	settingButton.isMouseOver(mouse);
    	settingButton.update();
    	
    	evalButton.isMouseOver(mouse);
    	evalButton.update();
    	
    	
    	if (mouse.pressed) {
    		
	    	if (menuPanel.getMenuState() == MenuState.MAIN) {
	    		if (engineButton.isMouseOver(mouse)) {
	    			this.startGame(true);
	    		}
	    		if (playerButton.isMouseOver(mouse)) {
	    			this.startGame(false);
	    		}
	    		if (settingButton.isMouseOver(mouse)) {
	    			menuPanel.setMenuState(MenuState.SETTINGS);
	    		}
	    	}
	    	else if (menuPanel.getMenuState() == MenuState.SETTINGS) {
	    		if (evalButton.isMouseOver(mouse)) {
	    			evalButton.setMousePressed(!evalButton.isMousePressed());
	    		}
	    	}
    	}
    }
    
    public void startGame(boolean vsEngine) {
        boardPanel = new BoardPanel(vsEngine);
        container.add(boardPanel, "GAME");
        
        cardLayout.show(container, "GAME");
        container.revalidate();
        container.repaint();
        boardPanel.launchGame();
        stopMenu();
    }
    
    
    public void stopMenu() {
        menuThread = null;
    }



    @Override
    public void run() {
        double drawInterval = 1000000000 / 60;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (menuThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
            	update();
                menuPanel.repaint(); // repaint the panel, not the frame
                delta--;
            }
        }
    }
}

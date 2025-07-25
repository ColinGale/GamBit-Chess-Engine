package view;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main {

	    public static void main(String[] args) {
	        javax.swing.SwingUtilities.invokeLater(() -> {
	            ChessMenu menu = new ChessMenu();
	            menu.launchMenu(); // Start the menu loop
	        });
	    }
}


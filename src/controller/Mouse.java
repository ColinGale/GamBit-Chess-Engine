package controller;

import java.awt.event.*;
import view.BoardPanel;

public class Mouse implements MouseListener, MouseMotionListener {
	
	private BoardPanel board;
	public boolean justClicked = false;
    public boolean pressed = false;
    public int x, y;
    
    public Mouse(BoardPanel board) {
    	this.board = board;
    }
    
    public Mouse() {}

	@Override
    public void mousePressed(MouseEvent e) {
        pressed = true;
        justClicked = true;
        x = e.getX();
        y = e.getY();
        if (board != null) board.updateDraggedPosition(x, y); // call drag update immediately
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
        x = e.getX();
        y = e.getY();
        if (board != null) board.updateDraggedPosition(x, y); // call drag update immediately
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        
        if (board != null) board.updateDraggedPosition(x, y); // call drag update immediately
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    // Unused interface methods
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}

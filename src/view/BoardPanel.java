package view;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import controller.Keyboard;
import controller.Mouse;
import model.Bitboard;
import model.GamBit;


public class BoardPanel extends JPanel implements Runnable{
	
	// WINDOW SETUP
	public static final int WIDTH = 1100;
	public static final int HEIGHT = 800;
	Board blankBoard = new Board();
	
	// CONTROLLERS
	Mouse mouse;
	Keyboard keyboard;
	
	// DRAGGING PIECE
	boolean isDragging = false;
	BufferedImage draggedImage = null;
	int sourceRow, sourceCol;
	int draggedPieceX, draggedPieceY;
	ArrayList<Integer> draggedPieceMoves;
	
	//DISPLAY
	int prevMove;
	
	
	// GAME SETUP
	Thread gameThread;
	final int FPS = 60;
	boolean checkmate;
	boolean stalemate;
	boolean vsEngine;
	GamBit engine;
	
	// PROMOTION
	boolean promotion;
	int promotionSquare;
	ArrayList<int[]> promotionPieces;
	
	// BOARD REPRESENTATION
	Bitboard board = new Bitboard();
	Bitboard simBoard = new Bitboard();
	boolean toMove = true;
	
	public BoardPanel(boolean vsEngine) {
		this.vsEngine = vsEngine;
		if (vsEngine) {
			engine = new GamBit(5, board);
		}
		
		mouse = new Mouse(this);
		keyboard = new Keyboard();
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(new Color(44,43,41));
		
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		setFocusable(true);
		requestFocusInWindow();
		addKeyListener(keyboard);
		
		preloadPieceImages();
		
		// load all valid move squares to prevent slow start
		for (int sq = 0; sq < 64; sq++) {
		    board.getAllValidMovesAtSquare(sq, true);
		    board.getAllValidMovesAtSquare(sq, false);
		}
		setPromotionPieces();
	}
	
	
	private void setPromotionPieces() {
		promotionPieces = new ArrayList<>();
		promotionPieces.add(new int[]{2, 9}); // rook
		promotionPieces.add(new int[]{3, 9}); // knight
		promotionPieces.add(new int[]{4, 9}); // bishop
		promotionPieces.add(new int[]{5, 9}); // queen
		
		
		
	}

	public void launchGame() {
		gameThread = new Thread(this);
		// calls the run() method
		gameThread.start();
	}
	
	
	private void update() {
		if (keyboard.printDebug) {
			System.out.println(Integer.toBinaryString(board.getCastlingRights()));
			keyboard.printDebug = false;
		}
		
		
		if (promotion) {
			promoting();
		}
		else if (!checkmate && !stalemate) {
			// MOUSE BUTTON PRESSED
			if (mouse.justClicked) {
				mouse.justClicked = false;
			    sourceCol = mouse.x / Board.SQUARE_SIZE;
			    sourceRow = mouse.y / Board.SQUARE_SIZE;

			    draggedImage = getPieceImageAt(sourceRow, sourceCol);
			    
			    int initSquare = calculateBitboardSquare(sourceRow, sourceCol);
			    draggedPieceMoves = board.getAllValidMovesAtSquare(initSquare, toMove);

			}
			if (!mouse.pressed) {
				if (draggedImage != null) {
				    int currentMouseRow = mouse.y / Board.SQUARE_SIZE;
				    int currentMouseCol = mouse.x / Board.SQUARE_SIZE;
				    
				    int initSquare = calculateBitboardSquare(sourceRow, sourceCol);
				    int finalSquare = calculateBitboardSquare(currentMouseRow, currentMouseCol);
				    
				    
				    int flag = getValidMoveFlag(initSquare, finalSquare);
				    
				    // any of the move is a promotion move
				    if (flag == 2 || flag == 3 || flag == 4 || flag == 5) {
				    	promotion = true;
				    	promotionSquare = finalSquare;
				    	
				    }
				    // if the move is found in the legal moves, make the move on the board
				    else if (flag != -1) {
				    	int move = board.generateMove(initSquare, finalSquare, flag);
				    	board.makeMove(move);
				    	toMove = !toMove;
				    	prevMove = move;
				    }
				    
				    draggedImage = null;
					draggedPieceMoves.clear();
				}
				
				// copy the display board to match the actual board
				simBoard = new Bitboard(board);
				checkmate = board.isCheckMate(toMove);
				stalemate = board.isStaleMate(toMove);
			}

		}
	}
	
	public void updateEngine() {
		engine.negamax(board, false, 5, -1000000, 1000000);
		int move = engine.getBestMove();
		board.makeMove(move);
		toMove = !toMove;
		prevMove = move;
		
		simBoard = new Bitboard(board);
		checkmate = board.isCheckMate(toMove);
		stalemate = board.isStaleMate(toMove);
		
	}
		
	
	
	private void promoting() {
		
		int flag = 0;
		if (mouse.pressed) {
			
			for (int[] piece : promotionPieces) {
				if (piece[1] == mouse.x/Board.SQUARE_SIZE && piece[0] == mouse.y/Board.SQUARE_SIZE) {
					if (piece[0] == 2) {
						flag = 3;
					}
					else if (piece[0] == 3) {
						flag = 5;
					}
					else if (piece[0] == 4) {
						flag = 4;
					}
					else if (piece[0] == 5) {
						flag = 2;
					}
				}
			}
			
			
			
			promotion = false;
			int initSquare = calculateBitboardSquare(sourceRow, sourceCol);
			int move = board.generateMove(initSquare, promotionSquare, flag);
			board.makeMove(move);
			board.printBoard();
			toMove = !toMove;
		}
	}


	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// draw a blank board on the screen
		Graphics2D g2 = (Graphics2D) g;
		blankBoard.draw(g2);
		
		// visual for the last made move
		if (prevMove != 0 && !checkmate && !stalemate) {
			int initSquare = prevMove & 0x3F;
			int finalSquare = (prevMove >> 6) & 0x3F;
			
			int initRow = (8 - 1) - (initSquare / 8);
			int initCol = (8 - 1) - (initSquare % 8);
			
			int finalRow = (8 - 1) - (finalSquare / 8);
	    	int finalCol = (8 - 1) - (finalSquare % 8);
			
	    	// modify the opacity
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
	    	
			g2.setColor(new Color(144,213,255));
			g2.fillRect(Board.SQUARE_SIZE * initCol, Board.SQUARE_SIZE * initRow, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
			g2.fillRect(Board.SQUARE_SIZE * finalCol, Board.SQUARE_SIZE * finalRow, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			
		}
		
		// draw all pieces
		simBoard.drawBoard(g2);
		
		if (draggedImage != null && !checkmate && !stalemate) {
		    
			// draw all possible moves when dragging a piece
		    for (int move : draggedPieceMoves) {
		    	
		    	int finalSquare = (move >> 6) & 0x3F;
		    	int rowBP = (8 - 1) - (finalSquare / 8);
		    	int colBP = (8 - 1) - (finalSquare % 8);
			
				int centerX = colBP * Board.SQUARE_SIZE + Board.HALF_SQUARE_SIZE;
			    int centerY = rowBP * Board.SQUARE_SIZE + Board.HALF_SQUARE_SIZE;
			    int diameter = Board.SQUARE_SIZE / 3;
			    int radius = diameter / 2;
	
			    g2.setColor(new Color(75,72,71, 128));
			    g2.fillOval(centerX - radius, centerY - radius, diameter, diameter);
		    
		    }
		    
		    

		    int currentMouseRow = mouse.y / Board.SQUARE_SIZE;
		    int currentMouseCol = mouse.x / Board.SQUARE_SIZE;
		    
		    // highlight the square under the mouse
		    int hoverSquare = calculateBitboardSquare(currentMouseRow, currentMouseCol);
		    boolean isValidSquare = false;
		    for (int move : draggedPieceMoves) {
		        int finalSquare = (move >> 6) & 0x3F;
		        if (finalSquare == hoverSquare) {
		            isValidSquare = true;
		            break;
		        }
		    }

		    int drawRow = currentMouseRow;
		    int drawCol = currentMouseCol;
		    
		    g2.setColor(isValidSquare ? new Color(255, 255, 0, 128) : new Color(255, 0, 0, 128));
		    
		    // only draw if you are moving away from current square
		    if (sourceRow != currentMouseRow || sourceCol != currentMouseCol) {
		    	g2.fillRect(drawCol * Board.SQUARE_SIZE, drawRow * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
		    }
		    
			
            g2.drawImage(draggedImage, draggedPieceX - Board.SQUARE_SIZE / 2, draggedPieceY - Board.SQUARE_SIZE / 2,
                    Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
        }
		
		// STATUS MESSAGE
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
		g2.setColor(Color.white);
		
		if (promotion) {
			g2.drawString("Promote to: ", 840, 150);
			for (int[] piece : promotionPieces) {
				if (piece[0] == 2) {
					GraphicsHelper.drawRook(2, 9, toMove, g2);
				}
				else if (piece[0] == 3) {
					GraphicsHelper.drawKnight(3, 9, toMove, g2);
				}
				else if (piece[0] == 4) {
					GraphicsHelper.drawBishop(4, 9, toMove, g2);
				}
				else if (piece[0] == 5) {
					GraphicsHelper.drawQueen(5, 9, toMove, g2);
				}
			}
		}
		
		else if (!checkmate && !stalemate) {
			if (toMove) {
				g2.drawString("White to move", 820, 50);
				if (board.isCheck()) {
					g2.setColor(Color.red);
					g2.drawString("Check!", 860, 90);
					
					
					int kingSquare = board.getKingSquare(toMove);
					
					
					if (draggedImage == null) {
					g2.setColor(Color.red);
					// modify the opacity
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
					
					
					int kingRow = (8 - 1) - (kingSquare / 8);
			    	int kingCol = (8 - 1) - (kingSquare % 8);
					g2.fillRect(kingCol*Board.SQUARE_SIZE, kingRow*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					
					}
				}
			}
			else {
				g2.drawString("Black to move", 820, 50);
				if (board.isCheck()) {
					g2.setColor(Color.red);
					g2.drawString("Check!", 860, 90);
					
					
					int kingSquare = board.getKingSquare(toMove);
					
					
					if (draggedImage == null) {
						g2.setColor(Color.red);
						// modify the opacity
						g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
						
						
						int kingRow = (8 - 1) - (kingSquare / 8);
				    	int kingCol = (8 - 1) - (kingSquare % 8);
						g2.fillRect(kingCol*Board.SQUARE_SIZE, kingRow*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
						g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					
					}
				}
			}
			
			simBoard.drawKings(g2);
		}
		else if (checkmate) {
			String s = "";
			if (!toMove) {
				s += "White Wins!";
				g2.setColor(Color.white);
				g2.drawString("1-0", 820, 130);
			}
			else {
				s += "Black Wins!";
				g2.setColor(Color.white);
				g2.drawString("0-1", 820, 130);
			}
			
			g2.drawString(s, 820, 90);
			
			g2.setColor(new Color(199, 0, 57));
			g2.drawString("Checkmate!", 820, 50);
			
			int kingSquare = board.getKingSquare(toMove);
			if (draggedImage == null) {
				
				g2.setColor(new Color(199, 0, 57));
				
				int kingRow = (8 - 1) - (kingSquare / 8);
		    	int kingCol = (8 - 1) - (kingSquare % 8);
				g2.fillRect(kingCol*Board.SQUARE_SIZE, kingRow*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
				
				simBoard.drawKings(g2);
			}
		}
		else if (stalemate) {
			String s = "Stalemate";
			
			g2.setColor(Color.white);
			g2.drawString(s, 820, 50);
			
			g2.drawString("1/2 - 1/2", 820, 90);
		}	
	}
	
	public int getValidMoveFlag(int initSquare, int finalSquare) {
		for (int move : draggedPieceMoves) {
			if (finalSquare == ((move >> 6) & 0x3F) && initSquare == (move & 0x3F)) {
				return (move >> 12) & 0xF;
			}
		}
		
		// if no moves match the desired drop square, return -1 indicating illegal move
		return -1;
	}
	
	public int calculateBitboardSquare(int row, int col) {
		int rowBB = (8 - 1) - row;
	    int colBB = (8 - 1) - col;
	    int initSquare = rowBB * 8 + colBB;
	    return initSquare;
	}
	
	public boolean getIsDragging() {
		return isDragging;
	}
	
	public void updateDraggedPosition(int x, int y) {
	    draggedPieceX = x;
	    draggedPieceY = y;
	}
	
	public BufferedImage getPieceImageAt(int row, int col) {
	    int rowBB = (8 - 1) - row;
	    int colBB = (8 - 1) - col;
	    int square = rowBB * 8 + colBB;
	    char p = board.getPieceOnSquare(square);
	    
	    simBoard.removePieceAtSquare(square);
	    
	    if (p == 0 || p == '.') {
	        return null; // empty square
	    }

	    String color = !Character.isLowerCase(p) ? "white" : "black";
	    String type;

	    switch (Character.toLowerCase(p)) {
	        case 'p': type = "pawn"; break;
	        case 'r': type = "rook"; break;
	        case 'n': type = "knight"; break;
	        case 'b': type = "bishop"; break;
	        case 'q': type = "queen"; break;
	        case 'k': type = "king"; break;
	        default: return null;
	    }

	    String imagePath = "/piece/" + color + "_" + type + ".png";
	    try {
	        return GraphicsHelper.getImage(imagePath);
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println("Failed to load image: " + imagePath);
	        return null;
	    }
	}
	
	public void preloadPieceImages() {
	    String[] colors = { "white", "black" };
	    String[] types = { "pawn", "rook", "knight", "bishop", "queen", "king" };

	    for (String color : colors) {
	        for (String type : types) {
	            String path = "/piece/" + color + "_" + type + ".png";
	            GraphicsHelper.getImage(path);
	        }
	    }
	}

	@Override
	public void addNotify() {
	    super.addNotify();
	    requestFocusInWindow();
	}


	@Override
	public void run() {
		
		// GAME LOOP
		double drawInterval = 1000000000 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while (gameThread != null) {
			// gets the exact time of the program running
			currentTime = System.nanoTime();
			
			// calculates the difference of the currentTime and the last time the game was updated (scaled by drawInterval)
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;
			
			// every 1/60th of a second, the game is updated with new information
			if (delta >= 1) {
				if (vsEngine == false) {
					update();
					repaint();
					delta--;
				}
				else {
					if (toMove) {
						update();
						repaint();
						delta--;
					}
					else {
						updateEngine();
						repaint();
						delta--;
					}
				}
			}
		}
		
	}
}

package model;

import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Stack;

import javax.imageio.ImageIO;

import view.Board;
import view.GraphicsHelper;
import view.PieceType;

public class Bitboard {
	
	// white pieces
	private long whitePawns;
	private long whiteRooks;
	private long whiteKnights;
	private long whiteBishops;
	private long whiteQueens;
	private long whiteKing;
	
	// black pieces
	private long blackPawns;
	private long blackRooks;
	private long blackKnights;
	private long blackBishops;
	private long blackQueens;
	private long blackKing;
	
	//both
	private long blackPieces;
	private long whitePieces;
	private long allPieces;
	
	private long hasNotMoved;
	
	private long enpassant;
	
	private long checkingPieces;
	
	// turn
	private boolean toMove; // true = white, false = black
	
	// move flags
	final int FLAG_QUIET = 0;
	final int FLAG_CAPTURE = 1;
	final int FLAG_PROMOTE_Q = 2;
	final int FLAG_PROMOTE_R = 3;
	final int FLAG_PROMOTE_B = 4;
	final int FLAG_PROMOTE_N = 5;
	final int FLAG_EN_PASSANT = 6;
	final int FLAG_CASTLE_KINGSIDE = 7;
	final int FLAG_CASTLE_QUEENSIDE = 8;
	// etc.
	
	//private HashMap<String, Integer> fenHistory = new HashMap<>();
	public Stack<Bitboard> gameStack;
	
	public Bitboard() {
		whitePawns = 0x000000000000FF00L;
		blackPawns = 0x00FF000000000000L;
		
		whiteRooks = 0x0000000000000081L;
		blackRooks = 0x8100000000000000L;
		
		whiteKnights = 0x0000000000000042L;
		blackKnights = 0x4200000000000000L;
		
		whiteBishops = 0x0000000000000024L;
		blackBishops = 0x2400000000000000L;
		
		whiteQueens = 0x0000000000000010L;
		blackQueens = 0x1000000000000000L;
		
		whiteKing = 0x0000000000000008L;
		blackKing = 0x0800000000000000L;
		
		blackPieces = 0xFFFF000000000000L;
		whitePieces = 0x000000000000FFFFL;
		
		allPieces = blackPieces | whitePieces;
		hasNotMoved = allPieces;
		
		enpassant = 0x0L;
		checkingPieces = 0X0L;
		
		toMove = true;
		gameStack = new Stack<>();
	}
	
	
	// copy constructor for stateStack
	public Bitboard(Bitboard board) {
		this.whitePawns = board.whitePawns;
		this.whiteRooks = board.whiteRooks;
		this.whiteBishops = board.whiteBishops;
		this.whiteKnights = board.whiteKnights;
		this.whiteQueens = board.whiteQueens;
		this.whiteKing = board.whiteKing;
		
		this.blackPawns = board.blackPawns;
		this.blackRooks = board.blackRooks;
		this.blackBishops = board.blackBishops;
		this.blackKnights = board.blackKnights;
		this.blackQueens = board.blackQueens;
		this.blackKing = board.blackKing;
		
		this.whitePieces = board.whitePieces;
		this.blackPieces = board.blackPieces;
		
		allPieces = blackPieces | whitePieces;
		this.hasNotMoved = board.hasNotMoved;
		this.enpassant = board.enpassant;
		this.checkingPieces = board.checkingPieces;
		this.toMove = board.toMove;
		
		this.gameStack = new Stack<>();
		
		for (Bitboard b : board.gameStack) {
			this.gameStack.add(b.copy());
		}
		
	}
	
	public Bitboard copy() {
		Bitboard copy = new Bitboard();

	    copy.whitePawns = this.whitePawns;
	    copy.whiteRooks = this.whiteRooks;
	    copy.whiteKnights = this.whiteKnights;
	    copy.whiteBishops = this.whiteBishops;
	    copy.whiteQueens = this.whiteQueens;
	    copy.whiteKing = this.whiteKing;

	    copy.blackPawns = this.blackPawns;
	    copy.blackRooks = this.blackRooks;
	    copy.blackKnights = this.blackKnights;
	    copy.blackBishops = this.blackBishops;
	    copy.blackQueens = this.blackQueens;
	    copy.blackKing = this.blackKing;

	    copy.whitePieces = this.whitePieces;
	    copy.blackPieces = this.blackPieces;
	    copy.allPieces = this.allPieces;

	    copy.hasNotMoved = this.hasNotMoved;
	    copy.enpassant = this.enpassant;
	    copy.checkingPieces = this.checkingPieces;
	    copy.toMove = this.toMove;

	    copy.gameStack = new Stack<>();

	    return copy;
	}
	
	// @pre must take in a integer 0-63 representing each square on chess board starting with h1
	public char getPieceOnSquare(int square) {
		long mask = 0x1L << square;
		
		if ((whitePawns & mask) != 0) return 'P';
		if ((blackPawns & mask) != 0) return 'p';
		if ((whiteRooks & mask) != 0) return 'R';
		if ((blackRooks & mask) != 0) return 'r';
		if ((whiteKnights & mask) != 0) return 'N';
		if ((blackKnights & mask) != 0) return 'n';
		if ((whiteBishops & mask) != 0) return 'B';
		if ((blackBishops & mask) != 0) return 'b';
		if ((whiteQueens & mask) != 0) return 'Q';
		if ((blackQueens & mask) != 0) return 'q';
		if ((whiteKing & mask) != 0) return 'K';
		if ((blackKing & mask) != 0) return 'k';
		
		return '.';
	}
	
	// for the view
	public PieceType getPieceTypeAt(int square) {
	    long mask = 1L << square;
	    
	    if ((whitePawns & mask) != 0) return PieceType.WP;
	    if ((whiteRooks & mask) != 0) return PieceType.WR;
	    if ((whiteKnights & mask) != 0) return PieceType.WN;
	    if ((whiteBishops & mask) != 0) return PieceType.WB;
	    if ((whiteQueens & mask) != 0) return PieceType.WQ;
	    if ((whiteKing & mask) != 0) return PieceType.WK;

	    if ((blackPawns & mask) != 0) return PieceType.BP;
	    if ((blackRooks & mask) != 0) return PieceType.BR;
	    if ((blackKnights & mask) != 0) return PieceType.BN;
	    if ((blackBishops & mask) != 0) return PieceType.BB;
	    if ((blackQueens & mask) != 0) return PieceType.BQ;
	    if ((blackKing & mask) != 0) return PieceType.BK;

	    return PieceType.EMPTY;
	}

	
	public void makeMove(int move) {
		
		gameStack.push(this.copy());
		
		//String fen = this.generateSimpleFEN();
	    //fenHistory.put(fen, fenHistory.getOrDefault(fen, 0) + 1);
		
		int initSquare = getInitialSquare(move);
		int finalSquare = getFinalSquare(move);
		int flag = getMoveFlag(move);
		
		if (initSquare < 0 || initSquare > 63 || finalSquare < 0 || finalSquare > 63) {
		    throw new IllegalArgumentException("Illegal move square: " + initSquare + " → " + finalSquare);
		}

		
		long initSquareBB = 0x1L << initSquare;
		long finalSquareBB = 0x1L << finalSquare;
		
		
		enpassant = 0x0L;
		hasNotMoved &= ~initSquareBB;
		
		if (toMove) {
			
		    // White is moving, so check if Black has a piece on finalSquare
		    blackPawns &= ~finalSquareBB;
		    blackRooks &= ~finalSquareBB;
		    blackKnights &= ~finalSquareBB;
		    blackBishops &= ~finalSquareBB;
		    blackQueens &= ~finalSquareBB;
		    blackKing &= ~finalSquareBB;
		    
		    // update total piece BBs
		    whitePieces &= ~initSquareBB;
		    whitePieces |= finalSquareBB;
		    blackPieces &= ~finalSquareBB;
		    
		    
			if ((whitePawns & initSquareBB)!= 0){
				whitePawns &= ~initSquareBB;
				
				if ( ((initSquareBB << 16) & finalSquareBB) != 0) enpassant |= (initSquareBB << 8);
				
				// en passant
				if (flag == FLAG_EN_PASSANT) {
					long enpassantSquareBB = 0x1L << (finalSquare - 8);
					blackPawns &= ~enpassantSquareBB;
					blackPieces &= ~enpassantSquareBB;
				}
				
				// promotion from pawn move
				if (flag == FLAG_PROMOTE_Q) whiteQueens |= finalSquareBB;
				else if (flag == FLAG_PROMOTE_R) whiteRooks |= finalSquareBB;
				else if (flag == FLAG_PROMOTE_B) whiteBishops |= finalSquareBB;
				else if (flag == FLAG_PROMOTE_N) whiteKnights |= finalSquareBB;
				else whitePawns |= finalSquareBB;
			}
			else if ((whiteRooks & initSquareBB) != 0) {
				whiteRooks &= ~initSquareBB;
				whiteRooks |= finalSquareBB;
			}
			else if ((whiteKnights & initSquareBB) != 0) {
				whiteKnights &= ~initSquareBB;
				whiteKnights |= finalSquareBB;
			}
			else if ((whiteBishops & initSquareBB) != 0) {
				whiteBishops &= ~initSquareBB;
				whiteBishops |= finalSquareBB;
			}
			else if ((whiteQueens & initSquareBB) != 0) {
				whiteQueens &= ~initSquareBB;
				whiteQueens |= finalSquareBB;
			}
			else if ((whiteKing & initSquareBB) != 0) {
				if (flag == FLAG_CASTLE_KINGSIDE) {
					whiteRooks &= ~0x1;
					whitePieces &= ~0x1;
					whiteRooks |= 0x4;
					whitePieces |= 0x4;
				}
				else if (flag == FLAG_CASTLE_QUEENSIDE) {
				    whiteRooks &= ~0x80;     // remove rook from b1 (square 1 << 6 = 0x40)
				    whitePieces &= ~0x80;
				    whiteRooks |= 0x10;      // add rook to a1 (square 1 << 7 = 0x80)
				    whitePieces |= 0x10;
				}

				
				whiteKing &= ~initSquareBB;
				whiteKing |= finalSquareBB;
			}	
		}
		else {
			
			
		    // Black is moving, so check if White has a piece on finalSquare
			
		    whitePawns &= ~finalSquareBB;
		    whiteRooks &= ~finalSquareBB;
		    whiteKnights &= ~finalSquareBB;
		    whiteBishops &= ~finalSquareBB;
		    whiteQueens &= ~finalSquareBB;
		    whiteKing &= ~finalSquareBB;
		    
		    // update total piece BBs
		    blackPieces &= ~initSquareBB;
		    blackPieces |= finalSquareBB;
		    whitePieces &= ~finalSquareBB;
		    
			if ((blackPawns & initSquareBB)!= 0){
				blackPawns &= ~initSquareBB;
				
				if ( (initSquareBB & (finalSquareBB << 16)) != 0) {
					
					enpassant |= (initSquareBB >>> 8);
				}
				
				// en passant
				if (flag == FLAG_EN_PASSANT) {
					long enpassantSquareBB = 0x1L << (finalSquare + 8);
					whitePawns &= ~enpassantSquareBB;
					whitePieces &= ~enpassantSquareBB;
				}
				
				// promotion from pawn move
				if (flag == FLAG_PROMOTE_Q) blackQueens |= finalSquareBB;
				else if (flag == FLAG_PROMOTE_R) blackRooks |= finalSquareBB;
				else if (flag == FLAG_PROMOTE_B) blackBishops |= finalSquareBB;
				else if (flag == FLAG_PROMOTE_N) blackKnights |= finalSquareBB;
				else blackPawns |= finalSquareBB;
			}
			else if ((blackRooks & initSquareBB) != 0) {
				blackRooks &= ~initSquareBB;
				blackRooks |= finalSquareBB;
			}
			else if ((blackKnights & initSquareBB) != 0) {
				blackKnights &= ~initSquareBB;
				blackKnights |= finalSquareBB;
			}
			else if ((blackBishops & initSquareBB) != 0) {
				blackBishops &= ~initSquareBB;
				blackBishops |= finalSquareBB;
			}
			else if ((blackQueens & initSquareBB) != 0) {
				blackQueens &= ~initSquareBB;
				blackQueens |= finalSquareBB;
			}
			else if ((blackKing & initSquareBB) != 0) {
				if (flag == FLAG_CASTLE_KINGSIDE) {
					blackRooks &= ~0x0100000000000000L;
					blackPieces &= ~0x0100000000000000L;
					blackRooks |= 0x0400000000000000L;
					blackPieces |= 0x0400000000000000L;
				}
				else if (flag == FLAG_CASTLE_QUEENSIDE) {
				    blackRooks &= ~0x8000000000000000L;  // remove rook from a8
				    blackPieces &= ~0x8000000000000000L;
				    blackRooks |= 0x1000000000000000L;   // add rook to d8
				    blackPieces |= 0x1000000000000000L;
				}

				
				blackKing &= ~initSquareBB;
				blackKing |= finalSquareBB;
			}	
				
		}
		
		// get opposing king square
		int kingSquare = getKingSquare(!toMove);
		allPieces = blackPieces | whitePieces;
		updateCheckingPieces(kingSquare, toMove);
		//if (checkingPieces != 0) System.out.println("CHECK!");
		
		toMove = !toMove;
		
		//if (isCheckMate(toMove)) System.out.println("Checkmate!");
		//if (isStaleMate(toMove)) System.out.println("Stalemate!");
	}
	
	public boolean isValidMove(int move) {
		int initSquare = getInitialSquare(move);
		int finalSquare = getFinalSquare(move);
		
		long initSquareBB = 0x1L << initSquare;
		long finalSquareBB = 0x1L << finalSquare;
		
		if (toMove) {
			if ((finalSquareBB & whitePieces) != 0) return false;
			
			if ((whitePawns & initSquareBB) != 0) return simulateMoveIsLegal(move) && isValidMovePawn(move);
			else if ((whiteRooks & initSquareBB) != 0) return simulateMoveIsLegal(move) && isValidMoveRook(move);
			else if ((whiteKnights & initSquareBB) != 0) return simulateMoveIsLegal(move) && isValidMoveKnight(move);
			else if ((whiteBishops & initSquareBB) != 0) return simulateMoveIsLegal(move) && isValidMoveBishop(move);
			else if ((whiteQueens & initSquareBB) != 0) return simulateMoveIsLegal(move) && isValidMoveQueen(move);
			else if ((whiteKing & initSquareBB) != 0) return simulateMoveIsLegal(move) && isValidMoveKing(move);
			else return false;
		}
		else {
			if ((finalSquareBB & blackPieces) != 0) return false;
			
			if ((blackPawns & initSquareBB)!= 0) return simulateMoveIsLegal(move) && isValidMovePawn(move);
			else if ((blackRooks & initSquareBB) != 0) return simulateMoveIsLegal(move) && isValidMoveRook(move);
			else if ((blackKnights & initSquareBB) != 0) return simulateMoveIsLegal(move) && isValidMoveKnight(move);
			else if ((blackBishops & initSquareBB) != 0) return simulateMoveIsLegal(move) && isValidMoveBishop(move);
			else if ((blackQueens & initSquareBB) != 0) return simulateMoveIsLegal(move) && isValidMoveQueen(move);
			else if ((blackKing & initSquareBB) != 0) return simulateMoveIsLegal(move) && isValidMoveKing(move);
			else return false;
		}
	}
	
	private boolean isValidMoveKing(int move) {
		int initSquare = getInitialSquare(move);
		int finalSquare = getFinalSquare(move);
		int flag = getMoveFlag(move);

		
		if (flag == FLAG_CASTLE_KINGSIDE) {
			
		    if (initSquare == 3) {
		    	if ((hasNotMoved & 1L) == 0) return false;
		        // g1 = 1, f1 = 2
		        return (allPieces & ((1L << 1) | (1L << 2))) == 0;
		    }
		    if (initSquare == 59) {
		    	if ((hasNotMoved & (1L << 56)) == 0) return false;
		        // g8 = 57, f8 = 58
		        return (allPieces & ((1L << 57) | (1L << 58))) == 0;
		    }
		}


	    if (flag == FLAG_CASTLE_QUEENSIDE) {
	        if (initSquare == 3) {
	        	if ((hasNotMoved & (1L << 7)) == 0) return false;
	            return (allPieces & ((1L << 4) | (1L << 5) | (1L << 6))) == 0;
	        }
	        if (initSquare == 59) {
	        	if ((hasNotMoved & (1L << 63)) == 0) return false;
	            return (allPieces & ((1L << 60) | (1L << 61) | (1L << 62))) == 0;
	        }
	    }
		
		long attackMask = MoveGenerator.getKingAttackMasks()[initSquare];
		long finalSquareBB = 0x1L << finalSquare;
		
		
		return ((attackMask & finalSquareBB) != 0);
	}

	private boolean isValidMoveQueen(int move) {
		
		int initSquare = getInitialSquare(move);
		int finalSquare = getFinalSquare(move);
		
		long attackMask = MoveGenerator.getQueenAttacks(initSquare, allPieces);
		long finalSquareBB = 0x1L << finalSquare;
		
		
		return ((attackMask & finalSquareBB) != 0);
	}

	private boolean isValidMoveBishop(int move) {
		int initSquare = getInitialSquare(move);
		int finalSquare = getFinalSquare(move);
		
		long attackMask = MoveGenerator.getBishopAttacks(initSquare, allPieces);
		long finalSquareBB = 0x1L << finalSquare;
		
		
		return ((attackMask & finalSquareBB) != 0);
	}

	private boolean isValidMoveRook(int move) {
		int initSquare = getInitialSquare(move);
		int finalSquare = getFinalSquare(move);
		
		long attackMask = MoveGenerator.getRookAttacks(initSquare, allPieces);
		long finalSquareBB = 0x1L << finalSquare;
		
		
		return ((attackMask & finalSquareBB) != 0);
	}

	private boolean isValidMovePawn(int move) {
		int initSquare = getInitialSquare(move);
		int finalSquare = getFinalSquare(move);
		int flag = getMoveFlag(move);
		
		long initSquareBB = 0x1L << initSquare;
		long finalSquareBB = 0x1L << finalSquare;
		
		
		long moveMask = toMove ? MoveGenerator.getWhitePawnMoveMasks()[initSquare] : MoveGenerator.getBlackPawnMoveMasks()[initSquare];
		long attackMask = toMove ? MoveGenerator.getWhitePawnAttackMasks()[initSquare] : MoveGenerator.getBlackPawnAttackMasks()[initSquare];
		
		// Check for en passant — assume flag 2 means en passant
	    if (flag == FLAG_EN_PASSANT && (enpassant & finalSquareBB) != 0) {
	        return true;
	    }
		
		// when moving forward twice
		if (((initSquareBB << 16) & finalSquareBB) != 0) {
			// piece in the way
			if ( ((initSquareBB << 8) & allPieces) != 0) return false;
		}
		if (((initSquareBB >>> 16) & finalSquareBB) != 0) {
			// piece in the way
			if ( ((initSquareBB >>> 8) & allPieces) != 0) return false;
		}

		return (((moveMask | attackMask) & finalSquareBB) != 0);
	}

	private boolean isValidMoveKnight(int move) {
		int initSquare = getInitialSquare(move);
		int finalSquare = getFinalSquare(move);
		
		long attackMask = MoveGenerator.getKnightAttackMasks()[initSquare];
		long finalSquareBB = 0x1L << finalSquare;
		
		return ((attackMask & finalSquareBB) != 0);
		
	}
	
	// handling checkmate
	
	public boolean isCheckMate(boolean toMove) {
		if (checkingPieces != 0 && generateAllLegalMoves(toMove).size() == 0) return true;
		else return false;
	}
	
	// handling stalemate
	
	public boolean isStaleMate(boolean toMove) {
		if (checkingPieces == 0 && generateAllLegalMoves(toMove).size() == 0) return true;
		else return false;
	}
	
	
	// handling checks
	
	public void updateCheckingPieces(int kingSquare, boolean toMove) {
		checkingPieces = 0L;
		
		if (toMove) {
			checkingPieces |= MoveGenerator.getKnightAttackMasks()[kingSquare] & whiteKnights;
			checkingPieces |= MoveGenerator.getBishopAttacks(kingSquare, allPieces) & whiteBishops;
			checkingPieces |= MoveGenerator.getRookAttacks(kingSquare, allPieces) & whiteRooks;
			checkingPieces |= MoveGenerator.getQueenAttacks(kingSquare, allPieces) & whiteQueens;
			checkingPieces |= MoveGenerator.getPawnAttacksTo(kingSquare, true) & whitePawns;
		}
		else {
			checkingPieces |= MoveGenerator.getKnightAttackMasks()[kingSquare] & blackKnights;
			checkingPieces |= MoveGenerator.getBishopAttacks(kingSquare, allPieces) & blackBishops;
			checkingPieces |= MoveGenerator.getRookAttacks(kingSquare, allPieces) & blackRooks;
			checkingPieces |= MoveGenerator.getQueenAttacks(kingSquare, allPieces) & blackQueens;
			checkingPieces |= MoveGenerator.getPawnAttacksTo(kingSquare, false) & blackPawns;
		}
		
		// check for illegal king contact (used for legality)
		long enemyKing = toMove ? whiteKing : blackKing;
		checkingPieces |= MoveGenerator.getKingAttackMasks()[kingSquare] & enemyKing;
		
	}
	
	public boolean isCheck() {
		return (checkingPieces != 0);
	}
	
	
	public boolean simulateMoveIsLegal(int move) {
		//if (getFinalSquare(move) == getKingSquare(!toMove)) return false;
		
	    makeMove(move);
	    
	    int kingSquare = getKingSquare(!toMove); // king of the moving side
	    
	    updateCheckingPieces(kingSquare, toMove);

	    boolean legal = checkingPieces == 0;
	    undoMove();

	    return legal;
	}
	
	public Double simulateMoveIsLegalScored(int move, boolean toMoveBoard) {
		//if (getFinalSquare(move) == getKingSquare(!toMove)) return null;
		
	    makeMove(move);
	    
	    int kingSquare = getKingSquare(!toMove); // King of the moving side
	    
	    updateCheckingPieces(kingSquare, toMove);

	    boolean legal = checkingPieces == 0;
	    Double eval = legal ? this.evaluate(toMove) : null;
	    undoMove();
	    
	    return eval;
	}
	
	public void undoMove() {
		Bitboard board = gameStack.pop();
		
		this.whitePawns = board.whitePawns;
		this.whiteRooks = board.whiteRooks;
		this.whiteBishops = board.whiteBishops;
		this.whiteKnights = board.whiteKnights;
		this.whiteQueens = board.whiteQueens;
		this.whiteKing = board.whiteKing;
		
		this.blackPawns = board.blackPawns;
		this.blackRooks = board.blackRooks;
		this.blackBishops = board.blackBishops;
		this.blackKnights = board.blackKnights;
		this.blackQueens = board.blackQueens;
		this.blackKing = board.blackKing;
		
		this.whitePieces = board.whitePieces;
		this.blackPieces = board.blackPieces;
		
		allPieces = blackPieces | whitePieces;
		this.hasNotMoved = board.hasNotMoved;
		this.enpassant = board.enpassant;
		this.checkingPieces = board.checkingPieces;
		this.toMove = board.toMove;
		
		/*
		// Remove current FEN from history
	    String fen = this.generateSimpleFEN();
	    fenHistory.put(fen, fenHistory.getOrDefault(fen, 1) - 1);
	    if (fenHistory.get(fen) <= 0) {
	        fenHistory.remove(fen);
	    }
	    */
	}
	
	// ZOBRIST HASHING METHODS
	
	public int getKingSquare(boolean toMove) {
		long kingBB = toMove ? whiteKing : blackKing;
		return Long.numberOfTrailingZeros(kingBB);
	}
	
	public int getCastlingRights() {
		int rights = 0;
		
		for (int i = 0; i < 2; i++) {
			// king and rook have not been moved
			if ((hasNotMoved & (1L << (3 + 56 * i))) != 0 && (hasNotMoved & 1L << (56 * i)) != 0) rights |= 1L << (2 * i);
			if ((hasNotMoved & (1L << (3 + 56 * i))) != 0 && (hasNotMoved & 1L << (7 + 56 * i)) != 0) rights |= 1L << (2* i + 1);
		}
		
		return rights;
	}
	
	public int enpassantFile() {
		long fileH = 0x0101010101010101L;
		
		for (int i = 0; i < 7; i++) {
			if ((enpassant & (fileH << i)) != 0) return i;
		}
		
		return -1;
	}
	
	public ArrayList<Integer> generateAllLegalMoves(boolean toMove) {
		
		ArrayList<Integer> legalMoves = new ArrayList<Integer>();
		long colorPieces = toMove ? whitePieces : blackPieces;
		
		long knights = toMove ? whiteKnights : blackKnights;
		
		while (knights != 0) {
			int initSquare = Long.numberOfTrailingZeros(knights);
			long initMask = 1L << initSquare;
			
			// remove the knight in order to move to next knight's move generation
			knights &= ~initMask; 
			
			// attack mask that can not move onto a white piece
			long attackMask = MoveGenerator.getKnightAttackMasks()[initSquare] & ~colorPieces;
			
			while (attackMask != 0) {
				int finalSquare = Long.numberOfTrailingZeros(attackMask);
				long finalMask = 1L <<  finalSquare;
				
				// same as before with the knight
				attackMask &= ~finalMask;
				
				// all knight moves are 0 flag moves (for now)
				int move = generateMove(initSquare, finalSquare, 0);
				
				if (simulateMoveIsLegal(move)) legalMoves.add(move);
			}
			
		}
		
		long rooks = toMove ? whiteRooks : blackRooks;
		
		while (rooks != 0) {
			int initSquare = Long.numberOfTrailingZeros(rooks);
			long initMask = 1L << initSquare;
			
			// remove the rook in order to move to next rook's move generation
			rooks &= ~initMask; 
			
			// attack mask that can not move onto a white piece
			long attackMask = MoveGenerator.getRookAttacks(initSquare, allPieces) & ~colorPieces;
			
			while (attackMask != 0) {
				int finalSquare = Long.numberOfTrailingZeros(attackMask);
				long finalMask = 1L <<  finalSquare;
				
				// same as before with the rook
				attackMask &= ~finalMask;
				
				// all rook moves are 0 flag moves (for now) NOTE: CASTLING IS A KING MOVE
				int move = generateMove(initSquare, finalSquare, 0);
				
				if (simulateMoveIsLegal(move)) legalMoves.add(move);
			}
			
		}
		
		long bishops = toMove ? whiteBishops : blackBishops;
		
		while (bishops != 0) {
			int initSquare = Long.numberOfTrailingZeros(bishops);
			long initMask = 1L << initSquare;
			
			// remove the bishop in order to move to next bishop's move generation
			bishops &= ~initMask; 
			
			// attack mask that can not move onto a white piece
			long attackMask = MoveGenerator.getBishopAttacks(initSquare, allPieces) & ~colorPieces;
			
			while (attackMask != 0) {
				int finalSquare = Long.numberOfTrailingZeros(attackMask);
				long finalMask = 1L <<  finalSquare;
				
				// same as before with the bishop
				attackMask &= ~finalMask;
				
				// all bishop moves are 0 flag moves (for now)
				int move = generateMove(initSquare, finalSquare, 0);
				
				if (simulateMoveIsLegal(move)) legalMoves.add(move);
			}
			
		}
		
		long queens = toMove ? whiteQueens : blackQueens;
		
		while (queens != 0) {
			int initSquare = Long.numberOfTrailingZeros(queens);
			long initMask = 1L << initSquare;
			
			// remove the bishop in order to move to next bishop's move generation
			queens &= ~initMask; 
			
			// attack mask that can not move onto a white piece
			long attackMask = MoveGenerator.getQueenAttacks(initSquare, allPieces) & ~colorPieces;
			
			while (attackMask != 0) {
				int finalSquare = Long.numberOfTrailingZeros(attackMask);
				long finalMask = 1L <<  finalSquare;
				
				// same as before with the bishop
				attackMask &= ~finalMask;
				
				// all queen moves are 0 flag moves (for now)
				int move = generateMove(initSquare, finalSquare, 0);
				
				if (simulateMoveIsLegal(move)) legalMoves.add(move);
			}
			
		}
		
		long pawns = toMove ? whitePawns : blackPawns;
		
		while (pawns != 0) {
			int initSquare = Long.numberOfTrailingZeros(pawns);
			long initMask = 1L << initSquare;
			
			// remove the bishop in order to move to next bishop's move generation
			pawns &= ~initMask; 
			
			
			// pawns have both a moveMask and an attackMask we must consider
			long moveMask = toMove ? MoveGenerator.getWhitePawnMoveMasks()[initSquare] : MoveGenerator.getBlackPawnMoveMasks()[initSquare];
			long attackMask = toMove ? MoveGenerator.getWhitePawnAttackMasks()[initSquare] : MoveGenerator.getBlackPawnAttackMasks()[initSquare];
			
			// can't move on top of pieces
			moveMask &= ~allPieces;
			
			long opposite = toMove ? blackPieces : whitePieces;
			
			// add enpassant square to opposite sides pieces
			if ((attackMask & enpassant) != 0) opposite |= enpassant;
			
			// can only attack opposite pieces
			attackMask &= opposite;
			
			while (moveMask != 0) {
				int finalSquare = Long.numberOfTrailingZeros(moveMask);
				long finalMask = 1L <<  finalSquare;
				
				// same as before with the pawn
				moveMask &= ~finalMask;
				
				int move;
				
				// if pawn is on the back rank, move is a promotion
				if ((finalSquare >= 0 && finalSquare <= 7) || (finalSquare >= 56 && finalSquare <= 63)) {
					// loop through all possible promotions
					for (int i = 2; i < 7; i++) {
						move = generateMove(initSquare, finalSquare, i);
						if (simulateMoveIsLegal(move)) legalMoves.add(move);
					}
				}
				else {
					// if the pawn move is not a promotion, then it has no flag (for now)
					move = generateMove(initSquare, finalSquare, 0);
					if (simulateMoveIsLegal(move)&& isValidMovePawn(move)) legalMoves.add(move);
				}
			}
			
			while (attackMask != 0) {
				int finalSquare = Long.numberOfTrailingZeros(attackMask);
				long finalMask = 1L <<  finalSquare;
				
				attackMask &= ~finalMask;
				
				int move;
				
				// if pawn is on the back rank, move is a promotion
				if ((finalSquare >= 0 && finalSquare <= 7) || (finalSquare >= 56 && finalSquare <= 63)) {
					// loop through all possible promotions
					for (int i = 2; i < 7; i++) {
						move = generateMove(initSquare, finalSquare, i);
						if (simulateMoveIsLegal(move)) legalMoves.add(move);
					}
				}
				
				// if pawn is taking on an enpassant square, mark that flag
				else if ((finalMask & enpassant) != 0) {
					move = generateMove(initSquare, finalSquare, FLAG_EN_PASSANT);
					if (simulateMoveIsLegal(move)) legalMoves.add(move);
				}
				else {
					// if the pawn attack is not enpassant, then it is zero (for now)
					move = generateMove(initSquare, finalSquare, 0);
					if (simulateMoveIsLegal(move)) legalMoves.add(move);
				}
				
			}
			
		}
		
		long king = toMove ? whiteKing : blackKing;
		
		// no while loop because only can have one king per color
		int initSquare = Long.numberOfTrailingZeros(king);
		long initMask = 1L << initSquare;
		
		long attackMask = MoveGenerator.getKingAttackMasks()[initSquare] & ~colorPieces;

		long possibleCastling = MoveGenerator.possibleCastling(toMove, hasNotMoved) & ~allPieces;
		
		attackMask |= possibleCastling;
		
		while (attackMask != 0) {
			int finalSquare = Long.numberOfTrailingZeros(attackMask);
			long finalMask = 1L <<  finalSquare;
			
			attackMask &= ~finalMask;
			
			int move;
			
			// if king moves twice (castling)
			if (Math.abs(finalSquare - initSquare) == 2) {
				
				int flag = finalSquare < initSquare ? FLAG_CASTLE_KINGSIDE : FLAG_CASTLE_QUEENSIDE;
				
				// check if rook is in corner
				boolean rookPresent = false;
				
				if (toMove) {
					if (flag == FLAG_CASTLE_KINGSIDE) rookPresent = (whiteRooks & (1L << 0)) != 0;
					else rookPresent = (whiteRooks & (1L << 7)) != 0;
					} 
				else {
					if (flag ==  FLAG_CASTLE_KINGSIDE) rookPresent = (blackRooks & (1L << 56)) != 0;
					else rookPresent = (blackRooks & (1L << 63)) != 0;
				}

			    int inBetweenSquare = initSquare + (finalSquare - initSquare) / 2;
			    int inBetweenMove = generateMove(initSquare, inBetweenSquare, 0);
				
				move = generateMove(initSquare, finalSquare, flag);
				
				// can't castle through check (isValidMoveKing ensures castling is not through pieces)
				if (rookPresent && simulateMoveIsLegal(move) && simulateMoveIsLegal(inBetweenMove) && isValidMoveKing(move) && (checkingPieces == 0)) {
					legalMoves.add(move);
				}
			}
			else {
				// if the king move is not a castling move, then it has no flag (for now)
				move = generateMove(initSquare, finalSquare, 0);
				if (simulateMoveIsLegal(move)) legalMoves.add(move);
			}
			
		}
		
		
		return legalMoves;
		
	}
	
	
	public ArrayList<MoveSet> generateLegalMovesOrdered(boolean toMove) {
	    ArrayList<MoveSet> allLegalMoves = generateAllLegalMovesScored(toMove);
	    ArrayList<MoveSet> captures = new ArrayList<>();
	    ArrayList<MoveSet> promotions = new ArrayList<>();
	    ArrayList<MoveSet> checks = new ArrayList<>();
	    ArrayList<MoveSet> quietMoves = new ArrayList<>();

	    long opponentPieces = toMove ? blackPieces : whitePieces;

	    for (MoveSet moveSet : allLegalMoves) {
	        int move = moveSet.getMove();
	        int targetSquare = getFinalSquare(move);
	        long targetMask = 1L << targetSquare;

	        // 1. Promotion
	        if (isPromotion(move)) {
	            promotions.add(new MoveSet(move, 1000)); // High base score
	            continue;
	        }

	        // 2. Capture (MVV-LVA)
	        if ((targetMask & opponentPieces) != 0) {
	            int attacker = getType(move, true);
	            int victim = getType(move, false);
	            double score = MoveOrder.getMVVLVAValue(attacker, victim);
	            captures.add(new MoveSet(move, score));
	            continue;
	        }

	        // 3. Check (non-capture)
	        makeMove(move);
	        updateCheckingPieces(getKingSquare(!toMove), toMove);
	        if (checkingPieces != 0) {
	        	double score = generateAllLegalMovesScored(toMove).isEmpty() ? 100000 : 50;
	        	checks.add(new MoveSet(move, score)); 
	        } else {
	            quietMoves.add(new MoveSet(move, 0));
	        }
	        undoMove();
	    }

	    captures.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
	    checks.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
	    promotions.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));


	    ArrayList<MoveSet> ordered = new ArrayList<>();
	    
	    // promotions first, then captures, then checks, then quiet moves
	    ordered.addAll(promotions);
	    ordered.addAll(captures);
	    ordered.addAll(checks);
	    ordered.addAll(quietMoves);

	    return ordered;
	}

	
	
	public ArrayList<int[]> getCaptureAndChecks(boolean toMove) {
		ArrayList<Integer> allLegalMoves = generateAllLegalMoves(toMove);
		ArrayList<int[]> allCaptureAndChecks = new ArrayList<>();
		
		long opponentPieces = toMove ? blackPieces : whitePieces;
		
		for (int move : allLegalMoves) {
			int targetSquare = getFinalSquare(move);
			long targetMask = 1L << targetSquare;
			
			if ((targetMask & opponentPieces) != 0) {
				int attackerIndex = getType(move, true);
				int victimIndex = getType(move, false);
				int score = MoveOrder.getMVVLVAValue(attackerIndex, victimIndex);
				
				allCaptureAndChecks.add(new int[] {move, score});
				continue;
			}
			
			makeMove(move);
		    boolean givesCheck = isCheck();

		    // check if it's a quiet move that leads to checkmate
		    boolean isMate = isCheckMate(!toMove);
		    undoMove();

		    if (givesCheck || isMate) {
		        int attackerIndex = getType(move, true);
		        int victimIndex = getType(move, false);
		        int score = isMate ? 100000 : MoveOrder.getMVVLVAValue(attackerIndex, victimIndex);

		        allCaptureAndChecks.add(new int[] {move, score});
		    }
		}
		
		// sort by score - descending order
		Collections.sort(allCaptureAndChecks, new Comparator<int[]>() {
	        @Override
	        public int compare(int[] arr1, int[] arr2) {
	            return Integer.compare(arr2[1], arr1[1]);
	        }});
		
		return allCaptureAndChecks;
	}
	
	
	boolean isKingAt(int square) {
	    return (whiteKing & (1L << square)) != 0 || (blackKing & (1L << square)) != 0;
	}

	
	public int getType(int move, boolean isAttacker) {
		int square = isAttacker ? getInitialSquare(move) : getFinalSquare(move);
		switch(Character.toLowerCase(getPieceOnSquare(square))) {
			case 'p': 
				return 0;
			case 'n':
				return 1;
			case 'b':
				return 2;
			case 'r':
				return 3;
			case 'q':
				return 4;
			case 'k':
				return 5;
			default:
				return -1;
		}
	}
	
	
	public ArrayList<MoveSet> generateAllLegalMovesScored(boolean toMove) {
		
		ArrayList<MoveSet> legalMoves = new ArrayList<>();
		long colorPieces = toMove ? whitePieces : blackPieces;
		
		long knights = toMove ? whiteKnights : blackKnights;
		
		while (knights != 0) {
			int initSquare = Long.numberOfTrailingZeros(knights);
			long initMask = 1L << initSquare;
			
			// remove the knight in order to move to next knight's move generation
			knights &= ~initMask; 
			
			// attack mask that can not move onto a white piece
			long attackMask = MoveGenerator.getKnightAttackMasks()[initSquare] & ~colorPieces;
			
			while (attackMask != 0) {
				int finalSquare = Long.numberOfTrailingZeros(attackMask);
				long finalMask = 1L <<  finalSquare;
				
				// same as before with the knight
				attackMask &= ~finalMask;
				
				// all knight moves are 0 flag moves (for now)
				int move = generateMove(initSquare, finalSquare, 0);
				
				Double eval = simulateMoveIsLegalScored(move, toMove);
				if (eval != null) legalMoves.add(new MoveSet(move, eval));
			}
			
		}
		
		long rooks = toMove ? whiteRooks : blackRooks;
		
		while (rooks != 0) {
			int initSquare = Long.numberOfTrailingZeros(rooks);
			long initMask = 1L << initSquare;
			
			// remove the rook in order to move to next rook's move generation
			rooks &= ~initMask; 
			
			// attack mask that can not move onto a white piece
			long attackMask = MoveGenerator.getRookAttacks(initSquare, allPieces) & ~colorPieces;
			
			while (attackMask != 0) {
				int finalSquare = Long.numberOfTrailingZeros(attackMask);
				long finalMask = 1L <<  finalSquare;
				
				// same as before with the rook
				attackMask &= ~finalMask;
				
				// all rook moves are 0 flag moves (for now) NOTE: CASTLING IS A KING MOVE
				int move = generateMove(initSquare, finalSquare, 0);
				
				Double eval = simulateMoveIsLegalScored(move, toMove);
				if (eval != null) legalMoves.add(new MoveSet(move, eval));
			}
			
		}
		
		long bishops = toMove ? whiteBishops : blackBishops;
		
		while (bishops != 0) {
			int initSquare = Long.numberOfTrailingZeros(bishops);
			long initMask = 1L << initSquare;
			
			// remove the bishop in order to move to next bishop's move generation
			bishops &= ~initMask; 
			
			// attack mask that can not move onto a white piece
			long attackMask = MoveGenerator.getBishopAttacks(initSquare, allPieces) & ~colorPieces;
			
			while (attackMask != 0) {
				int finalSquare = Long.numberOfTrailingZeros(attackMask);
				long finalMask = 1L <<  finalSquare;
				
				// same as before with the bishop
				attackMask &= ~finalMask;
				
				// all bishop moves are 0 flag moves (for now)
				int move = generateMove(initSquare, finalSquare, 0);
				
				Double eval = simulateMoveIsLegalScored(move, toMove);
				if (eval != null) legalMoves.add(new MoveSet(move, eval));
			}
			
		}
		
		long queens = toMove ? whiteQueens : blackQueens;
		
		while (queens != 0) {
			int initSquare = Long.numberOfTrailingZeros(queens);
			long initMask = 1L << initSquare;
			
			// remove the bishop in order to move to next bishop's move generation
			queens &= ~initMask; 
			
			// attack mask that can not move onto a white piece
			long attackMask = MoveGenerator.getQueenAttacks(initSquare, allPieces) & ~colorPieces;
			
			while (attackMask != 0) {
				int finalSquare = Long.numberOfTrailingZeros(attackMask);
				long finalMask = 1L <<  finalSquare;
				
				// same as before with the bishop
				attackMask &= ~finalMask;
				
				// all queen moves are 0 flag moves (for now)
				int move = generateMove(initSquare, finalSquare, 0);
				
				Double eval = simulateMoveIsLegalScored(move, toMove);
				if (eval != null) legalMoves.add(new MoveSet(move, eval));
			}
			
		}
		
		long pawns = toMove ? whitePawns : blackPawns;
		
		while (pawns != 0) {
			int initSquare = Long.numberOfTrailingZeros(pawns);
			long initMask = 1L << initSquare;
			
			// remove the bishop in order to move to next bishop's move generation
			pawns &= ~initMask; 
			
			
			// pawns have both a moveMask and an attackMask we must consider
			long moveMask = toMove ? MoveGenerator.getWhitePawnMoveMasks()[initSquare] : MoveGenerator.getBlackPawnMoveMasks()[initSquare];
			long attackMask = toMove ? MoveGenerator.getWhitePawnAttackMasks()[initSquare] : MoveGenerator.getBlackPawnAttackMasks()[initSquare];
			
			// can't move on top of pieces
			moveMask &= ~allPieces;
			
			long opposite = toMove ? blackPieces : whitePieces;
			
			// add enpassant square to opposite sides pieces
			if ((attackMask & enpassant) != 0) opposite |= enpassant;
			
			// can only attack opposite pieces
			attackMask &= opposite;
			
			while (moveMask != 0) {
				int finalSquare = Long.numberOfTrailingZeros(moveMask);
				long finalMask = 1L <<  finalSquare;
				
				// same as before with the pawn
				moveMask &= ~finalMask;
				
				int move;
				
				// if pawn is on the back rank, move is a promotion
				if ((finalSquare >= 0 && finalSquare <= 7) || (finalSquare >= 56 && finalSquare <= 63)) {
					// loop through all possible promotions
					for (int i = 2; i < 7; i++) {
						move = generateMove(initSquare, finalSquare, i);
						Double eval = simulateMoveIsLegalScored(move, toMove);
						if (eval != null) legalMoves.add(new MoveSet(move, eval));
					}
				}
				else {
					// if the pawn move is not a promotion, then it has no flag (for now)
					move = generateMove(initSquare, finalSquare, 0);
					Double eval = simulateMoveIsLegalScored(move, toMove);
					if (eval != null && isValidMovePawn(move)) legalMoves.add(new MoveSet(move, eval));
				}
			}
			
			while (attackMask != 0) {
				int finalSquare = Long.numberOfTrailingZeros(attackMask);
				long finalMask = 1L <<  finalSquare;
				
				attackMask &= ~finalMask;
				
				int move;
				
				// if pawn is on the back rank, move is a promotion
				if ((finalSquare >= 0 && finalSquare <= 7) || (finalSquare >= 56 && finalSquare <= 63)) {
					// loop through all possible promotions
					for (int i = 2; i < 7; i++) {
						move = generateMove(initSquare, finalSquare, i);
						Double eval = simulateMoveIsLegalScored(move, toMove);
						if (eval != null) legalMoves.add(new MoveSet(move, eval));
					}
				}
				
				// if pawn is taking on an enpassant square, mark that flag
				else if ((finalMask & enpassant) != 0) {
					move = generateMove(initSquare, finalSquare, FLAG_EN_PASSANT);
					Double eval = simulateMoveIsLegalScored(move, toMove);
					if (eval != null) legalMoves.add(new MoveSet(move, eval));
				}
				else {
					// if the pawn attack is not enpassant, then it is zero (for now)
					move = generateMove(initSquare, finalSquare, 0);
					Double eval = simulateMoveIsLegalScored(move, toMove);
					if (eval != null) legalMoves.add(new MoveSet(move, eval));
				}
				
			}
			
		}
		
		long king = toMove ? whiteKing : blackKing;
		
		// no while loop because only can have one king per color
		int initSquare = Long.numberOfTrailingZeros(king);
		long initMask = 1L << initSquare;
		
		long attackMask = MoveGenerator.getKingAttackMasks()[initSquare] & ~colorPieces;

		long possibleCastling = MoveGenerator.possibleCastling(toMove, hasNotMoved) & ~(allPieces);

		
		attackMask |= possibleCastling;
		
		while (attackMask != 0) {
			int finalSquare = Long.numberOfTrailingZeros(attackMask);
			long finalMask = 1L <<  finalSquare;
			
			attackMask &= ~finalMask;
			
			int move;
			
			// if king moves twice (castling)
			if (Math.abs(finalSquare - initSquare) == 2) {
				
				int flag = finalSquare < initSquare ? FLAG_CASTLE_KINGSIDE : FLAG_CASTLE_QUEENSIDE;
				
				// check if rook is in corner
				boolean rookPresent = false;
				
				if (toMove) {
					if (flag == FLAG_CASTLE_KINGSIDE) rookPresent = (whiteRooks & (1L << 0)) != 0;
					else rookPresent = (whiteRooks & (1L << 7)) != 0;
					} 
				else {
					if (finalSquare < initSquare) rookPresent = (blackRooks & (1L << 56)) != 0;
					else rookPresent = (blackRooks & (1L << 63)) != 0;
				}

			    int inBetweenSquare = initSquare + (finalSquare - initSquare) / 2;
			    int inBetweenMove = generateMove(initSquare, inBetweenSquare, 0);
				
				move = generateMove(initSquare, finalSquare, flag);
				
				// can't castle through check (isValidMoveKing ensures castling is not through pieces)
				Double eval = simulateMoveIsLegalScored(move, toMove);
				
				if (rookPresent && eval != null && simulateMoveIsLegal(inBetweenMove) && isValidMoveKing(move) && (checkingPieces == 0)) {
					legalMoves.add(new MoveSet(move, eval));
				}
			}
			else {
				// if the king move is not a castling move, then it has no flag (for now)
				move = generateMove(initSquare, finalSquare, 0);
				Double eval = simulateMoveIsLegalScored(move, toMove);
				if (eval != null) legalMoves.add(new MoveSet(move, eval));
			}
			
		}
		
		
		return legalMoves;
		
	}	
	
	public int generateMove(int initialSquare, int finalSquare, int flag) {
		return initialSquare | (finalSquare << 6) | (flag << 12);
	}
	
	public String getTurn() {
		if (toMove) return "White";
		else return "Black";
	}
	
	public boolean getToMove() {
		return toMove;
	}
	
	public int getMoveFlag(int move) {
		return (move >> 12) & 0xF;
	}
	
	public int getInitialSquare(int move) {
		return move & 0x3F;
	}
	
	public int getFinalSquare(int move) {
		return (move >> 6) & 0x3F;
	}
	
	private boolean isPromotion(int move) {
	    int promotionFlag = (move >> 12) & 0xF;
	    if (promotionFlag >= FLAG_PROMOTE_Q && promotionFlag <= FLAG_PROMOTE_N) return true;
	    else return false;
	}

	
	public void nextTurn() {
		toMove = !toMove;
	}
	
	public double evaluate(boolean toMove) {
	    return Math.random(); // Temporarily for debugging
	}
	
	// EVALUATE
	
	public String generateSimpleFEN() {
		String fen = "";
		for (int rank = 7; rank >= 0; rank--) {
			String innerString = "";
			int count = 0;
			for (int file = 0; file < 8; file++) {
				int square = rank * 8 + (7 - file);
				char piece = getPieceOnSquare(square);
				if (piece == '.') count++;
				else {
					if (count != 0) innerString += count;
					
					innerString += piece;
					count = 0;
				}
			}
			
			if (count > 0) innerString += count;
			
			if (rank != 0) innerString += "/";
			fen += innerString;
		}
		
		return fen;
	}
	
	
	public double evaluate() {
		// material eval
		double eval = 5.0 * (Long.bitCount(whiteRooks) - Long.bitCount(blackRooks))
					+ 3.5 * (Long.bitCount(whiteBishops) - Long.bitCount(blackBishops))
					+ 3.0 * (Long.bitCount(whiteKnights) - Long.bitCount(blackKnights))
					+ 9.0 * (Long.bitCount(whiteQueens) - Long.bitCount(blackQueens))
					+ 1.0 * (Long.bitCount(whitePawns) - Long.bitCount(blackPawns));
		
 
		// mobility/position eval
		
		// ROOKS
		 eval += (EvaluationTools.evaluateRookMobilityAndPosition(whiteRooks, whitePieces, allPieces) 
				- EvaluationTools.evaluateRookMobilityAndPosition(blackRooks, blackPieces, allPieces));
		
		// BISHOPS
		 eval += (EvaluationTools.evaluateBishopMobilityAndPosition(whiteBishops, whitePieces, allPieces) 
				- EvaluationTools.evaluateBishopMobilityAndPosition(blackBishops, blackPieces, allPieces));
		
		// KNIGHTS
		eval += (EvaluationTools.evaluateKnightMobilityAndPosition(whiteKnights, whitePieces, allPieces) 
				- EvaluationTools.evaluateKnightMobilityAndPosition(blackKnights, blackPieces, allPieces));
		
		// QUEENS
		eval += (EvaluationTools.evaluateQueenMobilityAndPosition(whiteQueens, whitePieces, allPieces) 
				- EvaluationTools.evaluateQueenMobilityAndPosition(blackQueens, blackPieces, allPieces));
		
		// KING
		eval += (EvaluationTools.evaluateKingMobilityAndPosition(whiteKing, whitePieces, allPieces, whitePawns | blackPawns, true) 
				- EvaluationTools.evaluateKingMobilityAndPosition(blackKing, blackPieces, allPieces, whitePawns | blackPawns, false));
		
		// PAWNS
		eval += (EvaluationTools.evaluatePawnPosition(whitePawns, true)
				- EvaluationTools.evaluatePawnPosition(blackPawns, false));
		
		
		/*
		 * 
	    // Penalize repetition
	    String fen = this.generateSimpleFEN();
	    int repetitions = fenHistory.getOrDefault(fen, 0);
	    if (repetitions >= 3) return 0;
	    */
		
	    return eval;
	}
		
	

/*
 * 

	public double evaluate(boolean whiteToMove) {
		// material eval
		double eval = 5.0 * (Long.bitCount(whiteRooks) - Long.bitCount(blackRooks))
					+ 3.5 * (Long.bitCount(whiteBishops) - Long.bitCount(blackBishops))
					+ 3.0 * (Long.bitCount(whiteKnights) - Long.bitCount(blackKnights))
					+ 9.0 * (Long.bitCount(whiteQueens) - Long.bitCount(blackQueens))
					+ 1.0 * (Long.bitCount(whitePawns) - Long.bitCount(blackPawns));
		
 
		// mobility/position eval
		
		// ROOKS
		 eval += (EvaluationTools.evaluateRookMobilityAndPosition(whiteRooks, whitePieces, allPieces) 
				- EvaluationTools.evaluateRookMobilityAndPosition(blackRooks, blackPieces, allPieces));
		
		// BISHOPS
		 eval += (EvaluationTools.evaluateBishopMobilityAndPosition(whiteBishops, whitePieces, allPieces) 
				- EvaluationTools.evaluateBishopMobilityAndPosition(blackBishops, blackPieces, allPieces));
		
		// KNIGHTS
		eval += (EvaluationTools.evaluateKnightMobilityAndPosition(whiteKnights, whitePieces, allPieces) 
				- EvaluationTools.evaluateKnightMobilityAndPosition(blackKnights, blackPieces, allPieces));
		
		// QUEENS
		eval += (EvaluationTools.evaluateQueenMobilityAndPosition(whiteQueens, whitePieces, allPieces) 
				- EvaluationTools.evaluateQueenMobilityAndPosition(blackQueens, blackPieces, allPieces));
		
		// KING
		eval += (EvaluationTools.evaluateKingMobilityAndPosition(whiteKing, whitePieces, allPieces, whitePawns | blackPawns, true) 
				- EvaluationTools.evaluateKingMobilityAndPosition(blackKing, blackPieces, allPieces, whitePawns | blackPawns, false));
		
		// PAWNS
		eval += (EvaluationTools.evaluatePawnPosition(whitePawns, true)
				- EvaluationTools.evaluatePawnPosition(blackPawns, false));

		
		eval = whiteToMove ? eval : -eval;
		
		return eval;
		
		
	}
	*/
	
	
	// VIEW HELPER METHODS
	
	public void drawBoard(Graphics2D g2) {
		drawPiece(whitePawns, blackPawns, "pawn", g2);
		drawPiece(whiteRooks, blackRooks, "rook", g2);
		drawPiece(whiteBishops, blackBishops, "bishop", g2);
		drawPiece(whiteKnights, blackKnights, "knight", g2);
		drawPiece(whiteQueens, blackQueens, "queen", g2);
		drawPiece(whiteKing, blackKing, "king", g2);
		
	}
	
	public void drawKings(Graphics2D g2) {
		drawPiece(whiteKing, blackKing, "king", g2);
	}
	
	private void drawPiece(long whiteP, long blackP, String pType, Graphics2D g2) {
		
		for (int i = 0; i < 2; i++) {
			long pieces = i == 0 ? whiteP : blackP;
			String color = i == 0 ? "white" : "black";
			
			
			// get image for certain white piece
			String imagePath = "/piece/" + color + "_" + pType + ".png";
			BufferedImage image = null;
			try {
				image = GraphicsHelper.getImage(imagePath);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Couldn't find image " + imagePath);
				return;
			}
			
			while (pieces != 0) {
				int square = Long.numberOfTrailingZeros(pieces);
				long initMask = 1L << square;
				pieces &= ~initMask; 
				
				// convert BB Square to BB Row/Col
				int colBB = square % 8;
				int rowBB = square / 8;
				
				// convert BB Row/Col to display's Row/Col
				int colScreen = (8 - 1) - colBB;
				int rowScreen = (8 - 1) - rowBB;
				
				// get x and y for the row/col
				int x = GraphicsHelper.getX(colScreen);
				int y = GraphicsHelper.getY(rowScreen);
				
				g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			}
		}
	}
	
	public void removePieceAtSquare(int square) {
	    long removeBB = 1L << square;

	    // White pieces
	    if ((whitePawns & removeBB) != 0) whitePawns &= ~removeBB;
	    if ((whiteRooks & removeBB) != 0) whiteRooks &= ~removeBB;
	    if ((whiteKnights & removeBB) != 0) whiteKnights &= ~removeBB;
	    if ((whiteBishops & removeBB) != 0) whiteBishops &= ~removeBB;
	    if ((whiteQueens & removeBB) != 0) whiteQueens &= ~removeBB;
	    if ((whiteKing & removeBB) != 0) whiteKing &= ~removeBB;

	    // Black pieces
	    if ((blackPawns & removeBB) != 0) blackPawns &= ~removeBB;
	    if ((blackRooks & removeBB) != 0) blackRooks &= ~removeBB;
	    if ((blackKnights & removeBB) != 0) blackKnights &= ~removeBB;
	    if ((blackBishops & removeBB) != 0) blackBishops &= ~removeBB;
	    if ((blackQueens & removeBB) != 0) blackQueens &= ~removeBB;
	    if ((blackKing & removeBB) != 0) blackKing &= ~removeBB;
	}
	
	public ArrayList<Integer> getAllValidMovesAtSquare(int square, boolean toMove) {
		ArrayList<Integer> allValidMoves = generateAllLegalMoves(toMove);
		ArrayList<Integer> allValidMovesAtSquare = new ArrayList<Integer>();
		
		for (int move : allValidMoves) {
			if (getInitialSquare(move) == square) allValidMovesAtSquare.add(move);
		}
		
		return allValidMovesAtSquare;
	}
	
	
	// DEBUG METHODS
	
	public void printBoard() {
		for (int rank = 7; rank >= 0; rank--) {
			System.out.print(rank + 1 + " ");
			for (int file = 0; file < 8; file++) {
				int square = rank * 8 + (7 - file);
				char piece = getPieceOnSquare(square);
				System.out.print(piece + " ");
			}
			
			System.out.print("\n");
		}
		
		System.out.println("  a b c d e f g h \n");
		
	}
}

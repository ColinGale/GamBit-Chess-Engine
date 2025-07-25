package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class MoveGenerator {
	
	// pawn moves
	private static final long[] whitePawnMoveMasks = new long[64];
	private static final long[] blackPawnMoveMasks = new long[64];
	// pawn attacks
	private static final long[] whitePawnAttackMasks = new long[64];
	private static final long[] blackPawnAttackMasks = new long[64];
	
	// king moves
	private static final long[] kingAttackMasks = new long[64];
	
	// knight moves
	private static final long[] knightAttackMasks = new long[64];
	
	// rook moves
	private static final int[] rookShifts = new int[64];
	private static final long[] rookMasks = new long[64];
	private static final long[] rookMagics = new long[64];
	private static long[][] rookAttackTable = new long[64][];
	
	// bishop moves
	private static final int[] bishopShifts = new int[64];
	private static final long[] bishopMasks = new long[64];
	private static final long[] bishopMagics = new long[64];
	private static long[][] bishopAttackTable = new long[64][];
	
	// queen moves
	public static final int[] queenShifts = new int[64];
	public static final long[] queenMasks= new long[64];
	public static final long[] queenMagics = new long[64];
	private static final long[][] queenAttackTable = new long[64][];
	
	
	static {
		for (int square = 0; square < 64; square++) {
			knightAttackMasks[square] = computeKnightAttacks(square);
			kingAttackMasks[square] = computeKingAttacks(square);
			whitePawnMoveMasks[square] = computePawnMoves(square, 1);
			blackPawnMoveMasks[square] = computePawnMoves(square, -1);
		}
		
		generatePawnAttackMasks();
		createRookAttackTable();
		createBishopAttackTable();
		createQueenAttackTable();
	}
	
	private static long computeKingAttacks(int square) {
	    long bitboard = 1L << square;

	    long notAFile = 0xfefefefefefefefeL;
	    long notHFile = 0x7f7f7f7f7f7f7f7fL;

	    long attacks = 0L;

	    // Up
	    attacks |= bitboard << 8;
	    // Down
	    attacks |= bitboard >>> 8;

	    // Left
	    attacks |= (bitboard & notAFile) >>> 1;
	    // Right
	    attacks |= (bitboard & notHFile) << 1;

	    // Diagonals
	    attacks |= (bitboard & notAFile) << 7;
	    attacks |= (bitboard & notHFile) << 9;
	    attacks |= (bitboard & notAFile) >>> 9;
	    attacks |= (bitboard & notHFile) >>> 7;

	    return attacks;
	}


	private static long computeKnightAttacks(int square) {
		long bitboard = 1L << square;
		long attacks = 0L;

		long notAFile = 0xfefefefefefefefeL;
		long notABFile = 0xfcfcfcfcfcfcfcfcL;
		long notHFile = 0x7f7f7f7f7f7f7f7fL;
		long notGHFile = 0x3f3f3f3f3f3f3f3fL;

		if ((bitboard & notHFile) != 0)        attacks |= bitboard << 17;
		if ((bitboard & notAFile) != 0)        attacks |= bitboard << 15;
		if ((bitboard & notGHFile) != 0)       attacks |= bitboard << 10;
		if ((bitboard & notABFile) != 0)       attacks |= bitboard << 6;
		if ((bitboard & notABFile) != 0)       attacks |= bitboard >>> 10;
		if ((bitboard & notGHFile) != 0)       attacks |= bitboard >>> 6;
		if ((bitboard & notAFile) != 0)        attacks |= bitboard >>> 17;
		if ((bitboard & notHFile) != 0)        attacks |= bitboard >>> 15;

		return attacks;
	}
	
	private static long computePawnMoves(int square, int direction) {
	    long bitboard = 1L << square;
	    long moves = 0L;

	    int rank = square / 8;

	    long oneForward = direction > 0 ? (bitboard << 8) : (bitboard >>> 8);
	    moves |= oneForward;

	    // if it's the first rank for that color (rank 1 for white, rank 6 for black)
	    boolean isFirstMoveRank = (direction == 1 && rank == 1) || (direction == -1 && rank == 6);
	    if (isFirstMoveRank) {
	        long twoForward = direction > 0 ? (bitboard << 16) : (bitboard >>> 16);
	        moves |= twoForward;
	    }

	    return moves;
	}
	
	public static void generatePawnAttackMasks() {
	    final long fileA = 0x0101010101010101L;
	    final long fileH = 0x8080808080808080L;

	    for (int square = 0; square < 64; square++) {
	        long bitboard = 1L << square;
	        long whiteAttacks = 0L;
	        long blackAttacks = 0L;

	        // white attacks: up-left and up-right
	        if ((bitboard & fileA) == 0) {
	            whiteAttacks |= bitboard << 7;
	        }
	        if ((bitboard & fileH) == 0) {
	            whiteAttacks |= bitboard << 9;
	        }

	        // black attacks: down-left and down-right
	        if ((bitboard & fileA) == 0) {
	            blackAttacks |= bitboard >>> 9;
	        }
	        if ((bitboard & fileH) == 0) {
	            blackAttacks |= bitboard >>> 7;
	        }

	        whitePawnAttackMasks[square] = whiteAttacks;
	        blackPawnAttackMasks[square] = blackAttacks;
	    }
	}
	
	public static long getPawnAttacksTo(int square, boolean fromWhite) {
	    long target = 1L << square;
	    long attacksFrom = 0L;
	    
	    long FileA = 0x0101010101010101L;
	    long FileH = 0x8080808080808080L;

	    if (fromWhite) {
	        // if a white pawn is attacking this square, it must be to the bottom-left or bottom-right of it
	        // So we shift target DOWN-LEFT and DOWN-RIGHT to get where the pawn would have to be
	        if ((target & FileA) == 0) attacksFrom |= target >>> 9; // down-left
	        if ((target & FileH) == 0) attacksFrom |= target >>> 7; // down-right
	    } else {
	        // For black pawns attacking, the pawn must be UP-LEFT or UP-RIGHT
	        if ((target & FileA) == 0) attacksFrom |= target << 7; // up-left
	        if ((target & FileH) == 0) attacksFrom |= target << 9; // up-right
	    }

	    return attacksFrom;
	}

	
	
	// CASTLING SPECIFIC
	
	public static long possibleCastling(boolean toMove, long hasNotMoved) {
	    long castleSquares = 0x0L;

	    // Castling destination squares
	    long kingSideCastle = toMove ? 1L << 1 : 1L << 61;  // g1 or g8
	    long queenSideCastle = toMove ? 1L << 5 : 1L << 57; // c1 or c8

	    // King start square
	    long kingSquare = toMove ? 1L << 3 : 1L << 59;      // e1 or e8

	    // Correct rook positions
	    long kingSideRook = toMove ? 1L << 0 : 1L << 56;    // h1 or h8
	    long queenSideRook = toMove ? 1L << 7 : 1L << 63;   // a1 or a8

	    if ((hasNotMoved & kingSquare) != 0) {
	        if ((hasNotMoved & kingSideRook) != 0) castleSquares |= kingSideCastle;
	        if ((hasNotMoved & queenSideRook) != 0) castleSquares |= queenSideCastle;
	    }

	    return castleSquares;
	}

	
	// ATTACK TABLES

	public static void createRookAttackTable() {
		
		ArrayList<Long> magicList = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader("res/magic_data/rook_magic_data.txt"))) {
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	String[] lineArray = line.split(" ");
		        if (lineArray[0].equals("square")) continue;
		        
		        int square = Integer.valueOf(lineArray[0]);
		        int shift = Integer.valueOf(lineArray[1]);

		        long mask = new BigInteger(lineArray[2].substring(2), 16).longValue();
		        long magic = new BigInteger(lineArray[3].substring(2), 16).longValue();
		        
		        
		        
		        rookShifts[square] = shift;
		        rookMasks[square] = mask;
		        rookMagics[square] = magic;
		        
		        
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 // Generate attack tables
	    for (int square = 0; square < 64; square++) {
	        long mask = rookMasks[square];
	        int shift = rookShifts[square];
	        long magic = rookMagics[square];
	        
	        long[] blockers = createAllBlockerBitboards(mask);
	        int size = 1 << Long.bitCount(mask);
	        rookAttackTable[square] = new long[size];

	        for (long blocker : blockers) {
	            int index = (int)((blocker * magic) >>> (shift));
	            long attack = GenerateRookMagics.getLegalRookMoves(mask, blocker, square);
	            rookAttackTable[square][index] = attack;
	        }
	    }
	}
	
	public static void createBishopAttackTable() {
		
		ArrayList<Long> magicList = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader("res/magic_data/bishop_magic_data.txt"))) {
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	String[] lineArray = line.split(" ");
		        if (lineArray[0].equals("square")) continue;
		        
		        int square = Integer.valueOf(lineArray[0]);
		        int shift = Integer.valueOf(lineArray[1]);

		        long mask = new BigInteger(lineArray[2].substring(2), 16).longValue();
		        long magic = new BigInteger(lineArray[3].substring(2), 16).longValue();
		        
		        
		        
		        bishopShifts[square] = shift;
		        bishopMasks[square] = mask;
		        bishopMagics[square] = magic;
		        
		        
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 // Generate attack tables
	    for (int square = 0; square < 64; square++) {
	        long mask = bishopMasks[square];
	        int shift = bishopShifts[square];
	        long magic = bishopMagics[square];
	        
	        long[] blockers = createAllBlockerBitboards(mask);
	        int size = 1 << Long.bitCount(mask);
	        bishopAttackTable[square] = new long[size];

	        for (long blocker : blockers) {
	            int index = (int)((blocker * magic) >>> (shift));
	            long attack = GenerateBishopMagics.getLegalBishopMoves(mask, blocker, square);
	            bishopAttackTable[square][index] = attack;
	        }
	    }
	}
	
	public static void createQueenAttackTable() {
		
		ArrayList<Long> magicList = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader("res/magic_data/queen_magic_data.txt"))) {
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	String[] lineArray = line.split(" ");
		        if (lineArray[0].equals("square")) continue;
		        
		        int square = Integer.valueOf(lineArray[0]);
		        int shift = Integer.valueOf(lineArray[1]);

		        long mask = new BigInteger(lineArray[2].substring(2), 16).longValue();
		        long magic = new BigInteger(lineArray[3].substring(2), 16).longValue();
		        
		        
		        
		        queenShifts[square] = shift;
		        queenMasks[square] = mask;
		        queenMagics[square] = magic;
		        
		        
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 // Generate attack tables
	    for (int square = 0; square < 64; square++) {
	        long mask = queenMasks[square];
	        int shift = queenShifts[square];
	        long magic = queenMagics[square];
	        
	        long[] blockers = createAllBlockerBitboards(mask);
	        int size = 1 << Long.bitCount(mask);
	        queenAttackTable[square] = new long[size];

	        for (long blocker : blockers) {
	            int index = (int)((blocker * magic) >>> (shift));
	            long attack = GenerateQueenMagics.getLegalQueenMoves(mask, blocker, square);
	            queenAttackTable[square][index] = attack;
	        }
	    }
	}
	
	public static long getRookAttacks(int square, long occupancy) {
	    long blockers = occupancy & rookMasks[square];    // masked blockers (no edges)
	    long magic = rookMagics[square];
	    int shift = rookShifts[square];
	    int index = (int)((blockers * magic) >>> shift);
	    long attacks = rookAttackTable[square][index];

	    // Now manually add edge squares beyond blockers for each direction:
	    int row = square / 8;
	    int col = square % 8;

	    // Directions: up, down, left, right
	    // Helper lambda to add edge squares beyond blockers
	    attacks |= addEdgeSquaresRook(square, occupancy, row, col);

	    return attacks;
	}
	
	public static long getBishopAttacks(int square, long occupancy) {
	    long blockers = occupancy & bishopMasks[square];    // masked blockers (no edges)
	    long magic = bishopMagics[square];
	    int shift = bishopShifts[square];
	    int index = (int)((blockers * magic) >>> shift);
	    long attacks = bishopAttackTable[square][index];

	    // Now manually add edge squares beyond blockers for each direction:
	    int row = square / 8;
	    int col = square % 8;

	    // Directions: up, down, left, right
	    // Helper lambda to add edge squares beyond blockers
	    attacks |= addEdgeSquaresBishop(square, occupancy, row, col);

	    return attacks;
	}
	
	public static long getQueenAttacks(int square, long occupancy) {
		long blockers = occupancy & queenMasks[square];
		long magic = queenMagics[square];
		int shift = queenShifts[square];
		int index = (int)((blockers * magic) >>> shift);
		long attacks = queenAttackTable[square][index];
		
		
	    // Now manually add edge squares beyond blockers for each direction:
	    int row = square / 8;
	    int col = square % 8;

	    // Directions: up, down, left, right
	    // Helper lambda to add edge squares beyond blockers (in both bishop and rook directions)
	    attacks |= addEdgeSquaresBishop(square, occupancy, row, col);
	    attacks |= addEdgeSquaresRook(square, occupancy, row, col);
	    
	    return attacks;
	}
	
	public static long[] createAllBlockerBitboards(long movementMask) {
		ArrayList<Integer> moveSquareIndices = new ArrayList<>();
		
		// create a list of all indices of the bits in a single movementMask
		for (int i = 0; i < 64; i++) {
			if ((movementMask & (0x1L << i)) != 0) moveSquareIndices.add(i);
		}
		
		// calculate total number of bitboards for each movementMask (one for each possible arrangement of pieces)
		int numBitboard = 1 << moveSquareIndices.size(); // 2^n (each position could be 1 or 0, giving a total of 2^n possibilities)
		long[] blockerBitboards = new long[numBitboard];
		
		for (int  bitboardIndex = 0; bitboardIndex < numBitboard; bitboardIndex++) {
			long blocker = 0L;
			for (int bitIndex = 0; bitIndex < moveSquareIndices.size(); bitIndex++) {
				
				if ((bitboardIndex & (0x1 << bitIndex)) != 0) {
					blocker |= 0x1L << moveSquareIndices.get(bitIndex);
				}
			}
			blockerBitboards[bitboardIndex] = blocker;
		}
		
		return blockerBitboards;
	}

	// Add edges beyond blockers in rook directions
	private static long addEdgeSquaresRook(int square, long occupancy, int row, int col) {
	    long edgeAttacks = 0L;

	    // Up (row increasing)
	    for (int r = row + 1; r <= 7; r++) {
	        int sq = r * 8 + col;
	        edgeAttacks |= 1L << sq;
	        if ((occupancy & (1L << sq)) != 0) break;  // blocker stops ray
	    }
	    // Down (row decreasing)
	    for (int r = row - 1; r >= 0; r--) {
	        int sq = r * 8 + col;
	        edgeAttacks |= 1L << sq;
	        if ((occupancy & (1L << sq)) != 0) break;
	    }
	    // Right (col increasing)
	    for (int c = col + 1; c <= 7; c++) {
	        int sq = row * 8 + c;
	        edgeAttacks |= 1L << sq;
	        if ((occupancy & (1L << sq)) != 0) break;
	    }
	    // Left (col decreasing)
	    for (int c = col - 1; c >= 0; c--) {
	        int sq = row * 8 + c;
	        edgeAttacks |= 1L << sq;
	        if ((occupancy & (1L << sq)) != 0) break;
	    }

	    return edgeAttacks;
	}
	
	// Add edges beyond blockers in bishop directions
	private static long addEdgeSquaresBishop(int square, long occupancy, int row, int col) {
	    long edgeAttacks = 0L;

	    // Up-Right (row++, col++)
	    for (int r = row + 1, c = col + 1; r <= 7 && c <= 7; r++, c++) {
	        int sq = r * 8 + c;
	        edgeAttacks |= 1L << sq;
	        if ((occupancy & (1L << sq)) != 0) break;  // blocker stops ray
	    }

	    // Up-Left (row++, col--)
	    for (int r = row + 1, c = col - 1; r <= 7 && c >= 0; r++, c--) {
	        int sq = r * 8 + c;
	        edgeAttacks |= 1L << sq;
	        if ((occupancy & (1L << sq)) != 0) break;
	    }

	    // Down-Right (row--, col++)
	    for (int r = row - 1, c = col + 1; r >= 0 && c <= 7; r--, c++) {
	        int sq = r * 8 + c;
	        edgeAttacks |= 1L << sq;
	        if ((occupancy & (1L << sq)) != 0) break;
	    }

	    // Down-Left (row--, col--)
	    for (int r = row - 1, c = col - 1; r >= 0 && c >= 0; r--, c--) {
	        int sq = r * 8 + c;
	        edgeAttacks |= 1L << sq;
	        if ((occupancy & (1L << sq)) != 0) break;
	    }

	    return edgeAttacks;
	}

	
	public static long[] getWhitePawnMoveMasks() {
		return whitePawnMoveMasks;
	}
	
	public static long[] getBlackPawnMoveMasks() {
		return blackPawnMoveMasks;
	}
	
	public static long[] getWhitePawnAttackMasks() {
		return whitePawnAttackMasks;
	}
	
	public static long[] getBlackPawnAttackMasks() {
		return blackPawnAttackMasks;
	}

	public static long[] getKnightAttackMasks() {
		return knightAttackMasks;
	}
	
	public static long[] getKingAttackMasks() {
		return kingAttackMasks;
	}

}

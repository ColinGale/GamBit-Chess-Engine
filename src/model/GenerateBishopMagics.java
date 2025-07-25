package model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GenerateBishopMagics {
	
	private static final int[] bishopShifts = new int[64];
	private static final long[] bishopMasks = new long[64];
	private static final long[] bishopMagics = new long[64];
	
	public static void main(String[] args) {
		
        try (PrintWriter writer = new PrintWriter("res/magic_data/bishop_magic_data.txt")) {
            writer.println("square shift mask magic");

            for (int square = 0; square < 64; square++) {
                long mask = generateBishopMask(square);
                
                
                // how many bits actually need to be considered for a blocker BB
                long[] blockers = MoveGenerator.createAllBlockerBitboards(mask);
                int relevantBits = Long.bitCount(mask);
                
                // legal moves for each blocker BB
                long[] moves = new long[blockers.length];

                for (int i = 0; i < blockers.length; i++) {
                    moves[i] = getLegalBishopMoves(mask, blockers[i], square);
                }

                long magic = Magics.findMagicNumber(square, relevantBits, blockers, moves);
                int shift = 64 - relevantBits;

                bishopShifts[square] = shift;
                bishopMasks[square] = mask;
                bishopMagics[square] = magic;

                writer.printf("%d %d 0x%X 0x%X%n", square, shift, mask, magic);
                System.out.printf("Square %2d: shift=%2d  magic=0x%X%n", square, shift, magic);
            }
            
            System.out.println("Magic numbers written to bishop_magic_data.txt");
        } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public static long generateBishopMask(int square) {
	    long mask = 0L;

	    int row = square / 8;
	    int col = square % 8;

	    // diagonal up-right
	    for (int r = row + 1, c =  col + 1; r <= 6 && c <= 6; r++, c++) {
	    	mask |= (0x1L << (r * 8 + c));
	    }
	    
	    // diagonal down-right
	    for (int r = row - 1, c = col + 1; r >= 1 && c <= 6; r--, c++) {
	    	mask |= (0x1L << (r * 8 + c));
	    }
	    
	    // diagonal up-left
	    for (int r = row + 1, c =  col - 1; r <= 6 && c >= 1; r++, c--) {
	    	mask |= (0x1L << (r * 8 + c));
	    }
	    
	    // diagonal down-left
	    for (int r = row - 1, c = col - 1; r >= 1 && c >= 1; r--, c--) {
	    	mask |= (0x1L << (r * 8 + c));
	    }

	    return mask;
	}
	
	public static long generateFullBishopMask(int square) {
	    long mask = 0L;

	    int row = square / 8;
	    int col = square % 8;

	    // diagonal up-right
	    for (int r = row + 1, c =  col + 1; r <= 7 && c <= 7; r++, c++) {
	    	mask |= (0x1L << (r * 8 + c));
	    }
	    
	    // diagonal down-right
	    for (int r = row - 1, c = col + 1; r >= 0 && c <= 7; r--, c++) {
	    	mask |= (0x1L << (r * 8 + c));
	    }
	    
	    // diagonal up-left
	    for (int r = row + 1, c =  col - 1; r <= 7 && c >= 0; r++, c--) {
	    	mask |= (0x1L << (r * 8 + c));
	    }
	    
	    // diagonal down-left
	    for (int r = row - 1, c = col - 1; r >= 0 && c >= 0; r--, c--) {
	    	mask |= (0x1L << (r * 8 + c));
	    }

	    return mask;
	}
	
	
	public static long getLegalBishopMoves(long attackMask, long blockerBitboard, int square) {
		long legalMoves = 0x0L;
		int[] directions = {9, 7, -7, -9};
		
		
		for (int dir : directions) {
			int currSquare = square;
			
			while (true) {
				int nextSquare = currSquare + dir;
				
				// if the nextSquare is not in the available bishop moves with no other pieces around
				if ((attackMask & (0x1L << nextSquare)) == 0) break;
				
				legalMoves |= (0x1L << nextSquare);
				
				// after the first blocker, break out so bishop can not pass through (only take that first blocker)
				if ((blockerBitboard & (0x1L << nextSquare)) != 0) break;
				
				currSquare = nextSquare;
			}	
		}
		
		return legalMoves;
	}
}

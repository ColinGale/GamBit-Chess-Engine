package model;

import java.util.Arrays;
import java.util.Random;

public class Magics {
	
	// magic numbers
	
		public static long findMagicNumber(int square, int relevantBits, long[] blockers, long[] moves) {
		    for (int attempt = 0; attempt < 1000000; attempt++) {
		        long magic = generateMagicNumber();
		        if (isMagicGood(magic, blockers, moves, relevantBits)) {
		            return magic;
		        }
		    }
		    throw new RuntimeException("Magic number not found for square " + square);
		}
		
		
		
		// inputs: magic - the magic number multiplier we are testing
		//		   blockers - array of possible blockers for each square
		//		   moves - array of bitboards of legal moves for each blocker
		//		   shift - relevant bits (only bits shared by mask & blocker)
		
		public static boolean isMagicGood(long magic, long[] blockers, long[] moves, int relevantBits) {
			int size = 1 << relevantBits; // 2 ^ relevant bits
			long[] used = new long[size];
			Arrays.fill(used, Long.MIN_VALUE); // will be used to check if index is used or not
			
			for (int i =0; i < blockers.length; i++) {
				int index = (int)(((blockers[i] * magic) >>> (64 - relevantBits))); // testing unique index
				if (used[index] != Long.MIN_VALUE && used[index] != moves[i]) {
					return false; // if index has been used or index maps to the wrong move, return false
				}
				
				used[index] = moves[i];
			}
			
			return true; // if loop completes, this is a valid magic number
			
		}
		
	    public static long generateMagicNumber() {
	        return (nextLong() & nextLong() & nextLong()) | 0x0800000000000000L;
	    }

	    public static long nextLong() {
	        return new Random().nextLong();
	    }
}

package model;

public class EvaluationTools {
	
		private static final double[] KNIGHT_POS = {
		    -0.5, -0.4, -0.3, -0.3, -0.3, -0.3, -0.4, -0.5,
		    -0.4, -0.2,  0.0,  0.05, 0.05, 0.0, -0.2, -0.4,
		    -0.3,  0.05, 0.1,  0.15, 0.15, 0.1,  0.05, -0.3,
		    -0.3,  0.0,  0.15, 0.2,  0.2,  0.15, 0.0,  -0.3,
		    -0.3,  0.05, 0.15, 0.2,  0.2,  0.15, 0.05, -0.3,
		    -0.3,  0.0,  0.1,  0.15, 0.15, 0.1,  0.0,  -0.3,
		    -0.4, -0.2,  0.0,  0.0,  0.0,  0.0, -0.2, -0.4,
		    -0.5, -0.4, -0.3, -0.3, -0.3, -0.3, -0.4, -0.5
		};
		
		private static final double[] BISHOP_POS = {
			    -0.4, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.4,
			    -0.3, -0.1,  0.0,  0.0,  0.0,  0.0, -0.1, -0.3,
			    -0.3,  0.0,  0.1,  0.15, 0.15, 0.1,  0.0, -0.3,
			    -0.3,  0.05, 0.15, 0.2,  0.2,  0.15, 0.05, -0.3,
			    -0.3,  0.05, 0.15, 0.2,  0.2,  0.15, 0.05, -0.3,
			    -0.3,  0.0,  0.1,  0.15, 0.15, 0.1,  0.0, -0.3,
			    -0.3, -0.1,  0.0,  0.0,  0.0,  0.0, -0.1, -0.3,
			    -0.4, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.4
			};
		
		private static final double[] ROOK_POS = {
			     0.0,  0.0,  0.0,  0.1,  0.1,  0.0,  0.0,  0.0,
			    -0.05, 0.0,  0.0,  0.05, 0.05, 0.0,  0.0, -0.05,
			    -0.05, 0.0,  0.0,  0.05, 0.05, 0.0,  0.0, -0.05,
			    -0.05, 0.0,  0.0,  0.05, 0.05, 0.0,  0.0, -0.05,
			    -0.05, 0.0,  0.0,  0.05, 0.05, 0.0,  0.0, -0.05,
			    -0.05, 0.0,  0.0,  0.05, 0.05, 0.0,  0.0, -0.05,
			     0.0,  0.05, 0.1,  0.15, 0.15, 0.1,  0.05, 0.0,
			     0.0,  0.0,  0.05, 0.1,  0.1,  0.05, 0.0,  0.0
			};
		
		private static final double[] QUEEN_POS = {
			    -0.2, -0.1, -0.1, -0.05, -0.05, -0.1, -0.1, -0.2,
			    -0.1,  0.0,  0.0,   0.0,  0.0,   0.0,  0.0, -0.1,
			    -0.1,  0.0,  0.1,   0.1,  0.1,   0.1,  0.0, -0.1,
			    -0.05,0.0,  0.1,   0.1,  0.1,   0.1,  0.0, -0.05,
			     0.0,  0.05,0.1,   0.1,  0.1,   0.1,  0.05, 0.0,
			    -0.05,0.05,0.05,  0.05, 0.05,  0.05, 0.05, -0.05,
			    -0.1,  0.0,  0.05, 0.0,  0.0,   0.05, 0.0,  -0.1,
			    -0.2, -0.1, -0.1, -0.05,-0.05, -0.1, -0.1, -0.2
			};
		
		private static final double[] KING_POS_MID = {
			    -0.3, -0.4, -0.4, -0.5, -0.5, -0.4, -0.4, -0.3,
			    -0.3, -0.4, -0.4, -0.5, -0.5, -0.4, -0.4, -0.3,
			    -0.3, -0.4, -0.4, -0.5, -0.5, -0.4, -0.4, -0.3,
			    -0.3, -0.4, -0.4, -0.5, -0.5, -0.4, -0.4, -0.3,
			    -0.2, -0.3, -0.3, -0.4, -0.4, -0.3, -0.3, -0.2,
			    -0.1, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.1,
			     0.2,  0.2,  0.0,  0.0,  0.0,  0.0,  0.2,  0.2,
			     0.2,  0.3,  0.1,  0.0,  0.0,  0.1,  0.3,  0.2
			};
		
		
		private static final double[] KING_POS_END = {
			    -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1,
			    -0.1,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.1,
			    -0.1,  0.0,  0.1,  0.1,  0.1,  0.1,  0.0, -0.1,
			    -0.1,  0.0,  0.1,  0.2,  0.2,  0.1,  0.0, -0.1,
			    -0.1,  0.0,  0.1,  0.2,  0.2,  0.1,  0.0, -0.1,
			    -0.1,  0.0,  0.1,  0.1,  0.1,  0.1,  0.0, -0.1,
			    -0.1,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.1,
			    -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1
			};
		
		private static final double[] PAWN_POS = {
			     0.0,   0.0,   0.0,   0.0,   0.0,   0.0,   0.0,   0.0,
			     0.5,   0.5,   0.5,   0.5,   0.5,   0.5,   0.5,   0.5,
			     0.2,   0.2,   0.3,   0.4,   0.4,   0.3,   0.2,   0.2,
			     0.1,   0.1,   0.2,   0.3,   0.3,   0.2,   0.1,   0.1,
			     0.05,  0.05,  0.1,   0.25,  0.25,  0.1,   0.05,  0.05,
			     0.0,   0.0,   0.0,   0.2,   0.2,   0.0,   0.0,   0.0,
			     0.05, -0.05, -0.1,   0.0,   0.0,  -0.1,  -0.05,  0.05,
			     0.0,   0.0,   0.0,   0.0,   0.0,   0.0,   0.0,   0.0
			};



	
	// Per-move mobility weight
	private static final double[] MOBILITY_WEIGHTS = {
	    0.06, // Knight
	    0.07, // Bishop
	    0.05, // Rook
	    0.04, // Queen
	    0.03, // King (midgame)
	    0.06 // King (endgame)
	};


	public static double evaluateQueenMobilityAndPosition(long queenBB, long ownPieces, long allPieces) {
		double eval = 0;
		while (queenBB != 0) {
			int initSquare = Long.numberOfTrailingZeros(queenBB);
			
			// position eval
			eval += QUEEN_POS[initSquare];
			
			long initMask = 1L << initSquare;
			
			// remove the piece in order to move to next piece's move generation
			queenBB &= ~initMask; 
			
			// attack mask that can not move onto own piece
			long attackMask = MoveGenerator.getQueenAttacks(initSquare, allPieces) & ~ownPieces;
			
			
			eval += Long.bitCount(attackMask) * MOBILITY_WEIGHTS[3];
		}
		
		return eval;
	}
	
	public static double evaluateBishopMobilityAndPosition(long bishopBB, long ownPieces, long allPieces) {
		double eval = 0;
		while (bishopBB != 0) {
			int initSquare = Long.numberOfTrailingZeros(bishopBB);
			
			// position eval
			eval += BISHOP_POS[initSquare];
			
			long initMask = 1L << initSquare;
			
			// remove the piece in order to move to next piece's move generation
			bishopBB &= ~initMask; 
			
			// attack mask that can not move onto own piece
			long attackMask = MoveGenerator.getBishopAttacks(initSquare, allPieces) & ~ownPieces;
			
			
			eval += Long.bitCount(attackMask) * MOBILITY_WEIGHTS[1];
		}
		
		return eval;
	}
	
	public static double evaluateKnightMobilityAndPosition(long knightBB, long ownPieces, long allPieces) {
		double eval = 0;
		while (knightBB != 0) {
			int initSquare = Long.numberOfTrailingZeros(knightBB);
			long initMask = 1L << initSquare;
			
			// position eval
			eval += KNIGHT_POS[initSquare];
			
			// remove the piece in order to move to next piece's move generation
			knightBB &= ~initMask; 
			
			// attack mask that can not move onto own piece
			long attackMask = MoveGenerator.getKnightAttackMasks()[initSquare] & ~ownPieces;
			
			
			eval += Long.bitCount(attackMask) * MOBILITY_WEIGHTS[0];
		}
		
		return eval;
	}
	
	public static double evaluateRookMobilityAndPosition(long rookBB, long ownPieces, long allPieces) {
		double eval = 0;
		while (rookBB != 0) {
			int initSquare = Long.numberOfTrailingZeros(rookBB);
			
			// position eval
			eval += ROOK_POS[initSquare];
			
			long initMask = 1L << initSquare;
			
			// remove the piece in order to move to next piece's move generation
			rookBB &= ~initMask; 
			
			// attack mask that can not move onto own piece
			long attackMask = MoveGenerator.getRookAttacks(initSquare, allPieces) & ~ownPieces;
			
			
			eval += Long.bitCount(attackMask) * MOBILITY_WEIGHTS[2];
		}
		
		return eval;
	}
	
	public static double evaluateKingMobilityAndPosition(long king, long ownPieces, long allPieces, long allPawns, boolean isWhite) {
		
		// king is evaluated differently during midgame and endgame, endgame is 9 or less pieces (including kings)
		long withoutPawns = allPieces & ~allPawns;
		boolean endgame = Long.bitCount(withoutPawns) <= 9;
		double weight = endgame ? MOBILITY_WEIGHTS[5] : MOBILITY_WEIGHTS[4];
		
		
		double eval = 0;
		int initSquare = Long.numberOfTrailingZeros(king);
		int index = !isWhite ? initSquare : mirrorSquare(initSquare);
		
		// eval position
		eval += endgame ? KING_POS_END[index] : KING_POS_MID[index];
		
		long initMask = 1L << initSquare;
		
		// attack mask that can not move onto own piece
		long attackMask = MoveGenerator.getRookAttacks(initSquare, allPieces) & ~ownPieces;
			
			
		eval += Long.bitCount(attackMask) * weight;
		
		return eval;
	}
	
	public static double evaluatePawnPosition(long pawnBB, boolean isWhite) {
	    double eval = 0;
	    while (pawnBB != 0) {
	        int square = Long.numberOfTrailingZeros(pawnBB);
	        long mask = 1L << square;
	        pawnBB &= ~mask;

	        // Flip board for black pieces
	        int index = isWhite ? mirrorSquare(square) : square;
	        eval += PAWN_POS[index];
	    }
	    return eval;
	}
	
	
	public static int mirrorSquare(int sq) {
	    int rank = sq / 8;
	    int file = sq % 8;
	    return (7 - rank) * 8 + file;
	}

}

package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZobristHashing {
	
	// 64 squares x 12 pieces
    private static long[][] zobristTable = new long[12][64];
    
    
    
    // 0 if white, random long if black
    private static long sideToMoveHash;
    
    // 4 bits = 16 combinations
    // 0000 = no castling, 0001 = white kingside, ...
    private static long[] castlingRightsHash = new long[16];
    
    
    private static long[] enpassantFileHash = new long[8];
    
    private static final Random rnd = new Random(1337);
    
    static {
    	initialize();
    }

    private static long randomLong() {
    	// generates a random 64 bit number
        return rnd.nextLong();
    }

    public static void initialize() {
    	
    	sideToMoveHash = randomLong();
        for (int i = 0; i < 12; i++) {
            long[] squareList = new long[64];
            for (int k = 0; k < 64; k++) {
                squareList[k] = randomLong();
            }
            zobristTable[i] = squareList;
        }
        
        for (int i = 0; i < 16; i++) {
        	castlingRightsHash[i] = randomLong();
        }
        
        for (int i = 0; i < 8; i++) {
            enpassantFileHash[i] = randomLong();
        }
    }

    public static long computeHash(Bitboard board) {
        long hash = 0;
        for (int square = 0; square < 64; square++) {
            char piece = board.getPieceOnSquare(square);
            int index = getIndex(piece);
            if (index != -1) {
                hash ^= zobristTable[index][square];
            }
        }
        
        int rights = board.getCastlingRights();
        hash ^= castlingRightsHash[rights];
        
        // only hash for black to move
        if (!board.getToMove()) {
        	hash ^= sideToMoveHash;
        }
        
        if (board.enpassantFile() != -1) hash ^= enpassantFileHash[board.enpassantFile()];
        
        return hash;
    }
    
    public static long getZobristMoveHash() {
    	return sideToMoveHash;
    }
    
    public static long[] getZobristCastlingRights() {
    	return castlingRightsHash;
    }
    
    public static long[] getZobristEnpassantFile() {
    	return enpassantFileHash;
    }
    
    public static long[][] getTable(){
    	return zobristTable;
    }

    public static int getIndex(char piece) {
        switch (piece) {
            case 'P': return 0;
            case 'N': return 1;
            case 'B': return 2;
            case 'R': return 3;
            case 'Q': return 4;
            case 'K': return 5;
            case 'p': return 6;
            case 'n': return 7;
            case 'b': return 8;
            case 'r': return 9;
            case 'q': return 10;
            case 'k': return 11;
            default: return -1;
        }
    }
}

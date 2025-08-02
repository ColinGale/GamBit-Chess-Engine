package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZobristHashing {

    private static List<List<Long>> zobristTable = new ArrayList<>();
    
    
    
    // 0 if white, random long if black
    private static long sideToMoveHash;
    
    // 4 bits = 16 combinations
    // 0000 = no castling, 0001 = white kingside, ...
    private static long[] castlingRightsHash = new long[16];
    
    
    private static long[] enpassantFileHash = new long[8];
    
    private static final Random rnd = new Random();
    
    static {
    	initialize();
    }

    private static long randomLong() {
    	// generates a random 64 bit number
        return rnd.nextLong();
    }

    public static void initialize() {
    	zobristTable.clear();
    	
    	sideToMoveHash = randomLong();
        for (int i = 0; i < 64; i++) {
            List<Long> pieceList = new ArrayList<>();
            for (int k = 0; k < 12; k++) {
                pieceList.add(randomLong());
            }
            zobristTable.add(pieceList);
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
                hash ^= zobristTable.get(square).get(index);
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

    private static int getIndex(char piece) {
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

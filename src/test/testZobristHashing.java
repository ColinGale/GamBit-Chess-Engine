package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import model.Bitboard;
import model.ZobristHashing;

class testZobristHashing {

	@Test
	public void testThreefoldRepetitionHash() {
	    Bitboard board = new Bitboard();
	    long hash1 = ZobristHashing.computeHash(board);
	    int move;
	    
	    // e4
	    move = board.generateMove(11, 27, 0);
	    board.makeMove(move);
	    
	    // e5
	    move = board.generateMove(51, 35, 0);
	    board.makeMove(move);

	    board.undoMove();
	    board.undoMove();
	    long hash2 = ZobristHashing.computeHash(board);
	    assertEquals(hash1, hash2);
	}

}

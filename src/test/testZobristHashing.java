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
	
	@Test
	public void testEnpassantHash() {
	    Bitboard board = new Bitboard();
	    int move;
	    
	    // e4
	    move = board.generateMove(11, 27, 0);
	    board.makeMove(move);
	    
	    // e6
	    move = board.generateMove(51, 43, 0);
	    board.makeMove(move);
	    
	    // e5
	    move = board.generateMove(27, 35, 0);
	    board.makeMove(move);
	    
	    // d5
	    move = board.generateMove(52, 36, 0);
	    board.makeMove(move);
	    
	    long hash1 = ZobristHashing.computeHash(board);
	    long hash3 = board.getZobristHash();
	    
	    Bitboard board2 = new Bitboard();
	    
	    // e3
	    move = board2.generateMove(11, 19, 0);
	    board2.makeMove(move);
	    
	    // e6
	    move = board2.generateMove(51, 43, 0);
	    board2.makeMove(move);
	    
	    // e4
	    move = board2.generateMove(19, 27, 0);
	    board2.makeMove(move);
	    
	    // d6
	    move = board2.generateMove(52, 44, 0);
	    board2.makeMove(move);
	    
	    // e5
	    move = board2.generateMove(27, 35, 0);
	    board2.makeMove(move);
	    
	    // d6
	    move = board2.generateMove(44, 36, 0);
	    board2.makeMove(move);
	    
	    long hash2 = ZobristHashing.computeHash(board2);
	    long hash4 = board2.getZobristHash();
	    
	    assertNotEquals(hash1, hash2);	
	    assertNotEquals(hash3, hash4);
	    assertEquals(board.toString(), board2.toString());
	}
	
	@Test
	public void testUndoHash() {
	    Bitboard board = new Bitboard();
	    int move;
	    long hash1 = board.getZobristHash();
	    
	    // e4
	    move = board.generateMove(11, 27, 0);
	    board.makeMove(move);
	    board.undoMove();
	    
	    
	    long hash2 = board.getZobristHash();
	    
	    assertEquals(hash1, hash2);	    
	}
	
	@Test
	public void testMoveHash() {
	    Bitboard board = new Bitboard();
	    Bitboard board2 = new Bitboard();
	    int move;
	    
	    
	    // e4
	    move = board.generateMove(11, 27, 0);
	    board.makeMove(move);
	    board2.makeMove(move);
	    
	    long hash1 = board.getZobristHash();
	    long hash2 = board2.getZobristHash();
	    
	    assertEquals(hash1, hash2);	    
	    board.undoMove();
	    hash1 = board.getZobristHash();
	    assertNotEquals(hash1, hash2);
	}
	
	@Test
	public void testZobristTables() {
		Bitboard board1 = new Bitboard();
		Bitboard board2 = new Bitboard(board1);

		System.out.println(board1.getZobristHash() == board2.getZobristHash()); // should be true

		// e4
	    int move = board1.generateMove(11, 27, 0);
	    board1.makeMove(move);
		board1.undoMove();

		System.out.println(board1.getZobristHash() == board2.getZobristHash()); // should still be true

	}
}

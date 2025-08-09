package model;

import java.util.ArrayList;
import java.util.Stack;

public class GamBit {
	
	public static final double MATE_SCORE = 10000000;
	public static final double NEG_MATE_SCORE = -10000000;
	
	private int depth;
	private Bitboard board;
	private int bestMove;
	
	// temporary fix to 3-move repetition before adding zobrist-hashing
	private int prevMove1;
	private int prevMove2;
	
	
	// benchmark testing
	private int nodeCount = 0;
	
	public GamBit(int depth) {
		this.depth = depth;
		board = new Bitboard();
		bestMove = 0;
		prevMove1 = 0;
		prevMove2 = 0;
	}
	
	public GamBit(int depth, Bitboard board) {
		this.depth = depth;
		this.board = board;
		bestMove = 0;
		prevMove1 = 0;
		prevMove2 = 0;
	}
	
	public double negamax(Bitboard simBoard, boolean toMove, int depth, double alpha, double beta) {
	    nodeCount++;
		if (simBoard.isCheckMate(toMove)) {
	    	double val = -MATE_SCORE - depth;
	    	return val;
	    }
	    if (simBoard.isStaleMate(toMove)) return 0;

	    if (depth == 0) {
	        double eval = quiescence(simBoard, toMove, alpha, beta);
	        return eval;
	    }

	    ArrayList<MoveSet> moveList = simBoard.generateLegalMovesOrdered(toMove);

	    double bestVal = -MATE_SCORE * 2;

	    for (MoveSet moveSet : moveList) {
	    	
	    	int stackSizeBefore = simBoard.gameStack.size();
	    	
	        simBoard.makeMove(moveSet.getMove());
	        double val = -negamax(simBoard, !toMove, depth - 1, -beta, -alpha);
	        simBoard.undoMove();
	        
	        assert simBoard.gameStack.size() == stackSizeBefore : "Stack corruption!";
	        
	        if (depth == this.depth && sameMove(moveSet.getMove(), prevMove2)) continue;

	        if (depth == this.depth && val > bestVal) {
	            bestMove = moveSet.getMove();
	        }

	        bestVal = Math.max(bestVal, val);
	        alpha = Math.max(alpha, val);
	        if (alpha >= beta) break;
	    }

	    return bestVal;
	}

	
	public double quiescence(Bitboard simBoard, boolean toMove, double alpha, double beta) {
		nodeCount++;
	    if (simBoard.isCheckMate(toMove)) {
	        return -MATE_SCORE;
	    }
	    if (simBoard.isStaleMate(toMove)) return 0;

	    double staticEval = simBoard.evaluate();
	    staticEval = toMove ? staticEval : -staticEval;

	    if (staticEval >= beta) return staticEval;
	    if (staticEval > alpha) alpha = staticEval;

	    ArrayList<int[]> captureAndChecks = simBoard.getCaptureAndChecks(toMove);

	    double bestVal = staticEval;

	    for (int[] moveArray : captureAndChecks) {
	        int stackSizeBefore = simBoard.gameStack.size();

	        simBoard.makeMove(moveArray[0]);
	        double val = -quiescence(simBoard, !toMove, -beta, -alpha);
	        simBoard.undoMove();

	        assert simBoard.gameStack.size() == stackSizeBefore : "Stack corruption!";

	        if (val >= beta) return val;
	        if (val > bestVal) bestVal = val;
	        if (val > alpha) alpha = val;
	    }

	    return bestVal;
	}

	public void updatePrevMoves(int newMove) {
		prevMove2 = prevMove1;
		prevMove1 = newMove;
	}
	
	public boolean sameMove(int move1, int move2) {
		return move1 == move2;
	}
	
	public Bitboard getBoard() {
		return board;
	}
	
	public int getBestMove() {
		return bestMove;
	}
	
	public int getNodeCount() {
		return nodeCount;
	}
	
	public void resetStats() {
		nodeCount = 0;
	}

}

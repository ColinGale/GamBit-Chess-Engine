package model;

import java.util.ArrayList;
import java.util.HashMap;

public class GamBit {
    
    public static final double MATE_SCORE = 10000000;
    public static final double NEG_MATE_SCORE = -10000000;
    
    private int depth;
    private Bitboard board;
    private int bestMove;

    // 1M TT entries, power of two size for fast masking
    private TTEntry[] transpositionTable = new TTEntry[1 << 20]; 

    // Benchmark counters
    private int nodeCount = 0;
    int[] nodesAtDepth = new int[depth + 1];
    public int ttHits = 0;
    public int ttCutoffs = 0;

    public GamBit(int depth) {
        this.depth = depth;
        this.board = new Bitboard();
        this.bestMove = 0;
        for (int i = 0; i < transpositionTable.length; i++) {
            transpositionTable[i] = new TTEntry();
        }
    }
    
    public GamBit(int depth, Bitboard board) {
        this.depth = depth;
        this.board = board;
        this.bestMove = 0;
        for (int i = 0; i < transpositionTable.length; i++) {
            transpositionTable[i] = new TTEntry();
        }
    }
    
    public double negamax(Bitboard simBoard, boolean toMove, int depth, double alpha, double beta) {

        nodeCount++;
        
        // terminal checks
        if (simBoard.isCheckMate(toMove)) return -MATE_SCORE - depth;
        if (simBoard.isStaleMate(toMove)) return 0;
        
        if (depth == 0) {
            return quiescence(simBoard, toMove, alpha, beta);
        }
        
        
        long hash = simBoard.getZobristHash();
        int index = (int) (hash & (transpositionTable.length - 1));
        TTEntry entry = transpositionTable[index];

        // transposition Table lookup
        if (entry.isValid(hash, depth)) {
            ttHits++;
            
            if (depth == this.depth && entry.getMove() != 0) {
                bestMove = entry.getMove();
            }
            if (entry.getFlag() == TTEntry.EXACT) {
            	if (entry.getMove() != 0) {
                    simBoard.makeMove(entry.getMove());
                    double val = -negamax(simBoard, !toMove, depth - 1, -beta, -alpha);
                    simBoard.undoMove();
                    return val;
                } else {
                    // No move stored, just return eval
                    return entry.getEval();
                }
            }
            else if (entry.getFlag() == TTEntry.LOWERBOUND) alpha = Math.max(alpha, entry.getEval());
            else if (entry.getFlag() == TTEntry.UPPERBOUND) beta = Math.min(beta, entry.getEval());
            
            if (alpha >= beta) {
                ttCutoffs++;
                return entry.getEval();
            }
        }



        // moves ordered by MVV_LVA
        ArrayList<MoveSet> moveList = simBoard.generateLegalMovesOrdered(toMove);

        // TT move prioritization
        if (entry != null && entry.getZobristKey() == hash) {
            int ttMove = entry.getMove();

            // only reorder if move is legal and not null
            for (int i = 0; i < moveList.size(); i++) {
                if (moveList.get(i).getMove() == ttMove) {
                    // swap TT move to front
                    if (i != 0) {
                        MoveSet best = moveList.remove(i);
                        moveList.add(0, best);
                    }
                    break;
                }
            }
        }

        double bestVal = -MATE_SCORE * 2;
        int bestMoveSoFar = 0;
        double originalAlpha = alpha;

        for (MoveSet moveSet : moveList) {
            simBoard.makeMove(moveSet.getMove());
            double val = -negamax(simBoard, !toMove, depth - 1, -beta, -alpha);
            simBoard.undoMove();

            if (val > bestVal) {
                bestVal = val;
                bestMoveSoFar = moveSet.getMove();
            }
            alpha = Math.max(alpha, val);

            // Beta cutoff
            if (alpha >= beta) {
                entry.set(hash, depth, bestVal, TTEntry.LOWERBOUND, bestMoveSoFar);
                if (depth == this.depth) bestMove = bestMoveSoFar;
                return bestVal;
            }
        }
        // Store in TT
        int flag;
        if (bestVal <= originalAlpha) flag = TTEntry.UPPERBOUND; // All node
        else if (bestVal >= beta) flag = TTEntry.LOWERBOUND;     // Cut node
        else flag = TTEntry.EXACT;                               // PV node

        entry.set(hash, depth, bestVal, flag, bestMoveSoFar);

        if (depth == this.depth) bestMove = bestMoveSoFar;

        return bestVal;
    }

    public double quiescence(Bitboard simBoard, boolean toMove, double alpha, double beta) {
        nodeCount++;

        if (simBoard.isCheckMate(toMove)) return -MATE_SCORE;
        if (simBoard.isStaleMate(toMove)) return 0;

        double staticEval = simBoard.evaluate();
        if (!toMove) staticEval = -staticEval;

        if (staticEval >= beta) return staticEval;
        if (staticEval > alpha) alpha = staticEval;

        ArrayList<int[]> captureAndChecks = simBoard.getCaptureAndChecks(toMove);

        for (int[] moveArray : captureAndChecks) {
            simBoard.makeMove(moveArray[0]);
            double val = -quiescence(simBoard, !toMove, -beta, -alpha);
            simBoard.undoMove();

            if (val >= beta) return val;
            if (val > alpha) alpha = val;
        }

        return alpha;
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
    
    public void clearTT() {
        for (int i = 0; i < transpositionTable.length; i++) {
            transpositionTable[i].set(0L, -1, 0.0, TTEntry.EXACT, 0);
        }
    }
    
    public void resetStats() {
    	nodeCount = 0;
        ttHits = 0;
        ttCutoffs = 0;
        nodesAtDepth = new int[depth + 1];
    }

	public long getTTCutOffs() {
		return ttCutoffs;
	}
	
	public long getTTHits() {
		return ttHits;
	}

}

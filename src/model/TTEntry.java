package model;

public class TTEntry {
    public static final int EXACT = 0;
    public static final int LOWERBOUND = 1;
    public static final int UPPERBOUND = 2;

    private long zobristKey;
    private double eval;
    private int depth;
    private int flag;
    private int move;

    public TTEntry() {
        zobristKey = 0L;
        depth = -1;
        move = 0;
    }

    public boolean isValid(long key, int searchDepth) {
        return (this.depth >= searchDepth) && (this.zobristKey == key);
    }

    public void set(long key, int depth, double eval, int flag, int move) {
        this.zobristKey = key;
        this.depth = depth;
        this.eval = eval;
        this.flag = flag;
        this.move = move;
    }

    public long getZobristKey() { return zobristKey; }
    public double getEval() { return eval; }
    public int getDepth() { return depth; }
    public int getFlag() { return flag; }
    public int getMove() { return move; }
}

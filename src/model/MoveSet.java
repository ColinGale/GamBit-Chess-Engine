package model;

public class MoveSet {
	
	private int move;
	private double score;
	
	public MoveSet(int move, double score) {
		this.move = move;
		this.score = score;
	}

	public int getMove() {
		return move;
	}

	public double getScore() {
		return score;
	}

}

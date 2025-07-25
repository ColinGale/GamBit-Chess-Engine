package model;

public class MoveOrder {
	
	static final int[][] MVV_LVA = new int[6][6]; // [victim][attacker]

	static {
	    int[] pieceValue = {1, 3, 3, 5, 9, 1000}; // P, N, B, R, Q, K
	    for (int victim = 0; victim < 6; victim++) {
	        for (int attacker = 0; attacker < 6; attacker++) {
	            MVV_LVA[victim][attacker] = pieceValue[victim] * 10 - pieceValue[attacker];
	        }
	    }
	}

	public static int getMVVLVAValue(int attackerType, int victimType) {
		if (victimType == -1) return 0;
	    return MVV_LVA[victimType][attackerType];
	}


}

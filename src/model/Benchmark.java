package model;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class Benchmark {
	
	public static void runNPSBenchmark(int depth, int runs) {
	    Bitboard board = new Bitboard();
	    GamBit engine = new GamBit(depth, board);

	    long totalNodes = 0;
	    long totalTimeNano = 0;
	    long totalTTCutOffs = 0;
	    long totalTTHits = 0;
	    
	    int warmup = 3;
	    for (int i = 0; i < warmup; i++) {
	    	
	    	double result = engine.negamax(board, true, depth, -Double.MAX_VALUE, Double.MAX_VALUE);
	    	
	    	engine.clearTT();
	    	engine.resetStats();
	    }

	    for (int i = 0; i < runs; i++) {
	    	engine.clearTT();
	    	engine.resetStats();
	    	
	        long startTime = System.nanoTime();
	        double result = engine.negamax(board, true, depth, -Double.MAX_VALUE, Double.MAX_VALUE);
	        long endTime = System.nanoTime();

	        long elapsedTime = endTime - startTime;
	        totalTimeNano += elapsedTime;
	        totalNodes += engine.getNodeCount();
	        totalTTCutOffs += engine.getTTCutOffs();
	        totalTTHits += engine.getTTHits();

	        System.out.printf("Run %d: Eval = %.2f, Nodes = %d, Time = %.2f sec\n", i + 1, result, engine.getNodeCount(), elapsedTime / 1e9);
	    }

	    double avgNPS = totalNodes / (totalTimeNano / 1e9);
	    System.out.println("========== Benchmark Result ==========");
	    System.out.printf("Depth: %d | Runs: %d\n", depth, runs);
	    System.out.printf("Total Nodes: %d\n", totalNodes);
	    System.out.printf("Total Time: %.2f sec\n", totalTimeNano / 1e9);
	    System.out.printf("TT Hit Rate: %.2f%%\n", 100.0 * totalTTHits / totalNodes);
	    System.out.printf("TT Cutoff Rate: %.2f%%\n", 100.0 * totalTTCutOffs / totalNodes);
	    System.out.printf("Average NPS: %.0f nodes/sec\n", avgNPS);
	    
	    OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        System.out.println("========== System Info ==========");
        System.out.println("OS Name: " + System.getProperty("os.name"));
        System.out.println("OS Version: " + System.getProperty("os.version"));
        System.out.println("Architecture: " + System.getProperty("os.arch"));
        System.out.println("Available Cores: " + Runtime.getRuntime().availableProcessors());

        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOS =
                    (com.sun.management.OperatingSystemMXBean) osBean;

            System.out.printf("System Load: %.2f%%\n", sunOS.getSystemCpuLoad() * 100);
            System.out.printf("Process Load: %.2f%%\n", sunOS.getProcessCpuLoad() * 100);
        }

        System.out.printf("Java Version: %s\n", System.getProperty("java.version"));
        System.out.printf("JVM: %s\n", System.getProperty("java.vm.name"));
        System.out.println();
	}


}
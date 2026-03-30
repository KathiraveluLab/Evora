package org.evora.core.util;

import org.evora.core.model.*;
import org.evora.core.orchestration.*;
import java.util.*;

/**
 * Empirical Evaluation Suite for the Évora Framework.
 * Reproduces Figure 5 (Speedup) and Figure 6 (Complexity) from the ETT 2018 paper.
 */
public class Benchmarker {

    private static final String[] SERVICE_POOL = {"s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8"};
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Évora Empirical Evaluation Suite (Section 5)   ");
        System.out.println("==================================================");
        System.out.println("Nodes | Greedy (ms) | Heuristic (ms) | Speedup");
        System.out.println("--------------------------------------------------");

        int[] nodeSizes = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        String[] nsc = {"s1", "s2", "s3", "s4", "s5"};
        UserPolicy policy = new UserPolicy(1.0, 1.0, 1.0);

        for (int n : nodeSizes) {
            Map<String, Node> topology = generateRandomTopology(n);
            Orchestrator orchestrator = new Orchestrator(topology);

            // Benchmark Greedy
            long startGreedy = System.nanoTime();
            orchestrator.solveGreedy(nsc, policy);
            long endGreedy = System.nanoTime();
            double greedyTime = (endGreedy - startGreedy) / 1_000_000.0;

            // Benchmark Heuristic
            long startHeuristic = System.nanoTime();
            orchestrator.solveHeuristic(nsc, policy);
            long endHeuristic = System.nanoTime();
            double heuristicTime = (endHeuristic - startHeuristic) / 1_000_000.0;

            double speedup = (heuristicTime > 0) ? (heuristicTime / greedyTime) : 1.0;

            System.out.printf("%-6d | %-11.4f | %-14.4f | %.2fx\n", 
                             n, greedyTime, heuristicTime, speedup);
        }
    }

    /**
     * Generates a random topology of N nodes with randomized services and links.
     */
    private static Map<String, Node> generateRandomTopology(int n) {
        Map<String, Node> nodes = new HashMap<>();
        for (int i = 0; i < n; i++) {
            String id = "n" + i;
            
            // Randomly assign 3 services from the pool
            Set<String> services = new HashSet<>();
            while (services.size() < 3) {
                services.add(SERVICE_POOL[RANDOM.nextInt(SERVICE_POOL.length)]);
            }

            // Create Node with random metrics
            Node node = new Node(id, new String[]{}, services.toArray(new String[0]));
            node.setCost(5 + RANDOM.nextDouble() * 20);
            node.setLatency(1 + RANDOM.nextDouble() * 10);
            node.setThroughput(50 + RANDOM.nextDouble() * 500);
            
            nodes.put(id, node);
        }

        // Add some random link latencies to simulate a connected graph
        for (int i = 0; i < n; i++) {
            Node node = nodes.get("n" + i);
            for (int k = 0; k < 3; k++) {
                String neighborId = "n" + RANDOM.nextInt(n);
                if (!neighborId.equals(node.getId())) {
                    node.addNeighborLatency(neighborId, 1 + RANDOM.nextDouble() * 20);
                }
            }
        }

        return nodes;
    }
}

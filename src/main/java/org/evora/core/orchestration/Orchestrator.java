package org.evora.core.orchestration;
import org.evora.core.model.*;

import java.util.Map;

/**
 * The Évora Orchestrator.
 * Implements VNF placement algorithms to minimize the MILP penalty function.
 */
public class Orchestrator {
    private Map<String, Node> nodeMap;

    public Orchestrator(Map<String, Node> nodeMap) {
        this.nodeMap = nodeMap;
    }

    /**
     * Solves the VNF placement problem using a Greedy approach.
     * Iterates through the service chain and selects the node that minimizes the penalty.
     */
    public PlacementSolution solveGreedy(String[] serviceChain, UserPolicy policy) {
        System.out.println("\n--- Initiating Greedy Placement Orchestration ---");
        PlacementSolution solution = new PlacementSolution();
        double totalPenalty = 0;
        double minThroughput = Double.MAX_VALUE;

        for (String service : serviceChain) {
            Node bestNode = null;
            double minLocalPenalty = Double.MAX_VALUE;

            for (Node node : nodeMap.values()) {
                if (containsService(node, service)) {
                    double penalty = calculatePenalty(node, policy);
                    if (penalty < minLocalPenalty) {
                        minLocalPenalty = penalty;
                        bestNode = node;
                    }
                }
            }

            if (bestNode != null) {
                solution.addMapping(service, bestNode.getId());
                totalPenalty += minLocalPenalty;
                minThroughput = Math.min(minThroughput, bestNode.getThroughput());
                System.out.println("Assigned " + service + " -> " + bestNode.getId() + " (Penalty: " + String.format("%.4f", minLocalPenalty) + ")");
            } else {
                System.err.println("CRITICAL: No node found hosting service: " + service);
                solution.setCompliant(false);
            }
        }

        // Final Compliance Checks (SLO Thresholds)
        Double throughputThreshold = policy.getThreshold("minThroughput");
        if (throughputThreshold != null && minThroughput < throughputThreshold) {
            System.out.println("ALERT: Solution throughput (" + minThroughput + ") below threshold (" + throughputThreshold + ")");
            solution.setCompliant(false);
        }

        solution.setTotalPenalty(totalPenalty);
        return solution;
    }

    private PlacementSolution bestSolution;
    private double bestPenalty = Double.MAX_VALUE;

    public PlacementSolution solveHeuristic(String[] serviceChain, UserPolicy policy) {
        System.out.println("\n--- Initiating Heuristic Placement Orchestration (Recursive) ---");
        bestPenalty = Double.MAX_VALUE;
        bestSolution = null;
        
        backtrack(serviceChain, policy, 0, 0, new PlacementSolution(), null);
        
        if (bestSolution == null) {
            System.err.println("CRITICAL: No valid heuristic solution found.");
            return new PlacementSolution();
        }
        
        System.out.println("Heuristic Search Complete. Best Total Penalty: " + String.format("%.4f", bestPenalty));
        return bestSolution;
    }

    private void backtrack(String[] serviceChain, UserPolicy policy, int index, double currentPenalty, 
                          PlacementSolution currentSolution, String lastNodeId) {
        
        // Pruning: if current path is already worse than the best found, stop.
        if (currentPenalty >= bestPenalty) return;

        // Base case: all services placed
        if (index == serviceChain.length) {
            bestPenalty = currentPenalty;
            // Create a deep copy of the current solution
            bestSolution = new PlacementSolution();
            for (Map.Entry<String, String> entry : currentSolution.getMappings().entrySet()) {
                bestSolution.addMapping(entry.getKey(), entry.getValue());
            }
            bestSolution.setTotalPenalty(bestPenalty);
            return;
        }

        String service = serviceChain[index];
        for (Node node : nodeMap.values()) {
            if (containsService(node, service)) {
                // Calculate Node Penalty
                double nodePenalty = calculatePenalty(node, policy);
                
                // Calculate Link Latency Penalty (if not the first service)
                double linkPenalty = 0;
                if (lastNodeId != null) {
                    Node prevNode = nodeMap.get(lastNodeId);
                    // Link penalty = policy_beta * link_latency
                    linkPenalty = policy.getBeta() * prevNode.getLatencyTo(node.getId());
                }

                double nextPenalty = currentPenalty + nodePenalty + linkPenalty;
                
                // Recursive step
                currentSolution.addMapping(service, node.getId());
                backtrack(serviceChain, policy, index + 1, nextPenalty, currentSolution, node.getId());
                
                // Backtrack (cleanup for next branch)
                currentSolution.getMappings().remove(service);
            }
        }
    }

    /**
     * Calculates the MILP penalty function: alpha*C + beta*L + gamma*T^-1
     */
    private double calculatePenalty(Node node, UserPolicy policy) {
        double costTerm = policy.getAlpha() * node.getCost();
        double latencyTerm = policy.getBeta() * node.getLatency();
        double throughputTerm = policy.getGamma() * (1.0 / node.getThroughput());
        
        return costTerm + latencyTerm + throughputTerm;
    }

    private boolean containsService(Node node, String serviceName) {
        for (String s : node.getServices()) {
            if (s.equalsIgnoreCase(serviceName)) return true;
        }
        return false;
    }
}

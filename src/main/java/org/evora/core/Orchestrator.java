/*
 * Copyright (c) 2018. Pradeeban Kathiravelu. All rights reserved.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.evora.core;

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

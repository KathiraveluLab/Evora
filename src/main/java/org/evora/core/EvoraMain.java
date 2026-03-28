/*
 * Copyright (c) 2018. Pradeeban Kathiravelu. All rights reserved.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.evora.core;

import java.util.HashMap;
import java.util.Map;

public class EvoraMain {
    private static Map<String, Node> tempNodes = new HashMap();
    private static String[] nsc = {"s5", "s4", "s3"};

    /**
     * Considers only the connected graphs.
     */
    private static void populateNscMap() {
        // Initializing the 12-node topology with varied resource metrics
        tempNodes.put("n9", new Node("n9", new String[]{"n6"}, new String[] {"s4"}, 12.0, 4.0, 150.0));
        tempNodes.put("n6", new Node("n6", new String[]{"n9", "n7"}, new String[] {"s2", "s3", "S4"}, 15.0, 3.0, 200.0));
        tempNodes.put("n7", new Node("n7", new String[]{"n6", "n8"}, new String[] {"s3", "S1"}, 8.0, 6.0, 80.0));
        tempNodes.put("n8", new Node("n8", new String[]{"n7", "n10"}, new String[] {"s2"}, 10.0, 5.0, 100.0));
        tempNodes.put("n10", new Node("n10", new String[]{"n8", "n11", "n12"}, new String[] {"s5"}, 20.0, 2.0, 300.0));
        tempNodes.put("n11", new Node("n11", new String[]{"n10", "n12", "n13"}, new String[] {"s1"}, 14.0, 4.0, 120.0));
        tempNodes.put("n12", new Node("n12", new String[]{"n10", "n11"}, new String[] {"s3", "s4"}, 11.0, 4.5, 110.0));
        tempNodes.put("n13", new Node("n13", new String[]{"n11", "n15"}, new String[] {"s2"}, 9.0, 5.5, 90.0));
        tempNodes.put("n15", new Node("n15", new String[]{"n13", "n14", "n16", "n17"}, new String[] {"s1"}, 18.0, 2.5, 250.0));
        tempNodes.put("n14", new Node("n14", new String[]{"n15"}, new String[] {"s2"}, 7.0, 7.0, 70.0));
        tempNodes.put("n17", new Node("n17", new String[]{"n15"}, new String[] {"s2"}, 10.0, 5.0, 100.0));
        tempNodes.put("n16", new Node("n16", new String[]{"n15"}, new String[] {"s3", "s4"}, 13.0, 3.5, 140.0));
    }

    public static void main(String[] args) {
        populateNscMap();
        System.out.println("Évora Research Framework Initialized.");
        
        try {
            // Load Service Registry configuration
            org.evora.registry.ServiceRegistry.getInstance().loadConfig("src/main/resources/services.conf");
            
            // Test Compatibility Layer: Chain Axis2 service with a CXF service
            CompatibilityLayer.chainServices(
                "dupl_instance_count", "axis2", 
                "dupl_instance_count", "cxf", 
                Map.of("sample", "data")
            );

            // Test Dynamic Placement Orchestrator
            Orchestrator orchestrator = new Orchestrator(tempNodes);
            
            // Policy 1: Cost Optimized (alpha=10.0, others=1.0)
            UserPolicy costPolicy = new UserPolicy(10.0, 1.0, 1.0);
            PlacementSolution sol1 = orchestrator.solveGreedy(nsc, costPolicy);
            System.out.println("Cost-Optimized Solution: " + sol1);

            // Policy 2: Latency Optimized (beta=10.0, others=1.0)
            UserPolicy latencyPolicy = new UserPolicy(1.0, 10.0, 1.0);
            PlacementSolution sol2 = orchestrator.solveGreedy(nsc, latencyPolicy);
            System.out.println("Latency-Optimized Solution: " + sol2);
            
        } catch (Exception e) {
            System.err.println("Error initializing Évora Ecosystem: " + e.getMessage());
        }
    }


}

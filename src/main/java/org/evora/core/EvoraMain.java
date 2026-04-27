package org.evora.core;

import org.evora.core.model.*;
import org.evora.core.invoker.*;
import org.evora.core.orchestration.*;
import java.util.HashMap;
import java.util.Map;

public class EvoraMain {
    private static Map<String, Node> tempNodes = new HashMap();
    private static String[] nsc = NSCBuilder.getDefaultChain();

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

        // Adding link latencies (ms) as per ETT 2018 topology
        tempNodes.get("n10").addNeighborLatency("n12", 2.0);
        tempNodes.get("n12").addNeighborLatency("n16", 5.0);
        tempNodes.get("n16").addNeighborLatency("n15", 3.0);
        tempNodes.get("n15").addNeighborLatency("n6", 12.0);
        tempNodes.get("n6").addNeighborLatency("n7", 4.0);
    }

    public static void main(String[] args) {
        System.out.println("Évora Research Framework Initialized.");
        
        try {
            // Load Service Registry configuration
            org.evora.registry.ServiceRegistry.getInstance().loadConfig("src/main/resources/services.conf");

            Map<String, Node> topology;
            String[] activeChain;
            UserPolicy activePolicy;

            String inputPath = "src/main/resources/input.json";
            if (new java.io.File(inputPath).exists()) {
                System.out.println("--- Loading Custom Inputs from " + inputPath + " ---");
                org.evora.core.util.JsonInputLoader.SimulationInput input = org.evora.core.util.JsonInputLoader.load(inputPath);
                topology = input.topology;
                activeChain = input.serviceChain;
                activePolicy = input.policy;
            } else {
                System.out.println("--- Using Internal Default Topology ---");
                populateNscMap();
                topology = tempNodes;
                activeChain = nsc;
                activePolicy = new UserPolicy(1.0, 10.0, 1.0); // Default latency-optimized
            }
            
            // Test Compatibility Layer: Chain Axis2 service with a CXF service
            CompatibilityLayer.chainServices(
                "dupl_instance_count", "axis2", 
                "dupl_instance_count", "cxf", 
                Map.of("sample", "data")
            );

            // Test Dynamic Placement Orchestrator
            Orchestrator orchestrator = new Orchestrator(topology);
            
            // 1. Solve using the loaded policy/chain
            System.out.println("\n--- Orchestrating Loaded Service Chain ---");
            PlacementSolution sol = orchestrator.solveGreedy(activeChain, activePolicy);
            System.out.println("Heuristic Solution: " + sol);

            // Test Dynamic Flow Actuation
            DynamicActuator actuator = new DynamicActuator();
            actuator.actuate(sol);

            // Test SLM: VNF Migration Scenario
            System.out.println("\n--- Initiating Dynamic Migration Test (SLM) ---");
            ServiceLifecycleManager slm = new ServiceLifecycleManager(orchestrator, actuator);
            
            // Trigger SLM re-evaluation on the first node of the chain
            String targetService = activeChain[0];
            String targetNodeId = sol.getMappings().get(targetService);
            System.out.println("Simulating congestion on node " + targetNodeId + " for service " + targetService);
            topology.get(targetNodeId).setLatency(100.0); 

            slm.onResourceChange(targetService, targetNodeId, activeChain, activePolicy);
            
            System.out.println("\n--- Évora Simulation Complete ---");
            System.exit(0);
            
        } catch (Exception e) {
            System.err.println("Error initializing Évora Ecosystem: " + e.getMessage());
            System.exit(1);
        }
    }


}

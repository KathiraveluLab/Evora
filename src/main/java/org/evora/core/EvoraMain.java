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
            System.out.println("Greedy Latency-Optimized Solution: " + sol2);

            // Test solveHeuristic (Global Optimum)
            PlacementSolution solHeuristic = orchestrator.solveHeuristic(nsc, latencyPolicy);
            System.out.println("Heuristic Latency-Optimized Solution: " + solHeuristic);

            // Test Dynamic Flow Actuation
            DynamicActuator actuator = new DynamicActuator();
            actuator.actuate(solHeuristic);

            // Test SLM: VNF Migration Scenario (Section V.C)
            System.out.println("\n--- Initiating Dynamic Migration Test (SLM) ---");
            ServiceLifecycleManager slm = new ServiceLifecycleManager(orchestrator, actuator);
            
            // Simulate congestion on the current optimal node for 's5'
            String s5NodeId = solHeuristic.getMappings().get("s5");
            Node s5Node = tempNodes.get(s5NodeId);
            System.out.println("Simulating congestion on node " + s5NodeId + " (Latency 2ms -> 50ms)");
            s5Node.setLatency(50.0); 

            // Trigger SLM re-evaluation
            slm.onResourceChange("s5", s5NodeId, nsc, latencyPolicy);
            
            System.out.println("\n--- Évora Simulation Complete ---");
            System.exit(0);
            
        } catch (Exception e) {
            System.err.println("Error initializing Évora Ecosystem: " + e.getMessage());
            System.exit(1);
        }
    }


}

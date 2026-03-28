/*
 * Copyright (c) 2018. Pradeeban Kathiravelu. All rights reserved.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.evora.core;

/**
 * Service Lifecycle Manager (SLM) for Évora.
 * Handles dynamic VNF migration and re-optimization as described in Section V.C.
 */
public class ServiceLifecycleManager {
    private Orchestrator orchestrator;
    private DynamicActuator actuator;

    public ServiceLifecycleManager(Orchestrator orchestrator, DynamicActuator actuator) {
        this.orchestrator = orchestrator;
        this.actuator = actuator;
    }

    /**
     * Called when a node's resource metrics change dynamically.
     * Triggers a re-evaluation of the current placement.
     */
    public void onResourceChange(String service, String currentNodeId, String[] serviceChain, UserPolicy policy) {
        System.out.println("\n[SLM] Event Detected: Resource metrics changed for node " + currentNodeId);
        System.out.println("[SLM] Evaluating if service " + service + " needs migration...");

        // Re-run the orchestrator to find the new optimal placement
        PlacementSolution newSolution = orchestrator.solveHeuristic(serviceChain, policy);
        String newBestNodeId = newSolution.getMappings().get(service);

        if (!newBestNodeId.equals(currentNodeId)) {
            System.out.println("[SLM] MIGRATION TRIGGERED: " + service + " moving from " + currentNodeId + " to " + newBestNodeId);
            actuateMigration(service, currentNodeId, newBestNodeId);
        } else {
            System.out.println("[SLM] NO MIGRATION NEEDED: Current node " + currentNodeId + " remains optimal.");
        }
    }

    private void actuateMigration(String service, String fromNode, String toNode) {
        System.out.println("[SLM] Actuating flow modification for VNF migration...");
        // In a real system, we might need to handle state transfer here (Section V.C)
        // For the research prototype, we trigger the SDN flow update
        PlacementSolution migrationStep = new PlacementSolution();
        migrationStep.addMapping(service, toNode);
        actuator.actuate(migrationStep);
        
        System.out.println("[SLM] Migration Successful: " + service + " -> " + toNode);
    }
}

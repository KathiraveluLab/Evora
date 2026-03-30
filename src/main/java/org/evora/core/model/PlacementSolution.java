package org.evora.core.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Model to store the result of a VNF placement orchestration.
 */
public class PlacementSolution {
    private Map<String, String> serviceToNode = new LinkedHashMap<>();
    private double totalPenalty;
    private boolean compliant = true;

    public void addMapping(String service, String node) {
        serviceToNode.put(service, node);
    }

    public Map<String, String> getMappings() {
        return serviceToNode;
    }

    public double getTotalPenalty() {
        return totalPenalty;
    }

    public void setTotalPenalty(double totalPenalty) {
        this.totalPenalty = totalPenalty;
    }

    public boolean isCompliant() {
        return compliant;
    }

    public void setCompliant(boolean compliant) {
        this.compliant = compliant;
    }

    @Override
    public String toString() {
        return "PlacementSolution{" +
                "mappings=" + serviceToNode +
                ", penalty=" + String.format("%.4f", totalPenalty) +
                ", compliant=" + compliant +
                '}';
    }
}

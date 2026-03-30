package org.evora.core.model;

import java.util.HashMap;
import java.util.Map;

/**
 * POJO for user-defined policies and SLOs.
 * Used to weight the MILP optimization model: min(alpha*C + beta*L + gamma*T^-1).
 */
public class UserPolicy {
    private double alpha = 1.0; // Cost weight
    private double beta = 1.0;  // Latency weight
    private double gamma = 1.0; // Throughput weight
    
    private Map<String, Double> thresholds = new HashMap<>();

    public UserPolicy(double alpha, double beta, double gamma) {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
    }

    public double getAlpha() { return alpha; }
    public double getBeta() { return beta; }
    public double getGamma() { return gamma; }

    public void setThreshold(String key, double value) {
        thresholds.put(key, value);
    }

    public Double getThreshold(String key) {
        return thresholds.get(key);
    }
}


package org.evora.core.model;

public class Node {
    private String id;
    private String links[];
    private String services[];
    
    // Resource metrics for MILP optimization
    private double cost = 10.0;     // Default unit cost
    private double latency = 5.0;  // Default processing latency (ms)
    private double throughput = 100.0; // Default throughput (Mbps)
    
    // Link latencies to neighbors (ms)
    private java.util.Map<String, Double> neighborLatencies = new java.util.HashMap<>();

    public Node(String id, String[] links, String[] services) {
        this.id = id;
        this.links = links;
        this.services = services;
    }

    public Node(String id, String[] links, String[] services, double cost, double latency, double throughput) {
        this.id = id;
        this.links = links;
        this.services = services;
        this.cost = cost;
        this.latency = latency;
        this.throughput = throughput;
    }

    public String[] getServices() {
        return services;
    }

    public void setServices(String[] services) {
        this.services = services;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getLinks() {
        return links;
    }

    public void setLinks(String[] links) {
        this.links = links;
    }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public double getLatency() { return latency; }
    public void setLatency(double latency) { this.latency = latency; }

    public double getThroughput() { return throughput; }
    public void setThroughput(double throughput) { this.throughput = throughput; }

    public java.util.Map<String, Double> getNeighborLatencies() { return neighborLatencies; }
    public void addNeighborLatency(String neighborId, double latency) {
        this.neighborLatencies.put(neighborId, latency);
    }
    public double getLatencyTo(String neighborId) {
        return neighborLatencies.getOrDefault(neighborId, 10.0); // Default link delay 10ms
    }
}

/*
 * Copyright (c) 2018. Pradeeban Kathiravelu. All rights reserved.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 */

package org.evora.core;

public class Node {
    private String id;
    private String links[];
    private String services[];
    
    // Resource metrics for MILP optimization
    private double cost = 10.0;     // Default unit cost
    private double latency = 5.0;  // Default processing latency (ms)
    private double throughput = 100.0; // Default throughput (Mbps)

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
}

package org.evora.registry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Évora Service Registry.
 * Manages service descriptions and endpoints for heterogeneous building blocks.
 */
public class ServiceRegistry {
    private static ServiceRegistry instance = null;
    private Map<String, ServiceDescription> services = new HashMap<>();

    private ServiceRegistry() {}

    public static ServiceRegistry getInstance() {
        if (instance == null) {
            instance = new ServiceRegistry();
        }
        return instance;
    }

    public void loadConfig(String path) throws IOException {
        this.services = NginxConfigParser.parse(path);
        System.out.println("Loaded " + services.size() + " services from " + path);
    }

    public ServiceDescription getService(String name) {
        return services.get(name);
    }

    public Map<String, ServiceDescription> getAllServices() {
        return services;
    }

    /**
     * Logic to find the best-fit endpoint for a service implementation.
     * Currently returns the first available endpoint (Round-robin or load-based can be added).
     */
    public String getBestFitEndpoint(String serviceName, String implName) {
        ServiceDescription sd = services.get(serviceName);
        if (sd != null && sd.getImplementations().containsKey(implName)) {
            return sd.getImplementations().get(implName).get(0);
        }
        return null;
    }
}

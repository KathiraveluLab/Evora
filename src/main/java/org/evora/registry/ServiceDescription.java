package org.evora.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model for a service description in the Mayan registry.
 * Supports simple services and composite service compositions.
 */
public class ServiceDescription {
    private String name;
    private String type; // "simple" or "composition"
    private String entryPoint;
    private String description;
    
    // For simple services: implementation name (e.g., "axis2") to list of endpoints
    private Map<String, List<String>> implementations = new HashMap<>();
    
    // For composite services: sub-service names and their order/properties
    private Map<String, Map<String, Object>> subServices = new HashMap<>();

    public ServiceDescription(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, List<String>> getImplementations() {
        return implementations;
    }

    public void addImplementation(String implName, List<String> endpoints) {
        this.implementations.put(implName, endpoints);
    }

    public Map<String, Map<String, Object>> getSubServices() {
        return subServices;
    }

    public void addSubService(String subServiceName, Map<String, Object> properties) {
        this.subServices.put(subServiceName, properties);
    }
}

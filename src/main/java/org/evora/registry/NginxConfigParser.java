package org.evora.registry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A basic parser for the Nginx-style service configuration format.
 */
public class NginxConfigParser {

    public static Map<String, ServiceDescription> parse(String filePath) throws IOException {
        Map<String, ServiceDescription> services = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                if (line.startsWith("service ") && line.endsWith("{")) {
                    String serviceName = line.substring(8, line.length() - 1).trim();
                    ServiceDescription sd = parseService(reader, serviceName);
                    services.put(serviceName, sd);
                }
            }
        }
        return services;
    }

    private static ServiceDescription parseService(BufferedReader reader, String name) throws IOException {
        ServiceDescription sd = new ServiceDescription(name);
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.equals("}")) break;
            if (line.isEmpty() || line.startsWith("#")) continue;
            
            if (line.startsWith("type ")) {
                sd.setType(line.substring(5).replace(";", "").trim());
            } else if (line.startsWith("entry_point ")) {
                sd.setEntryPoint(line.substring(12).replace(";", "").trim());
            } else if (line.startsWith("description {")) {
                StringBuilder desc = new StringBuilder();
                while (!(line = reader.readLine().trim()).equals("};")) {
                    desc.append(line).append(" ");
                }
                sd.setDescription(desc.toString().trim());
            } else if (line.startsWith("impl ") && line.endsWith("{")) {
                String implName = line.substring(5, line.length() - 1).trim();
                List<String> endpoints = parseImpl(reader);
                sd.addImplementation(implName, endpoints);
            } else if (line.startsWith("services {") && "composition".equals(sd.getType())) {
                parseSubServices(reader, sd);
            }
        }
        return sd;
    }

    private static List<String> parseImpl(BufferedReader reader) throws IOException {
        List<String> endpoints = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.equals("}")) break;
            if (line.isEmpty() || line.startsWith("#")) continue;
            
            if (line.contains(" ")) {
                // Handle both "key value;" and simple value lists if needed
                String endpoint = line.substring(line.lastIndexOf(" ") + 1).replace(";", "").trim();
                endpoints.add(endpoint);
            } else {
                endpoints.add(line.replace(";", ""));
            }
        }
        return endpoints;
    }

    private static void parseSubServices(BufferedReader reader, ServiceDescription sd) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.equals("}")) break;
            if (line.isEmpty() || line.startsWith("#")) continue;
            
            if (line.endsWith("{")) {
                String subServiceName = line.substring(0, line.length() - 1).trim();
                Map<String, Object> props = new HashMap<>();
                while (!(line = reader.readLine().trim()).equals("}")) {
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split(" ");
                    if (parts.length >= 2) {
                        props.put(parts[0], parts[1].replace(";", ""));
                    }
                }
                sd.addSubService(subServiceName, props);
            }
        }
    }
}

package org.evora.core.util;

import org.evora.core.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility to load Évora simulation inputs from a JSON file.
 */
public class JsonInputLoader {

    public static class SimulationInput {
        public Map<String, Node> topology;
        public String[] serviceChain;
        public UserPolicy policy;
    }

    public static SimulationInput load(String filePath) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject root = new JSONObject(content);
        SimulationInput input = new SimulationInput();

        // 1. Load Topology
        input.topology = new HashMap<>();
        JSONArray nodesArray = root.getJSONArray("topology");
        for (int i = 0; i < nodesArray.length(); i++) {
            JSONObject nObj = nodesArray.getJSONObject(i);
            String id = nObj.getString("id");
            
            JSONArray neighborsArray = nObj.getJSONArray("neighbors");
            String[] neighbors = new String[neighborsArray.length()];
            for (int k = 0; k < neighborsArray.length(); k++) neighbors[k] = neighborsArray.getString(k);

            JSONArray servicesArray = nObj.getJSONArray("services");
            String[] services = new String[servicesArray.length()];
            for (int k = 0; k < servicesArray.length(); k++) services[k] = servicesArray.getString(k);

            Node node = new Node(id, neighbors, services);
            node.setCost(nObj.getDouble("cost"));
            node.setLatency(nObj.getDouble("latency"));
            node.setThroughput(nObj.getDouble("throughput"));
            
            input.topology.put(id, node);
        }

        // 2. Load Link Latencies
        if (root.has("links")) {
            JSONArray linksArray = root.getJSONArray("links");
            for (int i = 0; i < linksArray.length(); i++) {
                JSONObject lObj = linksArray.getJSONObject(i);
                String from = lObj.getString("from");
                String to = lObj.getString("to");
                double latency = lObj.getDouble("latency");
                if (input.topology.containsKey(from)) {
                    input.topology.get(from).addNeighborLatency(to, latency);
                }
            }
        }

        // 3. Load Service Chain
        JSONArray chainArray = root.getJSONArray("serviceChain");
        input.serviceChain = new String[chainArray.length()];
        for (int i = 0; i < chainArray.length(); i++) input.serviceChain[i] = chainArray.getString(i);

        // 4. Load Policy
        JSONObject pObj = root.getJSONObject("policy");
        input.policy = new UserPolicy(
            pObj.getDouble("costWeight"),
            pObj.getDouble("latencyWeight"),
            pObj.getDouble("throughputWeight")
        );

        return input;
    }
}

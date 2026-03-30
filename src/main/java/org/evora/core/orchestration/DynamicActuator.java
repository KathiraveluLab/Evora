package org.evora.core.orchestration;
import org.evora.core.model.*;

import java.util.Map;

/**
 * The Dynamic Actuator for SDN Flow Management.
 * Translates orchestration solutions into real-time flow modifications
 * using the Messaging4Transport middleware.
 */
public class DynamicActuator {

    /**
     * Actuates the placement solution by pushing flow updates to the SDN Controller.
     * @param solution The placement solution to be actuated.
     */
    public void actuate(PlacementSolution solution) {
        if (!solution.isCompliant()) {
            System.err.println("ABORT: Cannot actuate a non-compliant placement solution.");
            return;
        }

        System.out.println("\n--- Initiating Dynamic Flow Actuation (Mayan IV.B) ---");
        
        for (Map.Entry<String, String> entry : solution.getMappings().entrySet()) {
            String service = entry.getKey();
            String nodeId = entry.getValue();
            
            // Logic to publish to ODL via AMQP/M4T
            publishFlowUpdate(service, nodeId);
        }
        
        System.out.println("Actuation Complete. Flow tables updated in OpenDaylight.");
    }

    /**
     * Publishes a flow update to the MOM-SDN bridge using the real M4T service.
     */
    private void publishFlowUpdate(String service, String nodeId) {
        String topic = "odl/flow_update/" + service;
        String payload = "{\"action\": \"ROUTE\", \"target_node\": \"" + nodeId + "\"}";
        
        System.out.println("[Actuator] Calling Messaging4Transport to update flow: " + service);
        
        // Real invocation of the M4T Service
        org.opendaylight.messaging4transport.Messaging4TransportService m4t = 
            org.opendaylight.messaging4transport.M4TFactory.getService();
            
        m4t.publish(topic, payload);
    }
}

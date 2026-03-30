package org.evora.core.invoker;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.rpc.client.RPCServiceClient;
import javax.xml.namespace.QName;
import java.util.Map;

/**
 * Axis2 implementation of the Invoker.
 * Handles specific SOAP/OMElement formatting required by the Axis2 engine.
 */
public class Axis2Invoker implements Invoker {
    @Override
    public Object invoke(String endpoint, Map<String, Object> params) {
        System.out.println("[Axis2 Engine] Invoking SOAP endpoint: " + endpoint);
        try {
            RPCServiceClient serviceClient = new RPCServiceClient();
            EndpointReference targetEPR = new EndpointReference(endpoint);
            serviceClient.getOptions().setTo(targetEPR);

            // Extract namespace and operation from params, or use sensible defaults for a research prototype
            String namespace = (String) params.getOrDefault("namespace", "http://ws.apache.org/axis2");
            String operationName = (String) params.getOrDefault("operation", "execute");

            QName opName = new QName(namespace, operationName);

            // Filter out internal metadata keys from the actual service arguments
            Object[] args = params.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals("namespace") && !entry.getKey().equals("operation"))
                    .map(Map.Entry::getValue)
                    .toArray();

            // Synchronous SOAP invocation (assuming a generic return type of Object/String for simplicity)
            Class<?>[] returnTypes = new Class[]{Object.class};
            Object[] response = serviceClient.invokeBlocking(opName, args, returnTypes);

            return (response != null && response.length > 0) ? response[0] : null;

        } catch (Exception e) {
            System.err.println("[Axis2 Error] Failed to invoke service: " + e.getMessage());
            return "Axis2_Invocation_Error: " + e.getMessage();
        }
    }
}

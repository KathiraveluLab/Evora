package org.evora.core.invoker;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import java.util.Map;

/**
 * Apache CXF implementation of the Invoker.
 * Supports JAX-WS and JAX-RS based service invocations.
 */
public class CXFInvoker implements Invoker {
    @Override
    public Object invoke(String endpoint, Map<String, Object> params) {
        System.out.println("[CXF Engine] Invoking JAX-WS/RS endpoint: " + endpoint);
        try {
            JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
            
            // In a dynamic client scenario, the endpoint is typically the WSDL URL or needs the ?wsdl suffix
            String wsdlUrl = (String) params.getOrDefault("wsdl", endpoint + "?wsdl");
            Client client = dcf.createClient(wsdlUrl);

            String operationName = (String) params.getOrDefault("operation", "execute");

            // Filter out metadata and prepare arguments for the dynamic invocation
            Object[] args = params.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals("wsdl") && !entry.getKey().equals("operation"))
                    .map(Map.Entry::getValue)
                    .toArray();

            Object[] response = client.invoke(operationName, args);

            return (response != null && response.length > 0) ? response[0] : null;

        } catch (Exception e) {
            System.err.println("[CXF Error] Failed to invoke service: " + e.getMessage());
            return "CXF_Invocation_Error: " + e.getMessage();
        }
    }
}

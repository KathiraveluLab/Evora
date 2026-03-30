package org.evora.core.invoker;

import org.evora.registry.ServiceRegistry;
import org.json.JSONObject;
import org.json.XML;
import java.util.Map;

/**
 * The 'Glue' logic that bridges incompatible service engines (Axis2 vs. CXF).
 * It selects the appropriate invoker based on the service implementation
 * and handles data transformations (Future Work: Data Converters).
 */
public class CompatibilityLayer {
    private static final Invoker axis2Invoker = new Axis2Invoker();
    private static final Invoker cxfInvoker = new CXFInvoker();

    /**
     * Invokes a service by name and implementation type.
     * @param serviceName Name of the service.
     * @param implType Type of implementation (axis2, cxf).
     * @param params Inbound parameters.
     * @return Execution result.
     */
    public static Object invokeService(String serviceName, String implType, Map<String, Object> params) {
        ServiceRegistry registry = ServiceRegistry.getInstance();
        String endpoint = registry.getBestFitEndpoint(serviceName, implType);
        
        if (endpoint == null) {
            System.err.println("WARNING: No endpoint found for service: " + serviceName + " [" + implType + "]");
            return "NULL_Result_for_" + serviceName;
        }

        Invoker invoker;
        if ("axis2".equalsIgnoreCase(implType)) {
            invoker = axis2Invoker;
        } else if ("cxf".equalsIgnoreCase(implType)) {
            invoker = cxfInvoker;
        } else {
            // Default generic invoker for unspecified engines
            invoker = (e, p) -> "Generic_Result_from_" + e;
        }

        return invoker.invoke(endpoint, params);
    }
    
    /**
     * Chains two potentially incompatible services together.
     * This is the 'Glue' that allows a SOAP service output to become REST input.
     */
    public static Object chainServices(String s1, String impl1, String s2, String impl2, Map<String, Object> initialParams) {
        System.out.println("\n--- Initiating Chained Invocation: " + s1 + " -> " + s2 + " ---");
        
        Object result1 = invokeService(s1, impl1, initialParams);
        System.out.println("Service 1 Result: " + result1);
        
        // Data Transformation 'Glue' Logic:
        // Automatically detect XML output and convert to JSON for modern service compatibility.
        Object transformedResult = result1;
        if (result1 instanceof String && ((String) result1).trim().startsWith("<")) {
            System.out.println("[Glue] XML detected, converting to JSON for next service...");
            transformedResult = convertXmlToJson((String) result1);
            System.out.println("[Glue] Transformed Result: " + transformedResult);
        }

        Map<String, Object> nextParams = Map.of("chained_input", transformedResult);
        
        Object result2 = invokeService(s2, impl2, nextParams);
        System.out.println("Service 2 Result: " + result2);
        
        return result2;
    }

    /**
     * Helper to perform XML to JSON transformation.
     * @param xml Input XML string.
     * @return JSON representation.
     */
    private static String convertXmlToJson(String xml) {
        try {
            JSONObject json = XML.toJSONObject(xml);
            return json.toString(4);
        } catch (Exception e) {
            System.err.println("[Glue Error] XML to JSON conversion failed: " + e.getMessage());
            return xml; // Fallback to original content
        }
    }
}

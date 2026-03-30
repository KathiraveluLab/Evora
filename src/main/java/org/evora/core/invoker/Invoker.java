package org.evora.core.invoker;

import java.util.Map;

/**
 * Interface for service invocation across different engines.
 * Acts as the base abstraction for heterogeneous VNF implementations.
 */
public interface Invoker {
    /**
     * Executes a service invocation.
     * @param endpoint The service endpoint (IP/URL).
     * @param params Input parameters for the service.
     * @return Output of the service execution.
     */
    Object invoke(String endpoint, Map<String, Object> params);
}

/*
 * Copyright (c) 2018. Pradeeban Kathiravelu. All rights reserved.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.evora.core;

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

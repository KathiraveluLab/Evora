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
 * Apache CXF implementation of the Invoker.
 * Supports JAX-WS and JAX-RS based service invocations.
 */
public class CXFInvoker implements Invoker {
    @Override
    public Object invoke(String endpoint, Map<String, Object> params) {
        System.out.println("[CXF Engine] Invoking JAX-WS/RS endpoint: " + endpoint + " with parameters: " + params);
        // In a real scenario, this would use CXF Dynamic Client Factory
        return "CXF_Response_Data_from_" + endpoint;
    }
}

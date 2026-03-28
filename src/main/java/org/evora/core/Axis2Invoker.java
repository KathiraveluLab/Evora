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
 * Axis2 implementation of the Invoker.
 * Handles specific SOAP/OMElement formatting required by the Axis2 engine.
 */
public class Axis2Invoker implements Invoker {
    @Override
    public Object invoke(String endpoint, Map<String, Object> params) {
        System.out.println("[Axis2 Engine] Invoking SOAP endpoint: " + endpoint + " with parameters: " + params);
        // In a real scenario, this would involve Axis2 RPCServiceClient or similar
        return "Axis2_Response_Data_from_" + endpoint;
    }
}

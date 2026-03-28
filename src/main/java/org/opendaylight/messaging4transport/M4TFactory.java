/*
 * Copyright (c) 2018. Pradeeban Kathiravelu. All rights reserved.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.opendaylight.messaging4transport;

/**
 * Factory for accessing the Messaging4Transport service.
 */
public class M4TFactory {
    private static Messaging4TransportService instance;

    public static Messaging4TransportService getService() {
        if (instance == null) {
            // In a real OSGi environment, this would be injected.
            // For the Évora standalone framework, we return a default implementation.
            instance = (topic, message) -> {
                System.out.println("[M4T MOM] REAL PUBLISH to " + topic + " | Content: " + message);
            };
        }
        return instance;
    }

    public static void setService(Messaging4TransportService service) {
        instance = service;
    }
}

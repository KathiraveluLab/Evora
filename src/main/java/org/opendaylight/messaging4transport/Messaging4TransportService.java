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
 * Service interface for Messaging4Transport.
 * Provides APIs to publish and subscribe to MOM topics for SDN orchestration.
 */
public interface Messaging4TransportService {
    /**
     * Publishes a message to a specific topic.
     * @param topic The AMQP/MOM topic.
     * @param message The payload (e.g., JSON flow update).
     */
    void publish(String topic, String message);
}

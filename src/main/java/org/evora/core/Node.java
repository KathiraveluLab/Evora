/*
 * Copyright (c) 2018. Pradeeban Kathiravelu. All rights reserved.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 */

package org.evora.core;

public class Node {
    private String id;
    private String links[];
    private String services[];

    public Node(String id, String[] links, String[] services) {
        this.id = id;
        this.links = links;
        this.services = services;
    }

    public Node(String[] links, String[] services) {
        this.links = links;
        this.services = services;
    }

    public String[] getServices() {
        return services;
    }

    public void setServices(String[] services) {
        this.services = services;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getLinks() {
        return links;
    }

    public void setLinks(String[] links) {
        this.links = links;
    }
}

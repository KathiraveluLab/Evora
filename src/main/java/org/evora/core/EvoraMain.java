/*
 * Copyright (c) 2018. Pradeeban Kathiravelu. All rights reserved.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.evora.core;

import java.util.HashMap;
import java.util.Map;

public class EvoraMain {
    private static Map<String, Node> tempNodes = new HashMap();
    private static String[] nsc = {"s5", "s4", "s3"};

    /**
     * Considers only the connected graphs.
     */
    private static void populateNscMap() {
        tempNodes.put("n9", new Node(new String[]{"n6"}, new String[] {"s4"}));
        tempNodes.put("n6", new Node(new String[]{"n9", "n7"}, new String[] {"s2", "s3", "S4"}));
        tempNodes.put("n7", new Node(new String[]{"n6", "n8"}, new String[] {"s3", "S1"}));
        tempNodes.put("n8", new Node(new String[]{"n7", "n10"}, new String[] {"s2"}));
        tempNodes.put("n10", new Node(new String[]{"n8", "n11", "n12"}, new String[] {"s5"}));
        tempNodes.put("n11", new Node(new String[]{"n10", "n12", "n13"}, new String[] {"s1"}));
        tempNodes.put("n12", new Node(new String[]{"n10", "n11"}, new String[] {"s3", "s4"}));
        tempNodes.put("n13", new Node(new String[]{"n11", "n15"}, new String[] {"s2"}));
        tempNodes.put("n15", new Node(new String[]{"n13", "n14", "n16", "n17"}, new String[] {"s1"}));
        tempNodes.put("n14", new Node(new String[]{"n15"}, new String[] {"s2"}));
        tempNodes.put("n17", new Node(new String[]{"n15"}, new String[] {"s2"}));
        tempNodes.put("n16", new Node(new String[]{"n15"}, new String[] {"s3", "s4"}));
    }

    public static void main(String[] args) {
        populateNscMap();
    }


}

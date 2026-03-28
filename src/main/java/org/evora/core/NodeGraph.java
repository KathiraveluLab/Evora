/*
 * Copyright (c) 2018. Pradeeban Kathiravelu. All rights reserved.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 */

package org.evora.core;

import java.util.ArrayList;
import java.util.List;

public class NodeGraph {
    private NodeGraph nodeGraph = null;
    private List<Node> nodeList;

    private NodeGraph(){
        nodeList = new ArrayList<>();
    }

    public NodeGraph getNodeGraph() {
        if (nodeGraph == null) {
            nodeGraph = new NodeGraph();
        }
        return nodeGraph;
    }

    public void addNode(Node node) {
        nodeList.add(node);
    }

    public void addNodes(List<Node> nodes) {
        nodeList.addAll(nodes);
    }
}


package org.evora.core.model;

import java.util.ArrayList;
import java.util.List;

public class NodeGraph {
    private static NodeGraph instance = null;
    private List<Node> nodeList;

    private NodeGraph(){
        nodeList = new ArrayList<>();
    }

    public static NodeGraph getInstance() {
        if (instance == null) {
            instance = new NodeGraph();
        }
        return instance;
    }

    public void addNode(Node node) {
        nodeList.add(node);
    }

    public void addNodes(List<Node> nodes) {
        nodeList.addAll(nodes);
    }
}

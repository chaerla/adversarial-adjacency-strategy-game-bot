package lib;

import javax.swing.tree.TreeNode;
import java.util.HashMap;
import java.util.Map;

public class NodeTree {
    private int value;
    private NodeTree parent;
    private Map<Coordinate, NodeTree> children;
    private int individualId = -1;

    public NodeTree(int value) {
        this.value = value;
        this.children = new HashMap<>();
    }

    public NodeTree(int value, NodeTree parent) {
        this.value = value;
        this.parent = parent;
        this.children = new HashMap<>();
    }

    public NodeTree getChild(Coordinate key) {
        return children.get(key);
    }

    public boolean childExists(Coordinate key) {
        return children.containsKey(key);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void addChildren(int value, Coordinate coordinate) {
        NodeTree children = new NodeTree(value, this);
        this.children.put(coordinate, children);
    }

    public Map<Coordinate, NodeTree> getChildren() {
        return children;
    }

    public int getValue() {
        return value;
    }

    public NodeTree getParent() {
        return parent;
    }

    public int getIndividualId() {
        return individualId;
    }

    public void setIndividualId(int individualId) {
        this.individualId = individualId;
    }
}

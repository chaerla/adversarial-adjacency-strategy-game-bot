package lib;

import javax.swing.tree.TreeNode;
import java.util.HashMap;
import java.util.Map;

public class NodeTree {
    private double value;
    private NodeTree parent;
    private Map<Coordinate, NodeTree> children;

    public NodeTree(double value) {
        this.value = value;
        this.children = new HashMap<>();
    }

    public NodeTree(double value, NodeTree parent) {
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

    public void setValue(double value) {
        this.value = value;
    }

    public void addChildren(double value, Coordinate coordinate) {
        NodeTree children = new NodeTree(value, this);
        this.children.put(coordinate, children);
    }
}

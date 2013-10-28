package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;
import org.jdom2.Element;

public class NonTerminal implements TreeNode {

    /** The ID of the node */
    private String id;

    /** Label */
    private String label;

    /** Wether the edge between this node and its parent is the "head" or not. */
    private boolean head;

    /** The ID of the edge between this node and its parent. */
    private String edgeId;

    /** Nodes' children */
    private List<TreeNode> children;


    NonTerminal(String id, String label) {
	this.id = id;
	this.label = label;
	this.head = false;
	this.children = new ArrayList<TreeNode>();
    }

    public String getId() {
	return this.id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getEdgeId() {
	return this.edgeId;
    }

    public void setEdgeId(String edgeId) {
	this.edgeId = edgeId;
    }

    public String getLabel() {
	return this.label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

    public boolean getHead() {
	return this.head;
    }

    public void setHead(boolean head) {
	this.head = head;
    }

    public void addChild(TreeNode tn) {
	this.children.add(tn);
    }

    public List<TreeNode> getChildren() {
	return this.children;
    }

}

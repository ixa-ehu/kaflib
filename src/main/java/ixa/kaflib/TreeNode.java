package ixa.kaflib;

import java.util.List;


public abstract class TreeNode extends IdentifiableAnnotation implements SentenceLevelAnnotation {

    /** The id of the edge between this node and its parent. */
    private String edgeId;

    /** Wether the edge between this node and its parent is the "head" or not. */
    private boolean head;

    private static final long serialVersionUID = 1L;


    public TreeNode(AnnotationContainer annotationContainer, String id, boolean head) {
        super(annotationContainer, id);
	this.head = head;
    }

    public boolean hasEdgeId() {
	return this.edgeId != null;
    }

    public String getEdgeId() {
	return this.edgeId;
    }

    public void setEdgeId(String edgeId) {
	this.edgeId = edgeId;
    }
    
    public Boolean isHead() {
	return this.head;
    }

    public void setHead(boolean head) {
	this.head = head;
    }

    public boolean isTerminal() {
	return this instanceof Terminal;
    }

    public abstract void addChild(TreeNode tn) throws Exception;
    
    public abstract void addChild(TreeNode tn, Boolean isHead) throws Exception;

    public abstract List<TreeNode> getChildren();

    
    @Deprecated
    public boolean getHead() {
	return this.isHead();
    }

    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof TreeNode)) return false;
	TreeNode ann = (TreeNode) o;
	return Utils.areEquals(this.edgeId, ann.edgeId) &&
		Utils.areEquals(this.head, ann.head) &&
		Utils.areEquals(this.isTerminal, ann.isTerminal);
    }
    */
}

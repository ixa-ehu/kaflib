package ixa.kaflib;


public abstract class TreeNode extends IdentifiableAnnotation implements SentenceLevelAnnotation {

    /** The id of the edge between this node and its parent. */
    protected String edgeId;

    /** Wether the edge between this node and its parent is the "head" or not. */
    protected boolean head;

    private static final long serialVersionUID = 1L;


    public TreeNode(AnnotationContainer annotationContainer, String id) {
        super(annotationContainer, id);
	this.head = false;
    }

    public boolean hasEdgeId() {
	return this.edgeId != null;
    }

    public String getEdgeId() {
	return this.edgeId;
    }

    public Boolean isHead() {
	return this.head;
    }

    public boolean isTerminal() {
	return this instanceof Terminal;
    }

    public boolean isNonTerminal() {
	return this instanceof NonTerminal;
    }

    
    @Deprecated
    public boolean getHead() {
	return this.isHead();
    }

    @Deprecated
    public void setHead(boolean head) {
	this.head = head;
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

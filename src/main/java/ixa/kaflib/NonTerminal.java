package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class NonTerminal extends TreeNode {

    /** Label */
    private String label;

    /** Nodes' children */
    private List<TreeNode> children;
    
    private static final long serialVersionUID = 1L;


    NonTerminal(AnnotationContainer annotationContainer, String id, String label) {
	super(annotationContainer, id);
	this.label = label;
	this.children = new ArrayList<TreeNode>();
    }

    public String getLabel() {
	return this.label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

    public void addChild(TreeNode tn) {
	String newEdgeId = this.annotationContainer.getIdManager().getNextId(AnnotationType.EDGE);
	this.addChild(tn, newEdgeId, false);
    }

    public void addChild(TreeNode tn, String edgeId) {
	this.addChild(tn, edgeId, false);
    }

    public void addChild(TreeNode tn, Boolean isHead) {
	String newEdgeId = this.annotationContainer.getIdManager().getNextId(AnnotationType.EDGE);
	this.addChild(tn, newEdgeId, isHead);
    }

    public void addChild(TreeNode tn, String edgeId, Boolean isHead) {
	this.annotationContainer.getIdManager().updateCounter(AnnotationType.EDGE, edgeId);
	tn.edgeId = edgeId;
	tn.head = isHead;
	this.children.add(tn);
	this.annotationContainer.indexAnnotationReferences(AnnotationType.NON_TERMINAL, this, tn);
    }

    public List<TreeNode> getChildren() {
	return this.children;
    }
    
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	List<Annotation> nonTerminals = new ArrayList<Annotation>();
	List<Annotation> terminals = new ArrayList<Annotation>();
	for (TreeNode child : this.getChildren()) {
	    if (child instanceof NonTerminal) nonTerminals.add(child);
	    else terminals.add(child);
	}
	if (!nonTerminals.isEmpty()) {
	    referenced.put(AnnotationType.NON_TERMINAL, nonTerminals);
	}
	if (!terminals.isEmpty()) {
	    referenced.put(AnnotationType.TERMINAL, terminals);
	}
	return referenced;
    }
    
    @Override
    public Integer getOffset() {
	if (this.children.size() == 0) return null;
	return this.children.get(0).getOffset();
    }
    
    public Integer getSent() {
	return this.children.get(0).getSent();
    }
    
    public Integer getPara() {
	return this.children.get(0).getPara();
    }
    
    @Override
    public String toString() {
	String str = "";
	Iterator<TreeNode> it = this.getChildren().iterator();
	while (it.hasNext()) {
	    str += it.next().toString();
	    if (it.hasNext()) str += " ";
	}
	return str;
    }
    
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof NonTerminal)) return false;
	NonTerminal ann = (NonTerminal) o;
	return Utils.areEquals(this.label, ann.label) &&
		Utils.areEquals(this.children, ann.children);
    }
    */
}

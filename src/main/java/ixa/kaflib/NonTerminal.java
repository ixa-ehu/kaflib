package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class NonTerminal extends TreeNode {

    /** Label */
    private String label;

    /** Nodes' children */
    private List<TreeNode> children;


    NonTerminal(String id, String label) {
	super(id, false, false);
	this.label = label;
	this.children = new ArrayList<TreeNode>();
    }

    public String getLabel() {
	return this.label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

    public void addChild(TreeNode tn) throws Exception {
	this.children.add(tn);
    }

    public List<TreeNode> getChildren() {
	return this.children;
    }
    
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	List<Annotation> terms = new ArrayList<Annotation>();
	for (TreeNode node : this.children) {
	    terms.addAll(node.getReferencedAnnotations().get(AnnotationType.TERM));
	}
	referenced.put(AnnotationType.TERM, terms);
	return referenced;
    }

}

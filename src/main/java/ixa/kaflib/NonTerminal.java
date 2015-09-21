package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class NonTerminal extends TreeNode implements SentenceLevelAnnotation {

    /** Label */
    private String label;

    /** Nodes' children */
    private List<TreeNode> children;
    
    private static final long serialVersionUID = 1L;


    NonTerminal(AnnotationContainer annotationContainer, String id, String label) {
	super(annotationContainer, id, false, false);
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

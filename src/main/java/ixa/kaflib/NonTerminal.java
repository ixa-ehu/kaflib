package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;
import org.jdom2.Element;

public class NonTerminal implements TreeNode {

    /** Reference to the main annotationContainer of the document to which this tree is related (required) */
    private AnnotationContainer annotationContainer;

    /** Nodes' children */
    private List<TreeNode> children;

    /** Label */ //?
    private String label;

    NonTerminal(AnnotationContainer annotationContainer, String label) {
	this.annotationContainer = annotationContainer;
	this.label = label;
	this.children = new ArrayList<TreeNode>();
    }

    public NonTerminal createNonTerminal(String label) {
	NonTerminal tn = new NonTerminal(this.annotationContainer, label);
	this.children.add(tn);
	return tn;
    }

    public Terminal createTerminal(Term t) {
	Terminal tn = new Terminal(this.annotationContainer, t, false);
	this.children.add(tn);
	return tn;
    }

    public Terminal createTerminal(Term t, boolean isHead) {
	Terminal tn = new Terminal(this.annotationContainer, t, isHead);
	this.children.add(tn);
	return tn;
    }

    public List<TreeNode> getChildren() {
	return this.children;
    }

    public Element getDOMElem() {
	Element elem = new Element("nt");
	elem.setAttribute("label", this.label);
	for (TreeNode tn : this.children) {
	    elem.addContent(tn.getDOMElem());
	}
	return elem;
    }

}

package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;
import org.jdom2.Element;

public class NonTerminal implements TreeNode {

    /** Nodes' children */
    private List<TreeNode> children;

    /** Label */ //?
    private String label;

    NonTerminal(String label) {
	this.label = label;
	this.children = new ArrayList<TreeNode>();
    }

    public NonTerminal newNonTerminal(String label) {
	NonTerminal tn = new NonTerminal(label);
	this.children.add(tn);
	return tn;
    }

    public Terminal newTerminal(Term t) {
	Span<Term> span = new Span<Term>();
	span.addTarget(t, false);
	Terminal tn = new Terminal(span);
	this.children.add(tn);
	return tn;
    }

    public Terminal newTerminal(Term t, boolean isHead) {
	Span<Term> span = new Span<Term>();
	span.addTarget(t, isHead);
	Terminal tn = new Terminal(span);
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

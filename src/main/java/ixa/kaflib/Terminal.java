package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;
import org.jdom2.Element;

public class Terminal implements TreeNode {

    /** The term referenced by this terminal */
    private Span<Term> span;

    Terminal(Span<Term> span) {
	this.span = span;
    }

    /** Return the first term in the span */
    public Term getTerm() {
	List<Term> terms = span.getTargets();
	if (terms.size() <= 0) {
	    return null;
	}
	return terms.get(0);
    }

    /** Returns all terms in the span */
    public List<Term> getTerms() {
	return span.getTargets();
    }

    public boolean isHead() {
	return this.span.hasHead();
    }

    public Element getDOMElem() {
	Element elem = new Element("t");
	if (this.isHead()) {
	    elem.setAttribute("head", "yes");
	}
        Element span = new Element("span");
	Element target = new Element("target");
	target.setAttribute("id", this.getTerm().getId());
	span.addContent(target);
	elem.addContent(span);
	return elem;
    }

}

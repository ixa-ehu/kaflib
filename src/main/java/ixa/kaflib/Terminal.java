package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;
import org.jdom2.Element;

public class Terminal implements TreeNode {

    /** The term referenced by this terminal */
    private Target target;

    Terminal(AnnotationContainer annotationContainer, Term t, boolean isHead) {
	this.target = new Target(annotationContainer, t.getId(), isHead);
    }

    public Term getTerm() {
	return target.getTerm();
    }

    public boolean isHead() {
	return target.isHead();
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

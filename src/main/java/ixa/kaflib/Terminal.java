package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;
import org.jdom2.Element;

public class Terminal implements TreeNode {

    /** The ID of the node */
    private String id;

    /** The term referenced by this terminal */
    private Span<Term> span;

    Terminal(String id, Span<Term> span) {
	this.id = id;
	this.span = span;
    }

    public String getId() {
	return this.id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /** Return the first term in the span */
    public Term getTerm() {
	List<Term> terms = this.span.getTargets();
	if (terms.size() <= 0) {
	    return null;
	}
	return terms.get(0);
    }

    /** Returns all terms in the span */
    public List<Term> getTerms() {
	return this.span.getTargets();
    }
    
    /** Adds a term to the span */
    public void addTerm(Term term) {
	this.span.addTarget(term);
    }

    /** Returns the Span object */
    public Span<Term> getSpan() {
	return this.span;
    }

    private String getStrValue() {
	String str = "";
	for (Term term : span.getTargets()) {
	    if (!str.isEmpty()) {
		str += " ";
	    }
	    str += term.getStr();
	}
	return str;
    }

   public String getStr() {
       String strValue = this.getStrValue();
       if (strValue.startsWith("-") || strValue.endsWith("-")) {
   		return strValue.replace("-", "- ");
   	}
   	else if (strValue.contains("--")) { 
   		return strValue.replace("--", "-");
   	}
   	else {
   		return strValue;
   	}
    }
}

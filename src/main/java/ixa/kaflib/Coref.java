package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/** The coreference layer creates clusters of term spans (which we call mentions) which share the same referent. For instance, “London” and “the capital city of England” are two mentions referring to the same entity. It is said that those mentions corefer. */
public class Coref {

    /** Coreference's ID (required) */
    private String coid;

    /** Mentions to the same entity (at least one required) */
    private List<Span<Term>> references;

    Coref(String coid, List<Span<Term>> references) {
	if (references.size() < 1) {
	    throw new IllegalStateException("Coreferences must contain at least one reference span");
	}
	if (references.get(0).size() < 1) {
	    throw new IllegalStateException("Coreferences' reference's spans must contain at least one target");
	}
	this.coid = coid;
	this.references = references;
    }

    Coref(Coref coref, HashMap<String, Term> terms) {
	this.coid = coref.coid;
	/* Copy references */
	String id = coref.getId();
	this.references = new ArrayList<Span<Term>>();
	for (Span<Term> span : coref.getReferences()) {
	    /* Copy span */
	    List<Term> targets = span.getTargets();
	    List<Term> copiedTargets = new ArrayList<Term>();
	    for (Term term : targets) {
		Term copiedTerm = terms.get(term.getId());
		if (copiedTerm == null) {
		    throw new IllegalStateException("Term not found when copying " + id);
		}
		copiedTargets.add(copiedTerm);
	    }
	    if (span.hasHead()) {
		Term copiedHead = terms.get(span.getHead().getId());
		this.references.add(new Span<Term>(copiedTargets, copiedHead));
	    }
	    else {
		this.references.add(new Span<Term>(copiedTargets));
	    }
	}
    }

    public String getId() {
	return coid;
    }

    void setId(String id) {
	this.coid = id;
    }

    /** Returns the term targets of the first span. When targets of other spans are needed getReferences() method should be used. */ 
    public List<Term> getTerms() {
	return this.references.get(0).getTargets();
    }

    /** Adds a term to the first span. */
    public void addTerm(Term term) {
	this.references.get(0).addTarget(term);
    }

    /** Adds a term to the first span. */
    public void addTerm(Term term, boolean isHead) {
	this.references.get(0).addTarget(term, isHead);
    }

    public List<Span<Term>> getReferences() {
	return this.references;
    }

    public void addReference(Span<Term> span) {
	this.references.add(span);
    }

    public String getSpanStr(Span<Term> span) {
	String str = "";
	for (Term term : span.getTargets()) {
	    if (!str.isEmpty()) {
		str += " ";
	    }
	    str += term.getStr();
	}
	return str;
    }
}

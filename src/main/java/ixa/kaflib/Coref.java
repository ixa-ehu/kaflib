package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

/** The coreference layer creates clusters of term spans (which we call mentions) which share the same referent. For instance, “London” and “the capital city of England” are two mentions referring to the same entity. It is said that those mentions corefer. */
public class Coref {

    /** Reference to the main annotationContainer of the document to which this coreference is related (required) */
    private AnnotationContainer annotationContainer;

    /** Coreference's ID (required) */
    private String coid;

    /** Mentions to the same entity (at least one required) */
    private List<Span<Term>> references;

    Coref(AnnotationContainer annotationContainer, String coid, List<Span<Term>> references) {
	if (references.size() < 1) {
	    throw new IllegalStateException("Coreferences must contain at least one reference span");
	}
	if (references.get(0).size() < 1) {
	    throw new IllegalStateException("Coreferences' reference's spans must contain at least one target");
	}
	this.annotationContainer = annotationContainer;
	this.coid = coid;
	this.references = references;
    }

    public String getId() {
	return coid;
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

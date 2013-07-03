package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

/** Class for representing features. There are two types of features: properties and categories. */
public class Feature implements Relational {

    private AnnotationContainer annotationContainer;

    /* Feature's ID (required) */
    private String id;

    /* Lemma (required) */
    private String lemma;

    private List<Span<Term>> references;

    private List<ExternalRef> externalReferences;

    Feature(AnnotationContainer annotationContainer, String id, String lemma, List<Span<Term>> references) {
	if (references.size() < 1) {
	    throw new IllegalStateException("Features must contain at least one reference span");
	}
	if (references.get(0).size() < 1) {
	    throw new IllegalStateException("Features' reference's spans must contain at least one target");
	}
	this.id = id;
	this.annotationContainer = annotationContainer;
	this.lemma = lemma;
	this.references = references;
    }

    public boolean isAProperty() {
	return this.id.matches("p.*");
    }

    public boolean isACategory() {
	return this.id.matches("c.*");
    }

    public String getId() {
	return this.id;
    }

    public String getLemma() {
	return this.lemma;
    }

    public void setLemma(String lemma) {
	this.lemma = lemma;
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
	references.add(span);
    }

    public List<ExternalRef> getExternalRefs() {
	return externalReferences;
    }

    public void addExternalRef(ExternalRef externalRef) {
	externalReferences.add(externalRef);
    }

    public void addExternalRefs(List<ExternalRef> externalRefs) {
	externalReferences.addAll(externalRefs);
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

    public String getStr() {
	return getSpanStr(this.getReferences().get(0));
    }
}

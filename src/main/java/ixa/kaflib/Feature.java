package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;


/** Class for representing features. There are two types of features: properties and categories. */
public class Feature extends IdentifiableAnnotation implements Relational, Serializable {

    /* Lemma (required) */
    private String lemma;

    private List<Span<Term>> references;

    private List<ExternalRef> externalReferences;

    Feature(String id, String lemma, List<Span<Term>> references) {
	super(id);
	if (references.size() < 1) {
	    throw new IllegalStateException("Features must contain at least one reference span");
	}
	if (references.get(0).size() < 1) {
	    throw new IllegalStateException("Features' reference's spans must contain at least one target");
	}
	this.id = id;
	this.lemma = lemma;
	this.references = references;
	this.externalReferences = new ArrayList<ExternalRef>();
    }

    Feature(Feature feature, HashMap<String, Term> terms) {
	super(feature.id);
	this.id = feature.id;
	this.lemma = feature.lemma;
	/* Copy references */
	String id = feature.getId();
	this.references = new ArrayList<Span<Term>>();
	for (Span<Term> span : feature.getSpans()) {
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
	/* Copy external references */
	this.externalReferences = new ArrayList<ExternalRef>();
	for (ExternalRef externalRef : feature.getExternalRefs()) {
	    this.externalReferences.add(new ExternalRef(externalRef));
	}
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

    void setId(String id) {
	this.id = id;
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

    public List<Span<Term>> getSpans() {
	return this.references;
    }

    public void addSpan(Span<Term> span) {
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
	return getSpanStr(this.getSpans().get(0));
    }
    
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	List<Annotation> terms = new ArrayList<Annotation>();
	for (Span<Term> span : this.getSpans()) {
	    terms.addAll((List<Annotation>)(List<?>) span.getTargets());
	}
	referenced.put(AnnotationType.TERM, terms);
	return referenced;
    }


    /** Deprecated */
    public List<List<Term>> getReferences() {
	List<List<Term>> list = new ArrayList<List<Term>>();
	for (Span<Term> span : this.references) {
	    list.add(span.getTargets());
	}
	return list;
    }

    /** Deprecated */
    public void addReference(List<Term> span) {
	this.references.add(KAFDocument.<Term>list2Span(span));
    }
    
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof Feature)) return false;
	Feature ann = (Feature) o;
	return Utils.areEquals(this.lemma, ann.lemma) &&
		Utils.areEquals(this.references, ann.references) &&
		Utils.areEquals(this.externalReferences, ann.externalReferences);
    }
    */
}

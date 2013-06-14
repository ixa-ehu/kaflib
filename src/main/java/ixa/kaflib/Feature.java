package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

/** Class for representing features. There are two types of features: properties and categories. */
public class Feature {

    private AnnotationContainer annotationContainer;

    private String id;

    private String lemma;

    private List<List<String>> references;

    private List<ExternalRef> externalReferences;

    Feature(AnnotationContainer annotationContainer, String id, String lemma, List<List<Term>> references) {
	if (references.size() < 1) {
	    throw new IllegalStateException("Features must contain at least one reference span");
	}
	if (references.get(0).size() < 1) {
	    throw new IllegalStateException("Entities' reference's spans must contain at least one target");
	}
	this.id = id;
	this.annotationContainer = annotationContainer;
	this.lemma = lemma;
	List<List<String>> newReferences = new ArrayList<List<String>>();
	for (List<Term> span : references) {
	    List<String> newSpan = new ArrayList<String>();
	    for (Term term : span) {
		newSpan.add(term.getId());
	    }
	    newReferences.add(newSpan);
	}
	this.references = newReferences;
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

    public List<List<Term>> getReferences() {
	List<List<Term>> termSpans = new ArrayList<List<Term>>();
	for (List<String> idSpan : this.references) {
	    List<Term> termSpan = new ArrayList<Term>();
	    for (String id : idSpan) {
		Term newTerm = annotationContainer.getTermById(id);
		termSpan.add(newTerm);
	    }
	    termSpans.add(termSpan);
	}
	return termSpans;
    }

    public void addReference(List<Term> span) {
	List<String> idSpan = new ArrayList<String>();
	for (Term term : span) {
	    idSpan.add(term.getId());
	}
	references.add(idSpan);
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

    public String getSpanStr(List<Term> terms) {
	String str = "";
	for (Term term : terms) {
	    if (!str.isEmpty()) {
		str += " ";
	    }
	    str += term.getStr();
	}
	return str;
    }
}

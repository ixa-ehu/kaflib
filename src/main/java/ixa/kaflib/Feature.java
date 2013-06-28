package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

/** Class for representing features. There are two types of features: properties and categories. */
public class Feature implements Relational {

    private AnnotationContainer annotationContainer;

    private String id;

    private String lemma;

    private List<Targets<Term>> references;

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
	this.references = new ArrayList<Targets<Term>>();
	for (List<Term> span : references) {
	    this.references.add(new Targets(annotationContainer, span));
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

    public String getLemma() {
	return this.lemma;
    }

    public void setLemma(String lemma) {
	this.lemma = lemma;
    }

    public List<List<Term>> getReferences() {
	List<List<Term>> spans = new ArrayList<List<Term>>();
	for (Targets<Term> span : this.references) {
	    spans.add(span.getTargets());
	}
	return spans;
    }

    public void addReference(List<Term> span) {
	references.add(new Targets(annotationContainer, span));
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

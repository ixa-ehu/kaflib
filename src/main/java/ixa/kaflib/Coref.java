package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


/** The coreference layer creates clusters of term spans (which we call mentions) which share the same referent. For instance, “London” and “the capital city of England” are two mentions referring to the same entity. It is said that those mentions corefer. */
public class Coref extends IdentifiableAnnotation {

    /** (optional) */
    private String type;

    /** Mentions to the same entity (at least one required) */
    private List<Span<Term>> mentions;

    /** External references (optional) */
    private List<ExternalRef> externalReferences;
    
    private static final long serialVersionUID = 1L;


    Coref(AnnotationContainer annotationContainer, String id, List<Span<Term>> mentions) {
	super(annotationContainer, id);
	if (mentions.size() < 1) {
	    throw new IllegalStateException("Coreferences must contain at least one reference span");
	}
	if (mentions.get(0).size() < 1) {
	    throw new IllegalStateException("Coreferences' reference's spans must contain at least one target");
	}
	this.mentions = mentions;
	this.externalReferences = new ArrayList<ExternalRef>();
    }

    public boolean hasType() {
	return this.type != null;
    }

    public String getType() {
	return this.type;
    }

    public void setType(String type) {
	this.type = type;
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

    /** Returns the term targets of the first span. When targets of other spans are needed getReferences() method should be used. */ 
    public List<Term> getTerms() {
	return this.mentions.get(0).getTargets();
    }

    /** Adds a term to the first span. */
    public void addTerm(Term term) {
	this.mentions.get(0).addTarget(term);
    }

    /** Adds a term to the first span. */
    public void addTerm(Term term, boolean isHead) {
	this.mentions.get(0).addTarget(term, isHead);
    }

    public List<Span<Term>> getSpans() {
	return this.mentions;
    }

    public void addSpan(Span<Term> span) {
	this.mentions.add(span);
    }
    
    @Override
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	List<Annotation> terms = new ArrayList<Annotation>();
	for (Span<Term> span : this.getSpans()) {
	    terms.addAll((List<Annotation>)(List<?>) span.getTargets());
	}
	referenced.put(AnnotationType.TERM, terms);
	return referenced;
    }    
    
    @Override
    public Integer getOffset() {
	if (this.mentions.size() == 0) return null;
	return this.mentions.get(0).getOffset();
    }
    
    @Override
    public String toString() {
	return null;
    }
    
    
    @Deprecated
    public List<List<Target>> getReferences() {
	List<List<Target>> list = new ArrayList<List<Target>>();
	for (Span<Term> span : this.mentions) {
	    list.add(KAFDocument.span2TargetList(span));
	}
	return list;
    }

    @Deprecated
    public void addReference(List<Target> span) {
	this.mentions.add(KAFDocument.targetList2Span(span));
    }
    
    @Deprecated
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
    
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof Coref)) return false;
	Coref ann = (Coref) o;
	return Utils.areEquals(this.type, ann.type) &&
		Utils.areEquals(this.mentions, ann.mentions) &&
		Utils.areEquals(this.externalReferences, ann.externalReferences);
    }
    */
}

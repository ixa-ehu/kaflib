package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/** A named entity is a term (or a multiword) that clearly identifies one item. The optional Named Entity layer is used to reference terms that are named entities. */
public class Entity extends IdentifiableAnnotation implements Relational, SentenceLevelAnnotation {

    /** Type of the named entity (optional). Currently, 8 values are possible: 
     * - Person
     * - Organization
     * - Location
     * - Date
     * - Time
     * - Money
     * - Percent
     * - Misc
     */ 
    private String type;
    
    private String source;

    /** Reference to different occurrences of the same named entity in the document (at least one required) */
    private List<Span<Term>> references;

    /** External references (optional) */
    private List<ExternalRef> externalReferences;
    
    private static final long serialVersionUID = 1L;

    
    Entity(AnnotationContainer annotationContainer, String id, List<Span<Term>> references) {
	super(annotationContainer, id);
	if (references.size() < 1) {
	    throw new IllegalStateException("Entities must contain at least one reference span");
	}
	if (references.get(0).size() < 1) {
	    throw new IllegalStateException("Entities' reference's spans must contain at least one target");
	}
	this.references = references;
	this.externalReferences = new ArrayList<ExternalRef>();
    }

    public boolean hasType() {
	return type != null;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public boolean hasSource() {
	return source != null;
    }

    public String getSource() {
	return source;
    }

    public void setSource(String source) {
	this.source = source;
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
	this.references.add(span);
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
    
    public Integer getSent() {
	return this.getSpans().get(0).getFirstTarget().getSent();
    }
    
    public Integer getPara() {
	return this.getSpans().get(0).getFirstTarget().getPara();
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
    
    @Override
    public Integer getOffset() {
	if (this.getSpans().size() == 0) return null;
	return this.getSpans().get(0).getOffset();
    }
    
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof Entity)) return false;
	Entity ann = (Entity) o;
	return Utils.areEquals(this.type, ann.type) &&
		Utils.areEquals(this.source, ann.source) &&
		Utils.areEquals(this.references, ann.references) &&
		Utils.areEquals(this.externalReferences, ann.externalReferences);
    }
    */

}

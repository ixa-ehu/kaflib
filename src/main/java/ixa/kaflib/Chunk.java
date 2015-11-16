package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.List;
import java.util.HashMap;
import java.util.Map;


/** Chunks are noun, verb or prepositional phrases, spanning terms. */
public class Chunk extends IdentifiableAnnotation implements SentenceLevelAnnotation {

    /** Type of the phrase (optional) */
    private String phrase;

    /** Declension case (optional) */
    private String chunkcase;

    /** Chunk's target terms (at least one required) */
    private Span<Term> span;

    private static final long serialVersionUID = 1L;

    
    Chunk(AnnotationContainer annotationContainer, String id, Span<Term> span) {
	super(annotationContainer, id);
	if (span.size() < 1) {
	    throw new IllegalStateException("Chunks must contain at least one term target");
	}
	this.span = span;
    }

    public boolean hasHead() {
	return (span.getHead() != null);
    }

    public Term getHead() {
	return span.getHead();
    }

    public boolean hasPhrase() {
	return phrase != null;
    }

    public String getPhrase() {
	return phrase;
    }

    public void setPhrase(String phrase) {
	this.phrase = phrase;
    }

    public boolean hasCase() {
	return chunkcase != null;
    }

    public String getCase() {
	return chunkcase;
    }

    public void setCase(String chunkcase) {
	this.chunkcase = chunkcase;
    }

    public List<Term> getTerms() {
	return this.span.getTargets();
    }

    public void addTerm(Term term) {
	this.span.addTarget(term);
    }

    public void addTerm(Term term, boolean isHead) {
	this.span.addTarget(term, isHead);
    }

    public Span<Term> getSpan() {
	return this.span;
    }

    public void setSpan(Span<Term> span) {
	this.span = span;
    }

    @Override
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.TERM, (List<Annotation>)(List<?>) this.getSpan().getTargets());
	return referenced;
    }
    
    @Override
    public Integer getOffset() {
	return this.span.getOffset();
    }
    
    @Override
    public Integer getSent() {
	return this.span.getFirstTarget().getSent();
    }
    
    @Override
    public Integer getPara() {
	return this.span.getFirstTarget().getPara();
    }
    
    @Override
    public String toString() {
	return this.span.toString();
    }

    
    @Deprecated
    public void setHead(Term term) {
        this.span.setHead(term);
    }
    
    @Deprecated
    public String getStr() {
	String str = "";
	for (Term term : this.span.getTargets()) {
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
	if (!(o instanceof Chunk)) return false;
	Chunk ann = (Chunk) o;
	return Utils.areEquals(this.phrase, ann.phrase) &&
		Utils.areEquals(this.chunkcase, ann.chunkcase) &&
		Utils.areEquals(this.span, ann.span);
    }
    */	
}

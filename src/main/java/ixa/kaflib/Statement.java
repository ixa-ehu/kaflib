package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Statement extends IdentifiableAnnotation {

    private StatementTarget target;
    private StatementSource source;
    private StatementCue cue;
    
    private static final long serialVersionUID = 1L;
    
    
    public Statement(AnnotationContainer annotationContainer, String id, StatementTarget target) {
	super(annotationContainer, id);
	this.target = target;
    }

    public StatementTarget getTarget() {
	return this.target;
    }
    
    public void setTarget(StatementTarget target) {
	this.target = target;
    }
    
    public Boolean hasSource() {
	return this.source != null;
    }
    
    public StatementSource getSource() {
	return this.source;
    }
    
    public void setSource(StatementSource source) {
	this.source = source;
    }
    
    public Boolean hasCue() {
	return this.cue != null;
    }
    
    public StatementCue getCue() {
	return this.cue;
    }
    
    public void setCue(StatementCue cue) {
	this.cue = cue;
    }
    
    public Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> refs = new HashMap<AnnotationType, List<Annotation>>();
	List<Annotation> terms = new ArrayList<Annotation>();
	for (Term t : this.target.getSpan().getTargets()) {
	    terms.add(t);
	}
	if (this.hasSource()) {
	    for (Term t : this.source.getSpan().getTargets()) {
		terms.add(t);
	    }
	}
	if (this.hasCue()) {
	    for (Term t : this.cue.getSpan().getTargets()) {
		terms.add(t);
	    }
	}
	refs.put(AnnotationType.TERM, terms);
	return refs;
    }
    
    @Override
    public Integer getOffset() {
	return Math.min(Math.min(this.getTarget().getOffset(), this.getSource().getOffset()), this.getCue().getOffset());
    }
    
    @Override
    public String toString() {
	return this.target.toString();
    }
    
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof Statement)) return false;
	Statement ann = (Statement) o;
	return Utils.areEquals(this.target, ann.target) &&
		Utils.areEquals(this.source, ann.source) && 
		Utils.areEquals(this.cue, ann.cue);
    }
    */
    
    
    public static class StatementTarget extends Annotation {

	private Span<Term> span;
	
	private static final long serialVersionUID = 1L;
	
	
	public StatementTarget(AnnotationContainer annotationContainer, Span<Term> span) {
	    super(annotationContainer);
	    this.span = span;
	}
	
	public Span<Term> getSpan() {
	    return this.span;
	}
	
	public void setSpan(Span<Term> span) {
	    this.span = span;
	}
	
	public Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	    Map<AnnotationType, List<Annotation>> refs = new HashMap<AnnotationType, List<Annotation>>();
	    List<Annotation> terms = new ArrayList<Annotation>();
	    for (Term t : this.getSpan().getTargets()) {
		terms.add(t);
	    }
	    refs.put(AnnotationType.TERM, terms);
	    return refs;
	}

	@Override
	public Integer getOffset() {
	    return this.getSpan().getOffset();
	}
	
	@Override
	public String toString() {
	    return this.span.toString();
	}

	/*
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof StatementTarget)) return false;
	    StatementTarget ann = (StatementTarget) o;
	    return Utils.areEquals(this.span, ann.span);
	}
	*/
    }
    
    public static class StatementSource extends Annotation {

	private Span<Term> span;
	
	private static final long serialVersionUID = 1L;
	
	
	public StatementSource(AnnotationContainer annotationContainer, Span<Term> span) {
	    super(annotationContainer);
	    this.span = span;
	}
	
	public Span<Term> getSpan() {
	    return this.span;
	}
	
	public void setSpan(Span<Term> span) {
	    this.span = span;
	}
	
	public Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	    Map<AnnotationType, List<Annotation>> refs = new HashMap<AnnotationType, List<Annotation>>();
	    List<Annotation> terms = new ArrayList<Annotation>();
	    for (Term t : this.getSpan().getTargets()) {
		terms.add(t);
	    }
	    refs.put(AnnotationType.TERM, terms);
	    return refs;
	}

	@Override
	public Integer getOffset() {
	    return this.getSpan().getOffset();
	}
	
	@Override
	public String toString() {
	    return this.span.toString();
	}

	/*
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof StatementSource)) return false;
	    StatementSource ann = (StatementSource) o;
	    return Utils.areEquals(this.span, ann.span);
	}
	*/
    }
    
    public static class StatementCue extends Annotation {

	private Span<Term> span;
	
	private static final long serialVersionUID = 1L;
	
	
	public StatementCue(AnnotationContainer annotationContainer, Span<Term> span) {
	    super(annotationContainer);
	    this.span = span;
	}
	
	public Span<Term> getSpan() {
	    return this.span;
	}
	
	public void setSpan(Span<Term> span) {
	    this.span = span;
	}
	
	public Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	    Map<AnnotationType, List<Annotation>> refs = new HashMap<AnnotationType, List<Annotation>>();
	    List<Annotation> terms = new ArrayList<Annotation>();
	    for (Term t : this.getSpan().getTargets()) {
		terms.add(t);
	    }
	    refs.put(AnnotationType.TERM, terms);
	    return refs;
	}

	@Override
	public Integer getOffset() {
	    return this.getSpan().getOffset();
	}
	
	@Override
	public String toString() {
	    return this.span.toString();
	}

	/*
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof StatementCue)) return false;
	    StatementCue ann = (StatementCue) o;
	    return Utils.areEquals(this.span, ann.span);
	}
	*/
    }
}

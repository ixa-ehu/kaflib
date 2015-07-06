package ixa.kaflib;

import ixa.kaflib.KAFDocument.Layer;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Statement extends IdentifiableAnnotation {
    
    private StatementTarget target;
    private StatementSource source;
    private StatementCue cue;
    
    
    public Statement(String id, StatementTarget target) {
	super(id);
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
    
    public Map<Layer, List<Annotation>> getReferencedAnnotations() {
	Map<Layer, List<Annotation>> refs = new HashMap<Layer, List<Annotation>>();
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
	refs.put(Layer.TERMS, terms);
	return refs;
    }
    
    
    public static class StatementTarget extends Annotation {
	private Span<Term> span;
	
	public StatementTarget(Span<Term> span) {
	    this.span = span;
	}
	
	public Span<Term> getSpan() {
	    return this.span;
	}
	
	public void setSpan(Span<Term> span) {
	    this.span = span;
	}
	
	public Map<Layer, List<Annotation>> getReferencedAnnotations() {
	    Map<Layer, List<Annotation>> refs = new HashMap<Layer, List<Annotation>>();
	    List<Annotation> terms = new ArrayList<Annotation>();
	    for (Term t : this.getSpan().getTargets()) {
		terms.add(t);
	    }
	    return refs;
	}
    }
    
    public static class StatementSource extends Annotation {
	private Span<Term> span;
	
	public StatementSource(Span<Term> span) {
	    this.span = span;
	}
	
	public Span<Term> getSpan() {
	    return this.span;
	}
	
	public void setSpan(Span<Term> span) {
	    this.span = span;
	}
	
	public Map<Layer, List<Annotation>> getReferencedAnnotations() {
	    Map<Layer, List<Annotation>> refs = new HashMap<Layer, List<Annotation>>();
	    List<Annotation> terms = new ArrayList<Annotation>();
	    for (Term t : this.getSpan().getTargets()) {
		terms.add(t);
	    }
	    return refs;
	}
    }
    
    public static class StatementCue extends Annotation {
	private Span<Term> span;
	
	public StatementCue(Span<Term> span) {
	    this.span = span;
	}
	
	public Span<Term> getSpan() {
	    return this.span;
	}
	
	public void setSpan(Span<Term> span) {
	    this.span = span;
	}
	
	public Map<Layer, List<Annotation>> getReferencedAnnotations() {
	    Map<Layer, List<Annotation>> refs = new HashMap<Layer, List<Annotation>>();
	    List<Annotation> terms = new ArrayList<Annotation>();
	    for (Term t : this.getSpan().getTargets()) {
		terms.add(t);
	    }
	    return refs;
	}
    }
}

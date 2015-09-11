package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Utils;
import ixa.kaflib.Term.Sentiment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredicateAnchor extends IdentifiableAnnotation implements SentenceLevelAnnotation {
    
    private Timex3 anchorTime;
    private Timex3 beginPoint;
    private Timex3 endPoint;
    private Span<Predicate> span;

    
    public PredicateAnchor(String id, Span<Predicate> span) {
	super(id);
	this.span = span;
    }
    
    public boolean hasAnchorTime() {
	return this.anchorTime != null;
    }
    
    public Timex3 getAnchorTime() {
	return this.anchorTime;
    }
    
    public void setAnchorTime(Timex3 anchorTime) {
	this.anchorTime = anchorTime;
    }
    
    public boolean hasBeginPoint() {
	return this.beginPoint != null;
    }
    
    public Timex3 getBeginPoint() {
	return this.beginPoint;
    }
    
    public void setBeginPoint(Timex3 beginPoint) {
	this.beginPoint = beginPoint;
    }
    
    public boolean hasEndPoint() {
	return this.endPoint != null;
    }
    
    public Timex3 getEndPoint() {
	return this.endPoint;
    }
    
    public void setEndPoint(Timex3 endPoint) {
	this.endPoint = endPoint;
    }
    
    public Span<Predicate> getSpan() {
	return this.span;
    }
    
    public void setSpan(Span<Predicate> span) {
	this.span = span;
    }
    
    public Integer getSent() {
	return this.span.getFirstTarget().getSent();
    }
    
    public Integer getPara() {
	return this.span.getFirstTarget().getPara();
    }
    
    @Override
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> refs = new HashMap<AnnotationType, List<Annotation>>();
	if (this.span != null) {
	    refs.put(AnnotationType.PREDICATE, (List<Annotation>)(List<?>)this.span.getTargets());
	}
	return refs;
    }
    
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof PredicateAnchor)) return false;
	PredicateAnchor ann = (PredicateAnchor) o;
	return Utils.areEquals(this.anchorTime, ann.anchorTime) &&
		Utils.areEquals(this.beginPoint, ann.beginPoint) &&
		Utils.areEquals(this.endPoint, ann.endPoint) &&
		Utils.areEquals(this.span, ann.span);
    }
    */

}

package ixa.kaflib;

import ixa.kaflib.KAFDocument.Layer;
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

    
    public PredicateAnchor(String id, Timex3 anchorTime, Timex3 beginPoint, Timex3 endPoint, Span<Predicate> span) {
	super(id);
	this.anchorTime = anchorTime;
	this.beginPoint = beginPoint;
	this.endPoint = endPoint;
	this.span = span;
    }
    
    public Timex3 getAnchorTime() {
	return this.anchorTime;
    }
    
    public void setAnchorTime(Timex3 anchorTime) {
	this.anchorTime = anchorTime;
    }
    
    public Timex3 getBeginPoint() {
	return this.beginPoint;
    }
    
    public void setBeginPoint(Timex3 beginPoint) {
	this.beginPoint = beginPoint;
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
    Map<Layer, List<Annotation>> getReferencedAnnotations() {
	Map<Layer, List<Annotation>> refs = new HashMap<KAFDocument.Layer, List<Annotation>>();
	if (this.span != null) {
	    refs.put(Layer.SRL, (List<Annotation>)(List<?>)this.span.getTargets());
	}
	return refs;
    }
    
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

}

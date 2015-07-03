package ixa.kaflib;

import ixa.kaflib.KAFDocument.Layer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredicateAnchor extends IdentifiableAnnotation {
    
    private String anchorTime;
    private String beginPoint;
    private String endPoint;
    private Span<Predicate> span;

    
    public PredicateAnchor(String id, String anchorTime, String beginPoint, String endPoint, Span<Predicate> span) {
	super(id);
	this.anchorTime = anchorTime;
	this.beginPoint = beginPoint;
	this.endPoint = endPoint;
	this.span = span;
    }
    
    public String getAnchorTime() {
	return this.anchorTime;
    }
    
    public void setAnchorTime(String anchorTime) {
	this.anchorTime = anchorTime;
    }
    
    public String getBeginPoint() {
	return this.beginPoint;
    }
    
    public void setBeginPoint(String beginPoint) {
	this.beginPoint = beginPoint;
    }
    
    public String getEndPoint() {
	return this.endPoint;
    }
    
    public void setEndPoint(String endPoint) {
	this.endPoint = endPoint;
    }
    
    public Span<Predicate> getSpan() {
	return this.span;
    }
    
    public void setSpan(Span<Predicate> span) {
	this.span = span;
    }
    
    @Override
    Map<Layer, List<Annotation>> getReferencedAnnotations() {
	Map<Layer, List<Annotation>> refs = new HashMap<KAFDocument.Layer, List<Annotation>>();
	if (this.span != null) {
	    refs.put(Layer.SRL, (List<Annotation>)(List<?>)this.span.getTargets());
	}
	return refs;
    }

}

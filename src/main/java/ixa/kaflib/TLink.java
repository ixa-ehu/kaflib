package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class TLink extends IdentifiableAnnotation {

    private TLinkReferable from;

    private TLinkReferable to;

    private String relType;
    
    private static final long serialVersionUID = 1L;

    
    TLink(AnnotationContainer annotationContainer, String id, TLinkReferable from, TLinkReferable to, String relType) {
        super(annotationContainer, id);
	this.from = from;
	this.to = to;
	this.relType = relType;
    }

    public TLinkReferable getFrom() {
	return this.from;
    }

    public void setFrom(TLinkReferable from) {
	this.from = from;
    }

    public TLinkReferable getTo() {
	return this.to;
    }

    public void setTo(TLinkReferable to) {
	this.to = to;
    }

    public String getFromType() {
	return (this.from instanceof Predicate) ? "event" : "timex";
    }

    public String getToType() {
	return (this.to instanceof Predicate) ? "event" : "timex";
    }

    public String getRelType() {
	return this.relType;
    }

    public void setRelType(String relType) {
	this.relType = relType;
    }
    
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	List<Annotation> predicates = new ArrayList<Annotation>();
	List<Annotation> timexes = new ArrayList<Annotation>();
	if (this.from instanceof Predicate) predicates.add((Annotation)this.from);
	else timexes.add((Annotation)this.from);
	if (this.to instanceof Predicate) predicates.add((Annotation)this.to);
	else timexes.add((Annotation)this.to);
	referenced.put(AnnotationType.PREDICATE, predicates);
	referenced.put(AnnotationType.TIMEX3, timexes);
	return referenced;
    }
    
    @Override
    public Integer getOffset() {
	return Math.min(this.getFrom().getOffset(), this.getTo().getOffset());
    }
    
    @Override
    public String toString() {
	return this.relType + "(" + this.from + ", " + this.to + ")";
    }
    
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof TLink)) return false;
	TLink ann = (TLink) o;
	return Utils.areEquals(this.from, ann.from) &&
		Utils.areEquals(this.to,  ann.to) &&
		Utils.areEquals(this.relType, ann.relType);
    }
    */
}


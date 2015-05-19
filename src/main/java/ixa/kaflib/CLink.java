package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class CLink extends IdentifiableAnnotation {

    private Predicate from;

    private Predicate to;

    private String relType;

    
    CLink(String id, Predicate from, Predicate to) {
	super(id);
	this.from = from;
	this.to = to;
    }

    public Predicate getFrom() {
	return this.from;
    }

    public void setFrom(Predicate from) {
	this.from = from;
    }

    public Predicate getTo() {
	return this.to;
    }

    public void setTo(Predicate to) {
	this.to = to;
    }

    public boolean hasRelType() {
	return this.relType != null;
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
	predicates.add(this.getFrom());
	predicates.add(this.getTo());
	referenced.put(AnnotationType.PREDICATE, predicates);
	return referenced;
    }
}

package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

/** Class for representing relations between entities and/or features. */
public class Relation extends IdentifiableAnnotation implements Serializable {

    /* From (required) */
    private Relational from;

    /* To (required) */
    private Relational to;

    /* Confidence (optional) */
    private float confidence;

    Relation(String id, Relational from, Relational to) {
	super(id);
	this.id = id;
	this.from = from;
	this.to = to;
	this.confidence = -1.0f;
    }

    Relation(Relation relation, HashMap<String, Relational> relational) {
	super(relation.getId());
	this.id = relation.getId();
	if (relation.from != null) {
	    this.from = relational.get(relation.from.getId());
	    if (this.from == null) {
		throw new IllegalStateException("Couldn't find relational " + relation.from.getId() + " when copying " + relation.getId());
	    }
	}
	if (relation.to != null) {
	    this.to = relational.get(relation.to.getId());
	    if (this.to == null) {
		throw new IllegalStateException("Couldn't find relational " + relation.to.getId() + " when copying " + relation.getId());
	    }
	}
	this.confidence = relation.confidence;
    }

    public String getId() {
	return this.id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public Relational getFrom() {
        return this.from;
    }

    public void setFrom(Relational obj) {
	this.from = obj;
    }

    public Relational getTo() {
        return this.to;
    }

    public void setTo(Relational obj) {
	this.to = obj;
    }

    public boolean hasConfidence() {
	return confidence >= 0;
    }

    public float getConfidence() {
	if (confidence < 0) {
	    return 1.0f;
	}
	return confidence;
    }

    public void setConfidence(float confidence) {
	if ((confidence < 0.0f) || (confidence > 1.0f)) {
	    throw new IllegalStateException("Confidence's value in a relation must be >=0 and <=1. [0, 1].");
	}
	this.confidence = confidence;
    }

    public String getStr() {
	String str = "(" + this.from.getStr() + ", " + this.to.getStr() + ")";
	if (this.hasConfidence()) {
	    str += " [" + this.getConfidence() + "]";
	}
	return str;
    }
    
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	List<Annotation> entities = new ArrayList<Annotation>();
	List<Annotation> properties = new ArrayList<Annotation>();
	List<Annotation> categories = new ArrayList<Annotation>();
	if (this.from instanceof Entity) entities.add((Annotation)this.from);
	else if (((Feature)this.from).isAProperty()) properties.add((Annotation)this.from);
	else categories.add((Annotation)this.from);
	if (this.to instanceof Entity) entities.add((Annotation)this.to);
	else if (((Feature)this.to).isAProperty()) properties.add((Annotation)this.to);
	else categories.add((Annotation)this.to);
	referenced.put(AnnotationType.ENTITY, (List<Annotation>)(List<?>) entities);
	referenced.put(AnnotationType.PROPERTY, (List<Annotation>)(List<?>) properties);
	referenced.put(AnnotationType.CATEGORY, (List<Annotation>)(List<?>) categories);
	return referenced;
    }
    
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof Relation)) return false;
	Relation ann = (Relation) o;
	return Utils.areEquals(this.from, ann.from) &&
		Utils.areEquals(this.to,  ann.to) &&
		Utils.areEquals(this.confidence, ann.confidence);
    }
    */
}

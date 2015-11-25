package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

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
    
    private static final long serialVersionUID = 1L;

    
    Relation(AnnotationContainer annotationContainer, String id, Relational from, Relational to) {
	super(annotationContainer, id);
	this.id = id;
	this.from = from;
	this.to = to;
	this.confidence = -1.0f;
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
    
    public AnnotationType getFromType() {
	if (this.from instanceof Entity) return AnnotationType.ENTITY;
	else if (((Feature)this.from).isAProperty()) return AnnotationType.PROPERTY;
	else return AnnotationType.CATEGORY;
    }

    public void setFrom(Relational obj) {
	this.annotationContainer.unindexAnnotationReferences(AnnotationType.RELATION, this, (Annotation)this.from);
	this.from = obj;
	this.annotationContainer.indexAnnotationReferences(AnnotationType.RELATION, this, (Annotation)this.from);
    }

    public Relational getTo() {
        return this.to;
    }
    
    public AnnotationType getToType() {
	if (this.to instanceof Entity) return AnnotationType.ENTITY;
	else if (((Feature)this.to).isAProperty()) return AnnotationType.PROPERTY;
	else return AnnotationType.CATEGORY;
    }

    public void setTo(Relational obj) {
	this.annotationContainer.unindexAnnotationReferences(AnnotationType.RELATION, this, (Annotation)this.to);
	this.to = obj;
	this.annotationContainer.indexAnnotationReferences(AnnotationType.RELATION, this, (Annotation)this.to);
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

    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	List<Annotation> entities = new ArrayList<Annotation>();
	List<Annotation> properties = new ArrayList<Annotation>();
	List<Annotation> categories = new ArrayList<Annotation>();
	AnnotationType fromType = this.getFromType();
	AnnotationType toType = this.getToType();
	if (fromType == AnnotationType.ENTITY) entities.add((Annotation)this.from);
	else if (fromType == AnnotationType.PROPERTY) properties.add((Annotation)this.from);
	else categories.add((Annotation)this.from);
	if (toType == AnnotationType.ENTITY) entities.add((Annotation)this.to);
	else if (toType == AnnotationType.PROPERTY) properties.add((Annotation)this.to);
	else categories.add((Annotation)this.to);
	referenced.put(AnnotationType.ENTITY, (List<Annotation>)(List<?>) entities);
	referenced.put(AnnotationType.PROPERTY, (List<Annotation>)(List<?>) properties);
	referenced.put(AnnotationType.CATEGORY, (List<Annotation>)(List<?>) categories);
	return referenced;
    }
    
    @Override
    public Integer getOffset() {
	return Math.min(this.getFrom().getOffset(), this.getTo().getOffset());
    }
    
    @Override
    public String toString() {
	return "(" + this.from + ", " + this.to + ")";
    }
    
    
    @Deprecated
    public String getStr() {
	String str = "(" + this.from.getStr() + ", " + this.to.getStr() + ")";
	if (this.hasConfidence()) {
	    str += " [" + this.getConfidence() + "]";
	}
	return str;
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

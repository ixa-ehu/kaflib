package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

/** Class for representing relations between entities and/or features. */
public class Relation {
    
    private AnnotationContainer annotationContainer;
    private String id;
    private Relational from;
    private Relational to;
    private float confidence;

    Relation (AnnotationContainer annotationContainer, String id, Relational from, Relational to) {
	this.annotationContainer = annotationContainer;
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
	return "(" + this.from.getStr() + ", " + this.to.getStr() + ")";
    }
}

package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

/** Class for representing relations between entities and/or features. */
public class Relation {
    
    private AnnotationContainer annotationContainer;
    private String id;
    private String fromId;
    private String toId;
    private float confidence;

    Relation (AnnotationContainer annotationContainer, String id, Relational from, Relational to) {
	this.annotationContainer = annotationContainer;
	this.id = id;
	this.fromId = from.getId();
	this.toId = to.getId();
	this.confidence = -1.0f;
    }

    public String getId() {
	return this.id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public Relational getFrom() {
	if (fromId.matches("e.*")) {
	    return annotationContainer.getEntityById(fromId);
	}
	else if (fromId.matches("p.*")) {
	    return annotationContainer.getPropertyById(fromId);
	}
	else {
	    return annotationContainer.getCategoryById(fromId);
	}
    }

    public void setFrom(Relational obj) {
	this.fromId = obj.getId();
    }

    public Relational getTo() {
	if (toId.matches("e.*")) {
	    return annotationContainer.getEntityById(toId);
	}
	else if (toId.matches("p.*")) {
	    return annotationContainer.getPropertyById(toId);
	}
	else {
	    return annotationContainer.getCategoryById(toId);
	}
    }

    public void setTo(Relational obj) {
	this.toId = obj.getId();
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
}

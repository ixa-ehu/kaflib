package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

/** The coreference layer creates clusters of term spans (which we call mentions) which share the same referent. For instance, “London” and “the capital city of England” are two mentions referring to the same entity. It is said that those mentions corefer. */
public class Coref {

    /** Reference to the main annotationContainer of the document to which this coreference is related (required) */
    private AnnotationContainer annotationContainer;

    /** Coreference's ID (required) */
    private String coid;

    /** Mentions to the same entity (at least one required) */
    private List<List<Target>> references;

    Coref(AnnotationContainer annotationContainer, String coid, List<List<Target>> references) {
	if (references.size() < 1) {
	    throw new IllegalStateException("Coreferences must contain at least one reference span");
	}
	if (references.get(0).size() < 1) {
	    throw new IllegalStateException("Coreferences' reference's spans must contain at least one target");
	}
	this.annotationContainer = annotationContainer;
	this.coid = coid;
	this.references = references;
    }

    public String getId() {
	return coid;
    }

    public List<List<Target>> getReferences() {
	return references;
    }

    public void addReference(List<Target> span) {
	references.add(span);
    }
}
package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Linked Entity in the text.
 */
public class LinkedEntity extends IdentifiableAnnotation {

    /**
     * LinedEntity's properties
     */
    private String resource;
    private String reference;
    private double confidence;

    /**
     * Mentions to the same entity (at least one required)
     */
    private Span<WF> mentions;

    private static final long serialVersionUID = 1L;

    LinkedEntity(AnnotationContainer annotationContainer, String id, Span<WF> mentions) {
	super(annotationContainer, id);
	this.mentions = mentions;
    }

    public String getResource() {
	return resource;
    }

    public void setResource(String resource) {
	this.resource = resource;
    }

    public String getReference() {
	return reference;
    }

    public void setReference(String reference) {
	this.reference = reference;
    }

    public double getConfidence() {
	return confidence;
    }

    public void setConfidence(double confidence) {
	this.confidence = confidence;
    }

    public Span<WF> getSpan() {
	return this.mentions;
    }

    public String getSpanStr() {
	String str = "";
	for (WF wf : mentions.getTargets()) {
	    if (!str.isEmpty()) {
		str += " ";
	    }
	    str += wf.getForm();
	}
	return str;
    }

    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.WF, (List<Annotation>) (List<?>) this.getSpan().getTargets());
	return referenced;
    }
    
    @Override
    public Integer getOffset() {
	return this.getSpan().getOffset();
    }
    
    /**
     * Returns the term targets of the first span. When targets of other spans
     * are needed getReferences() method should be used.
     */
    public Span<WF> getWFs() {
	return mentions;
    }

    /*
     * @Override public boolean equals(Object o) { if (this == o) return true;
     * if (!(o instanceof LinkedEntity)) return false; LinkedEntity ann =
     * (LinkedEntity) o; return Utils.areEquals(this.resource, ann.resource) &&
     * Utils.areEquals(this.reference, ann.reference) &&
     * Utils.areEquals(this.confidence, ann.confidence) &&
     * Utils.areEquals(this.mentions, ann.mentions); }
     */

}

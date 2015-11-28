package ixa.kaflib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ixa.kaflib.KAFDocument.AnnotationType;

/** Class for representing terms. Terms refer to previous word forms (and groups multi-words) and attach lemma, part of speech, synset and name entity information. */
public class Term extends TermBase {

    /** If it's a compound term, it's components (optional) */
    private Compound compound; // Parent compound term of this component

    private static final long serialVersionUID = 1L;
    
    Term(AnnotationContainer annotationContainer, String id, Span<WF> span) {
	super(annotationContainer, id, span);
	this.compound = null;
    }
    

    public Compound getCompound() {
	return this.compound;
    }
    

    boolean isComponent() {
	return this.compound != null;
    }

    void setCompound(Compound compound) {
	this.compound = compound;
    }
    
    @Override
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.WF, (List<Annotation>)(List<?>) this.getSpan().getTargets());
	List<Annotation> sentiments = new ArrayList<Annotation>();
	if (this.hasSentiment()) {
	    sentiments.add(this.getSentiment());
	    referenced.put(AnnotationType.SENTIMENT, sentiments);
	}
	return referenced;
    }

}

package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Factuality layer
 */
public class Factvalue extends Annotation implements SentenceLevelAnnotation {

    private WF word;
    private String prediction;
    private Double confidence;
    
    private static final long serialVersionUID = 1L;
    

    Factvalue(AnnotationContainer annotationContainer, WF word, String prediction) {
	super(annotationContainer);
	this.word = word;
	this.prediction = prediction;
    }

    public String getId() {
	return this.word.getId();
    } 

    public WF getWF() {
	return word;
    }

    public String getPrediction() {
	return this.prediction;
    }

    public void setPrediction(String prediction) {
	this.prediction = prediction;
    }

    public boolean hasConfidence() {
	return this.confidence != null;
    }

    public void setConfidence(Double confidence) {
	this.confidence = confidence;
    }

    public Double getConfidence() {
	return this.confidence;
    }
    
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	List<Annotation> wfs = new ArrayList<Annotation>();
	wfs.add(this.getWF());
	referenced.put(AnnotationType.WF, wfs);
	return referenced;
    }
    
    @Override
    public Integer getOffset() {
	return this.getWF().getOffset();
    }
    
    @Override
    public Integer getSent() {
	return this.word.getSent();
    }
    
    @Override
    public Integer getPara() {
	return this.word.getPara();
    }
    
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof Factvalue)) return false;
	Factvalue ann = (Factvalue) o;
	return Utils.areEquals(this.word, ann.word) &&
		Utils.areEquals(this.prediction, ann.prediction) &&
		Utils.areEquals(this.confidence, ann.confidence);
    }
    */
    
}

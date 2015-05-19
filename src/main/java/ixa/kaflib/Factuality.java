package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Factuality layer
 */
public class Factuality extends Annotation {

    private WF word;
    private String prediction;
    private Double confidence;
    //ArrayList<FactualityPart> factualityParts = new ArrayList<FactualityPart>();

    public Factuality(WF word, String prediction) {
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
    
    
    /*
    public ArrayList<FactualityPart> getFactualityParts() {
	return factualityParts;
    }

    public void addFactualityPart(FactualityPart part) {
	this.factualityParts.add(part);
    }

    public void addFactualityPart(String prediction, double confidence) {
	this.addFactualityPart(new FactualityPart(prediction, confidence));
    }
    
    public List<WF> getWFs() {
	return word.getWFs();
    }

    public FactualityPart getMaxPart() {
	FactualityPart ret = null;
	double base = 0;

	for (FactualityPart p : factualityParts) {
	    if (p.getConfidence() > base) {
		ret = p;
		base = p.getConfidence();
	    }
	}

	return ret;
    }
    
    private class FactualityPart {

	String prediction;
	Double confidence;

	FactualityPart(String prediction) {
	    this.prediction = prediction;
	}

	FactualityPart(String prediction, double confidence) {
	    this.prediction = prediction;
	    this.confidence = confidence;
	}

	String getPrediction() {
	    return prediction;
	}

	void setPrediction(String prediction) {
	    this.prediction = prediction;
	}

	boolean hasConfidence() {
	    return this.confidence != null;
	}

	double getConfidence() {
	    return confidence;
	}

	void setConfidence(Double confidence) {
	    this.confidence = confidence;
	}

	@Override
	public String toString() {
	    return "FactualityPart{" +
		"prediction='" + prediction + '\'' +
		", confidence=" + confidence +
		'}';
	}
    }
    */

}

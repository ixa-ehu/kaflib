package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

/** Class for representing opinions. */
public class Opinion {

    public static class OpinionHolder {
	private AnnotationContainer annotationContainer;
	private List<String> targets;

	OpinionHolder(AnnotationContainer annotationContainer) {
	    this.annotationContainer = annotationContainer;
	    targets = new ArrayList<String>();
	}

	public List<Term> getTerms() {
	    return annotationContainer.getTermsById(targets);
	}

	public void addTerm(Term term) {
	    targets.add(term.getId());
	}
    }

    public static class OpinionTarget {
	private AnnotationContainer annotationContainer;
	private List<String> targets;

	OpinionTarget(AnnotationContainer annotationContainer) {
	    this.annotationContainer = annotationContainer;
	    targets = new ArrayList<String>();
	}

	public List<Term> getTerms() {
	    return annotationContainer.getTermsById(targets);
	}

	public void addTerm(Term term) {
	    targets.add(term.getId());
	}
    }

    public static class OpinionExpression {
	private AnnotationContainer annotationContainer;
	private String polarity;
	private String strength;
	private String subjectivity;
	private String sentimentSemanticType;
	private String sentimentProductFeature;
	private List<String> targets;

	OpinionExpression(AnnotationContainer annotationContainer) {
	    this.annotationContainer = annotationContainer;
	    targets = new ArrayList<String>();
	}

	public boolean hasPolarity() {
	    return (this.polarity != null);
	}

	public String getPolarity() {
	    return polarity;
	}

	public void setPolarity(String polarity) {
	    this.polarity = polarity;
	}

	public boolean hasStrength() {
	    return (this.strength != null);
	}

	public String getStrength() {
	    return strength;
	}

	public void setStrength(String strength) {
	    this.strength = strength;
	}

	public boolean hasSubjectivity() {
	    return (this.subjectivity != null);
	}

	public String getSubjectivity() {
	    return subjectivity;
	}

	public void setSubjectivity(String subjectivity) {
	    this.subjectivity = subjectivity;
	}

	public boolean hasSentimentSemanticType() {
	    return (this.sentimentSemanticType != null);
	}

	public String getSentimentSemanticType() {
	    return sentimentSemanticType;
	}

	public void setSentimentSemanticType(String sentimentSemanticType) {
	    this.sentimentSemanticType = sentimentSemanticType;
	}

	public boolean hasSentimentProductFeature() {
	    return (this.sentimentProductFeature != null);
	}

	public String getSentimentProductFeature() {
	    return sentimentProductFeature;
	}

	public void setSentimentProductFeature(String sentimentProductFeature) {
	    this.sentimentProductFeature = sentimentProductFeature;
	}

	public List<Term> getTerms() {
	    return annotationContainer.getTermsById(targets);
	}

	public void addTerm(Term term) {
	    targets.add(term.getId());
	}
    }
    

    private AnnotationContainer annotationContainer;

    private String id;

    private OpinionHolder opinionHolder;

    private OpinionTarget opinionTarget;

    private OpinionExpression opinionExpression;


    Opinion(AnnotationContainer annotationContainer, String id) {
	this.id = id;
	this.annotationContainer = annotationContainer;
    }

    public String getId() {
	return this.id;
    }

    public OpinionHolder getOpinionHolder() {
	return opinionHolder;
    }

    public OpinionTarget getOpinionTarget() {
	return opinionTarget;
    }

    public OpinionExpression getOpinionExpression() {
	return opinionExpression;
    }

    public OpinionHolder createOpinionHolder() {
	this.opinionHolder = new Opinion.OpinionHolder(this.annotationContainer);
	return this.opinionHolder;
    }

    public OpinionTarget createOpinionTarget() {
	this.opinionTarget = new Opinion.OpinionTarget(this.annotationContainer);
	return this.opinionTarget;
    }

    public OpinionExpression createOpinionExpression() {
	this.opinionExpression = new Opinion.OpinionExpression(this.annotationContainer);
	return this.opinionExpression;
    }

}

package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

/** Class for representing opinions. */
public class Opinion {

    public static class OpinionHolder {
	private AnnotationContainer annotationContainer;
	private Span<Term> span;

	OpinionHolder(AnnotationContainer annotationContainer, Span<Term> span) {
	    this.annotationContainer = annotationContainer;
	    this.span = span;
	}

	public List<Term> getTerms() {
	    return this.span.getTargets();
	}

	public void addTerm(Term term) {
	    this.span.addTarget(term);
	}

	public void addTerm(Term term, boolean isHead) {
	    this.span.addTarget(term, isHead);
	}

	public Span<Term> getSpan() {
	    return this.span;
	}

	public void setSpan(Span<Term> span) {
	    this.span = span;
	}
    }

    public static class OpinionTarget {
	private AnnotationContainer annotationContainer;
	private Span<Term> span;

	OpinionTarget(AnnotationContainer annotationContainer, Span<Term> span) {
	    this.annotationContainer = annotationContainer;
	    this.span = span;
	}

	public List<Term> getTerms() {
	    return this.span.getTargets();
	}

	public void addTerm(Term term) {
	    this.span.addTarget(term);
	}

	public void addTerm(Term term, boolean isHead) {
	    this.span.addTarget(term, isHead);
	}

	public Span<Term> getSpan() {
	    return this.span;
	}

	public void setSpan(Span<Term> span) {
	    this.span = span;
	}
    }

    public static class OpinionExpression {
	private AnnotationContainer annotationContainer;
	private String polarity;
	private String strength;
	private String subjectivity;
	private String sentimentSemanticType;
	private String sentimentProductFeature;
	private Span<Term> span;

	OpinionExpression(AnnotationContainer annotationContainer, Span<Term> span) {
	    this.annotationContainer = annotationContainer;
	    this.span = span;
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
	    return this.span.getTargets();
	}

	public void addTerm(Term term) {
	    this.span.addTarget(term);
	}

	public void addTerm(Term term, boolean isHead) {
	    this.span.addTarget(term, isHead);
	}

	public Span<Term> getSpan() {
	    return this.span;
	}

	public void setSpan(Span<Term> span) {
	    this.span = span;
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

    public OpinionHolder createOpinionHolder(Span<Term> span) {
	this.opinionHolder = new Opinion.OpinionHolder(this.annotationContainer, span);
	return this.opinionHolder;
    }

    public OpinionTarget createOpinionTarget(Span<Term> span) {
	this.opinionTarget = new Opinion.OpinionTarget(this.annotationContainer, span);
	return this.opinionTarget;
    }

    public OpinionExpression createOpinionExpression(Span<Term> span) {
	this.opinionExpression = new Opinion.OpinionExpression(this.annotationContainer, span);
	return this.opinionExpression;
    }

    public String getSpanStr(Span<Term> span) {
	String str = "";
	for (Term term : span.getTargets()) {
	    if (!str.isEmpty()) {
		str += " ";
	    }
	    str += term.getStr();
	}
	return str;
    }

    public String getStr() {
	return getSpanStr(this.getOpinionExpression().getSpan());
    }

}

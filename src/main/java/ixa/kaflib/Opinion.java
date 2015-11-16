package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/** Class for representing opinions. */
public class Opinion extends IdentifiableAnnotation {

    public static class OpinionHolder extends Annotation {
	private String type;
	private Span<Term> span;
	
	private static final long serialVersionUID = 1L;

	
	OpinionHolder(AnnotationContainer annotationContainer, Span<Term> span) {
	    super(annotationContainer);
	    this.span = span;
	}

	public boolean hasType() {
	    return type != null;
	}

	public String getType() {
	    return type;
	}

	public void setType(String type) {
	    this.type = type;
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

	Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	    Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	    referenced.put(AnnotationType.TERM,
		    (List<Annotation>) (List<?>) this.getSpan().getTargets());
	    return referenced;
	}
	    
	@Override
	public Integer getOffset() {
	    return this.getSpan().getOffset();
	}

	@Override
	public String toString() {
	    return this.span.toString();
	}
	
	/*
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof OpinionHolder)) return false;
	    OpinionHolder ann = (OpinionHolder) o;
	    return Utils.areEquals(this.type, ann.type) &&
		    Utils.areEquals(this.span, ann.span);
	}
	*/
    }

    public static class OpinionTarget extends Annotation {
	private Span<Term> span;

	private static final long serialVersionUID = 1L;
	

	OpinionTarget(AnnotationContainer annotationContainer, Span<Term> span) {
	    super(annotationContainer);
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

	Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	    Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	    referenced.put(AnnotationType.TERM,
		    (List<Annotation>) (List<?>) this.getSpan().getTargets());
	    return referenced;
	}
	    
	@Override
	public Integer getOffset() {
	    return this.getSpan().getOffset();
	}

	@Override
	public String toString() {
	    return this.span.toString();
	}
	
	/*
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof OpinionTarget)) return false;
	    OpinionTarget ann = (OpinionTarget) o;
	    return Utils.areEquals(this.span, ann.span);
	}
	*/
    }

    public static class OpinionExpression extends Annotation {

	/* Polarity (optional) */
	private String polarity;

	/* Strength (optional) */
	private String strength;

	/* Subjectivity (optional) */
	private String subjectivity;

	/* Sentiment semantic type (optional) */
	private String sentimentSemanticType;

	/* Sentiment product feature (optional) */
	private String sentimentProductFeature;

	private Span<Term> span;

	private static final long serialVersionUID = 1L;
	

	OpinionExpression(AnnotationContainer annotationContainer, Span<Term> span) {
	    super(annotationContainer);
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

	Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	    Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	    referenced.put(AnnotationType.TERM, (List<Annotation>) (List<?>) this.getSpan().getTargets());
	    return referenced;
	}
	    
	@Override
	public Integer getOffset() {
	    return this.span.getOffset();
	}
	
	@Override
	public String toString() {
	    return this.span.toString();
	}

	/*
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof OpinionExpression)) return false;
	    OpinionExpression ann = (OpinionExpression) o;
	    return Utils.areEquals(this.polarity, ann.polarity) &&
		    Utils.areEquals(this.strength, ann.strength) &&
		    Utils.areEquals(this.subjectivity, ann.subjectivity) &&
		    Utils.areEquals(this.sentimentSemanticType, ann.sentimentSemanticType) &&
		    Utils.areEquals(this.sentimentProductFeature, ann.sentimentProductFeature) &&
		    Utils.areEquals(this.span, ann.span);
	}
	*/
    }

    private OpinionHolder opinionHolder;
    private OpinionTarget opinionTarget;
    private OpinionExpression opinionExpression;

    private static final long serialVersionUID = 1L;
    
    
    Opinion(AnnotationContainer annotationContainer, String id) {
	super(annotationContainer, id);
    }

    public boolean hasOpinionHolder() {
	return opinionHolder != null;
    }

    public boolean hasOpinionTarget() {
	return opinionTarget != null;
    }

    public boolean hasOpinionExpression() {
	return opinionExpression != null;
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

    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	List<Term> referencedTerms = new ArrayList<Term>();
	
	if ((this.opinionExpression != null) && (this.opinionExpression.getSpan() != null))
	    referencedTerms.addAll(this.opinionExpression.getSpan().getTargets());
	if ((this.opinionHolder != null) && (this.opinionHolder.getSpan() != null)) referencedTerms.addAll(this.opinionHolder.getSpan().getTargets());
	if ((this.opinionTarget != null) && (this.opinionTarget.getSpan() != null)) referencedTerms.addAll(this.opinionTarget.getSpan().getTargets());
	referenced.put(AnnotationType.TERM, (List<Annotation>) (List<?>) referencedTerms);
	return referenced;
    }
    
    @Override
    public Integer getOffset() {
	return Math.min(Math.min(this.getOpinionExpression().getOffset(), this.getOpinionHolder().getOffset()), this.getOpinionTarget().getOffset());
    }
    
    @Override
    public String toString() {
	return this.opinionExpression.toString();
    }
    
    
    @Deprecated
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

    @Deprecated
    public String getStr() {
	return getSpanStr(this.getOpinionExpression().getSpan());
    }

    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof Opinion)) return false;
	Opinion ann = (Opinion) o;
	return Utils.areEquals(this.opinionHolder, ann.opinionHolder) &&
		Utils.areEquals(this.opinionTarget, ann.opinionTarget) &&
		Utils.areEquals(this.opinionExpression, ann.opinionExpression);
    }
    */
}

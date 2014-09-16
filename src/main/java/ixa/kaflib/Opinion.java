package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;

/** Class for representing opinions. */
public class Opinion implements Serializable {

    public static class OpinionHolder implements Serializable {
	private String type;
	private Span<Term> span;

	OpinionHolder(Span<Term> span) {
	    this.span = span;
	}

	OpinionHolder(OpinionHolder oh, HashMap<String, Term> terms) {
	    /* Copy span */
	    Span<Term> span = oh.span;
	    List<Term> targets = span.getTargets();
	    List<Term> copiedTargets = new ArrayList<Term>();
	    for (Term term : targets) {
		Term copiedTerm = terms.get(term.getId());
		if (copiedTerm == null) {
		    throw new IllegalStateException("Term not found when copying opinion_holder");
		}
		copiedTargets.add(copiedTerm);
	    }
	    if (span.hasHead()) {
		Term copiedHead = terms.get(span.getHead().getId());
		this.span = new Span<Term>(copiedTargets, copiedHead);
	    }
	    else {
		this.span = new Span<Term>(copiedTargets);
	    }	    
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
    }

    public static class OpinionTarget implements Serializable {
	private Span<Term> span;

	OpinionTarget(Span<Term> span) {
	    this.span = span;
	}

	OpinionTarget(OpinionTarget ot, HashMap<String, Term> terms) {
	    /* Copy span */
	    Span<Term> span = ot.span;
	    List<Term> targets = span.getTargets();
	    List<Term> copiedTargets = new ArrayList<Term>();
	    for (Term term : targets) {
		Term copiedTerm = terms.get(term.getId());
		if (copiedTerm == null) {
		    throw new IllegalStateException("Term not found when copying opinion_target");
		}
		copiedTargets.add(copiedTerm);
	    }
	    if (span.hasHead()) {
		Term copiedHead = terms.get(span.getHead().getId());
		this.span = new Span<Term>(copiedTargets, copiedHead);
	    }
	    else {
		this.span = new Span<Term>(copiedTargets);
	    }
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

    public static class OpinionExpression implements Serializable {
	
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

	OpinionExpression(Span<Term> span) {
	    this.span = span;
	}

	OpinionExpression(OpinionExpression oe, HashMap<String, Term> terms) {
	    this.polarity = oe.polarity;
	    this.strength = oe.strength;
	    this.subjectivity = oe.subjectivity;
	    this.sentimentSemanticType = oe.sentimentSemanticType;
	    this.sentimentProductFeature = oe.sentimentProductFeature;
	    /* Copy span */
	    Span<Term> span = oe.span;
	    List<Term> targets = span.getTargets();
	    List<Term> copiedTargets = new ArrayList<Term>();
	    for (Term term : targets) {
		Term copiedTerm = terms.get(term.getId());
		if (copiedTerm == null) {
		    throw new IllegalStateException("Term not found when copying opinion_expression");
		}
		copiedTargets.add(copiedTerm);
	    }
	    if (span.hasHead()) {
		Term copiedHead = terms.get(span.getHead().getId());
		this.span = new Span<Term>(copiedTargets, copiedHead);
	    }
	    else {
		this.span = new Span<Term>(copiedTargets);
	    }
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
    
    private String id;

    private OpinionHolder opinionHolder;

    private OpinionTarget opinionTarget;

    private OpinionExpression opinionExpression;


    Opinion(String id) {
	this.id = id;
    }

    Opinion(Opinion opinion, HashMap<String, Term> terms) {
	this.id = opinion.id;
	if (opinion.opinionHolder != null) {
	    this.opinionHolder = new OpinionHolder(opinion.opinionHolder, terms);
	}
	if (opinion.opinionTarget != null) {
	    this.opinionTarget = new OpinionTarget(opinion.opinionTarget, terms);
	}
	if (opinion.opinionExpression != null) {
	    this.opinionExpression = new OpinionExpression(opinion.opinionExpression, terms);
	}
    }

    public String getId() {
	return this.id;
    }

    void setId(String id) {
	this.id = id;
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
	this.opinionHolder = new Opinion.OpinionHolder(span);
	return this.opinionHolder;
    }

    public OpinionTarget createOpinionTarget(Span<Term> span) {
	this.opinionTarget = new Opinion.OpinionTarget(span);
	return this.opinionTarget;
    }

    public OpinionExpression createOpinionExpression(Span<Term> span) {
	this.opinionExpression = new Opinion.OpinionExpression(span);
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

package ixa.kaflib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ixa.kaflib.KAFDocument.AnnotationType;


abstract class TermBase extends IdentifiableAnnotation implements SentenceLevelAnnotation, WithExternalRefs {
    
    /** Type of the term (optional). Currently, 2 values are possible: open and close. */
    private String type;

    /** Lemma of the term (optional) */
    private String lemma;

    /** Part of speech (optional). Possible values are:
     * - common noun (N)
     * - proper noun (R)
     * - adjective (G)
     * - verb (V)
     * - preposition (P)
     * - adverb (A)
     * - conjunction (C)
     * - determiner (D)
     * - other (O)
     **/
    private String pos;

    /** Morphosyntactic feature encoded as a single attribute (optional) */
    private String morphofeat;

    /** Declension case of the term (optional) */
    private String termcase;

    /** Sentiment features (optional) */
    private Sentiment sentiment;

    /** Target elements are used to refer to the target word. If the term is a multiword, multiple target elements are used. (required)*/
    private Span<WF> span;

    /** ExternalReferences are used to associate terms to external lexical or semantic resources, such as elements of a Knowledge base: semantic lexicon  (like WordNet) or an ontology (optional) */
    private ExternalReferences externalRefs;
    
    protected AnnotationType annotationType;

    private static final long serialVersionUID = 1L;


    TermBase(AnnotationContainer annotationContainer, String id, Span<WF> span) {
	super(annotationContainer, id);
	this.setSpan((span != null) ? span : new Span<WF>());
	this.externalRefs = new ExternalReferences();
	this.annotationType = AnnotationType.TERM;
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

    public boolean hasLemma() {
	return lemma != null;
    }

    public String getLemma() {
	return lemma;
    }

    public void setLemma(String lemma) {
	this.lemma = lemma;
    }

    public boolean hasPos() {
	return pos != null;
    }

    public String getPos() {
	return pos;
    }

    public void setPos(String pos) {
	this.pos = pos;
    }

    public boolean hasMorphofeat() {
	return morphofeat != null;
    }

    public String getMorphofeat() {
	return morphofeat;
    }

    public void setMorphofeat(String morphofeat) {
	this.morphofeat = morphofeat;
    }

    public boolean hasCase() {
	return termcase != null;
    }

    public String getCase() {
	return termcase;
    }

    public void setCase(String termcase) {
	this.termcase = termcase;
    }

    public Sentiment newSentiment() {
	Sentiment newSentiment = new Sentiment(this.annotationContainer);
	this.setSentiment(newSentiment);
	return newSentiment;
    }

    public boolean hasSentiment() {
	return (this.sentiment != null);
    }

    public Sentiment getSentiment() {
	return sentiment;
    }
    
    public void setSentiment(Sentiment sentiment) {
	if (this.sentiment != null) {
	    this.annotationContainer.unindexAnnotationReferences(this.annotationType, this, this.sentiment);	    
	}
        this.sentiment = sentiment;
	this.annotationContainer.indexAnnotationReferences(this.annotationType, this, this.sentiment);
    }

    public Span<WF> getSpan() {
	return this.span;
    }

    public void setSpan(Span<WF> span) {
	span.setOwner(this, this.annotationType, this.annotationContainer);
	this.span = span;
    }

    public ExternalReferences getExternalReferences() {
	return this.externalRefs;
    }

    @Override
    public Integer getOffset() {
	return this.span.getOffset();
    }
    
    @Override
    public Integer getSent() {
	return this.getSpan().isEmpty() ? null : this.getSpan().getFirstTarget().getSent();
    }
    
    @Override
    public Integer getPara() {
	return this.getSpan().isEmpty() ? null : this.getSpan().getFirstTarget().getPara();
    }
    
    @Override
    public String toString() {
	return this.span.toString();
    }
    
    
    @Deprecated
    // Use toString()
    public String getForm() {
	String str = "";
	for (WF wf : span.getTargets()) {
	    if (!str.isEmpty()) {
		str += " ";
	    }
	    str += wf.getForm();
	}
	return str;
    }

    @Deprecated
    public Sentiment createSentiment() {
	return this.newSentiment();
    }

    @Deprecated
    // Use toStringComment()
    public String getStr() {
	String strValue = this.getForm();
	boolean valid = false;
	while (!valid) {
	    if (strValue.startsWith("-") || strValue.endsWith("-")) {
		strValue = strValue.replace("-", " - ");
	    }
	    else if (strValue.contains("--")) { 
		strValue = strValue.replace("--", "-");
	    }
	    else {
		valid = true;
	    }
	}
	return strValue;
    }

    @Deprecated
    public List<WF> getWFs() {
	return this.span.getTargets();
    }

    @Deprecated
    public WF getHeadWF() {
	return this.span.getHead();
    }

    @Deprecated
    public void addWF(WF wf) {
	this.span.addTarget(wf);
    }

    @Deprecated
    public void addWF(WF wf, boolean isHead) {
	this.span.addTarget(wf, isHead);
    }

    @Deprecated
    public List<ExternalRef> getExternalRefs() {
	return this.externalRefs.get();
    }

    @Deprecated
    public void addExternalRef(ExternalRef externalRef) {
	this.externalRefs.add(externalRef);
    }

    @Deprecated
    public void addExternalRefs(List<ExternalRef> externalRefs) {
	this.externalRefs.add(externalRefs);
    }

    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof Term)) return false;
	Term ann = (Term) o;
	return Utils.areEquals(this.type, ann.type) &&
		Utils.areEquals(this.lemma, ann.lemma) &&
		Utils.areEquals(this.pos, ann.pos) &&
		Utils.areEquals(this.morphofeat, ann.morphofeat) &&
		Utils.areEquals(this.termcase, ann.termcase) &&
		Utils.areEquals(this.sentiment, ann.sentiment) &&
		Utils.areEquals(this.components, ann.components) &&
		Utils.areEquals(this.head, ann.head) &&
		Utils.areEquals(this.span, ann.span) &&
		Utils.areEquals(this.externalReferences, ann.externalReferences) &&
		Utils.areEquals(this.isComponent, ann.isComponent) &&
		Utils.areEquals(this.compound, ann.compound);
    }
    */


    /** The term layer represents sentiment information which is context-independent and that can be found in a sentiment lexicon.
     * It is related to concepts expressed by words/ terms (e.g. beautiful) or multi-word expressions (e. g. out of order).
     * We provide possibilities to store sentiment information at word level and at sense/synset level. In the latter case, the sentiment information
     * is included in the “external_reference” section and a WSD process may identify the correct sense with its sentiment information. The extension contains the following information categories.
     */
    public static class Sentiment extends Annotation {

	/** Identifier and reference to an external sentiment resource (optional) */
	private String resource;

	/** Refers to the property of a word to express positive, negative or no sentiment (optional). These values are possible:
	 * - Positive
	 * - Negative
	 * - Neutral
	 * - Or numerical value on a numerical scale
	 */
	private String polarity;

	/** Refers to the strength of the polarity (optional). These values are possible:
	 * - Weak
	 * - Average
	 * - Strong
	 * - Or Numerical value
	 */
	private String strength;

	/** Refers to the property of a words to express an opionion (or not) (optional).
	 * - Subjective/Objective
	 * - Factual/opinionated
	 */
	private String subjectivity;

	/** Refers to a sentiment-related semantic type (optional):
	 * - Aesthetics_evaluation
	 * - Moral_judgment
	 * - Emotion
	 * - etc
	 */
	private String sentimentSemanticType;

	/** Refers to words which modify the polarity of another word (optional):
	 * - Intensifier/weakener polarity shifter
	 */
	private String sentimentModifier;

	/** Refers to words which themselves do not carry polarity, but are kind of vehicles of it (optional):
	 * - Find, think, in my opinion, according to...
	 */
	private String sentimentMarker;

	/** Refers to a domain; mainly used in feature-based sentiment analysis (optional):
	 * - Values are related to specific domain. For the tourist domain, for example, staff, cleanliness, beds, bathroom, transportation, location, etc...
	 */
	private String sentimentProductFeature;

	private static final long serialVersionUID = 1L;

	
	Sentiment(AnnotationContainer annotationContainer) {
	    super(annotationContainer);
	}

	public boolean hasResource() {
	    return resource != null;
	}

	public String getResource() {
	    return resource;
	}

	public void setResource(String val) {
	    resource = val;
	}

	public boolean hasPolarity() {
	    return polarity != null;
	}

	public String getPolarity() {
	    return polarity;
	}

	public void setPolarity(String val) {
	    polarity = val;
	}

	public boolean hasStrength() {
	    return strength != null;
	}

	public String getStrength() {
	    return strength;
	}

	public void setStrength(String val) {
	    strength = val;
	}

	public boolean hasSubjectivity() {
	    return subjectivity != null;
	}

	public String getSubjectivity() {
	    return subjectivity;
	}

	public void setSubjectivity(String val) {
	    subjectivity = val;
	}

	public boolean hasSentimentSemanticType() {
	    return sentimentSemanticType != null;
	}

	public String getSentimentSemanticType() {
	    return sentimentSemanticType;
	}

	public void setSentimentSemanticType(String val) {
	    sentimentSemanticType = val;
	}

	public boolean hasSentimentModifier() {
	    return sentimentModifier != null;
	}

	public String getSentimentModifier() {
	    return sentimentModifier;
	}

	public void setSentimentModifier(String val) {
	    sentimentModifier = val;
	}

	public boolean hasSentimentMarker() {
	    return sentimentMarker != null;
	}

	public String getSentimentMarker() {
	    return sentimentMarker;
	}

	public void setSentimentMarker(String val) {
	    sentimentMarker = val;
	}

	public boolean hasSentimentProductFeature() {
	    return sentimentProductFeature != null;
	}

	public String getSentimentProductFeature() {
	    return sentimentProductFeature;
	}

	public void setSentimentProductFeature(String val) {
	    sentimentProductFeature = val;
	}
	
	@Override
	Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	    return new HashMap<AnnotationType, List<Annotation>>();
	}
	
	@Override
	public Integer getOffset() {
	    return null;
	}
	
	@Override
	public String toString() {
	    if (this.resource == null && this.polarity == null) return new String();
	    else if (this.resource == null) return this.polarity;
	    return this.resource + ": " + this.polarity;
	}
	
	/*
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof Sentiment)) return false;
	    Sentiment ann = (Sentiment) o;
	    return Utils.areEquals(this.resource, ann.resource) &&
		    Utils.areEquals(this.polarity, ann.polarity) &&
		    Utils.areEquals(this.strength, ann.strength) &&
		    Utils.areEquals(this.subjectivity, ann.subjectivity) &&
		    Utils.areEquals(this.sentimentSemanticType, ann.sentimentSemanticType) &&
		    Utils.areEquals(this.sentimentModifier, ann.sentimentModifier) &&
		    Utils.areEquals(this.sentimentMarker, ann.sentimentMarker) &&
		    Utils.areEquals(this.sentimentProductFeature, ann.sentimentProductFeature);
	}
	*/
    }
    
}

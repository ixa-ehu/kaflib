package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


/** Class for representing terms. Terms refer to previous word forms (and groups multi-words) and attach lemma, part of speech, synset and name entity information. */
public class Term extends IdentifiableAnnotation implements SentenceLevelAnnotation {

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

    /** If it's a compound term, it's components (optional) */
    private List<Term> components;

    /** Head component (optional) */
    private Term head;

    /** Target elements are used to refer to the target word. If the term is a multiword, multiple target elements are used. (required)*/
    private Span<WF> span;

    /** ExternalReferences are used to associate terms to external lexical or semantic resources, such as elements of a Knowledge base: semantic lexicon  (like WordNet) or an ontology (optional) */
    private List<ExternalRef> externalReferences;

    private boolean isComponent;
    private Term compound; // Parent compound term of this component


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

	Sentiment() {
	}

	Sentiment(Sentiment sentiment) {
	    this.resource = sentiment.resource;
	    this.polarity = sentiment.polarity;
	    this.strength = sentiment.strength;
	    this.subjectivity = sentiment.subjectivity;
	    this.sentimentSemanticType = sentiment.sentimentSemanticType;
	    this.sentimentModifier = sentiment.sentimentModifier;
	    this.sentimentMarker = sentiment.sentimentMarker;
	    this.sentimentProductFeature = sentiment.sentimentProductFeature;
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
	
	Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	    return new HashMap<AnnotationType, List<Annotation>>();
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

    Term(String id, Span<WF> span, boolean isComponent) {
	/*
	if (span.size() < 1) {
	    throw new IllegalStateException("A Term must have at least one WF");
	}
	*/
	super(id);
	this.components = new ArrayList();
	this.span = span;
	this.externalReferences = new ArrayList<ExternalRef>();
	this.isComponent = isComponent;
    }

    /* Copy constructor */
    Term(Term term, HashMap<String, WF> wfs) {
	// Copy simple fields
	super(term.getId());
	this.type = term.type;
	this.lemma = term.lemma;
	this.pos = term.pos;
	this.morphofeat = term.morphofeat;
	this.termcase = term.termcase;
	// Copy sentiment
	if (term.hasSentiment()) {
	    this.sentiment = new Sentiment(term.sentiment);
	}
	// Copy components and head
	HashMap<String, Term> newComponents = 
	    new HashMap<String, Term>();
	this.components = new ArrayList<Term>();
	for (Term component : term.components) {
	    Term copyComponent = new Term(component, wfs);
	    this.components.add(copyComponent);
	    newComponents.put(component.getId(), copyComponent);
	}
	if (term.hasHead()) {
	    this.head = newComponents.get(term.head.getId());
	}
	// Copy span
	List<WF> targets = term.span.getTargets();
	List<WF> copiedTargets = new ArrayList<WF>();
	for (WF wf : targets) {
	    WF copiedWf = wfs.get(wf.getId());
	    if (copiedWf == null) {
		throw new IllegalStateException("WF not found when copying Term " + term.getId());
	    }
	    copiedTargets.add(copiedWf);
	}
	if (term.span.hasHead()) {
	    WF copiedHead = wfs.get(term.span.getHead().getId());
	    this.span = new Span<WF>(copiedTargets, copiedHead);
	}
	else {
	    this.span = new Span<WF>(copiedTargets);
	}
	// Copy external references
	this.externalReferences = new ArrayList<ExternalRef>();
	for (ExternalRef externalRef : term.getExternalRefs()) {
	    this.externalReferences.add(new ExternalRef(externalRef));
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

    public boolean hasHead() {
	return (this.head != null);
    }

    public Term getHead() {
        return this.head;
    }

    /** Creates and adds a Sentiment object.
     * @return a new sentiment.
     */
    public Sentiment createSentiment() {
	Sentiment newSentiment = new Sentiment();
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
        this.sentiment = sentiment;
    }

    public List<Term> getComponents() {
	return this.components;
    }

    public void addComponent(Term component) {
	components.add(component);
    }

    public void addComponent(Term component, boolean isHead) {
	components.add(component);
	if (isHead) {
	    this.head = component;
	}
    }

    public List<WF> getWFs() {
	return this.span.getTargets();
    }

    public WF getHeadWF() {
	return this.span.getHead();
    }

    public void addWF(WF wf) {
	this.span.addTarget(wf);
    }

    public void addWF(WF wf, boolean isHead) {
	this.span.addTarget(wf, isHead);
    }

    public Span<WF> getSpan() {
	return this.span;
    }

    public void setSpan(Span<WF> span) {
	this.span = span;
    }

    public Integer getSent() {
	Span<WF> wfs = this.getSpan();
	List<WF> wfl = wfs.getTargets();
	WF wf = wfl.get(0);
	//
	if (wf == null) System.out.println(wfl.size());
	Integer sent = wf.getSent();
	return this.getSpan().getTargets().get(0).getSent();
	/*
	if (!this.isComponent()) {
	    return this.getSpan().getTargets().get(0).getSent();
	} else {
	    return this.getCompound().getSent();
	}
	*/
    }
    
    public Integer getPara() {
	return this.getSpan().getTargets().get(0).getPara();
    }

    public List<ExternalRef> getExternalRefs() {
	return externalReferences;
    }

    public void addExternalRef(ExternalRef externalRef) {
	externalReferences.add(externalRef);
    }

    public void addExternalRefs(List<ExternalRef> externalRefs) {
	externalReferences.addAll(externalRefs);
    }

    boolean isComponent() {
	return this.isComponent;
    }

    public void setCompound(Term compound) {
	this.compound = compound;
    }

    public Term getCompound() {
	return this.compound;
    }
    
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.WF, (List<Annotation>)(List<?>) this.getSpan().getTargets());
	return referenced;
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
}

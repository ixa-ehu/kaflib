package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/** Class for representing terms. Terms refer to previous word forms (and groups multi-words) and attach lemma, part of speech, synset and name entity information. */
public class Term {

    /** Reference to the main annotationContainer of the document to which this term is related (required) */
    private AnnotationContainer annotationContainer;

    /** Term's ID (required) */
    private String tid;

    /** Type of the term (required). Currently, 2 values are possible: open and close. */
    private String type;

    /** Lemma of the term (required) */
    private String lemma;

    /** Part of speech (required). Possible values are:
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

    /** If the term is a compound, the id of the head component (optional) */
    private String head;

    /** Declension case of the term (optional) */
    private String termcase;

    /** Comment string for debugging purposes */
    private String strValue;

    /** Sentiment features (optional) */
    private Sentiment sentiment;

    /** If it's a compound term, it's components (optional) */ 
    private List<Component> components;

    /** Hash map indexing components by their ID */
    private HashMap<String, Integer> componentIndex;

    /** Target elements are used to refer to the target word. If the term is a multiword, multiple target elements are used. (required)*/
    private List<String> targets;

    /** ExternalReferences are used to associate terms to external lexical or semantic resources, such as elements of a Knowledge base: semantic lexicon  (like WordNet) or an ontology (optional) */
    private List<ExternalRef> externalReferences;

    /** The term layer represents sentiment information which is context-independent and that can be found in a sentiment lexicon.
     * It is related to concepts expressed by words/ terms (e.g. beautiful) or multi-word expressions (e. g. out of order).
     * We provide possibilities to store sentiment information at word level and at sense/synset level. In the latter case, the sentiment information
     * is included in the “external_reference” section and a WSD process may identify the correct sense with its sentiment information. The extension contains the following information categories.
     */
    public static class Sentiment {

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

	Sentiment() {}

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
    }

    /** Compound terms can be represented in KAF by including them within terms. For example, the Dutch term landbouwbeleid (English: agriculture policy) would contain two components: landbouw + beleid. */
    public static class Component {

	/** Component¡s ID (required) */
	private String id;

	/** Lemma of the component (required) */
	private String lemma;

	/** Part of speech (required) */
	private String pos;

	/** Declension case (optional) */
	private String componentcase;

	/** External references (optional) */
	private List<ExternalRef> externalReferences;

	Component(String id, String lemma, String pos) {
	    this.id = id;
	    this.lemma = lemma;
	    this.pos = pos;
	    this.externalReferences = new ArrayList<ExternalRef>();
	}

	public String getId() {
	    return id;
	}

	public String getLemma() {
	    return lemma;
	}

	public void setLemma(String val) {
	    this.lemma = lemma;
	}

	public String getPos() {
	    return pos;
	}

	public void setPos(String val) {
	    this.pos = pos;
	}

	public boolean hasCase() {
	    return componentcase != null;
	}

	public String getCase() {
	    return componentcase;
	}

	public void setCase(String val) {
	    this.componentcase = componentcase;
	}

	public List<ExternalRef> getExternalRefs() {
	    return externalReferences;
	}

	public void addExternalRef(ExternalRef val) {
	    externalReferences.add(val);
	}

	public void addExternalRefs(List<ExternalRef> val) {
	    externalReferences.addAll(val);
	}
    }

    /** To create a new Term, the annotationContainer reference is necessary apart from the id, type, lemma, pos and, at least, one word form reference. */
    Term(AnnotationContainer annotationContainer, String id, String type, String lemma, String pos, List<WF> wfs) {
	if (wfs.size() < 1) {
	    throw new IllegalStateException("A Term must have at least one WF");
	}
	this.annotationContainer = annotationContainer;
	this.tid = id;
	this.type = type;
	this.lemma = lemma;
	this.pos = pos;
	this.strValue = "";
	this.components = new ArrayList();
	this.componentIndex = new HashMap<String, Integer>();
	this.targets = new ArrayList<String>();
	for (WF target : wfs) {
	    this.addWF(target);
	}
	this.externalReferences = new ArrayList<ExternalRef>();
    }

    public String getId() {
	return tid;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getLemma() {
	return lemma;
    }

    public void setLemma(String lemma) {
	this.lemma = lemma;
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

    private void updateStr(String str) {
	if (!this.strValue.isEmpty()) {
	    this.strValue += " ";
	}
	this.strValue += str;
    }

    public String getStr() {
	return strValue;
    }

    public boolean hasHead() {
	return head != null;
    }

    public Component getHead() {
	int headIndex = componentIndex.get(head);
	return components.get(headIndex);
    }

    public void setHead(String id) {
	head = id;
    }
    
    public void setHead(Component component) {
	head = component.getId();
    }

    public boolean hasSentiment() {
	return this.sentiment != null;
    }

    public Sentiment getSentiment() {
	return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
	this.sentiment = sentiment;
    }

    public List<Component> getComponents() {
	return components;
    }

    public void addComponent(Component component) {
	components.add(component);
	componentIndex.put(component.getId(), components.size() - 1);
    }

    public List<WF> getWFs() {
	return annotationContainer.getWFsById(targets);
    }

    public void addWF(WF wf) {
	targets.add(wf.getId());
	this.updateStr(wf.getForm());
    }

    public int getSent() {
	return annotationContainer.getWFById(this.targets.get(0)).getSent();
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
}
    
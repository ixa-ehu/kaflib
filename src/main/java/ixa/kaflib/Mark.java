package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class Mark extends IdentifiableAnnotation implements SentenceLevelAnnotation, MultiLayerAnnotation {

    private String source;

    private String type;

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

    private String morphofeat;

    private String markcase;

    private Span<WF> span;
    
    private Term.Sentiment sentiment;

    private List<ExternalRef> externalReferences;


    Mark(String id, Span<WF> span) {
	/*
	if (span.size() < 1) {
	    throw new IllegalStateException("A Mark must have at least one WF");
	}
	*/
	super(id);
	this.span = span;
	this.externalReferences = new ArrayList<ExternalRef>();
    }

    public boolean hasSource() {
	return this.source != null;
    }
    
    public String getSource() {
	return this.source;
    }

    public void setSource(String source) {
	this.source = source;
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
	return markcase != null;
    }

    public String getCase() {
	return markcase;
    }

    public void setCase(String markcase) {
	this.markcase = markcase;
    }

    public String getStr() {
	String str = "";
	for (WF wf : span.getTargets()) {
	    if (!str.isEmpty()) {
		str += " ";
	    }
	    str += wf.getForm();
	}
	return str;
    }

    public Span<WF> getSpan() {
	return this.span;
    }

    public void setSpan(Span<WF> span) {
	this.span = span;
    }
    
    public boolean hasSentiment() {
	return this.sentiment != null;
    }
    
    public Term.Sentiment getSentiment() {
	return this.sentiment;
    }
    
    public void setSentiment(Term.Sentiment sentiment) {
	this.sentiment = sentiment;
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
    
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.WF, (List<Annotation>)(List<?>) this.getSpan().getTargets());
	return referenced;
    }

    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof Mark)) return false;
	Mark ann = (Mark) o;
	return Utils.areEquals(this.source, ann.source) &&
		Utils.areEquals(this.type, ann.type) &&
		Utils.areEquals(this.lemma, ann.lemma) &&
		Utils.areEquals(this.pos, ann.pos) &&
		Utils.areEquals(this.morphofeat, ann.morphofeat) &&
		Utils.areEquals(this.markcase, ann.markcase) &&
		Utils.areEquals(this.span, ann.span) &&
		Utils.areEquals(this.externalReferences, ann.externalReferences);
    }
    */
    
    @Override
    public Integer getSent() {
	return this.span.getFirstTarget().getSent();
    }
    
    @Override
    public Integer getPara() {
	return this.span.getFirstTarget().getPara();
    }

    @Override
    public String getGroupID() {
	return this.getSource();
    }

}

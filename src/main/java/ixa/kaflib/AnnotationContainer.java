package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Layer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jdom2.Element;


/** A container to keep all annotations of a document (word forms, terms, dependencies, chunks, entities and coreferences). There are different hash maps to index annotations by different properties as ID, sentence... It enables to retrieve annotations by different properties in an effective way. Performance is very important. */
class AnnotationContainer implements Serializable {

    /* Annotation containers */
    private String rawText;
    private Map<AnnotationType, List<Annotation>> annotations; /* (AnnotationType => Annotations) */
    private Set<Element> unknownLayers;
    
    /* Indices */
    private Map<Annotation, Map<AnnotationType, TreeSet<Annotation>>> invRefIndex; /* (Annotation => (AnnotationType => Annotations)) */
    private Map<AnnotationType, Map<Integer, TreeSet<Annotation>>> sentIndex; /* (AnnotationType => (Sentence => Annotations)) */
    private Map<Integer, TreeSet<Integer>> paraSentIndex; /* Para => List<Sent> */
    private Map<Integer, Map<Integer, Integer>> paraSentIndexInfo; /* (Sentence => (Paragraph => NumberOfAnnotations)) */
    private Boolean paraSentIndexUpToDate;

    private Integer creationOrderId = 0;
    
    private static final long serialVersionUID = 42L; // Serializable...


    /** This creates a new AnnotationContainer object */
    AnnotationContainer() {
	rawText = new String();
	annotations = new HashMap<AnnotationType, List<Annotation>>();
	unknownLayers = new HashSet<Element>();
	invRefIndex = new HashMap<Annotation, Map<AnnotationType, TreeSet<Annotation>>>();
	sentIndex = new HashMap<AnnotationType, Map<Integer, TreeSet<Annotation>>>();
	paraSentIndex = new TreeMap<Integer, TreeSet<Integer>>();
	paraSentIndexInfo = new HashMap<Integer, Map<Integer, Integer>>();
	paraSentIndexUpToDate = true;
    }

    Integer getNextCreationOrderId() {
	return this.creationOrderId++;
    }
    
    /** Returns raw text */
    String getRawText() {
	return rawText;
    }

    List<Annotation> getAnnotations(Layer layer) {
	List<Annotation> annotations = new ArrayList<Annotation>();
	for (AnnotationType type : KAFDocument.LAYER_2_TYPES.get(layer)) {
	    annotations.addAll(this.annotations.get(type));
	}
	return annotations;
    }
    

    List<Annotation> getAnnotations(AnnotationType type) {
	List<Annotation> annotations = this.annotations.get(type);
	return (annotations == null) ? new ArrayList<Annotation>() : annotations;
    }

    List<Annotation> getAnnotationsBy(Annotation ann, AnnotationType type) {
	Map<AnnotationType, TreeSet<Annotation>> annIndex = this.invRefIndex.get(ann);
	if (annIndex == null) return new ArrayList<Annotation>();
	TreeSet<Annotation> annotations = annIndex.get(type);
	return (annotations == null) ? new ArrayList<Annotation>() : new ArrayList<Annotation>(annotations);
    }
    
    List<Annotation> getAnnotationsBy(Annotation ann, Layer layer) {
	List<Annotation> annotations = new ArrayList<Annotation>();
	for (AnnotationType type : KAFDocument.LAYER_2_TYPES.get(layer)) {
	    annotations.addAll(this.getAnnotationsBy(ann, type));
	}
	return annotations;
    }

    List<Annotation> getAnnotationsBy(List<Annotation> anns, AnnotationType type) {
	Set<Annotation> annotations = new TreeSet<Annotation>();
	for (Annotation ann : anns) {
	    annotations.addAll(this.getAnnotationsBy(ann, type));
	}
	return new ArrayList<Annotation>(annotations);
    }

    List<Annotation> getAnnotationsBy(List<Annotation> anns, Layer layer) {
	Set<Annotation> annotations = new TreeSet<Annotation>();
	for (Annotation ann : anns) {
	    annotations.addAll(this.getAnnotationsBy(ann, layer));
	}
	return new ArrayList<Annotation>(annotations);
    }

    List<Annotation> getAnnotationsBySent(Integer sent, AnnotationType type) {
	Map<Integer, TreeSet<Annotation>> typeIndex = this.sentIndex.get(type);
	if (typeIndex == null) return new ArrayList<Annotation>();
	TreeSet<Annotation> annotations = typeIndex.get(sent);
	return (annotations == null) ? new ArrayList<Annotation>() : new ArrayList<Annotation>(annotations);
    }
    
    List<Annotation> getAnnotationsBySent(Integer sent, Layer layer) {
	Set<Annotation> annotations = new TreeSet<Annotation>();
	for (AnnotationType type : KAFDocument.LAYER_2_TYPES.get(layer)) {
	    annotations.addAll(this.getAnnotationsBySent(sent, type));
	}
	return new ArrayList<Annotation>(annotations);
    }
    
    List<Annotation> getAnnotationsByPara(Integer para, AnnotationType type) {
	Set<Annotation> paraAnnotations = new TreeSet<Annotation>();
	for (Integer sent : this.getSentsByPara(para)) {
	    paraAnnotations.addAll(this.getAnnotationsBySent(sent, type));
	}
	return new ArrayList<Annotation>(paraAnnotations);
    }
    
    List<Annotation> getAnnotationsByPara(Integer para, Layer layer) {
	Set<Annotation> annotations = new TreeSet<Annotation>();
	for (AnnotationType type : KAFDocument.LAYER_2_TYPES.get(layer)) {
	    annotations.addAll(this.getAnnotationsByPara(para, type));
	}
	return new ArrayList<Annotation>(annotations);
    }

    List<List<Annotation>> getAnnotationsBySentences(AnnotationType type) {
	List<List<Annotation>> annsBySents = new ArrayList<List<Annotation>>();
	for (Integer sent : this.getSentences()) {
	    annsBySents.add(this.getAnnotationsBySent(sent, type));
	}
	return annsBySents;
    }
    
    List<List<Annotation>> getAnnotationsBySentences(Layer layer) {
	List<List<Annotation>> annotations = new ArrayList<List<Annotation>>();
	for (Integer sent : this.getSentences()) {
	    Set<Annotation> sentAnnotations = new TreeSet<Annotation>();
	    for (AnnotationType type : KAFDocument.LAYER_2_TYPES.get(layer)) {
		sentAnnotations.addAll(this.getAnnotationsBySent(sent, type));
	    }
	    annotations.add(new ArrayList<Annotation>(sentAnnotations));
	}
	return annotations;
    }
    
    List<List<Annotation>> getAnnotationsByParagraphs(AnnotationType type) {
	List<List<Annotation>> annsByParas = new ArrayList<List<Annotation>>();
	for (Integer para : this.getParagraphs()) {
	    annsByParas.add(this.getAnnotationsByPara(para, type));
	}
	return annsByParas;
    }

    List<List<Annotation>> getAnnotationsByParagraphs(Layer layer) {
	List<List<Annotation>> annotations = new ArrayList<List<Annotation>>();
	for (Integer para : this.getParagraphs()) {
	    Set<Annotation> paraAnnotations = new TreeSet<Annotation>();
	    for (AnnotationType type : KAFDocument.LAYER_2_TYPES.get(layer)) {
		paraAnnotations.addAll(this.getAnnotationsByPara(para, type));
	    }
	    annotations.add(new ArrayList<Annotation>(paraAnnotations));
	}
	return annotations;
    }

    Set<Element> getUnknownLayers() {
	return this.unknownLayers;
    }
    
    List<Integer> getSentsByPara(Integer para) {
	if (!this.paraSentIndexUpToDate) {
	    this.updateParaSentIndex();
	}
	Set<Integer> sents = this.paraSentIndex.get(para);
	if (sents == null) return new ArrayList<Integer>();
	return new ArrayList<Integer>(sents);
    }

    Integer getNumSentences() {
	Set<Integer> sentences = this.sentIndex.get(AnnotationType.WF).keySet();
	return (sentences != null) ? sentences.size() : 0;
    }
    
    Integer getNumParagraphs() {
	if (!this.paraSentIndexUpToDate) {
	    this.updateParaSentIndex();
	}
	Set<Integer> paragraphs = this.paraSentIndex.keySet();
	return (paragraphs != null) ? paragraphs.size() : 0;
    }
    
    List<Integer> getSentences() {
	List<Integer> sentences = new ArrayList<Integer>(this.sentIndex.get(AnnotationType.WF).keySet());
	Collections.sort(sentences);
	return sentences;
    }
    
    List<Integer> getParagraphs() {
	if (!this.paraSentIndexUpToDate) {
	    this.updateParaSentIndex();
	}
	Set<Integer> paragraphs = this.paraSentIndex.keySet();
	return new ArrayList<Integer>(paragraphs);
    }
    
    Integer getFirstSentence() {
	List<Integer> sentences = this.getSentences();
	return (sentences.size() > 0) ? sentences.get(0) : null;
    }
    
    Integer getFirstParagraph() {
	List<Integer> paragraphs = this.getParagraphs();
	return (paragraphs.size() > 0) ? paragraphs.get(0) : null;
    }

    Integer getPosition(AnnotationType type, Annotation ann) {
	List<Annotation> annotations = this.annotations.get(type);
	return (annotations != null) ? annotations.indexOf(ann) : null;
    }

    
    void setRawText(String str) {
	rawText = str;
    }
    
    void add(Annotation ann, AnnotationType type) {
	this.addAnnotation(ann, type, null, false);
    }
    
    void addSorted(Annotation ann, AnnotationType type) {
	this.addAnnotation(ann, type, null, true);
    }

    void addAt(Annotation ann, AnnotationType type, Integer position) {
	this.addAnnotation(ann, type, position, false);
    }

    void addUnknownLayer(Element layer) {
	this.unknownLayers.add(layer);
    }

    void indexAnnotationReferences(AnnotationType sourceType, Annotation source, Annotation target) {
	this.indexAnnotationReference(sourceType, source, target);
	/* Index target's children recursively */
	for (Map.Entry<AnnotationType, List<Annotation>> entry : target.getReferencedAnnotations().entrySet()) {
	    for (Annotation targetChild : entry.getValue()) {
		this.indexAnnotationReference(sourceType, source, targetChild);
	    }
	}
    }

    void unindexAnnotationReferences(AnnotationType sourceType, Annotation source, Annotation target) {
	this.unindexAnnotationReference(sourceType, source, target);
	/* Unindex target's children recursively */
	for (Map.Entry<AnnotationType, List<Annotation>> entry : target.getReferencedAnnotations().entrySet()) {
	    for (Annotation targetChild : entry.getValue()) {
		this.unindexAnnotationReference(sourceType, source, targetChild);
	    }
	}
    }

    void updateAnnotationSent(Annotation ann, AnnotationType type, Integer oldSent, Integer newSent) {
	if (ann instanceof WF) {
	    Integer para = ((WF) ann).getPara();
	    this.unindexAnnotationBySent(ann, type, oldSent);
	    this.indexAnnotationBySent(ann, type, newSent);
	    this.unindexSentByPara(oldSent, para);
	    this.indexSentByPara(newSent, para);
	}
    }

    void updateAnnotationPara(Annotation ann, AnnotationType type, Integer oldPara, Integer newPara) {
	if (ann instanceof WF) {
	    Integer sent = ((SentenceLevelAnnotation) ann).getSent();
	    this.unindexSentByPara(sent, oldPara);
	    this.indexSentByPara(sent, newPara);
	}
    }
    
    void remove(Annotation ann, AnnotationType type) {
	List<Annotation> annotations = this.annotations.get(type);
	if (annotations != null) annotations.remove(ann);
	/* Unindex */
	if (ann instanceof SentenceLevelAnnotation) {
	    Integer sent = ((SentenceLevelAnnotation) ann).getSent();
	    Integer para = ((ParagraphLevelAnnotation) ann).getPara();
	    this.unindexAnnotationBySent(ann, type, sent);
	    this.unindexSentByPara(sent, para);
	}
    }

    void remove(AnnotationType type) {
	List<Annotation> annotations = this.annotations.get(type);
	while (!annotations.isEmpty()) {
	    this.remove(annotations.get(0), type);
	}
    }

    void remove(Layer layerName) {
	for (AnnotationType type : KAFDocument.LAYER_2_TYPES.get(layerName)) {
	    this.remove(type);
	}
    }

    
    /* Helper methods */
    
    private void addAnnotation(Annotation ann, AnnotationType type, Integer position, Boolean sorted) {
	/* Add to the container */
	List<Annotation> annotations = this.annotations.get(type);
	if (annotations == null) {
	    annotations = new ArrayList<Annotation>();
	    this.annotations.put(type, annotations);
	}
	if (((position == null) || (position > annotations.size())) && (!sorted)) {
	    annotations.add(ann);
	} else if (position != null) {
	    annotations.add(position, ann);
	} else { // sorted
	    ListIterator<Annotation> iter = annotations.listIterator();
	    while (iter.hasNext()) {
		Annotation nextAnn = iter.next();
		if (nextAnn.getOffset() > ann.getOffset()) {
		    if (iter.hasPrevious()) {
			iter.previous();
			iter.add(ann);
		    } else {
			annotations.add(0, ann);
		    }
		    break;
		}
	    }
	    if (!iter.hasNext()) {
		annotations.add(ann);
	    }
	}
	/* Index */
	this.indexAnnotation(ann, type);
    }
    
    private void indexAnnotation(Annotation ann, AnnotationType type) {
	/* Inverse references index*/
	Map<AnnotationType, List<Annotation>> invReferences = ann.getReferencedAnnotations();
	Iterator<Map.Entry<AnnotationType, List<Annotation>>> it = invReferences.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry<AnnotationType, List<Annotation>> pair = it.next();
	    for (Annotation target : pair.getValue()) {
		this.indexAnnotationReferences(type, ann, target);
	    }
	}
	/* Index annotation by sentence */
	if (ann instanceof SentenceLevelAnnotation) {
	    Integer sent = ((SentenceLevelAnnotation) ann).getSent();
	    this.indexAnnotationBySent(ann, type, sent);
	    /* Index sentence by paragraph */
	    if (ann instanceof WF) {
		Integer para = ((SentenceLevelAnnotation) ann).getPara();
		this.indexSentByPara(sent, para);
	    }
	}
    }
    
    private void indexAnnotationReference(AnnotationType sourceType, Annotation source, Annotation target) {
	Map<AnnotationType, TreeSet<Annotation>> annotationRefs= this.invRefIndex.get(target);
	if (annotationRefs == null) {
	    annotationRefs = new HashMap<AnnotationType, TreeSet<Annotation>>();
	    this.invRefIndex.put(target, annotationRefs);
	}
	TreeSet<Annotation> typeRefs = annotationRefs.get(sourceType);
	if (typeRefs == null) {
	    typeRefs = new TreeSet<Annotation>();
	    annotationRefs.put(sourceType, typeRefs);
	}
	typeRefs.add(source);
    }
    
    private void unindexAnnotationReference(AnnotationType sourceType, Annotation source, Annotation target) {
	Map<AnnotationType, TreeSet<Annotation>> annotationRefs = this.invRefIndex.get(target);
	if (annotationRefs == null) return;
	TreeSet<Annotation> typeRefs = annotationRefs.get(sourceType);
	if (typeRefs == null) return;
	typeRefs.remove(source);
	if (typeRefs.isEmpty()) {
	    annotationRefs.remove(sourceType);
	    if (annotationRefs.isEmpty()) {
		this.invRefIndex.remove(target);
	    }
	}
    }
    
    private void indexAnnotationBySent(Annotation ann, AnnotationType type, Integer sent) {
	if (!(ann instanceof SentenceLevelAnnotation)) return;
	this.addAnnotationToSentIndex(ann, type, sent);
	/* Index inversely referenced annotations */
	Map<AnnotationType, TreeSet<Annotation>> referencedAnnotations = this.invRefIndex.get(ann);
	if (referencedAnnotations != null) {
	    for (Map.Entry<AnnotationType, TreeSet<Annotation>> entry : referencedAnnotations.entrySet()) {
		AnnotationType currentType = entry.getKey();
		for (Annotation currentAnn : entry.getValue()) {
		    this.addAnnotationToSentIndex(currentAnn, currentType, sent);
		}
	    }
	}
    }
    
    private void addAnnotationToSentIndex(Annotation ann, AnnotationType type, Integer sent) {
	if (ann.getOffset() != null) { // Do not index annotations with no offset defined (those not related to any WFs)
	    if (sent != null) {
		/* Add annotation to sentence index */
		Map<Integer, TreeSet<Annotation>> typeIndex = this.sentIndex.get(type);
		if (typeIndex == null) {
		    typeIndex = new HashMap<Integer, TreeSet<Annotation>>();
		    this.sentIndex.put(type, typeIndex);
		}
		TreeSet<Annotation> annotations = typeIndex.get(sent);
		if (annotations == null) {
		    annotations = new TreeSet<Annotation>();
		    typeIndex.put(sent, annotations);
		}
		if (!annotations.contains(ann)) {
		    annotations.add(ann);
		}
	    }
	}
    }

    private void unindexAnnotationBySent(Annotation ann, AnnotationType type, Integer sent) {
	if (!(ann instanceof SentenceLevelAnnotation)) return;
	this.removeAnnotationFromSentIndex(ann, type, sent);
	/* Unindex inversely referenced annotations */
	Map<AnnotationType, TreeSet<Annotation>> referencedAnnotations = this.invRefIndex.get(ann);
	if (referencedAnnotations != null) {
	    for (Map.Entry<AnnotationType, TreeSet<Annotation>> entry : referencedAnnotations.entrySet()) {
		AnnotationType currentType = entry.getKey();
		for (Annotation currentAnn : entry.getValue()) {
		    this.removeAnnotationFromSentIndex(currentAnn, currentType, sent);
		}
	    }
	}
    }
    
    private void removeAnnotationFromSentIndex(Annotation ann, AnnotationType type, Integer sent) {
	if (sent != null) {
	    /* Remove annotation from sentence index */
	    Map<Integer, TreeSet<Annotation>> typeIndex = this.sentIndex.get(type);
	    if (typeIndex != null) {
		TreeSet<Annotation> annotations = typeIndex.get(sent);
		if (annotations != null) {
		    annotations.remove(ann);
		    if (annotations.isEmpty()) {
			typeIndex.remove(sent);
			if (typeIndex.isEmpty()) {
			    this.sentIndex.remove(type);
			}
		    }
		}
	    }
	}
    }
    
    private void indexSentByPara(Integer sent, Integer para) {
	if ((sent != null) && (para != null)) {
	    Map<Integer, Integer> numAnnotationsSent = this.paraSentIndexInfo.get(sent);
	    if (numAnnotationsSent == null) {
		numAnnotationsSent = new HashMap<Integer, Integer>();
		this.paraSentIndexInfo.put(sent, numAnnotationsSent);
	    }
	    Integer numAnnotations = numAnnotationsSent.get(para);
	    if (numAnnotations == null) {
		numAnnotations = 0;
	    }
	    numAnnotations++;
	    numAnnotationsSent.put(para, numAnnotations);
	    this.paraSentIndexUpToDate = false;
	}
    }
    
    private void unindexSentByPara(Integer sent, Integer para) {
	if ((sent != null) && (para != null)) {
	    Map<Integer, Integer> numAnnotationsSent = this.paraSentIndexInfo.get(sent);
	    if (numAnnotationsSent != null) {
		Integer numAnnotations = numAnnotationsSent.get(para);
		if (numAnnotations != null) {
		    numAnnotations--;
		    numAnnotationsSent.put(para, numAnnotations);
		    if (numAnnotations <= 0) {
			numAnnotationsSent.remove(para);
			this.paraSentIndexUpToDate = false;
			if (numAnnotationsSent.isEmpty()) {
			    this.paraSentIndexInfo.remove(sent);
			}
		    }
		}
	    }
	}
    }

    private void updateParaSentIndex() {
	this.paraSentIndex.clear();
	for (Map.Entry<Integer, Map<Integer, Integer>> sentInfo : this.paraSentIndexInfo.entrySet()) {
	    Integer sent = sentInfo.getKey();
	    Integer maxAnnotationsParagraph = null;
	    Integer maxAnnotations = 0;
	    for (Map.Entry<Integer, Integer> paraInfo : sentInfo.getValue().entrySet()) {
		Integer para = paraInfo.getKey();
		Integer numAnnotations = paraInfo.getValue();
		if (numAnnotations >= maxAnnotations) {
		    maxAnnotationsParagraph = para;
		    maxAnnotations= numAnnotations;
		}
	    }
	    if (maxAnnotationsParagraph != null) {
		TreeSet<Integer> paraSents = this.paraSentIndex.get(maxAnnotationsParagraph);
		if (paraSents == null) {
		    paraSents = new TreeSet<Integer>();
		    this.paraSentIndex.put(maxAnnotationsParagraph, paraSents);
		}
		paraSents.add(sent);
	    }
	}
	this.paraSentIndexUpToDate = true;
    }

    
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof AnnotationContainer)) return false;
	AnnotationContainer ac = (AnnotationContainer) o;
	return Utils.areEquals(this.layers, ac.layers) &&
		Utils.areEquals(this.rawText, ac.rawText) &&
		Utils.areEquals(this.unknownLayers, ac.unknownLayers);
    }
    */
    
}

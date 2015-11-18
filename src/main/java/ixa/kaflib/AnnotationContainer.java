package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Layer;
import ixa.kaflib.KAFDocument.Utils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;

import org.jdom2.Element;


/** A container to keep all annotations of a document (word forms, terms, dependencies, chunks, entities and coreferences). There are different hash maps to index annotations by different properties as ID, sentence... It enables to retrieve annotations by different properties in an effective way. Performance is very important. */
class AnnotationContainer implements Serializable {

    /* Annotation containers */
    private String rawText;
    private Map<Layer, List<Annotation>> layers; /* (Layer => Annotations) */
    private Map<AnnotationType, List<Annotation>> annotations; /* (AnnotationType => Annotations) */
    private Set<Element> unknownLayers;
    
    /* Indices */
    private Map<Annotation, Map<AnnotationType, List<Annotation>>> invRefIndex; /* (Annotation => (AnnotationType => Annotations)) */
    private Map<AnnotationType, Map<Integer, List<Annotation>>> sentIndex; /* (AnnotationType => (Sentence => Annotations)) */
    private Map<AnnotationType, Map<Integer, List<Annotation>>> paraIndex; /* (AnnotationType => (Paragraph => Annotations)) */
    private Map<Integer, Set<Integer>> paraSentIndex; /* Para => List<Sent> */
    private Set<Integer> indexedSents; /* Used to keep count of which sentences have already been indexed by paragraphs
    					(to avoid repeating the same sentence in different paragraphs, due to tokenizer bugs */
    
    private Integer creationOrderId = 0;


    /** This creates a new AnnotationContainer object */
    AnnotationContainer() {
	rawText = new String();
	layers = new HashMap<Layer, List<Annotation>>();
	annotations = new HashMap<AnnotationType, List<Annotation>>();
	unknownLayers = new HashSet<Element>();
	invRefIndex = new HashMap<Annotation, Map<AnnotationType, List<Annotation>>>();
	sentIndex = new HashMap<AnnotationType, Map<Integer, List<Annotation>>>();
	paraIndex = new HashMap<AnnotationType, Map<Integer, List<Annotation>>>();
	paraSentIndex = new HashMap<Integer, Set<Integer>>();
	indexedSents = new HashSet<Integer>();
    }

    Integer getNextCreationOrderId() {
	return this.creationOrderId++;
    }
    
    /** Returns raw text */
    String getRawText() {
	return rawText;
    }

    List<Annotation> getAnnotations(Layer layer) {
	List<Annotation> annotations = this.layers.get(layer);
	return (annotations == null) ? new ArrayList<Annotation>() : annotations;
    }

    List<Annotation> getAnnotations(AnnotationType type) {
	List<Annotation> annotations = this.annotations.get(type);
	return (annotations == null) ? new ArrayList<Annotation>() : annotations;
    }

    List<Annotation> getInverse(Annotation ann, AnnotationType type) {
	Map<AnnotationType, List<Annotation>> annIndex = this.invRefIndex.get(ann);
	if (annIndex == null) return new ArrayList<Annotation>();
	List<Annotation> annotations = annIndex.get(type);
	return (annotations == null) ? new ArrayList<Annotation>() : annotations;
    }

    List<Annotation> getInverse(List<Annotation> anns, AnnotationType type) {
	List<Annotation> result = new ArrayList<Annotation>();
	for (Annotation ann : anns) {
	    result.addAll(this.getInverse(ann, type));
	}
	return result;
    }

    /** Returns all unknown layers as a DOM Element list */
    Set<Element> getUnknownLayers() {
	return unknownLayers;
    }
    
    
    void setRawText(String str) {
	rawText = str;
    }
    
    void append(Annotation ann, Layer layer, AnnotationType type) {
	this.addAnnotation(ann, layer, type, null, false);
    }
    
    void addSorted(Annotation ann, Layer layer, AnnotationType type) {
	this.addAnnotation(ann, layer, type, null, true);
    }

    void addAt(Annotation ann, Layer layer, AnnotationType type, Integer position) {
	this.addAnnotation(ann, layer, type, position, false);
    }

    /** Adds an unknown layer to the container in DOM format */
    void add(Element layer) {
	this.unknownLayers.add(layer);
    }
    
    
    private void indexAnnotation(Annotation ann, AnnotationType type) {
	/* Inverse references index*/
	Map<AnnotationType, List<Annotation>> invReferences = ann.getReferencedAnnotations();
	Iterator<Map.Entry<AnnotationType, List<Annotation>>> it = invReferences.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry<AnnotationType, List<Annotation>> pair = it.next();
	    for (Annotation ref : pair.getValue()) {
		Helper.addInvReference(ann, ref, type, this.invRefIndex);
	    }
	}
	/* Sentence and paragraph index */
	this.indexAnnotationParaSent(ann, type);
    }
    
    private void indexAnnotationParaSent(Annotation ann, AnnotationType type) {
	if (ann.getOffset() != null) { // Do not index annotations with no offset defined (those not related to any WFs)
	    if (ann instanceof SentenceLevelAnnotation) {
		Integer sent = ((SentenceLevelAnnotation) ann).getSent();
		Integer para = ((ParagraphLevelAnnotation) ann).getPara();
		if (sent != null) {
		    Helper.addToIndex(ann, type, sent, this.sentIndex);
		}
		if (para != null) {
		    Helper.addToIndex(ann, type, para, this.paraIndex);
		    if (!indexedSents.contains(sent)) {
			this.addSentToPara(sent, para);
			indexedSents.add(sent);
		    }
		}
	    }
	    else if (ann instanceof ParagraphLevelAnnotation) {
		Integer para = ((ParagraphLevelAnnotation) ann).getPara();
		if (para != null) {
		    Helper.addToIndex(ann, type, para, this.paraIndex);
		}
	    }
	}
    }
    
    void reindexAnnotationParaSent(Annotation ann, AnnotationType type, Integer oldSent, Integer oldPara) {
	/* Remove index */
	if (oldSent != null) {
	    Helper.removeFromIndex(ann, type, oldSent, this.sentIndex);
	}
	if (oldPara != null) {
	    Helper.removeFromIndex(ann, type, oldPara, this.paraIndex);
	}
	/* Re-index */
	this.indexAnnotationParaSent(ann, type);
	/* Re-index related annotations */
	/*
	for (Layer relatedLayer : Layer.values()) {
	    for (Annotation relatedAnn : this.getInverse(ann, relatedLayer)) {
		System.out.println("SARTZERA");
		this.reindexAnnotationParaSent(relatedAnn, relatedLayer, oldSent, oldPara);
	    }
	}
	*/
    }

    void remove(Annotation ann, Layer layer, AnnotationType type) {
	Helper.remove(ann, layer, this.layers);
	Helper.remove(ann, type, this.annotations);
	if (ann instanceof SentenceLevelAnnotation) {
	    Integer sent = ((SentenceLevelAnnotation) ann).getSent();
	    List<Annotation> sentAnnotations = this.getSentAnnotations(sent, type);
	    sentAnnotations.remove(ann);
	    if (ann instanceof ParagraphLevelAnnotation) {
		Integer para = ((ParagraphLevelAnnotation) ann).getPara();
		List<Annotation> paraAnnotations = this.getParaAnnotations(para, type);
		paraAnnotations.remove(ann);
	    }
	}
    }

    void removeLayer(Layer layerName) {
	this.layers.remove(layerName);
    }

    /**
     * Returns all sentences in a paragraph.
     * @param para The paragraph
     * @return
     */
    List<Integer> getParaSents(Integer para) {
	Set<Integer> sents = this.paraSentIndex.get(para);
	if (sents == null) return new ArrayList<Integer>();
	List<Integer> sentList = new ArrayList<Integer>(sents);
	Collections.sort(sentList);
	return sentList;
    }

    List<Annotation> getSentAnnotations(Integer sent, AnnotationType type) {
	return Helper.getIndexedAnnotations(type, sent, this.sentIndex);
    }

    List<Annotation> getParaAnnotations(Integer para, AnnotationType type) {
	return Helper.getIndexedAnnotations(type, para, this.paraIndex);
    }
    
    Integer getNumSentences() {
	if (this.sentIndex.get(AnnotationType.WF) == null) return 0;
	return this.sentIndex.get(AnnotationType.WF).size();
    }
    
    Integer getNumParagraphs() {
	if (this.paraIndex.get(AnnotationType.WF) == null) return 0;
	return this.paraIndex.get(AnnotationType.WF).size();
    }
    
    Integer getFirstSentence() {
	if (this.sentIndex.get(AnnotationType.WF) != null) {
	    if (this.sentIndex.get(AnnotationType.WF) != null) {
		return Helper.getMinKey(this.sentIndex.get(AnnotationType.WF));
	    }
	}
	return null;
    }
    
    Integer getFirstParagraph() {
	if (this.paraIndex.get(AnnotationType.WF) != null) {
	    if (this.paraIndex.get(AnnotationType.WF) != null) {
		return Helper.getMinKey(this.paraIndex.get(AnnotationType.WF));
	    }
	}
	return null;
    }
    
    /** Return all annotations of type "type" classified into sentences */
    List<List<Annotation>> getSentences(AnnotationType type) {
	List<List<Annotation>> sentences = new ArrayList<List<Annotation>>();
	for (int sent : Helper.getIndexKeys(type, this.sentIndex)) {
	    sentences.add(this.getSentAnnotations(sent, type));
	}
	return sentences;
    }

    /** Return all annotations of type "type" classified into paragraphs */
    List<List<Annotation>> getParagraphs(AnnotationType type) {
	List<List<Annotation>> paragraphs = new ArrayList<List<Annotation>>();
	for (int para : Helper.getIndexKeys(type, this.paraIndex)) {
	    paragraphs.add(this.getParaAnnotations(para, type));
	}
	return paragraphs;
    }

    Integer getPosition(Layer layer, Annotation ann) {
	return this.layers.get(layer).indexOf(ann);
    }
    
    void addSentToPara(Integer sent, Integer para) {
	Set<Integer> paraSents = this.paraSentIndex.get(para);
	if (paraSents == null) {
	    paraSents = new HashSet<Integer>();
	    this.paraSentIndex.put(para, paraSents);
	}
	paraSents.add(sent);
    }
    
    
    /* Helper methods */
    
    private void addAnnotation(Annotation ann, Layer layer, AnnotationType type, Integer position, Boolean sorted) {
	Helper.addAnnotation(ann, layer, position, sorted, this.layers);
	Helper.addAnnotation(ann, type, position, sorted, this.annotations);
	/* Index */
	this.indexAnnotation(ann, type);
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

    
    private static class Helper {
	
	static <T> void addToIndex(Annotation ann, T type, Integer key, Map<T, Map<Integer, List<Annotation>>> index) {
	    if (key != null) {
		Map<Integer, List<Annotation>> typeIndex = index.get(type);
		if (typeIndex == null) {
		    typeIndex = new HashMap<Integer, List<Annotation>>();
		    index.put(type, typeIndex);
		}
		List<Annotation> annotations = typeIndex.get(key);
		if (annotations == null) {
		    annotations = new ArrayList<Annotation>();
		    typeIndex.put(key, annotations);
		}
		annotations.add(ann);
		Collections.sort(annotations);
	    }
	}
	
	static <T> void removeFromIndex(Annotation ann, T type, Integer key, Map<T, Map<Integer, List<Annotation>>> index) {
	    if (key != null) {
		Map<Integer, List<Annotation>> typeIndex = index.get(type);
		if (typeIndex != null) {
		    List<Annotation> annotations = typeIndex.get(key);
		    if (annotations != null) {
			annotations.remove(ann);
			if (annotations.isEmpty()) {
			    typeIndex.remove(key);
			    if (typeIndex.isEmpty()) {
				index.remove(type);
			    }
			}
		    }

		}
	    }
	}
	
	static <T> void addAnnotation(Annotation ann, T type, Integer position, Boolean sorted, Map<T, List<Annotation>> container) {
	    List<Annotation> annotations = container.get(type);
	    if (annotations == null) {
		annotations = new ArrayList<Annotation>();
		container.put(type, annotations);
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
	}

	static <T> List<Annotation> getIndexedAnnotations(T type, Integer key, Map<T, Map<Integer, List<Annotation>>> index) {
	    Map<Integer, List<Annotation>> typeIndex = index.get(type);
	    if (typeIndex == null) return new ArrayList<Annotation>();
	    List<Annotation> annotations = typeIndex.get(key);
	    return (annotations == null) ? new ArrayList<Annotation>() : annotations;
	}
	
	static <T> List<Integer> getIndexKeys(T type, Map<T, Map<Integer, List<Annotation>>> index) {
	    Map<Integer, List<Annotation>> typeIndex = index.get(type);
	    if (typeIndex == null) return new ArrayList<Integer>();
	    List<Integer> keys= new ArrayList<Integer>(typeIndex.keySet());
	    Collections.sort(keys);
	    return keys;
	}
	
	static <T> void addInvReference(Annotation src, Annotation ref, T type, Map<Annotation, Map<T, List<Annotation>>> index) {
	    Map<T, List<Annotation>> annIndex = index.get(ref);
	    if (annIndex == null) {
		annIndex = new HashMap<T, List<Annotation>>();
		index.put(ref, annIndex);
	    }
	    List<Annotation> refAnnotations = annIndex.get(type);
	    if (refAnnotations == null) {
		refAnnotations = new ArrayList<Annotation>();
		annIndex.put(type, refAnnotations);
	    }
	    refAnnotations.add(src);
	}
	
	static <T> List<Annotation> getInvReferences(Annotation ann, T type, Map<Annotation, Map<T, List<Annotation>>> index) {
	    Map<T, List<Annotation>> annIndex = index.get(ann);
	    if (annIndex == null) return new ArrayList<Annotation>();
	    List<Annotation> annotations = annIndex.get(type);
	    return (annotations == null) ? new ArrayList<Annotation>() : annotations;
	}
	
	static <T> List<Annotation> getInvReferences(Annotation ann, Map<Annotation, Map<T, List<Annotation>>> index) {
	    List<Annotation> annotations = new ArrayList<Annotation>();
	    Map<T, List<Annotation>> annIndex = index.get(ann);
	    if (annIndex == null) return new ArrayList<Annotation>();
	    for (T type: annIndex.keySet()) {
		annotations.addAll(annIndex.get(type));
	    }
	    return annotations;
	}

	static <T> List<Annotation> get(T type, Map<T, List<Annotation>> container) {
	    List<Annotation> annotations = container.get(type);
	    return (annotations != null) ? annotations : new ArrayList<Annotation>();
	}

	static <T> void remove(Annotation ann, T type, Map<T, List<Annotation>> container) {
	    List<Annotation> annotations = container.get(type);
	    if (annotations != null) annotations.remove(ann);
	}
	
	static <T> Integer getMinKey(Map<Integer, T> map) {
	    if (map.isEmpty()) return null;
	    Set<Integer> keys = map.keySet();
	    Integer min = Integer.MAX_VALUE;
	    for (Integer key : keys) {
		if (key < min) {
		    min = key;
		}
	    }
	    return min;
	}
    }
    
}

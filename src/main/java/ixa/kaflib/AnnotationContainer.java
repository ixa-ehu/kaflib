package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Layer;
import ixa.kaflib.KAFDocument.Utils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
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

    /* Annotation layers */
    private String rawText;
    private Map<Layer, Map<String, List<Annotation>>> layers; /* (Layer => (Group => Annotations)) */
    private Map<AnnotationType, Map<String, List<Annotation>>> annotations; /* (AnnotationType => (Group => Annotations)) */
    private Set<Element> unknownLayers;
    
    /* Indices */
    private Map<Annotation, Map<AnnotationType, List<Annotation>>> invRefIndex; /* (Annotation => (AnnotationType => Annotations)) */
    private Map<AnnotationType, Map<String, Map<Integer, List<Annotation>>> > sentIndex; /* (AnnotationType => (Group => (Sentence => Annotations))) */
    private Map<AnnotationType, Map<String, Map<Integer, List<Annotation>>> > paraIndex; /* (AnnotationType => (Group => (Paragraph => Annotations))) */
    private Map<Integer, Set<Integer>> paraSentIndex; /* Para => List<Sent> */
    private Set<Integer> indexedSents; /* Used to keep count of which sentences have already been indexed by paragraphs
    					(to avoid repeating the same sentence in different paragraphs, due to tokenizer bugs */
    
    static final String DEFAULT_GROUP = "kaflib_default_group";


    /** This creates a new AnnotationContainer object */
    AnnotationContainer() {
	rawText = new String();
	layers = new HashMap<Layer, Map<String, List<Annotation>>>();
	annotations = new HashMap<AnnotationType, Map<String, List<Annotation>>>();
	unknownLayers = new HashSet<Element>();
	invRefIndex = new HashMap<Annotation, Map<AnnotationType, List<Annotation>>>();
	sentIndex = new HashMap<AnnotationType, Map<String, Map<Integer, List<Annotation>>>>();
	paraIndex = new HashMap<AnnotationType, Map<String, Map<Integer, List<Annotation>>>>();
	paraSentIndex = new HashMap<Integer, Set<Integer>>();
	indexedSents = new HashSet<Integer>();
    }

    
    /** Returns raw text */
    String getRawText() {
	return rawText;
    }

    List<Annotation> getLayer(Layer layer) {
	return Helper.get(layer, this.layers);
    }
    
    List<Annotation> getAnnotations(AnnotationType type) {
	return Helper.get(type, this.annotations);
    }

    List<Annotation> getLayer(Layer layer, String group) {
	return Helper.get(layer, group, this.layers);
    }
    
    List<Annotation> getAnnotations(AnnotationType type, String group) {
	return Helper.get(type, group, this.annotations);
    }

    List<Annotation> getInverse(Annotation ann) {
	return Helper.getInvReferences(ann, this.invRefIndex);
    }
    
    List<Annotation> getInverse(Annotation ann, AnnotationType type) {
	return Helper.getInvReferences(ann, type, this.invRefIndex);
    }
    
    List<Annotation> getInverse(List<Annotation> anns, AnnotationType type) {
	List<Annotation> result = new ArrayList<Annotation>();
	for (Annotation ann : anns) {
	    result.addAll(Helper.getInvReferences(ann, type, this.invRefIndex));
	}
	return result;
    }
    
    List<String> getGroupIDs(AnnotationType type) {
	return Helper.getGroupIDs(type, this.annotations);
    }

    /** Returns all unknown layers as a DOM Element list */
    Set<Element> getUnknownLayers() {
	return unknownLayers;
    }
    
    void setRawText(String str) {
	rawText = str;
    }
    
    void add(Annotation ann, Layer layer, AnnotationType type) {
	this.add(ann, layer, type, null);
    }
    
    void add(Annotation ann, Layer layer, AnnotationType type, Integer position) {
	Helper.addAnnotation(ann, layer, getGroupID(ann), position, this.layers);
	Helper.addAnnotation(ann, type, getGroupID(ann), position, this.annotations);
	/* Index */
	this.indexAnnotation(ann, type);
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
	String groupID = getGroupID(ann);
	if (ann instanceof SentenceLevelAnnotation) {
	    Integer sent = ((SentenceLevelAnnotation) ann).getSent();
	    Integer para = ((ParagraphLevelAnnotation) ann).getPara();
	    Helper.addToIndex(ann, type, groupID, sent, this.sentIndex);
	    if (para > 0) {
		Helper.addToIndex(ann, type, groupID, para, this.paraIndex);
		if (!indexedSents.contains(sent)) {
		    this.addSentToPara(sent, para);
		    indexedSents.add(sent);
		}
	    }
	}
	else if (ann instanceof ParagraphLevelAnnotation) {
	    Integer para = ((ParagraphLevelAnnotation) ann).getPara();
	    if (para > 0) {
		Helper.addToIndex(ann, type, groupID, para, this.paraIndex);
	    }
	}
    }
    
    void reindexAnnotationParaSent(Annotation ann, AnnotationType type, Integer oldSent, Integer oldPara) {
	String groupID = getGroupID(ann);
	/* Remove index */
	Helper.removeFromIndex(ann, type, groupID, oldSent, this.sentIndex);
	Helper.removeFromIndex(ann, type, groupID, oldPara, this.paraIndex);
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
	this.remove(ann, layer, type, DEFAULT_GROUP);
    }

    void remove(Annotation ann, Layer layer, AnnotationType type, String group) {
	Helper.remove(ann, layer, this.layers);
	Helper.remove(ann, type, this.annotations);
	if (ann instanceof SentenceLevelAnnotation) {
	    String groupID = getGroupID(ann);
	    Integer sent = ((SentenceLevelAnnotation) ann).getSent();
	    List<Annotation> sentAnnotations = this.getSentAnnotations(sent, type, groupID);
	    sentAnnotations.remove(ann);
	    if (ann instanceof ParagraphLevelAnnotation) {
		Integer para = ((ParagraphLevelAnnotation) ann).getPara();
		List<Annotation> paraAnnotations = this.getParaAnnotations(para, type, groupID);
		paraAnnotations.remove(ann);
	    }
	}
    }
    
    void removeLayer(Layer layerName) {
	this.layers.remove(layerName);
    }
    
    void removeLayer(Layer layerName, String groupID) {
	Map<String, List<Annotation>> layer = this.layers.get(layerName);
	if (layer != null) {
	    layer.remove(groupID);
	}
    }

    /**
     * Returns all sentences in a paragraph.
     * @param para The paragraph
     * @return
     */
    List<Integer> getParaSents(Integer para) {
	List<Integer> sentList = new ArrayList<Integer>(this.paraSentIndex.get(para));
	Collections.sort(sentList);
	return sentList;
    }

    List<Annotation> getSentAnnotations(Integer sent, AnnotationType type) {
	return this.getSentAnnotations(sent, type, DEFAULT_GROUP);
    }
    
    List<Annotation> getSentAnnotations(Integer sent, AnnotationType type, String groupID) {
	return Helper.getIndexedAnnotations(type, groupID, sent, this.sentIndex);
    }

    List<Annotation> getParaAnnotations(Integer para, AnnotationType type) {
	return this.getParaAnnotations(para, type, DEFAULT_GROUP);
    }
    
    List<Annotation> getParaAnnotations(Integer para, AnnotationType type, String groupID) {
	return Helper.getIndexedAnnotations(type, groupID, para, this.paraIndex);
    }
    
    Integer getNumSentences() {
	return this.sentIndex.size();
    }
    
    Integer getNumParagraphs() {
	if (this.paraIndex.get(AnnotationType.WF) == null) return 0;
	return this.paraIndex.get(AnnotationType.WF).get(DEFAULT_GROUP).size();
    }
    
    /** Returns all tokens classified into sentences */
    List<List<Annotation>> getSentences(AnnotationType type) {
	return this.getSentences(type, DEFAULT_GROUP);
    }
    
    /** Return all annotations of type "type" classified into sentences */
    List<List<Annotation>> getSentences(AnnotationType type, String groupID) {
	List<List<Annotation>> sentences = new ArrayList<List<Annotation>>();
	for (int sent : Helper.getIndexKeys(type, groupID, this.sentIndex)) {
	    sentences.add(this.getSentAnnotations(sent, type));
	}
	return sentences;
    }
    
    /** Returns all tokens classified into paragraphs */
    List<List<Annotation>> getParagraphs(AnnotationType type) {
	return this.getParagraphs(type, DEFAULT_GROUP);
    }
    
    /** Return all annotations of type "type" classified into paragraphs */
    List<List<Annotation>> getParagraphs(AnnotationType type, String groupID) {
	List<List<Annotation>> paragraphs = new ArrayList<List<Annotation>>();
	for (int para : Helper.getIndexKeys(type, groupID, this.paraIndex)) {
	    paragraphs.add(this.getParaAnnotations(para, type));
	}
	return paragraphs;
    }

    
    private static String getGroupID(Annotation ann) {
	return (ann instanceof MultiLayerAnnotation) ? ((MultiLayerAnnotation) ann).getGroupID() : DEFAULT_GROUP;
    }
    
    Integer getPosition(Layer layer, Annotation ann) {
	return this.layers.get(layer).get(DEFAULT_GROUP).indexOf(ann);
    }
    
    void addSentToPara(Integer sent, Integer para) {
	Set<Integer> paraSents = this.paraSentIndex.get(para);
	if (paraSents == null) {
	    paraSents = new HashSet<Integer>();
	    this.paraSentIndex.put(para, paraSents);
	}
	paraSents.add(sent);
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
	
	static <T> void addToIndex(Annotation ann, T type, String groupID, Integer key, Map<T, Map<String, Map<Integer, List<Annotation>>>> index) {
	    if (key != null) {
		Map<String, Map<Integer, List<Annotation>> > typeIndex = index.get(type);
		if (typeIndex == null) {
		    typeIndex = new HashMap<String, Map<Integer, List<Annotation>> >();
		    index.put(type, typeIndex);
		}
		Map<Integer, List<Annotation>> groupIndex = typeIndex.get(groupID);
		if (groupIndex == null) {
		    groupIndex = new HashMap<Integer, List<Annotation>>();
		    typeIndex.put(groupID, groupIndex);
		}
		List<Annotation> annotations = groupIndex.get(key);
		if (annotations == null) {
		    annotations = new ArrayList<Annotation>();
		    groupIndex.put(key, annotations);
		}
		annotations.add(ann);
	    }
	}
	
	static <T> void removeFromIndex(Annotation ann, T type, String groupID, Integer key, Map<T, Map<String, Map<Integer, List<Annotation>>>> index) {
	    if (key != null) {
		Map<String, Map<Integer, List<Annotation>> > typeIndex = index.get(type);
		if (typeIndex != null) {
		    Map<Integer, List<Annotation>> groupIndex = typeIndex.get(groupID);
		    if (groupIndex != null) {
			List<Annotation> annotations = groupIndex.get(key);
			if (annotations != null) {
			    annotations.remove(ann);
			}
		    }
		}
	    }
	}
	
	static <T> void addAnnotation(Annotation ann, T type, String groupID, Integer position, Map<T, Map<String, List<Annotation>>> container) {
	    Map<String, List<Annotation>> typeGroups = container.get(type);
	    if (typeGroups == null) {
		typeGroups = new HashMap<String, List<Annotation>>();
		container.put(type, typeGroups);
	    }
	    List<Annotation> annotations = typeGroups.get(groupID);
	    if (annotations == null) {
		annotations = new ArrayList<Annotation>();
		typeGroups.put(groupID, annotations);
	    }
	    if ((position == null) || (position > annotations.size())) {
		annotations.add(ann);
	    } else {
		annotations.add(position, ann);
	    }
	}

	static <T> List<Annotation> getIndexedAnnotations(T type, String groupID, Integer key, Map<T, Map<String, Map<Integer, List<Annotation>>>> index) {
	    Map<String, Map<Integer, List<Annotation>> > typeIndex = index.get(type);
	    if (typeIndex == null) return new ArrayList<Annotation>();
	    Map<Integer, List<Annotation>> groupIndex = typeIndex.get(groupID);
	    if (groupIndex == null) return new ArrayList<Annotation>();
	    List<Annotation> annotations = groupIndex.get(key);
	    return (annotations == null) ? new ArrayList<Annotation>() : annotations;
	}
	
	static <T> List<Integer> getIndexKeys(T type, String groupID, Map<T, Map<String, Map<Integer, List<Annotation>>>> index) {
	    Map<String, Map<Integer, List<Annotation>>> typeIndex = index.get(type);
	    if (typeIndex != null) {
		Map<Integer, List<Annotation>> groupIndex = typeIndex.get(groupID);
		if (groupIndex != null) {
		    List<Integer> keys= new ArrayList<Integer>(groupIndex.keySet());
		    Collections.sort(keys);
		    return keys;
		}
	    }
	    return new ArrayList<Integer>();
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

	static <T> List<Annotation> get(T type, Map<T, Map<String, List<Annotation>>> container) {
	    List<Annotation> annotations = new ArrayList<Annotation>();
	    for (String group : Helper.getGroupIDs(type, container)) {
		annotations.addAll(Helper.get(type, group, container));
	    }
	    return annotations;
	}

	static <T> List<Annotation> get(T type, String group, Map<T, Map<String, List<Annotation>>> container) {
	    Map<String, List<Annotation>> groups = container.get(type);
	    if (groups == null) return new ArrayList<Annotation>();
	    List<Annotation> annotations = groups.get(group);
	    return (annotations == null) ? new ArrayList<Annotation>() : annotations;
	}

	static <T> List<String> getGroupIDs(T type, Map<T, Map<String, List<Annotation>>> container) {
	    Map<String, List<Annotation>> groups = container.get(type);
	    return (groups == null) ? new ArrayList<String>() : new ArrayList<String>(groups.keySet());
	}
	
	static <T> void remove(Annotation ann, T type, Map<T, Map<String, List<Annotation>>> container) {
	    Helper.remove(ann, type, DEFAULT_GROUP, container);
	}
	
	static <T> void remove(Annotation ann, T type, String group, Map<T, Map<String, List<Annotation>>> container) {
	    Map<String, List<Annotation>> groups = container.get(type);
	    if (groups == null) return;
	    List<Annotation> annotations = groups.get(group);
	    if (annotations != null) groups.remove(ann);
	} 
    }
    
}

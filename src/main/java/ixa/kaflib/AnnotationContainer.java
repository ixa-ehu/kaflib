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
    private Set<Element> unknownLayers;
    
    /* Indices */
    private Map<Annotation, Map<Layer, List<Annotation>>> invRefIndex; /* (Annotation => (Layer => Annotations)) */
    private Map<Layer, Map<String, Map<Integer, List<Annotation>>> > sentIndex; /* (Layer => (Group => (Sentence => Annotations))) */
    private Map<Layer, Map<String, Map<Integer, List<Annotation>>> > paraIndex; /* (Layer => (Group => (Paragraph => Annotations))) */
    private Map<Integer, Set<Integer>> paraSentIndex; /* Para => List<Sent> */
    private Set<Integer> indexedSents; /* Used to keep count of which sentences have already been indexed by paragraphs
    					(to avoid repeating the same sentence in different paragraphs, due to tokenizer bugs */
    
    static final String DEFAULT_GROUP = "kaflib_default_group";


    /** This creates a new AnnotationContainer object */
    AnnotationContainer() {
	rawText = new String();
	layers = new HashMap<Layer, Map<String, List<Annotation>>>();
	unknownLayers = new HashSet<Element>();
	invRefIndex = new HashMap<Annotation, Map<Layer, List<Annotation>>>();
	sentIndex = new HashMap<Layer, Map<String, Map<Integer, List<Annotation>>>>();
	paraIndex = new HashMap<Layer, Map<String, Map<Integer, List<Annotation>>>>();
	paraSentIndex = new HashMap<Integer, Set<Integer>>();
	indexedSents = new HashSet<Integer>();
    }

    
    /** Returns raw text */
    String getRawText() {
	return rawText;
    }
    
    List<Annotation> get(Layer layer) {
	List<Annotation> annotations = new ArrayList<Annotation>();
	for (String group : this.getGroupIDs(layer)) {
	    annotations.addAll(this.get(layer, group));
	}
	return annotations;
    }
    
    List<Annotation> get(Layer layer, String group) {
	Map<String, List<Annotation>> layerGroups = this.layers.get(layer);
	if (layerGroups == null) return new ArrayList<Annotation>();
	List<Annotation> annotations = layerGroups.get(group);
	return (annotations == null) ? new ArrayList<Annotation>() : annotations;
    }
    
    List<Annotation> getInverse(Annotation ann) {
	return Helper.getInvReferences(ann, this.invRefIndex);
    }
    
    List<Annotation> getInverse(Annotation ann, Layer layer) {
	return Helper.getInvReferences(ann, layer, this.invRefIndex);
    }
    
    List<Annotation> getInverse(List<Annotation> anns, Layer layer) {
	List<Annotation> result = new ArrayList<Annotation>();
	for (Annotation ann : anns) {
	    result.addAll(Helper.getInvReferences(ann, layer, this.invRefIndex));
	}
	return result;
    }
    
    List<String> getGroupIDs(Layer layer) {
	Map<String, List<Annotation>> layerGroups = this.layers.get(layer);
	return (layerGroups == null) ? new ArrayList<String>() : new ArrayList<String>(layerGroups.keySet());
    }

    /** Returns all unknown layers as a DOM Element list */
    Set<Element> getUnknownLayers() {
	return unknownLayers;
    }
    
    void setRawText(String str) {
	rawText = str;
    }

    void add(Annotation ann, Layer layer) {
	this.add(ann, layer, null);
    }
    
    void add(Annotation ann, Layer layer, Integer position) {
	Helper.addAnnotation(ann, layer, getGroupID(ann), position, this.layers);
	/* Index */
	this.indexAnnotation(ann, layer);
    }

    
    /** Adds an unknown layer to the container in DOM format */
    void add(Element layer) {
	this.unknownLayers.add(layer);
    }
    
    private void indexAnnotation(Annotation ann, Layer layer) {
	/* Inverse references index*/
	Map<Layer, List<Annotation>> invReferences = ann.getReferencedAnnotations();
	Iterator<Map.Entry<Layer, List<Annotation>>> it = invReferences.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry<Layer, List<Annotation>> pair = it.next();
	    for (Annotation ref : pair.getValue()) {
		Helper.addInvReference(ann, ref, layer, this.invRefIndex);
	    }
	}
	/* Sentence and paragraph index */
	this.indexAnnotationParaSent(ann, layer);
    }
    
    private void indexAnnotationParaSent(Annotation ann, Layer layer) {
	String groupID = getGroupID(ann);
	if (ann instanceof SentenceLevelAnnotation) {
	    Integer sent = ((SentenceLevelAnnotation) ann).getSent();
	    Integer para = ((ParagraphLevelAnnotation) ann).getPara();
	    Helper.addToIndex(ann, layer, groupID, sent, this.sentIndex);
	    if (para > 0) {
		Helper.addToIndex(ann, layer, groupID, para, this.paraIndex);
		if (!indexedSents.contains(sent)) {
		    this.addSentToPara(sent, para);
		    indexedSents.add(sent);
		}
	    }
	}
	else if (ann instanceof ParagraphLevelAnnotation) {
	    Integer para = ((ParagraphLevelAnnotation) ann).getPara();
	    if (para > 0) {
		Helper.addToIndex(ann, layer, groupID, para, this.paraIndex);
	    }
	}
    }
    
    void reindexAnnotationParaSent(Annotation ann, Layer layer, Integer oldSent, Integer oldPara) {
	String groupID = getGroupID(ann);
	/* Remove index */
	Helper.removeFromIndex(ann, layer, groupID, oldSent, this.sentIndex);
	Helper.removeFromIndex(ann, layer, groupID, oldPara, this.paraIndex);
	/* Re-index */
	this.indexAnnotationParaSent(ann, layer);
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

    void remove(Annotation ann, Layer layer) {
	Map<String, List<Annotation>> layerGroups = this.layers.get(layer);
	if (layerGroups != null) {
	    layerGroups.remove(ann);
	}
	if (ann instanceof SentenceLevelAnnotation) {
	    String groupID = getGroupID(ann);
	    Integer sent = ((SentenceLevelAnnotation) ann).getSent();
	    List<Annotation> sentAnnotations = this.getSentAnnotations(sent, layer, groupID);
	    sentAnnotations.remove(ann);
	    if (ann instanceof ParagraphLevelAnnotation) {
		Integer para = ((ParagraphLevelAnnotation) ann).getPara();
		List<Annotation> paraAnnotations = this.getParaAnnotations(para, layer, groupID);
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

    List<Annotation> getSentAnnotations(Integer sent, Layer layer) {
	return this.getSentAnnotations(sent, layer, DEFAULT_GROUP);
    }
    
    List<Annotation> getSentAnnotations(Integer sent, Layer layer, String groupID) {
	return Helper.getIndexedAnnotations(layer, groupID, sent, this.sentIndex);
    }

    List<Annotation> getParaAnnotations(Integer para, Layer layer) {
	return this.getParaAnnotations(para, layer, DEFAULT_GROUP);
    }
    
    List<Annotation> getParaAnnotations(Integer para, Layer layer, String groupID) {
	return Helper.getIndexedAnnotations(layer, groupID, para, this.paraIndex);
    }
    
    Integer getNumSentences() {
	return this.sentIndex.size();
    }
    
    Integer getNumParagraphs() {
	if (this.paraIndex.get(Layer.TEXT) == null) return 0;
	return this.paraIndex.get(Layer.TEXT).get(DEFAULT_GROUP).size();
    }
    
    /** Returns all tokens classified into sentences */
    List<List<Annotation>> getSentences(Layer layer) {
	return this.getSentences(layer, DEFAULT_GROUP);
    }
    
    /** Return all annotations of type "type" classified into sentences */
    List<List<Annotation>> getSentences(Layer layer, String groupID) {
	List<List<Annotation>> sentences = new ArrayList<List<Annotation>>();
	for (int sent : Helper.getIndexKeys(layer, groupID, this.sentIndex)) {
	    sentences.add(this.getSentAnnotations(sent, layer));
	}
	return sentences;
    }
    
    /** Returns all tokens classified into paragraphs */
    List<List<Annotation>> getParagraphs(Layer layer) {
	return this.getParagraphs(layer, DEFAULT_GROUP);
    }
    
    /** Return all annotations of type "type" classified into paragraphs */
    List<List<Annotation>> getParagraphs(Layer layer, String groupID) {
	List<List<Annotation>> paragraphs = new ArrayList<List<Annotation>>();
	for (int para : Helper.getIndexKeys(layer, groupID, this.paraIndex)) {
	    paragraphs.add(this.getParaAnnotations(para, layer));
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
    
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof AnnotationContainer)) return false;
	AnnotationContainer ac = (AnnotationContainer) o;
	return Utils.areEquals(this.layers, ac.layers) &&
		Utils.areEquals(this.rawText, ac.rawText) &&
		Utils.areEquals(this.unknownLayers, ac.unknownLayers);
    }

    private static class Helper {
	
	static void addToIndex(Annotation ann, Layer layer, String groupID, Integer key, Map<Layer, Map<String, Map<Integer, List<Annotation>>>> index) {
	    if (key != null) {
		Map<String, Map<Integer, List<Annotation>> > layerIndex = index.get(layer);
		if (layerIndex == null) {
		    layerIndex = new HashMap<String, Map<Integer, List<Annotation>> >();
		    index.put(layer, layerIndex);
		}
		Map<Integer, List<Annotation>> groupIndex = layerIndex.get(groupID);
		if (groupIndex == null) {
		    groupIndex = new HashMap<Integer, List<Annotation>>();
		    layerIndex.put(groupID, groupIndex);
		}
		List<Annotation> annotations = groupIndex.get(key);
		if (annotations == null) {
		    annotations = new ArrayList<Annotation>();
		    groupIndex.put(key, annotations);
		}
		annotations.add(ann);
	    }
	}
	
	static void removeFromIndex(Annotation ann, Layer layer, String groupID, Integer key, Map<Layer, Map<String, Map<Integer, List<Annotation>>>> index) {
	    if (key != null) {
		Map<String, Map<Integer, List<Annotation>> > layerIndex = index.get(layer);
		if (layerIndex != null) {
		    Map<Integer, List<Annotation>> groupIndex = layerIndex.get(groupID);
		    if (groupIndex != null) {
			List<Annotation> annotations = groupIndex.get(key);
			if (annotations != null) {
			    annotations.remove(ann);
			}
		    }
		}
	    }
	}
	
	static void addAnnotation(Annotation ann, Layer layer, String groupID, Integer position, Map<Layer, Map<String, List<Annotation>>> layers) {
	    Map<String, List<Annotation>> layerGroups = layers.get(layer);
	    if (layerGroups == null) {
		layerGroups = new HashMap<String, List<Annotation>>();
		layers.put(layer, layerGroups);
	    }
	    List<Annotation> annotations = layerGroups.get(groupID);
	    if (annotations == null) {
		annotations = new ArrayList<Annotation>();
		layerGroups.put(groupID, annotations);
	    }
	    if ((position == null) || (position > annotations.size())) {
		annotations.add(ann);
	    } else {
		annotations.add(position, ann);
	    }
	}

	static List<Annotation> getIndexedAnnotations(Layer layer, String groupID, Integer key, Map<Layer, Map<String, Map<Integer, List<Annotation>>>> index) {
	    Map<String, Map<Integer, List<Annotation>> > layerIndex = index.get(layer);
	    if (layerIndex == null) return new ArrayList<Annotation>();
	    Map<Integer, List<Annotation>> groupIndex = layerIndex.get(groupID);
	    if (groupIndex == null) return new ArrayList<Annotation>();
	    List<Annotation> annotations = groupIndex.get(key);
	    return (annotations == null) ? new ArrayList<Annotation>() : annotations;
	}
	
	static List<Integer> getIndexKeys(Layer layer, String groupID, Map<Layer, Map<String, Map<Integer, List<Annotation>>>> index) {
	    Map<String, Map<Integer, List<Annotation>>> layerIndex = index.get(layer);
	    if (layerIndex != null) {
		Map<Integer, List<Annotation>> groupIndex = layerIndex.get(groupID);
		if (groupIndex != null) {
		    List<Integer> keys= new ArrayList<Integer>(groupIndex.keySet());
		    Collections.sort(keys);
		    return keys;
		}
	    }
	    return new ArrayList<Integer>();
	}
	
	static void addInvReference(Annotation src, Annotation ref, Layer layer, Map<Annotation, Map<Layer, List<Annotation>>> index) {
	    Map<Layer, List<Annotation>> annIndex = index.get(ref);
	    if (annIndex == null) {
		annIndex = new HashMap<Layer, List<Annotation>>();
		index.put(ref, annIndex);
	    }
	    List<Annotation> refAnnotations = annIndex.get(layer);
	    if (refAnnotations == null) {
		refAnnotations = new ArrayList<Annotation>();
		annIndex.put(layer, refAnnotations);
	    }
	    refAnnotations.add(src);
	}
	
	static List<Annotation> getInvReferences(Annotation ann, Layer layer, Map<Annotation, Map<Layer, List<Annotation>>> index) {
	    Map<Layer, List<Annotation>> annIndex = index.get(ann);
	    if (annIndex == null) return new ArrayList<Annotation>();
	    List<Annotation> annotations = annIndex.get(layer);
	    return (annotations == null) ? new ArrayList<Annotation>() : annotations;
	}
	
	static List<Annotation> getInvReferences(Annotation ann, Map<Annotation, Map<Layer, List<Annotation>>> index) {
	    List<Annotation> annotations = new ArrayList<Annotation>();
	    Map<Layer, List<Annotation>> annIndex = index.get(ann);
	    if (annIndex == null) return new ArrayList<Annotation>();
	    for (Layer layer : annIndex.keySet()) {
		annotations.addAll(annIndex.get(layer));
	    }
	    return annotations;
	}
    }
    
}

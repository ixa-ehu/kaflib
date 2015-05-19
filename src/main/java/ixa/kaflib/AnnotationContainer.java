package ixa.kaflib;

import ixa.kaflib.KAFDocument.Layers;
import ixa.kaflib.KAFDocument.AnnotationType;
import java.io.Serializable;
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
    private Map<AnnotationType, Map<String, List<Annotation>>> layers; /* (Layer => (Group => Annotations)) */
    private List<Element> unknownLayers;
    
    /* Indices */
    private Map<Annotation, Map<AnnotationType, List<Annotation>>> invRefIndex; /* (Layer => (Layer => Annotations)) */
    private Map<AnnotationType, Map<String, Map<Integer, List<Annotation>>> > sentIndex; /* (Layer => (Group => (Sentence => Annotations))) */
    private Map<AnnotationType, Map<String, Map<Integer, List<Annotation>>> > paraIndex; /* (Layer => (Group => (Paragraph => Annotations))) */
    private Map<Integer, Set<Integer>> paraSentIndex; /* Para => List<Sent> */
    
    private static final String DEFAULT_GROUP = "kaflib_default_group";


    /** This creates a new AnnotationContainer object */
    AnnotationContainer() {
	rawText = new String();
	layers = new HashMap<AnnotationType, Map<String, List<Annotation>>>();
	unknownLayers = new ArrayList<Element>();
	invRefIndex = new HashMap<Annotation, Map<AnnotationType, List<Annotation>>>();
	sentIndex = new HashMap<AnnotationType, Map<String, Map<Integer, List<Annotation>>>>();
	paraIndex = new HashMap<AnnotationType, Map<String, Map<Integer, List<Annotation>>>>();
	paraSentIndex = new HashMap<Integer, Set<Integer>>();
    }

    
    /** Returns raw text */
    String getRawText() {
	return rawText;
    }
    
    List<Annotation> get(AnnotationType type) {
	return this.get(type, DEFAULT_GROUP);
    }
    
    List<Annotation> get(AnnotationType type, String group) {
	Map<String, List<Annotation>> layer = this.layers.get(type);
	if (layer == null) return new ArrayList<Annotation>();
	List<Annotation> annotations = layer.get(group);
	return (annotations == null) ? new ArrayList<Annotation>() : annotations;
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
	Map<String, List<Annotation>> layer = this.layers.get(type);
	return (layer == null) ? new ArrayList<String>() : new ArrayList<String>(layer.keySet());
    }

    /** Returns all unknown layers as a DOM Element list */
    List<Element> getUnknownLayers() {
	return unknownLayers;
    }
    
    void setRawText(String str) {
	rawText = str;
    }

    void add(Annotation ann, AnnotationType type) {
	this.add(ann, type, null);
    }
    
    void add(Annotation ann, AnnotationType type, Integer position) {
	Helper.addAnnotation(ann, type, getGroupID(ann), position, this.layers);
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
	String groupID = getGroupID(ann);
	if (ann instanceof SentenceLevelAnnotation) {
	    Integer sent = ((SentenceLevelAnnotation) ann).getSent();
	    Helper.addToIndex(ann, type, groupID, sent, this.sentIndex);
	    if (ann instanceof ParagraphLevelAnnotation) {
		Integer para = ((ParagraphLevelAnnotation) ann).getPara();
		Helper.addToIndex(ann, type, groupID, para, this.paraIndex);
	    }
	}
    }

    
    void remove(Annotation ann, AnnotationType type) {
	Map<String, List<Annotation>> layer = this.layers.get(type);
	if (layer != null) {
	    layer.remove(ann);
	}
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

    void removeLayer(Layers layerName) {
	this.layers.remove(layerName);
    }
    
    void removeLayer(Layers layerName, String groupID) {
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
	return this.paraIndex.size();
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
    
    Integer termPosition(Term term) {
	return this.layers.get(AnnotationType.TERM).get(DEFAULT_GROUP).indexOf(term);
    }

    
    private static class Helper {
	
	static void addToIndex(Annotation ann, AnnotationType type, String groupID, Integer key, Map<AnnotationType, Map<String, Map<Integer, List<Annotation>>>> index) {
	    if (key != null) {
		Map<String, Map<Integer, List<Annotation>> > layerIndex = index.get(type);
		if (layerIndex == null) {
		    layerIndex = new HashMap<String, Map<Integer, List<Annotation>> >();
		    index.put(type, layerIndex);
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
	
	static void addAnnotation(Annotation ann, AnnotationType type, String groupID, Integer position, Map<AnnotationType, Map<String, List<Annotation>>> layers) {
	    Map<String, List<Annotation>> layer = layers.get(type);
	    if (layer == null) {
		layer = new HashMap<String, List<Annotation>>();
		layers.put(type, layer);
	    }
	    List<Annotation> annotations = layer.get(groupID);
	    if (annotations == null) {
		annotations = new ArrayList<Annotation>();
		layer.put(groupID, annotations);
	    }
	    if ((position == null) || (position > annotations.size())) {
		annotations.add(ann);
	    } else {
		annotations.add(position, ann);
	    }
	}

	static List<Annotation> getIndexedAnnotations(AnnotationType type, String groupID, Integer key, Map<AnnotationType, Map<String, Map<Integer, List<Annotation>>>> index) {
	    Map<String, Map<Integer, List<Annotation>> > layerIndex = index.get(type);
	    if (layerIndex == null) return new ArrayList<Annotation>();
	    Map<Integer, List<Annotation>> groupIndex = layerIndex.get(groupID);
	    if (groupIndex == null) return new ArrayList<Annotation>();
	    List<Annotation> annotations = groupIndex.get(key);
	    return (annotations == null) ? new ArrayList<Annotation>() : annotations;
	}
	
	static List<Integer> getIndexKeys(AnnotationType type, String groupID, Map<AnnotationType, Map<String, Map<Integer, List<Annotation>>>> index) {
	    Map<String, Map<Integer, List<Annotation>>> layerIndex = index.get(type);
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
	
	static void addInvReference(Annotation src, Annotation ref, AnnotationType type, Map<Annotation, Map<AnnotationType, List<Annotation>>> index) {
	    Map<AnnotationType, List<Annotation>> annIndex = index.get(ref);
	    if (annIndex == null) {
		annIndex = new HashMap<AnnotationType, List<Annotation>>();
		index.put(ref, annIndex);
	    }
	    List<Annotation> refAnnotations = annIndex.get(type);
	    if (refAnnotations == null) {
		refAnnotations = new ArrayList<Annotation>();
		annIndex.put(type, refAnnotations);
	    }
	    refAnnotations.add(src);
	}
	
	static List<Annotation> getInvReferences(Annotation ann, AnnotationType type, Map<Annotation, Map<AnnotationType, List<Annotation>>> index) {
	    Map<AnnotationType, List<Annotation>> annIndex = index.get(ann);
	    if (annIndex == null) return new ArrayList<Annotation>();
	    List<Annotation> annotations = annIndex.get(type);
	    return (annotations == null) ? new ArrayList<Annotation>() : annotations;
	}
    }

}

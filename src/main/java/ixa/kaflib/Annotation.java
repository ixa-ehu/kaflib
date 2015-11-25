package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public abstract class Annotation implements Comparable<Annotation>, Serializable {

    protected AnnotationContainer annotationContainer;
    protected Integer creationOrder;
    
    private static final long serialVersionUID = 1L;
    
    
    Annotation(AnnotationContainer annotationContainer) {
	this.annotationContainer = annotationContainer;
	this.creationOrder = this.annotationContainer.getNextCreationOrderId();
    }
    
    abstract Map<AnnotationType, List<Annotation>> getReferencedAnnotations();

    public Map<AnnotationType, List<Annotation>> getReferencedAnnotationsDeep() {
	Map<AnnotationType, List<Annotation>> referencedAnns = new HashMap<AnnotationType, List<Annotation>>();
	mergeAnnotationMaps(referencedAnns, this.getReferencedAnnotations());
	for (Map.Entry<AnnotationType, List<Annotation>> entry : this.getReferencedAnnotations().entrySet()) {
	    for (Annotation annotation : entry.getValue()) {
		mergeAnnotationMaps(referencedAnns, annotation.getReferencedAnnotationsDeep());
	    }
	}
	return referencedAnns;
    }

    public abstract Integer getOffset();
    
    @Override
    public int compareTo(Annotation annotation) {
	if (this.equals(annotation)) return 0;
	if (this.getOffset() < annotation.getOffset()) return -1;
	else if (this.getOffset() > annotation.getOffset()) return 1;
	else { // this.offset == annotation.getOffset()
	    if (this.creationOrder < annotation.creationOrder) return -1;
	    else return 1; // this.creationOrder > annotation.creationOrder
	}
    }
    
    public abstract String toString();
    
    public String toStringComment() {
	String strValue = this.toString();
	boolean valid = false;
	while (!valid) {
	    valid = true;
	    if (strValue.contains("--")) { 
		strValue = strValue.replace("--", "- -");
		valid = false;
	    }
	    if (strValue.startsWith("-")) {
		strValue = " " + strValue;
		valid = false;
	    }
	    if (strValue.endsWith("-")) {
		strValue = strValue + " ";
		valid = false;
	    }
	}
	return strValue;
    }
    

    private static void mergeAnnotationMaps(Map<AnnotationType, List<Annotation>> originalMap, Map<AnnotationType, List<Annotation>> newMap) {
	for (Map.Entry<AnnotationType, List<Annotation>> entry : newMap.entrySet()) {
	    AnnotationType type = entry.getKey();
	    List<Annotation> annotations = new ArrayList<Annotation>(entry.getValue());
	    List<Annotation> typeAnns = originalMap.get(type);
	    if (typeAnns == null) {
		typeAnns = new ArrayList<Annotation>();
		originalMap.put(type, typeAnns);
	    }
	    Set<Annotation> typeAnnSet = new TreeSet<Annotation>(typeAnns);
	    typeAnnSet.addAll(annotations);
	    originalMap.put(type, new ArrayList<Annotation>(typeAnnSet));
	}
    }
}

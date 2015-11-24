package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.io.Serializable;
import java.util.Map;
import java.util.List;


public abstract class Annotation implements Comparable<Annotation>, Serializable {

    protected AnnotationContainer annotationContainer;
    protected Integer creationOrder;
    
    private static final long serialVersionUID = 1L;
    
    
    Annotation(AnnotationContainer annotationContainer) {
	this.annotationContainer = annotationContainer;
	this.creationOrder = this.annotationContainer.getNextCreationOrderId();
    }
    
    abstract Map<AnnotationType, List<Annotation>> getReferencedAnnotations();
    
    /*
    public Map<AnnotationType, List<Annotation>> getReferencedAnnotationsDeep() {
	Map<AnnotationType, List<Annotation>> referencedAnns = new HashMap<KAFDocument.AnnotationType, List<Annotation>>();
	for (Map.Entry<AnnotationType, List<Annotation>> entry : this.getReferencedAnnotations().entrySet()) {
	    AnnotationType type = entry.getKey();
	    List<Annotation> annotations = new ArrayList<Annotation>(entry.getValue());
	    referencedAnns.put(type, annotations);
	    
	    
	}
	return referencedAnns;
    }
    */

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
    
    /*
    private static void insertAnnotationsInMap(Map<AnnotationType, List<Annotation>> originalMap, Map<AnnotationType, List<Annotation>> newMap) {
	for (Map.Entry<AnnotationType, List<Annotation>> entry : newMap.entrySet()) {
	    AnnotationType type = entry.getKey();
	    List<Annotation> annotations = new ArrayList<Annotation>(entry.getValue());
	    Set<Annotation> typeAnns = new LinkedHashSet<Annotation>(originalMap.get(type));
	    if (typeAnns == null) {
		typeAnns = new LinkedHashSet<Annotation>();
		originalMap.put(type, typeAnns);
	    }
	    typeAnns.addAll(annotations);
	}
    }
    */
}

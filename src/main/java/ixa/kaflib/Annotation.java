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

    public abstract Integer getOffset();
    
    @Override
    public int compareTo(Annotation annotation) {
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
}

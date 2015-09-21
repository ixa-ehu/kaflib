package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.io.Serializable;
import java.util.Map;
import java.util.List;


public abstract class Annotation implements Comparable<Annotation>, Serializable {

    protected AnnotationContainer annotationContainer;
    
    private static final long serialVersionUID = 1L;
    
    
    Annotation(AnnotationContainer annotationContainer) {
	this.annotationContainer = annotationContainer;
    }
    
    abstract Map<AnnotationType, List<Annotation>> getReferencedAnnotations();

    public abstract Integer getOffset();
    
    @Override
    public int compareTo(Annotation annotation) {
	if (this.getOffset() < annotation.getOffset()) return -1;
	else if (this.getOffset() > annotation.getOffset()) return 1;
	return 0;
    }
}

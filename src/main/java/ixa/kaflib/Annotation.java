package ixa.kaflib;

import ixa.kaflib.KAFDocument.Layer;
import java.io.Serializable;
import java.util.Map;
import java.util.List;


public abstract class Annotation implements Serializable {
    
    abstract Map<Layer, List<Annotation>> getReferencedAnnotations();
    
}

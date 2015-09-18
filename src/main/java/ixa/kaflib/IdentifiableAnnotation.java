package ixa.kaflib;


public abstract class IdentifiableAnnotation
    extends Annotation
    implements Comparable<IdentifiableAnnotation> {

    protected String id;
    
    private static final long serialVersionUID = 1L;

    
    IdentifiableAnnotation(AnnotationContainer annotationContainer, String id) {
	super(annotationContainer);
	this.id = id;
    }

    public String getId() {
	return this.id;
    }

    void setId(String id) {
	this.id = id;
    }

    @Override
    public int compareTo(IdentifiableAnnotation o) {
	return this.getId().compareTo(o.getId());
    }
     
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof IdentifiableAnnotation)) return false;
	IdentifiableAnnotation ann = (IdentifiableAnnotation) o;
	return Utils.areEquals(this.id, ann.id);
    }
    */
}

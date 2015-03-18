package ixa.kaflib;


abstract class IdentifiableAnnotation
    extends Annotation
    implements Comparable<IdentifiableAnnotation> {

    protected String id;

    IdentifiableAnnotation(String id) {
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
}

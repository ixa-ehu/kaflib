package ixa.kaflib;

public class Target<T> {

    private AnnotationContainer annotationContainer;
    //private String target;
    private T target;

    Target(AnnotationContainer annotationContainer, T target) {
	this.annotationContainer = annotationContainer;
	this.target = target;
    }

    public T getTarget() {
	return this.target;
    }

    public void setTarget(T target) {
	this.target = target;
    }
}

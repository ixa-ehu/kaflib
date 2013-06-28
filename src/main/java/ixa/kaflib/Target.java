package ixa.kaflib;

public class Target<T> {

    private AnnotationContainer annotationContainer;
    //private String id;
    private T targetObj;
    private boolean isHead;

    Target(AnnotationContainer annotationContainer, T targetObj, boolean isHead) {
	this.annotationContainer = annotationContainer;
	this.targetObj = targetObj;
	this.isHead = isHead;
    }

    public T getTarget() {
	return targetObj;
    }

    public boolean isHead() {
	return isHead;
    }

    public void setTarget(T targetObj) {
	this.targetObj = targetObj;
	this.isHead = false;
    }

    public void setTarget(T targetObj, boolean isHead) {
	this.targetObj = targetObj;
	this.isHead = isHead;
    }

    public void setHead(boolean isHead) {
	this.isHead = isHead;
    }
}

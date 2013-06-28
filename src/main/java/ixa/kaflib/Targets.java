package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

public class Targets<T> {

    private AnnotationContainer annotationContainer;
    //private List<String> targets;
    private List<T> targets;
    private int headIndex;

    Targets(AnnotationContainer annotationContainer) {
	this.annotationContainer = annotationContainer;
	this.targets = new ArrayList();
	this.headIndex = -1;
    }

    public List<T> getTargets() {
	return this.targets;
    }

    public T getHead() {
	if (this.headIndex < 0) {
	    return null;
	}
	return this.targets.get(this.headIndex);
    }

    public void addTarget(T target) {
	this.targets.add(target);
    }

    public void addTarget(T target, boolean isHead) {
	this.targets.add(target);
	if (isHead) {
	    this.headIndex = this.targets.size() - 1;
	}
    }
}

package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

public class Span<T> {

    private AnnotationContainer annotationContainer;
    //private List<String> targets;
    private List<T> targets;
    private T head;

    Span(AnnotationContainer annotationContainer) {
	this.annotationContainer = annotationContainer;
	this.targets = new ArrayList<T>();
	this.head = null;
    }

    Span(AnnotationContainer annotationContainer, List<T> targets) {
	this.annotationContainer = annotationContainer;
	this.targets = targets;
	this.head = null;
    }

    Span(AnnotationContainer annotationContainer, List<T> targets, T head) {
	this.annotationContainer = annotationContainer;
	this.targets = targets;
	this.head = head;
    }

    public List<T> getTargets() {
	return this.targets;
    }

    public boolean hasHead() {
	return (this.head != null);
    }

    public T getHead() {
	return this.head;
    }

    public void setHead(T head) {
	this.head = head;
    }

    public void addTarget(T target) {
	this.targets.add(target);
    }

    public void addTarget(T target, boolean isHead) {
	this.targets.add(target);
	if (isHead) {
	    this.head = target;
	}
    }

    public void addTargets(List<T> targets) {
	this.targets.addAll(targets);
    }

    public boolean hasTarget(T target) {
	for (T t : targets) {
	    if (t == target) {
		return true;
	    }
	}
	return false;
    }

    public int size() {
	return this.targets.size();
    }
}

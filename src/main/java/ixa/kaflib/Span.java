package ixa.kaflib;

import ixa.kaflib.KAFDocument.Utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;

public class Span<T extends IdentifiableAnnotation> implements Serializable {

    //private List<String> targets;
    private List<T> targets;
    private List<T> sortedTargets;
    private T head;


    Span() {
	this.targets = new ArrayList<T>();
	this.sortedTargets = new ArrayList<T>();
	this.head = null;
    }

    Span(List<T> targets) {
	this(targets, null);
    }

    Span(List<T> targets, T head) {
	this.targets = targets;
	this.sortedTargets = new ArrayList<T>();
	this.head = head;
    }

    public boolean isEmpty() {
	return (this.targets.size() <= 0);
    }

    public List<T> getTargets() {
	return this.targets;
    }

    public T getFirstTarget() {
	return this.targets.get(0);
    }

    public boolean hasHead() {
	return (this.head != null);
    }

    public T getHead() {
	return this.head;
    }

    public boolean isHead(T target) {
	return (target == this.head);
    }

    public void setHead(T head) {
	this.head = head;
    }

    public void addTarget(T target) {
	this.targets.add(target);
	this.sortedTargets.add(target);
	Collections.sort(this.sortedTargets);
    }

    public void addTarget(T target, boolean isHead) {
	this.addTarget(target);
	if (isHead) {
	    this.head = target;
	}
    }

    public void addTargets(List<T> targets) {
	for (T target : targets) {
	    this.addTarget(target);
	}
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

    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof Span)) return false;
	Span ann = (Span) o;
	return Utils.areEquals(this.sortedTargets, ann.sortedTargets) &&
		Utils.areEquals(this.head, ann.head);
    }
    */

    @Override
	public int hashCode() {
	String spanId = "";
	for (T target : this.sortedTargets) {
	    if (!spanId.isEmpty()) spanId += "_";
	    spanId += target.getId();
	}
	return spanId.hashCode();
    }
    @Override
	public String toString() {
	return "Span{" +
	    "targets=" + targets +
	    '}';
    }
}

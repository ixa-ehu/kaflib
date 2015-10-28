package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;


public class Span<T extends IdentifiableAnnotation> implements Serializable {
    private List<T> targets;
    private List<T> sortedTargets;
    private T head;

    private static final long serialVersionUID = 1L;


    Span() {
	this(new ArrayList<T>(), null);
    }

    Span(List<T> targets) {
	this(targets, null);
    }

    Span(List<T> targets, T head) {
	this.targets = targets;
	this.sortedTargets = new ArrayList<T>(targets);
	Collections.sort(this.sortedTargets);
	this.head = head;
    }

    public Integer size() {
	return this.targets.size();
    }

    public Boolean isEmpty() {
	return (this.size() <= 0);
    }

    public Boolean hasTarget(T target) {
	return this.targets.contains(target);
    }

    public List<T> getTargets() {
	return this.targets;
    }

    public List<T> getTargetsSorted() {
	return this.sortedTargets;
    }

    public T getFirstTarget() {
	return this.getTargets().get(0);
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
	this.targets.addAll(targets);
	this.sortedTargets.addAll(targets);
	Collections.sort(this.sortedTargets);
    }

    public void removeTarget(T target) {
	this.targets.remove(target);
	this.sortedTargets.remove(target);
	if (this.head.equals(target)) this.head = null;
    }

    public boolean hasHead() {
	return (this.head != null);
    }

    public T getHead() {
	return this.head;
    }

    public Boolean isHead(T target) {
	return (target.equals(this.head));
    }

    public void setHead(T head) {
	this.head = head;
    }

    public Integer getOffset() {
	if (this.isEmpty()) return null;
	return this.sortedTargets.get(0).getOffset();
    }

    @Override
    public String toString() {
	String str = new String("[");
	for (int i=0; i<this.targets.size(); i++) {
	    str += this.targets.get(i).toString();
	    if (i < this.targets.size()-1) {
		str += new String(" ");
	    }
	}
	str += new String("]");
	return str;
    }
    
    @Override
    public int hashCode() {
	return this.getOffset();
    }
    
    @Override
    public boolean equals(Object o) {
	Span<?> span = (Span<?>) o;
	return this.sortedTargets.equals(span.sortedTargets)
		&& ((this.head == null && span.head == null) || this.head.equals(span.head)); 
    }
}

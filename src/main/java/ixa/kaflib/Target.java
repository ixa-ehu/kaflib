package ixa.kaflib;

import ixa.kaflib.KAFDocument.Utils;

import java.io.Serializable;


public class Target implements Serializable {
    private Term term;
    private boolean head;

    Target(Term term, boolean head) {
	this.term = term;
	this.head = head;
    }

    public Term getTerm() {
	return this.term;
    }

    public boolean isHead() {
	return head;
    }

    public void setTerm(Term term) {
	this.term = term;
	this.head = false;
    }

    public void setTerm(Term term, boolean head) {
	this.term = term;
	this.head = head;
    }

    public void setHead(boolean head) {
	this.head = head;
    }
    
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof Target)) return false;
	Target ann = (Target) o;
	return Utils.areEquals(this.term, ann.term) &&
		Utils.areEquals(this.head, ann.head);
    }
    */

}

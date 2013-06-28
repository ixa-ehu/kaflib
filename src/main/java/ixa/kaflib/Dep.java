package ixa.kaflib;

import java.util.List;

/** Dependencies represent dependency relations among terms. */
public class Dep {

    /** Reference to the main annotationContainer of the document to which this dependency is related (required) */
    private AnnotationContainer annotationContainer;

    /** Source term of the dependency (required) */
    private Target<Term> from;

    /** Target term of the dependency (required) */
    private Target<Term> to;

    /** Relational function of the dependency (required). One of:
     * - `subj´ (grammatical subject)
     * - `obj´ (objects and/or complements)
     * - `mod´ (modifier of noun or verb)
     */
    private String rfunc;

    /** Declension case (optional) */
    private String depcase;

    Dep(AnnotationContainer annotationContainer, Term from, Term to, String rfunc) {
	this.annotationContainer = annotationContainer;
	this.from = new Target(annotationContainer, from);
	this.to = new Target(annotationContainer, to);
	this.rfunc = rfunc;
    }

    public Term getFrom() {
	return this.from.getTarget();
    }

    public void setFrom(Term term) {
	this.from.setTarget(term);
    }

    public Term getTo() {
	return to.getTarget();
    }

    public void setTo(Term term) {
	this.to.setTarget(term);
    }

    public String getRfunc() {
	return rfunc;
    }

     public void setRfunc(String rfunc) {
	 this.rfunc = rfunc;
    }

    public boolean hasCase() {
	return depcase != null;
    }

    public String getCase() {
	return depcase;
    }

    public void setCase(String depcase) {
	this.depcase = depcase;
    }

    public String getStr() {
	return rfunc + "(" + this.getFrom().getStr() + ", " + this.getTo().getStr() + ")";
    }
}

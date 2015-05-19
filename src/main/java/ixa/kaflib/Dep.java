package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


/** Dependencies represent dependency relations among terms. */
public class Dep extends Annotation {

    /** Source term of the dependency (required) */
    private Term from;

    /** Target term of the dependency (required) */
    private Term to;

    /** Relational function of the dependency (required) */
    private String rfunc;

    /** Declension case (optional) */
    private String depcase;

    Dep(Term from, Term to, String rfunc) {
	this.from = from;
	this.to = to;
	this.rfunc = rfunc;
    }

    Dep(Dep dep, HashMap<String, Term> terms) {
	this.from = terms.get(dep.from.getId());
	if (this.from == null) {
	    throw new IllegalStateException("Couldn't find the term when loading dep (" + dep.getFrom().getId()+", "+dep.getTo().getId()+")");
	}
	this.to = terms.get(dep.to.getId());
	if (this.to == null) {
	    throw new IllegalStateException("Couldn't find the term when loading dep (" + dep.getFrom().getId()+", "+dep.getTo().getId()+")");
	}
	this.rfunc = dep.rfunc;
	this.depcase = dep.depcase;
    }

    public Term getFrom() {
	return this.from;
    }

    public void setFrom(Term term) {
	this.from = term;
    }

    public Term getTo() {
	return to;
    }

    public void setTo(Term term) {
	this.to = term;
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
    
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	List<Annotation> terms = new ArrayList<Annotation>();
	terms.add(this.getFrom());
	terms.add(this.getTo());
	referenced.put(AnnotationType.TERM, terms);
	return referenced;
    }
}

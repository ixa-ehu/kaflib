package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

/** A named entity is a term (or a multiword) that clearly identifies one item. The optional Named Entity layer is used to reference terms that are named entities. */
public class Entity {

    /** Reference to the main annotationContainer of the document to which this named entity is related (required) */
    private AnnotationContainer annotationContainer;

    /** Named entity's ID (required) */
    private String eid;

    /** Type of the named entity (required). Currently, 8 values are possible: 
     * - Person
     * - Organization
     * - Location
     * - Date
     * - Time
     * - Money
     * - Percent
     * - Misc
     */ 
    private String type;

    /** Reference to different occurrences of the same named entity in the document (at least one required) */
    private List<List<String>> references;

    /** External references (optional) */
    private List<ExternalRef> externalReferences;

    Entity(AnnotationContainer annotationContainer, String eid, String type, List<List<Term>> references) {
	if (references.size() < 1) {
	    throw new IllegalStateException("Entities must contain at least one reference span");
	}
	if (references.get(0).size() < 1) {
	    throw new IllegalStateException("Entities' reference's spans must contain at least one target");
	}
	this.annotationContainer = annotationContainer;
	this.eid = eid;
	this.type = type;
	this.references = new ArrayList<List<String>>();
	for (List<Term> termSpan : references) {
	    List<String> idSpan = new ArrayList<String>();
	    for (Term term : termSpan) {
		idSpan.add(term.getId());
	    }
	    this.references.add(idSpan);
	}
	this.externalReferences = new ArrayList<ExternalRef>();
    }

    public String getId() {
	return eid;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public List<List<Term>> getReferences() {
	List<List<Term>> termSpans = new ArrayList<List<Term>>();
	for (List<String> idSpan : this.references) {
	    List<Term> termSpan = new ArrayList<Term>();
	    for (String id : idSpan) {
		Term newTerm = annotationContainer.getTermById(id);
		termSpan.add(newTerm);
	    }
	    termSpans.add(termSpan);
	}
	return termSpans;
    }

    public void addReference(List<Term> span) {
	List<String> idSpan = new ArrayList<String>();
	for (Term term : span) {
	    idSpan.add(term.getId());
	}
	references.add(idSpan);
    }

    public List<ExternalRef> getExternalRefs() {
	return externalReferences;
    }

    public void addExternalRef(ExternalRef externalRef) {
	externalReferences.add(externalRef);
    }

    public void addExternalRefs(List<ExternalRef> externalRefs) {
	externalReferences.addAll(externalRefs);
    }
}
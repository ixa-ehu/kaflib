package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

/** Chunks are noun, verb or prepositional phrases, spanning terms. */
public class Chunk {

    /** Reference to the main annotationContainer of the document to which this dependency is related (required) */
    private AnnotationContainer annotationContainer;

    /** Chunk's ID (required) */
    private String cid;

    /** The chunk head term (required) */
    private String head;

    /** Type of the phrase (required) */
    private String phrase;

    /** Declension case (optional) */
    private String chunkcase;

    /** Chunk's target terms (at least one required) */
    private List<String> targets;

    Chunk(AnnotationContainer annotationContainer, String cid, String head, String phrase, List<Term> terms) {
	if (terms.size() < 1) {
	    throw new IllegalStateException("Chunks must contain at least one term target");
	}
	this.annotationContainer = annotationContainer;
	this.cid = cid;
	this.head = head;
	this.phrase = phrase;
	this.targets = new ArrayList<String>();
	for (Term target : terms) {
	    this.addTerm(target);
	}
    }

    public String getId() {
	return cid;
    }

    public Term getHead() {
	return annotationContainer.getTermById(head);
    }

    public void setHead(String id) {
	head = id;
    }

    public void setHead(Term term) {
	head = term.getId();
    }

    public String getPhrase() {
	return phrase;
    }

    public void setPhrase(String phrase) {
	this.phrase = phrase;
    }

    public boolean hasCase() {
	return chunkcase != null;
    }

    public String getCase() {
	return chunkcase;
    }

    public void setCase(String chunkcase) {
	this.chunkcase = chunkcase;
    }

    public List<Term> getTerms() {
	return annotationContainer.getTermsById(targets);
    }

    public void addTerm(Term term) {
	targets.add(term.getId());
    }

    public String getStr() {
	String str = "";
	for (String termId : targets) {
	    Term term = annotationContainer.getTermById(termId);
	    if (!str.isEmpty()) {
		str += " ";
	    }
	    str += term.getStr();
	}
	return str;
    }
}
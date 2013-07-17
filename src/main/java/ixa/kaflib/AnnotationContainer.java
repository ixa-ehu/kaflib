package ixa.kaflib;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jdom2.Element;
import org.jdom2.JDOMException;

/** A container to keep all annotations of a document (word forms, terms, dependencies, chunks, entities and coreferences). There are different hash maps to index annotations by different properties as ID, sentence... It enables to retrieve annotations by different properties in an effective way. Performance is very important. */
class AnnotationContainer {

    /** List to keep all word forms */
    private List<WF> text;

    /** Next offset: sum of all words' length plus one char per word */
    private int nextOffset;

    /** List to keep all terms */
    private List<Term> terms;

    /** List to keep all dependencies */
    private List<Dep> deps;

    /** List to keep all chunks */
    private List<Chunk> chunks;

    /** List to keep all named entities */
    private List<Entity> entities;

    /** List to keep all properties */
    private List<Feature> properties;

    /** List to keep all categories */
    private List<Feature> categories;

    /** List to keep all coreferences */
    private List<Coref> coreferences;

    /** List to keep all opinions */
    private List<Opinion> opinions;

    /** List to keep all relations */
    private List<Relation> relations;

    /** List to keep all predicates */
    private List<Predicate> predicates;

    /** List to keep all trees */
    private List<Tree> trees;

    /** Hash map to index word forms by the sentence. It maps sentence's IDs to a list of token IDs from that sentence.  */
    private HashMap<Integer, List<WF>> textIndexedBySent;

    /** Hash map to index terms by the sentence. It maps sentence's IDs to a list of term IDs from that sentence.  */
    private HashMap<Integer, List<Term>> termsIndexedBySent;

    /** Hash map for mapping word forms to terms. */
    private HashMap<String, Term> termsIndexedByWF;

    /** This creates a new AnnotationContainer object */
    AnnotationContainer() {
	text = new ArrayList();
	nextOffset = 0;
	terms = new ArrayList();
	deps = new ArrayList();
	chunks = new ArrayList();
	entities = new ArrayList();
	properties = new ArrayList();
	categories = new ArrayList();
	coreferences = new ArrayList();
	opinions = new ArrayList();
	relations = new ArrayList();
	predicates = new ArrayList();
	trees = new ArrayList();

	textIndexedBySent = new HashMap<Integer, List<WF>>();
	termsIndexedBySent = new HashMap<Integer, List<Term>>();
	termsIndexedByWF = new HashMap<String, Term>();
    }

    /** Returns all word forms. */
    List<WF> getText() {
	return text;
    }

    /** Returns all terms */
    List<Term> getTerms() {
	return terms;
    }

    /** Returns all dependencies */
    List<Dep> getDeps() {
	return deps;
    }

    /** Returns all chunks */
    List<Chunk> getChunks() {
	return chunks;
    }

    /** Returns all named entities */
    List<Entity> getEntities() {
	return entities;
    }

    /** Returns all properties */
    List<Feature> getProperties() {
	return properties;
    }

    /** Returns all categories */
    List<Feature> getCategories() {
	return categories;
    }

    /** Returns all coreferences */
    List<Coref> getCorefs() {
	return coreferences;
    }

    /** Returns all opinions */
    List<Opinion> getOpinions() {
	return opinions;
    }

    /** Returns all relations */
    List<Relation> getRelations() {
	return relations;
    }

    /** Returns all predicates */
    List<Predicate> getPredicates() {
	return predicates;
    }

    /** Returns all trees */
    List<Tree> getConstituents() {
	return trees;
    }

    /** Adds a word form to the container */
    void add(WF wf) {
	text.add(wf);
	//nextOffset += wf.getLength() + 1;
    }

    /** Adds a term to the container */
    void add(Term term) {
	terms.add(term);
	for (WF wf : term.getWFs()) {
	    termsIndexedByWF.put(wf.getId(), term);
	}

	/* Index by sentence */
        if (term.getSent() != -1) {
	    indexTermBySent(term, term.getSent());
	}
    }

    /** Adds a dependency to the container */
    void add(Dep dep) {
	deps.add(dep);
    }

    /** Adds a chunk to the container */
    void add(Chunk chunk) {
	chunks.add(chunk);
    }

    /** Adds a named entity to the container */
    void add(Entity entity) {
	entities.add(entity);
    }

    /** Adds a feature to the container. It checks if it is a property or a category. */
    void add(Feature feature) {
	if (feature.isAProperty()) {
	    properties.add(feature);
	}
	else {
	    categories.add(feature);
	}		
    }

    /** Adds a coreference to the container */
    void add(Coref coref) {
	coreferences.add(coref);
    }

    /** Adds an opinion to the container */
    void add(Opinion opinion) {
	opinions.add(opinion);
    }

    /** Adds a relation to the container */
    void add(Relation relation) {
	relations.add(relation);
    }

    /** Adds a predicate to the container */
    void add(Predicate predicate) {
	predicates.add(predicate);
    }

    /** Adds a tree to the container */
    void add(Tree tree) {
	trees.add(tree);
    }

    /** Index a WF by its sentence number */
    void indexWFBySent(WF wf, Integer sent) {
	if (sent == -1) {
	    throw new IllegalStateException("You can't call indexWFBySent not having defined the sentence for this token");
	}
	List<WF> sentWfs = textIndexedBySent.get(sent);
	if (sentWfs == null) {
	    sentWfs = new ArrayList<WF>();
	    textIndexedBySent.put(sent, sentWfs);
	}
	sentWfs.add(wf);
    }


    /** Index a Term by its sentence number */
    void indexTermBySent(Term term, Integer sent) {
	if (sent == -1) {
	    throw new IllegalStateException("You can't call indexTermBySent not having defined the sentence for its WFs");
	}
	List<Term> sentTerms = termsIndexedBySent.get(sent);
	if (sentTerms == null) {
	    sentTerms = new ArrayList<Term>();
	    termsIndexedBySent.put(sent, sentTerms);
	}
	sentTerms.add(term);
    }

    /** Returns all tokens classified by sentences */
    List<List<WF>> getSentences() {
	List<List<WF>> sentences = new ArrayList<List<WF>>();
	Set<Integer> sentNumsSet = this.textIndexedBySent.keySet();
        List<Integer> sentNumsList = new ArrayList<Integer>(sentNumsSet);
	Collections.sort(sentNumsList);
	for (int i : sentNumsList) {
	    List<WF> wfs = this.textIndexedBySent.get(i);
	    sentences.add(wfs);
	}
	return sentences;
    }

    /** Returns WFs from a sentence */
    List<WF> getSentenceWFs(int sent) {
        return this.textIndexedBySent.get(sent);
    }

    /** Returns terms from a sentence */
    List<Term> getSentenceTerms(int sent) {
        return this.termsIndexedBySent.get(sent);
    }

    Term getTermByWF(WF wf) {
	return this.termsIndexedByWF.get(wf.getId());
    }

    /** Returns a list of terms containing the word forms given on argument.
     * @param wfIds a list of word form IDs whose terms will be found.
     * @return a list of terms containing the given word forms.
     */
    List<Term> getTermsByWFs(List<WF> wfs) {
	LinkedHashSet<Term> terms = new LinkedHashSet<Term>();
	for (WF wf : wfs) {
	    terms.add(this.termsIndexedByWF.get(wf.getId()));
	}
	return new ArrayList<Term>(terms);
    }

    /** Returns next WF's offset. */
    int getNextOffset() {
	return nextOffset;
    }


    /** Deprecated. Returns a list of terms containing the word forms given on argument.
     * @param wfIds a list of word form IDs whose terms will be found.
     * @return a list of terms containing the given word forms.
     */
    List<Term> getTermsByWFIds(List<String> wfIds) {
	LinkedHashSet<Term> terms = new LinkedHashSet<Term>();
	for (String wfId : wfIds) {
	    terms.add(this.termsIndexedByWF.get(wfId));
	}
	return new ArrayList<Term>(terms);
    }
}

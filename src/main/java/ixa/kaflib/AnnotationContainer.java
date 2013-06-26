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

    /** List to keep all trees */
    private List<Tree> trees;

    /** Hash map to index word forms by their ID. It maps IDs to the corresponding token's index in the main list of tokens. */
    private HashMap<String, Integer> textIndexedById;

    /** Hash map to index word forms by the sentence. It maps sentence's IDs to a list of token IDs from that sentence.  */
    private HashMap<Integer, List<String>> textIndexedBySent;

    /** Hash map to index terms by their ID */
    private HashMap<String, Integer> termsIndexedById;

    /** Hash map to index terms by the sentence. It maps sentence's IDs to a list of term IDs from that sentence.  */
    private HashMap<Integer, List<String>> termsIndexedBySent;

    /** Hash map for mapping word forms to terms. */
    private HashMap<String, String> termsIndexedByWF;

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
	trees = new ArrayList();

	textIndexedById = new HashMap<String, Integer>();
	textIndexedBySent = new HashMap<Integer, List<String>>();
	termsIndexedById = new HashMap<String, Integer>();
	termsIndexedBySent = new HashMap<Integer, List<String>>();
	termsIndexedByWF = new HashMap<String, String>();
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

    /** Returns all trees */
    List<Tree> getTrees() {
	return trees;
    }

    /** Adds a word form to the container */
    void add(WF wf) {
	text.add(wf);
	//nextOffset += wf.getLength() + 1;
	textIndexedById.put(wf.getId(), text.size() - 1);
    }

    /** Index a WF by its sentence number */
    void indexWFBySent(WF wf) {
	Integer sent = wf.getSent();
	if (sent == -1) {
	    throw new IllegalStateException("You can't call indexWFBySent not having defined the sentence for this token");
	}
	List<String> sentWfs = textIndexedBySent.get(sent);
	if (sentWfs == null) {
	    sentWfs = new ArrayList<String>();
	    textIndexedBySent.put(sent, sentWfs);
	}
	sentWfs.add(wf.getId());
    }

    /** Adds a term to the container */
    void add(Term term) {
	terms.add(term);
	termsIndexedById.put(term.getId(), terms.size() - 1);
	for (WF wf : term.getWFs()) {
	    termsIndexedByWF.put(wf.getId(), term.getId());
	}

	/* Index by sentence */
        if (term.getSent() != -1) {
	    indexTermBySent(term);
	}
    }

    /** Index a Term by its sentence number */
    void indexTermBySent(Term term) {
	Integer sent = term.getSent();
	if (sent == -1) {
	    throw new IllegalStateException("You can't call indexTermBySent not having defined the sentence for its WFs");
	}
	List<String> sentTerms = termsIndexedBySent.get(sent);
	if (sentTerms == null) {
	    sentTerms = new ArrayList<String>();
	    termsIndexedBySent.put(sent, sentTerms);
	}
	sentTerms.add(term.getId());
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

    /** Adds a property feature to the container */
    void addProperty(Feature property) {
	properties.add(property);
    }

    /** Adds a category feature to the container */
    void addCategory(Feature category) {
	categories.add(category);
    }

    /** Adds a coreference to the container */
    void add(Coref coref) {
	coreferences.add(coref);
    }

    /** Adds a tree to the container */
    void add(Tree tree) {
	trees.add(tree);
    }

    /** Returns a word form given it's ID */
    WF getWFById(String id) {
	int ind = textIndexedById.get(id);
	return text.get(ind);
    }

    /** Returns a list of word forms given their IDs */
    List<WF> getWFsById(List<String> ids) {
	List<WF> foundWFs = new ArrayList<WF>();
	for (String id : ids) {
	    foundWFs.add(getWFById(id));
	}
	return foundWFs;
    }

    /** Returns a term given it's ID */
    Term getTermById(String id) {
	int ind = termsIndexedById.get(id);
	return terms.get(ind);
    }

    /** Returns the terms which corresponds to the given word form */
    Term getTermByWFId(String wfId) {
	String termId = termsIndexedByWF.get(wfId);
	if (termId == null) {
	    return null;
	}
	return getTermById(termId);
    }

    /** Returns a list of terms given their IDs */
    List<Term> getTermsById(List<String> ids) {
	List<Term> foundTerms = new ArrayList<Term>();
	for (String id : ids) {
	    foundTerms.add(getTermById(id));
	}
	return foundTerms;
    }

    /** Returns all tokens classified by sentences */
    List<List<WF>> getSentences() {
	List<List<WF>> sentences = new ArrayList<List<WF>>();
	Set<Integer> sentNumsSet = this.textIndexedBySent.keySet();
        List<Integer> sentNumsList = new ArrayList<Integer>(sentNumsSet);
	Collections.sort(sentNumsList);
	for (int i : sentNumsSet) {
	    System.out.println("set: "+i);
	}
	for (int i : sentNumsList) {
	    System.out.println("list: "+i);
	}
	for (int i : sentNumsList) {
	    List<String> wfIds = this.textIndexedBySent.get(i);
	    List<WF> wfs = new ArrayList<WF>();
	    for (String wfId : wfIds) {
		  wfs.add(this.getWFById(wfId));
	    }
	    sentences.add(wfs);
	}
	return sentences;
    }

    /** Returns WFs from a sentence */
    List<WF> getSentenceWFs(int sent) {
	List<WF> wfs = new ArrayList<WF>();
	for (String wfId : this.textIndexedBySent.get(sent)) {
	    wfs.add(getWFById(wfId));
	}
	return wfs;
    }

    /** Returns terms from a sentence */
    List<Term> getSentenceTerms(int sent) {
	List<Term> terms = new ArrayList<Term>();
	for (String termId : this.termsIndexedBySent.get(sent)) {
	    terms.add(getTermById(termId));
	}
	return terms;
    }

    /** Returns a list of terms containing the word forms given on argument.
     * @param wfIds a list of word form IDs whose terms will be found.
     * @return a list of terms containing the given word forms.
     */
    List<Term> getTermsFromWFs(List<String> wfIds) {
	LinkedHashSet<Term> terms = new LinkedHashSet<Term>();
	for (String wfId : wfIds) {
	    terms.add(getTermByWFId(wfId));
	}
	return new ArrayList<Term>(terms);
    }

    /** Returns next WF's offset. */
    int getNextOffset() {
	return nextOffset;
    }
}

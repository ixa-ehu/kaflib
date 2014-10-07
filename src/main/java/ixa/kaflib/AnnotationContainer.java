package ixa.kaflib;

import java.io.IOException;
import java.io.Serializable;
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

import org.jdom2.Element;
import org.jdom2.JDOMException;

/** A container to keep all annotations of a document (word forms, terms, dependencies, chunks, entities and coreferences). There are different hash maps to index annotations by different properties as ID, sentence... It enables to retrieve annotations by different properties in an effective way. Performance is very important. */
class AnnotationContainer implements Serializable {

    private String rawText;

    /** List to keep all word forms */
    private List<WF> text;

    /** Next offset: sum of all words' length plus one char per word */
    private int nextOffset;

    /** List to keep all terms */
    private List<Term> terms;

    private Map<String, List<Mark>> marks;

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

    /** List to keep all timeExpressions */
    private List<Timex3> timeExpressions;

	/** List to keep all factualities */
	private List<Factuality> factualities;

	/** List to keep all linked entities */
	private List<LinkedEntity> linkedEntities;

	/** List to keep all opinions */
    private List<Opinion> opinions;

    /** List to keep all relations */
    private List<Relation> relations;

    /** List to keep all predicates */
    private List<Predicate> predicates;

    /** List to keep all trees */
    private List<Tree> trees;

    /** UNKNOWN annotation layers in plain DOM format */
    private List<Element> unknownLayers;

    /** Hash map for mapping word forms to terms. */
    private HashMap<String, List<Term>> termsIndexedByWF;
    private HashMap<String, Map<String, List<Mark>>> marksIndexedByWf;
    private HashMap<String, List<Dep>> depsIndexedByTerm;
    private HashMap<String, List<Chunk>> chunksIndexedByTerm;
    private HashMap<String, List<Entity>> entitiesIndexedByTerm;
    private HashMap<String, List<Coref>> corefsIndexedByTerm;
    private HashMap<String, List<Timex3>> timeExsIndexedByWF;
    private HashMap<String, List<Factuality>> factsIndexedByWF;
    private HashMap<String, List<LinkedEntity>> linkedEntitiesIndexedByWF;
    private HashMap<String, List<Feature>> propertiesIndexedByTerm;
    private HashMap<String, List<Feature>> categoriesIndexedByTerm;
    private HashMap<String, List<Opinion>> opinionsIndexedByTerm;
    private HashMap<String, List<Relation>> relationsIndexedByRelational;
    private HashMap<String, List<Predicate>> predicatesIndexedByTerm;

    HashMap<Integer, List<WF>> textIndexedBySent;
    HashMap<Integer, List<Term>> termsIndexedBySent;
    HashMap<Integer, Map<String, List<Mark>>> marksIndexedBySent;
    HashMap<Integer, List<Entity>> entitiesIndexedBySent;
    HashMap<Integer, List<Dep>> depsIndexedBySent;
    HashMap<Integer, List<Chunk>> chunksIndexedBySent;
    HashMap<Integer, List<Coref>> corefsIndexedBySent;
    HashMap<Integer, List<Timex3>> timeExsIndexedBySent;
    HashMap<Integer, List<Factuality>> factsIndexedBySent;
    HashMap<Integer, List<LinkedEntity>> linkedEntitiesIndexedBySent;
    HashMap<Integer, List<Feature>> propertiesIndexedBySent;
    HashMap<Integer, List<Feature>> categoriesIndexedBySent;
    HashMap<Integer, List<Opinion>> opinionsIndexedBySent;
    HashMap<Integer, List<Relation>> relationsIndexedBySent;
    HashMap<Integer, List<Predicate>> predicatesIndexedBySent;
    HashMap<Integer, List<Tree>> constituentsIndexedBySent;

    HashMap<Integer, LinkedHashSet<Integer>> sentsIndexedByParagraphs;


    /** This creates a new AnnotationContainer object */
    AnnotationContainer() {
	rawText = new String();
	text = new ArrayList();
	nextOffset = 0;
	terms = new ArrayList();
	marks = new HashMap();
	deps = new ArrayList();
	chunks = new ArrayList();
	entities = new ArrayList();
	properties = new ArrayList();
	categories = new ArrayList();
	coreferences = new ArrayList();
	timeExpressions = new ArrayList();
	factualities = new ArrayList();
	linkedEntities = new ArrayList();
	opinions = new ArrayList();
	relations = new ArrayList();
	predicates = new ArrayList();
	trees = new ArrayList();
	unknownLayers = new ArrayList<Element>();

	termsIndexedByWF = new HashMap<String, List<Term>>();
	marksIndexedByWf = new HashMap<String, Map<String, List<Mark>>>();
	depsIndexedByTerm = new HashMap<String, List<Dep>>();
	chunksIndexedByTerm =  new HashMap<String, List<Chunk>>();
	entitiesIndexedByTerm =  new HashMap<String, List<Entity>>();
	corefsIndexedByTerm =  new HashMap<String, List<Coref>>();
	timeExsIndexedByWF =  new HashMap<String, List<Timex3>>();
	linkedEntitiesIndexedByWF =  new HashMap<String, List<LinkedEntity>>();
	factsIndexedByWF = new HashMap<String, List<Factuality>>();
	propertiesIndexedByTerm =  new HashMap<String, List<Feature>>();
	categoriesIndexedByTerm =  new HashMap<String, List<Feature>>();
	opinionsIndexedByTerm =  new HashMap<String, List<Opinion>>();
	relationsIndexedByRelational =  new HashMap<String, List<Relation>>();
	predicatesIndexedByTerm = new HashMap<String, List<Predicate>>();

	textIndexedBySent = new HashMap<Integer, List<WF>>();
	termsIndexedBySent = new HashMap<Integer, List<Term>>();
	marksIndexedBySent = new HashMap<Integer, Map<String, List<Mark>>>();
	entitiesIndexedBySent = new HashMap<Integer, List<Entity>>();
	depsIndexedBySent = new HashMap<Integer, List<Dep>>();
	chunksIndexedBySent = new HashMap<Integer, List<Chunk>>();
	corefsIndexedBySent = new HashMap<Integer, List<Coref>>();
	timeExsIndexedBySent = new HashMap<Integer, List<Timex3>>();
	linkedEntitiesIndexedBySent = new HashMap<Integer, List<LinkedEntity>>();
	factsIndexedBySent =new HashMap<Integer, List<Factuality>>();
	propertiesIndexedBySent = new HashMap<Integer, List<Feature>>();
	categoriesIndexedBySent = new HashMap<Integer, List<Feature>>();
	opinionsIndexedBySent = new HashMap<Integer, List<Opinion>>();
	relationsIndexedBySent = new HashMap<Integer, List<Relation>>();
	predicatesIndexedBySent = new HashMap<Integer, List<Predicate>>();
	constituentsIndexedBySent = new HashMap<Integer, List<Tree>>();

	sentsIndexedByParagraphs = new HashMap<Integer, LinkedHashSet<Integer>>();
    }

    private <T> void indexBySent(T annotation, Integer sent, HashMap<Integer, List<T>> index) {
	if (sent > 0) {
	    if (index.get(sent) == null) {
		index.put(sent, new ArrayList<T>());
	    }
	    index.get(sent).add(annotation);
	}
    }

    private void indexMarkBySent(Mark mark, String source, Integer sent) {
	if (sent > 0) {
	    if (marksIndexedBySent.get(sent) == null) {
		marksIndexedBySent.put(sent, new HashMap<String, List<Mark>>());
	    }
	    if (marksIndexedBySent.get(sent).get(source) == null) {
		marksIndexedBySent.get(sent).put(source, new ArrayList<Mark>());
	    }
	    marksIndexedBySent.get(sent).get(source).add(mark);
	}
    } 

    void indexSentByPara(Integer sent, Integer para) {
	if ((sent > 0) && (para > 0)) {
	    if (this.sentsIndexedByParagraphs.get(para) == null) {
		this.sentsIndexedByParagraphs.put(para, new LinkedHashSet<Integer>());
	    }
	    this.sentsIndexedByParagraphs.get(para).add(sent);
	}
    }

    public List<Integer> getSentsByParagraph(Integer para) {
	return new ArrayList<Integer>(this.sentsIndexedByParagraphs.get(para));
    }

    <T> List<T> getLayerByPara(Integer para, HashMap<Integer, List<T>> index) {
	List<T> layer = new ArrayList<T>();
	for (Integer sent : this.getSentsByParagraph(para)) {
	    layer.addAll(index.get(sent));
	}
	return layer;
    }

    String getRawText() {
	return rawText;
    }

    /** Returns all word forms. */
    List<WF> getText() {
	return text;
    }

    /** Returns all terms */
    List<Term> getTerms() {
	return terms;
    }

    List<String> getMarkSources() {
	return new ArrayList<String>(marks.keySet());
    }

    List<Mark> getMarks(String source) {
	return (marks.get(source) == null) ? new ArrayList<Mark>() : marks.get(source);
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

    /** Returns all timeExpressions */
    List<Timex3> getTimeExs() {
	return timeExpressions;
    }

	List<Factuality> getFactualities() {
		return factualities;
	}

	List<LinkedEntity> getLinkedEntities() {
		return linkedEntities;
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

    /** Returns all unknown layers as a DOM Element list */
    List<Element> getUnknownLayers() {
	return unknownLayers;
    }

    void setRawText(String str) {
	rawText = str;
    }

    /** Adds a word form to the container */
    void add(WF wf) {
	text.add(wf);
	//nextOffset += wf.getLength() + 1;
	this.indexBySent(wf, wf.getSent(), this.textIndexedBySent);
    }

    private <T> void indexAnnotation(T annotation, String hashId, HashMap<String, List<T>> index) {
	if (index.get(hashId) == null) {
	    index.put(hashId, new ArrayList<T>());
	}
	index.get(hashId).add(annotation);
    }

    private void indexMarkByWf(Mark mark, String source, String tid) {
	if (marksIndexedByWf.get(tid) == null) {
	    marksIndexedByWf.put(tid, new HashMap<String, List<Mark>>());
	}
	if (marksIndexedByWf.get(tid).get(source) == null) {
	    marksIndexedByWf.get(tid).put(source, new ArrayList<Mark>());
	}
	marksIndexedByWf.get(tid).get(source).add(mark);
    }

    /** Adds a term to the container */
    void add(Term term) {
	this.add(term, this.terms.size());
    }

    void add(Term term, int index) {
	terms.add(index, term);
	for (WF wf : term.getWFs()) {
	    indexAnnotation(term, wf.getId(), termsIndexedByWF);
	}
	if (!term.isComponent()) {
	    this.indexBySent(term, term.getSent(), this.termsIndexedBySent);
	}
    }

    void remove(Term term) {
	this.terms.remove(term);
    }

    void add(Mark mark, String source) {
	List<Mark> sourceMarks = marks.get(source);
	if (sourceMarks == null) {
	    sourceMarks = new ArrayList<Mark>();
	}
	sourceMarks.add(mark);
	marks.put(source, sourceMarks);
	for (WF wf : mark.getSpan().getTargets()) {
	    indexMarkByWf(mark, source, wf.getId());
	}
        this.indexMarkBySent(mark, source, mark.getSpan().getTargets().get(0).getSent());
    }

    /** Adds a dependency to the container */
    void add(Dep dep) {
	deps.add(dep);
	/* Index by 'from' and 'to' terms */
	if (dep.getFrom() != null) {
	    String tId = dep.getFrom().getId();
	    indexAnnotation(dep, tId, depsIndexedByTerm);
	}
	if (dep.getTo() != null) {
	    String tId = dep.getTo().getId();
	    indexAnnotation(dep, tId, depsIndexedByTerm);
	}
	this.indexBySent(dep, dep.getFrom().getSent(), this.depsIndexedBySent);
    }

    /** Adds a chunk to the container */
    void add(Chunk chunk) {
	chunks.add(chunk);
	/* Index by terms */
	for (Term term : chunk.getTerms()) {
	    indexAnnotation(chunk, term.getId(), chunksIndexedByTerm);
	}
	this.indexBySent(chunk, chunk.getSpan().getTargets().get(0).getSent(), this.chunksIndexedBySent);
    }

    /** Adds a named entity to the container */
    void add(Entity entity) {
	entities.add(entity);
	/* Index by terms */
	for (Term term : entity.getTerms()) {
	    indexAnnotation(entity, term.getId(), entitiesIndexedByTerm);
	}
	this.indexBySent(entity, entity.getSpans().get(0).getTargets().get(0).getSent(), this.entitiesIndexedBySent);
    }

    /** Adds a feature to the container. It checks if it is a property or a category. */
    void add(Feature feature) {
	if (feature.isAProperty()) {
	    properties.add(feature);
	    /* Index by terms */
	    for (Term term : feature.getTerms()) {
		indexAnnotation(feature, term.getId(), propertiesIndexedByTerm);
	    }
	    //this.indexBySent(feature, feature.getSpans().get(0).getTargets().get(0).getSent(), this.propertiesIndexedBySent);
	}
	else {
	    categories.add(feature);
	    /* Index by terms */
	    for (Term term : feature.getTerms()) {
		indexAnnotation(feature, term.getId(), categoriesIndexedByTerm);
	    }
	    //this.indexBySent(feature, feature.getSpans().get(0).getTargets().get(0).getSent(), this.categoriesIndexedBySent);
	}
    }

    /** Adds a coreference to the container */
    void add(Coref coref) {
	coreferences.add(coref);
	/* Index by terms */
	for (Term term : coref.getTerms()) {
	    indexAnnotation(coref, term.getId(), corefsIndexedByTerm);
	}
	//this.indexBySent(coref, coref.getSpans().get(0).getTargets().get(0).getSent(), this.corefsIndexedBySent);
    }

    /** Adds a timeExpression to the container */
    void add(Timex3 timex3) {
	timeExpressions.add(timex3);
	/* Index by terms */
	if(timex3.getWFs() != null){
	    for (WF wf : timex3.getWFs()) {
		indexAnnotation(timex3, wf.getId(), timeExsIndexedByWF);
	    }
	}
    }

	/** Adds a factuality to the container */
	void add(Factuality factuality) {
	    factualities.add(factuality);
	    /* Index by terms */
	    indexAnnotation(factuality, factuality.getWF().getId(), factsIndexedByWF);
	}

	/** Adds a linked entity to the container */
	void add(LinkedEntity linkedEntity) {
		linkedEntities.add(linkedEntity);
	/* Index by terms */
		if(linkedEntity.getWFs() != null){
			for (WF wf : linkedEntity.getWFs().getTargets()) {
				indexAnnotation(linkedEntity, wf.getId(), linkedEntitiesIndexedByWF);
			}
		}
	}

	/** Adds an opinion to the container */
    void add(Opinion opinion) {
	opinions.add(opinion);
	/* Index by terms */
	/* Ezin hemen indexatu, terminoak oraindik ez baitira gehitu!!!
	LinkedHashSet<Term> terms = new LinkedHashSet<Term>();
	terms.addAll(opinion.getOpinionHolder().getTerms());
	terms.addAll(opinion.getOpinionTarget().getTerms());
	terms.addAll(opinion.getOpinionExpression().getTerms());	
	for (Term term : terms) {
	    indexAnnotation(opinion, term.getId(), opinionsIndexedByTerm);
	}
	*/

    }

    /** Adds a relation to the container */
    void add(Relation relation) {
	relations.add(relation);
	/* Index by 'from' and 'to' terms */
	if (relation.getFrom() != null) {
	    String rId = relation.getFrom().getId();
	    indexAnnotation(relation, rId, relationsIndexedByRelational);
	}
	if (relation.getTo() != null) {
	    String rId = relation.getTo().getId();
	    indexAnnotation(relation, rId, relationsIndexedByRelational);
	}
    }

    /** Adds a predicate to the container */
    void add(Predicate predicate) {
	predicates.add(predicate);
	/* Index by terms */
	for (Term term : predicate.getTerms()) {
	    indexAnnotation(predicate, term.getId(), predicatesIndexedByTerm);
	}
	this.indexBySent(predicate, predicate.getSpan().getTargets().get(0).getSent(), this.predicatesIndexedBySent);
    }

    /** Adds a tree to the container */
    void add(Tree tree) {
	trees.add(tree);
	TreeNode currentNode = tree.getRoot();
	while (!currentNode.isTerminal()) {
	    currentNode = ((NonTerminal) currentNode).getChildren().get(0);
	}
	Integer sent = ((Terminal) currentNode).getSpan().getTargets().get(0).getSent(); 
	this.indexBySent(tree, sent, this.constituentsIndexedBySent);
    }

    /** Adds an unknown layer to the container in DOM format */
    void add(Element layer) {
	unknownLayers.add(layer);
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

    Integer termPosition(Term term) {
	return this.terms.indexOf(term);
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
	List<Term> terms = this.termsIndexedByWF.get(wf.getId());
	if (terms == null) {
	    return null;
	}
	return terms.get(0);
    }

    List<Term> getTermsByWF(WF wf) {
	List<Term> terms = this.termsIndexedByWF.get(wf.getId());
	return (terms == null) ? new ArrayList<Term>() : terms;
    }

    /** Returns a list of terms containing the word forms given on argument.
     * @param wfIds a list of word form IDs whose terms will be found.
     * @return a list of terms containing the given word forms.
     */
    List<Term> getTermsByWFs(List<WF> wfs) {
	LinkedHashSet<Term> terms = new LinkedHashSet<Term>();
	for (WF wf : wfs) {
	    terms.addAll(getTermsByWF(wf));
	}
	return new ArrayList<Term>(terms);
    }

    List<Mark> getMarksByWf(WF wf, String source) {
	Map<String, List<Mark>> marks = this.marksIndexedByWf.get(wf.getId());
	if (marks == null) {
	    return new ArrayList<Mark>();
	}
	List<Mark> sourceMarks = marks.get(source);
	return (sourceMarks == null) ? new ArrayList<Mark>() : sourceMarks;
    }

    List<Dep> getDepsByTerm(Term term) {
	List<Dep> deps = this.depsIndexedByTerm.get(term.getId());
	return (deps == null) ? new ArrayList<Dep>() : deps;
    }

    List<Chunk> getChunksByTerm(Term term) {
	List<Chunk> chunks = this.chunksIndexedByTerm.get(term.getId());
	return (chunks == null) ? new ArrayList<Chunk>() : chunks;
    }

    List<Entity> getEntitiesByTerm(Term term) {
	List<Entity> entities = this.entitiesIndexedByTerm.get(term.getId());
	return (entities == null) ? new ArrayList<Entity>() : entities;
    }

    List<Coref> getCorefsByTerm(Term term) {
	List<Coref> corefs = this.corefsIndexedByTerm.get(term.getId());
	return (corefs == null) ? new ArrayList<Coref>() : corefs;
    }

    List<Timex3> getTimeExsByWF(WF wf) {
	List<Timex3> timeExs = this.timeExsIndexedByWF.get(wf.getId());
	return (timeExs == null) ? new ArrayList<Timex3>() : timeExs;
    }

    List<Feature> getPropertiesByTerm(Term term) {
	List<Feature> properties = this.propertiesIndexedByTerm.get(term.getId());
	return (properties == null) ? new ArrayList<Feature>() : properties;
    }

    List<Feature> getCategoriesByTerm(Term term) {
	List<Feature> categories = this.categoriesIndexedByTerm.get(term.getId());
	return (categories == null) ? new ArrayList<Feature>() : categories;
    }

    List<Opinion> getOpinionsByTerm(Term term) {
	List<Opinion> opinions = this.opinionsIndexedByTerm.get(term.getId());
	return (opinions == null) ? new ArrayList<Opinion>() : opinions;
    }

    List<Relation> getRelationsByRelational(Relational relational) {
	List<Relation> relations = this.relationsIndexedByRelational.get(relational.getId());
	return (relations == null) ? new ArrayList<Relation>() : relations;
    }

    List<Predicate> getPredicatesByTerm(Term term) {
	List<Predicate> predicates = this.predicatesIndexedByTerm.get(term.getId());
	return (predicates == null) ? new ArrayList<Predicate>() : predicates;
    }

    List<Dep> getDepsByTerms(List<Term> terms) {
	LinkedHashSet<Dep> deps = new LinkedHashSet<Dep>();
	for (Term term : terms) {
	    deps.addAll(getDepsByTerm(term));
	}
	return new ArrayList<Dep>(deps);
    }

    List<Chunk> getChunksByTerms(List<Term> terms) {
	LinkedHashSet<Chunk> chunks = new LinkedHashSet<Chunk>();
	for (Term term : terms) {
	    chunks.addAll(getChunksByTerm(term));
	}
	return new ArrayList<Chunk>(chunks);
    }

    List<Entity> getEntitiesByTerms(List<Term> terms) {
	LinkedHashSet<Entity> entities = new LinkedHashSet<Entity>();
	for (Term term : terms) {
	    entities.addAll(getEntitiesByTerm(term));
	}
	return new ArrayList<Entity>(entities);
    }

    List<Coref> getCorefsByTerms(List<Term> terms) {
	LinkedHashSet<Coref> corefs = new LinkedHashSet<Coref>();
	for (Term term : terms) {
	    corefs.addAll(getCorefsByTerm(term));
	}
	return new ArrayList<Coref>(corefs);
    }

    List<Timex3> getTimeExsByWFs(List<WF> wfs) {
	LinkedHashSet<Timex3> timeExs = new LinkedHashSet<Timex3>();
	for (WF wf : wfs) {
	    timeExs.addAll(getTimeExsByWF(wf));
	}
	return new ArrayList<Timex3>(timeExs);
    }

    List<Feature> getPropertiesByTerms(List<Term> terms) {
	LinkedHashSet<Feature> properties = new LinkedHashSet<Feature>();
	for (Term term : terms) {
	    properties.addAll(getPropertiesByTerm(term));
	}
	return new ArrayList<Feature>(properties);
    }

    List<Feature> getCategoriesByTerms(List<Term> terms) {
	LinkedHashSet<Feature> categories = new LinkedHashSet<Feature>();
	for (Term term : terms) {
	    categories.addAll(getCategoriesByTerm(term));
	}
	return new ArrayList<Feature>(categories);
    }

    List<Opinion> getOpinionsByTerms(List<Term> terms) {
	LinkedHashSet<Opinion> opinions = new LinkedHashSet<Opinion>();
	for (Term term : terms) {
	    opinions.addAll(getOpinionsByTerm(term));
	}
	return new ArrayList<Opinion>(opinions);
    }

    List<Relation> getRelationsByRelationals(List<Relational> relationals) {
	LinkedHashSet<Relation> relations = new LinkedHashSet<Relation>();
	for (Relational relational : relationals) {
	    relations.addAll(getRelationsByRelational(relational));
	}
	return new ArrayList<Relation>(relations);
    }

    List<Predicate> getPredicatesByTerms(List<Term> terms) {
	LinkedHashSet<Predicate> predicates = new LinkedHashSet<Predicate>();
	for (Term term : terms) {
	    predicates.addAll(getPredicatesByTerm(term));
	}
	return new ArrayList<Predicate>(predicates);
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
	    terms.addAll(this.termsIndexedByWF.get(wfId));
	}
	return new ArrayList<Term>(terms);
    }

    void removeLayer(KAFDocument.Layer layer) {
	switch (layer) {
	case text:
	    this.text.clear();
	    break;
	case terms:
	    this.terms.clear();
	    break;
	case deps:
	    this.deps.clear();
	    break;
	case chunks:
	    this.chunks.clear();
	    break;
	case entities:
	    this.entities.clear();
	    break;
	case properties:
	    this.properties.clear();
	    break;
	case categories:
	    this.categories.clear();
	    break;
	case coreferences:
	    this.coreferences.clear();
	    break;
	case opinions:
	    this.opinions.clear();
	    break;
	case relations:
	    this.relations.clear();
	    break;
	case srl:
	    this.predicates.clear();
	    break;
	case constituency:
	    this.trees.clear();
	    break;
	default:
	    throw new IllegalArgumentException("Wrong layer");
	}
    }
}

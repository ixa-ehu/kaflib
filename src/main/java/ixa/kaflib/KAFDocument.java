package ixa.kaflib;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import org.jdom2.JDOMException;


/** Respresents a KAF document. It's the main class of the library, as it keeps all elements of the document (word forms, terms, entities...) and manages all object creations. The document can be created by the user calling it's methods, or loading from an existing XML file.*/

public class KAFDocument {


    public class FileDesc {
	String author;
	String title;
	String creationtime;
	String filename;
	String filetype;
	Integer pages;

	private FileDesc() {}
    }

    public class Public {
	String publicId;
	String uri;
    
	private Public(String publicId) {
	    this.publicId = publicId;
	}
    }

    public class LinguisticProcessor {
	String name;
	String timestamp;
	String version;

	private LinguisticProcessor(String name, String timestamp, String version) {
	    this.name = name;
	    this.timestamp = timestamp;
	    this.version = version;
	}
    }

    /** Language identifier */
    private String lang;

    /** KAF version */
    private String version;

    /** Linguistic processors */
    private HashMap<String, List<LinguisticProcessor>> lps;

    private FileDesc fileDesc;

    private Public _public;

    /** Identifier manager */
    private IdManager idManager;

    /** Keeps all the annotations of the document */
    private AnnotationContainer annotationContainer;

    /** Creates an empty KAFDocument element */
    public KAFDocument(String lang, String version) {
	this.lang = lang;
	this.version = version;
	lps = new HashMap<String, List<LinguisticProcessor>>();
	idManager = new IdManager();
	annotationContainer = new AnnotationContainer();
    }

    /** Creates a new KAFDocument and loads the contents of the file passed as argument
     * @param file an existing KAF file to be loaded into the library.
     */
    public static KAFDocument createFromFile(File file) throws IOException, JDOMException {
	return ReadWriteManager.load(file);
    }

    /** Creates a new KAFDocument loading the content read from the reader given on argument.
     * @param stream Reader to read KAF content.
     */
    public static KAFDocument createFromStream(Reader stream) throws IOException, JDOMException {
	return ReadWriteManager.load(stream);
    }

    /** Sets the language of the processed document */
    public void setLang(String lang) {
	this.lang = lang;
    }

    /** Returns the language of the processed document */
    public String getLang() {
	return lang;
    }

    /** Sets the KAF version */
    public void setVersion(String version) {
	this.version = version;
    }

    /** Returns the KAF version */
    public String getVersion() {
	return version;
    }

    /** Adds a linguistic processor to the document header. The timestamp is added implicitly. */
    public LinguisticProcessor addLinguisticProcessor(String layer, String name, String version) {
	String timestamp = this.getTimestamp();
	LinguisticProcessor lp = new LinguisticProcessor(name, timestamp, version);
	List<LinguisticProcessor> layerLps = lps.get(layer);
	if (layerLps == null) {
	    layerLps = new ArrayList<LinguisticProcessor>();
	    lps.put(layer, layerLps);
	}
	layerLps.add(lp);
	return lp;
    }

    /** Adds a linguistic processor to the document header */
    public LinguisticProcessor addLinguisticProcessor(String layer, String name, String timestamp, String version) {
	LinguisticProcessor lp = new LinguisticProcessor(name, timestamp, version);
	List<LinguisticProcessor> layerLps = lps.get(layer);
	if (layerLps == null) {
	    layerLps = new ArrayList<LinguisticProcessor>();
	    lps.put(layer, layerLps);
	}
	layerLps.add(lp);
	return lp;
    }	

    /** Returns a list of linguistic processors from the document */
    public HashMap<String, List<LinguisticProcessor>> getLinguisticProcessors() {
	return lps;
    }

    /** Returns wether the given linguistic processor is already defined or not. */
    public boolean linguisticProcessorExists(String layer, String name, String version) {
	List<LinguisticProcessor> layerLPs = lps.get(layer);
	if (layerLPs == null) {
	    return false;
	}
	for (LinguisticProcessor lp : layerLPs) {
	    if (lp.name.equals(name) && lp.version.equals(version)) {
		return true;
	    }
	}
	return false;
    }

    public FileDesc createFileDesc() {
	this.fileDesc = new FileDesc();
	return this.fileDesc;
    }

    public FileDesc getFileDesc() {
	return this.fileDesc;
    }

    public Public createPublic(String publicId) {
	this._public = new Public(publicId);
	return this._public;
    }

    public Public getPublic() {
	return this._public;
    }

    /** Returns the annotation container used by this object */
    AnnotationContainer getAnnotationContainer() {
	return annotationContainer;
    }

    /** Creates a WF object to load an existing word form. It receives the ID as an argument. The WF is added to the document object.
     * @param id word form's ID.
     * @param form text of the word form itself.
     * @return a new word form.
     */
    public WF createWF(String id, String form) {
	idManager.updateWFCounter(id);
	WF newWF = new WF(annotationContainer, id, form);
	annotationContainer.add(newWF);
	return newWF;
    }

    /** Creates a new WF object. It assigns an appropriate ID to it.  The WF is added to the document object.
     * @param form text of the word form itself.
     * @return a new word form.
     */
    public WF createWF(String form) {
	String newId = idManager.getNextWFId();
	//int offset = annotationContainer.getNextOffset();
	WF newWF = new WF(annotationContainer, newId, form);
	//newWF.setOffset(offset);
	//newWF.setLength(form.length());
	annotationContainer.add(newWF);
	return newWF;
    }

    /** Creates a new WF object. It assigns an appropriate ID to it and it also assigns offset and length
     * attributes. The WF is added to the document object.
     * @param form text of the word form itself.
     * @return a new word form.
     */
    public WF createWF(String form, int offset) {
	String newId = idManager.getNextWFId();
	int offsetVal = offset;
	WF newWF = new WF(annotationContainer, newId, form);
	newWF.setOffset(offsetVal);
	newWF.setLength(form.length());
	annotationContainer.add(newWF);
	return newWF;
    }

    /** Creates a Term object to load an existing term. It receives the ID as an argument. The Term is added to the document object.
     * @param id term's ID.
     * @param type type of term. There are two types of term: open and close.
     * @param lemma the lemma of the term.
     * @param pos part of speech of the term.
     * @param wfs the list of word forms this term is formed by.
     * @return a new term.
     */
    public Term createTerm(String id, String type, String lemma, String pos, List<WF> wfs) {
	idManager.updateTermCounter(id);
	Term newTerm = new Term(annotationContainer, id, type, lemma, pos, wfs);
	annotationContainer.add(newTerm);
	return newTerm;
    }

    /** Creates a new Term. It assigns an appropriate ID to it. The Term is added to the document object.
     * @param type the type of the term. There are two types of term: open and close.
     * @param lemma the lemma of the term.
     * @param pos part of speech of the term.
     * @param wfs the list of word forms this term is formed by.
     * @return a new term.
     */
    public Term createTerm(String type, String lemma, String pos, List<WF> wfs) {
	String newId = idManager.getNextTermId();
	Term newTerm = new Term(annotationContainer, newId, type, lemma, pos, wfs);
	annotationContainer.add(newTerm);
	return newTerm;
    }

    /** Creates a new Term. It assigns an appropriate ID to it. The Term is added to the document object.
     * @param type the type of the term. There are two types of term: open and close.
     * @param lemma the lemma of the term.
     * @param pos part of speech of the term.
     * @param wfs the list of word forms this term is formed by.
     * @return a new term.
     */
    public Term createTermOptions(String type, String lemma, String pos, String morphofeat, List<WF> wfs) {
	String newId = idManager.getNextTermId();
	Term newTerm = new Term(annotationContainer, newId, type, lemma, pos, morphofeat, wfs);
	annotationContainer.add(newTerm);
	return newTerm;
    }

    /** Creates a Sentiment object.
     * @return a new sentiment.
     */
    public Term.Sentiment createSentiment() {
	Term.Sentiment newSentiment = new Term.Sentiment();
	return newSentiment;
    }

    /** Creates a Component object to load an existing component. It receives the ID as an argument. It doesn't add the component to the term.
     * @param id component's ID.
     * @param term the term which this component is part of.
     * @param lemma lemma of the component.
     * @param pos part of speech of the component.
     * @return a new component.
     */
     public Term.Component createComponent(String id, Term term, String lemma, String pos) {
	idManager.updateComponentCounter(id, term.getId());
	Term.Component newComponent = new Term.Component(id, lemma, pos);
	return newComponent;
    }

    /** Creates a new Component. It assigns an appropriate ID to it. It uses the ID of the term to create a new ID for the component. It doesn't add the component to the term.
     * @param term the term which this component is part of.
     * @param lemma lemma of the component.
     * @param pos part of speech of the component.
     * @return a new component.
     */
    public Term.Component createComponent(Term term, String lemma, String pos) {
	String newId = idManager.getNextComponentId(term.getId());
	Term.Component newComponent = new Term.Component(newId, lemma, pos);
	return newComponent;
    }

    /** Creates a new dependency. The Dep is added to the document object.
     * @param from the origin term of the dependency.
     * @param to the target term of the dependency.
     * @param rfunc relational function of the dependency.
     * @return a new dependency.
     */
    public Dep createDep(Term from, Term to, String rfunc) {
	Dep newDep = new Dep(annotationContainer, from.getId(), to.getId(), rfunc);
	annotationContainer.add(newDep);
	return newDep;
    }

    /** Creates a chunk object to load an existing chunk. It receives it's ID as an argument. The Chunk is added to the document object.
     * @param id chunk's ID.
     * @param head the chunk head.
     * @param phrase type of the phrase.
     * @param terms the list of the terms in the chunk.
     * @return a new chunk.
     */
    public Chunk createChunk(String id, Term head, String phrase, List<Term> terms) {
	idManager.updateChunkCounter(id);
	Chunk newChunk = new Chunk(annotationContainer, id, head.getId(), phrase, terms);
	annotationContainer.add(newChunk);
	return newChunk;
    }

    /** Creates a new chunk. It assigns an appropriate ID to it. The Chunk is added to the document object.
     * @param head the chunk head.
     * @param phrase type of the phrase.
     * @param terms the list of the terms in the chunk.
     * @return a new chunk.
     */
    public Chunk createChunk(Term head, String phrase, List<Term> terms) {
	String newId = idManager.getNextChunkId();
	Chunk newChunk = new Chunk(annotationContainer, newId, head.getId(), phrase, terms);
	annotationContainer.add(newChunk);
	return newChunk;
    }

    /** Creates an Entity object to load an existing entity. It receives the ID as an argument. The entity is added to the document object.
     * @param id the ID of the named entity.
     * @param type entity type. 8 values are posible: Person, Organization, Location, Date, Time, Money, Percent, Misc.
     * @param references it contains one or more span elements. A span can be used to reference the different occurrences of the same named entity in the document. If the entity is composed by multiple words, multiple target elements are used.
     * @return a new named entity.
     */
    public Entity createEntity(String id, String type, List<List<Term>> references) {
	idManager.updateEntityCounter(id);
	Entity newEntity = new Entity(annotationContainer, id, type, references);
	annotationContainer.add(newEntity);
	return newEntity;
    }

    /** Creates a new Entity. It assigns an appropriate ID to it. The entity is added to the document object.
     * @param type entity type. 8 values are posible: Person, Organization, Location, Date, Time, Money, Percent, Misc.
     * @param references it contains one or more span elements. A span can be used to reference the different occurrences of the same named entity in the document. If the entity is composed by multiple words, multiple target elements are used.
     * @return a new named entity.
     */
    public Entity createEntity(String type, List<List<Term>> references) {
	String newId = idManager.getNextEntityId();
	Entity newEntity = new Entity(annotationContainer, newId, type, references);
	annotationContainer.add(newEntity);
	return newEntity;
    }

    /** Creates a coreference object to load an existing Coref. It receives it's ID as an argument. The Coref is added to the document.
     * @param id the ID of the coreference.
     * @param references different mentions (list of targets) to the same entity.
     * @return a new coreference.
     */
    public Coref createCoref(String id, List<List<Target>> references) {
	idManager.updateEntityCounter(id);
	Coref newCoref = new Coref(annotationContainer, id, references);
	annotationContainer.add(newCoref);
	return newCoref;
    }

    /** Creates a new coreference. It assigns an appropriate ID to it. The Coref is added to the document.
     * @param references different mentions (list of targets) to the same entity.
     * @return a new coreference.
     */
    public Coref createCoref(List<List<Target>> references) {
	String newId = idManager.getNextCorefId();
	Coref newCoref = new Coref(annotationContainer, newId, references);
	annotationContainer.add(newCoref);
	return newCoref;
    }

    /** Creates a new property. It receives it's ID as an argument. The property is added to the document.
     * @param id the ID of the property.
     * @param lemma the lemma of the property.
     * @param references different mentions (list of targets) to the same property.
     * @return a new coreference.
     */
    public Feature createProperty(String id, String lemma, List<List<Term>> references) {
	idManager.updatePropertyCounter(id);
	Feature newProperty = new Feature(annotationContainer, id, lemma, references);
	annotationContainer.add(newProperty);
	return newProperty;
    }

    /** Creates a new property. It assigns an appropriate ID to it. The property is added to the document.
     * @param lemma the lemma of the property.
     * @param references different mentions (list of targets) to the same property.
     * @return a new coreference.
     */
    public Feature createProperty(String lemma, List<List<Term>> references) {
	String newId = idManager.getNextPropertyId();
	Feature newProperty = new Feature(annotationContainer, newId, lemma, references);
	annotationContainer.add(newProperty);
	return newProperty;
    }

    /** Creates a new category. It receives it's ID as an argument. The category is added to the document.
     * @param id the ID of the category.
     * @param lemma the lemma of the category.
     * @param references different mentions (list of targets) to the same category.
     * @return a new coreference.
     */
    public Feature createCategory(String id, String lemma, List<List<Term>> references) {
	idManager.updateCategoryCounter(id);
	Feature newCategory = new Feature(annotationContainer, id, lemma, references);
	annotationContainer.add(newCategory);
	return newCategory;
    }

    /** Creates a new category. It assigns an appropriate ID to it. The category is added to the document.
     * @param lemma the lemma of the category.
     * @param references different mentions (list of targets) to the same category.
     * @return a new coreference.
     */
    public Feature createCategory(String lemma, List<List<Term>> references) {
	String newId = idManager.getNextCategoryId();
	Feature newCategory = new Feature(annotationContainer, newId, lemma, references);
	annotationContainer.add(newCategory);
	return newCategory;
    }

    /** Creates a new opinion object. It assigns an appropriate ID to it. The opinion is added to the document.
     * @return a new opinion.
     */
    public Opinion createOpinion() {
	String newId = idManager.getNextOpinionId();
	Opinion newOpinion = new Opinion(annotationContainer, newId);
	annotationContainer.add(newOpinion);
	return newOpinion;
    }

    /** Creates a new opinion object. It receives its ID as an argument. The opinion is added to the document.
     * @return a new opinion.
     */
    public Opinion createOpinion(String id) {
        idManager.updateOpinionCounter(id);
	Opinion newOpinion = new Opinion(annotationContainer, id);
	annotationContainer.add(newOpinion);
	return newOpinion;
    }

    /** Creates a new relation between entities and/or sentiment features. It assigns an appropriate ID to it. The relation is added to the document.
     * @param from source of the relation
     * @param to target of the relation
     * @return a new relation
     */
    public Relation createRelation(Relational from, Relational to) {
	String newId = idManager.getNextRelationId();
	Relation newRelation = new Relation(annotationContainer, newId, from, to);
	annotationContainer.add(newRelation);
	return newRelation;
    }

    /** Creates a new relation between entities and/or sentiment features. It receives its ID as an argument. The relation is added to the document.
     * @param id the ID of the relation
     * @param from source of the relation
     * @param to target of the relation
     * @return a new relation
     */
    public Relation createRelation(String id, Relational from, Relational to) {
	idManager.updateRelationCounter(id);
	Relation newRelation = new Relation(annotationContainer, id, from, to);
	annotationContainer.add(newRelation);
	return newRelation;
    }

    /** Creates a new target. This method is overloaded. Any target created by calling this method won't be the head term.
     * @param term target term.
     * @return a new target.
     */
    public Target createTarget(Term term) {
	return new Target(annotationContainer, term.getId(), false);
    }

    /** Creates a new target. This method is overloaded. In this case, it receives a boolean argument which defines whether the target term is the head or not.
     * @param term target term.
     * @param isHead a boolean argument which defines whether the target term is the head or not.
     * @return a new target.
     */
    public Target createTarget(Term term, boolean isHead) {
	return new Target(annotationContainer, term.getId(), isHead);
    }

    /** Creates a new external reference.
     * @param resource indicates the identifier of the resource referred to.
     * @param reference code of the referred element.
     * @return a new external reference object.
     */
    public ExternalRef createExternalRef(String resource, String reference) {
	return new ExternalRef(resource, reference);
    }

    public Tree createParsingTree(String id) {
	Tree tree = new Tree(annotationContainer, id);
	annotationContainer.add(tree);
	return tree;
    }

    public Tree createParsingTree() {
	Tree tree = new Tree(annotationContainer, "idX");
	annotationContainer.add(tree);
	return tree;
    }

    /** Returns a list containing all WFs in the document */
    public List<WF> getWFs() {
	return annotationContainer.getText();
    }

    /** Returns a list with all sentences. Each sentence is a list of WFs. */
    public List<List<WF>> getSentences() {
	return annotationContainer.getSentences();
    }

    /** Returns a list with all terms in the document. */
    public List<Term> getTerms() {
	return annotationContainer.getTerms();
    }

    /** Returns a list of terms containing the word forms given on argument.
     * @param wfIds a list of word form IDs whose terms will be found.
     * @return a list of terms containing the given word forms.
     */
    public List<Term> getTermsFromWFs(List<String> wfIds) {
	return annotationContainer.getTermsFromWFs(wfIds);
    }

    /** Returns a list with all entities in the document */
    public List<Entity> getEntities() {
	return annotationContainer.getEntities();
    }

    /** Returns a list with all relations in the document */
    public List<Feature> getProperties() {
	return annotationContainer.getProperties();
    }

    /** Returns a list with all relations in the document */
    public List<Feature> getCategories() {
	return annotationContainer.getCategories();
    }

    /** Returns a list with all relations in the document */
    public List<Relation> getRelations() {
	return annotationContainer.getRelations();
    }

    /** Returns current timestamp. */
    public String getTimestamp() {
	Date date = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
	//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD'T'kk:mm:ssZ");
	String formattedDate = sdf.format(date);
	return formattedDate;
    }

    /** Merges the document with another one. If there are any conflicting values, the values of this object will be kept. **/
    public void merge(KAFDocument doc) {
	// Linguistic processors
	HashMap<String, List<LinguisticProcessor>> lps = doc.getLinguisticProcessors();
	for (Map.Entry<String, List<LinguisticProcessor>> entry : lps.entrySet()) {
	    String layer = entry.getKey();
	    List<LinguisticProcessor> lpList = entry.getValue();
	    for (LinguisticProcessor lp : lpList) {
		if (!this.linguisticProcessorExists(layer, lp.name, lp.version)) {
		    this.addLinguisticProcessor(layer, lp.name, lp.timestamp, lp.version);
		}
	    }
	}
	// WFs
	for (WF wf : doc.getWFs()) {
	    this.insertWF(wf);
	}
	// Terms
	for (Term term : doc.getTerms()) {
	    this.insertTerm(term);
	}
    }

    private String insertWF(WF wf) {
	String newId = idManager.getNextWFId();
	wf.setId(newId);
        annotationContainer.add(wf);
	return newId;
    }

    private String insertTerm(Term term) {
	String newId = idManager.getNextTermId();
	term.setId(newId);
        annotationContainer.add(term);
	return newId;
    }

    /** Saves the KAF document to an XML file.
     * @param filename name of the file in which the document will be saved.
     */
    public void save(String filename) {
	ReadWriteManager.save(this, filename);
    }

    public String toString() {
	return ReadWriteManager.kafToStr(this);
    }

    /** Prints the document on standard output. */
    public void print() {
	ReadWriteManager.print(this);
    }
}

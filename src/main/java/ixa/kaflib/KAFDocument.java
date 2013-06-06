package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import org.jdom2.JDOMException;


/** Respresents a KAF document. It's the main class of the library, as it keeps all elements of the document (word forms, terms, entities...) and manages all object creations. The document can be created by the user calling it's methods, or loading from an existing XML file.*/

public class KAFDocument {

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
    public void addLinguisticProcessor(String layer, String name, String version) {
	String timestamp = this.getTimestamp();
	LinguisticProcessor lp = new LinguisticProcessor(name, timestamp, version);
	List<LinguisticProcessor> layerLps = lps.get(layer);
	if (layerLps == null) {
	    layerLps = new ArrayList<LinguisticProcessor>();
	    lps.put(layer, layerLps);
	}
	layerLps.add(lp);
    }

    /** Adds a linguistic processor to the document header */
    public void addLinguisticProcessor(String layer, String name, String timestamp, String version) {
	LinguisticProcessor lp = new LinguisticProcessor(name, timestamp, version);
	List<LinguisticProcessor> layerLps = lps.get(layer);
	if (layerLps == null) {
	    layerLps = new ArrayList<LinguisticProcessor>();
	    lps.put(layer, layerLps);
	}
	layerLps.add(lp);
    }

    /** Returns a list of linguistic processors from the document */
    public HashMap<String, List<LinguisticProcessor>> getLinguisticProcessors() {
	return lps;
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
	WF newWF = new WF(id, form);
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
	WF newWF = new WF(newId, form);
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
	WF newWF = new WF(newId, form);
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

    /** Creates a new target. This method is overloaded. Any target created by calling this method won't be the head term.
     * @param term target term.
     * @return a new target.
     */
    public Target createTarget(Term term) {
	return new Target(annotationContainer, term.getId(), false);
    }


    /** Creates a new target. This method is overloaded. In this case, it receives a boolean argument which defines whether the target term is the head or not.
     * @param term target term.
     * @param a boolean argument which defines whether the target term is the head or not.
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

    /** Returns current timestamp. */
    public String getTimestamp() { 
	Date date = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
	String formattedDate = sdf.format(date);
	return formattedDate;
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
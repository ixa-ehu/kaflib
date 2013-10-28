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

	private LinguisticProcessor(String name) {
	    this.name = name;
	}

	/* Deprecated */
	private LinguisticProcessor(String name, String timestamp, String version) {
	    this.name = name;
	    this.timestamp = timestamp;
	    this.version = version;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getName() {
	    return name;
	}

	public boolean hasTimestamp() {
	    return timestamp != null;
	}

	public void setTimestamp(String timestamp) {
	    this.timestamp = timestamp;
	}

	public String getTimestamp() {
	    return timestamp;
	}

	public boolean hasVersion() {
	    return version != null;
	}

	public void setVersion(String version) {
	    this.version = version;
	}

	public String getVersion() {
	    return version;
	}
    }

    /** Language identifier */
    private String lang;

    /** KAF version */
    private String version;

    /** Linguistic processors */
    private Map<String, List<LinguisticProcessor>> lps;

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
	lps = new LinkedHashMap<String, List<LinguisticProcessor>>();
	idManager = new IdManager();
	annotationContainer = new AnnotationContainer();
    }

    /** Creates a new KAFDocument and loads the contents of the file passed as argument
     * @param file an existing KAF file to be loaded into the library.
     */
    public static KAFDocument createFromFile(File file) throws IOException {
	KAFDocument kaf = null;
	try {
	    kaf = ReadWriteManager.load(file);
	} catch(JDOMException e) {
	    e.printStackTrace();
	}
	return kaf;
    }

    /** Creates a new KAFDocument loading the content read from the reader given on argument.
     * @param stream Reader to read KAF content.
     */
    public static KAFDocument createFromStream(Reader stream) throws IOException {
	KAFDocument kaf = null;
	try {
	    kaf = ReadWriteManager.load(stream);
	} catch(JDOMException e) {
	    e.printStackTrace();
	}
	return kaf;
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
    public LinguisticProcessor addLinguisticProcessor(String layer, String name) {
	String timestamp = this.getTimestamp();
	LinguisticProcessor lp = new LinguisticProcessor(name);
	lp.setTimestamp(timestamp);
	List<LinguisticProcessor> layerLps = lps.get(layer);
	if (layerLps == null) {
	    layerLps = new ArrayList<LinguisticProcessor>();
	    lps.put(layer, layerLps);
	}
	layerLps.add(lp);
	return lp;
    }

    public void addLinguisticProcessors(Map<String, List<LinguisticProcessor>> lps) {
	for (Map.Entry<String, List<LinguisticProcessor>> entry : lps.entrySet()) {
	    List<LinguisticProcessor> layerLps = entry.getValue();
	    for (LinguisticProcessor lp : layerLps) {
		this.addLinguisticProcessor(entry.getKey(),
					lp.name,
					lp.timestamp,
					lp.version);
	    }
	}
    }
    
    /** Returns a hash of linguistic processors from the document.
     *  Hash: layer => LP
     */
    public Map<String, List<LinguisticProcessor>> getLinguisticProcessors() {
	return lps;
    }

    /** Returns wether the given linguistic processor is already defined or not. Both name and version must be exactly the same. */
    public boolean linguisticProcessorExists(String layer, String name, String version) {
	List<LinguisticProcessor> layerLPs = lps.get(layer);
	if (layerLPs == null) {
	    return false;
	}
	for (LinguisticProcessor lp : layerLPs) {
	    if (lp.version == null) {
		return false;
	    }
	    else if (lp.name.equals(name) && lp.version.equals(version)) {
		return true;
	    }
	}
	return false;
    }

    /** Returns wether the given linguistic processor is already defined or not. Both name and version must be exactly the same. */
    public boolean linguisticProcessorExists(String layer, String name) {
	List<LinguisticProcessor> layerLPs = lps.get(layer);
	if (layerLPs == null) {
	    return false;
	}
	for (LinguisticProcessor lp : layerLPs) {
	    if (lp.version != null) {
		return false;
	    }
	    else if (lp.name.equals(name)) {
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

    /** Set raw text **/
    public void setRawText(String rawText) {
	annotationContainer.setRawText(rawText);
    }

    /** Creates a WF object to load an existing word form. It receives the ID as an argument. The WF is added to the document object.
     * @param id word form's ID.
     * @param form text of the word form itself.
     * @return a new word form.
     */
    public WF newWF(String id, String form, int sent) {
	idManager.updateWFCounter(id);
	WF newWF = new WF(this.annotationContainer, id, form, sent);
	annotationContainer.add(newWF);
	return newWF;
    }

    /** Creates a new WF object. It assigns an appropriate ID to it.  The WF is added to the document object.
     * @param form text of the word form itself.
     * @return a new word form.
     */
    public WF newWF(String form, int offset) {
	String newId = idManager.getNextWFId();
	int offsetVal = offset;
	WF newWF = new WF(this.annotationContainer, newId, form, 0);
	newWF.setOffset(offsetVal);
	newWF.setLength(form.length());
	annotationContainer.add(newWF);
	return newWF;
    }

    /** Creates a new WF object. It assigns an appropriate ID to it and it also assigns offset and length
     * attributes. The WF is added to the document object.
     * @param form text of the word form itself.
     * @return a new word form.
     */
    public WF newWF(String form, int offset, int sent) {
	String newId = idManager.getNextWFId();
	int offsetVal = offset;
	WF newWF = new WF(this.annotationContainer, newId, form, sent);
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
    public Term newTerm(String id, Span<WF> span) {
	idManager.updateTermCounter(id);
	Term newTerm = new Term(id, span);
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
    public Term newTerm(Span<WF> span) {
	String newId = idManager.getNextTermId();
	Term newTerm = new Term(newId, span);
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
    public Term newTermOptions(String morphofeat, Span<WF> span) {
	String newId = idManager.getNextTermId();
	Term newTerm = new Term(newId, span);
	newTerm.setMorphofeat(morphofeat);
	annotationContainer.add(newTerm);
	return newTerm;
    }

    /** Creates a Sentiment object.
     * @return a new sentiment.
     */
    public Term.Sentiment newSentiment(String resource, String polarity) {
	Term.Sentiment newSentiment = new Term.Sentiment(resource, polarity);
	return newSentiment;
    }

    /** Creates a Component object to load an existing component. It receives the ID as an argument. It doesn't add the component to the term.
     * @param id component's ID.
     * @param term the term which this component is part of.
     * @param lemma lemma of the component.
     * @param pos part of speech of the component.
     * @return a new component.
     */
    public Term.Component newComponent(String id, Term term) {
	idManager.updateComponentCounter(id, term.getId());
	Term.Component newComponent = new Term.Component(id);
	return newComponent;
    }

    /** Creates a new Component. It assigns an appropriate ID to it. It uses the ID of the term to create a new ID for the component. It doesn't add the component to the term.
     * @param term the term which this component is part of.
     * @param lemma lemma of the component.
     * @param pos part of speech of the component.
     * @return a new component.
     */
    public Term.Component newComponent(Term term) {
	String newId = idManager.getNextComponentId(term.getId());
	Term.Component newComponent = new Term.Component(newId);
	return newComponent;
    }

    /** Creates a new dependency. The Dep is added to the document object.
     * @param from the origin term of the dependency.
     * @param to the target term of the dependency.
     * @param rfunc relational function of the dependency.
     * @return a new dependency.
     */
public Dep newDep(Term from, Term to, String rfunc) {
    Dep newDep = new Dep(from, to, rfunc);
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
public Chunk newChunk(String id, String phrase, Span<Term> span) {
    idManager.updateChunkCounter(id);
    Chunk newChunk = new Chunk(id, span);
    newChunk.setPhrase(phrase);
    annotationContainer.add(newChunk);
    return newChunk;
}

/** Creates a new chunk. It assigns an appropriate ID to it. The Chunk is added to the document object.
 * @param head the chunk head.
 * @param phrase type of the phrase.
 * @param terms the list of the terms in the chunk.
 * @return a new chunk.
 */
public Chunk newChunk(String phrase, Span<Term> span) {
    String newId = idManager.getNextChunkId();
    Chunk newChunk = new Chunk(newId, span);
    newChunk.setPhrase(phrase);
    annotationContainer.add(newChunk);
    return newChunk;
}

/** Creates an Entity object to load an existing entity. It receives the ID as an argument. The entity is added to the document object.
     * @param id the ID of the named entity.
     * @param type entity type. 8 values are posible: Person, Organization, Location, Date, Time, Money, Percent, Misc.
     * @param references it contains one or more span elements. A span can be used to reference the different occurrences of the same named entity in the document. If the entity is composed by multiple words, multiple target elements are used.
     * @return a new named entity.
     */
public Entity newEntity(String id, String type, List<Span<Term>> references) {
	idManager.updateEntityCounter(id);
	Entity newEntity = new Entity(id, references);
	newEntity.setType(type);
	annotationContainer.add(newEntity);
	return newEntity;
    }

    /** Creates a new Entity. It assigns an appropriate ID to it. The entity is added to the document object.
     * @param type entity type. 8 values are posible: Person, Organization, Location, Date, Time, Money, Percent, Misc.
     * @param references it contains one or more span elements. A span can be used to reference the different occurrences of the same named entity in the document. If the entity is composed by multiple words, multiple target elements are used.
     * @return a new named entity.
     */
public Entity newEntity(String type, List<Span<Term>> references) {
	String newId = idManager.getNextEntityId();
	Entity newEntity = new Entity(newId, references);
	newEntity.setType(type);
	annotationContainer.add(newEntity);
	return newEntity;
    }

    /** Creates a coreference object to load an existing Coref. It receives it's ID as an argument. The Coref is added to the document.
     * @param id the ID of the coreference.
     * @param references different mentions (list of targets) to the same entity.
     * @return a new coreference.
     */
    public Coref newCoref(String id, List<Span<Term>> mentions) {
	idManager.updateCorefCounter(id);
	Coref newCoref = new Coref(id, mentions);
	annotationContainer.add(newCoref);
	return newCoref;
    }

    /** Creates a new coreference. It assigns an appropriate ID to it. The Coref is added to the document.
     * @param references different mentions (list of targets) to the same entity.
     * @return a new coreference.
     */
    public Coref newCoref(List<Span<Term>> mentions) {
	String newId = idManager.getNextCorefId();
	Coref newCoref = new Coref(newId, mentions);
	annotationContainer.add(newCoref);
	return newCoref;
    }

    /** Creates a new property. It receives it's ID as an argument. The property is added to the document.
     * @param id the ID of the property.
     * @param lemma the lemma of the property.
     * @param references different mentions (list of targets) to the same property.
     * @return a new coreference.
     */
    public Feature newProperty(String id, String lemma, List<Span<Term>> references) {
	idManager.updatePropertyCounter(id);
	Feature newProperty = new Feature(id, lemma, references);
	annotationContainer.add(newProperty);
	return newProperty;
    }

    /** Creates a new property. It assigns an appropriate ID to it. The property is added to the document.
     * @param lemma the lemma of the property.
     * @param references different mentions (list of targets) to the same property.
     * @return a new coreference.
     */
    public Feature newProperty(String lemma, List<Span<Term>> references) {
	String newId = idManager.getNextPropertyId();
	Feature newProperty = new Feature(newId, lemma, references);
	annotationContainer.add(newProperty);
	return newProperty;
    }

    /** Creates a new category. It receives it's ID as an argument. The category is added to the document.
     * @param id the ID of the category.
     * @param lemma the lemma of the category.
     * @param references different mentions (list of targets) to the same category.
     * @return a new coreference.
     */
    public Feature newCategory(String id, String lemma, List<Span<Term>> references) {
	idManager.updateCategoryCounter(id);
	Feature newCategory = new Feature(id, lemma, references);
	annotationContainer.add(newCategory);
	return newCategory;
    }

    /** Creates a new category. It assigns an appropriate ID to it. The category is added to the document.
     * @param lemma the lemma of the category.
     * @param references different mentions (list of targets) to the same category.
     * @return a new coreference.
     */
    public Feature newCategory(String lemma, List<Span<Term>> references) {
	String newId = idManager.getNextCategoryId();
	Feature newCategory = new Feature(newId, lemma, references);
	annotationContainer.add(newCategory);
	return newCategory;
    }

    /** Creates a new opinion object. It assigns an appropriate ID to it. The opinion is added to the document.
     * @return a new opinion.
     */
    public Opinion newOpinion() {
	String newId = idManager.getNextOpinionId();
	Opinion newOpinion = new Opinion(newId);
	annotationContainer.add(newOpinion);
	return newOpinion;
    }

    /** Creates a new opinion object. It receives its ID as an argument. The opinion is added to the document.
     * @return a new opinion.
     */
    public Opinion newOpinion(String id) {
        idManager.updateOpinionCounter(id);
	Opinion newOpinion = new Opinion(id);
	annotationContainer.add(newOpinion);
	return newOpinion;
    }

    /** Creates a new relation between entities and/or sentiment features. It assigns an appropriate ID to it. The relation is added to the document.
     * @param from source of the relation
     * @param to target of the relation
     * @return a new relation
     */
    public Relation newRelation(Relational from, Relational to) {
	String newId = idManager.getNextRelationId();
	Relation newRelation = new Relation(newId, from, to);
	annotationContainer.add(newRelation);
	return newRelation;
    }

    /** Creates a new relation between entities and/or sentiment features. It receives its ID as an argument. The relation is added to the document.
     * @param id the ID of the relation
     * @param from source of the relation
     * @param to target of the relation
     * @return a new relation
     */
    public Relation newRelation(String id, Relational from, Relational to) {
	idManager.updateRelationCounter(id);
	Relation newRelation = new Relation(id, from, to);
	annotationContainer.add(newRelation);
	return newRelation;
    }

    /** Creates a new srl predicate. It receives its ID as an argument. The predicate is added to the document.
     * @param id the ID of the predicate
     * @param span span containing the targets of the predicate
     * @return a new predicate
     */
	public Predicate newPredicate(String id, Span<Term> span) {
	idManager.updatePredicateCounter(id);
	Predicate newPredicate = new Predicate(id, span);
	annotationContainer.add(newPredicate);
	return newPredicate;
    }

    /** Creates a new srl predicate. It assigns an appropriate ID to it. The predicate is added to the document.
     * @param span span containing all the targets of the predicate
     * @return a new predicate
     */
    public Predicate newPredicate(Span<Term> span) {
	String newId = idManager.getNextPredicateId();
	Predicate newPredicate = new Predicate(newId, span);
	annotationContainer.add(newPredicate);
	return newPredicate;
    }

    /** Creates a Role object to load an existing role. It receives the ID as an argument. It doesn't add the role to the predicate.
     * @param id role's ID.
     * @param predicate the predicate which this role is part of
     * @param semRole semantic role
     * @param span span containing all the targets of the role
     * @return a new role.
     */
    public Predicate.Role newRole(String id, Predicate predicate, String semRole, Span<Term> span) {
	idManager.updateRoleCounter(id);
	Predicate.Role newRole = new Predicate.Role(id, semRole, span);
	return newRole;
    }
    
    /** Creates a new Role object. It assigns an appropriate ID to it. It uses the ID of the predicate to create a new ID for the role. It doesn't add the role to the predicate.
     * @param predicate the predicate which this role is part of
     * @param semRole semantic role
     * @param span span containing all the targets of the role
     * @return a new role.
     */
    public Predicate.Role newRole(Predicate predicate, String semRole, Span<Term> span) {
	String newId = idManager.getNextRoleId();
	Predicate.Role newRole = new Predicate.Role(newId, semRole, span);
	return newRole;
    }

    /** Creates a new external reference.
     * @param resource indicates the identifier of the resource referred to.
     * @param reference code of the referred element.
     * @return a new external reference object.
     */
    public ExternalRef newExternalRef(String resource, String reference) {
	return new ExternalRef(resource, reference);
    }

    public Tree newConstituent(TreeNode root) {
	Tree tree = new Tree(root);
	annotationContainer.add(tree);
	return tree;
    }

    public void addConstituencyFromParentheses(String parseOut) throws Exception {
	Tree.parenthesesToKaf(parseOut, this);
    }

    public NonTerminal newNonTerminal(String id, String label) {
	NonTerminal tn = new NonTerminal(id, label);
	return tn;
    }

    public NonTerminal newNonTerminal(String label) {
	String newId = idManager.getNextNonterminalId();
	String newEdgeId = idManager.getNextEdgeId();
	NonTerminal newNonterminal = new NonTerminal(newId, label);
	newNonterminal.setEdgeId(newEdgeId);
	return newNonterminal;
    }

    public Terminal newTerminal(String id, Span<Term> span) {
	Terminal tn = new Terminal(id, span);
	return tn;
    }

    public Terminal newTerminal(Span<Term> span) {
	String newId = idManager.getNextTerminalId();
	String newEdgeId = idManager.getNextEdgeId();
	Terminal tn = new Terminal(newId, span);
	tn.setEdgeId(newEdgeId);
	return tn;
    }

    public static Span<WF> newWFSpan() {
	return new Span<WF>();
    }

    public static Span<WF> newWFSpan(List<WF> targets) {
	return new Span<WF>(targets);
    }

    public static Span<WF> newWFSpan(List<WF> targets, WF head) {
	return new Span<WF>(targets, head);
    }

    public static Span<Term> newTermSpan() {
	return new Span<Term>();
    }

    public static Span<Term> newTermSpan(List<Term> targets) {
	return new Span<Term>(targets);
    }

    public static Span<Term> newTermSpan(List<Term> targets, Term head) {
	return new Span<Term>(targets, head);
    }

    /** Returns the raw text **/
    public String getRawText() {
	return annotationContainer.getRawText();
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
     * @param wfs a list of word forms whose terms will be found.
     * @return a list of terms containing the given word forms.
     */
    public List<Term> getTermsByWFs(List<WF> wfs) {
	return annotationContainer.getTermsByWFs(wfs);
    }

    public List<Term> getSentenceTerms(int sent) {
	return annotationContainer.getSentenceTerms(sent);
    }

    public List<Dep> getDeps() {
	return annotationContainer.getDeps();
    }

    public List<Chunk> getChunks() {
	return annotationContainer.getChunks();
    }

    /** Returns a list with all entities in the document */
    public List<Entity> getEntities() {
	return annotationContainer.getEntities();
    }

    public List<Coref> getCorefs() {
	return annotationContainer.getCorefs();
    }

    /** Returns a list with all relations in the document */
    public List<Feature> getProperties() {
	return annotationContainer.getProperties();
    }

    /** Returns a list with all relations in the document */
    public List<Feature> getCategories() {
	return annotationContainer.getCategories();
    }

    public List<Opinion> getOpinions() {
	return annotationContainer.getOpinions();
    }

    /** Returns a list with all relations in the document */
    public List<Relation> getRelations() {
	return annotationContainer.getRelations();
    }

    public List<Tree> getConstituents() {
	return annotationContainer.getConstituents();
    }

    /** Returns current timestamp. */
    public String getTimestamp() {
	Date date = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
	//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD'T'kk:mm:ssZ");
	String formattedDate = sdf.format(date);
	return formattedDate;
    }

    /** Merges the document with another one. **/
    public void merge(KAFDocument doc) {
	HashMap<String, WF> copiedWFs = new HashMap<String, WF>(); // hash[old_id => new_WF_obj]
	HashMap<String, Term> copiedTerms = new HashMap<String, Term>(); // hash[old_id => new_Term_obj]
	HashMap<String, Relational> copiedRelationals = new HashMap<String, Relational>();
	// Linguistic processors
	Map<String, List<LinguisticProcessor>> lps = doc.getLinguisticProcessors();
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
	    WF wfCopy = new WF(wf, this.annotationContainer);
	    this.insertWF(wfCopy);
	    copiedWFs.put(wf.getId(), wfCopy);
	}
	// Terms
	for (Term term : doc.getTerms()) {
	    Term termCopy = new Term(term, copiedWFs);
	    this.insertTerm(termCopy);
	    copiedTerms.put(term.getId(), termCopy);
	}
	// Deps
	for (Dep dep : doc.getDeps()) {
	    Dep depCopy = new Dep(dep, copiedTerms);
	    this.insertDep(depCopy);
	}
	// Chunks
	for (Chunk chunk : doc.getChunks()) {
	    Chunk chunkCopy = new Chunk(chunk, copiedTerms);
	    this.insertChunk(chunkCopy);
	}
	// Entities
	for (Entity entity : doc.getEntities()) {
	    Entity entityCopy = new Entity(entity, copiedTerms);
	    this.insertEntity(entityCopy);
	    copiedRelationals.put(entity.getId(), entityCopy);
	}
	// Coreferences
	for (Coref coref : doc.getCorefs()) {
	    Coref corefCopy = new Coref(coref, copiedTerms);
	    this.insertCoref(corefCopy);
	}
	// Properties
	for (Feature property : doc.getProperties()) {
	    Feature propertyCopy = new Feature(property, copiedTerms);
	    this.insertProperty(propertyCopy);
	    copiedRelationals.put(property.getId(), propertyCopy);
	}
	// Categories
	for (Feature category : doc.getCategories()) {
	    Feature categoryCopy = new Feature(category, copiedTerms);
	    this.insertCategory(categoryCopy);
	    copiedRelationals.put(category.getId(), categoryCopy);
	}
	// Opinions
	for (Opinion opinion : doc.getOpinions()) {
	    Opinion opinionCopy = new Opinion(opinion, copiedTerms);
	    this.insertOpinion(opinionCopy);
	}
	// Relations
	for (Relation relation : doc.getRelations()) {
	    Relation relationCopy = new Relation(relation, copiedRelationals);
	    this.insertRelation(relationCopy);
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

    private void insertDep(Dep dep) {
	annotationContainer.add(dep);
    }

    private String insertChunk(Chunk chunk) {
	String newId = idManager.getNextChunkId();
	chunk.setId(newId);
	annotationContainer.add(chunk);
	return newId;
    }

    private String insertEntity(Entity entity) {
	String newId = idManager.getNextEntityId();
	entity.setId(newId);
	annotationContainer.add(entity);
	return newId;
    }

    private String insertCoref(Coref coref) {
	String newId = idManager.getNextCorefId();
	coref.setId(newId);
	annotationContainer.add(coref);
	return newId;
    }

    private String insertProperty(Feature property) {
	String newId = idManager.getNextPropertyId();
	property.setId(newId);
	annotationContainer.add(property);
	return newId;
    }

    private String insertCategory(Feature category) {
	String newId = idManager.getNextCategoryId();
	category.setId(newId);
	annotationContainer.add(category);
	return newId;
    }

    private String insertOpinion(Opinion opinion) {
	String newId = idManager.getNextOpinionId();
	opinion.setId(newId);
	annotationContainer.add(opinion);
	return newId;
    }

    private String insertRelation(Relation relation) {
	String newId = idManager.getNextRelationId();
	relation.setId(newId);
	annotationContainer.add(relation);
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

    
    /**************************/
    /*** DEPRECATED METHODS ***/
    /**************************/

    /** Deprecated */
    public LinguisticProcessor addLinguisticProcessor(String layer, String name, String version) {
        LinguisticProcessor lp = this.addLinguisticProcessor(layer, name);
	lp.setVersion(version);
	return lp;
    }

    /** Deprecated */
    public LinguisticProcessor addLinguisticProcessor(String layer, String name, String timestamp, String version) {
	LinguisticProcessor lp = this.addLinguisticProcessor(layer, name);
	lp.setTimestamp(timestamp);
	lp.setVersion(version);
	return lp;
    }
    
    /** Deprecated */
    public WF newWF(String id, String form) {
        return this.newWF(id, form, 0);
    }

    /** Deprecated */
    public WF newWF(String form) {
        return this.newWF(form, 0);
    }

    /** Deprecated */
    public WF createWF(String id, String form) {
	return this.newWF(id, form);
    }

    /** Deprecated */
    public WF createWF(String form) {
	return this.newWF(form);
    }

    /** Deprecated */
    public WF createWF(String form, int offset) {
	return this.newWF(form, offset);
    }

    /** Deprecated */
    public Term newTerm(String id, String type, String lemma, String pos, Span<WF> span) {
        Term term = newTerm(id, span);
	term.setType(type);
	term.setLemma(lemma);
	term.setPos(pos);
	return term;
    }

    /** Deprecated */
    public Term newTerm(String type, String lemma, String pos, Span<WF> span) {
        Term term = newTerm(span);
	term.setType(type);
	term.setLemma(lemma);
	term.setPos(pos);
	return term;
    }

    /** Deprecated */
    public Term newTermOptions(String type, String lemma, String pos, String morphofeat, Span<WF> span) {
	Term newTerm = newTermOptions(morphofeat, span);
	newTerm.setType(type);
	newTerm.setLemma(lemma);
	newTerm.setPos(pos);
	return newTerm;
    }

    /** Deprecated */
    public Term createTerm(String id, String type, String lemma, String pos, List<WF> wfs) {
	return this.newTerm(id, type, lemma, pos, this.<WF>list2Span(wfs));
    }

    /** Deprecated */
    public Term createTerm(String type, String lemma, String pos, List<WF> wfs) {
        return this.newTerm(type, lemma, pos, this.<WF>list2Span(wfs));
    }

    /** Deprecated */
    public Term createTermOptions(String type, String lemma, String pos, String morphofeat, List<WF> wfs) {
	return this.newTermOptions(type, lemma, pos, morphofeat, this.<WF>list2Span(wfs));
    }

    /** Deprecated */
    public Term.Sentiment newSentiment() {
        return this.newSentiment("", "");
    }

    /** Deprecated */
    public Term.Sentiment createSentiment() {
	return this.newSentiment();
    }
    
    /** Deprecated */
    public Term.Component newComponent(String id, Term term, String lemma, String pos) {
	Term.Component newComponent = this.newComponent(id, term);
	newComponent.setLemma(lemma);
	newComponent.setPos(pos);
	return newComponent;
    }

    /** Deprecated */
    public Term.Component newComponent(Term term, String lemma, String pos) {
	Term.Component newComponent = this.newComponent(term);
	newComponent.setLemma(lemma);
	newComponent.setPos(pos);
	return newComponent;
    }

    /** Deprecated */
    public Term.Component createComponent(String id, Term term, String lemma, String pos) {
	return this.newComponent(id, term, lemma, pos);
    }

    /** Deprecated */
    public Term.Component createComponent(Term term, String lemma, String pos) {
	return this.newComponent(term, lemma, pos);
    }

    /** Deprecated */
    public Dep createDep(Term from, Term to, String rfunc) {
	return this.createDep(from, to, rfunc);
    }

    /** Deprecated */
    public Chunk createChunk(String id, Term head, String phrase, List<Term> terms) {
	return this.newChunk(id, phrase, this.<Term>list2Span(terms, head));
    }

    /** Deprecated */
    public Chunk createChunk(Term head, String phrase, List<Term> terms) {
	return this.newChunk(phrase, this.<Term>list2Span(terms, head));
    }

    /** Deprecated */
    public Entity createEntity(String id, String type, List<List<Term>> references) {
	List<Span<Term>> spanReferences = new ArrayList<Span<Term>>();
	for (List<Term> list : references) {
	    spanReferences.add(this.list2Span(list));
	}
	return this.newEntity(id, type, spanReferences);
    }

    /** Deprecated */
    public Entity createEntity(String type, List<List<Term>> references) {
	List<Span<Term>> spanReferences = new ArrayList<Span<Term>>();
	for (List<Term> list : references) {
	    spanReferences.add(this.list2Span(list));
	}
	return this.newEntity(type, spanReferences);
    }

    /** Deprecated */
    public Coref createCoref(String id, List<List<Target>> references) {
	List<Span<Term>> spanReferences = new ArrayList<Span<Term>>();
	for (List<Target> list : references) {
	    spanReferences.add(this.targetList2Span(list));
	}
	return this.newCoref(id, spanReferences);
    }

    /** Deprecated */
    public Coref createCoref(List<List<Target>> references) {
	List<Span<Term>> spanReferences = new ArrayList<Span<Term>>();
	for (List<Target> list : references) {
	    spanReferences.add(this.targetList2Span(list));
	}
	return this.newCoref(spanReferences);
    }

    /** Deprecated */
    public Feature createProperty(String id, String lemma, List<List<Term>> references) {
	List<Span<Term>> spanReferences = new ArrayList<Span<Term>>();
	for (List<Term> list : references) {
	    spanReferences.add(this.list2Span(list));
	}
	return this.newProperty(id, lemma, spanReferences);
    }

    /** Deprecated */
    public Feature createProperty(String lemma, List<List<Term>> references) {
	List<Span<Term>> spanReferences = new ArrayList<Span<Term>>();
	for (List<Term> list : references) {
	    spanReferences.add(this.list2Span(list));
	}
	return this.newProperty(lemma, spanReferences);
    }

    /** Deprecated */
    public Feature createCategory(String id, String lemma, List<List<Term>> references) {
	List<Span<Term>> spanReferences = new ArrayList<Span<Term>>();
	for (List<Term> list : references) {
	    spanReferences.add(this.list2Span(list));
	}
	return this.newCategory(id, lemma, spanReferences);
    }

    /** Deprecated */
    public Feature createCategory(String lemma, List<List<Term>> references) {
	List<Span<Term>> spanReferences = new ArrayList<Span<Term>>();
	for (List<Term> list : references) {
	    spanReferences.add(this.list2Span(list));
	}
	return this.newCategory(lemma, spanReferences);
    }

    /** Deprecated */
    public Opinion createOpinion() {
	return this.newOpinion();
    }

    /** Deprecated */
    public Opinion createOpinion(String id) {
	return this.newOpinion(id);
    }

    /** Deprecated */
    public Relation createRelation(Relational from, Relational to) {
	return this.newRelation(from, to);
    }

    /** Deprecated */
    public Relation createRelation(String id, Relational from, Relational to) {
	return this.newRelation(id, from, to);
    }
    
    /** Deprecated */
    public ExternalRef createExternalRef(String resource, String reference) {
	return this.newExternalRef(resource, reference);
    }

    /** Deprecated. Creates a new target. This method is overloaded. Any target created by calling this method won't be the head term.
     * @param term target term.
     * @return a new target.
     */
    public static Target createTarget(Term term) {
	return new Target(term, false);
    }
    
    /** Deprecated. Creates a new target. This method is overloaded. In this case, it receives a boolean argument which defines whether the target term is the head or not.
     * @param term target term.
     * @param isHead a boolean argument which defines whether the target term is the head or not.
     * @return a new target.
     */
    public static Target createTarget(Term term, boolean isHead) {
	return new Target(term, isHead);
    }

    /** Converts a List into a Span */
    static <T> Span<T> list2Span(List<T> list) {
	Span<T> span = new Span<T>();
	for (T elem : list) {
	    span.addTarget(elem);
	}
	return span;
    }
    
    /** Converts a List into a Span */
    static <T> Span<T> list2Span(List<T> list, T head) {
	Span<T> span = new Span<T>();
	for (T elem : list) {
	    if (head == elem) {
		span.addTarget(elem, true);
	    } else {
		span.addTarget(elem);
	    }
	}
	return span;
    }

    /** Converts a Target list into a Span of terms */
    static Span<Term> targetList2Span(List<Target> list) {
	Span<Term> span = new Span<Term>();
	for (Target target : list) {
	    if (target.isHead()) {
		span.addTarget(target.getTerm(), true);
	    } else {
		span.addTarget(target.getTerm());
	    }
	}
	return span;
    }

    /** Converts a Span into a Target list */
    static List<Target> span2TargetList(Span<Term> span) {
	List<Target> list = new ArrayList<Target>();
	for (Term t : span.getTargets()) {
	    list.add(KAFDocument.createTarget(t, (t==span.getHead())));
	}
	return list;
    }

    /** Deprecated. Returns a list of terms containing the word forms given on argument.
     * @param wfIds a list of word form IDs whose terms will be found.
     * @return a list of terms containing the given word forms.
     */
    public List<Term> getTermsFromWFs(List<String> wfIds) {
	return annotationContainer.getTermsByWFIds(wfIds);
    }
}

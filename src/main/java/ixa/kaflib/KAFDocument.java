package ixa.kaflib;

import ixa.kaflib.Opinion.OpinionExpression;
import ixa.kaflib.Opinion.OpinionHolder;
import ixa.kaflib.Opinion.OpinionTarget;
import ixa.kaflib.Predicate.Role;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.io.File;
import java.io.Reader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jdom2.JDOMException;
import org.jdom2.Element;


/** Respresents a KAF document. It's the main class of the library, as it keeps all elements of the document (word forms, terms, entities...) and manages all object creations. The document can be created by the user calling it's methods, or loading from an existing XML file.*/
public class KAFDocument implements Serializable {

    public enum Layer {
	TEXT,
	TERMS,
	ENTITIES,
	CHUNKS,
	DEPS,
	CONSTITUENCY,
	COREFERENCES,
	OPINIONS,
	CAUSAL_RELATIONS,
	TEMPORAL_RELATIONS,
	SRL,
	TIME_EXPRESSIONS,
	FACTUALITY_LAYER,
	MARKABLES,
	PROPERTIES,
	CATEGORIES,
	RELATIONS,
	LINKED_ENTITIES,
	TOPICS,
    }

    public enum AnnotationType {
	WF,
	TERM,
	MW,
	COMPONENT,
	SENTIMENT,
	ENTITY,
	CHUNK,
	DEP,
	TREE,
	NON_TERMINAL,
	TERMINAL,
	EDGE,
	COREF,
	OPINION,
	OPINION_HOLDER,
	OPINION_TARGET,
	OPINION_EXPRESSION,
	CLINK,
	TLINK,
	PREDICATE,
	ROLE,
	TIMEX3,
	FACTUALITY,
	MARK,
	PROPERTY,
	CATEGORY,
	LINKED_ENTITY,
	RELATION,
	TOPIC,
    }
    
    static Map<Layer, AnnotationType> layerAnnotationTypes;
    static Map<AnnotationType, Class<?>> annotationTypeClasses;

    public class FileDesc implements Serializable {
	public String author;
	public String title;
	public String filename;
	public String filetype;
	public Integer pages;
	public String creationtime;

	private FileDesc() {}
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof FileDesc)) return false;
	    FileDesc fd = (FileDesc) o;
	    return Utils.areEquals(this.author, fd.author) &&
		    Utils.areEquals(this.title, fd.title) &&
		    Utils.areEquals(this.filename, fd.filename) &&
		    Utils.areEquals(this.filetype, fd.filetype) &&
		    Utils.areEquals(this.pages, fd.pages) &&
		    Utils.areEquals(this.creationtime, fd.creationtime);
	}
    }

    public class Public implements Serializable {
	public String publicId;
	public String uri;

	private Public() {
	}
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof Public)) return false;
	    Public pub = (Public) o;
	    return Utils.areEquals(this.publicId, pub.publicId) &&
		    Utils.areEquals(this.uri, pub.uri);
	}
    }

    public class LinguisticProcessor implements Serializable {
	String layer;
	String name;
	String timestamp;
	String beginTimestamp;
	String endTimestamp;
	String version;
	String hostname;

	private LinguisticProcessor(String name, String layer) {
	    this.layer = layer;
	    this.name = name;
	}

	/* Deprecated */
	private LinguisticProcessor(String name, String timestamp, String version) {
	    this.name = name;
	    this.timestamp = timestamp;
	    this.version = version;
	}

	public String getLayer() {
	    return this.layer;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getName() {
	    return name;
	}

	public boolean hasTimestamp() {
	    return this.timestamp != null;
	}

	public void setTimestamp(String timestamp) {
	    this.timestamp = timestamp;
	}

	public void setTimestamp() {
	    String timestamp = createTimestamp();
	    this.timestamp = timestamp;
	}

	public String getTimestamp() {
	    return this.timestamp;
	}

	public boolean hasBeginTimestamp() {
	    return beginTimestamp != null;
	}

	public void setBeginTimestamp(String timestamp) {
	    this.beginTimestamp = timestamp;
	    if (!this.hasHostname()) {
		try {
		    this.setHostname(InetAddress.getLocalHost().getHostName());
		} catch(UnknownHostException e) {}
	    }
	}

	public void setBeginTimestamp() {
	    String timestamp = createTimestamp();
	    this.setBeginTimestamp(timestamp);
	}

	public String getBeginTimestamp() {
	    return beginTimestamp;
	}

	public boolean hasEndTimestamp() {
	    return endTimestamp != null;
	}

	public void setEndTimestamp(String timestamp) {
	    this.endTimestamp = timestamp;
	}

	public void setEndTimestamp() {
	    String timestamp = createTimestamp();
	    this.endTimestamp = timestamp;
	}

	public String getEndTimestamp() {
	    return endTimestamp;
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

	public Boolean hasHostname() {
	    return this.hostname != null;
	}

	public String getHostname() {
	    return this.hostname;
	}

	public void setHostname(String hostname) {
	    this.hostname = hostname;
	}

	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof LinguisticProcessor)) return false;
	    LinguisticProcessor lp = (LinguisticProcessor) o;
	    return Utils.areEquals(this.layer, lp.layer) &&
		    Utils.areEquals(this.name, lp.name) &&
		    Utils.areEquals(this.version, lp.version);
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
	
	layerAnnotationTypes = new HashMap<Layer, AnnotationType>();
	layerAnnotationTypes.put(Layer.TEXT, AnnotationType.WF);
	layerAnnotationTypes.put(Layer.TERMS, AnnotationType.TERM);
	layerAnnotationTypes.put(Layer.ENTITIES, AnnotationType.ENTITY);
	layerAnnotationTypes.put(Layer.CHUNKS, AnnotationType.CHUNK);
	layerAnnotationTypes.put(Layer.DEPS, AnnotationType.DEP);
	layerAnnotationTypes.put(Layer.CONSTITUENCY, AnnotationType.TREE);
	layerAnnotationTypes.put(Layer.COREFERENCES, AnnotationType.COREF);
	layerAnnotationTypes.put(Layer.OPINIONS, AnnotationType.OPINION);
	layerAnnotationTypes.put(Layer.CAUSAL_RELATIONS, AnnotationType.CLINK);
	layerAnnotationTypes.put(Layer.TEMPORAL_RELATIONS, AnnotationType.TLINK);
	layerAnnotationTypes.put(Layer.SRL, AnnotationType.PREDICATE);
	layerAnnotationTypes.put(Layer.TIME_EXPRESSIONS, AnnotationType.TIMEX3);
	layerAnnotationTypes.put(Layer.FACTUALITY_LAYER, AnnotationType.FACTUALITY);
	layerAnnotationTypes.put(Layer.MARKABLES, AnnotationType.MARK);
	layerAnnotationTypes.put(Layer.PROPERTIES, AnnotationType.PROPERTY);
	layerAnnotationTypes.put(Layer.CATEGORIES, AnnotationType.CATEGORY);
	layerAnnotationTypes.put(Layer.RELATIONS, AnnotationType.RELATION);
	layerAnnotationTypes.put(Layer.LINKED_ENTITIES, AnnotationType.LINKED_ENTITY);
	layerAnnotationTypes.put(Layer.TOPICS, AnnotationType.TOPIC);
	
	annotationTypeClasses = new HashMap<AnnotationType, Class<?>>();
	annotationTypeClasses.put(AnnotationType.WF, WF.class);
	annotationTypeClasses.put(AnnotationType.TERM, Term.class);
	annotationTypeClasses.put(AnnotationType.COMPONENT, Term.class);
	annotationTypeClasses.put(AnnotationType.MW, Term.class);
	annotationTypeClasses.put(AnnotationType.ENTITY, Entity.class);
	annotationTypeClasses.put(AnnotationType.CHUNK, Chunk.class);
	annotationTypeClasses.put(AnnotationType.DEP, Dep.class);
	annotationTypeClasses.put(AnnotationType.TREE, Tree.class);
	annotationTypeClasses.put(AnnotationType.NON_TERMINAL, NonTerminal.class);
	annotationTypeClasses.put(AnnotationType.TERMINAL, Terminal.class);
	//annotationTypeClasses.put(AnnotationType.EDGE, .class);
	annotationTypeClasses.put(AnnotationType.COREF, Coref.class);
	annotationTypeClasses.put(AnnotationType.OPINION, Opinion.class);
	annotationTypeClasses.put(AnnotationType.OPINION_HOLDER, OpinionHolder.class);
	annotationTypeClasses.put(AnnotationType.OPINION_TARGET, OpinionTarget.class);
	annotationTypeClasses.put(AnnotationType.OPINION_EXPRESSION, OpinionExpression.class);
	annotationTypeClasses.put(AnnotationType.CLINK, CLink.class);
	annotationTypeClasses.put(AnnotationType.TLINK, TLink.class);
	annotationTypeClasses.put(AnnotationType.PREDICATE, Predicate.class);
	annotationTypeClasses.put(AnnotationType.ROLE, Role.class);
	annotationTypeClasses.put(AnnotationType.TIMEX3, Timex3.class);
	annotationTypeClasses.put(AnnotationType.FACTUALITY, Factuality.class);
	annotationTypeClasses.put(AnnotationType.MARK, Mark.class);
	annotationTypeClasses.put(AnnotationType.PROPERTY, Feature.class);
	annotationTypeClasses.put(AnnotationType.CATEGORY, Feature.class);
	annotationTypeClasses.put(AnnotationType.LINKED_ENTITY, LinkedEntity.class);
	annotationTypeClasses.put(AnnotationType.RELATION, Relation.class);
	annotationTypeClasses.put(AnnotationType.TOPIC, Topic.class);
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
    public static KAFDocument createFromStream(Reader stream) throws IOException, JDOMException {
	KAFDocument kaf = null;
	kaf = ReadWriteManager.load(stream);
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
	String timestamp = createTimestamp();
	LinguisticProcessor lp = new LinguisticProcessor(name, layer);
	//lp.setBeginTimestamp(timestamp); // no default timestamp
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
		LinguisticProcessor newLp = this.addLinguisticProcessor(entry.getKey(), lp.name);
		if (lp.hasTimestamp()) newLp.setTimestamp(lp.getTimestamp());
		if (lp.hasBeginTimestamp()) newLp.beginTimestamp = lp.beginTimestamp;
		if (lp.hasEndTimestamp()) newLp.setEndTimestamp(lp.getEndTimestamp());
		if (lp.hasVersion()) newLp.setVersion(lp.getVersion());
	    }
	}
    }

    /** Returns a hash of linguistic processors from the document.
     *  Hash: layer => LP
     */
    public Map<String, List<LinguisticProcessor>> getLinguisticProcessors() {
	return lps;
    }

    public List<LinguisticProcessor> getLinguisticProcessorList() {
	List<LinguisticProcessor> result = new ArrayList<LinguisticProcessor>();
	for (List<LinguisticProcessor> lps : this.lps.values()) {
	    for (LinguisticProcessor lp : lps) {
		result.add(lp);
	    }
	}
	return result;
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

    public Public createPublic() {
	this._public = new Public();
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
	idManager.updateCounter(AnnotationType.WF, id);
	WF newWF = new WF(this.annotationContainer, id, form, sent);
	annotationContainer.add(newWF, Layer.TEXT);
	return newWF;
    }

    /** Creates a new WF object. It assigns an appropriate ID to it and it also assigns offset and length
     * attributes. The WF is added to the document object.
     * @param form text of the word form itself.
     * @return a new word form.
     */
    public WF newWF(String form, int offset) {
	String newId = idManager.getNextId(AnnotationType.WF);
	int offsetVal = offset;
	WF newWF = new WF(this.annotationContainer, newId, form, 0);
	newWF.setOffset(offsetVal);
	newWF.setLength(form.length());
	annotationContainer.add(newWF, Layer.TEXT);
	return newWF;
    }

    /** Creates a new WF object. It assigns an appropriate ID to it.  The WF is added to the document object.
     * @param form text of the word form itself.
     * @return a new word form.
     */
    public WF newWF(String form, int offset, int sent) {
	String newId = idManager.getNextId(AnnotationType.WF);
	WF newWF = new WF(this.annotationContainer, newId, form, sent);
	newWF.setOffset(offset);
	newWF.setLength(form.length());
	annotationContainer.add(newWF, Layer.TEXT);
	return newWF;
    }


    /** Adds an existing annotation containing an ID to the
     * document. The ID counter is correctly updated. If the ID is
     * being used by another annotation, a new one is assigned and
     * returned.
     */
    /*
    public String add(IdentifiableAnnotation ann) {
	String id = ann.getId();
	if (idManager.idExists(id)) {
	    id = idManager.getNextId(Annotations.WF); // ???
	    ann.setId(id);
	} else {
	    idManager.updateCounter(Annotations.WF, id); // ???
	}
	annotationContainer.add(ann);
	return id;
    }
    */

    /*
    public void add(Term ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(Entity ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(Chunk ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(Coref ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(Dep ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(Factuality ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(Mark ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(Timex3 ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(TLink ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(CLink ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(LinkedEntity ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(Relation ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(Predicate ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(Tree ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(Property ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(Category ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }

    public void add(Opinion ann) {
	idManager.updateWFCounter(wf.getId());
	annotationContainer.add(wf);
    }
    */






    /** Creates a Term object to load an existing term. It receives the ID as an argument. The Term is added to the document object.
     * @param id term's ID.
     * @param type type of term. There are two types of term: open and close.
     * @param lemma the lemma of the term.
     * @param pos part of speech of the term.
     * @param wfs the list of word forms this term is formed by.
     * @return a new term.
     */
    public Term newTerm(String id, Span<WF> span) {
	idManager.updateCounter(AnnotationType.TERM, id);
	Term newTerm = new Term(id, span, false);
	annotationContainer.add(newTerm, Layer.TERMS);
	return newTerm;
    }

    public Term newTerm(String id, Span<WF> span, boolean isComponent) {
	idManager.updateCounter(AnnotationType.TERM, id);
	Term newTerm = new Term(id, span, isComponent);
	if (!isComponent) {
	    annotationContainer.add(newTerm, Layer.TERMS);
	}
	return newTerm;
    }

    public Term newTerm(Span<WF> span, boolean isComponent) {
	String newId = idManager.getNextId(AnnotationType.TERM);
	Term newTerm = new Term(newId, span, isComponent);
	if (!isComponent) {
	    annotationContainer.add(newTerm, Layer.TERMS);
	}
	return newTerm;
    }    

    public Term newTerm(String id, Span<WF> span, Integer position) {
	idManager.updateCounter(AnnotationType.TERM, id);
	Term newTerm = new Term(id, span, false);
	annotationContainer.add(newTerm, Layer.TERMS, position);
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
	String newId = idManager.getNextId(AnnotationType.TERM);
	Term newTerm = new Term(newId, span, false);
	annotationContainer.add(newTerm, Layer.TERMS);
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
	String newId = idManager.getNextId(AnnotationType.TERM);
	Term newTerm = new Term(newId, span, false);
	newTerm.setMorphofeat(morphofeat);
	annotationContainer.add(newTerm, Layer.TERMS);
	return newTerm;
    }

    public Term newCompound(List<Term> terms, String lemma) {
	Span<WF> span = new Span<WF>();
	for (Term term : terms) {
	    span.addTargets(term.getSpan().getTargets()); 
	}
	String newId = idManager.getNextId(AnnotationType.MW);
	Term compound = newTerm(newId, span, annotationContainer.getPosition(Layer.TERMS, terms.get(0)));
	compound.setLemma(lemma);
	for (Term term : terms) {
	    compound.addComponent(term);
	    term.setCompound(compound);
	    this.annotationContainer.remove(term, Layer.TERMS);
	}
	return compound;
    }

    /** Creates a Sentiment object.
     * @return a new sentiment.
     */
    public Term.Sentiment newSentiment() {
	Term.Sentiment newSentiment = new Term.Sentiment();
	return newSentiment;
    }

    public Mark newMark(String id, String source, Span<WF> span) {
	idManager.updateCounter(AnnotationType.MARK, id);
	Mark newMark = new Mark(id, source, span);
	annotationContainer.add(newMark, Layer.MARKABLES);
	return newMark;
    }

    public Mark newMark(String source, Span<WF> span) {
	String newId = idManager.getNextId(AnnotationType.MARK);
	Mark newMark = new Mark(newId, source, span);
	annotationContainer.add(newMark, Layer.MARKABLES);
	return newMark;
    }

    /** Creates a new dependency. The Dep is added to the document object.
     * @param from the origin term of the dependency.
     * @param to the target term of the dependency.
     * @param rfunc relational function of the dependency.
     * @return a new dependency.
     */
public Dep newDep(Term from, Term to, String rfunc) {
    Dep newDep = new Dep(from, to, rfunc);
    annotationContainer.add(newDep, Layer.DEPS);
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
    idManager.updateCounter(AnnotationType.CHUNK, id);
    Chunk newChunk = new Chunk(id, span);
    newChunk.setPhrase(phrase);
    annotationContainer.add(newChunk, Layer.CHUNKS);
    return newChunk;
}

/** Creates a new chunk. It assigns an appropriate ID to it. The Chunk is added to the document object.
 * @param head the chunk head.
 * @param phrase type of the phrase.
 * @param terms the list of the terms in the chunk.
 * @return a new chunk.
 */
public Chunk newChunk(String phrase, Span<Term> span) {
    String newId = idManager.getNextId(AnnotationType.CHUNK);
    Chunk newChunk = new Chunk(newId, span);
    newChunk.setPhrase(phrase);
    annotationContainer.add(newChunk, Layer.CHUNKS);
    return newChunk;
}

/** Creates an Entity object to load an existing entity. It receives the ID as an argument. The entity is added to the document object.
     * @param id the ID of the named entity.
     * @param type entity type. 8 values are posible: Person, Organization, Location, Date, Time, Money, Percent, Misc.
     * @param references it contains one or more span elements. A span can be used to reference the different occurrences of the same named entity in the document. If the entity is composed by multiple words, multiple target elements are used.
     * @return a new named entity.
     */
public Entity newEntity(String id, List<Span<Term>> references) {
    idManager.updateCounter(AnnotationType.ENTITY, id);
	Entity newEntity = new Entity(id, references);
	annotationContainer.add(newEntity, Layer.ENTITIES);
	return newEntity;
    }

    /** Creates a new Entity. It assigns an appropriate ID to it. The entity is added to the document object.
     * @param type entity type. 8 values are posible: Person, Organization, Location, Date, Time, Money, Percent, Misc.
     * @param references it contains one or more span elements. A span can be used to reference the different occurrences of the same named entity in the document. If the entity is composed by multiple words, multiple target elements are used.
     * @return a new named entity.
     */
public Entity newEntity(List<Span<Term>> references) {
	String newId = idManager.getNextId(AnnotationType.ENTITY);
	Entity newEntity = new Entity(newId, references);
	annotationContainer.add(newEntity, Layer.ENTITIES);
	return newEntity;
    }

    /** Creates a coreference object to load an existing Coref. It receives it's ID as an argument. The Coref is added to the document.
     * @param id the ID of the coreference.
     * @param references different mentions (list of targets) to the same entity.
     * @return a new coreference.
     */
    public Coref newCoref(String id, List<Span<Term>> mentions) {
	idManager.updateCounter(AnnotationType.COREF, id);
	Coref newCoref = new Coref(id, mentions);
	annotationContainer.add(newCoref, Layer.COREFERENCES);
	return newCoref;
    }

    /** Creates a new coreference. It assigns an appropriate ID to it. The Coref is added to the document.
     * @param references different mentions (list of targets) to the same entity.
     * @return a new coreference.
     */
    public Coref newCoref(List<Span<Term>> mentions) {
	String newId = idManager.getNextId(AnnotationType.COREF);
	Coref newCoref = new Coref(newId, mentions);
	annotationContainer.add(newCoref, Layer.COREFERENCES);
	return newCoref;
    }

    /** Creates a timeExpressions object to load an existing Timex3. It receives it's ID as an argument. The Timex3 is added to the document.
     * @param id the ID of the coreference.
     * @param references different mentions (list of targets) to the same entity.
     * @return a new timex3.
     */
    public Timex3 newTimex3(String id, String type) {
	idManager.updateCounter(AnnotationType.TIMEX3, id);
	Timex3 newTimex3 = new Timex3(id, type);
	annotationContainer.add(newTimex3, Layer.TIME_EXPRESSIONS);
	return newTimex3;
    }

     /** Creates a new timeExpressions. It assigns an appropriate ID to it. The Coref is added to the document.
     * @param references different mentions (list of targets) to the same entity.
     * @return a new timex3.
     */
    public Timex3 newTimex3(String type) {
	String newId = idManager.getNextId(AnnotationType.TIMEX3);
	Timex3 newTimex3 = new Timex3(newId, type);
	annotationContainer.add(newTimex3, Layer.TIME_EXPRESSIONS);
	return newTimex3;
    }

    public TLink newTLink(String id, TLinkReferable from, TLinkReferable to, String relType) {
	idManager.updateCounter(AnnotationType.TLINK, id);
	TLink newTLink = new TLink(id, from, to, relType);
	annotationContainer.add(newTLink, Layer.TEMPORAL_RELATIONS);
	return newTLink;
    }

    public TLink newTLink(TLinkReferable from, TLinkReferable to, String relType) {
	String newId = idManager.getNextId(AnnotationType.TLINK);
	TLink newTLink = new TLink(newId, from, to, relType);
	annotationContainer.add(newTLink, Layer.TEMPORAL_RELATIONS);
	return newTLink;
    }

    public CLink newCLink(String id, Predicate from, Predicate to) {
	idManager.updateCounter(AnnotationType.CLINK, id);
	CLink newCLink = new CLink(id, from, to);
	annotationContainer.add(newCLink, Layer.CAUSAL_RELATIONS);
	return newCLink;
    }

    public CLink newCLink(Predicate from, Predicate to) {
	String newId = idManager.getNextId(AnnotationType.CLINK);
	CLink newCLink = new CLink(newId, from, to);
	annotationContainer.add(newCLink, Layer.CAUSAL_RELATIONS);
	return newCLink;
    }

	/** Creates a factualitylayer object and add it to the document
	 * @param term the Term of the coreference.
	 * @return a new factuality.
	 */
    public Factuality newFactuality(WF wf, String prediction) {
	Factuality factuality = new Factuality(wf, prediction);
	annotationContainer.add(factuality, Layer.FACTUALITY_LAYER);
	return factuality;
    }

	/** Creates a LinkedEntity object and add it to the document
	 * @param term the Term of the coreference.
	 * @return a new factuality.
	 */
    /*
	public LinkedEntity newLinkedEntity(Span<WF> span) {
		String newId = idManager.getNextId(Annotations.LINKED_ENTITY);
		LinkedEntity linkedEntity = new LinkedEntity(newId, span);
		annotationContainer.add(linkedEntity);
		return linkedEntity;
	}
    */

	/** Creates a new property. It receives it's ID as an argument. The property is added to the document.
     * @param id the ID of the property.
     * @param lemma the lemma of the property.
     * @param references different mentions (list of targets) to the same property.
     * @return a new coreference.
     */
    public Feature newProperty(String id, String lemma, List<Span<Term>> references) {
	idManager.updateCounter(AnnotationType.PROPERTY, id);
	Feature newProperty = new Feature(id, lemma, references);
	annotationContainer.add(newProperty, Layer.PROPERTIES);
	return newProperty;
    }
    
    /** Creates a new property. It assigns an appropriate ID to it. The property is added to the document.
     * @param lemma the lemma of the property.
     * @param references different mentions (list of targets) to the same property.
     * @return a new coreference.
     */
    public Feature newProperty(String lemma, List<Span<Term>> references) {
	String newId = idManager.getNextId(AnnotationType.PROPERTY);
	Feature newProperty = new Feature(newId, lemma, references);
	annotationContainer.add(newProperty, Layer.PROPERTIES);
	return newProperty;
    }
    
    /** Creates a new category. It receives it's ID as an argument. The category is added to the document.
     * @param id the ID of the category.
     * @param lemma the lemma of the category.
     * @param references different mentions (list of targets) to the same category.
     * @return a new coreference.
     */
    public Feature newCategory(String id, String lemma, List<Span<Term>> references) {
	idManager.updateCounter(AnnotationType.CATEGORY, id);
	Feature newCategory = new Feature(id, lemma, references);
	annotationContainer.add(newCategory, Layer.CATEGORIES);
	return newCategory;
    }
    
    /** Creates a new category. It assigns an appropriate ID to it. The category is added to the document.
     * @param lemma the lemma of the category.
     * @param references different mentions (list of targets) to the same category.
     * @return a new coreference.
     */
    public Feature newCategory(String lemma, List<Span<Term>> references) {
	String newId = idManager.getNextId(AnnotationType.CATEGORY);
	Feature newCategory = new Feature(newId, lemma, references);
	annotationContainer.add(newCategory, Layer.CATEGORIES);
	return newCategory;
    }
    
    /** Creates a new opinion object. It assigns an appropriate ID to it. The opinion is added to the document.
     * @return a new opinion.
     */
    public Opinion newOpinion() {
	String newId = idManager.getNextId(AnnotationType.OPINION);
	Opinion newOpinion = new Opinion(newId);
	annotationContainer.add(newOpinion, Layer.OPINIONS);
	return newOpinion;
    }

    /** Creates a new opinion object. It receives its ID as an argument. The opinion is added to the document.
     * @return a new opinion.
     */
    public Opinion newOpinion(String id) {
        idManager.updateCounter(AnnotationType.OPINION, id);
	Opinion newOpinion = new Opinion(id);
	annotationContainer.add(newOpinion, Layer.OPINIONS);
	return newOpinion;
    }

    /** Creates a new relation between entities and/or sentiment features. It assigns an appropriate ID to it. The relation is added to the document.
     * @param from source of the relation
     * @param to target of the relation
     * @return a new relation
     */
    public Relation newRelation(Relational from, Relational to) {
	String newId = idManager.getNextId(AnnotationType.RELATION);
	Relation newRelation = new Relation(newId, from, to);
	annotationContainer.add(newRelation, Layer.RELATIONS);
	return newRelation;
    }
    
    /** Creates a new relation between entities and/or sentiment features. It receives its ID as an argument. The relation is added to the document.
     * @param id the ID of the relation
     * @param from source of the relation
     * @param to target of the relation
     * @return a new relation
     */
    public Relation newRelation(String id, Relational from, Relational to) {
	idManager.updateCounter(AnnotationType.RELATION, id);
	Relation newRelation = new Relation(id, from, to);
	annotationContainer.add(newRelation, Layer.RELATIONS);
	return newRelation;
    }
    
    /** Creates a new srl predicate. It receives its ID as an argument. The predicate is added to the document.
     * @param id the ID of the predicate
     * @param span span containing the targets of the predicate
     * @return a new predicate
     */
    public Predicate newPredicate(String id, Span<Term> span) {
	    idManager.updateCounter(AnnotationType.PREDICATE, id);
	Predicate newPredicate = new Predicate(id, span);
	annotationContainer.add(newPredicate, Layer.SRL);
	return newPredicate;
    }

    /** Creates a new srl predicate. It assigns an appropriate ID to it. The predicate is added to the document.
     * @param span span containing all the targets of the predicate
     * @return a new predicate
     */
    public Predicate newPredicate(Span<Term> span) {
	String newId = idManager.getNextId(AnnotationType.PREDICATE);
	Predicate newPredicate = new Predicate(newId, span);
	annotationContainer.add(newPredicate, Layer.SRL);
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
	idManager.updateCounter(AnnotationType.ROLE, id);
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
	String newId = idManager.getNextId(AnnotationType.ROLE);
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
    
    public ExternalRef newExternalRef(String resource) {
	return new ExternalRef(resource, null);
    }

    public Tree newConstituent(TreeNode root, String type) {
	Tree tree = new Tree(root, type);
	annotationContainer.add(tree, Layer.CONSTITUENCY);
	return tree;
    }

    public Tree newConstituent(TreeNode root) {
	return this.newConstituent(root, annotationContainer.DEFAULT_GROUP);
    }

    public void addConstituencyFromParentheses(String parseOut) throws Exception {
	Tree.parenthesesToKaf(parseOut, this);
    }

    public NonTerminal newNonTerminal(String id, String label) {
	NonTerminal tn = new NonTerminal(id, label);
	String newEdgeId = idManager.getNextId(AnnotationType.EDGE);
	tn.setEdgeId(newEdgeId);
	return tn;
    }

    public NonTerminal newNonTerminal(String label) {
	String newId = idManager.getNextId(AnnotationType.NON_TERMINAL);
	String newEdgeId = idManager.getNextId(AnnotationType.EDGE);
	NonTerminal newNonterminal = new NonTerminal(newId, label);
	newNonterminal.setEdgeId(newEdgeId);
	return newNonterminal;
    }

    public Terminal newTerminal(String id, Span<Term> span) {
	Terminal tn = new Terminal(id, span);
	String newEdgeId = idManager.getNextId(AnnotationType.EDGE);
	tn.setEdgeId(newEdgeId);
	return tn;
    }

    public Terminal newTerminal(Span<Term> span) {
	String newId = idManager.getNextId(AnnotationType.TERMINAL);
	String newEdgeId = idManager.getNextId(AnnotationType.EDGE);
	Terminal tn = new Terminal(newId, span);
	tn.setEdgeId(newEdgeId);
	return tn;
    }

    public Topic newTopic(String value) {
	Topic newTopic = new Topic(value);
	annotationContainer.add(newTopic, Layer.TOPICS);
	return newTopic;
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

    void addUnknownLayer(Element layer) {
	annotationContainer.add(layer);
    }

    /** Returns the raw text **/
    public String getRawText() {
	return annotationContainer.getRawText();
    }
    
    public List<Annotation> getLayer(Layer layer) {
	return annotationContainer.get(layer);
    }
    
    public List<Annotation> getLayer(Layer layer, String group) {
	return annotationContainer.get(layer, group);
    }

    /** Returns a list containing all WFs in the document */
    public List<WF> getWFs() {
	return (List<WF>)(List<?>) annotationContainer.get(Layer.TEXT);
    }

    /** Returns a list with all sentences. Each sentence is a list of WFs. */
    public List<List<WF>> getSentences() {
	return (List<List<WF>>)(List<?>) annotationContainer.getSentences(Layer.TEXT);
    }

    public Integer getFirstSentence() {
	return this.getWFs().get(0).getSent();
    }

    public Integer getNumSentences() {
	List<WF> wfs = this.getWFs();
	Integer firstSentence = wfs.get(0).getSent();
	Integer lastSentence = wfs.get(wfs.size()-1).getSent();
	return lastSentence - firstSentence + 1;
    }

    public List<Integer> getSentsByParagraph(Integer para) {
	return this.annotationContainer.getParaSents(para);
    }

    public Integer getFirstParagraph() {
	return this.getWFs().get(0).getPara();
    }

    public Integer getNumParagraphs() {
	return this.annotationContainer.getNumParagraphs();
    }

    /** Returns a list with all terms in the document. */
    public List<Term> getTerms() {
	return (List<Term>)(List<?>) annotationContainer.get(Layer.TERMS);
    }

    /** Retrieve the term at position index.
     * @param index the global index of the term in the document, starting at zero.
     * @return the required term.
     */
    public Term termNth(Integer index) {
        return this.getTerms().get(index) ;
    }

    /** Returns a list of terms containing the word forms given on argument.
     * @param wfs a list of word forms whose terms will be found.
     * @return a list of terms containing the given word forms.
     */
    /*
    public List<Term> getTermsByWFs(List<WF> wfs) {
	return annotationContainer.getTermsByWFs(wfs);
    }
    */

    public List<Term> getSentenceTerms(int sent) {
	return (List<Term>)(List<?>) annotationContainer.getSentAnnotations(sent, Layer.TERMS);
    }

    public List<String> getMarkSources() {
	return annotationContainer.getGroupIDs(Layer.MARKABLES);
    }

    public List<Mark> getMarks(String source) {
	return (List<Mark>)(List<?>) annotationContainer.get(Layer.MARKABLES, source);
    }

    public List<Dep> getDeps() {
	return (List<Dep>)(List<?>) annotationContainer.get(Layer.DEPS);
    }

    public List<Chunk> getChunks() {
	return (List<Chunk>)(List<?>) annotationContainer.get(Layer.CHUNKS);
    }

    /** Returns a list with all entities in the document */
    public List<Entity> getEntities() {
	return (List<Entity>)(List<?>) annotationContainer.get(Layer.ENTITIES);
    }

    public List<Coref> getCorefs() {
	return (List<Coref>)(List<?>) annotationContainer.get(Layer.COREFERENCES);
    }

    public List<Timex3> getTimeExs() {
	return (List<Timex3>)(List<?>) annotationContainer.get(Layer.TIME_EXPRESSIONS);
    }

    public List<TLink> getTLinks() {
	return (List<TLink>)(List<?>) annotationContainer.get(Layer.TEMPORAL_RELATIONS);
    }

    public List<CLink> getCLinks() {
	return (List<CLink>)(List<?>) annotationContainer.get(Layer.CAUSAL_RELATIONS);
    }

    /** Returns a list with all relations in the document */
    public List<Feature> getProperties() {
	return (List<Feature>)(List<?>) annotationContainer.get(Layer.PROPERTIES);
    }

    /** Returns a list with all relations in the document */
    public List<Feature> getCategories() {
	return (List<Feature>)(List<?>) annotationContainer.get(Layer.CATEGORIES);
    }

    public List<Opinion> getOpinions() {
	return (List<Opinion>)(List<?>) annotationContainer.get(Layer.OPINIONS);
    }

    /** Returns a list with all relations in the document */
    public List<Relation> getRelations() {
	return (List<Relation>)(List<?>) annotationContainer.get(Layer.RELATIONS);
    }

    public List<Tree> getConstituents(String type) {
	return (List<Tree>)(List<?>) annotationContainer.get(Layer.CONSTITUENCY, type);
    }

    public List<Tree> getConstituents() {
	return (List<Tree>)(List<?>) annotationContainer.get(Layer.CONSTITUENCY, annotationContainer.DEFAULT_GROUP);
    }

    public List<Predicate> getPredicates() {
	return (List<Predicate>)(List<?>) annotationContainer.get(Layer.SRL);
    }

    public List<Factuality> getFactualities() {
	return (List<Factuality>)(List<?>) annotationContainer.get(Layer.FACTUALITY_LAYER);
    }

    public Set<Element> getUnknownLayers() {
	return annotationContainer.getUnknownLayers();
    }
    
    public List<Annotation> getBySent(Layer layer, Integer sent) {
	return this.annotationContainer.getSentAnnotations(sent, layer);
    }
    
    public List<Annotation> getBySent(Layer layer, String group, Integer sent) {
	return this.annotationContainer.getSentAnnotations(sent, layer, group);
    }
    
    public List<Annotation> getByPara(Layer layer, Integer para) {
	return this.annotationContainer.getParaAnnotations(para, layer);
    }
    
    public List<Annotation> getByPara(Layer layer, String group, Integer para) {
	return this.annotationContainer.getParaAnnotations(para, layer, group);
    }

    /*
    public List<WF> getWFsBySent(Integer sent) {
	return (List<WF>)(List<?>) this.annotationContainer.getSentAnnotations(sent, AnnotationType.WF);
    }

    public List<WF> getWFsByPara(Integer para) {
	return (List<WF>)(List<?>) this.annotationContainer.getParaAnnotations(para, AnnotationType.WF);
    }

    public List<Term> getTermsBySent(Integer sent) {
	return (List<Term>)(List<?>) this.annotationContainer.getSentAnnotations(sent, AnnotationType.TERM);
    }

    public List<Term> getTermsByPara(Integer para) {
	return (List<Term>)(List<?>) this.annotationContainer.getParaAnnotations(para, AnnotationType.TERM);
    }

    public List<Entity> getEntitiesBySent(Integer sent) {
	return (List<Entity>)(List<?>) this.annotationContainer.getSentAnnotations(sent, AnnotationType.ENTITY);
    }

    public List<Entity> getEntitiesByPara(Integer para) {
	return (List<Entity>)(List<?>) this.annotationContainer.getParaAnnotations(para, AnnotationType.ENTITY);
    }

    public List<Dep> getDepsBySent(Integer sent) {
	return (List<Dep>)(List<?>) this.annotationContainer.getSentAnnotations(sent, AnnotationType.DEP);
    }

    public List<Dep> getDepsByPara(Integer para) {
	return (List<Dep>)(List<?>) this.annotationContainer.getParaAnnotations(para, AnnotationType.DEP);
    }

    public List<Tree> getConstituentsBySent(String treeType, Integer sent) {
	return (List<Tree>)(List<?>) this.annotationContainer.getSentAnnotations(sent, AnnotationType.TREE, treeType);
    }

    public List<Tree> getConstituentsBySent(Integer sent) {
        return getConstituentsBySent("notype", sent);
    }

    public List<Tree> getConstituentsByPara(String treeType, Integer para) {
	return (List<Tree>)(List<?>) this.annotationContainer.getParaAnnotations(para, AnnotationType.TREE, treeType);
    }

    public List<Tree> getConstituentsByPara(Integer para) {
	return this.getConstituentsByPara("notype", para);
    }

    public List<Chunk> getChunksBySent(Integer sent) {
	return (List<Chunk>)(List<?>) this.annotationContainer.getSentAnnotations(sent, AnnotationType.CHUNK);
    }

    public List<Chunk> getChunksByPara(Integer para) {
	return (List<Chunk>)(List<?>) this.annotationContainer.getParaAnnotations(para, AnnotationType.CHUNK);
    }

    /*
    public List<Coref> getCorefsBySent(Integer sent) {
	List<Coref> corefs = this.annotationContainer.corefsIndexedBySent.get(sent);
	return (corefs == null) ? new ArrayList<Coref>() : corefs;
    }

    public List<Coref> getCorefsByPara(Integer para) {
	return this.annotationContainer.getLayerByPara(para, this.annotationContainer.corefsIndexedBySent);
    }
    */

    /*
    public List<Opinion> getOpinionsBySent(Integer sent) {
	List<Opinion> opinions = this.annotationContainer.opinionsIndexedBySent.get(sent);
	return (opinions == null) ? new ArrayList<Opinion>() : opinions;
    }

    public List<Opinion> getOpinionsByPara(Integer para) {
	return this.annotationContainer.getLayerByPara(para, this.annotationContainer.opinionsIndexedBySent);
    }
    */

    /*
    public List<Predicate> getPredicatesBySent(Integer sent) {
	return (List<Predicate>)(List<?>) this.annotationContainer.getSentAnnotations(sent, AnnotationType.PREDICATE);
    }

    public List<Predicate> getPredicatesByPara(Integer para) {
	return (List<Predicate>)(List<?>) this.annotationContainer.getParaAnnotations(para, AnnotationType.PREDICATE);
    }
    */

    /** Returns current timestamp. */
    public String createTimestamp() {
	Date date = new Date();
	//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	String formattedDate = sdf.format(date);
	return formattedDate;
    }

    /** Copies the annotations to another KAF document */
    /*
    private void copyAnnotationsToKAF(KAFDocument kaf,
				      List<WF> wfs,
				      List<Term> terms,
				      List<Dep> deps,
				      List<Chunk> chunks,
				      List<Entity> entities,
				      List<Coref> corefs,
				      List<Timex3> timeExs,
				      List<Feature> properties,
				      List<Feature> categories,
				      List<Opinion> opinions,
				      List<Relation> relations,
				      List<Predicate> predicates
				      ) {
	HashMap<String, WF> copiedWFs = new HashMap<String, WF>();
	HashMap<String, Term> copiedTerms = new HashMap<String, Term>();
	HashMap<String, Relational> copiedRelationals = new HashMap<String, Relational>();

	// WFs
	for (WF wf : wfs) {
	    WF wfCopy = new WF(wf, kaf.getAnnotationContainer());
	    kaf.insertWF(wfCopy);
	    copiedWFs.put(wf.getId(), wfCopy);
	}
	// Terms
	for (Term term : terms) {
	    Term termCopy = new Term(term, copiedWFs);
	    kaf.insertTerm(termCopy);
	    copiedTerms.put(term.getId(), termCopy);
	}
	// Deps
	for (Dep dep : deps) {
	    Dep depCopy = new Dep(dep, copiedTerms);
	    kaf.insertDep(depCopy);
	}
	// Chunks
	for (Chunk chunk : chunks) {
	    Chunk chunkCopy = new Chunk(chunk, copiedTerms);
	    kaf.insertChunk(chunkCopy);
	}
	// Entities
	for (Entity entity : entities) {
	    Entity entityCopy = new Entity(entity, copiedTerms);
	    kaf.insertEntity(entityCopy);
	    copiedRelationals.put(entity.getId(), entityCopy);
	}
	// Coreferences
	for (Coref coref : corefs) {
	    Coref corefCopy = new Coref(coref, copiedTerms);
	    kaf.insertCoref(corefCopy);
	}
	// TimeExpressions
	for (Timex3 timex3 : timeExs) {
	    Timex3 timex3Copy = new Timex3(timex3, copiedWFs);
	    kaf.insertTimex3(timex3Copy);
	}
	// Properties
	for (Feature property : properties) {
	    Feature propertyCopy = new Feature(property, copiedTerms);
	    kaf.insertProperty(propertyCopy);
	    copiedRelationals.put(property.getId(), propertyCopy);
	}
	// Categories
	for (Feature category : categories) {
	    Feature categoryCopy = new Feature(category, copiedTerms);
	    kaf.insertCategory(categoryCopy);
	    copiedRelationals.put(category.getId(), categoryCopy);
	}
	// Opinions
	for (Opinion opinion : opinions) {
	    Opinion opinionCopy = new Opinion(opinion, copiedTerms);
	    kaf.insertOpinion(opinionCopy);
	}
	// Relations
	for (Relation relation : relations) {
	    Relation relationCopy = new Relation(relation, copiedRelationals);
	    kaf.insertRelation(relationCopy);
	}
	// Predicates
	/*
	for (Predicate predicate : predicates) {
	    Predicate predicateCopy = new Predicate(predicate, copiedTerms);
	    kaf.insertPredicate(predicateCopy);
	}

    }

    /** Returns a new document containing all annotations related to the given WFs */
    /* Couldn't index opinion by terms. Terms are added after the Opinion object is created, and there's no way to access the annotationContainer from the Opinion.*/
    /*
    public KAFDocument split(List<WF> wfs) {
        List<Term> terms = this.annotationContainer.getTermsByWFs(wfs);
	List<Dep> deps = this.annotationContainer.getDepsByTerms(terms);
	List<Chunk> chunks = this.annotationContainer.getChunksByTerms(terms);
	List<Entity> entities = this.annotationContainer.getEntitiesByTerms(terms);
	List<Coref> corefs = this.annotationContainer.getCorefsByTerms(terms);
	List<Timex3> timeExs = this.annotationContainer.getTimeExsByWFs(wfs);
	List<Feature> properties = this.annotationContainer.getPropertiesByTerms(terms);
	List<Feature> categories = this.annotationContainer.getCategoriesByTerms(terms);
	// List<Opinion> opinions = this.annotationContainer.getOpinionsByTerms(terms);
	List<Predicate> predicates = this.annotationContainer.getPredicatesByTerms(terms);
	List<Relational> relationals = new ArrayList<Relational>();
	relationals.addAll(properties);
	relationals.addAll(categories);
	relationals.addAll(entities);
	List<Relation> relations = this.annotationContainer.getRelationsByRelationals(relationals);

	KAFDocument newKaf = new KAFDocument(this.getLang(), this.getVersion());
	newKaf.addLinguisticProcessors(this.getLinguisticProcessors());
	this.copyAnnotationsToKAF(newKaf, wfs, terms, deps, chunks, entities, corefs, timeExs, properties, categories, new ArrayList<Opinion>(), relations, predicates);

	return newKaf;
    }
    */
    
    private KAFDocument createDocFromWFs() {
	KAFDocument newDoc = new KAFDocument("en", "v?");
	return newDoc;
    }

    public List<KAFDocument> splitInSentences()
    {
	List<KAFDocument> sentNafs = new ArrayList<KAFDocument>();
	Integer numParagraphs = this.getNumParagraphs();
	for (Integer paragraph = 1; paragraph <= numParagraphs; paragraph++) {
	    List<Integer> sentences = this.getSentsByParagraph(paragraph);
	    for (Integer sentence : sentences) {
		KAFDocument naf = new KAFDocument(this.getLang(), this.getVersion());
		naf.setRawText(this.getRawText());
		for (Layer layer : Layer.values()) {
		    if (this.isSentenceLevelAnnotationType(layer)) {
			List<Annotation> annotations = new ArrayList<Annotation>();
			if (this.isMultiLayerAnnotationType(layer)) {
			    for (String groupId : annotationContainer.getGroupIDs(layer)) {
				annotations.addAll(this.getBySent(layer, groupId, sentence));
			    }
			} else {
			    annotations = this.getBySent(layer, sentence);
			}
			for (Annotation ann : annotations) {
			    naf.addExistingAnnotation(ann, layer);
			}
		    }
		}
		sentNafs.add(naf);
	    }
	}
	return sentNafs;
    }

    public List<KAFDocument> splitInParagraphs()
    {
	List<KAFDocument> paraNafs = new ArrayList<KAFDocument>();
	Integer numParagraphs = this.getNumParagraphs();
	for (Integer paragraph = 1; paragraph <= numParagraphs; paragraph++) {
	    KAFDocument naf = new KAFDocument(this.getLang(), this.getVersion());
	    naf.setRawText(this.getRawText());
	    for (Layer layer : Layer.values()) {
		if (isParagraphLevelAnnotationType(layer)) {
		    List<Annotation> annotations = new ArrayList<Annotation>();
		    if (isMultiLayerAnnotationType(layer)) {
			for (String groupId : annotationContainer.getGroupIDs(layer)) {
			    annotations.addAll(this.getByPara(layer, groupId, paragraph));
			}
		    } else {
			annotations = this.getByPara(layer, paragraph);
		    }
		    for (Annotation ann : annotations) {
			naf.addExistingAnnotation(ann, layer);
		    }
		}
	    }
	    paraNafs.add(naf);
	}
	return paraNafs;
    }
    
    public static KAFDocument join(List<KAFDocument> nafs)
    {
	KAFDocument firstNaf = nafs.get(0);
	KAFDocument joinedNaf = new KAFDocument(firstNaf.getLang(), nafs.get(0).getVersion());
	joinedNaf.setRawText(firstNaf.getRawText());
	for (KAFDocument nafPart : nafs) {
	    for (Layer layer : Layer.values()) {
		List<Annotation> annotations = new ArrayList<Annotation>();
		if (isMultiLayerAnnotationType(layer)) {
		    for (String groupId : nafPart.annotationContainer.getGroupIDs(layer)) {
			annotations.addAll(nafPart.getLayer(layer, groupId));
		    }
		} else {
		    annotations = nafPart.getLayer(layer);
		}
		for (Annotation ann : annotations) {
		    joinedNaf.addExistingAnnotation(ann, layer);
		}
	    }
	}
	return joinedNaf;
    }
    
    public Integer getParagraph()
    {
	List<WF> wfs = this.getWFs();
	return (wfs.size() > 0) ? this.getWFs().get(0).getPara() : null;
    }
    
    public Integer getSentence()
    {
	List<WF> wfs = this.getWFs();
	return (wfs.size() > 0) ? this.getWFs().get(0).getSent() : null;
    }

//  Join...
    
    public void addExistingAnnotation(Annotation ann, Layer layer) {
	if (isIdentifiableAnnotationType(layer)) {
	    AnnotationType type = layerAnnotationTypes.get(layer);
	    String newId = idManager.getNextId(type);
	    ((IdentifiableAnnotation) ann).setId(newId);
	}
	annotationContainer.add(ann, layer);
    }
    
    private static Boolean isMultiLayerAnnotationType(Layer layer) {
	AnnotationType type = layerAnnotationTypes.get(layer);
	if (type == null) return false;
	Class<?> annotationClass = annotationTypeClasses.get(type);
	if (annotationClass == null) return false;
	return MultiLayerAnnotation.class.isAssignableFrom(annotationClass);
    }
    
    private static Boolean isSentenceLevelAnnotationType(Layer layer) {
	AnnotationType type = layerAnnotationTypes.get(layer);
	if (type == null) return false;
	Class<?> annotationClass = annotationTypeClasses.get(type);
	if (annotationClass == null) return false;
	return SentenceLevelAnnotation.class.isAssignableFrom(annotationClass);
    }
    
    private static Boolean isParagraphLevelAnnotationType(Layer layer) {
	AnnotationType type = layerAnnotationTypes.get(layer);
	if (type == null) return false;
	Class<?> annotationClass = annotationTypeClasses.get(type);
	if (annotationClass == null) return false;
	return ParagraphLevelAnnotation.class.isAssignableFrom(annotationClass);
    }
    
    private static Boolean isIdentifiableAnnotationType(Layer layer) {
	AnnotationType type = layerAnnotationTypes.get(layer);
	if (type == null) return false;
	Class<?> annotationClass = annotationTypeClasses.get(type);
	if (annotationClass == null) return false;
	return IdentifiableAnnotation.class.isAssignableFrom(annotationClass);
    }
    

    public WF createFromWF(WF origWf)
    {
	WF newWf = this.newWF(origWf.getForm(), origWf.getOffset());
	newWf.setSent(origWf.getSent());
	if (origWf.hasPara()) newWf.setPara(origWf.getPara());
	if (origWf.hasPage()) newWf.setPage(origWf.getPage());
	if (origWf.hasOffset()) newWf.setLength(origWf.getLength());
	if (origWf.hasXpath()) newWf.setXpath(origWf.getXpath());
	return newWf;
    }

    public Term createFromTerm(Term origTerm, Map<String, WF> wfIndex)
    {
	Span<WF> newSpan = this.newWFSpan();
	for (WF origWf : origTerm.getSpan().getTargets()) {
	    newSpan.addTarget(wfIndex.get(origWf.getId()));
	}
	Term newTerm = (origTerm.isComponent()) ? this.newTerm(newSpan, true) : this.newTerm(newSpan);
	if (origTerm.hasType()) newTerm.setType(origTerm.getType());
	if (origTerm.hasLemma()) newTerm.setLemma(origTerm.getLemma());
	if (origTerm.hasPos()) newTerm.setPos(origTerm.getPos());
	if (origTerm.hasMorphofeat()) newTerm.setMorphofeat(origTerm.getMorphofeat());
	if (origTerm.hasCase()) newTerm.setCase(origTerm.getCase());
        if (origTerm.hasSentiment()) {
	    Term.Sentiment origSentiment = origTerm.getSentiment();
	    Term.Sentiment newSentiment = this.newSentiment();
	    if (origSentiment.hasResource()) newSentiment.setResource(origSentiment.getResource());
	    if (origSentiment.hasPolarity()) newSentiment.setPolarity(origSentiment.getPolarity());
	    if (origSentiment.hasStrength()) newSentiment.setStrength(origSentiment.getStrength());
	    if (origSentiment.hasSubjectivity()) newSentiment.setSubjectivity(origSentiment.getSubjectivity());
	    if (origSentiment.hasSentimentSemanticType()) newSentiment.setSentimentSemanticType(origSentiment.getSentimentSemanticType());
	    if (origSentiment.hasSentimentModifier()) newSentiment.setSentimentModifier(origSentiment.getSentimentModifier());
	    if (origSentiment.hasSentimentMarker()) newSentiment.setSentimentMarker(origSentiment.getSentimentMarker());
	    if (origSentiment.hasSentimentProductFeature()) newSentiment.setSentimentProductFeature(origSentiment.getSentimentProductFeature());
	    newTerm.setSentiment(newSentiment);
	}
	List<ExternalRef> extRefs = this.createFromExternalRefs(origTerm.getExternalRefs());
	newTerm.addExternalRefs(extRefs);
	if (!newTerm.isComponent()) {
	    List<Term> origComponents = new ArrayList<Term>();
	    for (Term origComponent : origComponents) {
		Term newComponent = this.createFromTerm(origComponent, wfIndex);
		newTerm.addComponent(newComponent);
	    }
	}
	return newTerm;
    }

    public Dep createFromDep(Dep origDep, HashMap<String, Term> termIndex)
    {
	Dep newDep = this.newDep(termIndex.get(origDep.getFrom().getId()), termIndex.get(origDep.getTo().getId()), origDep.getRfunc());
	if (origDep.hasCase()) newDep.setCase(origDep.getCase());
	return newDep;
    }

    public Chunk createFromChunk(Chunk origChunk, HashMap<String, Term> termIndex)
    {
	Span<Term> newSpan = this.newTermSpan();
	for (Term origTerm : origChunk.getSpan().getTargets()) {
	    newSpan.addTarget(termIndex.get(origTerm.getId()));
	}
	Chunk newChunk = this.newChunk(origChunk.getPhrase(), newSpan);
	if (origChunk.hasCase()) newChunk.setCase(origChunk.getCase());
	return newChunk;
    }

    public Entity createFromEntity(Entity origEntity, HashMap<String, Term> termIndex)
    {
	List<Span<Term>> newReferences = new ArrayList<Span<Term>>();
	for (Span<Term> span : origEntity.getSpans()) {
	    Span<Term> newSpan = this.newTermSpan();
	    for (Term origTerm : span.getTargets()) {
		newSpan.addTarget(termIndex.get(origTerm.getId()));
	    }
	    newReferences.add(newSpan);
	}
	Entity newEntity = this.newEntity(newReferences);
	if (origEntity.hasType()) newEntity.setType(origEntity.getType());
	List<ExternalRef> extRefs = this.createFromExternalRefs(origEntity.getExternalRefs());
	newEntity.addExternalRefs(extRefs);
	return newEntity;
    }
    
    public Tree createFromConstituent(Tree origTree, HashMap<String, Term> termIndex)
    {
	TreeNode newRoot = this.createFromTreeNode(origTree.getRoot(), termIndex);
	Tree newTree = this.newConstituent(newRoot);
	return newTree;
    }

    public TreeNode createFromTreeNode(TreeNode origNode, HashMap<String, Term> termIndex)
    {
	if (origNode.isTerminal()) {
	    Span<Term> origSpan = ((Terminal) origNode).getSpan();
	    Span<Term> newSpan = this.newTermSpan();
	    for (Term origTerm : origSpan.getTargets()) {
		newSpan.addTarget(termIndex.get(origTerm.getId()));
	    }
	    Terminal newNode = this.newTerminal(newSpan);
	    if (origNode.hasEdgeId()) newNode.setEdgeId(origNode.getEdgeId());
	    return newNode;
	}
	else {
	    String label = ((NonTerminal) origNode).getLabel();
	    NonTerminal newNode = this.newNonTerminal(label);
	    for (TreeNode origChild : ((NonTerminal) origNode).getChildren()) {
		TreeNode newChild = this.createFromTreeNode(origChild, termIndex);
		try {
		    newNode.addChild(newChild);
		} catch (Exception e) {}
	    }
	    if (origNode.hasEdgeId()) newNode.setEdgeId(origNode.getEdgeId());
	    return newNode;
	}
    }

    public Coref createFromCoref(Coref origCoref, HashMap<String, Term> termIndex)
    {
	List<Span<Term>> newMentions = new ArrayList<Span<Term>>();
	for (Span<Term> span : origCoref.getSpans()) {
	    Span<Term> newSpan = this.newTermSpan();
	    for (Term origTerm : span.getTargets()) {
		newSpan.addTarget(termIndex.get(origTerm.getId()));
	    }
	    newMentions.add(newSpan);
	}
	Coref newCoref = this.newCoref(newMentions);
	return newCoref;
    }

    /*
    public String insertTimex3(Timex3 timex3)
    {
	String newId = idManager.getNextId(Annotations.TIMEX3);
	timex3.setId(newId);
	annotationContainer.add(timex3);
	return newId;
    }
    */

    /*
    public String insertProperty(Feature property)
    {
	String newId = idManager.getNextId(Annotations.PROPERTY);
	property.setId(newId);
	annotationContainer.add(property);
	return newId;
    }
    */

    public Opinion createFromOpinion(Opinion origOpinion, HashMap<String, Term> termIndex)
    {
	Opinion newOpinion = this.newOpinion();

	if (origOpinion.hasOpinionHolder()) {
	    Opinion.OpinionHolder origHolder = origOpinion.getOpinionHolder();
	    Span<Term> newSpan = this.newTermSpan();
	    for (Term origTerm : origHolder.getSpan().getTargets()) {
		newSpan.addTarget(termIndex.get(origTerm.getId()));
	    }
	    Opinion.OpinionHolder newHolder = newOpinion.createOpinionHolder(newSpan);
	    if (origHolder.hasType()) newHolder.setType(origHolder.getType());
	}

	if (origOpinion.hasOpinionTarget()) {
	    Opinion.OpinionTarget origTarget = origOpinion.getOpinionTarget();
	    Span<Term> newSpan = this.newTermSpan();
	    for (Term origTerm : origTarget.getSpan().getTargets()) {
		newSpan.addTarget(termIndex.get(origTerm.getId()));
	    }
	    Opinion.OpinionTarget newTarget = newOpinion.createOpinionTarget(newSpan);
	}

	if (origOpinion.hasOpinionExpression()) {
	    Opinion.OpinionExpression origExpression = origOpinion.getOpinionExpression();
	    Span<Term> newSpan = this.newTermSpan();
	    for (Term origTerm : origExpression.getSpan().getTargets()) {
		newSpan.addTarget(termIndex.get(origTerm.getId()));
	    }
	    Opinion.OpinionExpression newExpression = newOpinion.createOpinionExpression(newSpan);
	    if (origExpression.hasPolarity()) newExpression.setPolarity(origExpression.getPolarity());
	    if (origExpression.hasStrength()) newExpression.setStrength(origExpression.getStrength());
	    if (origExpression.hasSubjectivity()) newExpression.setSubjectivity(origExpression.getSubjectivity());
	    if (origExpression.hasSentimentSemanticType()) newExpression.setSentimentSemanticType(origExpression.getSentimentSemanticType());
	    if (origExpression.hasSentimentProductFeature()) newExpression.setSentimentProductFeature(origExpression.getSentimentProductFeature());
	}

	return newOpinion;
    }

    public Predicate createFromPredicate(Predicate origPredicate, HashMap<String, Term> termIndex)
    {
	Span<Term> newSpan = this.newTermSpan();
	for (Term origTerm : origPredicate.getSpan().getTargets()) {
	    newSpan.addTarget(termIndex.get(origTerm.getId()));
	}
	Predicate newPredicate = this.newPredicate(newSpan);
	if (origPredicate.hasUri()) newPredicate.setUri(origPredicate.getUri());
	if (origPredicate.hasConfidence()) newPredicate.setConfidence(origPredicate.getConfidence());
	List<Predicate.Role> origRoles = origPredicate.getRoles();
	List<Predicate.Role> newRoles = new ArrayList<Predicate.Role>();
	for (Predicate.Role origRole : origRoles) {
	    Span<Term> newRoleSpan = this.newTermSpan();
	    for (Term origTerm : origRole.getSpan().getTargets()) {
		newRoleSpan.addTarget(termIndex.get(origTerm.getId()));
	    }
	    Predicate.Role newRole = this.newRole(newPredicate, origRole.getSemRole(), newRoleSpan);
	    List<ExternalRef> extRefs = this.createFromExternalRefs(origRole.getExternalRefs());
	    newRole.addExternalRefs(extRefs);
	    newPredicate.addRole(newRole);
	}
	List<ExternalRef> extRefs = this.createFromExternalRefs(origPredicate.getExternalRefs());
	newPredicate.addExternalRefs(extRefs);
	return newPredicate;
    }

    public List<ExternalRef> createFromExternalRefs(List<ExternalRef> origExternalRefs)
    {
	List<ExternalRef> newExtRefs = new ArrayList<ExternalRef>();
	for (ExternalRef origExtRef : origExternalRefs) {
	    newExtRefs.add(this.createFromExternalRef(origExtRef));
	}
	return newExtRefs;
    }

    public ExternalRef createFromExternalRef(ExternalRef origExternalRef)
    {
	ExternalRef newExtRef = this.newExternalRef(origExternalRef.getResource(), origExternalRef.getReference());
	if (origExternalRef.hasConfidence()) newExtRef.setConfidence(origExternalRef.getConfidence());
	for(ExternalRef subRef : origExternalRef.getExternalRefs()) {
	    newExtRef.addExternalRef(createFromExternalRef(subRef));
	}
	return newExtRef;
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
	return this.newWF(id, form, 0);
    }

    /** Deprecated */
    public WF createWF(String form) {
	return this.newWF(form, 0);
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
    public Term.Sentiment createSentiment() {
	return this.newSentiment();
    }

    /** Deprecated */
    /*
    public Component newComponent(String id, Term term, String lemma, String pos) {
	Component newComponent = this.newComponent(id, term);
	newComponent.setLemma(lemma);
	newComponent.setPos(pos);
	return newComponent;
    }
    */

    /** Deprecated */
    
    /*public Component newComponent(Term term, String lemma, String pos) {
	Term.Component newComponent = this.newComponent(term);
	newComponent.setLemma(lemma);
	newComponent.setPos(pos);
	return newComponent;
    }
    */

    /** Deprecated */
    /*
    public Component createComponent(String id, Term term, String lemma, String pos) {
	return this.newComponent(id, term, lemma, pos);
    }
    */

    /** Deprecated */
    /*
      public Component createComponent(Term term, String lemma, String pos) {
	return this.newComponent(term, lemma, pos);
    }
    */

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
	Entity entity = this.newEntity(id, spanReferences);
	entity.setType(type);
	return entity;
    }

    /** Deprecated */
    public Entity createEntity(String type, List<List<Term>> references) {
	List<Span<Term>> spanReferences = new ArrayList<Span<Term>>();
	for (List<Term> list : references) {
	    spanReferences.add(this.list2Span(list));
	}
	Entity entity = this.newEntity(spanReferences);
	entity.setType(type);
	return entity;
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
    /*
    public Feature createProperty(String id, String lemma, List<List<Term>> references) {
	List<Span<Term>> spanReferences = new ArrayList<Span<Term>>();
	for (List<Term> list : references) {
	    spanReferences.add(this.list2Span(list));
	}
	return this.newProperty(id, lemma, spanReferences);
    }
    */

    /** Deprecated */
    /*
    public Feature createProperty(String lemma, List<List<Term>> references) {
	List<Span<Term>> spanReferences = new ArrayList<Span<Term>>();
	for (List<Term> list : references) {
	    spanReferences.add(this.list2Span(list));
	}
	return this.newProperty(lemma, spanReferences);
    }
    */

    /** Deprecated */
    /*
    public Feature createCategory(String id, String lemma, List<List<Term>> references) {
	List<Span<Term>> spanReferences = new ArrayList<Span<Term>>();
	for (List<Term> list : references) {
	    spanReferences.add(this.list2Span(list));
	}
	return this.newCategory(id, lemma, spanReferences);
    }
    */

    /** Deprecated */
    /*
      public Feature createCategory(String lemma, List<List<Term>> references) {
	List<Span<Term>> spanReferences = new ArrayList<Span<Term>>();
	for (List<Term> list : references) {
	    spanReferences.add(this.list2Span(list));
	}
	return this.newCategory(lemma, spanReferences);
    }
    */

    /** Deprecated */
    public Opinion createOpinion() {
	return this.newOpinion();
    }

    /** Deprecated */
    public Opinion createOpinion(String id) {
	return this.newOpinion(id);
    }

    /** Deprecated */
    /*
    public Relation createRelation(Relational from, Relational to) {
	return this.newRelation(from, to);
    }
    */

    /** Deprecated */
    /*
    public Relation createRelation(String id, Relational from, Relational to) {
	return this.newRelation(id, from, to);
    }
    */

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

    public void removeLayer(Layer layer) {
	this.annotationContainer.removeLayer(layer);
    }

    /** Converts a List into a Span */
    static <T extends IdentifiableAnnotation> Span<T> list2Span(List<T> list) {
	Span<T> span = new Span<T>();
	for (T elem : list) {
	    span.addTarget(elem);
	}
	return span;
    }

    /** Converts a List into a Span */
    static <T extends IdentifiableAnnotation> Span<T> list2Span(List<T> list, T head) {
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
    /*
    public List<Term> getTermsFromWFs(List<String> wfIds) {
	return annotationContainer.getTermsByWFIds(wfIds);
    }
    */
    
    // ADDED BY FRANCESCO

    private static final Map<String, Character> DEP_PATH_CHARS = new HashMap<String, Character>();

    private static final Map<String, Pattern> DEP_PATH_REGEXS = new HashMap<String, Pattern>();

    private static char getDepPathChar(final String label) {
    final String key = label.toLowerCase();
    synchronized (DEP_PATH_CHARS) {
        Character letter = DEP_PATH_CHARS.get(key);
        if (letter == null) {
            letter = 'a';
            for (final Character ch : DEP_PATH_CHARS.values()) {
                if (ch >= letter) {
                    letter = (char) (ch + 1);
                }
            }
            DEP_PATH_CHARS.put(key, letter);
        }
        return letter;
    }
    }

    private static String getDepPathString(final Term from, final Iterable<Dep> path) {
    final StringBuilder builder = new StringBuilder("_");
    Term term = from; // current node in the path
    for (final Dep dep : path) {
        char prefix;
        if (dep.getFrom() == term) {
            prefix = '+';
            term = dep.getTo();
        } else {
            prefix = '-';
            term = dep.getFrom();
        }
        for (final String label : dep.getRfunc().split("-")) {
            final Character letter = getDepPathChar(label);
            builder.append(prefix).append(letter);
        }
        builder.append("_");
    }
    return builder.toString();
    }

    private static Pattern getDepPathRegex(final String pattern) {
    synchronized (DEP_PATH_REGEXS) {
        Pattern regex = DEP_PATH_REGEXS.get(pattern);
        if (regex == null) {
            final StringBuilder builder = new StringBuilder();
            builder.append('_');
            int start = -1;
            for (int i = 0; i < pattern.length(); ++i) {
                final char ch = pattern.charAt(i);
                if (Character.isLetter(ch) || ch == '-') {
                    if (start < 0) {
                        start = i;
                    }
                } else {
                    if (start >= 0) {
                        final boolean inverse = pattern.charAt(start) == '-';
                        final String label = pattern.substring(inverse ? start + 1 : start, i);
                        final char letter = getDepPathChar(label);
                        builder.append("([^_]*")
                                .append(Pattern.quote((inverse ? "-" : "+") + letter))
                                .append("[^_]*_)");
                        start = -1;
                    }
                    if (!Character.isWhitespace(ch)) {
                        builder.append(ch);
                    }
                }
            }
            regex = Pattern.compile(builder.toString());
            DEP_PATH_REGEXS.put(pattern, regex);
        }
        return regex;
    }
    }

    public boolean matchDepPath(final Term from, final Iterable<Dep> path, final String pattern) {
    String pathString = getDepPathString(from, path);
    Pattern pathRegex = getDepPathRegex(pattern);
    return pathRegex.matcher(pathString).matches();
    }

    public List<Dep> getDepPath(final Term from, final Term to) {
    if (from == to) {
        return Collections.emptyList();
    }
    final List<Dep> toPath = new ArrayList<Dep>();
    for (Dep dep = getDepToTerm(to); dep != null; dep = getDepToTerm(dep.getFrom())) {
        toPath.add(dep);
        if (dep.getFrom() == from) {
            Collections.reverse(toPath);
            return toPath;
        }
    }
    final List<Dep> fromPath = new ArrayList<Dep>();
    for (Dep dep = getDepToTerm(from); dep != null; dep = getDepToTerm(dep.getFrom())) {
        fromPath.add(dep);
        if (dep.getFrom() == to) {
            return fromPath;
        }
        for (int i = 0; i < toPath.size(); ++i) {
            if (dep.getFrom() == toPath.get(i).getFrom()) {
                for (int j = i; j >= 0; --j) {
                    fromPath.add(toPath.get(j));
                }
                return fromPath;
            }
        }
    }
    return null; // unconnected nodes
    }

    
    public Dep getDepToTerm(final Term term) {
    for (final Dep dep : getDepsByTerm(term)) {
        if (dep.getTo() == term) {
            return dep;
        }
    }
    return null;
    }
    

    /*
    public List<Dep> getDepsFromTerm(final Term term) {
    final List<Dep> result = new ArrayList<Dep>();
    for (final Dep dep : getDepsByTerm(term)) {
        if (dep.getFrom() == term) {
            result.add(dep);
        }
    }
    return result;
    }
    */

    
    public List<Dep> getDepsByTerm(final Term term) {
    return (List<Dep>)(List<?>) this.annotationContainer.getInverse(term, Layer.DEPS);
    }
    
    public Term getTermsHead(Iterable<Term> descendents) {
    final Set<Term> termSet = new HashSet<Term>();
    for (Term term : descendents) {
        termSet.add(term);
    }
    Term root = null;
    for (final Term term : termSet) {
        final Dep dep = getDepToTerm(term);
        if (dep == null || !termSet.contains(dep.getFrom())) {
            if (root == null) {
                root = term;
            } else if (root != term) {
                return null;
            }
        }
    }
    return root;
    }

    public Set<Term> getTermsByDepAncestors(final Iterable<Term> ancestors) {
    final Set<Term> terms = new HashSet<Term>();
    final List<Term> queue = new LinkedList<Term>();
    for (final Term term : ancestors) {
        terms.add(term);
        queue.add(term);
    }
    while (!queue.isEmpty()) {
        final Term term = queue.remove(0);
        final List<Dep> deps = getDepsByTerm(term);
        for (final Dep dep : deps) {
            if (dep.getFrom() == term) {
                if (terms.add(dep.getTo())) {
                    queue.add(dep.getTo());
                }
            }
        }
    }
    return terms;
    }

    public Set<Term> getTermsByDepAncestors(final Iterable<Term> ancestors, final String pattern) {
    final Set<Term> result = new HashSet<Term>();
    for (final Term term : ancestors) {
        for (final Term descendent : getTermsByDepAncestors(Collections.singleton(term))) {
            final List<Dep> path = getDepPath(term, descendent);
            if (matchDepPath(term, path, pattern)) {
                result.add(descendent);
            }
        }
    }
    return result;
    }
    
    public List<Entity> getEntitiesByTerm(Term term) {
	return (List<Entity>)(List<?>) this.annotationContainer.getInverse(term, Layer.ENTITIES);
    }


    public List<Predicate> getPredicatesByTerm(Term term) {
	return (List<Predicate>)(List<?>) this.annotationContainer.getInverse(term, Layer.SRL);
    }
    
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof KAFDocument)) return false;
	KAFDocument naf = (KAFDocument) o;
	/* Language and version */
	if (!this.getLang().equals(naf.getLang()) || !this.getVersion().equals(naf.getVersion())) return false;
	/* NAF header */
	if (!this.headerEquals(naf)) return false;
	/* Layers and annotations */
	return Utils.areEquals(this.annotationContainer, naf.annotationContainer);
    }

    private Boolean headerEquals(KAFDocument naf) {
	return Utils.areEquals(this.fileDesc, naf.fileDesc) &&
		Utils.areEquals(this._public, naf._public) &&
		Utils.areEquals(this.lps, naf.lps);
    }
    
    
    static class Utils {
	static boolean areEquals(Object a, Object b) {
	    return (a == null) ? (b == null) : a.equals(b);
	}
    }

}

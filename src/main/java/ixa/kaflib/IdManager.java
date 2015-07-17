package ixa.kaflib;

import java.util.regex.*;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.io.Serializable;
import ixa.kaflib.KAFDocument.AnnotationType;


/** Manages ID creation. Each ID is created taking into account the annotations of the same type created so far, in a document context. This class keeps a counter for each type of annotation (terms, chunks...). */
class IdManager implements Serializable {

    /* Prefix of each type of ids */
    private Map<AnnotationType, String> prefixes;

    private static final String WF_PREFIX = "w";
    private static final String TERM_PREFIX = "t";
    private static final String MARK_PREFIX = "m";
    private static final String MW_PREFIX = "t.mw";
    private static final String COMPONENT_PREFIX = ".";
    private static final String CHUNK_PREFIX = "c";
    private static final String ENTITY_PREFIX = "e";
    private static final String COREF_PREFIX = "co";
    private static final String TIMEX3_PREFIX = "tmx";
    private static final String TLINK_PREFIX = "tlink";
    private static final String PREDICATE_ANCHOR_PREFIX = "an";
    private static final String CLINK_PREFIX = "clink";
    private static final String LINKED_ENTITY_PREFIX = "le";
    private static final String PROPERTY_PREFIX = "p";
    private static final String CATEGORY_PREFIX = "c";
    private static final String OPINION_PREFIX = "o";
    private static final String RELATION_PREFIX = "r";
    private static final String PREDICATE_PREFIX = "pr";
    private static final String ROLE_PREFIX = "rl";
    private static final String TERMINAL_PREFIX = "ter";
    private static final String NONTERMINAL_PREFIX = "nter";
    private static final String EDGE_PREFIX = "tre";
    private static final String FACTUALITY_PREFIX = "f";
    private static final String STATEMENT_PREFIX = "a";

    /* All the IDs in the document */
    private HashSet<String> ids;

    /* Counters for each type of annotations */
    private Map<AnnotationType, Integer> counters;

    /* Inconsistent ID flags */
    private Map<AnnotationType, Boolean> inconsistentId;


    IdManager() {
	this.prefixes = new HashMap<AnnotationType, String>();
	this.prefixes.put(AnnotationType.WF, WF_PREFIX);
	this.prefixes.put(AnnotationType.TERM, TERM_PREFIX);
	this.prefixes.put(AnnotationType.COMPONENT, COMPONENT_PREFIX);
	this.prefixes.put(AnnotationType.ENTITY, ENTITY_PREFIX);
	this.prefixes.put(AnnotationType.CHUNK, CHUNK_PREFIX);
	this.prefixes.put(AnnotationType.NON_TERMINAL, NONTERMINAL_PREFIX);
	this.prefixes.put(AnnotationType.TERMINAL, TERMINAL_PREFIX);
	this.prefixes.put(AnnotationType.EDGE, EDGE_PREFIX);
	this.prefixes.put(AnnotationType.COREF, COREF_PREFIX);
	this.prefixes.put(AnnotationType.OPINION, OPINION_PREFIX);
	this.prefixes.put(AnnotationType.CLINK, CLINK_PREFIX);
	this.prefixes.put(AnnotationType.TLINK, TLINK_PREFIX);
	this.prefixes.put(AnnotationType.PREDICATE_ANCHOR, PREDICATE_ANCHOR_PREFIX);
	this.prefixes.put(AnnotationType.PREDICATE, PREDICATE_PREFIX);
	this.prefixes.put(AnnotationType.ROLE, ROLE_PREFIX);
	this.prefixes.put(AnnotationType.TIMEX3, TIMEX3_PREFIX);
	this.prefixes.put(AnnotationType.MARK, MARK_PREFIX);
	this.prefixes.put(AnnotationType.LINKED_ENTITY, LINKED_ENTITY_PREFIX);
	this.prefixes.put(AnnotationType.PROPERTY, PROPERTY_PREFIX);
	this.prefixes.put(AnnotationType.CATEGORY, CATEGORY_PREFIX);
	this.prefixes.put(AnnotationType.RELATION, RELATION_PREFIX);
	this.prefixes.put(AnnotationType.FACTUALITY, FACTUALITY_PREFIX);
	this.prefixes.put(AnnotationType.STATEMENT, STATEMENT_PREFIX);
	
	this.ids = new HashSet<String>();

	this.counters = new HashMap<AnnotationType, Integer>();
	for (AnnotationType ann : AnnotationType.values()) {
	    this.counters.put(ann, 0);
	}

	this.inconsistentId = new HashMap<AnnotationType, Boolean>();
	for (AnnotationType ann : AnnotationType.values()) {
	    this.inconsistentId.put(ann, false);
	}
    }
    
    Boolean idExists(String id) {
	return this.ids.contains(id);
    }

    private void insertId(String id) {
	this.ids.add(id);
    }

    private int extractCounterFromId(String id) {
	//Matcher matcher = Pattern.compile(".*?(\\d+)$").matcher(id);
	Matcher matcher = Pattern.compile("\\d+$").matcher(id);
	if (!matcher.find()) {
	    throw new IllegalStateException("IdManager doesn't recognise the given id's (" + id  + ") format.");
	}
	return Integer.valueOf(matcher.group(0));
    }
    
    String getNextId(AnnotationType ann) {
	if (this.inconsistentId.get(ann)) {
	    throw new IllegalStateException("Inconsistent " + ann + "IDs. Can't create new " + ann + " IDs.");
	}
	Integer nextCount = this.counters.get(ann) + 1;
	this.counters.put(ann, nextCount);
	String id = this.prefixes.get(ann) + Integer.toString(nextCount);
	this.insertId(id);
	return id;
    }

    void updateCounter(AnnotationType ann, String id) {
	this.insertId(id);
	try {
	    Integer currentCounter = this.counters.get(ann);
	    Integer newCounter = extractCounterFromId(id);
	    if (currentCounter < newCounter) {
		this.counters.put(ann, newCounter);
	    }
	} catch(IllegalStateException e) {
	    this.inconsistentId.put(ann, true);
	}
    }
}

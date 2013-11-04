package ixa.kaflib;

import java.util.regex.*;
import java.util.HashMap;

/** Manages ID creation. Each ID is created taking into account the annotations of the same type created so far, in a document context. This class keeps a counter for each type of annotation (terms, chunks...). */
class IdManager {

    /* Prefix of each type of ids */
    private static final String WF_PREFIX = "w";
    private static final String TERM_PREFIX = "t";
    private static final String COMPONENT_PREFIX = ".";
    private static final String CHUNK_PREFIX = "c";
    private static final String ENTITY_PREFIX = "e";
    private static final String COREF_PREFIX = "co";
    private static final String PROPERTY_PREFIX = "p";
    private static final String CATEGORY_PREFIX = "c";
    private static final String OPINION_PREFIX = "o";
    private static final String RELATION_PREFIX = "r";
    private static final String PREDICATE_PREFIX = "pr";
    private static final String ROLE_PREFIX = "rl";
    private static final String TERMINAL_PREFIX = "ter";
    private static final String NONTERMINAL_PREFIX = "nter";
    private static final String EDGE_PREFIX = "tre";

    /* Counters for each type of annotations */
    private int wfCounter;
    private int termCounter;
    private int chunkCounter;
    private int entityCounter;
    private int corefCounter;
    private int propertyCounter;
    private int categoryCounter;
    private int opinionCounter;
    private int relationCounter;
    private int predicateCounter;
    private int terminalCounter;
    private int nonterminalCounter;
    private int edgeCounter;
    private HashMap<String, Integer> componentCounter;
    private int roleCounter;

    IdManager() {
	this.wfCounter = 0;
	this.termCounter = 0;
	this.chunkCounter = 0;
	this.entityCounter = 0;
	this.corefCounter = 0;
	this.propertyCounter = 0;
	this.categoryCounter = 0;
	this.opinionCounter = 0;
	this.relationCounter = 0;
	this.predicateCounter = 0;
	this.terminalCounter = 0;
	this.nonterminalCounter = 0;
	this.edgeCounter = 0;
	this.componentCounter = new HashMap<String, Integer>();
	this.roleCounter = 0;
    }

    String getNextWFId() {
	return WF_PREFIX + Integer.toString(++wfCounter);
    }

    String getNextTermId() {
	return TERM_PREFIX + Integer.toString(++termCounter);
    }

    String getNextChunkId() {
	return CHUNK_PREFIX + Integer.toString(++chunkCounter);
    }
    
    String getNextEntityId() {
	return ENTITY_PREFIX + Integer.toString(++entityCounter);
    }
    
    String getNextCorefId() {
	return COREF_PREFIX + Integer.toString(++corefCounter);
    }

    String getNextPropertyId() {
	return PROPERTY_PREFIX + Integer.toString(++propertyCounter);
    }

    String getNextCategoryId() {
	return CATEGORY_PREFIX + Integer.toString(++categoryCounter);
    }

    String getNextOpinionId() {
	return OPINION_PREFIX + Integer.toString(++opinionCounter);
    }

    String getNextRelationId() {
	return RELATION_PREFIX + Integer.toString(++relationCounter);
    }

    String getNextPredicateId() {
	return PREDICATE_PREFIX + Integer.toString(++predicateCounter);
    }

    String getNextTerminalId() {
	return TERMINAL_PREFIX + Integer.toString(++terminalCounter);
    }

    String getNextNonterminalId() {
	return NONTERMINAL_PREFIX + Integer.toString(++nonterminalCounter);
    }

    String getNextEdgeId() {
	return EDGE_PREFIX + Integer.toString(++edgeCounter);
    }

    String getNextComponentId(String termId) {
	String newId;
	int nextIndex;
	if (!componentCounter.containsKey(termId)) {
	    nextIndex = 1;
	} else {
	    nextIndex = componentCounter.get(termId) + 1;
	}
	newId = termId + COMPONENT_PREFIX + Integer.toString(nextIndex);
	componentCounter.put(termId, nextIndex);
	return newId;
    }

    String getNextRoleId() {
	return ROLE_PREFIX + Integer.toString(++roleCounter);
    }

    private int extractCounterFromId(String id) {
	Matcher matcher = Pattern.compile(".*?(\\d+)$").matcher(id);
	if (!matcher.find()) {
	    throw new IllegalStateException("IdManager doesn't recognise the given id's (" + id  + ") format.");
	}
	return Integer.valueOf(matcher.group(1));
    }

    void updateWFCounter(String id) {
	wfCounter = extractCounterFromId(id);
    }

    void updateTermCounter(String id) {
	termCounter = extractCounterFromId(id);
    }

    void updateChunkCounter(String id) {
	chunkCounter = extractCounterFromId(id);
    }

    void updateEntityCounter(String id) {
	entityCounter = extractCounterFromId(id);
    }

    void updateCorefCounter(String id) {
	corefCounter = extractCounterFromId(id);
    }

    void updatePropertyCounter(String id) {
	propertyCounter = extractCounterFromId(id);
    }

    void updateCategoryCounter(String id) {
	categoryCounter = extractCounterFromId(id);
    }

    void updateOpinionCounter(String id) {
	opinionCounter = extractCounterFromId(id);
    }

    void updateRelationCounter(String id) {
	relationCounter = extractCounterFromId(id);
    }

    void updatePredicateCounter(String id) {
	predicateCounter = extractCounterFromId(id);
    }

    void updateTerminalCounter(String id) {
	terminalCounter = extractCounterFromId(id);
    }

    void updateNonterminalCounter(String id) {
	nonterminalCounter = extractCounterFromId(id);
    }

    void updateEdgeCounter(String id) {
	edgeCounter = extractCounterFromId(id);
    }

    void updateComponentCounter(String id, String termId) {
	int componentInd;
	Matcher matcher = Pattern.compile("^"+TERM_PREFIX+"_?\\d+\\"+COMPONENT_PREFIX+"(\\d+)$").matcher(id);
	if (!matcher.find()) {
	    throw new IllegalStateException("IdManager doesn't recognise the given id's (" + id + ") format. Should be "+TERM_PREFIX+"_?[0-9]+\\"+COMPONENT_PREFIX+"[0-9]+");
	}
	componentInd = Integer.valueOf(matcher.group(1));
	componentCounter.put(termId, componentInd);
    }

    void updateRoleCounter(String id) {
        roleCounter = extractCounterFromId(id);
    }
}

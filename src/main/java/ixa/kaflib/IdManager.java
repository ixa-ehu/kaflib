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

    /* Inconsistent ID flags */
    private boolean inconsistentIdText;
    private boolean inconsistentIdTerm;
    private boolean inconsistentIdComponent;
    private boolean inconsistentIdChunk;
    private boolean inconsistentIdEntity;
    private boolean inconsistentIdCoref;
    private boolean inconsistentIdProperty;
    private boolean inconsistentIdCategory;
    private boolean inconsistentIdOpinion;
    private boolean inconsistentIdRelation;
    private boolean inconsistentIdPredicate;
    private boolean inconsistentIdRole;
    private boolean inconsistentIdTerminal;
    private boolean inconsistentIdNonTerminal;
    private boolean inconsistentIdEdge;

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

	this.inconsistentIdText = false;
	this.inconsistentIdTerm = false;
	this.inconsistentIdComponent = false;
	this.inconsistentIdChunk = false;
	this.inconsistentIdEntity = false;
	this.inconsistentIdCoref = false;
	this.inconsistentIdProperty = false;
	this.inconsistentIdCategory = false;
	this.inconsistentIdOpinion = false;
	this.inconsistentIdRelation = false;
	this.inconsistentIdPredicate = false;
	this.inconsistentIdRole = false;
	this.inconsistentIdTerminal = false;
	this.inconsistentIdNonTerminal = false;
	this.inconsistentIdEdge = false;
    }

    String getNextWFId() {
	if (this.inconsistentIdText) {
	    throw new IllegalStateException("Inconsistent WF IDs. Can't create new WF IDs.");
	}
	return WF_PREFIX + Integer.toString(++wfCounter);
    }

    String getNextTermId() {
	if (this.inconsistentIdTerm) {
	    throw new IllegalStateException("Inconsistent term IDs. Can't create new term IDs.");
	}
	return TERM_PREFIX + Integer.toString(++termCounter);
    }

    String getNextChunkId() {
	if (this.inconsistentIdChunk) {
	    throw new IllegalStateException("Inconsistent chunk IDs. Can't create new chunk IDs.");
	}
	return CHUNK_PREFIX + Integer.toString(++chunkCounter);
    }
    
    String getNextEntityId() {
	if (this.inconsistentIdEntity) {
	    throw new IllegalStateException("Inconsistent entity IDs. Can't create new entity IDs.");
	}
	return ENTITY_PREFIX + Integer.toString(++entityCounter);
    }
    
    String getNextCorefId() {
	if (this.inconsistentIdCoref) {
	    throw new IllegalStateException("Inconsistent coref IDs. Can't create new coref IDs.");
	}
	return COREF_PREFIX + Integer.toString(++corefCounter);
    }

    String getNextPropertyId() {
	if (this.inconsistentIdProperty) {
	    throw new IllegalStateException("Inconsistent property IDs. Can't create new property IDs.");
	}
	return PROPERTY_PREFIX + Integer.toString(++propertyCounter);
    }

    String getNextCategoryId() {
	if (this.inconsistentIdCategory) {
	    throw new IllegalStateException("Inconsistent category IDs. Can't create new category IDs.");
	}
	return CATEGORY_PREFIX + Integer.toString(++categoryCounter);
    }

    String getNextOpinionId() {
	if (this.inconsistentIdOpinion) {
	    throw new IllegalStateException("Inconsistent opinion IDs. Can't create new opinion IDs.");
	}
	return OPINION_PREFIX + Integer.toString(++opinionCounter);
    }

    String getNextRelationId() {
	if (this.inconsistentIdRelation) {
	    throw new IllegalStateException("Inconsistent relation IDs. Can't create new relation IDs.");
	}
	return RELATION_PREFIX + Integer.toString(++relationCounter);
    }

    String getNextPredicateId() {
	if (this.inconsistentIdPredicate) {
	    throw new IllegalStateException("Inconsistent predicate IDs. Can't create new predicate IDs.");
	}
	return PREDICATE_PREFIX + Integer.toString(++predicateCounter);
    }

    String getNextTerminalId() {
	if (this.inconsistentIdTerminal) {
	    throw new IllegalStateException("Inconsistent terminal IDs. Can't create new terminal IDs.");
	}
	return TERMINAL_PREFIX + Integer.toString(++terminalCounter);
    }

    String getNextNonterminalId() {
	if (this.inconsistentIdNonTerminal) {
	    throw new IllegalStateException("Inconsistent non-terminal IDs. Can't create new non-terminal IDs.");
	}
	return NONTERMINAL_PREFIX + Integer.toString(++nonterminalCounter);
    }

    String getNextEdgeId() {
	if (this.inconsistentIdEdge) {
	    throw new IllegalStateException("Inconsistent edge IDs. Can't create new edge IDs.");
	}
	return EDGE_PREFIX + Integer.toString(++edgeCounter);
    }

    String getNextComponentId(String termId) {
	String newId;
	int nextIndex;
	if (this.inconsistentIdComponent) {
	    throw new IllegalStateException("Inconsistent component IDs. Can't create new component IDs.");
	}
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
	if (this.inconsistentIdRole) {
	    throw new IllegalStateException("Inconsistent role IDs. Can't create new role IDs.");
	}
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
	try {
	    wfCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdText = true;
	}
    }

    void updateTermCounter(String id) {
	try {
	    termCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdTerm = true;
	}
    }

    void updateChunkCounter(String id) {
	try {
	    chunkCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdChunk = true;
	}
    }

    void updateEntityCounter(String id) {
	try {
	    entityCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdEntity = true;
	}
    }

    void updateCorefCounter(String id) {
	try {
	    corefCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdCoref = true;
	}
    }

    void updatePropertyCounter(String id) {
	try {
	    propertyCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdProperty = true;
	}
    }

    void updateCategoryCounter(String id) {
	try {
	    categoryCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdCategory = true;
	}
    }

    void updateOpinionCounter(String id) {
	try {
	    opinionCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdOpinion = true;
	}
    }

    void updateRelationCounter(String id) {
	try {
	    relationCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdRelation = true;
	}
    }

    void updatePredicateCounter(String id) {
	try {
	    predicateCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdPredicate = true;
	}
    }

    void updateTerminalCounter(String id) {
	try {
	    terminalCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdTerminal = true;
	}
    }

    void updateNonterminalCounter(String id) {
	try {
	    nonterminalCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdNonTerminal = true;
	}
    }

    void updateEdgeCounter(String id) {
	try {
	    edgeCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdEdge = true;
	}
    }

    void updateComponentCounter(String id, String termId) {
	int componentInd;
	Matcher matcher = Pattern.compile("^"+TERM_PREFIX+"_?\\d+\\"+COMPONENT_PREFIX+"(\\d+)$").matcher(id);
	if (!matcher.find()) {
	    /*
	      throw new IllegalStateException("IdManager doesn't recognise the given id's (" + id + ") format. Should be "+TERM_PREFIX+"_?[0-9]+\\"+COMPONENT_PREFIX+"[0-9]+");
	    */
	    this.inconsistentIdComponent = true;
	}
	componentInd = Integer.valueOf(matcher.group(1));
	componentCounter.put(termId, componentInd);
    }

    void updateRoleCounter(String id) {
	try {
	    roleCounter = extractCounterFromId(id);
	} catch(IllegalStateException e) {
	    this.inconsistentIdRole = true;
	}
    }
}

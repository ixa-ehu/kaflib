package ixa.kaflib;

import java.util.regex.*;
import java.util.HashMap;

/** Manages ID creation. Each ID is created taking into account the annotations of the same type created so far, in a document context. This class keeps a counter for each type of annotation (terms, chunks...). */
class IdManager {

    /* Prefix of each type of ids */
    private static final String WF_PREFIX = "wid";
    private static final String TERM_PREFIX = "tid";
    private static final String CHUNK_PREFIX = "cid";
    private static final String ENTITY_PREFIX = "eid";
    private static final String COREF_PREFIX = "coid";

    /* Counters for each type of annotations */
    private int wfCounter;
    private int termCounter;
    private int chunkCounter;
    private int entityCounter;
    private int corefCounter;
    private HashMap<String, Integer> componentCounter;

    IdManager() {
	this.wfCounter = 0;
	this.termCounter = 0;
	this.chunkCounter = 0;
	this.entityCounter = 0;
	this.corefCounter = 0;
	this.componentCounter = new HashMap<String, Integer>();
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

    String getNextComponentId(String termId) {
	String newId;
	int nextIndex;
	if (!componentCounter.containsKey(termId)) {
	    nextIndex = 1;
	} else {
	    nextIndex = componentCounter.get(termId) + 1;
	}
	newId = termId + "." + Integer.toString(nextIndex);
	componentCounter.put(termId, nextIndex);
	return newId;
    }

    private int extractCounterFromId(String id) {
	Matcher matcher = Pattern.compile("^[a-z]*(\\d+)$").matcher(id);
	if (!matcher.find()) {
	    throw new IllegalStateException("IdManager doesn't recognise the given id's (" + id  + ") format. Should be [a-z]*[0-9]+");
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

    void updateComponentCounter(String id, String termId) {
	int componentInd;
	Matcher matcher = Pattern.compile("^tid\\d+\\.(\\d+)$").matcher(id);
	if (!matcher.find()) {
	    throw new IllegalStateException("IdManager doesn't recognise the given id's (" + id + ") format. Should be t[0-9]+\\.[0-9]+");
	}
	componentInd = Integer.valueOf(matcher.group(1));
	componentCounter.put(termId, componentInd);
    }
}
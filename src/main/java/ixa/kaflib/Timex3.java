package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/** The coreference layer creates clusters of term spans (which we call mentions) which share the same referent. For instance, “London” and “the capital city of England” are two mentions referring to the same entity. It is said that those mentions corefer. */
public class Timex3 {

    /** Timex3's ID (required) */
    private String timex3id;

    /** Timex3's type */
    private String type;

    /** Timex3's value */
    private String value;

    /** Timex3's functionInDocument */
    private String funcInDoc;

    /** Mentions to the same entity (at least one required) */
    private List<Span<WF>> mentions;

    Timex3(String timex3id){
	this.timex3id = timex3id;
	this.mentions = new ArrayList<Span<WF>>();
    }

    Timex3(String timex3id, List<Span<WF>> mentions) {
	/*
	if (mentions.size() < 1) {
	    throw new IllegalStateException("Timex3 must contain at least one reference span");
	}
	*/
	/*
	if (mentions.get(0).size() < 1) {
	   throw new IllegalStateException("Timex3' reference's spans must contain at least one target");
	}
	*/
	this.timex3id = timex3id;
	this.mentions = mentions;
    }

    Timex3(Timex3 timex3, HashMap<String, WF> WFs) {
	this.timex3id = timex3.timex3id;
	this.type = timex3.type;
	/* Copy references */
	String id = timex3.getId();
	this.mentions = new ArrayList<Span<WF>>();
	for (Span<WF> span : timex3.getSpans()) {
	    /* Copy span */
	    List<WF> targets = span.getTargets();
	    List<WF> copiedTargets = new ArrayList<WF>();
	    for (WF wf : targets) {
		WF copiedWF = WFs.get(wf.getId());
		if (copiedWF == null) {
		    throw new IllegalStateException("Term not found when copying " + id);
		}
		copiedTargets.add(copiedWF);
	    }
	    if (span.hasHead()) {
		WF copiedHead = WFs.get(span.getHead().getId());
		this.mentions.add(new Span<WF>(copiedTargets, copiedHead));
	    }
	    else {
		this.mentions.add(new Span<WF>(copiedTargets));
	    }
	}
    }

    public String getId() {
	return timex3id;
    }

    void setId(String id) {
	this.timex3id = id;
    }

    public String getType() {
	return type;
    }

    public void setType(String type){
	this.type = type;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value){
	this.value = value;
    }

    public String getFuncInDoc() {
	return funcInDoc;
    }

    public void setFuncInDoc(String funcInDoc){
	this.funcInDoc = funcInDoc;
    }

    /** Returns the term targets of the first span. When targets of other spans are needed getReferences() method should be used. */ 
    public List<WF> getWFs() {
	if (this.mentions.size()>0){
	    return this.mentions.get(0).getTargets();
	}
	else{
	    return null;
	}
    }

    /** Adds a term to the first span. */
    public void addWF(WF wf) {
	this.mentions.get(0).addTarget(wf);
    }

    /** Adds a term to the first span. */
    public void addWF(WF wf, boolean isHead) {
	this.mentions.get(0).addTarget(wf, isHead);
    }

    public List<Span<WF>> getSpans() {
	return this.mentions;
    }

    public void addSpan(Span<WF> span) {
	this.mentions.add(span);
    }

    public String getSpanStr(Span<WF> span) {
	String str = "";
	for (WF wf : span.getTargets()) {
	    if (!str.isEmpty()) {
		str += " ";
	    }
	    str += wf.getForm();
	}
	return str;
    }

    /** Deprecated */
    /*public List<List<Target>> getReferences() {
	List<List<Target>> list = new ArrayList<List<Target>>();
	for (Span<WF> span : this.mentions) {
	    list.add(KAFDocument.span2TargetList(span));
	}
	return list;
	}*/

    /** Deprecated */
    /*public void addReference(List<Target> span) {
	this.mentions.add(KAFDocument.targetList2Span(span));
	}*/
}

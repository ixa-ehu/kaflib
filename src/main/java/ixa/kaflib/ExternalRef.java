package ixa.kaflib;

import ixa.kaflib.KAFDocument.Utils;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;


public class ExternalRef implements Serializable {
    private String resource;
    private String reference;
    private Float confidence;
    private String reftype;
    private String status;
    private String source;
    private List<ExternalRef> externalRefs;
    private Term.Sentiment sentiment;


    ExternalRef(String resource, String reference) {
	this.resource = resource;
	this.reference = reference;
	this.confidence = -1.0f;
	this.externalRefs = new ArrayList<ExternalRef>();
    }

    ExternalRef(ExternalRef externalReference) {
	this.resource = externalReference.resource;
	this.reference = externalReference.reference;
	this.confidence = externalReference.confidence;
	if (externalReference.externalRefs != null) {
	    this.externalRefs = new ArrayList<ExternalRef>(externalReference.externalRefs);
	}
	this.sentiment = externalReference.sentiment;
    }

    public String getResource() {
	return resource;
    }

    public void setResource(String val) {
	resource = val;
    }
    
    public boolean hasReference() {
	return this.reference != null;
    }

    public String getReference() {
	return reference;
    }

    public void setReference(String val) {
	reference = val;
    }

    public boolean hasReftype() {
	return this.reftype != null;
    }

    public String getReftype() {
	return this.reftype;
    }

    public void setReftype(String reftype) {
	this.reftype = reftype;
    }

    public boolean hasStatus() {
	return this.status != null;
    }

    public String getStatus() {
	return this.status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public boolean hasSource() {
	return this.source != null;
    }

    public String getSource() {
	return this.source;
    }

    public void setSource(String source) {
	this.source = source;
    }

    public boolean hasConfidence() {
	return confidence != -1.0;
    }

    public Float getConfidence() {
	return confidence;
    }

    public void setConfidence(Float val) {
	confidence = val;
    }

    public boolean hasExternalRef() {
	return this.externalRefs.size() > 0;
    }

    public List<ExternalRef> getExternalRefs() {
	return externalRefs;
    }

    public void addExternalRef(ExternalRef externalRef) {
	this.externalRefs.add(externalRef);
    }

    // Deprecated. Add to the end of the list.

    public void setExternalRef(ExternalRef externalRef) {
	this.externalRefs.add(externalRef);
    }

    public boolean hasSentiment() {
	return this.sentiment != null;
    }

    public Term.Sentiment getSentiment() {
	return this.sentiment;
    }

    public void setSentiment(Term.Sentiment sentiment) {
	this.sentiment = sentiment;
    }
    
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof ExternalRef)) return false;
	ExternalRef ann = (ExternalRef) o;
	return Utils.areEquals(this.resource, ann.resource) &&
		Utils.areEquals(this.reference, ann.reference) &&
		Utils.areEquals(this.confidence, ann.confidence) &&
		Utils.areEquals(this.reftype, ann.reftype) &&
		Utils.areEquals(this.status, ann.status) &&
		Utils.areEquals(this.source, ann.source) &&
		Utils.areEquals(this.externalRefs, ann.externalRefs) &&
		Utils.areEquals(this.sentiment, ann.sentiment);
    }
    */

}

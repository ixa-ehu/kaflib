package ixa.kaflib;

import java.io.Serializable;


public class ExternalRef implements Serializable {
    private String resource;
    private String reference;
    private Float confidence;
    private String reftype;
    private String status;
    private String source;
    private ExternalRef externalRef;
    private Term.Sentiment sentiment;


    ExternalRef(String resource, String reference) {
	this.resource = resource;
	this.reference = reference;
	this.confidence = -1.0f;
    }

    ExternalRef(ExternalRef externalReference) {
	this.resource = externalReference.resource;
	this.reference = externalReference.reference;
	this.confidence = externalReference.confidence;
	if (externalReference.externalRef != null) {
	    this.externalRef = new ExternalRef(externalReference.externalRef);
	}
	this.sentiment = externalReference.sentiment;
    }

    public String getResource() {
	return resource;
    }

    public void setResource(String val) {
	resource = val;
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
	return this.externalRef != null;
    }

    public ExternalRef getExternalRef() {
	return externalRef;
    }

    public void setExternalRef(ExternalRef externalRef) {
	this.externalRef = externalRef;
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
}

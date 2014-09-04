package ixa.kaflib;

import java.io.Serializable;


public class ExternalRef implements Serializable {
    private String resource;
    private String reference;
    private Float confidence;
    private ExternalRef externalRef;

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
}

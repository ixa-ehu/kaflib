package ixa.kaflib;

import java.io.Serializable;


public class CLink implements Serializable {

    private String id;

    private Coref from;

    private Coref to;

    private String relType;

    
    CLink(String id, Coref from, Coref to) {
	this.id = id;
	this.from = from;
	this.to = to;
    }

    public String getId() {
	return this.id;
    }

    public Coref getFrom() {
	return this.from;
    }

    public void setFrom(Coref from) {
	this.from = from;
    }

    public Coref getTo() {
	return this.to;
    }

    public void setTo(Coref to) {
	this.to = to;
    }

    public boolean hasRelType() {
	return this.relType != null;
    }

    public String getRelType() {
	return this.relType;
    }

    public void setRelType(String relType) {
	this.relType = relType;
    }
}

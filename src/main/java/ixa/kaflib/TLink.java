package ixa.kaflib;


public class TLink {

    private String id;

    private TLinkReferable from;

    private TLinkReferable to;

    private String relType;

    
    TLink(String id, TLinkReferable from, TLinkReferable to, String relType) {
	this.id = id;
	this.from = from;
	this.to = to;
	this.relType = relType;
    }

    public String getId() {
	return this.id;
    }

    public TLinkReferable getFrom() {
	return this.from;
    }

    public void setFrom(TLinkReferable from) {
	this.from = from;
    }

    public TLinkReferable getTo() {
	return this.to;
    }

    public void setTo(TLinkReferable to) {
	this.to = to;
    }

    public String getFromType() {
	return (this.from instanceof Coref) ? "event" : "timex";
    }

    public String getToType() {
	return (this.to instanceof Coref) ? "event" : "timex";
    }

    public String getRelType() {
	return this.relType;
    }

    public void setRelType(String relType) {
	this.relType = relType;
    }
}


package ixa.kaflib;


public class TLink extends IdentifiableAnnotation {

    private TLinkReferable from;

    private TLinkReferable to;

    private String relType;

    
    TLink(String id, TLinkReferable from, TLinkReferable to, String relType) {
        super(id);
	this.from = from;
	this.to = to;
	this.relType = relType;
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
	return (this.from instanceof Predicate) ? "event" : "timex";
    }

    public String getToType() {
	return (this.to instanceof Predicate) ? "event" : "timex";
    }

    public String getRelType() {
	return this.relType;
    }

    public void setRelType(String relType) {
	this.relType = relType;
    }
}


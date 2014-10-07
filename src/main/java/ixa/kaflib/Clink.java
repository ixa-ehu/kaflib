package ixa.kaflib;

import java.util.List;
import java.util.HashMap;
import java.io.Serializable;


/** causalRelations represent clink relations among events. */
public class Clink implements Serializable {

    /** Clink's ID (required) */
    private String clinkid;

    /** Source id of the relation (required) */
    private String from;

    /** Target id of the relation (required) */
    private String to;

    /** Relation type (required) */
    private String relType;


    Clink(String clinkid, String from, String to, String relType) {
	this.from = from;
	this.to = to;
	this.relType = relType;
	this.clinkid = clinkid;
    }

    Clink(Clink clink) {
	this.from = clink.from;
	this.to = clink.to;
	this.relType = clink.relType;

	this.clinkid = clink.clinkid;
    }

    
    public String getId() {
	return clinkid;
    }

    void setId(String id) {
	this.clinkid = id;
    }

    public String getFrom() {
	return this.from;
    }

    public void setFrom(String term) {
	this.from = term;
    }

    public String getTo() {
	return to;
    }

    public void setTo(String term) {
	this.to = term;
    }

    public String getRelType() {
	return relType;
    }

     public void setRelType(String relType) {
	 this.relType = relType;
    }
    
    public String getStr() {
	return relType + "(" + this.getFrom() + ", " + this.getTo() + ")";
    }
}

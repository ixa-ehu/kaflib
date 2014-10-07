package ixa.kaflib;

import java.util.List;
import java.util.HashMap;
import java.io.Serializable;


/** temporalRelations represent tlink relations among terms. */
public class Tlink implements Serializable {

    /** Tlink's ID (required) */
    private String tlinkid;

    /** Source id of the relation (required) */
    private String from;

    /** Target id of the relation (required) */
    private String to;

    /** Type of the source of the relation (event or timex)*/
    private String fromType;

    /** Type of the target of the relation (event or timex)*/
    private String toType;

    /** Relation type (required) */
    private String relType;


    Tlink(String tlinkid, String from, String to, String relType, String fromType, String toType) {
	this.from = from;
	this.to = to;
	this.relType = relType;
	this.fromType = fromType;
	this.toType = toType;
	this.tlinkid = tlinkid;
    }

    Tlink(Tlink tlink) {
	this.from = tlink.from;
	this.to = tlink.to;
	this.relType = tlink.relType;

	this.fromType = tlink.fromType;
	this.toType = tlink.toType;
	this.tlinkid = tlink.tlinkid;
    }

    
    public String getId() {
	return tlinkid;
    }

    void setId(String id) {
	this.tlinkid = id;
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

    public String getFromType() {
	return this.fromType;
    }

    public void setFromType(String ftype) {
	this.fromType = ftype;
    }

    public String getToType() {
	return this.toType;
    }

    public void setToType(String ttype) {
	this.toType = ttype;
    }
    
    public String getStr() {
	return relType + "(" + this.getFrom() + ", " + this.getTo() + ")";
    }
}

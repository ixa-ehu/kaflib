package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Utils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;


/** Class for representing word forms. These are the result of the tokenization process. */
public class WF extends IdentifiableAnnotation implements SentenceLevelAnnotation {

    private AnnotationContainer annotationContainer;

    /** Sentence id (required) */
    private int sent;
    
    /** The offset (in characters) of the original word form (required) */
    private int offset;

    /** The length (in characters) of the word form (required) */
    private int length;
    
    /** Paragraph id (optional) */
    private int para;

    /** Page id (optional) */
    private int page;

    /** In case of source xml files, the xpath expression identifying the original word form (optional) */
    private String xpath;

    /** The word form text (required) */
    private String form;
    
    private static final String ID_PREFIX = "w";
    

    WF(AnnotationContainer annotationContainer, String id, int offset, int length, String form, int sent) {
	super(id);
	this.annotationContainer = annotationContainer;
	this.offset = offset;
	this.length = length;
	this.form = form;
        this.setSent(sent);
	this.para = -1;
	this.page = -1;
    }

    WF(WF wf, AnnotationContainer annotationContainer) {
	super(wf.getId());
	this.annotationContainer = annotationContainer;
	this.sent = wf.sent;
	this.para = wf.para;
	this.page = wf.page;
	this.offset = wf.offset;
	this.length = wf.length;
	this.xpath = wf.xpath;
	this.form = wf.form;
    }
    
    String getIdPrefix() {
	return ID_PREFIX;
    }
    
    public int getOffset() {
	return offset;
    }

    public void setOffset(int offset) {
	this.offset = offset;
    }

    public int getLength() {
	return length;
    }

    public void setLength(int length) {
	this.length = length;
    }

    public Integer getSent() {
	return sent;
    }

    public void setSent(int sent) {
	Integer oldSent = this.sent;
	Integer oldPara = this.para;
	this.sent = sent;
	if (oldSent > 0) {
	    annotationContainer.reindexAnnotationParaSent(this, KAFDocument.AnnotationType.WF, oldSent, oldPara);
	}
	/*
	annotationContainer.indexWFBySent(this, sent);
	// If there's a term associated with this WF, index it as well
	Term term = annotationContainer.getTermByWF(this);
	if (term != null) {
	    annotationContainer.indexTermBySent(term, sent);
	}
	*/
    }

    public boolean hasPara() {
	return para != -1;
    }

    public Integer getPara() {
	return para;
    }

    public void setPara(int para) {
	Integer oldSent = this.sent;
	Integer oldPara = this.para;
	this.para = para;
	annotationContainer.reindexAnnotationParaSent(this, KAFDocument.AnnotationType.WF, oldSent, oldPara);
	//this.annotationContainer.indexSentByPara(this.sent, para);
    }

    public boolean hasPage() {
	return page != -1;
    }

    public int getPage() {
	return page;
    }

    public void setPage(int page) {
	this.page = page;
    }

    public boolean hasOffset() {
	return offset != -1;
    }

    public boolean hasXpath() {
	return xpath != null;
    }

    public String getXpath() {
	return xpath;
    }

    public void setXpath(String xpath) {
	this.xpath = xpath;
    }

    public String getForm() {
	return form;
    }

    public void setForm(String form) {
	this.form = form;
    }
    
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	return new HashMap<AnnotationType, List<Annotation>>();
    }

    @Override
    public String toString() {
	return this.getForm();
    }
    
    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof WF)) return false;
	WF ann = (WF) o;
	return Utils.areEquals(this.sent, ann.sent) &&
		Utils.areEquals(this.para, ann.para) &&
		Utils.areEquals(this.page, ann.page) &&
		Utils.areEquals(this.offset, ann.offset) &&
		Utils.areEquals(this.length, ann.length) &&
		Utils.areEquals(this.xpath, ann.xpath) &&
		Utils.areEquals(this.form, ann.form);
    }
    */

}
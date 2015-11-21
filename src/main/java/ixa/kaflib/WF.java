package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.List;
import java.util.Map;
import java.util.HashMap;


/** Class for representing word forms. These are the result of the tokenization process. */
public class WF extends IdentifiableAnnotation implements SentenceLevelAnnotation {

    /** Sentence id (required) */
    private Integer sent;
    
    /** The offset (in characters) of the original word form (required) */
    private Integer offset;

    /** The length (in characters) of the word form (required) */
    private Integer length;
    
    /** Paragraph id (optional) */
    private Integer para;

    /** Page id (optional) */
    private Integer page;

    /** In case of source xml files, the xpath expression identifying the original word form (optional) */
    private String xpath;

    /** The word form text (required) */
    private String form;
    
    private static final long serialVersionUID = 1L;


    WF(AnnotationContainer annotationContainer, String id, Integer offset, Integer length, String form, Integer sent) {
	super(annotationContainer, id);
	this.annotationContainer = annotationContainer;
	this.offset = offset;
	this.length = length;
	this.form = form;
	this.sent = sent;
    }
    
    @Deprecated
    public boolean hasOffset() {
	return offset != null;
    }
    
    @Override
    public Integer getOffset() {
	return offset;
    }

    public void setOffset(Integer offset) {
	this.offset = offset;
    }

    public Integer getLength() {
	return length;
    }

    public void setLength(Integer length) {
	this.length = length;
    }

    public Integer getSent() {
	return sent;
    }

    public void setSent(Integer sent) {
	Integer oldSent = this.sent;
	this.sent = sent;
	this.annotationContainer.updateAnnotationSent(this, AnnotationType.WF, oldSent, sent);
    }

    public boolean hasPara() {
	return para != null;
    }

    public Integer getPara() {
	return para;
    }

    public void setPara(Integer para) {
	Integer oldPara = this.para;
	this.para = para;
	this.annotationContainer.updateAnnotationPara(this, AnnotationType.WF, oldPara, para);
    }

    public boolean hasPage() {
	return page != null;
    }

    public Integer getPage() {
	return page;
    }

    public void setPage(Integer page) {
	this.page = page;
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
    
    @Override
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
	return Utils.areEquals(this.id, ann.id) &&
		Utils.areEquals(this.sent, ann.sent) &&
		Utils.areEquals(this.para, ann.para) &&
		Utils.areEquals(this.page, ann.page) &&
		Utils.areEquals(this.offset, ann.offset) &&
		Utils.areEquals(this.length, ann.length) &&
		Utils.areEquals(this.xpath, ann.xpath) &&
		Utils.areEquals(this.form, ann.form);
    }
    */

}
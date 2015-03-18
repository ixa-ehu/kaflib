package ixa.kaflib;


/** Class for representing word forms. These are the result of the tokenization process. */
public class WF extends IdentifiableAnnotation {

    private AnnotationContainer annotationContainer;

    /** Sentence id (required) */
    private int sent;

    /** Paragraph id (optional) */
    private int para;

    /** Page id (optional) */
    private int page;

    /** The offset (in characters) of the original word form (optional) */
    private int offset;

    /** The length (in characters) of the word form (optional) */
    private int length;

    /** In case of source xml files, the xpath expression identifying the original word form (optional) */
    private String xpath;

    /** The word form text (required) */
    private String form;

    WF(AnnotationContainer annotationContainer, String id, String form, int sent) {
	super(id);
	this.annotationContainer = annotationContainer;
	this.form = form;
        this.setSent(sent);
	this.para = -1;
	this.page = -1;
	this.offset = -1;
	this.length = -1;
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

    public int getSent() {
	return sent;
    }

    public void setSent(int sent) {
	this.sent = sent;
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

    public int getPara() {
	return para;
    }

    public void setPara(int para) {
	this.para = para;
	this.annotationContainer.indexSentByPara(this.sent, para);
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

    public int getOffset() {
	return offset;
    }

    public void setOffset(int offset) {
	this.offset = offset;
    }

    public boolean hasLength() {
	return length != -1;
    }

    public int getLength() {
	return length;
    }

    public void setLength(int length) {
	this.length = length;
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
    public String toString() {
	return this.getForm();
    }

}

package ixa.kaflib;

/** Class for representing word forms. These are the result of the tokenization process. */
public class WF {

    private AnnotationContainer annotationContainer;

    /** ID of the word form (required) */
    private String wid;

    /** Sentence id (optional) */
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

    WF(AnnotationContainer annotationContainer, String wid, String form) {
	this.annotationContainer = annotationContainer;
	this.wid = wid;
	this.form = form;
	this.sent = -1;
	this.para = -1;
	this.page = -1;
	this.offset = -1;
	this.length = -1;
    }

    public String getId() {
	return wid;
    }

    public boolean hasSent() {
	return sent != -1;
    }

    public int getSent() {
	return sent;
    }

    public void setSent(int sent) {
	this.sent = sent;
	annotationContainer.indexWFBySent(this);
	// If there's a term associated with this WF, index it as well
	Term term = annotationContainer.getTermByWFId(this.wid);
	if (term != null) {
	    annotationContainer.indexTermBySent(term);
	}
    }

    public boolean hasPara() {
	return para != -1;
    }

    public int getPara() {
	return para;
    }

    public void setPara(int para) {
	this.para = para;
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
}

package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Terminal extends TreeNode {

    /** The term referenced by this terminal */
    private Span<Term> span;
    
    private static final long serialVersionUID = 1L;

    
    Terminal(AnnotationContainer annotationContainer, String id, Span<Term> span) {
	super(annotationContainer, id, false);
	this.setSpan(span);
    }

    /** Returns the Span object */
    public Span<Term> getSpan() {
	return this.span;
    }
    
    public void setSpan(Span<Term> span) {
	span.setOwner(this, AnnotationType.TERMINAL, this.annotationContainer);
	this.span = span;
    }

    public void addChild(TreeNode tn) throws Exception {
	throw new Exception("It is not possible to add child nodes to Terminal nodes.");
    }
    
    public void addChild(TreeNode tn, Boolean isHead) throws Exception {
	this.addChild(tn);
	tn.setHead(isHead);
    }

    public List<TreeNode> getChildren() {
	return null;
    }
    
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.TERM, (List<Annotation>)(List<?>) this.getSpan().getTargets());
	return referenced;
    }
    
    @Override
    public Integer getOffset() {
	return this.getSpan().getOffset();
    }
    
    @Override
    public Integer getSent() {
	return this.getSpan().getFirstTarget().getSent();
    }
    
    @Override
    public Integer getPara() {
	return this.getSpan().getFirstTarget().getPara();
    }
    
    @Override
    public String toString() {
	return this.span.toString();
    }


    @Deprecated
    private String getStrValue() {
	String str = "";
	for (Term term : span.getTargets()) {
	    if (!str.isEmpty()) {
		str += " ";
	    }
	    str += term.getStr();
	}
	return str;
    }

    @Deprecated
    public String getStr() {
	String strValue = this.getStrValue();
	if (strValue.startsWith("-") || strValue.endsWith("-")) {
	    return strValue.replace("-", "- ");
   	}
   	else if (strValue.contains("--")) { 
	    return strValue.replace("--", "-");
   	}
   	else {
	    return strValue;
   	}
    }

    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof Terminal)) return false;
	Terminal ann = (Terminal) o;
	return Utils.areEquals(this.span, ann.span);
    }
    */
}

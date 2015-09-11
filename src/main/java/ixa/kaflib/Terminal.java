package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Terminal extends TreeNode implements SentenceLevelAnnotation {

    /** The term referenced by this terminal */
    private Span<Term> span;

    Terminal(String id, Span<Term> span) {
	super(id, false, true);
	this.span = span;
    }

    /** Returns the Span object */
    public Span<Term> getSpan() {
	return this.span;
    }

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

    public void addChild(TreeNode tn) throws Exception {
	throw new Exception("It is not possible to add child nodes to Terminal nodes.");
    }

    public List<TreeNode> getChildren() {
	return null;
    }
    
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.TERM, (List<Annotation>)(List<?>) this.getSpan().getTargets());
	return referenced;
    }
    
    public Integer getSent() {
	return this.getSpan().getFirstTarget().getSent();
    }
    
    public Integer getPara() {
	return this.getSpan().getFirstTarget().getPara();
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

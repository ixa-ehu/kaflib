package ixa.kaflib;

import ixa.kaflib.KAFDocument.Layer;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;


public class Factuality extends IdentifiableAnnotation implements SentenceLevelAnnotation {
    
    private Span<Term> span;
    private List<FactVal> factVals;
    
    Factuality(String id, Span<Term> span) {
	super(id);
	this.span = span;
	this.factVals = new ArrayList<FactVal>();
    }
    
    public String getId() {
	return this.id;
    }
    
    public Span<Term> getSpan() {
	return this.span;
    }
    
    public List<FactVal> getFactVals() {
	return this.factVals;
    }
    
    public void addFactVal(FactVal factVal) {
	this.factVals.add(factVal);
    }
    
    @Override
    public Integer getPara() {
	return this.span.getFirstTarget().getPara();
    }
    
    @Override
    public Integer getSent() {
	return this.span.getFirstTarget().getSent();
    }
    
    @Override
    Map<Layer, List<Annotation>> getReferencedAnnotations() {
	Map<Layer, List<Annotation>> references = new HashMap<Layer, List<Annotation>>();
	references.put(Layer.TERMS, (List<Annotation>)(List<?>)this.span.getTargets());
	return references;
    }
    
    public String getSpanStr(Span<Term> span) {
   	String str = "";
   	for (Term term : span.getTargets()) {
   	    if (!str.isEmpty()) {
   		str += " ";
   	    }
   	    str += term.getStr();
   	}
   	return str;
       }
    
    
    public static class FactVal extends Annotation {
	
	private String value;
	private String resource;
	private String source;
	private Float confidence;
	
	FactVal(String value, String resource) {
	    this.value = value;
	    this.resource = resource;
	}
	
	public String getValue() {
	    return this.value;
	}
	
	public void setValue(String value) {
	    this.value = value;
	}
	
	public String getResource() {
	    return this.resource;
	}
	
	public void setResource(String resource) {
	    this.resource = resource;
	}
	
	public boolean hasSource() {
	    return this.source != null;
	}
	
	public String getSource() {
	    return this.source;
	}
	
	public void setSource(String source) {
	    this.source = source;
	}
	
	public boolean hasConfidence() {
	    return this.confidence != null;
	}
	
	public Float getConfidence() {
	    return this.confidence;
	}
	
	public void setConfidence(Float confidence) {
	    this.confidence = confidence;
	}
	
	@Override
	Map<Layer, List<Annotation>> getReferencedAnnotations() {
	    return new HashMap<Layer, List<Annotation>>();
	}
    }
}

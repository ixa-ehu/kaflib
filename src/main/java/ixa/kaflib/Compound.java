package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Compound extends TermBase {

    protected List<Term> components;
    protected Term head;
    protected Integer nextIdIndex;
    
    private static final long serialVersionUID = 1L;
    
    
    Compound(AnnotationContainer annotationContainer, String id) {
	super(annotationContainer, id, new Span<WF>());
	this.annotationType = AnnotationType.MW;
	this.components = new ArrayList<Term>();
	this.nextIdIndex = 1;
    }
    

    public List<Term> getComponents() {
	return this.components;
    }

    public Term newComponent(String id, Span<WF> span, Boolean isHead) {
	this.updateComponentIdIndex(id);
	Term component = new Term(this.annotationContainer, id, span);
	this.addToComponents(component, isHead);
	return component;
    }
    
    public Term newComponent(Span<WF> span, Boolean isHead) {
	String newId = this.getNextComponentId();
	Term component = new Term(this.annotationContainer, newId, span);
	this.addToComponents(component, isHead);
	return component;
    }

    public void addComponent(Term term) {
	term.id = this.getNextComponentId();
	if (term.annotationType == AnnotationType.TERM) {
	    this.annotationContainer.remove(term, AnnotationType.TERM);
	}
	this.addToComponents(term, false);
    }

    public void addComponent(Term term, boolean isHead) {
	term.id = this.getNextComponentId();
	if (term.annotationType == AnnotationType.TERM) {
	    this.annotationContainer.remove(term, AnnotationType.TERM);
	}
	this.addToComponents(term, isHead);
    }

    public boolean hasHead() {
	return (this.head != null);
    }

    public Term getHead() {
        return this.head;
    }
    
    @Override
    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.WF, (List<Annotation>)(List<?>) this.getSpan().getTargets());
	referenced.put(AnnotationType.COMPONENT, (List<Annotation>)(List<?>)this.getComponents());
	List<Annotation> sentiments = new ArrayList<Annotation>();
	if (this.hasSentiment()) sentiments.add(this.getSentiment());
	referenced.put(AnnotationType.SENTIMENT, sentiments);
	return referenced;
    }

    
    private void addToComponents(Term component, Boolean isHead) {
	this.getSpan().addTargets(component.getSpan().getTargets());
	component.setCompound(this);
	component.annotationType = AnnotationType.COMPONENT;
	components.add(component);
	this.annotationContainer.indexAnnotationReferences(AnnotationType.MW, this, component);
	if (isHead) this.head = component;
    }

    private String getNextComponentId() {
	return this.id + "." + this.nextIdIndex++;
    }

    private void updateComponentIdIndex(String componentId) {
	String pattern = "^" + this.id + "\\.(\\d+)$";
	Matcher matcher = Pattern.compile(pattern).matcher(componentId);
	if (matcher.find()) {
	    this.nextIdIndex = Integer.valueOf(matcher.group(1)) + 1;
	}
    }
    
}

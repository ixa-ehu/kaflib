package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Predicate extends IdentifiableAnnotation implements SentenceLevelAnnotation, TLinkReferable {

    public static class Role extends IdentifiableAnnotation {

	private String semRole;
	private Span<Term> span;
	private List<ExternalRef> externalReferences;
	
	private static final long serialVersionUID = 1L;

	Role(AnnotationContainer annotationContainer, String id, String semRole, Span<Term> span) {
	    super(annotationContainer, id);
	    this.semRole = semRole;
	    this.span = span;
	    this.externalReferences = new ArrayList<ExternalRef>();
	}

	public String getSemRole() {
	    return this.semRole;
	}

	public void setSemRole(String semRole) {
	    this.semRole = semRole;
	}

	public Span<Term> getSpan() {
	    return this.span;
	}

	public void setSpan(Span<Term> span) {
	    this.span = span;
	}

	public List<Term> getTerms() {
	    return this.span.getTargets();
	}

	public void addTerm(Term term) {
	    this.span.addTarget(term);
	}

	public void addTerm(Term term, boolean isHead) {
	    this.span.addTarget(term, isHead);
	}

	Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	    Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	    List<Annotation> terms = new ArrayList<Annotation>();
	    terms.addAll((List<Annotation>) (List<?>) this.span.getTargets());
	    referenced.put(AnnotationType.TERM, terms);
	    return referenced;
	}

	@Override
	public Integer getOffset() {
	    return this.getSpan().getOffset();
	}

	public List<ExternalRef> getExternalRefs() {
	    return externalReferences;
	}

	public void addExternalRef(ExternalRef externalRef) {
	    externalReferences.add(externalRef);
	}

	public void addExternalRefs(List<ExternalRef> externalRefs) {
	    externalReferences.addAll(externalRefs);
	}
	
	@Override
	public String toString() {
	    return this.span.toString();
	}

	@Deprecated
	public String getStr() {
	    String str = "";
	    for (Term term : this.span.getTargets()) {
		if (!str.isEmpty()) {
		    str += " ";
		}
		str += term.getStr();
	    }
	    return str;
	}

	/*
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof Role)) return false;
	    Role ann = (Role) o;
	    return Utils.areEquals(this.semRole, ann.semRole) &&
		    Utils.areEquals(this.span, ann.span)  &&
		    Utils.areEquals(this.externalReferences, ann.externalReferences);
	}
	*/
    }

    private String uri;
    private float confidence;
    private Span<Term> span;
    private List<Role> roles;
    private List<ExternalRef> externalReferences;
	
    private static final long serialVersionUID = 1L;

    Predicate(AnnotationContainer annotationContainer, String id, Span<Term> span) {
	super(annotationContainer, id);
	this.span = span;
	this.roles = new ArrayList<Role>();
	this.confidence = -1.0f;
	this.externalReferences = new ArrayList<ExternalRef>();
    }

    public boolean hasUri() {
	return (this.uri != null);
    }

    public String getUri() {
	return this.uri;
    }

    public void setUri(String uri) {
	this.uri = uri;
    }

    public boolean hasConfidence() {
	return confidence != -1.0f;
    }

    public float getConfidence() {
	return confidence;
    }

    public void setConfidence(float confidence) {
	this.confidence = confidence;
    }

    public Span<Term> getSpan() {
	return this.span;
    }

    public void setSpan(Span<Term> span) {
	this.span = span;
    }

    public List<Term> getTerms() {
	return this.span.getTargets();
    }

    public void addTerm(Term term) {
	this.span.addTarget(term);
    }

    public void addTerm(Term term, boolean isHead) {
	this.span.addTarget(term, isHead);
    }
    
    public Integer getSent() {
	return this.span.getFirstTarget().getSent();
    }  
    
    public Integer getPara() {
	return this.span.getFirstTarget().getPara();
    }

    Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	List<Annotation> terms = new ArrayList<Annotation>();
	terms.addAll((List<Annotation>) (List<?>) this.span.getTargets());
	referenced.put(AnnotationType.TERM, terms);
	return referenced;
    }

    public List<ExternalRef> getExternalRefs() {
	return externalReferences;
    }

    public void addExternalRef(ExternalRef externalRef) {
	externalReferences.add(externalRef);
    }

    public void addExternalRefs(List<ExternalRef> externalRefs) {
	externalReferences.addAll(externalRefs);
    }

    public List<Role> getRoles() {
	return this.roles;
    }

    public void addRole(Role role) {
	this.roles.add(role);
    }
    
    @Override
    public Integer getOffset() {
	return this.getSpan().getOffset();
    }

    @Override
    public String toString() {
	String str = new String();
	if (!this.span.isEmpty()) {
	    str += this.span.getFirstTarget().getId() + " " + this.span.getFirstTarget().toString();
	}
	str += ":";
	for (Role role: this.roles) {
	    if (!role.getSpan().isEmpty()) {
		str += " " + role.getSemRole() + "[" + role.getSpan().getFirstTarget().getId() + " " + role.getSpan().getFirstTarget() + "]";
	    }
	}
	
	return str;
    }
    
    
    @Deprecated
    public String getStr() {
	String str = "";
	if (!this.span.isEmpty()) {
	    Term target = this.span.getFirstTarget();
	    str += target.getId() + " " + target.getStr() + " ";
	}
	str += ":";
	for (Role role : this.roles) {
	    if (!role.span.isEmpty()) {
		Term roleTarget = role.getSpan().getFirstTarget();
		str += " " + role.getSemRole() + "[" + roleTarget.getId() + " "
			+ roleTarget.getStr() + "]";
	    }
	}
	return str;
    }

    @Deprecated
    public String getSpanStr() {
	String str = "";
	for (Term term : this.span.getTargets()) {
	    if (!str.isEmpty()) {
		str += " ";
	    }
	    str += term.getStr();
	}
	return str;
    }

    /*
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof Predicate)) return false;
	Predicate ann = (Predicate) o;
	return Utils.areEquals(this.uri, ann.uri) &&
		Utils.areEquals(this.confidence, ann.confidence) &&
		Utils.areEquals(this.span, ann.span) &&
		Utils.areEquals(this.roles, ann.roles) &&
		Utils.areEquals(this.externalReferences, ann.externalReferences);
    }
    */
}

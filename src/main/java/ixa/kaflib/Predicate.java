package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;

public class Predicate {

    public static class Role {
	private String rid;
	private String semRole;
	private List<String> roleTypes;
	private Span<Term> span;

	Role(String id, String semRole, Span span, List<String> roleTypes) {
	    this.rid = id;
	    this.semRole = semRole;
	    this.span = span;
	    if (roleTypes.size() < 1) {
		throw new IllegalStateException("A Role must have at least one roleType");
	    }
	    this.roleTypes = roleTypes;
	}

	public String getId() {
	    return this.rid;
	}

	public void setId(String id) {
	    this.rid = id;
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


	public List<String> getRoleTypes() {
	    return roleTypes;
	}

	public void addRoleType(String roleType) {
	    roleTypes.add(roleType);
	}

    }

    private String id;
    private String uri;
    private float confidence;
    private List<String> predTypes;
    private Span<Term> span;
    private List<Role> roles;

    Predicate(String id, Span<Term> span, List<String> predTypes) {
	this.id = id;
	this.span = span;
	this.roles = new ArrayList<Role>();
	if (predTypes.size() < 1) {
	    throw new IllegalStateException("A Predicate must have at least one predType");
	}
	this.predTypes = predTypes;
	this.confidence = -1.0f;
    }

    public String getId() {
	return this.id;
    }

    public void setId(String id) {
	this.id = id;
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
		str += " " + role.getSemRole() + "[" + roleTarget.getId() + " " + roleTarget.getStr() + "]";
	    }
	}
	return str;
    }

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

    public List<String> getPredTypes() {
	return predTypes;
    }

    public void addPredType(String predType) {
	predTypes.add(predType);
    }

    public List<Role> getRoles() {
	return this.roles;
    }

    public void addRole(Role role) {
	this.roles.add(role);
    }
}

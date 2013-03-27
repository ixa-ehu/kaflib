package ixa.kaflib;

public class Target {
    private AnnotationContainer annotationContainer;
    private String id;
    private boolean head;

    Target(AnnotationContainer annotationContainer, String id, boolean head) {
	this.annotationContainer = annotationContainer;
	this.id = id;
	this.head = head;
    }

    public Term getTerm() {
	return annotationContainer.getTermById(id);
    }

    public boolean isHead() {
	return head;
    }

    public void setTerm(String id) {
	this.id = id;
	this.head = false;
    }

    public void setTerm(Term term) {
	this.id = term.getId();
	this.head = false;
    }

    public void setTerm(String id, boolean head) {
	this.id = id;
	this.head = head;
    }

    public void setTerm(Term term, boolean head) {
	this.id = term.getId();
	this.head = head;
    }

    public void setHead(boolean head) {
	this.head = head;
    }
}
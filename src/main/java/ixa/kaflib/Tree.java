package ixa.kaflib;

//import java.util.List;
//import java.util.ArrayList;

/**  */
public class Tree { //?

    /** Reference to the main annotationContainer of the document to which this tree is related (required) */
    private AnnotationContainer annotationContainer;

    /** Tree's ID (required) */
    private String treeid; //?

    /** Tree's root node */
    private TreeNode root;


    Tree(AnnotationContainer annotationContainer, String treeid) { //?
	this.annotationContainer = annotationContainer;
	this.treeid = treeid;
    }

    public String getId() {
	return treeid;
    }

    public TreeNode getRoot() {
	return root;
    }

    public NonTerminal createNRoot(String label) {
	this.root = new NonTerminal(annotationContainer, label);
	return (NonTerminal)root;
    }

    public Terminal createTRoot(Term term) {
	Span<Term> span = new Span<Term>(this.annotationContainer);
	span.addTarget(term, true);
	this.root = new Terminal(annotationContainer, span);
	return (Terminal)root;
    }

}

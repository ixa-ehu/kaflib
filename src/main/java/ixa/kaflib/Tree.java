package ixa.kaflib;

//import java.util.List;
//import java.util.ArrayList;

/**  */
public class Tree { //?

    /** Tree's ID (required) */
    private String treeid; //?

    /** Tree's root node */
    private TreeNode root;


    Tree(String treeid) { //?
	this.treeid = treeid;
    }

    public String getId() {
	return treeid;
    }

    public TreeNode getRoot() {
	return root;
    }

    public NonTerminal newNRoot(String label) {
	this.root = new NonTerminal(label);
	return (NonTerminal)root;
    }

    public Terminal newTRoot(Term term) {
	Span<Term> span = new Span<Term>();
	span.addTarget(term, true);
	this.root = new Terminal(span);
	return (Terminal)root;
    }

}

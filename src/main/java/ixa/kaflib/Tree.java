package ixa.kaflib;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**  */
public class Tree { //?

    /** Tree's root node */
    private TreeNode root;

    Tree(TreeNode root) {
	this.root = root;
    }

    public TreeNode getRoot() {
	return this.root;
    }

    public void setRoot(TreeNode root) {
	this.root = root;
    }



    /***********************************************************/
    /* Code for converting OpenNLP's parentheses output to NAF */
    /***********************************************************/

    static void parenthesesToKaf(String parOut, KAFDocument kaf) throws Exception {
	/*
	List<Term> terms = kaf.getTerms();
	Span<Term> span = kaf.newTermSpan();
	span.addTarget(terms.get(1));
	Terminal t = kaf.newTerminal(span);
	Tree tree = kaf.newConstituent(t);
	*/
	String[] tokens = Tree.tokenize(parOut);
	Tree.check(tokens);
        HashMap<Integer, Integer> parMatching = Tree.matchParentheses(tokens);
	HashMap<Integer, Term> termMatching = Tree.matchTerms(tokens, kaf.getTerms());
	List<Tree> trees = new ArrayList<Tree>();
	int current = 0;
	while (current < tokens.length) {
	    int end = parMatching.get(current);
	    NonTerminal root = Tree.createNonTerminal(tokens, current+1, end-1, parMatching, termMatching, kaf);
	    kaf.newConstituent(root);
	    current = end + 1;
	}
    }

    private static String[] tokenize(String parOut) {
	List<String> tokens = new ArrayList<String>();
	int current = 0;
	int length = parOut.length();
	String token = new String("");
	while (current < length) {
	    char nextChar = parOut.charAt(current++);
	    if (nextChar == '(') {
		if (!token.isEmpty()) {
		    tokens.add(token);
		}
		tokens.add(new String("("));
		token = new String("");
	    }
	    else if (nextChar == ')') {
		if (!token.isEmpty()) {
		    tokens.add(token);
		}
		tokens.add(new String(")"));
		token = new String("");
	    }
	    else if ((nextChar == ' ') || (nextChar == '\n')) {
		if (!token.isEmpty()) {
		    tokens.add(token);
		    token = new String();
		}
	    }
	    else {
		token += nextChar;
	    }
	}
	return tokens.toArray(new String[tokens.size()]);
    }

    private static HashMap<Integer, Integer>  matchParentheses(String[] tokens) {
	HashMap<Integer, Integer> indexes = new HashMap<Integer, Integer>();
	Stack<Integer> stack = new Stack<Integer>();
	int ind = 0;
	for (String token : tokens) {
	    if (token.equals("(")) {
		stack.push(ind);
	    }
	    else if (token.equals(")")) {
		indexes.put(stack.pop(), ind);
	    }
	    ind++;
	}
	return indexes;
    }

    private static HashMap<Integer, Term> matchTerms(String[] tokens, List<Term> terms) throws Exception {
	HashMap<Integer, Term> mapping = new HashMap<Integer, Term>();
	int nextTerm = 0;
	for (int i=1; i<tokens.length; i++) {
	    if ((!tokens[i].equals("(")) && (!tokens[i].equals(")"))) {
		if ((!tokens[i-1].equals("(")) && (!tokens[i-1].equals(")"))) {
		    if (!terms.get(nextTerm).getStr().equals(tokens[i])) {
			System.out.println(terms.get(nextTerm).getStr() + " / " + tokens[i]);
			throw new Exception("Can't perform parentheses=>NAF at constituency");
		    }
		    mapping.put(i, terms.get(nextTerm));
		    nextTerm++;
		}
	    }
	}
	return mapping;
    }

    private static NonTerminal createNonTerminal(String[] tokens, int start, int end, HashMap<Integer, Integer> parenthesesMap, HashMap<Integer, Term> termMap, KAFDocument kaf) {
	NonTerminal nt = kaf.newNonTerminal(tokens[start]);
	if (end - start == 1) {
	    Terminal t = Tree.createTerminal(tokens[end], termMap.get(end), kaf);
	    nt.addChild(t);
	}
	else {
	    int current = start + 1;
	    while (current <= end) {
		int subParEnd = parenthesesMap.get(current);
		NonTerminal nnt = Tree.createNonTerminal(tokens, current+1, subParEnd-1, parenthesesMap, termMap, kaf);
		nt.addChild(nnt);
		current = subParEnd + 1;
	    }
	}
	return nt;
    }

    private static Terminal createTerminal(String token, Term term, KAFDocument kaf) {
	Span<Term> span = kaf.newTermSpan();
	span.addTarget(term);
	return kaf.newTerminal(span);
    }

    private static void check(String[] tokens) throws Exception {
	int opened = 0;
	for (int i=0; i<tokens.length; i++) {
	    if (tokens[i].equals("(")) {
		if ((i>0) && (tokens[i-1].equals("("))) {
		    throw Tree.getException(tokens, i);
		}
		else if (i == tokens.length-1) {
		    throw Tree.getException(tokens, i);
		}
		opened++;
	    }
	    else if (tokens[i].equals(")")) {
		if ((i<3) || tokens[i-1].equals("(")) {
		    throw Tree.getException(tokens, i);
		}
		opened--;
	    }
	    else { // string token
		if ((i==0) || (i == tokens.length-1)) {
		    throw Tree.getException(tokens, i);
		}
		else if (isAWord(tokens[i-1]) && isAWord(tokens[i+1])) {
		    throw Tree.getException(tokens, i);
		}
		else if (tokens[i-1].equals(")")) {
		    throw Tree.getException(tokens, i);
		}
		else if (tokens[i-1].equals("(") && tokens[i+1].equals(")")) {
		    throw Tree.getException(tokens, i);
		}
	    }
	}
	if (opened != 0) {
	    throw Tree.getException(tokens, tokens.length-1);
	}
    }

    private static boolean isAWord(String token) {
	return (!token.equals("(")) && (!token.equals(")"));
    }

    private static Exception getException(String[] tokens, int ind) {
	String str = new String("Parentheses format not valid: \"... ");
	for (int i=(ind<5 ? 0 : ind-5); i<(ind>tokens.length-6 ? tokens.length-1 : ind+5); i++) {
	    if (i == ind) {
		str += "->";
	    }
	    str += tokens[i];
	    if (i == ind) {
		str += "<-";
	    }
	    str += " ";
	}
	return new Exception(str + " ...\"");
    }
}

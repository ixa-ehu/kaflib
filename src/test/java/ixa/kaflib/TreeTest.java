package ixa.kaflib;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ixa.kaflib.KAFDocument.AnnotationType;


public class TreeTest {
    
    @Test
    public void testTerminal() {
	AnnotationContainer container = new AnnotationContainer();	
	WF w1 = new WF(container, "w1", 3, 1, "a", 2);
	WF w2 = new WF(container, "w2", 5, 1, "b", 2);
	Term t1 = new Term(container, "t1", new Span<WF>(w1));
	Term t2 = new Term(container, "t2", new Span<WF>(w2));
	Terminal terminal = new Terminal(container, "ter1", new Span<Term>(t1, t2));
	
	assertEquals("ter1", terminal.getId());
	assertTrue(terminal.isTerminal());
	assertFalse(terminal.isNonTerminal());
	assertEquals(new Span<Term>(t1, t2), terminal.getSpan());
	
	assertFalse(terminal.hasEdgeId());
	assertFalse(terminal.isHead());
	
	assertEquals("a b", terminal.toString());
	assertEquals(new Integer(3), terminal.getOffset());
	assertEquals(new Integer(2), terminal.getSent());
	assertNull(terminal.getPara());
	
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<KAFDocument.AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.WF, Arrays.asList((Annotation)w1, w2));
	referenced.put(AnnotationType.TERM, Arrays.asList((Annotation)t1, t2));
	assertEquals(referenced, terminal.getReferencedAnnotationsDeep());
    }
    
    @Test
    public void testNonTerminal() {
	AnnotationContainer container = new AnnotationContainer();	
	WF w1 = new WF(container, "w1", 3, 1, "a", 2);
	WF w2 = new WF(container, "w2", 5, 1, "b", 2);
	Term t1 = new Term(container, "t1", new Span<WF>(w1));
	Term t2 = new Term(container, "t2", new Span<WF>(w2));
	Terminal ter1= new Terminal(container, "ter1", new Span<Term>(t1));
	Terminal ter2= new Terminal(container, "ter2", new Span<Term>(t2));
	NonTerminal nter = new NonTerminal(container, "nter1", "NP");
	
	assertEquals("nter1", nter.getId());
	assertFalse(nter.isTerminal());
	assertTrue(nter.isNonTerminal());
	assertFalse(nter.isHead());
	assertFalse(nter.hasEdgeId());
	
	assertEquals("NP", nter.getLabel());
	nter.setLabel("VP");
	assertEquals("VP", nter.getLabel());

	assertEquals(new ArrayList<Terminal>(), nter.getChildren());
	nter.addChild(ter1);
	nter.addChild(ter2);
	assertEquals(Arrays.asList(ter1, ter2), nter.getChildren());
	
	assertEquals("a b", nter.toString());
	assertEquals(new Integer(3), nter.getOffset());
	assertEquals(new Integer(2), nter.getSent());
	assertNull(nter.getPara());
	
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<KAFDocument.AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.WF, Arrays.asList((Annotation)w1, w2));
	referenced.put(AnnotationType.TERM, Arrays.asList((Annotation)t1, t2));
	referenced.put(AnnotationType.TERMINAL, Arrays.asList((Annotation)ter1, ter2));
	assertEquals(referenced, nter.getReferencedAnnotationsDeep());
    }

    @Test
    public void testTree() {
	AnnotationContainer container = new AnnotationContainer();
	WF w1 = new WF(container, "w1", 3, 1, "a", 2);
	WF w2 = new WF(container, "w2", 5, 1, "b", 2);
	Term t1 = new Term(container, "t1", new Span<WF>(w1));
	Term t2 = new Term(container, "t2", new Span<WF>(w2));
	Terminal ter1= new Terminal(container, "ter1", new Span<Term>(t1));
	Terminal ter2= new Terminal(container, "ter2", new Span<Term>(t2));
	NonTerminal root = new NonTerminal(container, "nter1", "ROOT");
	NonTerminal nter1 = new NonTerminal(container, "nter2", "NP");
	NonTerminal nter2 = new NonTerminal(container, "nter3", "VP");
	Tree tree = new Tree(container, root, "type1");

	assertFalse(ter1.isHead());
	assertFalse(ter2.isHead());
	assertFalse(nter1.isHead());
	assertFalse(nter2.isHead());
	
	assertFalse(nter1.hasEdgeId());
	assertFalse(nter2.hasEdgeId());
	assertFalse(ter1.hasEdgeId());
	assertFalse(ter2.hasEdgeId());
	
	nter1.addChild(ter1, "tre5", true);
	nter2.addChild(ter2);
	root.addChild(nter1, "edge10");
	root.addChild(nter2, true);

	assertTrue(ter1.isHead());
	assertFalse(ter2.isHead());
	assertFalse(nter1.isHead());
	assertTrue(nter2.isHead());
	
	assertTrue(nter1.hasEdgeId());
	assertTrue(nter2.hasEdgeId());
	assertTrue(ter1.hasEdgeId());
	assertTrue(ter2.hasEdgeId());
	assertEquals("tre5", ter1.getEdgeId());
	assertEquals("tre6", ter2.getEdgeId());
	assertEquals("edge10", nter1.getEdgeId());
	assertEquals("tre11", nter2.getEdgeId());
	
	assertEquals("type1", tree.getType());
	assertEquals(root, tree.getRoot());
	tree.setType("type2");
	assertEquals("type2", tree.getType());
	
	assertEquals("a b", tree.toString());
	assertEquals(new Integer(3), tree.getOffset());
	assertEquals(new Integer(2), tree.getSent());
	assertNull(tree.getPara());
	
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<KAFDocument.AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.WF, Arrays.asList((Annotation)w1, w2));
	referenced.put(AnnotationType.TERM, Arrays.asList((Annotation)t1, t2));
	referenced.put(AnnotationType.TERMINAL, Arrays.asList((Annotation)ter1, ter2));
	referenced.put(AnnotationType.NON_TERMINAL, Arrays.asList((Annotation)root, nter1, nter2));
	assertEquals(referenced, tree.getReferencedAnnotationsDeep());
    }

}

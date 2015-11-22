package ixa.kaflib;

import static org.junit.Assert.*;
import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Layer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;


public class AnnotationContainerTest {

    @Test
    public void testInsertions() {
	AnnotationContainer container = new AnnotationContainer();
	WF wf1 = new WF(container, "w1", 4, 1, "a", 1);
	WF wf2 = new WF(container, "w2", 8, 1, "b", 1);
	WF wf3 = new WF(container, "w3", 6, 1, "c", 1);
	WF wf4 = new WF(container, "w4", 2, 1, "d", 1);
	WF wf5 = new WF(container, "w5", 1, 1, "e", 1);
	WF wf6 = new WF(container, "w6", 10, 1, "f", 1);
	WF wf7 = new WF(container, "w7", 11, 1, "g", 1);
	container.add(wf1, AnnotationType.WF);
	container.add(wf2, AnnotationType.WF);
	container.addSorted(wf3, AnnotationType.WF);
	container.addAt(wf4, AnnotationType.WF, 0);
	container.addSorted(wf5, AnnotationType.WF);
	container.addSorted(wf6, AnnotationType.WF);
	container.add(wf7,  AnnotationType.WF);
	
	List<WF> wfs = Arrays.asList(wf5, wf4, wf1, wf3, wf2, wf6, wf7);
	assertEquals(wfs, container.getAnnotations(AnnotationType.WF));
    }
    
    @Test
    public void testInverseIndex() {
	AnnotationContainer container = new AnnotationContainer();
	WF wf1 = new WF(container, "w1", 0, 1, "a", 1);
	WF wf2 = new WF(container, "w2", 2, 1, "b", 1);
	WF wf3 = new WF(container, "w3", 4, 1, "c", 1);
	WF wf4 = new WF(container, "w4", 6, 1, "d", 1);
	WF wf5 = new WF(container, "w5", 8, 1, "e", 1);
	WF wf6 = new WF(container, "w6", 10, 1, "f", 1);
	WF wf7 = new WF(container, "w7", 12, 1, "g", 1);
	WF wf8 = new WF(container, "w8", 14, 1, "h", 1);
	WF wf9 = new WF(container, "w9", 16, 1, "i", 1);
	container.add(wf1, AnnotationType.WF);
	container.add(wf2, AnnotationType.WF);
	container.add(wf3, AnnotationType.WF);
	container.add(wf4, AnnotationType.WF);
	container.add(wf5, AnnotationType.WF);
	container.add(wf6, AnnotationType.WF);
	container.add(wf7, AnnotationType.WF);
	container.add(wf8, AnnotationType.WF);
	Term t1 = new Term(container, "t1", new Span<WF>(wf1, wf2));
	Term t2 = new Term(container, "t2", new Span<WF>(wf3));
	Term t3 = new Term(container, "t3", new Span<WF>(wf4, wf5, wf6));
	Term t4 = new Term(container, "t4", new Span<WF>(wf7));
	Term t5 = new Term(container, "t5", new Span<WF>(wf7));
	container.add(t1, AnnotationType.TERM);
	container.add(t2, AnnotationType.TERM);
	container.add(t3, AnnotationType.TERM);
	container.add(t4, AnnotationType.TERM);
	container.add(t5, AnnotationType.TERM);
	Entity e1 = new Entity(container, "e1", Arrays.asList(new Span<Term>(t1)));
	Entity e2 = new Entity(container, "e2", Arrays.asList(new Span<Term>(t2, t3)));
	Entity e3 = new Entity(container, "e3", Arrays.asList(new Span<Term>(t4), new Span<Term>(t5)));
	container.add(e1, AnnotationType.ENTITY);
	container.add(e2, AnnotationType.ENTITY);
	container.add(e3, AnnotationType.ENTITY);

	assertEquals(Arrays.asList(t1), container.getAnnotationsBy(wf1, AnnotationType.TERM));
	assertEquals(Arrays.asList(t1), container.getAnnotationsBy(wf2, AnnotationType.TERM));
	assertEquals(Arrays.asList(t4, t5), container.getAnnotationsBy(wf7, AnnotationType.TERM));
	assertEquals(Arrays.asList(), container.getAnnotationsBy(wf9, AnnotationType.TERM));
	assertEquals(Arrays.asList(e1), container.getAnnotationsBy(t1, AnnotationType.ENTITY));
	assertEquals(Arrays.asList(e1), container.getAnnotationsBy(wf2, AnnotationType.ENTITY));
	assertEquals(Arrays.asList(), container.getAnnotationsBy(wf9, AnnotationType.ENTITY));
	
	assertEquals(Arrays.asList(t1), container.getAnnotationsBy(wf1, Layer.TERMS));
	assertEquals(Arrays.asList(t1), container.getAnnotationsBy(wf2, Layer.TERMS));
	assertEquals(Arrays.asList(t4, t5), container.getAnnotationsBy(wf7, Layer.TERMS));
	assertEquals(Arrays.asList(), container.getAnnotationsBy(wf9, Layer.TERMS));
	assertEquals(Arrays.asList(e1), container.getAnnotationsBy(t1, Layer.ENTITIES));
	assertEquals(Arrays.asList(e1), container.getAnnotationsBy(wf2, Layer.ENTITIES));
	assertEquals(Arrays.asList(), container.getAnnotationsBy(wf9, Layer.ENTITIES));

	assertEquals(Arrays.asList(t1), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf1, wf2)), AnnotationType.TERM));
	assertEquals(Arrays.asList(t1, t2, t3, t4, t5), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf1, wf3, wf4, wf6, wf7)), AnnotationType.TERM));
	assertEquals(Arrays.asList(e2), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf3, wf4, wf5)), AnnotationType.ENTITY));
	assertEquals(Arrays.asList(e2, e3), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf3, wf4, wf5, wf7)), AnnotationType.ENTITY));
	assertEquals(Arrays.asList(e1), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf9, wf1)), AnnotationType.ENTITY));
	assertEquals(Arrays.asList(e3, e1, e2), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf7, wf1, wf3)), AnnotationType.ENTITY));
	
	assertEquals(Arrays.asList(t1), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf1, wf2)), Layer.TERMS));
	assertEquals(Arrays.asList(t1, t2, t3, t4, t5), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf1, wf3, wf4, wf6, wf7)), Layer.TERMS));
	assertEquals(Arrays.asList(e2), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf3, wf4, wf5)), Layer.ENTITIES));
	assertEquals(Arrays.asList(e1), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf9, wf1)), AnnotationType.ENTITY));
	assertEquals(Arrays.asList(e3, e1, e2), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf7, wf1, wf3)), AnnotationType.ENTITY));
    }
    
    /*
    NonTerminal nter1 = new NonTerminal(container, "nter1", "S");
    Terminal ter1 = new Terminal(container, "ter1", new Span<Term>(t1));
    nter1.addChild(ter1);
    NonTerminal nter2 = new NonTerminal(container, "nter2", "VP");
    nter1.addChild(nter2);
    Terminal ter2 = new Terminal(container, "ter2", new Span<Term>(t2));
    nter2.addChild(ter2);
    Tree tree1 = new Tree(container, nter1, "constituency");
    container.add(tree1, AnnotationType.TREE);
    Dep d1 = new Dep(container, t2, t1, "func");
    Dep d2 = new Dep(container, t5, t1, "func");
    Dep d3 = new Dep(container, t3, t5, "func");
    Dep d4 = new Dep(container, t4, t3, "func");
    container.add(d1, AnnotationType.DEP);
    container.add(d2, AnnotationType.DEP);
    container.add(d3, AnnotationType.DEP);
    container.add(d4, AnnotationType.DEP);
    */
}

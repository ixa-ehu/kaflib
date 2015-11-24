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
	
	/* Unindex reference */
	
	container.unindexAnnotationReferences(AnnotationType.ENTITY, e2, t3);

	assertEquals(Arrays.asList(t3), container.getAnnotationsBy(wf4, AnnotationType.TERM));
	assertEquals(Arrays.asList(), container.getAnnotationsBy(wf4, AnnotationType.ENTITY));
	assertEquals(Arrays.asList(e2), container.getAnnotationsBy(wf3, AnnotationType.ENTITY));
	assertEquals(Arrays.asList(t3), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf4, wf5, wf6)), AnnotationType.TERM));
	assertEquals(Arrays.asList(), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf4, wf5, wf6)), AnnotationType.ENTITY));
	assertEquals(Arrays.asList(e2), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf3, wf4, wf5, wf6)), AnnotationType.ENTITY));
	
	/* Index reference */

	container.indexAnnotationReferences(AnnotationType.ENTITY, e2, t3);
	
	assertEquals(Arrays.asList(t3), container.getAnnotationsBy(wf4, AnnotationType.TERM));
	assertEquals(Arrays.asList(e2), container.getAnnotationsBy(wf4, AnnotationType.ENTITY));
	assertEquals(Arrays.asList(t3), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf4, wf5, wf6)), AnnotationType.TERM));
	assertEquals(Arrays.asList(e2), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf4, wf5, wf6)), AnnotationType.ENTITY));
	assertEquals(Arrays.asList(e2), container.getAnnotationsBy(new ArrayList<Annotation>(Arrays.asList(wf3, wf4, wf5, wf6)), AnnotationType.ENTITY));
    }
    
    @Test
    public void testSentIndex() {
	AnnotationContainer container = new AnnotationContainer();
	container.setRawText("a b c d e f g h i j k l");
	WF wf1 = new WF(container, "w1", 0, 1, "a", 0);
	WF wf2 = new WF(container, "w2", 2, 1, "b", 0);
	WF wf3 = new WF(container, "w3", 4, 1, "c", 1);
	WF wf4 = new WF(container, "w4", 6, 1, "d", 1);
	WF wf5 = new WF(container, "w5", 8, 1, "e", 1);
	WF wf6 = new WF(container, "w6", 10, 1, "f", 1);
	WF wf7 = new WF(container, "w7", 12, 1, "g", 2);
	WF wf8 = new WF(container, "w8", 14, 1, "h", 3);
	WF wf9 = new WF(container, "w9", 16, 1, "i", 3);
	WF wf10 = new WF(container, "w10", 18, 1, "j", 3);
	WF wf11 = new WF(container, "w11", 20, 1, "k", 6);
	WF wf12 = new WF(container, "w12", 22, 1, "l", 6);
	container.add(wf1, AnnotationType.WF);
	container.add(wf2, AnnotationType.WF);
	container.add(wf3, AnnotationType.WF);
	container.add(wf4, AnnotationType.WF);
	container.add(wf5, AnnotationType.WF);
	container.add(wf6, AnnotationType.WF);
	container.add(wf7, AnnotationType.WF);
	container.add(wf8, AnnotationType.WF);
	container.add(wf9, AnnotationType.WF);
	container.add(wf10, AnnotationType.WF);
	container.add(wf11, AnnotationType.WF);
	container.add(wf12, AnnotationType.WF);
	Term t1 = new Term(container, "t1", new Span<WF>(wf1, wf2));
	Term t2 = new Term(container, "t2", new Span<WF>(wf3));
	Term t3 = new Term(container, "t3", new Span<WF>(wf4, wf5, wf6));
	Term t4 = new Term(container, "t4", new Span<WF>(wf7));
	Term t5 = new Term(container, "t5", new Span<WF>(wf8));
	Term t6 = new Term(container, "t6", new Span<WF>(wf8));
	Term t7 = new Term(container, "t7", new Span<WF>(wf9));
	Term t8 = new Term(container, "t8", new Span<WF>(wf11));
	Term t9 = new Term(container, "t9", new Span<WF>(wf12));
	container.add(t1, AnnotationType.TERM);
	container.add(t2, AnnotationType.TERM);
	container.add(t3, AnnotationType.TERM);
	container.add(t4, AnnotationType.TERM);
	container.add(t5, AnnotationType.TERM);
	container.add(t6, AnnotationType.TERM);
	container.add(t7, AnnotationType.TERM);
	container.add(t8, AnnotationType.TERM);
	container.add(t9, AnnotationType.TERM);
	Entity e1 = new Entity(container, "e1", Arrays.asList(new Span<Term>(t1)));
	Entity e2 = new Entity(container, "e2", Arrays.asList(new Span<Term>(t2, t3)));
	Entity e3 = new Entity(container, "e3", Arrays.asList(new Span<Term>(t5), new Span<Term>(t6)));
	Entity e4 = new Entity(container, "e4", Arrays.asList(new Span<Term>(t7)));
	Entity e5 = new Entity(container, "e5", Arrays.asList(new Span<Term>(t8, t9)));
	container.add(e1, AnnotationType.ENTITY);
	container.add(e2, AnnotationType.ENTITY);
	container.add(e3, AnnotationType.ENTITY);
	container.add(e4, AnnotationType.ENTITY);
	container.add(e5, AnnotationType.ENTITY);
	
	
	/* Test: no sentence data edited */
	
	assertEquals(new Integer(5), container.getNumSentences());
	assertEquals(Arrays.asList(0, 1, 2, 3, 6), container.getSentences());
	assertEquals(new Integer(0), container.getFirstSentence());
	
	assertEquals(new Integer(0), container.getNumParagraphs());
	assertEquals(Arrays.asList(), container.getParagraphs());
	assertNull(container.getFirstParagraph());

	assertEquals(new String("a b c d e f g h i j k l"), container.getRawText());
	assertEquals(Arrays.asList(), container.getSentsByPara(1));
	assertEquals(new Integer(2), container.getPosition(AnnotationType.TERM, t3));
	
	assertEquals(Arrays.asList(wf1, wf2), container.getAnnotationsBySent(0, AnnotationType.WF));
	assertEquals(Arrays.asList(t1), container.getAnnotationsBySent(0, AnnotationType.TERM));
	assertEquals(Arrays.asList(wf3, wf4, wf5, wf6), container.getAnnotationsBySent(1, AnnotationType.WF));
	assertEquals(Arrays.asList(Arrays.asList(wf1, wf2), Arrays.asList(wf3, wf4, wf5, wf6), Arrays.asList(wf7), Arrays.asList(wf8, wf9, wf10), Arrays.asList(wf11, wf12)), container.getAnnotationsBySentences(AnnotationType.WF));
	assertEquals(Arrays.asList(Arrays.asList(t1), Arrays.asList(t2, t3), Arrays.asList(t4), Arrays.asList(t5, t6, t7), Arrays.asList(t8, t9)), container.getAnnotationsBySentences(AnnotationType.TERM));
	assertEquals(Arrays.asList(Arrays.asList(e1), Arrays.asList(e2), Arrays.asList(), Arrays.asList(e3, e4), Arrays.asList(e5)), container.getAnnotationsBySentences(AnnotationType.ENTITY));
	
	assertEquals(Arrays.asList(wf1, wf2), container.getAnnotationsBySent(0, Layer.TEXT));
	assertEquals(Arrays.asList(wf3, wf4, wf5, wf6), container.getAnnotationsBySent(1, Layer.TEXT));
	assertEquals(Arrays.asList(Arrays.asList(wf1, wf2), Arrays.asList(wf3, wf4, wf5, wf6), Arrays.asList(wf7), Arrays.asList(wf8, wf9, wf10), Arrays.asList(wf11, wf12)), container.getAnnotationsBySentences(Layer.TEXT));
	assertEquals(Arrays.asList(Arrays.asList(t1), Arrays.asList(t2, t3), Arrays.asList(t4), Arrays.asList(t5, t6, t7), Arrays.asList(t8, t9)), container.getAnnotationsBySentences(Layer.TERMS));
	assertEquals(Arrays.asList(Arrays.asList(e1), Arrays.asList(e2), Arrays.asList(), Arrays.asList(e3, e4), Arrays.asList(e5)), container.getAnnotationsBySentences(Layer.ENTITIES));
	
	assertEquals(Arrays.asList(), container.getAnnotationsByPara(0, AnnotationType.WF));
	assertEquals(Arrays.asList(), container.getAnnotationsByParagraphs(AnnotationType.WF));
	assertEquals(Arrays.asList(), container.getAnnotationsByParagraphs(Layer.TEXT));
	
	
	/* Test: edited */
	
	// [wf8, wf9, wf10, t5, t6, t7, e3, e4] => sent:(3->6)
	wf8.setSent(6);
	wf9.setSent(6);
	wf10.setSent(6);
	container.updateAnnotationSent(wf8, AnnotationType.WF, 3, 6);
	container.updateAnnotationSent(wf9, AnnotationType.WF, 3, 6);
	container.updateAnnotationSent(wf10, AnnotationType.WF, 3, 6);
	// [wf2, t1*, e1*] => sent:(0->2)
	wf2.setSent(2);
	container.updateAnnotationSent(wf2, AnnotationType.WF, 0, 2);
	
	assertEquals(new Integer(4), container.getNumSentences());
	assertEquals(Arrays.asList(0, 1, 2, 6), container.getSentences());
	assertEquals(new Integer(0), container.getFirstSentence());
	assertEquals(new Integer(6), container.getPosition(AnnotationType.TERM, t7));

	assertEquals(Arrays.asList(wf1), container.getAnnotationsBySent(0, AnnotationType.WF));
	assertEquals(Arrays.asList(), container.getAnnotationsBySent(0, AnnotationType.TERM));
	assertEquals(Arrays.asList(t1, t4), container.getAnnotationsBySent(2, AnnotationType.TERM));
	assertEquals(Arrays.asList(e2), container.getAnnotationsBySent(1, AnnotationType.ENTITY));
	assertEquals(Arrays.asList(wf2, wf7), container.getAnnotationsBySent(2, AnnotationType.WF));
	assertEquals(Arrays.asList(Arrays.asList(wf1), Arrays.asList(wf3, wf4, wf5, wf6), Arrays.asList(wf2, wf7), Arrays.asList(wf8, wf9, wf10, wf11, wf12)), container.getAnnotationsBySentences(AnnotationType.WF));
	assertEquals(Arrays.asList(Arrays.asList(), Arrays.asList(t2, t3), Arrays.asList(t1, t4), Arrays.asList(t5, t6, t7, t8, t9)), container.getAnnotationsBySentences(AnnotationType.TERM));
	assertEquals(Arrays.asList(Arrays.asList(), Arrays.asList(e2), Arrays.asList(e1), Arrays.asList(e3, e4, e5)), container.getAnnotationsBySentences(AnnotationType.ENTITY));

	assertEquals(Arrays.asList(wf1), container.getAnnotationsBySent(0, Layer.TEXT));
	assertEquals(Arrays.asList(), container.getAnnotationsBySent(0, Layer.TERMS));
	assertEquals(Arrays.asList(t1, t4), container.getAnnotationsBySent(2, Layer.TERMS));
	assertEquals(Arrays.asList(e2), container.getAnnotationsBySent(1, Layer.ENTITIES));
	assertEquals(Arrays.asList(wf2, wf7), container.getAnnotationsBySent(2, Layer.TEXT));
	assertEquals(Arrays.asList(Arrays.asList(wf1), Arrays.asList(wf3, wf4, wf5, wf6), Arrays.asList(wf2, wf7), Arrays.asList(wf8, wf9, wf10, wf11, wf12)), container.getAnnotationsBySentences(Layer.TEXT));
	assertEquals(Arrays.asList(Arrays.asList(), Arrays.asList(t2, t3), Arrays.asList(t1, t4), Arrays.asList(t5, t6, t7, t8, t9)), container.getAnnotationsBySentences(Layer.TERMS));
	assertEquals(Arrays.asList(Arrays.asList(), Arrays.asList(e2), Arrays.asList(e1), Arrays.asList(e3, e4, e5)), container.getAnnotationsBySentences(Layer.ENTITIES));
	
	// remove wf2, t5, t6, all:entities
	container.remove(wf2,  AnnotationType.WF);
	container.remove(t5, AnnotationType.TERM);
	container.remove(t6, AnnotationType.TERM);
	container.remove(Layer.ENTITIES);
	
	assertEquals(Arrays.asList(wf7), container.getAnnotationsBySent(2, AnnotationType.WF));
	assertEquals(Arrays.asList(t4), container.getAnnotationsBySent(2, AnnotationType.TERM));
	assertEquals(Arrays.asList(), container.getAnnotationsBySent(3, AnnotationType.TERM));
	assertEquals(Arrays.asList(Arrays.asList(wf1), Arrays.asList(wf3, wf4, wf5, wf6), Arrays.asList(wf7), Arrays.asList(wf8, wf9, wf10, wf11, wf12)), container.getAnnotationsBySentences(AnnotationType.WF));
	assertEquals(Arrays.asList(Arrays.asList(), Arrays.asList(t2, t3), Arrays.asList(t4), Arrays.asList(t7, t8, t9)), container.getAnnotationsBySentences(AnnotationType.TERM));
	assertEquals(Arrays.asList(Arrays.asList(), Arrays.asList(), Arrays.asList(), Arrays.asList()), container.getAnnotationsBySentences(Layer.ENTITIES));
    }
    
    @Test
    public void testParaIndex() {
	AnnotationContainer container = new AnnotationContainer();
	container.setRawText("a b c d e f g h i j k l");
	WF wf1 = new WF(container, "w1", 0, 1, "a", 0);
	wf1.setPara(1);
	WF wf2 = new WF(container, "w2", 2, 1, "b", 0);
	wf2.setPara(1);
	WF wf3 = new WF(container, "w3", 4, 1, "c", 1);
	wf3.setPara(2);
	WF wf4 = new WF(container, "w4", 6, 1, "d", 1);
	wf4.setPara(2);
	WF wf5 = new WF(container, "w5", 8, 1, "e", 1);
	wf5.setPara(2);
	WF wf6 = new WF(container, "w6", 10, 1, "f", 1);
	wf6.setPara(2);
	WF wf7 = new WF(container, "w7", 12, 1, "g", 2);
	wf7.setPara(2);
	WF wf8 = new WF(container, "w8", 14, 1, "h", 3);
	wf8.setPara(2);
	WF wf9 = new WF(container, "w9", 16, 1, "i", 3);
	wf9.setPara(2);
	WF wf10 = new WF(container, "w10", 18, 1, "j", 3);
	wf10.setPara(2);
	WF wf11 = new WF(container, "w11", 20, 1, "k", 6);
	wf11.setPara(4);
	WF wf12 = new WF(container, "w12", 22, 1, "l", 6);
	wf12.setPara(4);
	container.add(wf1, AnnotationType.WF);
	container.add(wf2, AnnotationType.WF);
	container.add(wf3, AnnotationType.WF);
	container.add(wf4, AnnotationType.WF);
	container.add(wf5, AnnotationType.WF);
	container.add(wf6, AnnotationType.WF);
	container.add(wf7, AnnotationType.WF);
	container.add(wf8, AnnotationType.WF);
	container.add(wf9, AnnotationType.WF);
	container.add(wf10, AnnotationType.WF);
	container.add(wf11, AnnotationType.WF);
	container.add(wf12, AnnotationType.WF);
	Term t1 = new Term(container, "t1", new Span<WF>(wf1, wf2));
	Term t2 = new Term(container, "t2", new Span<WF>(wf3));
	Term t3 = new Term(container, "t3", new Span<WF>(wf4, wf5, wf6));
	Term t4 = new Term(container, "t4", new Span<WF>(wf7));
	Term t5 = new Term(container, "t5", new Span<WF>(wf8));
	Term t6 = new Term(container, "t6", new Span<WF>(wf8));
	Term t7 = new Term(container, "t7", new Span<WF>(wf9));
	Term t8 = new Term(container, "t8", new Span<WF>(wf11));
	Term t9 = new Term(container, "t9", new Span<WF>(wf12));
	container.add(t1, AnnotationType.TERM);
	container.add(t2, AnnotationType.TERM);
	container.add(t3, AnnotationType.TERM);
	container.add(t4, AnnotationType.TERM);
	container.add(t5, AnnotationType.TERM);
	container.add(t6, AnnotationType.TERM);
	container.add(t7, AnnotationType.TERM);
	container.add(t8, AnnotationType.TERM);
	container.add(t9, AnnotationType.TERM);
	Entity e1 = new Entity(container, "e1", Arrays.asList(new Span<Term>(t1)));
	Entity e2 = new Entity(container, "e2", Arrays.asList(new Span<Term>(t2, t3)));
	Entity e3 = new Entity(container, "e3", Arrays.asList(new Span<Term>(t5), new Span<Term>(t6)));
	Entity e4 = new Entity(container, "e4", Arrays.asList(new Span<Term>(t7)));
	Entity e5 = new Entity(container, "e5", Arrays.asList(new Span<Term>(t8, t9)));
	container.add(e1, AnnotationType.ENTITY);
	container.add(e2, AnnotationType.ENTITY);
	container.add(e3, AnnotationType.ENTITY);
	container.add(e4, AnnotationType.ENTITY);
	container.add(e5, AnnotationType.ENTITY);
	
	
	/* Test: paragraph index */
	
	assertEquals(new Integer(3), container.getNumParagraphs());
	assertEquals(Arrays.asList(1, 2, 4), container.getParagraphs());
	assertEquals(new Integer(1), container.getFirstParagraph());
	assertEquals(Arrays.asList(), container.getSentsByPara(0));
	assertEquals(Arrays.asList(0), container.getSentsByPara(1));
	assertEquals(Arrays.asList(1, 2, 3), container.getSentsByPara(2));
	assertEquals(Arrays.asList(), container.getSentsByPara(3));
	assertEquals(Arrays.asList(6), container.getSentsByPara(4));
	
	assertEquals(Arrays.asList(wf1, wf2), container.getAnnotationsByPara(1, AnnotationType.WF));
	assertEquals(Arrays.asList(t1), container.getAnnotationsByPara(1, AnnotationType.TERM));
	assertEquals(Arrays.asList(t2, t3, t4, t5, t6, t7), container.getAnnotationsByPara(2, AnnotationType.TERM));
	assertEquals(Arrays.asList(Arrays.asList(wf1, wf2), Arrays.asList(wf3, wf4, wf5, wf6, wf7, wf8, wf9, wf10), Arrays.asList(wf11, wf12)), container.getAnnotationsByParagraphs(AnnotationType.WF));
	assertEquals(Arrays.asList(Arrays.asList(t1), Arrays.asList(t2, t3, t4, t5, t6, t7), Arrays.asList(t8, t9)), container.getAnnotationsByParagraphs(AnnotationType.TERM));
	assertEquals(Arrays.asList(Arrays.asList(e1), Arrays.asList(e2, e3, e4), Arrays.asList(e5)), container.getAnnotationsByParagraphs(AnnotationType.ENTITY));
	
	assertEquals(Arrays.asList(wf1, wf2), container.getAnnotationsByPara(1, Layer.TEXT));
	assertEquals(Arrays.asList(t1), container.getAnnotationsByPara(1, Layer.TERMS));
	assertEquals(Arrays.asList(t2, t3, t4, t5, t6, t7), container.getAnnotationsByPara(2, Layer.TERMS));
	assertEquals(Arrays.asList(Arrays.asList(wf1, wf2), Arrays.asList(wf3, wf4, wf5, wf6, wf7, wf8, wf9, wf10), Arrays.asList(wf11, wf12)), container.getAnnotationsByParagraphs(Layer.TEXT));
	assertEquals(Arrays.asList(Arrays.asList(t1), Arrays.asList(t2, t3, t4, t5, t6, t7), Arrays.asList(t8, t9)), container.getAnnotationsByParagraphs(Layer.TERMS));
	assertEquals(Arrays.asList(Arrays.asList(e1), Arrays.asList(e2, e3, e4), Arrays.asList(e5)), container.getAnnotationsByParagraphs(Layer.ENTITIES));
	
	
	/* Test: paragraph data edited */
	
	// [wf1, wf2, t1, e1] => para:(1->3)
	wf1.setPara(3);
	wf1.setPara(3);
	container.updateAnnotationPara(wf1, AnnotationType.WF, 1, 3);
	// [wf5, t3*, e2*] => para:(2->4)
	wf5.setPara(4);
	container.updateAnnotationPara(wf5, AnnotationType.WF, 2, 4);
	// [wf8, wf9, t6, t7, e3*, e4] => para:(2->4)
	wf8.setPara(4);
	wf9.setPara(9);
	container.updateAnnotationPara(wf8, AnnotationType.WF, 2, 4);
	container.updateAnnotationPara(wf9, AnnotationType.WF, 2, 4);
	

	assertEquals(new Integer(3), container.getNumParagraphs());
	assertEquals(Arrays.asList(2, 3, 4), container.getParagraphs());
	assertEquals(new Integer(2), container.getFirstParagraph());
	assertEquals(Arrays.asList(), container.getSentsByPara(0));
	assertEquals(Arrays.asList(), container.getSentsByPara(1));
	assertEquals(Arrays.asList(1, 2), container.getSentsByPara(2));
	assertEquals(Arrays.asList(0), container.getSentsByPara(3));
	assertEquals(Arrays.asList(3, 6), container.getSentsByPara(4));

	assertEquals(Arrays.asList(), container.getAnnotationsByPara(1, AnnotationType.TERM));
	assertEquals(Arrays.asList(wf3, wf4, wf5, wf6, wf7), container.getAnnotationsByPara(2, AnnotationType.WF));
	assertEquals(Arrays.asList(t2, t3, t4), container.getAnnotationsByPara(2, AnnotationType.TERM));
	assertEquals(Arrays.asList(t5, t6, t7, t8, t9), container.getAnnotationsByPara(4, AnnotationType.TERM));
	assertEquals(Arrays.asList(Arrays.asList(wf3, wf4, wf5, wf6, wf7), Arrays.asList(wf1, wf2), Arrays.asList(wf8, wf9, wf10, wf11, wf12)), container.getAnnotationsByParagraphs(AnnotationType.WF));
	assertEquals(Arrays.asList(Arrays.asList(t2, t3, t4), Arrays.asList(t1), Arrays.asList(t5, t6, t7, t8, t9)), container.getAnnotationsByParagraphs(AnnotationType.TERM));
	assertEquals(Arrays.asList(Arrays.asList(e2), Arrays.asList(e1), Arrays.asList(e3, e4, e5)), container.getAnnotationsByParagraphs(AnnotationType.ENTITY));
	
	assertEquals(Arrays.asList(), container.getAnnotationsByPara(1, Layer.TERMS));
	assertEquals(Arrays.asList(wf3, wf4, wf5, wf6, wf7), container.getAnnotationsByPara(2, Layer.TEXT));
	assertEquals(Arrays.asList(t2, t3, t4), container.getAnnotationsByPara(2, Layer.TERMS));
	assertEquals(Arrays.asList(t5, t6, t7, t8, t9), container.getAnnotationsByPara(4, Layer.TERMS));
	assertEquals(Arrays.asList(Arrays.asList(wf3, wf4, wf5, wf6, wf7), Arrays.asList(wf1, wf2), Arrays.asList(wf8, wf9, wf10, wf11, wf12)), container.getAnnotationsByParagraphs(Layer.TEXT));
	assertEquals(Arrays.asList(Arrays.asList(t2, t3, t4), Arrays.asList(t1), Arrays.asList(t5, t6, t7, t8, t9)), container.getAnnotationsByParagraphs(Layer.TERMS));
	assertEquals(Arrays.asList(Arrays.asList(e2), Arrays.asList(e1), Arrays.asList(e3, e4, e5)), container.getAnnotationsByParagraphs(Layer.ENTITIES));
    }
    
}

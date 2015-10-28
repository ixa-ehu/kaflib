package ixa.kaflib;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class SpanTest {
    
    @Test
    public void testConstructor() {
	AnnotationContainer annCont = new AnnotationContainer();
	WF wf1 = new WF(annCont, "w1", 17, 3, "The", 1);
	WF wf2 = new WF(annCont, "w2", 21, 3, "cat", 1);
	WF wf3 = new WF(annCont, "w3", 25, 2, "is", 1);
	WF wf4 = new WF(annCont, "w4", 28, 4, "gone", 1);
	List<WF> wfs1 = new ArrayList<WF>(Arrays.asList(wf1, wf2, wf3, wf4));
	List<WF> wfs2 = new ArrayList<WF>(wfs1);
	Span<WF> span1 = new Span<WF>(wfs1);
	Span<WF> span2 = new Span<WF>(wfs2, wf4);
	Span<WF> span3 = new Span<WF>();
	testSpan(span1, wfs1, null, "Span constructed");
	testSpan(span2, wfs2, wf4, "Span constructed");
	testSpan(span3, new ArrayList<WF>(), null, "Span constructed");
	// List argument
	Span<WF> span4 = new Span<WF>(wf1, wf2, wf3, wf4);
	testSpan(span4, new ArrayList<WF>(Arrays.asList(wf1, wf2, wf3, wf4)), null, "Span constructed");
    }
    
    @Test
    public void testBasic() {
	AnnotationContainer annCont = new AnnotationContainer();
	WF wf1 = new WF(annCont, "w1", 17, 3, "The", 1);
	WF wf2 = new WF(annCont, "w2", 21, 3, "cat", 1);
	WF wf3 = new WF(annCont, "w3", 25, 2, "is", 1);
	WF wf4 = new WF(annCont, "w4", 28, 4, "gone", 1);
	WF wf5 = new WF(annCont, "w5", 33, 1, ".", 1);
	WF wf6 = new WF(annCont, "w0", 3, 1, ".", 1);
	WF wf7 = new WF(annCont, "w6", 1, 1, ".", 1);
	WF wf8 = new WF(annCont, "w7", 40, 1, ".", 1);
	List<WF> wfs = new ArrayList<WF>(Arrays.asList(wf1, wf2, wf3, wf4));
	Span<WF> span = new Span<WF>(wfs);
	/* HAS methods */
	assertFalse("hasHead should be false", span.hasHead());
	span.setHead(wf4);
	assertTrue("hasHead should be true after assigning one", span.hasHead());
	assertTrue("span contains target bat doesn not reflect so", span.hasTarget(wf1));
	assertTrue("span contains target bat doesn not reflect so", span.hasTarget(wf3));
	assertTrue("span contains target bat doesn not reflect so", span.hasTarget(wf4));
	assertFalse("", span.hasTarget(wf5));
	/* GET methods */
	assertEquals("", wf1, span.getFirstTarget());
	assertEquals("incorrect size of span", new Integer(4), span.size());
	assertFalse("filled span should return false when isEmpty() was called", span.isEmpty());
	assertEquals("span::getOffset() does not work", new Integer(17), span.getOffset());
	Span<WF> emptySpan = new Span<WF>();
	assertEquals("empty span should return 0 when size() was called", new Integer(0), emptySpan.size());
	assertTrue("empty span should return true when isEmpty() was called", emptySpan.isEmpty());
	assertNull("emptySpan::getOffset() should return null", emptySpan.getOffset());
	/* SET and ADD methods */
	span.setHead(null);
	span.addTarget(wf5);
	List<WF> wfs1 = new ArrayList<WF>(Arrays.asList(wf1, wf2, wf3, wf4, wf5));
	testSpan(span, wfs1, null, "Target added");
	span.addTarget(wf6, false);
	List<WF> wfs2 = new ArrayList<WF>(Arrays.asList(wf1, wf2, wf3, wf4, wf5, wf6));
	testSpan(span, wfs2, null, "Target added");
	assertEquals("span::getOffset() does not work", new Integer(3), span.getOffset());
	span.addTarget(wf8);
	List<WF> wfs4 = new ArrayList<WF>(Arrays.asList(wf1, wf2, wf3, wf4, wf5, wf6, wf8));
	span.setHead(wf8);
	testSpan(span, wfs4, wf8, "Target added");
	assertTrue("span::isHead() does not work", span.isHead(wf8));
	assertFalse("span::isHead() does not work", span.isHead(wf7));
	assertEquals("getFirstTarget() does not work", wf1, span.getFirstTarget());
	assertEquals("incorrect size of span", new Integer(7), span.size());
	span.removeTarget(wf7);
	span.removeTarget(wf8);
	testSpan(span, wfs2, null, "Targets removed (head included)");
	span.addTargets(Arrays.asList(wf7, wf8));
	List<WF> wfs5 = new ArrayList<WF>(Arrays.asList(wf1, wf2, wf3, wf4, wf5, wf6, wf7, wf8));
	testSpan(span, wfs5, null, "Targets added");
	/* toString() */
	assertEquals("Span::toString() problem", "The cat is gone . . . .", span.toString());
	/* hashCode() */
	List<WF> wfs10 = new ArrayList<WF>(Arrays.asList(wf1, wf2, wf3));
	List<WF> wfs11 = new ArrayList<WF>(Arrays.asList(wf1, wf2, wf3));
	List<WF> wfs12 = new ArrayList<WF>(Arrays.asList(wf1, wf2, wf3));
	List<WF> wfs13 = new ArrayList<WF>(Arrays.asList(wf1, wf2, wf3));
	Span<WF> span10 = new Span<WF>(wfs10);
	Span<WF> span11 = new Span<WF>(wfs11);
	Span<WF> span12 = new Span<WF>(wfs12);
	span12.addTarget(wf4);
	span12.addTarget(wf5);
	Span<WF> span13 = new Span<WF>(wfs13);
	span13.addTarget(wf5);
	span13.addTarget(wf4);
	assertEquals("Equal spans should return the same hashCode", span10.hashCode(), span11.hashCode());
	assertEquals("Equal spans (different insertion order, but the same targets) should return the same hashCode", span12.hashCode(), span13.hashCode());
	assertFalse("Spans with different targets should not be equals", span10.equals(span13));
	assertTrue("Spans with the same targets (different insertion order) should be equals", span12.equals(span13));
    }
    
    
    private <T extends IdentifiableAnnotation> void testSpan(Span<T> span, List<T> targets, T head, String msg) {
	assertEquals(msg + ": incorrect targets", targets, span.getTargets());
	assertEquals(msg + ": incorrect head", head, span.getHead());
    }
    
    
 
}
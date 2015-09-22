package ixa.kaflib;

import static org.junit.Assert.*;
import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

public class WFTest {

    @Test
    public void testConstructor() {
	WF wf = new WF(new AnnotationContainer(), "w11", 34, 4, "test", 1);
	this.testWF(wf, "w11", 34, 4, "test", 1, null, null, null, "WF contructor called");
    }

    @Test
    public void testBasic() {
	WF wf = new WF(new AnnotationContainer(), "w1", 0, 4, "test", 1);
	// GET methods
	this.testWF(wf, "w1", 0, 4, "test", 1, null, null, null, "WF contructor called");
	// HAS methods
	assertFalse("should have returned false", wf.hasPara());
	assertFalse("should have returned false", wf.hasPage());
	assertFalse("should have returned false", wf.hasXpath());
	assertTrue("should have returned true", wf.hasOffset());
	// SET methods
	wf.setForm("test2");
	wf.setOffset(39);
	wf.setLength(5);
	wf.setSent(3);
	wf.setPara(2);
	wf.setPage(1);
	wf.setXpath("//text/p[2]");
	this.testWF(wf, "w1", 39, 5, "test2", 3, 2, 1, "//text/p[2]", "WF attributes changed with setters");	
    }
    
    @Test
    public void testGetReferencedAnnotations() {
	WF wf = new WF(new AnnotationContainer(), "w1", 0, 4, "test", 1);
	assertEquals("WF object returned some referenced annotations", new HashMap<AnnotationType, List<Annotation>>(), wf.getReferencedAnnotations());
    }
    
    @Test
    public void testToString() {
	WF wf1 = new WF(new AnnotationContainer(), "w1", 0, 4, "test", 1);
	assertEquals("WF did not return the correct form when calling toString()", "test", wf1.toString());
	WF wf2 = new WF(new AnnotationContainer(), "w1", 0, 4, "testing toString()_function", 1);
	assertEquals("WF did not return the correct form when calling toString()", "testing toString()_function", wf2.toString());
    }
    
    
    static void testWF(WF wf, String id, Integer offset, Integer length, String form, Integer sent, Integer para, Integer page, String xpath, String msg) {
	assertEquals(msg + ": wrong id", id, wf.getId());
	assertEquals(msg + ": wrong offset", offset, wf.getOffset());
	assertEquals(msg + ": wrong length", length, wf.getLength());
	assertEquals(msg + ": wrong form", form, wf.getForm());
	assertEquals(msg + ": wrong sent", sent, wf.getSent());
	assertEquals(msg + ": wrong para", para, wf.getPara());
	assertEquals(msg + ": wrong page", page, wf.getPage());
	assertEquals(msg + ": wrong xpath", xpath, wf.getXpath());
    }
    
    static Span<WF> createWFSpan(AnnotationContainer annotationContainer) {
	WF wf1 = new WF(annotationContainer, "w1", 0, 3, "The", 3);
	wf1.setPara(2);
	WF wf2 = new WF(annotationContainer, "w2", 4, 5, "White", 3);
	wf2.setPara(2);
	WF wf3 = new WF(annotationContainer, "w3", 10, 5, "House", 3);
	wf3.setPara(2);
	Span<WF> span = new Span<WF>();
	span.addTarget(wf1);
	span.addTarget(wf2);
	span.addTarget(wf3, true);
	return span;
    }
}

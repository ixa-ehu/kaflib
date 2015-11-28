package ixa.kaflib;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ixa.kaflib.KAFDocument.AnnotationType;


public class DepTest {

    @Test
    public void testBasic() {
	AnnotationContainer container = new AnnotationContainer();
	WF w1 = new WF(container, "w1", 3, 1, "a", 2);
	WF w3 = new WF(container, "w3", 7, 1, "c", 2);
	WF w4 = new WF(container, "w4", 9, 1, "d", 2);
	WF w8 = new WF(container, "w8", 15, 1, "h", 2);
	Term t1 = new Term(container, "t1", new Span<WF>(w1));
	Term t3 = new Term(container, "t3", new Span<WF>(w3));
	Term t4 = new Term(container, "t4", new Span<WF>(w4));
	Term t8 = new Term(container, "t8", new Span<WF>(w8));
	Dep d1 = new Dep(container, t8, t1, "rfuncvalue");

	assertEquals(t8, d1.getFrom());
	assertEquals(t1, d1.getTo());
	d1.setFrom(t4);
	d1.setTo(t3);
	assertEquals(t4, d1.getFrom());
	assertEquals(t3, d1.getTo());
	
	assertEquals("rfuncvalue", d1.getRfunc());
	d1.setRfunc("subj");
	assertEquals("subj", d1.getRfunc());
	
	assertFalse(d1.hasCase());
	d1.setCase("casevalue");
	assertEquals("casevalue", d1.getCase());
	
	assertEquals("subj(d, c)", d1.toString());
	assertEquals(new Integer(7), d1.getOffset());
	assertEquals(new Integer(2), d1.getSent());
	assertNull(d1.getPara());
	
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<KAFDocument.AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.WF, Arrays.asList((Annotation)w3, w4));
	referenced.put(AnnotationType.TERM, Arrays.asList((Annotation)t3, t4));
	assertEquals(referenced, d1.getReferencedAnnotationsDeep());
    }

}

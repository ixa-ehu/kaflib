package ixa.kaflib;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ixa.kaflib.KAFDocument.AnnotationType;


public class ChunkTest {

    @Test
    public void testBasic() {
	AnnotationContainer container = new AnnotationContainer();
	WF w1 = new WF(container, "w1", 4, 1, "a", 2);
	WF w2 = new WF(container, "w2", 6, 1, "b", 3);
	Term t1 = new Term(container, "t1", new Span<WF>(w1));
	Term t2 = new Term(container, "t2", new Span<WF>(w2));
	Chunk c1 = new Chunk(container, "c5", new Span<Term>(t1));
	
	assertEquals("c5", c1.getId());
	assertEquals(new Span<Term>(t1), c1.getSpan());
	
	assertFalse(c1.hasHead());
	c1.getSpan().addTarget(t2, true);
	assertTrue(c1.hasHead());
	assertEquals(t2, c1.getHead());
	
	assertFalse(c1.hasPhrase());
	c1.setPhrase("phrasevalue");
	assertEquals("phrasevalue", c1.getPhrase());
	
	assertFalse(c1.hasCase());
	c1.setCase("casevalue");
	assertEquals("casevalue", c1.getCase());
	
	assertEquals("a b", c1.toString());
	assertEquals(new Integer(4), c1.getOffset());
	assertEquals(new Integer(2), c1.getSent());
	assertNull(c1.getPara());
	
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<KAFDocument.AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.WF, Arrays.asList((Annotation)w1, w2));
	referenced.put(AnnotationType.TERM, Arrays.asList((Annotation)t1, t2));
	assertEquals(referenced, c1.getReferencedAnnotationsDeep());
    }

}

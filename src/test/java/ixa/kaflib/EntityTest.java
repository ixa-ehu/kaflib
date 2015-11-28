package ixa.kaflib;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ixa.kaflib.KAFDocument.AnnotationType;


public class EntityTest {

    @Test
    public void testBasic() {
	AnnotationContainer container = new AnnotationContainer();
	WF w1 = new WF(container, "w1", 4, 1, "a", 2);
	WF w2 = new WF(container, "w2", 6, 1, "b", 3);
	Term t1 = new Term(container, "t1", new Span<WF>(w1));
	Term t2 = new Term(container, "t2", new Span<WF>(w2));
	Entity e1 = new Entity(container, "e1", Arrays.asList(new Span<Term>(t1), new Span<Term>(t2)));
	assertEquals("e1", e1.getId());
	assertEquals(Arrays.asList(new Span<Term>(t1), new Span<Term>(t2)), e1.getSpans());
	assertFalse(e1.hasType());
	e1.setType("Time");
	assertEquals("Time", e1.getType());

	assertFalse(e1.hasSource());
	e1.setSource("src");
	assertEquals("src", e1.getSource());
	
	assertEquals(new ExternalReferences(), e1.getExternalReferences());
	
	assertEquals("a", e1.toString());
	assertEquals(new Integer(4), e1.getOffset());
	assertEquals(new Integer(2), e1.getSent());
	assertNull(e1.getPara());
	
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.WF, Arrays.asList((Annotation)w1, w2));
	referenced.put(AnnotationType.TERM, Arrays.asList((Annotation)t1, t2));
	//referenced.put(AnnotationType.TERM, Arrays.asList((Annotation)t1, t2));
	assertEquals(referenced, e1.getReferencedAnnotationsDeep());
    }

}

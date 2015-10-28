package ixa.kaflib;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;


public class AnnotationTest {

    @Test
    public void testBasic() {
	// Compare
	AnnotationContainer container = new AnnotationContainer();
	WF wf1 = new WF(container, "w1", 37, 3, "cat", 1);
	WF wf2 = new WF(container, "w2", 33, 3, "the", 1);
	Span<WF> span1 = new Span<WF>(wf1, wf2);
	Term t1 = new Term(container, "t1", span1, false);
	Term t2 = new Term(container, "t2", span1, false);
	WF wf3 = new WF(container, "w3", 27, 5, "black", 1);
	// wf3, wf2, t1, t2, wf1
	List<Annotation> annotations = new ArrayList<Annotation>();
	annotations.add(wf1);
	annotations.add(wf2);
	annotations.add(t1);
	annotations.add(t2);
	annotations.add(wf3);
	Collections.sort(annotations);
	assertEquals(Arrays.asList(wf3, wf2, t1, t2, wf1), annotations);
	// toStringComment
	WF wf10 = new WF(container, "w1", 1, 55, "--the cat - is - - black -- but the dog -- is white ---", 1);
	assertEquals(new String(" - -the cat - is - - black - - but the dog - - is white - - - "), wf10.toStringComment());
    }
    
}

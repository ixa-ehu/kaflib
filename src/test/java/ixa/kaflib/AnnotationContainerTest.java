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
	WF wf1 = new WF(container, "wf1", 4, 1, "a", 1);
	WF wf2 = new WF(container, "wf2", 8, 1, "b", 1);
	WF wf3 = new WF(container, "wf3", 6, 1, "c", 1);
	WF wf4 = new WF(container, "wf4", 2, 1, "d", 1);
	WF wf5 = new WF(container, "wf5", 1, 1, "e", 1);
	WF wf6 = new WF(container, "wf6", 10, 1, "f", 1);
	WF wf7 = new WF(container, "wf7", 11, 1, "g", 1);
	container.append(wf1, AnnotationType.WF);
	container.append(wf2, AnnotationType.WF);
	container.addSorted(wf3, AnnotationType.WF);
	container.addAt(wf4, AnnotationType.WF, 0);
	container.addSorted(wf5, AnnotationType.WF);
	container.addSorted(wf6, AnnotationType.WF);
	container.append(wf7,  AnnotationType.WF);
	
	List<WF> wfs = Arrays.asList(wf5, wf4, wf1, wf3, wf2, wf6, wf7);
	assertEquals(wfs, container.getAnnotations(AnnotationType.WF));
    }
    
}

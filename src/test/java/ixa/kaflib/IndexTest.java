package ixa.kaflib;

import static org.junit.Assert.*;
import ixa.kaflib.KAFDocument.AnnotationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class IndexTest {

    @Test
    public void testSentIndex() {
	KAFDocument naf = new KAFDocument("en", "test");
	WF wf1 = naf.newWF(0, "The", 1);
	WF wf2 = naf.newWF(4, "house", 1);
	WF wf3 = naf.newWF(10, "is", 1);
	WF wf4 = naf.newWF(13, "white", 1);
	WF wf5 = naf.newWF(19, ".", 1);
	List<WF> sent1 = new ArrayList<WF>(Arrays.asList(wf1, wf2, wf3, wf4, wf5));
	assertEquals("WFs were not correctly indexed by sent when created", sent1, naf.getBySent(AnnotationType.WF, 1));
	assertEquals("WFs were not correctly indexed by sent when created", sent1, naf.getWFsBySent(1));
	assertEquals("WFs were not correctly indexed by sent when created", new ArrayList<WF>(), naf.getBySent(AnnotationType.WF, 2));
	assertEquals("WFs were not correctly indexed by sent when created", new ArrayList<WF>(), naf.getWFsBySent(2));
	WF wf6 = naf.newWF(21, "The", 2);
	WF wf7 = naf.newWF(25, "car", 2);
	WF wf8 = naf.newWF(29, "is", 2);
	WF wf9 = naf.newWF(32, "blue", 2);
	WF wf10 = naf.newWF(37, ".", 2);
	List<WF> sent2 = new ArrayList<WF>(Arrays.asList(wf6, wf7, wf8, wf9, wf10));
	assertEquals("WFs were not correctly indexed by sent when created", sent2, naf.getBySent(AnnotationType.WF, 2));
	assertEquals("WFs were not correctly indexed by sent when created", sent2, naf.getWFsBySent(2));
	// Edit sent values
	wf2.setSent(2);
	wf7.setSent(4);
	sent1.remove(wf2);
	sent2.add(0, wf2);
	sent2.remove(wf7);
	List<WF> sent4 = new ArrayList<WF>(Arrays.asList(wf7));
	List<WF> sent3 = new ArrayList<WF>();
	assertEquals("WFs were not correctly indexed by sent when editing sent value", sent1, naf.getBySent(AnnotationType.WF, 1));
	assertEquals("WFs were not correctly indexed by sent when editing sent value", sent1, naf.getWFsBySent(1));
	assertEquals("WFs were not correctly indexed by sent when editing sent value", sent2, naf.getBySent(AnnotationType.WF, 2));
	assertEquals("WFs were not correctly indexed by sent when editing sent value", sent2, naf.getWFsBySent(2));
	assertEquals("WFs were not correctly indexed by sent when editing sent value", sent3, naf.getBySent(AnnotationType.WF, 3));
	assertEquals("WFs were not correctly indexed by sent when editing sent value", sent3, naf.getWFsBySent(3));
	assertEquals("WFs were not correctly indexed by sent when editing sent value", sent4, naf.getBySent(AnnotationType.WF, 4));
	assertEquals("WFs were not correctly indexed by sent when editing sent value", sent4, naf.getWFsBySent(4));
	// Test 0 sentence
	WF wf11 = naf.newWF(0, "The", 0);
	WF wf12 = naf.newWF(4, "goods", 0);
	List<WF> sent0 = Arrays.asList(wf11, wf12);
	assertEquals("WFs were not correctly indexed by sent when sent is 0", sent0, naf.getBySent(AnnotationType.WF, 0));
	assertEquals("WFs were not correctly indexed by sent when sent is 0", sent0, naf.getWFsBySent(0));	
    }

}

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
	/*
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
	*/	
    }
    
    @Test
    public void testParaIndex() {
	/*
	KAFDocument naf = new KAFDocument("en", "test");
	assertEquals("WFs were returned after being queried by paragraph without any value defined", new ArrayList<WF>(), naf.getByPara(AnnotationType.WF, 0));
	assertEquals("WFs were returned after being queried by paragraph without any value defined", new ArrayList<WF>(), naf.getByPara(AnnotationType.WF, 1));
	WF wf1 = naf.newWF(0, "The", 2);
	wf1.setPara(1);
	WF wf2 = naf.newWF(4, "house", 2);
	wf2.setPara(1);
	WF wf3 = naf.newWF(10, "is", 2);
	wf3.setPara(1);
	WF wf4 = naf.newWF(13, "white", 2);
	wf4.setPara(1);
	WF wf5 = naf.newWF(19, ".", 2);
	wf5.setPara(1);
	List<WF> para1 = new ArrayList<WF>(Arrays.asList(wf1, wf2, wf3, wf4, wf5));
	assertEquals("WFs were not correctly indexed by paragraph when created", new ArrayList<WF>(), naf.getByPara(AnnotationType.WF, 0));
	assertEquals("WFs were not correctly indexed by paragraph when created", para1, naf.getByPara(AnnotationType.WF, 1));
	assertEquals("WFs were not correctly indexed by paragraph when created", para1, naf.getWFsByPara(1));
	assertEquals("WFs were not correctly indexed by paragraph when created", new ArrayList<WF>(), naf.getByPara(AnnotationType.WF, 2));
	assertEquals("WFs were not correctly indexed by paragraph when created", new ArrayList<WF>(), naf.getWFsByPara(2));
	WF wf6 = naf.newWF(21, "The", 4);
	wf6.setPara(2);
	WF wf7 = naf.newWF(25, "car", 4);
	wf7.setPara(2);
	WF wf8 = naf.newWF(29, "is", 4);
	wf8.setPara(2);
	WF wf9 = naf.newWF(32, "blue", 4);
	wf9.setPara(2);
	WF wf10 = naf.newWF(37, ".", 4);
	wf10.setPara(2);
	List<WF> para2 = new ArrayList<WF>(Arrays.asList(wf6, wf7, wf8, wf9, wf10));
	assertEquals("WFs were not correctly indexed by paragraph when created", para2, naf.getByPara(AnnotationType.WF, 2));
	assertEquals("WFs were not correctly indexed by paragraph when created", para2, naf.getWFsByPara(2));
	// Edit sent values
	wf2.setPara(2);
	wf7.setPara(4);
	para1.remove(wf2);
	para2.add(0, wf2);
	para2.remove(wf7);
	List<WF> para4 = new ArrayList<WF>(Arrays.asList(wf7));
	List<WF> para3 = new ArrayList<WF>();
	assertEquals("WFs were not correctly indexed by paragraph when editing paragraph value", para1, naf.getByPara(AnnotationType.WF, 1));
	assertEquals("WFs were not correctly indexed by paragraph when editing paragraph value", para1, naf.getWFsByPara(1));
	assertEquals("WFs were not correctly indexed by paragraph when editing paragraph value", para2, naf.getByPara(AnnotationType.WF, 2));
	assertEquals("WFs were not correctly indexed by paragraph when editing paragraph value", para2, naf.getWFsByPara(2));
	assertEquals("WFs were not correctly indexed by paragraph when editing paragraph value", para3, naf.getByPara(AnnotationType.WF, 3));
	assertEquals("WFs were not correctly indexed by paragraph when editing paragraph value", para3, naf.getWFsByPara(3));
	assertEquals("WFs were not correctly indexed by paragraph when editing paragraph value", para4, naf.getByPara(AnnotationType.WF, 4));
	assertEquals("WFs were not correctly indexed by paragraph when editing paragraph value", para4, naf.getWFsByPara(4));
	// Test 0 paragraph
	WF wf11 = naf.newWF(0, "The", 1);
	wf11.setPara(0);
	WF wf12 = naf.newWF(4, "goods", 1);
	wf12.setPara(0);
	List<WF> para0 = Arrays.asList(wf11, wf12);
	assertEquals("WFs were not correctly indexed by paragraph when paragraph is 0", para0, naf.getByPara(AnnotationType.WF, 0));
	assertEquals("WFs were not correctly indexed by paragraph when paragraph is 0", para0, naf.getWFsByPara(0));
	*/
    }
    
    @Test
    public void testSentenceQueriers() {
	/*
	KAFDocument naf = new KAFDocument("en", "test");
	WF wf1 = naf.newWF(0, "The", 0);
	WF wf2 = naf.newWF(4, "house", 0);
	WF wf3 = naf.newWF(10, "is", 0);
	WF wf4 = naf.newWF(13, "white", 0);
	WF wf5 = naf.newWF(19, ".", 0);
	WF wf6 = naf.newWF(21, "The", 3);
	WF wf7 = naf.newWF(25, "car", 3);
	WF wf8 = naf.newWF(29, "is", 3);
	WF wf9 = naf.newWF(32, "blue", 3);
	WF wf10 = naf.newWF(37, ".", 3);
	List<WF> sent0 = Arrays.asList(wf1, wf2, wf3, wf4, wf5);
	List<WF> sent3 = Arrays.asList(wf6, wf7, wf8, wf9, wf10);
	List<List<WF>> sentences = Arrays.asList(sent0, sent3);
	// getSentences()
	List<List<WF>> queriedSentences = naf.getSentences();
	assertEquals("KAFDocument::getSentences() does not work", sentences, queriedSentences);
	// getNumSentences()
	assertEquals("KAFDocument::getNumSentences() does not return the correct number of sentences", new Integer(2), naf.getNumSentences());
	wf4.setSent(2);
	wf9.setSent(4);
	assertEquals("KAFDocument::getNumSentences() does not return the correct number of sentences", new Integer(4), naf.getNumSentences());
	wf6.setSent(4);
	wf7.setSent(4);
	wf8.setSent(4);
	wf10.setSent(4);	
	assertEquals("KAFDocument::getNumSentences() does not return the correct number of sentences", new Integer(3), naf.getNumSentences());
	// getFirstSentence()
	assertEquals("KAFDocument::getFirstSentence() does not return the first sentence number", new Integer(0), naf.getFirstSentence());
	wf1.setSent(2);
	wf2.setSent(2);
	wf3.setSent(2);
	wf5.setSent(1);
	assertEquals("KAFDocument::getFirstSentence() does not return the first sentence number", new Integer(1), naf.getFirstSentence());
	*/
    }
    
    @Test
    public void testParagraphQueriers() {
	/*
	KAFDocument naf = new KAFDocument("en", "test");
	WF wf1 = naf.newWF(0, "The", 1);
	wf1.setPara(0);
	WF wf2 = naf.newWF(4, "house", 1);
	wf2.setPara(0);
	WF wf3 = naf.newWF(10, "is", 1);
	wf3.setPara(0);
	WF wf4 = naf.newWF(13, "white", 1);
	wf4.setPara(0);
	WF wf5 = naf.newWF(19, ".", 1);
	wf5.setPara(0);
	WF wf6 = naf.newWF(21, "The", 5);
	wf6.setPara(3);
	WF wf7 = naf.newWF(25, "car", 5);
	wf7.setPara(3);
	WF wf8 = naf.newWF(29, "is", 5);
	wf8.setPara(3);
	WF wf9 = naf.newWF(32, "blue", 5);
	wf9.setPara(3);
	WF wf10 = naf.newWF(37, ".", 5);
	wf10.setPara(3);
	List<WF> para0 = Arrays.asList(wf1, wf2, wf3, wf4, wf5);
	List<WF> para3 = Arrays.asList(wf6, wf7, wf8, wf9, wf10);
	List<List<WF>> paragraphs = Arrays.asList(para0, para3);
	// getParagraphs()
	List<List<WF>> queriedParagraphs= naf.getParagraphs();
	assertEquals("KAFDocument::getParagraphs() does not work", paragraphs, queriedParagraphs);
	// getNumParagraphs()
	assertEquals("KAFDocument::getNumParagraphs() does not return the correct number of paragraphs", new Integer(2), naf.getNumParagraphs());
	wf4.setPara(2);
	wf9.setPara(4);
	assertEquals("KAFDocument::getNumParagraphs() does not return the correct number of paragraphs", new Integer(4), naf.getNumParagraphs());
	wf6.setPara(4);
	wf7.setPara(4);
	wf8.setPara(4);
	wf10.setPara(4);	
	assertEquals("KAFDocument::getNumParagraphs() does not return the correct number of paragraphs", new Integer(3), naf.getNumParagraphs());
	// getFirstParagraph()
	assertEquals("KAFDocument::getFirstParagraph() does not return the first paragraph number", new Integer(0), naf.getFirstParagraph());
	wf1.setPara(2);
	wf2.setPara(2);
	wf3.setPara(2);
	wf5.setPara(1);
	assertEquals("KAFDocument::getFirstParagraph() does not return the first paragraph number", new Integer(1), naf.getFirstParagraph());
	*/
    }
    
    @Test
    public void testGetSentsByPara() {
	/*
	KAFDocument naf = new KAFDocument("en", "test");
	assertEquals("Sentences were not correctly indexed by paragraphs", new ArrayList<Integer>(), naf.getSentsByParagraph(0));
	assertEquals("Sentences were not correctly indexed by paragraphs", new ArrayList<Integer>(), naf.getSentsByParagraph(1));
	naf.newWF(0, "a", 0).setPara(0);
	assertEquals("Sentences were not correctly indexed by paragraphs", Arrays.asList(0), naf.getSentsByParagraph(0));	
	naf.newWF(0, "b", 0).setPara(0);
	naf.newWF(0, "c", 1).setPara(0);
	assertEquals("Sentences were not correctly indexed by paragraphs", Arrays.asList(0, 1), naf.getSentsByParagraph(0));
	naf.newWF(0, "d", 2).setPara(1);
	naf.newWF(0, "e", 2).setPara(1);
	assertEquals("Sentences were not correctly indexed by paragraphs", Arrays.asList(2), naf.getSentsByParagraph(1));
	naf.newWF(0, "f", 5).setPara(3);
	assertEquals("Sentences were not correctly indexed by paragraphs", Arrays.asList(5), naf.getSentsByParagraph(3));
	naf.newWF(0, "g", 3).setPara(3);
	assertEquals("Sentences were not correctly indexed by paragraphs", Arrays.asList(3, 5), naf.getSentsByParagraph(3));
	assertEquals("Sentences were not correctly indexed by paragraphs", new ArrayList<Integer>(), naf.getSentsByParagraph(2));
	assertEquals("Sentences were not correctly indexed by paragraphs", new ArrayList<Integer>(), naf.getSentsByParagraph(4));
	*/
    }

}

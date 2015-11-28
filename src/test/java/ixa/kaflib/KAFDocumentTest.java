package ixa.kaflib;

import static org.junit.Assert.*;
import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Layer;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;


public class KAFDocumentTest {
    
    private final static String NAF_PATH = "src/test/resources/naf_example.xml";


    @Test
    public void testConstructor() {
	KAFDocument naf = new KAFDocument("en", "test");
	this.testLangVersionHelper(naf, "en", "test");
    }
    
    
    @Test
    public void testCreateFromFile() {
	File nafFile = new File(NAF_PATH);
	try {
	    KAFDocument naf = KAFDocument.createFromFile(nafFile);
	    this.testLangVersionHelper(naf, "en", "test");
	} catch(Exception e) {
	    e.printStackTrace();
	    fail("Error creating NAF from file \"naf_example.xml\": " + e.getMessage());
	}
    }
    
    @Test
    public void testCreateFromStream() {
	try {
	    Reader nafStream = new FileReader(NAF_PATH);
	    KAFDocument naf = KAFDocument.createFromStream(nafStream);
	    this.testLangVersionHelper(naf, "en", "test");
	} catch(Exception e) {
	    e.printStackTrace();
	    fail("Error creating NAF from file \"naf_example.xml\": " + e.getMessage());
	}
    }
    
    @Test
    public void testLangVersion() {
	KAFDocument naf = new KAFDocument("en", "test");
	naf.setLang("es");
	naf.setVersion("test_v2");
	this.testLangVersionHelper(naf, "es", "test_v2");
    }
    
    @Test
    public void testFileDesc() {
	KAFDocument naf = createDoc();
	assertNull("NAF document's fileDesc was not null before creating one", naf.getFileDesc());
	KAFDocument.FileDesc fd = naf.createFileDesc();
	assertNotNull("NAF document's fileDesc was null after creating one", naf.getFileDesc());
    }
    
    @Test
    public void testPublic() {
	KAFDocument naf = createDoc();
	assertNull("NAF document's 'public' was not null before creating one", naf.getPublic());
	KAFDocument.Public pb = naf.createPublic();
	assertNotNull("NAF document's 'public' was null after creating one", naf.getPublic());	
    }
    
    @Test
    public void testLinguisticProcessor() {
	// Initial status
	KAFDocument naf = createDoc();
	List<KAFDocument.LinguisticProcessor> lpList = new ArrayList<KAFDocument.LinguisticProcessor>();
	Map<String, List<KAFDocument.LinguisticProcessor>> lpMap = new LinkedHashMap<String, List<KAFDocument.LinguisticProcessor>>();
	assertEquals("NAF document contains LinguisticProcessors before creating them explicitly", lpList, naf.getLinguisticProcessorList());
	assertEquals("NAF document contains LinguisticProcessors before creating them explicitly", lpMap, naf.getLinguisticProcessors());
	// Add LP1
	KAFDocument.LinguisticProcessor lp1 = naf.addLinguisticProcessor("text", "test_tokenizer");
	lpList.add(lp1);
	List<KAFDocument.LinguisticProcessor> lpListText = new ArrayList<KAFDocument.LinguisticProcessor>();
	lpListText.add(lp1);
	lpMap.put("text", lpListText);
	assertEquals("LinguisticProcessor lp1 was not correctly added to the document", lpList, naf.getLinguisticProcessorList());
	assertEquals("LinguisticProcessor lp1 was not correctly added to the document", lpMap, naf.getLinguisticProcessors());
	// Add LP2
	KAFDocument.LinguisticProcessor lp2 = naf.addLinguisticProcessor("terms", "test_pos_tagger");
	lpList.add(lp2);
	List<KAFDocument.LinguisticProcessor> lpListTerms = new ArrayList<KAFDocument.LinguisticProcessor>();
	lpListTerms.add(lp2);
	lpMap.put("terms", lpListTerms);
	assertEquals("LinguisticProcessor lp2 was not correctly added to the document", lpList, naf.getLinguisticProcessorList());
	assertEquals("LinguisticProcessor lp2 was not correctly added to the document", lpMap, naf.getLinguisticProcessors());
	// Add LP3
	KAFDocument.LinguisticProcessor lp3 = naf.addLinguisticProcessor("text", "test_tokenizer_2");
	lpList.add(1, lp3);
	lpListText.add(lp3);
	lpMap.put("text", lpListText);
	assertEquals("LinguisticProcessor lp2 was not correctly added to the document", lpList, naf.getLinguisticProcessorList());
	assertEquals("LinguisticProcessor lp2 was not correctly added to the document", lpMap, naf.getLinguisticProcessors());
	// Add many preexisting LPs to a new NAF document
	KAFDocument naf2 = createDoc();
	naf2.addLinguisticProcessors(lpMap);
	assertEquals("LPs in doc1 and doc2 are different after calling doc2.addLinguisticProcessors(doc1LPs)", naf.getLinguisticProcessors(), naf2.getLinguisticProcessors());
	// Add many preexisting LPs to a NAF document which already contains some of the mentioned LPs
	KAFDocument naf3 = createDoc();
	naf3.addLinguisticProcessor("text", "test_tokenizer_2");
	naf3.addLinguisticProcessors(lpMap);
	assertEquals("LPs in doc1 and doc2 are different after calling doc2.addLinguisticProcessors(doc1LPs). doc2 contained some of the LPs in doc1.", naf.getLinguisticProcessors(), naf2.getLinguisticProcessors());
	// Check the hostname is correctly implicitly added
	KAFDocument.LinguisticProcessor lp4 = naf.addLinguisticProcessor("text", "test_tokenizer");
	assertNull("Hostname is not null when creating a LP", lp4.getHostname());
	lp4.setBeginTimestamp();
	assertNotNull("Hostname is null after calling setBeginTimestamp, when it should be implicitly added", lp4.getHostname());	
	// Test LinguisticProcessor.equals(...)
	KAFDocument.LinguisticProcessor lpeq1 = naf.addLinguisticProcessor("deps", "test_dep1");
	KAFDocument.LinguisticProcessor lpeq2 = naf.addLinguisticProcessor("deps", "test_dep2");
	KAFDocument.LinguisticProcessor lpeq3 = naf.addLinguisticProcessor("deps", "test_dep1");
	KAFDocument.LinguisticProcessor lpeq4 = naf.addLinguisticProcessor("entities", "test_dep1");
	assertNotEquals("Two LPs created calling addLinguisticProcessor(layer, name) with different names are equal", lpeq1, lpeq2);
	assertNotEquals("Two LPs created calling addLinguisticProcessor(layer, name) with different layers are equal", lpeq1, lpeq4);
	assertEquals("Two LPs created calling addLinguisticProcessor(layer, name) with the same name are not equal", lpeq1, lpeq3);
	lpeq1.setVersion("test_version");
	assertNotEquals("Two LPs with different version value are equal", lpeq1, lpeq3);
	lpeq3.setVersion("test_version_2");
	assertNotEquals("Two LPs with different version value are equal", lpeq1, lpeq3);
	lpeq3.setVersion("test_version");
	assertEquals("Two LPs with the same version value are not equal", lpeq1, lpeq3);
	lpeq1.setHostname("host1");
	assertEquals("Two LPs with different hostname are not equal, when they should", lpeq1, lpeq3);
	lpeq3.setHostname("host2");
	assertEquals("Two LPs with different hostname are not equal, when they should", lpeq1, lpeq3);
	lpeq3.setHostname("host1");
	assertEquals("Two LPs with equal hostname are not equal", lpeq1, lpeq3);
	lpeq1.setTimestamp("ts1");
	assertEquals("Two LPs with different timestamp are not equal, when they should", lpeq1, lpeq3);
	lpeq3.setTimestamp("ts2");
	assertEquals("Two LPs with different timestamp are not equal, when they should", lpeq1, lpeq3);
	lpeq3.setTimestamp("ts1");
	assertEquals("Two LPs with equal timestamp are not equal", lpeq1, lpeq3);
	lpeq1.setBeginTimestamp("bts1");
	assertEquals("Two LPs with different beginTimestamp are not equal, when they should", lpeq1, lpeq3);
	lpeq3.setBeginTimestamp("bts2");
	assertEquals("Two LPs with different beginTimestamp are not equal, when they should", lpeq1, lpeq3);
	lpeq3.setBeginTimestamp("bts1");
	assertEquals("Two LPs with equal beginTimestamp are not equal", lpeq1, lpeq3);
	lpeq1.setEndTimestamp("ets1");
	assertEquals("Two LPs with different endTimestamp are not equal, when they should", lpeq1, lpeq3);
	lpeq3.setEndTimestamp("ets2");
	assertEquals("Two LPs with different endTimestamp are not equal, when they should", lpeq1, lpeq3);
	lpeq3.setEndTimestamp("ets1");
	assertEquals("Two LPs with equal endTimestamp are not equal", lpeq1, lpeq3);
    }
    
    @Test
    public void testRawText() {
	KAFDocument naf = createDoc();
	naf.setRawText("Testing...");
	assertEquals("Raw text is not correctly added", "Testing...", naf.getRawText());
    }

    @Test
    public void testGetSentence() {
	KAFDocument naf = createDoc();	
	WF wf1 = naf.newWF(19, 3, "The", 2);
	WF wf2 = naf.newWF(23, 9, "president", 2);
	WF wf3 = naf.newWF(33, "is", 2);
	WF wf4 = naf.newWF(36, "black", 2);
	assertEquals("KAFDocument::getSentence() did not return the actual sentence number of the doc (which only contains one sentence)", new Integer(2), naf.getSentence());
	wf1.setSent(3);
	assertEquals("KAFDocument::getSentence() did not return the actual sentence number of the doc (after editing the sentence number of the first WF)", new Integer(2), naf.getSentence());
	wf4.setSent(0);
	assertEquals("KAFDocument::getSentence() did not return the actual sentence number of the doc (after editing sent values of WFs)", new Integer(0), naf.getSentence());
    }
    
    @Test
    public void testGetParagraph() {
	KAFDocument naf = createDoc();	
	WF wf1 = naf.newWF(19, 3, "The", 3);
	wf1.setPara(2);
	WF wf2 = naf.newWF(23, 9, "president", 3);
	wf2.setPara(2);
	WF wf3 = naf.newWF(33, "is", 3);
	wf3.setPara(2);
	WF wf4 = naf.newWF(36, "black", 3);
	wf4.setPara(2);
	assertEquals("KAFDocument::getParagraph() did not return the actual paragraph number of the doc (which only contains one paragraph)", new Integer(2), naf.getParagraph());
	wf1.setPara(3);
	assertEquals("KAFDocument::getParagraph() did not return the actual paragraph number of the doc (after editing the paragraph number of the first WF)", new Integer(2), naf.getParagraph());
	wf4.setPara(0);
	assertEquals("KAFDocument::getParagraph() did not return the actual paragraph number of the doc (after editing paragraph values of WFs)", new Integer(2), naf.getParagraph());
	wf3.setPara(0);
	assertEquals("KAFDocument::getParagraph() did not return the actual paragraph number of the doc (after editing paragraph values of WFs)", new Integer(0), naf.getParagraph());
    }
    
    @Test
    public void testWFFunctions() {
	/* WF creation */
	KAFDocument naf1 = createDoc();
	WF wf1 = naf1.newWF("w1", 0, 3, "The", 0);
	wf1.setPara(0);
	WF wf2 = naf1.newWF("w2", 4, 5, "house", 0);
	wf2.setPara(0);
	WF wf3 = naf1.newWF("w3", 10, "is", 0);
	wf3.setPara(0);
	WF wf4 = naf1.newWF("w6", 13, "white", 0);
	wf4.setPara(0);
	WF wf5 = naf1.newWF(19, 3, "The", 1);
	wf5.setPara(1);
	WF wf6 = naf1.newWF(23, 9, "president", 1);
	wf6.setPara(1);
	WF wf7 = naf1.newWF(33, "is", 1);
	wf7.setPara(1);
	WF wf8 = naf1.newWF(36, "black", 1);
	wf8.setPara(1);
	WFTest.testWF(wf1, "w1", 0, 3, "The", 0, 0, null, null, "KAFDocument::newWF called");
	WFTest.testWF(wf2, "w2", 4, 5, "house", 0, 0, null, null, "KAFDocument::newWF called");
	WFTest.testWF(wf3, "w3", 10, 2, "is", 0, 0, null, null, "KAFDocument::newWF called");
	WFTest.testWF(wf4, "w6", 13, 5, "white", 0, 0, null, null, "KAFDocument::newWF called");
	WFTest.testWF(wf5, "w7", 19, 3, "The", 1, 1, null, null, "KAFDocument::newWF called");
	WFTest.testWF(wf6, "w8", 23, 9, "president", 1, 1, null, null, "KAFDocument::newWF called");
	WFTest.testWF(wf7, "w9", 33, 2, "is", 1, 1, null, null, "KAFDocument::newWF called");
	WFTest.testWF(wf8, "w10", 36, 5, "black", 1, 1, null, null, "KAFDocument::newWF called");
	/* WF queries */
	List<WF> wfs = Arrays.asList(wf1, wf2, wf3, wf4, wf5, wf6, wf7, wf8);
	List<Annotation> queriedWFs = naf1.getAnnotations(AnnotationType.WF);
	assertEquals("KAFDocument::getAnnotations() did not work correctly with WFs", wfs, queriedWFs);
	List<Annotation> queriedTextLayer = naf1.getLayer(Layer.TEXT);
	assertEquals("KAFDocument::getLayer() did not work correctly with TEXT layer", wfs, queriedTextLayer);
	List<WF> queriedWFs2 = naf1.getWFs();
	assertEquals("KAFDocument::getWFs() did not return all WFs", wfs, queriedWFs2);
	/* Queries by sentence*/
	List<WF> sent1 = Arrays.asList(wf5, wf6, wf7, wf8);
	assertEquals("KAFDocument::getBySent(WF) did not return the correct sent", sent1, naf1.getBySent(AnnotationType.WF, 1));
	assertEquals("KAFDocument::getWFsBySent() did not return the correct sent", sent1, naf1.getWFsBySent(1));
	wf1.setSent(1);
	sent1 = Arrays.asList(wf1, wf5, wf6, wf7, wf8);
	assertEquals("KAFDocument::getBySent(WF) did not return the correct sent", sent1, naf1.getBySent(AnnotationType.WF, 1));
	assertEquals("KAFDocument::getWFsBySent() did not return the correct sent", sent1, naf1.getWFsBySent(1));
	/* Queries by paragraph*/
	List<WF> para0 = Arrays.asList(wf2, wf3, wf4);
	List<WF> para1 = Arrays.asList(wf1, wf5, wf6, wf7, wf8);
	assertEquals("KAFDocument::getByPara(WF) did not return the correct para", para0, naf1.getByPara(AnnotationType.WF, 0));
	assertEquals("KAFDocument::getByPara(WF) did not return the correct para", para1, naf1.getByPara(AnnotationType.WF, 1));
	assertEquals("KAFDocument::getWFsByPara() did not return the correct para", para1, naf1.getWFsByPara(1));
	wf1.setPara(1);
	para1 = Arrays.asList(wf1, wf5, wf6, wf7, wf8);
	assertEquals("KAFDocument::getByPara(WF) did not return the correct para", para1, naf1.getByPara(AnnotationType.WF, 1));
	assertEquals("KAFDocument::getWFsByPara() did not return the correct para", para1, naf1.getWFsByPara(1));
    }
    
    @Test
    public void testTermFunctions() {
	/* Term creation */
	KAFDocument naf = createDoc();
	WF wf1 = naf.newWF("w1", 0, 3, "The", 0);
	WF wf2 = naf.newWF("w2", 4, 5, "house", 0);
	WF wf3 = naf.newWF("w3", 10, "is", 0);
	WF wf4 = naf.newWF("w6", 13, "white", 0);
	WF wf5 = naf.newWF(19, 3, "The", 1);
	WF wf6 = naf.newWF(23, 9, "president", 1);
	WF wf7 = naf.newWF(33, "is", 1);
	WF wf8 = naf.newWF(36, "black", 1);
	Term t1 = naf.newTerm("t1", new Span<WF>(wf1, wf2));
	Term t2 = naf.newTerm(new Span<WF>(wf3));
	Term t3 = naf.newTerm("t3", new Span<WF>(wf4));
	Term t4 = naf.newTerm("t4", new Span<WF>(wf5));
	Term t5 = naf.newTerm(new Span<WF>(wf6));
	Term t6 = naf.newTerm("t8", new Span<WF>(wf7));
	Term t7 = naf.newTerm(new Span<WF>(wf8));
	Compound mw1 = naf.newCompound("t.mw3");
	mw1.addComponent(t4);
	mw1.addComponent(t5);
	Compound mw2 = naf.newCompound();
	mw2.addComponent(t1);
	mw2.addComponent(t2);
	TermTest.testTerm(t1, "t.mw4.1", null, null, null, null, null, null, new Span<WF>(wf1, wf2), new ArrayList<ExternalRef>(), true, mw2, "");
	TermTest.testTerm(t2, "t.mw4.2", null, null, null, null, null, null, new Span<WF>(wf3), new ArrayList<ExternalRef>(), true, mw2, "");
	TermTest.testTerm(t3, "t3", null, null, null, null, null, null, new Span<WF>(wf4), new ArrayList<ExternalRef>(), false, null, "");
	TermTest.testTerm(t4, "t.mw3.1", null, null, null, null, null, null, new Span<WF>(wf5), new ArrayList<ExternalRef>(), true, mw1, "");
	TermTest.testTerm(t5, "t.mw3.2", null, null, null, null, null, null, new Span<WF>(wf6), new ArrayList<ExternalRef>(), true, mw1, "");
	TermTest.testTerm(t6, "t8", null, null, null, null, null, null, new Span<WF>(wf7), new ArrayList<ExternalRef>(), false, null, "");
	TermTest.testTerm(t7, "t9", null, null, null, null, null, null, new Span<WF>(wf8), new ArrayList<ExternalRef>(), false, null, "");
	TermTest.testCompound(mw1, "t.mw3", null, null, null, null, null, null, Arrays.asList(t4, t5), null, new Span<WF>(wf5, wf6), new ArrayList<ExternalRef>(), "");
	TermTest.testCompound(mw2, "t.mw4", null, null, null, null, null, null, Arrays.asList(t1, t2), null, new Span<WF>(wf1, wf2, wf3), new ArrayList<ExternalRef>(), "");
	/* Sentiment creation */
	Term.Sentiment s1 = naf.newSentiment();
	TermTest.testSentiment(s1, null, null, null, null, null, null, null, null, "");
	/* Term queries */
	List<Term> terms = Arrays.asList(t3, t6, t7);
	List<Term> components = Arrays.asList(t1, t2, t4, t5);
	List<Compound> mws = Arrays.asList(mw1, mw2);
	List<Annotation> termsLayer = Arrays.asList((Annotation)mw2, t3, mw1, t6, t7);
	/*
	List<Annotation> queriedTerms = naf.getAnnotations(AnnotationType.TERM);
	assertEquals("KAFDocument::getAnnotations() did not work correctly with Terms", terms, queriedTerms);
	List<Annotation> queriedComponents= naf.getAnnotations(AnnotationType.COMPONENT);
	assertEquals("KAFDocument::getAnnotations() did not work correctly with Components", components, queriedComponents);
	List<Annotation> queriedMWs = naf.getAnnotations(AnnotationType.MW);
	assertEquals("KAFDocument::getAnnotations() did not work correctly with MWs", mws, queriedMWs);
	List<Annotation> queriedTermsLayer = naf.getLayer(Layer.TERMS);
	assertEquals("KAFDocument::getLayer() did not work correctly with TERMS layer", termsLayer, queriedTermsLayer);
	List<Term> queriedTerms2 = naf.getTerms();
	assertEquals("KAFDocument::getTerms() did not return all terms", termsLayer, queriedTerms2);
    	*/
    }
    
    
    static KAFDocument createDoc() {
	String defaultLang = "en";
	String defaultVersion = "test";
	return new KAFDocument(defaultLang, defaultVersion);
    }
    
    private void testLangVersionHelper(KAFDocument naf, String lang, String version) {
	assertEquals("Error comparing NAF document language", lang, naf.getLang());
	assertEquals("Error comparing NAF document version", version, naf.getVersion());
    }

}

package ixa.kaflib;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
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
	KAFDocument naf = this.createDoc();
	assertNull("NAF document's fileDesc was not null before creating one", naf.getFileDesc());
	KAFDocument.FileDesc fd = naf.createFileDesc();
	assertNotNull("NAF document's fileDesc was null after creating one", naf.getFileDesc());
    }
    
    @Test
    public void testPublic() {
	KAFDocument naf = this.createDoc();
	assertNull("NAF document's 'public' was not null before creating one", naf.getPublic());
	KAFDocument.Public pb = naf.createPublic();
	assertNotNull("NAF document's 'public' was null after creating one", naf.getPublic());	
    }
    
    @Test
    public void testLinguisticProcessor() {
	// Initial status
	KAFDocument naf = this.createDoc();
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
	KAFDocument naf2 = this.createDoc();
	naf2.addLinguisticProcessors(lpMap);
	assertEquals("LPs in doc1 and doc2 are different after calling doc2.addLinguisticProcessors(doc1LPs)", naf.getLinguisticProcessors(), naf2.getLinguisticProcessors());
	// Add many preexisting LPs to a NAF document which already contains some of the mentioned LPs
	KAFDocument naf3 = this.createDoc();
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
	KAFDocument naf = this.createDoc();
	naf.setRawText("Testing...");
	assertEquals("Raw text is not correctly added", "Testing...", naf.getRawText());
    }
    
    @Test
    public void testCommonGetters() {
	
    }
    
    
    
    private KAFDocument createDoc() {
	String defaultLang = "en";
	String defaultVersion = "test";
	return new KAFDocument(defaultLang, defaultVersion);
    }
    
    private void testLangVersionHelper(KAFDocument naf, String lang, String version) {
	assertEquals("Error comparing NAF document language", lang, naf.getLang());
	assertEquals("Error comparing NAF document version", version, naf.getVersion());
    }

}

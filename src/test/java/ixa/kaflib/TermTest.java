package ixa.kaflib;

import static org.junit.Assert.*;
import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.Term.Sentiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TermTest {
    
    @Test
    public void testConstructor() {
	AnnotationContainer annCont = new AnnotationContainer();
	Span<WF> span = WFTest.createWFSpan(annCont);
	Term term = new Term(annCont, "t12", span);
	testTerm(term, "t12", null, null, null, null, null, null, new ArrayList<Term>(), null, span, new ArrayList<ExternalRef>(), false, null, "Term created");
    }
    
    @Test
    public void testBasic() {
	AnnotationContainer annCont = new AnnotationContainer();
	Span<WF> span = WFTest.createWFSpan(annCont);
	Span<WF> span2 = WFTest.createWFSpan(annCont);
	Term term = new Term(annCont, "t1", span);
	testTerm(term, "t1", null, null, null, null, null, null, new ArrayList<Term>(), null, span, new ArrayList<ExternalRef>(), false, null, "Term created");
	Term component1 = new Term(annCont, "t2", span);
	testTerm(component1, "t2", null, null, null, null, null, null, new ArrayList<Term>(), null, span, new ArrayList<ExternalRef>(), false, null, "Term created");
	Term component2 = new Term(annCont, "t3", span);
	testTerm(component2, "t3", null, null, null, null, null, null, new ArrayList<Term>(), null, span, new ArrayList<ExternalRef>(), false, null, "Term created");
	Term compound = new Term(annCont, "t.mw4", span2);
	compound.setAnnotationType(AnnotationType.MW);
	testTerm(compound, "t.mw4", null, null, null, null, null, null, new ArrayList<Term>(), null, span2, new ArrayList<ExternalRef>(), false, null, "Compund created");
	List<Term> components = new ArrayList<Term>();
	/* HAS methods */
	assertFalse("should have returned false", term.hasType());
	assertFalse("should have returned false", term.hasLemma());
	assertFalse("should have returned false", term.hasPos());
	assertFalse("should have returned false", term.hasMorphofeat());
	assertFalse("should have returned false", term.hasCase());
	assertFalse("should have returned false", term.hasSentiment());
	assertFalse("should have returned false", term.hasHead());	
	/* SET methods */
	term.setType("open");
	term.setLemma("house");
	term.setPos("V");
	term.setMorphofeat("feat");
	term.setCase("case1");
	Sentiment sentiment = term.createSentiment();
	testTerm(term, "t1", "open", "house", "V", "feat", "case1", sentiment, components, null, span, new ArrayList<ExternalRef>(), false, null, "Term created and several values set");
	term.setSpan(span2);
	testTerm(term, "t1", "open", "house", "V", "feat", "case1", sentiment, components, null, span2, new ArrayList<ExternalRef>(), false, null, "Term created and several values set");
	compound.addComponent(component1);
	components.add(component1);
	compound.addComponent(component2, true);
	components.add(component2);
	testTerm(compound, "t.mw4", null, null, null, null, null, null, components, component2, span2, new ArrayList<ExternalRef>(), false, null, "Components added to compound");
    }
    
    @Test
    public void testComponents() {
	AnnotationContainer annCont = new AnnotationContainer();
	Span<WF> span = WFTest.createWFSpan(annCont);
	Term component1 = new Term(annCont, "t2", span);
	Term component2 = new Term(annCont, "t3", span);
	Term compound = new Term(annCont, "t.mw1", span);
	compound.setAnnotationType(AnnotationType.MW);
	compound.addComponent(component1);
	compound.addComponent(component2, true);
	Term component3 = compound.newComponent("t3.mw", span, false);
	Term component4 = compound.newComponent("t.mw1.6", span, false);
	Term component5 = compound.newComponent(span, false);
	
	assertEquals("t.mw1", compound.getId());
	assertEquals("t.mw1.1", component1.getId());
	assertEquals("t.mw1.2", component2.getId());
	assertEquals("t3.mw", component3.getId());
	assertEquals("t.mw1.6", component4.getId());
	assertEquals("t.mw1.7", component5.getId());
    }
    
    @Test
    public void testExternalReferences() {
	ExternalRef er1 = new ExternalRef("res1", "ref1");
	ExternalRef er2 = new ExternalRef("res2", "ref2");
	AnnotationContainer annCont = new AnnotationContainer();
	Span<WF> span = WFTest.createWFSpan(annCont);
	Term term = new Term(annCont, "t1", span);
	term.getExternalReferences().add(er1);
	term.getExternalReferences().add(er2);
	ExternalReferences extRefs = new ExternalReferences();
	extRefs.add(Arrays.asList(er1, er2));
	assertEquals(extRefs, term.getExternalReferences());
    }
    
    @Test
    public void testGetReferencedAnnotations() {
	AnnotationContainer annCont = new AnnotationContainer();
	WF wf1 = new WF(annCont, "w1", 0, 1, "a", 1);
	WF wf2 = new WF(annCont, "w2", 2, 1, "b", 1);
	WF wf3 = new WF(annCont, "w3", 4, 1, "c", 1);
	Span<WF> span1 = new Span<WF>(wf1, wf2);
	Span<WF> span2 = new Span<WF>(wf3);
	Span<WF> span3 = new Span<WF>(wf1, wf2, wf3);
	Term compound = new Term(annCont, "t.mw1", span3);
	compound.setAnnotationType(AnnotationType.MW);
	Term component1 = new Term(annCont, "t2", span1);
	compound.addComponent(component1, false);
	Term component2 = compound.newComponent(span2, true);
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.WF, (List<Annotation>)(List<?>)span3.getTargets());
	List<Annotation> components = new ArrayList<Annotation>();
	components.add(component1);
	components.add(component2);
	referenced.put(AnnotationType.COMPONENT, components);
	referenced.put(AnnotationType.SENTIMENT, new ArrayList<Annotation>());
	assertEquals("Term object did not return the correct referenced WFs", referenced, compound.getReferencedAnnotations());
    }
    
    @Test
    public void testGetOffset() {
	AnnotationContainer annCont = new AnnotationContainer();
	Span<WF> span = WFTest.createWFSpan(annCont);
	Term term = new Term(annCont, "t1", span);
	assertEquals("Term did not return the correct offset when calling getOffset()", new Integer(0), term.getOffset());
	span.removeTarget(span.getFirstTarget());
	assertEquals("Term did not return the correct offset when calling getOffset()", new Integer(4), term.getOffset());
	WF wf11 = new WF(annCont, "w11", 1, 2, "is", 3);
	span.addTarget(wf11);
	assertEquals("Term did not return the correct offset when calling getOffset()", new Integer(1), term.getOffset());
    }
    
    @Test
    public void testSentPara() {
	AnnotationContainer annCont = new AnnotationContainer();
	Span<WF> span = WFTest.createWFSpan(annCont);
	Term term = new Term(annCont, "t1", span);
	assertEquals("sent value is not correct", new Integer(3), term.getSent());
	assertEquals("para value is not correct", new Integer(2), term.getPara());
	WF wf11 = new WF(annCont, "w11", 15, 4, "test", 0);
	span.addTarget(wf11);
	assertEquals("sent value is not correct after adding a new target", new Integer(3), term.getSent());
	assertEquals("para value is not correct after adding a new target", new Integer(2), term.getPara());
	wf11.setPara(1);
	assertEquals("sent value is not correct after adding a new target", new Integer(3), term.getSent());
	assertEquals("para value is not correct after adding a new target", new Integer(2), term.getPara());
	WF wf1 = span.getFirstTarget();
	wf1.setSent(4);
	wf1.setPara(3);
	assertEquals("sent value is not correct after changing the first target's sent", new Integer(4), term.getSent());
	assertEquals("para value is not correct after changing the first target's para", new Integer(3), term.getPara());
    }
    
    @Test
    public void testToString() {
	AnnotationContainer annCont = new AnnotationContainer();
	Span<WF> span = WFTest.createWFSpan(annCont);
	Term term = new Term(annCont, "t1", span);
	assertEquals("Term did not return the correct string when calling toString()", "The White House", term.toString());
	WF wf11 = new WF(annCont, "w11", 15, 4, "area", 3);
	term.getSpan().addTarget(wf11);
	assertEquals("Term did not return the correct string when calling toString()", "The White House area", term.toString());
    }

    @Test
    public void testSentimentClass() {
	AnnotationContainer annCont = new AnnotationContainer();	
	Sentiment sentiment = new Sentiment(annCont);
	testSentiment(sentiment, null, null, null, null, null, null, null, null, "Sentiment created");
	/* HAS methods */
	assertFalse(sentiment.hasResource());
	assertFalse(sentiment.hasPolarity());
	assertFalse(sentiment.hasStrength());
	assertFalse(sentiment.hasSubjectivity());
	assertFalse(sentiment.hasSentimentSemanticType());
	assertFalse(sentiment.hasSentimentModifier());
	assertFalse(sentiment.hasSentimentMarker());
	assertFalse(sentiment.hasSentimentProductFeature());
	/* SET methods */
	sentiment.setResource("res");
	sentiment.setPolarity("pol");
	sentiment.setStrength("str");
	sentiment.setSubjectivity("sub");
	sentiment.setSentimentSemanticType("sst");
	sentiment.setSentimentModifier("smo");
	sentiment.setSentimentMarker("sma");
	sentiment.setSentimentProductFeature("spf");
	testSentiment(sentiment, "res", "pol", "str", "sub", "sst", "smo", "sma", "spf", "Sentiment values changed by set methods");
	/* HAS methods */
	assertTrue(sentiment.hasResource());
	assertTrue(sentiment.hasPolarity());
	assertTrue(sentiment.hasStrength());
	assertTrue(sentiment.hasSubjectivity());
	assertTrue(sentiment.hasSentimentSemanticType());
	assertTrue(sentiment.hasSentimentModifier());
	assertTrue(sentiment.hasSentimentMarker());
	assertTrue(sentiment.hasSentimentProductFeature());
	/* getReferencedAnnotations() */
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	assertEquals("Term.Sentiment object did return some referenced annotations", referenced, sentiment.getReferencedAnnotations());
	/* getOffset() */
	assertNull("Sentiment offset always should be null", sentiment.getOffset());
	/* toString() */
	assertEquals("Term.Sentiment::toString() error", "res: pol", sentiment.toString());
	sentiment.setResource(null);
	assertEquals("Term.Sentiment::toString() error", "pol", sentiment.toString());
	sentiment.setPolarity(null);
	assertEquals("Term.Sentiment::toString() error", "", sentiment.toString());
    }
    
    @Test
    public void testDeprecatedMethods() {
	ExternalRef er1 = new ExternalRef("res1", "ref1");
	ExternalRef er2 = new ExternalRef("res2", "ref2");
	AnnotationContainer annCont = new AnnotationContainer();
	Span<WF> span = WFTest.createWFSpan(annCont);
	Term term = new Term(annCont, "t1", span);
	term.addExternalRef(er1);
	term.addExternalRefs(Arrays.asList(er2));
	assertEquals(Arrays.asList(er1, er2), term.getExternalRefs());
    }
    
    static void testTerm(Term term, String id, String type, String lemma, String pos, String morphofeat, String termCase, Term.Sentiment sentiment,
	    List<Term> components, Term head, Span<WF> span, List<ExternalRef> externalRefs, Boolean isComponent, Term compound, String msg) {
	assertEquals(msg + ": ids do not match", id, term.getId());
	assertEquals(msg + ": types do not match", type, term.getType());
	assertEquals(msg + ": lemmas do not match", lemma, term.getLemma());
	assertEquals(msg + ": pos values do not match", pos, term.getPos());
	assertEquals(msg + ": morphofeats do not match", morphofeat, term.getMorphofeat());
	assertEquals(msg + ": cases do not match", termCase, term.getCase());
	assertEquals(msg + ": sentiments do not match", sentiment, term.getSentiment());
	assertEquals(msg + ": components do not match", components, term.getComponents());
	assertEquals(msg + ": heads do not match", head, term.getHead());
	assertEquals(msg + ": spans do not match", span, term.getSpan());
	assertEquals(msg + ": externalRefs do not match", externalRefs, term.getExternalRefs());
	assertEquals(msg + ": isComponents do not match", isComponent, term.isComponent());
	assertEquals(msg + ": compounds do not match", compound, term.getCompound());
    }
    
    static void testSentiment(Sentiment sentiment, String resource, String polarity, String strength, String subjectivity, String sentimentSemanticType,
	    String sentimentModifier, String sentimentMarker, String sentimentProductFeature, String msg) {
	assertEquals(msg + ": resources do not match", resource, sentiment.getResource());
	assertEquals(msg + ": polaritys do not match", polarity, sentiment.getPolarity());
	assertEquals(msg + ": strengths do not match", strength, sentiment.getStrength());
	assertEquals(msg + ": subjectivitys do not match", subjectivity, sentiment.getSubjectivity());
	assertEquals(msg + ": sentimentSemanticTypes do not match", sentimentSemanticType, sentiment.getSentimentSemanticType());
	assertEquals(msg + ": sentimentModifiers do not match", sentimentModifier, sentiment.getSentimentModifier());
	assertEquals(msg + ": sentimentMarkers do not match", sentimentMarker, sentiment.getSentimentMarker());
	assertEquals(msg + ": sentimentProductFeatures do not match", sentimentProductFeature, sentiment.getSentimentProductFeature());
    }

}

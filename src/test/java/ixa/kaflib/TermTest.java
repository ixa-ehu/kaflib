package ixa.kaflib;

import static org.junit.Assert.*;
import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.TermBase.Sentiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TermTest {

    @Test
    public void testTermBasic() {
	AnnotationContainer annCont = new AnnotationContainer();
	Span<WF> span = WFTest.createWFSpan(annCont);
	Span<WF> span2 = WFTest.createWFSpan(annCont);
	Term term = new Term(annCont, "t1", span);
	assertEquals("t1", term.getId());
	assertEquals(span, term.getSpan());
	/* SET/HAS/GET methods */
	assertFalse(term.hasType());
	assertFalse(term.hasLemma());
	assertFalse(term.hasPos());
	assertFalse(term.hasMorphofeat());
	assertFalse(term.hasCase());
	assertFalse(term.hasSentiment());
	term.setType("open");
	assertEquals("open", term.getType());
	assertTrue(term.hasType());
	term.setLemma("house");
	assertEquals("house", term.getLemma());
	assertTrue(term.hasLemma());
	term.setPos("V");
	assertEquals("V", term.getPos());
	assertTrue(term.hasPos());
	term.setMorphofeat("feat");
	assertEquals("feat", term.getMorphofeat());
	assertTrue(term.hasMorphofeat());
	term.setCase("case1");
	assertEquals("case1", term.getCase());
	assertTrue(term.hasCase());
	Sentiment sentiment = term.newSentiment();
	assertEquals(sentiment, term.getSentiment());
	assertTrue(term.hasSentiment());
	term.setSpan(span2);
	assertEquals(span2, term.getSpan());
    }
    
    @Test
    public void testCompoundBasic() {
	AnnotationContainer annCont = new AnnotationContainer();
	WF w1 = new WF(annCont, "w1", 0, 1, "a", 1);
	WF w2 = new WF(annCont, "w2", 2, 1, "b", 1);
	WF w3 = new WF(annCont, "w3", 4, 1, "c", 1);
	WF w4 = new WF(annCont, "w4", 6, 1, "d", 1);
	Span<WF> span = new Span<WF>(w1, w2, w3, w4);
	Span<WF> span1 = new Span<WF>(w1);
	Span<WF> span2 = new Span<WF>(w2);
	Span<WF> span3 = new Span<WF>(w3);
	Span<WF> span4 = new Span<WF>(w4);
	Compound compound = new Compound(annCont, "t.mw1");
	assertEquals("t.mw1", compound.getId());
	assertEquals(new Span<WF>(), compound.getSpan());
	/* SET/HAS/GET methods */
	Term component1 = compound.newComponent(span1, false);
	assertFalse(compound.hasHead());
	Term component2 = new Term(annCont, "t7", span2);
	compound.addComponent(component2, true);
	assertTrue(compound.hasHead());
	Term component3 = compound.newComponent("t.mw1.5", span3, true);
	Term component4 = compound.newComponent(span4, false);
	assertArrayEquals(new Term[]{component1, component2, component3, component4}, compound.getComponents().toArray());
	assertEquals(span, compound.getSpan());
	assertEquals(component3, compound.getHead());
	assertEquals("t.mw1.1", component1.getId());
	assertEquals("t.mw1.2", component2.getId());
	assertEquals("t.mw1.5", component3.getId());
	assertEquals("t.mw1.6", component4.getId());
	/* Referenced annotations */
	TermBase.Sentiment sentiment = compound.newSentiment();
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.SENTIMENT, Arrays.asList((Annotation)sentiment));
	referenced.put(AnnotationType.WF, Arrays.asList((Annotation)w1, w2, w3, w4));
	referenced.put(AnnotationType.COMPONENT, Arrays.asList((Annotation)component1, component2, component3, component4));
	assertEquals(referenced, compound.getReferencedAnnotations());
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
    public void testTermGetReferencedAnnotations() {
	AnnotationContainer annCont = new AnnotationContainer();
	WF wf1 = new WF(annCont, "w1", 0, 1, "a", 1);
	WF wf2 = new WF(annCont, "w2", 2, 1, "b", 1);
	Span<WF> span1 = new Span<WF>(wf1, wf2);
	Term term = new Term(annCont, "t1", span1);
	Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	referenced.put(AnnotationType.WF, (List<Annotation>)(List<?>)span1.getTargets());
	referenced.put(AnnotationType.SENTIMENT, new ArrayList<Annotation>());
	assertEquals("Term object did not return the correct referenced WFs", referenced, term.getReferencedAnnotations());
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
	/* Term */
	Span<WF> span = WFTest.createWFSpan(annCont);
	Term term = new Term(annCont, "t1", span);
	assertEquals("Term did not return the correct string when calling toString()", "The White House", term.toString());
	WF wf11 = new WF(annCont, "w11", 15, 4, "area", 3);
	term.getSpan().addTarget(wf11);
	assertEquals("Term did not return the correct string when calling toString()", "The White House area", term.toString());
	/* Compound */
	WF w1 = new WF(annCont, "w1", 0, 1, "a", 1);
	WF w2 = new WF(annCont, "w2", 2, 1, "b", 1);
	Compound compound = new Compound(annCont, "t.mw1");
	Term component1 = compound.newComponent(new Span<WF>(w1), false);
	Term component2 = compound.newComponent(new Span<WF>(w2), false);
	assertEquals("a b", compound.toString());
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
    
    private static void testTermBase(TermBase term, String id, String type, String lemma, String pos, String morphofeat, String termCase, Term.Sentiment sentiment,
	    Span<WF> span, List<ExternalRef> externalRefs, String msg) {
	assertEquals(msg + ": ids do not match", id, term.getId());
	assertEquals(msg + ": types do not match", type, term.getType());
	assertEquals(msg + ": lemmas do not match", lemma, term.getLemma());
	assertEquals(msg + ": pos values do not match", pos, term.getPos());
	assertEquals(msg + ": morphofeats do not match", morphofeat, term.getMorphofeat());
	assertEquals(msg + ": cases do not match", termCase, term.getCase());
	assertEquals(msg + ": sentiments do not match", sentiment, term.getSentiment());
	assertEquals(msg + ": spans do not match", span, term.getSpan());
	assertEquals(msg + ": externalRefs do not match", externalRefs, term.getExternalRefs());
    }
    
    static void testTerm(Term term, String id, String type, String lemma, String pos, String morphofeat, String termCase, Term.Sentiment sentiment,
	    Span<WF> span, List<ExternalRef> externalRefs, Boolean isComponent, Compound compound, String msg) {
	testTermBase(term, id, type, lemma, pos, morphofeat, termCase, sentiment, span, externalRefs, msg);
	assertEquals(msg + ": isComponents do not match", isComponent, term.isComponent());
	assertEquals(msg + ": compounds do not match", compound, term.getCompound());
    }
    
    static void testCompound(Compound compound, String id, String type, String lemma, String pos, String morphofeat, String termCase, Term.Sentiment sentiment,
	    List<Term> components, Term head, Span<WF> span, List<ExternalRef> externalRefs, String msg) {
	testTermBase(compound, id, type, lemma, pos, morphofeat, termCase, sentiment, span, externalRefs, msg);
	assertEquals(msg + ": components do not match", components, compound.getComponents());
	assertEquals(msg + ": heads do not match", head, compound.getHead());
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

package ixa.kaflib;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.CDATA;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.input.SAXBuilder;
import org.jdom2.JDOMException;
import org.jdom2.xpath.XPathExpression;

import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Layer;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.io.File;
import java.io.Writer;
import java.io.Reader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.*;

// xml entity conversion (isanvicente) 
import org.apache.commons.lang3.StringEscapeUtils;

//todo: read facts and linkedEntities

/** Reads XML files in KAF format and loads the content in a KAFDocument object, and writes the content into XML files. */
class ReadWriteManager {
    
    /** Loads the content of a KAF file into the given KAFDocument object */
    static KAFDocument load(File file) throws IOException, JDOMException, KAFNotValidException {
	SAXBuilder builder = new SAXBuilder();
	Document document = (Document) builder.build(file);
	Element rootElem = document.getRootElement();
	return DOMToKAF(document);
    }

    /** Loads the content of a String in KAF format into the given KAFDocument object */
    static KAFDocument load(Reader stream) throws IOException, JDOMException, KAFNotValidException {
	SAXBuilder builder = new SAXBuilder();
	Document document = (Document) builder.build(stream);
	Element rootElem = document.getRootElement();
	return DOMToKAF(document);
    }

    /** Writes the content of a given KAFDocument to a file. */
    static void save(KAFDocument kaf, String filename) {
	try {
	    File file = new File(filename);
	    Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
	    out.write(kafToStr(kaf));
	    out.flush();
	} catch (Exception e) {
	    System.out.println("Error writing to file");
	}
    }

    /** Writes the content of a KAFDocument object to standard output. */
    static void print(KAFDocument kaf) {
	try {
	    Writer out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF8"));
	    out.write(kafToStr(kaf));
	    out.flush();
	} catch (Exception e) {
	    System.out.println(e);
	}
    }

    /** Returns a string containing the XML content of a KAFDocument object. */
    static String kafToStr(KAFDocument kaf) {
	XMLOutputter out = new XMLOutputter(Format.getPrettyFormat().setLineSeparator(LineSeparator.UNIX).setTextMode(Format.TextMode.TRIM_FULL_WHITE));
	Document jdom = KAFToDOM(kaf);
	return out.outputString(jdom);
    }

    /** Loads a KAFDocument object from XML content in DOM format */
    private static KAFDocument DOMToKAF(Document dom) throws KAFNotValidException {
	HashMap<String, WF> wfIndex = new HashMap<String, WF>();
	HashMap<String, Term> termIndex = new HashMap<String, Term>();
	HashMap<String, Relational> relationalIndex = new HashMap<String, Relational>();
	HashMap<String, Predicate> predicateIndex = new HashMap<String, Predicate>();
	HashMap<String, Timex3> timexIndex = new HashMap<String, Timex3>();
	Element rootElem = dom.getRootElement();
	String lang = getAttribute("lang", rootElem, Namespace.XML_NAMESPACE);
	String kafVersion = getAttribute("version", rootElem);
	KAFDocument kaf = new KAFDocument(lang, kafVersion);

	List<Element> rootChildrenElems = rootElem.getChildren();

	Element elem = rootElem.getChild("nafHeader");
	if (elem != null) {
	    List<Element> lpsElems = elem.getChildren("linguisticProcessors");
	    for (Element lpsElem : lpsElems) {
		String layer = getAttribute("layer", lpsElem);
		List<Element> lpElems = lpsElem.getChildren();
		for (Element lpElem : lpElems) {
		    String name = getAttribute("name", lpElem);
		    KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor(layer, name);
		    String timestamp = getOptAttribute("timestamp", lpElem);
		    if (timestamp != null) {
			newLp.setTimestamp(timestamp);
		    }
		    String beginTimestamp = getOptAttribute("beginTimestamp", lpElem);
		    if (beginTimestamp != null) {
			newLp.beginTimestamp = beginTimestamp;
		    }
		    String endTimestamp = getOptAttribute("endTimestamp", lpElem);
		    if (endTimestamp != null) {
			newLp.setEndTimestamp(endTimestamp);
		    }
		    String version = getOptAttribute("version", lpElem);
		    if (version != null) {
			newLp.setVersion(version);
		    }
		    String hostname = getOptAttribute("hostname", lpElem);
		    if (hostname != null) {
			newLp.setHostname(hostname);
		    }
		}
	    }
	    Element fileDescElem = elem.getChild("fileDesc");
	    if (fileDescElem != null) {
		KAFDocument.FileDesc fd = kaf.createFileDesc();
		String author = getOptAttribute("author", fileDescElem);
		if (author != null) {
		    fd.author = author;
		}
		String title = getOptAttribute("title", fileDescElem);
		if (title != null) {
		    fd.title = title;
		}
		String publisher = getOptAttribute("publisher", fileDescElem);
		if (publisher != null) {
		    fd.publisher = publisher;
		}
		String section = getOptAttribute("section", fileDescElem);
		if (section != null) {
		    fd.section = section;
		}
		String location = getOptAttribute("location", fileDescElem);
		if (location != null) {
		    fd.location = location;
		}
		String magazine = getOptAttribute("magazine", fileDescElem);
		if (magazine != null) {
		    fd.magazine = magazine;
		}
		String filename = getOptAttribute("filename", fileDescElem);
		if (filename != null) {
		    fd.filename = filename;
		}
		String filetype = getOptAttribute("filetype", fileDescElem);
		if (filetype != null) {
		    fd.filetype = filetype;
		}
		String pages = getOptAttribute("pages", fileDescElem);
		if (pages != null) {
		    fd.pages = Integer.parseInt(pages);
		}
		String creationtime = getOptAttribute("creationtime", fileDescElem);
		if (creationtime != null) {
		    fd.creationtime = creationtime;
		}
	    }
	    Element publicElem = elem.getChild("public");
	    if (publicElem != null) {
		KAFDocument.Public pub = kaf.createPublic();
		String publicId = getOptAttribute("publicId", publicElem);
		if (publicId != null) {
		    pub.publicId = publicId;
		}
		String uri = getOptAttribute("uri", publicElem);
		if (uri != null) {
		    pub.uri = uri;
		}
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("raw");
	if (elem != null) {
	    kaf.setRawText(elem.getText());
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("text");
	if (elem != null) {
	    List<Element> wfElems = elem.getChildren();
	    for (Element wfElem : wfElems) {
		String wid = getAttribute("id", wfElem);
		String wOffset = getAttribute("offset", wfElem);
		String wLength = getAttribute("length", wfElem);
		String wForm = wfElem.getText();
		String wSent = getAttribute("sent", wfElem);
		WF newWf = kaf.newWF(wid, Integer.valueOf(wOffset), Integer.valueOf(wLength), wForm, Integer.valueOf(wSent));
		String wPara = getOptAttribute("para", wfElem);
		if (wPara != null) {
		    newWf.setPara(Integer.valueOf(wPara));
		}
		String wPage = getOptAttribute("page", wfElem);
		if (wPage != null) {
		    newWf.setPage(Integer.valueOf(wPage));
		}
		String wXpath = getOptAttribute("xpath", wfElem);
		if (wXpath != null) {
		    newWf.setXpath(wXpath);
		}
		wfIndex.put(newWf.getId(), newWf);
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("terms");
	if (elem != null) {
	    List<Element> termElems = elem.getChildren();
	    for (Element termElem : termElems) {
		DOMToTerm(termElem, kaf, false, wfIndex, termIndex, null);
	    }
	    rootChildrenElems.remove(elem);
	}

	elem = rootElem.getChild("entities");
	if (elem != null) {
	    List<Element> entityElems = elem.getChildren();
	    for (Element entityElem : entityElems) {
		String entId = getAttribute("id", entityElem);
		List<Element> referencesElem = entityElem.getChildren("references");
		if (referencesElem.size() < 1) {
		    throw new IllegalStateException("Every entity must contain a 'references' element");
		}
		List<Element> spanElems = referencesElem.get(0).getChildren();
		if (spanElems.size() < 1) {
		    throw new IllegalStateException("Every entity must contain a 'span' element inside 'references'");
		}
		List<Span<Term>> references = new ArrayList<Span<Term>>();
		for (Element spanElem : spanElems) {
		    Span<Term> span = kaf.newTermSpan();
		    List<Element> targetElems = spanElem.getChildren();
		    if (targetElems.size() < 1) {
			throw new IllegalStateException("Every span in an entity must contain at least one target inside");  
		    }
		    for (Element targetElem : targetElems) {
			String targetTermId = getAttribute("id", targetElem);
			Term targetTerm = termIndex.get(targetTermId);
			if (targetTerm == null) {
			    throw new KAFNotValidException("Term " + targetTermId + " not found when loading entity " + entId);
			}
			boolean isHead = isHead(targetElem);
			span.addTarget(targetTerm, isHead);
		    }
		    references.add(span);
		}
		Entity newEntity = kaf.newEntity(entId, references);
		String entType = getOptAttribute("type", entityElem);
		if (entType != null) {
		    newEntity.setType(entType);
		}
		String entSource= getOptAttribute("source", entityElem);
		if (entSource != null) {
		    newEntity.setSource(entSource);
		}
		List<Element> externalReferencesElems = entityElem.getChildren("externalReferences");
		if (externalReferencesElems.size() > 0) {
		    List<ExternalRef> externalRefs = getExternalReferences(externalReferencesElems.get(0), kaf);
		    newEntity.addExternalRefs(externalRefs);
		}
		relationalIndex.put(newEntity.getId(), newEntity);
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("constituency");
	if (elem != null) {
	    List<Element> treeElems = elem.getChildren("tree");
	    for (Element treeElem : treeElems) {
		String type = getOptAttribute("type", treeElem);
		HashMap<String, TreeNode> treeNodes = new HashMap<String, TreeNode>();
		HashMap<String, Boolean> rootNodes = new HashMap<String, Boolean>();
		// Terminals
		List<Element> terminalElems = treeElem.getChildren("t");
		for (Element terminalElem : terminalElems) {
		    String id = getAttribute("id", terminalElem);
		    Element spanElem = terminalElem.getChild("span");
		    if (spanElem == null) {
			throw new KAFNotValidException("Constituent non terminal nodes need a span");
		    }
		    Span<Term> span = loadTermSpan(spanElem, termIndex, id);
		    treeNodes.put(id, kaf.newTerminal(id, span));
		    rootNodes.put(id, true);
		}
		// NonTerminals
		List<Element> nonTerminalElems = treeElem.getChildren("nt");
		for (Element nonTerminalElem : nonTerminalElems) {
		    String id = getAttribute("id", nonTerminalElem);
		    String label = getAttribute("label", nonTerminalElem);
		    treeNodes.put(id, kaf.newNonTerminal(id, label));
		    rootNodes.put(id, true);
		}
		// Edges
		List<Element> edgeElems = treeElem.getChildren("edge");
		for (Element edgeElem : edgeElems) {
		    String fromId = getAttribute("from", edgeElem);
		    String toId = getAttribute("to", edgeElem);
		    String edgeId = getOptAttribute("id", edgeElem);
		    String head = getOptAttribute("head", edgeElem);
		    boolean isHead = (head != null && head.equals("yes")) ? true : false;
		    TreeNode parentNode = treeNodes.get(toId);
		    TreeNode childNode = treeNodes.get(fromId);
		    if ((parentNode == null) || (childNode == null)) {
			throw new KAFNotValidException("There is a problem with the edge(" + fromId + ", " + toId + "). One of its targets doesn't exist.");
		    }
		    try {
			((NonTerminal) parentNode).addChild(childNode);
		    } catch(Exception e) {}
		    rootNodes.put(fromId, false);
		    if (edgeId != null) {
			childNode.setEdgeId(edgeId);
		    }
		    if (isHead) {
			((NonTerminal) childNode).setHead(isHead);
		    }
		}
		// Constituent objects
		for (Map.Entry<String, Boolean> areRoot : rootNodes.entrySet()) {
		    if (areRoot.getValue()) {
			TreeNode rootNode = treeNodes.get(areRoot.getKey());
			if (type == null) {
			    kaf.newConstituent(rootNode);
			} else {
			    kaf.newConstituent(rootNode, type);
			}
		    }
		}
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("chunks");
	if (elem != null) {
	    //System.out.println("chunks");
	    List<Element> chunkElems = elem.getChildren();
	    for (Element chunkElem : chunkElems) {
		String chunkId = getAttribute("id", chunkElem);
		String headId = getAttribute("head", chunkElem);
		Term chunkHead = termIndex.get(headId);
		if (chunkHead == null) {
		    throw new KAFNotValidException("Term " + headId + " not found when loading chunk " + chunkId);
		}
		Element spanElem = chunkElem.getChild("span");
		if (spanElem == null) {
		    throw new IllegalStateException("Every chunk must contain a span element");
		}
		List<Element> chunksTermElems = spanElem.getChildren("target");
		Span<Term> span = kaf.newTermSpan();
		for (Element chunksTermElem : chunksTermElems) {
		    String termId = getAttribute("id", chunksTermElem);
		    boolean isHead = isHead(chunksTermElem);
		    Term targetTerm = termIndex.get(termId);
		    if (targetTerm == null) {
			throw new KAFNotValidException("Term " + termId + " not found when loading chunk " + chunkId);
		    }
		    span.addTarget(targetTerm, ((targetTerm == chunkHead) || isHead));
		}
		if (!span.hasTarget(chunkHead)) {
		    throw new KAFNotValidException("The head of the chunk is not in it's span.");
		}
		Chunk newChunk = kaf.newChunk(chunkId, span);
		String chunkPhrase = getOptAttribute("phrase", chunkElem);
		if (chunkPhrase != null) {
		    newChunk.setPhrase(chunkPhrase);
		}
		String chunkCase = getOptAttribute("case", chunkElem);
		if (chunkCase != null) {
		    newChunk.setCase(chunkCase);
		}
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("coreferences");
	if (elem != null) {
	    List<Element> corefElems = elem.getChildren();
	    for (Element corefElem : corefElems) {
		String coId = getAttribute("id", corefElem);
		List<Element> spanElems = corefElem.getChildren("span");
		if (spanElems.size() < 1) {
		    throw new IllegalStateException("Every coref must contain a 'span' element inside 'references'");
		}
		List<Span<Term>> mentions = new ArrayList<Span<Term>>();
		for (Element spanElem : spanElems) {
		    Span<Term> span = kaf.newTermSpan();
		    List<Element> targetElems = spanElem.getChildren();
		    if (targetElems.size() < 1) {
			throw new IllegalStateException("Every span in an entity must contain at least one target inside");  
		    }
		    for (Element targetElem : targetElems) {
			String targetTermId = getAttribute("id", targetElem);
			Term targetTerm = termIndex.get(targetTermId);
			if (targetTerm == null) {
			    throw new KAFNotValidException("Term " + targetTermId + " not found when loading coref " + coId);
			}
			boolean isHead = isHead(targetElem);
			span.addTarget(targetTerm, isHead);
		    }
		    mentions.add(span);
		}
		Coref newCoref = kaf.newCoref(coId, mentions);
		String corefType = getOptAttribute("type", corefElem);
		if (corefType != null) {
		    newCoref.setType(corefType);
		}
		List<Element> externalReferencesElems = corefElem.getChildren("externalReferences");
		if (externalReferencesElems.size() > 0) {
		    List<ExternalRef> externalRefs = getExternalReferences(externalReferencesElems.get(0), kaf);
		    newCoref.addExternalRefs(externalRefs);
		}
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("opinions");
	if (elem != null) {	    List<Element> opinionElems = elem.getChildren("opinion");
	    for (Element opinionElem : opinionElems) {
		String opinionId = getAttribute("id", opinionElem);
		Opinion opinion = kaf.newOpinion(opinionId);
		Element opinionHolderElem = opinionElem.getChild("opinion_holder");
		if (opinionHolderElem != null) {
		    Span<Term> span = kaf.newTermSpan();
		    Opinion.OpinionHolder opinionHolder = opinion.createOpinionHolder(span);
		    String ohType = getOptAttribute("type", opinionHolderElem);
		    if (ohType != null) {
			opinionHolder.setType(ohType);
		    }
		    Element spanElem = opinionHolderElem.getChild("span");
		    if (spanElem != null) {
			List<Element> targetElems = spanElem.getChildren("target");
			for (Element targetElem : targetElems) {
			    String refId = getOptAttribute("id", targetElem);
			    boolean isHead = isHead(targetElem);
			    Term targetTerm = termIndex.get(refId);
			    if (targetTerm == null) {
				throw new KAFNotValidException("Term " + refId + " not found when loading opinion " + opinionId);
			    }
			    span.addTarget(targetTerm, isHead);
			}
		    }
		}
		Element opinionTargetElem = opinionElem.getChild("opinion_target");
		if (opinionTargetElem != null) {
		    Span<Term> span = kaf.newTermSpan();
		    Opinion.OpinionTarget opinionTarget = opinion.createOpinionTarget(span);
		    Element spanElem = opinionTargetElem.getChild("span");
		    if (spanElem != null) {
			List<Element> targetElems = spanElem.getChildren("target");
			for (Element targetElem : targetElems) {
			    String refId = getOptAttribute("id", targetElem);
			    boolean isHead = isHead(targetElem);
			    Term targetTerm = termIndex.get(refId);
			    if (targetTerm == null) {
				throw new KAFNotValidException("Term " + refId + " not found when loading opinion " + opinionId);
			    }
			    span.addTarget(targetTerm, isHead);
			}
		    }
		}
		Element opinionExpressionElem = opinionElem.getChild("opinion_expression");
		if (opinionExpressionElem != null) {
		    Span<Term> span = kaf.newTermSpan();
		    String polarity = getOptAttribute("polarity", opinionExpressionElem);
		    String strength = getOptAttribute("strength", opinionExpressionElem);
		    String subjectivity = getOptAttribute("subjectivity", opinionExpressionElem);
		    String sentimentSemanticType = getOptAttribute("sentiment_semantic_type", opinionExpressionElem);
		    String sentimentProductFeature = getOptAttribute("sentiment_product_feature", opinionExpressionElem);
		    Opinion.OpinionExpression opinionExpression = opinion.createOpinionExpression(span);
		    if (polarity != null) {
			opinionExpression.setPolarity(polarity);
		    }
		    if (strength != null) {
			opinionExpression.setStrength(strength);
		    }
		    if (subjectivity != null) {
			opinionExpression.setSubjectivity(subjectivity);
		    }
		    if (sentimentSemanticType != null) {
			opinionExpression.setSentimentSemanticType(sentimentSemanticType);
		    }
		    if (sentimentProductFeature != null) {
			opinionExpression.setSentimentProductFeature(sentimentProductFeature);
		    }

		    Element spanElem = opinionExpressionElem.getChild("span");
		    if (spanElem != null) {
			List<Element> targetElems = spanElem.getChildren("target");
			for (Element targetElem : targetElems) {
			    String refId = getOptAttribute("id", targetElem);
			    boolean isHead = isHead(targetElem);
			    Term targetTerm = termIndex.get(refId);
			    if (targetTerm == null) {
				throw new KAFNotValidException("Term " + refId + " not found when loading opinion " + opinionId);
			    }
			    span.addTarget(targetTerm, isHead);
			}
		    }
		}
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("deps");
	if (elem != null) {
	    List<Element> depElems = elem.getChildren();
	    for (Element depElem : depElems) {
		String fromId = getAttribute("from", depElem);
		String toId = getAttribute("to", depElem);
		Term from = termIndex.get(fromId);
		if (from == null) {
		    throw new KAFNotValidException("Term " + fromId + " not found when loading Dep (" + fromId + ", " + toId + ")");
		}
		Term to = termIndex.get(toId);
		if (to == null) {
		    throw new KAFNotValidException("Term " + toId + " not found when loading Dep (" + fromId + ", " + toId + ")");
		}
		String rfunc = getAttribute("rfunc", depElem);
		Dep newDep = kaf.newDep(from, to, rfunc);
		String depcase = getOptAttribute("case", depElem);
		if (depcase != null) {
		    newDep.setCase(depcase);
		}
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("srl");
	if (elem != null) {
	    List<Element> predicateElems = elem.getChildren("predicate");
	    for (Element predicateElem : predicateElems) {
		String id = getAttribute("id", predicateElem);
		Span<Term> span = kaf.newTermSpan();
		Element spanElem = predicateElem.getChild("span");
		if (spanElem != null) {
		    List<Element> targetElems = spanElem.getChildren("target");
		    for (Element targetElem : targetElems) {
			String targetId = getAttribute("id", targetElem);
			boolean isHead = isHead(targetElem);
			Term targetTerm = termIndex.get(targetId);
			if (targetTerm == null) {
			    throw new KAFNotValidException("Term object " + targetId + " not found when loading predicate " + id);				
			}
			span.addTarget(targetTerm, isHead);
		    }
		}
		List<String> predTypes = new ArrayList<String>();
		List<Element> predTypeElems = predicateElem.getChildren("predType");
		for (Element predTypeElem : predTypeElems) {
		    String ptUri = getAttribute("uri", predTypeElem);
		    predTypes.add(ptUri);
		}
		Predicate newPredicate = kaf.newPredicate(id, span);
		String uri = getOptAttribute("uri", predicateElem);
		if (uri != null) {
		    newPredicate.setUri(uri);
		}
		List<Element> externalReferencesElems = predicateElem.getChildren("externalReferences");
		if (externalReferencesElems.size() > 0) {
		    List<ExternalRef> externalRefs = getExternalReferences(externalReferencesElems.get(0), kaf);
		    newPredicate.addExternalRefs(externalRefs);
		}
		String confidence = getOptAttribute("confidence", predicateElem);
		if (confidence != null) {
		    newPredicate.setConfidence(Float.valueOf(confidence));
		}
		List<Element> roleElems = predicateElem.getChildren("role");
		for (Element roleElem : roleElems) {
		    String rid = getAttribute("id", roleElem);
		    String semRole = getAttribute("semRole", roleElem);
		    Span<Term> roleSpan = kaf.newTermSpan();
		    Element roleSpanElem = roleElem.getChild("span");
		    if (roleSpanElem != null) {
			List<Element> targetElems = roleSpanElem.getChildren("target");
			for (Element targetElem : targetElems) {
			    String targetId = getAttribute("id", targetElem);
			    boolean isHead = isHead(targetElem);
			    Term targetTerm = termIndex.get(targetId);
			    if (targetTerm == null) {
				throw new KAFNotValidException("Term object " + targetId + " not found when loading role " + rid);				
			    }
			    roleSpan.addTarget(targetTerm, isHead);
			}
		    }
		    Predicate.Role newRole = kaf.newRole(rid, newPredicate, semRole, roleSpan);
		    List<Element> rExternalReferencesElems = roleElem.getChildren("externalReferences");
		    if (rExternalReferencesElems.size() > 0) {
			List<ExternalRef> externalRefs = getExternalReferences(rExternalReferencesElems.get(0), kaf);
			newRole.addExternalRefs(externalRefs);
		    }
		    newPredicate.addRole(newRole);
		}
		predicateIndex.put(newPredicate.getId(), newPredicate);
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("timeExpressions");
	if (elem != null) {
	    List<Element> timex3Elems = elem.getChildren();
	    Map<Timex3, String> uncompleteBeginPointRefs = new HashMap<Timex3, String>();
	    Map<Timex3, String> uncompleteEndPointRefs = new HashMap<Timex3, String>();
	    for (Element timex3Elem : timex3Elems) {
		String timex3Id = getAttribute("id", timex3Elem);
		String timex3Type = getAttribute("type", timex3Elem);
		Timex3 timex3 = kaf.newTimex3(timex3Id, timex3Type);
		String timex3BeginPointId = getOptAttribute("beginPoint", timex3Elem);
		if (timex3BeginPointId != null) {
		    Timex3 beginPoint = timexIndex.get(timex3BeginPointId);
		    if (beginPoint == null) {
			uncompleteBeginPointRefs.put(timex3, timex3BeginPointId);
		    } else {
			timex3.setBeginPoint(beginPoint);
		    }
		}
		String timex3EndPointId = getOptAttribute("endPoint", timex3Elem);
		if (timex3EndPointId != null) {
		    Timex3 endPoint = timexIndex.get(timex3EndPointId);
		    if (endPoint == null) {
			uncompleteEndPointRefs.put(timex3, timex3EndPointId);
		    } else {
			timex3.setEndPoint(endPoint);
		    }
		}
		String timex3Quant = getOptAttribute("quant", timex3Elem);
		if (timex3Quant != null) {
		    timex3.setQuant(timex3Quant);
		}
		String timex3Freq = getOptAttribute("freq", timex3Elem);
		if (timex3Freq != null) {
		    timex3.setFreq(timex3Freq);
		}
		String timex3FuncInDoc = getOptAttribute("functionInDocument", timex3Elem);
		if (timex3FuncInDoc != null) {
		    timex3.setFunctionInDocument(timex3FuncInDoc);
		}
		String timex3TempFunc = getOptAttribute("temporalFunction", timex3Elem);
		if (timex3TempFunc != null) {
		    Boolean tempFunc = timex3TempFunc.equals("true");
		    timex3.setTemporalFunction(tempFunc);
		}
		String timex3Value = getOptAttribute("value", timex3Elem);
		if (timex3Value != null) {
		    timex3.setValue(timex3Value);
		}
		String timex3ValueFromFunction = getOptAttribute("valueFromFunction", timex3Elem);
		if (timex3ValueFromFunction != null) {
		    timex3.setValueFromFunction(timex3ValueFromFunction);
		}
		String timex3Mod = getOptAttribute("mod", timex3Elem);
		if (timex3Mod != null) {
		    timex3.setMod(timex3Mod);
		}
		String timex3AnchorTimeId = getOptAttribute("anchorTimeId", timex3Elem);
		if (timex3AnchorTimeId != null) {
		    timex3.setAnchorTimeId(timex3AnchorTimeId);
		}
		String timex3Comment = getOptAttribute("comment", timex3Elem);
		if (timex3Comment != null) {
		    timex3.setComment(timex3Comment);
		}
		Element spanElem = timex3Elem.getChild("span");
		if (spanElem != null) {
		    Span<WF> timex3Span = kaf.newWFSpan();
		    for (Element targetElem : spanElem.getChildren("target")) {
			String targetId = getAttribute("id", targetElem);
			WF wf = wfIndex.get(targetId);
			if (wf == null) {
			    throw new KAFNotValidException("Word form " + targetId + " not found when loading timex3 " + timex3Id);
			}
			boolean isHead = isHead(targetElem);
			timex3Span.addTarget(wf, isHead);
		    }
		    timex3.setSpan(timex3Span);
		}
		timexIndex.put(timex3.getId(), timex3);
	    }
	    for (Timex3 timex3 : uncompleteBeginPointRefs.keySet()) {
		timex3.setBeginPoint(timexIndex.get(uncompleteBeginPointRefs.get(timex3)));
	    }
	    for (Timex3 timex3 : uncompleteEndPointRefs.keySet()) {
		timex3.setEndPoint(timexIndex.get(uncompleteEndPointRefs.get(timex3)));
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("temporalRelations");
	if (elem != null) {
	    List<Element> tLinkElems = elem.getChildren("tlink");
	    for (Element tLinkElem : tLinkElems) {
		String tlid = getAttribute("id", tLinkElem);
		String fromId = getAttribute("from", tLinkElem);
		String toId = getAttribute("to", tLinkElem);
		String fromType = getAttribute("fromType", tLinkElem);
		String toType = getAttribute("toType", tLinkElem);
		String relType = getAttribute("relType", tLinkElem);
		TLinkReferable from = fromType.equals("event")
			? predicateIndex.get(fromId) : timexIndex.get(fromId);
		TLinkReferable to = toType.equals("event")
			? predicateIndex.get(toId) : timexIndex.get(toId);
		TLink tLink = kaf.newTLink(tlid, from, to, relType);
	    }

	    List<Element> predAnchorElems = elem.getChildren("predicateAnchor");
	    for (Element predAnchorElem : predAnchorElems) {
		String id = getAttribute("id", predAnchorElem);
		Element spanElem = predAnchorElem.getChild("span");
		Span<Predicate> predAnchorSpan = KAFDocument.newSpan();
		if (spanElem != null) {
		    for (Element targetElem : spanElem.getChildren("target")) {
			String targetId = getAttribute("id", targetElem);
			Predicate predicate = predicateIndex.get(targetId);
			if (predicate == null) {
			    throw new KAFNotValidException("Predicate " + targetId + " not found when loading PredicateAnchor " + id);
			}
			boolean isHead = isHead(targetElem);
			predAnchorSpan.addTarget(predicate, isHead);
		    }
		}
		PredicateAnchor newPa = kaf.newPredicateAnchor(id, predAnchorSpan);
		String anchorTimeId = getOptAttribute("anchorTime", predAnchorElem);
		if (anchorTimeId != null) {
		    Timex3 anchorTime = timexIndex.get(anchorTimeId);
		    if (anchorTime == null) {
			throw new IllegalStateException("Invalid timex ID (" + anchorTimeId + ") in predicateAnchor " + id);
		    }
		    newPa.setAnchorTime(anchorTime);
		}
		String beginPointId = getOptAttribute("beginPoint", predAnchorElem);
		if (beginPointId != null) {
		    Timex3 beginPoint = timexIndex.get(beginPointId);
		    if (beginPoint == null) {
			throw new IllegalStateException("Invalid timex ID (" + beginPointId + ") in predicateAnchor " + id);
		    }
		    newPa.setBeginPoint(beginPoint);
		}
		String endPointId = getOptAttribute("endPoint", predAnchorElem);
		if (endPointId != null) {
		    Timex3 endPoint = timexIndex.get(endPointId);
		    if (endPoint == null) {
			throw new IllegalStateException("Invalid timex ID (" + endPointId + ") in predicateAnchor " + id);
		    }
		    newPa.setEndPoint(endPoint);
		}
	    }
	    
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("causalRelations");
	if (elem != null) {
	    List<Element> clinkElems = elem.getChildren("clink");
	    for (Element clinkElem : clinkElems) {
		String clid = getAttribute("id", clinkElem);
		String fromId = getAttribute("from", clinkElem);
		String toId = getAttribute("to", clinkElem);
		String relType = getOptAttribute("relType", clinkElem);
		Predicate from = predicateIndex.get(fromId);
		Predicate to = predicateIndex.get(toId);
		CLink clink = kaf.newCLink(clid, from, to);
		if (relType != null) {
		    clink.setRelType(relType);
		}
	    }
	    rootChildrenElems.remove(elem);
	}

	elem = rootElem.getChild("factualities");
	if (elem != null) {
	    List<Element> factualityElems = elem.getChildren("factuality");
	    for (Element factualityElem : factualityElems) {
		String id = getAttribute("id", factualityElem);
		Element spanElem = factualityElem.getChild("span");
		if (spanElem == null) {
		    throw new IllegalStateException("Every factuality must contain a 'span' element");
		}
		Span<Term> span = loadTermSpan(spanElem, termIndex, id);
		Factuality newFactuality = kaf.newFactuality(id, span);
		List<Element> factValElems = factualityElem.getChildren("factVal");
		for (Element factValElem : factValElems) {
		    newFactuality.addFactVal(DOMToFactVal(factValElem, kaf));
		}
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("factualityLayer");
	if (elem != null) {
	    List<Element> factualityElems = elem.getChildren("factvalue");
	    for (Element factualityElem : factualityElems) {
		String id = getAttribute("id", factualityElem);
		String prediction = getAttribute("prediction", factualityElem);
		String confidenceStr = getAttribute("confidence", factualityElem);
		Double confidence = null;
		if (confidenceStr != null) {
		    confidence = Double.parseDouble(confidenceStr);
		}
		Factvalue factuality = kaf.newFactvalue(wfIndex.get(id), prediction);
		if (confidence != null) {
		    factuality.setConfidence(confidence);
		}
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("features");
	if (elem != null) {
	    Element propertiesElem = elem.getChild("properties");
	    Element categoriesElem = elem.getChild("categories");
	    if (propertiesElem != null) {
		List<Element> propertyElems = propertiesElem.getChildren("property");
		for (Element propertyElem : propertyElems) {
		    String pid = getAttribute("id", propertyElem);
		    String lemma = getAttribute("lemma", propertyElem);
		    Element referencesElem = propertyElem.getChild("references");
		    if (referencesElem == null) {
			throw new IllegalStateException("Every property must contain a 'references' element");
		    }
		    List<Element> spanElems = referencesElem.getChildren("span");
		    if (spanElems.size() < 1) {
			throw new IllegalStateException("Every property must contain a 'span' element inside 'references'");
		    }
		    List<Span<Term>> references = new ArrayList<Span<Term>>();
		    for (Element spanElem : spanElems) {
			Span<Term> span = kaf.newTermSpan();
			List<Element> targetElems = spanElem.getChildren();
			if (targetElems.size() < 1) {
			    throw new IllegalStateException("Every span in a property must contain at least one target inside");  
			}
			for (Element targetElem : targetElems) {
			    String targetTermId = getAttribute("id", targetElem);
			    Term targetTerm = termIndex.get(targetTermId);
			    if (targetTerm == null) {
				throw new KAFNotValidException("Term " + targetTermId + " not found when loading property " + pid);
			    }
			    boolean isHead = isHead(targetElem);
			    span.addTarget(targetTerm, isHead);
			}
			references.add(span);
		    }
		    Feature newProperty = kaf.newProperty(pid, lemma, references);
		    List<Element> externalReferencesElems = propertyElem.getChildren("externalReferences");
		    if (externalReferencesElems.size() > 0) {
			List<ExternalRef> externalRefs = getExternalReferences(externalReferencesElems.get(0), kaf);
			newProperty.addExternalRefs(externalRefs);
		    }
		    relationalIndex.put(newProperty.getId(), newProperty);
		}
	    }
	    if (categoriesElem != null) {
		List<Element> categoryElems = categoriesElem.getChildren("category");
		for (Element categoryElem : categoryElems) {
		    String cid = getAttribute("id", categoryElem);
		    String lemma = getAttribute("lemma", categoryElem);
		    Element referencesElem = categoryElem.getChild("references");
		    if (referencesElem == null) {
			throw new IllegalStateException("Every category must contain a 'references' element");
		    }
		    List<Element> spanElems = referencesElem.getChildren("span");
		    if (spanElems.size() < 1) {
			throw new IllegalStateException("Every category must contain a 'span' element inside 'references'");
		    }
		    List<Span<Term>> references = new ArrayList<Span<Term>>();
		    for (Element spanElem : spanElems) {
			Span<Term> span = kaf.newTermSpan();
			List<Element> targetElems = spanElem.getChildren();
			if (targetElems.size() < 1) {
			    throw new IllegalStateException("Every span in a property must contain at least one target inside");  
			}
			for (Element targetElem : targetElems) {
			    String targetTermId = getAttribute("id", targetElem);
			    Term targetTerm = termIndex.get(targetTermId);
			    if (targetTerm == null) {
				throw new KAFNotValidException("Term " + targetTermId + " not found when loading category " + cid);
			    }
			    boolean isHead = isHead(targetElem);
			    span.addTarget(targetTerm, isHead);
			}
			references.add(span);
		    }
		    Feature newCategory = kaf.newCategory(cid, lemma, references);
		    List<Element> externalReferencesElems = categoryElem.getChildren("externalReferences");
		    if (externalReferencesElems.size() > 0) {
			List<ExternalRef> externalRefs = getExternalReferences(externalReferencesElems.get(0), kaf);
			newCategory.addExternalRefs(externalRefs);
		    }
		    relationalIndex.put(newCategory.getId(), newCategory);
		}
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("relations");
	if (elem != null) {	    List<Element> relationElems = elem.getChildren("relation");
	    for (Element relationElem : relationElems) {
		String id = getAttribute("id", relationElem);
		String fromId = getAttribute("from", relationElem);
		String toId = getAttribute("to", relationElem);
		String confidenceStr = getOptAttribute("confidence", relationElem);
		float confidence = -1.0f;
		if (confidenceStr != null) {
		    confidence = Float.parseFloat(confidenceStr);
		}
		Relational from = relationalIndex.get(fromId);
		if (from == null) {
		    throw new KAFNotValidException("Entity/feature object " + fromId + " not found when loading relation " + id);
		}
		Relational to = relationalIndex.get(toId);
		if (to == null) {
		    throw new KAFNotValidException("Entity/feature object " + toId + " not found when loading relation " + id);
		}
		Relation newRelation = kaf.newRelation(id, from, to);
		if (confidence >= 0) {
		    newRelation.setConfidence(confidence);
		}
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("topics");
	if (elem != null) {
	    List<Element> topicElems = elem.getChildren("topic");
	    for (Element topicElem : topicElems) {
		String value = topicElem.getText();
		Topic topic = kaf.newTopic(value); 
		String source = getOptAttribute("source", topicElem);
		if (source != null){
		    topic.setSource(source);
		}
		String method = getOptAttribute("method", topicElem);
		if (method != null){
		    topic.setMethod(method);
		}
		String confidenceStr = getOptAttribute("confidence", topicElem);
		float confidence = -1.0f;
		if (confidenceStr != null) {
		    confidence = Float.parseFloat(confidenceStr);
		    topic.setConfidence(confidence);
		}
		String URI = getOptAttribute("uri", topicElem);
		if (URI != null){
		    topic.setURI(URI);
		}
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("markables");
	if (elem != null) {
	    List<Element> markElems = elem.getChildren();
	    for (Element markElem : markElems) {
		String sid = getAttribute("id", markElem);

		Element spanElem = markElem.getChild("span");
		if (spanElem == null) {
		    throw new IllegalStateException("Every mark must contain a span element");
		}
		List<Element> marksWfElems = spanElem.getChildren("target");
		Span<WF> span = kaf.newWFSpan();
		for (Element marksWfElem : marksWfElems) {
		    String wfId = getAttribute("id", marksWfElem);
		    boolean isHead = isHead(marksWfElem);
		    WF wf = wfIndex.get(wfId);
		    if (wf == null) {
			throw new KAFNotValidException("WF " + wfId + " not found when loading mark " + sid);
		    }
		    span.addTarget(wf, isHead);
		}
		String source = getOptAttribute("source", markElem);
		Mark newMark = (source == null) ? kaf.newMark(sid, span) : kaf.newMark(sid, source, span); 
		Element sentimentElem = markElem.getChild("sentiment");
		if (sentimentElem != null) {
		    newMark.setSentiment(DOMToSentiment(sentimentElem, kaf));
		}
		String type = getOptAttribute("type", markElem);
		if (type != null) {
		    newMark.setType(type);
		}
		String lemma = getOptAttribute("lemma", markElem);
		if (lemma != null) {
		    newMark.setLemma(lemma);
		}
		String pos = getOptAttribute("pos", markElem);
		if (pos != null) {
		    newMark.setPos(pos);
		}
		String tMorphofeat = getOptAttribute("morphofeat", markElem);
		if (tMorphofeat != null) {
		    newMark.setMorphofeat(tMorphofeat);
		}
		String markcase = getOptAttribute("case", markElem);
		if (markcase != null) {
		    newMark.setCase(markcase);
		}
		List<Element> externalReferencesElems = markElem.getChildren("externalReferences");
		if (externalReferencesElems.size() > 0) {
		    List<ExternalRef> externalRefs = getExternalReferences(externalReferencesElems.get(0), kaf);
		    newMark.addExternalRefs(externalRefs);
		}
	    }
	    rootChildrenElems.remove(elem);
	}
	
	elem = rootElem.getChild("attribution");
	if (elem != null) {
	    List<Element> statementElems = elem.getChildren("statement");
	    for (Element statementElem : statementElems) {
		String statementId = getAttribute("id", statementElem);
		Element stTargetElem = statementElem.getChild("statement_target");
		if (stTargetElem == null) {
		    throw new KAFNotValidException("Statement " + statementId + " does not contain the compulsory StatementTarget element.");
		}
		Span<Term> span = kaf.newTermSpan();
		Statement.StatementTarget target = kaf.newStatementTarget(span);
		Element spanElem = stTargetElem.getChild("span");
		if (spanElem != null) {
		    List<Element> targetElems = spanElem.getChildren("target");
		    for (Element targetElem : targetElems) {
			String refId = getOptAttribute("id", targetElem);
			boolean isHead = isHead(targetElem);
			Term targetTerm = termIndex.get(refId);
			if (targetTerm == null) {
			    throw new KAFNotValidException("Term " + refId + " not found when loading statement " + statementId);
			}
			span.addTarget(targetTerm, isHead);
		    }
		}
		Statement statement = kaf.newStatement(statementId, target);
			
		Element stSourceElem = statementElem.getChild("statement_source");
		if (stSourceElem != null) {
		    Element sourceSpanElem = stSourceElem.getChild("span");
		    if (sourceSpanElem == null) {
			throw new KAFNotValidException("StatementSource elements need to contain the span element");
		    }
		    Span<Term> sourceSpan = loadTermSpan(sourceSpanElem, termIndex, statementId);
		    statement.setSource(kaf.newStatementSource(sourceSpan));
		}
		Element cueElem = statementElem.getChild("statement_cue");
		if (cueElem != null) {
		    Element cueSpanElem = cueElem.getChild("span");
		    if (cueSpanElem == null) {
			throw new KAFNotValidException("StatementCue elements need to contain the span element");
		    }
		    Span<Term> cueSpan = loadTermSpan(cueSpanElem, termIndex, statementId);
		    statement.setCue(kaf.newStatementCue(cueSpan));
		}
	    }
	    rootChildrenElems.remove(elem);
	}
	
	
	for (Element unknownLayerElem : rootChildrenElems) { // These layers are not recognised by the library
	    kaf.addUnknownLayer(unknownLayerElem);
	}

	return kaf;
    }

    private static void DOMToTerm(Element termElem, KAFDocument kaf, boolean isComponent, Map<String, WF> wfIndex, Map<String, Term> termIndex, Term parentTerm) throws KAFNotValidException {
	String tid = getAttribute("id", termElem);
	Element spanElem = termElem.getChild("span");
	List<Element> termsWfElems = new ArrayList<Element>();
	if (!isComponent) {
	    if (spanElem == null) {
		throw new IllegalStateException("Every term must contain a span element");
	    }
	    termsWfElems = spanElem.getChildren("target");
	}
	Span<WF> span = kaf.newWFSpan();
	for (Element termsWfElem : termsWfElems) {
	    String wfId = getAttribute("id", termsWfElem);
	    boolean isHead = isHead(termsWfElem);
	    WF wf = wfIndex.get(wfId);
	    if (wf == null) {
		throw new KAFNotValidException("Wf " + wfId + " not found when loading term " + tid);
	    }
	    span.addTarget(wf, isHead);
	}
	Term newTerm = kaf.newTerm(tid, span, isComponent);
	String type = getOptAttribute("type", termElem);
	if (type != null) {
	    newTerm.setType(type);
	}
	String lemma = getOptAttribute("lemma", termElem);
	if (lemma != null) {
	    newTerm.setLemma(lemma);
	}
	String pos = getOptAttribute("pos", termElem);
	if (pos != null) {
	    newTerm.setPos(pos);
	}
	String tMorphofeat = getOptAttribute("morphofeat", termElem);
	if (tMorphofeat != null) {
	    newTerm.setMorphofeat(tMorphofeat);
	}
	String tHead = getOptAttribute("head", termElem);
	String termcase = getOptAttribute("case", termElem);
	if (termcase != null) {
	    newTerm.setCase(termcase);
	}
	List<Element> sentimentElems = termElem.getChildren("sentiment");
	if (sentimentElems.size() > 0) {
	    Term.Sentiment sentiment = DOMToSentiment(sentimentElems.get(0), kaf);
	    newTerm.setSentiment(sentiment);
	}
	    
	if (!isComponent) {
	    List<Element> termsComponentElems = termElem.getChildren("component");
	    for (Element termsComponentElem : termsComponentElems) {
	        DOMToTerm(termsComponentElem, kaf, true, wfIndex, termIndex, newTerm);
	    }
	} else {
	    parentTerm.addComponent(newTerm);
	}
	List<Element> externalReferencesElems = termElem.getChildren("externalReferences");
	if (externalReferencesElems.size() > 0) {
	    List<ExternalRef> externalRefs = getExternalReferences(externalReferencesElems.get(0), kaf);
	    newTerm.addExternalRefs(externalRefs);
	}
	termIndex.put(newTerm.getId(), newTerm);
    }

    private static Term.Sentiment DOMToSentiment(Element sentimentElem, KAFDocument kaf) {
	Term.Sentiment newSentiment = kaf.newSentiment();
	String sentResource = getOptAttribute("resource", sentimentElem);
	if (sentResource != null) {
	    newSentiment.setResource(sentResource);
	}
	String sentPolarity = getOptAttribute("polarity", sentimentElem);
	if (sentPolarity != null) {
	    newSentiment.setPolarity(sentPolarity);
	}		
	String sentStrength = getOptAttribute("strength", sentimentElem);
	if (sentStrength != null) {
	    newSentiment.setStrength(sentStrength);
	}
	String sentSubjectivity = getOptAttribute("subjectivity", sentimentElem);
	if (sentSubjectivity != null) {
	    newSentiment.setSubjectivity(sentSubjectivity);
	}
	String sentSentimentSemanticType = getOptAttribute("sentiment_semantic_type", sentimentElem);
	if (sentSentimentSemanticType != null) {
	    newSentiment.setSentimentSemanticType(sentSentimentSemanticType);
	}
	String sentSentimentModifier = getOptAttribute("sentiment_modifier", sentimentElem);
	if (sentSentimentModifier != null) {
	    newSentiment.setSentimentModifier(sentSentimentModifier);
	}
	String sentSentimentMarker = getOptAttribute("sentiment_marker", sentimentElem);
	if (sentSentimentMarker != null) {
	    newSentiment.setSentimentMarker(sentSentimentMarker);
	}
	String sentSentimentProductFeature = getOptAttribute("sentiment_product_feature", sentimentElem);
	if (sentSentimentProductFeature != null) {
	    newSentiment.setSentimentProductFeature(sentSentimentProductFeature);
	}
        return newSentiment;
    }
    
    private static Factuality.FactVal DOMToFactVal(Element factValElem, KAFDocument kaf) {
	String value = getAttribute("value", factValElem);
	String resource = getAttribute("resource", factValElem);
	Factuality.FactVal newFactVal = kaf.newFactVal(value, resource);
	String source = getOptAttribute("source", factValElem);
	if (source != null) {
	    newFactVal.setSource(source);
	}
	String confidence = getOptAttribute("confidence", factValElem);
	if (confidence != null) {
	    newFactVal.setConfidence(Float.valueOf(confidence));
	}
	return newFactVal;
    }


    private static Span<Term> loadTermSpan(Element spanElem, HashMap<String, Term> terms, String objId) throws KAFNotValidException {
	List<Element> targetElems = spanElem.getChildren("target");
	if (targetElems.size() < 1) {
	    throw new KAFNotValidException("A span element can not be empty");
	}
	Span<Term> span = KAFDocument.newTermSpan();
	for (Element targetElem : targetElems) {
	    String targetId = getAttribute("id", targetElem);
	    boolean isHead = isHead(targetElem);
	    Term targetTerm = terms.get(targetId);
	    if (targetTerm == null) {
		throw new KAFNotValidException("Term object " + targetId + " not found when loading object " + objId);
	    }
	    span.addTarget(targetTerm, isHead);
	}
	return span;
    }

    private static Element createTermSpanElem(Span<Term> span) {
	Element spanElem = new Element("span");
	for (Term term : span.getTargets()) {
	    Element targetElem = new Element("target");
	    String targetId = term.getId();
	    targetElem.setAttribute("id", targetId);
	    if (span.isHead(term)) {
		targetElem.setAttribute("head", "yes");
	    }
	    spanElem.addContent(targetElem);
	}
	return spanElem;
    }

    private static List<ExternalRef> getExternalReferences(Element externalReferencesElem, KAFDocument kaf) {
	List<ExternalRef> externalRefs = new ArrayList<ExternalRef>();
	List<Element> externalRefElems = externalReferencesElem.getChildren();
	for (Element externalRefElem : externalRefElems) {
	    ExternalRef externalRef = getExternalRef(externalRefElem, kaf);
	    externalRefs.add(externalRef);
	}
	return externalRefs;
    }

    private static ExternalRef getExternalRef(Element externalRefElem, KAFDocument kaf) {
	String resource = getAttribute("resource", externalRefElem);
	ExternalRef newExternalRef = kaf.newExternalRef(resource);
	String references = getOptAttribute("reference", externalRefElem);
	if (references != null) {
	    newExternalRef.setReference(references);
	}
	String confidence = getOptAttribute("confidence", externalRefElem);
	if (confidence != null) {
	    newExternalRef.setConfidence(Float.valueOf(confidence));
	}
	String reftype = getOptAttribute("reftype", externalRefElem);
	if (reftype != null) {
	    newExternalRef.setReftype(reftype);
	}
	String status = getOptAttribute("status", externalRefElem);
	if (status != null) {
	    newExternalRef.setStatus(status);
	}
	String source = getOptAttribute("source", externalRefElem);
	if (source != null) {
	    newExternalRef.setSource(source);
	}
	List<Element> sentimentElems = externalRefElem.getChildren("sentiment");
	if (sentimentElems.size() > 0) {
	    Term.Sentiment sentiment = DOMToSentiment(sentimentElems.get(0), kaf);
	    newExternalRef.setSentiment(sentiment);
	}
	List<Element> subRefElems = externalRefElem.getChildren("externalRef");
	for(Element subRefElem :  subRefElems) {
	    ExternalRef subRef = getExternalRef(subRefElem, kaf);
	    newExternalRef.addExternalRef(subRef);
	}
	return newExternalRef;
    }

    private static String getAttribute(String attName, Element elem) {
	String value = elem.getAttributeValue(attName);
	if (value==null) {
	    throw new IllegalStateException(attName+" attribute must be defined for element "+elem.getName());
	}
	return value;
    }

    private static String getAttribute(String attName, Element elem, Namespace nmspace) {
	String value = elem.getAttributeValue(attName, nmspace);
	if (value==null) {
	    throw new IllegalStateException(attName+" attribute must be defined for element "+elem.getName());
	}
	return value;
    }

    private static String getOptAttribute(String attName, Element elem) {
	String value = elem.getAttributeValue(attName);
	if (value==null) {
	    return null;
	}
	return value;
    }

    private static boolean isHead(Element elem) {
	String value = elem.getAttributeValue("head");
	if (value == null) {
	    return false;
	}
	if (value.equals("yes")) {
	    return true;
	}
	return false;
    }


    private static class Edge {
	String id;
	String from;
	String to;
	boolean head;

	Edge(TreeNode from, TreeNode to) {
	    if (from.hasEdgeId()) {
		this.id = from.getEdgeId();
	    }
	    this.from = from.getId();
	    this.to = to.getId();
	    this.head = from.getHead();
	}
    }

    /** Returns the content of the given KAFDocument in a DOM document. */
    private static Document KAFToDOM(KAFDocument kaf) {
	AnnotationContainer annotationContainer = kaf.getAnnotationContainer();
	Element root = new Element("NAF");
	root.setAttribute("lang", kaf.getLang(), Namespace.XML_NAMESPACE);
	root.setAttribute("version", kaf.getVersion());

	Document doc = new Document(root);

	Element kafHeaderElem = new Element("nafHeader");
	root.addContent(kafHeaderElem);

	KAFDocument.FileDesc fd = kaf.getFileDesc();
	if (fd != null) {
	    Element fdElem = new Element("fileDesc");
	    if (fd.author != null) {
		fdElem.setAttribute("author", fd.author);
	    }
	    if (fd.creationtime != null) {
		fdElem.setAttribute("creationtime", fd.creationtime);
	    }
	    if (fd.title != null) {
		fdElem.setAttribute("title", fd.title);
	    }
	    if (fd.publisher != null) {
		fdElem.setAttribute("publisher", fd.publisher);
	    }
	    if (fd.section != null) {
		fdElem.setAttribute("section", fd.section);
	    }
	    if (fd.location != null) {
		fdElem.setAttribute("location", fd.location);
	    }
	    if (fd.magazine != null) {
		fdElem.setAttribute("magazine", fd.magazine);
	    }
	    if (fd.filename != null) {
		fdElem.setAttribute("filename", fd.filename);
	    }
	    if (fd.filetype != null) {
		fdElem.setAttribute("filetype", fd.filetype);
	    }
	    if (fd.pages != null) {
		fdElem.setAttribute("pages", Integer.toString(fd.pages));
	    }
	    kafHeaderElem.addContent(fdElem);
	}

	KAFDocument.Public pub = kaf.getPublic();
	if (pub != null) {
	    Element pubElem = new Element("public");
	    if (pub.publicId != null) {
		pubElem.setAttribute("publicId", pub.publicId);
	    }
	    if (pub.uri != null) {
		pubElem.setAttribute("uri", pub.uri);
	    }
	    kafHeaderElem.addContent(pubElem);
	}

	Map<String, List<KAFDocument.LinguisticProcessor>> lps = kaf.getLinguisticProcessors();
	for (Map.Entry entry : lps.entrySet()) {
	    Element lpsElem = new Element("linguisticProcessors");
	    lpsElem.setAttribute("layer", (String) entry.getKey());
	    for (KAFDocument.LinguisticProcessor lp : (List<KAFDocument.LinguisticProcessor>) entry.getValue()) {
		Element lpElem = new Element("lp");
		lpElem.setAttribute("name", lp.name);
		if (lp.hasTimestamp()) {
		    lpElem.setAttribute("timestamp", lp.timestamp);
		}
		if (lp.hasBeginTimestamp()) {
		    lpElem.setAttribute("beginTimestamp", lp.beginTimestamp);
		}
		if (lp.hasEndTimestamp()) {
		    lpElem.setAttribute("endTimestamp", lp.endTimestamp);
		}
		if (lp.hasVersion()) {
		    lpElem.setAttribute("version", lp.version);
		}
		if (lp.hasHostname()) {
		    lpElem.setAttribute("hostname", lp.hostname);
		}
		lpsElem.addContent(lpElem);
	    }
	    kafHeaderElem.addContent(lpsElem);
	}

	String rawText = annotationContainer.getRawText();
	if (rawText.length() > 0) {
	    Element rawElem = new Element("raw");
	    CDATA cdataElem =  new CDATA(rawText);
	    rawElem.addContent(cdataElem);
	    root.addContent(rawElem);
	}

	List<WF> text = (List<WF>)(List<?>)annotationContainer.getLayer(Layer.TEXT);
	if (text.size() > 0) {
	    Element textElem = new Element("text");
	    for (WF wf : text) {
		Element wfElem = new Element("wf");
		wfElem.setAttribute("id", wf.getId());
		wfElem.setAttribute("offset", Integer.toString(wf.getOffset()));
		wfElem.setAttribute("length", Integer.toString(wf.getLength()));
		wfElem.setAttribute("sent", Integer.toString(wf.getSent()));
		if (wf.hasPara()) {
		    wfElem.setAttribute("para", Integer.toString(wf.getPara()));
		}
		if (wf.hasPage()) {
		    wfElem.setAttribute("page", Integer.toString(wf.getPage()));
		}
		if (wf.hasXpath()) {
		    wfElem.setAttribute("xpath", wf.getXpath());
		}
		wfElem.setText(wf.getForm());
		textElem.addContent(wfElem);
	    }
	    root.addContent(textElem);
	}

	List<Term> terms = (List<Term>)(List<?>)annotationContainer.getLayer(Layer.TERMS);
	if (terms.size() > 0) {
	    Element termsElem = new Element("terms");
	    for (Term term : terms) {
		termToDOM(term, false, termsElem);
	    }
	    root.addContent(termsElem);
	}

	List<Mark> marks = (List<Mark>)(List<?>)annotationContainer.getLayer(Layer.MARKABLES);
	if (marks.size() > 0) {
	    Element marksElem = new Element("markables");
	    for (Mark mark : marks) {
		Comment markComment = new Comment(StringEscapeUtils.escapeXml11(mark.getStr()));
		marksElem.addContent(markComment);
		Element markElem = new Element("mark");
		markElem.setAttribute("id", mark.getId());
		if (mark.hasSource()) {
		    markElem.setAttribute("source", mark.getSource());
		}
		if (mark.hasType()) {
		    markElem.setAttribute("type", mark.getType());
		}
		if (mark.hasLemma()) {
		    markElem.setAttribute("lemma", mark.getLemma());
		}
		if (mark.hasPos()) {
		    markElem.setAttribute("pos", mark.getPos());
		}
		if (mark.hasMorphofeat()) {
		    markElem.setAttribute("morphofeat", mark.getMorphofeat());
		}
		if (mark.hasCase()) {
		    markElem.setAttribute("case", mark.getCase());
		}
		if (mark.hasSentiment()) {
		    markElem.addContent(sentimentToDOM(mark.getSentiment()));
		}
		Element spanElem = new Element("span");
		Span<WF> span = mark.getSpan();
		for (WF target : span.getTargets()) {
		    Element targetElem = new Element("target");
		    targetElem.setAttribute("id", target.getId());
		    if (target == span.getHead()) {
			targetElem.setAttribute("head", "yes");
		    }
		    spanElem.addContent(targetElem);
		}
		markElem.addContent(spanElem);
		List<ExternalRef> externalReferences = mark.getExternalRefs();
		if (externalReferences.size() > 0) {
		    Element externalReferencesElem = externalReferencesToDOM(externalReferences);
		    markElem.addContent(externalReferencesElem);
		}
		marksElem.addContent(markElem);
	    }
	    root.addContent(marksElem);
	}

	List<Dep> deps = (List<Dep>)(List<?>)annotationContainer.getLayer(Layer.DEPS);
	if (deps.size() > 0) {
	    Element depsElem = new Element("deps");
	    for (Dep dep : deps) {
		Comment depComment = new Comment(StringEscapeUtils.escapeXml11(dep.getStr()));
		depsElem.addContent(depComment);
		Element depElem = new Element("dep");
		depElem.setAttribute("from", dep.getFrom().getId());
		depElem.setAttribute("to", dep.getTo().getId());
		depElem.setAttribute("rfunc", dep.getRfunc());
		if (dep.hasCase()) {
		    depElem.setAttribute("case", dep.getCase());
		}
		depsElem.addContent(depElem);
	    }
	    root.addContent(depsElem);
	}

	List<Chunk> chunks = (List<Chunk>)(List<?>)annotationContainer.getLayer(Layer.CHUNKS);
	if (chunks.size() > 0) {
	    Element chunksElem = new Element("chunks");
	    for (Chunk chunk : chunks) {
		Comment chunkComment = new Comment(StringEscapeUtils.escapeXml11(chunk.getStr()));
		chunksElem.addContent(chunkComment);
		Element chunkElem = new Element("chunk");
		chunkElem.setAttribute("id", chunk.getId());
		chunkElem.setAttribute("head", chunk.getHead().getId());
		if (chunk.hasPhrase()) {
		    chunkElem.setAttribute("phrase", chunk.getPhrase());
		}
		if (chunk.hasCase()) {
		    chunkElem.setAttribute("case", chunk.getCase());
		}
		Element spanElem = new Element("span");
		for (Term target : chunk.getTerms()) {
		    Element targetElem = new Element("target");
		    targetElem.setAttribute("id", target.getId());
		    spanElem.addContent(targetElem);
		}
		chunkElem.addContent(spanElem);
		chunksElem.addContent(chunkElem);
	    }
	    root.addContent(chunksElem);
	}

	List<Entity> entities = (List<Entity>)(List<?>)annotationContainer.getLayer(Layer.ENTITIES);
	if (entities.size() > 0) {
	    Element entitiesElem = new Element("entities");
	    for (Entity entity : entities) {
		Element entityElem = new Element("entity");
		entityElem.setAttribute("id", entity.getId());
		if (entity.hasType()) {
		    entityElem.setAttribute("type", entity.getType());
		}
		if (entity.hasSource()) {
		    entityElem.setAttribute("source", entity.getSource());
		}
		Element referencesElem = new Element("references");
		for (Span<Term> span : entity.getSpans()) {
		    Comment spanComment = new Comment(StringEscapeUtils.escapeXml11(entity.getSpanStr(span)));
		    referencesElem.addContent(spanComment);
		    Element spanElem = new Element("span");
		    for (Term term : span.getTargets()) {
			Element targetElem = new Element("target");
			targetElem.setAttribute("id", term.getId());
			if (term == span.getHead()) {
			    targetElem.setAttribute("head", "yes");
			}
			spanElem.addContent(targetElem);
		    }
		    referencesElem.addContent(spanElem);
		}
		entityElem.addContent(referencesElem);
		List<ExternalRef> externalReferences = entity.getExternalRefs();
		if (externalReferences.size() > 0) {
		    Element externalReferencesElem = externalReferencesToDOM(externalReferences);
		    entityElem.addContent(externalReferencesElem);
		}
		entitiesElem.addContent(entityElem);
	    }
	    root.addContent(entitiesElem);
	}

	List<Coref> corefs = (List<Coref>)(List<?>)annotationContainer.getLayer(Layer.COREFERENCES);
	if (corefs.size() > 0) {
	    Element corefsElem = new Element("coreferences");
	    for (Coref coref : corefs) {
		Element corefElem = new Element("coref");
		corefElem.setAttribute("id", coref.getId());
		if (coref.hasType()) {
		    corefElem.setAttribute("type", coref.getType());
		}
		for (Span<Term> span : coref.getSpans()) {
		    Comment spanComment = new Comment(StringEscapeUtils.escapeXml11(coref.getSpanStr(span)));
		    corefElem.addContent(spanComment);
		    Element spanElem = new Element("span");
		    for (Term target : span.getTargets()) {
			Element targetElem = new Element("target");
			targetElem.setAttribute("id", target.getId());
			if (target == span.getHead()) {
			    targetElem.setAttribute("head", "yes");
			}
			spanElem.addContent(targetElem);
		    }
		    corefElem.addContent(spanElem);
		}
		List<ExternalRef> externalReferences = coref.getExternalRefs();
		if (externalReferences.size() > 0) {
		    Element externalReferencesElem = externalReferencesToDOM(externalReferences);
		    corefElem.addContent(externalReferencesElem);
		}
		corefsElem.addContent(corefElem);
	    }
	    root.addContent(corefsElem);
	}

	List<Timex3> timeExs = (List<Timex3>)(List<?>)annotationContainer.getLayer(Layer.TIME_EXPRESSIONS);
	if (timeExs.size() > 0){
	    Element timeExsElem = new Element("timeExpressions");
	    for (Timex3 timex3 : timeExs) {
		Element timex3Elem = new Element("timex3");
		timex3Elem.setAttribute("id", timex3.getId());
		timex3Elem.setAttribute("type", timex3.getType());
		if (timex3.hasBeginPoint()) {
		    timex3Elem.setAttribute("beginPoint", timex3.getBeginPoint().getId());
		}
		if (timex3.hasEndPoint()) {
		    timex3Elem.setAttribute("endPoint", timex3.getEndPoint().getId());
		}
		if (timex3.hasQuant()) {
		    timex3Elem.setAttribute("quant", timex3.getQuant());
		}
		if (timex3.hasFreq()) {
		    timex3Elem.setAttribute("freq", timex3.getFreq());
		}
		if (timex3.hasFunctionInDocument()) {
		    timex3Elem.setAttribute("functionInDocument", timex3.getFunctionInDocument());
		}
		if (timex3.hasTemporalFunction()) {
		    String tempFun = timex3.getTemporalFunction() ? "true" : "false";
		    timex3Elem.setAttribute("temporalFunction", tempFun);
		}
		if (timex3.hasValue()) {
		    timex3Elem.setAttribute("value", timex3.getValue());
		}
		if (timex3.hasValueFromFunction()) {
		    timex3Elem.setAttribute("valueFromFunction", timex3.getValueFromFunction());
		}
		if (timex3.hasMod()) {
		    timex3Elem.setAttribute("mod", timex3.getMod());
		}
		if (timex3.hasAnchorTimeId()) {
		    timex3Elem.setAttribute("anchorTimeId", timex3.getAnchorTimeId());
		}
		if (timex3.hasComment()) {
		    timex3Elem.setAttribute("comment", timex3.getComment());
		}
		if (timex3.hasSpan()) {
		    Span<WF> span = timex3.getSpan();
		    Comment spanComment = new Comment(StringEscapeUtils.escapeXml11(timex3.getSpanStr(span)));
		    timex3Elem.addContent(spanComment);
		    Element spanElem = new Element("span");
		    for (WF target : span.getTargets()) {
			Element targetElem = new Element("target");
			targetElem.setAttribute("id", target.getId());
			if (target == span.getHead()) {
			    targetElem.setAttribute("head", "yes");
			}
			spanElem.addContent(targetElem);
		    }
		    timex3Elem.addContent(spanElem);
		}
		/*
		for (Span<WF> span : timex3.getSpans()) {
		    Comment spanComment = new Comment(StringEscapeUtils.escapeXml11(timex3.getSpanStr(span)));
		    timex3Elem.addContent(spanComment);
		    Element spanElem = new Element("span");
		    for (WF target : span.getTargets()) {
			Element targetElem = new Element("target");
			targetElem.setAttribute("id", target.getId());
			if (target == span.getHead()) {
			    targetElem.setAttribute("head", "yes");
			}
			spanElem.addContent(targetElem);
		    }
		    timex3Elem.addContent(spanElem);
		}
		*/
		timeExsElem.addContent(timex3Elem);
	    }
	    root.addContent(timeExsElem);
	}
	
	List<Factuality> factualities = (List<Factuality>)(List<?>)annotationContainer.getLayer(Layer.FACTUALITIES);
	if (factualities.size() > 0) {
		Element factsElement = new Element("factualities");
		for (Factuality f : factualities) {
			Element factElem = new Element("factuality");
			factElem.setAttribute("id", f.getId());
			Span<Term> span = f.getSpan();
			Comment spanComment = new Comment(StringEscapeUtils.escapeXml11(f.getSpanStr(span)));
			factElem.addContent(spanComment);
			Element spanElem = new Element("span");
			for (Term term : span.getTargets()) {
			    Element targetElem = new Element("target");
			    targetElem.setAttribute("id", term.getId());
			    if (term == span.getHead()) {
				targetElem.setAttribute("head", "yes");
			    }
			    spanElem.addContent(targetElem);
			}
			factElem.addContent(spanElem);
			for (Factuality.FactVal factVal : f.getFactVals()) {
			    factElem.addContent(factValToDOM(factVal));			    
			}
			factsElement.addContent(factElem);
		}
		root.addContent(factsElement);
	}

	List<Factvalue> factValues = (List<Factvalue>)(List<?>)annotationContainer.getLayer(Layer.FACTUALITY_LAYER);
	if (factValues.size() > 0) {
		Element factsElement = new Element("factualitylayer");
		for (Factvalue f : factValues) {
			Element fact = new Element("factvalue");
			fact.setAttribute("id", f.getId());
			fact.setAttribute("prediction", f.getPrediction());
			fact.setAttribute("confidence", Double.toString(f.getConfidence()));
			/*
			for (Factuality.FactualityPart p : f.getFactualityParts()) {
				Element factPartial = new Element("factuality");
				factPartial.setAttribute("prediction", p.getPrediction());
				factPartial.setAttribute("confidence", Double.toString(p.getConfidence()));
				fact.addContent(factPartial);
			}
			*/

			factsElement.addContent(fact);
		}
		root.addContent(factsElement);
	}

	List<LinkedEntity> linkedEntities = (List<LinkedEntity>)(List<?>)annotationContainer.getLayer(Layer.LINKED_ENTITIES);
	if (linkedEntities.size() > 0) {
		Element linkedEntityElement = new Element("linkedEntities");
		for (LinkedEntity e : linkedEntities) {
			Element lEnt = new Element("linkedEntity");
			lEnt.setAttribute("id", e.getId());
			lEnt.setAttribute("resource", e.getResource());
			lEnt.setAttribute("reference", e.getReference());
			lEnt.setAttribute("confidence", Double.toString(e.getConfidence()));

			Comment spanComment = new Comment(StringEscapeUtils.escapeXml11(e.getSpanStr()));
			lEnt.addContent(spanComment);
			Element spanElem = new Element("span");
			for (WF target : e.getWFs().getTargets()) {
				Element targetElem = new Element("target");
				targetElem.setAttribute("id", target.getId());
				spanElem.addContent(targetElem);
			}
			lEnt.addContent(spanElem);

			linkedEntityElement.addContent(lEnt);
		}
		root.addContent(linkedEntityElement);
	}

	Element featuresElem = new Element("features");
	List<Feature> properties = (List<Feature>)(List<?>)annotationContainer.getLayer(Layer.PROPERTIES);
	if (properties.size() > 0) {
	    Element propertiesElem = new Element("properties");
	    for (Feature property : properties) {
		Element propertyElem = new Element("property");
		propertyElem.setAttribute("id", property.getId());
		propertyElem.setAttribute("lemma", property.getLemma());
		List<Span<Term>> references = property.getSpans();
		Element referencesElem = new Element("references");
		for (Span<Term> span : references) {
		    Comment spanComment = new Comment(StringEscapeUtils.escapeXml11(property.getSpanStr(span)));
		    referencesElem.addContent(spanComment);
		    Element spanElem = new Element("span");
		    for (Term term : span.getTargets()) {
			Element targetElem = new Element("target");
			targetElem.setAttribute("id", term.getId());
			if (term == span.getHead()) {
			    targetElem.setAttribute("head", "yes");
			}
			spanElem.addContent(targetElem);
		    }
		    referencesElem.addContent(spanElem);
		}
		propertyElem.addContent(referencesElem);
		propertiesElem.addContent(propertyElem);
	    }
	    featuresElem.addContent(propertiesElem);
	}
	List<Feature> categories = (List<Feature>)(List<?>)annotationContainer.getLayer(Layer.CATEGORIES);
	if (categories.size() > 0) {
	    Element categoriesElem = new Element("categories");
	    for (Feature category : categories) {
		Element categoryElem = new Element("category");
		categoryElem.setAttribute("id", category.getId());
		categoryElem.setAttribute("lemma", category.getLemma());
		List<Span<Term>> references = category.getSpans();
		Element referencesElem = new Element("references");
		for (Span<Term> span : references) {
		    Comment spanComment = new Comment(StringEscapeUtils.escapeXml11(category.getSpanStr(span)));
		    referencesElem.addContent(spanComment);
		    Element spanElem = new Element("span");
		    for (Term term : span.getTargets()) {
			Element targetElem = new Element("target");
			targetElem.setAttribute("id", term.getId());
			if (term == span.getHead()) {
			    targetElem.setAttribute("head", "yes");
			}
			spanElem.addContent(targetElem);
		    }
		    referencesElem.addContent(spanElem);
		}
		categoryElem.addContent(referencesElem);
		categoriesElem.addContent(categoryElem);
	    }
	    featuresElem.addContent(categoriesElem);
	}
	if (featuresElem.getChildren().size() > 0) {
	    root.addContent(featuresElem);
	}

	List<Opinion> opinions = (List<Opinion>)(List<?>)annotationContainer.getLayer(Layer.OPINIONS);
	if (opinions.size() > 0) {
	    Element opinionsElem = new Element("opinions");
	    for (Opinion opinion : opinions) {
		Element opinionElem = new Element("opinion");
		opinionElem.setAttribute("id", opinion.getId());
		Opinion.OpinionHolder holder = opinion.getOpinionHolder();
		if (holder != null) {
		    Element opinionHolderElem = new Element("opinion_holder");
		    if (holder.hasType()) {
			opinionHolderElem.setAttribute("type", holder.getType());
		    }
		    Comment comment = new Comment(StringEscapeUtils.escapeXml11(opinion.getSpanStr(opinion.getOpinionHolder().getSpan())));
		    opinionHolderElem.addContent(comment);
		    List<Term> targets = holder.getTerms();
		    Span<Term> span = holder.getSpan();
		    if (targets.size() > 0) {
			Element spanElem = new Element("span");
			opinionHolderElem.addContent(spanElem);
			for (Term target : targets) {
			    Element targetElem = new Element("target");
			    targetElem.setAttribute("id", target.getId());
			    if (target == span.getHead()) {
				targetElem.setAttribute("head", "yes");
			    }
			    spanElem.addContent(targetElem);
			}
		    }
		    opinionElem.addContent(opinionHolderElem);
		}
		Opinion.OpinionTarget opTarget = opinion.getOpinionTarget();
		if (opTarget != null) {
		    Element opinionTargetElem = new Element("opinion_target");
		    Comment comment = new Comment(StringEscapeUtils.escapeXml11(opinion.getSpanStr(opinion.getOpinionTarget().getSpan())));
		    opinionTargetElem.addContent(comment);
		    List<Term> targets = opTarget.getTerms();
		    Span<Term> span = opTarget.getSpan();
		    if (targets.size() > 0) {
			Element spanElem = new Element("span");
			opinionTargetElem.addContent(spanElem);
			for (Term target : targets) {
			    Element targetElem = new Element("target");
			    targetElem.setAttribute("id", target.getId());
			    if (target == span.getHead()) {
				targetElem.setAttribute("head", "yes");
			    }
			    spanElem.addContent(targetElem);
			}
		    }
		    opinionElem.addContent(opinionTargetElem);
		}
		Opinion.OpinionExpression expression = opinion.getOpinionExpression();
		if (expression != null) {
		    Element opinionExpressionElem = new Element("opinion_expression");
		    Comment comment = new Comment(StringEscapeUtils.escapeXml11(opinion.getSpanStr(opinion.getOpinionExpression().getSpan())));
		    opinionExpressionElem.addContent(comment);
		    if (expression.hasPolarity()) {
			opinionExpressionElem.setAttribute("polarity", expression.getPolarity());
		    }
		    if (expression.hasStrength()) {
			opinionExpressionElem.setAttribute("strength", expression.getStrength());
		    }
		    if (expression.hasSubjectivity()) {
			opinionExpressionElem.setAttribute("subjectivity", expression.getSubjectivity());
		    }
		    if (expression.hasSentimentSemanticType()) {
		    opinionExpressionElem.setAttribute("sentiment_semantic_type", expression.getSentimentSemanticType());
		    }
		    if (expression.hasSentimentProductFeature()) {
			opinionExpressionElem.setAttribute("sentiment_product_feature", expression.getSentimentProductFeature());
		    }
		    List<Term> targets = expression.getTerms();
		    Span<Term> span = expression.getSpan();
		    if (targets.size() > 0) {
			Element spanElem = new Element("span");
			opinionExpressionElem.addContent(spanElem);
			for (Term target : targets) {
			    Element targetElem = new Element("target");
			    targetElem.setAttribute("id", target.getId());
			    if (target == span.getHead()) {
				targetElem.setAttribute("head", "yes");
			    }
			    spanElem.addContent(targetElem);
			}
		    }
		    opinionElem.addContent(opinionExpressionElem);
		}

		opinionsElem.addContent(opinionElem);
	    }
	    root.addContent(opinionsElem);
	}

	List<Relation> relations = (List<Relation>)(List<?>)annotationContainer.getLayer(Layer.RELATIONS);
	if (relations.size() > 0) {
	    Element relationsElem = new Element("relations");
	    for (Relation relation : relations) {
		Comment comment = new Comment(StringEscapeUtils.escapeXml11(relation.getStr()));
		relationsElem.addContent(comment);
		Element relationElem = new Element("relation");
		relationElem.setAttribute("id", relation.getId());
		relationElem.setAttribute("from", relation.getFrom().getId());
		relationElem.setAttribute("to", relation.getTo().getId());
		if (relation.hasConfidence()) {
		    relationElem.setAttribute("confidence", String.valueOf(relation.getConfidence()));
		}
		relationsElem.addContent(relationElem);
	    }
	    root.addContent(relationsElem);
	}

	List<Predicate> predicates = (List<Predicate>)(List<?>)annotationContainer.getLayer(Layer.SRL);
	if (predicates.size() > 0) {
	    Element predicatesElem = new Element("srl");
	    for (Predicate predicate : predicates) {
		Comment predicateComment = new Comment(StringEscapeUtils.escapeXml11(predicate.getStr()));
		predicatesElem.addContent(predicateComment);
		Element predicateElem = new Element("predicate");
		predicateElem.setAttribute("id", predicate.getId());
		if (predicate.hasUri()) {
		    predicateElem.setAttribute("uri", predicate.getUri());
		}
		if (predicate.hasConfidence()) {
		    predicateElem.setAttribute("confidence", Float.toString(predicate.getConfidence()));
		}
		Span<Term> span = predicate.getSpan();
		if (span.getTargets().size() > 0) {
		    Comment spanComment = new Comment(StringEscapeUtils.escapeXml11(predicate.getSpanStr()));
		    Element spanElem = new Element("span");
		    predicateElem.addContent(spanComment);
		    predicateElem.addContent(spanElem);
		    for (Term target : span.getTargets()) {
			Element targetElem = new Element("target");
			targetElem.setAttribute("id", target.getId());
			if (target == span.getHead()) {
			    targetElem.setAttribute("head", "yes");
			}
			spanElem.addContent(targetElem);
		    }
		}
		List<ExternalRef> externalReferences = predicate.getExternalRefs();
		if (externalReferences.size() > 0) {
		    Element externalReferencesElem = externalReferencesToDOM(externalReferences);
		    predicateElem.addContent(externalReferencesElem);
		}
		for (Predicate.Role role : predicate.getRoles()) {
		    Element roleElem = new Element("role");
		    roleElem.setAttribute("id", role.getId());
		    roleElem.setAttribute("semRole", role.getSemRole());
		    Span<Term> roleSpan = role.getSpan();
		    if (roleSpan.getTargets().size() > 0) {
			Comment spanComment = new Comment(StringEscapeUtils.escapeXml11(role.getStr()));
			Element spanElem = new Element("span");
			roleElem.addContent(spanComment);
			roleElem.addContent(spanElem);
			for (Term target : roleSpan.getTargets()) {
			    Element targetElem = new Element("target");
			    targetElem.setAttribute("id", target.getId());
			    if (target == roleSpan.getHead()) {
				targetElem.setAttribute("head", "yes");
			    }
			    spanElem.addContent(targetElem);
			}
		    }
		    List<ExternalRef> rExternalReferences = role.getExternalRefs();
		    if (rExternalReferences.size() > 0) {
			Element externalReferencesElem = externalReferencesToDOM(rExternalReferences);
			roleElem.addContent(externalReferencesElem);
		    }
		    predicateElem.addContent(roleElem);
		}
		predicatesElem.addContent(predicateElem);
	    }
	    root.addContent(predicatesElem);
	}

        List<String> treeTypes = annotationContainer.getGroupIDs(AnnotationType.TREE);
	if (treeTypes.size() > 0) {
	    Element constituentsElem = new Element("constituency");	
	    for (String type : treeTypes) {
		List<Tree> trees = (List<Tree>)(List<?>)annotationContainer.getLayer(Layer.CONSTITUENCY, type);
		for (Tree tree : trees) {
		    Element treeElem = new Element("tree");
		    if (!tree.getType().equals(annotationContainer.DEFAULT_GROUP)) {
			treeElem.setAttribute("type", tree.getType());
		    }
		    constituentsElem.addContent(treeElem);
		    List<NonTerminal> nonTerminals = new LinkedList<NonTerminal>();
		    List<Terminal> terminals = new LinkedList<Terminal>();
		    List<Edge> edges = new ArrayList<Edge>();
		    TreeNode rootNode = tree.getRoot();
		    extractTreeNodes(rootNode, nonTerminals, terminals, edges);
		    Collections.sort(nonTerminals, new Comparator<NonTerminal>() {
			    public int compare(NonTerminal nt1, NonTerminal nt2) {
				if (cmpId(nt1.getId(), nt2.getId()) < 0) {
				    return -1;
				} else if (nt1.getId().equals(nt2.getId())) {
				    return 0;
				} else {
				    return 1;
				}
			    }
			});
		    Collections.sort(terminals, new Comparator<Terminal>() {
			    public int compare(Terminal t1, Terminal t2) {
				if (cmpId(t1.getId(), t2.getId()) < 0) {
				    return -1;
				} else if (t1.getId().equals(t2.getId())) {
				    return 0;
				} else {
				    return 1;
				}
			    }
			});
		    Comment ntCom = new Comment("Non-terminals");
		    treeElem.addContent(ntCom);
		    for (NonTerminal node : nonTerminals) {
			Element nodeElem = new Element("nt");
			nodeElem.setAttribute("id", node.getId());
			nodeElem.setAttribute("label", node.getLabel());
			treeElem.addContent(nodeElem);
		    }
		    Comment tCom = new Comment("Terminals");
		    treeElem.addContent(tCom);
		    for (Terminal node : terminals) {
			Element nodeElem = new Element("t");
			nodeElem.setAttribute("id", node.getId());
			nodeElem.addContent(createTermSpanElem(node.getSpan()));
			// Comment
			Comment tStrCom = new Comment(StringEscapeUtils.escapeXml11(node.getStr()));
			treeElem.addContent(tStrCom);
			treeElem.addContent(nodeElem);
		    }
		    Comment edgeCom = new Comment("Tree edges");
		    treeElem.addContent(edgeCom);
		    for (Edge edge : edges) {
			Element edgeElem = new Element("edge");
			if (edge.id != null) {
			    edgeElem.setAttribute("id", edge.id);
			}
			edgeElem.setAttribute("from", edge.from);
			edgeElem.setAttribute("to", edge.to);
			if (edge.head) {
			    edgeElem.setAttribute("head", "yes");
			}
			treeElem.addContent(edgeElem);
		    }
		}  
	    }
	    root.addContent(constituentsElem); 
	}

	List<Annotation> tempRels = (List<Annotation>)(List<?>)annotationContainer.getLayer(Layer.TEMPORAL_RELATIONS);
	if (tempRels.size() > 0) {
	    Element tempRelsElem = new Element("temporalRelations");
	    for (Annotation tempRel : tempRels) {
		if (tempRel instanceof TLink) {
		    TLink tLink = (TLink)tempRel;
		    Comment tLinkComment = new Comment
			(StringEscapeUtils.escapeXml11(tLink.getRelType() + "(" + tLink.getFrom().getId() + ", " + tLink.getTo().getId() + ")"));
		    tempRelsElem.addContent(tLinkComment);
		    Element tLinkElem = new Element("tlink");
		    tLinkElem.setAttribute("id", tLink.getId());
		    tLinkElem.setAttribute("from", tLink.getFrom().getId());
		    tLinkElem.setAttribute("to", tLink.getTo().getId());
		    tLinkElem.setAttribute("fromType", tLink.getFromType());
		    tLinkElem.setAttribute("toType", tLink.getToType());
		    tLinkElem.setAttribute("relType", tLink.getRelType());
		    tempRelsElem.addContent(tLinkElem);
		} else if (tempRel instanceof PredicateAnchor) {
		    PredicateAnchor predAnchor = (PredicateAnchor)tempRel;
		    Element predAnchorElem = new Element("predicateAnchor");
		    predAnchorElem.setAttribute("id", predAnchor.getId());
		    if (predAnchor.hasAnchorTime()) predAnchorElem.setAttribute("anchorTime", predAnchor.getAnchorTime().getId());
		    if (predAnchor.hasBeginPoint()) predAnchorElem.setAttribute("beginPoint", predAnchor.getBeginPoint().getId());
		    if (predAnchor.hasEndPoint()) predAnchorElem.setAttribute("endPoint", predAnchor.getEndPoint().getId());
		    Element spanElem = new Element("span");
		    Span<Predicate> span = predAnchor.getSpan();
		    for (Predicate target : span.getTargets()) {
			Element targetElem = new Element("target");
			targetElem.setAttribute("id", target.getId());
			if (target == span.getHead()) {
			    targetElem.setAttribute("head", "yes");
			}
			spanElem.addContent(targetElem);
		    }
		    predAnchorElem.addContent(spanElem);
		    tempRelsElem.addContent(predAnchorElem);
		}
	    }
	    root.addContent(tempRelsElem);
	}

	List<CLink> cLinks = (List<CLink>)(List<?>)annotationContainer.getLayer(Layer.CAUSAL_RELATIONS);
	if (cLinks.size() > 0) {
	    Element cLinksElem = new Element("causalRelations");
	    for (CLink cLink : cLinks) {
		String commentStr = "";
		if (cLink.hasRelType()) {
		    commentStr += cLink.getRelType();
		}
		commentStr += "(" + cLink.getFrom().getId() + ", " + cLink.getTo().getId() + ")";
		Comment cLinkComment = new Comment(StringEscapeUtils.escapeXml11(commentStr));
		cLinksElem.addContent(cLinkComment);
		Element cLinkElem = new Element("clink");
		cLinkElem.setAttribute("id", cLink.getId());
		cLinkElem.setAttribute("from", cLink.getFrom().getId());
		cLinkElem.setAttribute("to", cLink.getTo().getId());
		if (cLink.hasRelType()) {
		    cLinkElem.setAttribute("relType", cLink.getRelType());
		}
		cLinksElem.addContent(cLinkElem);
	    }
	    root.addContent(cLinksElem);
	}
	
	List<Topic> topics = (List<Topic>)(List<?>)annotationContainer.getLayer(Layer.TOPICS);
	if (topics.size() > 0) {
	    Element topicsElem = new Element("topics");
	    for (Topic topic : topics) {
		Element topicElem = new Element("topic");
		topicElem.setText(topic.getTopicValue());
		if (topic.hasSource()) {
		    topicElem.setAttribute("source", topic.getSource());
		}
		if (topic.hasMethod()) {
		    topicElem.setAttribute("method", topic.getMethod());
		}
		if (topic.hasURI()) {
		    topicElem.setAttribute("uri", topic.getURI());
		}
		if (topic.hasConfidence()) {
		    topicElem.setAttribute("confidence", Double.toString(topic.getConfidence()));
		}
		topicsElem.addContent(topicElem);
	    }
	    root.addContent(topicsElem);
	}
	
	List<Statement> statements = (List<Statement>)(List<?>)annotationContainer.getLayer(Layer.ATTRIBUTION);
	if (statements.size() > 0) {
	    Element attributionElem = new Element("attribution");
	    for (Statement statement : statements) {
		Element statementElem = new Element("statement");
		statementElem.setAttribute("id", statement.getId());
		Statement.StatementTarget stTarget = statement.getTarget();
		Element stTargetElem = new Element("statement_target");
		statementElem.addContent(stTargetElem);
		stTargetElem.addContent(spanToDOM(stTarget.getSpan()));
		if (statement.hasSource()) {
		    Statement.StatementSource stSource = statement.getSource();
		    Element stSourceElem = new Element("statement_source");
		    statementElem.addContent(stSourceElem);
		    stSourceElem.addContent(spanToDOM(stSource.getSpan()));
		}
		if (statement.hasCue()) {
		    Statement.StatementCue stCue= statement.getCue();
		    Element stCueElem = new Element("statement_cue");
		    statementElem.addContent(stCueElem);
		    stCueElem.addContent(spanToDOM(stCue.getSpan()));
		}
		attributionElem.addContent(statementElem);
	    }
	    root.addContent(attributionElem);
	}
	
	
	Set<Element> unknownLayers = annotationContainer.getUnknownLayers();
	for (Element layer : unknownLayers) {
	    layer.detach();
	    root.addContent(layer);
	}
	

	return doc;
    }
    
    private static <T extends IdentifiableAnnotation> Element spanToDOM(Span<T> span) {
	Element spanElem = new Element("span");
	for ( T target : span.getTargets()) {
	    Element targetElem = new Element("target");
	    targetElem.setAttribute("id", target.getId());
	    if (target == span.getHead()) {
		targetElem.setAttribute("head", "yes");
	    }
	    spanElem.addContent(targetElem);
	}
	return spanElem;
    }

    private static void termToDOM(Term term, boolean isComponent, Element termsElem) {
	String morphofeat;
	Term head;
	String termcase;
	if (!isComponent) {
	    Comment termComment = new Comment(StringEscapeUtils.escapeXml11(term.getStr()));
		//Comment termComment = new Comment(term.getStr());
	    termsElem.addContent(termComment);
	}
	String tag = (isComponent) ? "component" : "term";
	Element termElem = new Element(tag);
	termElem.setAttribute("id", term.getId());
	if (term.hasType()) {
	    termElem.setAttribute("type", term.getType());
	}
	if (term.hasLemma()) {
		//System.err.println(StringEscapeUtils.escapeHtml4(term.getLemma())+" "+StringEscapeUtils.escapeXml11(term.getLemma()));
	    termElem.setAttribute("lemma", StringEscapeUtils.escapeXml11(term.getLemma()));
	}
	if (term.hasPos()) {
	    termElem.setAttribute("pos", term.getPos());
	}
	if (term.hasMorphofeat()) {
	    termElem.setAttribute("morphofeat", term.getMorphofeat());
	}
	if (term.hasHead()) {
	    termElem.setAttribute("head", term.getHead().getId());
	}
	if (term.hasCase()) {
	    termElem.setAttribute("case", term.getCase());
	}
	if (term.hasSentiment()) {
	    Term.Sentiment sentiment = term.getSentiment();
	    Element sentimentElem = sentimentToDOM(sentiment);
	    termElem.addContent(sentimentElem);
	}
	if (!isComponent) {
	    Element spanElem = new Element("span");
	    Span<WF> span = term.getSpan();
	    for (WF target : term.getWFs()) {
		Element targetElem = new Element("target");
		targetElem.setAttribute("id", target.getId());
		if (target == span.getHead()) {
		    targetElem.setAttribute("head", "yes");
		}
		spanElem.addContent(targetElem);
	    }
	    termElem.addContent(spanElem);

	    List<Term> components = term.getComponents();
	    if (components.size() > 0) {
		for (Term component : components) {
		    termToDOM(component, true, termElem);
		}
	    }
	}
	List<ExternalRef> externalReferences = term.getExternalRefs();
	if (externalReferences.size() > 0) {
	    Element externalReferencesElem = externalReferencesToDOM(externalReferences);
	    termElem.addContent(externalReferencesElem);
	}
	termsElem.addContent(termElem);
    }

    private static void extractTreeNodes(TreeNode node, List<NonTerminal> nonTerminals, List<Terminal> terminals, List<Edge> edges) {
	if (node instanceof NonTerminal) {
	    nonTerminals.add((NonTerminal) node);
	    List<TreeNode> treeNodes = ((NonTerminal) node).getChildren();
	    for (TreeNode child : treeNodes) {
		edges.add(new Edge(child, node));
		extractTreeNodes(child, nonTerminals, terminals, edges);
	    }
	} else {
	    terminals.add((Terminal) node);
	}
    }

    private static Element externalReferencesToDOM(List<ExternalRef> externalRefs) {
	Element externalReferencesElem = new Element("externalReferences");
	for (ExternalRef externalRef : externalRefs) {
	    Element externalRefElem = externalRefToDOM(externalRef);
	    externalReferencesElem.addContent(externalRefElem);
	}
	return externalReferencesElem;
    }

    private static Element externalRefToDOM(ExternalRef externalRef) {
	Element externalRefElem = new Element("externalRef");
	externalRefElem.setAttribute("resource", externalRef.getResource());
	if (externalRef.hasReference()) {
	    externalRefElem.setAttribute("reference", externalRef.getReference());
	}
	if (externalRef.hasConfidence()) {
	    externalRefElem.setAttribute("confidence", Float.toString(externalRef.getConfidence()));
	}
	if (externalRef.hasReftype()) {
	    externalRefElem.setAttribute("reftype", externalRef.getReftype());
	}
	if (externalRef.hasStatus()) {
	    externalRefElem.setAttribute("status", externalRef.getStatus());
	}
	if (externalRef.hasSource()) {
	    externalRefElem.setAttribute("source", externalRef.getSource());
	}
	if (externalRef.hasSentiment()) {
	    Element sentimentElem = sentimentToDOM(externalRef.getSentiment());
	    externalRefElem.addContent(sentimentElem);
	}
	for(ExternalRef subExternalRef : externalRef.getExternalRefs()) {
	    Element subExternalRefElem = externalRefToDOM(subExternalRef);
	    externalRefElem.addContent(subExternalRefElem);
	}
	return externalRefElem;
    }

    private static Element sentimentToDOM(Term.Sentiment sentiment) {
	Element sentimentElem = new Element("sentiment");
	if (sentiment.hasResource()) {
	    sentimentElem.setAttribute("resource", sentiment.getResource());
	}
	if (sentiment.hasPolarity()) {
	    sentimentElem.setAttribute("polarity", sentiment.getPolarity());
	}
	if (sentiment.hasStrength()) {
	    sentimentElem.setAttribute("strength", sentiment.getStrength());
	}
	if (sentiment.hasSubjectivity()) {
	    sentimentElem.setAttribute("subjectivity", sentiment.getSubjectivity());
	}
	if (sentiment.hasSentimentSemanticType()) {
	    sentimentElem.setAttribute("sentiment_semantic_type", sentiment.getSentimentSemanticType());
	}
	if (sentiment.hasSentimentModifier()) {
	    sentimentElem.setAttribute("sentiment_modifier", sentiment.getSentimentModifier());
	}
	if (sentiment.hasSentimentMarker()) {
	    sentimentElem.setAttribute("sentiment_marker", sentiment.getSentimentMarker());
	}
	if (sentiment.hasSentimentProductFeature()) {
	    sentimentElem.setAttribute("sentiment_product_feature", sentiment.getSentimentProductFeature());
	}
	return sentimentElem;
    }
    
    private static Element factValToDOM(Factuality.FactVal factVal) {
	Element factValElem = new Element("factVal");
	factValElem.setAttribute("value", factVal.getValue());
	factValElem.setAttribute("resource", factVal.getResource());
	if (factVal.hasSource()) {
	    factValElem.setAttribute("source", factVal.getSource());
	}
	if (factVal.hasConfidence()) {
	    factValElem.setAttribute("confidence", factVal.getConfidence().toString());
	}
	return factValElem;
    }

    private static int cmpId(String id1, String id2) {
	int nbr1 = extractNumberFromId(id1);
	int nbr2 = extractNumberFromId(id2);
	if (nbr1 < nbr2) {
	    return -1;
	} else if (nbr1 == nbr2) {
	    return 0;
	} else {
	    return 1;
	}
    }

    private static int extractNumberFromId(String id) {
	Matcher matcher = Pattern.compile("^[a-z]*_?(\\d+)$").matcher(id);
	if (!matcher.find()) {
	    throw new IllegalStateException("IdManager doesn't recognise the given id's (" + id  + ") format. Should be [a-z]*_?[0-9]+");
	}
	return Integer.valueOf(matcher.group(1));
    }

}

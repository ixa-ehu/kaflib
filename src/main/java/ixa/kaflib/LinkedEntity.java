package ixa.kaflib;

import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.KAFDocument.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Linked Entity in the text.
 */
public class LinkedEntity extends IdentifiableAnnotation {

	/**
	 * LinedEntity's properties
	 */
	private String resource;
	private String reference;
	private double confidence;

	/**
	 * Mentions to the same entity (at least one required)
	 */
	private Span<WF> mentions;

	LinkedEntity(String id) {
	    super(id);
		this.mentions = new Span<WF>();
	}

	LinkedEntity(String id, Span<WF> mentions) {
		super(id);
		if (mentions.size() < 1) {
			throw new IllegalStateException("LinkedEntity must contain at least one reference span");
		}
//		if (mentions.get(0).size() < 1) {
//			throw new IllegalStateException("LinkedEntity' reference's spans must contain at least one target");
//		}
		this.mentions = mentions;
	}

	LinkedEntity(LinkedEntity linkedEntity, HashMap<String, WF> WFs) {
	    super(linkedEntity.getId());
		this.resource = linkedEntity.resource;
		this.reference = linkedEntity.reference;
		this.confidence = linkedEntity.confidence;

		String id = linkedEntity.getId();
		this.mentions = linkedEntity.getWFs();
//		for (Span<WF> span : linkedEntity.getSpans()) {
//			List<WF> targets = span.getTargets();
//			List<WF> copiedTargets = new ArrayList<WF>();
//			for (WF wf : targets) {
//				WF copiedWF = WFs.get(wf.getId());
//				if (copiedWF == null) {
//					throw new IllegalStateException("Term not found when copying " + id);
//				}
//				copiedTargets.add(copiedWF);
//			}
//			if (span.hasHead()) {
//				WF copiedHead = WFs.get(span.getHead().getId());
//				this.mentions.add(new Span<WF>(copiedTargets, copiedHead));
//			}
//			else {
//				this.mentions.add(new Span<WF>(copiedTargets));
//			}
//		}
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
	public Span<WF> getSpan() {
	    return this.mentions;
	}

	public String getSpanStr() {
		String str = "";
		for (WF wf : mentions.getTargets()) {
			if (!str.isEmpty()) {
				str += " ";
			}
			str += wf.getForm();
		}
		return str;
	}
	
	Map<AnnotationType, List<Annotation>> getReferencedAnnotations() {
	    Map<AnnotationType, List<Annotation>> referenced = new HashMap<AnnotationType, List<Annotation>>();
	    referenced.put(AnnotationType.WF, (List<Annotation>)(List<?>) this.getSpan().getTargets());
	    return referenced;
	}


	/**
	 * Returns the term targets of the first span. When targets of other spans are needed getReferences() method should be used.
	 */
	public Span<WF> getWFs() {
		return mentions;
//		if (this.mentions.size() > 0) {
//			return this.mentions.get(0).getTargets();
//		}
//		else {
//			return null;
//		}
	}
	    
	/*
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof LinkedEntity)) return false;
	    LinkedEntity ann = (LinkedEntity) o;
	    return Utils.areEquals(this.resource, ann.resource) &&
		    Utils.areEquals(this.reference, ann.reference) &&
		    Utils.areEquals(this.confidence, ann.confidence) &&
		    Utils.areEquals(this.mentions, ann.mentions);
	}
	*/

}

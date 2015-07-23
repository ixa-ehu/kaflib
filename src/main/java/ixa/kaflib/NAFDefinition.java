package ixa.kaflib;

import ixa.kaflib.Opinion.OpinionExpression;
import ixa.kaflib.Opinion.OpinionHolder;
import ixa.kaflib.Opinion.OpinionTarget;
import ixa.kaflib.Predicate.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NAFDefinition implements Serializable {
    
    public enum Layer {
	TEXT,
	TERMS,
	ENTITIES,
	CHUNKS,
	DEPS,
	CONSTITUENCY,
	COREFERENCES,
	OPINIONS,
	CAUSAL_RELATIONS,
	TEMPORAL_RELATIONS,
	SRL,
	TIME_EXPRESSIONS,
	FACTUALITIES,
	FACTUALITY_LAYER,
	MARKABLES,
	PROPERTIES,
	CATEGORIES,
	RELATIONS,
	LINKED_ENTITIES,
	TOPICS,
	ATTRIBUTION,
    }

    public enum AnnotationType {
	WF,
	TERM,
	MW,
	COMPONENT,
	SENTIMENT,
	ENTITY,
	CHUNK,
	DEP,
	TREE,
	NON_TERMINAL,
	TERMINAL,
	EDGE,
	COREF,
	OPINION,
	OPINION_HOLDER,
	OPINION_TARGET,
	OPINION_EXPRESSION,
	CLINK,
	TLINK,
	PREDICATE_ANCHOR,
	PREDICATE,
	ROLE,
	TIMEX3,
	FACTUALITY,
	FACTVALUE,
	MARK,
	PROPERTY,
	CATEGORY,
	LINKED_ENTITY,
	RELATION,
	TOPIC,
	STATEMENT,
	STATEMENT_TARGET,
	STATEMENT_SOURCE,
	STATEMENT_CUE,
    }
    
    private List<AnnotationType> highLevelAnnotationTypes;
    private Map<AnnotationType, Layer> highLevelAnnotationType2Layer;
    private Map<Layer, List<AnnotationType>> layer2highLevelAnnotationTypes;
    private Map<AnnotationType, Class<?>> annotationTypeClasses;
    
    private static final long serialVersionUID = 42L; // Serializable...
    

    NAFDefinition() {
	highLevelAnnotationTypes = Arrays.asList(
		AnnotationType.WF,
		AnnotationType.TERM,
		AnnotationType.ENTITY,
		AnnotationType.CHUNK,
		AnnotationType.DEP,
		AnnotationType.TREE,
		AnnotationType.COREF,
		AnnotationType.OPINION,
		AnnotationType.CLINK,
		AnnotationType.TLINK,
		AnnotationType.PREDICATE_ANCHOR,
		AnnotationType.PREDICATE,
		AnnotationType.TIMEX3,
		AnnotationType.FACTUALITY,
		AnnotationType.FACTVALUE,
		AnnotationType.MARK,
		AnnotationType.PROPERTY,
		AnnotationType.CATEGORY,
		AnnotationType.LINKED_ENTITY,
		AnnotationType.RELATION,
		AnnotationType.TOPIC,
		AnnotationType.STATEMENT);

	highLevelAnnotationType2Layer = new HashMap<AnnotationType, Layer>();
	addTypeLayerMapping(AnnotationType.WF, Layer.TEXT);
	addTypeLayerMapping(AnnotationType.TERM, Layer.TERMS);
	addTypeLayerMapping(AnnotationType.ENTITY, Layer.ENTITIES);
	addTypeLayerMapping(AnnotationType.CHUNK, Layer.CHUNKS);
	addTypeLayerMapping(AnnotationType.DEP, Layer.DEPS);
	addTypeLayerMapping(AnnotationType.TREE, Layer.CONSTITUENCY);
	addTypeLayerMapping(AnnotationType.COREF, Layer.COREFERENCES);
	addTypeLayerMapping(AnnotationType.OPINION, Layer.OPINIONS);
	addTypeLayerMapping(AnnotationType.CLINK, Layer.CAUSAL_RELATIONS);
	addTypeLayerMapping(AnnotationType.TLINK, Layer.TEMPORAL_RELATIONS);
	addTypeLayerMapping(AnnotationType.PREDICATE_ANCHOR, Layer.TEMPORAL_RELATIONS);
	addTypeLayerMapping(AnnotationType.PREDICATE, Layer.SRL);
	addTypeLayerMapping(AnnotationType.TIMEX3, Layer.TIME_EXPRESSIONS);
	addTypeLayerMapping(AnnotationType.FACTUALITY, Layer.FACTUALITIES);
	addTypeLayerMapping(AnnotationType.FACTVALUE, Layer.FACTUALITY_LAYER);
	addTypeLayerMapping(AnnotationType.MARK, Layer.MARKABLES);
	addTypeLayerMapping(AnnotationType.PROPERTY, Layer.PROPERTIES);
	addTypeLayerMapping(AnnotationType.CATEGORY, Layer.CATEGORIES);
	addTypeLayerMapping(AnnotationType.LINKED_ENTITY, Layer.LINKED_ENTITIES);
	addTypeLayerMapping(AnnotationType.RELATION, Layer.RELATIONS);
	addTypeLayerMapping(AnnotationType.TOPIC, Layer.TOPICS);
	addTypeLayerMapping(AnnotationType.STATEMENT, Layer.ATTRIBUTION);

	annotationTypeClasses = new HashMap<AnnotationType, Class<?>>();
	annotationTypeClasses.put(AnnotationType.WF, WF.class);
	annotationTypeClasses.put(AnnotationType.TERM, Term.class);
	annotationTypeClasses.put(AnnotationType.COMPONENT, Term.class);
	annotationTypeClasses.put(AnnotationType.MW, Term.class);
	annotationTypeClasses.put(AnnotationType.ENTITY, Entity.class);
	annotationTypeClasses.put(AnnotationType.CHUNK, Chunk.class);
	annotationTypeClasses.put(AnnotationType.DEP, Dep.class);
	annotationTypeClasses.put(AnnotationType.TREE, Tree.class);
	annotationTypeClasses.put(AnnotationType.NON_TERMINAL, NonTerminal.class);
	annotationTypeClasses.put(AnnotationType.TERMINAL, Terminal.class);
	annotationTypeClasses.put(AnnotationType.COREF, Coref.class);
	annotationTypeClasses.put(AnnotationType.OPINION, Opinion.class);
	annotationTypeClasses.put(AnnotationType.OPINION_HOLDER, OpinionHolder.class);
	annotationTypeClasses.put(AnnotationType.OPINION_TARGET, OpinionTarget.class);
	annotationTypeClasses.put(AnnotationType.OPINION_EXPRESSION, OpinionExpression.class);
	annotationTypeClasses.put(AnnotationType.CLINK, CLink.class);
	annotationTypeClasses.put(AnnotationType.TLINK, TLink.class);
	annotationTypeClasses.put(AnnotationType.PREDICATE, Predicate.class);
	annotationTypeClasses.put(AnnotationType.ROLE, Role.class);
	annotationTypeClasses.put(AnnotationType.TIMEX3, Timex3.class);
	annotationTypeClasses.put(AnnotationType.FACTUALITY, Factuality.class);
	annotationTypeClasses.put(AnnotationType.FACTVALUE, Factvalue.class);
	annotationTypeClasses.put(AnnotationType.MARK, Mark.class);
	annotationTypeClasses.put(AnnotationType.PROPERTY, Feature.class);
	annotationTypeClasses.put(AnnotationType.CATEGORY, Feature.class);
	annotationTypeClasses.put(AnnotationType.LINKED_ENTITY, LinkedEntity.class);
	annotationTypeClasses.put(AnnotationType.RELATION, Relation.class);
	annotationTypeClasses.put(AnnotationType.TOPIC, Topic.class);
	annotationTypeClasses.put(AnnotationType.STATEMENT, Statement.class);
    }
    
    private void addTypeLayerMapping(AnnotationType type, Layer layer) {
	highLevelAnnotationType2Layer.put(type, layer);
	List<AnnotationType> types = layer2highLevelAnnotationTypes.get(layer);
	if (types == null) {
	    types = new ArrayList<AnnotationType>();
	    layer2highLevelAnnotationTypes.put(layer, types);
	}
	types.add(type);
    }
    
    public List<AnnotationType> getHighLevelAnnotations() {
	return highLevelAnnotationTypes;
    }
    
    public Layer getLayer(AnnotationType type) {
	return highLevelAnnotationType2Layer.get(type);
    }
    
    public Class<?> getAnnotationClass(AnnotationType type) {
	return annotationTypeClasses.get(type);
    }
    
    public List<AnnotationType> getAnnotations(Layer layer) {
	return layer2highLevelAnnotationTypes.get(layer);
    }
    
}

package ixa.kaflib;

import java.util.ArrayList;
import java.util.List;


public class ExternalReferences {

    private List<ExternalRef> externalRefs;

    ExternalReferences() {
	this.externalRefs = new ArrayList<ExternalRef>();
    }

    public List<ExternalRef> get() {
	return this.externalRefs;
    }
    
    public void add(ExternalRef externalRef) {
	this.externalRefs.add(externalRef);
    }
    
    public void add(List<ExternalRef> externalRefs) {
	this.externalRefs.addAll(externalRefs);
    }
    
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof ExternalReferences)) return false;
	ExternalReferences extRefs = (ExternalReferences) o;
	return this.externalRefs.equals(extRefs.externalRefs);
    }
}

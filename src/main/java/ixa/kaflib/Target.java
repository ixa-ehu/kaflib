package ixa.kaflib;

public class Target<T> {
    //private String target;
    private T target;

    Target(T target) {
	this.target = target;
    }

    public T getTarget() {
	return this.target;
    }

    public void setTarget(T target) {
	this.target = target;
    }
}

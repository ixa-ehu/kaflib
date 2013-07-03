package ixa.kaflib;

public class KAFNotValidException extends Exception {

    private static final String commonMsg = "Input KAF document is not valid.";

    public KAFNotValidException(String msg) {
	super(commonMsg + " " + msg);
    }

}

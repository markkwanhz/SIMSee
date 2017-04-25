package util.exception;

public class MaxFrequencyException extends Exception {
    private static final long serialVersionUID = -6459225049580278166L;

    public MaxFrequencyException(){
        super("Max frequency cannot be smaller than fundamental frequency");
    }
}

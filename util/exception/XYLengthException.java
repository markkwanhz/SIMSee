package util.exception;

public class XYLengthException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public XYLengthException(){
        super("The length of vector x & y is not equal.");
    }
}

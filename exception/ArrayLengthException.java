package exception;

public class ArrayLengthException extends Exception {
    private static final long serialVersionUID = 4350066976269318335L;

    public ArrayLengthException(){
        super("Length of the array is illegal.");
    }
}

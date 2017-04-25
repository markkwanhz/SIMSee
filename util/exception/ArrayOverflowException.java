package util.exception;

public class ArrayOverflowException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public ArrayOverflowException(){
        super("Subscript out of range!");
    }

}

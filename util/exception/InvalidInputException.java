package util.exception;

public class InvalidInputException extends Exception {
    private static final long serialVersionUID = 1278667825043831767L;

    public InvalidInputException(){
        super("Your input is invalid, please check!");
    }
}

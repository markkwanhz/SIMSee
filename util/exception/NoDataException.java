package util.exception;

public class NoDataException extends Exception {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public NoDataException() {
        super("Error! No data has been loaded!");
    }

}

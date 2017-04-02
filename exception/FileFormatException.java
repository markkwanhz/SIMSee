package exception;

public class FileFormatException extends Exception {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public FileFormatException() {
        super("Error! Data file has unexpected format!");
    }

}

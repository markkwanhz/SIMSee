package fileread;

import database.DataSection;
import exception.FileFormatException;

import java.io.StringReader;
import java.util.Scanner;

/**
 * An example of info file: PGB(1) Output Desc="U:1" Group="Main" Max=2.0
 * Min=-2.0 Units=""
 */
public class InfoReader extends FileReader {
    public InfoReader(String f, DataSection d) {
        super(f, d);
    }

    protected String[] readString(String src) throws FileFormatException {
        String[] des = new String[6];
        Scanner inString = new Scanner(new StringReader(src));
        inString.next();
        des[0] = inString.next();
        des[1] = extractQuo(inString.next());
        des[2] = extractQuo(inString.next());
        des[3] = inString.next().substring(4);
        des[4] = inString.next().substring(4);
        des[5] = extractQuo(inString.next());
        inString.close();
        return des;
    }

    private static String extractQuo(String src) throws FileFormatException {
        String des = "";
        int index1 = src.indexOf('\"', 0);
        int index2 = src.indexOf('\"', index1 + 1);
        if (index1 == -1) {
            throw new FileFormatException();
        }
        des = src.substring(index1 + 1, index2);
        return des;
    }

    protected void register(String[] s, DataSection d) {
        d.registerInfo(s);
    }

    protected void postProgress() {
    }
}

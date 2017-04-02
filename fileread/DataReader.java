package fileread;

import database.DataSection;
import exception.FileFormatException;

public class DataReader extends FileReader {
    public DataReader(String f, DataSection d) {
        super(f, d);
    }

    protected String[] readString(String src) throws FileFormatException {
        String[] temp = src.split(" +");
        if (temp.length == 0) {
            return null;
        }
        String[] des = new String[temp.length - 1];
        System.arraycopy(temp, 1, des, 0, des.length);
        return des;
    }

    protected void register(String[] s, DataSection d) {
        for (int k = 0; k < s.length; k++) {
            d.registerData(k, Double.parseDouble(s[k]));
        }
    }

    protected void postProgress() {
        this.data.postProgress();
    }
}

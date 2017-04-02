package fileread;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import database.DataSection;
import exception.FileFormatException;

public abstract class FileReader {
    String fileName;
    DataSection data;

    public FileReader(String f, DataSection d) {
        this.fileName = f;
        this.data = d;
    }

    public void readFile() throws IOException, FileFormatException {
        FileInputStream fis = new FileInputStream(this.fileName);
        Scanner infoScan = new Scanner(fis);
        String buff = "";
        String[] s;

        while (infoScan.hasNext()) {
            buff = infoScan.nextLine();
            if (buff.equals("") == false) {
                s = readString(buff);
                if (s == null)
                    continue;
                register(s, this.data);
            }
        }
        this.postProgress();
        fis.close();
    }

    abstract protected String[] readString(String src)
            throws FileFormatException;

    abstract protected void register(String[] s, DataSection d);

    abstract protected void postProgress();
}

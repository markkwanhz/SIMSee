package fileread;

import database.DataBase;
import exception.FileFormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.Scanner;

/**
*An example of info file:
*PGB(1) Output Desc="U:1" Group="Main" Max=2.0 Min=-2.0 Units=""
*/
public class InfoReader {
	static void readInfoFile(String f, DataBase d) 
			throws FileNotFoundException, FileFormatException{
		FileInputStream fis = new FileInputStream(f);
		Scanner infoScan = new Scanner(fis);
		String buff = "";
		String[] s = new String[6];

		while (infoScan.hasNext()){
			buff = infoScan.nextLine();
			if(buff.equals("")==false){
			    readString(buff,s);
			    d.registerInfo(s);
			}
		}
	}
	static private void readString(String src, String[] des) 
			throws FileFormatException{
		Scanner inString = new Scanner(new StringReader(src));
		inString.next();
		des[0] = inString.next();
		des[1] = extractQuo(inString.next());
		des[2] = extractQuo(inString.next());
		des[3] = inString.next().substring(4);
		des[4] = inString.next().substring(4);
		des[5] = extractQuo(inString.next());
	}
	static private String extractQuo(String src) throws FileFormatException{
		String des = "";
		int index1 = src.indexOf('\"', 0);
    	int index2 = src.indexOf('\"', index1+1);
    	if(index1 == -1){
    		throw new FileFormatException();
    	}
    	des = src.substring(index1+1, index2);
		return des;
	}
	public static void main(String[] args) 
			throws FileNotFoundException, FileFormatException{
		String file = "C:/Users/M/Documents/test.gf42/test.inf";
		DataBase db = new DataBase();
		readInfoFile(file,db);
		System.out.println(db.listTypes("type"));
		System.out.println(db.listTypes("group"));
		System.out.println(db.listNames("type", "Output"));		
		return;
	}
}

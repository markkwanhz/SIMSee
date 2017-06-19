package util.fileread;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;

import util.database.DataSection;

public class GsonReader {
    private static String[] BlankStringArray = {};
    private String fileName;
    private DataSection data;

    public GsonReader(String f, DataSection d){
        fileName = f;
        data = d;
    }
    
    public void readFile() 
            throws FileNotFoundException{
        FileInputStream fis = new FileInputStream(fileName);
        InputStreamReader in = new InputStreamReader(fis);
        Gson gson = new Gson();
        Examples example = gson.fromJson(in, Examples.class);
        Iterator<Map.Entry<String, ArrayList<DataBlock>>> it = example.entrySet().iterator();
        Map.Entry<String, ArrayList<DataBlock>> entry;
        ArrayList<DataBlock> list;
        DataBlock currentBlock;
        while(it.hasNext()){
            entry = it.next();
            list = entry.getValue();
            for(int k = 0; k<list.size(); k++){
                currentBlock = list.get(k);
                if(currentBlock.cmd.equals("draw")){
                    Map<String, ArrayList<ArrayList<Double>>> channels = (Map<String, ArrayList<ArrayList<Double>>>)currentBlock.data;
                    Iterator<Map.Entry<String, ArrayList<ArrayList<Double>>>> it2 = channels.entrySet().iterator();
                    Map.Entry<String, ArrayList<ArrayList<Double>>> entry2;
                    ArrayList<ArrayList<Double>> series;
                    ArrayList<Double> pair;
                    while(it2.hasNext()){
                        entry2 = it2.next();
                        //data.registerInfo(entry2.getKey(), BlankStringArray, BlankStringArray);
                        series = entry2.getValue();
                        for(int j = 0; j<series.size(); j++){
                            pair = series.get(j);
                            
                        }
                    }
                }
            }
        }
    }
    
    public static void main(String[] args) {
        GsonReader gsonReader = new GsonReader("C:/Users/M/Documents/Tsinghua/大三下/JAVA/结果数据.json", null);
        try {
            gsonReader.readFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return;
    }

}

class DataBlock{
    public String cmd;
    public Object data;
    public String machine_mac;
    public Integer task_id;
    public Integer type;
}

class Examples extends HashMap<String, ArrayList<DataBlock>>{
    private static final long serialVersionUID = 4255151909758017345L;
    
}

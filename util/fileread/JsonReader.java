package util.fileread;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.google.gson.Gson;

import util.database.DataSection;

public class JsonReader {
    private String fileName;
    private DataSection data;
    private FileInputStream fis;
    private InputStreamReader in;

    public JsonReader(String f, DataSection d)
            throws FileNotFoundException{
        fileName = f;
        data = d;
        fis = new FileInputStream(fileName);
        in = new InputStreamReader(fis);
        taskIDDesired = "";
    }
    
    
    private Examples example;
    public Vector<String> analyseFile(){
        Gson gson = new Gson();
        example = gson.fromJson(in, Examples.class);
        Vector<String> taskIDList = new Vector<>();
        for(Object obj : example.keySet()){
            String key = (String) obj;
            taskIDList.add(key);
        }
        return taskIDList;
    }
    
    private String taskIDDesired;
    public void setTaskID(String taskID){
        taskIDDesired = taskID;
    }
    
    public void readFile()
            throws IOException{
        ArrayList<DataBlock> desiredTask = example.get(taskIDDesired);
        //Iterator<Map.Entry<String, ArrayList<DataBlock>>> taskIDIterator = example.entrySet().iterator();
        //Map.Entry<String, ArrayList<DataBlock>> entry;
        //ArrayList<DataBlock> list;
        DataBlock currentBlock;
        //while(taskIDIterator.hasNext()){
            //entry = taskIDIterator.next();
            //list = entry.getValue();
            for(int k = 0; k<desiredTask.size(); k++){
                currentBlock = desiredTask.get(k);
                if(currentBlock.cmd.equals("draw")/*&&currentBlock.task_id==Integer.parseInt(taskIDDesired)*/){
                    @SuppressWarnings("unchecked")
                    Map<String, ArrayList<ArrayList<Double>>> channels = (Map<String, ArrayList<ArrayList<Double>>>)currentBlock.data;
                    Iterator<Map.Entry<String, ArrayList<ArrayList<Double>>>> it2 = channels.entrySet().iterator();
                    Map.Entry<String, ArrayList<ArrayList<Double>>> entry2;
                    ArrayList<ArrayList<Double>> series;
                    ArrayList<Double> pair;
                    String signalName;
                    while(it2.hasNext()){
                        entry2 = it2.next();
                        series = entry2.getValue();
                        signalName = entry2.getKey();
                        data.registerInfo(
                                signalName, 
                                DataSection.CLOUDPSSField, 
                                new String[]{signalName, Integer.toString(currentBlock.task_id), currentBlock.machine_mac, Integer.toString(currentBlock.type)}, 
                                new String[]{"task_id","machine_mac"},
                                new String[]{Integer.toString(currentBlock.task_id),currentBlock.machine_mac}
                                );
                        for(int j = 0; j<series.size(); j++){
                            pair = series.get(j);
                            BigDecimal bg = new BigDecimal(pair.get(0)).setScale(6, RoundingMode.HALF_UP);
                            data.registerData(signalName, bg.doubleValue(), pair.get(1));
                        }
                    }
                }
            }
        //}
        in.close();
        fis.close();
        data.postProgress();
    }
    
    public static void main(String[] args) {
        DataSection data = new DataSection();
        try {
            JsonReader jsonReader = new JsonReader("C:/Users/M/Documents/Tsinghua/大三下/JAVA/数据结果-2.json", data);
            jsonReader.analyseFile();
            //jsonReader.readFile();
        } catch (IOException e) {
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

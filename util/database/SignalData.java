package util.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import util.exception.ArrayLengthException;
import util.exception.ArrayOverflowException;
import util.exception.NoDataException;

public class SignalData {

    //Properties
//    private String signalType;
//    private String signalName;
//    private String signalGroup;
//    private double max, min;
//    private String units;
    private HashMap<String, String> properties;
    
    //Critical field
    private ArrayList<Double> timeBuff;
    private ArrayList<Double> doubleBuff;
    private double[] time;
    private double[] data;
    private double resolution;
    
    public SignalData(String[] field, String[] value) {
//        if(s==null){
//            String[] t = {"","","","0","0",""};
//            s = t;
//        }
//        this.signalType = s[0];
//        this.signalName = s[1];
//        this.signalGroup = s[2];
//        this.max = Double.parseDouble(s[3]);
//        this.min = Double.parseDouble(s[4]);
//        this.units = s[5];
        if(field.length != value.length){
            try {
                throw new ArrayLengthException();
            } catch (ArrayLengthException e) {
                e.printStackTrace();
            }
        }
        properties = new HashMap<>();
        for(int index = 0; index<field.length; index++){
            properties.put(field[index], value[index]);
        }
        doubleBuff = new ArrayList<Double>();
        timeBuff = new ArrayList<Double>();
    }
    
    public double getRes(){
        return resolution;
    }

    /**
     * show whether a given time index is legal
     * @param time
     * @return true(legal) or false(illegal)
     */
    public boolean isLegalTime(double time){
        return (time>=this.time[0])&&(time<=this.time[this.time.length-1]);
    }
    
    public double[][] queryArray(int index, int num) throws NoDataException,
            ArrayOverflowException {
        if (this.size() == 0) {
            throw new NoDataException();
        } else if (index < 0 || index + num > this.size()) {
            throw new ArrayOverflowException();
        }
        double[][] queryAns = new double[2][];
        queryAns[0] = new double[num];
        queryAns[1] = new double[num];
        System.arraycopy(this.time, index, queryAns[0], 0, num);
        System.arraycopy(this.data, index, queryAns[1], 0, num);
        return queryAns;
    }
    
    public double[][] queryFullArray() throws NoDataException{
        int size = this.size();
        if (size == 0){
            throw new NoDataException();
        }
        double[][] queryAns = new double[2][];
        queryAns[0] = new double[size];
        queryAns[1] = new double[size];
        System.arraycopy(this.time, 0, queryAns[0], 0, size);
        System.arraycopy(this.data, 0, queryAns[1], 0, size);
        return queryAns;
    }
    
    public double queryRecord(int index){
        return index>this.data.length?this.data[data.length-1]:this.data[index];
    }

    public void postProgress() {
        int len = this.doubleBuff.size();
        this.data = new double[len];
        for (int k = 0; k < len; k++) {
            this.data[k] = this.doubleBuff.get(k);
        }
        this.doubleBuff.clear();
        this.doubleBuff = null;
        
        this.time = new double[len];
        for (int k = 0; k < len; k++){
            this.time[k] = this.timeBuff.get(k);
        }
        this.timeBuff.clear();
        this.timeBuff = null;
        
        if(len>2){
            resolution = time[1] - time[0];
        }
    }

    public void add(double record) {
        this.doubleBuff.add(record);
    }
    
    public void registerData(double time, double value){
        this.doubleBuff.add(value);
        this.timeBuff.add(time);
    }

    public int size() {
        return this.data.length;
    }

    @Override
    public String toString() {
//        return "Signal Tpye: " + this.signalType + ";\n" + "Signal Name: "
//                + this.signalName + ";\n" + "Signal Group: " + this.signalGroup
//                + ";\n" + "Number of data: " + this.size() + "\n"
//                + "Maximum value: " + this.max + "\n" + "Minimum value "
//                + this.min + "\n" + "Units: " + this.units;
        String ans = "<html>";
        Iterator<Map.Entry<String, String>> it = properties.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry<String, String>)it.next();
            ans += "<B>" + entry.getKey() + ":</B>  " + entry.getValue() + "<br>";
        }
        ans += "</html>";
        return ans;
    }
}

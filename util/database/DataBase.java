package util.database;

import java.util.HashMap;
import java.util.Iterator;

import util.exception.ArrayOverflowException;
import util.exception.NoDataException;

public class DataBase extends HashMap<String, SignalData> {
    private static final long serialVersionUID = 1L;
//    private double resolution;

    public DataBase() {
        super();
    }

    public void registerSignalCol(String name, String[] field, String[] value) {
        this.put(name, new SignalData(field, value));
    }

    public void registerData(String signalName, double time, double signal) {
        this.get(signalName).registerData(time, signal);
    }

    public void postProgress() {
        Iterator<java.util.Map.Entry<String, SignalData>> it = this.entrySet()
                .iterator();
        while (it.hasNext()) {
            it.next().getValue().postProgress();
        }
    }

    /**
     * query data from database
     * @param timeStart
     * @param num length of the desired part
     * @param signalName 
     * @return A two-dimension array, [0] stores time and [1] stores signal
     * @throws NoDataException
     * @throws ArrayOverflowException
     */
    public double[][] queryData(double timeStart, int num, String signalName)
            throws NoDataException, ArrayOverflowException {
        int start = (int) Math.round(timeStart / getRes(signalName));
        double[][] ans = this.get(signalName).queryArray(start, num);
        return ans;
    }
    
    public double[][] queryFullData(String signalName)
            throws NoDataException{
        return this.get(signalName).queryFullArray();
    }
    
    public double queryRecord(double time, String signalName){
        int index = (int) Math.round(time / getRes(signalName));
        double r1 = this.get(signalName).queryRecord(index);
        double r2 = this.get(signalName).queryRecord(index+1);
        double record = r1 + (time - getRes(signalName)*(double)index)*(r2-r1);
        return record;
    }
    
    public String querySignalInfo(String signalName){
        return this.get(signalName).toString();
    }
    
    public double getRes(String signalName){
        return this.get(signalName).getRes();
    }
    
    public boolean isLegalTime(String signalName, double time){
        return this.get(signalName).isLegalTime(time);
    }
    
//    public void setRes(double res){
//        this.resolution = res;
//    }
}

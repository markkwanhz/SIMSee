package util.database;

import java.util.HashMap;
import java.util.Iterator;

import util.exception.ArrayOverflowException;
import util.exception.NoDataException;

public class DataBase extends HashMap<String, SignalData> {
    private static final long serialVersionUID = 1L;
    private double resolution;

    public DataBase() {
        super();
        String[] time = { "Time", "Time", "Time", "0", "0", "s" };
        this.put("Time", new SignalData(DataSection.PSCADField,time));
    }

    public void registerSignalCol(String name, String[] field, String[] value) {
        this.put(name, new SignalData(field, value));
    }

    public void registerData(String signalName, double signal) {
        this.get(signalName).add(signal);
    }

    public void postProgress() {
        Iterator<java.util.Map.Entry<String, SignalData>> it = this.entrySet()
                .iterator();
        while (it.hasNext()) {
            it.next().getValue().postProgress();
        }
    }

    public double[] queryData(double timeStart, int num, String signalName)
            throws NoDataException, ArrayOverflowException {
        int start = (int) (timeStart / resolution);
        double[] ans = this.get(signalName).queryArray(start, num);
        return ans;
    }
    
    public double queryRecord(double time, String signalName){
        int index = (int) (time/resolution);
        double r1 = this.get(signalName).queryRecord(index);
        double r2 = this.get(signalName).queryRecord(index+1);
        double record = r1 + (time - resolution*(double)index)*(r2-r1);
        return record;
    }
    
    public String querySignalInfo(String signalName){
        return this.get(signalName).toString();
    }
    
    public double getRes(){
        return this.resolution;
    }
    
    public void setRes(double res){
        this.resolution = res;
    }
    
    @Override
    public void clear(){
        super.clear();
        resolution = 0;
    }
}

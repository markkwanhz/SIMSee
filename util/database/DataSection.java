package util.database;

import java.util.ArrayList;

import util. data.TimeSeriesData;
import util.exception.ArrayOverflowException;
import util.exception.NoDataException;
import util.exception.XYLengthException;

public class DataSection {
    private DataBase data;
    private PropertyBase property;
    private ArrayList<String> signalName;
    private SignalData time;

    public DataSection() {
        data = new DataBase();
        property = new PropertyBase();
        signalName = new ArrayList<String>();
        time = new SignalData(null);
    }

    public void registerInfo(String[] s) {
        this.property.registerInfo(s);
        this.data.registerSignalCol(s);
        this.signalName.add(s[1]);
    }

    public String listTypes(int index) {
        return this.property.listTypes(index);
    }

    public String listNames(int index, String type) {
        return this.property.listNames(index, type);
    }

    public void registerData(int signalIndex, double signal) {
        if (signalIndex == 0) {
            this.time.add(signal);
        } else {
            this.data.registerData(signalName.get(signalIndex - 1), signal);
        }
    }

    public TimeSeriesData queryData(double timeStart, double timeEnd, String signalName)
            throws NoDataException, ArrayOverflowException {
        if(timeStart<this.time.queryRecord(0)||timeEnd>this.time.queryRecord(this.time.size()-1)){
            throw new ArrayOverflowException();
        }
        int size = (int) ((timeEnd - timeStart)/this.data.getRes()) + 1;
        int key = (int) (timeStart/this.data.getRes());
        double[][]ans = new double[2][];
        double res = this.data.getRes();
        ans[0] = this.time.queryArray(key, size);
        ans[1] = this.data.queryData(timeStart, size, signalName);
        try {
            return new TimeSeriesData(ans, signalName,res);
        } catch (XYLengthException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    public double queryRecord(double time, String signalName) 
            throws ArrayOverflowException{
        if(time<0 || time > this.time.queryRecord(this.time.size()-1))
            throw new ArrayOverflowException();
        return this.data.queryRecord(time, signalName);
    }

    public void postProgress() {
        this.data.postProgress();
        this.time.postProgress();
        double tr = this.time.queryRecord(1) - this.time.queryRecord(0);
        this.data.setRes(tr);
    }
    
    public TimeSeriesData[] getAllData() throws NoDataException, ArrayOverflowException{
        int len = this.signalName.size();
        double start = this.time.queryRecord(0);
        double end = this.time.queryRecord(this.time.size()-1);
        TimeSeriesData[] data = new TimeSeriesData[len];
        for(int k = 0; k<len; k++){
            data[k] = this.queryData(start, end, this.signalName.get(k));
        }
        return data;
    }

}

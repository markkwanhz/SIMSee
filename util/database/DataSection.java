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
    
    public static String[] PSCADField = {"Signal Type", "Signal Name", "Signal Group", "Max", "Min", "Units"};
    public static String[] BlankField = {};

    public DataSection() {
        data = new DataBase();
        property = new PropertyBase();
        signalName = new ArrayList<String>();
        time = new SignalData(BlankField,BlankField);
    }

    /**
     * New a signal in the database
     * @param name signal's name
     * @param field the property-name list of the signal
     * @param value the property-value list of the signal
     * @param tags the tag-list of the signal
     * @param prop the prop-list (ordered by the <b>tags</b>) of the signal
     */
    public void registerInfo(String name, String[] field, String[] value, 
            String[] tags, String[] prop) {
        if(data.get(name)==null){
            for(int k = 0; k<tags.length; k++){
                this.property.registerInfo(tags[k], prop[k], name);
            }
            this.data.registerSignalCol(name, field, value);
            this.signalName.add(name);
        }
    }

    /**
     * List all available signal types in the data section
     * @return 
     */
    public String[] listTypes() {
        String[] ans = this.property.listTypes();
        return ans;
    }

    /**
     * List all available signals that fit the given 
     * key and type in the data section
     * @param tag "group" or "type", etc
     * @param type available categories in "group" or "type" 
     * according to <b>key</b>
     * @return
     */
    public String[] listNames(String tag, String type) {
        return this.property.listNames(tag, type).split(" ");
    }
    
    /**
     * List all available signals in the data section
     * @return
     */
    public Object[] listNames(){
        return signalName.toArray();
    }

    /**
     * register data into the database with signal index
     * @param signalIndex
     * @param signal the value(double)
     */
    public void registerData(int signalIndex, double signal) {
        if (signalIndex == 0) {
            this.time.add(signal);
        } else {
            this.data.registerData(signalName.get(signalIndex - 1), signal);
        }
    }
    
    /**
     * register data into the database with signal name
     * @param signalName
     * @param signal the value(double)
     */
    public void registerData(String signalName, double signal){
        
    }

    /**
     * Query to the database for the given signal name to get data within the given time period.
     * @param timeStart start of the period;
     * @param timeEnd end of the period;
     * @param signalName the signal that is being queried.
     * @return A TimeSeriesData instance containing the desired data.
     * @throws NoDataException No such signal has been recorded.
     * @throws ArrayOverflowException The given time period is out of bound.
     */
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
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get the full array of a given signal
     * @param signalName
     * @return
     * @throws NoDataException
     * @throws ArrayOverflowException
     */
    public TimeSeriesData querySignal(String signalName) 
            throws NoDataException, ArrayOverflowException{
        double[][]ans = new double[2][];
        ans[0] = this.time.queryArray(0, this.time.size());
        ans[1] = this.data.queryData(0, this.time.size(), signalName);
        double res = this.data.getRes();
        try {
            return new TimeSeriesData(ans, signalName,res);
        } catch (XYLengthException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get the given signal's value at the given moment.
     * @param time The moment.
     * @param signalName The signal's name.
     * @return The value.
     * @throws ArrayOverflowException
     */
    public double queryRecord(double time, String signalName) 
            throws ArrayOverflowException{
        if(time<0 || time > this.time.queryRecord(this.time.size()-1))
            throw new ArrayOverflowException();
        return this.data.queryRecord(time, signalName);
    }
    
    public String querySignalInfo(String signalName){
        return this.data.querySignalInfo(signalName);
    }

    public void postProgress() {
        this.data.postProgress();
        this.time.postProgress();
        double tr = this.time.queryRecord(1) - this.time.queryRecord(0);
        this.data.setRes(tr);
    }
    /**
     * Get all data from the database.
     * @return A TimeSeriesData array.
     * @throws NoDataException 
     * @throws ArrayOverflowException
     */
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
    
    /**
     * Reset the data section to initial condition
     */
    public void resetDataSection(){
        data.clear();
        property.clear();
        signalName.clear();
        time = new SignalData(BlankField, BlankField);
    }
   
}

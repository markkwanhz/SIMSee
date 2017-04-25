package util.data;

import java.util.ArrayList;

import util.exception.XYLengthException;

/**
 * 时间序列数据，double[][]xy为[0]时间+[1]数据
 * @author M
 *
 */
public class TimeSeriesData {
    private ArrayList<Double> signal;
    private ArrayList<Double> time;
    private double[][] xySeries;
    private double resolution;
    private String signalName;
    
    public TimeSeriesData(String signalName, double resolution){
        this.signalName = signalName;
        this.resolution = resolution;
        signal = new ArrayList<Double>();
        time = new ArrayList<Double>();
    }
    
    public TimeSeriesData(double[][] xy, String signalName, double resolution)
            throws XYLengthException{
        if(xy[0].length!=xy[1].length){
            throw new XYLengthException();
        }
        this.xySeries = xy;
        this.signalName = signalName;
        this.resolution = resolution;
    }
    
    public double[][] getData(){
        return this.xySeries;
    }
    
    public String getName(){
        return this.signalName;
    }
    
    public double getRes(){
        return this.resolution;
    }
    
    public void add(double time,double signal){
        this.time.add(time);
        this.signal.add(signal);
    }
    
    public void arrange(){
        int len = time.size();
        xySeries = new double[2][len];
        for(int k = 0; k<len; k++){
            xySeries[0][k] = time.get(k);
            xySeries[1][k] = signal.get(k);
        }
        time.clear();
        signal.clear();
        time = null;
        signal = null;
    }
    
}

package data;

import exception.XYLengthException;

/**
 * 时间序列数据，double[][]xy为[0]时间+[1]数据
 * @author M
 *
 */
public class TimeSeriesData {
    private double[][] xySeries;
    private double resolution;
    private String signalName;
    
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
}

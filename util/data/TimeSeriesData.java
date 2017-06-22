package util.data;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math3.analysis.function.Abs;

import ui.chart.MyRange;
import util.exception.ArrayOverflowException;
import util.exception.XYLengthException;
import util.fft.FFTProperties;

/**
 * 时间序列数据，double[][]xy为[0]时间+[1]数据。有两种构造方法：1. 直接使用double[][]进行构造；2.
 * 用signal name构造，可以动态添加元素，但是在使用前必须调用arrange()
 * 
 * @author M
 *
 */
public class TimeSeriesData {
    private ArrayList<Double> signal;
    private ArrayList<Double> time;
    private double[][] xySeries;
    private double resolution;
    private String signalName;
    private MyRange xRange;
    private MyRange yRange;

    public TimeSeriesData(String signalName, double resolution) {
        this.signalName = signalName;
        this.resolution = resolution;
        signal = new ArrayList<Double>();
        time = new ArrayList<Double>();
    }

    public TimeSeriesData(double[][] xy, String signalName, double resolution)
            throws XYLengthException {
        if (xy[0].length != xy[1].length) {
            throw new XYLengthException();
        }
        this.xySeries = xy;
        this.signalName = signalName;
        this.resolution = resolution;
        
        double lower, upper;
        lower = xySeries[0][0]; 
        upper = xySeries[0][xySeries[0].length-1];
        this.xRange = new MyRange(lower, upper);
        
        lower = upper = xySeries[1][0];
        for(int k = 0; k<xySeries[1].length; k++){
            if(xySeries[1][k]>upper){
                upper = xySeries[1][k];
            } else if(xySeries[1][k]<lower){
                lower = xySeries[1][k];
            }
        }
        this.yRange = new MyRange(lower, upper);
    }

    public double[][] getData() {
        return this.xySeries;
    }

    public String getName() {
        return this.signalName;
    }

    public double getRes() {
        return this.resolution;
    }
    
    public double size(){
        return this.xySeries[0].length;
    }
    
    public MyRange getDomainRange(){
        return xRange;
    }
    
    public MyRange getRangeRange(){
        return yRange;
    }

    public void add(double time, double signal) {
        this.time.add(time);
        this.signal.add(signal);
    }

    public void arrange() {
        this.xRange = new MyRange(Collections.min(time), Collections.max(time));
        this.yRange = new MyRange(Collections.min(signal), Collections.max(signal));
        
        int len = time.size();
        xySeries = new double[2][len];
        for (int k = 0; k < len; k++) {
            xySeries[0][k] = time.get(k);
            xySeries[1][k] = signal.get(k);
        }
        time.clear();
        signal.clear();
        time = null;
        signal = null;
    }
    
    public int getAvailableValueIndex(double benchmark)
            throws ArrayOverflowException{
        int hi = xySeries[0].length - 1, lo = 0, index;
        if(benchmark > xySeries[0][hi] || benchmark < xySeries[0][0])
            throw new ArrayOverflowException();
        while(lo<hi-1){
            index = (lo+hi)/2;
            if(benchmark > xySeries[0][index]){
                lo = index;
            }
            else {
                hi = index;
            }
        }
        Abs abs = new Abs();
        double gap1 = abs.value(benchmark - xySeries[0][lo]);
        double gap2 = abs.value(benchmark - xySeries[0][hi]);
        return gap1>gap2? hi:lo;
    }
    
    public double getAvailableValue(double benchmark) 
            throws ArrayOverflowException{
        return xySeries[0][getAvailableValueIndex(benchmark)];
    }
    
    public TimeSeriesData[] split(double start, FFTProperties p) 
            throws ArrayOverflowException, XYLengthException{
        double fundamentalFrequency = Double.parseDouble(p.getProperty("FundamentalFrequency"));
        int cycles = Integer.parseInt(p.getProperty("NumberOfCycle"));
        
        TimeSeriesData[] ans = new TimeSeriesData[3];
        int index = getAvailableValueIndex(start);
        int fftLen = (int) (1./(fundamentalFrequency*resolution)*cycles);
        if(index + fftLen > xySeries[0].length)
            throw new ArrayOverflowException();
        
        double[][] fftSrc = new double[2][fftLen];
        System.arraycopy(xySeries[0], index, fftSrc[0], 0, fftLen);
        System.arraycopy(xySeries[1], index, fftSrc[1], 0, fftLen);
        ans[0] = new TimeSeriesData(fftSrc, "FFTSrc", resolution);
        
        if(index == 0){
            ans[1] = null;
        } else {
            double [][] front = new double[2][index];
            System.arraycopy(xySeries[0], 0, front[0], 0, index);
            System.arraycopy(xySeries[1], 0, front[1], 0, index);
            ans[1] = new TimeSeriesData(front, "front", resolution);
        }
        
        int backLen = xySeries[0].length - (index + fftLen);
        if(backLen == 0){
            ans[2] = null;
        } else {
            double [][] back = new double[2][backLen];
            System.arraycopy(xySeries[0], index + fftLen, back[0], 0, backLen);
            System.arraycopy(xySeries[1], index + fftLen, back[1], 0, backLen);
            ans[2] = new TimeSeriesData(back, "back", resolution);
        }
        return ans;
    }

}

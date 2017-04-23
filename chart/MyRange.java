package chart;

import org.jfree.data.Range;

public class MyRange {
    private Range range;
    
    public MyRange(double lower, double upper){
        this.range = new Range(lower,upper);
    }
    
    public void setRange(Range r){
        this.range = r;
    }
    
    public double getUpperBound(){
        return this.range.getUpperBound();
    }
    
    public double getLowerBound(){
        return this.range.getLowerBound();
    }
    
    public double getLength(){
        return this.range.getLength();
    }
    
    public Range getRange(){
        return this.range;
    }
}

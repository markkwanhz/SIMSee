package ui.chart;

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
    
    /**
     * Adjust the input range to make sure it will not go beyond current MyRange
     * @param r The range you want to adjust
     * @return The adjusted range
     */
    public Range adjustRange(Range r){
        if(r.getLength()>range.getLength()){
            return range;
        } else {
            double up = r.getUpperBound();
            double upp = range.getUpperBound();
            double low = r.getLowerBound();
            double loww = range.getLowerBound();
            if(up>upp){
                return new Range(upp-r.getLength(), upp);
            } else if (low<loww) {
                return new Range(loww, loww+r.getLength());
            } else {
                return r;
            }
        }
    }
}

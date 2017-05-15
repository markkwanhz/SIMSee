package util.power;

import util.data.TimeSeriesData;
import util.exception.ArrayLengthException;

public class PowerAnalysis {
    private TimeSeriesData u, i;
    private double fundamentalF;
    
    public PowerAnalysis() throws ArrayLengthException{
        this(null,null,0);
    }
    
    public PowerAnalysis(TimeSeriesData ud, TimeSeriesData id, double ff) 
            throws ArrayLengthException {
        if (ud != null && id != null) {
            setSignal(ud, id);
            fundamentalF = ff;
        }
    }
    
    public TimeSeriesData getPower(){
        if(u==null||i==null)
            return null;
        double[] time = u.getData()[0];
        double[] vol = u.getData()[1];
        double[] cur = i.getData()[1];
        int index = 0, ii;
        double r = time[1] - time[0];
        int sampleCnt = (int) (1/(r*fundamentalF));
        double p;
        TimeSeriesData power = new TimeSeriesData("Power", 1/fundamentalF);
        while(index + sampleCnt <= time.length){
            p = 0;
            for(ii = index;ii<index+sampleCnt;ii++){
                p += vol[ii]*cur[ii];
            }
            p *= (r*fundamentalF);
            power.add(time[index]+0.5/fundamentalF, p);
            index = ii;
        }
        power.arrange();
        return power;
    }
    
    public void setSignal(TimeSeriesData ud, TimeSeriesData id) 
            throws ArrayLengthException{
        if (ud.getData()[0].length != id.getData()[0].length) {
            throw new ArrayLengthException();
        } else {
            u = ud;
            i = id;
        }
    }
    
    public void setFF(double ff){
        fundamentalF = ff;
    }
    
}

package util.data;

/**
 * 数据内容为FFT结果
 * @author M
 */
public class FFTData {
    private double[] freq;
    private double[] value;
    private double[] argu;
    private double sampleTime;
    private double THD;
    
    public FFTData(){
        freq = null;
        value = null;
        argu = null;
        sampleTime = 0;
    }
    
    public FFTData(double[] f, double[] v, double[] arg, double st, double THD){
        this.freq = f;
        this.value = v;
        this.argu = arg;
        this.sampleTime = st;
        this.THD = THD;
    }
    
    public void setFreq(double[] f){
        this.freq = f;
    }
    
    public void setValue(double[] v){
        this.value = v;
    }
    
    public void setArgu(double[] a){
        this.argu = a;
    }
    
    public void setSampleTime(double st){
        this.sampleTime = st;
    }
    
    public void setTHD(double THD){
        this.THD = THD;
    }
    
    public double[] getFreq(){
        return this.freq;
    }
    
    public double[] getValue(){
        return this.value;
    }
    
    public double[] getArgu(){
        return this.argu;
    }
    
    public double getSampleTime(){
        return this.sampleTime;
    }
    
    public double getTHD(){
        return this.THD;
    }
    
    public boolean isEmpty(){
        if(freq == null)
            return true;
        else
            return false;
    }
}

package fft;

import java.util.Properties;

/**
 * 控制FFT的参数表<br/>
 * 参数项:<br/>
 * <b>StartTime</b>&nbsp&nbsp开始时间<br/>
 * <b>NumberOfCycle</b>&nbsp&nbsp要进行FFT变换的周期数<br/>
 * <b>FundamentalFrequency</b>&nbsp&nbsp基波频率<br/>
 * <b>MaxFrequency</b>&nbsp&nbsp待显示的最大频率<br/>
 * <b>MaxTHDFrequency</b>&nbsp&nbsp计算THD的最大频率 1-Nyquist频率 2-待显示的最大频率<br/>
 * <b>FrequencyAxis</b>&nbsp&nbsp频率轴种类 1-Hertz 2-Harmonic order<br/> 
 * @author M
 *
 */
public class FFTProperties extends Properties {
    private static final long serialVersionUID = 1L;
    
    public static final String NyquistFrequency = "NyquistFrequency";
    public static final String MaxDisplayFrequency = "MaxDisplayFrequency";

    public FFTProperties(){
        super();
        this.put("StartTime", "0");
        this.put("NumberOfCycle", "1");
        this.put("FundamentalFrequency", "50");
        this.put("MaxFrequency", "1000");
        this.put("MaxTHDFrequency", MaxDisplayFrequency);
        this.put("FrequencyAxis", "Hertz");
    }
}

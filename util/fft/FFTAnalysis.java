package util.fft;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.analysis.function.Min;
import org.apache.commons.math3.analysis.function.Sqrt;
import org.apache.commons.math3.complex.Complex;
import org.jtransforms.fft.DoubleFFT_1D;

import util.data.FFTData;
import util.data.TimeSeriesData;
import util.exception.ArrayLengthException;
import util.exception.MaxFrequencyException;

/**
 * This class is used to do FFT Analysis.
 * @author M
 *
 */
public class FFTAnalysis {
    private static double PI = 3.1415926535897932384626433832795;

    private double[] yData;
    private double res;
    private FFTProperties prop;

    /**
     * Constructor. 
     * @param data Only the y-value is required, so you can construct this 
     * TimeSeriesData with the two-dimension array {{y-value},{y-value}}
     * instead of {{x-value (time)},{y-value}}
     * @param p The FFTProperties
     */
    public FFTAnalysis(TimeSeriesData data, FFTProperties p) {
        this.yData = data.getData()[1];
        this.res = data.getRes();
        this.prop = p;
    }

    /**
     * Change the fft data. 
     * @param data Only the y-value is required, so you can construct this 
     * TimeSeriesData with the two-dimension array {{y-value},{y-value}}
     * instead of {{x-value (time)},{y-value}}
     * @param p The FFTProperties
     */
    public void setFFT(TimeSeriesData data, FFTProperties p) {
        this.yData = data.getData()[1];
        this.res = data.getRes();
        this.prop = p;
    }

    /**
     * 把用double存储的复数数组转换成Complex对象数组
     * 
     * @param dou
     *            用double存储的复数数组，dou[2k]为实部，dou[2k+1]为虚部
     * @return 返回一个Complex对象数组
     * @exception ArrayLengthException
     *                传入数组长度不是偶数
     */
    private Complex[] douToCom(double[] dou) throws ArrayLengthException {
        int len = dou.length;
        if (len % 2 != 0) {
            throw new ArrayLengthException();
        }
        len = len / 2;
        Complex[] com = new Complex[len];
        for (int k = 0; k < len; k++) {
            com[k] = new Complex(dou[2 * k], dou[2 * k + 1]);
        }
        return com;
    }

    /**
     * 获取幅角数组
     * 
     * @param com
     *            Complex对象数组
     * @return 返回一个double数组，内容为com里对应元素的幅角
     */
    private double[] getArgument(Complex[] com, int len) {
        Min min = new Min();
        len = (int) min.value(com.length / 2 + 1, len);
        double[] args = new double[len];
        for (int k = 0; k < len; k++) {
            args[k] = com[k].getArgument() / (2 * PI) * 360 + 90;
        }
        return args;
    }

    /**
     * 获取复数的模数组
     * 
     * @param com
     *            Complex对象数组
     * @return 返回一个double数组，内容为com里对应元素的模
     */
    private double[] getAbsValue(Complex[] com) {
        // 总采样点数为len
        int len = com.length;
        int len2 = len / 2 + 1;
        double[] v = new double[len2];
        for (int k = 1; k < len2; k++) {
            v[k] = com[k].abs() * 2. / len;
        }
        v[0] = com[0].abs() / len;

        if (len % 2 == 0) {
            v[len2 - 1] = com[len2 - 1].abs() / len;
        } else {
            v[len2 - 1] = com[len2 - 1].abs() * 2 / len;
        }
        return v;
    }

    /**
     * 获取频率轴数组
     * 
     * @param 无
     * @return 返回一个double数组，内容为各分量频率
     */
    private double[] getFreq() {
        int len = yData.length / 2 + 1;
        double[] temp = new double[len];
        String fAxis = this.prop.getProperty("FrequencyAxis");
        double ff =  fAxis.equals(FFTProperties.Hertz)?Double.parseDouble(this.prop
                .getProperty("FundamentalFrequency")) : 1;
        double cnt = Double.parseDouble(this.prop.getProperty("NumberOfCycle"));
        double upperBound = Double.parseDouble(this.prop
                .getProperty("MaxFrequency"))*ff/Double.parseDouble(this.prop
                        .getProperty("FundamentalFrequency"));
        Abs abs = new Abs();
        int k;
        for (k = 0; k < len; k++) {
            temp[k] = k * ff / cnt;
            if (temp[k] > upperBound && abs.value(temp[k] - upperBound) > 1E-2) {
                break;
            }
        }
        double[] freq = new double[k];
        System.arraycopy(temp, 0, freq, 0, k);
        return freq;
    }

    public FFTData fftAnalyse() throws ArrayLengthException,
            MaxFrequencyException {
        int len = yData.length;
        DoubleFFT_1D fftDo = new DoubleFFT_1D(len);
        double[] fft = new double[len * 2];
        System.arraycopy(yData, 0, fft, 0, len);
        fftDo.realForwardFull(fft);

        Complex[] com = douToCom(fft);
        double[] freq = getFreq();
        if (freq.length == 1) {
            throw new MaxFrequencyException();
        }
        double[] ang = getArgument(com, freq.length);

        Min min = new Min();
        double[] valueTemp = getAbsValue(com);
        double[] value = new double[(int) min.value(valueTemp.length,
                freq.length)];
        System.arraycopy(valueTemp, 0, value, 0, value.length);

        double THD = 0;
        Sqrt root = new Sqrt();
        int maxTHD = prop.getProperty("MaxTHDFrequency").equals(
                FFTProperties.NyquistFrequency) ? valueTemp.length
                : value.length;
        for (int k = 2; k < maxTHD; k++) {
            THD += valueTemp[k] * valueTemp[k];
        }
        THD = root.value(THD) / valueTemp[Integer.parseInt(prop.getProperty("NumberOfCycle"))];
        return new FFTData(freq, value, ang, this.res, THD);
    }
}

package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.UIManager;

import ui.chart.FFTResultsPanel;
import ui.util.DpiSetting;
import util.data.FFTData;
import util.data.TimeSeriesData;
import util.exception.XYLengthException;
import util.fft.FFTAnalysis;
import util.fft.FFTProperties;

public class TestFFTPanel {

    public static void main(String[] args) throws Exception {
        String s1 = "C:/Users/M/Documents/Tsinghua/JAVA/DataVisualization/test.txt";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(s1);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        double []signal = new double[2000];
        Scanner din = new Scanner(fis);
        int index = 0;
        while(din.hasNext()){
            signal[index++] = din.nextDouble();
        }
        try {
            din.close();
            fis.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        double[][] d = new double[2][];
        d[0] = d[1] = signal;
        TimeSeriesData dd = null;
        try {
            dd = new TimeSeriesData(d, "test", 1e-5);
        } catch (XYLengthException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        FFTProperties fftP = new FFTProperties();
        fftP.put("MaxFrequency", "3000");
        fftP.put("MaxTHDFrequency", FFTProperties.NyquistFrequency);
        FFTAnalysis fft = new FFTAnalysis(dd, fftP);
        FFTData ans = fft.fftAnalyse();
        
        //for screen dpi setting
        //int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        DpiSetting.updateDPI();
        FFTResultsPanel fftp = new FFTResultsPanel(ans);
        JFrame haha = new JFrame();
        haha.add(fftp);
        haha.pack();
        haha.setVisible(true);
        haha.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
//        fftP.put("MaxFrequency", "1000");
//        fftP.put("MaxTHDFrequency", FFTProperties.MaxDisplayFrequency);
//        fft.setFFT(dd, fftP);
//        ans = fft.fftAnalyse();
//        fftp.setDataSet(ans);
        return;
    }
}

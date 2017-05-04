package test;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;

import ui.chart.FFTSrcPanel;
import ui.util.DpiSetting;
import util.data.TimeSeriesData;
import util.database.DataSection;
import util.exception.ArrayOverflowException;
import util.exception.FileFormatException;
import util.exception.NoDataException;
import util.exception.XYLengthException;
import util.fft.FFTProperties;
import util.fileread.*;

public class TestFFTSrc {
    public static void main(String[] args) throws IOException,
            FileFormatException, NoDataException, ArrayOverflowException, XYLengthException {
        String s1 = "C:/Users/M/Documents/PSCAD/test.gf42/test.inf";
        String s2 = "C:/Users/M/Documents/PSCAD/test.gf42/test_01.out";
        DataSection data = new DataSection();
        FileReader fr = new InfoReader(s1, data);
        fr.readFile();
        fr = new DataReader(s2, data);
        fr.readFile();
        
        try
        {
            //BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencyAppleLike;
            //org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            DpiSetting.updateDPI();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        FFTSrcPanel fp = new FFTSrcPanel();
        JFrame frame = new JFrame("a plot panel");
        frame.setContentPane(fp);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        FFTProperties fftp = new FFTProperties();
        TimeSeriesData d = data.querySignal("U:1");
        fp.setData(d);
        fp.setControl(fftp);
        
        fftp.setProperty("StartTime", "0.202");
        fp.setControl(fftp);
        fp.switchFig(FFTSrcPanel.FFTWindow);
        
        return;
    }
}

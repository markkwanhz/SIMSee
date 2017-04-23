package test;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;

import chart.TimeSeriesPanel;
import data.TimeSeriesData;
import database.DataSection;
import exception.ArrayOverflowException;
import exception.FileFormatException;
import exception.NoDataException;
import exception.XYLengthException;
import fileread.*;

public class TestSeriesPanel {
    public static void main(String[] args) throws IOException,
            FileFormatException, NoDataException, ArrayOverflowException, XYLengthException {
        String s1 = "C:/Users/M/Documents/test.gf42/test.inf";
        String s2 = "C:/Users/M/Documents/test.gf42/test_01.out";
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
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        TimeSeriesPanel fp = new TimeSeriesPanel();
        JFrame frame = new JFrame("a plot panel");
        frame.setContentPane(fp);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TimeSeriesData[] xy = data.getAllData();
        fp.addData(xy);
        
        return;
    }
}

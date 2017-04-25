package test;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;

import ui.chart.TimeSeriesPanel;
import ui.util.DpiSetting;
import util.data.TimeSeriesData;
import util.database.DataSection;
import util.exception.ArrayLengthException;
import util.exception.ArrayOverflowException;
import util.exception.FileFormatException;
import util.exception.NoDataException;
import util.fileread.DataReader;
import util.fileread.FileReader;
import util.fileread.InfoReader;
import util.power.PowerAnalysis;

public class TestPower {

    public static void main(String[] args) 
            throws IOException, FileFormatException, ArrayLengthException, NoDataException, ArrayOverflowException {
        String s1 = "C:/Users/M/Documents/test.gf42/test.inf";
        String s2 = "C:/Users/M/Documents/test.gf42/test_01.out";
        DataSection data = new DataSection();
        FileReader fr = new InfoReader(s1, data);
        fr.readFile();
        fr = new DataReader(s2, data);
        fr.readFile();
        
        PowerAnalysis pa = new PowerAnalysis(data.queryData(0, 0.5, "U:1"),
                data.queryData(0, 0.5, "I:1"),50);
        TimeSeriesData ans = pa.getPower();
        
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            DpiSetting.updateDPI();
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
        fp.addData(ans);
        
        return;
    }

}

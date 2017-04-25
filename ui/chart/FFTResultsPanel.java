package ui.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.DefaultIntervalXYDataset;

import ui.util.DpiSetting;
import util.data.FFTData;

public class FFTResultsPanel extends JPanel {
    private static final long serialVersionUID = -3597241434501104993L;

    //Tabbed Panel
    private JTabbedPane contentPanel;
    
    //BarChart Panel Components
    private ChartPanel cp;
    private JFreeChart barChart;
    private XYPlot xyPlot;
    
    //List Panel Components
    private JScrollPane jsp;
    private JList<String> list;
    private DefaultListModel<String> listContent;
    
    //data
    private DefaultIntervalXYDataset dataSet;
    private FFTData fftResults;
    
    public FFTResultsPanel(FFTData result) throws Exception{
        super();
        
        dataSet = new DefaultIntervalXYDataset();
        barChart = ChartFactory.createXYBarChart(null, 
                "Frequency", 
                false, 
                "Amplitude",
                dataSet, 
                PlotOrientation.VERTICAL, 
                false, true, false);
        cp = new ChartPanel(barChart,false, false, false, false, false);
        cp.setMouseZoomable(false);
        xyPlot = barChart.getXYPlot();
        xyPlot.setBackgroundPaint(Color.WHITE);
        xyPlot.setDomainGridlinesVisible(false);
        xyPlot.setRangeGridlinePaint(Color.GRAY);
        XYBarRenderer r = (XYBarRenderer) xyPlot.getRenderer();
        r.setGradientPaintTransformer(null);
        r.setBarPainter(new StandardXYBarPainter());
        r.setSeriesPaint(0, Color.BLUE);
        //r.setShadowVisible(true);
        
        listContent = new DefaultListModel<String>();
        list = new JList<String>(listContent);
        list.setFont(new Font("Consolas",Font.PLAIN,DpiSetting.getNormalFontSize()));
        jsp = new JScrollPane(list);
        
        contentPanel = new JTabbedPane();
        contentPanel.add("Bar Chart",cp);
        contentPanel.add("List", jsp);
        contentPanel.setBackground(Color.WHITE);
        
        setLayout(new BorderLayout());
        add(contentPanel,"Center");
        setBackground(Color.WHITE);
        
        if(result != null){
            setDataSet(result);
        }
    }
    
    public void setDataSet(FFTData fftData) throws Exception{
        fftResults = fftData;
        if(fftData.isEmpty()){
            throw new Exception("Empty fftData!");
        }
        double[][] result = new double[6][];
        int len = fftData.getFreq().length;
        result[0] = fftData.getFreq().clone();
        double freqRes = result[0][1] - result[0][0];
        result[1] = new double[len];
        result[2] = new double[len];
        for(int k = 0; k<len; k++){
            result[1][k] = result[0][k] - freqRes/5;
            result[2][k] = result[0][k] + freqRes/5;
        }
        result[3] = fftData.getValue().clone();
        result[4] = new double[len];
        result[5] = new double[len];
        
        dataSet.addSeries("FFTResults", result);
        DecimalFormat format = new DecimalFormat("#.##%");
        String sTHD = format.format(fftResults.getTHD());
        barChart.setTitle("Fundamental (" + (int)result[0][1] + " Hz) = " 
               + String.format("%.2f", result[3][1]) + ", THD = " + sTHD);
        updateList();
    }
    
    private void updateList(){
        listContent.removeAllElements();
        String temp;
        double[] freq = fftResults.getFreq();
        double[] value = fftResults.getValue();
        double[] angle = fftResults.getArgu();
        
        DecimalFormat format1 = new DecimalFormat("#.##%");
        DecimalFormat format2 = new DecimalFormat("0.000E0");
        
        temp = "Samples per cycle = " + (int)(1/(freq[1]*fftResults.getSampleTime()));
        listContent.addElement(temp);
        
        temp = "DC Component      = " + format2.format(value[0]);
        listContent.addElement(temp);
        
        temp = "Fundamental       = ";
        temp += format2.format(value[1]) + " peak " + "(";
        temp += format2.format(value[1]/1.414213562373095) +" rms)";
        listContent.addElement(temp);
        
        temp = "THD               = " + format1.format(fftResults.getTHD());
        listContent.addElement(temp);
        
        temp = " ";
        listContent.addElement(temp);
        
        int len = freq.length;
        for(int k = 0; k<len; k++){
            temp = String.format("%6d", (int)(freq[k])) + String.format("%5s", "Hz :");
            temp += String.format("%16.3f", value[k]);
            temp += "     ";
            temp += String.format("% 8.2f", angle[k]);
            temp += "°";
            listContent.addElement(temp);
        }
    }
    
}

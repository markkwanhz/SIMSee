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
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.DefaultIntervalXYDataset;

import ui.util.DpiSetting;
import util.data.FFTData;
import util.fft.FFTProperties;

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
    private FFTProperties fftp;
    
    public FFTResultsPanel(FFTData result, FFTProperties fftp) throws Exception{
        super();
        
        this.fftp = fftp;
        
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
        cp.setMaximumDrawWidth(3200);
        cp.setMaximumDrawHeight(1800);
        xyPlot = barChart.getXYPlot();
        xyPlot.setBackgroundPaint(Color.WHITE);
        xyPlot.setDomainGridlinesVisible(false);
        xyPlot.setRangeGridlinePaint(Color.GRAY);
        XYBarRenderer r = (XYBarRenderer) xyPlot.getRenderer();
        r.setGradientPaintTransformer(null);
        r.setBarPainter(new StandardXYBarPainter());
        r.setSeriesPaint(0, Color.BLUE);
        Font font = new Font("Times New Roman", Font.TRUETYPE_FONT, DpiSetting.getNormalFontSize());
        xyPlot.getDomainAxis().setTickLabelFont(font);
        xyPlot.getRangeAxis().setTickLabelFont(font);
        font = new Font("Times New Roman", Font.BOLD, DpiSetting.getMenuSize());
        xyPlot.getDomainAxis().setLabelFont(font);
        xyPlot.getRangeAxis().setLabelFont(font);
        
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
        Font font = new Font("Arial", Font.BOLD, DpiSetting.getTitleSize());
        TextTitle t = new TextTitle("Fundamental (" + Integer.parseInt(fftp.getProperty("FundamentalFrequency")) + " Hz) = " 
               + String.format("%.2f", result[3][Integer.parseInt(fftp.getProperty("NumberOfCycle"))]) + ","
                       + " THD = " + sTHD, font);
        barChart.setTitle(t);
        updateList();
    }
    
    private void updateList(){
        listContent.removeAllElements();
        String temp;
        double[] freq = fftResults.getFreq();
        double[] value = fftResults.getValue();
        double[] angle = fftResults.getArgu();
        int fundamentalIndex = Integer.parseInt(fftp.getProperty("NumberOfCycle"));
        
        DecimalFormat format1 = new DecimalFormat("#.##%");
        DecimalFormat format2 = new DecimalFormat("0.000E0");
        
        temp = "Samples per cycle = " + (int)(1/(freq[fundamentalIndex]*fftResults.getSampleTime()));
        listContent.addElement(temp);
        
        temp = "DC Component      = " + format2.format(value[0]);
        listContent.addElement(temp);
        
        temp = "Fundamental       = ";
        temp += format2.format(value[fundamentalIndex]) + " peak " + "(";
        temp += format2.format(value[fundamentalIndex]/1.414213562373095) +" rms)";
        listContent.addElement(temp);
        
        temp = "THD               = " + format1.format(fftResults.getTHD());
        listContent.addElement(temp);
        
        temp = " ";
        listContent.addElement(temp);
        
        int len = freq.length;
        for(int k = 0; k<len; k++){
            temp = String.format("%9.2f", (freq[k]));
            if(fftp.getProperty("FrequencyAxis").equals(FFTProperties.Hertz)){
                temp += String.format("%4s", "Hz:");
            }
            if (k==0){
                temp += String.format("%15s", "(DC)");
            } else if (k==fundamentalIndex){
                temp += String.format("%15s", "(fundamental)");
            } else {
                temp += String.format("%15s", " ");
            }
            temp += String.format("%16.3f", value[k]);
            temp += "     ";
            temp += String.format("% 8.2f", angle[k]);
            temp += "°";
            listContent.addElement(temp);
        }
    }
    
}

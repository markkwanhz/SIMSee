package ui.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.DefaultXYDataset;

import ui.util.DpiSetting;
import util.data.TimeSeriesData;
import util.exception.ArrayOverflowException;
import util.exception.InvalidInputException;
import util.exception.XYLengthException;
import util.fft.FFTProperties;

/**
 * FFTSrcPanel is a Chart Panel to display signal and FFT window
 * @author M
 *
 */
public class FFTSrcPanel extends ChartPanel{
    private static final long serialVersionUID = -5084793078279563774L;
    
    // Constant fields
    public static final String SignalWindow = "SignalWindow";
    public static final String FFTWindow = "FFTWindow";
    
    // Chart Components
    private DefaultXYDataset signalData, fftSrcData; //0:FFT source; 1:front part; 2:back part;
    private XYPlot signalPlot, fftSrcPlot;
    private JFreeChart signalChart, fftSrcChart;
    
    public FFTSrcPanel(){
        super(null, false, false, false, false, false);
        setBackground(Color.WHITE);
        
        //Initialize
        Font font = new Font("Arial", Font.BOLD, DpiSetting.getTitleSize());
        signalData = new DefaultXYDataset();
        fftSrcData = new DefaultXYDataset();
        signalChart = ChartFactory.createXYLineChart(null, "time/s", "value", signalData);
        signalChart.setTitle(new TextTitle("Signal", font));
        signalPlot = signalChart.getXYPlot();
        fftSrcChart = ChartFactory.createXYLineChart(null, "time/s", "value", fftSrcData);
        fftSrcChart.setTitle(new TextTitle("FFT Window", font));
        fftSrcPlot = fftSrcChart.getXYPlot();
        
        font = new Font("Times New Roman", Font.TRUETYPE_FONT, DpiSetting.getNormalFontSize());
        signalPlot.getDomainAxis().setTickLabelFont(font);
        signalPlot.getRangeAxis().setTickLabelFont(font);
        fftSrcPlot.getDomainAxis().setTickLabelFont(font);
        fftSrcPlot.getRangeAxis().setTickLabelFont(font);
        font = new Font("Times New Roman", Font.BOLD, DpiSetting.getMenuSize());
        signalPlot.getDomainAxis().setLabelFont(font);
        signalPlot.getRangeAxis().setLabelFont(font);
        fftSrcPlot.getDomainAxis().setLabelFont(font);
        fftSrcPlot.getRangeAxis().setLabelFont(font);
        //Background setting
        signalPlot.setBackgroundPaint(Color.WHITE);
        signalPlot.setDomainGridlinePaint(Color.GRAY);
        signalPlot.setRangeGridlinePaint(Color.GRAY);
        fftSrcPlot.setBackgroundPaint(Color.WHITE);
        fftSrcPlot.setDomainGridlinePaint(Color.GRAY);
        fftSrcPlot.setRangeGridlinePaint(Color.GRAY);
        
        //Line Color and Shape setting
        XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) fftSrcPlot.getRenderer();
        BasicStroke stroke = new BasicStroke(1f);
        r.setSeriesStroke(0, stroke);
        r.setSeriesPaint(0, Color.RED);
        
        r = (XYLineAndShapeRenderer) signalPlot.getRenderer();
        r.setSeriesStroke(0, stroke);
        r.setSeriesStroke(1, stroke);
        r.setSeriesStroke(2, stroke);
        r.setSeriesPaint(0, Color.RED);
        r.setSeriesPaint(1, Color.BLUE);
        r.setSeriesPaint(2, Color.BLUE);
        
        signalChart.removeLegend();
        fftSrcChart.removeLegend();
        
        this.setChart(signalChart);
        this.setMouseZoomable(false);
        this.setMaximumDrawWidth(3200);
        this.setMaximumDrawHeight(1800);
    }
    
    private TimeSeriesData tsData;
    private FFTProperties prop;
    /**
     * Set the data to be displayed. Before calling this method,
     * you should update the FFT properties first.
     * @param d contains the TimeSeries data
     * @throws XYLengthException 
     * @throws ArrayOverflowException 
     */
    public void setData(TimeSeriesData d) 
            throws ArrayOverflowException, XYLengthException{
        if(tsData!=null && d.getName().equals(tsData.getName()))
            return;
        tsData = d;
        setControl(prop);
    }
    
    /**
     * Change the figure control. This method will refresh the figure.
     * @param p
     * @throws ArrayOverflowException 
     * @throws XYLengthException 
     */
    public void setControl(FFTProperties p) 
            throws ArrayOverflowException, XYLengthException{
        prop = p;
        if(tsData == null)
            return;
        double start = Double.parseDouble(p.getProperty("StartTime"));
        TimeSeriesData[] data = tsData.split(start, p);
        fftSrcData.addSeries(data[0].getName(), data[0].getData());
        signalData.addSeries(data[0].getName(), data[0].getData());
        if(data[1]!=null){
            signalData.addSeries(data[1].getName(), data[1].getData());
        } else {
            signalData.removeSeries("front");
        }
        if(data[2]!=null){
            signalData.addSeries(data[2].getName(), data[2].getData());
        } else {
            signalData.removeSeries("back");
        }
    }
    
    /**
     * switchFig is used to switch the chart to display signal or FFT source.
     * @param fig legal value: <i>FFTSrcPanel.SignalWindow</i> or <i>FFTSrcPanel.FFTWindow</i>
     */
    public void switchFig(String fig){
        if(fig.equals(SignalWindow)){
            this.setChart(signalChart);
            this.setMouseZoomable(false);
            this.restoreAutoBounds();
        } else {
            this.setChart(fftSrcChart);
            this.setMouseZoomable(false);
            this.restoreAutoBounds();
        }
    }
    
    /**
     * @throws XYLengthException 
     * @throws ArrayOverflowException 
     * 
     */
    public TimeSeriesData getFFTSrc() 
            throws InvalidInputException{
        if(tsData==null)
            throw new NullPointerException();
        double start = 0;
        try{
            start = Double.parseDouble(prop.getProperty("StartTime"));
            TimeSeriesData src = tsData.split(start, prop)[0];
            return src;
        } catch (Exception e){
            throw new InvalidInputException();
        }
    }
    
    /**
     * Refresh the FFT Src Panel
     */
    public void refresh(){
        signalData.removeSeries("front");
        signalData.removeSeries("back");
        signalData.removeSeries("FFTSrc");
        fftSrcData.removeSeries("FFTSrc");
    }
}

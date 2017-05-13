package ui.chart;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import util.data.TimeSeriesData;
import util.exception.ArrayOverflowException;
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
        signalData = new DefaultXYDataset();
        fftSrcData = new DefaultXYDataset();
        signalChart = ChartFactory.createXYLineChart("Signal", "time/s", "value", signalData);
        signalPlot = signalChart.getXYPlot();
        fftSrcChart = ChartFactory.createXYLineChart("FFT Window", "time/s", "value", fftSrcData);
        fftSrcPlot = fftSrcChart.getXYPlot();
        
        
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
     * Set the data to be displayed
     * @param d contains the TimeSeries data
     * @throws XYLengthException 
     * @throws ArrayOverflowException 
     */
    public void setData(TimeSeriesData d) 
            throws ArrayOverflowException, XYLengthException{
        if(d.getName().equals(tsData.getName()))
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
}

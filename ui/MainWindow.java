package ui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import ui.chart.FFTResultsPanel;
import ui.chart.TimeSeriesPanel;
import util.database.DataSection;
import util.fft.FFTAnalysis;
import util.fft.FFTProperties;
import util.power.PowerAnalysis;

public class MainWindow {
    //Top frame
    private JFrame mainFrame;
    private JTabbedPane topTab;
    
    //Page1 Signal Inspector
    private TimeSeriesPanel tsp1;
    private TimeSeriesControlPanel tscp;
    
    //Page2 FFT analysis
    private FFTProperties fftp;
    private FFTResultsPanel fftPanel;
    
    //Page3 Power analysis
    private TimeSeriesPanel tsp2;
    
    //Data and utils
    private DataSection data;
    private FFTAnalysis fft;
    private PowerAnalysis power;
    
    /**
     * Constructor
     * @param None
     */
    public MainWindow(){
        
    }

    public static void main(String[] args) {

    }

}

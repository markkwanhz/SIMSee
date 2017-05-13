package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import layout.TableLayout;
import ui.chart.FFTResultsPanel;
import ui.chart.FFTSrcPanel;
import ui.chart.TimeSeriesPanel;
import ui.util.DpiSetting;
import util.data.TimeSeriesData;
import util.database.DataSection;
import util.exception.ArrayLengthException;
import util.exception.ArrayOverflowException;
import util.exception.FileFormatException;
import util.exception.NoDataException;
import util.fft.FFTAnalysis;
import util.fft.FFTProperties;
import util.fileread.DataReader;
import util.fileread.FileReader;
import util.fileread.InfoReader;
import util.power.PowerAnalysis;

public class MainWindow implements ActionListener {
    public static String PowerCalculate = "Power Calculate";
    
    //Top frame
    private JFrame mainFrame;
    private JTabbedPane topTab;
    
    //MenuBar
    private JMenuBar menuBar;
    
    //Page1 Signal Inspector
    private TimeSeriesPanel tsp1;
    private TimeSeriesControlPanel tscp;
    
    //Page2 FFT analysis
    private FFTProperties fftp;
    private FFTSrcPanel fftsp;
    private FFTResultsPanel fftPanel;
    private FFTControlPanel fftcp;
    
    //Page3 Power analysis
    private TimeSeriesPanel tsp2;
    private PowerControlPanel pcp;
    
    //Data and utils
    private DataSection data;
    private DataSection dataNew;
    private FFTAnalysis fft;
    private PowerAnalysis power;
    private FileReader input;
    
    /**
     * Constructor
     * @param None
     */
    public MainWindow(){
        data = new DataSection();
        fftp = new FFTProperties();
        
        mainFrame = new JFrame("PSCAD See");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topTab = new JTabbedPane();
        mainFrame.setContentPane(topTab);
        
        genMenu();
        genTab1();
        genTab2();
        genTab3();
        
        mainFrame.setJMenuBar(menuBar);
        
        mainFrame.setSize(DpiSetting.getFittedDimension(new Dimension(1000, 650)));
        //mainFrame.setUndecorated(true);
        mainFrame.setVisible(true);
    }
    
    //Menu Bar
    JMenu menu1;
    JMenuItem item11;
    JMenuItem item12;
    JMenuItem item13;
    
    /**
     * Generate the menu bar
     */
    private void genMenu() {
        menuBar = new JMenuBar();
        
        Font menuFont = new Font("Times New Roman", Font.PLAIN,
                DpiSetting.getMenuSize());
        menu1 = new JMenu(" File ");
        menu1.setFont(menuFont);
        
        menuBar.add(menu1);
        
        item11 = new JMenuItem("Import .inf file");
        item12 = new JMenuItem("Import output file(.out)");
        item13 = new JMenuItem("Exit");
        
        menu1.add(item11);
        menu1.add(item12);
        menu1.addSeparator();
        menu1.add(item13);
        
        item11.addActionListener(this);
        item12.addActionListener(this);
        item13.addActionListener(this);
        
    }
    
    /**
     * generate tab1, displaying signal inspector
     */
    private void genTab1() {
        JPanel tab1 = new JPanel();
        tab1.setBorder(new EmptyBorder(5, 5, 5, 5));
        tab1.setBackground(Color.WHITE);
        tsp1 = new TimeSeriesPanel();
        tsp1.setCrosshairVisible(false);
        tscp = new TimeSeriesControlPanel(data, tsp1);
        tab1.setLayout(new BorderLayout());
        tab1.add(tsp1, "Center");
        tab1.add(tscp,"East");
        topTab.add(" Signal Inspector ", tab1);
    }
    
    /**
     * generate tab2, displaying fft
     */
    private void genTab2(){
        JPanel tab2 = new JPanel();
        JPanel chart = new JPanel();
        tab2.setBorder(new EmptyBorder(5,5,5,5));
        tab2.setBackground(Color.WHITE);
        try {
            fftPanel = new FFTResultsPanel(null);
            fftPanel.setBorder(BorderFactory.createTitledBorder("FFT analysis"));
            fftsp = new FFTSrcPanel();
            fftsp.setBorder(BorderFactory.createTitledBorder("Signal"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        fftcp = new FFTControlPanel(data, fftp);
        tab2.setLayout(new BorderLayout());
        double border = DpiSetting.convertDouble(5);
        double[][] size = {{border,TableLayout.FILL,border},{border,0.4,border,0.6,border}};
        TableLayout layout = new TableLayout(size);
        chart.setLayout(layout);
        chart.setBackground(Color.WHITE);
        chart.add(fftsp,"1,1");
        chart.add(fftPanel,"1,3");
        tab2.add(chart,"Center");
        tab2.add(fftcp,"East");
        topTab.add(" FFT Analysis ", tab2);
    }
    
    /**
     * generate tab3, displaying power calculating result
     */
    private void genTab3(){
        pcp = new PowerControlPanel();
        tsp2 = new TimeSeriesPanel();
        tsp2.setTitle("Power Analysis");
        JPanel tab3 = new JPanel();
        int border = DpiSetting.convertInt(5);
        tab3.setBorder(BorderFactory.createEmptyBorder(border,border,border,border));
        tab3.setLayout(new BorderLayout());
        tab3.add(pcp,"East");
        tab3.add(tsp2,"Center");
        tab3.setBackground(Color.WHITE);
        topTab.add(" Power calculation ", tab3);
        pcp.addButtonListener(this);
    }
    
    /**
     * Import inf file, used in menu1 item1 action listener. 
     */
    private void importINF(){
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PSCAD project info file(*.inf)","inf");
        JFileChooser jChooser = new JFileChooser();
        jChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jChooser.setFileFilter(filter);
        int returnval = jChooser.showOpenDialog(null);
        if (returnval == JFileChooser.APPROVE_OPTION) {
            dataNew = new DataSection();
            String path = jChooser.getSelectedFile().getPath();
            input = new InfoReader(path, dataNew);
            try {
                input.readFile();
            } catch (IOException | FileFormatException err) {
                JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
            }
            input = null;
        }
    }
    
    private void importData(){
        if(dataNew == null){
            JOptionPane.showMessageDialog(null, "An inf file should be loaded first.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PSCAD output file(*.out)","out");
        JFileChooser jChooser = new JFileChooser();
        jChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jChooser.setFileFilter(filter);
        int returnval = jChooser.showOpenDialog(null);
        if (returnval == JFileChooser.APPROVE_OPTION) {
            Progress p = new Progress(mainFrame, Progress.IMPORT);
            new Thread() {
                public void run() {
                    String path = jChooser.getSelectedFile().getPath();
                    input = new DataReader(path, dataNew);
                    try {
                        input.readFile();
                        p.setValue(50);
                    } catch (IOException | FileFormatException err) {
                        JOptionPane.showMessageDialog(null, err, "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    data = dataNew;
                    dataNew = null;
                    input = null;
                    TimeSeriesData[] xy;
                    try {
                        xy = data.getAllData();
                        tsp1.resetPanel();
                        tsp1.addData(xy);
                        p.setValue(80);
                    } catch (NoDataException | ArrayOverflowException e1) {
                        e1.printStackTrace();
                    }
                    tscp.refresh();
                    tscp.setDataSection(data);
                    pcp.updateData(data);
                    p.setValue(100);
                }
            }.start();
            p.setVisible(true);
        }
    }
    
    private void calculatePower(String[] s){
        try {
            TimeSeriesData u = data.querySignal(s[0]);
            TimeSeriesData i = data.querySignal(s[1]);
            power = new PowerAnalysis(u, i, 50);
            TimeSeriesData p = power.getPower();
            tsp2.resetPanel();
            tsp2.addData(p);
            tsp2.setSignalVisible("Power", true);
        } catch (NoDataException | ArrayOverflowException | ArrayLengthException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow mw = new MainWindow();
            }
        });
    }

    /**
     * Decided what to do when pressing buttons
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        if(s == item11){
            new Thread(){
                public void run(){
                    importINF();
                }
            }.start();
        } else if (s == item12){
            importData();
        } else if (s == item13){
            System.exit(0);
        } else if (e.getActionCommand().equals(PowerCalculate)){
            calculatePower(pcp.getSignals());
        }
    }

}

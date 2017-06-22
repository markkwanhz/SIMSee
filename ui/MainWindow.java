package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import layout.TableLayout;
import ui.chart.FFTResultsPanel;
import ui.chart.FFTSrcPanel;
import ui.chart.TimeSeriesPanel;
import ui.util.DpiSetting;
import ui.util.MyExportDialog;
import util.data.FFTData;
import util.data.TimeSeriesData;
import util.database.DataSection;
import util.exception.ArrayLengthException;
import util.exception.ArrayOverflowException;
import util.exception.FileFormatException;
import util.exception.InvalidInputException;
import util.exception.NoDataException;
import util.exception.XYLengthException;
import util.fft.FFTAnalysis;
import util.fft.FFTProperties;
import util.fileread.DataReader;
import util.fileread.FileReader;
import util.fileread.InfoReader;
import util.power.PowerAnalysis;

public class MainWindow implements ActionListener {
    /*These final static fields are used for action commands*/
    public static String POWERCALCULATE = "Power Calculate";
    public static String FFTREFRESH = "FFT Refresh";
    public static String DISPLAY = "Display";
    public static String IMPORTPSCAD = "Import PSCAD";
    public static String IMPORTCLOUDPSS = "Import Cloudpss";
    public static String EXIT = "Exit";
    public static String ExportSignalInspector = "Exportsi";
    public static String ExportFFTResult = "Exportfft";
    public static String ExportPowerResult = "Exportpower";
    public static String ABOUT = "About";
    public static String XZOOM = "XZooming";
    public static String YZOOM = "YZooming";
    public static String BOTHZOOM = "BothZooming";
    
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
        
        mainFrame = new JFrame("Sim See");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topTab = new JTabbedPane();
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.getContentPane().setBackground(Color.WHITE);
        mainFrame.add(topTab,"Center");
        
        genMenu();
        genTab1();
        genTab2();
        genTab3();
        
        mainFrame.setJMenuBar(menuBar);
        
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        Dimension frameSize = DpiSetting.getFittedDimension(new Dimension(1000, 650));
        mainFrame.setLocation((screenSize.width-frameSize.width)/2, (screenSize.height-frameSize.height)/2);
        mainFrame.setSize(frameSize);
        //mainFrame.setUndecorated(true);
        mainFrame.setVisible(true);
    }
    
    //Menu Bar
    private JMenu menu1;
    private JMenu menu2;
    private JMenu menu3;
    private JMenu menu4;
    private JMenuItem item11;
    private JMenuItem item12;
    private JMenuItem item13;
    private JMenuItem item21;
    private JMenuItem item22;
    private JMenuItem item23;
    private JMenuItem item31;
    private JCheckBoxMenuItem item41;
    private JCheckBoxMenuItem item42;
    private JCheckBoxMenuItem item43;
    
    /**
     * Generate the menu bar
     */
    private void genMenu() {
        menuBar = new JMenuBar();
        
        Font menuFont = new Font("Times New Roman", Font.PLAIN,
                DpiSetting.getMenuSize());
        menu1 = new JMenu(" File ");
        menu1.setMnemonic('F');
        menu2 = new JMenu(" Tools ");
        menu2.setMnemonic('T');
        menu3 = new JMenu(" More ");
        menu3.setMnemonic('M');
        menu4 = new JMenu("Zoom");
        menu4.setMnemonic('Z');
        menu1.setFont(menuFont);
        menu2.setFont(menuFont);
        menu3.setFont(menuFont);
        menu4.setFont(menuFont);
        
        menuBar.add(menu1);
        menuBar.add(menu2);
        menuBar.add(menu4);
        menuBar.add(menu3);
        
        item11 = new JMenuItem("Import PSCAD .inf and .out file");
        item11.setActionCommand(IMPORTPSCAD);
        item12 = new JMenuItem("Import CloudPSS simulation file");
        item12.setActionCommand(IMPORTCLOUDPSS);
        item13 = new JMenuItem("Exit");
        item13.setActionCommand(EXIT);
        item21 = new JMenuItem("Export Signal Chart");
        item21.setActionCommand(ExportSignalInspector);
        item22 = new JMenuItem("Export FFT Result Chart");
        item22.setActionCommand(ExportFFTResult);
        item23 = new JMenuItem("Export Power Calculation Chart");
        item23.setActionCommand(ExportPowerResult);
        item31 = new JMenuItem("About");
        item31.setActionCommand(ABOUT);
        item41 = new JCheckBoxMenuItem("X Axis", false);
        item41.setActionCommand(XZOOM);
        item42 = new JCheckBoxMenuItem("Y Axis", false);
        item42.setActionCommand(YZOOM);
        item43 = new JCheckBoxMenuItem("Both Axes", true);
        item43.setActionCommand(BOTHZOOM);
        
        menu1.add(item11);
        menu1.add(item12);
        menu1.addSeparator();
        menu1.add(item13);
        menu2.add(item21);
        menu2.add(item22);
        menu2.add(item23);
        menu3.add(item31);
        menu4.add(item41);
        menu4.add(item42);
        menu4.add(item43);
        
        item11.addActionListener(this);
        item12.addActionListener(this);
        item13.addActionListener(this);
        item21.addActionListener(this);
        item22.addActionListener(this);
        item23.addActionListener(this);
        item31.addActionListener(this);
        item41.addActionListener(this);
        item42.addActionListener(this);
        item43.addActionListener(this);
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
            fftPanel = new FFTResultsPanel(null, fftp);
            fftPanel.setBorder(BorderFactory.createTitledBorder("FFT analysis"));
            fftsp = new FFTSrcPanel();
            fftsp.setControl(fftp);
            fftsp.setBorder(BorderFactory.createTitledBorder("Signal"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        fftcp = new FFTControlPanel(data, fftp);
        fftcp.addButtonListener(this);
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
//    private JPanel tab3;
//    private JPanel blankTab3;
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
        
//        blankTab3 = new JPanel();
//        blankTab3.setBackground(Color.gray);
//        blankTab3.setLayout(new TableLayout(new double[][]{{TableLayout.FILL},{TableLayout.FILL}}));
//        JLabel txt = new JLabel("Power Calculation is unavailable with CloudPss data file");
//        txt.setFont(new Font("Times New Roman", Font.BOLD, DpiSetting.convertInt(22)));
//        blankTab3.add(txt, "0,0,c,c");
    }
    
    private static String zoomState = BOTHZOOM;
    public static String getZoomState(){
        return zoomState;
    }
    public static void setZoomState(String state){
        zoomState = state;
    }
    
    String fileLoc;
    /**
     * Import inf file, used in menu1 item1 action listener. 
     */
    private void importPSCADINF(){
        new Thread(){
            public void run(){
                FileNameExtensionFilter filter = new FileNameExtensionFilter("PSCAD project info file(*.inf)","inf");
                JFileChooser jChooser = (fileLoc == null)?new JFileChooser():new JFileChooser(fileLoc);
                jChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jChooser.setDialogTitle("Open project information file...");
                jChooser.setFileFilter(filter);
                int returnval = jChooser.showOpenDialog(mainFrame);
                if (returnval == JFileChooser.APPROVE_OPTION) {
                    dataNew = new DataSection();
                    String path = jChooser.getSelectedFile().getPath();
                    fileLoc = jChooser.getSelectedFile().getParentFile().getPath();
                    input = new InfoReader(path, dataNew);
                    try {
                        input.readFile();
                    } catch (IOException | FileFormatException err) {
                        JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    input = null;
                    importPSCADData();
                }
            }
        }.start();
    }
    
    private void importPSCADData(){
        if(dataNew == null){
            JOptionPane.showMessageDialog(null, "An inf file should be loaded first.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PSCAD output file(*.out)","out");
        JFileChooser jChooser = new JFileChooser(fileLoc);
        jChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jChooser.setDialogTitle("Open data output file...");
        jChooser.setFileFilter(filter);
        int returnval = jChooser.showOpenDialog(mainFrame);
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
                    } catch (NoDataException | ArrayOverflowException | XYLengthException e1) {
                        e1.printStackTrace();
                    }
                    tscp.refresh();
                    fftsp.refresh();
                    tscp.setDataSection(data);
                    fftcp.updateData(data);
                    pcp.updateData(data);
                    p.setValue(100);
                }
            }.start();
            p.setVisible(true);
        }
    }
    
    private void importCloudPSS(){
        dataNew = new DataSection();
        CloudPssImportDialog cloudPssImportDialog = new CloudPssImportDialog(mainFrame, dataNew);
        int returnVal = cloudPssImportDialog.showImportDialog();
        if(returnVal == CloudPssImportDialog.LOAD_OPTION){
            TimeSeriesData[] xy;
            data = dataNew;
            try {
                xy = data.getAllData();
                tsp1.resetPanel();
                tsp1.addData(xy);
            } catch (NoDataException | ArrayOverflowException | XYLengthException e1) {
                e1.printStackTrace();
            }
            tscp.refresh();
            fftsp.refresh();
            tscp.setDataSection(data);
            fftcp.updateData(data);
            pcp.updateData(data);
        }
        dataNew = null;
    }
    
    private void calculatePower(String[] s){
        new Thread(){
            public void run(){
                try {
                    TimeSeriesData u = data.querySignal(s[0]);
                    TimeSeriesData i = data.querySignal(s[1]);
                    power = new PowerAnalysis(u, i, Double.parseDouble(s[2]));
                    TimeSeriesData p = power.getPower();
                    tsp2.resetPanel();
                    tsp2.addData(p);
                    tsp2.setSignalVisible("Power", true);
                } catch (NoDataException | ArrayOverflowException | ArrayLengthException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    
    public void disablePower(){
        item23.setEnabled(false);
        topTab.setEnabledAt(2, false);
    }
    
    public void enablePower(){
        item23.setEnabled(true);
        topTab.setEnabledAt(2, true);
    }

    public static void main(String[] args) {
        try
        {
            //BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencyAppleLike;
            //org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
            UIManager.getDefaults().put("Button.showMnemonics", Boolean.TRUE);
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
                @SuppressWarnings("unused")
                MainWindow mw = new MainWindow();
            }
        });
    }

    /**
     * Decided what to do when pressing buttons
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        try{
            if(s.equals(IMPORTPSCAD)){
                importPSCADINF();
                enablePower();
            } else if (s.equals(IMPORTCLOUDPSS)){
                importCloudPSS();
                disablePower();
            } else if (s.equals(EXIT)){
                System.exit(0);
            } else if (s.equals(POWERCALCULATE)){
                calculatePower(pcp.getSignals());
            } else if (s.equals(FFTREFRESH)){
                refreshFFTSrc();
            } else if (s.equals(DISPLAY)){
                fftDisplay();
            } else if (s.equals(ExportSignalInspector)){
                MyExportDialog export = new MyExportDialog();
                export.showExportDialog(mainFrame, "Export Signal Chart as ...", tsp1.getPrintComponent(), "export" );
            } else if (s.equals(ExportFFTResult)) {
                MyExportDialog export = new MyExportDialog();
                export.showExportDialog(mainFrame, "Export FFT Result Chart as ...", fftPanel.getPrintComponent(), "export" );
            } else if (s.equals(ExportPowerResult)) {
                MyExportDialog export = new MyExportDialog();
                export.showExportDialog(mainFrame, "Export Power Calculation Chart as ...", tsp2.getPrintComponent(), "export" );
            } else if (s.equals(ABOUT)){
                AboutDialog ad = new AboutDialog(mainFrame);
                ad.setVisible(true);
            } else if (s.equals(XZOOM)){
                setZoomState(XZOOM);
                item42.setSelected(false);
                item43.setSelected(false);
            } else if (s.equals(YZOOM)){
                setZoomState(YZOOM);
                item41.setSelected(false);
                item43.setSelected(false);
            } else if (s.equals(BOTHZOOM)){
                setZoomState(BOTHZOOM);
                item41.setSelected(false);
                item42.setSelected(false);
            }
        } catch (InvalidInputException e1) {
            JOptionPane.showMessageDialog(mainFrame, "Your input is invalid. Please check!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (NullPointerException exception){
            return;
        }
    }

    private String fftSrcSignal;
    private void fftDisplay() throws InvalidInputException {
        if(fftcp.getStatus()==null){
            throw new InvalidInputException();
        }
        TimeSeriesData src = fftsp.getFFTSrc();
        if(fft == null){
            fft = new FFTAnalysis(src, fftp);
        } else {
            fft.setFFT(src, fftp);
        }
        try{
            FFTData ans = fft.fftAnalyse();
            fftPanel.setDataSet(ans);
        }catch(Exception e){
            throw new InvalidInputException();
        }
    }

    private void refreshFFTSrc() throws InvalidInputException {
        if(fftcp.getStatus()==null){
            throw new InvalidInputException();
        };
        new Thread(){
            public void run(){
                String[] status = fftcp.getStatus();
                if(fftSrcSignal == null || !fftSrcSignal.equals(status[0])){
                    try {
                        TimeSeriesData td = data.querySignal(status[0]);
                        fftSrcSignal = status[0];
                        fftsp.setData(td);
                    } catch (NoDataException | ArrayOverflowException | XYLengthException e) {
                        JOptionPane.showMessageDialog(null, "Your input is invalid. Please check!", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else{
                    try {
                        fftsp.setControl(fftp);
                    } catch (ArrayOverflowException | XYLengthException e) {
                        JOptionPane.showMessageDialog(null, "Your input is invalid. Please check!", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                fftsp.switchFig(status[1]);
            }
        }.start();
    }

}

package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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

import ui.chart.FFTResultsPanel;
import ui.chart.TimeSeriesPanel;
import ui.util.DpiSetting;
import util.data.TimeSeriesData;
import util.database.DataSection;
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
    private FFTResultsPanel fftPanel;
    private FFTControlPanel fftcp;
    
    //Page3 Power analysis
    private TimeSeriesPanel tsp2;
    
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
        
        mainFrame.setJMenuBar(menuBar);
        
        mainFrame.setSize(DpiSetting.getFittedDimension(new Dimension(1000, 650)));
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
    
    private void genTab2(){
        JPanel tab2 = new JPanel();
        tab2.setBorder(new EmptyBorder(5,5,5,5));
        tab2.setBackground(Color.WHITE);
        try {
            fftPanel = new FFTResultsPanel(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        fftcp = new FFTControlPanel(data, fftp);
        tab2.setLayout(new BorderLayout());
        tab2.add(fftPanel,"Center");
        tab2.add(fftcp,"East");
        topTab.add(" FFT Analysis ", tab2);
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
                    p.setValue(100);
                }
            }.start();
            p.setVisible(true);
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
     * Decided what to do when pressing the menu items
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
        }
    }

}

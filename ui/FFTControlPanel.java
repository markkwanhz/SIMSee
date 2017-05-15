package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import layout.TableLayout;
import ui.chart.FFTSrcPanel;
import ui.util.DpiSetting;
import util.database.DataSection;
import util.fft.FFTProperties;

public class FFTControlPanel extends JPanel {
    private static final long serialVersionUID = 4190507279262577841L;
    //Components
    private JPanel pane1;
    private JPanel pane2;
    
    //Data
    //private DataSection data;
    private FFTProperties fftp;
    
    //Constructor
    public FFTControlPanel(DataSection data, FFTProperties fftp){
        //this.data = data;
        this.fftp = fftp;
        genPane1();
        genPane2();
        BorderLayout layout = new BorderLayout();
        int border = DpiSetting.convertInt(5);
        layout.setVgap(border);
        setBorder(new EmptyBorder(border, border, border, border));
        setLayout(layout);
        setBackground(Color.WHITE);
        add(pane1, "North");
        add(pane2, "Center");
    }
    
    private JButton refresh;
    private JComboBox<String> signalName;
    private DefaultComboBoxModel<String> signalNameModel;
    private JRadioButton signal, fftWindow;
    private void genPane1(){
        pane1 = new JPanel();
        pane1.setBackground(Color.WHITE);
        pane1.setBorder(BorderFactory.createTitledBorder("Available Signals"));
        JLabel lab1 = new JLabel("Name:");
        JLabel lab2 = new JLabel("Display:");
        refresh = new JButton("Refresh");
        refresh.setActionCommand(MainWindow.FFTREFRESH);
        lab2.setHorizontalAlignment(SwingConstants.CENTER);
        signalNameModel = new DefaultComboBoxModel<>();
        signalName = new JComboBox<>(signalNameModel);
        ButtonGroup group = new ButtonGroup();
        signal = new JRadioButton("Signal", true);
        signal.setBackground(Color.WHITE);
        fftWindow = new JRadioButton("FFT Window");
        fftWindow.setBackground(Color.WHITE);
        group.add(signal);
        group.add(fftWindow);
        
        double border = DpiSetting.convertDouble(10);
        double[][] size = {
                {border,0.3,border,0.7,border}, //columns
                {border,TableLayout.FILL,5,TableLayout.FILL,5,TableLayout.FILL,5,TableLayout.FILL,border}//rows
        };
        pane1.setLayout(new TableLayout(size));
        pane1.add(refresh,"1,1,l,c");
        pane1.add(lab1,"1,3,c,c");
        pane1.add(signalName,"3,3,f,c");
        pane1.add(lab2,"1,5,1,7");
        pane1.add(signal,"3,5,c,c");
        pane1.add(fftWindow,"3,7,c,c");
    }
    
    private JTextField startTime, nCycles, fF, mF;
    private JComboBox<String> maxTHDF, fAxis;
    private JButton dp;
    private void genPane2(){
        pane2 = new JPanel();
        pane2.setBackground(Color.WHITE);
        pane2.setBorder(BorderFactory.createTitledBorder("FFT Settings"));
        JLabel lab1 = new JLabel("Start time(s):");
        JLabel lab2 = new JLabel("Number of cycles:");
        JLabel lab3 = new JLabel("Fundamental frequency(Hz):");
        JLabel lab4 = new JLabel("Max frequency(Hz):");
        JLabel lab5 = new JLabel("Max Frequency for THD computation:");
        JLabel lab6 = new JLabel("Frequency axis:");
        startTime = new JTextField("0");
        nCycles = new JTextField("1");
        fF = new JTextField("50");
        mF = new JTextField("1000");
        
        String [] s1 = new String[2];
        s1[0] = "Max display frequency";
        s1[1] = "Nyquist frequency";
        maxTHDF = new JComboBox<>(s1);
        String [] s2 = new String[2];
        s2[0] = "Hertz";
        s2[1] = "Harmonic order";
        fAxis = new JComboBox<>(s2);
        
        dp = new JButton("Display");
        dp.setActionCommand(MainWindow.DISPLAY);
        
        double border = DpiSetting.convertDouble(10);
        double[][] size = {
                {border,DpiSetting.convertDouble(160),0.5*border,TableLayout.FILL,border}, //columns
                {border,DpiSetting.convertInt(30),0.5*border,DpiSetting.convertInt(30),0.5*border,DpiSetting.convertInt(30),
                    0.5*border,DpiSetting.convertInt(30),0.5*border,DpiSetting.convertInt(30),0.5*border,DpiSetting.convertInt(30),
                    0.5*border,DpiSetting.convertInt(30),0.5*border,DpiSetting.convertInt(30),TableLayout.FILL}  //rows
        };
        TableLayout layout = new TableLayout(size);
        pane2.setLayout(layout);
        pane2.add(lab1,"1,1,l,c");
        pane2.add(startTime, "3,1,f,c");
        pane2.add(lab2, "1,3,l,c");
        pane2.add(nCycles,"3,3,f,c");
        pane2.add(lab3, "1,5,l,c");
        pane2.add(fF,"3,5,f,c");
        pane2.add(lab4, "1,7,l,c");
        pane2.add(mF, "3,7,f,c");
        pane2.add(lab5, "1,9,3,9");
        pane2.add(maxTHDF, "1,11,3,11");
        pane2.add(lab6, "1,13,l,c");
        pane2.add(fAxis, "3,13,f,c");
        pane2.add(dp,"1,15,3,15");
    }
    
    /**
     * Update the data section when loading a new file. This
     * will make the signalList refreshed and signal chooser
     * reset.
     * @param d
     */
    public void updateData(DataSection d){
        signalNameModel.removeAllElements();
        Object[] s = d.listNames();
        for(int k = 0; k<s.length; k++){
            signalNameModel.addElement((String)s[k]);
        }
    }
    
    public void addButtonListener(ActionListener l){
        refresh.addActionListener(l);
        dp.addActionListener(l);
    }
    
    /**
     * Get current status of the fft control panel and 
     * update fft properties as well.
     * @return If the return is null, it means that some 
     * (UI) input's format is incorrect.
     */
    public String[] getStatus(){
        String[] status = new String[2];
        status[0] = signalName.getSelectedItem().toString();
        status[1] = signal.isSelected()? FFTSrcPanel.SignalWindow:FFTSrcPanel.FFTWindow;
        
        String sTime = startTime.getText();
        String nC = nCycles.getText();
        String ff = fF.getText();
        String mf = mF.getText();
        try{
            Double.parseDouble(sTime);
            Integer.parseInt(nC);
            Double.parseDouble(ff);
            Double.parseDouble(mf);
        } catch(Exception e){
            return null;
        }
        fftp.setProperty("StartTime", sTime);
        fftp.setProperty("NumberOfCycle", nC);
        fftp.setProperty("FundamentalFrequency", ff);
        fftp.setProperty("MaxFrequency", mf);
        if (maxTHDF.getSelectedItem().toString()
                .equals("Max display frequency")) {
            fftp.setProperty("MaxTHDFrequency", FFTProperties.MaxDisplayFrequency);
        } else {
            fftp.setProperty("MaxTHDFrequency", FFTProperties.NyquistFrequency);
        }
        if (fAxis.getSelectedItem().toString()
                .equals("Hertz")) {
            fftp.setProperty("FrequencyAxis", FFTProperties.Hertz);
        } else {
            fftp.setProperty("FrequencyAxis", FFTProperties.HarmonicOrder);
        }
        return status;
    }
}

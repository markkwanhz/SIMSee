package ui;

import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.database.DataSection;
import util.fft.FFTProperties;

public class FFTControlPanel extends JPanel {
    //Components
    private JPanel pane1;
    private JPanel pane2;
    
    //Data
    private DataSection data;
    private FFTProperties fftp;
    private ArrayList<String> signalList;
    
    //Constructor
    public FFTControlPanel(DataSection data, FFTProperties fftp){
        this.data = data;
        this.fftp = fftp;
        signalList = new ArrayList<String>();
        genPane1();
        genPane2();
    }
    
    private void genPane1(){
        
    }
    
    private JTextField startTime, nCycles, fF, mF;
    private JComboBox<String> maxTHDF, fAxis;
    private void genPane2(){
        pane2 = new JPanel();
        JLabel lab1 = new JLabel("Start time(s):");
        JLabel lab2 = new JLabel("Number of cycles:");
        JLabel lab3 = new JLabel("Fundamental frequency(Hz):");
        JLabel lab4 = new JLabel("Max frequency(Hz):");
        JLabel lab5 = new JLabel("Max Frequency for THD computation:");
        JLabel lab6 = new JLabel("Frequency axis:");
        startTime = new JTextField("");
        nCycles = new JTextField("");
        fF = new JTextField("");
        mF = new JTextField("");
        
        String [] s1 = new String[2];
        s1[0] = "Nyquist frequency";
        s1[1] = "Max display frequency";
        maxTHDF = new JComboBox<>(s1);
        String [] s2 = new String[2];
        s2[0] = "Hertz";
        s2[1] = "Harmonic order";
        fAxis = new JComboBox<>(s2);
        
        
        
    }
    
    /**
     * Update the data section when loading a new file. This
     * will make the signalList refreshed and signal chooser
     * reset.
     * @param d
     */
    public void updateData(DataSection d){
        
    }
}

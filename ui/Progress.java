package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import ui.util.DpiSetting;

/**
 * A GUI window extends JDialog that shows a JProgressBar
 * @author M
 *
 */
public class Progress extends JDialog implements Observer {
    private static final long serialVersionUID = 4323874903072754647L;
    
    public static final String IMPORT = "Reading files..";
    public static final String CALFFT = "Carrying out FFT analysis...";
    public static final String CALPOWER = "Calculating power...";
    
    private JProgressBar jpb;
    private JFrame mainFrame;
    private JLabel label;
    private Set se; 
    
    public Progress(JFrame f, String type){
        super(f, "Processing", true);
        mainFrame = f;
        initGUI(type);
        se = new Set();
        se.addObserver(this);
    }
    
    private void initGUI(String type) {
        jpb = new JProgressBar(0, 100);
        label = new JLabel(type);
        jpb.setBackground(Color.WHITE);
        jpb.setForeground(new Color(30, 144, 255));
        
        JPanel jp = new JPanel();
        BorderLayout bd = new BorderLayout();
        bd.setVgap(8);
        jp.setLayout(bd);
        jp.setBorder(new EmptyBorder(5, 5, 5, 5));
        jp.add(BorderLayout.NORTH, label);
        jp.add(BorderLayout.CENTER, jpb);
        setContentPane(jp);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setSize(DpiSetting.getFittedDimension(new Dimension(300, 90)));
        
        setResizable(false);
        setLocationRelativeTo(mainFrame);
    }
    
    class Set extends Observable{
        private int rate;
        
        public int getRate(){
            return rate;
        }
        
        public void setRate(int r){
            rate = r;
            setChanged();
            notifyObservers(new Integer(r));
        }
    }
    
    public void setValue(int i){
        se.setRate(i);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof Integer){
            int i = ((Integer)arg).intValue();
            jpb.setValue(i);
            if(i == 100){
                try {
                    label.setText("Finished!");
                    Thread.sleep(1000);
                    setVisible(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

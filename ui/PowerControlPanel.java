package ui;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ui.util.DpiSetting;
import util.database.DataSection;
import layout.TableLayout;

public class PowerControlPanel extends JPanel {
    private static final long serialVersionUID = -4822791896531500689L;
    private JComboBox<String> u;
    private JComboBox<String> i;
    private DefaultComboBoxModel<String> signalList1;
    private DefaultComboBoxModel<String> signalList2;
    private JButton calculate;
    
    public PowerControlPanel(){
        super();
        signalList1 = new DefaultComboBoxModel<>();
        signalList2 = new DefaultComboBoxModel<>();
        u = new JComboBox<>(signalList1);
        i = new JComboBox<>(signalList2);
        JLabel lab1 = new JLabel("Choose u");
        JLabel lab2 = new JLabel("Choose i");
        calculate = new JButton("Calculate");
        calculate.setActionCommand(MainWindow.PowerCalculate);
        
        double border = DpiSetting.convertDouble(5);
        double [][] size = {
                {border,TableLayout.FILL,border},
                {border,DpiSetting.convertInt(30),border,DpiSetting.convertInt(30),
                    border,DpiSetting.convertInt(30),border,DpiSetting.convertInt(30),
                    border,DpiSetting.convertInt(30),TableLayout.FILL}
        };
        TableLayout layout = new TableLayout(size);
        setLayout(layout);
        add(lab1, "1,1,l,c");
        add(u, "1,3,f,c");
        add(lab2, "1,5,l,c");
        add(i, "1,7,f,c");
        add(calculate,"1,9,f,c");
        
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Input chooser"));
    }
    
    public void updateData(DataSection d){
        signalList1.removeAllElements();
        signalList2.removeAllElements();
        Object[] signals = d.listNames();
        for(int k = 0; k<signals.length; k++){
            signalList1.addElement((String)signals[k]);
            signalList2.addElement((String)signals[k]);
        }
    }
    
    public String[] getSignals(){
        String[] ans = new String[2];
        ans[0] = u.getSelectedItem().toString();
        ans[1] = i.getSelectedItem().toString();
        return ans;
    }
    
    public void addButtonListener(ActionListener l){
        calculate.addActionListener(l);
    }
}

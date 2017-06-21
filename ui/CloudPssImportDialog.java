package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import ui.util.DpiSetting;
import util.database.DataSection;
import util.fileread.JsonReader;
import layout.TableLayout;

public class CloudPssImportDialog extends JDialog implements ActionListener, ItemListener {
    private static final long serialVersionUID = -7345371148533757376L;
    
    private static String CHOOSE = "Choose file";
    private static String LOAD = "Load file";
    
    public static int LOAD_OPTION = 1;
    public static int CANCEL_OPTION = 0;
    public static int ERROR_OPTION = -1;
    
    private JLabel label1, label2;
    private JButton fileChoose, fileLoad;
    private JTextField filePath;
    private JComboBox<String> taskChooser;
    private ComboBoxModel<String> model;
    
    private DataSection data;
    private JsonReader reader;

    public CloudPssImportDialog(JFrame father, DataSection data) {
        super(father, "Load CloudPss Result File...", true);
        this.data = data;
        
        label1 = new JLabel("Json File");
        label2 = new JLabel("Task ID");
        fileChoose = new JButton("Choose File");
        fileChoose.setActionCommand(CHOOSE);
        fileLoad = new JButton("Load");
        fileLoad.setActionCommand(LOAD);
        filePath = new JTextField("");
        filePath.setEditable(false);
        filePath.setBackground(Color.WHITE);
        model = new DefaultComboBoxModel<String>();
        taskChooser = new JComboBox<>(model);
        taskChooser.addItemListener(this);
        
        fileChoose.addActionListener(this);
        fileLoad.addActionListener(this);

        JPanel jp = new JPanel();
        double border = DpiSetting.convertDouble(20);
        double[][] size = new double[][] {
                { border, 80, border / 2, TableLayout.FILL, border / 2, 100,
                        border }, // columns
                { border / 2, TableLayout.FILL, 5, TableLayout.FILL, border / 2 } // rows
        };
        TableLayout layout = new TableLayout(size);
        jp.setLayout(layout);
        jp.setBackground(Color.WHITE);
        jp.add(label1, "1, 1, c, c");
        jp.add(filePath, "3, 1, f, c");
        jp.add(fileChoose, "5, 1, c, c");
        jp.add(label2, "1, 3, c, c");
        jp.add(taskChooser, "3, 3, f, c");
        jp.add(fileLoad, "5, 3, f, c");

        setLayout(new BorderLayout());
        setContentPane(jp);
        setSize(DpiSetting.getFittedDimension(new Dimension(400, 150)));
        setResizable(false);
        setLocationRelativeTo(father);
        //setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                returnVal = CANCEL_OPTION;
            }
        });
    }

    private int returnVal;
    public int showImportDialog(){
        returnVal = ERROR_OPTION;
        setVisible(true);
        return returnVal;
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String s = event.getActionCommand();
        if(s.equals(CHOOSE)){
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON file","json");
            JFileChooser jChooser = new JFileChooser();
            jChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jChooser.setDialogTitle("Open CloudPss data file...");
            jChooser.setFileFilter(filter);
            int returnval = jChooser.showOpenDialog(this);
            if (returnval == JFileChooser.APPROVE_OPTION) {
                String path = jChooser.getSelectedFile().getPath();
                String fileName = jChooser.getSelectedFile().getName();
                Vector<String> taskIDList;
                filePath.setText(fileName);
                try{
                    reader = new JsonReader(path, data);
                    taskIDList = reader.analyseFile();
                    model = new DefaultComboBoxModel<>(taskIDList);
                    taskChooser.setModel(model);
                    reader.setTaskID(taskIDList.get(0));
                } catch (IOException e){
                    JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if(s.equals(LOAD)){
            try {
                reader.readFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
            }
            returnVal = LOAD_OPTION;
            dispose();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED){
            String taskIDSelected = (String) e.getItem();
            reader.setTaskID(taskIDSelected);
        }
    }
}

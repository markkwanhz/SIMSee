package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ui.chart.TimeSeriesPanel;
import ui.util.DpiSetting;
import util.database.DataSection;

/**
 * Time series line chart's control panel. To control which signal to be
 * displayed, and the focus of the crosshair.
 * 
 * @author M
 *
 */
public class TimeSeriesControlPanel extends JPanel implements ItemListener,
        ListSelectionListener, MouseListener, MouseMotionListener {
    private static final long serialVersionUID = 1L;

    //Signal chooser
    private JPanel signalPane;
    private JComboBox<String> filter;

    private JScrollPane signalChooser;
    private JList<JCheckBox> signalList;
    private HashMap<String, JCheckBox> allList;
    private HashMap<String, Vector<JCheckBox>> listContent;

    //Crosshair chooser
    private JPanel crosshairPane;
    private JScrollPane crosshairChooser;
    private JList<String> crosshairList;
    private DefaultListModel<String> crosshairListContent;

    private DataSection data;
    private TimeSeriesPanel tsPanel;

    /**
     * Redefine the JList item type
     * 
     * @author M
     *
     */
    @SuppressWarnings("rawtypes")
    class CheckBoxItem implements ListCellRenderer {
        private JCheckBox jcb;

        public CheckBoxItem() {
            jcb = new JCheckBox();
            jcb.setFont(new Font("Consolas", Font.PLAIN, DpiSetting
                    .getNormalFontSize()));
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                jcb.setBackground(new Color(30, 144, 255));
                jcb.setForeground(Color.WHITE);
            } else {
                jcb.setBackground(Color.WHITE);
                jcb.setForeground(Color.BLACK);
            }
            jcb.setText(((JCheckBox) value).getText());
            jcb.setSelected(((JCheckBox) value).isSelected());
            return jcb;
        }

    }

    @SuppressWarnings("unchecked")
    public TimeSeriesControlPanel(DataSection d, TimeSeriesPanel tsp) {
        Font font = new Font("Consolas", Font.PLAIN,
                DpiSetting.getNormalFontSize());
        filter = new JComboBox<String>();
        filter.setPreferredSize(DpiSetting.getFittedDimension(new Dimension(130, 20)));
        filter.setFont(font);

        signalList = new JList<JCheckBox>();
        signalList.setCellRenderer(new CheckBoxItem());
        signalList.addMouseMotionListener(this);
        signalChooser = new JScrollPane(signalList);

        crosshairListContent = new DefaultListModel<String>();
        crosshairList = new JList<String>(crosshairListContent);
        crosshairList.setFont(font);
        crosshairChooser = new JScrollPane(crosshairList);

        signalPane = new JPanel();
        JPanel temp = new JPanel();
        temp.setBackground(Color.WHITE);
        temp.setLayout(new FlowLayout(0));
        temp.add(new JLabel("Filter:"));
        temp.add(filter);
        signalPane.setBackground(Color.WHITE);
        signalPane.setLayout(new BorderLayout());
        signalPane.setBorder(BorderFactory.createTitledBorder("Signal Chooser"));
        signalPane.add(temp, "North");
        signalPane.add(signalChooser, "Center");
        
        crosshairPane = new JPanel();
        crosshairPane.setBackground(Color.WHITE);
        crosshairPane.setLayout(new BorderLayout());
        crosshairPane.add(crosshairChooser,"Center");
        crosshairPane.setBorder(BorderFactory.createTitledBorder("Crosshair Focus"));
        
        setLayout(new GridLayout(2, 1));
        setBackground(Color.WHITE);
        add(signalPane);
        add(crosshairPane);
        setPreferredSize(DpiSetting.getFittedDimension(new Dimension(220, 600)));

        allList = new HashMap<String,JCheckBox>();
        listContent = new HashMap<String, Vector<JCheckBox>>();
        
        tsPanel = tsp;
        setDataSection(d);

        filter.addItemListener(this);
        signalList.addMouseListener(this);
        crosshairList.addListSelectionListener(this);
    }

    /**
     * Update the data base
     * 
     * @param d
     */
    public void setDataSection(DataSection d) {
        
        data = d;

        filter.removeAllItems();
        filter.addItem("All");
        String[] listTmp = data.listTypes();
        for (int k = 0; k < listTmp.length; k++) {
//            if (listTmp[k].equals(""))
//                continue;
            filter.addItem(listTmp[k]);
        }

        allList.clear();
        listContent.clear();
        Vector<JCheckBox> allTmp = new Vector<JCheckBox>();
        listContent.put("All", allTmp);
        for (int k = 0; k < filter.getItemCount(); k++) {
            String filterName = filter.getItemAt(k);
            JCheckBox tmpBox;
            if (filterName.equals("All")){
                Object[] names = data.listNames();
                for(int m = 0; m<names.length; m++){
                    tmpBox = new JCheckBox((String)names[m]);
                    tmpBox.addItemListener(this);
                    allList.put((String)names[m],tmpBox);
                    allTmp.add(tmpBox);
                }
            } else {
                String[] filterSplitted = filterName.split(":");
                Vector<JCheckBox> tmpList = new Vector<JCheckBox>();
                String[] nameList = data.listNames(filterSplitted[0],
                        filterSplitted[1]);
                for (int j = 0; j < nameList.length; j++) {
//                    if (allList.get(nameList[j]) == null) {
//                        tmpBox = new JCheckBox(nameList[j]);
//                        tmpBox.addItemListener(this);
//                        allList.put(nameList[j], tmpBox);
//                        allTmp.add(tmpBox);
//                        tmpList.addElement(tmpBox);
//                    } else {
                        tmpList.addElement(allList.get(nameList[j]));
//                    }
                }
                listContent.put(filterName, tmpList);
            }
        }
        signalList.setListData(allTmp);
        crosshairListContent.removeAllElements();
        crosshairListContent.addElement("(None)");
    }
    
    public void refresh() {
        filter.setSelectedIndex(0);
        crosshairList.setSelectedIndex(0);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == filter) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedItem = (String) e.getItem();
                Vector<JCheckBox> l = listContent.get(selectedItem);
                signalList.setListData(l);
            }
        }
        else if (e.getSource() instanceof JCheckBox){
            JCheckBox boxChange = (JCheckBox)e.getSource();
            if(boxChange.isSelected()){
                String selected = crosshairList.getSelectedValue();
                sortedInsert(boxChange.getText());
                crosshairList.setSelectedIndex(crosshairListContent.indexOf(selected));
                tsPanel.setSignalVisible(boxChange.getText(), true);
                
            } else {
                if(crosshairList.getSelectedIndex() == crosshairListContent.indexOf(boxChange.getText())){
                    crosshairList.setSelectedIndex(0);
                }
                crosshairListContent.removeElement(boxChange.getText());
                tsPanel.setSignalVisible(boxChange.getText(), false);
            }
        }
    }
    
    /**
     * Insert the selected signal to the displaying signal list
     * according to alphabetical order.
     * @param text the selected signal name
     */
    protected void sortedInsert(String text) {
        for(int k = 1; k<crosshairListContent.getSize(); k++){
            if(crosshairListContent.getElementAt(k).compareTo(text)>0){
                crosshairListContent.insertElementAt(text, k);
                return;
            }
        }
        crosshairListContent.addElement(text);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(e.getValueIsAdjusting()) return;
        if(e.getSource() == crosshairList){
            String focus = crosshairList.getSelectedValue();
            if(focus == null){
                return;
            }
            if(focus.equals("(None)")){
                tsPanel.setCrosshairVisible(false);
            } else {
                try {
                    tsPanel.setCrosshairSignal(focus);
                    tsPanel.setCrosshairVisible(true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(signalList.getSelectedIndex()!=-1){
            if(e.getClickCount() == 1){
                signalList.getSelectedValue().setSelected(!signalList.getSelectedValue().isSelected());
                signalList.repaint();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        @SuppressWarnings("unchecked")
        JList<JCheckBox> l = (JList<JCheckBox>)e.getSource();
        ListModel<JCheckBox> m = l.getModel();
        int index = l.locationToIndex(e.getPoint());
        if(index > -1){
            String toolTip = data.querySignalInfo(m.getElementAt(index).getText());
            l.setToolTipText(toolTip);
        }else{
            l.setToolTipText(null);
        }
        
    }
}

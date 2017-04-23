package chart;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;



/**
 * A Swing GUI component for displaying a JSlider.
 * The panel allows mouse wheel to control the TimeSeriesChart
 * @author M
 */
public class SliderPanel extends JPanel{
    /*For Serialization*/
    private static final long serialVersionUID = 4608835729764666730L;
    private JSlider slider;
    private int init = 0;
    
    public SliderPanel(int orientation){
        super();
        this.slider = new TransparentSlider(orientation,0,200,0);
        this.slider.setPaintLabels(false);
        this.slider.setPaintTicks(false);
        this.slider.setPaintTrack(true);
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints s = new GridBagConstraints();
        s.gridx = 0; s.gridy = 0; s.gridwidth = 1; s.gridheight = 1; 
        s.weightx = 0; s.weighty = 0; 
        switch(orientation){
        case JSlider.VERTICAL:
            s.fill = GridBagConstraints.VERTICAL; s.anchor = GridBagConstraints.EAST;
            break;
        case JSlider.HORIZONTAL:
            s.fill = GridBagConstraints.HORIZONTAL; s.anchor = GridBagConstraints.SOUTH;
            break;
        }
        this.setLayout(layout);
        this.add(slider);
        layout.setConstraints(slider, s);
//        if(orientation == JSlider.VERTICAL){
//            this.add(slider,"West");
//        }else{
//            this.add(slider,"South");
//        }
        this.setOpaque(false);
        this.slider.setOpaque(false);
    }
    
    public void addListener(ChangeListener l){
        this.slider.addChangeListener(l);
    }
    
    public void resetSlider(){
        this.slider.setValue(this.init);
    }
    
    public void setSlider(int loc){
        this.slider.setValue(loc);
    }
    
    public double getValue(){
        return ((double)this.slider.getValue())/200;
    }
}

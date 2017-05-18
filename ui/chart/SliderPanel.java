package ui.chart;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import layout.TableLayout;
import ui.util.DpiSetting;



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
        this.slider = new TransparentSlider(orientation,0,10000,0);
        this.slider.setPaintLabels(false);
        this.slider.setPaintTicks(false);
        this.slider.setPaintTrack(true);
        
        if(orientation == JSlider.HORIZONTAL){
            double[][] size = {{DpiSetting.convertDouble(60),TableLayout.FILL},{TableLayout.FILL}};
            TableLayout layout = new TableLayout(size);
            this.setLayout(layout);
            this.add(slider,"1,0,f,f");
        } else {
            double[][] size = {{TableLayout.FILL},{TableLayout.FILL,DpiSetting.convertDouble(40)}};
            TableLayout layout = new TableLayout(size);
            this.setLayout(layout);
            this.add(slider,"0,0,f,f");
        }
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
        return ((double)this.slider.getValue())/10000;
    }
}

package ui.chart;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;
/**
 * A JSlider that turns transparent when mouse moves out of its bounds
 * @author M
 */
public class TransparentSlider extends JSlider {
    /*For serialization*/
    private static final long serialVersionUID = 7224126963967186723L;
    
    private float alpha = 0.4f;
    
    class MyMouseListener extends MouseAdapter{
        @Override
        public void mouseEntered(MouseEvent arg0) {
           alpha = 0.8f;
           repaint();
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
           alpha = 0.4f;
           repaint();
        } 
    }
    
    public TransparentSlider(int or, int lo, int hi, int loca){
        super(or,lo,hi,loca);
        this.addMouseListener(new MyMouseListener());
    }
    
    @Override
    public void paint(Graphics g) { 
        Graphics2D g2 = (Graphics2D) g.create(); 
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)); 
        super.paint(g2); 
        g2.dispose(); 
    }
}

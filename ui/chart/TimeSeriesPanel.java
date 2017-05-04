package ui.chart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;

import util.data.TimeSeriesData;

/**
 * 
 */
public class TimeSeriesPanel extends JPanel {
    private static final long serialVersionUID = -3682074995996044430L;

    // Chart Components
    private DefaultXYDataset xyData;
    private XYPlot xyPlot;
    private JFreeChart chart;
    private ChartPanel cp;

    // Sliders Components
    /**
     * MouseWheelListener for yAxis zooming (mouse wheeling on SliderY)
     * 
     * @author M
     */
    class MyMouseWheelListener implements MouseWheelListener {
        private ValueAxis axis;
        private MyRange limit;
        private SliderPanel sd;

        public MyMouseWheelListener(ValueAxis ax, MyRange li, SliderPanel sd) {
            this.axis = ax;
            this.limit = li;
            this.sd = sd;
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent arg0) {
            Range range = this.axis.getRange();
            if (arg0.getWheelRotation() < 0) {
                Range temp = Range.expand(range, 0, -0.5);
                this.axis.setRange(temp);
            } else if (arg0.getWheelRotation() > 0) {
                if (range.getLength() > 0.5 * this.limit.getLength()) {
                    this.axis.setRange(this.limit.getRange());
                    // this.sd.resetSlider();
                } else {
                    Range temp = Range.expand(range, 0, 1);
                    double gapHi = temp.getUpperBound()
                            - this.limit.getUpperBound();
                    if (gapHi > 0) {
                        temp = Range.shift(temp, -gapHi);
                    }
                    this.axis.setRange(temp);
                }
            }
            range = this.axis.getRange();
            double gap = this.limit.getLength() - range.getLength();
            int loc = (int) ((range.getLowerBound() - this.limit
                    .getLowerBound()) * 200 / gap);
            this.sd.setSlider(loc);
        }
    }

    class SlidingListener implements ChangeListener {
        private ValueAxis axis;
        private MyRange limit;
        private SliderPanel sp;

        public SlidingListener(ValueAxis ax, MyRange li, SliderPanel s) {
            this.axis = ax;
            this.limit = li;
            this.sp = s;
        }

        @Override
        public void stateChanged(ChangeEvent arg0) {
            Range range = this.axis.getRange();
            Range temp = new Range(this.limit.getLowerBound(),
                    this.limit.getLowerBound() + range.getLength());
            double gap = this.limit.getLength() - range.getLength();
            double distance = gap * this.sp.getValue();
            temp = Range.shift(temp, distance, true);
            this.axis.setRange(temp);
        }

    }

    // Slider for yAxis
    private SliderPanel ySlider;
    // Slider for xAxis
    private SliderPanel xSlider;

    // For zooming
    private ValueAxis xAxis, yAxis;
    private MyRange xLimit, yLimit;

    // CrosshairOverlay
    private Crosshair xCrosshair;
    private Crosshair yCrosshair;
    private int crosshairIndex = 0;

    private void addCrosshair() {
        CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
        this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY,
                new BasicStroke(0f));
        this.xCrosshair.setLabelVisible(true);
        this.xCrosshair.setLabelBackgroundPaint(new Color(255, 255, 240));
        this.yCrosshair = new Crosshair(Double.NaN, Color.GRAY,
                new BasicStroke(0f));
        this.yCrosshair.setLabelVisible(true);
        this.yCrosshair.setLabelBackgroundPaint(new Color(255, 255, 240));
        crosshairOverlay.addDomainCrosshair(xCrosshair);
        crosshairOverlay.addRangeCrosshair(yCrosshair);
        cp.addOverlay(crosshairOverlay);
    }

    class crosshairListener implements ChartMouseListener {

        @Override
        public void chartMouseClicked(ChartMouseEvent event) {
            // ignore
        }

        @Override
        public void chartMouseMoved(ChartMouseEvent event) {
            if(xyData.getSeriesCount()==0)
                return;
            Rectangle2D dataArea = cp.getScreenDataArea();
            double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea,
                    RectangleEdge.BOTTOM);
            double y = DatasetUtilities.findYValue(xyData, crosshairIndex, x);
            xCrosshair.setValue(x);
            yCrosshair.setValue(y);
        }

    }

    // Constructor
    @SuppressWarnings("deprecation")
    public TimeSeriesPanel() {
        super();
        this.setBackground(Color.WHITE);

        this.xyData = new DefaultXYDataset();
        this.chart = ChartFactory.createXYLineChart(null, "time/s", "value",
                xyData);
        this.xyPlot = (XYPlot) this.chart.getPlot();
        // set the grid color and background color
        this.xyPlot.setBackgroundPaint(Color.WHITE);
        this.xyPlot.setDomainGridlinePaint(Color.GRAY);
        this.xyPlot.setRangeGridlinePaint(Color.GRAY);
        XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) this.xyPlot.getRenderer();
        BasicStroke stroke = new BasicStroke(1.5f);
        r.setStroke(stroke);
        
        XYTitleAnnotation xyta = new XYTitleAnnotation(0.001, 0.999,
                chart.getLegend(), RectangleAnchor.TOP_LEFT);
        this.xyPlot.addAnnotation(xyta);
        this.chart.removeLegend();

        this.cp = new ChartPanel(chart, false, false, false, false, false);
        this.cp.setMouseZoomable(false);
        this.addCrosshair();
        this.cp.addChartMouseListener(new crosshairListener());
        this.xLimit = new MyRange(0, 10);
        this.yLimit = new MyRange(0, 10);
        this.xAxis = this.xyPlot.getDomainAxis();
        this.yAxis = this.xyPlot.getRangeAxis();

        this.ySlider = new SliderPanel(JSlider.VERTICAL);
        this.ySlider.addMouseWheelListener(new MyMouseWheelListener(this.yAxis,
                this.yLimit, this.ySlider));
        this.ySlider.addListener(new SlidingListener(this.yAxis, this.yLimit,
                this.ySlider));
        this.xSlider = new SliderPanel(JSlider.HORIZONTAL);
        this.xSlider.addMouseWheelListener(new MyMouseWheelListener(this.xAxis,
                this.xLimit, this.xSlider));
        this.xSlider.addListener(new SlidingListener(this.xAxis, this.xLimit,
                this.xSlider));

        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        this.add(ySlider, "West");
        this.add(xSlider, "South");
        this.add(cp, "Center");
        
    }

    /**
     * 往TimeSeriesPanel中增加信号
     * 
     * @param data
     *            TimeSeriesData 对象
     */
    public void addData(TimeSeriesData data) {
        int cnt = this.xyData.getSeriesCount();
        this.xyData.addSeries(data.getName(), data.getData());
        this.xLimit.setRange(Range.expand(
                DatasetUtilities.findDomainBounds(this.xyData), 0, 0.05));
        this.yLimit.setRange(Range.expand(
                DatasetUtilities.findRangeBounds(this.xyData), 0.05, 0.05));
        if(cnt == 0){
            this.cp.restoreAutoBounds();
        }
    }
    
    /**
     * 往TimeSeriesPanel中增加信号数组
     * 
     * @param data
     *            TimeSeriesData 对象
     */
    public void addData(TimeSeriesData[] data) {
        for(int k = 0; k<data.length; k++){
            this.addData(data[k]);
        }
        this.hideAllSignal();
    }

    /**
     * 设置是否打开十字准星
     * 
     * @param crosshairVisible
     *            true or false
     */
    public void setCrosshairVisible(boolean crosshairVisible) {
        this.xCrosshair.setVisible(crosshairVisible);
        this.yCrosshair.setVisible(crosshairVisible);
    }

    /**
     * 设置十字准星的目标信号
     * 
     * @param name
     *            目标信号名称
     * @throws Exception
     *             信号不存在
     */
    public void setCrosshairSignal(String name) throws Exception {
        int index = this.xyData.indexOf(name);
        if (index == -1) {
            throw new Exception("Illegal signal name for crosshair.");
        }
        this.crosshairIndex = index;
    }
    
    /**
     * Set the given signal's visibility
     * @param signalName
     * @param visible true(visible) or false(invisible)
     */
    public void setSignalVisible(String signalName, boolean visible){
        int index = xyData.indexOf(signalName);
        XYLineAndShapeRenderer r = (XYLineAndShapeRenderer)xyPlot.getRenderer();
        r.setSeriesVisible(index, visible);
    }
    
    
    public void hideAllSignal(){
        XYLineAndShapeRenderer r = (XYLineAndShapeRenderer)xyPlot.getRenderer();
        for(int index = 0; index<xyData.getSeriesCount(); index++){
            r.setSeriesVisible(index, false);
        }
    }
    
    public void showAllSignal(){
        XYLineAndShapeRenderer r = (XYLineAndShapeRenderer)xyPlot.getRenderer();
        for(int index = 0; index<xyData.getSeriesCount(); index++){
            r.setSeriesVisible(index, true);
        }
    }
}

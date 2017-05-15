package ui.chart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

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
import org.jfree.chart.labels.StandardCrosshairLabelGenerator;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;

import ui.util.DpiSetting;
import util.data.TimeSeriesData;

/**
 * 
 */
public class TimeSeriesPanel extends JPanel implements MouseListener, MouseMotionListener {
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
                    .getLowerBound()) * 10000 / gap);
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
        Font font = new Font("Consolos", Font.PLAIN,
                DpiSetting.getNormalFontSize());
        this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY,
                new BasicStroke(0f));
        this.xCrosshair.setLabelFont(font);
        DecimalFormat decimalFormat = new DecimalFormat("0.000E0");
        this.xCrosshair.setLabelGenerator(new StandardCrosshairLabelGenerator("{0}", decimalFormat));
        this.xCrosshair.setLabelBackgroundPaint(new Color(255, 255, 240));
        this.xCrosshair.setLabelVisible(true);
        this.yCrosshair = new Crosshair(Double.NaN, Color.GRAY,
                new BasicStroke(0f));
        this.yCrosshair.setLabelFont(font);
        this.yCrosshair.setLabelGenerator(new StandardCrosshairLabelGenerator("{0}", decimalFormat));
        this.yCrosshair.setLabelBackgroundPaint(new Color(255, 255, 240));
        this.yCrosshair.setLabelVisible(true);
        crosshairOverlay.addDomainCrosshair(xCrosshair);
        crosshairOverlay.addRangeCrosshair(yCrosshair);
        cp.addOverlay(crosshairOverlay);
        setCrosshairVisible(false);
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
        this.chart = ChartFactory.createXYLineChart(null, "Time/s", "Value",
                xyData);
        this.xyPlot = (XYPlot) this.chart.getPlot();
        // set the grid color and background color
        this.xyPlot.setBackgroundPaint(Color.WHITE);
        this.xyPlot.setDomainGridlinePaint(Color.GRAY);
        this.xyPlot.setRangeGridlinePaint(Color.GRAY);
        XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) this.xyPlot.getRenderer();
        BasicStroke stroke = new BasicStroke(DpiSetting.convertFloat(1.5f));
        r.setStroke(stroke);
        r.setBaseSeriesVisible(false);
        
        LegendTitle legendTitle = chart.getLegend();
        Font font = new Font("Times New Roman", Font.TRUETYPE_FONT, DpiSetting.getNormalFontSize());
        legendTitle.setItemFont(font);
        XYTitleAnnotation xyta = new XYTitleAnnotation(0.001, 0.999,
                chart.getLegend(), RectangleAnchor.TOP_LEFT);
        this.xyPlot.addAnnotation(xyta);
        this.chart.removeLegend();

        this.cp = new ChartPanel(chart, false, false, false, false, false);
        this.cp.setMouseZoomable(false);
        this.cp.setMaximumDrawWidth(3200);
        this.cp.setMaximumDrawHeight(1800);
        this.addCrosshair();
        this.cp.addChartMouseListener(new crosshairListener());
        this.xLimit = new MyRange(0, 10);
        this.yLimit = new MyRange(0, 10);
        this.xAxis = this.xyPlot.getDomainAxis();
        this.yAxis = this.xyPlot.getRangeAxis();
        font = new Font("Times New Roman", Font.BOLD, DpiSetting.getMenuSize());
        this.xAxis.setLabelFont(font);
        this.yAxis.setLabelFont(font);
        font = new Font("Times New Roman", Font.TRUETYPE_FONT, DpiSetting.getNormalFontSize());
        this.xAxis.setTickLabelFont(font);
        this.yAxis.setTickLabelFont(font);

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
        
        domainRange = new HashMap<String,MyRange>();
        rangeRange = new HashMap<String,MyRange>();
        
        this.cp.addMouseListener(this);
        this.cp.addMouseMotionListener(this);
    }
    
    private HashMap<String, MyRange> domainRange;
    private HashMap<String, MyRange> rangeRange;

    /**
     * 往TimeSeriesPanel中增加信号
     * 
     * @param data
     *            TimeSeriesData 对象
     */
    public void addData(TimeSeriesData data) {
        int cnt = this.xyData.getSeriesCount();
        xyData.addSeries(data.getName(), data.getData());
        domainRange.put(data.getName(), data.getDomainRange());
        rangeRange.put(data.getName(), data.getRangeRange());
//        this.xLimit.setRange(Range.expand(
//                DatasetUtilities.findDomainBounds(this.xyData), 0, 0.05));
//        this.yLimit.setRange(Range.expand(
//                DatasetUtilities.findRangeBounds(this.xyData), 0.05, 0.05));
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
     * Set the title of the chart
     * @param title the title
     */
    public void setTitle(String title){
        Font font = new Font("Arial", Font.BOLD, DpiSetting.getTitleSize());
        TextTitle t = new TextTitle(title, font);
        chart.setTitle(t);
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
        ArrayList<Integer> visibleIndex = new ArrayList<>(xyData.getSeriesCount());
        for(index = 0; index<xyData.getSeriesCount(); index++){
            if(r.getSeriesVisible(index))
                visibleIndex.add(index);
        }
        if(visibleIndex.size()==0){
            xLimit.setRange(new Range(0, 10));
            yLimit.setRange(new Range(0, 10));
        } else {
            double xlower = domainRange.get(xyData.getSeriesKey(visibleIndex.get(0))).getLowerBound();
            double xupper = domainRange.get(xyData.getSeriesKey(visibleIndex.get(0))).getUpperBound();
            double ylower = rangeRange.get(xyData.getSeriesKey(visibleIndex.get(0))).getLowerBound();
            double yupper = rangeRange.get(xyData.getSeriesKey(visibleIndex.get(0))).getUpperBound();
            double temp;
            for(int k = 1; k<visibleIndex.size(); k++){
                temp =  domainRange.get(xyData.getSeriesKey(visibleIndex.get(k))).getLowerBound();
                xlower = temp<xlower?temp:xlower; 
                temp =  domainRange.get(xyData.getSeriesKey(visibleIndex.get(k))).getUpperBound();
                xupper = temp>xupper?temp:xupper; 
                temp =  rangeRange.get(xyData.getSeriesKey(visibleIndex.get(k))).getLowerBound();
                ylower = temp<ylower?temp:ylower; 
                temp =  rangeRange.get(xyData.getSeriesKey(visibleIndex.get(k))).getUpperBound();
                yupper = temp>yupper?temp:yupper; 
            }
            xLimit.setRange(Range.expand(new Range(xlower, xupper), 0, 0.05));
            yLimit.setRange(Range.expand(new Range(ylower, yupper), 0.05, 0.05));
        }
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
    
    public void resetPanel(){
        xyData = new DefaultXYDataset();
        xyPlot.setDataset(xyData);
        xLimit.setRange(new Range(0, 10));
        yLimit.setRange(new Range(0, 10));
        xAxis.setRange(xLimit.getRange());
        yAxis.setRange(yLimit.getRange());
        domainRange.clear();
        rangeRange.clear();
        setCrosshairVisible(false);
    }
    
    //The following methods are for dragging the chart using mouse

    Point startPoint;
    Range recordXRange;
    Range recordYRange;
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 2){
            xAxis.setRange(xLimit.getRange());
            yAxis.setRange(yLimit.getRange());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //Set the start point
        int mods = e.getModifiers();
        //left button has been pressed
        if (mods == InputEvent.BUTTON1_MASK) {
            if (startPoint == null) {
                Rectangle2D dataArea = cp.getScreenDataArea();
                Point p = e.getPoint();
                if (dataArea.contains(p)) {
                    startPoint = p;
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //Release startPoint
        int mods = e.getModifiers();
        if (mods == InputEvent.BUTTON1_MASK && startPoint!=null){
            startPoint = null;
            recordXRange = null;
            recordYRange = null;
            setCursor((Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)));
            Range xtemp = xAxis.getRange();
            Range ytemp = yAxis.getRange();
            double gap = xLimit.getLength() - xtemp.getLength();
            int loc = (int) ((xtemp.getLowerBound() - xLimit
                    .getLowerBound()) * 10000 / gap);
            xSlider.setSlider(loc);
            gap = yLimit.getLength() - ytemp.getLength();
            loc = (int) ((ytemp.getLowerBound() - yLimit
                    .getLowerBound()) * 10000 / gap);
            ySlider.setSlider(loc);
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (startPoint!=null){
            double dx = e.getX() - startPoint.getX();
            double dy = e.getY() - startPoint.getY();
            if (dx == 0.0 && dy == 0.0){
                return;
            }
            if(recordXRange == null){
                recordXRange = xAxis.getRange();
                recordYRange = yAxis.getRange();
            }
            Rectangle2D dataArea = cp.getScreenDataArea();
            double xMove = -dx / dataArea.getWidth() * recordXRange.getLength();
            double yMove = dy / dataArea.getHeight() * recordYRange.getLength();
            Range xtemp = xLimit.adjustRange(Range.shift(recordXRange, xMove, true));
            Range ytemp = yLimit.adjustRange(Range.shift(recordYRange, yMove, true));
            xAxis.setRange(xtemp);
            yAxis.setRange(ytemp);
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}

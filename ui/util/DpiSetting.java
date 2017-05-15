package ui.util;

import java.awt.Dimension;
import java.awt.Toolkit;

public class DpiSetting {
    private static int DEFAULTDPI = 96;
    private static int DPI = 96;
    private static int NORMALSIZE = 12;
    private static int MENUSIZE = 14;
    private static int TITLESIZE = 16;
    
    /**
     * Update the program's dpi setting from current running environment.
     * You should call this method before instantiating any GUI components. 
     */
    public static void updateDPI(){
        DPI = Toolkit.getDefaultToolkit().getScreenResolution();
    }
    
    /**
     * Get the normal text font size fitted current dpi settings
     * @return Normal text font size
     */
    public static int getNormalFontSize(){
        return DPI*NORMALSIZE/DEFAULTDPI;
    }
    
    /**
     * Get the menu font size fitted current dpi settings
     * @return Menu font size
     */
    public static int getMenuSize(){
        return DPI*MENUSIZE/DEFAULTDPI;
    }
    
    /**
     * Calculate the dimension <b>a</b> used in current screen
     * which has the same physical size of <b>b</b> in default 
     * dpi screens (96).
     * @param b Dimension in default dpi setting screens.
     * @return The adjusted dimension.
     */
    public static Dimension getFittedDimension(Dimension b){
        double w = b.getWidth();
        double h = b.getHeight();
        w = w*DPI/DEFAULTDPI;
        h = h*DPI/DEFAULTDPI;
        return new Dimension((int)w, (int)h);
    }
    
    public static double convertDouble(double d) {
        return d*DPI/DEFAULTDPI;
    }
    
    public static float convertFloat(float f){
        return f*DPI/DEFAULTDPI;
    }
    
    public static int convertInt(int i){
        return i*DPI/DEFAULTDPI;
    }

    /**
     * Get the title font size fitted current dpi settings
     * @return Title font size
     */
    public static int getTitleSize() {
        return DPI*TITLESIZE/DEFAULTDPI;
    }
    
}

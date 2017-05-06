package ui.util;

import java.awt.Dimension;
import java.awt.Toolkit;

public class DpiSetting {
    private static int defaultDPI = 96;
    private static int dpi = 96;
    private static int normalSize = 12;
    private static int menuSize = 14;
    
    /**
     * Update the program's dpi setting from current running environment.
     * You should call this method before instantiating any GUI components. 
     */
    public static void updateDPI(){
        dpi = Toolkit.getDefaultToolkit().getScreenResolution();
    }
    
    /**
     * Get the normal text font size fitted current dpi settings
     * @return Normal text font size
     */
    public static int getNormalFontSize(){
        return dpi*normalSize/defaultDPI;
    }
    
    /**
     * Get the menu font size fitted current dpi settings
     * @return Menu font size
     */
    public static int getMenuSize(){
        return dpi*menuSize/defaultDPI;
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
        w = w*dpi/defaultDPI;
        h = h*dpi/defaultDPI;
        return new Dimension((int)w, (int)h);
    }
}

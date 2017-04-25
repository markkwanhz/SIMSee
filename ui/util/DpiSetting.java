package ui.util;

import java.awt.Toolkit;

public class DpiSetting {
    private static int defaultDPI = 96;
    private static int dpi = 96;
    private static int normalSize = 12;
    
    public static void updateDPI(){
        dpi = Toolkit.getDefaultToolkit().getScreenResolution();
    }
    
    public static int getNormalFontSize(){
        return dpi*normalSize/defaultDPI;
    }
}

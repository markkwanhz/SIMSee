package ui.util;

import java.awt.Component;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.exportchooser.AbstractExportFileType;
import org.freehep.util.export.ExportDialog;
import org.freehep.util.export.ExportFileType;
import org.jfree.chart.ChartPanel;

public class MyExportDialog extends ExportDialog {
    private static final long serialVersionUID = -1982152034069863825L;
    
    private static final String rootKey = ExportDialog.class.getName();
    private static final String SAVE_AS_TYPE = rootKey +".SaveAsType";
    private static final String SAVE_AS_FILE = rootKey +".SaveAsFile";
    
    @Override
    /**
     * Called to acually write out the file.
     * Override this method to provide special handling (e.g. in a WebStart app)
     * @return true if the file was written, or false to cancel operation
     */
    protected boolean writeFile(Component component, ExportFileType t) throws IOException{
        try {
            Field fileField = ExportDialog.class.getDeclaredField("file");
            Field propsField = ExportDialog.class.getDeclaredField("props");
            Field creatorField = ExportDialog.class.getDeclaredField("creator");
            propsField.setAccessible(true);
            fileField.setAccessible(true);
            creatorField.setAccessible(true);
            JTextField file = (JTextField) fileField.get(this);
            Properties props = (Properties) propsField.get(this);
            String creator = (String) creatorField.get(this);
            Method privateMethod = ExportDialog.class.getDeclaredMethod("currentType", null);
            privateMethod.setAccessible(true);
            
            File f = new File(file.getText());
            if (f.exists())
            {
               int ok = JOptionPane.showConfirmDialog(this,"Replace existing file?");
               if (ok != JOptionPane.OK_OPTION) return false;
            }

            t = (AbstractExportFileType)t;
            Class[] cls = {File.class, Component.class, Properties.class};
            Method privateMethod2 = AbstractExportFileType.class.getDeclaredMethod("getGraphics", cls);
            privateMethod2.setAccessible(true);

            Object[] obj = {f,component,props};
            VectorGraphics g = (VectorGraphics) privateMethod2.invoke(t, obj);
            
            //
            if (g != null) {
                g.setCreator(creator);
                g.setProperties(props);
                g.startExport();
                int w = Integer.parseInt(props.getProperty("size-w", "-1"));
                int h = Integer.parseInt(props.getProperty("size-h", "-1"));
                Rectangle r = (w>=0 && h>=0)? new Rectangle(0,0,w,h):new Rectangle(component.getSize());
                ((ChartPanel)component).getChart().draw(g, r);
                g.endExport();
            }
            props.put(SAVE_AS_FILE,file.getText());
            props.put(SAVE_AS_TYPE,((ExportFileType)privateMethod.invoke(this, null)).getFileFilter().getDescription());
            return true;
            
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }
}

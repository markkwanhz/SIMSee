package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import ui.util.DpiSetting;

public class AboutDialog extends JDialog implements MouseListener, MouseMotionListener{
    private static final long serialVersionUID = 3000747637885861106L;
    
    private JTextPane textPane;
    private StyledDocument doc;

    public AboutDialog(JFrame f){
        super(f, "About", true);
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setBackground(Color.WHITE);
        int border = DpiSetting.convertInt(20);
        p.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));
        
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.addMouseListener(this);
        textPane.addMouseMotionListener(this);
        doc = textPane.getStyledDocument();
        String t = "PSCAD See\n" + "Author: Markghz\n" + "GitHub: ";
        String textLink = "https://github.com/Markghz/PSCADvisualization";
        Font font = new Font("Consolas", Font.PLAIN, DpiSetting.getNormalFontSize());
        MutableAttributeSet attrs = textPane.getInputAttributes();
        StyleConstants.setFontFamily(attrs, font.getFamily());
        StyleConstants.setFontSize(attrs, font.getSize());
        StyleConstants.setItalic(attrs, (font.getStyle() & Font.ITALIC) != 0);
        StyleConstants.setBold(attrs, (font.getStyle() & Font.BOLD) != 0);
        
        Style regularBlue = doc.addStyle("regularBlue", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
        StyleConstants.setFontFamily(regularBlue, font.getFamily());
        StyleConstants.setFontSize(regularBlue, font.getSize());
        StyleConstants.setForeground(regularBlue, Color.BLUE);
        StyleConstants.setUnderline(regularBlue, true);
        regularBlue.addAttribute("linkact", new ChatLinkListener(textLink));
        
        try {
            doc.insertString(0, t, attrs);
            doc.insertString(doc.getLength(), textLink, regularBlue);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        
        p.add(textPane,"Center");
        setContentPane(p);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(f);
    }
    
    @SuppressWarnings("serial")
    class ChatLinkListener extends AbstractAction
    {
        private String textLink;

        ChatLinkListener(String textLink)
        {
            this.textLink = textLink;
        }

        protected void execute() {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URL(textLink).toURI());
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }

        public void actionPerformed(ActionEvent e)
        {
            execute();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Element ele = doc.getCharacterElement(textPane.viewToModel(e.getPoint()));
        AttributeSet as = ele.getAttributes();
        ChatLinkListener fla = (ChatLinkListener)as.getAttribute("linkact");
        if(fla != null)
        {
            fla.execute();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Element ele = doc.getCharacterElement(textPane.viewToModel(e.getPoint()));
        AttributeSet as = ele.getAttributes();
        ChatLinkListener fla = (ChatLinkListener)as.getAttribute("linkact");
        if(fla != null)
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}

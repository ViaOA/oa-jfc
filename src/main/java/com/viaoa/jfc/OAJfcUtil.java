/*  Copyright 1999 Vince Via vvia@viaoa.com
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.viaoa.jfc;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;

import com.viaoa.hub.Hub;
import com.viaoa.jfc.border.CustomLineBorder;
import com.viaoa.object.OACalcInfo;
import com.viaoa.object.OAObjectInfo;

public class OAJfcUtil {

	/*
	 https://liquidlnf.dev.java.net/	
	 UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
	 com.birosoft.liquid.LiquidLookAndFeel.setLiquidDecorations(true);
	*/
	
	public static void showLookAndFeels() {
		UIManager.LookAndFeelInfo[] lfs = UIManager.getInstalledLookAndFeels();
		for (int i=0; lfs != null && i < lfs.length; i++) {
			System.out.println(i + ") " + lfs[i].getName());
		}
	}
	public static void showLookAndFeelDefaults() {
		UIDefaults uid = UIManager.getLookAndFeel().getDefaults();
		Enumeration keys  = uid.keys();
		while (keys.hasMoreElements()) { 
		    String key   = (String) keys.nextElement(); 
		    Object value = uid.get(key); 
		    System.out.println (key + " = " + value); 
		  } 
	}

	public static void showSystemInfo() {
		Properties props = System.getProperties();
		Enumeration en = props.keys();
		for (int i=0; en.hasMoreElements(); i++) {
			Object key = en.nextElement();
			if (key instanceof String) System.out.println(i+") " + key + " = " + props.getProperty((String)key));;
		}
	}

    public static Window getWindow(Component comp) {
        if (comp == null) return null;
        Window window = SwingUtilities.getWindowAncestor(comp);
        if (window != null) return window;
        for(Container p = comp.getParent(); p != null; p = p.getParent()) {
            if (p instanceof JPopupMenu) {
                JPopupMenu pm = (JPopupMenu) p;
                Component compx = ((JPopupMenu) p).getInvoker();
                if (compx instanceof Container) {
                    p = (Container) compx;
                }
            }
            if (p instanceof Window) {
                window = (Window) p;
                break;
            }
        }
        return window;
    }

    
    
    //** ===========================
    //      Static Helper methods
    
    public static void useRedBorder(JComponent lbl) {
        if (lbl == null) return;
        lbl.setBorder(OATable.BORDER_Red);
    }
    public static void useYellowBorder(JComponent lbl) {
        if (lbl == null) return;
        lbl.setBorder(OATable.BORDER_Yellow);
    }
    public static void useColorBorder(JComponent lbl, Color color) {
        if (lbl == null) return;
        if (color == null) lbl.setBorder(null);
        lbl.setBorder(new CompoundBorder(new LineBorder(Color.white, 1), new LineBorder(color)));
    }
    public static void useNoBorder(JComponent lbl) {
        if (lbl == null) return;
        lbl.setBorder(null);
    }

    public static void useDitto(JLabel lbl) {
        if (lbl == null) return;
        useGrayText(lbl);
        lbl.setText(" '' ");
        alignCenter(lbl);
    }
    public static void alignCenter(JLabel lbl) {
        if (lbl == null) return;
        lbl.setHorizontalAlignment(JLabel.CENTER);
    }
    public static void alignLeft(JLabel lbl) {
        if (lbl == null) return;
        lbl.setHorizontalAlignment(JLabel.LEFT);
    }
    public static void alignRight(JLabel lbl) {
        if (lbl == null) return;
        lbl.setHorizontalAlignment(JLabel.RIGHT);
    }
    
    public static void useTableSelectedBorder(JComponent lbl) {
        if (lbl == null) return;
        lbl.setBorder(new CompoundBorder(new LineBorder(Color.white, 1), UIManager.getBorder("Table.focusCellHighlightBorder")));         
    }
    public static void useGrayText(JLabel lbl) {
        if (lbl == null) return;
        lbl.setForeground(Color.GRAY);
    }
    public static void useItalics(JLabel lbl) {
        if (lbl == null) return;
        lbl.setFont(lbl.getFont().deriveFont(Font.ITALIC));
    }
    public static void useItalic(JLabel lbl) {
        if (lbl == null) return;
        lbl.setFont(lbl.getFont().deriveFont(Font.ITALIC));
    }
    public static void usePlain(JLabel lbl) {
        if (lbl == null) return;
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));
    }
    public static void useBold(JLabel lbl) {
        if (lbl == null) return;
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
    }

    public static void useColorIcon(JLabel lbl, Color color1) {
        useColorIcon(lbl, color1, null);
    }
    public static void useColorIcon(JLabel lbl, Color color1, Color color2) {
        if (lbl == null) return;
        if (color1 == null) color1 = Color.WHITE;

        LabelColorIcon myColorIcon = new LabelColorIcon(color1, color2);
        lbl.setIcon(myColorIcon);        
    }
    
    static class LabelColorIcon implements Icon {
        Color color, color2;        
        public LabelColorIcon(Color c) {
            this.color = c;
        }
        public LabelColorIcon(Color c, Color c2) {
            this.color = c;
            this.color2 = c2;
        }
        public int getIconHeight() {
            return 17;
        }
        public int getIconWidth() {
            return 12;
        }

        public void paintIcon(Component comp,Graphics g,int x,int y) {
            Color c = color==null?Color.white:color;
            g.setColor(c);
            g.fillRect(x+1,y+3,12,12);
            if (color2 != null) {
                Polygon p = new Polygon();
                p.addPoint(13, 3);
                p.addPoint(1, 15);
                p.addPoint(13, 15);
                g.setColor(color2);
                g.fillPolygon(p);
            }
        }
    }
    
    public static void setBorderError(JComponent label) {
        if (label == null) return;
        label.setBorder(OATable.BORDER_Red);
    }
    public static void setJustifyLeft(JLabel label) {
        if (label == null) return;
        label.setHorizontalAlignment(JLabel.LEFT);
    }
    public static void setJustifyRight(JLabel label) {
        if (label == null) return;
        label.setHorizontalAlignment(JLabel.RIGHT);
    }
    public static void setJustifyCenter(JLabel label) {
        if (label == null) return;
        label.setHorizontalAlignment(JLabel.RIGHT);
    }

    private static ConcurrentHashMap<JComponent, Blinker> hmBlinker = new ConcurrentHashMap<JComponent, Blinker>();
    private static class Blinker {
        JComponent component;
        Color colorFg, colorBg;
        Color colorFgOrig, colorBgOrig;
        int cnt;
        int tot;
    }

    
    public static void blink(final JComponent component) {
        if (component == null) return;
        blink(component, OATable.COLOR_Change_Foreground, OATable.COLOR_Change_Background, 1, 1000);
    }
    
    public static void blink(final JComponent component, final int numberOfTimes, int msDelay) {
        if (component == null) return;
        blink(component, component.getBackground(), component.getForeground(), numberOfTimes, msDelay);
    }
    public static void blink(JComponent component, Color fgColor, Color bgColor, int numberOfTimes, final int msDelay) {
        if (component == null) return;

        if (fgColor == null) fgColor = component.getBackground();
        if (bgColor == null) bgColor = component.getForeground();
        
        component.setOpaque(true);
        
        Blinker blinkerx;
        boolean bFound;
        synchronized (hmBlinker) {
            blinkerx = hmBlinker.get(component);
            if (blinkerx == null) {
                bFound = false;
                blinkerx = new Blinker();
                blinkerx.component = component;
                blinkerx.colorFgOrig = component.getForeground();
                blinkerx.colorBgOrig = component.getBackground();
            }
            else {
                bFound = true;
            }
            hmBlinker.put(component, blinkerx);
        }
        synchronized (blinkerx) {
            blinkerx.cnt = 0;
            blinkerx.tot = numberOfTimes;
            blinkerx.colorFg = fgColor;
            blinkerx.colorBg = bgColor;
        }
        if (bFound) return;
        
        final Blinker blinker = blinkerx;
        final Timer timer = new Timer(msDelay, null);
        
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean b;
                synchronized (blinker) {
                    b = (blinker.cnt++ % 2 == 0);
                    Color c;
                    c = (b ? blinker.colorFg : blinker.colorFgOrig);
                    blinker.component.setForeground(c);
                    
                    c = (b ? blinker.colorBg : blinker.colorBgOrig);
                    blinker.component.setBackground(c);
                    
                    b = (!b && ((blinker.cnt / 2) >= blinker.tot));
                }
                if (b) {
                    synchronized (hmBlinker) {
                        hmBlinker.remove(component);
                    }
                    timer.stop();
                }
            }
        };                 
        timer.addActionListener(al);
        timer.setRepeats(true);
        timer.setInitialDelay(80);
        timer.start();
    }

    
    /**
     * Same as window.pack(), except that it works with OAResizePanel.
     */
    public static void pack(Window window) {
        if (window == null) return;
        try {
            OAResizePanel.setPacking(window);
            window.pack();
        }
        finally {
            OAResizePanel.setPacking(null);
        }
    }

    static int averageCharWidth = 0;
    static double dAverageCharWidth; 
    static int averageCharHeight = 0;
    static int lastFontSize = 0;

    
    /**
     * Used to determine the pixel width based on the average width of a character 'X'.
     */
    public static int getCharWidth(Component comp, int columns) {
        if (comp == null) return 0;
        return getCharWidth(comp, comp.getFont(), columns);
    }
    public static int getCharWidth(int columns) {
        if (dAverageCharWidth == 0.0) getCharWidth();
        return (int) (dAverageCharWidth * columns);
    }
    public static int getCharWidth() {
        if (averageCharWidth != 0) {
            return averageCharWidth;
        }
        JTextField txt = new JTextField();
        Font font = txt.getFont();
        return getCharWidth(txt, font, 1);
    }
    public static int getCharWidth(Component comp, Font font, int columns) {
        if (comp == null) return 0;

        if (averageCharWidth == 0 || (font != null && font.getSize() != lastFontSize)) {
            if (font == null) {
                System.out.println("OATable.getCharWidth=null, will use average=12 as default");
                Exception e = new Exception("OATable.getCharWidth=null, will use average=12 as default");
                e.printStackTrace();
                return (11 * columns);
            }
            lastFontSize = font.getSize();
            FontMetrics fm = comp.getFontMetrics(font);
            //averageCharWidth = (int) (fm.stringWidth("9XYma") / 5);
            // averageCharWidth = fm.charWidth('m');  // =11, same code used by JTextField.getColumnWidth 

            // 2018116
            String s = "AaBbCcDdEeFfGgMmOoWwYyZz012345";
            dAverageCharWidth = fm.stringWidth(s);
            dAverageCharWidth = ((double)dAverageCharWidth) / s.length();
            averageCharWidth = (int) Math.ceil(dAverageCharWidth);  // =7or8
            //was: averageCharWidth = (int) (fm.stringWidth("9m0M123456") / 10);  // =7or8

            /* test
            Font fontx = new Font( "Monospaced", Font.PLAIN, 12 );
            fm = comp.getFontMetrics(fontx);
            int x2 = fm.charWidth('m'); =7
            */
        }
        
        return (int) (dAverageCharWidth * columns);
    }

    public static int getCharHeight() {
        if (averageCharHeight != 0) return averageCharHeight;
        JTextField txt = new JTextField();
        Font font = txt.getFont();
        return getCharHeight(txt, font);
    }
    
    public static int getCharHeight(Component comp, Font font) {
        if (averageCharHeight == 0 || (font != null && font.getSize() != lastFontSize)) {
            lastFontSize = font.getSize();
            FontMetrics fm = comp.getFontMetrics(font);
            averageCharHeight = (int) fm.getHeight();
        }
        return (averageCharHeight);
    }
    
    public static GridBagConstraints getGridBagConstraints() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(3, 3, 3, 3);
        gc.anchor = gc.WEST;
        gc.gridwidth = 1;

        // helper
        // gc.weightx = gc.weighty = 1.0;
        // gc.gridwidth = gc.REMAINDER;
        // gc.fill = gc.HORIZONTAL;
        
        // gc.weightx = gc.weighty = 0;
        // gc.gridwidth = 1;
        // gc.fill = gc.NONE;
        
        return gc;
    }
    private static final Border borderPanel = new EmptyBorder(10,10,5,5);
    public static Border getPanelBorder() {
        return borderPanel;
    }


    
    

    public final static Color colorBackgroundLighter = new Color(248, 250, 252);
    public final static Color colorBackground = new Color(240, 245, 249);
    public final static Color colorBackgroundDarker = new Color(203, 220, 236);
    public final static Color colorBackgroundDarkest = new Color(94, 147, 212);
    
    public static String setLookAndFeel(String laf) throws Exception {
        if (laf == null || laf.trim().length() == 0) {
            laf = UIManager.getCrossPlatformLookAndFeelClassName();
            // laf = UIManager.getSystemLookAndFeelClassName();
        }
        
        UIManager.setLookAndFeel(laf);

        // change gray background to light blue'ish
        // codes:  https://link.springer.com/content/pdf/bbm%3A978-1-4302-0033-8%2F1.pdf
        
        // google: "Java UIManager color keys"
        //   https://alvinalexander.com/java/java-uimanager-color-keys-list
        
        ArrayList<String> al = new ArrayList<>();
        UIDefaults uid = UIManager.getLookAndFeelDefaults();
        for (Entry<Object, Object> entry : uid.entrySet()) {
            Object idx = entry.getKey();
            if (!(idx instanceof String)) continue;
            String id = (String) idx;
            if (id.toLowerCase().indexOf("background") >= 0) {
                al.add(id);
            }
        }
        ColorUIResource c = (ColorUIResource) UIManager.get("Panel.background");  // 238,238,238
         
        for (String id : al) {
          Object objx = uid.get(id);
          if (!(objx instanceof ColorUIResource)) continue;
          if (objx.equals(c)) {
              UIManager.put(id, new ColorUIResource(colorBackground));
          }
        }
        UIManager.put("ComboBox.background", new ColorUIResource(Color.white));
        UIManager.put("ComboBox.disabledBackground", new ColorUIResource(Color.white));
        UIManager.put("Label.background", new ColorUIResource(Color.white));
        UIManager.put("TextField.disabledBackground", new ColorUIResource(Color.white));
        UIManager.put("TextField.inactiveBackground", new ColorUIResource(colorBackgroundLighter));  // txt.setEditable(false);
            
        return laf;
    }
    
    // 20181210
    public static void initializeLabel(JLabel lbl, boolean bIsCalc) {
        if (lbl == null) return;
        lbl.setOpaque(true); 

        if (bIsCalc) {
            Border border = lbl.getBorder();
            border = new CompoundBorder(new CustomLineBorder(0, 3, 0, 0, colorBackgroundDarker), border); 
            lbl.setBorder(border);
        }
    }
    
    
    private static Window window;
    public static void setMainWindow(Window win) {
        window = win;
    }
    public static void setMainWindow(Component... comps) {
        for (int i=0; window==null && comps!=null && i<comps.length; i++) {
            window = OAJfcUtil.getWindow(comps[i]);
        }
    }
    public static Window getMainWindow() {
        return window;
    }
    

    private static JLabel lblStatusBar;
    public static void setStatusBarLabel(JLabel lbl) {
        lblStatusBar = lbl;
    }
    public static JLabel getStatusBarLabel() {
        return lblStatusBar;
    }
    
    
    public static void showErrorMessage(String title, String msg) {
        JOptionPane.showMessageDialog(getMainWindow(), 
                msg, title, JOptionPane.ERROR_MESSAGE); 
    }
    public static void showWarningMessage(String title, String msg) {
        JOptionPane.showMessageDialog(getMainWindow(), 
                msg, title, JOptionPane.WARNING_MESSAGE); 
    }
    public static void showInformationMessage(String title, String msg) {
        JOptionPane.showMessageDialog(getMainWindow(), 
                msg, title, JOptionPane.INFORMATION_MESSAGE); 
    }
    public static boolean showConfirmMessage(String title, String msg) {
        int i = JOptionPane.showConfirmDialog(getMainWindow(), 
                msg,
                title, 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
    
        return (i == JOptionPane.YES_OPTION);
    }
    
    
    public static void showMessage(String title, String errorMsg) {
        JOptionPane.showMessageDialog(getMainWindow(), 
                errorMsg, title, JOptionPane.INFORMATION_MESSAGE); 
    }


}


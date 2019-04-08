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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.table.*;

import com.viaoa.object.*;
import com.viaoa.util.OAString;
import com.viaoa.hub.*;
import com.viaoa.jfc.border.CustomLineBorder;
import com.viaoa.jfc.control.*;
import com.viaoa.jfc.table.*;

public class OALabel extends JLabel implements OATableComponent, OAJfcComponent {
    private OALabelController control;
    private OATable table;
    private String heading = "";
    private boolean bHtml;
    

    /**
        Create label that is bound to a property for the active object in a Hub.
    */
    public OALabel(Hub hub, String propertyPath) {
        control = new OALabelController(hub, propertyPath);
        initialize();
    }
    /**
        Create label that is bound to a property for the active object in a Hub.
        @param cols width of label.
    */
    public OALabel(Hub hub, String propertyPath, int cols) {
        control = new OALabelController(hub, propertyPath);
        setColumns(cols);
        initialize();
    }

    /**
        Create label that is bound to a property for an object.
    */
    public OALabel(OAObject hubObject, String propertyPath) {
        control = new OALabelController(hubObject, propertyPath);
        initialize();
    }

    /**
        Create label that is bound to a property for an object.
        @param cols width of label.
    */
    public OALabel(OAObject hubObject, String propertyPath, int cols) {
        control = new OALabelController(hubObject, propertyPath);
        setColumns(cols);
        initialize();
    }

    /** Used with imageProperty, imagePath to display icon */
    public OALabel(Hub hub) {
        control = new OALabelController(hub);
        initialize();
    }

    @Override
    public void initialize() {
        boolean bIsCalc = false;
        Hub h = getHub();
        if (h != null) {
            OAObjectInfo oi = h.getOAObjectInfo();
            String prop = control.getPropertyPath();
            OACalcInfo ci = oi.getCalcInfo(prop);
            bIsCalc = (ci != null);
        }
        OAJfcUtil.initializeLabel(this, bIsCalc);
    }
    
    public OALabelController getController() {
    	return control;
    }

    public void setPassword(boolean b) {
        getController().setPassword(b);
    }
    public boolean isPassword() {
        return getController().isPassword();
    }

    
    /** 
        Format used to display this property.  Used to format Date, Times and Numbers.
    */
    public void setFormat(String fmt) {
        control.setFormat(fmt);
    }
    /** 
        Format used to display this property.  Used to format Date, Times and Numbers.
    */
    public String getFormat() {
        return control.getFormat();
    }

    public void addNotify() {
        if (getColumns() > 0) setColumns(getColumns());
        super.addNotify();
    }
    
    
    /**
        Get the property name used for displaying an image with component.
    */
    public void setImageProperty(String prop) {
        control.setImagePropertyPath(prop);
    }
    /**
        Get the property name used for displaying an image with component.
    */
    public String getImageProperty() {
        return control.getImagePropertyPath();
    }

    /**
        Root directory path where images are stored.
    */
    public void setImagePath(String path) {
        control.setImagePropertyPath(path);
    }
    /**
        Root directory path where images are stored.
    */
    public String getImagePath() {
        return control.getImagePropertyPath();
    }

    
    public int getMaxImageHeight() {
    	return control.getMaxImageHeight();
	}
	public void setMaxImageHeight(int maxImageHeight) {
		control.setMaxImageHeight(maxImageHeight);
	}

	public int getMaxImageWidth() {
		return control.getMaxImageWidth();
	}
	public void setMaxImageWidth(int maxImageWidth) {
		control.setMaxImageWidth(maxImageWidth);
	}
    
    
    /**
        Hub this this component is bound to.
    */
    public Hub getHub() {
        if (control == null) return null;
        return control.getHub();
    }

    /**
        Returns the single object that is bound to this component.
    */
    public Object getObject() {
        return control.getObject();
    }

    /**
        Set by OATable when this component is used as a column.
    */
    public void setTable(OATable table) {
        this.table = table;
        if (table != null) table.resetColumn(this);
    }


    /**
        Set by OATable when this component is used as a column.
    */
    public OATable getTable() {
        return table;
    }

    /**
        Width of label, based on average width of the font's character 'w'.
    */
    public int getColumns() {
        return control.getColumns();            
    }
    /**
        Width of label, based on average width of the font's character.
    */
    public void setColumns(int x) {
        control.setColumns(x);
        invalidate();
    }
    public void setMaximumColumns(int x) {
        control.setMaximumColumns(x);
        invalidate();
    }
    public int getMaximumColumns() {
        return control.getMaximumColumns();
    }
    public void setMaxColumns(int x) {
        control.setMaximumColumns(x);
        invalidate();
    }
    public void setMaxCols(int x) {
        control.setMaximumColumns(x);
        invalidate();
    }
    public int getMaxColumns() {
        return control.getMaximumColumns();
    }
    public void setMiniColumns(int x) {
        control.setMinimumColumns(x);
        invalidate();
    }
    public int getMiniColumns() {
        return control.getMinimumColumns();
    }
    public void setMinimumColumns(int x) {
        control.setMinimumColumns(x);
        invalidate();
    }
    public int getMinimumColumns() {
        return control.getMinimumColumns();
    }

    
    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if (isPreferredSizeSet()) return d;

        String text = getText();
        if (text == null) text = "";
        final int textLen = text.length();
        
        // resize based on size of text        
        int cols = getController().getCalcColumns();
        int maxCols = getMaximumColumns();
        if (cols < 1 && maxCols < 1) return d;
        if (maxCols < 1) maxCols = cols;
        else if (cols < 1) cols = 0;
        
        if (textLen >= cols && textLen > 0) {
            FontMetrics fm = getFontMetrics(getFont());
            if (textLen > maxCols) text = text.substring(0, maxCols);
            d.width = fm.stringWidth(text) + 8;
        }
        else {
            d.width = OAJfcUtil.getCharWidth(cols);
        }

        Insets ins = getInsets();
        if (ins != null) d.width += ins.left + ins.right;
        
        if (control != null) {
            Icon icon = getIcon();
            if (icon != null) d.width += (icon.getIconWidth() + 10);
        }
        
        d.height = OATextField.getStaticPreferredHeight();
        return d;
    }
    
    
    @Override
    public Dimension getMaximumSize() {
        Dimension d = super.getMaximumSize();
        if (isMaximumSizeSet()) return d;

        String text = getText();
        if (text == null) text = "";
        final int textLen = text.length();
        
        // resize based on size of text        
        int cols = getController().getCalcColumns();
        int maxCols = getMaximumColumns();
        if (cols < 1 && maxCols < 1) return d;
        if (maxCols < 1) maxCols = cols;
        else if (cols < 1) cols = 0;

        if (textLen >= cols && textLen > 0) {
            FontMetrics fm = getFontMetrics(getFont());
            if (textLen > maxCols) text = text.substring(0, maxCols);
            d.width = fm.stringWidth(text) + 8;
        }
        else d.width = OAJfcUtil.getCharWidth(cols);
        
        Insets ins = getInsets();
        if (ins != null) d.width += ins.left + ins.right;
        
        if (control != null) {
            Icon icon = getIcon();
            if (icon != null) d.width += (icon.getIconWidth() + 10);
        }
        
        d.height = OATextField.getStaticPreferredHeight()+2;
        return d;
    }
    
    public Dimension getMinimumSize() {
        Dimension d = super.getMinimumSize();
        if (isMinimumSizeSet()) return d;
        
        Dimension dx = getPreferredSize();
        d.height = Math.max(dx.height, d.height);
        
        int cols = getMiniColumns();
        if (cols < 1) return d;
        d.width = OAJfcUtil.getCharWidth(cols+1);

        return d;
    }
        
    /**
        Property path used to retrieve/set value for this component.
    */
    @Override
    public String getPropertyPath() {
        if (control == null) return null;
        return control.getPropertyPath();
    }
/*    
    public String getEndPropertyName() {
        if (control == null) return null;
        return control.getEndPropertyName();
    }
*/    
    /**
        Column heading when this component is used as a column in an OATable.
    */
    public String getTableHeading() { 
        return heading;   
    }
    /**
        Column heading when this component is used as a column in an OATable.
    */
    public void setTableHeading(String heading) {
        this.heading = heading;
        if (table != null) table.setColumnHeading(table.getColumnIndex(this),heading);
    }

    /**
        Editor used when this component is used as a column in an OATable.
    */
    public TableCellEditor getTableCellEditor() {
        return null;
    }

    // OATableComponent Interface method
    public Component getTableRenderer(JLabel renderer, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (control != null) {
            control.getTableRenderer(renderer, table, value, isSelected, hasFocus, row, column);
        }
        return renderer;
    }

    
    public void setIconColorProperty(String s) {
    	control.setIconColorPropertyPath(s);
    }
    public String getIconColorProperty() {
    	return control.getIconColorPropertyPath();
    }
    
    public void setBackgroundColorProperty(String s) {
        control.setBackgroundColorPropertyPath(s);
    }
    public String getBackgroundColorProperty() {
    	return control.getBackgroundColorPropertyPath();
    }

    
    public void addEnabledCheck(Hub hub) {
        control.getEnabledChangeListener().add(hub);
    }
    
    public void addEnabledCheck(Hub hub, String propPath) {
        control.getEnabledChangeListener().addPropertyNotNull(hub, propPath);
    }
    public void addEnabledCheck(Hub hub, String propPath, Object compareValue) {
        control.getEnabledChangeListener().add(hub, propPath, compareValue);
    }
    protected boolean isEnabled(boolean defaultValue) {
        return defaultValue;
    }
    public void addVisibleCheck(Hub hub) {
        control.getVisibleChangeListener().add(hub);
    }
    
    public void addVisibleCheck(Hub hub, String propPath) {
        control.getVisibleChangeListener().addPropertyNotNull(hub, propPath);
    }
    public void addVisibleCheck(Hub hub, String propPath, Object compareValue) {
        control.getVisibleChangeListener().add(hub, propPath, compareValue);
    }
    protected boolean isVisible(boolean defaultValue) {
        return defaultValue;
    }

    public class OALabelController extends LabelController {
        public OALabelController(Hub hub) {
            super(hub, OALabel.this);
        }
        public OALabelController(Hub hub, String propertyPath) {
            super(hub, OALabel.this, propertyPath);
        }
        public OALabelController(OAObject hubObject, String propertyPath) {
            super(hubObject, OALabel.this, propertyPath);
        }        

        @Override
        protected boolean isVisible(boolean bIsCurrentlyVisible) {
            bIsCurrentlyVisible = super.isVisible(bIsCurrentlyVisible);
            return OALabel.this.isVisible(bIsCurrentlyVisible);
        }
        
        // 20160516
        @Override
        protected boolean isEnabled(boolean bIsCurrentlyEnabled) {
            return bIsCurrentlyEnabled;
        }
    }
    
    @Override
    public String getTableToolTipText(JTable table, int row, int col, String defaultValue) {
        Object obj = ((OATable) table).getObjectAt(row, col);
        defaultValue = getToolTipText(obj, row, defaultValue);
        return defaultValue;
    }
    
    @Override
    public void customizeTableRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column,boolean wasChanged, boolean wasMouseOver) {
        Object obj = ((OATable) table).getObjectAt(row, column);
        
        if ((value instanceof String) && getHtml()) {
            String sval = (String) value;
            if (sval.toLowerCase().indexOf("<html") < 0) {
                if (sval.indexOf("<") >= 0 && sval.indexOf(">") >= 0) {
                    lbl.setText("<html>"+sval);
                }
            }
        }
        
        customizeRenderer(lbl, obj, value, isSelected, hasFocus, row, wasChanged, wasMouseOver);
    }

    public void setLabel(JLabel lbl, boolean bAlwaysMatchEnabled) {
        getController().setLabel(lbl, bAlwaysMatchEnabled);
    }
    public void setLabel(JLabel lbl, Hub hubForLabel) {
        getController().setLabel(lbl, false, hubForLabel);
    }
    public void setLabel(JLabel lbl) {
        getController().setLabel(lbl);
    }
    public JLabel getLabel() {
        if (getController() == null) return null;
        return getController().getLabel();
    }

    private int lastHortAlign;
    private Object lastObject;
    @Override
    public void setText(String text) {
        boolean b = OAString.isEqual(text, getText());
        
        if (text == null) text = "";
        super.setText(text);
        
        if (!b && bAllowSetTextBlink) {
            Hub hx = getHub();
            if (hx == null) return;
            Object objx = hx.getAO();
            int x = getHorizontalAlignment();
            if (lastObject != objx) {
                lastObject = objx;
                lastHortAlign = x;
                return;
            }
            if (x != lastHortAlign) {
                lastHortAlign = x;
                return;
            }
            
            // blink
            OAJfcUtil.blink(this);
        }
        invalidate();
    }
    
    
    private boolean bAllowSetTextBlink=true;
    public void setAllowSetTextBlink(boolean b) {
        bAllowSetTextBlink = b;
    }
    
    
/* moved to OAJfcUtil    
    private Color fgColor, bgColor;
    private final AtomicInteger aiBlink = new AtomicInteger();
    
    public void blink(final Color fcolor, final Color bcolor, final int numberOfTimes) {
        final int cntBlink = aiBlink.getAndIncrement();
        if (fgColor == null) fgColor = this.getForeground(); 
        if (bgColor == null) bgColor = this.getBackground(); 
        final Timer timer = new Timer(150, null);

        ActionListener al = new ActionListener() {
            int cnt;
            public void actionPerformed(ActionEvent e) {
                if (cntBlink != aiBlink.get()) {
                    cnt = numberOfTimes-1;
                }

                boolean b = (cnt++ % 2 == 0);
                
                Color c;
                if (fcolor != null) {
                    c = (b ? fcolor : fgColor);
                    setForeground(c);
                }
                if (bcolor != null) {
                    c = (b ? bcolor : bgColor);
                    setBackground(c);
                }

                if (!b && ((cnt / 2) >= numberOfTimes) ) {
                    timer.stop();
                }
            }
        };                 
        timer.addActionListener(al);
        timer.setRepeats(true);
        timer.setInitialDelay(250);
        timer.start();
    }
*/    
    
    public void setDisplayTemplate(String s) {
        this.control.setDisplayTemplate(s);
    }
    public String getDisplayTemplate() {
        return this.control.getDisplayTemplate();
    }
    public void setToolTipTextTemplate(String s) {
        this.control.setToolTipTextTemplate(s);
    }
    public String getToolTipTextTemplate() {
        return this.control.getToolTipTextTemplate();
    }
    public void setToolTipTextPropertyPath(String pp) {
        this.control.setToolTipTextPropertyPath(pp);
    }
    
    public void setHtml(boolean b) {
        control.setHtml(b);
    }
    public boolean getHtml() {
        return control.getHtml();
    }

    /*
    public void setNameValueHub(Hub<String> hub) {
        control.setNameValueHub(hub);
    }
    public Hub<String> getNameValueHub() {
        return control.getNameValueHub();
    }
    */
}

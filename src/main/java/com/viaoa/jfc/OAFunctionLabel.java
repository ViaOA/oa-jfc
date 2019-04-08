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
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;
import javax.swing.table.*;

import com.viaoa.object.*;
import com.viaoa.util.OAConv;
import com.viaoa.util.OAString;
import com.viaoa.func.OAFunction;
import com.viaoa.hub.*;
import com.viaoa.jfc.control.*;
import com.viaoa.jfc.table.*;

/**
 * Uses OAFunction to update a label.
 * @author vvia
 *
 */
public class OAFunctionLabel extends JLabel implements OATableComponent, OAJfcComponent {
    private OAFunctionLabelController control;
    private OATable table;
    private String heading = "";
    
    public enum Type {
        Sum
        //todo: Count, Max, Min
    }
    
    public OAFunctionLabel(Hub hub, Type type, String propertyPath) {
        control = new OAFunctionLabelController(hub, type, propertyPath);
        initialize();
    }
    public OAFunctionLabel(Hub hub, Type type, String propertyPath, int cols) {
        control = new OAFunctionLabelController(hub, type, propertyPath);
        setColumns(cols);
        initialize();
    }

    @Override
    public void initialize() {
        OAJfcUtil.initializeLabel(this, true);
    }
    
    public OAFunctionLabelController getController() {
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
        Hub this this component is bound to.
    */
    public Hub getHub() {
        if (control == null) return null;
        return control.getHub();
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
    public void setMaxColumns(int x) {
        control.setMaximumColumns(x);
        invalidate();
    }
    public int getMaxColumns() {
        return control.getMaximumColumns();
    }
    public int getMaximumColumns() {
        return control.getMaximumColumns();
    }
    public void setMiniColumns(int x) {
        control.setMinimumColumns(x);
        invalidate();
    }
    public int getMiniColumns() {
        return control.getMinimumColumns();
    }
    public void setMinColumns(int x) {
        control.setMinimumColumns(x);
        invalidate();
    }
    public int getMinColumns() {
        return control.getMinimumColumns();
    }

/**    
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if (isPreferredSizeSet()) return d;
        int cols = getColumns();
        if (cols <= 0) {
            cols = control.getPropertyInfoDisplayColumns();
        }
        
        if (cols > 0) {
            Insets ins = getInsets();
            int inx = ins == null ? 0 : ins.left + ins.right;
            d.width = OAJfcUtil.getCharWidth(cols)+inx+2;
        }
        d.height = OATextField.getStaticPreferredHeight();
        
        return d;
    }
    
    public Dimension getMaximumSize() {
        Dimension d = super.getMaximumSize();
        if (isMaximumSizeSet()) return d;
        int cols = getMaxColumns();
        if (cols < 1)  {
            //maxCols = control.getDataSourceMaxColumns();
            //if (maxCols < 1) {
            cols = control.getPropertyInfoMaxLength();
            
            if (cols < 1) {
                cols = getColumns() * 2; 
            }
        }

        Insets ins = getInsets();
        int inx = ins == null ? 0 : ins.left + ins.right;
        
        if (cols > 0) d.width = OAJfcUtil.getCharWidth(cols) + inx;
        else {
            // also check size of text, so that label is not bigger then the text it needs to display
            String s = getText();
            if (s == null) s = " ";
            
            FontMetrics fm = getFontMetrics(getFont());
            d.width = Math.min(d.width, fm.stringWidth(s)+inx+2);
        }
        
        // dont size under pref size
        Dimension dx = getPreferredSize();
        d.width = Math.max(d.width, dx.width);
        d.height = OATextField.getStaticPreferredHeight()+2;

        return d;
    }

    public Dimension getMinimumSize() {
        Dimension d = super.getMinimumSize();
        if (isMinimumSizeSet()) return d;
        int cols = getMiniColumns();
        if (cols < 1) return d;
        d.width = OAJfcUtil.getCharWidth(cols+1);
        return d;
    }
*/
    
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

    public Type getType() {
        return getController().getType();
    }

    public class OAFunctionLabelController extends LabelController {
        private Type type;
        public OAFunctionLabelController(Hub hub, Type type, String propertyPath) {
            super(hub, OAFunctionLabel.this, propertyPath, HubChangeListener.Type.HubValid, false);  // false= listen to all objects in Hub 
            //was: getChangeListener().add(hub, ppFunc, true, HubChangeListener.Type.HubValid, null, false);
        }

        public Type getType() {
            return type;
        }
        
        @Override
        protected boolean isVisible(boolean bIsCurrentlyVisible) {
            bIsCurrentlyVisible = super.isVisible(bIsCurrentlyVisible);
            return OAFunctionLabel.this.isVisible(bIsCurrentlyVisible);
        }
        
        @Override
        protected boolean isEnabled(boolean bIsCurrentlyEnabled) {
            return true;
        }
        
        @Override
        protected void _update() {
            if (thisLabel == null) return;
            String val;
            if (hub == null) val = "";
            else if (!hub.isValid()) val = "";
            else {
                double sum = OAFunction.sum(hub, getPropertyPath());
                val = OAConv.toString(sum, getFormat());
            }
            thisLabel.setText(val);
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
        customizeRenderer(lbl, obj, value, isSelected, hasFocus, row, wasChanged, wasMouseOver);
    }

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

    private Hub lastHub;
    @Override
    public void setText(String text) {
        boolean b = OAString.isEqual(text, getText());
        super.setText(text);
        if (b) return;
        Hub hx = getHub();
        if (hx == null) return;
        hx = getHub().getSharedHub();
        if (lastHub != hx) {
            lastHub = hx;
            return;
        }
        // blink
        OAJfcUtil.blink(this);
    }
    
}

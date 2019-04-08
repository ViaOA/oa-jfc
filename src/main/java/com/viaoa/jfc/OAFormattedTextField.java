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
import javax.swing.table.*;

import com.viaoa.hub.*;
import com.viaoa.jfc.control.*;
import com.viaoa.jfc.table.*;
import com.viaoa.util.OAConv;
import com.viaoa.util.OAString;

public class OAFormattedTextField extends BaseFormattedTextField implements OATableComponent, OAJfcComponent {
    private FormattedTextFieldController control;
    private OATable table;
    private String heading = "";
    private OAFormattedTextFieldTableCellEditor tableCellEditor;
    private String format; // instead of using control.format 

    /**
        Create TextField that is bound to a property path in a Hub.
        @param propertyPath path from Hub, used to find bound property.
        @param cols is the width
    */
    public OAFormattedTextField(Hub hub, String propertyPath, int cols, String mask, String validChars, boolean bRightJustified, boolean bAllowSpaces) {
        super(mask, validChars, bRightJustified, bAllowSpaces);
        control = new FormattedTextFieldController(hub, this, propertyPath) {
            @Override
            protected boolean isEnabled(boolean bIsCurrentlyEnabled) {
                return OAFormattedTextField.this.isEnabled(bIsCurrentlyEnabled);
            }
            @Override
            protected boolean isVisible(boolean bIsCurrentlyVisible) {
                return OAFormattedTextField.this.isVisible(bIsCurrentlyVisible);
            }
            @Override
            public String isValid(Object object, Object value) {
                String msg = OAFormattedTextField.this.isValidCallback(object, value);
                if (msg == null) msg = super.isValid(object, value);
                return msg;
            }
            @Override
            public void afterChangeActiveObject() {
                super.afterChangeActiveObject();
                OAFormattedTextField.this.revalidate();
            }
            @Override
            public void afterPropertyChange() {
                super.afterPropertyChange();
                OAFormattedTextField.this.revalidate();
            }
        };
        setColumns(cols);
        if (mask != null && mask.length() > 0) {
            boolean b = true;
            for (int i=0; !b && i<mask.length(); i++) {
                char ch = mask.charAt(i);
                if ("#,.09 ".indexOf(ch) < 0) b = false; 
            }
            if (b) this.format = "R("+mask+")";
            else this.format = "L("+mask+")";
        }
        initialize();
    }

    @Override
    public void initialize() {
    }
    
    
    public void addNotify() {
        super.addNotify();
        control.afterChangeActiveObject();
    }

    // ----- OATableComponent Interface methods -----------------------
    public Hub getHub() {
        return control.getHub();
    }
    public void setTable(OATable table) {
        this.table = table;
    }
    public OATable getTable() {
        return table;
    }
    public void setColumns(int x) {
        super.setColumns(x);
        getController().setColumns(x);
    }
    
    @Override
    public OAJfcController getController() {
        return control;
    }
    
    @Override
    public String getFormat() {
        return this.format;
    }

/*    
    public String getEndPropertyName() {
        return control.getEndPropertyName();
    }
*/    
    public String getPropertyPath() {
        return control.getPropertyPath();
    }
    public String getTableHeading() {
        return heading;
    }
    public void setTableHeading(String heading) {
        this.heading = heading;
        if (table != null) table.setColumnHeading(table.getColumnIndex(this),heading);
    }

    @Override
    protected int getColumnWidth() {
        int x = OAJfcUtil.getCharWidth();
        return x;
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if (isPreferredSizeSet()) return d;
        
        int cols = getController().getCalcColumns();

        String s = getText();
        if (s == null) s = "";
        int x = s.length();
        if (x >= cols) {
            int max = getMaximumColumns();
            if (max > 0) {
                if (x < max) cols = x+1;
                else cols = max;
            }
        }
        Insets ins = getInsets();
        int inx = ins == null ? 0 : ins.left + ins.right;
        
        d.width = OAJfcUtil.getCharWidth(cols) + inx;
        return d;
    }
    @Override
    public Dimension getMaximumSize() {
        Dimension d = super.getMaximumSize();
        if (isMaximumSizeSet()) return d;

        // resize based on size of text        
        int cols = getController().getCalcColumns();
        String s = getText();
        if (s == null) s = "";
        int x = Math.max(s.length(), cols);
        
        int maxCols = getMaximumColumns();
        if (maxCols < 1) {
            maxCols = cols;
        }

        cols = Math.min(x, maxCols);
        
        Insets ins = getInsets();
        int inx = ins == null ? 0 : ins.left + ins.right;
        
        d.width = OAJfcUtil.getCharWidth(cols) + inx;
        return d;
    }
    public Dimension getMinimumSize() {
        Dimension d = super.getMinimumSize();
        if (isMinimumSizeSet()) return d;
        int cols = getMinimumColumns();

        if (cols < 1) return d;
        d.width = OAJfcUtil.getCharWidth(cols);
        return d;
    }

    public void setMaximumColumns(int x) {
        control.setMaximumColumns(x);
        invalidate();
    }
    public int getMaximumColumns() {
        return control.getMaximumColumns();
    }
    public void setMinimumColumns(int x) {
        control.setMinimumColumns(x);
        invalidate();
    }
    public int getMinimumColumns() {
        return control.getMinimumColumns();
    }
    public void setMaxColumns(int x) {
        control.setMaximumColumns(x);
        invalidate();
    }
    public int getMaxColumns() {
        return control.getMaximumColumns();
    }
    public void setMinColumns(int x) {
        control.setMinimumColumns(x);
        invalidate();
    }
    public int getMinColumns() {
        return control.getMinimumColumns();
    }
    
    
    
    /** called by getTableCellRendererComponent */
    public Component getTableRenderer(JLabel renderer, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (control != null) {
            control.getTableRenderer(renderer, table, value, isSelected, hasFocus, row, column);
            if (OAString.isNotEmpty(this.format)) {
                value = OAConv.toString(value, this.format);
                renderer.setText((String) value);
            }
        }
        return renderer;
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

    public TableCellEditor getTableCellEditor() {
        if (tableCellEditor == null) {
            tableCellEditor = new OAFormattedTextFieldTableCellEditor(this);
        }
        return tableCellEditor;
    }

    
    
    @Override
    protected void onValueChange(String value) {
        if (control != null) {
            control.saveText(value);
        }
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


    /**
     * This is a callback method that can be overwritten to determine if the component should be visible or not.
     * @return null if no errors, else error message
     */
    public String isValidCallback(Object object, Object value) {
        return null;
    }
    
    public void setLabel(JLabel lbl) {
        getController().setLabel(lbl);
    }
    public JLabel getLabel() {
        if (getController() == null) return null;
        return getController().getLabel();
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
}




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
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.*;

import com.viaoa.hub.*;
import com.viaoa.jfc.control.*;
import com.viaoa.jfc.table.*;

public class OAAutoCompleteTextField extends JTextField implements OATableComponent, OAJfcComponent {
    private AutoCompleteTextFieldController control;
    private OATable table;
    private String heading = "";
    
    /**
     * Textfield that allows for finding object in hub.
     */
    public OAAutoCompleteTextField(Hub hub, String propertyPath, int cols) {
        control = new AutoCompleteTextFieldController(hub, this, propertyPath);
        setColumns(cols);
        enableEvents(AWTEvent.FOCUS_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
        initialize();
    }

    @Override
    public void initialize() {
    }
    
    public AutoCompleteTextFieldController getController() {
    	return control;
    }

    public void setFormat(String fmt) {
        control.setFormat(fmt);
    }
    public String getFormat() {
        return control.getFormat();
    }

    public Hub getHub() {
        if (control == null) return null;
        return control.getHub();
    }

    public void setMaxResults(int x) {
        control.setMaxResults(x); 
    }
    public int getMaxResults() {
        return control.getMaxResults();
    }
    
    
    public void setTable(OATable table) {
        this.table = table;
        if (table != null) table.resetColumn(this);
    }

    public OATable getTable() {
        return table;
    }

    public void setLabel(JLabel lbl) {
        getController().setLabel(lbl);
    }
    public void setLabel(JLabel lbl, Hub hubForLabel) {
        getController().setLabel(lbl, false, hubForLabel);
    }
    public JLabel getLabel() {
        if (getController() == null) return null;
        return getController().getLabel();
    }

    @Override
    protected int getColumnWidth() {
        int x = OAJfcUtil.getCharWidth();
        return x;
    }
    
    /**
        Width of label, based on average width of the font's character.
    */
    public void setColumns(int x) {
        super.setColumns(x);
        control.setColumns(x);
        invalidate();
    }
    /**
     * Max columns to be displayed, used when calculating max size.
     */
    public void setMaxCols(int x) {
        setMaximumColumns(x);
    }
    public void setMaxColumns(int x) {
        setMaximumColumns(x);
    }
    public void setMaximumColumns(int x) {
        control.setMaximumColumns(x);
        invalidate();
    }
    public int getMaxColumns() {
        return control.getMaximumColumns();
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
    public void setMinColumns(int x) {
        control.setMinimumColumns(x);
        invalidate();
    }
    public int getMinColumns() {
        return control.getMinimumColumns();
    }

    // 20181120
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

        d.height = OATextField.getStaticPreferredHeight(); 
        
        Insets ins = getInsets();
        if (ins != null) d.width += ins.left + ins.right;
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
    
        d.height = OATextField.getStaticPreferredHeight()+2; 
        
        Insets ins = getInsets();
        if (ins != null) d.width += ins.left + ins.right;
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
        
    /**
        Property path used to retrieve/set value for this component.
    */
    @Override
    public String getPropertyPath() {
        if (control == null) return null;
        return control.getPropertyPath();
    }

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
    protected MyTableCellEditor tableCellEditor;
    public TableCellEditor getTableCellEditor() {
        if (tableCellEditor != null) return tableCellEditor;
            
        tableCellEditor = new MyTableCellEditor();
                
        this.setBorder(new LineBorder(UIManager.getColor("Table.selectionBackground"), 1));
        
        return tableCellEditor;
    }

    private class MyTableCellEditor extends OATableCellEditor {
        public MyTableCellEditor() {
            super(OAAutoCompleteTextField.this);
        }
        public Object getCellEditorValue() {
            return OAAutoCompleteTextField.this.getText();
        }

        public void startCellEditing(java.util.EventObject e) {
            super.startCellEditing(e);
            try {
                OAAutoCompleteTextField.this.control.autoCompleteList.bIgnorePopup = true;
                OAAutoCompleteTextField.this.selectAll();
            }
            finally {
                OAAutoCompleteTextField.this.control.autoCompleteList.bIgnorePopup = false;
            }
        }

        int pos1, pos2;
        public void keyPressed(KeyEvent e) {
            pos1 = OAAutoCompleteTextField.this.getSelectionStart();
            pos2 = OAAutoCompleteTextField.this.getSelectionEnd();
        }

        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            editArrowKeys = (OATableCellEditor.UP | OATableCellEditor.DOWN | OATableCellEditor.LEFT | OATableCellEditor.RIGHT );
            if (pos1 == pos2) {
                if (key == KeyEvent.VK_LEFT) {
                    if (pos1 == 0) editArrowKeys = 0;
                }
                if (key == KeyEvent.VK_RIGHT) {
                    int x = OAAutoCompleteTextField.this.getText().length();
                    if (pos2 == x) editArrowKeys = 0;
                }
            }
            super.keyReleased(e);
        }
        
        public boolean getIgnorePopup(boolean bClear)  {
            if (lastMouseEvent == null) return false;
            if (bClear) lastMouseEvent = null;
            return true;
        }
        
    };
    
    
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

    public void setSearchTemplate(String s) {
        this.control.setSearchTemplate(s);
    }
    public String getSearchTemplate() {
        return this.control.getSearchTemplate();
    }

    @Override
    protected void processFocusEvent(FocusEvent e) {
        super.processFocusEvent(e);
        if (e.getID() == FocusEvent.FOCUS_GAINED) getController().onFocusGained();
        else if (e.getID() == FocusEvent.FOCUS_LOST) getController().onFocusLost();
    }
    
    @Override
    protected void processMouseEvent(MouseEvent e) {
        if ((e.getID() == MouseEvent.MOUSE_PRESSED) && tableCellEditor != null && tableCellEditor.getIgnorePopup(false)) {
            e.consume();
            return;
        }
        if ((e.getID() == MouseEvent.MOUSE_RELEASED) && tableCellEditor != null && tableCellEditor.getIgnorePopup(true)) {
            e.consume();
            return;
        }
        super.processMouseEvent(e);
    }
    
}

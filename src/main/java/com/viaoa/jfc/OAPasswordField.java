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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;

import com.viaoa.hub.Hub;
import com.viaoa.jfc.control.TextFieldController;
import com.viaoa.jfc.table.OAPasswordFieldTableCellEditor;
import com.viaoa.jfc.table.OATableComponent;
import com.viaoa.object.OAObject;
import com.viaoa.object.OAPropertyInfo;
import com.viaoa.util.OAPropertyPath;

public class OAPasswordField extends JPasswordField implements OATableComponent, OAJfcComponent {
	private OAPasswordFieldController control;
	private OATable table;
	private String heading = "";

	/**
	 * Bind a PasswordField to a property path in the active object of a Hub.
	 */
	public OAPasswordField(Hub hub, String propertyPath) {
		control = new OAPasswordFieldController(hub, propertyPath);
		initialize();
	}

	/**
	 * Bind a PasswordField to a property path in the active object of a Hub.
	 */
	public OAPasswordField(Hub hub, String propertyPath, int cols) {
		control = new OAPasswordFieldController(hub, propertyPath);
		setColumns(cols);
		initialize();
	}

	/**
	 * Bind a component to a property path for an object.
	 */
	public OAPasswordField(OAObject oaObject, String propertyPath) {
		control = new OAPasswordFieldController(oaObject, propertyPath);
		initialize();
	}

	/**
	 * Bind a component to a property path for an object.
	 */
	public OAPasswordField(OAObject oaObject, String propertyPath, int cols) {
		control = new OAPasswordFieldController(oaObject, propertyPath);
		setColumns(cols);
		initialize();
	}

	@Override
	public void initialize() {
		Hub h = getHub();
		if (h == null) {
			return;
		}
		OAPropertyPath pp = new OAPropertyPath(h.getObjectClass(), getController().getPropertyPath());

		OAPropertyInfo pi = pp.getEndPropertyInfo();
		if (pi != null) {
			if (pi.isEncrypted()) {
				setConversion('E');
			}
			if (pi.isSHAHash()) {
				setConversion('S');
			}
		}
	}

	@Override
	public OAPasswordFieldController getController() {
		return control;
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

	public String getPropertyPath() {
		return control.getPropertyPath();
	}

	public String getTableHeading() {
		return heading;
	}

	public void setTableHeading(String heading) {
		this.heading = heading;
		if (table != null) {
			table.setColumnHeading(table.getColumnIndex(this), heading);
		}
	}

	private OAPasswordFieldTableCellEditor tableCellEditor;

	public TableCellEditor getTableCellEditor() {
		if (tableCellEditor == null) {
			tableCellEditor = new OAPasswordFieldTableCellEditor(this);
		}
		return tableCellEditor;
	}

	/**
	 * 'U'ppercase, 'L'owercase, 'T'itle, 'J'ava identifier 'E'ncrpted password/encrypt 'S'HA password
	 */
	public void setConversion(char conv) {
		getController().setConversion(conv);
	}

	public char getConversion() {
		return getController().getConversion();
	}

	/** called by getTableCellRendererComponent */
	public Component getTableRenderer(JLabel renderer, JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		String s = renderer.getText();
		int x = s.length();
		StringBuffer sb = new StringBuffer(x);
		for (int i = 0; i < 8; i++) {
			sb.append('*');
		}
		renderer.setText(new String(sb));

		if (hasFocus) {
			renderer.setBorder(new LineBorder(UIManager.getColor("Table.selectionBackground"), 1));
		} else {
			renderer.setBorder(null);
		}

		if (hasFocus) {
			renderer.setForeground(UIManager.getColor("Table.focusCellForeground"));
			renderer.setBackground(UIManager.getColor("Table.focusCellBackground"));
		} else if (isSelected) {
			renderer.setForeground(UIManager.getColor("Table.selectionForeground"));
			renderer.setBackground(UIManager.getColor("Table.selectionBackground"));
		} else {
			renderer.setForeground(UIManager.getColor(table.getForeground()));
			renderer.setBackground(UIManager.getColor(table.getBackground()));
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
	public void customizeTableRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column,
			boolean wasChanged, boolean wasMouseOver) {
		Object obj = ((OATable) table).getObjectAt(row, column);
		customizeRenderer(lbl, obj, value, isSelected, hasFocus, row, wasChanged, wasMouseOver);
	}

	@Override
	public String getFormat() {
		return control.getFormat();
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
	 *
	 * @return null if no errors, else error message
	 */
	protected String isValidCallback(Object object, Object value) {
		return null;
	}

	public class OAPasswordFieldController extends TextFieldController {
		public OAPasswordFieldController(Hub hub, String propertyPath) {
			super(hub, OAPasswordField.this, propertyPath);
			setColumns(OAPasswordField.this.getColumns());
			// passwords are not encrypted by default
			// setConversion('P');
		}

		public OAPasswordFieldController(OAObject hubObject, String propertyPath) {
			super(hubObject, OAPasswordField.this, propertyPath);
			setColumns(OAPasswordField.this.getColumns());
			// passwords are not encrypted by default
			// setConversion('P');
		}

		@Override
		protected boolean isEnabled(boolean bIsCurrentlyEnabled) {
			bIsCurrentlyEnabled = super.isEnabled(bIsCurrentlyEnabled);
			return OAPasswordField.this.isEnabled(bIsCurrentlyEnabled);
		}

		@Override
		protected boolean isVisible(boolean bIsCurrentlyVisible) {
			bIsCurrentlyVisible = super.isVisible(bIsCurrentlyVisible);
			return OAPasswordField.this.isVisible(bIsCurrentlyVisible);
		}

		@Override
		public String isValid(Object object, Object value) {
			String msg = OAPasswordField.this.isValidCallback(object, value);
			if (msg == null) {
				msg = super.isValid(object, value);
			}
			return msg;
		}
	}

	public void setLabel(JLabel lbl) {
		getController().setLabel(lbl);
	}

	public JLabel getLabel() {
		if (getController() == null) {
			return null;
		}
		return getController().getLabel();
	}

	public void setConfirmMessage(String msg) {
		getController().setConfirmMessage(msg);
	}

	public String getConfirmMessage() {
		return getController().getConfirmMessage();
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
		if (isPreferredSizeSet()) {
			return d;
		}

		int cols = getController().getCalcColumns();

		String s = getText();
		if (s == null) {
			s = "";
		}
		int x = s.length();
		if (x >= cols) {
			int max = getMaximumColumns();
			if (max > 0) {
				if (x < max) {
					cols = x + 1;
				} else {
					cols = max;
				}
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
		if (isMaximumSizeSet()) {
			return d;
		}

		// resize based on size of text
		int cols = getController().getCalcColumns();
		String s = getText();
		if (s == null) {
			s = "";
		}
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
		if (isMinimumSizeSet()) {
			return d;
		}
		int cols = getMinimumColumns();

		if (cols < 1) {
			return d;
		}
		d.width = OAJfcUtil.getCharWidth(cols);
		return d;
	}

	/**
	 * Max columns to be displayed, used when calculating max size.
	 */
	public void setMaxInput(int x) {
		control.setMaxInput(x);
	}

	public int getMaxInput() {
		return control.getCalcMaxInput();
	}
}

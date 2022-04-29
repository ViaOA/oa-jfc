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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;

import com.viaoa.hub.Hub;
import com.viaoa.jfc.control.ToggleButtonController;
import com.viaoa.jfc.table.OACheckBoxTableCellEditor;
import com.viaoa.jfc.table.OATableComponent;
import com.viaoa.object.OAObject;
import com.viaoa.util.OAFilter;

public class OACheckBox extends JCheckBox implements OATableComponent, OAJfcComponent {
	OACheckBoxController control;
	OATable table;
	String heading;

	/**
	 * Create an unbound CheckBox.
	 */
	public OACheckBox() {
		createHub2ToggleButton(null, null, null, null);
		initialize();
	}

	public OACheckBox(String txt) {
		createHub2ToggleButton(null, null, null, null);
		setText(txt);
		initialize();
	}

	public void bind(Hub hub, String propertyPath) {
		control = new OACheckBoxController(hub, propertyPath, true, false);
	}

	private void createHub2ToggleButton(Hub hub, String propertyPath, Object onValue, Object offValue) {
		if (onValue == null) {
			onValue = new Boolean(true);
		}
		if (offValue == null) {
			offValue = new Boolean(false);
		}
		control = new OACheckBoxController(hub, propertyPath, onValue, offValue);
	}

	@Override
	public void initialize() {
	}

	/**
	 * Create CheckBox that is bound to a property for the active object in a Hub.
	 *
	 * @param cols is width of list using character width size.
	 */
	public OACheckBox(Hub hub, String propertyPath, int cols) {
		this(hub, propertyPath);
		setColumns(cols);
		initialize();
	}

	/**
	 * Create CheckBox that is bound to a property for the active object in a Hub.
	 */
	public OACheckBox(Hub hub, String propertyPath) {
		createHub2ToggleButton(hub, propertyPath, null, null);
		initialize();
	}

	/**
	 * Create CheckBox that is bound to a property for the active object in a Hub.
	 *
	 * @param onValue  value if value is considered true.
	 * @param offValue value if value is considered false.
	 */
	public OACheckBox(Hub hub, String propertyPath, Object onValue, Object offValue) {
		createHub2ToggleButton(hub, propertyPath, onValue, offValue);
		initialize();
	}

	/**
	 * Bind a button to have it add/remove objects with another Hub.
	 *
	 * @param hub       that has active object that is added/removed from hubSelect
	 * @param hubSelect the active object from hub will be added/removed from this Hub.
	 */
	public OACheckBox(Hub hub, Hub hubSelect) {
		control = new OACheckBoxController(hub, hubSelect);
		initialize();
	}

	protected boolean beforeChecked(boolean bSelected) {
		if (control == null) {
			return false;
		}
		if (control.isChanging()) {
			return false;
		}
		boolean b = confirmChange(bSelected);
		if (!b) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					control.afterChangeActiveObject(null);
				}
			});
		}
		return b;
	}

	/**
	 * Create CheckBox that is bound to a property for an object.
	 */
	public OACheckBox(OAObject oaObject, String propertyPath) {
		control = new OACheckBoxController(oaObject, propertyPath);
		initialize();
	}

	/**
	 * Create CheckBox that is bound to a property for an object.
	 *
	 * @param onValue  value if value is considered true.
	 * @param offValue value if value is considered false.
	 */
	public OACheckBox(OAObject oaObject, String propertyPath, Object onValue, Object offValue) {
		control = new OACheckBoxController(oaObject, propertyPath, onValue, offValue);
		initialize();
	}

	@Override
	public ToggleButtonController getController() {
		return control;
	}

	/**
	 * Bind a button to have it add/remove objects a Hub.
	 *
	 * @param hubSelect the active object from hub will be added/removed from this Hub.
	 */
	public void setSelectHub(Hub hubSelect) {
		control.setSelectHub(hubSelect);
	}

	/**
	 * Returns the Hub that maintains the selected objects.
	 *
	 * @see setSelectHub(Hub)
	 */
	public Hub getSelectHub() {
		return control.getSelectHub();
	}

	public void setXORValue(int xor) {
		control.setXORValue(xor);
	}

	public int getXORValue() {
		return control.getXORValue();
	}

	// ----- OATableComponent Interface methods -----------------------
	public Hub getHub() {
		if (control == null) {
			return null;
		}
		return control.getHub();
	}

	public void setTable(OATable table) {
		this.table = table;
		if (table != null) {
			table.resetColumn(this);
		}
	}

	public OATable getTable() {
		return table;
	}

	/** value property will be set to when selected. Default: TRUE */
	public Object getOnValue() {
		return control.valueOn;
	}

	public void setOnValue(Object value) {
		control.valueOn = value;
	}

	/** value property will be set to when deselected. Default: FALSE */
	public Object getOffValue() {
		return control.valueOff;
	}

	public void setOffValue(Object value) {
		control.valueOff = value;
	}

	/**
	 * Width of component, based on average width of the font's character.
	 */
	public int getColumns() {
		return getController().getColumns();
	}

	/**
	 * Width of component, based on average width of the font's character.
	 */
	public void setColumns(int x) {
		getController().setColumns(x);
	}

	@Override
	public String getPropertyPath() {
		return control.getPropertyPath();
	}
	/*
	public String getEndPropertyName() {
	    return control.getEndPropertyName();
	}
	*/

	/**
	 * Column heading when this component is used as a column in an OATable.
	 */
	public String getTableHeading() {
		return heading;
	}

	/**
	 * Column heading when this component is used as a column in an OATable.
	 */
	public void setTableHeading(String heading) { //zzzzz
		this.heading = heading;
		if (table != null) {
			table.setColumnHeading(table.getColumnIndex(this), heading);
		}
	}

	public JComponent getComponent() {
		return this;
	}

	OACheckBoxTableCellEditor tableCellEditor;

	/**
	 * Editor used when this component is used as a column in an OATable.
	 */
	public TableCellEditor getTableCellEditor() {
		if (tableCellEditor == null) {
			tableCellEditor = new OACheckBoxTableCellEditor(this);
			this.setHorizontalAlignment(JLabel.CENTER);
			this.setOpaque(true);
			this.setBackground(UIManager.getColor("Table.selectionBackground"));
			// this.setBackground( UIManager.getColor("Table.focusCellBackground") );
			this.setBorderPainted(true);
			this.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
		}
		return tableCellEditor;
	}

	boolean bRemoved;

	public void addNotify() {
		super.addNotify();
		if (bRemoved) {
			//control.resetHubOrProperty();
			bRemoved = false;
		}
	}

	/* 2005/02/07 need to manually call close instead
	public void removeNotify() {
	    super.removeNotify();
	    close();
	}
	*/
	public void close() {
		bRemoved = true;
		control.close();
	}

	JCheckBox chkRenderer;

	/**
	 * Called by getTableCellRendererComponent to display this component.
	 */
	@Override
	public Component getTableRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (chkRenderer == null) {
			chkRenderer = new JCheckBox() {
				@Override
				public void paint(Graphics g) {
					super.paint(g);
					if (!bHalfChecked) {
						return;
					}
					g.setColor(Color.gray);
					Dimension d = getSize();
					int w = d.width / 2;
					int h = d.height / 2;
					g.fillRect(w - 2, h - 1, 5, 3);
				}

				@Override
				public void setBackground(Color bg) {
					super.setBackground(bg);
				}
			};
			chkRenderer.setOpaque(true);
			chkRenderer.setHorizontalAlignment(JLabel.CENTER);
		}
		chkRenderer.setEnabled(true);
		boolean tf = false;
		Object obj = control.getHub().elementAt(row);
		if (control.getSelectHub() != null) {
			// Object obj = control.getActualHub().elementAt(row);
			if (obj != null && control.getSelectHub().getObject(obj) != null) {
				tf = true;
			}
		} else {
			if (value != null && value instanceof Boolean) {
				tf = ((Boolean) value).booleanValue();
			}
		}

		if (!isSelected && !hasFocus) {
			chkRenderer.setForeground(UIManager.getColor(table.getForeground()));
			chkRenderer.setBackground(UIManager.getColor(table.getBackground()));
		}

		chkRenderer.setText(null);
		control.update(chkRenderer, obj, false);
		chkRenderer.setSelected(tf);

		if (isSelected || hasFocus) {
			chkRenderer.setForeground(UIManager.getColor("Table.selectionForeground"));
			chkRenderer.setBackground(UIManager.getColor("Table.selectionBackground"));
		}
		return chkRenderer;
	}

	@Override
	public void customizeTableRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column,
			boolean wasChanged, boolean wasMouseOver) {
		Object obj = ((OATable) table).getObjectAt(row, column);
		customizeRenderer(lbl, obj, value, isSelected, hasFocus, row, wasChanged, wasMouseOver);

		// 20200625 need to update chkRenderer
		chkRenderer.setBackground(lbl.getBackground());
		chkRenderer.setForeground(lbl.getForeground());
		chkRenderer.setFont(lbl.getFont());
	}

	@Override
	public String getTableToolTipText(JTable table, int row, int col, String defaultValue) {
		Object obj = ((OATable) table).getObjectAt(row, col);

		getToolTipText(obj, row, defaultValue);
		return defaultValue;
	}

	private boolean bHalfChecked;

	public void setHalfChecked(boolean b) {
		bHalfChecked = b;
	}

	public boolean isHalfChecked() {
		return bHalfChecked;
	}

	// 200804/27 Hack: to work with OATable, to have mouse click check the box.
	// If you have a chk in a column and then click another row in same column, then the mouse pressed is not sent to checkbox
	private boolean bMousePressed;
	private boolean bSelected;

	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		if (getTable() == null) {
			return;
		}
		int id = e.getID();
		if (id == MouseEvent.MOUSE_PRESSED) {
			bSelected = isSelected();
			bMousePressed = true;
		} else if (id == MouseEvent.MOUSE_RELEASED) {
			if (isEnabled()) { // 20101220 added isEnabled check
				if (bMousePressed && (bSelected == isSelected())) {
					setSelected(!bSelected);
				}
			}
			bMousePressed = false;
		}
	}

	/*
	Popup message used to confirm button click before running code.
	*/
	public void setConfirmMessage(String msg) {
		control.setConfirmMessage(msg);
	}

	/**
	 * Popup message used to confirm button click before running code.
	 */
	public String getConfirmMessage() {
		return control.getConfirmMessage();
	}

	/** returns true if command is allowed */
	protected boolean confirmChange(boolean bSelected) {
		return true;
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

	public void addEnabledCheck(Hub hub, OAFilter filter) {
		control.getEnabledChangeListener().add(hub, filter);
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

	public void addVisibleCheck(Hub hub, OAFilter filter) {
		control.getVisibleChangeListener().add(hub, filter);
	}

	protected boolean isVisible(boolean defaultValue) {
		return defaultValue;
	}

	/**
	 * This is a callback method that can be overwritten to determine if the component should be visible or not.
	 *
	 * @return null if no errors, else error message
	 */
	protected String isValid(Object object, Object value) {
		return null;
	}

	class OACheckBoxController extends ToggleButtonController {
		public OACheckBoxController(Hub hub, String propertyPath) {
			super(hub, OACheckBox.this, propertyPath);
		}

		public OACheckBoxController(Hub hub, String propertyPath, Object onValue, Object offValue) {
			super(hub, OACheckBox.this, propertyPath, onValue, offValue);
		}

		public OACheckBoxController(OAObject hubObject, String propertyPath) {
			super(hubObject, OACheckBox.this, propertyPath);
		}

		public OACheckBoxController(OAObject hubObject, String propertyPath, Object onValue, Object offValue) {
			super(hubObject, OACheckBox.this, propertyPath, onValue, offValue);
		}

		public OACheckBoxController(Hub hub, Hub hubSelect) {
			super(hub, hubSelect, OACheckBox.this);
		}

		@Override
		protected boolean isEnabled(boolean bIsCurrentlyEnabled) {
			bIsCurrentlyEnabled = super.isEnabled(bIsCurrentlyEnabled);
			return OACheckBox.this.isEnabled(bIsCurrentlyEnabled);
		}

		@Override
		protected boolean isVisible(boolean bIsCurrentlyVisible) {
			bIsCurrentlyVisible = super.isVisible(bIsCurrentlyVisible);
			return OACheckBox.this.isVisible(bIsCurrentlyVisible);
		}

		@Override
		public String isValid(Object object, Object value) {
			String msg = OACheckBox.this.isValid(object, value);
			if (msg == null) {
				msg = super.isValid(object, value);
			}
			return msg;
		}

		@Override
		public void itemStateChanged(ItemEvent evt) {
			boolean b = evt.getStateChange() == ItemEvent.SELECTED;
			if (beforeChecked(b)) {
				super.itemStateChanged(evt);
			}
		}
	}

	public void setLabel(JLabel lbl) {
		getController().setLabel(lbl);
	}
	/*
	public JLabel getLabel() {
	    return getController().getLabel();
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

	public void setFormat(String fmt) {
		control.setFormat(fmt);
	}

	public String getFormat() {
		return control.getFormat();
	}

}

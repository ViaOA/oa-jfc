package com.viaoa.jfc;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;

import com.viaoa.hub.Hub;
import com.viaoa.object.OAObject;

public class OACheckBoxLabel extends OALabel {
	private JCheckBox chkRenderer;

	public OACheckBoxLabel(Hub hub, String propertyPath) {
		super(hub, propertyPath);
	}

	public OACheckBoxLabel(Hub hub, String propertyPath, int cols) {
		super(hub, propertyPath, cols);
	}

	public OACheckBoxLabel(OAObject hubObject, String propertyPath) {
		super(hubObject, propertyPath);
	}

	public OACheckBoxLabel(OAObject hubObject, String propertyPath, int cols) {
		super(hubObject, propertyPath, cols);
	}

	@Override
	public void initialize() {
		chkRenderer = new JCheckBox();
		chkRenderer.setOpaque(true);
		chkRenderer.setHorizontalAlignment(JLabel.CENTER);
		chkRenderer.setEnabled(true);

		super.initialize();
	}

	@Override
	public Component getTableRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		boolean tf = false;
		Object obj = getController().getHub().elementAt(row);
		if (getController().getSelectHub() != null) {
			// Object obj = control.getActualHub().elementAt(row);
			if (obj != null && getController().getSelectHub().getObject(obj) != null) {
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
		getController().update(chkRenderer, obj, false);
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

		chkRenderer.setBackground(lbl.getBackground());

		chkRenderer.setForeground(lbl.getForeground());
		chkRenderer.setFont(lbl.getFont());
	}

}

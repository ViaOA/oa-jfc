package com.viaoa.jfc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;

import com.viaoa.hub.Hub;
import com.viaoa.object.OAObject;

public class OAColorLabel extends OALabel {
	public OAColorLabel(Hub hub, String propertyPath) {
		super(hub, propertyPath);
	}

	public OAColorLabel(Hub hub, String propertyPath, int cols) {
		super(hub, propertyPath, cols);
	}

	public OAColorLabel(OAObject hubObject, String propertyPath) {
		super(hubObject, propertyPath);
	}

	public OAColorLabel(OAObject hubObject, String propertyPath, int cols) {
		super(hubObject, propertyPath, cols);
	}

	public void paintComponent(Graphics g) {
		Object obj = getController().getValue(getHub().getAO());

		Color color;
		if (obj instanceof Color) {
			color = (Color) obj;
		} else {
			color = null;
		}

		Dimension d = getSize();
		if (color != null) {
			g.setColor(color);
			int w = d.width;
			int h = d.height;
			if (w > 6) {
				w -= 6;
			}
			if (h > 6) {
				h -= 6;
			}
			g.fillRect(3, 3, w, h);
		}
	}

	private MyTableLabel lblRendererTable;

	private class MyTableLabel extends JLabel {
		Color color;

		public void paintComponent(Graphics g) {
			Dimension d = getSize();
			int w = d.width;
			int h = d.height;

			Color c = getBackground();
			g.setColor(c);
			g.fillRect(0, 0, w, h);

			if (color != null) {
				g.setColor(color);
				if (w > 6) {
					w -= 6;
				}
				if (h > 6) {
					h -= 6;
				}
				g.fillRect(3, 3, w, h);
			}
		}
	}

	@Override
	public Component getTableRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (lblRendererTable == null) {
			lblRendererTable = new MyTableLabel();
			lblRendererTable.setText("  ");
		}

		super.getTableRenderer(lblRendererTable, table, value, isSelected, hasFocus, row, column);
		return lblRendererTable;
	}

	@Override
	public void customizeTableRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column,
			boolean wasChanged, boolean wasMouseOver) {
		super.customizeTableRenderer(lbl, table, value, isSelected, hasFocus, row, column, wasChanged, wasMouseOver);
		lbl.setText("  ");
		lblRendererTable.color = null;
		Hub h = ((OATable) table).getHub();
		if (h != null) {
			Object obj = h.elementAt(row);
			obj = getController().getValue(obj);
			if (obj instanceof Color) {
				lblRendererTable.color = (Color) obj;
			}
		}

		if (isSelected && !hasFocus) {
			lbl.setBackground(UIManager.getColor("Table.selectionBackground"));
		}

		Object obj = ((OATable) table).getObjectAt(row, column);
		customizeRenderer(lbl, obj, value, isSelected, hasFocus, row, wasChanged, wasMouseOver);
	}

}

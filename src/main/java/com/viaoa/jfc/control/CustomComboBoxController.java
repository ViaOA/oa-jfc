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
package com.viaoa.jfc.control;

import java.awt.Component;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;

import com.viaoa.hub.Hub;
import com.viaoa.hub.HubChangeListener;
import com.viaoa.hub.HubEvent;
import com.viaoa.jfc.OACustomComboBox;
import com.viaoa.jfc.OAJfcUtil;
import com.viaoa.object.OAObject;
import com.viaoa.undo.OAUndoManager;
import com.viaoa.undo.OAUndoableEdit;
import com.viaoa.util.OAConv;
import com.viaoa.util.OANullObject;
import com.viaoa.util.OAString;

/**
 * Functionality for binding a custom JComboBox to OA.
 *
 * @author vvia
 */
public class CustomComboBoxController extends OAJfcController {
	JComboBox comboBox;
	public boolean bDisplayPropertyOnly; // 2007/05/25 used by OATreeComboBox so that setSelectedItem() does not try to update property

	public CustomComboBoxController(Hub hub, JComboBox cb, String propertyPath, boolean bUseLinkHub) {
		super(hub, null, propertyPath, cb, (bUseLinkHub ? HubChangeListener.Type.HubValid : HubChangeListener.Type.AoNotNull), bUseLinkHub,
				true);
		create(cb);
	}

	public CustomComboBoxController(Object obj, JComboBox cb, String propertyPath, boolean bUseLinkHub) {
		super(null, obj, propertyPath, cb, HubChangeListener.Type.AoNotNull, bUseLinkHub, true);
		create(cb);
	}

	@Override
	protected void reset() {
		super.reset();
		if (comboBox != null) {
			create(comboBox);
		}
	}

	protected void create(JComboBox cb) {
		this.comboBox = cb;

		if (getHub() == null) {
			return;
		}
		// so all combos will have a renderer for calculating width
		comboBox.setRenderer(new MyListCellRenderer());

		comboBox.setModel(new MyComboBoxModel());

		HubEvent e = new HubEvent(getHub(), getHub().getActiveObject());
		afterChangeActiveObject(e); // this will set selectedPos in JComboBox
	}

	/**
	 * Changing active object in Hub will set the select item in ComboBox. ComboBox is enabled based on if Hub is valid.
	 */
	public @Override void afterChangeActiveObject(HubEvent evt) {
		if (comboBox == null) {
			return;
		}
		OAObject oaObject = (OAObject) getHub().getActiveObject();
		comboBox.hidePopup(); // this is for CustomComboBoxes that will change AO, so that it will auto close

		Object value;
		if (oaObject == null) {
			value = null;
		} else {
			value = getValue(oaObject);
			// was: else value = oaObject.getProperty(getPropertyName());
		}

		if (evt != null) {
			comboBox.setSelectedItem(value);
		}
		callUpdate();
		comboBox.repaint();
		super.afterChangeActiveObject(evt);
	}

	/**
	 * Used to change selected item if property name matches property used by ComboBox.
	 */
	public @Override void afterPropertyChange(HubEvent e) {
		if (comboBox != null && e.getPropertyName().equalsIgnoreCase(getHubListenerPropertyName())) {
			afterChangeActiveObject(e);
		}
		super.afterPropertyChange(e);
	}

	/**
	 * Called when item is selected in ComboBox to update the property for active object in the Hub.
	 */
	public void updatePropertyValue(Object value) {
		if (bDisplayPropertyOnly) {
			return;
		}

		Hub h = getHub();
		if (getEnableUndo() && h != null) {
			OAObject obj = (OAObject) h.getAO();
			if (obj != null) {
				Object prev = getValue(obj);

				// was; Object prev = obj.getProperty(getPropertyName());
				if (value != prev && (value == null || !value.equals(prev))) {

					String error = isValid(obj, value);
					if (OAString.isNotEmpty(error)) {
						JOptionPane.showMessageDialog(comboBox, error, "Invalid", JOptionPane.WARNING_MESSAGE);
					}
					if (!confirmPropertyChange(obj, value)) {
						return;
					}

					if (getEnableUndo()) {
						final boolean wasChanged = (obj instanceof OAObject) && ((OAObject) obj).getChanged();
						OAUndoManager.add(OAUndoableEdit.createUndoablePropertyChange(	undoDescription, obj, endPropertyName, prev, value,
																						wasChanged));
					}
					try {
						setValue(obj, value);
					} catch (Exception e) {
						e.printStackTrace();
						String s = "Exception while setting value, " + e.toString();
						JOptionPane.showMessageDialog(
														OAJfcUtil.getWindow(comboBox),
														s, "Command failed",
														JOptionPane.ERROR_MESSAGE);

					}
					// was: obj.setProperty(getPropertyName(), value);
				}
			}
		}
	}

	/**
	 * 2006/12/11 copied from Hub2ComboBox Default cell renderer.
	 */
	class MyListCellRenderer extends JLabel implements ListCellRenderer {
		public MyListCellRenderer() {
			setOpaque(true);
		}

		/** will either call OAComboBox.getRenderer() or Hub2ComboBox.getRenderer() */
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component comp = CustomComboBoxController.this.getRenderer(this, list, value, index, isSelected, cellHasFocus);
			return comp;
		}
	}

	public Component getRenderer(Component renderer, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		boolean bDone = false;
		String s = null;
		if (value == null || value instanceof OANullObject) {
			s = "   ";
		} else if (value instanceof OANullObject) {
			s = nullDescription;
		} else if (value instanceof String) { // from prototype setting
			s = (String) value;
		} else {
			if (renderer instanceof JLabel) {
				update((JComponent) renderer, value, false);
				bDone = true;
			} else {
				Object obj = getValue(value);
				// was: Object obj = OAReflect.getPropertyValue(value, getGetMethods());
				s = OAConv.toString(obj, getFormat());
				if (s.length() == 0) {
					s = " "; // if length == 0 then Jlist wont show any
				}
			}
		}

		if (renderer instanceof JLabel) {
			JLabel lbl = (JLabel) renderer;

			if (!bDone) {
				update((JComponent) renderer, value, false);
			}

			if (!isSelected) {
				lbl.setBackground(list.getBackground());
				lbl.setForeground(list.getForeground());
			}

			if (!bDone) {
				lbl.setText(s);
			}

			if (isSelected) {
				lbl.setBackground(list.getSelectionBackground());
				lbl.setForeground(list.getSelectionForeground());
			}
		}
		return renderer;
	}

	// 2006/12/11
	//==============================================================================
	// note: these need to be ran in the AWT Thread, use SwingUtilities.invokeLater() to call these
	class MyComboBoxModel extends DefaultListModel implements ComboBoxModel {
		OANullObject empty = OANullObject.instance;

		public synchronized void setSelectedItem(Object obj) {
			if (comboBox instanceof OACustomComboBox) { // 20120508 hake to make sure that propertyChange does not happen more then once
				if (((OACustomComboBox) comboBox).bSetting) {
					return;
				}
			}
			updatePropertyValue(obj);
		}

		public Object getSelectedItem() {
			Object obj = getHub().getActiveObject();
			if (obj == null) {
				obj = empty;
			}
			return obj;
		}

		public Object getElementAt(int index) {
			if (getHub() == null) {
				return empty;
			}
			Object obj = getHub().elementAt(index);
			if (obj == null) {
				obj = empty;
			}
			return obj;
		}

		public int getSize() {
			return 1;
		}
	}

}

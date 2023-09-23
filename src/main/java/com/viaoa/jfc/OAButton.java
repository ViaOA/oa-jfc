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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;

import com.viaoa.hub.Hub;
import com.viaoa.hub.HubChangeListener;
import com.viaoa.hub.HubChangeListener.Type;
import com.viaoa.hub.HubDetailDelegate;
import com.viaoa.jfc.control.ButtonController;
import com.viaoa.jfc.dialog.OAPasswordDialog;
import com.viaoa.jfc.table.OAButtonTableCellEditor;
import com.viaoa.jfc.table.OATableComponent;
import com.viaoa.object.OAObject;
import com.viaoa.object.OAObjectDelegate;
import com.viaoa.util.OAString;

public class OAButton extends JButton implements OATableComponent, OAJfcComponent {
	public boolean DEBUG;
	private OAButtonController control;

	public static final ButtonCommand OTHER = ButtonCommand.Other;
	public static final ButtonCommand UP = ButtonCommand.Up;
	public static final ButtonCommand DOWN = ButtonCommand.Down;
	public static final ButtonCommand SAVE = ButtonCommand.Save;
	public static final ButtonCommand CANCEL = ButtonCommand.Cancel;
	public static final ButtonCommand FIRST = ButtonCommand.First;
	public static final ButtonCommand LAST = ButtonCommand.Last;
	public static final ButtonCommand NEXT = ButtonCommand.Next;
	public static final ButtonCommand PREVIOUS = ButtonCommand.Previous;
	public static final ButtonCommand DELETE = ButtonCommand.Delete;
	public static final ButtonCommand REMOVE = ButtonCommand.Remove;
	public static final ButtonCommand WIZARD_NEW = ButtonCommand.WizardNew;
	public static final ButtonCommand NEW = ButtonCommand.New;
	public static final ButtonCommand INSERT = ButtonCommand.Insert;
	public static final ButtonCommand Add = ButtonCommand.Add;
	public static final ButtonCommand CUT = ButtonCommand.Cut;
	public static final ButtonCommand COPY = ButtonCommand.Copy;
	public static final ButtonCommand PASTE = ButtonCommand.Paste;
	public static final ButtonCommand NEW_MANUAL = ButtonCommand.NewManual;
	public static final ButtonCommand ADD_MANUAL = ButtonCommand.AddManual;
	public static final ButtonCommand CLEARAO = ButtonCommand.ClearAO;
	public static final ButtonCommand GOTO = ButtonCommand.GoTo;
	public static final ButtonCommand HUBSEARCH = ButtonCommand.HubSearch;
	public static final ButtonCommand SEARCH = ButtonCommand.Search;
	public static final ButtonCommand SELECT = ButtonCommand.Select;
	public static final ButtonCommand OBJECT_METHOD = ButtonCommand.ObjectMethod;
	public static final ButtonCommand HUB_METHOD = ButtonCommand.HubMethod;
	public static final ButtonCommand OK = ButtonCommand.Ok;
	public static final ButtonCommand REFRESH = ButtonCommand.Refresh;
	public static final ButtonCommand STATIC_OBJECT_METHOD = ButtonCommand.StaticObjectMethod;

	public enum ButtonCommand {
		Other(false), Up(true), Down(true), Save(false),
		Cancel(false), // cancel changes to AO
		First(true), Last(true),
		Next(true), Previous(true), Delete(true), Remove(true), New(true), Insert(true), Add(true),
		Cut(false), Copy(false), Paste(false),
		NewManual(true), AddManual(true), ClearAO(true), GoTo(false), HubSearch(true), Search(false), Select(true), ObjectMethod(false),
		// call a method on Hub
		HubMethod(false),
		WizardNew(true), Ok(false), Refresh(true),
		// call static method in OAObject
		StaticObjectMethod(false);

		private boolean bSetsAO;

		ButtonCommand(boolean bSetsAO) {
			this.bSetsAO = bSetsAO;
		}

		public boolean getSetsAO() {
			return bSetsAO;
		}
	}

	public enum ButtonEnabledMode {
		UsesIsEnabled(HubChangeListener.Type.AlwaysTrue),
		Always(HubChangeListener.Type.AlwaysTrue),
		ActiveObjectNotNull(HubChangeListener.Type.AoNotNull),
		ActiveObjectNull(HubChangeListener.Type.AoNull),
		HubIsValid(HubChangeListener.Type.HubValid),
		HubIsNotEmpty(HubChangeListener.Type.HubNotEmpty),
		HubIsEmpty(HubChangeListener.Type.HubEmpty),
		AOPropertyIsNotEmpty(HubChangeListener.Type.PropertyNotNull),
		AOPropertyIsEmpty(HubChangeListener.Type.PropertyNull),
		SelectHubIsNotEmpty(HubChangeListener.Type.AlwaysTrue),
		SelectHubIsEmpty(HubChangeListener.Type.AlwaysTrue);

		private HubChangeListener.Type type;

		ButtonEnabledMode(HubChangeListener.Type type) {
			this.type = type;
		}

		public HubChangeListener.Type getHubChangeListenerType() {
			return this.type;
		}
	}

    public static ButtonEnabledMode ALWAYS = ButtonEnabledMode.Always;
	public static ButtonEnabledMode UsesIsEnabled = ButtonEnabledMode.UsesIsEnabled;
	public static ButtonEnabledMode Always = ButtonEnabledMode.Always;
	public static ButtonEnabledMode ActiveObjectNotNull = ButtonEnabledMode.ActiveObjectNotNull;
	public static ButtonEnabledMode ActiveObjectNull = ButtonEnabledMode.ActiveObjectNull;
	public static ButtonEnabledMode HubIsValid = ButtonEnabledMode.HubIsValid;
	public static ButtonEnabledMode HubIsNotEmpty = ButtonEnabledMode.HubIsNotEmpty;
	public static ButtonEnabledMode HubIsEmpty = ButtonEnabledMode.HubIsEmpty;
	public static ButtonEnabledMode AOPropertyIsNotEmpty = ButtonEnabledMode.AOPropertyIsNotEmpty;
	public static ButtonEnabledMode AOPropertyIsEmpty = ButtonEnabledMode.AOPropertyIsEmpty;
	public static ButtonEnabledMode SelectHubIsNotEmpty = ButtonEnabledMode.SelectHubIsNotEmpty;
	public static ButtonEnabledMode SelectHubIsEmpty = ButtonEnabledMode.SelectHubIsEmpty;

	/**
	 * Create a new OAButton that is bound to a Hub and command.
	 */
	public OAButton(Hub hub, String text, Icon icon, ButtonEnabledMode enabledMode, ButtonCommand command) {
		this(hub, text, icon, enabledMode, command, true);
		initialize();
	}

	public OAButton(Hub hub, String text, ButtonEnabledMode enabledMode, ButtonCommand command) {
		this(hub, text, null, enabledMode, command, true);
	}

	public OAButton(Hub hub, String text, Icon icon, ButtonEnabledMode enabledMode, ButtonCommand command, boolean bCallSetup) {
		if (text != null) {
			setText(text);
		}
		if (icon != null) {
			setIcon(icon);
		}

		if (command == null) {
			command = ButtonCommand.Other;
		}

		if (enabledMode == null) {
			enabledMode = getDefaultEnabledMode(hub, command);
		}

		if (command == ButtonCommand.GoTo) {
			control = new OAButtonController(hub, enabledMode, command, HubChangeListener.Type.AoNotNull, false, true) {
				@Override
				protected boolean isEnabled(boolean bIsCurrentlyEnabled) {
					if (!bIsCurrentlyEnabled) {
						bIsCurrentlyEnabled = (hub.getAO() != null);
					}
					return bIsCurrentlyEnabled;
				}
			};
		} else if (command == ButtonCommand.Select) {
			control = new OAButtonController(hub, enabledMode, command, HubChangeListener.Type.AoNotNull, true, false);
		} else if (command == ButtonCommand.HubSearch) {
			control = new OAButtonController(hub, enabledMode, command) {
				@Override
				protected boolean isEnabled(boolean bIsCurrentlyEnabled) {
					if (!bIsCurrentlyEnabled) {
						if (hub != null) {
							Hub hx = hub.getLinkHub(true);
							if (hx != null) {
								Object objx = hx.getAO();
								if (!(objx instanceof OAObject)) {
									return false;
								}
								bIsCurrentlyEnabled = (((OAObject) objx).isEnabled(hub.getLinkPath(true)));
							} else {
								bIsCurrentlyEnabled = (hub.getSize() > 0);
							}
						}
					}
					return bIsCurrentlyEnabled;
				}
			};
		} else if (command == ButtonCommand.Search) {
			control = new OAButtonController(hub, ButtonEnabledMode.HubIsValid, command, HubChangeListener.Type.HubValid, false, false) {
				@Override
				public Object getSearchObject() {
					return OAButton.this.getSearchObject();
				}
			};

			Hub hubx = null;
			String propx = null;
			if (hub != null) {
				hubx = hub.getLinkHub(true);
				if (hubx != null) {
					propx = hub.getLinkPath(true);
				} else {
					hubx = hub.getMasterHub();
					if (hubx != null) {
						propx = HubDetailDelegate.getPropertyFromMasterToDetail(hub);
					}
				}
			}
			if (hubx != null && OAString.isNotEmpty(propx)) {
				control.getEnabledChangeListener().addObjectCallbackEnabled(hubx, propx);
			}
		} else if (command == ButtonCommand.Save) {
			control = new OAButtonController(hub, OAButton.ButtonEnabledMode.HubIsValid, command, HubChangeListener.Type.HubValid, false,
					false);
			//was: control = new OAButtonController(hub, OAButton.ButtonEnabledMode.ActiveObjectNotNull, command, HubChangeListener.Type.AoNotNull, false, false);
			control.getEnabledChangeListener().add(hub, OAObjectDelegate.WORD_Changed, true);
		} else if (command == ButtonCommand.New || command == ButtonCommand.WizardNew) {
			control = new OAButtonController(hub, OAButton.ButtonEnabledMode.HubIsValid, command, HubChangeListener.Type.HubValid, true,
					true);
			control.getEnabledChangeListener().addNewEnabled(hub);
		} else if (command == ButtonCommand.Add || command == ButtonCommand.AddManual) {
			control = new OAButtonController(hub, OAButton.ButtonEnabledMode.HubIsValid, command, HubChangeListener.Type.HubValid, true,
					true);
			control.getEnabledChangeListener().addAddEnabled(hub);
		} else if (command == ButtonCommand.Delete) {
			control = new OAButtonController(hub, OAButton.ButtonEnabledMode.HubIsValid, command, HubChangeListener.Type.HubValid, true,
					true);
			//was: control = new OAButtonController(hub, OAButton.ButtonEnabledMode.ActiveObjectNotNull, command, HubChangeListener.Type.AoNotNull, true, true);
			//was: control.getEnabledChangeListener().addDeleteEnabled(hub, true);
		} else if (command == ButtonCommand.Remove) {
			control = new OAButtonController(hub, OAButton.ButtonEnabledMode.HubIsValid, command, HubChangeListener.Type.HubValid, true,
					true);
			//was: control = new OAButtonController(hub, OAButton.ButtonEnabledMode.ActiveObjectNotNull, command, HubChangeListener.Type.AoNotNull, true, true);
			//was: control.getEnabledChangeListener().addRemoveEnabled(hub);
		} else if (command == ButtonCommand.ClearAO) {
			control = new OAButtonController(hub, OAButton.ButtonEnabledMode.ActiveObjectNotNull, command, HubChangeListener.Type.AoNotNull,
					true, true);
		} else if (command == ButtonCommand.Copy) {
			control = new OAButtonController(hub, OAButton.ButtonEnabledMode.HubIsValid, command, HubChangeListener.Type.HubValid, false,
					false);
			//was: control = new OAButtonController(hub, OAButton.ButtonEnabledMode.ActiveObjectNotNull, command, HubChangeListener.Type.AoNotNull, false, false);
			//was: control.getEnabledChangeListener().addCopyEnabled(hub);
		} else if (command == ButtonCommand.Cut) {
			control = new OAButtonController(hub, OAButton.ButtonEnabledMode.HubIsValid, command, HubChangeListener.Type.HubValid, false,
					true);
		} else if (command == ButtonCommand.Paste) {
			control = new OAButtonController(hub, OAButton.ButtonEnabledMode.HubIsValid, command, HubChangeListener.Type.HubValid, false,
					true);
			control.getEnabledChangeListener().addPasteEnabled(hub);
		} else if (command == ButtonCommand.Refresh) {
			control = new OAButtonController(hub, OAButton.ButtonEnabledMode.HubIsValid, command, HubChangeListener.Type.HubValid, true,
					true);
		} else {
			control = new OAButtonController(hub, enabledMode, command);
		}
		if (bCallSetup) {
			setup();
		}
	}

	public static ButtonEnabledMode getDefaultEnabledMode(Hub hub, ButtonCommand command) {
		ButtonEnabledMode enabledMode = ButtonEnabledMode.HubIsValid;
		switch (command) {
		case GoTo:
		case Other:
			// 20190203 changed to use HubIsValid, so that hubSelect can be used, and not just AO
			//was: if (hub != null) enabledMode = ButtonEnabledMode.ActiveObjectNotNull;
			//was: else enabledMode = ButtonEnabledMode.UsesIsEnabled;
			break;
		case First:
		case Last:
		case WizardNew:
		case New:
		case Insert:
		case Refresh:
		case Add:
		case NewManual:
		case AddManual:
		case Paste:
			enabledMode = ButtonEnabledMode.HubIsValid;
			break;
		case HubSearch:
			enabledMode = ButtonEnabledMode.HubIsNotEmpty;
			break;
		case ObjectMethod:
			enabledMode = ButtonEnabledMode.ActiveObjectNotNull;
			break;
		case HubMethod:
			enabledMode = ButtonEnabledMode.HubIsValid;
			break;
		case StaticObjectMethod:
			enabledMode = ButtonEnabledMode.HubIsValid;
			break;
		default:
			enabledMode = ButtonEnabledMode.ActiveObjectNotNull;
			break;
		}
		return enabledMode;
	}

	public void bind(Hub hub, ButtonCommand buttonCommad) {
		setHub(hub);
		getController().setCommand(buttonCommad);
	}

	public OAButton() {
		this(null, null, null, null, null);
	}

	public OAButton(String text) {
		this(null, text, null, null, null);
	}

	public OAButton(String text, Icon icon) {
		this(null, text, icon, null, null);
	}

	public OAButton(Icon icon) {
		this(null, null, icon, null, null);
	}

	public OAButton(Icon icon, boolean bCallSetup) {
		this(null, null, icon, null, null, bCallSetup);
	}

	public OAButton(Hub hub) {
		this(hub, null, null, null, null);
	}

	public OAButton(Hub hub, ButtonCommand command) {
		this(hub, null, null, null, command);
	}

	public OAButton(Hub hub, ButtonEnabledMode enabledMode) {
		this(hub, null, null, enabledMode, null);
	}

	public OAButton(Hub hub, String text) {
		this(hub, text, null, null, null);
	}

	public OAButton(Hub hub, String text, ButtonCommand command) {
		this(hub, text, null, null, command);
	}

	public OAButton(Hub hub, String text, ButtonCommand command, Icon icon) {
		this(hub, text, icon, null, command);
	}

	public OAButton(Hub hub, ButtonCommand command, String text) {
		this(hub, text, null, null, command);
	}

	public OAButton(Hub hub, String text, ButtonEnabledMode enabledMode) {
		this(hub, text, null, enabledMode, null);
	}

	public OAButton(Hub hub, Icon icon) {
		this(hub, null, icon, null, null);
	}

	public OAButton(Hub hub, Icon icon, ButtonCommand command) {
		this(hub, null, icon, null, command);
	}

	public OAButton(Hub hub, Icon icon, ButtonEnabledMode enabledMode) {
		this(hub, null, icon, enabledMode, null);
	}

	public OAButton(Hub hub, String text, Icon icon) {
		this(hub, text, icon, null, null);
	}

	public OAButton(Hub hub, String text, Icon icon, ButtonCommand command) {
		this(hub, text, icon, null, command);
	}

	public OAButton(Hub hub, String text, Icon icon, ButtonEnabledMode enabledMode) {
		this(hub, text, icon, enabledMode, null);
	}

	@Override
	public void initialize() {
	}

	@Override
	public ButtonController getController() {
		return control;
	}

	/**
	 * Built in command.
	 */
	public ButtonCommand getCommand() {
		return control.getCommand();
	}

	public void setManual(boolean b) {
		control.setManual(b);
	}

	public boolean getManual() {
		return control.getManual();
	}

	public ButtonEnabledMode getEnabledMode() {
		return control.getEnabledMode();
	}

	/**
	 * Retrieve an Icon from the viaoa.gui.icons directory.
	 *
	 * @param name name of file in icons directory.
	 */
	public static Icon getIcon(String name) {
		URL url = OAButton.class.getResource("icons/" + name);
		if (url == null) {
			return null;
		}
		return new ImageIcon(url);
	}

	public void setDefaultIcon() {
		ButtonCommand cmd = getCommand();
		if (cmd == null) {
			setIcon(null);
		} else {
			setIcon(getDefaultIcon(cmd));
		}
	}

	/**
	 * Retrieve an Icon from the viaoa.gui.icons directory.
	 */
	public static Icon getDefaultIcon(ButtonCommand cmd) {
		if (cmd == null) {
			return null;
		}
		int x = cmd.ordinal();
		String s = cmd.name();
		s = Character.toLowerCase(s.charAt(0)) + s.substring(1);
		if (s.endsWith("Manual")) {
			s = s.substring(0, s.length() - 6);
		}
		URL url = OAButton.class.getResource("icons/" + s + ".gif");
		if (url == null) {
			return null;
		}
		return new ImageIcon(url);
	}

	/**
	 * Sets the default icon, and tooltip (if they are not already set) based on the value of command. Also calls setup(this). Note: does
	 * not set default Text.
	 *
	 * @see #setup(boolean,boolean,boolean,boolean)
	 */
	public void setup() {
		boolean bIcon = (getIcon() == null);

		// use setup(b,b,b,b) if it needs to be set, or setDefaultText
		boolean bText = false;// (getText() == null || getText().length() == 0);
		boolean bTtt;
		if (control != null) {
			bTtt = (control.getCommand() != ButtonCommand.Other)
					&& (super.getToolTipText() == null || super.getToolTipText().length() == 0);
		} else {
			bTtt = false;
		}

		setup(true, bIcon, bText, bTtt);
	}

	public static String getDefaultText(ButtonCommand cmd) {
		if (cmd == null) {
			return "";
		}
		String s = cmd.name();
		if (s.indexOf("Manual") > 0) {
			s = s.substring(0, s.length() - 6);
		}
		return s;
	}

	public void setDefaultText() {
		ButtonCommand cmd = getCommand();
		setText(getDefaultText(cmd));
	}

	/**
	 * Sets the default icon, text, and tooltip based on the value of command.
	 *
	 * @param bSetup   if true, calls setup(this) to set border, mouseover.
	 * @param bIcon    if true, calls getIcon(command) to set icon
	 * @param bText    if true, set to command name
	 * @param bToolTip if true, set to command name plus name of object in Hub
	 */
	public void setup(boolean bSetup, boolean bIcon, boolean bText, boolean bToolTip) {
		if (bSetup) {
			this.setup(this);
		}
		ButtonCommand cmd = getCommand();
		if (cmd == null) {
			if (bIcon) {
				setIcon(null);
			}
			if (bText) {
				setText(null);
			}
			if (bToolTip) {
				setToolTipText(null);
			}
			return;
		}
		if (bIcon) {
			setIcon(getDefaultIcon(cmd));
		}

		if (bText) {
			setDefaultText();
		}
		if (bToolTip) {
			String s = "";
			if (getHub() != null) {
				s = getHub().getObjectClass().getSimpleName();
				s = com.viaoa.util.OAString.convertHungarian(s);
			}

			if (cmd == ButtonCommand.ObjectMethod || cmd == ButtonCommand.HubMethod) {
				String s2 = control.getMethodName();
				if (OAString.isEmpty(s2)) {
					s = null;
				} else {
					s += " " + s2;
				}
			} else {
				String s2 = cmd.name();
				s2 = OAString.convert(s2, "Manual", "");
				s += " ";
				s = cmd.name() + " " + s;
			}
			setToolTipText(s);
		}
	}

	/**
	 * Description to use for Undo and Redo presentation names.
	 */
	public void setUndoDescription(String s) {
		control.setUndoDescription(s);
	}

	/**
	 * Description to use for Undo and Redo presentation names.
	 */
	public String getUndoDescription() {
		return control.getUndoDescription();
	}

	/**
	 * Flag to enable undo, default is true.
	 */
	public void setEnableUndo(boolean b) {
		control.setEnableUndo(b);
	}

	/**
	 * Flag to enable undo, default is true.
	 */
	public boolean getEnableUndo() {
		return control.getEnableUndo();
	}

	public void setText(String s) {
		super.setText((s == null) ? "" : s);
	}

	public Hub getHub() {
		if (control == null) {
			return null;
		}
		return control.getHub();
	}

	public void setHub(Hub h) {
		control.setHub(h);
	}

	public void setMultiSelectHub(Hub hubMultiSelect) {
		setSelectHub(hubMultiSelect);
	}

	public void setSelectHub(Hub hubMultiSelect) {
		if (control == null) {
			return;
		}
		control.setSelectHub(hubMultiSelect);
	}

	public Hub getSelectHub() {
		if (control == null) {
			return null;
		}
		return control.getSelectHub();
	}

	public Hub getMultiSelectHub() {
		if (control == null) {
			return null;
		}
		return control.getSelectHub();
	}

	/**
	 * Method in object to execute on active object in hub.
	 */
	public void setMethodName(String methodName) {
		control.setMethodName(methodName);
	}

	/**
	 * Method in object to execute on active object in hub.
	 */
	public String getMethodName() {
		return control.getMethodName();
	}

	public void setOpenFileChooser(JFileChooser fc) {
		control.setOpenFileChooser(fc);
	}

	public JFileChooser getOpenFileChooser() {
		return control.getOpenFileChooser();
	}

	public void setSaveFileChooser(JFileChooser fc) {
		control.setSaveFileChooser(fc);
	}

	public JFileChooser getSaveFileChooser() {
		return control.getSaveFileChooser();
	}

	public void setConsoleProperty(String prop) {
		control.setConsoleProperty(prop);
	}

	public void setClearConsole(boolean b) {
		control.setClearConsole(b);
	}

	public void getClearConsole() {
		control.getClearConsole();
	}

	public String getConsoleProperty() {
		return control.getConsoleProperty();
	}

	/**
	 * if the hub for this command has a masterHub, then it can control this button if this is set to true. Default = true
	 */
	public void setMasterControl(boolean b) {
		control.setMasterControl(b);
	}

	public boolean getMasterControl() {
		return control.getMasterControl();
	}

	/**
	 * Returns the component that will receive focus when this button is clicked.
	 */
	public JComponent getFocusComponent() {
		return control.getFocusComponent();
	}

	/**
	 * Set the component that will receive focus when this button is clicked.
	 */
	public void setFocusComponent(JComponent focusComponent) {
		control.setFocusComponent(focusComponent);
		if (focusComponent != null) {
			setFocusPainted(false);
		}
	}

	/**
	 * Same as setupButton.
	 */
	public static void setup(AbstractButton cmd) {
		setupButton(cmd);
	}

	public static void setup(OASplitButton cmd) {
		setupButton(cmd);
	}

	/**
	 * Called automatically for all new OAButtons if AutoSetup is true (default).<br>
	 * Does the following:
	 * <ul>
	 * <li>setFocusPainted(false)
	 * <li>setBorderPainted(false)
	 * <li>setMargin(new Insets(1,1,1,1))
	 * <li>public void mouseEntered(MouseEvent e) { setBorderPainted(true) }
	 * <li>public void mouseExited(MouseEvent e) { setBorderPainted(false) }
	 * </ul>
	 */
	public static void setupButton(AbstractButton cmd) {
		_setupButton(cmd);
	}

	public static void setupButton(final OASplitButton sb) {
		final AbstractButton cmd1 = sb.getMainButton();
		final AbstractButton cmd2 = sb.getDropDownButton();

		cmd1.setBorderPainted(false);
		cmd1.setContentAreaFilled(false);
		// cmd1.setMargin(new Insets(1,1,1,1));

		cmd2.setBorderPainted(false);
		cmd2.setContentAreaFilled(false);
		// cmd2.setMargin(new Insets(1,1,1,1));

		JPopupMenu popup = null;
		if (sb instanceof OAMultiButtonSplitButton) {
			OAMultiButtonSplitButton msb = (OAMultiButtonSplitButton) sb;
			popup = msb.getPopupMenu();
		}
		if (popup != null) {
			popup.addPopupMenuListener(new PopupMenuListener() {
				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

				}

				@Override
				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
					cmd1.setContentAreaFilled(false);
					cmd2.setContentAreaFilled(false);
					cmd1.setBorderPainted(false);
					cmd2.setBorderPainted(false);
				}

				@Override
				public void popupMenuCanceled(PopupMenuEvent e) {
				}
			});
		}

		final JPopupMenu pm = popup;

		MouseAdapter ma = new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				if (sb.isEnabled()) {
					cmd1.setBorderPainted(true);
					if (cmd1.isEnabled()) {
						if (e.getComponent() == cmd1) {
							cmd1.setContentAreaFilled(true);
						}
					}
					cmd2.setBorderPainted(true);
					if (e.getComponent() == cmd2) {
						cmd2.setContentAreaFilled(true);
					}
				}
			}

			public void mouseExited(MouseEvent e) {
				cmd1.setContentAreaFilled(false);
				if (pm == null || !pm.isVisible()) {
					cmd2.setContentAreaFilled(false);
					cmd1.setBorderPainted(false);
					cmd2.setBorderPainted(false);
				}
			}
		};
		cmd1.addMouseListener(ma);
		cmd2.addMouseListener(ma);
	}

	private static void _setupButton(final AbstractButton cmd) {
		cmd.setFocusPainted(false);
		// 2004/11/10 removed. If focus is not called, then previous focus component will not get focus lost event
		// cmd.setRequestFocusEnabled(false); // 2003/6/3
		cmd.setBorderPainted(false);
		cmd.setContentAreaFilled(false);
		cmd.setMargin(new Insets(1, 1, 1, 1));

		cmd.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				if (cmd.isEnabled()) {
					cmd.setContentAreaFilled(true);
					cmd.setBorderPainted(true);
				}
			}

			public void mouseExited(MouseEvent e) {
				boolean b = false;
				if (cmd instanceof JToggleButton) {
					if (((JToggleButton) cmd).isSelected()) {
						b = true;
					}
				}
				cmd.setBorderPainted(b);
				cmd.setContentAreaFilled(b);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseExited(null);
			}
		});

		if (cmd instanceof JToggleButton) {
			cmd.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (cmd.isSelected()) {
						if (cmd.isEnabled()) {
							cmd.setContentAreaFilled(true);
							cmd.setBorderPainted(true);
						}
					} else {
						cmd.setBorderPainted(false);
						cmd.setContentAreaFilled(false);
					}
				}
			});
		}

	}

	/**
	 * Popup message used to confirm button click before running code.
	 */
	public void setConfirmMessage(String msg) {
		control.setConfirmMessage(msg);
	}

	/**
	 * Popup message used to confirm button click before running code.
	 */
	public String getConfirmMessage() {
		return control.getSuperConfirmMessage();
	}

	public void setConfirmComponent(JComponent comp) {
		control.setConfirmComponent(comp);
	}

	public JComponent getConfirmComponent() {
		return control.getConfirmComponent();
	}

	/**
	 * Popup message when command is completed
	 */
	public void setCompletedMessage(String msg) {
		control.setCompletedMessage(msg);
	}

	public String getCompletedMessage() {
		return control.default_getCompletedMessage();
	}

	public void setReturnMessage(String msg) {
		control.setReturnMessage(msg);
	}

	public String getReturnMessage() {
		return control.getReturnMessage();
	}

	/**
	 * Object to update whenever button is clicked.
	 */
	public void setUpdateObject(OAObject object, String property, Object newValue) {
		control.setUpdateObject(object, property, newValue);
	}

	/**
	 * Update active object whenever button is clicked. If there is a multiSelectHub, then each object in it will also be updated.
	 */
	public void setUpdateObject(String property, Object newValue) {
		control.setUpdateObject(property, newValue);
	}

	/**
	 * @param keyStroke KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK, false)
	 */
	public void registerKeyStroke(KeyStroke keyStroke) {
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "oabutton");
		getActionMap().put("oabutton", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (control != null) {
					control.actionPerformed(e);
				}
			}
		});
	}

	public void registerKeyStroke(KeyStroke keyStroke, JComponent focusComponent) {
		if (focusComponent == null) {
			return;
		}
		focusComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, "oabutton");
		getActionMap().put("oabutton", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (control != null) {
					control.actionPerformed(e);
				}
			}
		});
	}

	// ----- OATableComponent Interface methods -----------------------
	private OATable table;
	private String heading;

	public void setTable(OATable table) {
		this.table = table;
	}

	public OATable getTable() {
		return table;
	}

	public void setColumns(int x) {
		getController().setColumns(x);
	}

	@Override
	public int getColumns() {
		return getController().getColumns();
	}

	public String getPropertyPath() {
		return null;
	}

	public void setPropertyPath(String path) {
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

	public Dimension getMinimumSize() {
		Dimension d = super.getPreferredSize();
		return d;
	}

	/** called by getTableCellRendererComponent */
	@Override
	public Component getTableRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return this;
	}

	@Override
	public void customizeTableRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column,
			boolean wasChanged, boolean wasMouseOver) {
		Object obj = ((OATable) table).getObjectAt(row, column);
		customizeRenderer(lbl, obj, value, isSelected, hasFocus, row, wasChanged, wasMouseOver);
	}

	@Override
	public String getTableToolTipText(JTable table, int row, int col, String defaultValue) {
		Object obj = ((OATable) table).getObjectAt(row, col);
		getToolTipText(obj, row, defaultValue);
		return defaultValue;
	}

	private OAButtonTableCellEditor tableCellEditor;

	public TableCellEditor getTableCellEditor() {
		if (tableCellEditor == null) {
			tableCellEditor = new OAButtonTableCellEditor(this);
		}
		return tableCellEditor;
	}

	@Override
	public String getFormat() {
		return null;
	}

	protected boolean isEnabled(boolean bIsCurrentlyEnabled) {
		/*20190202 removed, it needs to verify first
		if (!bIsCurrentlyEnabled) {
		    if (getSelectHub() != null && getSelectHub().size() > 0) {
		        if (getHub() == null || getHub().getAO() == null) bIsCurrentlyEnabled = true;
		    }
		}
		*/
		return bIsCurrentlyEnabled;
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

	public void addEnabledOnlyIfNew() {
		control.getEnabledChangeListener().add(control.getHub(), Type.AoNew);
	}

	/*
	protected boolean isEnabled(boolean defaultValue) {
	    return defaultValue;
	}
	*/
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
	protected String isValid(Object object, Object value) {
		return null;
	}

	public void setUseSwingWorker(boolean b) {
		if (control == null) {
			return;
		}
		control.setUseSwingWorker(b);
	}

	public boolean getUseSwingWorker() {
		if (control == null) {
			return false;
		}
		return control.getUseSwingWorker();
	}

	public void setProcessingText(String title, String msg) {
		if (control == null) {
			return;
		}
		control.setProcessingText(title, msg);
	}

	// this can be overwritten to customize an object copy.
	protected OAObject createCopy(OAObject obj) {
		return control._createCopy(obj);
	}

	// ActionPerformed methods
	public boolean beforeActionPerformed() {
		if (control == null) {
			return false;
		}
		return control.default_beforeActionPerformed();
	}

	public boolean confirmActionPerformed() {
		return control.default_confirmActionPerformed();
	}

	/** This is where the "real" work is done when actionPerformed is called. */
	protected boolean onActionPerformed() throws Exception {
		if (control == null) {
			return false;
		}
		return control.default_onActionPerformed();
	}

	protected boolean onActionGetInput() {
		return true;
	}

	public void afterActionPerformed() {
		control.default_afterActionPerformed();
	}

	public void afterActionPerformedFailure(String msg, Exception e) {
		control.default_afterActionPerformedFailure(msg, e);
	}

	class OAButtonController extends ButtonController {
		public OAButtonController(Hub hub, OAButton.ButtonEnabledMode enabledMode, OAButton.ButtonCommand command,
				HubChangeListener.Type type, boolean bDirectlySetsAO, boolean bIncludeExtendedChecks) {
			super(hub, OAButton.this, enabledMode, command,
					type,
					bDirectlySetsAO,
					bIncludeExtendedChecks);
		}

		public OAButtonController(Hub hub, ButtonEnabledMode enabledMode, ButtonCommand command) {
			super(hub, OAButton.this, enabledMode, command);
		}

		@Override
		public String isValid(Object object, Object value) {
			String msg = OAButton.this.isValid(object, value);
			if (msg == null) {
				msg = super.isValid(object, value);
			}
			return msg;
		}

		@Override
		protected boolean isEnabled(boolean bIsCurrentlyEnabled) {
			bIsCurrentlyEnabled = super.isEnabled(bIsCurrentlyEnabled);
			return OAButton.this.isEnabled(bIsCurrentlyEnabled);
		}

		@Override
		protected boolean isVisible(boolean bIsCurrentlyVisible) {
			return OAButton.this.isVisible(bIsCurrentlyVisible);
		}

		// ActionPerformed
		@Override
		public boolean beforeActionPerformed() {
			return OAButton.this.beforeActionPerformed();
		}

		@Override
		public String getConfirmMessage() {
			return OAButton.this.getConfirmMessage();
		}

		public String getSuperConfirmMessage() {
			return super.getConfirmMessage();
		}

		@Override
		public boolean confirmActionPerformed() {
			return OAButton.this.confirmActionPerformed();
		}

		@Override
		protected boolean onActionPerformed() throws Exception {
			return OAButton.this.onActionPerformed();
		}

		@Override
		protected boolean onActionGetInput() {
			return OAButton.this.onActionGetInput();
		}

		@Override
		public void afterActionPerformed() {
			OAButton.this.afterActionPerformed();
		}

		@Override
		public void afterActionPerformedFailure(String msg, Exception e) {
			OAButton.this.afterActionPerformedFailure(msg, e);
		}

		@Override
		public String getCompletedMessage() {
			return OAButton.this.getCompletedMessage();
		}

		@Override
		protected OAObject createCopy(OAObject obj) {
			obj = OAButton.this.createCopy(obj);
			return obj;
		}

		protected OAObject _createCopy(OAObject obj) {
			return super.createCopy(obj);
		}

		@Override
		public void setSelectHub(Hub newHub) {
			super.setSelectHub(newHub);

			if (newHub != null) {
				// listen to all of the selectHub to see if it's enabled
				String s = getMethodName();
				if (OAString.isNotEmpty(s)) {
					getChangeListener().addObjectCallbackEnabled(newHub, s, true);
				}
			}

			/* 20190203 remove, since this is alredy taken care of at setup
			if (command == ButtonCommand.Copy) {
			    enabledMode = OAButton.ButtonEnabledMode.HubIsValid;
			    getEnabledChangeListener().clear();
			    getEnabledChangeListener().addHubNotEmpty(newHub);
			}
			*/
		}
	}

	/**
	 * Component to display in the processing window
	 */
	public void setDisplayComponent(JComponent comp) {
		control.setDisplayComponent(comp);
	}

	public JComponent getDisplayComponent() {
		return control.getDisplayComponent();
	}

	public void setPasswordDialog(OAPasswordDialog dlg) {
		control.setPasswordDialog(dlg);
	}

	public OAPasswordDialog getPasswordDialog() {
		return control.getPasswordDialog();
	}

	/**
	 * Used to password protect the command. Note: this can be overwritten, to be called when the user input pw needs to be verified. To do
	 * this, then call setPasswordProtected(true)
	 *
	 * @param pw SHAHash encryted password
	 * @see OAString#getSHAHash(String)
	 * @see #setPasswordProtected
	 */
	public void setSHAHashPassword(String pw) {
		control.setSHAHashPassword(pw);
	}

	public String getSHAHashPassword() {
		return control.getSHAHashPassword();
	}

	public void setPasswordProtected(boolean b) {
		control.setPasswordProtected(b);
	}

	public boolean getPasswordProtected() {
		return control.getPasswordProtected();
	}
	/*
	public String getEndPropertyName() {
	    return control.getEndPropertyName();
	}
	*/

	public Object getSearchObject() {
		return null;
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		String tt = super.getToolTipText(event);
		if (control != null && !this.isEnabled()) {
			String s = control.getEnabledChangeListener().getFailureReason();
			if (OAString.isNotEmpty(s)) {
				if (tt != null && tt.toLowerCase().indexOf("<html>") < 0) {
					tt = "<html>" + tt;
				}
				tt = OAString.concat(tt, s, "<br>");
			}
		}
		return tt;
	}
}

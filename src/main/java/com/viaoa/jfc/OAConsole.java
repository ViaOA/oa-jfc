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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.WeakHashMap;
import java.awt.datatransfer.StringSelection;

import javax.swing.*;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.viaoa.hub.Hub;
import com.viaoa.hub.HubChangeListener;
import com.viaoa.hub.HubDetailDelegate;
import com.viaoa.hub.HubEvent;
import com.viaoa.hub.HubListener;
import com.viaoa.hub.HubListenerAdapter;
import com.viaoa.hub.HubMerger;
import com.viaoa.jfc.console.Console;
import com.viaoa.jfc.control.OAJfcController;
import com.viaoa.jfc.dnd.OATransferable;
import com.viaoa.object.OAObject;
import com.viaoa.util.OAString;

/**
 * Acts as a console to display and scroll changes to a property. Ex: Message.text, where each change to text will be added to the display.
 *
 * @author vvia
 */
public class OAConsole extends OATable implements FocusListener, MouseListener {
	private final Hub hubListen;
	private String property;
	private String listenProperty;
	private final WeakHashMap<OAObject, Hub<Console>> hmConsole = new WeakHashMap<OAObject, Hub<Console>>();
	private int columns;
	private HubListener hubListener, hubListener2;
	private Hub hubForMerger;
	private int maxRows = 500;

	public OAConsole(Hub hub, String property, int columns) {
		super(new Hub<Console>(Console.class));
		this.hubListen = hub;
		this.property = property;
		this.columns = columns;

		setSelectHub(new Hub(Console.class));

		setup();
		setupMenu();
	}

	public void setLabel(JLabel lbl) {
		if (lbl == null) {
			return;
		}
		OAJfcController jc = new OAJfcController(hubListen, new JLabel(), property, null, HubChangeListener.Type.HubValid, false, false);
		jc.setLabel(lbl);
	}

	@Override
	public void setSelectHub(Hub hub) {
		super.setSelectHub(hub);
	}

	public void setMaxRows(int x) {
		this.maxRows = x;
	}

	public int getMaxRows() {
		return maxRows;
	}

	public void close() {
	    if (hubListener != null) {
	        if (hubForMerger != null) hubForMerger.removeHubListener(hubListener);
	        else if (hubListen != null) hubListen.removeHubListener(hubListener);
	    }
	    
		if (hmConsole != null) {
			hmConsole.clear();
		}
		
		if (hubListen != null && hubListener2 != null) {
		    hubListen.removeHubListener(hubListener2);
		}
		
		hubListener = hubListener2 = null;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	public void setup() {
		OALabel lbl;
		// addColumn("xxx", 10, new OALabel(getHub(), Console.P_DateTime, 10));
		addColumn("xxx", columns, new OALabel(getHub(), Console.P_Text, columns));
		setPreferredSize(5, 1);

		setTableHeader(null);
		setShowHorizontalLines(false);
		setAllowDnD(false);
		setAllowSorting(false);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		hubListener = new HubListenerAdapter() {
			@Override
			public void afterPropertyChange(HubEvent e) {
				if (listenProperty == null) {
					return;
				}

				String prop = e.getPropertyName();
				if (prop == null) {
					return;
				}
				if (!listenProperty.equalsIgnoreCase(prop)) {
					return;
				}

                if (getHub() == null) {
                    return;
                }
				
				Object obj = e.getObject();
				if (obj == null) {
					return;
				}

				if (!(obj instanceof OAObject)) {
					return;
				}
				OAObject oaObj = (OAObject) obj;

                if (hubForMerger == null) {
                    if (e.getObject() != hubListen.getAO()) return;
                }
				
				OAConsole.this.afterPropertyChange(oaObj, (String) e.getNewValue());
			}

			@Override
			public void afterRemove(HubEvent e) {
				Object obj = e.getObject();
				if (obj == null) {
					return;
				}
				if (!(obj instanceof OAObject)) {
					return;
				}
				OAObject oaObj = (OAObject) obj;
				
				hmConsole.remove(oaObj);
			}
		};

		hubListener2 = new HubListenerAdapter() {
			@Override
			public void afterChangeActiveObject(HubEvent e) {
				Object obj = e.getObject();
				if (!(obj instanceof OAObject)) {
					OAConsole.this.getHub().setSharedHub(null);
					return;
				}
				OAObject oaObj = (OAObject) obj;
				OAConsole.this.makeActive(oaObj);
			}

			@Override
			public void beforeRemoveAll(HubEvent e) {
				hmConsole.clear();
			}
		};

		listenProperty = property;
		if (property != null) {
			String prop = property;
			Hub h = hubListen;

			if (prop.indexOf('.') > 0) {
				hubForMerger = new Hub();
				int dcnt = OAString.dcount(prop, '.');
				String s = OAString.field(prop, ".", 1, dcnt - 1);
				HubMerger hm = new HubMerger(h, hubForMerger, s, true);
				
				listenProperty = OAString.field(prop, ".", dcnt);
				hubForMerger.addHubListener(hubListener, listenProperty, true);
			} else {
				hubListen.addHubListener(hubListener, property, true);
			}
            hubListen.addHubListener(hubListener2);
            OAObject oaObj = (OAObject) hubListen.getAO();
            if (oaObj != null) {
                makeActive(oaObj);
            }
		}

		addFocusListener(this);
		addMouseListener(this);
	}

	protected void setupMenu() {
        final JPopupMenu pmenu = new JPopupMenu();
        JMenuItem mi;
        ActionListener al;
        
        mi = new JMenuItem("Copy to clipboard");
        pmenu.add(mi);
        al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();
				for (Console c : (Hub<Console>) getHub()) {
					sb.append(c.getDateTime().toString("HH:mm:ss.SSS")+" " + c.getText() + "\n");
				}
				
		        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		        StringSelection selection = new StringSelection(sb.toString());				
		        cb.setContents(selection, null);
			}
		};
		mi.addActionListener(al);

		pmenu.addSeparator();
		
        mi = new JMenuItem("Clear console");
        pmenu.add(mi);
        al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getHub().removeAll();
			}
		};
		mi.addActionListener(al);
		
		

        pmenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Point pt = e.getPoint();
                    pmenu.show(OAConsole.this, pt.x, pt.y);
                }
                super.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Point pt = e.getPoint();
                    pmenu.show(OAConsole.this, pt.x, pt.y);
                }
                super.mouseReleased(e);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
	}
	
	protected void makeActive(OAObject oaObj) {
		Hub<Console> h = hmConsole.get(oaObj);
		if (h == null) {
			h = new Hub<Console>(Console.class);
			hmConsole.put(oaObj, h);
		}
		getHub().setSharedHub(h);
	}

	protected void afterPropertyChange(OAObject oaObj, String val) {
		if (SwingUtilities.isEventDispatchThread()) {
			_afterPropertyChange(oaObj, val);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					_afterPropertyChange(oaObj, val);
				}
			});
		}
	}

	protected void _afterPropertyChange(OAObject oaObj, String val) {
	    Hub<Console> hubx;
	    if (hubForMerger != null) { 
	        OAObject objx = (OAObject) hubListen.getAO();
    		hubx = hmConsole.get(objx);
    		if (hubx == null) {
    			hubx = new Hub<Console>(Console.class);
    			hmConsole.put(objx, hubx);
    		}
	    }
	    else {
            hubx = hmConsole.get(oaObj);
            if (hubx == null) {
                hubx = new Hub<Console>(Console.class);
                hmConsole.put(oaObj, hubx);
            }
	    }

		if (val == null) {
			hubx.clear();
			return;
		}

		Console console = new Console();
		console.setText(val);
		if (hubx.getSize() > maxRows) {
			hubx.remove(0);
		}
		hubx.add(console);

		if (!OAConsole.this.bHasFocus && !OAConsole.this.bHasMouse) {
			boolean b;
			if (hubForMerger != null) {
				b = hubForMerger.contains(oaObj);
			} else {
				b = (OAConsole.this.hubListen.getAO() == oaObj);
			}
			if (b) {
				int pos = OAConsole.this.getHub().getSize();
				Rectangle rect = OAConsole.this.getCellRect(pos, 0, true);
				try {
					OAConsole.this.scrollRectToVisible(rect);
				} catch (Exception ex) {
				}
				OAConsole.this.repaint();
			}
		}
	}

	private volatile boolean bHasFocus;

	@Override
	public void focusGained(FocusEvent e) {
		bHasFocus = true;
	}

	@Override
	public void focusLost(FocusEvent e) {
		bHasFocus = false;
	}

	private volatile boolean bHasMouse;

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		bHasMouse = true;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		bHasMouse = false;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		return d;
	}

	protected boolean isVisible(boolean bIsCurrentlyVisible) {
		return bIsCurrentlyVisible;
	}

	protected boolean isEnabled(boolean bIsCurrentlyEnabled) {
		return bIsCurrentlyEnabled;
	}

}

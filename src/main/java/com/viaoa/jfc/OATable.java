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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.viaoa.hub.Hub;
import com.viaoa.hub.HubAODelegate;
import com.viaoa.hub.HubChangeListener;
import com.viaoa.hub.HubDataDelegate;
import com.viaoa.hub.HubDelegate;
import com.viaoa.hub.HubDetailDelegate;
import com.viaoa.hub.HubEvent;
import com.viaoa.hub.HubEventDelegate;
import com.viaoa.hub.HubFilter;
import com.viaoa.hub.HubLinkDelegate;
import com.viaoa.hub.HubListenerAdapter;
import com.viaoa.jfc.border.CustomLineBorder;
import com.viaoa.jfc.control.OAJfcController;
import com.viaoa.jfc.control.OATreeTableController;
import com.viaoa.jfc.dnd.OATransferable;
import com.viaoa.jfc.table.OATableCellEditor;
import com.viaoa.jfc.table.OATableCellRenderer;
import com.viaoa.jfc.table.OATableColumn;
import com.viaoa.jfc.table.OATableColumnCustomizer;
import com.viaoa.jfc.table.OATableComponent;
import com.viaoa.jfc.table.OATableFilterComponent;
import com.viaoa.jfc.table.OATableListener;
import com.viaoa.object.OAObject;
import com.viaoa.object.OASiblingHelper;
import com.viaoa.object.OAThreadLocalDelegate;
import com.viaoa.template.OATemplate;
import com.viaoa.undo.OAUndoManager;
import com.viaoa.undo.OAUndoableEdit;
import com.viaoa.util.OACompare;
import com.viaoa.util.OAConv;
import com.viaoa.util.OANullObject;
import com.viaoa.util.OAProperties;
import com.viaoa.util.OAReflect;
import com.viaoa.util.OAString;

/**
 * Used for building a Table of columns/rows listing Objects. All columns are created by adding an OATableComponent as a column to the
 * Table. Current components that support the OATableComponent interface,that can be used/added as table columns include: OACheckBox,
 * OAComboBox, OALabel, OAPasswordField, OARadioButton, OATextField.
 * <p>
 * Full support for Drag and Drop (DND), and options to control how it works.
 * <p>
 * OATable supports a multi selection list, by supplying a second Hub that is used to contain the selected objects.
 * <p>
 * OATable allows for creating custom renderers. Each component can have its own renderer, and OATable has its own own renderer that is
 * called for each cell.<br>
 * Also see OATable.getRenderer(...) to be able to customize any cell.
 * <p>
 * &nbsp;&nbsp;&nbsp;<img src="doc-files/table.gif" alt="">
 * <p>
 * Example:<br>
 * <p>
 * Create an OATable that will display a list (Hub) of Employees
 *
 * <pre>
 * Hub hubEmployee = new Hub(Employee.class);
 * Hub hubDepartment = new Hub(Department.class);
 * hubDepartment.setLink(hubEmployee);
 *
 * OATable table = new OATable();
 * OALabel lbl = new OALabel(hubEmployee, &quot;Id&quot;);
 * table.addColumn(&quot;Id&quot;, 14, lbl);
 * OATextField txt = new OATextField(hubEmployee, &quot;firstName&quot;);
 * table.addColumn(&quot;First Name&quot;, 14, txt);
 * OAComboBox cbo = new OAComboBox(hubDepartment, &quot;name&quot;);
 * table.addColumn(&quot;Department&quot;, 22, cbo);
 * OACheckBox chk = new OACheckBox(hubEmployee, &quot;fullTime&quot;);
 * table.addColumn(&quot;FT&quot;, 6, chk);
 * table.setPreferredSize(6, 3, true); // 6 rows, 3 columns, plus width of scrollbar
 * table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
 * panel.add(new JScrollPane(table));
 *
 * table.getTableHeader().setReorderingAllowed(false);
 *
 * Note: use this to get the original column position
 *  col = columnModel.getColumn(col).getModelIndex();
 * </pre>
 * <p>
 * For more information about this package, see <a href="package-summary.html#package_description">documentation</a>.
 *
 * @see OATableComponent
 */
public class OATable extends JTable implements DragGestureListener, DropTargetListener {
	private static Logger LOG = Logger.getLogger(OATable.class.getName());

	protected int prefCols = 1, prefRows = 5, prefMaxRows;
	protected Hub hub;
	protected Hub hubFilterMaster;
	protected HubFilter hubFilter;
	protected Hub hubSelect;
	protected TableController control;
	protected OATableModel oaTableModel;
	protected Vector<OATableColumn> columns = new Vector<OATableColumn>(5, 5);
	protected boolean includeScrollBar; // used by setPreferredSize
	protected boolean bAllowDrag = false;
	protected boolean bAllowDrop = false;
	protected boolean bRemoveDragObject;
	protected DropTarget dropTarget;
	protected DragSource dragSource = DragSource.getDefaultDragSource();
	final static DragSourceListener dragSourceListener = new MyDragSourceListener();
	protected PanelHeaderRenderer headerRenderer; // 2006/10/13
	protected JPopupMenu popupMenu; // used for alignment options
	protected boolean bAllowSorting;
	protected static Icon[] iconDesc;
	protected static Icon[] iconAsc;
	private boolean bEnableEditors = true;
	public boolean bDEBUG;
	private OASiblingHelper siblingHelper; // 20180704

	public static final Color COLOR_Odd = UIManager.getColor("Table.background");
	public static final Color COLOR_Even = new Color(249, 255, 255);
	public static final Color COLOR_Focus = UIManager.getColor("Table.foreground");

	public static final Color COLOR_MouseOver = OAJfcUtil.colorBackgroundDarkest;
	// public static final Color COLOR_MouseOver = new Color(0, 0, 110);

	public static final Color COLOR_Change_Foreground = Color.yellow;
	public static final Color COLOR_Change_Background = new Color(0, 0, 105);
	public static final Border BORDER_Change = new LineBorder(COLOR_Change_Background, 1);

	public static final Color COLOR_Focus_Forground = UIManager.getColor("Table.background");
	public static final Border BORDER_Focus = new LineBorder(Color.white, 1);

	public static final Border BORDER_Red = new CompoundBorder(new LineBorder(Color.white, 1), new LineBorder(Color.red));
	public static final Border BORDER_Purple = new CompoundBorder(new LineBorder(Color.white, 1), new LineBorder(new Color(136, 0, 136))); // #880088
	public static final Border BORDER_Yellow = new CompoundBorder(new LineBorder(Color.white, 1), new LineBorder(Color.yellow));
	public static final Border BORDER_Select = new CompoundBorder(new LineBorder(Color.white, 1),
			UIManager.getBorder("Table.focusCellHighlightBorder"));

	static {
		iconAsc = new Icon[4];
		iconDesc = new Icon[4];

		URL url = OAButton.class.getResource("icons/sortAsc.gif");
		if (url != null) {
			iconAsc[0] = new ImageIcon(url);
		}
		url = OAButton.class.getResource("icons/sortDesc.gif");
		if (url != null) {
			iconDesc[0] = new ImageIcon(url);
		}

		url = OAButton.class.getResource("icons/sortAsc1.gif");
		if (url != null) {
			iconAsc[1] = new ImageIcon(url);
		}
		url = OAButton.class.getResource("icons/sortDesc1.gif");
		if (url != null) {
			iconDesc[1] = new ImageIcon(url);
		}

		url = OAButton.class.getResource("icons/sortAsc2.gif");
		if (url != null) {
			iconAsc[2] = new ImageIcon(url);
		}
		url = OAButton.class.getResource("icons/sortDesc2.gif");
		if (url != null) {
			iconDesc[2] = new ImageIcon(url);
		}

		url = OAButton.class.getResource("icons/sortAsc3.gif");
		if (url != null) {
			iconAsc[3] = new ImageIcon(url);
		}
		url = OAButton.class.getResource("icons/sortDesc3.gif");
		if (url != null) {
			iconDesc[3] = new ImageIcon(url);
		}
	}

	/** Create a new Table. */
	public OATable() {
		this(true);
	}

	/** Create a new Table that is bound to a Hub. */
	public OATable(Hub hub) {
		this(hub, true);
	}

	/**
	 * Create a new Table that is bound to a Hub.
	 *
	 * @param bAddHack if true (default) then register [Enter] key and consume
	 */
	public OATable(Hub hub, boolean bAddHack) {
		this(bAddHack);
		setHub(hub);
	}

	protected OATable(JComponent compDummy) {
	}

	/**
	 * @param bAddHack if true (default) then register [Enter] key and consume
	 */
	protected OATable(boolean bAddHack) {
		JTableHeader head = getTableHeader();
		head.setUpdateTableInRealTime(false);

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // AUTO_RESIZE_LAST_COLUMN
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		setDoubleBuffered(true);
		setPreferredScrollableViewportSize(new Dimension(150, 100));
		setAutoCreateColumnsFromModel(false);
		setColumnSelectionAllowed(false);
		if (bAddHack) {
			addHack();
		}
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		Boolean b = new Boolean(true);
		Class c = b.getClass();
		setSurrendersFocusOnKeystroke(true);

		setIntercellSpacing(new Dimension(6, 2));
		int x = getRowHeight();
		setRowHeight(x + 2);

		// 20190207 changed back to true
		getTableHeader().setReorderingAllowed(true);

		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
		dropTarget = new DropTarget(this, this);

		// 20101031 have table fill the scrollpane viewport area, instead of just the rows area
		setFillsViewportHeight(true);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}

	@Override
	public void scrollRectToVisible(Rectangle aRect) {
		super.scrollRectToVisible(aRect);
	}

	/**
	 * 2006/10/12 If you would like to allow for sorting on a clicked column heading. The user can use [ctrl] to click on multiple headings.
	 * Re-clicking on a heading will changed from ascending to descending order.
	 *
	 * @param b
	 */
	public void setAllowSorting(boolean b) {
		bAllowSorting = b;
		/*
		 * if (headerRenderer == null) { headerRenderer = new ButtonHeaderRenderer(this); Enumeration e
		 * = columnModel.getColumns(); for ( ;e.hasMoreElements(); ) { TableColumn col = (TableColumn)
		 * e.nextElement(); col.setHeaderRenderer(headerRenderer); } }
		 */
	}

	public boolean getAllowSorting() {
		return bAllowSorting;
	}

	/**
	 * Columns will be resized to be at least the size of the heading text.
	 */
	public void resizeColumnsToFitHeading() {
		FontMetrics fm = this.getFontMetrics(getFont());
		for (OATableColumn tc : getAllTableColumns()) {
			int w = tc.tc.getWidth();
			String s = (String) tc.tc.getHeaderValue();
			if (s == null) {
				s = "";
			}
			int w2 = OAJfcUtil.getCharWidth(Math.max(s.length(), 1));

			if (w < w2 + 12) {
				tc.tc.setPreferredWidth(w2 + 12);
				tc.tc.setWidth(w2 + 12);
				tc.defaultWidth = w2 + 12;
			}
		}
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		String s = super.getToolTipText(event);
		if (event != null) {
			Point pt = event.getPoint();

			int col = columnAtPoint(pt);
			int row = rowAtPoint(pt);

			// 20180716
			col = convertColumnIndexToModel(col);

			OATable t = getRightTable();
			if (t != null) {
				s = t.getToolTipText1(row, col, s);
			} else {
				t = getLeftTable();
				if (t != null) {
					col += t.getColumnCount();
				}
				s = getToolTipText1(row, col, s);
			}
		}
		return s;
	}

	private int cntTT;

	private String getToolTipText1(int row, int col, String defaultValue) {
		OATableColumn[] tcs = getAllTableColumns();

		OATableColumn tc = null;
		final Object obj = getObjectAt(row, col);

		if (col >= 0 && col < tcs.length) {
			tc = (OATableColumn) tcs[col];

			if (tc != null) {
				String s = tc.getToolTipText();
				if (OAString.isNotEmpty(s)) {
					defaultValue = s;
				}

				OATableComponent tcomp = tc.getOATableComponent();
				if (tcomp != null) {
					if (tcomp instanceof OAJfcComponent) {
						// call jfc controller, which also calls editquery
						defaultValue = ((OAJfcComponent) tcomp).getController().getToolTipText(obj, defaultValue);
						// call component
						defaultValue = ((OAJfcComponent) tcomp).getToolTipText(obj, row, defaultValue);
					}
				}
				// call table column
				defaultValue = tc.getTableToolTipText(this, row, col, defaultValue);
				// call tc customizer
				OATableColumnCustomizer tcc = tc.getCustomizer();
				if (tcc != null) {
					defaultValue = tcc.getToolTipText(obj, row, defaultValue);
				}
			}
		}
		// call table
		defaultValue = getToolTipText(row, col, defaultValue);

		if (tc != null && defaultValue != null && defaultValue.indexOf("<%=") >= 0) {
			if (tc.templateToolTip == null || tc.templateToolTip.getTemplate().equals(defaultValue)) {
				tc.templateToolTip = new OATemplate<>(defaultValue);
			}
			if (obj instanceof OAObject) {
				defaultValue = tc.templateToolTip.process((OAObject) obj);
			} else {
				defaultValue = "";
			}
		}
		if (defaultValue != null && defaultValue.indexOf('<') >= 0 && defaultValue.toLowerCase().indexOf("<html>") < 0) {
			defaultValue = "<html>" + defaultValue;
		}

		if (!OAString.isEmpty(OAString.trim(defaultValue))) {
			if (cntTT++ % 2 == 0) {
				defaultValue += " "; // so that it is changed and will show by mouse
			}
		}
		return defaultValue;
	}

	public String getToolTipText(int row, int col, String defaultValue) {
		/* not needed, redundant from getToolTipText1
		OATableColumn[] tcs = getAllTableColumns();

		if (col >= 0 && col < tcs.length) {
		    OATableColumn tc = (OATableColumn) tcs[col];
		    defaultValue = tc.getToolTipText(this, row, col, defaultValue);
		}
		*/
		return defaultValue;
	}

	/**
	 * Clear all of the filter values.
	 */
	public void resetFilters() {
		try {
			control.aiIgnoreValueChanged.incrementAndGet();
			_resetFilters();

		} finally {
			control.aiIgnoreValueChanged.decrementAndGet();
		}
	}

	private void _resetFilters() {
		for (OATableColumn tc : getAllTableColumns()) {
			if (tc.getFilterComponent() != null) {
				tc.getFilterComponent().reset();
			}
		}
		if (hubFilter != null) {
			hubFilter.refresh();
		} else if (tableRight != null && tableRight.hubFilter != null) {
			tableRight.hubFilter.refresh();
		}
		// 20180521
		if (hubSelect != null) {
			control.rebuildListSelectionModel();
		}

		Container cont = getParent();
		for (int i = 0; i < 3 && cont != null; i++) {
			cont.repaint();
			cont = cont.getParent();
		}
	}

	// 2006/12/29 called by superclass, this is overwritten from JTable
	protected JTableHeader createDefaultTableHeader() {
		headerRenderer = new PanelHeaderRenderer(this);

		JTableHeader th = new JTableHeader(columnModel) {
			public String getToolTipText(MouseEvent e) {
				java.awt.Point p = e.getPoint();
				int pos = columnModel.getColumnIndexAtX(p.x);
				if (pos < 0) {
					return null;
				}
				pos = columnModel.getColumn(pos).getModelIndex();
				String tt = null;
				if (pos >= 0 && pos < columns.size()) {
					OATableColumn tc = (OATableColumn) columns.elementAt(pos);
					tt = OATable.this.getColumnHeaderToolTipText(tc, p);
				}
				return tt;
			}
		};
		th.addMouseListener(new MouseAdapter() {
			Point pt;

			public void mousePressed(MouseEvent e) {
				pt = e.getPoint();
			}

			public void mouseReleased(MouseEvent e) {
				if (pt != null) {
					onHeadingMouseReleased(e, pt);
				}
				pt = null;
			}
		});
		return th;
	}

	protected void performSort() {
		boolean bx = OAThreadLocalDelegate.addSiblingHelper(siblingHelper);
		try {
			_performSort();
		} finally {
			if (bx) {
				OAThreadLocalDelegate.removeSiblingHelper(siblingHelper);
			}
		}
	}

	private boolean bIgnoreRefresh;

	// 2006/10/12
	private void _performSort() {
		if (!bAllowSorting) {
			return;
		}
		String s = null;

		OATableColumn[] allColumns = getAllTableColumns();

		int x = allColumns.length;
		OATableColumn colHubSelect = null;

		final ArrayList<OATableColumn> alSelectedColumns = new ArrayList<OATableColumn>();

		for (int i = 1;; i++) {
			boolean b = false;
			for (int j = 0; j < x; j++) {
				OATableColumn col = allColumns[j];
				col.getMethods(this.hub); // make sure that path has been set up correctly, to match to
											// table.hub
				if (col.sortOrder == i) {
					alSelectedColumns.add(col);
					if (OAString.isEmpty(col.path)) {
						if (!OAString.isEmpty(col.pathIntValue)) {
							if (s == null) {
								s = col.pathIntValue;
							} else {
								s += ", " + col.pathIntValue;
							}
							if (col.sortDesc) {
								s += " DESC";
							}
						} else {
							if (hubSelect != null) {
								colHubSelect = col;
							}
						}
					} else {
						if (s == null) {
							s = col.path;
						} else {
							s += ", " + col.path;
						}
						if (col.sortDesc) {
							s += " DESC";
						}
					}
					b = true;
				}
			}
			if (!b) {
				break;
			}
		}

		final Object[] objs = new Object[hubSelect == null ? 0 : hubSelect.getSize()];
		if (hubSelect != null) {
			hubSelect.copyInto(objs);
		}

		if (colHubSelect != null) {
			final OATableColumn tcSelect = colHubSelect;
			hub.sort(new Comparator() {
				@Override
				public int compare(Object o1, Object o2) {
					if (o1 == o2) {
						return 0;
					}
					int x = 0;
					for (OATableColumn col : alSelectedColumns) {

						Object z1 = col.getValue(hub, o1);
						Object z2 = col.getValue(hub, o2);

						x = OACompare.compare(z1, z2);
						if (x != 0) {
							if (col.sortDesc) {
								x *= -1;
							}
							if (col == tcSelect) {
								x *= -1;
							}
							break;
						}
					}
					return x;
				}
			});
		} else if (s != null) {
			hub.sort(s);
		} else {
			if (hub.isSorted()) {
				hub.cancelSort();
			}
			if (hubFilter != null && !bIgnoreRefresh) {
				try {
					bIgnoreRefresh = true;
					Object objx = getHub().getAO();
					hubFilter.refresh();
					getHub().setAO(objx);
				} finally {
					bIgnoreRefresh = false;
				}
			}
		}
		// 20150810 dont keep sorted
		if (hub.isSorted() && !getKeepSorted()) {
			hub.cancelSort();
		}

		if (hubSelect != null) {
			control.rebuildListSelectionModel();
			/*
			hubSelect.clear();
			for (Object obj : objs) {
			    hubSelect.add(obj);
			}
			*/
		}

		// reset AO
		// 20111008 add if
		if (hub.getAO() != null && hubSelect == null) {
			HubAODelegate.setActiveObjectForce(hub, hub.getAO());
		}
	}

	/**
	 * Returns Hub that is bound to Table.
	 */
	public Hub getHub() {
		return hub;
	}

	private boolean bKeepSorted;

	public void setKeepSorted(boolean b) {
		bKeepSorted = b;
	}

	public boolean getKeepSorted() {
		return bKeepSorted;
	}

	/**
	 * Sets Hub that is bound to Table.
	 */
	public void setHub(Hub h) {
		this.hub = h;
		resetFilterHub();
		oaTableModel = new OATableModel(hub);
		setModel(oaTableModel);
		control = new TableController(hub, this);
	}

	public OAJfcController getController() {
		return control;
	}

	protected void resetFilterHub() {
		Hub hx = getMasterFilterHub();
		if (hx == null) {
			hx = this.hub;
			if (hx == null) {
				return;
			}
		}
		siblingHelper = new OASiblingHelper(hx);

		int x = columns.size();
		for (int i = 0; i < x; i++) {
			OATableColumn tc = (OATableColumn) columns.elementAt(i);
			tc.setMethods(null);
			siblingHelper.add(tc.getPathFromTableHub(hub));
		}
	}

	// 20160722
	@Override
	public void addNotify() {
		super.addNotify();
		if (hubViewable != null) {
			getViewableHub();
		}
	}

	// 20180620 created
	// 20181004 get object used by component (at col)
	public Object getObjectAt(int row, int col) {
		Hub h = getHub();
		if (h == null) {
			return null;
		}
		Object obj = h.getAt(row);
		if (obj == null) {
			return null;
		}

		OATableColumn[] tcs = getAllTableColumns();
		if (col >= 0 && col < tcs.length) {
			OATableColumn tc = (OATableColumn) tcs[col];
			obj = tc.getObjectForTableObject(obj);
		}
		return obj;
	}

	public Object getRowObject(int row) {
		Hub h = getHub();
		if (h == null) {
			return null;
		}
		Object obj = h.getAt(row);
		return obj;
	}

	// hub used for viewable rows, so that column props (and dependent props) are only set up to work with portion of hub, instead of the full real hub
	protected Hub hubViewable;
	private AtomicInteger aiViewableChanged;

	public Hub getViewableHub() {
		boolean bCallUpdate = false;
		if (hubViewable == null) {
			hubViewable = new Hub(getHub().getObjectClass());
			bCallUpdate = true;
		}
		if (aiViewableChanged == null) {
			Component c = this.getParent();
			for (; c != null; c = c.getParent()) {
				if (!(c instanceof JScrollPane)) {
					continue;
				}
				aiViewableChanged = new AtomicInteger();
				bCallUpdate = true;
				JViewport vp = ((JScrollPane) c).getViewport();
				vp.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						updateViewableHub();
					}
				});
				break;
			}
		}
		if (bCallUpdate) {
			updateViewableHub();
		}
		return hubViewable;
	}

	protected void updateViewableHub() {
		final int cnt = aiViewableChanged == null ? 0 : aiViewableChanged.incrementAndGet();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (aiViewableChanged == null || aiViewableChanged.get() == cnt) {
					_updateViewableHub();
				}
			}
		});
	}

	private void _updateViewableHub() {
		if (hubViewable == null) {
			return;
		}
		Rectangle rec = getVisibleRect();
		int rowTop = rowAtPoint(rec.getLocation());
		rec.translate(0, rec.height);
		int rowBottom = rowAtPoint(rec.getLocation());

		boolean bx = OAThreadLocalDelegate.addSiblingHelper(siblingHelper);
		hubViewable.clear();
		OAThreadLocalDelegate.setLoading(true);
		try {
			int x = rowBottom;
			for (int i = rowTop; i <= x; i++) {
				Object objx = hub.getAt(i);
				if (objx == null) {
					break;
				}
				hubViewable.add(objx);
			}
		} finally {
			if (bx) {
				OAThreadLocalDelegate.removeSiblingHelper(siblingHelper);
			}
			OAThreadLocalDelegate.setLoading(false);
			HubEventDelegate.fireOnNewListEvent(hubViewable, true);
		}
	}

	// START Drag&Drop
	static class MyDragSourceListener implements DragSourceListener {
		public void dragEnter(DragSourceDragEvent e) {
		}

		public void dragOver(DragSourceDragEvent e) {
		}

		public void dropActionChanged(DragSourceDragEvent e) {
		}

		public void dragExit(DragSourceEvent e) {
		}

		public void dragDropEnd(DragSourceDropEvent e) {
		}
	}

	/**
	 * Flag to enable Drag and Drop. default=true
	 */
	public void setAllowDnd(boolean b) {
		setAllowDrop(b);
		setAllowDrag(b);
		OATable t = getLeftTable();
		if (t != null) {
			t.setAllowDnd(b);
		}
	}

	/**
	 * Flag to enable Drag and Drop. default=true
	 */
	public void setAllowDnD(boolean b) {
		setAllowDnd(b);
	}

	/**
	 * Flag to enable Drag and Drop. default=true
	 */
	public void allowDnd(boolean b) {
		setAllowDnd(b);
	}

	/**
	 * Flag to enable Drag and Drop. default=true
	 */
	public void allowDnD(boolean b) {
		setAllowDnd(b);
	}

	/**
	 * Flag to enable Dropping. default=true
	 */
	public boolean getAllowDrop() {
		return bAllowDrop;
	}

	/**
	 * Flag to enable Dropping. default=true
	 */
	public void setAllowDrop(boolean b) {
		bAllowDrop = b;
		OATable t = getLeftTable();
		if (t != null) {
			t.bAllowDrop = b;
		}
	}

	/**
	 * Flag to enable Dragging. default=true
	 */
	public boolean getAllowDrag() {
		OATable t = getRightTable();
		if (t != null) {
			return t.bAllowDrag;
		}
		return bAllowDrag;
	}

	/**
	 * Flag to enable Dragging. default=true
	 */
	public void setAllowDrag(boolean b) {
		bAllowDrag = b;
		OATable t = getLeftTable();
		if (t != null) {
			t.bAllowDrag = b;
		}
	}

	/**
	 * Flag to have a Dragged object removed from Hub. default=true
	 */
	public void setRemoveDragObject(boolean b) {
		bRemoveDragObject = b;
		OATable t = getLeftTable();
		if (t != null) {
			t.bRemoveDragObject = b;
		}
	}

	/**
	 * Flag to have a Dragged object removed from Hub. default=true
	 */
	public boolean getRemoveDragObject() {
		return bRemoveDragObject;
	}

	/** Used to support drag and drop (DND). */
	public void dragGestureRecognized(DragGestureEvent e) {
		if (!getAllowDrag()) {
			return;
		}
		if (hub != null) {
			Object obj = hub.getActiveObject();
			if (obj != null) {
				OATransferable t = new OATransferable(hub, obj);
				dragSource.startDrag(e, null, t, dragSourceListener);
			}
		}
	}

	/** Used to support drag and drop (DND). */
	public void dragEnter(DropTargetDragEvent e) {
		if (getAllowDrop() && e.isDataFlavorSupported(OATransferable.HUB_FLAVOR)) {
			e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
		} else {
			e.rejectDrag();
		}
	}

	/** Used to support drag and drop (DND). */
	public void dragOver(DropTargetDragEvent e) {
		Point pt = e.getLocation();
		autoscroll(pt);
	}

	private static Insets autoScrollInsets = new Insets(20, 20, 20, 20);

	private void autoscroll(Point cursorLocation) {
		Rectangle rectOuter = getVisibleRect();
		Rectangle rectInner = new Rectangle(rectOuter.x + autoScrollInsets.left, rectOuter.y + autoScrollInsets.top,
				rectOuter.width - (autoScrollInsets.left + autoScrollInsets.right),
				rectOuter.height - (autoScrollInsets.top + autoScrollInsets.bottom));
		if (!rectInner.contains(cursorLocation)) {
			Rectangle rect = new Rectangle(cursorLocation.x - autoScrollInsets.left, cursorLocation.y - autoScrollInsets.top,
					autoScrollInsets.left + autoScrollInsets.right, autoScrollInsets.top + autoScrollInsets.bottom);
			scrollRectToVisible(rect);
		}
	}

	/** Used to support drag and drop (DND). */
	public void dropActionChanged(DropTargetDragEvent e) {
	}

	/** Used to support drag and drop (DND). */
	public void dragExit(DropTargetEvent e) {
	}

	public boolean getAllowDrop(Hub hubDrag, Object objectDrag, Hub hubDrop) {
		return true;
	}

	/** Used to support drag and drop (DND). */
	public void drop(DropTargetDropEvent e) {
		try {
			if (!e.getTransferable().isDataFlavorSupported(OATransferable.HUB_FLAVOR)) {
				return;
			}
			if (!getAllowDrop()) {
				return;
			}
			if (hub == null) {
				return;
			}
			if (!isEnabled()) {
				return;
			}

			// get object to move/copy
			Hub dragHub = (Hub) e.getTransferable().getTransferData(OATransferable.HUB_FLAVOR);
			Object dragObject = (Object) e.getTransferable().getTransferData(OATransferable.OAOBJECT_FLAVOR);

			Point pt = e.getLocation();
			int row = rowAtPoint(pt);
			if (row < 0) {
				row = hub.getSize();
			}

			Rectangle rect = getCellRect(row, 0, true);

			if (rect != null && pt.y > (rect.y + (rect.height / 2))) {
				row++;
			}

			// 20161101 allow dnd with treetable
			OATreeTableController ttc = null;
			for (OATableColumn tc : columns) {
				OATableComponent comp = tc.getOATableComponent();
				if (comp instanceof OATreeTableController) {
					ttc = (OATreeTableController) comp;
					break;
				}
			}

			if (hub.contains(dragObject)) {
				int pos = hub.getPos(dragObject);
				if (pos == row) {
					return;
				}
				//if (pos+1 == row || pos-1 == row) return;
				int x = row;
				if (row < pos) {
					x++;
				}
				if (pos + 1 == x) {
					return;
				}
				x = JOptionPane.showOptionDialog(	OAJfcUtil.getWindow(OATable.this), "Ok to move row " + (pos + 1) + " to row " + x + "?",
													"Confirmation", 0, JOptionPane.QUESTION_MESSAGE, null, new String[] { "Yes", "No" },
													"Yes");
				if (x != 0) {
					return;
				}
			}

			if (ttc != null) {
				if (hub.getAt(row) == null) {
					Hub h = ttc.getRootHub();
					Object objx = h.getMasterObject();
					if (objx != null) {
						if (!objx.getClass().equals(ttc.getOneLinkInfo().getToClass())) {
							objx = null;
						}
					}
					((OAObject) dragObject).setProperty(ttc.getOneLinkInfo().getName(), objx);
				} else {
					Hub h = (Hub) ttc.getManyLinkInfo().getValue(hub.getAt(row));
					h.add(dragObject);
				}
				ttc.refresh();
			} else if (hub.getObjectClass().isAssignableFrom(dragObject.getClass())) {
				if (!getAllowDrop(dragHub, dragObject, hub)) {
					return;
				}
				int pos = hub.getPos(dragObject);

				if (pos >= 0) {
					if (!hub.isSorted()) {
						// move
						if (pos < row) {
							row--;
						}
						hub.move(pos, row);
						// 20091214
						OAUndoManager.add(OAUndoableEdit.createUndoableMove(null, hub, pos, row));
					}
				} else {
					if (hub.isSorted()) {
						hub.add(dragObject);
						// 20091214
						OAUndoManager.add(OAUndoableEdit.createUndoableAdd(null, hub, dragObject));
					} else {
						hub.insert(dragObject, row);
						// 20091214
						OAUndoManager.add(OAUndoableEdit.createUndoableInsert(null, hub, dragObject, row));
					}
					if (getRemoveDragObject()) {
						if (dragHub != hub) {
							dragHub.remove(dragObject);
						}
					}
				}
				hub.setActiveObject(dragObject);
			}
		} catch (Exception ex) {
		} finally {
			e.dropComplete(true);
		}
	}

	// END Drag&Drop

	/**
	 * Separate Hub that can contain selected objects. This will allow for a multi-select table.
	 */
	public void setSelectHub(Hub hub) {
		setSelectHub(hub, true);
	}

	public void setSelectHub(Hub hub, boolean bAllowRemovingFromSelectHub) {
		this.hubSelect = hub;
		int x = (hub == null) ? ListSelectionModel.SINGLE_SELECTION : ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
		getSelectionModel().setSelectionMode(x);
		control.setSelectHub(hubSelect, bAllowRemovingFromSelectHub);
		// 20190117 this should be turned on by calliing setMultiSelectControlKey(true)
		//bMultiSelectControlKey = (hub != null);
	}

	/**
	 * Separate Hub that can contain selected objects. This will allow for a multi-select table.
	 */
	public Hub getSelectHub() {
		return hubSelect;
	}

	/****
	 * public void setEditingRow(int aRow) { if (Hub.DEBUG) System.out.println("OATable.setEditingRow()"); super.setEditingRow(aRow); if
	 * (aRow >= 0 && hub != null) { if (hub.getPos(hub.getActiveObject()) != aRow) { hub.setActiveObject(aRow); int pos = hub.getPos(); if
	 * (pos != aRow) { getCellEditor().stopCellEditing(); setRowSelectionInterval(pos,pos); } } } }
	 ****/

	/**
	 * Number of columns that should be visible when determinng the preferred size of the Table.
	 */
	public int getPreferredColumns() {
		return prefCols;
	}

	/**
	 * Number of columns that should be visible when determinng the preferred size of the Table.
	 */
	public void setPreferredColumns(int cols) {
		setPreferredSize(prefRows, cols);
	}

	/**
	 * Number of rows that should be visible when determinng the preferred size of the Table.
	 */
	public int getPreferredRows() {
		return prefRows;
	}

	/**
	 * Number of rows that should be visible when determinng the preferred size of the Table.
	 */
	public void setPreferredRows(int rows) {
		setPreferredSize(rows, prefCols);
	}

	/**
	 * Number of columns and rows that should be visible when determinng the preferred size of the Table.
	 */
	public void setPreferredSize(int rows, int cols) {
		this.setPreferredSize(rows, cols, true);
	}

	/**
	 * Number of columns and rows that should be visible when determinng the preferred size of the Table.
	 *
	 * @param includeScrollBar add the width of a scrollbar to the preferred width of the Table.
	 */
	public void setPreferredSize(int rows, int cols, boolean includeScrollBar) {
		prefCols = ((cols > 0) ? cols : 1);
		prefRows = ((rows > 0) ? rows : 1);
		this.includeScrollBar = includeScrollBar;
		calcPreferredSize();
	}

	/**
	 * Number of display rows that this table can grow to.
	 */
	public void setPreferredMaxRows(int rows) {
		prefMaxRows = rows;
	}

	/**
	 * Used to have JTable.getCellRenderer(row, column) call OATable.getRenderer()
	 */
	class MyTableCellRenderer implements TableCellRenderer {
		TableCellRenderer rend;
		OATable table;

		public MyTableCellRenderer(OATable t) {
			this.table = t;
		}

		private int cntError;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component comp = null;
			try {
				hasFocus = hasFocus && row >= 0 && hub.getPos() == row;
				comp = _getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			} catch (Exception e) {
				if (cntError++ < 25 || (cntError % 250) == 0) {
					LOG.log(Level.WARNING, "error with column=" + column, e);
				}
			}
			return comp;
		}

		private Component _getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			if (rend == null) {
				return null;
			}

			boolean bMouseOver = (row == mouseOverRow && column == mouseOverColumn);

			column = convertColumnIndexToModel(column); // mouseover does not adjust column value

			// moved thishere 20180716
			OATable t = getRightTable();
			if (t != null) {
			} else {
				t = getLeftTable();
				if (t != null) {
					column += t.getColumnCount();
				}
			}

			// see: OATableCellRenderer
			Component comp = rend.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			Component compOrig = comp;

			t = getRightTable();
			if (t != null) {
				comp = t.getRenderer(comp, table, value, isSelected, hasFocus, row, column, wasChanged(row, column), bMouseOver);
			} else {
				t = getLeftTable();
				if (t != null) {
					//was:  column += t.getColumnCount();
				}
				comp = getRenderer(comp, table, value, isSelected, hasFocus, row, column, wasChanged(row, column), bMouseOver);
			}
			if (comp == null) {
				comp = compOrig;
			}

			return comp;
		}
	}

	private MyTableCellRenderer myRend;

	/**
	 * JTable method used to get the renderer for a cell. This is set up to automatically call getRenderer() from the column's component.
	 * Dont overwrite this method, since OATable could be made up of 2 tables.
	 *
	 * @see #getRenderer This needs to be used instead of overwriting this method - especially with OATableScrollPane.
	 * @see #customizeRenderer(JLabel, JTable, Object, boolean, boolean, int, int, boolean, boolean)
	 */
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		// This will call MyTableCellRenderer.getTableCellRendererComponent(),
		// which will then call OATable.getRenderer()
		if (myRend == null) {
			myRend = new MyTableCellRenderer(this);
		}

		// this will set the default renderer, ex:
		myRend.rend = super.getCellRenderer(row, column);
		return myRend;
	}

	// 20150315
	protected ConcurrentHashMap<String, Long> hmRowColumnChanged;
	private boolean bShowChanges;
	private Timer timerShowChanges;
	private final Object lockShowChanges = new Object();

	/**
	 * Flag to track changes to row,col (cells).
	 */
	public boolean getShowChanges() {
		return bShowChanges;
	}

	public void setShowChanges(final boolean b) {
		synchronized (lockShowChanges) {
			bShowChanges = b;
			if (tableRight != null) {
				return;
			}
			if (!b) {
				hmRowColumnChanged = null;
				if (timerShowChanges != null) {
					timerShowChanges.stop();
					timerShowChanges = null;
					hmRowColumnValue = null;
				}
			} else if (b && hmRowColumnChanged == null) {
				hmRowColumnChanged = new ConcurrentHashMap<String, Long>();

				timerShowChanges = new Timer(80, new ActionListener() { // was: 25
					int emptyCount;

					public void actionPerformed(ActionEvent e) {
						long tsNow = 0;
						for (Map.Entry<String, Long> entry : hmRowColumnChanged.entrySet()) {
							if (tsNow == 0) {
								tsNow = System.currentTimeMillis();
							}
							if (tsNow > entry.getValue().longValue() + 1000) { // was: 500
								hmRowColumnChanged.remove(entry.getKey());
								if (hmRowColumnValue != null) {
									hmRowColumnValue.remove(entry.getKey());
								}
							}
						}
						if (tsNow > 0) {
							OATable.this.repaint(250);
							if (tableLeft != null) {
								tableLeft.repaint(250);
							}
						} else {
							emptyCount++;
							if (emptyCount > 10) {
								emptyCount = 0;
								synchronized (OATable.this.lockShowChanges) {
									if (hmRowColumnChanged.size() == 0) {
										timerShowChanges.stop();
									}
								}
							}
						}
					}
				});
				timerShowChanges.setRepeats(true);
			}
		}
		if (tableLeft != null) {
			tableLeft.setShowChanges(b);
		}
	}

	public void setChanged(int row, int col) {
		OATable t = getRightTable();
		if (t != null) {
			t.setChanged(row, col);
			return;
		}
		if (!bShowChanges) {
			return;
		}

		// if (!OARemoteThreadDelegate.isRemoteThread()) return;
		synchronized (lockShowChanges) {
			hmRowColumnChanged.put(row + "." + col, System.currentTimeMillis());
			if (!timerShowChanges.isRunning()) {
				timerShowChanges.start();
			}
		}
	}

	private ConcurrentHashMap<String, Object> hmRowColumnValue;

	public void setChanged(int row, int col, Object newValue) {
		OATable t = getRightTable();
		if (t != null) {
			t.setChanged(row, col, newValue);
			return;
		}

		if (!bShowChanges) {
			return;
		}
		if (hmRowColumnValue == null) {
			hmRowColumnValue = new ConcurrentHashMap<String, Object>();
		}
		String k = row + "." + col;
		if (newValue == null) {
			newValue = OANullObject.instance;
		}
		Object old = hmRowColumnValue.get(k);
		if (!OACompare.isEqual(old, newValue)) {
			if (old != null) {
				setChanged(row, col);
			}
			hmRowColumnValue.put(k, newValue);
		}
	}

	public boolean wasChanged(int row, int col) {
		OATable t = getRightTable();
		if (t != null) {
			return t.wasChanged(row, col);
		}
		if (!bShowChanges) {
			return false;
		}

		if (hmRowColumnChanged == null) {
			return false;
		}
		Long longx = hmRowColumnChanged.get(row + "." + col);
		return (longx != null);
	}

	@Override
	public void setRowHeight(int rowHeight) {
		super.setRowHeight(rowHeight);
		OATable t = getLeftTable();
		if (t != null) {
			t.setRowHeight(rowHeight);
		}
	}

	/**
	 * Can be overwritten to customize the component used to renderer a Table cell. see #getRenderer(JComponent, JTable, Object, boolean,
	 * boolean, int, int) to customize the component see #customizeRenderer(JLabel, JTable, Object, boolean, boolean, int, int) Preferred
	 * way
	 */
	public Component getRenderer_OLD(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component comp = null;
		if (myRend != null) {
			comp = myRend.rend.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		if (comp instanceof JLabel) {
			OATable t = getRightTable();
			if (t != null) {
				// qq t.customizeRenderer((JLabel) comp, table, value, isSelected, hasFocus, row,
				// column);
			} else {
				t = getLeftTable();
				if (t != null) {
					column += t.getColumnCount();
				}
				// qq customizeRenderer((JLabel) comp, table, value, isSelected, hasFocus, row, column);
			}
		}
		return comp;
	}

	// listeners for customizing Renderers
	Vector vecListener;

	/**
	 * Add a listener that is called to customize the rendering component for a cell.
	 */
	public void addListener(OATableListener l) {
		if (vecListener == null) {
			vecListener = new Vector(2, 2);
		}
		if (!vecListener.contains(l)) {
			vecListener.addElement(l);
		}
	}

	/**
	 * Remove a listener that is called to customize the rendering component for a cell.
	 */
	public void removeListener(OATableListener l) {
		vecListener.removeElement(l);
	}

	public OATableListener[] getListeners() {
		if (vecListener == null) {
			return null;
		}
		OATableListener[] l = new OATableListener[vecListener.size()];
		vecListener.copyInto(l);
		return l;
	}

	public boolean debug;

	/**
	 * Determine preferred size based on number of preferred number of columns and rows.
	 */
	public void calcPreferredSize() {
		int w = 0;
		int cols = prefCols;

		OATableColumn[] tcs = getAllTableColumns();

		int i = 0;
		for (OATableColumn tc : getAllTableColumns()) {
			if (i++ == cols) {
				break;
			}
			w += tc.tc.getWidth();
		}

		w += cols * getIntercellSpacing().width;
		w += 2;
		// was: int h = getIntercellSpacing().height + getRowHeight();
		// 20101027 rowHeight includes spacing
		int h = getRowHeight();

		int hx = Math.max(prefRows, Math.min(getHub().size(), prefMaxRows));

		h *= hx;

		if (w < 20) {
			w = 20;
		}
		if (h < 20) {
			h = 20;
		}

		if (includeScrollBar) {
			w += 18; // scrollbar
		}
		setPreferredScrollableViewportSize(new Dimension(w, h));

		// have table resized in layoutManager
		this.invalidate();
		Component c = this.getParent();
		if (c instanceof JViewport) {
			c = c.getParent();
			if (c != null && c instanceof JScrollPane) {
				c = c.getParent();
			}
		}
		if (c != null && c.getParent() != null) {
			c = c.getParent();
		}
		if (c != null) {
			c.invalidate();
			c.validate();
		}
	}

	public void calcForPopup() {
		int w = 0;
		int cols = prefCols;

		OATableColumn[] tcs = getAllTableColumns();

		int i = 0;
		for (OATableColumn tc : getAllTableColumns()) {
			if (i++ == cols) {
				break;
			}
			w += tc.tc.getWidth();
		}

		w += cols * getIntercellSpacing().width;
		w += 2;
		int h = getRowHeight();
		int rows = getHub().getCurrentSize();
		if (rows < 5) {
			rows = 5;
		}
		rows = Math.min(rows, prefRows);
		h *= rows;
		if (w < 20) {
			w = 20;
		}
		if (h < 20) {
			h = 20;
		}

		if (includeScrollBar) {
			w += 18; // scrollbar
		}
		setPreferredScrollableViewportSize(new Dimension(w, h));

		// have table resized in layoutManager
		this.invalidate();
		Component c = this.getParent();
		if (c instanceof JViewport) {
			c = c.getParent();
			if (c != null && c instanceof JScrollPane) {
				c = c.getParent();
			}
		}
		if (c != null && c.getParent() != null) {
			c = c.getParent();
		}
		if (c != null) {
			c.invalidate();
			c.validate();
		}
	}

	/**
	 * Returns the column position for an OATableComponent.
	 */
	public int getColumnIndex(OATableComponent c) {
		if (tableLeft != null) {
			int x = tableLeft.getColumnIndex(c);
			if (x >= 0) {
				return x;
			}
		}

		int x = columns.size();
		for (int i = 0; i < x; i++) {
			OATableColumn tc = (OATableColumn) columns.elementAt(i);
			if (tc.getOATableComponent() == c) {
				if (tableLeft != null) {
					i += tableLeft.columns.size();
				}
				return i;
			}
		}
		return -1;
	}

	/**
	 * Change the heading for a column number. First column is at postion 0.
	 */
	public void setColumnHeading(int col, String heading) {
		if (col < columns.size() && col >= 0) {
			getColumnModel().getColumn(col).setHeaderValue(heading);
			invalidate();
			this.getParent().validate();
		}
	}

	/**
	 * Change the width for a column number, based on character width. First column is at postion 0.
	 */
	public void setColumnWidth(int col, int w) {
		if (col < columns.size() && col >= 0) {
			getColumnModel().getColumn(col).setWidth(w);
			calcPreferredSize();
		}
	}

	/**
	 * Set the property path used to display values for a column. This could be necessary when it can not be determined by the columns
	 * OATableComponent.
	 */
	public void setColumnPropertyPath(int col, String propertyPath) {
		if (col < columns.size() && col >= 0) {
			OATableColumn tc = (OATableColumn) columns.elementAt(col);
			tc.path = propertyPath;
			tc.bIsAlreadyExpanded = true;
			tc.setMethods(null);
			if (oaTableModel != null) {
				boolean b = false;
				try {
					if (control != null) {
						b = true;
						control.aiIgnoreValueChanged.incrementAndGet();
					}
					oaTableModel.fireTableStructureChanged();
				} finally {
					if (b && control != null) {
						control.aiIgnoreValueChanged.decrementAndGet();
					}
				}
			}
		}
	}

	public void resetColumn(OATableComponent comp) {
		int col = getColumnIndex(comp);
		if (col < columns.size() && col >= 0) {
			OATableColumn tc = (OATableColumn) columns.elementAt(col);
			tc.path = comp.getPropertyPath();
			tc.setMethods(null);
			if (oaTableModel != null) {
				boolean b = false;
				try {
					if (control != null) {
						b = true;
						control.aiIgnoreValueChanged.incrementAndGet();
					}
					oaTableModel.fireTableStructureChanged();
				} finally {
					if (b && control != null) {
						control.aiIgnoreValueChanged.decrementAndGet();
					}
				}
			}
		}
	}

	// 20150428
	/**
	 * Add a column that will that will use checkboxes to show selected rows.
	 */
	public OATableColumn addCounterColumn() {
		bResizeCounterColumn = true;
		return addCounterColumn("#", 4);
	}

	public OATableColumn addCounterColumn(String heading, int width) {
		OALabel lbl = new OALabel(getHub(), "") {
			@Override
			public void customizeTableRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
					int column, boolean wasChanged, boolean wasMouseOver) {
				lbl.setText("" + (row + 1) + " ");
				lbl.setHorizontalAlignment(SwingConstants.RIGHT);
				if (wasMouseOver) {
					lbl.setForeground(Color.white);
				} else if (!isSelected) {
					lbl.setForeground(Color.gray);
				}
			}

			@Override
			public String getTableToolTipText(JTable table, int row, int col, String defaultValue) {
				defaultValue = super.getTableToolTipText(table, row, col, defaultValue);
				if (OAString.isEmpty(defaultValue)) {
					defaultValue = (row + 1) + " of " + getHub().getSize();
				}
				return defaultValue;
			}
		};
		tcCount = addColumn(heading, width, lbl);
		tcCount.setAllowSorting(false);
		resizeCounterColumn();
		return tcCount;
	}

	protected OATableColumn tcCount;
	protected boolean bResizeCounterColumn;

	public void resizeCounterColumn() {
		if (!bResizeCounterColumn || tcCount == null) {
			return;
		}
		String s = "" + getHub().getSize();
		int x = Math.max(s.length(), 2);
		tcCount.getTableColumn().setPreferredWidth(OAJfcUtil.getCharWidth(x) + 11);
	}

	// 20150423
	/**
	 * Add a column that will that will use checkboxes to show selected rows.
	 *
	 * @param hubSelect
	 * @param heading
	 * @param width
	 */
	public void addSelectionColumn(Hub hubSelect, String heading, int width) {
		// 20190206 added b
		boolean b = hubSelect != null && hubSelect.getMasterHub() == null; // default
		addSelectionColumn(hubSelect, heading, width, b); // b was true
	}

	public void addSelectionColumn(Hub hubSelect, String heading, int width, boolean bAllowRemovingFromSelectHub) {
		if (hubSelect == null) {
			return;
		}
		setSelectHub(hubSelect, bAllowRemovingFromSelectHub);
		chkSelection = new OACheckBox(hub, hubSelect) {
			@Override
			public String getTableToolTipText(JTable table, int row, int col, String defaultValue) {
				Object obj = hub.getAt(row);
				if (obj == null || OATable.this.hubSelect == null) {
					return super.getTableToolTipText(table, row, col, defaultValue);
				}
				int pos = OATable.this.hubSelect.getPos(obj);
				if (pos < 0) {
					return OATable.this.hubSelect.getSize() + " selected";
				}
				return (pos + 1) + " of " + OATable.this.hubSelect.getSize() + " selected";
			}

			@Override
			public Component getTableRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
					int column) {
				Component comp = super.getTableRenderer(lbl, table, value, isSelected, hasFocus, row, column);
				if (row == -1) {
					// heading
					//need to set checked=true if all selected
					boolean b = isAllSelected();
					chkRenderer.setSelected(b);
					setHalfChecked(!b && isAnySelected());
				} else {
					setHalfChecked(false);
				}
				return comp;
			}
		};
		chkSelection.setToolTipText(" ");
		chkSelection.setTableHeading(heading);
		OATableColumn tc = addColumn(heading, width, chkSelection);
	}

	protected boolean isAllSelected() {
		Hub h = getSelectHub();
		if (h == null) {
			return false;
		}
		int x = h.getSize();
		if (x == 0) {
			return false;
		}
		if (getHub().getSize() > x) {
			return false;
		}

		if (getHub().size() == 0) {
			return false;
		}
		for (Object obj : getHub()) {
			if (!h.contains(obj)) {
				return false;
			}
		}
		return true;
	}

	protected boolean isAnySelected() {
		Hub h = getSelectHub();
		if (h == null) {
			return false;
		}
		if (h.getSize() == 0) {
			return false;
		}
		if (getHub().getSize() == 0) {
			return false;
		}
		for (Object obj : h) {
			if (getHub().contains(obj)) {
				return true;
			}
		}
		return false;
	}

	protected OACheckBox chkSelection;

	// 20150511
	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggleUsingControlKey, boolean extendUsingShiftKey) {
		// extendUsingShiftKey=true if its a mouse drag

		if (chkSelection == null && tableRight != null) {
			chkSelection = tableRight.chkSelection;
		}

		if (chkSelection != null && hubSelect != null) {
			if (extendUsingShiftKey) {
				if (bIsMouseDragging) {
					if (lastMouseDragRow < 0) {
						lastMouseDragRow = rowIndex;
						return; // ignore right now
					}
					if (rowIndex == lastMouseDragRow) {
						return;
					}
					getSelectionModel().addSelectionInterval(lastMouseDragRow, lastMouseDragRow);
				}
			} else {
				// 20180521
				if (!toggleUsingControlKey && bMultiSelectControlKey) {
					toggleUsingControlKey = true;
				}
			}

			if (!extendUsingShiftKey && !toggleUsingControlKey && !bIsProcessKeyBinding && !bMultiSelectControlKey) {
				int addColumns = 0;
				if (tableLeft != null) {
					addColumns = tableLeft.getColumnCount();
				}
				if ((columnIndex + addColumns) == getColumnIndex(chkSelection)) {
					toggleUsingControlKey = true;
				}
			}
		}
		try {
			super.changeSelection(rowIndex, columnIndex, toggleUsingControlKey, extendUsingShiftKey);
		} catch (Exception e) {
		}
	}

	private boolean bMultiSelectControlKey;

	/**
	 * Mulitselect flag to know if clicking a row will always act like controlkey was used.
	 *
	 * @param bMultiSelectControlKey if true, then all row clicks will automatically turn on ctrol key.
	 */
	public void setMultiSelectControlKey(boolean b) {
		this.bMultiSelectControlKey = b;
		if (tableLeft != null) {
			tableLeft.bMultiSelectControlKey = b;
		}
		if (tableRight != null) {
			tableRight.bMultiSelectControlKey = b;
		}
	}

	// 20150423
	// was:
	public void changeSelection_OLD(int rowIndex, int columnIndex, boolean toggleUsingControlKey, boolean extendUsingShiftKey) {
		// extendUsingShiftKey=true if its a mouse drag

		if (chkSelection == null && tableRight != null) {
			chkSelection = tableRight.chkSelection;
		}

		if (chkSelection != null && hubSelect != null) {

			if (extendUsingShiftKey) {
				if (bIsMouseDragging) {
					if (lastMouseDragRow < 0) {
						lastMouseDragRow = rowIndex;
						return; // ignore right now
					}
					if (rowIndex == lastMouseDragRow) {
						return;
					}
					getSelectionModel().addSelectionInterval(lastMouseDragRow, lastMouseDragRow);
				}
			}

			if (columnIndex == getColumnIndex(chkSelection)) {
				if (!extendUsingShiftKey) {
					toggleUsingControlKey = true;
				}
			} else {
				if (!extendUsingShiftKey && hubSelect.getSize() > 0 && !toggleUsingControlKey) {
					toggleUsingControlKey = true;
				}
			}
		}
		super.changeSelection(rowIndex, columnIndex, toggleUsingControlKey, extendUsingShiftKey);
	}

	/**
	 * Create a new column using an OATableComponent.
	 */
	public OATableColumn addColumn(OATableComponent comp) {
		TableCellEditor c = comp.getTableCellEditor();
		return this.addColumnMain(comp.getTableHeading(), -1, comp.getPropertyPath(), comp, c, -1, null);
	}

	/**
	 * Create a new column using an OATableComponent.
	 */
	public OATableColumn add(OATableComponent comp) {
		TableCellEditor c = comp.getTableCellEditor();
		return this.addColumnMain(comp.getTableHeading(), -1, comp.getPropertyPath(), comp, c, -1, null);
	}

	/**
	 * Create a new column using an OATableComponent.
	 *
	 * @param heading column heading
	 * @param width   of column based on average character width.
	 */
	public OATableColumn addColumn(String heading, int columns, OATableComponent comp) {
		TableCellEditor c = comp.getTableCellEditor();
		return this.addColumnMain(heading, columns, comp.getPropertyPath(), comp, c, -1, null);
	}

	public OATableColumn addColumn(String heading, OATableComponent comp) {
		TableCellEditor c = comp.getTableCellEditor();
		return this.addColumnMain(heading, -1, comp.getPropertyPath(), comp, c, -1, null);
	}

	/**
	 * Create a new column using an OATableComponent.
	 *
	 * @param heading column heading
	 * @param width   of column based on average character width.
	 */
	public OATableColumn add(String heading, int columns, OATableComponent comp) {
		TableCellEditor c = comp.getTableCellEditor();
		return this.addColumnMain(heading, columns, comp.getPropertyPath(), comp, c, -1, null);
	}

	/**
	 * Create a new column using an OATableComponent.
	 *
	 * @param heading column heading
	 * @param width   of column based on average character width.
	 * @param path    Set the property path used to display values for a column. This could be necessary when it can not be determined by
	 *                the columns OATableComponent.
	 */
	public OATableColumn addColumn(String heading, int columns, String path, OATableComponent comp) {
		TableCellEditor c = comp.getTableCellEditor();
		OATableColumn tc = this.addColumnMain(heading, columns, path, comp, c, -1, null);
		return tc;
	}

	/**
	 * Create a new column using an OATableComponent.
	 *
	 * @param heading column heading
	 * @param width   of column based on average character width.
	 * @param path    Set the property path used to display values for a column. This could be necessary when it can not be determined by
	 *                the columns OATableComponent.
	 */
	public OATableColumn add(String heading, int columns, String path, OATableComponent comp) {
		TableCellEditor c = comp.getTableCellEditor();
		OATableColumn tc = this.addColumnMain(heading, columns, path, comp, c, -1, null);
		return tc;
	}

	/**
	 * Create a new column using an OATableComponent.
	 *
	 * @param heading column heading
	 * @param columns of column based on average character width.
	 * @param path    Set the property path used to display values for a column. This could be necessary when it can not be determined by
	 *                the columns OATableComponent.
	 * @param index   column number, -1 to append to existing columns
	 */
	public OATableColumn addColumn(String heading, int columns, String path, OATableComponent comp, int index) {
		TableCellEditor c = comp.getTableCellEditor();
		OATableColumn tc = this.addColumnMain(heading, columns, path, comp, c, index, comp.getFormat());
		return tc;
	}

	/**
	 * Create a new column using an OATableComponent.
	 *
	 * @param heading column heading
	 * @param columns of column based on average character width.
	 * @param path    Set the property path used to display values for a column. This could be necessary when it can not be determined by
	 *                the columns OATableComponent.
	 * @param index   column number, -1 to append to existing columns
	 */
	public OATableColumn add(String heading, int columns, String path, OATableComponent comp, int index) {
		TableCellEditor c = comp.getTableCellEditor();
		return this.addColumnMain(heading, columns, path, comp, c, index, null);
	}

	/**
	 * Create a new column using a path.
	 *
	 * @param heading column heading
	 * @param columns of column based on average character width.
	 * @param path    Set the property path used to display values for a column.
	 */
	public OATableColumn addColumn(String heading, int columns, String path) {
		OALabel lbl = new OALabel(getHub(), path);
		return this.addColumnMain(heading, columns, path, lbl, (TableCellEditor) null, -1, null);
	}

	/**
	 * Create a new column using a path.
	 *
	 * @param heading column heading
	 * @param columns of column based on average character width.
	 * @param path    Set the property path used to display values for a column.
	 */
	public OATableColumn addColumn(String heading, int columns, String path, String fmt) {
		OALabel lbl = new OALabel(getHub(), path);
		return this.addColumnMain(heading, columns, path, null, (TableCellEditor) null, -1, fmt);
	}

	/**
	 * Create a new column using a path.
	 *
	 * @param heading column heading
	 * @param columns of column based on average character width.
	 * @param path    Set the property path used to display values for a column.
	 */
	public OATableColumn add(String heading, int columns, String path) {
		OALabel lbl = new OALabel(getHub(), path);
		return this.addColumnMain(heading, columns, path, null, (TableCellEditor) null, -1, null);
	}

	/**
	 * Create a new column using a path.
	 *
	 * @param heading column heading
	 * @param columns of column based on average character width.
	 * @param path    Set the property path used to display values for a column.
	 */
	public OATableColumn add(String heading, int columns, String path, String fmt) {
		return this.addColumnMain(heading, columns, path, null, (TableCellEditor) null, -1, fmt);
	}

	/**
	 * qqqqqqqqqq static int averageCharWidth = 0; static int averageCharHeight = 0; static int lastFontSize = 0; / ** Used to determine the
	 * pixel width based on the average width of a character 'X'. / public static int getCharWidth(Component comp, int columns) { if (comp
	 * == null) return 0; return getCharWidth(comp, comp.getFont(), columns); } public static int getCharWidth(int columns) { if
	 * (averageCharWidth != 0) { return averageCharWidth * columns; } JTextField txt = new JTextField(); Font font = txt.getFont(); return
	 * getCharWidth(txt, font, columns); } public static int getCharWidth(Component comp, Font font, int columns) { if (comp == null) return
	 * 0; if (averageCharWidth == 0 || (font != null && font.getSize() != lastFontSize)) { if (font == null) {
	 * System.out.println("OATable.getCharWidth=null, will use average=12 as default"); Exception e = new
	 * Exception("OATable.getCharWidth=null, will use average=12 as default"); e.printStackTrace(); return (11 * columns); } lastFontSize =
	 * font.getSize(); FontMetrics fm = comp.getFontMetrics(font); //averageCharWidth = (int) (fm.stringWidth("9XYma") / 5); //
	 * averageCharWidth = fm.charWidth('m'); // =11, same code used by JTextField.getColumnWidth averageCharWidth = (int)
	 * (fm.stringWidth("9m0M123456") / 10); // =7 / * test Font fontx = new Font( "Monospaced", Font.PLAIN, 12 ); fm =
	 * comp.getFontMetrics(fontx); int x2 = fm.charWidth('m'); =7 / } return (averageCharWidth * columns); } public static int
	 * getCharHeight() { if (averageCharHeight != 0) return averageCharHeight; JTextField txt = new JTextField(); Font font = txt.getFont();
	 * return getCharHeight(txt, font); } public static int getCharHeight(Component comp, Font font) { if (averageCharHeight == 0 || (font
	 * != null && font.getSize() != lastFontSize)) { lastFontSize = font.getSize(); FontMetrics fm = comp.getFontMetrics(font);
	 * averageCharHeight = (int) fm.getHeight(); } return (averageCharHeight); }
	 ***/

	/*
	 * later ... public void addColumn(String heading, int width, OATableColumn oatc) { int pos =
	 * columns.size(); columns.insertElementAt(oatc, pos);
	 *
	 * Font font = ((JComponent)oatc.oaComp).getFont(); if (width < 0 && oatc.oaComp != null) { width =
	 * ((JComponent)oatc.oaComp).getPreferredSize().width; width /=
	 * getCharWidth((JComponent)oatc.oaComp, font, 1); } int w = OATable.getCharWidth(this,font,width);
	 * w += 8; // borders, etc.
	 *
	 * TableColumn tc = new TableColumn(pos); tc.setPreferredWidth(oatc.defaultWidth); tc.setWidth(w);
	 * tc.setCellEditor(oatc.comp);
	 *
	 * tc.setCellRenderer( new OATableCellRenderer(oatc) ); tc.setHeaderValue(heading);
	 * tc.sizeWidthToFit(); getColumnModel().addColumn(tc);
	 *
	 * column.tc = oatc; if (headerRenderer != null) tc.setHeaderRenderer(headerRenderer); // 2006/10/13
	 *
	 * tc.setHeaderRenderer(headerRenderer); // 2006/12/29
	 *
	 * calcPreferredSize(); }
	 */

	/**
	 * Main method for adding a new Table Column.
	 */
	protected OATableColumn addColumnMain(String heading, int cols, String path, OATableComponent oaComp, final TableCellEditor editComp,
			int index, String fmt) {
		Font font;

		String ppTable = oaComp == null ? null : oaComp.getTablePropertyPath(this);
		if (OAString.isNotEmpty(ppTable)) {
			path = ppTable;
		}

		Component comp = null;
		if (oaComp instanceof JComponent) {
			comp = (Component) oaComp;
		} else if (oaComp != null) {
			TableCellEditor tce = oaComp.getTableCellEditor();
			if (tce != null) {
				comp = tce.getTableCellEditorComponent(this, null, false, -1, -1);
			}
		}

		if (comp != null) {
			font = comp.getFont();

			// 20151226 stop table editor on focuslost
			FocusListener fl = (new FocusListener() {
				int focusRow;

				@Override
				public void focusLost(FocusEvent e) {
					TableCellEditor ed = getCellEditor();
					if (ed != editComp) {
						return;
					}
					if (focusRow != getHub().getPos()) {
						return;
						// 20180830 removed, since components already have focus listener that stop the editing
						// ed.stopCellEditing();
					}
				}

				@Override
				public void focusGained(FocusEvent e) {
					focusRow = getHub().getPos();
				}
			});
			comp.addFocusListener(fl);
		} else {
			font = getFont();
		}

		if (cols <= 0) {
			if (comp != null) {
				if (comp instanceof OAJfcComponent) {
					cols = ((OAJfcComponent) comp).getController().getColumns();
				} else {
					cols = comp.getPreferredSize().width;
					cols /= OAJfcUtil.getCharWidth(comp, font, 1);
				}
			} else {
				cols = heading == null ? 3 : heading.length();
			}
		}
		int width;
		width = OAJfcUtil.getCharWidth(this, font, cols);
		if (comp instanceof JComponent) {
			Insets ins = ((JComponent) comp).getInsets();
			if (ins != null) {
				width += ins.left + ins.right;
			}
		}
		width += 4;

		TableCellRenderer rend = null;

		OATableColumn column = new OATableColumn(this, path, editComp, rend, oaComp, fmt);
		column.defaultWidth = width;
		if (oaComp != null) {
			oaComp.setTable(this);
		}

		int col = index;
		if (index == -1) {
			col = columns.size();
		}

		columns.insertElementAt(column, col);
		if (oaTableModel != null) {
			boolean b = false;
			try {
				if (control != null) {
					b = true;
					control.aiIgnoreValueChanged.incrementAndGet();
				}
				oaTableModel.fireTableStructureChanged();
			} finally {
				if (b && control != null) {
					control.aiIgnoreValueChanged.decrementAndGet();
				}
			}
		}

		final TableColumn tc = new TableColumn(col);

		tc.setPreferredWidth(width);
		tc.setWidth(width);
		tc.setCellEditor(editComp);

		tc.setCellRenderer(new OATableCellRenderer(column));
		tc.setHeaderValue(heading);
		// 20150927 removed, not positive what this does, but it apprears to set the width based on component. I want it based on columns, so I'm removing it
		// tc.sizeWidthToFit(); // 2006/12/26
		getColumnModel().addColumn(tc);

		column.headerRenderer = null;
		column.tc = tc; // 2006/10/12
		tc.setHeaderRenderer(headerRenderer);

		calcPreferredSize();

		// 20181201 hide/show the column based on visible changeListener
		if (comp instanceof OAJfcComponent) {
			OAJfcComponent jc = (OAJfcComponent) comp;
			final HubChangeListener hcl = jc.getController().getVisibleChangeListener();
			HubChangeListener hclx = new HubChangeListener() {
				boolean b = true;
				int w, max, prefWidth;

				@Override
				protected void onChange() {
					boolean bx = hcl.getValue();
					if (bx) {
						if (w > 0) {
							tc.setResizable(true);
							tc.setMaxWidth(max);
							tc.setMinWidth(0);
							tc.setWidth(w);
							tc.setPreferredWidth(prefWidth);
						}
					} else {
						w = tc.getWidth();
						max = tc.getMaxWidth();
						prefWidth = tc.getPreferredWidth();
						tc.setMinWidth(0);
						tc.setWidth(0);
						tc.setPreferredWidth(0);
						tc.setMaxWidth(0);
						tc.setResizable(false);
					}
				}
			};
			hcl.addHubChangeListener(hclx);
		}

		if (siblingHelper != null) {
			try {
				siblingHelper.add(column.getPathFromTableHub(hub));
			} catch (Exception e) {
			}
		}

		return column;
	}

	/**
	 * Remove a column from Table.
	 */
	public void removeColumn(int pos) {
		if (pos < 0) {
			return;
		}
		if (pos >= columns.size()) {
			return;
		}

		columns.removeElementAt(pos);
		getColumnModel().removeColumn(getColumnModel().getColumn(pos));
	}

	/*****
	 * protected void addImpl(Component comp,Object constraints,int index) { if (comp instanceof OATableComponent) { OATableComponent oacomp
	 * = (OATableComponent) comp; int w = oacomp.getColumns(); if (w < 0) w = 8; addColumn(oacomp.getTableHeading(), w,
	 * oacomp.getPropertyPath(), oacomp, index); } else super.addImpl(comp,constraints,index); } public Component getComponent(int n) { if
	 * (!bDesignTime) return super.getComponent(n); int x = columns.size(); for (int i=0,j=0; i<x; i++) { OATableColumn tc = (OATableColumn)
	 * columns.elementAt(i); if (tc.oaComp != null) { if (j == n) return (Component) tc.oaComp; j++; } } return null; } public int
	 * getComponentCount() { if (!bDesignTime) return super.getComponentCount(); int x = columns.size(); int cnt=0; for (int i=0; i<x; i++)
	 * { OATableColumn tc = (OATableColumn) columns.elementAt(i); if (tc.oaComp != null) cnt++; } return cnt; } public Component[]
	 * getComponents() { if (!bDesignTime) return super.getComponents(); Component[] comps = new Component[getComponentCount()]; int x =
	 * columns.size(); for (int i=0,j=0; i<x; i++) { OATableColumn tc = (OATableColumn) columns.elementAt(i); if (tc.oaComp != null) {
	 * comps[j] = (Component) tc.oaComp; j++; } } return comps; } public void remove(Component comp) { if (!bDesignTime) return
	 * super.remove(comp); int x = columns.size(); for (int i=0; i<x; i++) { OATableColumn tc = (OATableColumn) columns.elementAt(i); if
	 * (tc.comp == comp) removeColumn(i); } } public void removeAll() { int x = columns.size(); for (int i=0; i<x; i++) { removeColumn(0); }
	 * }
	 ******/

	/**
	 * Table Model using a Hub.
	 */
	class OATableModel extends DefaultTableModel {
		Hub hub;

		public OATableModel(Hub hub) {
			this.hub = hub;
		}

		public int getColumnCount() {
			return columns.size();
		}

		public int getRowCount() {
			if (hub == null) {
				return 0;
			}
			return Math.abs(hub.getSize());
		}

		public void fireTableStructureChanged() {
			for (int i = 0; i < 3; i++) {
				boolean b = false;
				try {
					if (control != null) {
						b = true;
						control.aiIgnoreValueChanged.incrementAndGet();
					}
					_fireTableStructureChanged();
					break;
				} catch (Exception e) {
					// ignore
				} finally {
					if (b && control != null) {
						control.aiIgnoreValueChanged.decrementAndGet();
					}
				}
			}
		}

		private void _fireTableStructureChanged() {
			// need to retain the selected objects
			super.fireTableStructureChanged();

			getSelectionModel().clearSelection();
			if (hubSelect != null) {
				for (Object obj : hubSelect) {
					int x = hub.getPos(obj);
					if (x >= 0) {
						addRowSelectionInterval(x, x);
					}
				}
			} else {
				int x = hub.getPos();
				if (x >= 0) {
					control.setSelectedRow(x);
				}
			}
			OATable t = OATable.this;
			if (t != null) {
				t.repaint(100);
				JTableHeader th = t.getTableHeader();
				if (th != null) {
					th.repaint(100);
				}
				OATable tx = t.getLeftTable();
				if (tx != null) {
					th = tx.getTableHeader();
					if (th != null) {
						th.repaint(100);
					}
				}
				tx = t.getRightTable();
				if (tx != null) {
					th = tx.getTableHeader();
					if (th != null) {
						th.repaint(100);
					}
				}
			}
		}

		public void fireTableRowsUpdated(int pos1, int pos2) {
			super.fireTableRowsUpdated(pos1, pos2);
		}

		public void fireTableRowsInserted(int firstRow, int lastRow) {
			super.fireTableRowsInserted(firstRow, lastRow);
		}

		public void fireTableRowsDeleted(int firstRow, int lastRow) {
			super.fireTableRowsDeleted(firstRow, lastRow);
		}

		public Class getColumnClass(int c) {
			Method[] ms = ((OATableColumn) columns.elementAt(c)).getMethods(hub);

			int i = ms.length;
			if (i == 0) {
				return hub.getObjectClass();
			}
			Method m = ms[i - 1];
			Class cl = m.getReturnType();

			return OAReflect.getPrimitiveClassWrapper(cl);
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			boolean b = (((OATableColumn) columns.elementAt(columnIndex)).getTableCellEditor() != null);
			return b;
		}

		public void setValueAt(Object obj, int row, int col) {
			// dont do this: if (hub.getActiveObject() != hub.elementAt(row)) hub.setActiveObject(row);
			// do nothing, the editor component is object aware
		}

		boolean loadMoreFlag;
		boolean loadingMoreFlag;

		public Object getValueAt(int row, int col) {
			boolean bx = OAThreadLocalDelegate.addSiblingHelper(siblingHelper);
			try {
				return _getValueAt(row, col);
			} finally {
				if (bx) {
					OAThreadLocalDelegate.removeSiblingHelper(siblingHelper);
				}
			}
		}

		private Object _getValueAt(int row, int col) {
			if (hub == null) {
				return "";
			}
			Object obj;
			int cnt = hub.getSize();

			if (hub.isMoreData()) {
				if (row + 5 >= cnt) {
					if (!loadMoreFlag && !loadingMoreFlag) {
						loadMoreFlag = true;
						loadingMoreFlag = true;

						if (isEditing()) {
							getCellEditor().stopCellEditing(); // instead of
						}
						// "removeEditor();"
						obj = hub.elementAt(row);
						hub.elementAt(row + 5);

						// 20160818 removed, not needed, afterFetchMore will handle this
						//was: hubAdapter.onNewList(null);

						// make sure cell is visible
						int pos = hub.getPos(obj);
						if (pos < 0) {
							pos = 0;
						}
						Rectangle cellRect;
						cellRect = getCellRect(pos, col, true);
						scrollRectToVisible(cellRect);
						repaint(100);

						pos = hub.getPos(hub.getActiveObject());
						if (pos < 0) {
							getSelectionModel().clearSelection();
						} else {
							setRowSelectionInterval(pos, pos);
						}

						loadingMoreFlag = false;

					}
				} else {
					loadMoreFlag = false;
				}
			}

			obj = hub.elementAt(row);
			if (obj == null) {
				return "";
			}

			OATableColumn tc = (OATableColumn) columns.elementAt(col);
			obj = tc.getValue(hub, obj);

			obj = tc.getOATableComponent().getValue(tableLeft, row, col, obj);

			return obj;
		}
		/* not needed, tablecolumn has a similar method to getObject
		public Object getObjectAt(int row, int col) {
		    if (hub == null) return null;
		    Object obj;
		    int cnt = hub.getSize();

		    if (hub.isMoreData()) {
		        if (row + 5 >= cnt) {
		            if (!loadMoreFlag && !loadingMoreFlag) {
		                loadMoreFlag = true;
		                loadingMoreFlag = true;

		                if (isEditing()) getCellEditor().stopCellEditing(); // instead of
		                                                                    // "removeEditor();"
		                obj = hub.elementAt(row);
		                hub.elementAt(row + 5);

		                // make sure cell is visible
		                int pos = hub.getPos(obj);
		                if (pos < 0) pos = 0;
		                Rectangle cellRect;
		                cellRect = getCellRect(pos, col, true);
		                scrollRectToVisible(cellRect);
		                repaint(100);

		                pos = hub.getPos(hub.getActiveObject());
		                if (pos < 0) getSelectionModel().clearSelection();
		                else setRowSelectionInterval(pos, pos);

		                loadingMoreFlag = false;
		            }
		        }
		        else loadMoreFlag = false;
		    }

		    obj = hub.elementAt(row);
		    if (obj == null) return null;

		    OATableColumn tc = (OATableColumn) columns.elementAt(col);
		    obj = tc.getObject(hub, obj);
		    return obj;
		}
		*/

	}

	// ******************************** H A C K S ********************************************
	// ******************************** H A C K S ********************************************
	// ******************************** H A C K S ********************************************
	// Hack: this should to be called within the constructor
	private void addHack() {
		// this is needed so that other components that have called registerKeyboardAction()
		// wont get <enter> key
		registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		// 10/18/99 jdk1.2 The new BasicTableUI ignores the [Enter], but we want to have it setFocus
		registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = getSelectedRow();
				int col = getSelectedColumn();
				editCellAt(row, col, e);
				requestFocus();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), JComponent.WHEN_FOCUSED);
		registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);
	}

	protected AbstractButton cmdDoubleClick;

	/**
	 * Button to perform a doClick() when table clickCount == 2
	 */
	public void setDoubleClickButton(AbstractButton cmd) {
		cmdDoubleClick = cmd;
		OATable t = getLeftTable();
		if (t != null) {
			t.cmdDoubleClick = cmd;
		}
	}

	/**
	 * Button to perform a doClick() when table clickCount == 2
	 */
	public AbstractButton getDoubleClickButton() {
		return cmdDoubleClick;
	}

	private JPopupMenu compPopupMenu;

	@Override
	public void setComponentPopupMenu(JPopupMenu popup) {
		// super.setComponentPopupMenu(popup);
		this.compPopupMenu = popup;
		OATable t = getLeftTable();
		if (t != null) {
			t.compPopupMenu = popup;
		}
	}

	/*
	 * dont include this, since it will then be used by JFC, which wont then use special code in
	 * processMouseEvent to show the popupMenu public JPopupMenu getComponentPopupMenu() { return
	 * this.compPopupMenu; }
	 */
	public JPopupMenu getMyComponentPopupMenu() {
		return this.compPopupMenu;
	}

	// similar to private in jtable
	protected void myClearSelectionAndLeadAnchor() {
		for (int i = 0; i < 3; i++) {
			try {
				selectionModel.setValueIsAdjusting(true);
				columnModel.getSelectionModel().setValueIsAdjusting(true);

				_myClearSelectionAndLeadAnchor();
				break;
			} catch (Exception e) {
				// no-op
			} finally {
				selectionModel.setValueIsAdjusting(false);
				columnModel.getSelectionModel().setValueIsAdjusting(false);
			}
		}
	}

	protected void _myClearSelectionAndLeadAnchor() {
		clearSelection();

		selectionModel.setAnchorSelectionIndex(-1);
		selectionModel.setLeadSelectionIndex(-1);
		columnModel.getSelectionModel().setAnchorSelectionIndex(-1);
		columnModel.getSelectionModel().setLeadSelectionIndex(-1);
	}

	protected void myClearSelectionAndLeadAnchor1() {
		for (int i = 0; i < 3; i++) {
			try {
				selectionModel.setValueIsAdjusting(true);
				columnModel.getSelectionModel().setValueIsAdjusting(true);

				_myClearSelectionAndLeadAnchor();
				break;
			} catch (Exception e) {
				// no-op
			}
		}
	}

	protected void myClearSelectionAndLeadAnchor2() {
		selectionModel.setValueIsAdjusting(false);
		columnModel.getSelectionModel().setValueIsAdjusting(false);
	}

	// 20150424
	private int lastMouseDragRow = -1;
	private boolean bIsMouseDragging;

	/**
	 * Capture double click and call double click button.
	 *
	 * @see #getDoubleClickButton
	 */
	@Override
	protected void processMouseEvent(MouseEvent e) {

		if (e.getID() == MouseEvent.MOUSE_RELEASED) {
			lastMouseDragRow = -1;
			bIsMouseDragging = false;
		}

		// 20150511 popup trigger to work for windows and Mac

		if (compPopupMenu != null) {
			if (e.getID() == MouseEvent.MOUSE_RELEASED || e.getID() == MouseEvent.MOUSE_PRESSED) {
				if (e.isPopupTrigger()) {
					Point pt = e.getPoint();
					int row = rowAtPoint(pt);

					if (!setHubPos(row)) {
						return;
					}
					//was: hub.setPos(row);
					compPopupMenu.show(this, pt.x, pt.y);
				}
			}
		}

		/*
		 * was if (compPopupMenu != null) { if (e.getID() == MouseEvent.MOUSE_RELEASED) { if (
		 * (e.getModifiers() & Event.META_MASK) != 0) { if (e.isPopupTrigger()) { Point pt =
		 * e.getPoint(); int row = rowAtPoint(pt);
		 *
		 * hub.setPos(row); / * ListSelectionModel lsm = getSelectionModel(); if
		 * (!lsm.isSelectedIndex(row)) { getSelectionModel().setSelectionInterval(row, row); } /
		 * compPopupMenu.show(this, pt.x, pt.y); } } } }
		 */

		if (e.getID() == MouseEvent.MOUSE_PRESSED) {
			Point pt = e.getPoint();
			int row = rowAtPoint(pt);
			if (row < 0) { // 20150428
				setHubPos(-1); // 20180905
				//was: hub.setPos(-1);
			}
			if (e.getClickCount() == 2) {
				if (hub.getPos() == row && row >= 0) {
					onDoubleClick();
				}
				return;
			}
		} else if (e.getID() == MouseEvent.MOUSE_EXITED) {
			onMouseOver(-1, -1, e);
		}
		super.processMouseEvent(e);
	}

	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
		if (e.getID() == MouseEvent.MOUSE_MOVED) {
			int row = rowAtPoint(e.getPoint());
			int col = columnAtPoint(e.getPoint());
			onMouseOver(row, col, e);
		} else if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
			bIsMouseDragging = true;
		}

		super.processMouseMotionEvent(e);
	}

	/**
	 * Method that is called whenever mouse click count = 2. Note: the activeObject of the clicked row will be the active object in the
	 * OATables Hub. Default behaviour is to call doubleClick Command, if it is enabled.
	 */
	public void onDoubleClick() {
		OATable t = getRightTable();
		if (t != null) {
			t.onDoubleClick();
			return;
		}

		if (cmdDoubleClick != null) {
			if (cmdDoubleClick.isEnabled()) {
				cmdDoubleClick.doClick();
			}
		}
	}

	public void setEnableEditors(boolean b) {
		bEnableEditors = b;
	}

	public boolean getEnableEditors() {
		return bEnableEditors;
	}

	public void setFilterMasterHub(Hub hubFilterMaster) {
		setMasterFilterHub(hubFilterMaster);
	}

	public Hub getMasterFilterHub() {
		return hubFilterMaster;
	}

	public void setMasterFilterHub(Hub hubFilterMaster) {
		this.hubFilterMaster = hubFilterMaster;

		resetFilterHub();

		if (headerRenderer != null) {
			if (hubFilterMaster == null) {
				headerRenderer.remove(headerRenderer.label);
			} else {
				headerRenderer.add(headerRenderer.label, BorderLayout.CENTER);
			}
			repaint(100);
		}
		if (hubFilter != null) {
			hubFilter.close();
			hubFilter = null;
		}
		if (hubFilterMaster == null) {
			return;
		}

		hubFilter = new HubFilter(hubFilterMaster, getHub(), true) {
			boolean bIsFilterBeingUsed = true;

			@Override
			public void initialize() {
				bIsFilterBeingUsed = false;
				for (OATableColumn tc : getAllTableColumns()) {
					OATableFilterComponent tfc = tc.getFilterComponent();
					if (tfc != null && tfc.isBeingUsed()) {
						bIsFilterBeingUsed = true;
						break;
					}
				}
				super.initialize();
				bIsFilterBeingUsed = true;
			}

			@Override
			public boolean isUsed(Object obj) {
				boolean b = _isUsed(obj);
				return b;
			}

			public boolean _isUsed(Object obj) {
				if (!bIsFilterBeingUsed) {
					return true;
				}
				for (OATableColumn tc : getAllTableColumns()) {
					OATableFilterComponent tfc = tc.getFilterComponent();
					if (tfc != null && !tfc.isUsed(obj)) {
						return false;
					}
				}
				return true;
			}
		};
		oaTableModel.fireTableStructureChanged();
	}

	public void refreshFilter() {
		final HubFilter hf = (hubFilter != null || tableRight == null) ? hubFilter : tableRight.hubFilter;

		if (hf == null) {
			return;
			// final int cnt = aiRefreshFilter.incrementAndGet();
		}

		SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
			Dimension dim;

			@Override
			protected Void doInBackground() throws Exception {
				boolean b = false;
				boolean bx = OAThreadLocalDelegate.addSiblingHelper(siblingHelper);
				try {
					if (control != null) {
						b = true;
						control.aiIgnoreValueChanged.incrementAndGet();
					}
					hf.refresh();
				} finally {
					if (b && control != null) {
						control.aiIgnoreValueChanged.decrementAndGet();
					}
					if (bx) {
						OAThreadLocalDelegate.removeSiblingHelper(siblingHelper);
					}
				}
				oaTableModel.fireTableStructureChanged();
				return null;
			}

			@Override
			protected void done() {
			}
		};
		sw.execute();
	}

	private String confirmMessage;

	public void setConfirmMessage(String msg) {
		confirmMessage = msg;
	}

	public String getConfirmMessage() {
		return confirmMessage;
	}

	// not used
	/*
	protected boolean confirm(int newRow) {
	    String confirmMessage = getConfirmMessage();
	    String confirmTitle = "Confirm";
	    Object obj = null;
	    if (hub != null && hub.getLinkHub() != null) {
	        obj = hub.getLinkHub().getAO();
	    }
	    if (obj instanceof OAObject) {
	        String s = HubLinkDelegate.getLinkToProperty(hub);
	        OAObjectCallback em = OAObjectCallbackDelegate.getConfirmAddEditQuery((OAObject)obj, s, hub.getAt(newRow), confirmMessage, confirmTitle);
	        confirmMessage = em.getConfirmMessage();
	        confirmTitle = em.getConfirmTitle();
	    }
	
	    boolean result = true;
	    if (OAString.isNotEmpty(confirmMessage)) {
	        if (OAString.isEmpty(confirmTitle)) confirmTitle = "Confirmation";
	        int x = JOptionPane.showOptionDialog(OAJFCUtil.getWindow(OATable.this), confirmMessage, confirmTitle, 0, JOptionPane.QUESTION_MESSAGE, null, new String[] { "Yes", "No" }, "Yes");
	        result = (x == 0);
	    }
	    return result;
	}
	*/

	protected boolean bEnableUndo = true;

	public void setEnableUndo(boolean b) {
		bEnableUndo = b;
	}

	public boolean getEnableUndo() {
		return bEnableUndo;
	}

	private final AtomicInteger aiRow = new AtomicInteger();
	private final AtomicBoolean abIgnoreHubPos = new AtomicBoolean();

	protected boolean setHubPos(int row) {
		if (abIgnoreHubPos.get()) {
			return false;
		}
		if (hub == null || hub.getPos() == row) {
			return true;
			// if (!confirm(row)) row = hub.getPos();
		}

		// 20181018 added validation/confirm
		if (hub.getLinkHub(true) != null) {
			Container cont = this.getParent();
			for (; cont != null; cont = cont.getParent()) {
				if (cont instanceof JPopupMenu) {
					((JPopupMenu) cont).setVisible(false);
					break;
				}
			}

			String s = control.isValidHubChangeAO(hub.getAt(row));
			boolean b = true;
			if (OAString.isNotEmpty(s)) {
				b = false;
				JOptionPane.showMessageDialog(this, s, "Warning", JOptionPane.WARNING_MESSAGE);
			} else if (!control.confirmHubChangeAO(hub.getAt(row))) {
				b = false;
			}

			if (!b) {
				abIgnoreHubPos.set(true);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						abIgnoreHubPos.set(false);
						control.afterChangeActiveObject(null);
					}
				});
				return false;
			}
		}

		boolean b = getEnableUndo() && hub != null && hub.getLinkHub(true) != null;
		if (b) {
			OAUndoableEdit ue = OAUndoableEdit.createUndoablePropertyChange(
																			"Change " + HubLinkDelegate.getLinkToProperty(hub),
																			hub.getLinkHub(true).getAO(),
																			HubLinkDelegate.getLinkToProperty(hub),
																			hub.getAO(), hub.getAt(row));

			OAUndoManager.add(ue);
		}
		_setHubPos(row);
		return true;
	}

	protected void _setHubPos(final int row) {
		aiRow.set(row);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		// 20180305 set AO, but not detailHubs
		boolean bx = OAThreadLocalDelegate.addSiblingHelper(siblingHelper);
		try {
			HubAODelegate.setActiveObject(hub, hub.getAt(row), row, true, false, false, false);
		} finally {
			if (bx) {
				OAThreadLocalDelegate.removeSiblingHelper(siblingHelper);
			}
		}

		SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				boolean bx = OAThreadLocalDelegate.addSiblingHelper(siblingHelper);
				try {
					HubDetailDelegate.preloadDetailData(hub, row);
				} finally {
					if (bx) {
						OAThreadLocalDelegate.removeSiblingHelper(siblingHelper);
					}
				}
				return null;
			}

			@Override
			protected void done() {
				if (aiRow.get() == row) {
					boolean bx = OAThreadLocalDelegate.addSiblingHelper(siblingHelper);
					try {
						control._bIsRunningValueChanged = true;
						HubAODelegate.updateDetailHubs(hub);
					} finally {
						control._bIsRunningValueChanged = false;
						if (bx) {
							OAThreadLocalDelegate.removeSiblingHelper(siblingHelper);
						}
					}
					setCursor(Cursor.getDefaultCursor());
				}

				Container cont = getParent();
				for (int i = 0; i < 3 && cont != null; i++) {
					cont.repaint();
					cont = cont.getParent();
				}

			}
		};
		sw.execute();
	}

	/**
	 * Overwritten to set active object in Hub.
	 */
	@Override
	public boolean editCellAt(int row, int column, java.util.EventObject e) {
		// hack: editCellAt() will not hide the current cell editor if the new "column"
		// does not have an editorComponent. If this happens, then it will return
		// false.

		if (hub.getPos() != row) {
			try {
				control._bIsRunningValueChanged = true; // 20131113
				// 20180605
				if (hubSelect != null) {
					if (hubSelect.size() > 1) {
						return false;
					}
					Object obj = hub.getAt(row);
					if (obj != null) {
						if (hubSelect.contains(obj)) { // removing from select hub
							hub.setAO(obj);
							//was:  hub.setAO(null);
							return false;
						}
					}
				}

				// 20171230
				if (!setHubPos(row)) {
					return false;
					//was: hub.setPos(row);
				}
			} finally {
				control._bIsRunningValueChanged = false;
			}
		}

		// 20151225 checkbox selection column does not need to have cellEdit, so that drag many can work
		if (chkSelection == null && tableRight != null) {
			chkSelection = tableRight.chkSelection;
		}
		if (chkSelection != null) {
			int addColumns = 0;
			if (tableLeft != null) {
				addColumns = tableLeft.getColumnCount();
			}
			if ((column + addColumns) == getColumnIndex(chkSelection)) {
				return false;
			}
		}

		if (hubSelect != null) {
			/* 20151225
			int x = hubSelect.getSize();
			if (x > 0) {
			    if (x > 1) return false;
			    if (hubSelect.getAt(0) != hub.getAO()) return false;
			}
			*/
		} else {
			try {
				// Note: 20100529 calling setRowSelectionInterval(row,row), which will call
				// valueChanged(evt)
				// will have e.getValueIsAdjusting() = true
				// was: setRowSelectionInterval(row,row); // this will not call setActiveObject(), since
				// e.getValueIsAdjusting() will be true
			} catch (RuntimeException ex) {
			}

			if (hub.getPos() != row) {
				return false; // cant change activeObject
			}
		}

		if (!bEnableEditors) {
			return false;
		}

		boolean b = super.editCellAt(row, column, e);

		// hack: if editCellAt() returned false and the old column had an editor, then
		// we must stop it now. Calling stopCellEditor() has no side effects, other
		// then removing the editorComponent

		if (!b && isEditing()) {
			getCellEditor().stopCellEditing();
		} else {
			if (getCellEditor() instanceof OATableCellEditor) {
				((OATableCellEditor) getCellEditor()).startCellEditing(e);
			}
			requestFocus(); // make sure component gets input
		}

		return b;
	}

	// hack: called by OATableCellEditor because JTable.removeEditor() sets isEditing to false
	// after it removes the component from the Table
	boolean checkFocusFlag = true;

	public void setCheckFocus(boolean b) {
		checkFocusFlag = b;
	}

	/**
	 * Overwritten to resume edit mode when focus is regained.
	 */
	@Override
	public void requestFocus() {
		// hack: set focus to editorComponent if it is showing.
		if (checkFocusFlag && isEditing() && bEnableEditors) {
			getEditorComponent().requestFocus();
		} else {
			super.requestFocus();
		}
	}

	// ================== 2006/12/29 :) =============================

	private OATableColumn popupTableColumn;
	private JMenuItem miAdd, miFind;
	private JMenu menuAddColumn, menuLoad, menuSave;
	private JCheckBoxMenuItem[] menuCheckBoxes;
	private JRadioButtonMenuItem[] menuSaveRadios;
	private JRadioButtonMenuItem[] menuLoadRadios;
	private OAProperties columnProperties;
	private String columnPrefix;
	private boolean bColumnPropertiesLoaded;
	private OATableColumn[][] tcColumnSetup = new OATableColumn[3][];
	private int[][] intColumnSetup = new int[3][];

	/**
	 * The properties used to store the settings.
	 *
	 * @param props
	 * @param name  prefix used for storing properties, unique name for this table.
	 */
	public void setColumnProperties(OAProperties props, String name) {
		columnProperties = props;
		columnPrefix = name;
		bColumnPropertiesLoaded = false;
	}

	protected void loadColumnProperties() {
		if (bColumnPropertiesLoaded || columnProperties == null || columnPrefix == null) {
			return;
		}
		bColumnPropertiesLoaded = true;
		for (int i = 0; i < 3; i++) {
			String line1 = columnProperties.getProperty(columnPrefix + ".setup" + (i + 1) + ".columns");
			if (line1 == null) {
				continue;
			}
			String line2 = columnProperties.getProperty(columnPrefix + ".setup" + (i + 1) + ".widths");
			if (line2 == null) {
				continue;
			}
			int x = OAString.dcount(line1, ",");

			tcColumnSetup[i] = new OATableColumn[x];
			intColumnSetup[i] = new int[x];

			for (int j = 1; j <= x; j++) {
				String w1 = OAString.field(line1, ',', j);
				if (w1 == null) {
					break;
				}
				w1 = w1.trim();
				String w2 = OAString.field(line2, ',', j);
				if (w2 == null) {
					continue;
				}
				int w = OAConv.toInt(w2);
				if (w <= 0) {
					continue;
				}
				intColumnSetup[i][j - 1] = w;

				int kx = columns.size();
				for (int k = 0; k < kx; k++) {
					OATableColumn tc = (OATableColumn) columns.elementAt(k);
					if (!w1.equalsIgnoreCase(tc.origPath)) {
						continue;
					}
					tcColumnSetup[i][j - 1] = tc;
					break;
				}
			}
		}
	}

	protected void saveColumnProperties() {
		loadColumnProperties();
		if (!bColumnPropertiesLoaded) {
			return;
		}
		for (int i = 0; i < 3; i++) {
			if (tcColumnSetup[i] == null) {
				continue;
			}
			if (intColumnSetup[i] == null) {
				continue;
			}
			String line1 = "";
			String line2 = "";
			for (int j = 0; j < tcColumnSetup[i].length; j++) {
				if (j > 0) {
					line1 += ",";
					line2 += ",";
				}
				if (tcColumnSetup[i][j] != null) {
					line1 += tcColumnSetup[i][j].path;
					line2 += intColumnSetup[i][j];
				}
			}
			columnProperties.put(columnPrefix + ".setup" + (i + 1) + ".columns", line1);
			columnProperties.put(columnPrefix + ".setup" + (i + 1) + ".widths", line2);
		}
		columnProperties.save();
	}

	protected void displayPopupMenu(OATableColumn tc, Point pt) {
		if (columnProperties == null) {
			return;
		}
		getPopupMenu();
		loadColumnProperties();

		// list of columns
		int x = columns.size();
		menuCheckBoxes = new JCheckBoxMenuItem[x];
		menuAddColumn.removeAll();
		for (int i = 0; i < x; i++) {
			final OATableColumn tcx = (OATableColumn) columns.elementAt(i);
			menuCheckBoxes[i] = new JCheckBoxMenuItem(tcx.tc.getHeaderValue() + "", tcx.bVisible);
			menuCheckBoxes[i].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					JCheckBoxMenuItem chk = (JCheckBoxMenuItem) e.getSource();
					if (chk.isSelected()) {
						onAddColumn(tcx);
					} else {
						onRemoveColumn(tcx);
					}
				}
			});
			OATableComponent tce = tcx.getOATableComponent();
			if (tce instanceof JComponent) {
				String s = ((JComponent) tce).getToolTipText();
				if (s != null && s.trim().length() > 0) {
					menuCheckBoxes[i].setToolTipText(s);
				}
			}
			menuAddColumn.add(menuCheckBoxes[i]);
		}

		// list of Save As
		if (!bColumnPropertiesLoaded) {
			menuSave.setVisible(false);
		} else {
			menuSave.setVisible(true);
			menuSaveRadios = new JRadioButtonMenuItem[3];
			menuSave.removeAll();
			ButtonGroup grp = new ButtonGroup();
			for (int i = 0; i < 3; i++) {
				menuSaveRadios[i] = new JRadioButtonMenuItem("Setup #" + (i + 1), false);
				grp.add(menuSaveRadios[i]);
				menuSaveRadios[i].addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						JRadioButtonMenuItem rad = (JRadioButtonMenuItem) e.getSource();
						if (rad.isSelected()) {
							for (int i = 0; i < menuSaveRadios.length; i++) {
								if (menuSaveRadios[i] == rad) {
									saveColumnSetup(i);
									break;
								}
							}
						}
					}
				});
				menuSave.add(menuSaveRadios[i]);
			}
		}

		// list of Load
		menuLoadRadios = new JRadioButtonMenuItem[4];
		menuLoad.removeAll();
		ButtonGroup grp = new ButtonGroup();
		for (int i = 0; i < 4; i++) {
			if (i == 0) {
				menuLoadRadios[i] = new JRadioButtonMenuItem("Default", false);
			} else {
				menuLoadRadios[i] = new JRadioButtonMenuItem("Setup #" + i, false);
				menuLoadRadios[i].setEnabled(tcColumnSetup[i - 1] != null);
			}
			grp.add(menuLoadRadios[i]);
			menuLoadRadios[i].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					JRadioButtonMenuItem rad = (JRadioButtonMenuItem) e.getSource();
					if (rad.isSelected()) {
						if (rad == menuLoadRadios[0]) {
							onLoadDefault();
						} else {
							for (int i = 0; i < menuLoadRadios.length; i++) {
								if (menuLoadRadios[i] == rad) {
									loadColumnSetup(i - 1);
									break;
								}
							}
						}
					}
				}
			});
			menuLoad.add(menuLoadRadios[i]);
			if (!bColumnPropertiesLoaded) {
				break;
			}
		}

		this.popupTableColumn = tc;
		getPopupMenu().show(OATable.this.getTableHeader(), pt.x, pt.y);
	}

	protected void saveColumnSetup(int pos) {
		if (pos > 2 || pos < 0) {
			return;
		}

		int x = columnModel.getColumnCount();
		tcColumnSetup[pos] = new OATableColumn[x];
		intColumnSetup[pos] = new int[x];

		OATableColumn[] tcs = new OATableColumn[x];
		for (int i = 0; i < x; i++) {
			TableColumn tc = columnModel.getColumn(i);
			int x2 = columns.size();
			for (int i2 = 0; i2 < x2; i2++) {
				OATableColumn tcx = (OATableColumn) columns.elementAt(i2);
				if (tcx.tc == tc) {
					tcx.currentWidth = tcx.tc.getWidth();
					tcColumnSetup[pos][i] = tcx;
					intColumnSetup[pos][i] = tcx.currentWidth;
				}
			}
		}
		saveColumnProperties();
	}

	protected void loadColumnSetup(int pos) {
		if (pos > 2 || pos < 0) {
			return;
		}
		if (tcColumnSetup[pos] == null) {
			return;
		}
		for (; columnModel.getColumnCount() > 0;) {
			columnModel.removeColumn(columnModel.getColumn(0));
		}
		int x = columns.size();
		for (int i = 0; i < x; i++) {
			OATableColumn tcx = (OATableColumn) columns.elementAt(i);
			tcx.bVisible = false;
		}
		x = tcColumnSetup[pos].length;
		for (int i = 0; i < x; i++) {
			OATableColumn tcx = tcColumnSetup[pos][i];
			tcx.bVisible = true;
			tcx.tc.setWidth(intColumnSetup[pos][i]);
			columnModel.addColumn(tcx.tc);
		}
	}

	protected void onAddColumn(OATableColumn tc) {
		if (tc != null) {
			tc.bVisible = true;

			int x = columns.size();
			int pos = 0;
			columnModel.addColumn(tc.tc);
			for (int i = 0; i < x; i++) {
				OATableColumn tcx = (OATableColumn) columns.elementAt(i);
				if (tcx == tc) {
					int cnt = columnModel.getColumnCount();
					if (pos != cnt - 1) {
						columnModel.moveColumn(cnt - 1, pos);
					}
					return;
				}
				if (tcx.bVisible) {
					pos++;
				}
			}
		}
	}

	protected void onRemoveColumn(OATableColumn tc) {
		if (tc != null) {
			tc.bVisible = false;
			columnModel.removeColumn(tc.tc);
		}
	}

	protected void onLoadDefault() {
		for (; columnModel.getColumnCount() > 0;) {
			columnModel.removeColumn(columnModel.getColumn(0));
		}
		int x = columns.size();
		for (int i = 0; i < x; i++) {
			OATableColumn tcx = (OATableColumn) columns.elementAt(i);
			tcx.bVisible = tcx.bDefault;
			tcx.tc.setWidth(tcx.defaultWidth);
			if (tcx.bVisible) {
				columnModel.addColumn(tcx.tc);
			}
		}
	}

	protected void onFind() {
	}

	protected JPopupMenu getPopupMenu() {
		if (popupMenu != null) {
			return popupMenu;
		}
		popupMenu = new JPopupMenu("Options");
		JMenu menu;

		menuAddColumn = new JMenu("Select Columns");
		popupMenu.add(menuAddColumn);
		/*
		 * miFind = new JMenuItem("Find ..."); miFind.addActionListener(new ActionListener() { public
		 * void actionPerformed(ActionEvent e) { onFind(); } }); popupMenu.add(miFind);
		 */
		popupMenu.addSeparator();

		menuLoad = new JMenu("Load");
		popupMenu.add(menuLoad);

		menuSave = new JMenu("Save As");
		popupMenu.add(menuSave);

		return popupMenu;
	}

	public void setSortColumn(OATableComponent oaComp, boolean bAsc, int pos) {
		OATableColumn tc;
		for (int i = 0; i < columns.size(); i++) {
			tc = (OATableColumn) columns.elementAt(i);
			if (tc.getOATableComponent() == oaComp) {
				tc.sortOrder = pos;
				tc.sortDesc = !bAsc;
				break;
			}
		}
		getTableHeader().repaint(100);
	}

	public void removeSort() {
		OATableColumn tc;
		for (int i = 0; i < columns.size(); i++) {
			tc = (OATableColumn) columns.elementAt(i);
			tc.sortOrder = 0;
		}
		getTableHeader().repaint(100);
	}

	public int getDisplayedColumnCount() {
		return columnModel.getColumnCount();
	}

	public OATableComponent getDisplayedColumnComponent(int pos) {
		int x = columnModel.getColumnCount();
		if (pos >= x) {
			return null;
		}
		TableColumn tc = columnModel.getColumn(pos);
		x = columns.size();
		for (int i = 0; i < x; i++) {
			OATableColumn tcx = (OATableColumn) columns.elementAt(i);
			if (tcx.tc == tc) {
				return tcx.getOATableComponent();
			}
		}
		return null;
	}

	public int getDisplayedColumnWidth(int pos) {
		int x = columnModel.getColumnCount();
		if (pos >= x) {
			return 0;
		}
		TableColumn tc = columnModel.getColumn(pos);
		return tc.getWidth();
	}

	protected String getColumnHeaderToolTipText(OATableColumn tc, Point pt) {
		String s = null;

		if (pt != null) {
			String ttt = tc.getToolTipText();
			if (tc.getOATableComponent() == chkSelection) {
				if (OAString.isNotEmpty(ttt)) {
					return ttt;
				}
				return "selected rows";
			}
			if (tableRight != null && tc.getOATableComponent() == tableRight.chkSelection) {
				if (OAString.isNotEmpty(ttt)) {
					return ttt;
				}
				return "selected rows";
			}
			if (tc == tcCount) {
				if (pt.y > headerRenderer.buttonHeight) {
					if (OAString.isNotEmpty(ttt)) {
						return ttt;
					}
					return "reset filters";
				}
			}
			if (tableRight != null && tc == tableRight.tcCount) {
				if (pt.y > tableRight.headerRenderer.buttonHeight) {
					if (OAString.isNotEmpty(ttt)) {
						return ttt;
					}
					return "reset filters";
				}
			}
		}

		if (tc != null && tc.getOATableComponent() instanceof JComponent) {
			OATableComponent tcomp = tc.getOATableComponent();
			if (tcomp instanceof OAJfcComponent) {
				OAJfcComponent jc = (OAJfcComponent) tcomp;
				jc.getController().getToolTipText(null, s);
			}

			String s2 = tc.getToolTipText();
			if (OAString.isNotEmpty(s2)) {
				s = s2;
			}

			if (tc.getCustomizer() != null) {
				s = tc.getCustomizer().getToolTipText(null, -1, s);
			}
			s = getColumnHeaderToolTipText(tc.getOATableComponent(), s);
			if (s == null || s.length() == 0) {
				if (tc.tc.getHeaderValue() != null) {
					s = tc.tc.getHeaderValue().toString();
				}
			}
		}

		if (s == null) {
			s = "column";
		}
		if (tc != null && tc.compFilter != null) {
			if (headerRenderer.buttonHeight > 0 && pt.y > headerRenderer.buttonHeight) {
				s = "enter value to filter";
			} else if (tableRight != null && pt.y > tableRight.headerRenderer.buttonHeight) {
				s = "enter value to filter";
			}
		} else {
			if (headerRenderer.buttonHeight > 0 && pt.y > headerRenderer.buttonHeight) {
				if (s.indexOf("html") < 0) {
					s = "no filter for " + s;
				}
			} else if (tableRight != null && pt.y > tableRight.headerRenderer.buttonHeight) {
				if (s.indexOf("html") < 0) {
					s = "no filter for " + s;
				}
			}
		}

		return s;
	}

	protected String getColumnHeaderToolTipText(OATableComponent comp, String tt) {
		return tt;
	}

	protected void onHeadingRightClick(OATableColumn tc, Point pt) {
		if (columnProperties != null) {
			displayPopupMenu(tc, pt);
		}
	}

	// 20101229 - set by OATableScrollPane
	protected OATable tableLeft;
	protected OATable tableRight;

	protected void setLeftTable(OATable table) {
		this.tableLeft = table;
	}

	public OATable getLeftTable() {
		return tableLeft;
	}

	protected void setRightTable(OATable table) {
		this.tableRight = table;

		setSelectionModel(table.getSelectionModel());
		setSelectHub(table.hubSelect, control.getAllowRemovingFromSelectHub());
		this.chkSelection = table.chkSelection;
		this.bMultiSelectControlKey = table.bMultiSelectControlKey;

		setAllowDrag(table.getAllowDrag());
		setAllowDrop(table.getAllowDrop());
		setAllowSorting(table.getAllowSorting());
		setDoubleClickButton(table.getDoubleClickButton());

		setComponentPopupMenu(table.getMyComponentPopupMenu());

		setIntercellSpacing(table.getIntercellSpacing());
		setRowHeight(table.getRowHeight());
		setShowChanges(table.getShowChanges());

		/*
		 * getTableHeader().setResizingAllowed(false); getTableHeader().setReorderingAllowed(false);
		 */
	}

	public OATable getRightTable() {
		return tableRight;
	}

	// includes joinedTable from OATableScrollPane
	protected OATableColumn[] getAllTableColumns() {

		int tot = columns.size();
		if (tableLeft != null) {
			tot += tableLeft.columns.size();
		}
		if (tableRight != null) {
			tot += tableRight.columns.size();
		}

		OATableColumn[] allColumns = new OATableColumn[tot];
		int pos = 0;
		for (int z = 0; z < 2; z++) {
			OATable t;
			if (z == 0) {
				if (this.tableLeft != null) {
					t = tableLeft;
				} else {
					t = this;
				}
			} else {
				if (this.tableRight != null) {
					t = tableRight;
				} else {
					t = this;
				}
			}
			for (int i = 0; i < t.columns.size(); i++) {
				OATableColumn col = (OATableColumn) t.columns.elementAt(i);
				allColumns[pos++] = col;
			}
			if (tableLeft == null && tableRight == null) {
				break;
			}
		}
		return allColumns;
	}

	protected void onHeadingClick(OATableColumn tc, MouseEvent e, Point pt) {
		if (!bAllowSorting) {
			return;
		}
		if (tc == null) {
			return;
		}
		if (!tc.getAllowSorting()) {
			return;
		}
		tc.setupTableColumn();

		// 20101229 setup to be able to remove the sort order on a column

		OATableColumn[] allColumns = getAllTableColumns();

		if (e.isControlDown() || e.isShiftDown()) {
			if (tc.sortOrder == 0) {
				int max = 0;
				for (int i = 0; i < allColumns.length; i++) {
					OATableColumn col = allColumns[i];
					tc.sortOrder = Math.max(tc.sortOrder, col.sortOrder);
				}
				tc.sortOrder++;
			} else {
				boolean bTurnOff = true;
				for (int i = 0; i < allColumns.length; i++) {
					OATableColumn col = allColumns[i];
					if (col != tc && col.sortOrder > 0) {
						bTurnOff = tc.sortDesc;
						break;
					}
				}
				if (bTurnOff) {
					tc.sortOrder = 0;
					tc.sortDesc = false;
				} else {
					tc.sortDesc = !tc.sortDesc;
				}
			}
		} else {
			boolean bTurnOff = false;
			if (tc.sortOrder > 0) {
				boolean b = false;
				for (int i = 0; !b && i < allColumns.length; i++) {
					OATableColumn col = allColumns[i];
					if (col != tc & col.sortOrder > 0) {
						b = true;
					}
				}
				if (!b) {
					if (tc.sortDesc) {
						bTurnOff = true;
					} else {
						tc.sortDesc = !tc.sortDesc;
					}
				}
			}
			tc.sortOrder = 1;
			for (int i = 0; i < allColumns.length; i++) {
				OATableColumn col = allColumns[i];
				if (col != tc || bTurnOff) {
					col.sortOrder = 0;
					col.sortDesc = false;
				}
			}
		}
		OATable.this.performSort();
		OATable.this.getTableHeader().repaint(100);
		if (OATable.this.tableLeft != null) {
			OATable.this.tableLeft.getTableHeader().repaint(100);
		} else if (OATable.this.tableRight != null) {
			OATable.this.tableRight.getTableHeader().repaint(100);
		}
	}

	protected void onHeadingMouseReleased(MouseEvent e, Point pt) {
		if (pt != null && !e.isPopupTrigger()) {
			Rectangle rec = new Rectangle(pt.x - 3, pt.y - 3, 6, 6);
			Point pt2 = e.getPoint();
			if (!rec.contains(pt2)) {
				return;
				// if (pt2 != null && (pt.x != pt2.x || pt.y != pt2.y)) return;
			}
		}

		int column = columnModel.getColumnIndexAtX(e.getX());
		if (column < 0) {
			return;
		}
		int myColumn = columnModel.getColumn(column).getModelIndex();
		OATableColumn tc = null;
		if (myColumn >= 0 && myColumn < columns.size()) {
			tc = (OATableColumn) columns.elementAt(myColumn);
		}
		if (tc == null) {
			return;
		}

		PanelHeaderRenderer bhr = headerRenderer;
		if (bhr == null || tableRight != null) {
			bhr = tableRight.headerRenderer;
		}
		if ((hubFilter != null || (tableRight != null && tableRight.hubFilter != null)) && bhr != null
				&& (bhr.buttonHeight > 0 || bhr.getPreferredSize() != null) && pt.y > bhr.buttonHeight) {
			// header editor

			// stop table editor
			TableCellEditor ed = getCellEditor();
			if (ed != null) {
				ed.stopCellEditing();
			}
			if (tableRight != null) {
				ed = tableRight.getCellEditor();
				if (ed != null) {
					ed.stopCellEditing();
				}
			}
			if (tableLeft != null) {
				ed = tableLeft.getCellEditor();
				if (ed != null) {
					ed.stopCellEditing();
				}
			}

			if (tc == tcCount || (tableRight != null && tc == tableRight.tcCount)) {
				resetFilters();
				return;
			}

			// 20150810
			if (tc.getOATableComponent() == this.chkSelection) {
				if (isAnySelected()) {
					// could be filtered
					if (hubFilter != null) {
						boolean b = true;
						for (Object obj : getSelectHub()) {
							if (!getHub().contains(obj)) {
								b = false;
								break;
							}
						}
						if (b) {
							getSelectHub().removeAll();
							getSelectionModel().clearSelection();
						} else {
							for (Object obj : getSelectHub()) {
								if (getHub().contains(obj)) {
									getSelectHub().remove(obj);
								}
							}
							control.rebuildListSelectionModel();
						}
					} else {
						getSelectHub().removeAll();
						getSelectionModel().clearSelection();
					}
				} else {
					Hub h = getHub();
					getSelectionModel().setSelectionInterval(0, h.getSize() - 1);
				}
				return;
			}

			if (headerRenderer != null) {
				headerRenderer.setupEditor(column);
			}
			return;
		}

		if ((e.getModifiers() & Event.META_MASK) != 0) {
			if (!e.isPopupTrigger()) {
				return;
			}
		}
		if (e.isPopupTrigger()) {
			if (tc != null) {
				onHeadingRightClick(tc, pt);
			}
			return;
		}
		if (tc != null) {
			onHeadingClick(tc, e, pt);
		}
	}

	// END END END END END END ===== 2006/12/29 CONSTRUCTION ZONE :) ===== END END END END END END

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		configureEnclosingScrollPane();
	}

	// 20101031 improve the look when table does not take up all of viewport
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				scrollPane.setBackground(getBackground());

				JTableHeader th = getTableHeader();
				if (th != null) {
					th.setBackground(getBackground());
				}

				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				viewport.setBackground(getBackground());

				/*
				 * JPanel pan = new JPanel(new BorderLayout()); pan.add(getTableHeader(),
				 * BorderLayout.WEST);
				 *
				 * JButton cmd = new JButton(""); pan.add(cmd, BorderLayout.CENTER);
				 *
				 * pan.setBackground(getBackground()); scrollPane.setColumnHeaderView(pan);
				 */
			}
		}
	}

	// 20151022 called by removeNotify. Fixed problem when using table in popup, and it calls calls removeNotity
	//   which removes the columnHeaderView, and it's not used for getting the preferred size
	//   this is copied from JTable
	protected void unconfigureEnclosingScrollPane() {
		// this replaces
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				// Make certain we are the viewPort's view and not, for
				// example, the rowHeaderView of the scrollPane -
				// an implementor of fixed columns might do this.
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				// 20151022 removed this line
				// scrollPane.setColumnHeaderView(null);
				// remove ScrollPane corner if one was added by the LAF
				Component corner = scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER);
				if (corner instanceof UIResource) {
					scrollPane.setCorner(	JScrollPane.UPPER_TRAILING_CORNER,
											null);
				}
			}
		}
	}

	private boolean bIsProcessKeyBinding;

	// 20101229 add this to be able to left/right arrow between joined tables
	@Override
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {

		Hub hub = getHub();
		final int rowBefore = (hub == null) ? 0 : hub.getPos();
		int code = e.getKeyCode();
		int colBefore = getSelectedColumn();

		bIsProcessKeyBinding = true;
		boolean bWasUsed = super.processKeyBinding(ks, e, condition, pressed);
		bIsProcessKeyBinding = false;

		if (!bWasUsed) {
			return false; // only want to know when a key was actually used
		}
		final int rowAfter = (hub == null) ? 0 : hub.getPos();

		if (tableLeft == null && tableRight == null) {
		} else {
			if (colBefore == getSelectedColumn()) { // column change was not able to be made
				if (code == KeyEvent.VK_LEFT) {
					if (colBefore != 0 || tableLeft == null) {
						return true;
					}
					// goto left table, last column
					int col = tableLeft.getColumnCount() - 1;
					tableLeft.setColumnSelectionInterval(col, col);
					int row = this.getSelectedRow();
					tableLeft.setRowSelectionInterval(row, row);
					tableLeft.requestFocus();
				} else if (code == KeyEvent.VK_RIGHT) {
					if (tableRight == null) {
						return true;
					}
					if (colBefore != this.getColumnCount() - 1) {
						return true;
					}
					// goto first column in right table
					tableRight.setColumnSelectionInterval(0, 0);
					int row = this.getSelectedRow();
					tableRight.setRowSelectionInterval(row, row);
					tableRight.requestFocus();
				}
			}
		}

		// 20150512
		if (code == KeyEvent.VK_UP) {
			if (rowAfter == rowBefore && rowBefore != 0) {
				if (hub != null) {
					int pos = hub.getPos() - 1;
					// if (pos >= 0) hub.setPos(pos);
				}
			}
		} else if (code == KeyEvent.VK_DOWN) {
			if (rowAfter == rowBefore) {
				if (hub != null) {
					int pos = hub.getPos() + 1;
					// if (pos < hub.getSize()) hub.setPos(pos);
				}
			}
		} else if (code == KeyEvent.VK_HOME) {
			if ((e.getModifiers() & Event.CTRL_MASK) != 0) {
				if (rowAfter == rowBefore) {
					// if (hub != null) hub.setPos(0);
				}
			} else {
				if (tableLeft != null) {
					tableLeft.setColumnSelectionInterval(0, 0);
					int row = this.getSelectedRow();
					tableLeft.setRowSelectionInterval(row, row);
					tableLeft.requestFocus();
				}
			}
		} else if (code == KeyEvent.VK_END) {
			if ((e.getModifiers() & Event.CTRL_MASK) != 0) {
				if (rowAfter == rowBefore) {
					if (hub != null) {
						int pos = hub.getSize() - 1;
						// if (pos >= 0) hub.setPos(pos);
					}
				}
			} else {
				if (tableRight != null) {
					int col = tableRight.getColumnCount() - 1;
					tableRight.setColumnSelectionInterval(col, col);
					int row = this.getSelectedRow();
					tableRight.setRowSelectionInterval(row, row);
					tableRight.requestFocus();
				}
			}
		}

		return true;
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		// 20180830
		OATableColumn tc = (OATableColumn) columns.elementAt(column);
		if (!tc.getOATableComponent().allowEdit()) {
			return null;
		}

		return super.getCellEditor(row, column);
	}

	// 20150426 adding features from CustomTable
	private int tttCnt;

	@Override
	public void setToolTipText(String text) {
		if (text == null) {
			text = "";
		}
		if (text.length() > 0) {
			text += ((tttCnt++) % 2 == 0) ? "" : " "; // make unique
		}
		super.setToolTipText(text);
	}

	protected int mouseOverRow = -1, mouseOverColumn;
	private Rectangle rectMouseOver;

	public void onMouseOver(int row, int column, MouseEvent evt) {
		super.setToolTipText("");
		if (mouseOverRow == row && mouseOverColumn == column) {
			return;
		}
		mouseOverRow = row;
		mouseOverColumn = column;
		repaint(50);
		/* 20160203 change to repaint, since treetable was not refreshing correctly on treenode column and mouseOver of the selected row
		if (rectMouseOver != null) {
		    repaint(rectMouseOver);
		    OATable tx = getLeftTable();
		    if (tx != null) {
		        tx.repaint();
		    }
		    tx = getRightTable();
		    if (tx != null) {
		        tx.repaint();
		    }
		}
		if (row < 0) rectMouseOver = null;
		else {
		    rectMouseOver = getCellRect(row, column, true);
		    repaint(rectMouseOver);
		}
		*/
	}

	private JLabel lblDummy;
	private Border borderDummy;

	//todo: add more doc here
	/**
	 * Called by getCellRender to customize the renderer.
	 *
	 * @param comp
	 * @param table
	 * @param value
	 * @param isSelected
	 * @param hasFocus
	 * @param row
	 * @param column
	 * @param wasChanged
	 * @param wasMouseOver
	 * @return
	 * @see #customizeRenderer(JLabel, JTable, Object, boolean, boolean, int, int, boolean, boolean) which is called by this method after it
	 *      sets the defaults.
	 */
	public Component getRenderer(Component comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column,
			boolean wasChanged, boolean wasMouseOver) {
		try {
			boolean bx = OAThreadLocalDelegate.addSiblingHelper(siblingHelper);
			try {
				comp = _getRenderer(comp, table, value, isSelected, hasFocus, row, column, wasChanged, wasMouseOver);
			} finally {
				if (bx) {
					OAThreadLocalDelegate.removeSiblingHelper(siblingHelper);
				}
			}
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Error in table rendering", e);
		}
		return comp;
	}

	private static Icon iconFake;

	protected Component _getRenderer(Component comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column,
			boolean wasChanged, boolean wasMouseOver) {
		JLabel lbl = null;
		// 1of3: set default settings
		if (!(comp instanceof JLabel)) {
			if (lblDummy == null) {
				lblDummy = new JLabel();
			}
			lbl = lblDummy;
			/*
			lbl.setBackground(Color.cyan);
			lbl.setForeground(Color.cyan);
			if (borderDummy == null) borderDummy = new LineBorder(Color.red);
			lbl.setBorder(borderDummy);
			*/
			if (iconFake == null) {
				iconFake = new OAColorIcon(Color.white, 0, 0);
			}
			lbl.setIcon(iconFake);
			lbl.setText("xqz");
			lbl.setBackground(comp.getBackground());
		} else {
			lbl = (JLabel) comp;
		}

		if (hub.getAt(row) != null) {
			if (!isSelected && !hasFocus) {
				lbl.setForeground(Color.BLACK);
				if (row % 2 == 0) {
					lbl.setBackground(COLOR_Even);
				} else {
					lbl.setBackground(COLOR_Odd);
				}
			}
		}

		if (wasChanged) {
			lbl.setForeground(COLOR_Change_Foreground);
			lbl.setBackground(COLOR_Change_Background);
			lbl.setBorder(BORDER_Change);
			if (isSelected) {
				// lbl.setBorder(??); // use selected background color
			}
		} else {
			if (hasFocus && row >= 0 && hub.getPos() == row) {
				lbl.setForeground(Color.white);
				lbl.setBackground(COLOR_Focus);
			}

			if (wasMouseOver) {
				lbl.setForeground(Color.white);
				lbl.setBackground(COLOR_MouseOver);
				lbl.setBorder(BORDER_Focus);
			} else {
				lbl.setBorder(null);
			}
		}

		// have the component customize
		OATableComponent oacomp = null;
		int x = (tableLeft == null) ? 0 : tableLeft.columns.size();

		final OATableColumn tc;
		if (tableLeft != null && column < tableLeft.columns.size()) {
			tc = (OATableColumn) tableLeft.columns.elementAt(column);
			oacomp = tc.getOATableComponent();
		} else if (column >= 0 && (column - x) < columns.size()) {
			tc = (OATableColumn) columns.elementAt(column - x);
			oacomp = tc.getOATableComponent();
		} else {
			tc = null;
		}

		// 1of4: have component update itself
		// 20181004
		final Object objx = getObjectAt(row, column);
		if (oacomp instanceof OAJfcComponent) {
			if (oacomp instanceof JLabel) {
				// 20220429
				((OAJfcComponent) oacomp).getController().update((JComponent) oacomp, objx, false); // was: lbl
				//was:
				// ((OAJfcComponent) oacomp).getController().update(lbl, objx, false); // was: lbl
			} else {
				((OAJfcComponent) oacomp).getController().update((JComponent) oacomp, objx, false); // was: lbl
			}
		}

		// 2of4: allow component to customize
		if (oacomp != null) {
			oacomp.customizeTableRenderer(lbl, table, value, isSelected, hasFocus, row, column, wasChanged, wasMouseOver);
		}

		// 3of4 allow tc to customize
		if (tc != null) {
			OATableColumnCustomizer tcc = tc.getCustomizer();
			if (tcc != null) {
				// Object obj = getObjectAt(row, column);
				tcc.customizeRenderer(lbl, value, isSelected, hasFocus, row, column, wasChanged, wasMouseOver);
			}
		}

		// 4of4: allow App to customize
		customizeRenderer(lbl, value, isSelected, hasFocus, row, column, wasChanged, wasMouseOver);

		if (lbl == lblDummy && comp != null) {
			/*
			Color c = lblDummy.getBackground();
			if (!Color.cyan.equals(c)) comp.setBackground(c);
			c = lblDummy.getForeground();
			if (!Color.cyan.equals(c)) comp.setForeground(c);
			if (lbl.getBorder() != borderDummy) {
			    if (comp instanceof JComponent) {
			        ((JComponent) comp).setBorder(lbl.getBorder());
			    }
			}
			*/
			// custom code wants to change the rendering comp
			if (!"xqz".equals(lblDummy.getText()) || lbl.getIcon() != iconFake) {
				if ("xqz".equals(lblDummy.getText())) {
					lbl.setText("");
				}
				comp = lbl;
			}
		}
		return comp;
	}

	/**
	 * This is called by getRenderer(..) after the default settings have been set.
	 */
	public void customizeRenderer(JLabel lbl, Object value, boolean isSelected, boolean hasFocus, int row, int column, boolean wasChanged,
			boolean wasMouseOver) {
		customizeRenderer(lbl, this, value, isSelected, hasFocus, row, column, wasChanged, wasMouseOver);
		// to be overwritten
	}

	public void customizeRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column,
			boolean wasChanged, boolean wasMouseOver) {
		// to be overwritten
	}
}

/**
 * Class used to bind Table to a Hub.
 */
class TableController extends OAJfcController implements ListSelectionListener {
	OATable table;
	private HubListenerAdapter hlSelect;

	final AtomicInteger aiIgnoreValueChanged = new AtomicInteger(); // used to ignore calls to valueChanged(...)
	volatile boolean _bIsRunningValueChanged; // flag set when valueChanged is running

	public TableController(Hub hub, OATable table) {
		super(hub, null, null, table, HubChangeListener.Type.HubValid, ((hub != null) ? (hub.getLinkHub(true) != null) : false), false);
		this.table = table;
		table.getSelectionModel().addListSelectionListener(this);
		// getHub().addHubListener(this);
		afterChangeActiveObject(null);
	}

	@Override
	protected String isValidHubChangeAO(Object objNew) {
		return super.isValidHubChangeAO(objNew);
	}

	@Override
	protected boolean confirmHubChangeAO(Object objNew) {
		return super.confirmHubChangeAO(objNew);
	}

	protected boolean getIgnoreValueChanged() {
		if (table.tableLeft != null) {
			if (table.tableLeft.control.aiIgnoreValueChanged.get() > 0) {
				return true;
			}
		} else if (table.tableRight != null) {
			if (table.tableRight.control.aiIgnoreValueChanged.get() > 0) {
				return true;
			}
		}
		return aiIgnoreValueChanged.get() > 0;
	}

	protected boolean getRunningValueChanged() {
		if (table.tableLeft != null) {
			if (table.tableLeft.control._bIsRunningValueChanged) {
				return true;
			}
		} else if (table.tableRight != null) {
			if (table.tableRight.control._bIsRunningValueChanged) {
				return true;
			}
		}
		return _bIsRunningValueChanged;
	}

	@Override
	public void setSelectHub(Hub hubSelect, boolean bAllowRemovingFromSelectHub) {
		if (this.hubSelect == hubSelect) {
			return;
		}
		super.setSelectHub(hubSelect, bAllowRemovingFromSelectHub);

		if (this.hubSelect != null && hlSelect != null) {
			this.hubSelect.removeHubListener(hlSelect);
			hlSelect = null;
		}
		if (hubSelect == null) {
			return;
		}

		hlSelect = new HubListenerAdapter() {
			final AtomicInteger aiAdd = new AtomicInteger();
			final AtomicInteger aiRemove = new AtomicInteger();

			public @Override void afterAdd(HubEvent e) {
				if (getRunningValueChanged()) { // 20200508
					return;
				}
				int x = aiAdd.incrementAndGet();
				if (x == 1) {
					SwingUtilities.invokeLater(() -> {
						boolean b = aiAdd.get() == 1;
						aiAdd.set(0);
						if (b) {
							_afterAdd(e);
						} else {
							onNewList(e);
						}
					});
				}
			}

			public void _afterAdd(HubEvent e) {
				Object obj = e.getObject();
				if (obj == null || hub == null) {
					return;
				}
				if (table.chkSelection != null) {
					table.repaint(200);
				}
				int pos = hub.getPos(obj);

				try {
					aiIgnoreValueChanged.incrementAndGet();
					hub.setPos(pos);
					if (pos >= 0) {
						ListSelectionModel lsm = table.getSelectionModel();
						lsm.addSelectionInterval(pos, pos);
					}
				} finally {
					aiIgnoreValueChanged.decrementAndGet();
				}

				Container cont = table.getParent();
				for (int i = 0; i < 3 && cont != null; i++) {
					cont.repaint(200);
					cont = cont.getParent();
				}
			}

			public @Override void afterInsert(HubEvent e) {
				afterAdd(e);
			}

			public @Override void afterRemove(HubEvent e) {
				if (getRunningValueChanged()) {
					return;
				}
				int x = aiRemove.incrementAndGet();
				if (x == 1) {
					SwingUtilities.invokeLater(() -> {
						boolean b = aiRemove.get() == 1;
						aiRemove.set(0);
						if (b) {
							_afterRemove(e);
						} else {
							onNewList(e);
						}
					});
				}
			}

			public void _afterRemove(HubEvent e) {
				if (table.chkSelection != null) {
					table.repaint(100);
				}
				int pos = HubDataDelegate.getPos(hub, e.getObject(), false, false);
				// int pos = hub.getPos(e.getObject());
				if (pos >= 0) {
					try {
						aiIgnoreValueChanged.incrementAndGet();
						ListSelectionModel lsm = table.getSelectionModel();
						lsm.removeSelectionInterval(pos, pos);
					} catch (Exception ex) {
						// no-op
					} finally {
						aiIgnoreValueChanged.decrementAndGet();
					}
				}
				Container cont = table.getParent();
				for (int i = 0; i < 3 && cont != null; i++) {
					cont.repaint(200);
					cont = cont.getParent();
				}
			}

			public @Override void onNewList(HubEvent e) {
				if (getRunningValueChanged()) {
					return;
				}
				if (!SwingUtilities.isEventDispatchThread()) {
					SwingUtilities.invokeLater(() -> onNewList(e));
					return;
				}
				table.myClearSelectionAndLeadAnchor1();
				rebuildListSelectionModel(true);
				table.myClearSelectionAndLeadAnchor2();
				if (table.chkSelection != null) {
					table.repaint(100);
				}
			}
		};
		hubSelect.addHubListener(hlSelect);
		rebuildListSelectionModel();
	}

	private AtomicInteger aiRebuildListSelectionModel = new AtomicInteger();

	static int qqq;

	private AtomicBoolean abRebuildListSelectionModel = new AtomicBoolean();

	protected void rebuildListSelectionModel() {
		rebuildListSelectionModel(false);
	}

	protected void rebuildListSelectionModel(boolean bRunNow) {
		if (bRunNow) {
			_rebuildListSelectionModel();
			return;
		}
		if (!abRebuildListSelectionModel.compareAndSet(false, true)) {
			return;
		}

		SwingUtilities.invokeLater(() -> {
			abRebuildListSelectionModel.set(false);
			_rebuildListSelectionModel();
		});
	}

	private void _rebuildListSelectionModel() {
		for (int i = 0; i < 3; i++) {
			int cnt = aiRebuildListSelectionModel.incrementAndGet();
			try {
				aiIgnoreValueChanged.incrementAndGet();
				_rebuildListSelectionModel(cnt);
				break;
			} catch (Exception e) {
				// retry again
				int qq = 4;
				qq++;
			} finally {
				if (hubSelect != null && hubSelect.size() == 0) {
					hub.setPos(-1); //20180605
				}
				aiIgnoreValueChanged.decrementAndGet();
			}
		}
	}

	private void _rebuildListSelectionModel(final int cnt) {

		if (HubDelegate.getCurrentState(hubSelect, null, null) != HubDelegate.HubCurrentStateEnum.InSync) {
			return;
		}
		if (HubDelegate.getCurrentState(table.hub, null, null) != HubDelegate.HubCurrentStateEnum.InSync) {
			return;
		}

		ListSelectionModel lsm = table.getSelectionModel();
		lsm.clearSelection();

		if (hubSelect == null) {
			int x = hub.getPos();
			if (x >= 0) {
				lsm.addSelectionInterval(x, x);
			}
			return;
		}

		// update hubSelect, to see if objects are in table.hub
		int beginPos = -1; // 20200428
		int endPos = -1;
		for (int i = 0;; i++) {
			Object obj = hubSelect.getAt(i);
			if (obj == null) {
				break;
			}
			if (cnt != aiRebuildListSelectionModel.get()) {
				break;
			}

			int pos = hub.indexOf(obj); // dont use hub.getPos(), since it will adjust "linkage"
			if (pos < 0) {
				// only remove if it is not in the hubFilterMaster (if used)
				Hub h = table.hubFilterMaster;
				if (h == null && table.tableRight != null) {
					h = table.tableRight.hubFilterMaster;
				}
				if (h != null) {
					pos = h.indexOf(obj);
				}
				if (pos < 0 && getAllowRemovingFromSelectHub()) {
					hubSelect.removeAt(i);
					i--;
				}
			} else {
				if (beginPos < 0) {
					beginPos = endPos = pos;
				} else {
					if (pos == endPos + 1) {
						endPos = pos;
					} else {
						lsm.addSelectionInterval(beginPos, endPos);
						lsm.addSelectionInterval(pos, pos);
						beginPos = endPos = -1;
					}
				}
			}
		}
		if (beginPos >= 0 && endPos >= 0) {
			lsm.addSelectionInterval(beginPos, endPos);
		}
	}

	public synchronized void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}

		if (_bIsRunningValueChanged) {
			return;
		}

		if (getIgnoreValueChanged()) {
			return;
		}

		_valueChanged(e);
	}

	private AtomicInteger aiValueChanged = new AtomicInteger();

	public void _valueChanged(ListSelectionEvent e) {
		final int cntx = aiValueChanged.incrementAndGet();

		int row1 = e.getFirstIndex();
		int row2 = e.getLastIndex();
		if (row2 < 0) {
			row2 = row1;
		}

		_bIsRunningValueChanged = true;

		if (hubSelect != null) {
			ListSelectionModel lsm = table.getSelectionModel();

			boolean bWasEmpty = hubSelect.getSize() == 0;
			if (bWasEmpty) {
				if (row1 == row2) {
					bWasEmpty = false;
				}
			}
			if (bWasEmpty) {
				OAThreadLocalDelegate.setLoading(true);
			}

			Object objClicked = null;
			try {

				for (int i = row1;;) {
					Object obj = table.hub.elementAt(i);
					boolean b = lsm.isSelectedIndex(i);

					if (b) {
						if (!hubSelect.contains(obj)) {
							objClicked = obj;
						}
					} else {
						if (hubSelect.contains(obj)) {
							objClicked = obj;
						}
					}

					if (obj != null) {
						if (b) {
							if (bWasEmpty || !hubSelect.contains(obj)) {
								hubSelect.add(obj);
							}
						} else {
							hubSelect.remove(obj);
						}
					}

					if (row2 > row1) {
						i++;
						if (i > row2) {
							break;
						}
					} else {
						i--;
						if (i < row2) {
							break;
						}
					}
				}
			} finally {
				if (bWasEmpty) {
					OAThreadLocalDelegate.setLoading(false);
					HubEventDelegate.fireOnNewListEvent(hubSelect, true);
				}
			}
			Object objx = hubSelect.getAt(hubSelect.size() - 1);
			int newAoPos = getHub().getPos(objx);
			//newAoPos = table.getSelectionModel().getLeadSelectionIndex();

			// 20180605
			if (objClicked == null || !hubSelect.contains(objClicked)) {
				newAoPos = -1;
			} else {
				newAoPos = getHub().getPos(objClicked);
			}

			// 20171230
			_bIsRunningValueChanged = false;
			if (hubSelect.getSize() == 1) {
				hub.setAO(hubSelect.getAt(0));
			} else if (newAoPos < 0) {
				hub.setAO(null);
			} else if (hubSelect.size() > 1) {
				hub.setPos(newAoPos); // 20200428
				//was: hub.setAO(null);
			} else {
				table.setHubPos(newAoPos);
			}
		} else {
			_bIsRunningValueChanged = false;
			int row = table.getSelectedRow();
			table.setHubPos(row);
		}

		/**
		 * was: getHub().setAO(newAoPos); } else { int row = table.getSelectedRow(); getHub().setPos(row); int pos = getHub().getPos(); if
		 * (pos != row) { // if the hub.pos is not the same, set it back _bIsRunningValueChanged = false; if (pos >= 0)
		 * table.setRowSelectionInterval(pos, pos); else table.clearSelection(); } } _bIsRunningValueChanged = false;
		 */
		Container cont = table.getParent();
		for (int i = 0; i < 3 && cont != null; i++) {
			cont.repaint(200);
			cont = cont.getParent();
		}
	}

	private boolean bHasHadMaster; // 20171217

	public @Override void onNewList(HubEvent e) {
		// super.onNewList(e);
		if (!bHasHadMaster) {
			if (table.hub.getMasterHub() != null) {
				bHasHadMaster = true;
			} else if (table.hubFilterMaster != null && table.hubFilterMaster.getMasterHub() != null) {
				bHasHadMaster = true;
			}
		}

		if (bHasHadMaster) {
			Hub h = table.hubFilterMaster;
			if (h == null) {
				h = table.hub;
			}

			if (h.getMasterObject() == null) {
				table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			} else {
				table.setCursor(Cursor.getDefaultCursor());
			}
		}

		boolean b = false;
		try {
			if (table.control != null) {
				b = true;
				table.control.aiIgnoreValueChanged.incrementAndGet();
			}
			table.oaTableModel.fireTableStructureChanged();
		} finally {
			if (b && table.control != null) {
				table.control.aiIgnoreValueChanged.decrementAndGet();
			}
		}

		int x = getHub().getPos();
		if (x >= 0) {
			setSelectedRow(x);
		} else {
			Rectangle cellRect = new Rectangle(0, 0, 10, 10);
			table.scrollRectToVisible(cellRect);
			// table.repaint();
		}

		// 20101229 new list needs to be resorted
		table.performSort();

		// update hubSelect, to see if objects are in table.hub
		rebuildListSelectionModel();

		if (table.tableLeft != null && table.tableLeft.tcCount != null) {
			table.tableLeft.resizeCounterColumn();
		} else {
			table.resizeCounterColumn();
		}
	}

	public @Override void afterSort(HubEvent e) {
		// super.afterSort(e);
		table.oaTableModel.fireTableStructureChanged();

		int x = getHub().getPos();
		if (x >= 0) {
			setSelectedRow(x);
		} else {
			Rectangle cellRect = new Rectangle(0, 0, 10, 10);
			table.scrollRectToVisible(cellRect);
			// table.repaint();
		}
		// table.repaint();
		rebuildListSelectionModel();
	}

	public @Override void afterMove(HubEvent e) {
		// super.afterMove(e);
		// 20110616
		if (table.tableLeft != null || table.tableRight != null) {
			table.repaint(100);
		}

		table.oaTableModel.fireTableRowsUpdated(e.getPos(), e.getToPos());
		afterChangeActiveObject(e);
		rebuildListSelectionModel();

		final Rectangle cellRect = table.getCellRect(e.getToPos(), 0, true);

		if (SwingUtilities.isEventDispatchThread()) {
			table.scrollRectToVisible(cellRect);
		} else {
			aiRebuildListSelectionModel.incrementAndGet();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					table.scrollRectToVisible(cellRect);
					rebuildListSelectionModel();
				}
			});
		}

	}

	public @Override void afterChangeActiveObject(HubEvent e) {
		_afterChangeActiveObject(e);
		super.afterChangeActiveObject(e);
	}

	public void _afterChangeActiveObject(HubEvent e) {
		if (getRunningValueChanged()) {
			return; // 20131113
		}
		if (getIgnoreValueChanged()) {
			return; // 20160127
			// super.afterChangeActiveObject(e);
		}

		int row = getHub().getPos();
		if (table.getCellEditor() != null) {
			table.getCellEditor().stopCellEditing();
		}

		// 20131113
		if (table.hubSelect == null) {
			if (row < 0) {
				table.myClearSelectionAndLeadAnchor();
			}
			setSelectedRow(row);
			rebuildListSelectionModel();
		} else {
			// 20151225
			if (getHub().getAO() != null) {
				table.hubSelect.add(getHub().getAO());
			}
			/* 20160516 removed, so that it will add the AO to the selected list, and not replace the selected list.
			for (Object obj : table.hubSelect) {
			    if (obj != getHub().getAO()) table.hubSelect.remove(obj);
			}
			rebuildListSelectionModel();
			*/

			// 20160516
			Rectangle cellRect;

			// 20200424
			if (row < 0 && hubSelect.size() > 1) {
				return;
			}

			if (row < 0) {
				cellRect = new Rectangle(0, 0, 10, 10);
			} else {
				Container cont = table.getParent();
				cellRect = table.getCellRect(row, 0, true);
				if (cont instanceof JViewport) {
					cellRect.x = ((JViewport) cont).getViewPosition().x;
					cellRect.width = 5;
				}
			}
			if (cellRect != null) {
				if (SwingUtilities.isEventDispatchThread()) {
					table.scrollRectToVisible(cellRect);
				} else {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							table.scrollRectToVisible(cellRect);
						}
					});
				}
			}
			// table.repaint();
		}
	}

	protected void setSelectedRow(final int row) {
		if (getRunningValueChanged()) {
			return;
		}
		if (SwingUtilities.isEventDispatchThread()) {
			_setSelectRow(row);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					_setSelectRow(row);
				}
			});
		}
	}

	private void _setSelectRow(int row) {
		try {
			aiIgnoreValueChanged.incrementAndGet();
			_setSelectRow2(row);
		} finally {
			aiIgnoreValueChanged.decrementAndGet();
		}
	}

	private void _setSelectRow2(int row) {
		if (table.getCellEditor() != null) {
			table.getCellEditor().stopCellEditing();
		}
		if (row < 0) {
			table.getSelectionModel().clearSelection();
		} else {
			row = getHub().getPos();
			try {
				// 20110408 need to allow for selecting multiple lines
				ListSelectionModel lsm = table.getSelectionModel();
				if (!lsm.getValueIsAdjusting() && !lsm.isSelectedIndex(row)) {
					table.setRowSelectionInterval(row, row);
				}
				// was: table.setRowSelectionInterval(row,row);
			} catch (Exception e) { // IllegalArgument: row index out of range. Happens when Hub is
									// changed
				return;
			}

			// 20101029 this would scroll to leftmost AO
			Rectangle cellRect;
			if (row < 0) {
				cellRect = new Rectangle(0, 0, 10, 10);
			} else {
				Container cont = table.getParent();
				cellRect = table.getCellRect(row, 0, true);
				if (cont instanceof JViewport) {
					cellRect.x = ((JViewport) cont).getViewPosition().x;
					cellRect.width = 5;
				}
			}

			if (cellRect != null) {
				table.scrollRectToVisible(cellRect);
			}
			table.repaint(100);
		}
	}

	public @Override void afterPropertyChange(HubEvent e) {
		// super.afterPropertyChange(e);
		if (!(e.getObject() instanceof OAObject)) {
			return;
		}

		// was: if ( ((OAObject)e.getObject()).isProperty(e.getPropertyName())) {
		table.repaint(100);
	}

	protected void removeInvoker(final int pos) {
		aiRebuildListSelectionModel.incrementAndGet();
		// 20110616
		if (table.tableRight != null) {
			rebuildListSelectionModel();
			table.repaint(100);
			return;
		}

		if (SwingUtilities.isEventDispatchThread()) {
			try {
				table.control.aiIgnoreValueChanged.incrementAndGet();
				table.oaTableModel.fireTableRowsDeleted(pos, pos);
			} finally {
				table.control.aiIgnoreValueChanged.decrementAndGet();
			}
			rebuildListSelectionModel();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						table.control.aiIgnoreValueChanged.incrementAndGet();
						try {
							table.oaTableModel.fireTableRowsDeleted(pos, pos);
						} catch (Exception e) {
						}
					} finally {
						table.control.aiIgnoreValueChanged.decrementAndGet();
					}
					rebuildListSelectionModel();
				}
			});
		}
	}

	/**
	 * 20090702, replaced with beforeRemove(), since activeObject is changed before afterRemove is called public @Override void
	 * afterRemove(HubEvent e) { removeInvoker(e.getPos()); }
	 */
	public @Override void beforeRemove(HubEvent e) {
		// super.beforeRemove(e);
		removeInvoker(e.getPos());
	}

	@Override
	public void afterRemove(HubEvent e) {
		// super.afterRemove(e);
		aiRebuildListSelectionModel.incrementAndGet();
		rebuildListSelectionModel();
		// 20101229 need to reset the activeRow
		int row = getHub().getPos();

		if (table.hubSelect == null) {
			setSelectedRow(row);
		}
		if (table.prefMaxRows > 0) {
			table.calcPreferredSize();
		}
	}

	protected void insertInvoker(final int pos, final boolean bIsAdd) {
		// 20110616
		if (table.tableRight != null) {
			// need to make sure that selectionModel is not changed
			table.repaint(100);
			if (!bIsAdd) {
				rebuildListSelectionModel();
			}
			return;
		}

		if (SwingUtilities.isEventDispatchThread()) {
			try {
				table.control.aiIgnoreValueChanged.incrementAndGet();
				table.oaTableModel.fireTableRowsInserted(pos, pos);
			} finally {
				table.control.aiIgnoreValueChanged.decrementAndGet();
			}
			if (!bIsAdd) {
				rebuildListSelectionModel();
			}
		} else {
			if (!bIsAdd) {
				aiRebuildListSelectionModel.incrementAndGet();
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						table.control.aiIgnoreValueChanged.incrementAndGet();
						table.oaTableModel.fireTableRowsInserted(pos, pos);
					} finally {
						table.control.aiIgnoreValueChanged.decrementAndGet();
					}
					rebuildListSelectionModel(); // 20181125 need to rebuild, since this is delayed
					//was: if (!bIsAdd) rebuildListSelectionModel();
				}
			});
		}
	}

	public @Override void afterInsert(HubEvent e) {
		// super.afterInsert(e);
		insertInvoker(e.getPos(), false);
		if (table.prefMaxRows > 0) {
			table.calcPreferredSize();
		}
	}

	public @Override void afterAdd(HubEvent e) {
		// super.afterAdd(e);
		if (getHub() != null) {
			insertInvoker(e.getPos(), true);
			table.setChanged(e.getPos(), -1);
		}
		if (table.prefMaxRows > 0) {
			table.calcPreferredSize();
		}
	}
}

// 20150518 add header editor, for entering filter data
class PanelHeaderRenderer extends JPanel implements TableCellRenderer {
	OATable table;
	JButton button;
	JLabel label;
	int buttonHeight, labelHeight;

	public PanelHeaderRenderer(OATable t) {
		this.table = t;

		setLayout(new BorderLayout());

		Color c = UIManager.getColor("Table.gridColor");
		if (c == null) {
			c = Color.black;
		}
		Border border = new CustomLineBorder(0, 0, 2, 0, c);
		setBorder(border);

		button = new JButton() {
			@Override
			public Dimension getPreferredSize() {
				Dimension dim = super.getPreferredSize();
				if (buttonHeight == 0) {
					buttonHeight = dim.height;
				} else {
					dim.height = buttonHeight;
				}
				return dim;
			}
		};
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setHorizontalTextPosition(SwingConstants.LEFT);
		add(button, BorderLayout.NORTH);

		label = new JLabel(" ") {
			@Override
			public Dimension getPreferredSize() {
				Dimension dim = super.getPreferredSize();
				if (labelHeight == 0) {
					labelHeight = dim.height + 4;
				} else {
					dim.height = labelHeight;
				}
				return dim;
			}
		};
		label.setOpaque(true);
		if (table.hubFilter != null || (table.tableRight != null && table.tableRight.hubFilter != null)) {
			add(label, BorderLayout.CENTER);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		if (labelHeight > 0 & buttonHeight > 0) {
			;
		} else if (table != null && table.tableRight != null) {
			if (table.tableRight.headerRenderer.labelHeight > 0 & table.tableRight.headerRenderer.buttonHeight > 0) {
				this.labelHeight = table.tableRight.headerRenderer.labelHeight;
				this.buttonHeight = table.tableRight.headerRenderer.buttonHeight;
			}
		} else if (table != null && table.tableLeft != null) {
			if (table.tableLeft.headerRenderer.labelHeight > 0 & table.tableLeft.headerRenderer.buttonHeight > 0) {
				this.labelHeight = table.tableLeft.headerRenderer.labelHeight;
				this.buttonHeight = table.tableLeft.headerRenderer.buttonHeight;
			}
		}
		dim.height = labelHeight + buttonHeight;
		return dim;
	}

	private Color bgColor;
	private Border border;
	private ImageIcon iconFilter;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		button.setText((value == null) ? "" : value.toString());

		int myColumn = table.getColumnModel().getColumn(column).getModelIndex();
		//OATableColumn tc = (OATableColumn) this.table.columns.elementAt(column);
		final OATableColumn tc = (OATableColumn) ((OATable) table).columns.elementAt(myColumn);

		Component comp = null;

		if (this.table.hubFilter != null || (this.table.tableRight != null && this.table.tableRight.hubFilter != null)) {
			OATableComponent tcFilter = tc.getFilterComponent();

			if (border == null) {
				Color c = UIManager.getColor("Table.gridColor");
				if (c == null) {
					c = Color.black;
				}
				//was:  border = new CustomLineBorder(1, 1, 3, 1, c);
				border = new CustomLineBorder(1, 1, 0, 1, c);
				border = new CompoundBorder(border, new EmptyBorder(0, 2, 0, 1));
			}

			label.setHorizontalTextPosition(SwingConstants.RIGHT);

			Icon icon = null;
			if (tcFilter != null) {
				label.setBackground(Color.white);

				if (iconFilter == null) {
					URL url = OAButton.class.getResource("icons/filter16.png");
					iconFilter = new ImageIcon(url);
				}
				icon = iconFilter;

				comp = tcFilter.getTableRenderer(label, table, value, false, false, -1, column);
			}

			if (tc.getOATableComponent() == this.table.chkSelection) {
				comp = this.table.chkSelection.getTableRenderer(label, table, value, false, false, -1, column);
			} else if (comp == null) {
				comp = label;
				label.setText(" ");
				if (bgColor == null) {
					bgColor = new Color(230, 230, 230);//bgColor = UIManager.getColor("Table.gridColor");
				}
				Color color = bgColor;
				if (color == null) {
					color = Color.white;
				}
				label.setBackground(color);
			}
			String s = label.getText();
			if (OAString.isEmpty(s)) {
				label.setText(" ");
			}

			if (comp instanceof JComponent) {
				((JComponent) comp).setBorder(border);
			}

			if (tc == this.table.tcCount || (this.table.tableRight != null && tc == this.table.tableRight.tcCount)) {
				if (iconResetFilter == null) {
					URL url = OAButton.class.getResource("icons/reset.gif");
					if (url == null) {
						return null;
					}
					iconResetFilter = new ImageIcon(url);
				}
				label.setText("");
				label.setBorder(null);
				icon = iconResetFilter;
				label.setBackground(Color.white);
				JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
				p.setBorder(null);
				p.setOpaque(true);
				p.setBackground(Color.white);
				p.add(comp);
				comp = p;
			}
			label.setIcon(icon);

			PanelHeaderRenderer.this.removeAll();
			PanelHeaderRenderer.this.setLayout(new BorderLayout());
			PanelHeaderRenderer.this.add(button, BorderLayout.NORTH);
			PanelHeaderRenderer.this.add(comp, BorderLayout.CENTER);
		}

		Icon icon = null;
		if (tc.sortOrder > 0) {
			int pos = tc.sortOrder;
			if (tc.sortOrder == 1) {
				pos = 0;
				int x = this.table.columns.size();
				for (int i = 0; i < x; i++) {
					OATableColumn tcx = (OATableColumn) this.table.columns.elementAt(i);
					if (tcx.sortOrder > 0 && tcx != tc) {
						pos = 1;
						break;
					}
				}
			} else if (pos > 3) {
				pos = 0;
			}
			if (tc.sortDesc) {
				icon = this.table.iconDesc[pos];
			} else {
				icon = this.table.iconAsc[pos];
			}
		}
		button.setIcon(icon);

		return this;
	}

	private Icon iconResetFilter;
	private FocusListener focusListener;
	private Component compFilter;

	public void setupEditor(int column) {
		OATableColumn tc = null;
		int myColumn = table.getColumnModel().getColumn(column).getModelIndex();
		if (column >= 0 && column < table.columns.size()) {
			tc = (OATableColumn) table.columns.elementAt(myColumn);
		}
		if (tc == null) {
			return;
		}

		Component comp = null;
		OATableComponent tcFilter = tc.getFilterComponent();
		if (tcFilter != null) {
			comp = tcFilter.getTableCellEditor().getTableCellEditorComponent(table, null, false, -1, column);
		}

		JTableHeader th = table.getTableHeader();

		if (compFilter != null) {
			compFilter.removeFocusListener(focusListener);
			th.remove(compFilter);
			compFilter = null;
			focusListener = null;
		}
		if (comp == null) {
			return;
		}
		compFilter = comp;

		focusListener = (new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				Component comp = (Component) e.getSource();
				JTableHeader th = table.getTableHeader();
				comp.removeFocusListener(this);
				th.remove(comp);
				if (compFilter == comp) {
					compFilter = null;
					focusListener = null;
				}
				table.getTableHeader().repaint(100);
				if (table.getLeftTable() != null) {
					table.getLeftTable().getTableHeader().repaint(100);
				}
				if (table.getRightTable() != null) {
					table.getRightTable().getTableHeader().repaint(100);
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});
		compFilter.addFocusListener(focusListener);

		if (comp.getParent() != table.getTableHeader()) {
			table.getTableHeader().add(comp);
		}

		Rectangle rect = table.getTableHeader().getHeaderRect(column);
		if (buttonHeight == 0) {
			getPreferredSize();
		}
		rect.y += buttonHeight + 1;
		rect.height -= (buttonHeight + 3);
		rect.x += 2;
		rect.width -= 3;

		compFilter.setBounds(rect);
		compFilter.requestFocusInWindow();
		this.table.repaint(100);
		table.getTableHeader().repaint(100);
		if (table.getLeftTable() != null) {
			table.getLeftTable().getTableHeader().repaint(100);
		}
		if (table.getRightTable() != null) {
			table.getRightTable().getTableHeader().repaint(100);
		}
	}
}

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
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.*;

import com.viaoa.hub.*;
import com.viaoa.jfc.dnd.*;
import com.viaoa.jfc.control.*;
import com.viaoa.jfc.table.*;
import com.viaoa.undo.OAUndoManager;
import com.viaoa.undo.OAUndoableEdit;


public class OAList extends JList implements OATableComponent, DragGestureListener, DropTargetListener, OAJfcComponent {
    protected OATable table;
    protected OAListController control;

    // 10/16/99 Drag&Drop  
    boolean bAllowDrag = false;
    boolean bAllowDrop = false;
    boolean bRemoveDragObject;
    DropTarget dropTarget;
    DragSource dragSource = DragSource.getDefaultDragSource();
    final static DragSourceListener dragSourceListener = new MyDragSourceListener();
    
    
    /**
        Create an unbound list.
    */
    public OAList() {
        this(null, null, 10, 5);
        initialize();
    }
    
    /**
        Create List that is bound to a Hub.
    */
    public OAList(Hub hub) {
        this(hub, null, 10, 5);
        initialize();
    }

    /**
        Create List that is bound to a property for the active object in a Hub.
    */
    public OAList(Hub hub, String propertyPath) {
        this(hub,propertyPath, 10,5);
        initialize();
    }

    /**
        Create List that is bound to a Hub.
        @param visibleRowCount number of rows to visually display.
        @param cols is width of list using character width size.
    */
    public OAList(Hub hub, String propertyPath, int visibleRowCount, int cols) {
        control = new OAListController(hub, propertyPath, visibleRowCount);
        
        if (cols > 0) setColumns(cols);
        else setPrototypeCellValue("0123456789ABCD");

        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE,this);
        dropTarget = new DropTarget(this,this);
        
        setBorder(new EmptyBorder(2,2,2,2));
        initialize();
    }

    @Override
    public void initialize() {
    }
    
    
    @Override
    public OAJfcController getController() {
        return control;
    }
    
    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if (d == null || d.width == 0) {
            d = new Dimension(
                    OAJfcUtil.getCharWidth(getColumns()), 
                    OAJfcUtil.getCharHeight() * getVisibleRows() 
            );
        }
        return d;
    }
    
    public Dimension getMaximumSize() {
        Dimension d = super.getMaximumSize();
        if (isMaximumSizeSet()) return d;
        int cols = getMaxColumns();
        if (cols < 1) cols = control.getPropertyInfoMaxLength(); 
        
        if (cols < getColumns())  {
            cols = getColumns() * 2; 
        }

        Insets ins = getInsets();
        int inx = ins == null ? 0 : ins.left + ins.right;
        
        d.width = OAJfcUtil.getCharWidth(cols) + inx;
        return d;
    }
    
    public Dimension getMinimumSize() {
        Dimension d = super.getMinimumSize();
        if (isMinimumSizeSet()) return d;
        int cols = getMiniColumns();
        
        if (cols > getColumns())  {
            cols = (getColumns() / 4);
        }
        cols = Math.max(0, cols);

        Insets ins = getInsets();
        int inx = ins == null ? 0 : ins.left + ins.right;
        
        d.width = OAJfcUtil.getCharWidth(cols) + inx;
        return d;
    }
    
    
    boolean bRemoved;
    
// 20180526 replaced with OASplitPane
    /*
    public void XX_addNotify() {
        super.addNotify();
        if (bRemoved) control.setHub(control.getHub());
        
        // 20120116 need to set divider if in a splitpane, since JList.getPreferredSize returns 0,0 if no rows in model
        Dimension d = this.getPreferredSize();
        if (d == null) return;

        // 20150927 only if it's directly in a splitpane
        Container c = this.getParent();
        if (c == null) return;
        if (!(c instanceof JSplitPane)) {
            c = c.getParent();
            if (!(c instanceof JSplitPane)) return;
        }
        JSplitPane split = (JSplitPane) c;
        int loc = split.getDividerLocation();
        if (split.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            if (loc < d.width) {
                split.setDividerLocation(d.width);
            }
        }            
        else {
            if (split.getDividerLocation() < d.height) {
                split.setDividerLocation(d.height);
            }
        }
    }
    */
    
    /* 2005/02/07 need to manually call close instead
    public void removeNotify() {
        super.removeNotify();
        bRemoved = true;
        control.close();
    }
    */
    public void close() {
        bRemoved = true;
        control.close();
    }
    

    /** 
        Called by Hub2List when item is selected 
    */
    public void valueChanged() {
    }

    /** 
        Called by Hub2List when item is selected 
    */
    public void onItemSelected(int row) {
    }
    
    /** 
    The "word(s)" to use for the empty row (null value).  Default=""  <br>
    Example: "none of the above".  
    <p>
    Note: Set to null if none should be used
	*/
	public String getNullDescription() {
	    return control.getNullDescription();
	}
	/** 
	    The "word(s)" to use for the empty row (null value).  Default=""  <br>
	    Example: "none of the above".  
	    <p>
	    Note: Set to null if none should be used
	*/
	public void setNullDescription(String s) {
	    control.setNullDescription(s);
	}
    
    
    /**
        Flag to know if a row/object can be removed from the Hub by using the [Delete] key.
    */
    public void setAllowRemove(boolean b) {
        control.setAllowRemove(b);
    }
    /**
        Flag to know if a row/object can be removed from the Hub by using the [Delete] key.
    */
    public boolean getAllowRemove() {
        return control.getAllowRemove();
    }
    
    /**
        Flag to know if a row/object can be deleted by using the [Delete] key.
    */
    public void setAllowDelete(boolean b) {
        control.setAllowDelete(b);
    }
    /**
        Flag to know if a row/object can be deleted by using the [Delete] key.
    */
    public boolean getAllowDelete() {
        return control.getAllowDelete();
    }

    /**
        Flag to know if a new row/object can be inserted by using the [Insert] key.
    */
    public void setAllowInsert(boolean b) {
        control.setAllowInsert(b);
    }
    /**
        Flag to know if a new row/object can be inserted by using the [Insert] key.
    */
    public boolean getAllowInsert() {
        return control.getAllowInsert();
    }

    /**
        Get the property name used for displaying an image with component.
    */
    public void setImageProperty(String prop) {
        control.setImagePropertyPath(prop);
    }
    /**
        Get the property name used for displaying an image with component.
    */
    public String getImageProperty() {
        return control.getImagePropertyPath();
    }

    public int getMaxImageHeight() {
    	return control.getMaxImageHeight();
	}
	public void setMaxImageHeight(int maxImageHeight) {
		control.setMaxImageHeight(maxImageHeight);
	}

	public int getMaxImageWidth() {
		return control.getMaxImageWidth();
	}
	public void setMaxImageWidth(int maxImageWidth) {
		control.setMaxImageWidth(maxImageWidth);
	}
    
    /**
        Root directory path where images are stored.
    */
    public void setImagePath(String path) {
        control.setImagePropertyPath(path);
    }
    /**
        Root directory path where images are stored.
    */
    public String getImagePath() {
        return control.getImagePropertyPath();
    }

    /**
        Get the image to display for the current object.
    */
    public Icon getIcon() {
        return control.getIcon();
    }
    /**
        Get the image to display for an object.
    */
    public Icon getIcon(Object obj) {
        return control.getIcon(obj);
    }
    
    
    
    // ----- OATableComponent Interface methods -----------------------
    // some of these are only included so that the ProperyPathCustomerEditor can be used
    
    
    /**
        Hub that this component is bound to.
    */
    public Hub getHub() {
        if (control == null) return null;
        return control.getHub();
    }


    /**
        Set by OATable when this component is used as a column.  
        Ignored, OAList can not be used as a column in an OATable.
    */
    public void setTable(OATable table) {
    }
    /**
        Set by OATable when this component is used as a column.  
        Ignored, OAList can not be used as a column in an OATable.
    */
    public OATable getTable() {
        return null;
    }

    /**
        Width of component, based on average width of the font's character.
    */
    public int getColumns() {
        return getController().getColumns();
    }
    
    /**
	    Width of ComboBox, based on average width of the font's character.
	*/
	public void setColumns(int x) {
        getController().setColumns(x);
		String str = null;
		for (int i=0; i<x; i++) {
			if (str == null) str = "X";
			else str += "m"; 
		}
	    super.setPrototypeCellValue(str);
	}

    
    
    /**
        Returns the amount of rows that are visible.
        Calls super.getVisibleRowCount()
    */
    public int getVisibleRows() {
        return super.getVisibleRowCount();
    }
    /**
        Sets the amount of rows that are visible.
        Calls super.setVisibleRowCount()
    */
    public void setVisibleRows(int rows) {
        super.setVisibleRowCount(rows);
    }

    /**
        Property path used to retrieve/set value for this component.
    */
    @Override
    public String getPropertyPath() {
        if (control == null) return null;
        return control.getPropertyPath();
    }
/*    
    public String getEndPropertyName() {
        if (control == null) return null;
        return control.getEndPropertyName();
    }
*/

    /** 
        Seperate Hub that can contain selected objects. 
        This will allow for a multiselect list.
    */
    public void setSelectionHub(Hub hub) {
        control.setSelectHub(hub);
    }
    public Hub getSelectionHub() {
        return control.getSelectHub();
    }
    public void setSelectHub(Hub hub) {
        control.setSelectHub(hub);
    }
    public Hub getSelectHub() {
        return control.getSelectHub();
    }

    /** 
        Popup message that will be used to confirm an insert/remove/delete/new.
    */
    public void setConfirmMessage(String msg) {
        control.setConfirmMessage(msg);
    }
    /** 
        Popup message that will be used to confirm an insert/remove/delete/new.
    */
    public String getConfirmMessage() {
        return control.getConfirmMessage();
    }
    
    
    /**
        Column heading when this component is used as a column in an OATable.
        Ignored, OAList can not be used as a column in an OATable.
    */
    public String getTableHeading() { 
        return "";
    }
    /**
        Column heading when this component is used as a column in an OATable.
        Ignored, OAList can not be used as a column in an OATable.
    */
    public void setTableHeading(String heading) {
    }

    /**
        Editor used when this component is used as a column in an OATable.
        Ignored, OAList can not be used as a column in an OATable.
    */
    public TableCellEditor getTableCellEditor() {
        return null;
    }

    // START Drag&Drop
    static class MyDragSourceListener implements DragSourceListener {
        public void dragEnter(DragSourceDragEvent e) {}
        public void dragOver(DragSourceDragEvent e) { }
        public void dropActionChanged(DragSourceDragEvent e) {}
        public void dragExit(DragSourceEvent e) {}
        public void dragDropEnd(DragSourceDropEvent e) {
        }
    }
    /** Flag to allow drag &amp; drop. default=true */
    public void setAllowDnD(boolean b) {
        setAllowDrop(b);
        setAllowDrag(b);
    }

    
    /** Flag to allow drop. default=true */
    public boolean getAllowDrop() {
        return bAllowDrop;
    }
    /** Flag to allow drop. default=true */
    public void setAllowDrop(boolean b) {
        bAllowDrop = b;
    }

    /** Flag to allow drag. default=true */
    public boolean getAllowDrag() {
        return bAllowDrag;
    }
    /** Flag to allow drag. default=true */
    public void setAllowDrag(boolean b) {
        bAllowDrag = b;
    }

    /** 
        Flag to allow know if a dragged object should be removed from list.
    */
    public void setRemoveDragObject(boolean b) {
        bRemoveDragObject = b;
    }
    /** 
        Flag to allow know if a dragged object should be removed from list.
    */
    public boolean getRemoveDragObject() {
        return bRemoveDragObject;
    }

    /** Used to support drag and drop (DND). */
    public void dragGestureRecognized(DragGestureEvent e) {
        if (!getAllowDrag()) return;
        Hub h = control.getHub();
        if (h != null) {
            Object obj = h.getActiveObject();
            if (obj != null) {
                OATransferable t = new OATransferable(h,obj);
                dragSource.startDrag(e, null, t, dragSourceListener);
            }
        }
    }

    /** Used to support drag and drop (DND). */
    public void dragEnter(DropTargetDragEvent e) {
        if (getAllowDrop() && e.isDataFlavorSupported(OATransferable.HUB_FLAVOR)) {
            e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        }
        else e.rejectDrag();
    }

    private long timeDragOver;
    /** Used to support drag and drop (DND). */
    public void dragOver(DropTargetDragEvent e) { 
        Point pt = e.getLocation();
        Container cont = this.getParent();
        Point p = this.getLocation();
        pt.translate(p.x, p.y);
        for (;cont != null; ) {
            if (cont instanceof JPanel) break;
            if (cont instanceof JScrollPane) {
                long ltime = (new java.util.Date()).getTime();
                if (ltime < (timeDragOver + 200)) break;
                JScrollPane sp = (JScrollPane) cont;
                JViewport viewport = sp.getViewport();

                Point ptViewPosition = (Point) viewport.getViewPosition().clone(); 
                Component comp = viewport.getView();
                

                // if (qqq == 0) System.out.println("viewportSize="+viewport.getSize()+"   ScrollPaneSize="+sp.getSize()+"  CompSize="+comp.getSize());
                // System.out.println((qqq++)+") "+ptViewPosition);
                boolean b = false;
                if (pt.y < 20) {
                    if (ptViewPosition.y != 0) {
                        ptViewPosition.y -= 20;
                        if (ptViewPosition.y < 0) ptViewPosition.y = 0;
                        b = true;;
                    }
                }
                else {
                    Dimension dimViewPort = viewport.getExtentSize();
                    
                    if (pt.y > dimViewPort.height - 20) {
                        Dimension dimComp = comp.getSize();
                        if ((ptViewPosition.y + dimViewPort.height) != dimComp.height) {
                            ptViewPosition.y += 20;
                            if ((ptViewPosition.y + dimViewPort.height) > dimComp.height) ptViewPosition.y = (dimComp.height - dimViewPort.height);
                            b = true;
                        }
                    }                    
                }

                if (pt.x < 20) {
                    if (ptViewPosition.x != 0) {
                        ptViewPosition.x -= 20;
                        if (ptViewPosition.x < 0) ptViewPosition.x = 0;
                        b = true;;
                    }
                }
                else {
                    Dimension dimViewPort = viewport.getExtentSize();
                    
                    if (pt.x > dimViewPort.width - 20) {
                        Dimension dimComp = comp.getSize();
                        if ((ptViewPosition.x + dimViewPort.width) != dimComp.width) {
                            ptViewPosition.x += 20;
                            if ((ptViewPosition.x + dimViewPort.width) > dimComp.height) ptViewPosition.x = (dimComp.width - dimViewPort.width);
                            b = true;
                        }
                    }                    
                }

                if (b) {
                    scrollDrag(viewport, ptViewPosition);
                    timeDragOver = ltime;
                }
                break;
            }
            p = cont.getLocation();
            pt.translate(p.x, p.y);
            cont = cont.getParent();
        }
    }
    
    /** Used to support drag and drop (DND). */
    protected void scrollDrag(final JViewport viewport, final Point ptViewPosition) {
        viewport.setViewPosition(ptViewPosition);
    }
    
    
    
    
    /** Used to support drag and drop (DND). */
    public void dropActionChanged(DropTargetDragEvent e) { 
    }
    /** Used to support drag and drop (DND). */
    public void dragExit(DropTargetEvent e) {  
    }
    /** Used to support drag and drop (DND). */
    public void drop(DropTargetDropEvent e) {
        try {
            if (!e.getTransferable().isDataFlavorSupported(OATransferable.HUB_FLAVOR)) return;
            if (!getAllowDrop()) return;

            // get object to move/copy
            Hub dragHub = (Hub) e.getTransferable().getTransferData(OATransferable.HUB_FLAVOR);
            Object dragObject = (Object) e.getTransferable().getTransferData(OATransferable.OAOBJECT_FLAVOR);
 
            Point pt = e.getLocation();
            
            int row = locationToIndex(pt);
            
            
            if (row < 0) row = getModel().getSize();

            Rectangle rect = getCellBounds(row,row);
            if (rect != null && pt.y > (rect.y + (rect.height/2))) row++;

            Hub newHub = getDropHub();
            if (newHub == null) return;
            
            // 20190215
            if (!HubAddRemoveDelegate.isAllowAddRemove(newHub)) return;
            if (!newHub.isValid()) return;
            
            Object toObject = newHub.elementAt(row);

            if ( newHub.getObjectClass().isAssignableFrom(dragObject.getClass()) ) {
                if (dragObject != toObject) {
                    
                	int pos = HubDataDelegate.getPos(newHub, dragObject, false, false);
                    if (pos >= 0) {
                        // move
                        if (!newHub.isSorted()) {
                            if (pos < row) row--;
                            newHub.move(pos, row);
                            // 20091214
                            OAUndoManager.add(OAUndoableEdit.createUndoableMove(null, newHub, pos, row));
                            control.afterChangeActiveObject(null);
                        }
                    }
                    else {
                        if (newHub.isSorted()) {
                            newHub.add(dragObject);
                            // 20091214
                            OAUndoManager.add(OAUndoableEdit.createUndoableAdd(null, newHub, dragObject));
                        }
                        else {
                            newHub.insert(dragObject, row);
                            // 20091214
                            OAUndoManager.add(OAUndoableEdit.createUndoableInsert(null, newHub, dragObject, row));
                        }
                        
                        if (getRemoveDragObject()) {
                            dragHub.remove(dragObject);   
                        }
                    }
                    newHub.setActiveObject(dragObject);
                }
            }
            e.dropComplete(true);
        }
        catch (UnsupportedFlavorException ex) {
        }
        catch (IOException ex) {
        }
    }
    /** 
     * This is used to work with MergedHubs, where the List hub is different then the source hub.
     * Default is to use this.getHub() 
     * 20080730
     */
    protected Hub getDropHub() {
    	return control.getHub();
    }
    //END Drag&Drop

    /** 
        Renderer used to display each row.  Can be overwritten to create custom rendering.
        Called by Hub2List.MyListCellRenderer.getListCellRendererComponent to get renderer. 
    */
    public Component getRenderer(Component comp, JList list,Object value, int index,boolean isSelected,boolean cellHasFocus) {
        if (!(comp instanceof JLabel)) return comp;
        
        JLabel lbl = (JLabel) comp;
        
        if (index == mouseOverRow) {
            lbl.setForeground(Color.white);
            lbl.setBackground(OATable.COLOR_MouseOver);
        }

        return comp;
    }
    
    
    
    /**
     * This is called by getRenderer(..) after the default settings have been set.
     */
    public void customizeRenderer(JLabel label, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (index < 0) return;
        Hub h = getHub();
        if (h == null) return;
        Object obj = h.getAt(index);
        customizeRenderer(label, obj, value, isSelected, cellHasFocus, index, false, false);
    }
    
    /** 
        Used to supply the renderer when this component is used in the column of an OATable.
        Can be overwritten to customize the rendering.
        <p>
        Note: Not used, OAList can not be used as a column for an OATable.    
    */
    public Component getTableRenderer(JLabel renderer, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return renderer;
    }

    @Override
    public String getTableToolTipText(JTable table, int row, int col, String defaultValue) {
        Object obj = ((OATable) table).getObjectAt(row, col);
        getToolTipText(obj, row, defaultValue);
        return defaultValue;
    }
    @Override
    public void customizeTableRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column,boolean wasChanged, boolean wasMouseOver) {
        Object obj = ((OATable) table).getObjectAt(row, column);
        customizeRenderer(lbl, obj, value, isSelected, hasFocus, row, wasChanged, wasMouseOver);
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


    protected AbstractButton cmdDoubleClick;
    /**
    Button to perform a doClick() when table clickCount == 2
	*/
	public void setDoubleClickButton(AbstractButton cmd) {
	    cmdDoubleClick = cmd;
	}
	/**
	    Button to perform a doClick() when table clickCount == 2
	*/
	public AbstractButton getDoubleClickButton() {
	    return cmdDoubleClick;
	}
    
	private boolean bAllowSelectNone = true;
	
	/**
	 * Determines if the user can clear the selection, by
	 * clicking at the bottom of the list, below the last row.
	 */
	public void setAllowSelectNone(boolean b) {
	    bAllowSelectNone = b;
	}
    public boolean getAllowSelectNone() {
        return bAllowSelectNone;
    }
	
    /**
    Capture double click and call double click button.
    @see #getDoubleClickButton
	*/
	protected void processMouseEvent(MouseEvent e) {
	    if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            int row = locationToIndex(e.getPoint());
            if (row >= 0) {
                Rectangle rect = getCellBounds(row, row);
                int y = e.getPoint().y;
                if (y > rect.y + rect.height) {
                    if (getAllowSelectNone()) getSelectionModel().clearSelection();
                    return;
                }
            }
        }
	    
	    super.processMouseEvent(e);
	    if (e.getID() == MouseEvent.MOUSE_PRESSED) {
	        if (e.getClickCount() == 2) {
	            if (cmdDoubleClick != null) cmdDoubleClick.doClick();
	            onDoubleClick();
	        }
	    }
        else if (e.getID() == MouseEvent.MOUSE_EXITED) {
            onMouseOver(-1, e);
        }
	}

    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            int row = locationToIndex(e.getPoint());
            if (row >= 0) {
                Rectangle rect = getCellBounds(row, row);
                int y = e.getPoint().y;
                if (y > rect.y + rect.height) row = -1;
            }
            onMouseOver(row, e);
        }
        
        super.processMouseMotionEvent(e);
    }
	

    protected int mouseOverRow=-1;
    private Rectangle rectMouseOver;

    public void onMouseOver(int row, MouseEvent evt) {
        mouseOverRow = row;
        if (rectMouseOver != null) repaint(rectMouseOver);
        if (row < 0) rectMouseOver = null;
        else rectMouseOver = getCellBounds(row, row);
    }
    
	
	/**
	    Method that is called whenever mouse click count = 2.
	    Note: the activeObject of the clicked row will be the active object in the OATables Hub.
	*/
	public void onDoubleClick() {
	
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
     * @return null if no errors, else error message
     */
    public String isValid(Object object, Object value) {
        return null;
    }


    public String getToolTipText(Object object, String defalutValue) {
        return defalutValue;
    }
    
    
    class OAListController extends ListController {
        public OAListController(Hub hub, String propertyPath, int visibleRow) {
            super(hub, OAList.this, propertyPath, visibleRow);
        }
        
        @Override
        protected boolean isEnabled(boolean bIsCurrentlyEnabled) {
            bIsCurrentlyEnabled = super.isEnabled(bIsCurrentlyEnabled);
            return OAList.this.isEnabled(bIsCurrentlyEnabled);
        }
        @Override
        protected boolean isVisible(boolean bIsCurrentlyVisible) {
            bIsCurrentlyVisible = super.isVisible(bIsCurrentlyVisible);
            return OAList.this.isVisible(bIsCurrentlyVisible);
        }
        @Override
        public String isValid(Object object, Object value) {
            String msg = OAList.this.isValid(object, value);
            if (msg == null) msg = super.isValid(object, value);
            return msg;
        }
        
        @Override
        public synchronized void valueChanged(ListSelectionEvent e) {
            super.valueChanged(e);
            OAList.this.valueChanged();
            OAList.this.onItemSelected(e.getFirstIndex());
        }
        @Override
        public Component getRenderer(Component renderer, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            renderer = super.getRenderer(renderer, list, value, index, isSelected, cellHasFocus);
            /*20181004 was: now done by oajfccontroller
            String tt = component.getToolTipText();
            tt = OAList.this.getToolTipText(value, tt);
            if (tt != null) ((JComponent)renderer).setToolTipText(tt);
            */
            Component comp = OAList.this.getRenderer(renderer, list, value, index, isSelected, cellHasFocus);
            if (renderer instanceof JLabel) {
                OAList.this.customizeRenderer((JLabel)renderer, list, value, index, isSelected, cellHasFocus);
            }
            return comp;
        }
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


    public void setMaximumColumns(int x) {
        control.setMaximumColumns(x);
        invalidate();
    }
    public int getMaximumColumns() {
        return control.getMaximumColumns();
    }
    public void setMaxColumns(int x) {
        control.setMaximumColumns(x);
        invalidate();
    }
    public int getMaxColumns() {
        return control.getMaximumColumns();
    }
    public void setMiniColumns(int x) {
        control.setMinimumColumns(x);
        invalidate();
    }
    public int getMiniColumns() {
        return control.getMinimumColumns();
    }
    public void setMinimumColumns(int x) {
        control.setMinimumColumns(x);
        invalidate();
    }
    public int getMinimumColumns() {
        return control.getMinimumColumns();
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


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
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

//import com.sun.java.swing.plaf.motif.MotifComboBoxUI;
//import com.sun.java.swing.plaf.windows.WindowsComboBoxUI;

import com.viaoa.hub.*;
import com.viaoa.util.*;

public class OAColorComboBox extends OACustomComboBox {
    private static final long serialVersionUID = 1L;

    /**
        Create a ColorComboBox that is bound to a property for the active object in a Hub.
        @param hub is Hub that used to display and edit color property in active object
        @param propertyPath is color property to display/edit
        @param columns is width to use, using average character width
    */
    public OAColorComboBox(Hub hub, String propertyPath, int columns) {
        super(hub, propertyPath, columns, false);
        setupRenderer();
        initialize();
    }

    /**
        Create a ColorComboBox that is bound to a property for the active object in a Hub.
        @param hub is Hub that used to display and edit color property in active object
        @param propertyPath is color property to display/edit
    */
    public OAColorComboBox(Hub hub, String propertyPath) {
        super(hub, propertyPath, false);
        setupRenderer();
        initialize();
    }

    /**
        Create a ColorComboBox that is bound to a property for an object.
        @param obj is object to be bound to
        @param propertyPath is color property to display/edit
        @param columns is width to use, using average character width
    */
    public OAColorComboBox(Object obj, String propertyPath, int columns) {
        super(obj, propertyPath, columns, false);
        setupRenderer();
        initialize();
    }

    /**
        Create a ColorComboBox that is bound to a property for an object.
        @param obj is object to be bound to
        @param propertyPath is color property to display/edit
    */
    public OAColorComboBox(Object obj, String propertyPath) {
        super(obj, propertyPath, false);
        setupRenderer();
        initialize();
    }

    @Override
    public void initialize() {
    }
    
    public void setSelectedItem(Object item) {
        super.setSelectedItem(item);
        repaint();  // since there is not a model
    }

    
    private MyListCellRenderer lblRenderer;
    protected void setupRenderer() {
        lblRenderer = new MyListCellRenderer();
        setRenderer(lblRenderer);
    }
    
    class MyListCellRenderer extends JLabel implements ListCellRenderer {
        int qq;
        Color validColor;
        public MyListCellRenderer() {
        }
        public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
            validColor = getColor();
            setBackground(validColor);
            
            if (index == -1 && value instanceof String) {
                // see super.OACustomComboBox.setColumns(x), which sets protoType value to use to get calc preferred size
                setText(value+"");  
            }
            else setText("");
            
            /*
            String s = "";
            if (validColor != null) {
                s = "Color is (RGB)" + validColor.getRed() + ", " + validColor.getGreen() + ", " + validColor.getBlue();
            }
            OAColorComboBox.this.setToolTipText(s);
            */
            
            return this;
        }
        public void setBackground(Color c) {
            if (validColor != null) {
                setOpaque(true);
                super.setBackground(validColor);
            }
            else {
                setOpaque(false);
            }
        }
    }

    public void setText(String s) {
        if (lblRenderer != null) lblRenderer.setText(s);
    }

    private MyTableLabel lblRendererTable;
    class MyTableLabel extends JLabel {
        Color color;
        public void paintComponent(Graphics g) {
            Dimension d = getSize();
            if (color != null) {
                g.setColor(color);
                int w = d.width;
                int h = d.height;
                if (w > 6) w -= 6;
                if (h > 6) h -= 6;
                g.fillRect(3,3,w,h);
            }
        }
    }
    
    /** 
        Used to supply the renderer when this component is used in the column of an OATable.
        Can be overwritten to customize the rendering.
    */
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
    public void customizeTableRenderer(JLabel lbl, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column,boolean wasChanged, boolean wasMouseOver) {
        super.customizeTableRenderer(lbl, table, value, isSelected, hasFocus, row, column, wasChanged, wasMouseOver);
        lbl.setText("  ");
        Color color = null;
        Hub h = ((OATable) table).getHub();
        if (h != null) {
            Object obj = h.elementAt(row);
            obj = control.getValue(obj);
            //was:  obj = OAReflect.getPropertyValue(obj, control.getGetMethods());
            if (obj instanceof Color) color = (Color) obj;
        }
        if (lblRendererTable == null) {
            lblRendererTable = new MyTableLabel();
            lblRendererTable.setText("  ");
        }
        lblRendererTable.color = color;
        Object obj = ((OATable) table).getObjectAt(row, column);
        customizeRenderer(lbl, obj, value, isSelected, hasFocus, row, wasChanged, wasMouseOver);
    }
    @Override
    public String getTableToolTipText(JTable table, int row, int col, String defaultValue) {
        Object obj = ((OATable) table).getObjectAt(row, col);
        getToolTipText(obj, row, defaultValue);
        return defaultValue;
    }

    

    /**
        Returns the color that is currently selected.
    */
    public Color getColor() {
        Object obj = getSelectedItem();
        Color c = null;
        if (obj instanceof Color) c = (Color) obj;
        else {
            if (obj != null && obj.getClass().equals(control.getHub().getObjectClass())) {
                obj = control.getValue(obj);
            	//was: obj = OAReflect.getPropertyValue(obj, control.getGetMethods());
                if (obj instanceof Color) c = (Color) obj;
            }
        }
        return c;
    }

    /**
        the color that is currently selected.
    */
    public void setColor(Color color) {
        setSelectedItem(color);
    }

    /**
        override to create popup calendar
    */
    public void updateUI() {
	    ComboBoxUI cui = (ComboBoxUI) UIManager.getUI(this);
/*	20200304 windows only options    
	    if (cui instanceof MotifComboBoxUI) {
	        cui = new MotifComboBoxUI() {
	            protected ComboPopup createPopup() {
	                return new CBColorPopup( (OAColorComboBox)comboBox );
	            }
	        };
	    }
	    else if (cui instanceof WindowsComboBoxUI) {
	        cui = new WindowsComboBoxUI() {
	            protected ComboPopup createPopup() {
	                return new CBColorPopup( (OAColorComboBox)comboBox );
	            }
	        };
	    }
	    else cui = new MetalComboBoxUI() {
	        protected ComboPopup createPopup() {
	            return new CBColorPopup( (OAColorComboBox)comboBox );
	        }
	    };
*/	    
        cui = new MetalComboBoxUI() {
            protected ComboPopup createPopup() {
                return new CBColorPopup( (OAColorComboBox)comboBox );
            }
        };
        setUI(cui);
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
}


class CBColorPopup implements ComboPopup, MouseMotionListener, MouseListener, KeyListener, PopupMenuListener {
	protected JPopupMenu popup;
    private OAColorComboBox comboBox;
    private MyColorPanel colorPanel;

	public CBColorPopup(final OAColorComboBox comboBox) {
	    this.comboBox = comboBox;

	    popup = new JPopupMenu();
	    popup.setBorder(BorderFactory.createLineBorder(Color.black));
	    popup.setLayout(new BorderLayout());
	    popup.addPopupMenuListener(this);

	    colorPanel = new MyColorPanel() {
        	public void setColor(Color c) {
        	    CBColorPopup.this.setColor(c);
        	    super.setColor(c);
        	}
	    };
	    
	    popup.add(colorPanel, BorderLayout.NORTH);
	    
	    JPanel pan = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
	    JButton cmd = new JButton("Clear");
	    cmd.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    	    CBColorPopup.this.setColor(null);
	    	}
	    });
	    OAButton.setup(cmd);
	    pan.add(cmd);

	    cmd = new JButton("More ...");
	    cmd.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		popup.setVisible(false);
	    		Color c = JColorChooser.showDialog(comboBox, "Select Color", getColor());
	    		if (c != null) CBColorPopup.this.setColor(c);
	    	}
	    });
	    OAButton.setup(cmd);
	    pan.add(cmd);

	    popup.add(pan, BorderLayout.SOUTH);
	    
	    popup.pack();
	}
	
    private boolean bSettingColor;
    public void setColor(Color color) {
        if (bSettingColor) return;
        bSettingColor = true;
        comboBox.setSelectedItem(color);
        colorPanel.setColor(color);
        comboBox.control.updatePropertyValue(color);
        bSettingColor = false;
    }
    
    public Color getColor() {
        return colorPanel.getColor();
    }

    public void show() {
        bSettingColor = true;
        colorPanel.setColor(comboBox.getColor());
        bSettingColor = false;
	    popup.show(comboBox, 0, comboBox.getHeight());
    }

	public void hide() {
	    popup.setVisible(false);
	}

	protected JList list = new JList();
	public JList getList() {
	    return list;
	}

	public MouseListener getMouseListener() {
	    return this;
	}

	public MouseMotionListener getMouseMotionListener() {
	    return this;
	}

	public KeyListener getKeyListener() {
	    return this;
	}

	public boolean isVisible() {
	    return popup.isVisible();
	}

	public void uninstallingUI() {
	    popup.removePopupMenuListener(this);
	}


	// MouseListener
	public void mousePressed( MouseEvent e ) {
		doPopup(e); // 20080515
	}
    public void mouseReleased( MouseEvent e ) {
    }
    

	// something else registered for MousePressed
	public void mouseClicked(MouseEvent e) {
		// 20080515 was: doPopup(e);
	}
	protected void doPopup(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) return;
        if (!comboBox.isEnabled()) return;

	    if (comboBox.isEditable() ) { 
	    	comboBox.getEditor().getEditorComponent().requestFocus();
	    } 
	    else {
	    	comboBox.requestFocus();
	    }
	    togglePopup();
	}

	protected boolean mouseInside = false;
	public void mouseEntered(MouseEvent e) {
	    mouseInside = true;
	}
	public void mouseExited(MouseEvent e) {
	    mouseInside = false;
	}

	// MouseMotionListener
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}

	// KeyListener
	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void keyReleased( KeyEvent e ) {
	    if ( e.getKeyCode() == KeyEvent.VK_SPACE ||
		 e.getKeyCode() == KeyEvent.VK_ENTER ) {
		togglePopup();
	    }
	}

	/**
	 * Variables hideNext and mouseInside are used to
	 * hide the popupMenu by clicking the mouse in the JComboBox
	 */
	public void popupMenuCanceled(PopupMenuEvent e) {}
	protected boolean hideNext = false;
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
//System.out.println("popupMenuWillBecomeInvisible");//qqqq
	    hideNext = mouseInside;
	}
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	}


	protected void togglePopup() {
		//20080515 was:	    if ( isVisible() || hideNext ) {
	    if ( isVisible() ) {

		hide();
	    } else {

		show();
	    }
	    hideNext = false;
	}
}


class MyColorPanel extends JPanel {
	protected Border m_unselectedBorder;
	protected Border m_selectedBorder;
	protected Border m_activeBorder;
	protected Border m_255Border;
	protected Hashtable m_panes;
	protected ColorPane m_selected;
    
	public MyColorPanel() {
		m_unselectedBorder = new CompoundBorder(
			new MatteBorder(1, 1, 1, 1, getBackground()),
			new BevelBorder(BevelBorder.LOWERED,
			Color.white, Color.gray));
		m_selectedBorder = new CompoundBorder(
			new MatteBorder(2, 2, 2, 2, Color.red),
			new MatteBorder(1, 1, 1, 1, getBackground()));
		m_activeBorder = new CompoundBorder(
			new MatteBorder(2, 2, 2, 2, Color.blue),
			new MatteBorder(1, 1, 1, 1, getBackground()));
		m_255Border = new CompoundBorder(
			new MatteBorder(1, 1, 1, 1, Color.darkGray),
			new MatteBorder(2, 2, 2, 2, getBackground()));


	    int[] valuesA = new int[] { 0, 128, 192, 220  };  // values used for rows
	    int[] values = new int[] { 0, 128, 255 };         // values used for columns (for each RGB)

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new BorderLayout());

		int cols = (values.length-1) * 6;
		int rows = (valuesA.length * 2) - 2;
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(rows, cols));
		panel.add(p);

		m_panes = new Hashtable();

        for (int xx=0; xx<2; xx++) {
            boolean bDown = (xx == 0);
            int row = 0;
            if (!bDown) row = 1;
            
            for ( ; row < valuesA.length-(bDown?0:1); row++) {
                int zero = 0;
                int max = 255;
                if (bDown) zero = valuesA[valuesA.length-1-row];
                else max = valuesA[valuesA.length-1-row];
                
                // red=255  blue=255-0
                for (int b=0; b<values.length; b++) {
		            int blue = values[values.length-1-b];
                    if (bDown) {
    		            if (blue < zero) blue = zero;
    		        }
    		        else {
    		            if (blue > max) blue = max;
    		        }
		            Color c = new Color(max, zero, blue);
		            ColorPane pn = new ColorPane(c);
		            p.add(pn);
		            m_panes.put(c, pn);
                }

                // red=255  green=0-255
                for (int g=1; g<values.length; g++) {
		            int green = values[g];
                    if (bDown) {
    		            if (green < zero) green = zero;
    		        }
    		        else {
    		            if (green > max) green = max;
    		        }
		            Color c = new Color(max, green, zero);
		            ColorPane pn = new ColorPane(c);
		            p.add(pn);
		            m_panes.put(c, pn);
                }
                // green=255  red=255-0
                for (int r=1; r<values.length; r++) {
		            int red = values[values.length-1-r];
                    if (bDown) {
    		            if (red < zero) red = zero;
    		        }
    		        else {
    		            if (red > max) red = max;
    		        }
		            Color c = new Color(red, max, zero);
		            ColorPane pn = new ColorPane(c);
		            p.add(pn);
		            m_panes.put(c, pn);
                }
                // green=255  blue=0-255
                for (int b=1; b<values.length; b++) {
		            int blue = values[b];
		            if (bDown) {
    		            if (blue < zero) blue = zero;
    		        }
    		        else {
    		            if (blue > max) blue = max;
    		        }
		            Color c = new Color(zero, max, blue);
		            ColorPane pn = new ColorPane(c);
		            p.add(pn);
		            m_panes.put(c, pn);
                }
                // blue=255  green=255-0
                for (int g=1; g<values.length; g++) {
		            int green = values[values.length-1-g];
                    if (bDown) {
    		            if (green < zero) green = zero;
    		        }
    		        else {
    		            if (green > max) green = max;
    		        }
		            Color c = new Color(zero, green, max);
		            ColorPane pn = new ColorPane(c);
		            p.add(pn);
		            m_panes.put(c, pn);
                }
                // blue=255  red=0-255
                for (int r=1; r<values.length-1; r++) {
		            int red = values[r];
		            if (bDown) {
    		            if (red < zero) red = zero;
    		        }
    		        else {
    		            if (red > max) red = max;
    		        }
		            Color c = new Color(red, zero, max);
		            ColorPane pn = new ColorPane(c);
		            p.add(pn);
		            m_panes.put(c, pn);
                }
            }
        }

		p = new JPanel();
		p.setBorder(new EmptyBorder(5, 0,0,0));
		p.setLayout(new GridLayout(1, cols));
		
		panel.add(p, BorderLayout.SOUTH);

        // white to black
        int x = (int) 256/cols;
        for (int i=0; i<cols; i++) {
            int c = (cols - i) * x;
            
            if (i == 0) c = 255;
            if (i+1 == cols) c = 0;
            
            Color color = new Color(c,c,c);
		    ColorPane pn = new ColorPane(color);
		    p.add(pn);
		    m_panes.put(color, pn);
        }
		add(panel);
	}

	public void setColor(Color c) {
        if (c == null) c = Color.black;
		Object obj = m_panes.get(c);
		if (m_selected != null) m_selected.setSelected(false);
		m_selected = (ColorPane)obj;
        if (obj == null) return;
		m_selected.setSelected(true);
	}

	public Color getColor() {
		if (m_selected == null)
			return null;
		return m_selected.getColor();
	}

	class ColorPane extends JPanel implements MouseListener {
		protected Color m_c;
		protected boolean m_selected;
		boolean b255;

		public ColorPane(Color c) {
            int r = c.getRed();
            int g = c.getGreen();
            int b = c.getBlue();
		    this.b255 = (r==0||r==255) && (g==0||g==255) && (b==0||b==255);
			m_c = c;
			setBackground(c);
			setBorder(b255 ? m_255Border : m_unselectedBorder);
			String msg = "R "+r+", G "+g+", B "+b;
			setToolTipText(msg);
			addMouseListener(this);
		}

		public Color getColor() {
			return m_c;
		}

		public Dimension getPreferredSize() {
			return new Dimension(15, 15);
		}

		public Dimension getMaximumSize() {
			return getPreferredSize();
		}

		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		public void setSelected(boolean selected) {
			m_selected = selected;
			if (m_selected)
				setBorder(m_selectedBorder);
			else {
				if (b255) setBorder(m_255Border);
				else setBorder(m_unselectedBorder);
			}
		}

		public boolean isSelected() {
			return m_selected;
		}

		public void mousePressed(MouseEvent e) {}

		public void mouseClicked(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {
			setColor(m_c);
			MenuSelectionManager.defaultManager().clearSelectedPath();
		}

		public void mouseEntered(MouseEvent e) {
			setBorder(m_activeBorder);
		}

		public void mouseExited(MouseEvent e) {
			setBorder(m_selected ? m_selectedBorder :
				b255 ? m_255Border : m_unselectedBorder);
		}
	}
}

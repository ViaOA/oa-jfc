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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import com.viaoa.util.OAArray;

/**
 * Manages multiple buttons to be choosen from a dropdown, displaying an active button.
 * 
 * Note: there is no action event for this button.  The buttons that are added will
 * get the action event by having the main button call the current active buttons doClick method.
 * @author vvia
 *
 */
public class OAMultiButtonSplitButton extends OASplitButton {
    private static final long serialVersionUID = 1L;
    public boolean DEBUG;

    private JPopupMenu popup;
    private JButton cmdSelected;
    private boolean bShowTextInSelectedButton = true;
    private boolean bShowSelectedButton = true;
    private boolean bAllowChangeMasterButton = true;
    

    private JButton[] buttons = new JButton[0];
    private final JPanel panHidden; // so that each comp/button will have a parent 
    private final GridBagConstraints gc;
    
    
    public OAMultiButtonSplitButton() {
        popup = new JPopupMenu() {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.width = Math.max(d.width, OAMultiButtonSplitButton.this.mainButton.getPreferredSize().width);
                return d;
            }
        };
        
        panHidden = new JPanel();
        Dimension d = new Dimension(0,0);
        panHidden.setMaximumSize(d);
        panHidden.setPreferredSize(d);
        panHidden.setMinimumSize(d);
        super.add(panHidden, BorderLayout.WEST);
        
        popup.setInvoker(this);
        popup.setLayout(new GridBagLayout());
        
        gc = new GridBagConstraints();
        Insets ins = new Insets(1, 3, 1, 3);
        gc.insets = ins;
        gc.anchor = gc.NORTH;
        gc.gridwidth = gc.REMAINDER;
        gc.fill = gc.BOTH;
        gc.weightx = gc.weighty = 1.0; 
        
        
        dropDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OAMultiButtonSplitButton sb = OAMultiButtonSplitButton.this;                
                popup.show(sb, 0, sb.getHeight());
            }
        });
        
        mainButton.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("enabled".equalsIgnoreCase(evt.getPropertyName())) {
                }
            }
        });
        
    }
    
    public JPopupMenu getPopupMenu() {
        return popup;
    }

    protected void update() {
        if (buttons == null || buttons.length == 0) return;

        boolean bEnabled = false;
        boolean bVisible = false;
        
        for (JButton button : buttons) {
            Container cx = button.getParent();
            if (cx == null || cx == popup || cx == panHidden) {
                popup.add(button, gc);
            }
            bEnabled = bEnabled || button.isEnabled();
            bVisible = bVisible || button.isVisible();
        }

        if (!bAllowChangeMasterButton) {
            cmdSelected = null;
            for (JButton button : buttons) {
                if (button.isEnabled()) {
                    cmdSelected = button;
                    Container cx = button.getParent();
                    if (cx == null || cx == popup || cx == panHidden) {
                        panHidden.add(cmdSelected, gc);
                    }
                    break;
                }
            }
            if (cmdSelected == null && buttons.length > 0) {
                cmdSelected = buttons[0];
                Container cx = cmdSelected.getParent();
                if (cx == null || cx == popup || cx == panHidden) {
                    panHidden.add(cmdSelected, gc);
                }
            }
        }

        updateMain(cmdSelected);
        setEnabled(bEnabled);
        setVisible(bVisible);
    }
    
    public void setSelected(JButton cmd) {
        if (cmd == null) return;
        cmdSelected = cmd;
        update();
    }
    
    protected void updateMain(JButton cmd) {
        if (bShowTextInSelectedButton) {
            mainButton.setText(cmdSelected.getText());
            mainButton.setFont(cmdSelected.getFont());
        }
        mainButton.setIcon(cmdSelected.getIcon());
        mainButton.setToolTipText(cmdSelected.getToolTipText());
        mainButton.setEnabled(cmdSelected.isEnabled());
    }
    public void setSelected(int pos) {
        if (!bShowSelectedButton) return;
        if (pos < 0 || buttons == null || pos >= buttons.length) return;
        setSelected(buttons[pos]);
    }
    
    
    public void setShowSelectedButton(boolean b) {
        bShowSelectedButton = b;
    }
    public boolean getShowSelectedButton() {
        return bShowSelectedButton;
    }
    
    public void setShowTextInSelectedButton(boolean b) {
        bShowTextInSelectedButton = b;
    }

    // can a dropdown button become the main button?
    public void setAllowChangeMasterButton(boolean b) {
        bAllowChangeMasterButton = b;
    }
    
    @Override
    protected void setupMainButtonListener() {
        mainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bShowSelectedButton) {
                    if (cmdSelected != null) {
                        cmdSelected.doClick();
                    }
                }
                else {
                    // show popup
                    dropDownButton.doClick();
                }
            }
        });
    }
    
  
    public int getButtonCount() {
        return buttons==null?0:buttons.length;
    }
    public JButton[] getButtons() {
        return buttons;
    }
    
    public void addButton(final JButton cmd, boolean bParmNotUsedA) {
        addButton(cmd);
    }
    
    public void addButton(final JButton cmd) {
        if (cmd == null) return;

        cmd.setHorizontalAlignment(SwingConstants.LEFT); 

        buttons = (JButton[]) OAArray.add(JButton.class, buttons, cmd);
        popup.add(cmd, gc);
        
        cmd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelected(cmd);
                popup.setVisible(false);
            }
        });
        
        cmd.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
            }
            @Override
            public void componentMoved(ComponentEvent e) {
            }
            @Override
            public void componentShown(ComponentEvent e) {
                update();
            }
            @Override
            public void componentHidden(ComponentEvent e) {
                update();
            }
        });

        cmd.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("enabled".equalsIgnoreCase(evt.getPropertyName())) {
                    update();
                }
            }
        });
        
        if (buttons.length == 1) cmdSelected = cmd;
        update();
    }
    
    
    public Dimension getPreferredSize() {
        if (mainButton == null) return super.getPreferredSize();
        
        Dimension d = mainButton.getPreferredSize();
    
        if (bShowTextInSelectedButton) {
            for (JButton b : buttons) {
                Dimension d2 = b.getPreferredSize();
                d.width = Math.max(d.width, d2.width);
            }
        }
        
        Dimension d2 = dropDownButton.getPreferredSize();
        d.width += d2.width + 5;
        d.height = Math.max(d.height, d2.height);
        return d;
    }
    
    
    
    
    
    static JButton bx;
    public static void main(String[] args) {
        final JFrame frm = new JFrame();
        frm.setLayout(new FlowLayout());
        frm.setDefaultCloseOperation(frm.EXIT_ON_CLOSE);

        OAMultiButtonSplitButton multiButton = new OAMultiButtonSplitButton();
        multiButton.setAllowChangeMasterButton(false);

        multiButton.setText("This is the button text");
        
        JButton but = new JButton("Test 1");
        but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("1");
            }
        });
        multiButton.addButton(but);
        bx = but;

        but = new JButton("Test2 another drop down button");
        but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("2");
               // bx.setVisible(!bx.isVisible());
            }
        });
        multiButton.addButton(but);

        multiButton.addButton(new JButton("Test 3 Button A"));
        multiButton.addButton(new JButton("Test 4 Button Z"));
        
        multiButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });

        frm.add(multiButton);

        frm.pack();
        
        //but.setVisible(false);
        
        Dimension d = frm.getSize();
        d.width *= 2;
        d.height *= 2;
        frm.setSize(d);
        frm.setLocation(1600, 500);
        frm.setVisible(true);
    }
}

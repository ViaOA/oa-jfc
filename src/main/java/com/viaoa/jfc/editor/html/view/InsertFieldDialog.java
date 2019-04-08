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
package com.viaoa.jfc.editor.html.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.viaoa.hub.Hub;
import com.viaoa.jfc.OAComboBox;
import com.viaoa.jfc.OATextField;
import com.viaoa.jfc.OATreeComboBox;
import com.viaoa.jfc.propertypath.OAPropertyPathTree;
import com.viaoa.jfc.propertypath.model.oa.ObjectDef;


public class InsertFieldDialog extends JDialog {

    protected boolean bCancelled;
    private Hub<ObjectDef> hub;
    private OATextField txt;
    private OATreeComboBox cboTree;
    private OAComboBox cboCustomFields;
    private Hub<String> hubCustomFields;
    private OAComboBox cboCustomCommands;
    private Hub<String> hubCustomCommands;
    

    public InsertFieldDialog(Window parent, Hub<ObjectDef> hub, Hub<String> hubCustomFields, Hub<String> hubCustomCommands) {
        super(parent, "Insert Field", ModalityType.APPLICATION_MODAL);

        this.hub = hub;
        this.hubCustomFields = hubCustomFields;
        this.hubCustomCommands = hubCustomCommands;
        
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout());
        add(getPanel(), BorderLayout.CENTER);
        
        this.pack();

        this.setLocationRelativeTo(parent);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            }
            @Override
            public void windowOpened(WindowEvent e) {
            }
        });
    }

    public void setVisible(boolean b) {
        if (b) bCancelled = true;
        super.setVisible(b);
        if (b) txt.requestFocus();
    }
    
    public boolean wasCanceled() {
        return bCancelled;
    }


    final JPanel panCombo = new JPanel(new BorderLayout(0,0));
    
    protected JPanel getPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel pan = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        Insets ins = new Insets(1, 1, 1, 10);
        gc.insets = ins;
        gc.anchor = gc.NORTHWEST;
        JLabel lbl;
        
        pan.add(new JLabel("Data Field:"), gc);
        gc.fill = gc.HORIZONTAL;
        
        panCombo.add(getComboBox(), BorderLayout.CENTER);
        pan.add(panCombo, gc);
        gc.fill = gc.NONE;
        gc.gridwidth = gc.REMAINDER;
        pan.add(new JLabel(""), gc);
        gc.gridwidth = 1;
        
        pan.add(new JLabel("Custom Field:"), gc);
        gc.fill = gc.HORIZONTAL;
        pan.add(getCustomFieldsComboBox(), gc);
        gc.fill = gc.NONE;
        gc.gridwidth = gc.REMAINDER;
        pan.add(new JLabel(""), gc);
        gc.gridwidth = 1;

        pan.add(new JLabel("Custom Command:"), gc);
        gc.fill = gc.HORIZONTAL;
        pan.add(getCustomCommandsComboBox(), gc);
        gc.fill = gc.NONE;
        gc.gridwidth = gc.REMAINDER;
        pan.add(new JLabel(""), gc);
        gc.gridwidth = 1;
        
        // bottom
        gc.gridwidth = gc.REMAINDER;
        gc.weightx = 1.0;
        gc.weighty = 1.0;
        pan.add(new JLabel(""), gc);

        panel.add(pan, BorderLayout.NORTH);

        Border border = new CompoundBorder(new EmptyBorder(5,5,5,5), new TitledBorder(""));
        border = new CompoundBorder(border, new EmptyBorder(5,5,5,5));
        pan.setBorder(border);
        panel.add(pan, BorderLayout.NORTH);
        
        
        pan = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        pan.add(getOkButton());
        pan.add(getCancelButton());
        pan.setBorder(border);
        panel.add(pan, BorderLayout.SOUTH);
        
        
        return panel;
    }
    
    private JButton butSelect;
    public JButton getOkButton() {
        if (butSelect == null) {
            butSelect = new JButton("Ok");
            ActionListener al = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    InsertFieldDialog.this.onOk();
                }
            };
            butSelect.addActionListener(al);
            butSelect.addActionListener(al);
            butSelect.registerKeyboardAction(al, "cmdOK", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_IN_FOCUSED_WINDOW);
            butSelect.setMnemonic('O');
        }
        return butSelect;
    }
    private JButton butCancel;
    public JButton getCancelButton() {
        if (butCancel == null) {
            butCancel = new JButton("Cancel");
            butCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    InsertFieldDialog.this.onCancel();
                }
            });
            butCancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "cancel");
            butCancel.getActionMap().put("cancel",
                    new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                            InsertFieldDialog.this.onCancel();
                        }
                    });
        }
        return butCancel;
    }

    protected void onCancel() {
        bCancelled = true;
        setVisible(false);
    }
    protected void onOk() {
        bCancelled = false;
        setVisible(false);
    }
    
    public OATextField getTextField() {
        if (txt == null) {
            txt = new OATextField();
            txt.setColumns(20);
        }
        return txt;
    }
    
    public OAComboBox getCustomFieldsComboBox() {
        if (cboCustomFields != null) return cboCustomFields;
        cboCustomFields = new OAComboBox(hubCustomFields, "", 20);
        //cboCustomFields.setPopupColumns(24);
        return cboCustomFields;
    }

    public OAComboBox getCustomCommandsComboBox() {
        if (cboCustomCommands != null) return cboCustomCommands;
        cboCustomCommands = new OAComboBox(hubCustomCommands, "", 20);
        return cboCustomCommands;
    }

    private OAPropertyPathTree tree;
    public OAPropertyPathTree getPropertyPathTree() {
        if (tree == null) {
            tree = new OAPropertyPathTree(hub, false, true, true, true, true) {
                @Override
                public void propertyPathCreated(String propertyPath) {
                    getTextField().setText(propertyPath);
                    cboTree.hidePopup();
                }
            };
        }
        return tree;
    }
    public OATreeComboBox getComboBox() {
        if (cboTree == null) {
            cboTree = new OATreeComboBox(getPropertyPathTree(), hub, "name");
            cboTree.setEditor(getTextField());
        }
        return cboTree;
    }
    public void setComboBox(OATreeComboBox cbo) {
        cboTree = cbo;
        if (cbo != null) {
            panCombo.removeAll();
            panCombo.add(cbo, BorderLayout.CENTER);
        }
    }
    
    public static void main(String[] args) {
        Hub<ObjectDef> hub = new Hub<ObjectDef>(ObjectDef.class);
        Hub<String> hub2 = new Hub<>(String.class);
        Hub<String> hub3 = new Hub<>(String.class);
        InsertFieldDialog dlg = new InsertFieldDialog(null, hub, hub2, hub3);
        dlg.setVisible(true);
    }
}

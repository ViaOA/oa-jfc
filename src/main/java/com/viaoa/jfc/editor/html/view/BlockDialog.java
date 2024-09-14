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
import com.viaoa.jfc.*;
import com.viaoa.jfc.editor.html.oa.Block;
import com.viaoa.jfc.editor.html.oa.DocElement;


/**
 * Used to edit document paragraphs (blocks).
 * @author vvia
 *
 */
public class BlockDialog extends JDialog {

    protected boolean bCancelled;
    private Hub<Block> hubBlock;
    private Hub<DocElement> hubDocElement;
    private Hub<DocElement> hubRootDocElement;

    public BlockDialog(Window parent, Hub<Block> hubBlock, Hub<DocElement> hubRootDocElement, Hub<DocElement> hubDocElement) {
        super(parent, "Paragraph properties", ModalityType.APPLICATION_MODAL);
        this.hubBlock = hubBlock;
        this.hubRootDocElement = hubRootDocElement;
        this.hubDocElement = hubDocElement;
        
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        
        setLayout(new BorderLayout());
        add(new JScrollPane(getPanel()), BorderLayout.CENTER);
        
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
    }
    
    public boolean wasCanceled() {
        return bCancelled;
    }

    
    
    private OATreeComboBox treeComboBox;
    public OATreeComboBox getTreeComboBox() {
        if (treeComboBox == null) {
            OATree tree = new OATree(24, 58);
            OATreeNode node = new OATreeNode("Name", hubRootDocElement, hubDocElement);
            tree.add(node);
            node.add(node, "name"); // recursive
            treeComboBox = new OATreeComboBox(tree, hubDocElement, "name");
            treeComboBox.setColumns(24);
            treeComboBox.setMaxColumns(52);
        }
        return treeComboBox;
    }

    
    protected JPanel getPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel pan = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        Insets ins = new Insets(1, 1, 1, 10);
        gc.insets = ins;
        gc.anchor = gc.NORTHWEST;
        JLabel lbl;
        OATextField txt;
        JComponent comp;

        pan.add(new JLabel("HTML Element:"), gc);
        gc.gridwidth = gc.REMAINDER;
        gc.weightx = 1d;
        gc.fill = gc.HORIZONTAL;
        comp = new OAResizePanel(getTreeComboBox(), 50);
        pan.add(comp, gc);
        gc.gridwidth = 1;
        gc.weightx = 0d;
        gc.fill = gc.NONE;        
                
        pan.add(new JLabel("Width:"), gc);
        txt = new OATextField(hubBlock, Block.P_Width, 4);
        txt.setMaxCols(10);
        gc.gridwidth = gc.REMAINDER;
        gc.weightx = 1d;
        gc.fill = gc.HORIZONTAL;
        comp = new OAResizePanel(txt, 50);
        pan.add(comp, gc);
        gc.gridwidth = 1;
        gc.weightx = 0d;
        gc.fill = gc.NONE;        

        pan.add(new JLabel("Height:"), gc);
        txt = new OATextField(hubBlock, Block.P_Height, 4);
        txt.setMaxCols(10);
        gc.gridwidth = gc.REMAINDER;
        gc.weightx = 1d;
        gc.fill = gc.HORIZONTAL;
        comp = new OAResizePanel(txt, 50);
        pan.add(comp, gc);
        gc.gridwidth = 1;
        gc.weightx = 0d;
        gc.fill = gc.NONE;        

        
        pan.add(new JLabel("Background Color"), gc);
        OAColorComboBox ccbo = new OAColorComboBox(hubBlock, Block.P_BackgroundColor, 4);
        gc.gridwidth = gc.REMAINDER;
        pan.add(ccbo, gc);
        gc.gridwidth = 1;
        
        pan.add(new JLabel("Margin:"), gc);
        txt = new OATextField(hubBlock, Block.P_Margin, 3);
        txt.setMaxCols(10);
        pan.add(txt, gc);

        pan.add(new JLabel("Top"), gc);
        txt = new OATextField(hubBlock, Block.P_MarginTop, 3);
        txt.setMaxCols(10);
        pan.add(txt, gc);

        pan.add(new JLabel("Right"), gc);
        txt = new OATextField(hubBlock, Block.P_MarginRight, 3);
        txt.setMaxCols(10);
        pan.add(txt, gc);
        
        pan.add(new JLabel("Bottom"), gc);
        txt = new OATextField(hubBlock, Block.P_MarginBottom, 3);
        txt.setMaxCols(10);
        pan.add(txt, gc);
        
        pan.add(new JLabel("Left"), gc);
        txt = new OATextField(hubBlock, Block.P_MarginLeft, 3);
        txt.setMaxCols(10);
        gc.gridwidth = gc.REMAINDER;
        pan.add(txt, gc);
        gc.gridwidth = 1;
        
        
        pan.add(new JLabel("Padding:"), gc);
        txt = new OATextField(hubBlock, Block.P_Padding, 3);
        txt.setMaxCols(10);
        pan.add(txt, gc);

        pan.add(new JLabel("Top"), gc);
        txt = new OATextField(hubBlock, Block.P_PaddingTop, 3);
        txt.setMaxCols(10);
        pan.add(txt, gc);

        pan.add(new JLabel("Right"), gc);
        txt = new OATextField(hubBlock, Block.P_PaddingRight, 3);
        txt.setMaxCols(10);
        pan.add(txt, gc);
        
        pan.add(new JLabel("Bottom"), gc);
        txt = new OATextField(hubBlock, Block.P_PaddingBottom, 3);
        txt.setMaxCols(10);
        pan.add(txt, gc);

        pan.add(new JLabel("Left"), gc);
        txt = new OATextField(hubBlock, Block.P_PaddingLeft, 3);
        txt.setMaxCols(10);
        gc.gridwidth = gc.REMAINDER;
        pan.add(txt, gc);
        gc.gridwidth = 1;
        
        
        pan.add(new JLabel("Border Width:"), gc);
        txt = new OATextField(hubBlock, Block.P_BorderWidth, 3);
        txt.setMaxCols(10);
        pan.add(txt, gc);

        pan.add(new JLabel("Top"), gc);
        txt = new OATextField(hubBlock, Block.P_BorderTopWidth, 3);
        txt.setMaxCols(10);
        pan.add(txt, gc);

        pan.add(new JLabel("Right"), gc);
        txt = new OATextField(hubBlock, Block.P_BorderRightWidth, 3);
        txt.setMaxCols(10);
        pan.add(txt, gc);
        
        pan.add(new JLabel("Bottom"), gc);
        txt = new OATextField(hubBlock, Block.P_BorderBottomWidth, 3);
        txt.setMaxCols(10);
        pan.add(txt, gc);
        
        pan.add(new JLabel("Left"), gc);
        txt = new OATextField(hubBlock, Block.P_BorderLeftWidth, 3);
        txt.setMaxCols(10);
        gc.gridwidth = gc.REMAINDER;
        pan.add(txt, gc);
        gc.gridwidth = 1;

        pan.add(new JLabel("Border Color"), gc);
        ccbo = new OAColorComboBox(hubBlock, Block.P_BorderColor, 4);
        pan.add(ccbo, gc);
        
        pan.add(new JLabel("Top"), gc);
        ccbo = new OAColorComboBox(hubBlock, Block.P_BorderTopColor, 4);
        pan.add(ccbo, gc);

        pan.add(new JLabel("Right"), gc);
        ccbo = new OAColorComboBox(hubBlock, Block.P_BorderRightColor, 4);
        pan.add(ccbo, gc);
        
        pan.add(new JLabel("Bottom"), gc);
        ccbo = new OAColorComboBox(hubBlock, Block.P_BorderBottomColor, 4);
        pan.add(ccbo, gc);
        
        pan.add(new JLabel("Left"), gc);
        ccbo = new OAColorComboBox(hubBlock, Block.P_BorderLeftColor, 4);
        gc.gridwidth = gc.REMAINDER;
        pan.add(ccbo, gc);
        gc.gridwidth = 1;
        
        
        
        pan.add(new JLabel("Border Style"), gc);
        Hub<String> h = Block.getBorderStyles();
        h.setLinkHub(hubBlock, Block.P_BorderStyle);
        OAComboBox cbo = new OAComboBox(h, "", 7);
        pan.add(cbo, gc);
        
        pan.add(new JLabel("Top"), gc);
        h = Block.getBorderStyles();
        h.setLinkHub(hubBlock, Block.P_BorderTopStyle);
        cbo = new OAComboBox(h, "", 7);
        pan.add(cbo, gc);

        pan.add(new JLabel("Right"), gc);
        h = Block.getBorderStyles();
        h.setLinkHub(hubBlock, Block.P_BorderRightStyle);
        cbo = new OAComboBox(h, "", 7);
        pan.add(cbo, gc);
        
        pan.add(new JLabel("Bottom"), gc);
        h = Block.getBorderStyles();
        h.setLinkHub(hubBlock, Block.P_BorderBottomStyle);
        cbo = new OAComboBox(h, "", 7);
        pan.add(cbo, gc);
        
        pan.add(new JLabel("Left"), gc);
        h = Block.getBorderStyles();
        h.setLinkHub(hubBlock, Block.P_BorderLeftStyle);
        cbo = new OAComboBox(h, "", 7);
        gc.gridwidth = gc.REMAINDER;
        pan.add(cbo, gc);
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
        pan.add(getApplyButton());
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
                    BlockDialog.this.onOk();
                }
            };
            butSelect.addActionListener(al);
            butSelect.registerKeyboardAction(al, "cmdOK", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_IN_FOCUSED_WINDOW);
            butSelect.setMnemonic('O');
        }
        return butSelect;
    }
    private JButton butApply;
    public JButton getApplyButton() {
        if (butApply == null) {
            butApply = new JButton("Apply");
            ActionListener al = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BlockDialog.this.onApply();
                }
            };
            butApply.addActionListener(al);
            butApply.registerKeyboardAction(al, "cmdApply", KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.ALT_DOWN_MASK, false), JComponent.WHEN_IN_FOCUSED_WINDOW);
            butApply.setMnemonic('A');
        }
        return butApply;
    }
    private JButton butCancel;
    public JButton getCancelButton() {
        if (butCancel == null) {
            butCancel = new JButton("Cancel");
            butCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BlockDialog.this.onCancel();
                }
            });
            butCancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "cancel");
            butCancel.getActionMap().put("cancel",
                    new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                            BlockDialog.this.onCancel();
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
    protected void onApply() {
    }
    

    public static void main(String[] args) {
        Hub hub = new Hub<Block>(Block.class);
        hub.add(new Block());
        hub.setPos(0);
        BlockDialog dlg = new BlockDialog(null, hub, new Hub<DocElement>(DocElement.class), new Hub<DocElement>(DocElement.class));
        dlg.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        dlg.addWindowListener(new WindowListener() {
            
            @Override
            public void windowOpened(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowIconified(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowDeiconified(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowDeactivated(WindowEvent e) {
                System.exit(0);
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
            
            @Override
            public void windowClosed(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void windowActivated(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }
        });
        dlg.setVisible(true);
        
    }
}

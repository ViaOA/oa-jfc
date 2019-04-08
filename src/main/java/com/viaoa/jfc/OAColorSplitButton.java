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

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;

import com.viaoa.image.ColorIcon;
import com.viaoa.image.ColoredLineUnderIcon;


/**
 * SplitButton that includes a dropdown color chooser.  The color chooser
 * has a button to display a custom JColorChooser dialog.
 * @author vvia
 */
public class OAColorSplitButton extends OASplitButton {
    private OAColorPopup popupColor;
    private ColoredLineUnderIcon colorIcon;
    private JColorChooser colorChooser;
    private JDialog dlgColorChooser;
    private Color color;
    private String colorChooserTitle;
    
    public OAColorSplitButton() {
        super();
        setIcon(null);  // creates default color icon
        setFocusable(false);
        
        JButton cmdDropDown = super.getDropDownButton();
        cmdDropDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OAColorSplitButton.this.getColorPopup().show();
            }
        });
    }        

    /**
     * This will create a custom icon, using the supplied icon 
     * with the current color painted under it.
     */
    public void setIcon(Icon icon) {
        colorIcon = new ColoredLineUnderIcon(icon);
        colorIcon.setColor(color);
        super.setIcon(colorIcon);
    }

    public OAColorPopup getColorPopup() {
        if (popupColor == null) {
            popupColor = new OAColorPopup(this) {
                @Override
                public void setColor(Color color) {
                    super.setColor(color);
                    OAColorSplitButton.this.setColor(color);
                    OAColorSplitButton.this.fireActionPerformed();
                }
                @Override
                public void onShowColorChooser() {
                    popupColor.setVisible(false);
                    OAColorSplitButton.this.getColorChooserDialog().setVisible(true);
                }
            };
        }
        return popupColor;
    }

    
    public void setColorChooserTitle(String colorChooserTitle) {
        this.colorChooserTitle = colorChooserTitle;
        if (dlgColorChooser != null) dlgColorChooser.setTitle(colorChooserTitle);
    }
    public void setMoreButtonText(String s) {
        getColorPopup().setMoreButtonText(s);
    }
    public void setClearButtonText(String s) {
        getColorPopup().setClearButtonText(s);
    }
    
    protected JDialog getColorChooserDialog() {
        if (dlgColorChooser == null) {
            colorChooser = new JColorChooser();
            colorChooser.setColor(color);
            
            dlgColorChooser = JColorChooser.createDialog(this, colorChooserTitle, true, colorChooser,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Color color = colorChooser.getColor();
                        OAColorSplitButton.this.setColor(color);
                        OAColorSplitButton.this.fireActionPerformed();
                    }
                },
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // CANCEL selected
                    }
                }
            );
        }
        return dlgColorChooser;
    }
    
    protected void fireActionPerformed() {
        ActionEvent e = new ActionEvent(this, 0, "");
        super.fireActionPerformed(e);
    }
    
    
    public void setColor(Color color) {
        this.color = color;
        if (colorChooser != null) colorChooser.setColor(color);
        colorIcon.setColor(color);
        repaint();
    }
    public void setCurrentColor(Color color) {
        getColorPopup().setCurrentColor(color);
    }
    
    public Color getColor() {
        return color;
    }
    
    
    
    public static void main(String[] args) {
        final JFrame frm = new JFrame();
        frm.setDefaultCloseOperation(frm.EXIT_ON_CLOSE);

        final OAColorSplitButton cmd = new OAColorSplitButton();
        cmd.setColorChooserTitle("Select Font Color"); 
        cmd.setText("Font");
        // Icon icon = new ImageIcon(ColorSplitButton.class.getResource("image/fontColor.gif"));
        // cmd.setIcon(icon);
        cmd.setToolTipText("Font color");
        
        cmd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frm.getContentPane().setBackground(cmd.getColor());
            }
        });

        cmd.setColorChooserTitle("");
        cmd.setMoreButtonText("more ...");
        cmd.setClearButtonText("clear");
    
        
        frm.add(cmd, BorderLayout.SOUTH);

        frm.setVisible(true);
        frm.pack();
        Dimension d = frm.getSize();
        d.width *= 2;
        d.height *= 2;
        frm.setSize(d);
        frm.setLocation(1600, 500);
    }
    
}


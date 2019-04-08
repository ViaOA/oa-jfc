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
package com.viaoa.jfc.editor.image.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.event.ChangeListener;

public class ContrastPanel extends JPanel {
    private JButton cmdOk;
    private JSlider slider;
    private JLabel lbl;
    
    public ContrastPanel() {
        super(new FlowLayout());
       
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 1, 1, 2);
    
        
        slider = new JSlider(-10, 10);
        slider.setValue(0);
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        //slider.setSnapToTicks(true);  // wont allow increments
        slider.setPaintTicks(true);
        slider.setExtent(2);
        slider.setLabelTable(slider.createStandardLabels(75));
        slider.setPaintLabels(true);
        
        
        add(slider);
        add(getLabel());
        
        add(getOkCommand());
    }
    
    public JSlider getContrastSlider() {
        return slider;
    }
    
    public JButton getOkCommand() {
        if (cmdOk == null) {
            cmdOk = new JButton("Ok");
            cmdOk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ContrastPanel.this.onOkCommand();
                }
            });
            String cmdName = "onClick";
            cmdOk.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0,false), cmdName);
            cmdOk.getActionMap().put(cmdName, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    onOkCommand();
                }
            });
        }
        return cmdOk;
    }
    
    public JLabel getLabel() {
        if (lbl == null) {
            lbl = new JLabel("     ") {
                Dimension d;
                @Override
                public Dimension getPreferredSize() {
                    if (d == null) {
                        d = super.getPreferredSize();
                        d.width += 10;
                    }
                    return d;
                }
            };
            lbl.setOpaque(false);
        }
        return lbl;
    }
    
    
    protected void onOkCommand() {
    }
}






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
package com.viaoa.jfc.editor.image.control;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

import com.viaoa.jfc.editor.image.view.ScalePanel;

/**
 * Used to manage a ScalePanel.
 * @author vincevia
 *
 */
public abstract class ScalePanelController {

    private ScalePanel panScale;
    private JPopupMenu popupScale;
    private boolean updatingComponents;
    private double scale;
    

    public ScalePanelController() {
    }

    public ScalePanel getScalePanel() {
        if (panScale == null) {
            panScale = new ScalePanel() {
                @Override
                public void onScaleCommand() {
                    ScalePanelController.this.onPerformScale(scale);
                }
                @Override
                public void onUseZoomScaleCommand() {
                    ScalePanelController.this.onUseZoomScale();
                }
                @Override
                protected void onScalePercentChange(int zoom) {
                    scale = (zoom/100.0);
                    updateComponents();
                }
                @Override
                protected void onWidthChange(int newWidth) {
                    if (updatingComponents) return;
                    if (newWidth > 0) {
                        double h = getImageWidth();
                        scale = (newWidth/h);
                        updateComponents();
                    }
                }
                @Override
                protected void onHeightChange(int newHeight) {
                    if (updatingComponents) return;
                    if (newHeight > 0) {
                        double h = getImageHeight();
                        scale = (newHeight/h);
                        updateComponents();
                    }
                }
            };
            
            
        }
        return panScale;
    }

    /** 
     * Popup used to display panel.
     * @return
     */
    protected JPopupMenu getScalePopup() {
        if (popupScale != null) return popupScale;
        
        popupScale = new JPopupMenu();
        popupScale.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        popupScale.setLayout(new BorderLayout());
        JPanel pan = getScalePanel();
        Border border = new CompoundBorder(new LineBorder(Color.lightGray), new EmptyBorder(2,2,2,2));
        pan.setBorder(border);
        popupScale.add(pan);
        
        return popupScale;
    }
  
    /**
     * Returns the scale setting to use, when scale command is selected. 
     * @return
     */
    public double getScale() {
        return scale;
    }
    
    public void setScale(double d) {
        this.scale = d;
        updateComponents();
    }

    protected void updateComponents() {
        if (updatingComponents) return;
        updatingComponents = true;

        int w = getImageWidth();
        int h = getImageHeight();

        if (scale <= 0) scale = 1;
        int iScale = (int) (scale * 100); 

        String s;
        if (w == 0 || h == 0) s = "no image";
        else {
            s = "width: "+w + ", height: " + h;
        }
        getScalePanel().getScaleDescriptionLabel().setText(s);
        
        w = (int) (w * scale);
        h = (int) (h * scale);
        
        getScalePanel().getPercentTextField().setText(iScale+ "");
        getScalePanel().getWidthTextField().setText(w + "");
        getScalePanel().getHeightTextField().setText(h + "");
        

        updatingComponents = false;
    }
    
    
        
    
    /** called when command is pushed. */
    protected abstract void onPerformScale(double scale);
    protected abstract void onUseZoomScale();
    
    /** Used to get width of image, which must be supplied by controller. */
    protected abstract int getImageWidth();
    /** Used to get height of image, which must be supplied by controller. */
    protected abstract int getImageHeight();
}

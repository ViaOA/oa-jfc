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
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import com.viaoa.hub.*;
import com.viaoa.jfc.editor.html.*;
import com.viaoa.jfc.editor.html.oa.*;
import com.viaoa.jfc.editor.html.oa.DocAttribute;
import com.viaoa.jfc.editor.html.oa.DocElement;
import com.viaoa.jfc.*;

/**
 * Used for testing and viewing OAHTML* classes.
 * note: see sample code below to use in app.
 * @author vincevia
 */
public class HtmlDebug {
    private OAHTMLTextPane editor;
    private OATree tree;
    private Hub hubElement;
    
    public HtmlDebug(OAHTMLTextPane editor) {
        this.editor = editor;
    }
    
    OAHTMLDocument getOAHTMLDocument() {
        return (OAHTMLDocument) editor.getDocument();
    }
    
    public JPanel getPanel() {
        JPanel pan = new JPanel();
        pan.setLayout(new BorderLayout());
        tree = new OATree(12); 
        
        OATreeTitleNode tnode = new OATreeTitleNode("Tree Elements");
        
        tree.setRoot(tnode);
        
        OATreeNode node = new OATreeNode("name", getElementHub());
        tnode.add(node);
        
        OATreeNode node1 = new OATreeNode("docElements.name");
        node.add(node1);

        OATreeNode node2 = new OATreeNode("docAttributes.name");
        node1.add(node2);
        
        
        node1.add(node1);
        
        pan.add(new JScrollPane(tree));
        
        JButton cmd = new JButton("Refresh");
        cmd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
        OACommand.setup(cmd);
        
        JPanel panCmd = new JPanel(new FlowLayout());
        panCmd.add(cmd);
        pan.add(panCmd, BorderLayout.SOUTH);
        
        return pan;
    }
    
    public void update() {
        Element ele = getOAHTMLDocument().getDefaultRootElement();
        getElementHub().clear();
        loadElements(ele, getElementHub());
        tree.expandAll();
        
        StyleSheet styles = getOAHTMLDocument().getStyleSheet();
        Enumeration rules = styles.getStyleNames();
        while (rules.hasMoreElements()) {
            String name = (String) rules.nextElement();
            Style rule = styles.getStyle(name);
            System.out.println(rule.toString());
        }
        getOAHTMLDocument().dump(System.out);
    }

    
    void loadElements(Element ele, Hub hubDocElement) {
        DocElement de = new DocElement();
        
        String name = ele.getName() + "["+getClassName(ele)+","+ele.getStartOffset()+","+ele.getEndOffset()+"]"; 
        
        int p1 = ele.getStartOffset();
        int p2 = ele.getEndOffset();
        
        if (ele.isLeaf()) {
            try {
                String s = getOAHTMLDocument().getText(p1, p2-p1);
                s = s.replace('\n', '~');
                name += ": \"" + s + "\"";
            }
            catch (Exception e) {
                System.out.println("Error: "+e);
            }
        }
        
        de.setName(name);
        hubDocElement.add(de);

        AttributeSet ats = ele.getAttributes();
        Enumeration enumx = ats.getAttributeNames();
        for ( ;enumx.hasMoreElements(); ) {
            Object o1 = enumx.nextElement();
            Object o2 = ats.getAttribute(o1);
            DocAttribute da = new DocAttribute();
            da.setName(o1.toString()+" ["+getClassName(o1)+"]" + " = " + o2.toString()+" ["+getClassName(o2)+"]");
            de.getDocAttributes().add(da);
        }
        
        int x = ele.getElementCount();
        for (int i=0; i<x; i++) {
            Element e = ele.getElement(i);
            loadElements(e, de.getDocElements());
        }
    }
    
    String getClassName(Object o) {
        Class c = o.getClass();
        String s = c.getName();
        s = s.replace("javax.swing.text.", "");
        s = s.replace("html.", "");
        return s;
    }
    
    Hub getElementHub() {
        if (hubElement == null) {
            hubElement = new Hub(DocElement.class);
        }
        return hubElement;
    }
    
/*
    HtmlDebug hd = new HtmlDebug(txtHtml);
    JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    sp.setDividerSize(10);
    sp.setOneTouchExpandable(true);
    
    sp.setLeftComponent(hd);
    sp.setRightComponent(new JScrollPane(editor));
    //sp.setDividerLocation(sp.getMinimumDividerLocation());
*/

/*

    public OAPanel createTemplateHTMLTextPanel() {
        OAHTMLTextPane txthtml = createTemplateHTMLTextPane();

        OAPanel pan = new OAPanel(txthtml);
        pan.setLayout(new BorderLayout());
        pan.add(new OAScroller(txthtml.getToolBar()), BorderLayout.NORTH);
        pan.add(new JScrollPane(txthtml), BorderLayout.CENTER);
     
        HtmlDebug hd = new HtmlDebug(txthtml);
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        sp.setDividerSize(10);
        sp.setOneTouchExpandable(true);
        
        sp.setLeftComponent(hd.getPanel());
        sp.setRightComponent(pan);
        //sp.setDividerLocation(sp.getMinimumDividerLocation());
        
        return pan;
    }

    public OAHTMLTextPane createTemplateHTMLTextPane() {
        OAHTMLTextPane html = new OAHTMLTextPane(getHub(), ReportDef.P_Template);
        html.setSpellChecker(Resource.getSpellChecker());
        html.setPreferredSize(10,  30);
        // need to create a place to have the images stored
        html.createImageHandler(ImageStore.class, ImageStore.P_Bytes, ImageStore.P_OrigFileName, ImageStore.P_Id);
        return html;
    }
*/

}


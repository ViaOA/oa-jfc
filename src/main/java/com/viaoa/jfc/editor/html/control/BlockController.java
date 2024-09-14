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
package com.viaoa.jfc.editor.html.control;

import java.awt.Color;
import java.io.StringWriter;
import java.util.*;

import javax.swing.SwingUtilities;
import javax.swing.text.*;
import javax.swing.text.html.*;

import com.viaoa.hub.*;
import com.viaoa.jfc.editor.html.*;
import com.viaoa.jfc.editor.html.oa.*;
import com.viaoa.jfc.editor.html.view.BlockDialog;
import com.viaoa.util.*;

/**
 * Controller for BlockDialog, used to edit a paragraph (block) attributes.
 * @author vvia
 *
 */
public class BlockController {
    private OAHTMLTextPane textPane;

    private Hub<DocElement> hubRootDocElement;
    private Hub<DocElement> hubDocElement;  // recursive detail hub
    private Hub<Block> hubBlock;
    private BlockDialog dlgBlock;

    private Element element;
    private AttributeSet attributeSet;
    
    public BlockController(OAHTMLTextPane textPane) {
        this.textPane = textPane;
    }

    public Hub<Block> getBlockHub() {
        if (hubBlock == null) {
            hubBlock = new Hub<Block>(Block.class);
            hubBlock.add(new Block());
            hubBlock.setPos(0);
        }
        return hubBlock;
    }
    public Block getBlock() {
        Block block = getBlockHub().getAt(0);
        return block;
    }
    
    public Hub<DocElement> getRootDocElements() {
        if (hubRootDocElement == null) {
            hubRootDocElement = new Hub<DocElement>(DocElement.class);
        }
        return hubRootDocElement;
    }
    public Hub<DocElement> getDocElements() {
        if (hubDocElement == null) {
            hubDocElement = new DetailHub(getRootDocElements(), "docElements");
            hubDocElement.addHubListener(new HubListenerAdapter() {
                @Override
                public void afterChangeActiveObject(HubEvent e) {
                    DocElement de = (DocElement) e.getObject();
                    if (de == null) return;
                    element = hmElement.get(de);
                    attributeSet = element.getAttributes();
                    updateBlock();
                }
            });
        }
        return hubDocElement;
    }
    
    
    public BlockDialog getBlockDialog() {
        if (dlgBlock == null) {
            dlgBlock = new BlockDialog(SwingUtilities.getWindowAncestor(textPane), getBlockHub(), getRootDocElements(), getDocElements()) {
                @Override
                protected void onApply() {
                    apply();
                }

                @Override
                protected void onOk() {
                    apply();
                    super.onOk();
                }

                @Override
                public void setVisible(boolean b) {
                    if (b) initSelectedElement(null);
                    super.setVisible(b);
                }
            };
        }
        return dlgBlock;
    }

    
    protected void initSelectedElement(final Element eleSelected) {
        getRootDocElements().clear();
        hmDocElement.clear();
        hmElement.clear();

        OAHTMLDocument doc = (OAHTMLDocument) textPane.getDocument();
        
        loadElements(doc.getRootElements()[0], getRootDocElements());

        hubRootDocElement.setPos(0);
        
        int position = textPane.getCaretPosition();
        element = eleSelected != null ? eleSelected : doc.getParagraphElement(position);
        attributeSet = element.getAttributes();
        
        DocElement de = hmDocElement.get(element);
        getDocElements().setAO(de);
        
        // set tree node
        ArrayList<DocElement> al = new ArrayList<DocElement>();
        Element ele = element;
        for ( ; ele != null; ele = ele.getParentElement()) {
            de = hmDocElement.get(ele);
            al.add(0, de);
        }
        DocElement[] des = new DocElement[al.size()];
        al.toArray(des);
        getBlockDialog().getTreeComboBox().getTree().setSelectedNode(des);
    }
    

    private HashMap<DocElement, Element> hmElement = new HashMap<DocElement, Element>();
    private HashMap<Element, DocElement> hmDocElement = new HashMap<Element, DocElement>();
    private void loadElements(Element ele, Hub hubDocElement) {
        DocElement de = new DocElement();
        hmElement.put(de, ele);
        hmDocElement.put(ele, de);
        
        String name = ele.getName() + "["+ele.getStartOffset()+","+ele.getEndOffset()+"] "; 
        hubDocElement.add(de);

        int x = ele.getElementCount();
        for (int i=0; i<x; i++) {
            Element e = ele.getElement(i);
            
            if (e.isLeaf()) {
                try {
                    int p1 = e.getStartOffset();
                    int p2 = e.getEndOffset();
                    String s = textPane.getDocument().getText(p1, p2-p1);
                    s = OAString.convert(s, "\n", "\\n");
                    if (i == 0) name += " \"";
                    name += s;
                    if (i+1 == x) name += "\"";
                }
                catch (Exception ex) {
                    System.out.println("BlockController Error: "+ex);
                }
            }
            else {
                loadElements(e, de.getDocElements());
            }
        }
        de.setName(name);
    }

    
    private void updateBlock() {
        Block block = getBlock();
        String s;

        OAHTMLDocument doc = (OAHTMLDocument) textPane.getDocument();
        OAHTMLEditorKit kit = (OAHTMLEditorKit) textPane.getEditorKit();
        /*        
        if (attributeSet.getAttribute(StyleConstants.NameAttribute) != HTML.Tag.DIV) {
            Element elemx = element.getParentElement();
            AttributeSet asx = elemx.getAttributes();
            
            if (asx.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.DIV) {
                element = elemx;
                attributeSet = asx;
            }            
        }
         */
        SimpleAttributeSet sas = new SimpleAttributeSet(attributeSet);

        Object obj;
        obj = sas.getAttribute(CSS.Attribute.WIDTH);
        block.setWidth(obj == null ? null : obj.toString());

        obj = sas.getAttribute(CSS.Attribute.HEIGHT);
        block.setHeight(obj == null ? null : obj.toString());

        obj = sas.getAttribute(CSS.Attribute.BACKGROUND_COLOR);
        block.setBackgroundColor(obj == null ? null : (Color) OAConv.convert(Color.class, obj.toString()));
        
        block.setMargin(0);
        obj = sas.getAttribute(CSS.Attribute.MARGIN_TOP);
        block.setMarginTop(obj == null ? 0 : OAConv.toInt(obj.toString()));

        obj = sas.getAttribute(CSS.Attribute.MARGIN_RIGHT);
        block.setMarginRight(obj == null ? 0 : OAConv.toInt(obj.toString()));

        obj = sas.getAttribute(CSS.Attribute.MARGIN_BOTTOM);
        block.setMarginBottom(obj == null ? 0 : OAConv.toInt(obj.toString()));

        obj = sas.getAttribute(CSS.Attribute.MARGIN_LEFT);
        block.setMarginLeft(obj == null ? 0 : OAConv.toInt(obj.toString()));

        int x = block.getMarginTop();
        if (x > 0 && block.getMarginBottom() == x && block.getMarginLeft() == x && block.getMarginRight() == x) {
            block.setMargin(x);
        }
        
        
        block.setPadding(0);
        obj = sas.getAttribute(CSS.Attribute.PADDING_TOP);
        block.setPaddingTop(obj == null ? 0 : OAConv.toInt(obj.toString()));

        obj = sas.getAttribute(CSS.Attribute.PADDING_RIGHT);
        block.setPaddingRight(obj == null ? 0 : OAConv.toInt(obj.toString()));

        obj = sas.getAttribute(CSS.Attribute.PADDING_BOTTOM);
        block.setPaddingBottom(obj == null ? 0 : OAConv.toInt(obj.toString()));

        obj = sas.getAttribute(CSS.Attribute.PADDING_LEFT);
        block.setPaddingLeft(obj == null ? 0 : OAConv.toInt(obj.toString()));

        x = block.getPaddingTop();
        if (x > 0 && block.getPaddingBottom() == x && block.getPaddingLeft() == x && block.getPaddingRight() == x) {
            block.setPadding(x);
        }
        
        block.setBorderWidth(0);
        obj = sas.getAttribute(CSS.Attribute.BORDER_TOP_WIDTH);
        block.setBorderTopWidth(obj == null ? 0 : OAConv.toInt(obj.toString()));

        obj = sas.getAttribute(CSS.Attribute.BORDER_RIGHT_WIDTH);
        block.setBorderRightWidth(obj == null ? 0 : OAConv.toInt(obj.toString()));

        obj = sas.getAttribute(CSS.Attribute.BORDER_BOTTOM_WIDTH);
        block.setBorderBottomWidth(obj == null ? 0 : OAConv.toInt(obj.toString()));

        obj = sas.getAttribute(CSS.Attribute.BORDER_LEFT_WIDTH);
        block.setBorderLeftWidth(obj == null ? 0 : OAConv.toInt(obj.toString()));

        x = block.getBorderTopWidth();
        if (x > 0 && block.getBorderBottomWidth() == x && block.getBorderLeftWidth() == x && block.getBorderRightWidth() == x) {
            block.setBorderWidth(x);
        }
        
        
        
        obj = sas.getAttribute(CSS.Attribute.BORDER_COLOR);
        block.setBorderColor(null);
        
        obj = sas.getAttribute(CSS.Attribute.BORDER_TOP_COLOR);
        block.setBorderTopColor(obj == null ? null : (Color) OAConv.convert(Color.class, obj.toString()));
        
        obj = sas.getAttribute(CSS.Attribute.BORDER_RIGHT_COLOR);
        block.setBorderRightColor(obj == null ? null : (Color) OAConv.convert(Color.class, obj.toString()));
        
        obj = sas.getAttribute(CSS.Attribute.BORDER_BOTTOM_COLOR);
        block.setBorderBottomColor(obj == null ? null : (Color) OAConv.convert(Color.class, obj.toString()));

        obj = sas.getAttribute(CSS.Attribute.BORDER_LEFT_COLOR);
        block.setBorderLeftColor(obj == null ? null : (Color) OAConv.convert(Color.class, obj.toString()));
        
        Color col = block.getBorderTopColor();
        if (col != null && block.getBorderBottomColor().equals(col) && block.getBorderLeftColor().equals(col) && block.getBorderRightColor().equals(col)) {
            block.setBorderColor(col);
        }

        
        
        obj = sas.getAttribute(CSS.Attribute.BORDER_STYLE);
        block.setBorderStyle(null);
        
        obj = sas.getAttribute(CSS.Attribute.BORDER_TOP_STYLE);
        block.setBorderTopStyle(obj == null ? null : obj.toString());
        
        obj = sas.getAttribute(CSS.Attribute.BORDER_RIGHT_STYLE);
        block.setBorderRightStyle(obj == null ? null : obj.toString());

        obj = sas.getAttribute(CSS.Attribute.BORDER_BOTTOM_STYLE);
        block.setBorderBottomStyle(obj == null ? null : obj.toString());
        
        obj = sas.getAttribute(CSS.Attribute.BORDER_LEFT_STYLE);
        block.setBorderLeftStyle(obj == null ? null : obj.toString());
        
        s = block.getBorderTopStyle();
        if (OAStr.isNotEmpty(s) && s.equals(block.getBorderBottomStyle()) && s.equals(block.getBorderLeftStyle()) && s.equals(block.getBorderRightStyle())) {
            block.setBorderStyle(s);
        }
        
    }

    public void apply() {
        try {
            _apply();
        }
        catch (Exception ex) {
            System.out.println("BlockController Error: "+ex);
            // throw new RuntimeException(ex);
        }
    }

    protected void _apply() throws Exception {
        final Block block = getBlock();

        OAHTMLDocument doc = (OAHTMLDocument) textPane.getDocument();
        OAHTMLEditorKit kit = (OAHTMLEditorKit) textPane.getEditorKit();

        SimpleAttributeSet sas = new SimpleAttributeSet(attributeSet);

        if (attributeSet.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.IMPLIED) {
            sas.removeAttribute(StyleConstants.NameAttribute);
            sas.addAttribute(StyleConstants.NameAttribute, HTML.Tag.P);
        }

        sas.removeAttribute(CSS.Attribute.WIDTH);
        sas.removeAttribute(CSS.Attribute.HEIGHT);
        sas.removeAttribute(CSS.Attribute.BACKGROUND_COLOR);

        sas.removeAttribute(CSS.Attribute.MARGIN);
        sas.removeAttribute(CSS.Attribute.MARGIN_TOP);
        sas.removeAttribute(CSS.Attribute.MARGIN_RIGHT);
        sas.removeAttribute(CSS.Attribute.MARGIN_BOTTOM);
        sas.removeAttribute(CSS.Attribute.MARGIN_LEFT);

        sas.removeAttribute(CSS.Attribute.PADDING);
        sas.removeAttribute(CSS.Attribute.PADDING_TOP);
        sas.removeAttribute(CSS.Attribute.PADDING_RIGHT);
        sas.removeAttribute(CSS.Attribute.PADDING_BOTTOM);
        sas.removeAttribute(CSS.Attribute.PADDING_LEFT);

        sas.removeAttribute(CSS.Attribute.BORDER_WIDTH);
        sas.removeAttribute(CSS.Attribute.BORDER_TOP_WIDTH);
        sas.removeAttribute(CSS.Attribute.BORDER_RIGHT_WIDTH);
        sas.removeAttribute(CSS.Attribute.BORDER_BOTTOM_WIDTH);
        sas.removeAttribute(CSS.Attribute.BORDER_LEFT_WIDTH);

        sas.removeAttribute(CSS.Attribute.BORDER_COLOR);
        sas.removeAttribute(CSS.Attribute.BORDER_TOP_COLOR);
        sas.removeAttribute(CSS.Attribute.BORDER_RIGHT_COLOR);
        sas.removeAttribute(CSS.Attribute.BORDER_BOTTOM_COLOR);
        sas.removeAttribute(CSS.Attribute.BORDER_LEFT_COLOR);

        sas.removeAttribute(CSS.Attribute.BORDER_STYLE);
        sas.removeAttribute(CSS.Attribute.BORDER_TOP_STYLE);
        sas.removeAttribute(CSS.Attribute.BORDER_RIGHT_STYLE);
        sas.removeAttribute(CSS.Attribute.BORDER_BOTTOM_STYLE);
        sas.removeAttribute(CSS.Attribute.BORDER_LEFT_STYLE);

        String style = block.getStyle();

        Object obj = sas.getAttribute(CSS.Attribute.BACKGROUND_IMAGE);
        if (obj != null) {
            sas.removeAttribute(CSS.Attribute.BACKGROUND_IMAGE);
            String s  = block.getWidth();
            int w = OAStr.isInteger(s) ? OAConv.toInt(s) : 0;
            s  = block.getHeight();
            int h = OAStr.isInteger(s) ? OAConv.toInt(s) : 0;

            s = obj.toString();
            int pos = s.indexOf('&');
            if (pos < 0)  pos = s.indexOf(')');
            if (pos > 0) s = s.substring(0, pos);

            if (s.indexOf('?') > 0) s += "&";
            else s += "?";
            s += "w="+w+"&h="+h+"";
            
            s = "url('" + s.substring(4) + "')";
            
            style += "background-image:" + s + ";";
        }
        
        AttributeSet asx = doc.getStyleSheet().getDeclaration(style);
        sas.addAttributes(asx);
        
        ((OAHTMLDocument) textPane.getDocument()).setAttributes(element, sas);

        // hack: changing attributes on a TABLE does not auto display (painter is not updated)
        //   this will "force" it to be recreated.
        
        AttributeSet attrs = element.getAttributes();
        Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
        Object o = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
        if (!(o instanceof HTML.Tag)) return;
        
        HTML.Tag kind = (HTML.Tag) o;
        if (kind != HTML.Tag.TABLE) return;

        int startOffset = element.getStartOffset();
        int endOffset = element.getEndOffset();

        // find parent that only has one child
        Element eleParent = element.getParentElement();
        if (eleParent == null) return;
        
        StringWriter writer = new StringWriter();
        kit.writeInner(writer, doc, eleParent);
        String htmlInner = writer.toString();
        doc.setInnerHTML(eleParent, htmlInner);

        // update tree and activeObject
        Element ele = doc.getParagraphElement(startOffset);
        Element eleFound = ele;
        for ( ;ele != null && ele.getParentElement() != null; ele = ele.getParentElement()) {
            if (ele.getStartOffset() != startOffset) {
                break;
            }
            if (ele.getEndOffset() != endOffset) {
                continue;
            }
            attrs = ele.getAttributes();
            o = attrs.getAttribute(StyleConstants.NameAttribute);
            if (o instanceof HTML.Tag) {
                kind = (HTML.Tag) o;
                if (kind == HTML.Tag.TABLE) {
                    eleFound = ele;
                }
            }
        }
        initSelectedElement(eleFound);
    }
}



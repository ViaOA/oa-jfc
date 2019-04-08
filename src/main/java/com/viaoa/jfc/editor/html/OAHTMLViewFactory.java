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
package com.viaoa.jfc.editor.html;

import java.awt.*;
import java.net.URL;

import javax.swing.text.*;
import javax.swing.text.html.*;

import com.viaoa.jfc.editor.html.view.*;
    
/**
 * ViewFactory created by OAHTMLEditorKit
 * @author vincevia
 */
public abstract class OAHTMLViewFactory extends HTMLEditorKit.HTMLFactory {

    public OAHTMLViewFactory() {
    }
    
    public View create(Element elem) {
        View view = null;
        Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
        if (!(o instanceof HTML.Tag)) {
            view = new LabelView(elem);
            return view;
        }                
            
        HTML.Tag kind = (HTML.Tag) o;
        if (kind == HTML.Tag.TABLE) {
            return super.create(elem);
        }
        if (kind == HTML.Tag.COMMENT) {
            return new InvisibleView(elem);
        }
        if (kind == HTML.Tag.SCRIPT) {
            return new InvisibleView(elem);
        }
        if (kind instanceof HTML.UnknownTag) {
            return new InvisibleView(elem);
        }
        if (kind == HTML.Tag.OBJECT) {
            return new InvisibleView(elem);
        }
        if (kind == HTML.Tag.STYLE) {
            return new InvisibleView(elem);
        }
        if (kind == HTML.Tag.BR) {
            // makes sure that blank lines use previous line Attributes, instead of default attributes.
            view = new InlineView(elem) {
                public int getBreakWeight(int axis, float pos, float len) {
                    if (axis == X_AXIS) return ForcedBreakWeight;
                    else return super.getBreakWeight(axis, pos, len);
                }

                public float getPreferredSpan(int axis) {
                    float fx = super.getPreferredSpan(axis);
                    if (axis == View.X_AXIS) return fx;
                    
                    int endPos = getStartOffset() - 1;
                    if (endPos < 0) return fx;

                    
                    View v = findParentView(this, endPos);
                    if (v != null) {
                        v = findChildView(v, endPos);
                        if (v != null) {
                            return v.getPreferredSpan(axis);
                        }
                    }
                    return fx; 
                }
                @Override
                public float getAlignment(int axis) {
                    float fx = super.getAlignment(axis);
                    if (axis == View.X_AXIS) return fx;
                    
                    int endPos = getStartOffset() - 1;
                    if (endPos < 0) return fx;

                    
                    View v = findParentView(this, endPos);
                    if (v != null) {
                        v = findChildView(v, endPos);
                        if (v != null) {
                            return v.getAlignment(axis);
                        }
                    }
                    return fx; 
                }
            };
            return view;
        }
        if (kind == HTML.Tag.SPAN) {
            view = new InlineView(elem) {
                public float getPreferredSpan(int axis) {
                    float fx = super.getPreferredSpan(axis);
                    if (axis == View.X_AXIS) return fx;
                    return 1f; 
                }
            };
            return view;
        }

        if (kind == HTML.Tag.CONTENT) {
            int p0 = elem.getStartOffset();
            int p1 = elem.getEndOffset();
            String vs = null;
            try {
                vs = elem.getDocument().getText(p0,(p1-p0));
            }
            catch (Exception e) {
            }

            if (vs != null && vs.length() == 1 && vs.charAt(0) == '\n') {
                // blank line
                view = new InlineView(elem) {
                    public float getPreferredSpan(int axis) {
                        float fx = super.getPreferredSpan(axis);
                        if (axis == View.X_AXIS) return fx;
                        
                        int endPos = getStartOffset() - 1;
                        if (endPos < 0) return fx;
                        
                        View v = findParentView(this, endPos);
                        if (v != null) {
                            v = findChildView(v, endPos);
                            if (v != null) return v.getPreferredSpan(axis);
                        }
                        return fx; 
                    }
                    @Override
                    public float getAlignment(int axis) {
                        float fx = super.getAlignment(axis);
                        if (axis == View.X_AXIS) return fx;
                        
                        int endPos = getStartOffset() - 1;
                        if (endPos < 0) return fx;

                        
                        View v = findParentView(this, endPos);
                        if (v != null) {
                            v = findChildView(v, endPos);
                            if (v != null) {
                                return v.getAlignment(axis);
                            }
                        }
                        return fx; 
                    }
                };
                return view;
            }
            return new InlineView(elem);
        }
            
        if (kind == HTML.Tag.IMG) {
            return new MyImageView(elem) {
                @Override
                protected Image getImage(String src, URL url) {
                    return OAHTMLViewFactory.this.getImage(src, url);
                }
            };
        }
        
        
        view = super.create(elem);
        return view;
    }

    protected View findParentView(View v, int findPos) {
        if (v == null) return null;
        for (;;) {
            v = v.getParent();
            if (v == null) return null;
            if (v.getStartOffset() <= findPos && v.getEndOffset() >= findPos) return v;
        }
    }
    protected View findChildView(View view, int findPos) {
        if (view == null) return null;
        int x = view.getViewCount();
        if (x == 0 && view instanceof LabelView) return view;
        for (int i=0; i<x; i++) {
            View v = view.getView(i);
            if (v.getStartOffset() <= findPos && v.getEndOffset() >= findPos) {
                return findChildView(v, findPos);
            }
        }
        return null;
    }
    
    /**
     * Used to supply images for a myImageView.  If null is returned,
     * then the myImageView will get the image.
     */
    protected abstract Image getImage(String src, URL url);
    
}
    

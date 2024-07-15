package com.viaoa.jfc.text;

import java.awt.event.*;
import java.net.URL;
import java.util.*;

import javax.swing.*;

import com.viaoa.hub.Hub;
import com.viaoa.jfc.*;
import com.viaoa.jfc.editor.html.OAHTMLTextPane;
import com.viaoa.jfc.editor.html.view.InsertFieldDialog;
import com.viaoa.jfc.propertypath.OAPropertyPathTree;
import com.viaoa.jfc.propertypath.delegate.ObjectDefDelegate;
import com.viaoa.jfc.propertypath.model.oa.*;
import com.viaoa.jfc.text.autocomplete.AutoComplete;
import com.viaoa.util.*;

/**
 * Controller to allow a model object property to be inserted into text.
 * 
 * @author vvia
 */
public class OATextFieldCodeController extends OATextController {

    private OATextField txt;
    private Hub<ObjectDef> hubObjectDef;

    private Class classOAObject;

    public enum Type {
        oatemplate, java, query;
    }

    protected Type type = Type.query;

    public OATextFieldCodeController(OATextField txt, Class classOAObject, Type type) {
        super(txt, null, false);
        this.txt = txt;
        this.classOAObject = classOAObject;
        this.type = type;

        setupAutoComplete();
    }

    int posx;

    protected void onRightMouse(MouseEvent e) {
        posx = editor.getCaretPosition();
        super.onRightMouse(e);
    }

    @Override
    protected void addMenuItems(JPopupMenu popupMenu) {
        popupMenu.addSeparator();

        JMenuItem miInsertField = new JMenuItem("Insert field ...");
        miInsertField.setToolTipText("Insert an object property or reference name");
        miInsertField.setMnemonic('I');
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK, false);
        miInsertField.setAccelerator(ks);
        editor.getInputMap(JComponent.WHEN_FOCUSED).put(ks, miInsertField);
        
        editor.getActionMap().put(miInsertField, getInsertFieldActionListener());

        URL url = OAHTMLTextPane.class.getResource("view/image/field.gif");
        miInsertField.setIcon(new ImageIcon(url));
        miInsertField.addActionListener(getInsertFieldActionListener());
        popupMenu.add(miInsertField);
    }

    private AbstractAction aaInsertField;

    protected AbstractAction getInsertFieldActionListener() {
        if (aaInsertField != null) return aaInsertField;

        aaInsertField = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                onInsertField();
            }
        };
        return aaInsertField;
    }

    public void onInsertField() {
        posx = editor.getCaretPosition();

        if (getObjectDefs().size() == 0) {
            ObjectDef od = ObjectDefDelegate.getObjectDef(hubObjectDef, classOAObject);
            hubObjectDef.setAO(od);
        }

        // formatting

        InsertFieldDialog dlg = getInsertFieldDialog();

        dlg.getTextField().setText("");

        /*
         * qqqq getCustomFields().setPos(-1); String[] sx = c.getCustomFields(); if (sx != null) { // even if empty getCustomFields().clear();
         * addDefaultCustomFields(); for (String s : sx) { getCustomFields().add(s); } }
         */

        getCustomCommands().setPos(-1);

        getInsertFieldDialog().setVisible(true);

        if (getInsertFieldDialog().wasCanceled()) {
            return;
        }

        String field = getInsertFieldDialog().getTextField().getText();
        if (OAString.isEmpty(field)) return;

        String txt = ((OATextField) editor).getText();
        String s1 = posx <= 0 ? "" : (txt.substring(0, posx) + " ");
        String s2 = posx >= txt.length() ? "" : (" " + txt.substring(posx));
        txt = s1 + field + s2;

        /*
         * qqq if (OAString.isEmpty(field)) { field = getCustomFields().getAO(); if (!OAString.isEmpty(field)) { field = "$" + field; } }
         * 
         * String cmd = getCustomCommands().getAO(); if (getCustomFields().getAO() == null && OAString.isNotEmpty(cmd)) { cmd = OAString.field(cmd,
         * ":", 2, 99).trim(); if (OAString.isNotEmpty(field)) { cmd = OAString.convert(cmd, "prop", field); } field = cmd; } else { if
         * (OAString.isNotEmpty(field)) { field = "<%=" + field + "%>"; } }
         */

        ((OATextField) editor).setText(txt);
        // qqqqqq htmlEditor.insertString(field);
    }

    private Hub<String> hubCustomField;

    public Hub<String> getCustomFields() {
        if (hubCustomField == null) {
            hubCustomField = new Hub<String>(String.class);
            addDefaultCustomFields();
        }
        return hubCustomField;
    }

    public void addDefaultCustomFields() {
        if (hubCustomField != null) {
            hubCustomField.add("Date");
            hubCustomField.add("Time");
            hubCustomField.add("Page");
        }
    }

    private Hub<String> hubCustomCommand;

    public Hub<String> getCustomCommands() {
        if (hubCustomCommand == null) {
            hubCustomCommand = new Hub<>(String.class);
            hubCustomCommand.add("format: <%=prop[,width||fmt]%>");
            hubCustomCommand.add("for each: <%=foreach [prop]%>..<%=end%>");
            hubCustomCommand.add("if statement: <%=if prop%>..<%=end%>");
            hubCustomCommand.add("ifnot statement: <%=ifnot prop%>..<%=end%>");
            hubCustomCommand.add("if equals statement: <%=ifequals prop \"value to match\"%>..<%=end%>");
            hubCustomCommand.add("format block: <%=format[X],'12 L'%>..<%=end%>");
            hubCustomCommand.add("include file: <%=include filename%>");
            hubCustomCommand.add("counter in foreach: <%=#counter, fmt%>");
            hubCustomCommand.add("sum: <%=#sum [prop] prop fmt%>");
            hubCustomCommand.add("count: <%=#count prop, fmt%>");
            // hubCustomField.add("");
        }
        return hubCustomCommand;
    }

    public Hub<ObjectDef> getObjectDefs() {
        if (hubObjectDef == null) {
            hubObjectDef = new Hub<ObjectDef>(ObjectDef.class);
        }
        return hubObjectDef;
    }

    private OATreeComboBox cboTree;

    public OATreeComboBox getComboBox() {
        if (cboTree != null) return cboTree;
        cboTree = new OATreeComboBox(getPropertyPathTree(), hubObjectDef, "name");
        cboTree.setEditor((OATextField) editor);

        return cboTree;
    }

    private OAPropertyPathTree tree;

    public OAPropertyPathTree getPropertyPathTree() {
        if (tree == null) {
            tree = new OAPropertyPathTree(hubObjectDef, false, true, true, true, true) {
                @Override
                public void propertyPathCreated(String propertyPath) {
                    ((OATextField) editor).setText(propertyPath);
                    cboTree.hidePopup();
                }
            };
        }
        return tree;
    }

    private InsertFieldDialog dlgInsertField;

    public InsertFieldDialog getInsertFieldDialog() {
        if (dlgInsertField == null) {
            dlgInsertField = new InsertFieldDialog(SwingUtilities.windowForComponent(editor), getObjectDefs(), getCustomFields(), getCustomCommands());
        }
        return dlgInsertField;
    }

    public void setupAutoComplete() {
        if (autoComplete != null) return;
        
        this.autoComplete = new AutoComplete(txt) {
            @Override
            protected String[] getMatches(String text) {
                return findMatches(text);
            }

            @Override
            protected String[] getSoundexMatches(String text) {
                return null;
            }

            @Override
            protected int getBeginPosition(int posCaret) throws Exception {
                int pos = editor.getCaretPosition();
                String val = txt.getText();
                int bpos = pos;
                if (bpos > 0) bpos--;
                for (; bpos < val.length() && bpos >= 0; bpos--) {
                    char ch = val.charAt(bpos);
                    if (ch == '.' || ch == ' ' || ch == '(' || ch == ')') {
                        if (bpos > 0) ++bpos;
                        break;
                    }
                }
                if (bpos < 0) bpos = 0;
                return bpos;
            }
        };

        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_PERIOD:
                        int x = txt.getCaretPosition();
                        String s = txt.getText();
                        if (x >= s.length()) {
                            autoComplete.showAutoComplete();
                        }
                        break;
                }
            }
        });
    
    }

    public String[] findMatches(String matchText) {
        if (getObjectDefs().size() == 0) {
            ObjectDef od = ObjectDefDelegate.getObjectDef(hubObjectDef, classOAObject);
            hubObjectDef.setAO(od);
        }

        String val = txt.getText();
        posx = editor.getCaretPosition();

        int bpos = posx;

        if (bpos > 0) bpos--;
        for (; bpos < val.length() && bpos >= 0; bpos--) {
            char ch = val.charAt(bpos);
            if (ch == ' ' || ch == '(' || ch == ')') {
                if (bpos > 0) ++bpos;
                break;
            }
        }
        if (bpos < 0) bpos = 0;
        String str = OAStr.substring(val, bpos, posx);

        int dcnt = OAStr.dcount(str, '.');
        if (dcnt == 0) dcnt = 1;
        List<String> al = new ArrayList();

        ObjectDef od = getObjectDefs().getAO();

        for (int i = 1; i <= dcnt; i++) {
            String s = OAStr.field(str, '.', i);

            if (i == dcnt) {
                // find matches
                for (PropertyDef pd : od.getPropertyDefs()) {
                    if (s.length() == 0 || OAStr.startsWith(pd.getName(), s, true)) {
                        al.add(pd.getLowerName());
                    }
                }
                for (CalcPropertyDef pd : od.getCalcPropertyDefs()) {
                    if (s.length() == 0 || OAStr.startsWith(pd.getName(), s, true)) {
                        al.add(pd.getLowerName());
                    }
                }
                for (LinkPropertyDef lp : od.getLinkPropertyDefs()) {
                    if (s.length() == 0 || OAStr.startsWith(lp.getName(), s, true)) {
                        al.add(lp.getLowerName());
                    }
                }
            }
            else {
                // link
                LinkPropertyDef lp = od.getLinkPropertyDefs().find(LinkPropertyDef.P_Name, s);
                if (lp != null) od = lp.getToObjectDef();
            }
        }

        String[] ss = al.toArray(new String[] {});
        return ss;
    }
}

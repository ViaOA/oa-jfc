package com.viaoa.jfc;

import com.viaoa.hub.Hub;
import com.viaoa.jfc.control.TextFieldController;
import com.viaoa.object.OAObject;


/**
 * 20191226
 * 
 * todo:  needs to support OAProperty timeZonePropertyPath & ignoreTimeZone
 * 
 * @author vvia
 */
public class OADateTimeTextField extends OATextField {
    
    public OADateTimeTextField() {
        super();
    }
    
    
    public OADateTimeTextField(TextFieldController control) {
        super(control);
    }

    /**
        Create TextField that is bound to a property path in a Hub.
        @param propertyPath path from Hub, used to find bound property.
    */
    public OADateTimeTextField(Hub hub, String propertyPath) {
        super(hub, propertyPath);
    }

    /**
        Create TextField that is bound to a property path in a Hub.
        @param propertyPath path from Hub, used to find bound property.
        @param cols is the width
    */
    public OADateTimeTextField(Hub hub, String propertyPath, int cols) {
        super(hub, propertyPath, cols);
    }

    /**
        Create TextField that is bound to a property path in a Hub.
        @param propertyPath path from Hub, used to find bound property.
    */
    public OADateTimeTextField(OAObject hubObject, String propertyPath) {
        super(hubObject, propertyPath);
    }

    /**
        Create TextArea that is bound to a property path in an Object.
        @param propertyPath path from Hub, used to find bound property.
        @param cols is the width
    */
    public OADateTimeTextField(OAObject hubObject, String propertyPath, int cols) {
        super(hubObject, propertyPath, cols);
    }
    
}

// Copied from OATemplate project by OABuilder 09/10/14 05:33 PM
package test.xice.tsac3.model.oa.search;

import com.viaoa.annotation.*;
import com.viaoa.object.*;
import com.viaoa.hub.*;
import com.viaoa.util.*;

import test.xice.tsac3.model.*;
import test.xice.tsac3.model.oa.ErrorInfo;

@OAClass(addToCache=false, initialize=true, useDataSource=false, localOnly=true)
public class ExceptionTypeSearch extends OAObject {
    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_ErrorInfos = "ErrorInfos";

    protected ErrorInfo errorInfos;

    public ErrorInfo getErrorInfos() {
        if (errorInfos == null) {
            errorInfos = (ErrorInfo) getObject(PROPERTY_ErrorInfos);
        }
        return errorInfos;
    }
    public void setErrorInfos(ErrorInfo newValue) {
        ErrorInfo old = this.errorInfos;
        fireBeforePropertyChange(PROPERTY_ErrorInfos, old, newValue);
        this.errorInfos = newValue;
        firePropertyChange(PROPERTY_ErrorInfos, old, this.errorInfos);
    }

}

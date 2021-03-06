// Generated by OABuilder
package test.xice.tsam.model.oa.propertypath;
 
import java.io.Serializable;

import test.xice.tsam.model.oa.Application;
import test.xice.tsam.model.oa.propertypath.ApplicationGroupPPx;
import test.xice.tsam.model.oa.propertypath.ApplicationStatusPPx;
import test.xice.tsam.model.oa.propertypath.ApplicationTypePPx;
import test.xice.tsam.model.oa.propertypath.ApplicationVersionPPx;
import test.xice.tsam.model.oa.propertypath.MRADClientPPx;
import test.xice.tsam.model.oa.propertypath.PPxInterface;
import test.xice.tsam.model.oa.propertypath.ServerPPx;

import test.xice.tsam.model.oa.*;
 
public class ApplicationPPx implements PPxInterface, Serializable {
    private static final long serialVersionUID = 1L;
    public final String pp;  // propertyPath
     
    public ApplicationPPx(String name) {
        this(null, name);
    }

    public ApplicationPPx(PPxInterface parent, String name) {
        String s = null;
        if (parent != null) {
            s = parent.toString();
        }
        if (s == null) s = "";
        if (name != null && name.length() > 0) {
            if (s.length() > 0 && name.charAt(0) != ':') s += ".";
            s += name;
        }
        pp = s;
    }

    public ApplicationStatusPPx applicationStatus() {
        ApplicationStatusPPx ppx = new ApplicationStatusPPx(this, Application.P_ApplicationStatus);
        return ppx;
    }

    public ApplicationTypePPx applicationType() {
        ApplicationTypePPx ppx = new ApplicationTypePPx(this, Application.P_ApplicationType);
        return ppx;
    }

    public ApplicationVersionPPx applicationVersions() {
        ApplicationVersionPPx ppx = new ApplicationVersionPPx(this, Application.P_ApplicationVersions);
        return ppx;
    }

    public ApplicationGroupPPx excludeApplicationGroups() {
        ApplicationGroupPPx ppx = new ApplicationGroupPPx(this, Application.P_ExcludeApplicationGroups);
        return ppx;
    }

    public ApplicationGroupPPx includeApplicationGroups() {
        ApplicationGroupPPx ppx = new ApplicationGroupPPx(this, Application.P_IncludeApplicationGroups);
        return ppx;
    }

    public MRADClientPPx mradClient() {
        MRADClientPPx ppx = new MRADClientPPx(this, Application.P_MRADClient);
        return ppx;
    }

    public ApplicationGroupPPx selectedApplicationGroups() {
        ApplicationGroupPPx ppx = new ApplicationGroupPPx(this, Application.P_SelectedApplicationGroups);
        return ppx;
    }

    public ServerPPx server() {
        ServerPPx ppx = new ServerPPx(this, Application.P_Server);
        return ppx;
    }

    public String id() {
        return pp + "." + Application.P_Id;
    }

    public String instanceNumber() {
        return pp + "." + Application.P_InstanceNumber;
    }

    public String tradingSystemId() {
        return pp + "." + Application.P_TradingSystemId;
    }

    public String name() {
        return pp + "." + Application.P_Name;
    }

    public String userId() {
        return pp + "." + Application.P_UserId;
    }

    public String showInMRAD() {
        return pp + "." + Application.P_ShowInMRAD;
    }

    public String autocomplete() {
        return pp + "." + Application.P_Autocomplete;
    }

    @Override
    public String toString() {
        return pp;
    }
}
 

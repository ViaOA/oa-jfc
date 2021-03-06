// Generated by OABuilder
package test.xice.tsam.model.oa.propertypath;
 
import java.io.Serializable;

import test.xice.tsam.model.oa.Server;
import test.xice.tsam.model.oa.propertypath.ApplicationPPx;
import test.xice.tsam.model.oa.propertypath.OSVersionPPx;
import test.xice.tsam.model.oa.propertypath.PPxInterface;
import test.xice.tsam.model.oa.propertypath.SiloPPx;

import test.xice.tsam.model.oa.*;
 
public class ServerPPx implements PPxInterface, Serializable {
    private static final long serialVersionUID = 1L;
    public final String pp;  // propertyPath
     
    public ServerPPx(String name) {
        this(null, name);
    }

    public ServerPPx(PPxInterface parent, String name) {
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

    public ApplicationPPx applications() {
        ApplicationPPx ppx = new ApplicationPPx(this, Server.P_Applications);
        return ppx;
    }

    public OSVersionPPx osVersion() {
        OSVersionPPx ppx = new OSVersionPPx(this, Server.P_OSVersion);
        return ppx;
    }

    public SiloPPx silo() {
        SiloPPx ppx = new SiloPPx(this, Server.P_Silo);
        return ppx;
    }

    public String id() {
        return pp + "." + Server.P_Id;
    }

    public String created() {
        return pp + "." + Server.P_Created;
    }

    public String name() {
        return pp + "." + Server.P_Name;
    }

    public String hostName() {
        return pp + "." + Server.P_HostName;
    }

    public String ipAddress() {
        return pp + "." + Server.P_IpAddress;
    }

    public String dnsName() {
        return pp + "." + Server.P_DnsName;
    }

    public String shortDnsName() {
        return pp + "." + Server.P_ShortDnsName;
    }

    public String userId() {
        return pp + "." + Server.P_UserId;
    }

    public String displayName() {
        return pp + "." + Server.P_DisplayName;
    }

    @Override
    public String toString() {
        return pp;
    }
}
 

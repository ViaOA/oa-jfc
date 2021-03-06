// Generated by OABuilder
package test.xice.tsac2.model.oa.propertypath;
 
import java.io.Serializable;

import test.xice.tsac2.model.oa.*;
 
public class TimezonePPx implements PPxInterface, Serializable {
    private static final long serialVersionUID = 1L;
    public final String pp;  // propertyPath
     
    public TimezonePPx(String name) {
        this(null, name);
    }

    public TimezonePPx(PPxInterface parent, String name) {
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

    public SitePPx sites() {
        SitePPx ppx = new SitePPx(this, Timezone.P_Sites);
        return ppx;
    }

    public String id() {
        return pp + "." + Timezone.P_Id;
    }

    public String name() {
        return pp + "." + Timezone.P_Name;
    }

    public String utcOffset() {
        return pp + "." + Timezone.P_UTCOffset;
    }

    public String displayName() {
        return pp + "." + Timezone.P_DisplayName;
    }

    @Override
    public String toString() {
        return pp;
    }
}
 

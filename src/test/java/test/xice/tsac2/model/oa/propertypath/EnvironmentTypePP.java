// Generated by OABuilder
package test.xice.tsac2.model.oa.propertypath;
 
import test.xice.tsac2.model.oa.*;
 
public class EnvironmentTypePP {
    private static EnvironmentPPx environments;
     

    public static EnvironmentPPx environments() {
        if (environments == null) environments = new EnvironmentPPx(EnvironmentType.P_Environments);
        return environments;
    }

    public static String id() {
        String s = EnvironmentType.P_Id;
        return s;
    }

    public static String type() {
        String s = EnvironmentType.P_Type;
        return s;
    }

    public static String name() {
        String s = EnvironmentType.P_Name;
        return s;
    }
}
 

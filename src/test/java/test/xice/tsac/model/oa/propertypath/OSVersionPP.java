// Generated by OABuilder
package test.xice.tsac.model.oa.propertypath;
 
import test.xice.tsac.model.oa.*;
 
public class OSVersionPP {
    private static OperatingSystemPPx operatingSystem;
    private static ServerPPx servers;
     

    public static OperatingSystemPPx operatingSystem() {
        if (operatingSystem == null) operatingSystem = new OperatingSystemPPx(OSVersion.P_OperatingSystem);
        return operatingSystem;
    }

    public static ServerPPx servers() {
        if (servers == null) servers = new ServerPPx(OSVersion.P_Servers);
        return servers;
    }

    public static String id() {
        String s = OSVersion.P_Id;
        return s;
    }

    public static String name() {
        String s = OSVersion.P_Name;
        return s;
    }
}
 

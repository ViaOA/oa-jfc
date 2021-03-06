// Generated by OABuilder
package test.xice.tsac3.model.oa.propertypath;
 
import test.xice.tsac3.model.oa.*;
 
public class ServerGroupPP {
    private static MRADClientPPx mradClients;
    private static SchedulePPx schedules;
    private static SiloPPx silo;
     

    public static MRADClientPPx mradClients() {
        if (mradClients == null) mradClients = new MRADClientPPx(ServerGroup.P_MRADClients);
        return mradClients;
    }

    public static SchedulePPx schedules() {
        if (schedules == null) schedules = new SchedulePPx(ServerGroup.P_Schedules);
        return schedules;
    }

    public static SiloPPx silo() {
        if (silo == null) silo = new SiloPPx(ServerGroup.P_Silo);
        return silo;
    }

    public static String id() {
        String s = ServerGroup.P_Id;
        return s;
    }

    public static String code() {
        String s = ServerGroup.P_Code;
        return s;
    }

    public static String name() {
        String s = ServerGroup.P_Name;
        return s;
    }

    public static String seq() {
        String s = ServerGroup.P_Seq;
        return s;
    }

    public static String start() {
        String s = "start";
        return s;
    }

    public static String stop() {
        String s = "stop";
        return s;
    }

    public static String kill() {
        String s = "kill";
        return s;
    }

    public static String suspend() {
        String s = "suspend";
        return s;
    }

    public static String resume() {
        String s = "resume";
        return s;
    }
}
 

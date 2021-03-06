// Generated by OABuilder
package test.xice.tsam.model.oa.propertypath;
 
import test.xice.tsam.model.oa.Silo;
import test.xice.tsam.model.oa.propertypath.ApplicationGroupPPx;
import test.xice.tsam.model.oa.propertypath.EnvironmentPPx;
import test.xice.tsam.model.oa.propertypath.MRADServerPPx;
import test.xice.tsam.model.oa.propertypath.ServerPPx;
import test.xice.tsam.model.oa.propertypath.SiloConfigPPx;
import test.xice.tsam.model.oa.propertypath.SiloTypePPx;

import test.xice.tsam.model.oa.*;
 
public class SiloPP {
    private static ApplicationGroupPPx applicationGroups;
    private static EnvironmentPPx environment;
    private static MRADServerPPx mradServer;
    private static ServerPPx servers;
    private static SiloConfigPPx siloConfigs;
    private static SiloTypePPx siloType;
     

    public static ApplicationGroupPPx applicationGroups() {
        if (applicationGroups == null) applicationGroups = new ApplicationGroupPPx(Silo.P_ApplicationGroups);
        return applicationGroups;
    }

    public static EnvironmentPPx environment() {
        if (environment == null) environment = new EnvironmentPPx(Silo.P_Environment);
        return environment;
    }

    public static MRADServerPPx mradServer() {
        if (mradServer == null) mradServer = new MRADServerPPx(Silo.P_MRADServer);
        return mradServer;
    }

    public static ServerPPx servers() {
        if (servers == null) servers = new ServerPPx(Silo.P_Servers);
        return servers;
    }

    public static SiloConfigPPx siloConfigs() {
        if (siloConfigs == null) siloConfigs = new SiloConfigPPx(Silo.P_SiloConfigs);
        return siloConfigs;
    }

    public static SiloTypePPx siloType() {
        if (siloType == null) siloType = new SiloTypePPx(Silo.P_SiloType);
        return siloType;
    }

    public static String id() {
        String s = Silo.P_Id;
        return s;
    }

    public static String networkMask() {
        String s = Silo.P_NetworkMask;
        return s;
    }

    public static String currentTime() {
        String s = Silo.P_CurrentTime;
        return s;
    }

    public static String schedulerMessage() {
        String s = Silo.P_SchedulerMessage;
        return s;
    }
}
 

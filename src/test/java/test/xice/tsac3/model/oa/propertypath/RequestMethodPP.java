// Generated by OABuilder
package test.xice.tsac3.model.oa.propertypath;
 
import test.xice.tsac3.model.oa.*;
 
public class RequestMethodPP {
    private static GSRequestPPx gSRequests;
     

    public static GSRequestPPx gSRequests() {
        if (gSRequests == null) gSRequests = new GSRequestPPx(RequestMethod.P_GSRequests);
        return gSRequests;
    }

    public static String id() {
        String s = RequestMethod.P_Id;
        return s;
    }

    public static String created() {
        String s = RequestMethod.P_Created;
        return s;
    }

    public static String name() {
        String s = RequestMethod.P_Name;
        return s;
    }

    public static String functionId() {
        String s = RequestMethod.P_FunctionId;
        return s;
    }

    public static String retryTimeoutSeconds() {
        String s = RequestMethod.P_RetryTimeoutSeconds;
        return s;
    }

    public static String retryWaitMs() {
        String s = RequestMethod.P_RetryWaitMs;
        return s;
    }

    public static String gemstoneTimeoutSeconds() {
        String s = RequestMethod.P_GemstoneTimeoutSeconds;
        return s;
    }

    public static String disabled() {
        String s = RequestMethod.P_Disabled;
        return s;
    }

    public static String usesHeavy() {
        String s = RequestMethod.P_UsesHeavy;
        return s;
    }

    public static String retryOnHeavy() {
        String s = RequestMethod.P_RetryOnHeavy;
        return s;
    }

    public static String avgRequestPerDay() {
        String s = RequestMethod.P_AvgRequestPerDay;
        return s;
    }

    public static String avgResponseSize() {
        String s = RequestMethod.P_AvgResponseSize;
        return s;
    }

    public static String avgResponseMs() {
        String s = RequestMethod.P_AvgResponseMs;
        return s;
    }
}
 

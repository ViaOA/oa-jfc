// Generated by OABuilder
package test.xice.tsac.model.oa.propertypath;
 
import test.xice.tsac.model.oa.*;
 
public class RCPackageListDetailPP {
    private static PackageTypePPx packageType;
    private static RCPackageListPPx rcPackageList;
     

    public static PackageTypePPx packageType() {
        if (packageType == null) packageType = new PackageTypePPx(RCPackageListDetail.P_PackageType);
        return packageType;
    }

    public static RCPackageListPPx rcPackageList() {
        if (rcPackageList == null) rcPackageList = new RCPackageListPPx(RCPackageListDetail.P_RCPackageList);
        return rcPackageList;
    }

    public static String id() {
        String s = RCPackageListDetail.P_Id;
        return s;
    }

    public static String code() {
        String s = RCPackageListDetail.P_Code;
        return s;
    }

    public static String name() {
        String s = RCPackageListDetail.P_Name;
        return s;
    }

    public static String repoDirectory() {
        String s = RCPackageListDetail.P_RepoDirectory;
        return s;
    }

    public static String invalidMessage() {
        String s = RCPackageListDetail.P_InvalidMessage;
        return s;
    }

    public static String selected() {
        String s = RCPackageListDetail.P_Selected;
        return s;
    }

    public static String loaded() {
        String s = RCPackageListDetail.P_Loaded;
        return s;
    }
}
 

// Generated by OABuilder
package test.xice.tsam.model.oa;
 
import java.util.logging.*;
import java.sql.*;
import com.viaoa.object.*;
import com.viaoa.hub.*;
import com.viaoa.util.*;
import test.xice.tsam.delegate.oa.HostInfoDelegate;
import test.xice.tsam.model.oa.Application;
import test.xice.tsam.model.oa.ApplicationVersion;
import test.xice.tsam.model.oa.HostInfo;
import test.xice.tsam.model.oa.MRADClient;
import com.viaoa.annotation.*;
import com.viaoa.util.OADateTime;

import test.xice.tsam.delegate.oa.*;
import test.xice.tsam.model.oa.filter.*;
import test.xice.tsam.model.oa.propertypath.*;
 
@OAClass(
    shortName = "hi",
    displayName = "Host Info"
)
@OATable(
    indexes = {
        @OAIndex(name = "HostInfoMradClient", columns = { @OAIndexColumn(name = "MradClientId") })
    }
)
public class HostInfo extends OAObject {
    private static final long serialVersionUID = 1L;
    private static Logger LOG = Logger.getLogger(HostInfo.class.getName());
    public static final String PROPERTY_Id = "Id";
    public static final String P_Id = "Id";
    public static final String PROPERTY_LastSSHCheck = "LastSSHCheck";
    public static final String P_LastSSHCheck = "LastSSHCheck";
    public static final String PROPERTY_SSHError = "SSHError";
    public static final String P_SSHError = "SSHError";
    public static final String PROPERTY_HostDate = "HostDate";
    public static final String P_HostDate = "HostDate";
    public static final String PROPERTY_HostName = "HostName";
    public static final String P_HostName = "HostName";
    public static final String PROPERTY_UnixName = "UnixName";
    public static final String P_UnixName = "UnixName";
    public static final String PROPERTY_CronTab = "CronTab";
    public static final String P_CronTab = "CronTab";
    public static final String PROPERTY_JarDirectory = "JarDirectory";
    public static final String P_JarDirectory = "JarDirectory";
    public static final String PROPERTY_TsamTelnet = "TsamTelnet";
    public static final String P_TsamTelnet = "TsamTelnet";
    public static final String PROPERTY_InstallVersion = "InstallVersion";
    public static final String P_InstallVersion = "InstallVersion";
     
    public static final String PROPERTY_StatusCode = "StatusCode";
    public static final String P_StatusCode = "StatusCode";
    public static final String PROPERTY_IDLVersion = "IDLVersion";
    public static final String P_IDLVersion = "IDLVersion";
    public static final String PROPERTY_AdminClientVersion = "AdminClientVersion";
    public static final String P_AdminClientVersion = "AdminClientVersion";
     
    public static final String PROPERTY_MRADClient = "MRADClient";
    public static final String P_MRADClient = "MRADClient";
     
    protected int id;
    protected OADateTime lastSSHCheck;
    protected String sshError;
    protected String hostDate;
    protected String hostName;
    protected String unixName;
    protected String cronTab;
    protected String jarDirectory;
    protected String tsamTelnet;
    protected String installVersion;
     
    // Links to other objects.
    protected transient MRADClient mradClient;
     
    public HostInfo() {
    }
     
    public HostInfo(int id) {
        this();
        setId(id);
    }
     
    @OAProperty(isUnique = true, displayLength = 5)
    @OAId()
    @OAColumn(sqlType = java.sql.Types.INTEGER)
    public int getId() {
        return id;
    }
    public void setId(int newValue) {
        fireBeforePropertyChange(P_Id, this.id, newValue);
        int old = id;
        this.id = newValue;
        firePropertyChange(P_Id, old, this.id);
    }
    
    @OAProperty(displayName = "Last SSH Check", displayLength = 15, columnLength = 12)
    @OAColumn(sqlType = java.sql.Types.TIMESTAMP)
    public OADateTime getLastSSHCheck() {
        return lastSSHCheck;
    }
    public void setLastSSHCheck(OADateTime newValue) {
        fireBeforePropertyChange(P_LastSSHCheck, this.lastSSHCheck, newValue);
        OADateTime old = lastSSHCheck;
        this.lastSSHCheck = newValue;
        firePropertyChange(P_LastSSHCheck, old, this.lastSSHCheck);
    }
    
    @OAProperty(displayName = "SSH Error", maxLength = 200, displayLength = 20, columnLength = 15)
    @OAColumn(maxLength = 200)
    public String getSSHError() {
        return sshError;
    }
    public void setSSHError(String newValue) {
        fireBeforePropertyChange(P_SSHError, this.sshError, newValue);
        String old = sshError;
        this.sshError = newValue;
        firePropertyChange(P_SSHError, old, this.sshError);
    }
    
    @OAProperty(displayName = "Host Date", maxLength = 35, displayLength = 14, columnLength = 10)
    @OAColumn(maxLength = 35)
    public String getHostDate() {
        return hostDate;
    }
    public void setHostDate(String newValue) {
        fireBeforePropertyChange(P_HostDate, this.hostDate, newValue);
        String old = hostDate;
        this.hostDate = newValue;
        firePropertyChange(P_HostDate, old, this.hostDate);
    }
    
    @OAProperty(displayName = "Host Name", maxLength = 35, displayLength = 8, columnLength = 7)
    @OAColumn(maxLength = 35)
    public String getHostName() {
        return hostName;
    }
    public void setHostName(String newValue) {
        fireBeforePropertyChange(P_HostName, this.hostName, newValue);
        String old = hostName;
        this.hostName = newValue;
        firePropertyChange(P_HostName, old, this.hostName);
    }
    
    @OAProperty(displayName = "Unix Name", maxLength = 35, displayLength = 12, columnLength = 10)
    @OAColumn(maxLength = 35)
    public String getUnixName() {
        return unixName;
    }
    public void setUnixName(String newValue) {
        fireBeforePropertyChange(P_UnixName, this.unixName, newValue);
        String old = unixName;
        this.unixName = newValue;
        firePropertyChange(P_UnixName, old, this.unixName);
    }
    
    @OAProperty(displayName = "Cron Tab", maxLength = 7, displayLength = 7)
    @OAColumn(sqlType = java.sql.Types.CLOB)
    public String getCronTab() {
        return cronTab;
    }
    public void setCronTab(String newValue) {
        fireBeforePropertyChange(P_CronTab, this.cronTab, newValue);
        String old = cronTab;
        this.cronTab = newValue;
        firePropertyChange(P_CronTab, old, this.cronTab);
    }
    
    @OAProperty(displayName = "Jar Directory", maxLength = 12, displayLength = 10, columnLength = 8)
    @OAColumn(sqlType = java.sql.Types.CLOB)
    public String getJarDirectory() {
        return jarDirectory;
    }
    public void setJarDirectory(String newValue) {
        fireBeforePropertyChange(P_JarDirectory, this.jarDirectory, newValue);
        String old = jarDirectory;
        this.jarDirectory = newValue;
        firePropertyChange(P_JarDirectory, old, this.jarDirectory);
    }
    
    @OAProperty(displayName = "Tsam Telnet", maxLength = 400, displayLength = 15, columnLength = 10)
    @OAColumn(maxLength = 400)
    public String getTsamTelnet() {
        return tsamTelnet;
    }
    public void setTsamTelnet(String newValue) {
        fireBeforePropertyChange(P_TsamTelnet, this.tsamTelnet, newValue);
        String old = tsamTelnet;
        this.tsamTelnet = newValue;
        firePropertyChange(P_TsamTelnet, old, this.tsamTelnet);
    }
    
    @OAProperty(displayName = "Install Version", maxLength = 35, displayLength = 15, columnLength = 10)
    @OAColumn(maxLength = 35)
    public String getInstallVersion() {
        return installVersion;
    }
    public void setInstallVersion(String newValue) {
        fireBeforePropertyChange(P_InstallVersion, this.installVersion, newValue);
        String old = installVersion;
        this.installVersion = newValue;
        firePropertyChange(P_InstallVersion, old, this.installVersion);
    }
    
    @OACalculatedProperty(displayName = "Status Code", displayLength = 6, properties = {P_LastSSHCheck, P_MRADClient+"."+MRADClient.P_IpAddress, P_MRADClient+"."+MRADClient.P_Application+"."+Application.P_ApplicationVersions+"."+ApplicationVersion.P_PackageType, P_SSHError})
    public int getStatusCode() {
        return HostInfoDelegate.getStatusCode(this);
    }
    
     
    @OACalculatedProperty(displayName = "IDL Version", displayLength = 8, properties = {P_JarDirectory})
    public String getIDLVersion() {
        String s = getJarDirectory();
        if (OAString.isEmpty(s)) return null;
        String s2 = "idl-";
        int pos = s.indexOf(s2);
        if (pos < 0) return null;
        int pos2 = s.indexOf(".jar", pos);
        if (pos2 < 0) return null;
        s = s.substring(pos+s2.length(), pos2);
        return s; 
    }
    
     
    @OACalculatedProperty(displayName = "Admin Client Version", displayLength = 8, columnLength = 6, properties = {P_JarDirectory})
    public String getAdminClientVersion() {
        String s = getJarDirectory();
        if (OAString.isEmpty(s)) return null;
        String s2 = "iceadminclient_util-";
        int pos = s.indexOf(s2);
        if (pos < 0) return null;
        int pos2 = s.indexOf(".jar", pos);
        if (pos2 < 0) return null;
        s = s.substring(pos+s2.length(), pos2);
        return s; 
    }
    
     
    @OAOne(
        displayName = "MRAD Client", 
        reverseName = MRADClient.P_HostInfo, 
        required = true, 
        allowCreateNew = false
    )
    @OAFkey(columns = {"MradClientId"})
    public MRADClient getMRADClient() {
        if (mradClient == null) {
            mradClient = (MRADClient) getObject(P_MRADClient);
        }
        return mradClient;
    }
    
    public void setMRADClient(MRADClient newValue) {
        fireBeforePropertyChange(P_MRADClient, this.mradClient, newValue);
        MRADClient old = this.mradClient;
        this.mradClient = newValue;
        firePropertyChange(P_MRADClient, old, this.mradClient);
    }
    
    public void load(ResultSet rs, int id) throws SQLException {
        this.id = id;
        java.sql.Timestamp timestamp;
        timestamp = rs.getTimestamp(2);
        if (timestamp != null) this.lastSSHCheck = new OADateTime(timestamp);
        this.sshError = rs.getString(3);
        this.hostDate = rs.getString(4);
        this.hostName = rs.getString(5);
        this.unixName = rs.getString(6);
        this.cronTab = rs.getString(7);
        this.jarDirectory = rs.getString(8);
        this.tsamTelnet = rs.getString(9);
        this.installVersion = rs.getString(10);
        int mradClientFkey = rs.getInt(11);
        if (!rs.wasNull() && mradClientFkey > 0) {
            setProperty(P_MRADClient, new OAObjectKey(mradClientFkey));
        }
        if (rs.getMetaData().getColumnCount() != 11) {
            throw new SQLException("invalid number of columns for load method");
        }

        changedFlag = false;
        newFlag = false;
    }
}
 

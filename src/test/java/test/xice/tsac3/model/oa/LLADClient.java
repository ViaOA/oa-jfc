// Generated by OABuilder
package test.xice.tsac3.model.oa;
 
import java.sql.*;
import com.viaoa.object.*;
import com.viaoa.hub.*;
import com.viaoa.util.*;
import com.viaoa.annotation.*;
import com.viaoa.util.OADateTime;

import test.xice.tsac3.model.oa.filter.*;
import test.xice.tsac3.model.oa.propertypath.*;
 
@OAClass(
    shortName = "lladc",
    displayName = "LLAD Client",
    displayProperty = "server"
)
@OATable(
    indexes = {
        @OAIndex(name = "LLADClientLladServer", columns = { @OAIndexColumn(name = "LladServerId") })
    }
)
public class LLADClient extends OAObject {
    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_Id = "Id";
    public static final String P_Id = "Id";
    public static final String PROPERTY_RouterName = "RouterName";
    public static final String P_RouterName = "RouterName";
    public static final String PROPERTY_RouterType = "RouterType";
    public static final String P_RouterType = "RouterType";
    public static final String PROPERTY_IpAddress = "IpAddress";
    public static final String P_IpAddress = "IpAddress";
    public static final String PROPERTY_StartedDateTime = "StartedDateTime";
    public static final String P_StartedDateTime = "StartedDateTime";
    public static final String PROPERTY_RegisteredDateTime = "RegisteredDateTime";
    public static final String P_RegisteredDateTime = "RegisteredDateTime";
    public static final String PROPERTY_LastPingDateTime = "LastPingDateTime";
    public static final String P_LastPingDateTime = "LastPingDateTime";
    public static final String PROPERTY_LastHeartbeatDateTime = "LastHeartbeatDateTime";
    public static final String P_LastHeartbeatDateTime = "LastHeartbeatDateTime";
    public static final String PROPERTY_Status = "Status";
    public static final String P_Status = "Status";
    public static final String PROPERTY_StatusAsString = "StatusAsString";
    public static final String P_StatusAsString = "StatusAsString";
    public static final String PROPERTY_ActiveMode = "ActiveMode";
    public static final String P_ActiveMode = "ActiveMode";
    public static final String PROPERTY_ViewOnly = "ViewOnly";
    public static final String P_ViewOnly = "ViewOnly";
    public static final String PROPERTY_ServerVersion = "ServerVersion";
    public static final String P_ServerVersion = "ServerVersion";
    public static final String PROPERTY_IdlVersion = "IdlVersion";
    public static final String P_IdlVersion = "IdlVersion";
     
    public static final String PROPERTY_EnableLLADCommands = "EnableLLADCommands";
    public static final String P_EnableLLADCommands = "EnableLLADCommands";
     
    public static final String PROPERTY_LLADServer = "LLADServer";
    public static final String P_LLADServer = "LLADServer";
    public static final String PROPERTY_Server = "Server";
    public static final String P_Server = "Server";
    public static final String PROPERTY_UserLoginHistories = "UserLoginHistories";
    public static final String P_UserLoginHistories = "UserLoginHistories";
    public static final String PROPERTY_UserLogins = "UserLogins";
    public static final String P_UserLogins = "UserLogins";
     
    protected int id;
    protected String routerName;
    protected String routerType;
    protected String ipAddress;
    protected OADateTime startedDateTime;
    protected OADateTime registeredDateTime;
    protected OADateTime lastPingDateTime;
    protected OADateTime lastHeartbeatDateTime;
    protected int status;
    public static final int STATUS_Unknown = 0;
    public static final int STATUS_Connected = 1;
    public static final int STATUS_Connecting = 2;
    public static final int STATUS_Suspended = 3;
    public static final int STATUS_Suspending = 4;
    public static final int STATUS_Resuming = 5;
    public static final int STATUS_Disconnecting = 6;
    public static final int STATUS_Disconnected = 7;
    public static final int STATUS_Error = 8;
    public static final Hub<String> hubStatus;
    static {
        hubStatus = new Hub<String>(String.class);
        hubStatus.addElement("Unknown");
        hubStatus.addElement("Connected");
        hubStatus.addElement("Connecting");
        hubStatus.addElement("Suspended");
        hubStatus.addElement("Suspending");
        hubStatus.addElement("Resuming");
        hubStatus.addElement("Disconnecting");
        hubStatus.addElement("Disconnected");
        hubStatus.addElement("Error");
    }
    protected boolean activeMode;
    protected boolean viewOnly;
    protected String serverVersion;
    protected String idlVersion;
     
    // Links to other objects.
    protected transient LLADServer lladServer;
    protected transient Server server;
    protected transient Hub<UserLoginHistory> hubUserLoginHistories;
    protected transient Hub<UserLogin> hubUserLogins;
     
    public LLADClient() {
    }
     
    public LLADClient(int id) {
        this();
        setId(id);
    }
     
    @OAProperty(isUnique = true, displayLength = 5, isProcessed = true)
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
    @OAProperty(displayName = "Router Name", maxLength = 75, displayLength = 30, columnLength = 35, isProcessed = true)
    @OAColumn(maxLength = 75)
    public String getRouterName() {
        return routerName;
    }
    
    public void setRouterName(String newValue) {
        fireBeforePropertyChange(P_RouterName, this.routerName, newValue);
        String old = routerName;
        this.routerName = newValue;
        firePropertyChange(P_RouterName, old, this.routerName);
    }
    @OAProperty(displayName = "Router Type", maxLength = 10, displayLength = 10, columnLength = 15, isProcessed = true)
    @OAColumn(maxLength = 10)
    public String getRouterType() {
        return routerType;
    }
    
    public void setRouterType(String newValue) {
        fireBeforePropertyChange(P_RouterType, this.routerType, newValue);
        String old = routerType;
        this.routerType = newValue;
        firePropertyChange(P_RouterType, old, this.routerType);
    }
    @OAProperty(displayName = "Ip Address", maxLength = 9, displayLength = 9, columnLength = 12, isProcessed = true)
    @OAColumn(maxLength = 9)
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String newValue) {
        fireBeforePropertyChange(P_IpAddress, this.ipAddress, newValue);
        String old = ipAddress;
        this.ipAddress = newValue;
        firePropertyChange(P_IpAddress, old, this.ipAddress);
    }
    @OAProperty(displayName = "Started Date Time", displayLength = 20, columnLength = 17, isProcessed = true)
    @OAColumn(sqlType = java.sql.Types.TIMESTAMP)
    public OADateTime getStartedDateTime() {
        return startedDateTime;
    }
    
    public void setStartedDateTime(OADateTime newValue) {
        fireBeforePropertyChange(P_StartedDateTime, this.startedDateTime, newValue);
        OADateTime old = startedDateTime;
        this.startedDateTime = newValue;
        firePropertyChange(P_StartedDateTime, old, this.startedDateTime);
    }
    @OAProperty(displayName = "Registered", displayLength = 15, isProcessed = true)
    @OAColumn(sqlType = java.sql.Types.TIMESTAMP)
    public OADateTime getRegisteredDateTime() {
        return registeredDateTime;
    }
    
    public void setRegisteredDateTime(OADateTime newValue) {
        fireBeforePropertyChange(P_RegisteredDateTime, this.registeredDateTime, newValue);
        OADateTime old = registeredDateTime;
        this.registeredDateTime = newValue;
        firePropertyChange(P_RegisteredDateTime, old, this.registeredDateTime);
    }
    @OAProperty(displayName = "Last Ping", displayLength = 15, isProcessed = true)
    @OAColumn(sqlType = java.sql.Types.TIMESTAMP)
    public OADateTime getLastPingDateTime() {
        return lastPingDateTime;
    }
    
    public void setLastPingDateTime(OADateTime newValue) {
        fireBeforePropertyChange(P_LastPingDateTime, this.lastPingDateTime, newValue);
        OADateTime old = lastPingDateTime;
        this.lastPingDateTime = newValue;
        firePropertyChange(P_LastPingDateTime, old, this.lastPingDateTime);
    }
    @OAProperty(displayName = "Last Heartbeat", displayLength = 15, columnName = "Heartbeat", isProcessed = true)
    @OAColumn(sqlType = java.sql.Types.TIMESTAMP)
    public OADateTime getLastHeartbeatDateTime() {
        return lastHeartbeatDateTime;
    }
    
    public void setLastHeartbeatDateTime(OADateTime newValue) {
        fireBeforePropertyChange(P_LastHeartbeatDateTime, this.lastHeartbeatDateTime, newValue);
        OADateTime old = lastHeartbeatDateTime;
        this.lastHeartbeatDateTime = newValue;
        firePropertyChange(P_LastHeartbeatDateTime, old, this.lastHeartbeatDateTime);
    }
    @OAProperty(displayLength = 18, columnLength = 15, isProcessed = true, isNameValue = true)
    @OAColumn(sqlType = java.sql.Types.INTEGER)
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int newValue) {
        fireBeforePropertyChange(P_Status, this.status, newValue);
        int old = status;
        this.status = newValue;
        firePropertyChange(P_Status, old, this.status);
    }
    public String getStatusAsString() {
        if (isNull(P_Status)) return "";
        String s = hubStatus.getAt(getStatus());
        if (s == null) s = "";
        return s;
    }
    @OAProperty(displayName = "Active Mode", displayLength = 5)
    @OAColumn(sqlType = java.sql.Types.BOOLEAN)
    public boolean getActiveMode() {
        return activeMode;
    }
    
    public void setActiveMode(boolean newValue) {
        fireBeforePropertyChange(P_ActiveMode, this.activeMode, newValue);
        boolean old = activeMode;
        this.activeMode = newValue;
        firePropertyChange(P_ActiveMode, old, this.activeMode);
    }
    @OAProperty(displayName = "View Only", displayLength = 5, isProcessed = true)
    @OAColumn(sqlType = java.sql.Types.BOOLEAN)
    public boolean getViewOnly() {
        return viewOnly;
    }
    
    public void setViewOnly(boolean newValue) {
        fireBeforePropertyChange(P_ViewOnly, this.viewOnly, newValue);
        boolean old = viewOnly;
        this.viewOnly = newValue;
        firePropertyChange(P_ViewOnly, old, this.viewOnly);
    }
    @OAProperty(displayName = "Server Version", maxLength = 55, displayLength = 18, columnLength = 14, isProcessed = true)
    @OAColumn(maxLength = 55)
    public String getServerVersion() {
        return serverVersion;
    }
    
    public void setServerVersion(String newValue) {
        fireBeforePropertyChange(P_ServerVersion, this.serverVersion, newValue);
        String old = serverVersion;
        this.serverVersion = newValue;
        firePropertyChange(P_ServerVersion, old, this.serverVersion);
    }
    @OAProperty(displayName = "Idl Version", maxLength = 25, displayLength = 12, columnLength = 8, isProcessed = true)
    @OAColumn(maxLength = 25)
    public String getIdlVersion() {
        return idlVersion;
    }
    
    public void setIdlVersion(String newValue) {
        fireBeforePropertyChange(P_IdlVersion, this.idlVersion, newValue);
        String old = idlVersion;
        this.idlVersion = newValue;
        firePropertyChange(P_IdlVersion, old, this.idlVersion);
    }
    @OACalculatedProperty(displayName = "Enable LLAD Commands", displayLength = 5, columnName = "Enable LLAD")
    public boolean getEnableLLADCommands() {
        return false;
    }
     
    @OAOne(
        reverseName = LLADServer.P_LLADClients, 
        required = true, 
        allowCreateNew = false
    )
    @OAFkey(columns = {"LladServerId"})
    public LLADServer getLLADServer() {
        if (lladServer == null) {
            lladServer = (LLADServer) getObject(P_LLADServer);
        }
        return lladServer;
    }
    
    public void setLLADServer(LLADServer newValue) {
        fireBeforePropertyChange(P_LLADServer, this.lladServer, newValue);
        LLADServer old = this.lladServer;
        this.lladServer = newValue;
        firePropertyChange(P_LLADServer, old, this.lladServer);
    }
    
    @OAOne(
        reverseName = Server.P_LLADClient, 
        required = true, 
        mustBeEmptyForDelete = true
    )
    @OAFkey(columns = {"ServerId"})
    public Server getServer() {
        if (server == null) {
            server = (Server) getObject(P_Server);
        }
        return server;
    }
    
    public void setServer(Server newValue) {
        fireBeforePropertyChange(P_Server, this.server, newValue);
        Server old = this.server;
        this.server = newValue;
        firePropertyChange(P_Server, old, this.server);
    }
    
    @OAMany(
        displayName = "User Login Histories", 
        toClass = UserLoginHistory.class, 
        owner = true, 
        reverseName = UserLoginHistory.P_LLADClient, 
        cascadeSave = true, 
        cascadeDelete = true, 
        uniqueProperty = UserLoginHistory.P_User
    )
    public Hub<UserLoginHistory> getUserLoginHistories() {
        if (hubUserLoginHistories == null) {
            hubUserLoginHistories = (Hub<UserLoginHistory>) getHub(P_UserLoginHistories);
        }
        return hubUserLoginHistories;
    }
    
    @OAMany(
        displayName = "User Logins", 
        toClass = UserLogin.class, 
        reverseName = UserLogin.P_LLADClient, 
        cascadeSave = true, 
        cascadeDelete = true
    )
    public Hub<UserLogin> getUserLogins() {
        if (hubUserLogins == null) {
            hubUserLogins = (Hub<UserLogin>) getHub(P_UserLogins);
        }
        return hubUserLogins;
    }
    
    // forceLogoutAllUsers - Force Logout all Users
    public void forceLogoutAllUsers(String reason) {
    }
     
    public void load(ResultSet rs, int id) throws SQLException {
        this.id = id;
        this.routerName = rs.getString(2);
        this.routerType = rs.getString(3);
        this.ipAddress = rs.getString(4);
        java.sql.Timestamp timestamp;
        timestamp = rs.getTimestamp(5);
        if (timestamp != null) this.startedDateTime = new OADateTime(timestamp);
        timestamp = rs.getTimestamp(6);
        if (timestamp != null) this.registeredDateTime = new OADateTime(timestamp);
        timestamp = rs.getTimestamp(7);
        if (timestamp != null) this.lastPingDateTime = new OADateTime(timestamp);
        timestamp = rs.getTimestamp(8);
        if (timestamp != null) this.lastHeartbeatDateTime = new OADateTime(timestamp);
        this.status = (int) rs.getInt(9);
        if (rs.wasNull()) {
            OAObjectInfoDelegate.setPrimitiveNull(this, LLADClient.P_Status, true);
        }
        this.activeMode = rs.getBoolean(10);
        if (rs.wasNull()) {
            OAObjectInfoDelegate.setPrimitiveNull(this, LLADClient.P_ActiveMode, true);
        }
        this.viewOnly = rs.getBoolean(11);
        if (rs.wasNull()) {
            OAObjectInfoDelegate.setPrimitiveNull(this, LLADClient.P_ViewOnly, true);
        }
        this.serverVersion = rs.getString(12);
        this.idlVersion = rs.getString(13);
        int lladServerFkey = rs.getInt(14);
        if (!rs.wasNull() && lladServerFkey > 0) {
            setProperty(P_LLADServer, new OAObjectKey(lladServerFkey));
        }
        int serverFkey = rs.getInt(15);
        if (!rs.wasNull() && serverFkey > 0) {
            setProperty(P_Server, new OAObjectKey(serverFkey));
        }
        if (rs.getMetaData().getColumnCount() != 15) {
            throw new SQLException("invalid number of columns for load method");
        }

        changedFlag = false;
        newFlag = false;
    }
}
 

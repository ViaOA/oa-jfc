// Generated by OABuilder
package test.xice.tsac3.model.oa;
 

import java.sql.*;
import com.viaoa.object.*;
import com.viaoa.hub.*;
import com.viaoa.util.*;
import com.viaoa.annotation.*;
import com.viaoa.util.OADateTime;

import test.xice.tsac3.model.oa.propertypath.*;
 
@OAClass(
    shortName = "env",
    displayName = "Environment",
    displayProperty = "name",
    rootTreePropertyPaths = {
        "[Site]."+Site.P_Environments
    }
)
@OATable(
    indexes = {
        @OAIndex(name = "EnvironmentIdL", columns = { @OAIndexColumn(name = "IdLId") }), 
        @OAIndex(name = "EnvironmentSite", columns = { @OAIndexColumn(name = "SiteId") })
    }
)
public class Environment extends OAObject {
    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_Id = "Id";
    public static final String P_Id = "Id";
    public static final String PROPERTY_Name = "Name";
    public static final String P_Name = "Name";
    public static final String PROPERTY_StatsLastUpdated = "StatsLastUpdated";
    public static final String P_StatsLastUpdated = "StatsLastUpdated";
    public static final String PROPERTY_Status = "Status";
    public static final String P_Status = "Status";
    public static final String PROPERTY_UsesDNS = "UsesDNS";
    public static final String P_UsesDNS = "UsesDNS";
    public static final String PROPERTY_UsesFirewall = "UsesFirewall";
    public static final String P_UsesFirewall = "UsesFirewall";
    public static final String PROPERTY_UsesVip = "UsesVip";
    public static final String P_UsesVip = "UsesVip";
    public static final String PROPERTY_AbbrevName = "AbbrevName";
    public static final String P_AbbrevName = "AbbrevName";
     
     
    public static final String PROPERTY_ActiveServerInstalls = "ActiveServerInstalls";
    public static final String P_ActiveServerInstalls = "ActiveServerInstalls";
    public static final String PROPERTY_CalcUsers = "CalcUsers";
    public static final String P_CalcUsers = "CalcUsers";
    public static final String PROPERTY_Companies = "Companies";
    public static final String P_Companies = "Companies";
    public static final String PROPERTY_EnvironmentServerTypes = "EnvironmentServerTypes";
    public static final String P_EnvironmentServerTypes = "EnvironmentServerTypes";
    public static final String PROPERTY_EnvironmentType = "EnvironmentType";
    public static final String P_EnvironmentType = "EnvironmentType";
    public static final String PROPERTY_IDL = "IDL";
    public static final String P_IDL = "IDL";
    public static final String PROPERTY_LoginUsers = "LoginUsers";
    public static final String P_LoginUsers = "LoginUsers";
    public static final String PROPERTY_MarketTypes = "MarketTypes";
    public static final String P_MarketTypes = "MarketTypes";
    public static final String PROPERTY_RCInstalledVersions = "RCInstalledVersions";
    public static final String P_RCInstalledVersions = "RCInstalledVersions";
    public static final String PROPERTY_Silos = "Silos";
    public static final String P_Silos = "Silos";
    public static final String PROPERTY_Site = "Site";
    public static final String P_Site = "Site";
     
    protected int id;
    protected String name;
    protected OADateTime statsLastUpdated;
    protected String status;
    protected boolean usesDNS;
    protected boolean usesFirewall;
    protected boolean usesVip;
    protected String abbrevName;
     
    // Links to other objects.
    protected transient Hub<ServerInstall> hubActiveServerInstalls;
    // protected transient Hub<User> hubCalcUsers;
    protected transient Hub<Company> hubCompanies;
    protected transient Hub<EnvironmentServerType> hubEnvironmentServerTypes;
    protected transient EnvironmentType environmentType;
    protected transient IDL idL;
    protected transient Hub<User> hubLoginUsers;
    protected transient Hub<MarketType> hubMarketTypes;
    protected transient Hub<RCInstalledVersion> hubRCInstalledVersions;
    // protected transient Hub<Silo> hubSilos;
    protected transient Site site;
     
    public Environment() {
    }
     
    public Environment(int id) {
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
    @OAProperty(maxLength = 25, displayLength = 12)
    @OAColumn(maxLength = 25)
    public String getName() {
        return name;
    }
    
    public void setName(String newValue) {
        fireBeforePropertyChange(P_Name, this.name, newValue);
        String old = name;
        this.name = newValue;
        firePropertyChange(P_Name, old, this.name);
    }
    @OAProperty(displayName = "Stats Last Updated", displayLength = 15, isProcessed = true)
    @OAColumn(sqlType = java.sql.Types.TIMESTAMP)
    public OADateTime getStatsLastUpdated() {
        return statsLastUpdated;
    }
    
    public void setStatsLastUpdated(OADateTime newValue) {
        fireBeforePropertyChange(P_StatsLastUpdated, this.statsLastUpdated, newValue);
        OADateTime old = statsLastUpdated;
        this.statsLastUpdated = newValue;
        firePropertyChange(P_StatsLastUpdated, old, this.statsLastUpdated);
    }
    @OAProperty(maxLength = 55, displayLength = 40, columnLength = 25, isProcessed = true)
    @OAColumn(maxLength = 55)
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String newValue) {
        fireBeforePropertyChange(P_Status, this.status, newValue);
        String old = status;
        this.status = newValue;
        firePropertyChange(P_Status, old, this.status);
    }
    @OAProperty(displayName = "Uses DNS", displayLength = 5)
    @OAColumn(name = "HasDNS", sqlType = java.sql.Types.BOOLEAN)
    public boolean getUsesDNS() {
        return usesDNS;
    }
    
    public void setUsesDNS(boolean newValue) {
        fireBeforePropertyChange(P_UsesDNS, this.usesDNS, newValue);
        boolean old = usesDNS;
        this.usesDNS = newValue;
        firePropertyChange(P_UsesDNS, old, this.usesDNS);
    }
    @OAProperty(displayName = "Uses Firewall", displayLength = 5)
    @OAColumn(name = "HasFirewall", sqlType = java.sql.Types.BOOLEAN)
    public boolean getUsesFirewall() {
        return usesFirewall;
    }
    
    public void setUsesFirewall(boolean newValue) {
        fireBeforePropertyChange(P_UsesFirewall, this.usesFirewall, newValue);
        boolean old = usesFirewall;
        this.usesFirewall = newValue;
        firePropertyChange(P_UsesFirewall, old, this.usesFirewall);
    }
    @OAProperty(displayName = "Uses VIP", displayLength = 5)
    @OAColumn(sqlType = java.sql.Types.BOOLEAN)
    public boolean getUsesVip() {
        return usesVip;
    }
    
    public void setUsesVip(boolean newValue) {
        fireBeforePropertyChange(P_UsesVip, this.usesVip, newValue);
        boolean old = usesVip;
        this.usesVip = newValue;
        firePropertyChange(P_UsesVip, old, this.usesVip);
    }
    @OAProperty(displayName = "Abbrev Name", maxLength = 6, displayLength = 6)
    @OAColumn(maxLength = 6)
    public String getAbbrevName() {
        return abbrevName;
    }
    
    public void setAbbrevName(String newValue) {
        fireBeforePropertyChange(P_AbbrevName, this.abbrevName, newValue);
        String old = abbrevName;
        this.abbrevName = newValue;
        firePropertyChange(P_AbbrevName, old, this.abbrevName);
    }
    @OAMany(
        displayName = "Active Server Installs", 
        toClass = ServerInstall.class, 
        isCalculated = true, 
        isServerSideCalc = true, 
        reverseName = ServerInstall.P_CalcEnvironment
    )
    public Hub<ServerInstall> getActiveServerInstalls() {
        if (hubActiveServerInstalls != null) return hubActiveServerInstalls;
        hubActiveServerInstalls = (Hub<ServerInstall>) getHub(P_ActiveServerInstalls);
        boolean b = !isServer();
        if (b) return hubActiveServerInstalls;  // serverSide calculated hub
        new HubMerger(this, hubActiveServerInstalls,
            OAString.cpp(Environment.P_Silos, Silo.P_Servers, Server.P_ServerInstalls)
        );
        return hubActiveServerInstalls;
    }
    @OAMany(
        displayName = "Users", 
        toClass = User.class, 
        isCalculated = true, 
        isServerSideCalc = true, 
        cacheSize = 5, 
        reverseName = User.P_CalcEnvironment1
    )
    public Hub<User> getCalcUsers() {
        boolean b = !isServer() || isHubLoaded(P_CalcUsers);
        Hub<User> hubCalcUsers = (Hub<User>) getHub(P_CalcUsers);
        if (b) return hubCalcUsers;  // serverSide calculated hub
        new HubMerger(this, hubCalcUsers,
            OAString.cpp(Environment.P_Silos, Silo.P_LLADServer, LLADServer.P_Users)
        );
        return hubCalcUsers;
    }
    @OAMany(
        toClass = Company.class, 
        owner = true, 
        reverseName = Company.P_Environment, 
        cascadeSave = true, 
        cascadeDelete = true
    )
    public Hub<Company> getCompanies() {
        if (hubCompanies == null) {
            hubCompanies = (Hub<Company>) getHub(P_Companies);
        }
        return hubCompanies;
    }
    
    @OAMany(
        displayName = "Environment Server Types", 
        toClass = EnvironmentServerType.class, 
        owner = true, 
        reverseName = EnvironmentServerType.P_Environment, 
        cascadeSave = true, 
        cascadeDelete = true, 
        matchHub = (Environment.P_Silos+"."+Silo.P_SiloType+"."+SiloType.P_ServerTypes), 
        matchProperty = EnvironmentServerType.P_ServerType
    )
    public Hub<EnvironmentServerType> getEnvironmentServerTypes() {
        if (hubEnvironmentServerTypes == null) {
            hubEnvironmentServerTypes = (Hub<EnvironmentServerType>) getHub(P_EnvironmentServerTypes);
        }
        return hubEnvironmentServerTypes;
    }
    
    @OAOne(
        displayName = "Environment Type", 
        reverseName = EnvironmentType.P_Environments, 
        allowCreateNew = false
    )
    @OAFkey(columns = {"EnvironmentTypeId"})
    public EnvironmentType getEnvironmentType() {
        if (environmentType == null) {
            environmentType = (EnvironmentType) getObject(P_EnvironmentType);
        }
        return environmentType;
    }
    
    public void setEnvironmentType(EnvironmentType newValue) {
        fireBeforePropertyChange(P_EnvironmentType, this.environmentType, newValue);
        EnvironmentType old = this.environmentType;
        this.environmentType = newValue;
        firePropertyChange(P_EnvironmentType, old, this.environmentType);
    }
    
    @OAOne(
        reverseName = IDL.P_Environments, 
        allowCreateNew = false
    )
    @OAFkey(columns = {"IdLId"})
    public IDL getIDL() {
        if (idL == null) {
            idL = (IDL) getObject(P_IDL);
        }
        return idL;
    }
    
    public void setIDL(IDL newValue) {
        fireBeforePropertyChange(P_IDL, this.idL, newValue);
        IDL old = this.idL;
        this.idL = newValue;
        firePropertyChange(P_IDL, old, this.idL);
    }
    
    @OAMany(
        displayName = "Logins", 
        toClass = User.class, 
        isCalculated = true, 
        reverseName = User.P_CalcEnvironment
    )
    public Hub<User> getLoginUsers() {
        if (hubLoginUsers != null) return hubLoginUsers;
        hubLoginUsers = (Hub<User>) getHub(P_LoginUsers);
        new HubMerger(this, hubLoginUsers,
            OAString.cpp(Environment.P_Silos, Silo.P_LLADServer, LLADServer.P_LLADClients, LLADClient.P_UserLogins, UserLogin.P_User)
        );
        return hubLoginUsers;
    }
    @OAMany(
        displayName = "Market Types", 
        toClass = MarketType.class, 
        owner = true, 
        reverseName = MarketType.P_Environment, 
        cascadeSave = true, 
        cascadeDelete = true
    )
    public Hub<MarketType> getMarketTypes() {
        if (hubMarketTypes == null) {
            hubMarketTypes = (Hub<MarketType>) getHub(P_MarketTypes);
        }
        return hubMarketTypes;
    }
    
    @OAMany(
        displayName = "RCInstalled Versions", 
        toClass = RCInstalledVersion.class, 
        owner = true, 
        reverseName = RCInstalledVersion.P_Environment, 
        cascadeSave = true, 
        cascadeDelete = true
    )
    public Hub<RCInstalledVersion> getRCInstalledVersions() {
        if (hubRCInstalledVersions == null) {
            hubRCInstalledVersions = (Hub<RCInstalledVersion>) getHub(P_RCInstalledVersions);
        }
        return hubRCInstalledVersions;
    }
    
    @OAMany(
        toClass = Silo.class, 
        owner = true, 
        cacheSize = 100, 
        reverseName = Silo.P_Environment, 
        cascadeSave = true, 
        cascadeDelete = true, 
        uniqueProperty = Silo.P_SiloType
    )
    public Hub<Silo> getSilos() {
        Hub<Silo> hubSilos;
        {
            hubSilos = (Hub<Silo>) getHub(P_Silos);
        }
        return hubSilos;
    }
    
    @OAOne(
        reverseName = Site.P_Environments, 
        required = true, 
        allowCreateNew = false
    )
    @OAFkey(columns = {"SiteId"})
    public Site getSite() {
        if (site == null) {
            site = (Site) getObject(P_Site);
        }
        return site;
    }
    
    public void setSite(Site newValue) {
        fireBeforePropertyChange(P_Site, this.site, newValue);
        Site old = this.site;
        this.site = newValue;
        firePropertyChange(P_Site, old, this.site);
    }
    
    public void load(ResultSet rs, int id) throws SQLException {
        this.id = id;
        this.name = rs.getString(2);
        java.sql.Timestamp timestamp;
        timestamp = rs.getTimestamp(3);
        if (timestamp != null) this.statsLastUpdated = new OADateTime(timestamp);
        this.status = rs.getString(4);
        this.usesDNS = rs.getBoolean(5);
        if (rs.wasNull()) {
            OAObjectInfoDelegate.setPrimitiveNull(this, Environment.P_UsesDNS, true);
        }
        this.usesFirewall = rs.getBoolean(6);
        if (rs.wasNull()) {
            OAObjectInfoDelegate.setPrimitiveNull(this, Environment.P_UsesFirewall, true);
        }
        this.usesVip = rs.getBoolean(7);
        if (rs.wasNull()) {
            OAObjectInfoDelegate.setPrimitiveNull(this, Environment.P_UsesVip, true);
        }
        this.abbrevName = rs.getString(8);
        int environmentTypeFkey = rs.getInt(9);
        if (!rs.wasNull() && environmentTypeFkey > 0) {
            setProperty(P_EnvironmentType, new OAObjectKey(environmentTypeFkey));
        }
        int idLFkey = rs.getInt(10);
        if (!rs.wasNull() && idLFkey > 0) {
            setProperty(P_IDL, new OAObjectKey(idLFkey));
        }
        int siteFkey = rs.getInt(11);
        if (!rs.wasNull() && siteFkey > 0) {
            setProperty(P_Site, new OAObjectKey(siteFkey));
        }
        if (rs.getMetaData().getColumnCount() != 11) {
            throw new SQLException("invalid number of columns for load method");
        }

        changedFlag = false;
        newFlag = false;
    }
}
 

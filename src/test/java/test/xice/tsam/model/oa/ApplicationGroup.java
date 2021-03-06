// Generated by OABuilder
package test.xice.tsam.model.oa;
 
import java.util.logging.*;
import java.sql.*;
import com.viaoa.object.*;
import com.viaoa.hub.*;
import com.viaoa.util.*;

import test.xice.tsam.delegate.oa.*;
import test.xice.tsam.model.oa.filter.*;
import test.xice.tsam.model.oa.propertypath.*;

import test.xice.tsam.model.oa.Application;
import test.xice.tsam.model.oa.ApplicationGroup;
import test.xice.tsam.model.oa.ApplicationType;
import test.xice.tsam.model.oa.Environment;
import test.xice.tsam.model.oa.Silo;
import test.xice.tsam.model.oa.Site;
import test.xice.tsam.model.oa.propertypath.ApplicationGroupPP;
import com.viaoa.annotation.*;
import com.viaoa.sync.OASync;
 
@OAClass(
    shortName = "ag",
    displayName = "Application Group",
    displayProperty = "name",
    sortProperty = "seq",
    rootTreePropertyPaths = {
        "[Site]."+Site.P_Environments+"."+Environment.P_Silos+"."+Silo.P_ApplicationGroups
    }
)
@OATable(
    indexes = {
        @OAIndex(name = "ApplicationGroupSilo", columns = { @OAIndexColumn(name = "SiloId") })
    }
)
public class ApplicationGroup extends OAObject {
    private static final long serialVersionUID = 1L;
    private static Logger LOG = Logger.getLogger(ApplicationGroup.class.getName());
    public static final String PROPERTY_Id = "Id";
    public static final String P_Id = "Id";
    public static final String PROPERTY_Code = "Code";
    public static final String P_Code = "Code";
    public static final String PROPERTY_Name = "Name";
    public static final String P_Name = "Name";
    public static final String PROPERTY_Seq = "Seq";
    public static final String P_Seq = "Seq";
     
     
    public static final String PROPERTY_ExcludeApplications = "ExcludeApplications";
    public static final String P_ExcludeApplications = "ExcludeApplications";
    public static final String PROPERTY_ExcludeApplicationTypes = "ExcludeApplicationTypes";
    public static final String P_ExcludeApplicationTypes = "ExcludeApplicationTypes";
    public static final String PROPERTY_IncludeApplications = "IncludeApplications";
    public static final String P_IncludeApplications = "IncludeApplications";
    public static final String PROPERTY_IncludeApplicationTypes = "IncludeApplicationTypes";
    public static final String P_IncludeApplicationTypes = "IncludeApplicationTypes";
    public static final String PROPERTY_SelectedApplications = "SelectedApplications";
    public static final String P_SelectedApplications = "SelectedApplications";
    public static final String PROPERTY_Silo = "Silo";
    public static final String P_Silo = "Silo";
     
    protected int id;
    protected String code;
    protected String name;
    protected int seq;
     
    // Links to other objects.
    protected transient Hub<Application> hubExcludeApplications;
    protected transient Hub<ApplicationType> hubExcludeApplicationTypes;
    protected transient Hub<Application> hubIncludeApplications;
    protected transient Hub<ApplicationType> hubIncludeApplicationTypes;
    protected transient Hub<Application> hubSelectedApplications;
    protected transient Silo silo;
     
    public ApplicationGroup() {
    }
     
    public ApplicationGroup(int id) {
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
    
    @OAProperty(maxLength = 15, displayLength = 8, columnLength = 16)
    @OAColumn(maxLength = 15)
    public String getCode() {
        return code;
    }
    public void setCode(String newValue) {
        fireBeforePropertyChange(P_Code, this.code, newValue);
        String old = code;
        this.code = newValue;
        firePropertyChange(P_Code, old, this.code);
    }
    
    @OAProperty(maxLength = 55, displayLength = 12, columnLength = 22)
    @OAColumn(maxLength = 55)
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        fireBeforePropertyChange(P_Name, this.name, newValue);
        String old = name;
        this.name = newValue;
        firePropertyChange(P_Name, old, this.name);
    }
    
    @OAProperty(displayLength = 5, isAutoSeq = true)
    @OAColumn(sqlType = java.sql.Types.INTEGER)
    public int getSeq() {
        return seq;
    }
    public void setSeq(int newValue) {
        fireBeforePropertyChange(P_Seq, this.seq, newValue);
        int old = seq;
        this.seq = newValue;
        firePropertyChange(P_Seq, old, this.seq);
    }
    
    @OAMany(
        displayName = "Exclude Applications", 
        toClass = Application.class, 
        reverseName = Application.P_ExcludeApplicationGroups
    )
    @OALinkTable(name = "ApplicationGroupExcludeApplication", indexName = "ApplicationExcludeApplicationGroup", columns = {"ApplicationGroupId"})
    public Hub<Application> getExcludeApplications() {
        if (hubExcludeApplications == null) {
            hubExcludeApplications = (Hub<Application>) getHub(P_ExcludeApplications);
        }
        return hubExcludeApplications;
    }
    
    @OAMany(
        displayName = "Exclude Application Types", 
        toClass = ApplicationType.class, 
        reverseName = ApplicationType.P_ExcludeApplicationGroups
    )
    @OALinkTable(name = "ApplicationGroupExcludeApplicationType", indexName = "ApplicationTypeExcludeApplicationGroup", columns = {"ApplicationGroupId"})
    public Hub<ApplicationType> getExcludeApplicationTypes() {
        if (hubExcludeApplicationTypes == null) {
            hubExcludeApplicationTypes = (Hub<ApplicationType>) getHub(P_ExcludeApplicationTypes);
        }
        return hubExcludeApplicationTypes;
    }
    
    @OAMany(
        displayName = "Include Applications", 
        toClass = Application.class, 
        reverseName = Application.P_IncludeApplicationGroups
    )
    @OALinkTable(name = "ApplicationGroupIncludeApplication", indexName = "ApplicationIncludeApplicationGroup", columns = {"ApplicationGroupId"})
    public Hub<Application> getIncludeApplications() {
        if (hubIncludeApplications == null) {
            hubIncludeApplications = (Hub<Application>) getHub(P_IncludeApplications);
        }
        return hubIncludeApplications;
    }
    
    @OAMany(
        displayName = "Include Application Types", 
        toClass = ApplicationType.class, 
        reverseName = ApplicationType.P_IncludeApplicationGroups
    )
    @OALinkTable(name = "ApplicationGroupIncludeApplicationType", indexName = "ApplicationTypeIncludeApplicationGroup", columns = {"ApplicationGroupId"})
    public Hub<ApplicationType> getIncludeApplicationTypes() {
        if (hubIncludeApplicationTypes == null) {
            hubIncludeApplicationTypes = (Hub<ApplicationType>) getHub(P_IncludeApplicationTypes);
        }
        return hubIncludeApplicationTypes;
    }
    
    @OAMany(
        displayName = "Selected Applications", 
        toClass = Application.class, 
        isCalculated = true, 
        isServerSideCalc = true, 
        reverseName = Application.P_SelectedApplicationGroups
    )
    public Hub<Application> getSelectedApplications() {
        if (hubSelectedApplications != null) return hubSelectedApplications;
        hubSelectedApplications = (Hub<Application>) getHub(P_SelectedApplications);
        boolean b = !isServer();
        if (b) return hubSelectedApplications;  // serverSide calculated hub
        
        // calc the list
        final OAFinder<ApplicationGroup, Application> finder = new OAFinder<ApplicationGroup, Application>(ApplicationGroupPP.silo().servers().applications().pp) {
            @Override
            protected void onFound(Application app) {
                if (getExcludeApplications().contains(app)) {
                    hubSelectedApplications.remove(app);
                    return;
                }
                if (getIncludeApplications().contains(app)) {
                    hubSelectedApplications.add(app);
                    return;
                }
                
                ApplicationType at = app.getApplicationType();
                if (at != null) {
                    if (getExcludeApplicationTypes().contains(at)) {
                        hubSelectedApplications.remove(app);
                        return;
                    }
                    if (getIncludeApplicationTypes().contains(at)) {
                        hubSelectedApplications.add(app);
                        return;
                    }
                }
                
                if ((getExcludeApplications().size() > 0) || (getExcludeApplicationTypes().size() > 0)) {
                    if (getIncludeApplications().size() == 0 && getIncludeApplicationTypes().size() == 0) {
                        hubSelectedApplications.add(app);
                        return;
                    }
                }
                hubSelectedApplications.remove(app);
            }
        };
        
        // create a single calc listener for any changes
        Hub<ApplicationGroup> hub = new Hub<ApplicationGroup>();
        hub.add(this);
        hub.addHubListener(
            new HubListenerAdapter() {
                @Override
                public void afterPropertyChange(HubEvent e) {
                    if ("xcalcThis".equalsIgnoreCase(e.getPropertyName())) {
                        OASync.sendMessages();
                        finder.find(ApplicationGroup.this);
                    }
                }
            }, 
            "xcalcThis", 
            new String[] {
                P_IncludeApplications, 
                P_IncludeApplicationTypes, 
                ApplicationGroupPP.silo().servers().applications().pp,                
                ApplicationGroupPP.silo().servers().applications().applicationType().pp,
                P_ExcludeApplications, 
                P_ExcludeApplicationTypes 
            }
        );
        finder.find(ApplicationGroup.this);
        return hubSelectedApplications;
    }
    @OAOne(
        reverseName = Silo.P_ApplicationGroups, 
        required = true, 
        allowCreateNew = false
    )
    @OAFkey(columns = {"SiloId"})
    public Silo getSilo() {
        if (silo == null) {
            silo = (Silo) getObject(P_Silo);
        }
        return silo;
    }
    
    public void setSilo(Silo newValue) {
        fireBeforePropertyChange(P_Silo, this.silo, newValue);
        Silo old = this.silo;
        this.silo = newValue;
        firePropertyChange(P_Silo, old, this.silo);
    }
    
    public void load(ResultSet rs, int id) throws SQLException {
        this.id = id;
        this.code = rs.getString(2);
        this.name = rs.getString(3);
        this.seq = (int) rs.getInt(4);
        if (rs.wasNull()) {
            OAObjectInfoDelegate.setPrimitiveNull(this, ApplicationGroup.P_Seq, true);
        }
        int siloFkey = rs.getInt(5);
        if (!rs.wasNull() && siloFkey > 0) {
            setProperty(P_Silo, new OAObjectKey(siloFkey));
        }
        if (rs.getMetaData().getColumnCount() != 5) {
            throw new SQLException("invalid number of columns for load method");
        }

        changedFlag = false;
        newFlag = false;
    }
}
 

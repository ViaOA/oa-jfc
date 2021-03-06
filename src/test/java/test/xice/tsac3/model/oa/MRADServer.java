// Generated by OABuilder
package test.xice.tsac3.model.oa;
 
import java.sql.*;
import com.viaoa.object.*;
import com.viaoa.hub.*;
import com.viaoa.util.*;

import test.xice.tsac3.model.oa.filter.*;
import test.xice.tsac3.model.oa.propertypath.*;

import com.viaoa.annotation.*;
 
@OAClass(
    shortName = "mrads",
    displayName = "MRADServer",
    displayProperty = "server"
)
@OATable(
    indexes = {
        @OAIndex(name = "MRADServerSilo", columns = { @OAIndexColumn(name = "SiloId") })
    }
)
public class MRADServer extends OAObject {
    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_Id = "Id";
    public static final String P_Id = "Id";
     
     
    public static final String PROPERTY_MRADClients = "MRADClients";
    public static final String P_MRADClients = "MRADClients";
    public static final String PROPERTY_Server = "Server";
    public static final String P_Server = "Server";
    public static final String PROPERTY_Silo = "Silo";
    public static final String P_Silo = "Silo";
     
    protected int id;
     
    // Links to other objects.
    protected transient Hub<MRADClient> hubMRADClients;
    protected transient Server server;
    protected transient Silo silo;
     
    public MRADServer() {
    }
     
    public MRADServer(int id) {
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
    @OAMany(
        toClass = MRADClient.class, 
        owner = true, 
        reverseName = MRADClient.P_MRADServer, 
        cascadeSave = true, 
        cascadeDelete = true
    )
    public Hub<MRADClient> getMRADClients() {
        if (hubMRADClients == null) {
            hubMRADClients = (Hub<MRADClient>) getHub(P_MRADClients);
        }
        return hubMRADClients;
    }
    
    @OAOne(
        reverseName = Server.P_MRADServer, 
        required = true
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
    
    @OAOne(
        reverseName = Silo.P_MRADServer, 
        required = true, 
        allowCreateNew = false, 
        allowAddExisting = false
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
        int serverFkey = rs.getInt(2);
        if (!rs.wasNull() && serverFkey > 0) {
            setProperty(P_Server, new OAObjectKey(serverFkey));
        }
        int siloFkey = rs.getInt(3);
        if (!rs.wasNull() && siloFkey > 0) {
            setProperty(P_Silo, new OAObjectKey(siloFkey));
        }
        if (rs.getMetaData().getColumnCount() != 3) {
            throw new SQLException("invalid number of columns for load method");
        }

        changedFlag = false;
        newFlag = false;
    }
}
 

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

import test.xice.tsam.model.oa.AdminUserCategory;
import com.viaoa.annotation.*;
 
@OAClass(
    shortName = "auc",
    displayName = "Admin User Category",
    displayProperty = "name"
)
@OATable(
    indexes = {
        @OAIndex(name = "AdminUserCategoryParentAdminUserCategory", columns = { @OAIndexColumn(name = "ParentAdminUserCategoryId") })
    }
)
public class AdminUserCategory extends OAObject {
    private static final long serialVersionUID = 1L;
    private static Logger LOG = Logger.getLogger(AdminUserCategory.class.getName());
    public static final String PROPERTY_Id = "Id";
    public static final String P_Id = "Id";
    public static final String PROPERTY_Name = "Name";
    public static final String P_Name = "Name";
     
     
    public static final String PROPERTY_AdminUserCategories = "AdminUserCategories";
    public static final String P_AdminUserCategories = "AdminUserCategories";
    public static final String PROPERTY_ParentAdminUserCategory = "ParentAdminUserCategory";
    public static final String P_ParentAdminUserCategory = "ParentAdminUserCategory";
     
    protected int id;
    protected String name;
     
    // Links to other objects.
    protected transient Hub<AdminUserCategory> hubAdminUserCategories;
    protected transient AdminUserCategory parentAdminUserCategory;
     
    public AdminUserCategory() {
    }
     
    public AdminUserCategory(int id) {
        this();
        setId(id);
    }
     
    @OAProperty(isUnique = true, displayLength = 3)
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
    
    @OAProperty(maxLength = 25, displayLength = 15, columnLength = 12)
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
    
    @OAMany(
        displayName = "Admin User Categories", 
        toClass = AdminUserCategory.class, 
        recursive = true, 
        reverseName = AdminUserCategory.P_ParentAdminUserCategory
    )
    public Hub<AdminUserCategory> getAdminUserCategories() {
        if (hubAdminUserCategories == null) {
            hubAdminUserCategories = (Hub<AdminUserCategory>) getHub(P_AdminUserCategories);
        }
        return hubAdminUserCategories;
    }
    
    @OAOne(
        displayName = "Parent Admin User Category", 
        reverseName = AdminUserCategory.P_AdminUserCategories
    )
    @OAFkey(columns = {"ParentAdminUserCategoryId"})
    public AdminUserCategory getParentAdminUserCategory() {
        if (parentAdminUserCategory == null) {
            parentAdminUserCategory = (AdminUserCategory) getObject(P_ParentAdminUserCategory);
        }
        return parentAdminUserCategory;
    }
    
    public void setParentAdminUserCategory(AdminUserCategory newValue) {
        fireBeforePropertyChange(P_ParentAdminUserCategory, this.parentAdminUserCategory, newValue);
        AdminUserCategory old = this.parentAdminUserCategory;
        this.parentAdminUserCategory = newValue;
        firePropertyChange(P_ParentAdminUserCategory, old, this.parentAdminUserCategory);
    }
    
    public void load(ResultSet rs, int id) throws SQLException {
        this.id = id;
        this.name = rs.getString(2);
        int parentAdminUserCategoryFkey = rs.getInt(3);
        if (!rs.wasNull() && parentAdminUserCategoryFkey > 0) {
            setProperty(P_ParentAdminUserCategory, new OAObjectKey(parentAdminUserCategoryFkey));
        }
        if (rs.getMetaData().getColumnCount() != 3) {
            throw new SQLException("invalid number of columns for load method");
        }

        changedFlag = false;
        newFlag = false;
    }
}
 

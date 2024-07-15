// OABuilder generated source code
package com.viaoa.jfc.propertypath.model.oa;

import com.viaoa.annotation.OAClass;
import com.viaoa.annotation.OAColumn;
import com.viaoa.annotation.OAOne;
import com.viaoa.annotation.OAProperty;
import com.viaoa.object.OAObject;

@OAClass(shortName = "pd", displayName = "Property Def", useDataSource = false, localOnly = true, addToCache = false)
public class PropertyDef extends OAObject {
	private static final long serialVersionUID = 1L;
	public static final String P_Name = "Name";
    public static final String P_LowerName = "LowerName";
	public static final String P_DisplayName = "DisplayName";

	public static final String P_ObjectDef = "ObjectDef";

	protected String name;
    protected String lowerName;
	protected String displayName;

	// Links to other objects.
	protected transient ObjectDef objectDef;

	public PropertyDef() {
	}

	@OAProperty(displayLength = 18)
	@OAColumn()
	public String getName() {
		return name;
	}

	public void setName(String newValue) {
		String old = name;
		this.name = newValue;
		firePropertyChange(P_Name, old, this.name);
	}

    @OAProperty(displayLength = 18)
    @OAColumn()
    public String getLowerName() {
        return lowerName;
    }

    public void setLowerName(String newValue) {
        String old = lowerName;
        this.lowerName = newValue;
        firePropertyChange(P_LowerName, old, this.lowerName);
    }
	
	
	@OAProperty(displayName = "Display Name", maxLength = 11, displayLength = 11)
	@OAColumn(maxLength = 11)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String newValue) {
		String old = displayName;
		this.displayName = newValue;
		firePropertyChange(P_DisplayName, old, this.displayName);
	}

	@OAOne(displayName = "Object Def", reverseName = ObjectDef.P_PropertyDefs)
	public ObjectDef getObjectDef() {
		if (objectDef == null) {
			objectDef = (ObjectDef) getObject(P_ObjectDef);
		}
		return objectDef;
	}

	public void setObjectDef(ObjectDef newValue) {
		ObjectDef old = this.objectDef;
		this.objectDef = newValue;
		firePropertyChange(P_ObjectDef, old, this.objectDef);
	}
}

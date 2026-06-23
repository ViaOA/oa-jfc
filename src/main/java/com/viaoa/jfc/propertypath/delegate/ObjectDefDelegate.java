/*  Copyright 1999 Vince Via vvia@viaoa.com
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.viaoa.jfc.propertypath.delegate;

import com.viaoa.graph.api.internal.OAGraphInternal;
import com.viaoa.hub.Hub;
import com.viaoa.jfc.propertypath.model.oa.*;
import com.viaoa.lang.OAString;
import com.viaoa.metadata.OACalcInfo;
import com.viaoa.metadata.OALinkInfo;
import com.viaoa.metadata.OAObjectInfo;
import com.viaoa.metadata.OAPropertyInfo;
import com.viaoa.runtime.OARuntime;


/**
 * 20130223 populate OAObjects from OAObjectInfo
 * @author vvia
 */
public class ObjectDefDelegate {

    public static ObjectDef getObjectDef(Hub<ObjectDef> hubObject, Class rootClass) {
        if (hubObject == null) return null;
        if (rootClass == null) return  null;
        
        ObjectDef od = null;
        for (ObjectDef odx : hubObject) {
            if (odx.getObjectClass() == rootClass) {
                od = odx;
                break;
            }
        }
        if (od != null) {
            return od;
        }
        
    	OAGraphInternal og = (OAGraphInternal) OARuntime.graph(rootClass);
        OAObjectInfo oi = og.internal().objects().info().getObjectInfo(rootClass);
        od = new ObjectDef();
        od.setObjectClass(rootClass);
        String s = rootClass.getName();
        s = OAString.getClassName(rootClass);
        od.setName(s);
        od.setDisplayName(oi.getDisplayName());
        hubObject.add(od);
        
        for (OAPropertyInfo pi : oi.getPropertyInfos()) {
            PropertyDef pd = new PropertyDef();
            pd.setName(pi.getName());
            pd.setLowerName(pi.getLowerName());
            pd.setDisplayName(pi.getName());
            od.getPropertyDefs().add(pd);
        }
        for (OACalcInfo ci : oi.getCalcInfos()) {
            CalcPropertyDef cd = new CalcPropertyDef();
            cd.setName(ci.getName());
            cd.setLowerName(ci.getLowerName());
            cd.setDisplayName(ci.getName());
            cd.setIsForHub(ci.getIsForHub());
            od.getCalcPropertyDefs().add(cd);
        }
        for (OALinkInfo li : oi.getLinkInfos()) {
            if (li.getPrivateMethod()) continue;
            if (!li.getUsed()) continue;
            LinkPropertyDef lp = new LinkPropertyDef();
            lp.setName(li.getName());
            lp.setLowerName(li.getLowerName());
            lp.setDisplayName(li.getName());
            lp.setType(li.getType()==OALinkInfo.ONE ? LinkPropertyDef.TYPE_One : LinkPropertyDef.TYPE_Many);
            od.getLinkPropertyDefs().add(lp);

            ObjectDef tod = getObjectDef(hubObject, li.getToClass());
            lp.setToObjectDef(tod);
        }
        
        return od;
    }

}



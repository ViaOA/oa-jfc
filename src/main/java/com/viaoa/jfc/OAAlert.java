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
package com.viaoa.jfc;

import javax.swing.SwingUtilities;

import com.viaoa.hub.Hub;
import com.viaoa.hub.HubEvent;
import com.viaoa.hub.HubListenerAdapter;
import com.viaoa.object.OAObject;
import com.viaoa.template.OATemplate;
import com.viaoa.util.OAConv;

public class OAAlert {

    protected Hub hub; 
    protected String propertyName;
    protected String title;
    protected String msg;
    protected OATemplate template;

    /**
     * 
     * @param hub
     * @param propertyName this is checked for hub.AO.  If true, then an popup message will show. 
     * @param title 
     * @param msg popup message, can be an OATemplate string
     */
    public OAAlert(Hub hub, String propertyName, String title, String msg) {
        this.hub = hub;
        this.propertyName = propertyName;
        this.msg = msg;
        this.title = title;
        
        if (hub == null) return;
        
        if (msg != null && msg.indexOf("<%=") >= 0) {
            template = new OATemplate(msg);
        }
        
        hub.addHubListener(new HubListenerAdapter() {
            @Override
            public void afterChangeActiveObject(HubEvent e) {
                super.afterChangeActiveObject(e);
            }
        });
        
    }
    
    protected void afterChangeActiveObject() {
        final Object obj = hub.getAO();
        if (!(obj instanceof OAObject)) return;
        final Object objx = ((OAObject) obj).getProperty(propertyName);
        if (!OAConv.toBoolean(objx)) return;

        final String msgx = (template != null) ? template.process((OAObject) obj) : msg;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                OAJfcUtil.showMessage(title, msgx);
            }
        });
    }
    
    
}

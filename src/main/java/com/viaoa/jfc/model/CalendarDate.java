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
package com.viaoa.jfc.model;

import com.viaoa.object.*;
import com.viaoa.annotation.OAClass;
import com.viaoa.util.*;

@OAClass(localOnly=true, addToCache=false, initialize=false)
public class CalendarDate extends OAObject {
	private static final long serialVersionUID = 1L;

	public static final String PROPERTY_Date = "Date";

	protected OADate date;
	
	public OADate getDate() {
		return date;
	}
	
	public void setDate(OADate newValue) {
		OADate old = this.date;
        fireBeforePropertyChange(PROPERTY_Date, old, newValue);
		this.date = newValue;
		firePropertyChange(PROPERTY_Date, old, this.date);
	}	
	

}


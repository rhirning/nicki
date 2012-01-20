/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class DynamicObjectRoot implements DataProvider, Serializable {
	private String baseDn;
	private EntryFilter entryFilter;

	public DynamicObjectRoot(String baseDn, EntryFilter entryFilter) {
		super();
		this.baseDn = baseDn;
		this.entryFilter = entryFilter;
	}

	public List<DynamicObject> getChildren(NickiContext context) {
		return context.loadChildObjects(baseDn, "objectClass=*");
	}

	public DynamicObject getRoot(NickiContext context) {
		return context.loadObject(baseDn);
	}

	public String getMessage() {
		return "";
	}

	public EntryFilter getEntryFilter() {
		return this.entryFilter;
	}

}

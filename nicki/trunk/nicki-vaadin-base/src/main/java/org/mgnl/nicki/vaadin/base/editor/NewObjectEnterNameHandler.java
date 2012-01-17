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

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.components.EnterNameHandler;

@SuppressWarnings("serial")
public class NewObjectEnterNameHandler extends EnterNameHandler implements Serializable {
	private NickiTreeEditor editor;
	private DynamicObject parent;
	private Class<? extends DynamicObject> classDefinition;
	

	public NewObjectEnterNameHandler(NickiTreeEditor treeEditor, DynamicObject parent,
			Class<? extends DynamicObject> classDefinition) {
		super();
		this.editor = treeEditor;
		this.parent = parent;
		this.classDefinition = classDefinition;
	}

	public void setName(String name) throws Exception {
		if (editor.create(parent, classDefinition, name)) {
			editor.refresh(parent);
		}
	}

}

/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.vaadin.base.components;

import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.vaadin.base.editor.NewObjectEnterNameHandler;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;


@SuppressWarnings("serial")
public class SimpleNewClassEditor extends EnterNameDialog implements NewClassEditor {
	private NickiTreeEditor treeEditor;

	public SimpleNewClassEditor(NickiTreeEditor nickiEditor, String messageBase, String title) {
		super(messageBase, title);
		this.treeEditor = nickiEditor;
	}

	public void init(TreeData parent, Class<? extends TreeData> classDefinition) {
		setHandler( new NewObjectEnterNameHandler(treeEditor, parent, classDefinition));
	}
}

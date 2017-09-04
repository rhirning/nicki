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
package org.mgnl.nicki.editor.log4j;


import java.io.Serializable;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.DataProvider;
import org.mgnl.nicki.core.data.FileEntry;
import org.mgnl.nicki.core.data.FileSystemRoot;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.ShowAllFilter;
import org.mgnl.nicki.vaadin.base.editor.TreeEditor;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;

@SuppressWarnings("serial")
public class FileSystemViewer extends CustomComponent implements Serializable {
	private NickiApplication nickiApplication;
	
	public FileSystemViewer(NickiApplication nickiApplication) {
		this.nickiApplication = nickiApplication;
		setCompositionRoot(getEditor());
		setSizeFull();
	}
	
	@SuppressWarnings("unchecked")
	private Component getEditor() {
		String root = "/";

		DataProvider treeDataProvider = new FileSystemRoot(root, new ShowAllFilter());
		TreeEditor editor = new TreeEditor(getNickiApplication(), getNickiContext(), treeDataProvider, getI18nBase());
		editor.configureClass(FileEntry.class, Icon.FOLDER, TreeEditor.CREATE.ALLOW, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW, FileEntry.class);
		DirectoryEditor directoryEditor = new DirectoryEditor(); 
		editor.setClassEditor(FileEntry.class, directoryEditor);
//		editor.addAction(new ImportTreeAction(editor, Org.class, I18n.getText(getI18nBase() + ".action.import"), getI18nBase()));
//		editor.addAction(new ExportTreeAction(getNickiContext(), Org.class, I18n.getText(getI18nBase() + ".action.export"), getI18nBase()));
//		editor.addAction(new ExportTreeAction(getNickiContext(), Script.class, I18n.getText(getI18nBase() + ".action.export"), getI18nBase()));
		editor.initActions();
		editor.setHeight("100%");
		return editor;
	}

	public String getI18nBase() {
		return "nicki.editor.filesystem";
	}

	public NickiContext getNickiContext() {
		return getNickiApplication().getNickiContext();
	}

	public NickiApplication getNickiApplication() {
		return nickiApplication;
	}
}

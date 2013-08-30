/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.editor.scripts;


import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.context.TargetFactory;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Org;
import org.mgnl.nicki.dynamic.objects.objects.Script;
import org.mgnl.nicki.vaadin.base.application.AccessGroup;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectRoot;
import org.mgnl.nicki.vaadin.base.editor.ExportTreeAction;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.ImportTreeAction;
import org.mgnl.nicki.vaadin.base.editor.ShowAllFilter;
import org.mgnl.nicki.vaadin.base.editor.DataProvider;
import org.mgnl.nicki.vaadin.base.editor.TreeEditor;

import com.vaadin.ui.Component;

@AccessGroup(name = {"nickiAdmins", "IDM-Development"})
@SuppressWarnings("serial")
public class ScriptEditor extends NickiApplication {
	
	public ScriptEditor() {
		super();
		setUseWelcomeDialog(DataHelper.booleanOf(Config.getProperty("nicki.projects.useWelcomeDialog", "false")));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getEditor() {
		ScriptViewer scriptViewer = new ScriptViewer();
		//scriptViewer.setRequest(getRequest());
		//scriptViewer.setResponse(getResponse());

		DataProvider treeDataProvider = new DynamicObjectRoot(Config.getProperty("nicki.scripts.basedn"), new ShowAllFilter());
		TreeEditor editor = new TreeEditor(this, getNickiContext(), treeDataProvider, getI18nBase());
		editor.configureClass(Org.class, Icon.FOLDER, TreeEditor.CREATE.ALLOW, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW, Org.class, Script.class );
		editor.configureClass(Script.class, Icon.DOCUMENT, TreeEditor.CREATE.ALLOW, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW);
		editor.setClassEditor(Script.class, scriptViewer);
		editor.addAction(new ImportTreeAction(editor, Org.class, I18n.getText(getI18nBase() + ".action.import"), getI18nBase()));
		editor.addAction(new ExportTreeAction(getNickiContext(), Org.class, I18n.getText(getI18nBase() + ".action.export"), getI18nBase()));
		editor.addAction(new ExportTreeAction(getNickiContext(), Script.class, I18n.getText(getI18nBase() + ".action.export"), getI18nBase()));
		editor.initActions();
		return editor;
	}

	@Override
	public Target getTarget() {
		return TargetFactory.getDefaultTarget();
	}

	@Override
	public String getI18nBase() {
		return "nicki.editor.script";
	}
	
}

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
package org.mgnl.nicki.editor.templates;



import java.io.Serializable;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Org;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.DataProvider;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectRoot;
import org.mgnl.nicki.vaadin.base.editor.ExportTreeAction;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.ImportTreeAction;
import org.mgnl.nicki.vaadin.base.editor.ShowAllFilter;
import org.mgnl.nicki.vaadin.base.editor.TreeEditor;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;

public class TemplateEditorComponent extends CustomComponent implements Serializable {

	private static final long serialVersionUID = -8245147689512577915L;
	private NickiApplication nickiApplication;

	public TemplateEditorComponent(NickiApplication nickiApplication) {
		this.nickiApplication = nickiApplication;
		setCompositionRoot(getEditor());
		setSizeFull();
	}

	@SuppressWarnings("unchecked")
	public Component getEditor() {
		TemplateViewer templateViewer = new TemplateViewer();

		DataProvider dataProvider = new DynamicObjectRoot(getTemplatesRoot(), new ShowAllFilter());
		TreeEditor editor = new TreeEditor(getNickiApplication(), getNickiApplication().getNickiContext(), dataProvider, getI18nBase());
		editor.configureClass(Org.class, Icon.FOLDER, TreeEditor.CREATE.ALLOW, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW, Org.class, Template.class );
		editor.configureClass(Template.class, Icon.DOCUMENT, TreeEditor.CREATE.ALLOW, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW);
		editor.setClassEditor(Template.class, templateViewer);
		editor.addAction(new PreviewTemplate(getNickiContext(), Template.class, I18n.getText(getI18nBase() + ".action.preview"), getI18nBase()));
		editor.addAction(new ImportTreeAction(editor, Org.class, I18n.getText(getI18nBase() + ".action.import"), getI18nBase()));
		editor.addAction(new ExportTreeAction(getNickiContext(), Org.class, I18n.getText(getI18nBase() + ".action.export"), getI18nBase()));
		editor.addAction(new ExportTreeAction(getNickiContext(), Template.class, I18n.getText(getI18nBase() + ".action.export"), getI18nBase()));
		editor.initActions();
		editor.setHeight("100%");

		return editor;
	}

	public String getI18nBase() {
		return "nicki.editor.templates";
	}

	public String getTemplatesRoot() {
		return Config.getProperty("nicki.templates.basedn");
	}

	public NickiContext getNickiContext() {
		return getNickiApplication().getNickiContext();
	}

	public NickiApplication getNickiApplication() {
		return nickiApplication;
	}


}

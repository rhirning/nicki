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
package org.mgnl.nicki.editor.mailtemplates;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.objects.Org;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.context.Target;
import org.mgnl.nicki.ldap.context.TargetFactory;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectRoot;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.DataProvider;
import org.mgnl.nicki.vaadin.base.editor.TreeEditor;

import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class MailTemplateEditor extends NickiApplication {
	
	public MailTemplateEditor() {
		super();
		setUseWelcomeDialog(DataHelper.booleanOf(Config.getProperty("nicki.projects.useWelcomeDialog", "false")));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getEditor() {
		DataProvider treeDataProvider = new DynamicObjectRoot(Config.getProperty("nicki.mailtemplates.basedn"), new MailTemplateFilter());
		TreeEditor editor = new TreeEditor(this, getNickiContext(), treeDataProvider, getI18nBase());
		editor.configureClass(Org.class, Icon.FOLDER, TreeEditor.CREATE.ALLOW, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW, Org.class, Template.class );
		editor.configureClass(Template.class, Icon.DOCUMENT, TreeEditor.CREATE.ALLOW, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW);
		editor.setClassEditor(Template.class, new I18nMailTemplateViewer(getI18nBase()));
		editor.addAction(new PreviewMail(getNickiContext(), Template.class, "Test de", "de"));
		editor.addAction(new PreviewMail(getNickiContext(), Template.class, "Test fr", "fr"));
		editor.addAction(new PreviewMail(getNickiContext(), Template.class, "Test it", "it"));
		editor.initActions();
		return editor;
	}
	@Override
	public Target getTarget() {
		return TargetFactory.getDefaultTarget();
	}
	
	@Override
	public String getI18nBase() {
		return "nicki.editor.mailtemplates";
	}

	
}

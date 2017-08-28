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
package org.mgnl.nicki.report;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.data.DataProvider;
import org.mgnl.nicki.core.data.EntryFilter;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.objects.Org;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.editor.templates.TemplateConfig;
import org.mgnl.nicki.editor.templates.TemplateEditor;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectRoot;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.ShowAllFilter;
import org.mgnl.nicki.vaadin.base.editor.TreeEditor;

import com.vaadin.ui.Component;

public class ReportApplication extends TemplateEditor {

	private static final long serialVersionUID = -8245147689512577915L;
    

	@SuppressWarnings("unchecked")
	@Override
	public Component getEditor() {

		DataProvider dataProvider = new DynamicObjectRoot(getTemplatesRoot(), getEntryFilter());
		TreeEditor editor = new TreeEditor(this, getNickiContext(), dataProvider, getI18nBase());
		editor.configureClass(Org.class, Icon.FOLDER, TreeEditor.CREATE.DENY, TreeEditor.DELETE.DENY, TreeEditor.RENAME.DENY, Org.class, Template.class );
		editor.configureClass(Template.class, Icon.DOCUMENT, TreeEditor.CREATE.DENY, TreeEditor.DELETE.DENY, TreeEditor.RENAME.DENY);
		TemplateConfig templateConfig = new TemplateConfig();
		boolean usePreview = DataHelper.booleanOf(Config.getProperty("nicki.report.usePreview", "false"));
		templateConfig.setUsePreview(usePreview);
		editor.setClassEditor(Template.class, templateConfig);
		editor.initActions();

		return editor;
	}

	public EntryFilter getEntryFilter() {
		return new ShowAllFilter();
	}
	


	@Override
	public String getI18nBase() {
		return "nicki.application.reports";
	}

}

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

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.mailtemplate.engine.MailTemplateEngine;
import org.mgnl.nicki.vaadin.base.editor.ClassEditor;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class I18nMailTemplateViewer extends CustomComponent implements ClassEditor {

	private AbsoluteLayout mainLayout;
	
	private TabSheet tab;
	private Template template;
	private String messageKeyBase;
	
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public I18nMailTemplateViewer(String messageKeyBase) {
		this.messageKeyBase = messageKeyBase;
	}
	
	public void setDynamicObject(NickiTreeEditor nickiEditor, DynamicObject dynamicObject) {
		this.template = (Template) dynamicObject;
		buildEditor();
		setCompositionRoot(mainLayout);
		
		createSheets();
		
	}


	private void createSheets() {
		MailTemplateViewer editor = new MailTemplateViewer(template, this.messageKeyBase);
		int position = 0;
		tab.addTab(editor, I18n.getText(this.messageKeyBase + ".tab.default"), null);
		for (int i = 0; i < MailTemplateEngine.LOCALE_NAMES.length; i++) {
			position++;
			String localeName = MailTemplateEngine.LOCALE_NAMES[i];
			String dn = template.getModel().getNamingLdapAttribute() + "=" + template.getNamingValue() + "_" + localeName
					+ "," + template.getParentPath();
			editor = new MailTemplateViewer(template.getContext(), dn, localeName, this.messageKeyBase);
			tab.addTab(editor, localeName, null);
		}
	}
	
	private AbsoluteLayout buildEditor() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setHeight("100%");
		mainLayout.addComponent(verticalLayout, "top:20.0px;left:20.0px;");

		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		tab = new TabSheet();
		tab.setWidth("640px");
		tab.setHeight("100.0%");
		tab.setImmediate(false);
		verticalLayout.addComponent(tab);

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setHeight(40, UNITS_PIXELS);
		verticalLayout.addComponent(horizontalLayout);
		
		return mainLayout;
	}

}

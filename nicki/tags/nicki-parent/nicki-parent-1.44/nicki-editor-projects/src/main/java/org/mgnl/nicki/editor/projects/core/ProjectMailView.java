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
package org.mgnl.nicki.editor.projects.core;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Project;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.editor.ClassEditor;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;
import org.mgnl.nicki.vaadin.base.fields.AttributeCheckbox;
import org.mgnl.nicki.vaadin.base.fields.AttributeSelectField;
import org.mgnl.nicki.vaadin.base.fields.AttributeTextField;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class ProjectMailView extends CustomComponent implements ClassEditor {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private CheckBox projectEmailVisible;
	@AutoGenerated
	private Select projectEmailDomain;
	@AutoGenerated
	private TextField projectEmail;
	
	private AttributeTextField projectEmailField;
	private AttributeSelectField projectEmailDomainField;
	private AttributeCheckbox projectEmailVisibleField;
	private Button saveButton;
	
	private Project project;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 * @param project 
	 */
	public ProjectMailView() {
	}

	public void setDynamicObject(NickiTreeEditor nickiEditor,
			DynamicObject dynamicObject) {
		this.project = (Project) dynamicObject;
		this.projectEmailField = new AttributeTextField();
		this.projectEmailField.init("email", project, null);
		this.projectEmailDomainField = new AttributeSelectField();
		this.projectEmailDomainField.init("emailDomain", project, null);
		this.projectEmailDomainField.setOptions(getEmailDomainOptions());
		
		this.projectEmailVisibleField = new AttributeCheckbox();
		this.projectEmailVisibleField.init("emailVisible", project, null);
		
		buildMainLayout();
		setCompositionRoot(mainLayout);
		setWidth("100%");
		setHeight(400, UNITS_PIXELS);

		// TODO add user code here
	}
	
	private Container getEmailDomainOptions() {
		String domainsString = Config.getProperty("nicki.projects.emaildomains", "");
		String domains[] = StringUtils.split(domainsString, ",");
		Container container = new IndexedContainer();
		for (int i = 0; i < domains.length; i++) {
			container.addItem(domains[i]);
		}
		return container;
	}



	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// projectName
		boolean readonly =true;
		if (StringUtils.isEmpty(this.project.getAttribute("email"))) {
			readonly = false;
		}
		projectEmail = (TextField) this.projectEmailField.getComponent(readonly);
		projectEmail.setWidth("400px");
		projectEmail.setCaption("Mailverteiler");
		mainLayout.addComponent(projectEmail, "top:20.0px;left:20.0px;");
		
		// projectEmailDomain
		projectEmailDomain = (Select) this.projectEmailDomainField.getComponent(readonly);
		projectEmailDomain.setWidth("400px");
		projectEmailDomain.setCaption("Dom�ne");
		mainLayout.addComponent(projectEmailDomain, "top:60.0px;left:20.0px;");
		
		// projectDescription
		projectEmailVisible = (CheckBox) this.projectEmailVisibleField.getComponent(false);
		projectEmailVisible.setCaption("Ist der Verteiler sichtbar");
		projectEmailVisible.setImmediate(true);
		mainLayout.addComponent(projectEmailVisible, "top:100.0px;left:20.0px;");
		
		saveButton = new Button(I18n.getText("nicki.editor.generic.button.save"));
		saveButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				try {
					project.update();
					getWindow().showNotification(I18n.getText("nicki.editor.save.info"));
				} catch (DynamicObjectException e) {
					getWindow().showNotification(I18n.getText("nicki.editor.save.error"), 
							e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});
		mainLayout.addComponent(saveButton, "top:140.0px;left:20.0px;");

		
		return mainLayout;
	}

}
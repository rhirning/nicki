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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.template.engine.TemplateHelper;
import org.mgnl.nicki.template.engine.TemplateParameter;
import org.mgnl.nicki.vaadin.base.data.DateHelper;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;

public class ConfiguredTemplateConfigDialog extends CustomComponent implements TemplateConfigDialog {

	private static final long serialVersionUID = -1295818262583276359L;

	private FormLayout mainLayout;
	
	private Template template;
	private Map<String, Object> params;
	private TemplateConfig templateConfig;

	public ConfiguredTemplateConfigDialog(Template template, Map<String, Object> params, TemplateConfig templateConfig) {
		this.template = template;
		this.params = params;
		this.templateConfig = templateConfig;
		
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private FormLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new FormLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		List<TemplateParameter> templateParameters = TemplateHelper.getTemplateHandler(template).getTemplateParameters();
		if (templateParameters != null) {
			for (TemplateParameter templateParameter : templateParameters) {
				if (!StringUtils.startsWith(templateParameter.getName(), ".")) {
					if (StringUtils.equalsIgnoreCase("date", templateParameter.getDataType())) {
						PopupDateField field = new PopupDateField();
						field.setCaption(templateParameter.getDisplayName());
						field.setImmediate(true);
						field.setWidth("-1px");
						field.setHeight("-1px");
						field.setInvalidAllowed(false);
						mainLayout.addComponent(field);
						DateHelper.init(field);
						field.addValueChangeListener(new ParamInputListener(templateParameter.getName(), params, templateConfig));
					} else if (StringUtils.equalsIgnoreCase("string", templateParameter.getDataType())) {
						TextField field = new TextField();
						field.setCaption(templateParameter.getDisplayName());
						field.setImmediate(true);
						field.setWidth("-1px");
						field.setHeight("-1px");
						field.setInvalidAllowed(false);
						mainLayout.addComponent(field);
						field.addValueChangeListener(new ParamInputListener(templateParameter.getName(), params, templateConfig));
					} else if (StringUtils.equalsIgnoreCase("static", templateParameter.getDataType())) {
						params.put(templateParameter.getName(), templateParameter.getValue());
					}
				}
			}
		}
		
		return mainLayout;
	}

}

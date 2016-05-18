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

import java.util.Map;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class ParamInputListener implements ValueChangeListener {

	private String name;
	private Map<String, Object> map;
	private TemplateConfig templateConfig;

	public ParamInputListener(PopupDateField field, String name, Map<String, Object> map, TemplateConfig templateConfig) {
		init(name, map, templateConfig);
	}

	public ParamInputListener(TextField field, String name, Map<String, Object> map, TemplateConfig templateConfig) {
		init(name, map, templateConfig);
	}

	public ParamInputListener(Table rights, String name, Map<String, Object> map, TemplateConfig templateConfig) {
		init(name, map, templateConfig);
	}

	public void init(String name, Map<String, Object> map, TemplateConfig templateConfig) {
		this.name = name;
		this.map = map;
		this.templateConfig = templateConfig;
	}

	public void valueChange(ValueChangeEvent event) {
		map.put(name, event.getProperty().getValue());
		this.templateConfig.paramsChanged();
	}

}
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
package org.mgnl.nicki.vaadin.base.search;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.util.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class DynamicObjectSearchFieldFactory<T extends DynamicObject> implements Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(DynamicObjectSearchFieldFactory.class);
	private NickiContext context;
	private Map<DynamicAttribute, String> map;
	
	public DynamicObjectSearchFieldFactory(NickiContext context, Map<DynamicAttribute, String> map) {
		this.context = context;
		this.map = map;
	}

	@SuppressWarnings("unchecked")
	public Component createField(DynamicAttribute dynAttribute) {
		DynamicAttributeSearchField<T> field = null;
		if (StringUtils.isNotEmpty(dynAttribute.getEditorClass())
				&& StringUtils.isNotBlank(dynAttribute.getSearchFieldClass())) {
			try {
				field = (DynamicAttributeSearchField<T>) Classes.newInstance(dynAttribute.getSearchFieldClass());
			} catch (Exception e) {
				field = null;
				LOG.error("Error", e);
			}
		}
		if (field == null) {
			field = new TextFieldSearchField<T>();
		}
		field.setWidth("100%");
		field.init(dynAttribute, map);
		return field.getComponent();
	}
	
	
	public void addFields(AbstractLayout layout, Class<T> clazz) throws InstantiateDynamicObjectException {
		DataModel model = context.getDataModel(clazz);
		for (DynamicAttribute dynAttribute : model.getAttributes().values()) {
			if (dynAttribute.isSearchable()) {
				layout.addComponent(createField(dynAttribute));
			}
		}
	}
	
}
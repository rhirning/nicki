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
package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.types.TextArea;
import org.mgnl.nicki.vaadin.base.fields.AttributeSelectObjectField;
import org.mgnl.nicki.vaadin.base.fields.AttributeTextAreaField;
import org.mgnl.nicki.vaadin.base.fields.AttributeTextField;
import org.mgnl.nicki.vaadin.base.fields.DynamicAttributeField;
import org.mgnl.nicki.vaadin.base.fields.TableListAttributeField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class DynamicObjectFieldFactory implements Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(DynamicObjectFieldFactory.class);
	private DynamicObjectValueChangeListener<String> objectListener;
	
	public DynamicObjectFieldFactory(DynamicObjectValueChangeListener<String> objectListener) {
		this.objectListener = objectListener;
	}

	@SuppressWarnings("unchecked")
	public Component createField(Component parent, DynamicObject dynamicObject, String attributeName, boolean create) {
		DynamicAttribute dynAttribute = dynamicObject.getDynamicAttribute(attributeName);
		DynamicAttributeField<String> field = null;
		if (StringUtils.isNotEmpty(dynAttribute.getEditorClass())) {
			try {
				field = (DynamicAttributeField<String>) Classes.newInstance(dynAttribute.getEditorClass());
				field.init(attributeName, dynamicObject, objectListener);
			} catch (Exception e) {
				field = null;
				LOG.error("Error", e);
			}
		}
		if (field == null) {
			if (dynAttribute.isMultiple()) {
				field = new TableListAttributeField();
			} else if (dynAttribute.isForeignKey()) {
				field = new AttributeSelectObjectField();
			} else if (dynAttribute.getAttributeClass() == TextArea.class) {
				field = new AttributeTextAreaField();
			} else {
				field = new AttributeTextField();
			}
			field.init(attributeName, dynamicObject, objectListener);
		}
		boolean readOnly = dynAttribute.isReadonly();
		if (!create && dynAttribute.isNaming()) {
			readOnly = true;
		}
		return field.getComponent(readOnly);
	}
	
	
	public void addFields(AbstractOrderedLayout layout, DynamicObject dynamicObject, boolean create) {
		DataModel model = dynamicObject.getModel();
		for (DynamicAttribute dynAttribute : model.getAttributes().values()) {
			if (!dynAttribute.isNaming()
					&& (objectListener == null || objectListener.acceptAttribute(dynAttribute.getName()))) {
				layout.addComponent(createField(layout, dynamicObject, dynAttribute.getName(), create));
			}
		}
	}
	
}
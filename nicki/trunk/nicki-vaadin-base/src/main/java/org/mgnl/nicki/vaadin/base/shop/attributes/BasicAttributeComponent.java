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
package org.mgnl.nicki.vaadin.base.shop.attributes;


import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.AttributeContent;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.attributes.AttributeComponent;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.Field;

@SuppressWarnings("serial")
public abstract class BasicAttributeComponent implements AttributeComponent, Serializable {
	private Field field;
	private boolean enabled;
	private InventoryArticle article;
	private CatalogArticleAttribute attribute;

	protected <T extends Object> T getContent(Class<T> classDefinition, Person user, Person person) {
		if (StringUtils.isNotEmpty(attribute.getContentClass())) {
			try {
				AttributeContent contentProvider = (AttributeContent) Class.forName(attribute.getContentClass()).newInstance();
				return contentProvider.getContent(classDefinition, user, person);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	
	@Override
	public void setValue(Object value) {
		field.setValue(value);
	}

	@Override
	public Object getvalue() {
		return field.getValue();
	}


	public void setField(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}
	
	@Override
	public void setCaption(String caption) {
		field.setCaption(caption);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		this.field.setEnabled(enabled);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setArticle(InventoryArticle article) {
		this.article = article;
	}

	public InventoryArticle getArticle() {
		return article;
	}

	public void setAttribute(CatalogArticleAttribute attribute) {
		this.attribute = attribute;
	}

	public CatalogArticleAttribute getAttribute() {
		return attribute;
	}
	
	@Override
	public String getStringValue(Object value) {
		return (String) value;
	}



}

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
package org.mgnl.nicki.shop.attributes;


import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.base.objects.CatalogArticleAttribute;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;
import org.mgnl.nicki.vaadin.base.fields.NickiField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public abstract class BasicAttributeComponent<F> implements AttributeComponent<F>, Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(BasicAttributeComponent.class);
	private NickiField<F> field;
	private boolean enabled;
	private InventoryArticle article;
	private CatalogArticleAttribute attribute;

	protected String getContent(Person user, Person person) {
		if (StringUtils.isNotEmpty(attribute.getContentClass())) {
			try {
				AttributeContent contentProvider = (AttributeContent) Classes.newInstance(attribute.getContentClass());
				return contentProvider.getContent(user, person);
			} catch (Exception e) {
				LOG.error("Error", e);
			}
		}
		return null;
	}

	protected VaadinComponent getVaadinContent(Person user, Person person) {
		if (StringUtils.isNotEmpty(attribute.getContentClass())) {
			try {
				AttributeVaadinContent contentProvider = (AttributeVaadinContent) Classes.newInstance(attribute.getContentClass());
				return contentProvider.getVaadinContent(user, person);
			} catch (Exception e) {
				LOG.error("Error", e);
			}
		}
		return null;
	}

	protected List<String> getListContent(Person user, Person person) {
		if (StringUtils.isNotEmpty(attribute.getContentClass())) {
			try {
				AttributeListContent contentProvider = (AttributeListContent) Classes.newInstance(attribute.getContentClass());
				return contentProvider.getContent(user, person);
			} catch (Exception e) {
				LOG.error("Error", e);
			}
		}
		return null;
	}

	@Override
	public Component getInstance(String caption, F date, ValueChangeListener listener) {
		setCaption(caption);
		try {
			getField().setValue(date);
		} catch (Exception e) {
		}
		if (isEnabled()) {
			getField().addValueChangeListener(listener);
		}
		return getField().getComponent();
	}


	
	public void setValue(F value) {
		field.setValue(value);
	}

	public F getValue() {
		return field.getValue();
	}


	public void setField(NickiField<F> field) {
		this.field = field;
	}

	public NickiField<F> getField() {
		return field;
	}
	
	public void setCaption(String caption) {
		field.setCaption(caption);
	}
	
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
	
	public String getStringValue(Object value) {
		return (String) value;
	}



}

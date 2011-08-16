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

package org.mgnl.nicki.vaadin.base.shop.attributes;


import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.attributes.AttributeComponent;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

public abstract class BasicAttributeComponent implements AttributeComponent {
	private Field field;
	private boolean enabled;
	private InventoryArticle article;
	private CatalogArticleAttribute attribute;

	
	@Override
	public void setValue(Object value) {
		field.setValue(value);
	}

	@Override
	public Object getvalue() {
		return field.getValue();
	}

	@Override
	public abstract Component getInstance(InventoryArticle article, CatalogArticleAttribute pageAttribute);

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

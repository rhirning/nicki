package org.mgnl.nicki.vaadin.base.shop.attributes;

import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public class LabelComponent implements AttributeComponent {

	private Label label;
	private InventoryArticle article;
	private CatalogArticleAttribute attribute;

	public LabelComponent() {
		label = new Label();
	}
	@Override
	public Component getInstance(InventoryArticle article,
			CatalogArticleAttribute attribute) {
		this.article = article;
		this.attribute = attribute;
		setCaption(attribute.getLabel());
		return label;
	}

	@Override
	public void setValue(Object value) {
	}

	@Override
	public Object getvalue() {
		return null;
	}
	@Override
	public void setCaption(String caption) {
		label.setCaption(caption);
	}
	@Override
	public void setEnabled(boolean enabled) {
	}
	@Override
	public boolean isEnabled() {
		return false;
	}
	@Override
	public String getStringValue(Object value) {
		return (String) value;
	}
}

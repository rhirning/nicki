package org.mgnl.nicki.vaadin.base.shop.attributes;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class LabelComponent extends BasicAttributeComponent implements AttributeComponent {

	private Label label;

	public LabelComponent() {
		label = new Label();
	}

	@Override
	public Component getInstance(Person user, Person person, InventoryArticle article, CatalogArticleAttribute attribute) {
		setArticle(article);
		setAttribute(attribute);
		String value = attribute.getLabel();
		if (StringUtils.isNotEmpty(value)) {
			value += ": ";
		}
		String content = getContent(String.class, user, person);
		value += StringUtils.trimToEmpty(content);
		label.setCaption(value);
		return label;
	}

	public Component XXXgetInstance(Person user, Person person, InventoryArticle article,
			CatalogArticleAttribute attribute) {
//		this.article = article;
//		this.attribute = attribute;
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

package org.mgnl.nicki.vaadin.base.shop.attributes;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public class StaticComponent extends BasicAttributeComponent implements AttributeComponent {

	private Label field;

	public StaticComponent() {
		field = new Label();
		field.setWidth("200px");
		field.setEnabled(false);
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
		field.setCaption(value);
		return field;
	}
}

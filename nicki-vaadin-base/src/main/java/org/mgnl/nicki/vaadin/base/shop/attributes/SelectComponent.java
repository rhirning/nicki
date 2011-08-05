package org.mgnl.nicki.vaadin.base.shop.attributes;

import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class SelectComponent extends BasicAttributeComponent implements AttributeComponent {

	ComboBox field;
	public SelectComponent() {
		field = new ComboBox();
		field.setImmediate(true);
		setField(field);
	}
	@Override
	public Component getInstance(Person user, Person person, InventoryArticle article, CatalogArticleAttribute attribute) {
		setArticle(article);
		setAttribute(attribute);
		setCaption(attribute.getLabel());
		List<String> content = getContent(List.class, user, person);
		if (content != null) {
			for (Iterator<String> iterator = content.iterator(); iterator.hasNext();) {
				field.addItem(iterator.next());
			}
		}
		if (isEnabled()) {
			getField().addListener(new CatalogAttributeInputListener(getArticle(), getAttribute()));
		}
		
		return getField();
	}
}

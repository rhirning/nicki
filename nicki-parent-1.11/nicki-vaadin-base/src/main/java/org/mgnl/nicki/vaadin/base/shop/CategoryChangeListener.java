package org.mgnl.nicki.vaadin.base.shop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

@SuppressWarnings("serial")
public class CategoryChangeListener implements Property.ValueChangeListener{
	private ShopWindow shopWindow;
	private AbstractComponentContainer container;

	public CategoryChangeListener(ShopWindow shopWindow,
			HorizontalLayout container) {
		this.shopWindow = shopWindow;
		this.container = container;
	}

	public void valueChange(ValueChangeEvent event) {
		List<Object> values = collectValues(this.container);
		this.shopWindow.setCategoryFilter(values);
//		this.shopWindow.getWindow().showNotification(values.toString(), Notification.TYPE_WARNING_MESSAGE);

	}
	
	public List<Object> collectValues(AbstractComponentContainer cont) {
		List<Object> list = new ArrayList<Object>();
		for (Iterator<Component> iterator = cont.getComponentIterator(); iterator.hasNext();) {
			Component component = iterator.next();
			if (component instanceof AbstractField) {
				boolean value = (Boolean) ((AbstractField) component).getValue();
				if (value) {
					list.add(component.getCaption());
				}
			}
			if (component instanceof AbstractComponentContainer) {
				list.addAll(collectValues((AbstractComponentContainer) component));
			}
		}
		return list;
	}

}

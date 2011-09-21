package org.mgnl.nicki.vaadin.base.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public abstract class BaseAttributeListener implements ValueChangeListener {

	private DynamicObject dynamicObject;
	private String name;
	public BaseAttributeListener(DynamicObject dynamicObject, String name) {
		this.setDynamicObject(dynamicObject);
		this.setName(name);
	}
	public abstract void textChange(TextChangeEvent event);

	public List<Object> collectValues(AbstractComponentContainer cont) {
		List<Object> list = new ArrayList<Object>();
		for (Iterator<Component> iterator = cont.getComponentIterator(); iterator.hasNext();) {
			Component component = iterator.next();
			if (component instanceof AbstractField) {
				String value = (String) ((AbstractField) component).getValue();
				if (StringUtils.isNotEmpty(value)) {
					list.add(value);
				}
			}
			if (component instanceof AbstractComponentContainer) {
				list.addAll(collectValues((AbstractComponentContainer) component));
			}
		}
		return list;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setDynamicObject(DynamicObject dynamicObject) {
		this.dynamicObject = dynamicObject;
	}
	public DynamicObject getDynamicObject() {
		return dynamicObject;
	}

}

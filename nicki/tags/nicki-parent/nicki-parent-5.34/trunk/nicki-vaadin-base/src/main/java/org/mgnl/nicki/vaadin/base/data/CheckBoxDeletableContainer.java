package org.mgnl.nicki.vaadin.base.data;

import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;

public abstract class CheckBoxDeletableContainer<T extends Deletable> extends IndexedContainer implements Container {
	private static final long serialVersionUID = -5658914311396563600L;
	

	public CheckBoxDeletableContainer(Class<? super T> type,
			Collection<? extends T> collection, Object columns[]) throws IllegalArgumentException {
		super();
		for (Object object : columns) {
			addContainerProperty(object, String.class, "");
		}
		addContainerProperty("delete", Component.class, null);
		for (T bean : collection) {
			Item item = addItem(bean);
			for (Object object : columns) {
				@SuppressWarnings("unchecked")
				Property<String> property = item.getItemProperty(object);
				property.setValue(get(bean, (String) object));
			}
			@SuppressWarnings("unchecked")
			Property<Component> editProperty = item.getItemProperty("delete");
			if (bean.isDeletable()) {
				editProperty.setValue(new DeleteCheckBox<T>(bean));
			}
		}
	}

	@SuppressWarnings("serial")
	public class DeleteCheckBox<T1 extends Deletable> extends CheckBox implements Property<Boolean> {
		
		public DeleteCheckBox(T1 bean) {
			setData(bean);
			if (bean.isDeleted()) {
				setValue(true);
			} else {
				setValue(false);
			}
			addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
					@SuppressWarnings("unchecked")
					T1 bean = (T1) getData();
					if ((boolean) event.getProperty().getValue()) {
						bean.delete();
					} else {
						bean.undelete();
					}
				}
			});
		}
		
	}

	public static String get(Object object, String name) {
		String methodName =  "get" + StringUtils.capitalize(name);
		try {
			Method method = object.getClass().getMethod(methodName, new Class[]{});
			return (String) method.invoke(object, new Object[]{});
		} catch (Exception e) {
		}
		return "";
	}
}
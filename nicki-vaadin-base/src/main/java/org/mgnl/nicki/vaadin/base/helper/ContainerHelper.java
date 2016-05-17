package org.mgnl.nicki.vaadin.base.helper;

import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;

public class ContainerHelper {
	private static final Logger LOG = LoggerFactory.getLogger(ContainerHelper.class);



	/*
	public static <T extends Object> Container getListContainer(Collection<T> data, String namedProperties) {
		Container container = new IndexedContainer(data);
		container.addContainerProperty(namedProperties, String.class, "");
		return container;
	}
	*/

	public static <T extends Object> Container getListContainer(Collection<T> data, String... namedProperties) {
		Container container = new IndexedContainer(data);
		for (String name : namedProperties) {
			container.addContainerProperty(name, String.class, "");
		}
		return container;
	}

	public static <T extends Object> Container getTableContainer(Collection<T> data, String... namedProperties) {
		return new TableContainer(data, namedProperties);
	}
	
	public static <T extends Object> Container getDataContainer(T data, String[] properties, String i18nBase) {
		Container container = new BeanItemContainer<ValuePair>(ValuePair.class);
		for (String property : properties) {
			addItem(container, data, property, i18nBase);
		}
		return container;
	}
	
	private static <T extends Object> void addItem(Container container, T data, String name, String i18nBase) {
		String translatedName = name;
		if (i18nBase != null) {
			translatedName = I18n.getText(i18nBase + "." + name);
		}
		try {
			
			if (data != null) {
				container.addItem(new ValuePair(translatedName, BeanUtils.getProperty(data, name)));
				
			} else {
				container.addItem(new ValuePair(translatedName, ""));
			}
		} catch (Exception e) {
			LOG.error("Error", e);
		}
	}
	
	public static class TableContainer extends IndexedContainer implements Container {
		private static final long serialVersionUID = 1495392912411015597L;

		public <T extends Object> TableContainer(
				Collection<? extends T> collection, String... namedProperties) throws IllegalArgumentException {
			super();
			for (String name : namedProperties) {
				addContainerProperty(name, Comparable.class, "");
			}
			for (T bean : collection) {
				Item item = addItem(bean);
				for (String name : namedProperties) {
					@SuppressWarnings("unchecked")
					Property<Object> property = item.getItemProperty(name);
					property.setValue(get(bean, name));
				}
			}
		}
	}

	public static Object get(Object object, String name) {
		String methodName =  "get" + StringUtils.capitalize(name);
		try {
			Method method = object.getClass().getMethod(methodName, new Class[]{});
			return method.invoke(object, new Object[]{});
		} catch (Exception e) {
		}
		return null;
	}

}

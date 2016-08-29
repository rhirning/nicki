package org.mgnl.nicki.vaadin.base.data;

import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

public abstract class DeletableContainer<T extends Deletable> extends IndexedContainer implements Container {
	private static final long serialVersionUID = -5658914311396563600L;
	
	private String deleteCaption;
	private String undeleteCaption;

	public DeletableContainer(Collection<? extends T> collection, Object columns[],
			String deleteCaption, String undeleteCaption) throws IllegalArgumentException {
		super();
		this.deleteCaption = deleteCaption;
		this.undeleteCaption = undeleteCaption;
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
			String caption;
			if (bean.isDeletable()) {
				if (bean.isDeleted()) {
				 caption = undeleteCaption;
				} else {
					 caption = deleteCaption;
				}
				editProperty.setValue(new DeleteButton<T>(bean, caption));
			}
		}
	}

	public class DeleteButton<T1 extends Deletable> extends Button implements Property<Component> {
		private static final long serialVersionUID = -5928278839208615249L;
		public DeleteButton(T1 bean, String title) {
			setCaption(title);
			setData(bean);
			addClickListener(new ClickListener() {
				private static final long serialVersionUID = -900229029571991404L;

				@Override
				public void buttonClick(ClickEvent event) {
					@SuppressWarnings("unchecked")
					T1 bean = (T1) event.getButton().getData();
					if (!bean.isDeleted()) {
						bean.delete();
						event.getButton().setCaption(undeleteCaption);
					} else {
						bean.undelete();
						event.getButton().setCaption(deleteCaption);
					}
					
				}
			});
		}
		@Override
		public DeleteButton<T1> getValue() {
			return this;
		}
		@Override
		public void setValue(Component newValue)
				throws ReadOnlyException {
			throw new ReadOnlyException();
		}
		@Override
		public Class<? extends Component> getType() {
			return this.getType();
		}
		
	}

	public static String get(Object object, String name) {
		String methodName =  "get" + StringUtils.capitalize(name);
		try {
			Method method = object.getClass().getMethod(methodName, new Class[]{});
			return (String) method.invoke(object);
		} catch (Exception e) {
			return "";
		}
	}
}

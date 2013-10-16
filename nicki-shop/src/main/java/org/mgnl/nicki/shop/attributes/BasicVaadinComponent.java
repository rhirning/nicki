package org.mgnl.nicki.shop.attributes;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.shop.attributes.CatalogAttributeInputListener;
import org.mgnl.nicki.shop.attributes.VaadinComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public abstract class BasicVaadinComponent implements VaadinComponent {
	private static final Logger LOG = LoggerFactory.getLogger(BasicVaadinComponent.class);
	
	private HorizontalLayout layout;
	private Label data;
	private CatalogAttributeInputListener catalogAttributeInputListener;
	private NickiContext context;
	

	public void setContext(NickiContext context) {
		this.context = context;
	}

	public BasicVaadinComponent() {
		layout = new HorizontalLayout();
		
		data = new Label();
		layout.addComponent(data);
		
		Button changer = new Button("Select");
		changer.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 8243791827133555616L;

			public void buttonClick(ClickEvent event) {
				try {
					showSelector();
				} catch (InstantiateDynamicObjectException e) {
					Notification.show("Error", e.getMessage(), Type.ERROR_MESSAGE);
					LOG.error("Could not start Selector", e);
				}
			}
		});
		layout.addComponent(changer);
	}

	public abstract void showSelector() throws InstantiateDynamicObjectException;
	
	public void setValue(String value) {
		data.setValue(value);
		if (catalogAttributeInputListener != null) {
			catalogAttributeInputListener.setValue(value);
		}
	}

	public void addValueChangeListener(
			CatalogAttributeInputListener catalogAttributeInputListener) {
		this.catalogAttributeInputListener = catalogAttributeInputListener;
	}

	public Component getComponent() {
		return layout;
	}

	public String getValue() {
		return data.getValue();
	}

	public void setCaption(String caption) {
		data.setCaption(caption);
		
	}

	public void setEnabled(boolean enabled) {
				
	}

	public boolean isEnabled() {
		return false;
	}


	public NickiContext getContext() {
		return context;
	}

}

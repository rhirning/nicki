package org.mgnl.nicki.shop.attributes;

import org.mgnl.nicki.shop.attributes.CatalogAttributeInputListener;
import org.mgnl.nicki.shop.attributes.VaadinComponent;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

public abstract class BasicVaadinComponent implements VaadinComponent {
	
	private HorizontalLayout layout;
	private Label data;
	private CatalogAttributeInputListener catalogAttributeInputListener;
	

	public BasicVaadinComponent() {
		layout = new HorizontalLayout();
		
		data = new Label();
		layout.addComponent(data);
		
		Button changer = new Button("Select");
		changer.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 8243791827133555616L;

			public void buttonClick(ClickEvent event) {
				showSelector();
			}
		});
		layout.addComponent(changer);
	}

	public abstract void showSelector();
	
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

}

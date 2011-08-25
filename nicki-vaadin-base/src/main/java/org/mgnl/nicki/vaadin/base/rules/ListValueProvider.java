package org.mgnl.nicki.vaadin.base.rules;

import org.mgnl.nicki.shop.catalog.Selector;
import org.mgnl.nicki.shop.rules.BaseDn;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ListSelect;

public class ListValueProvider implements ValueProviderComponent {

	private Selector selector;
	private AbstractSelect value;
	
	public ListValueProvider() {
	}

	public void init(Selector selector) {
		this.selector = selector;
	}

	@Override
	public AbstractSelect getValueList() {
		
		value = new ListSelect();
		value.setCaption("Wert");
		value.setImmediate(false);
		value.setWidth("200px");
		value.setHeight("200px");

		value.setNullSelectionAllowed(false);

		for (String entry : selector.getValues()) {
			value.addItem(entry);
		}
		return value;
	}
	
	public String getValue() {
		return (String)value.getValue();
	}

	@Override
	public String getQuery(String value) {
		return selector.getName() + "=" + value;
	}

	@Override
	public boolean isHierarchical() {
		return false;
	}

	@Override
	public BaseDn getBaseDn(String value) {
		return null;
	}


}

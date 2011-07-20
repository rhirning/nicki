package org.mgnl.nicki.vaadin.base.rights;

import org.mgnl.nicki.rights.core.RightAttribute;

import com.vaadin.ui.Component;
import com.vaadin.ui.NativeSelect;

public class CostCenterComponent implements AttributeComponent {

	@Override
	public Component getInstance(RightAttribute rightAttribute) {
		NativeSelect select = new NativeSelect(rightAttribute.getLabel());
		select.addItem("12345");
		return select;
	}

}

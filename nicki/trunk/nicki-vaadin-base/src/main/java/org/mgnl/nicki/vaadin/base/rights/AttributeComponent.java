package org.mgnl.nicki.vaadin.base.rights;

import org.mgnl.nicki.rights.core.RightAttribute;

import com.vaadin.ui.Component;

public interface AttributeComponent {

	Component getInstance(RightAttribute rightAttribute);

}

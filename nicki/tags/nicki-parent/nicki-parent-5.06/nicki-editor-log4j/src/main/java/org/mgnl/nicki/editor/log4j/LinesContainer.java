package org.mgnl.nicki.editor.log4j;

import org.mgnl.nicki.core.helper.NameValue;

import com.vaadin.data.util.BeanItemContainer;

public class LinesContainer extends BeanItemContainer<NameValue> {
	private static final long serialVersionUID = -4284842383485154677L;

	public LinesContainer()
			throws IllegalArgumentException {
		super(NameValue.class);
	}

}

package org.mgnl.nicki.vaadin.db.editor;

import java.util.List;

public abstract class DbBeanCloseListener implements DbBeanValueChangeListener {

	@Override
	public void valueChange(Object bean, String name, List<Object> values) {
	}

	@Override
	public void valueChange(Object bean, String attributeName, Object value) {
	}

	@Override
	public boolean acceptAttribute(String name) {
		return true;
	}

	@Override
	public void refresh(Object bean) {
	}

}

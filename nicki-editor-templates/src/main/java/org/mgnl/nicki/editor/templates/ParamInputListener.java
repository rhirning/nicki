package org.mgnl.nicki.editor.templates;

import java.util.Map;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Field;

@SuppressWarnings("serial")
public class ParamInputListener implements ValueChangeListener {

	Field field;
	String name;
	private Map<String, Object> map;

	public ParamInputListener(Field field, String name, Map<String, Object> map) {
		this.field = field;
		this.name = name;
		this.map = map;
	}

	public void valueChange(ValueChangeEvent event) {
		map.put(name, event.getProperty().getValue());
	}

}

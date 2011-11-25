package org.mgnl.nicki.editor.templates;

import java.util.Map;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Field;

@SuppressWarnings("serial")
public class ParamInputListener implements ValueChangeListener {

	private String name;
	private Map<String, Object> map;
	private TemplateConfig templateConfig;

	public ParamInputListener(Field field, String name, Map<String, Object> map, TemplateConfig templateConfig) {
		this.name = name;
		this.map = map;
		this.templateConfig = templateConfig;
	}

	public void valueChange(ValueChangeEvent event) {
		map.put(name, event.getProperty().getValue());
		this.templateConfig.paramsChanged();
	}

}

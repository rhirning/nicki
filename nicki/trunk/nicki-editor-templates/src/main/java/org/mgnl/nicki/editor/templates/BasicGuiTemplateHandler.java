package org.mgnl.nicki.editor.templates;

import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.template.engine.BasicTemplateHandler;

import com.vaadin.ui.Component;

public class BasicGuiTemplateHandler extends BasicTemplateHandler implements
		GuiTemplateHandler {

	public BasicGuiTemplateHandler(Template template) {
		super(template);
	}

	@Override
	public Component getConfigDialog(Template template,
			Map<String, Object> params, TemplateConfig templateConfig) {
		return new ConfiguredTemplateConfigDialog(template, params, templateConfig);
	}

}

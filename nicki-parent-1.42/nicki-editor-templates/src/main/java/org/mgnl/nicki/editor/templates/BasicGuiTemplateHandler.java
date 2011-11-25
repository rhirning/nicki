package org.mgnl.nicki.editor.templates;

import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.template.engine.BasicTemplateHandler;
import org.mgnl.nicki.template.engine.TemplateParameter;

import com.vaadin.ui.Component;

public class BasicGuiTemplateHandler extends BasicTemplateHandler implements
		GuiTemplateHandler {

	public BasicGuiTemplateHandler() {
		super();
	}

	@Override
	public Component getConfigDialog(Template template,
			Map<String, Object> params, TemplateConfig templateConfig) {
		return new ConfiguredTemplateConfigDialog(template, params, templateConfig);
	}

	@Override
	public boolean isComplete(Map<String, Object> params) {
		java.util.List<TemplateParameter> list = getTemplateParameters();
		if (list != null) {
			for (TemplateParameter templateParameter : list) {
				if (!params.containsKey(templateParameter.getName())) {
					return false;
				} else {
					if (null == params.get(templateParameter.getName())) {
						return false;
					}
				}
			}
		}
		return true;
	}

}

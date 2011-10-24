package org.mgnl.nicki.editor.templates;

import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.template.handler.TemplateHandler;

import com.vaadin.ui.Component;

public interface GuiTemplateHandler extends TemplateHandler {

	Component getConfigDialog(Template template,
			Map<String, Object> params, TemplateConfig templateConfig);

}

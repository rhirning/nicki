package org.mgnl.nicki.editor.templates;

import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.template.engine.TemplateHelper;

import com.vaadin.ui.Component;

public class GuiTemplateHelper extends TemplateHelper {

	public static GuiTemplateHandler getGuiTemplateHandler(Template template) {
		GuiTemplateHandler handler = null;
		if (template.hasHandler()) {
			try {
				handler = (GuiTemplateHandler) Class.forName(template.getHandler()).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (handler == null) {
			handler = new BasicGuiTemplateHandler();
			handler.setTemplate(template);
		}
		return handler;
	}

	public static Component getConfigDialog(
			Template template, Map<String, Object> params,
			TemplateConfig templateConfig) {
		return getGuiTemplateHandler(template).getConfigDialog(template, params, templateConfig);
	}

	public static boolean isComplete(Template template, Map<String, Object> params) {
		GuiTemplateHandler handler = getGuiTemplateHandler(template);
		return handler.isComplete(params);
	}


}

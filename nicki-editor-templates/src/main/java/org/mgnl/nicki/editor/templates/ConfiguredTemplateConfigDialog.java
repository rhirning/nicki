package org.mgnl.nicki.editor.templates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.template.engine.TemplateHelper;
import org.mgnl.nicki.template.engine.TemplateParameter;
import org.mgnl.nicki.vaadin.base.data.DateHelper;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;

public class ConfiguredTemplateConfigDialog extends CustomComponent implements TemplateConfigDialog {

	private static final long serialVersionUID = -1295818262583276359L;

	private FormLayout mainLayout;
	
	private Template template;
	private Map<TemplateParameter, Field> fields = new HashMap<TemplateParameter, Field>();
	private Map<String, Object> params;
	private TemplateConfig templateConfig;

	public ConfiguredTemplateConfigDialog(Template template, Map<String, Object> params, TemplateConfig templateConfig) {
		this.template = template;
		this.params = params;
		this.templateConfig = templateConfig;
		
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private FormLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new FormLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		List<TemplateParameter> templateParameters = TemplateHelper.getTemplateHandler(template).getTemplateParameters();
		if (templateParameters != null) {
			for (TemplateParameter templateParameter : templateParameters) {
				if (StringUtils.equalsIgnoreCase("date", templateParameter.getDataType())) {
					PopupDateField field = new PopupDateField();
					field.setCaption(templateParameter.getDisplayName());
					field.setImmediate(true);
					field.setWidth("-1px");
					field.setHeight("-1px");
					field.setInvalidAllowed(false);
					mainLayout.addComponent(field);
					DateHelper.init(field);
					field.addListener(new ParamInputListener(field, templateParameter.getName(), params, templateConfig));
					fields.put(templateParameter, field);
				} else if (StringUtils.equalsIgnoreCase("string", templateParameter.getDataType())) {
					TextField field = new TextField();
					field.setCaption(templateParameter.getDisplayName());
					field.setImmediate(true);
					field.setWidth("-1px");
					field.setHeight("-1px");
					field.setInvalidAllowed(false);
					mainLayout.addComponent(field);
					field.addListener(new ParamInputListener(field, templateParameter.getName(), params, templateConfig));
					fields.put(templateParameter, field);
				} else if (StringUtils.equalsIgnoreCase("static", templateParameter.getDataType())) {
					params.put(templateParameter.getName(), templateParameter.getValue());
				}
			}
		}
		
		return mainLayout;
	}

}

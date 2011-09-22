package org.mgnl.nicki.template.engine;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.template.engine.BasicTemplateHandler;
import org.mgnl.nicki.template.handler.TemplateHandler;


public class BasicTemplateStreamSource {
	private static final long serialVersionUID = 4222973194514516918L;

	Template template;
	private String templatePath;
	private TemplateHandler handler;
	
	public BasicTemplateStreamSource(Template template, NickiContext context) {
		this.template = template;
		// render template
		String parentPath = Config.getProperty("nicki.templates.basedn");
		templatePath = template.getSlashPath(parentPath);
		if (StringUtils.contains(templatePath, "_")) {
			templatePath = StringUtils.substringBefore(templatePath, "_");
		}
		handler = null;
		if (template.hasHandler()) {
			try {
				handler = (TemplateHandler) Class.forName(template.getHandler()).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (handler == null) {
			handler = new BasicTemplateHandler();
		}
		handler.setUser((Person) template.getContext().getUser());
		handler.setContext(context);
	}

	public Map<String, Object> getDataModel() {
		return handler.getDataModel();
	}

	public TemplateHandler getHandler() {
		return handler;
	}

	public String getTemplatePath() {
		return templatePath;
	}
	
	public InputStream getStringStream() {
		try {
			return TemplateEngine.getInstance().executeTemplate(getTemplatePath(), getDataModel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public InputStream getPdfStream() {
		try {
			return TemplateEngine.getInstance().executeTemplateAsPdf(getTemplatePath(), getDataModel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	public InputStream getCsVStream() {
		try {
			return TemplateEngine.getInstance().executeTemplateAsCsv(getTemplatePath(), getDataModel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	


}

package org.mgnl.nicki.editor.templates;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.XsltHelper;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.template.engine.BasicTemplateHandler;
import org.mgnl.nicki.template.engine.TemplateEngine;
import org.mgnl.nicki.template.handler.TemplateHandler;

import com.vaadin.terminal.StreamResource.StreamSource;

public class CsvStreamSource implements StreamSource {
	private static final long serialVersionUID = 4222973194514516918L;

	Template template;
	private NickiContext context = null;
	
	public CsvStreamSource(Template template, NickiContext context) {
		this.template = template;
		this.context = context;
	}

	@Override
	public InputStream getStream() {
		// render template
		String parentPath = Config.getProperty("nicki.templates.basedn");
		String templatePath = template.getSlashPath(parentPath);
		if (StringUtils.contains(templatePath, "_")) {
			templatePath = StringUtils.substringBefore(templatePath, "_");
		}
		TemplateHandler handler = null;
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
		try {
			String templateResult = TemplateEngine.executeTemplate(templatePath, handler.getDataModel());
//			return new ByteArrayInputStream(templateResult.getBytes("UTF-8"));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = new ByteArrayInputStream(templateResult.getBytes("UTF-8"));
			InputStream xslTemplate = this.getClass().getResourceAsStream("/META-INF/nicki/xsl/csv.xsl");
			XsltHelper.xsl(in, out, xslTemplate);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	

}

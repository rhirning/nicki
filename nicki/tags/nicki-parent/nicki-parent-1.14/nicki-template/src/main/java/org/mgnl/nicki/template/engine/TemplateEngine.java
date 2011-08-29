package org.mgnl.nicki.template.engine;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.template.engine.ConfigurationFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;


public class TemplateEngine {
	public static final String PROPERTY_BASE_DN = "nicki.templates.basedn";
	public static final String DEFAULT_BASE_DN = "ou=templates,o=utopia";
	
	
	public static String executeTemplate(String templateName, Map<String, Object> dataModel)
			throws IOException, TemplateException, InvalidPrincipalException {

		Configuration cfg = ConfigurationFactory.getInstance().getConfiguration(
		Config.getProperty(PROPERTY_BASE_DN, DEFAULT_BASE_DN));
		
		Template template = cfg.getTemplate(templateName);

		StringWriter out = new StringWriter();
		template.process(dataModel, out);
		out.flush();
		return out.toString();
	}

}

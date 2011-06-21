package org.mgnl.nicki.mailtemplate.test;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.context.AppContext;
import org.mgnl.nicki.template.engine.ConfigurationFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateTest {
	public static final String PROPERTY_BASE_DN = "nicki.templates.basedn";
	public static final String DEFAULT_BASE_DN = "ou=templates,o=utopia";

	/**
	 * @param args
	 * @throws IOException 
	 * @throws TemplateException 
	 * @throws InvalidPrincipalException 
	 */
	public static void main(String[] args) throws IOException, TemplateException, InvalidPrincipalException {
		Configuration cfg = ConfigurationFactory.getInstance().getConfiguration(Config.getProperty(PROPERTY_BASE_DN, DEFAULT_BASE_DN));
		Template temp = cfg.getTemplate("/CoCo/Rollen.ftl");
		

		Writer out = new OutputStreamWriter(System.out);
		temp.process(TestData.getDataModel(AppContext.getSystemContext()), out);
		out.flush(); 

	}
}

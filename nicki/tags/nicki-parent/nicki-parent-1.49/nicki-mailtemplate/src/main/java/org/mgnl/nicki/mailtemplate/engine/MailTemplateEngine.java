/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.mailtemplate.engine;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.mailtemplate.model.MailContainer;
import org.mgnl.nicki.mailtemplate.model.MailWrapper;
import org.mgnl.nicki.template.engine.ConfigurationFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;


public class MailTemplateEngine {
	public static final String MAILPART_SUBJECT = "subject";
	public static final String MAILPART_HEADER = "header";
	public static final String MAILPART_FOOTER = "footer";
	public static final String LOCALE_DE = "de";
	public static final String LOCALE_FR = "fr";
	public static final String LOCALE_IT = "it";
	public static final String PROPERTY_BASE_DN = "nicki.mailtemplates.basedn";
	public static final String DEFAULT_BASE_DN = "ou=mailtemplates,o=utopia";
	public static final String LOCALE_NAMES[] = {LOCALE_DE, LOCALE_FR, LOCALE_IT};
	public static final Locale LOCALES[] = {Locale.GERMAN, Locale.FRENCH, Locale.ITALIAN};
	public static final String MAILPARTS[] = {MAILPART_SUBJECT, MAILPART_HEADER, MAILPART_FOOTER};
	
	
	
	public static MailWrapper executeMailTemplate(String templateName, Map<String, Object> dataModel)
			throws IOException, TemplateException, InvalidPrincipalException {
		MailWrapper mailWrapper = new MailWrapper();

		Configuration cfg = ConfigurationFactory.getInstance().getConfiguration(
				Config.getProperty(PROPERTY_BASE_DN, DEFAULT_BASE_DN));
		for (int i = 0; i < LOCALES.length; i++) {
			Locale locale = LOCALES[i];
			String localeName = LOCALE_NAMES[i];
			MailContainer container = new MailContainer(localeName);
			for (int j = 0; j < MAILPARTS.length; j++) {
				String mailPart = MAILPARTS[j];
				
				Template template = cfg.getTemplate(templateName + "." + mailPart + ".ftl", locale);

				StringWriter out = new StringWriter();
				template.process(dataModel, out);
				out.flush();
				container.put(mailPart, out.toString());
			}
			mailWrapper.put(localeName, container);
		}
		return mailWrapper;
	}

}
/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.mailtemplate.test;

import java.io.IOException;

import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.context.AppContext;
import org.mgnl.nicki.mailtemplate.engine.MailTemplateEngine;
import org.mgnl.nicki.mailtemplate.model.MailWrapper;

import freemarker.template.TemplateException;

public class MailTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws TemplateException 
	 * @throws InvalidPrincipalException 
	 */
	public static void main(String[] args) throws IOException, TemplateException, InvalidPrincipalException {
		MailWrapper wrapper = 
		MailTemplateEngine.executeMailTemplate("/mail1", TestData.getDataModel(AppContext.getSystemContext()));
		System.out.println(wrapper.getMailPart(MailTemplateEngine.LOCALE_DE, MailTemplateEngine.MAILPART_SUBJECT));
		System.out.println(wrapper.getMailPart(MailTemplateEngine.LOCALE_DE, MailTemplateEngine.MAILPART_HEADER));
		System.out.println(wrapper.getMailPart(MailTemplateEngine.LOCALE_DE, MailTemplateEngine.MAILPART_FOOTER));
	}

}

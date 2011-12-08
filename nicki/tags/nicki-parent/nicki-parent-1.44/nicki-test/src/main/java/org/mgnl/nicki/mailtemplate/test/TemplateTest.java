/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
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

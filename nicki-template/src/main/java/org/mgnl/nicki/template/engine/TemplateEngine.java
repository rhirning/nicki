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
package org.mgnl.nicki.template.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.XsltRenderer;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.context.AppContext;
import org.mgnl.nicki.template.engine.ConfigurationFactory;
import org.mgnl.nicki.template.handler.TemplateHandler;
import org.mgnl.nicki.template.pdf.PdfTemplateRenderer;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateEngine {
	public enum OUTPUT_TYPE {TXT, PDF, CSV };
	public static final String PROPERTY_BASE_DN = "nicki.templates.basedn";
	
	private static TemplateEngine instance = new TemplateEngine();

	/*
	 * 4 Supported arguments user password template type
	 */
	public void execute(String userName, String password, String templateName, String outputTypeName, OutputStream out)
		throws Exception {

		OUTPUT_TYPE outputType = OUTPUT_TYPE.TXT;
		if (StringUtils.isNotEmpty(outputTypeName)) {
			if (StringUtils.equalsIgnoreCase(outputTypeName, OUTPUT_TYPE.CSV.toString())) {
				outputType = OUTPUT_TYPE.CSV;
			} else if (StringUtils.equalsIgnoreCase(outputTypeName, OUTPUT_TYPE.PDF.toString())) {
				outputType = OUTPUT_TYPE.PDF;
			}
		}
		Person user = (Person) AppContext.getSystemContext().login(userName, password);
		if (user == null) {
			throw new Exception("login failed");
		}
		TemplateHandler handler = new BasicTemplateHandler();
		handler.setUser(user);
		handler.setContext(user.getContext());
		
		switch (outputType) {
		case TXT:
			IOUtils.copy(executeTemplate(templateName, handler.getDataModel()), out);
			break;
		case CSV:
			IOUtils.copy(executeTemplateAsCsv(templateName, handler.getDataModel()), out);
			break;
		case PDF:
			IOUtils.copy(executeTemplateAsPdf(templateName, handler.getDataModel()), out);
			break;

		default:
			break;
		}
	}

	public InputStream executeTemplate(String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException {
		Configuration cfg = ConfigurationFactory.getInstance().getConfiguration(
				Config.getProperty(PROPERTY_BASE_DN));
		Template template = cfg.getTemplate(templateName);
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
	    RenderTemplate renderTemplate = new RenderTemplate(template, dataModel, pos);
	    renderTemplate.start();
		return pis;
	}

	public InputStream executeTemplateAsPdf(String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
		PdfTemplateRenderer renderer = new PdfTemplateRenderer(executeTemplate(templateName, dataModel), pos);
		renderer.start();
		return pis;
	}

	public InputStream executeTemplateAsCsv(String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {
		InputStream xslTemplate = this.getClass().getResourceAsStream("/META-INF/nicki/xsl/csv.xsl");
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
		XsltRenderer renderer = new XsltRenderer(executeTemplate(templateName, dataModel), pos, xslTemplate);
		renderer.start();
		return pis;
	}

	public static TemplateEngine getInstance() {
		return instance;
	}

}

/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.template.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.XsltRenderer;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.template.engine.ConfigurationFactory.TYPE;
import org.mgnl.nicki.template.handler.TemplateHandler;
import org.mgnl.nicki.template.pdf.PdfTemplateRenderer;
import org.mgnl.nicki.template.pdf.PdfTemplateRenderer2;
import org.mgnl.nicki.template.pdf.XlsTemplateRenderer;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateEngine {
	public enum OUTPUT_TYPE {TXT, PDF, CSV, PDF2, XLS };
	public final static String DEFAULT_CHARSET = "UTF-8";
	public final static String CSV_CHARSET = "ISO-8859-1";
	public static final String PROPERTY_BASE_DN = "nicki.templates.basedn";
	public static final String PROPERTY_BASE = "nicki.templates.base";

	private Configuration cfg;
	private static Map<ConfigurationFactory.TYPE, TemplateEngine> instances = new HashMap<>();

	public TemplateEngine(Configuration cfg) {
		this.cfg = cfg;
	}

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
			IOUtils.copy(executeTemplate(templateName, handler.getDataModel(), DEFAULT_CHARSET), out);
			break;
		case CSV:
			IOUtils.copy(executeTemplateAsCsv(templateName, handler.getDataModel()), out);
			break;
		case PDF:
			IOUtils.copy(executeTemplateAsPdf(templateName, handler.getDataModel()), out);
			break;
		case PDF2:
			IOUtils.copy(executeTemplateAsPdf2(templateName, handler.getDataModel()), out);
			break;

		default:
			break;
		}
	}

	public InputStream executeTemplate(String templateName,
			Map<String, Object> dataModel, String charset) throws IOException,
			TemplateException, InvalidPrincipalException {
		Template template = cfg.getTemplate(templateName);
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
	    RenderTemplate renderTemplate = new RenderTemplate(template, dataModel, pos, charset);
	    renderTemplate.start();
		return pis;
	}

	public InputStream executeTemplateAsPdf(String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
		PdfTemplateRenderer renderer = new PdfTemplateRenderer(executeTemplate(templateName, dataModel, DEFAULT_CHARSET), pos);
		renderer.start();
		return pis;
	}

	public InputStream executeTemplateAsPdf2(String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
		PdfTemplateRenderer2 renderer = new PdfTemplateRenderer2(executeTemplate(templateName, dataModel, DEFAULT_CHARSET), pos);
		renderer.start();
		return pis;
	}

	public InputStream executeTemplateAsXls(org.mgnl.nicki.dynamic.objects.objects.Template template, String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
	    
		XlsTemplateRenderer renderer = new XlsTemplateRenderer(template, executeTemplate(templateName, dataModel, DEFAULT_CHARSET), pos);
		renderer.start();
		return pis;
	}

	public InputStream executeTemplateAsXls(byte[] master, String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
	    
		XlsTemplateRenderer renderer = new XlsTemplateRenderer(master, executeTemplate(templateName, dataModel, DEFAULT_CHARSET), pos);
		renderer.start();
		return pis;
	}

	public InputStream executeTemplateAsCsv(String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {
		InputStream xslTemplate = this.getClass().getResourceAsStream("/META-INF/nicki/xsl/csv.xsl");
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
		XsltRenderer renderer = new XsltRenderer(executeTemplate(templateName, dataModel, DEFAULT_CHARSET), pos, xslTemplate);
		renderer.start();
		return pis;
	}

	public InputStream executeTemplateAsCsv2(String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {
		InputStream xslTemplate = this.getClass().getResourceAsStream("/META-INF/nicki/xsl/csv2.xsl");
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
		XsltRenderer renderer = new XsltRenderer(executeTemplate(templateName, dataModel, DEFAULT_CHARSET), pos, xslTemplate);
		renderer.start();
		return pis;
	}

	public static TemplateEngine getInstance() {
		return instances.get(ConfigurationFactory.TYPE.JNDI);
	}
	public static TemplateEngine getInstance(ConfigurationFactory.TYPE type) {
		if (!instances.containsKey(type)) {
			load(type);
		}
		return instances.get(type);
	}

	private static void load(TYPE type) {
		if (type == TYPE.JNDI) { 
			Configuration cfg = ConfigurationFactory.getInstance().getConfiguration(ConfigurationFactory.TYPE.JNDI,
						Config.getProperty(PROPERTY_BASE_DN));
				instances.put(TYPE.JNDI, new TemplateEngine(cfg));
		} else if (type == TYPE.CLASSPATH) {
			Configuration cfg = ConfigurationFactory.getInstance().getConfiguration(ConfigurationFactory.TYPE.CLASSPATH,
						Config.getProperty(PROPERTY_BASE, "/META-INF/templates"));
			instances.put(TYPE.CLASSPATH, new TemplateEngine(cfg));
		}
	}

}

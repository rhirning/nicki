
package org.mgnl.nicki.template.engine;

/*-
 * #%L
 * nicki-template
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.helper.XsltRenderer;
import org.mgnl.nicki.dynamic.objects.objects.EngineTemplate;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.template.engine.ConfigurationFactory.TYPE;
import org.mgnl.nicki.template.handler.TemplateHandler;
import org.mgnl.nicki.template.pdf.PdfTemplateRenderer2;
import org.mgnl.nicki.template.pdf.XlsTemplateRenderer;
import org.mgnl.nicki.template.pdf.XlsxTemplateRenderer;
import org.xml.sax.SAXException;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@SuppressWarnings("deprecation")
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
//			IOUtils.copy(executeTemplateAsPdf(templateName, handler.getDataModel()), out);
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

	public InputStream executeTemplateAsPdf2(String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException {
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
		PdfTemplateRenderer2 renderer = new PdfTemplateRenderer2(executeTemplate(templateName, dataModel, DEFAULT_CHARSET), pos);
		renderer.start();
		return pis;
	}

	@Deprecated
	public InputStream executeTemplateAsXls(EngineTemplate template, String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException {
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
	    
		XlsTemplateRenderer renderer = new XlsTemplateRenderer(template, executeTemplate(templateName, dataModel, DEFAULT_CHARSET), pos);
		renderer.start();
		return pis;
	}

	public InputStream executeTemplateAsXlsx(EngineTemplate template, String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException {
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
	    
		XlsxTemplateRenderer renderer = new XlsxTemplateRenderer(template, executeTemplate(templateName, dataModel, DEFAULT_CHARSET), pos);
		renderer.start();
		return pis;
	}

	@Deprecated
	public InputStream executeTemplateAsXls(byte[] master, String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException {
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
	    
		XlsTemplateRenderer renderer = new XlsTemplateRenderer(master, executeTemplate(templateName, dataModel, DEFAULT_CHARSET), pos);
		renderer.start();
		return pis;
	}

	public InputStream executeTemplateAsXlsx(byte[] master, String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException {
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
	    
		XlsxTemplateRenderer renderer = new XlsxTemplateRenderer(master, executeTemplate(templateName, dataModel, DEFAULT_CHARSET), pos);
		renderer.start();
		return pis;
	}

	public InputStream executeTemplateAsCsv(String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException {
		InputStream xslTemplate = this.getClass().getResourceAsStream("/META-INF/nicki/xsl/csv.xsl");
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
		XsltRenderer renderer = new XsltRenderer(executeTemplate(templateName, dataModel, DEFAULT_CHARSET), pos, xslTemplate);
		renderer.start();
		return pis;
	}

	public InputStream executeTemplateAsCsv2(String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException {
		InputStream xslTemplate = this.getClass().getResourceAsStream("/META-INF/nicki/xsl/csv2.xsl");
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
		XsltRenderer renderer = new XsltRenderer(executeTemplate(templateName, dataModel, DEFAULT_CHARSET), pos, xslTemplate);
		renderer.start();
		return pis;
	}

	public static TemplateEngine getInstance() {
		return getInstance(ConfigurationFactory.TYPE.JNDI);
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
						Config.getString(PROPERTY_BASE_DN));
				instances.put(TYPE.JNDI, new TemplateEngine(cfg));
		} else if (type == TYPE.CLASSPATH) {
			Configuration cfg = ConfigurationFactory.getInstance().getConfiguration(ConfigurationFactory.TYPE.CLASSPATH,
						Config.getString(PROPERTY_BASE, "/META-INF/templates"));
			instances.put(TYPE.CLASSPATH, new TemplateEngine(cfg));
		}
	}

}

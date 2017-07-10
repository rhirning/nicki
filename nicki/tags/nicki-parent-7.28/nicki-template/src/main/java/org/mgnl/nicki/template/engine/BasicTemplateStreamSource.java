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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.StreamConverter;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.template.handler.TemplateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BasicTemplateStreamSource {
	private static final Logger LOG = LoggerFactory.getLogger(BasicTemplateStreamSource.class);
	protected static enum TYPE {PDF, XLS, XHTML, STRING};

	Template template;
	Map<String, Object> params;
	private String templatePath;
	private TemplateHandler handler;
	
	public BasicTemplateStreamSource(Template template, NickiContext context, Map<String, Object> params, TYPE type) {
		this.template = template;
		this.params = params;
		// render template
		String parentPath = Config.getProperty("nicki.templates.basedn");
		templatePath = template.getSlashPath(parentPath);
		if (type == TYPE.PDF) {
			if (template.hasPart("pdf")) {
				templatePath += ".pdf.ftl";
			}
		} else if (type == TYPE.XLS && template.hasPart("xls")) {
			templatePath += ".xls.ftl";
		}
		if (StringUtils.contains(templatePath, "_")) {
			templatePath = StringUtils.substringBefore(templatePath, "_");
		}
		handler = TemplateHelper.getTemplateHandler(template);

		handler.setUser((Person) template.getContext().getUser());
		handler.setContext(context);
		handler.setParams(params);
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
			return TemplateEngine.getInstance().executeTemplate(getTemplatePath(), getDataModel(),
					TemplateEngine.DEFAULT_CHARSET);
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		
		return null;
	}

	public InputStream getPdfStream() {
		try {
			return TemplateEngine.getInstance().executeTemplateAsPdf(getTemplatePath(), getDataModel());
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		
		return null;
	}

	public InputStream getPdfStream2() {
		try {
			return TemplateEngine.getInstance().executeTemplateAsPdf2(getTemplatePath(), getDataModel());
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		
		return null;
	}

	public InputStream getXlsStream() {
		try {
			return TemplateEngine.getInstance().executeTemplateAsXls(template, getTemplatePath(), getDataModel());
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		
		return null;
	}
	
	public InputStream getCsVStream() {
		try {
			return convertStream(TemplateEngine.getInstance().executeTemplateAsCsv(getTemplatePath(), getDataModel()),
					Charset.forName(TemplateEngine.DEFAULT_CHARSET), Charset.forName(TemplateEngine.CSV_CHARSET));
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		
		return null;
	}
	
	public InputStream getCsVStream2() {
		try {
			return convertStream(TemplateEngine.getInstance().executeTemplateAsCsv2(getTemplatePath(), getDataModel()),
					Charset.forName(TemplateEngine.DEFAULT_CHARSET), Charset.forName(TemplateEngine.CSV_CHARSET));
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		
		return null;
	}

	public Template getTemplate() {
		return template;
	}

	public Map<String, Object> getParams() {
		return params;
	}
	

	public InputStream convertStream(InputStream inputStream, Charset charsetIn, Charset charsetOut) throws IOException {
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
		StreamConverter converter = new StreamConverter(inputStream, pos, charsetIn, charsetOut);
		converter.start();
		return pis;
	}

}

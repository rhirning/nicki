
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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.StreamConverter;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.template.handler.TemplateHandler;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class BasicTemplateStreamSource.
 */
@Slf4j
public class BasicTemplateStreamSource {
	
	/**
	 * The Enum TYPE.
	 */
	protected static enum TYPE {
/** The pdf. */
PDF, 
 /** The xls. */
 XLS, 
 /** The xhtml. */
 XHTML, 
 /** The string. */
 STRING};

	/** The template. */
	Template template;
	
	/** The params. */
	Map<String, Object> params;
	
	/** The template path. */
	private String templatePath;
	
	/** The handler. */
	private TemplateHandler handler;
	
	/**
	 * Instantiates a new basic template stream source.
	 *
	 * @param template the template
	 * @param context the context
	 * @param params the params
	 * @param type the type
	 */
	public BasicTemplateStreamSource(Template template, NickiContext context, Map<String, Object> params, TYPE type) {
		this.template = template;
		this.params = params;
		// render template
		String parentPath = Config.getString("nicki.templates.basedn");
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

	/**
	 * Gets the data model.
	 *
	 * @return the data model
	 */
	public Map<String, Object> getDataModel() {
		return handler.getDataModel();
	}

	/**
	 * Gets the handler.
	 *
	 * @return the handler
	 */
	public TemplateHandler getHandler() {
		return handler;
	}

	/**
	 * Gets the template path.
	 *
	 * @return the template path
	 */
	public String getTemplatePath() {
		return templatePath;
	}
	
	/**
	 * Gets the string stream.
	 *
	 * @return the string stream
	 */
	public InputStream getStringStream() {
		try {
			return TemplateEngine.getInstance().executeTemplate(getTemplatePath(), getDataModel(),
					TemplateEngine.DEFAULT_CHARSET);
		} catch (Exception e) {
			log.error("Error", e);
		}
		
		return null;
	}

	/**
	 * Gets the pdf stream 2.
	 *
	 * @return the pdf stream 2
	 */
	public InputStream getPdfStream2() {
		try {
			return TemplateEngine.getInstance().executeTemplateAsPdf2(getTemplatePath(), getDataModel());
		} catch (Exception e) {
			log.error("Error", e);
		}
		
		return null;
	}

	/**
	 * Gets the xlsx stream.
	 *
	 * @return the xlsx stream
	 */
	public InputStream getXlsxStream() {
		try {
			return TemplateEngine.getInstance().executeTemplateAsXlsx(template, getTemplatePath(), getDataModel());
		} catch (Exception e) {
			log.error("Error", e);
		}
		
		return null;
	}

	/**
	 * Gets the xls stream.
	 *
	 * @return the xls stream
	 */
	@Deprecated
	public InputStream getXlsStream() {
		try {
			return TemplateEngine.getInstance().executeTemplateAsXls(template, getTemplatePath(), getDataModel());
		} catch (Exception e) {
			log.error("Error", e);
		}
		
		return null;
	}
	
	/**
	 * Gets the cs V stream.
	 *
	 * @return the cs V stream
	 */
	public InputStream getCsVStream() {
		try {
			return convertStream(TemplateEngine.getInstance().executeTemplateAsCsv(getTemplatePath(), getDataModel()),
					Charset.forName(TemplateEngine.DEFAULT_CHARSET), Charset.forName(TemplateEngine.CSV_CHARSET));
		} catch (Exception e) {
			log.error("Error", e);
		}
		
		return null;
	}
	
	/**
	 * Gets the cs V stream 2.
	 *
	 * @return the cs V stream 2
	 */
	public InputStream getCsVStream2() {
		try {
			return convertStream(TemplateEngine.getInstance().executeTemplateAsCsv2(getTemplatePath(), getDataModel()),
					Charset.forName(TemplateEngine.DEFAULT_CHARSET), Charset.forName(TemplateEngine.CSV_CHARSET));
		} catch (Exception e) {
			log.error("Error", e);
		}
		
		return null;
	}

	/**
	 * Gets the template.
	 *
	 * @return the template
	 */
	public Template getTemplate() {
		return template;
	}

	/**
	 * Gets the params.
	 *
	 * @return the params
	 */
	public Map<String, Object> getParams() {
		return params;
	}
	

	/**
	 * Convert stream.
	 *
	 * @param inputStream the input stream
	 * @param charsetIn the charset in
	 * @param charsetOut the charset out
	 * @return the input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public InputStream convertStream(InputStream inputStream, Charset charsetIn, Charset charsetOut) throws IOException {
	    PipedOutputStream pos = new PipedOutputStream();
	    PipedInputStream pis = new PipedInputStream(pos);
		StreamConverter converter = new StreamConverter(inputStream, pos, charsetIn, charsetOut);
		converter.start();
		return pis;
	}

}

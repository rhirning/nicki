package org.mgnl.nicki.template.engine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.XsltHelper;
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
	public static final String DEFAULT_BASE_DN = "ou=templates,o=utopia";
	
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
			getInstance().executeTemplateAsTxt(templateName, handler.getDataModel(), System.out);
			break;
		case CSV:
			getInstance().executeTemplateAsCsv(templateName, handler.getDataModel(), System.out);
			break;
		case PDF:
			getInstance().executeTemplateAsPdf(templateName, handler.getDataModel(), System.out);
			break;

		default:
			break;
		}

	}


	public String executeTemplate(String templateName,
			Map<String, Object> dataModel) throws IOException,
			TemplateException, InvalidPrincipalException {

		Configuration cfg = ConfigurationFactory.getInstance()
				.getConfiguration(
						Config.getProperty(PROPERTY_BASE_DN, DEFAULT_BASE_DN));

		Template template = cfg.getTemplate(templateName);

		StringWriter out = new StringWriter();
		template.process(dataModel, out);
		out.flush();
		return out.toString();
	}

	public void executeTemplateAsTxt(String templateName,
			Map<String, Object> dataModel, OutputStream out) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {
		String templateResult = executeTemplate(templateName, dataModel);
		out.write(templateResult.getBytes("UTF-8"));
	}

	public void executeTemplateAsPdf(String templateName,
			Map<String, Object> dataModel, OutputStream out) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {
		String templateResult = executeTemplate(templateName, dataModel);
		PdfTemplateRenderer.getInstance().render(new ByteArrayInputStream(templateResult.getBytes("UTF-8")), out);
	}

	public void executeTemplateAsCsv(String templateName,
			Map<String, Object> dataModel, OutputStream out) throws IOException,
			TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {
		String templateResult = executeTemplate(templateName, dataModel);
		InputStream in = new ByteArrayInputStream(templateResult.getBytes("UTF-8"));
		InputStream xslTemplate = this.getClass().getResourceAsStream("/META-INF/nicki/xsl/csv.xsl");
		XsltHelper.xsl(in, out, xslTemplate);
	}

	public static TemplateEngine getInstance() {
		return instance;
	}

}

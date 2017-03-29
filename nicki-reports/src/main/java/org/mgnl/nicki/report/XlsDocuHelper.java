package org.mgnl.nicki.report;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.dynamic.objects.objects.EngineTemplate;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.template.engine.ConfigurationFactory;
import org.mgnl.nicki.template.engine.SimpleTemplate;
import org.mgnl.nicki.template.engine.ConfigurationFactory.TYPE;
import org.mgnl.nicki.template.engine.TemplateEngine;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public class XlsDocuHelper {

	public static InputStream generate(TYPE type, String templatePath, Map<String, Object> dataModel) throws IOException, TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {
		if (type == TYPE.JNDI) {
			return generateJNDI(templatePath, dataModel);
		} else if (type == TYPE.CLASSPATH) {
			return generateClasspath(templatePath, dataModel);
		} else {
			return null;
		}
	}

	private static InputStream generateJNDI(String templatePath, Map<String, Object> dataModel) throws IOException, TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {
		StringBuilder sb = new StringBuilder();
		String parts[] = StringUtils.split(templatePath, "/");
		for (int i = parts.length -1 ; i >= 0; i--) {
			sb.append("ou=").append(parts[i]).append(",");
		}
		sb.append(Config.getProperty("nicki.templates.basedn"));
		
		String templateDn = sb.toString();
		Template template = AppContext.getSystemContext().loadObject(Template.class, templateDn);
		TemplateEngine engine = TemplateEngine.getInstance(TYPE.JNDI);
		return engine.executeTemplateAsXls(template, templatePath + ".ftl", dataModel);
	}
	private static InputStream generateClasspath(String templatePath, Map<String, Object> dataModel) throws IOException, TemplateException, InvalidPrincipalException, ParserConfigurationException, SAXException, DocumentException {

		String base = "/META-INF/templates";
		Configuration cfg = ConfigurationFactory.getInstance().getConfiguration(ConfigurationFactory.TYPE.CLASSPATH,
				base);
		TemplateEngine engine = new TemplateEngine(cfg);
				
		EngineTemplate template = getTemplate(base, templatePath + ".ftl");
		return engine.executeTemplateAsXls(template, templatePath + ".ftl", dataModel);
	}
	

	public static EngineTemplate getTemplate(String base, String path) {
		StringBuilder sb = new StringBuilder();
		sb.append(base).append("/").append(path);
		
		String resourcePath = sb.toString();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(SimpleTemplate.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			try (Reader reader = new InputStreamReader(XlsDocuHelper.class.getResourceAsStream(resourcePath), "iso-8859-1")) {
				return (SimpleTemplate) jaxbUnmarshaller.unmarshal(reader);
			}
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}

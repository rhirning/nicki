import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.template.engine.ConfigurationFactory;
import org.mgnl.nicki.template.engine.TemplateEngine;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public class TestXLS {

	public static void main(String[] args) {
		Map<String, Object> dataModel = new HashMap<>();
		dataModel.put("user", "Ralf");
		generate("document.ftl", dataModel);
	}
	
	public static void generate(String template, Map<String, Object> dataModel) {
		Configuration cfg = ConfigurationFactory.getInstance().getConfiguration(ConfigurationFactory.TYPE.CLASSPATH,
				"/META-INF/templates");
		TemplateEngine engine = new TemplateEngine(cfg);
		

			try {
				FileOutputStream out = new FileOutputStream("target/document.xls");
				IOUtils.copy(engine.executeTemplateAsXls((byte[])null, template, dataModel), out);
			} catch (IOException | TemplateException | InvalidPrincipalException | ParserConfigurationException
					| SAXException | DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}

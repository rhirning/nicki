import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.mgnl.nicki.xls.engine.XlsEngine;
import org.mgnl.nicki.xls.template.XlsTemplate;

public class CreateXLS {

	public static void main(String[] args) {
		InputStream in = CreateXLS.class.getResourceAsStream("/document.xml");
		XlsEngine engine = new XlsEngine();
		try {
			FileOutputStream os = new FileOutputStream("target/output.xls");
			engine.render(null, new XlsTemplate(in), os);
		} catch (IOException | JAXBException e) {
			e.printStackTrace();
		}
	}

}

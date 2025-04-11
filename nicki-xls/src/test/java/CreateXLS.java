/*-
 * #%L
 * nicki-xls
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.xml.bind.JAXBException;

import org.mgnl.nicki.xls.engine.XlsEngine;
import org.mgnl.nicki.xls.template.XlsTemplate;

// TODO: Auto-generated Javadoc
/**
 * The Class CreateXLS.
 */
public class CreateXLS {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		InputStream in = CreateXLS.class.getResourceAsStream("/document.xml");
		XlsEngine engine = new XlsEngine();
		try {
			FileOutputStream os = new FileOutputStream("target/output.xlsx");
			engine.renderXlsx(null, new XlsTemplate(in), os);
		} catch (IOException | JAXBException e) {
			e.printStackTrace();
		}
	}

}

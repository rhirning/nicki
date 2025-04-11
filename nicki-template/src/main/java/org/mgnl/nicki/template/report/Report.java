
package org.mgnl.nicki.template.report;

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


import java.io.FileOutputStream;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.template.engine.TemplateEngine;


// TODO: Auto-generated Javadoc
/**
 * The Class Report.
 */
public class Report {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	/*
	 * 4 Supported arguments user password template type
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("Wrong number of arguments");
			return;
		}
		
		String userName = args[0];
		String password = DataHelper.getPassword(args[1]);
		String templateName = args[2];
		String outputType = "txt";
		if (args.length > 3) {
			outputType = args[3];
		}
		
		TemplateEngine.getInstance().execute(userName, password, templateName, outputType, System.out);
	}
	
	/**
	 * To file.
	 *
	 * @param userName the user name
	 * @param password the password
	 * @param templateName the template name
	 * @param outputType the output type
	 * @param fileName the file name
	 * @throws Exception the exception
	 */
	public static void toFile(String userName, String password, String templateName,
			String outputType, String fileName) throws Exception {
		FileOutputStream out = new FileOutputStream(fileName);
		TemplateEngine.getInstance().execute(userName, DataHelper.getPassword(password), templateName, outputType, out);
		
	}
}

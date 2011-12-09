/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.template.report;

import java.io.FileOutputStream;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.template.engine.TemplateEngine;


public class Report {

	/*
	 * 4 Supported arguments user password template type
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			throw new Exception("Wrong number of arguments");
		}
		
		if (args.length < 3) {
			throw new Exception("Wrong number of arguments");
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
	
	public static void toFile(String userName, String password, String templateName,
			String outputType, String fileName) throws Exception {
		FileOutputStream out = new FileOutputStream(fileName);
		TemplateEngine.getInstance().execute(userName, DataHelper.getPassword(password), templateName, outputType, out);
		
	}
}

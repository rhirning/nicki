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
	
	public static void toFile(String userName, String password, String templateName,
			String outputType, String fileName) throws Exception {
		FileOutputStream out = new FileOutputStream(fileName);
		TemplateEngine.getInstance().execute(userName, DataHelper.getPassword(password), templateName, outputType, out);
		
	}
}

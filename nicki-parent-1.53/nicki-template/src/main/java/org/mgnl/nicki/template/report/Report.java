/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
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

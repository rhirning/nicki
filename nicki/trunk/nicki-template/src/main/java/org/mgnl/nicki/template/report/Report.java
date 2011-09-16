package org.mgnl.nicki.template.report;

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
		String password = args[1];
		String templateName = args[2];
		String outputType = "txt";
		if (args.length > 3) {
			outputType = args[3];
		}
		
		TemplateEngine.getInstance().execute(userName, password, templateName, outputType, System.out);
	}
}

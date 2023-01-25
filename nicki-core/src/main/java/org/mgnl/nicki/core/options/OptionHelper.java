package org.mgnl.nicki.core.options;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.mgnl.nicki.core.helper.BeanUtilsHelper;
import org.mgnl.nicki.core.helper.DataHelper;

public class OptionHelper {

	public static <T> void handleOptions(T bean, String[] args) {
		Options options = getOptions(bean);
		// define parser
	    CommandLine cmd;
	    CommandLineParser parser = new DefaultParser();
	    HelpFormatter helper = new HelpFormatter();

        try {
			cmd = parser.parse(options, args);
			if (bean.getClass().getAnnotationsByType(CommandLineOption.class) != null) {
				for (CommandLineOption commandLineOption : bean.getClass().getAnnotationsByType(CommandLineOption.class)) {
					if (cmd.hasOption(commandLineOption.option())) {
						if (commandLineOption.bool()) {
							BeanUtilsHelper.setValue(bean, commandLineOption.attribute(), DataHelper.booleanOf(cmd.getOptionValue(commandLineOption.option())));
						} else {
							BeanUtilsHelper.setValue(bean, commandLineOption.attribute(), cmd.getOptionValue(commandLineOption.option()));
						}
					}
				}
			}
		} catch (ParseException e) {
	        System.out.println(e.getMessage());
	        helper.printHelp("Usage:", options);
	        System.exit(0);
		}		
	}

	private static <T> Options getOptions(T bean) {
		Options options = new Options();

		if (bean.getClass().getAnnotationsByType(CommandLineOption.class) != null) {
			for (CommandLineOption commandLineOption : bean.getClass().getAnnotationsByType(CommandLineOption.class)) {
				Option option = Option.builder(
						commandLineOption.option())
						.longOpt(commandLineOption.longOpt())
						.argName(commandLineOption.argName())
						.hasArg(commandLineOption.hasArg())
						.desc(commandLineOption.desc()).build();
				options.addOption(option);

			}
		}

		return options;
	}

}

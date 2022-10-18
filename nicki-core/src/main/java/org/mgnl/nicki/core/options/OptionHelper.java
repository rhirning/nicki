package org.mgnl.nicki.core.options;

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

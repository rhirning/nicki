package org.mgnl.nicki.core.options;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)  
@Repeatable(CommandLineOptions.class)
public @interface CommandLineOption {

	String option();
	
	String attribute();

	String longOpt();

	String argName() default "";

	boolean hasArg() default false;

	String desc();

	boolean bool() default false;

}

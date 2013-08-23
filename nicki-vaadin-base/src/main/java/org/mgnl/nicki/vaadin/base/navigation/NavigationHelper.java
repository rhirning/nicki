package org.mgnl.nicki.vaadin.base.navigation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigationHelper {
	private static final Logger LOG = LoggerFactory.getLogger(NavigationHelper.class);

	public static void executeCommands(Object object, NavigationCommand command) {
		List<NavigationCommandEntry> toBeRemoved = new ArrayList<NavigationCommandEntry>();
		for (NavigationCommandEntry commandEntry : command.getEntries()) {
			if (canExecute(object, commandEntry)) {
				try {
					execute(object, commandEntry);
					toBeRemoved.add(commandEntry);
				} catch (Exception e) {
					LOG.error("Error", e);
				}
				continue;
			}
		}
		
		for (NavigationCommandEntry navigationCommandEntry : toBeRemoved) {
			command.remove(navigationCommandEntry);
		}
	}

	private static boolean canExecute(Object object,
			NavigationCommandEntry commandEntry) {
		for (Method method : object.getClass().getDeclaredMethods()) {
			Command command = method.getAnnotation(Command.class);
			if (command != null && StringUtils.equals(command.name(), commandEntry.getCommandName())) {
				return true;
			}
		}
		return false;
	}

	private static void execute(Object object,
			NavigationCommandEntry commandEntry) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		for (Method method : object.getClass().getDeclaredMethods()) {
			Command command = method.getAnnotation(Command.class);
			if (command != null && StringUtils.equals(command.name(), commandEntry.getCommandName())) {
				method.invoke(object, commandEntry.getData());
			}
		}
		
	}

}

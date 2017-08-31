/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

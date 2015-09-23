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

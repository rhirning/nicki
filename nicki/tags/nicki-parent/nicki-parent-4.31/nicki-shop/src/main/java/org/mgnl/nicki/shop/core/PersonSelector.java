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
package org.mgnl.nicki.shop.core;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.vaadin.base.command.SelectPersonCommand;

import com.vaadin.ui.Component;

public interface PersonSelector extends Component {

	void init(NickiContext context, boolean useInternExtern, boolean useActiveInactive, SelectPersonCommand selectPersonCommand);

}

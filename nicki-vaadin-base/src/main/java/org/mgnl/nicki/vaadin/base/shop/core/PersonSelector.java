/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.shop.core;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.vaadin.base.command.SelectPersonCommand;

import com.vaadin.ui.ComponentContainer;

public interface PersonSelector extends ComponentContainer {

	void init(NickiContext context, boolean useActiveInactive, SelectPersonCommand selectPersonCommand);

}

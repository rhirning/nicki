package org.mgnl.nicki.vaadin.base.shop.core;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.vaadin.base.command.SelectPersonCommand;

import com.vaadin.ui.ComponentContainer;

public interface PersonSelector extends ComponentContainer {

	void init(NickiContext context, boolean useActiveInactive, SelectPersonCommand selectPersonCommand);

}

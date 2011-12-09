/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.editor;

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.ui.Component;

public interface ClassEditor extends Component {
	public void setDynamicObject(NickiTreeEditor nickiEditor, DynamicObject dynamicObject);

}

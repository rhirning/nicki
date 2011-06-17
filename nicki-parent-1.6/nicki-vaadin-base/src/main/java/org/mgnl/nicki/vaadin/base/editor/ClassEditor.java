package org.mgnl.nicki.vaadin.base.editor;

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.ui.Component;

public interface ClassEditor extends Component {
	public void setDynamicObject(NickiEditor nickiEditor, DynamicObject dynamicObject);

}

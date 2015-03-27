package org.mgnl.nicki.vaadin.base.dialog;

import com.vaadin.ui.Component;

public interface View extends Component{

	void init();

	boolean isModified();

}

package org.mgnl.nicki.vaadin.base.components;

import org.mgnl.nicki.vaadin.base.command.Command;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class DialogBase extends Window {
	private static final long serialVersionUID = -3504431507552994635L;
	
	private VerticalLayout layout;

	public DialogBase(Command command) {
		setCaption(command.getTitle());
		init();

        layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(false);
        layout.setSpacing(false);
        // make it undefined for auto-sizing window
//        layout.setSizeUndefined();
        setContent(layout);
		
	}
	


	public DialogBase(String title) {
		setCaption(title);
		init();
	}



	private void init() {

        layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(false);
        layout.setSpacing(false);
        // make it undefined for auto-sizing window
//        layout.setSizeUndefined();
        setContent(layout);
		
	}

	public void setCompositionRoot(Component component) {
		layout.addComponent(component);
	}


}

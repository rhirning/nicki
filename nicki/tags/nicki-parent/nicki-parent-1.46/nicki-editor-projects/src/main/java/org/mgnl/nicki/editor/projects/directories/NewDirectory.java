/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.editor.projects.directories;

import org.mgnl.nicki.core.i18n.I18n;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class NewDirectory extends CustomComponent {

	@AutoGenerated
	private AbsoluteLayout mainLayout;

	@AutoGenerated
	private Button closeButton;

	@AutoGenerated
	private Button createButton;

	@AutoGenerated
	private TextField directory;

	@AutoGenerated
	private Label headline;

	NewDirectoryHandler newDirectoryHandler;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public NewDirectory(NewDirectoryHandler handler) {
		this.newDirectoryHandler = handler;
		buildMainLayout();
		setCompositionRoot(mainLayout);

		directory.focus();
		applyI18n();
		
		createButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				newDirectoryHandler.createNewDirectory((String) directory.getValue());
			}
		});
		
		closeButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				newDirectoryHandler.closeNewDirectoryWindow();
			}
		});
	}

	private void applyI18n() {
		headline.setValue(I18n.getText("nicki.editor.project.newdirectory.headline"));
		createButton.setCaption(I18n.getText("nicki.editor.project.newdirectory.button.create"));
		closeButton.setCaption(I18n.getText("nicki.editor.project.newdirectory.button.close"));
	}
		
	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// headline
		headline = new Label();
		headline.setWidth("400px");
		headline.setHeight("-1px");
		headline.setValue("Headline");
		headline.setImmediate(false);
		mainLayout.addComponent(headline, "top:20.0px;left:20.0px;");
		
		// directory
		directory = new TextField();
		directory.setWidth("-1px");
		directory.setHeight("-1px");
		directory.setImmediate(false);
		mainLayout.addComponent(directory, "top:60.0px;left:20.0px;");
		
		// createButton
		createButton = new Button();
		createButton.setWidth("-1px");
		createButton.setHeight("-1px");
		createButton.setCaption("Create");
		createButton.setImmediate(true);
		mainLayout.addComponent(createButton, "top:100.0px;left:20.0px;");
		
		// closeButton
		closeButton = new Button();
		closeButton.setWidth("-1px");
		closeButton.setHeight("-1px");
		closeButton.setCaption("Close");
		closeButton.setImmediate(true);
		mainLayout.addComponent(closeButton, "top:100.0px;left:100.0px;");
		
		return mainLayout;
	}

}
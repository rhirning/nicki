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
package org.mgnl.nicki.editor.templates;


import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.editor.ClassEditor;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class TemplateConfig extends CustomComponent implements ClassEditor {

	private AbsoluteLayout mainLayout;
	
	private Component configDialog;
	private Template template;
	private Button previewButton;
	private Button htmlPreviewButton;
	private Link csvLink;
	private Link pdfLink;
	private NickiTreeEditor editor;
	private Map<String, Object> params = new HashMap<String, Object>();

	
	public TemplateConfig() {
	}

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public void setDynamicObject(NickiTreeEditor nickiEditor, DynamicObject dynamicObject) {
		this.editor = nickiEditor;
		this.template = (Template) dynamicObject;
		params = new HashMap<String, Object>();
		buildEditor();
		setCompositionRoot(mainLayout);
		initI18n();
		
		previewButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				try {
					preview();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		htmlPreviewButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				try {
					htmlPreview();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		pdfLink.setCaption("PDF");
		pdfLink.setTargetName("_blank");
		PdfStreamSource pdfStreamSource = new PdfStreamSource(template, template.getContext(), params);
		pdfLink.setResource(new LinkResource(pdfStreamSource, template.getName() + ".pdf",
				nickiEditor.getApplication(), "application/pdf"));

		csvLink.setCaption("CSV");
		csvLink.setTargetName("_blank");
		CsvStreamSource csvStreamSource = new CsvStreamSource(template, template.getContext(), params);
		csvLink.setResource(new LinkResource(csvStreamSource, template.getName() + ".csv",
				nickiEditor.getApplication(), "text/comma-separated-values"));
		
		paramsChanged();
	}
	
	private void initI18n() {
		previewButton.setCaption(I18n.getText(editor.getMessageKeyBase() + ".config.button.preview"));
		htmlPreviewButton.setCaption(I18n.getText(editor.getMessageKeyBase() + ".config.button.htmlpreview"));
		pdfLink.setCaption(I18n.getText(editor.getMessageKeyBase() + ".config.link.pdf"));
		csvLink.setCaption(I18n.getText(editor.getMessageKeyBase() + ".config.link.csv"));
	}

	protected void close() {
		getWindow().getParent().removeWindow(getWindow());
	}

	protected void preview() throws DynamicObjectException, NamingException {
		if (isComplete()) {
			PreviewTemplate preview = new PreviewTemplate(editor.getNickiContext(), editor.getMessageKeyBase(), params);
			preview.execute(editor.getWindow(), template);
		}
	}
	

	protected void htmlPreview() throws DynamicObjectException, NamingException {
		if (isComplete()) {
			HtmlPreviewTemplate preview = new HtmlPreviewTemplate(editor.getNickiContext(), editor.getMessageKeyBase(), params);
			preview.execute(editor.getWindow(), template);
		}
	}

	protected boolean isComplete() {
		if (!GuiTemplateHelper.isComplete(template, params)) {
			getWindow().showNotification(I18n.getText(editor.getMessageKeyBase()+ ".error.params.incomplete"));
			return false;
		} else {
			return true;
		}
		
	}
	
	private AbsoluteLayout buildEditor() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setHeight("100%");
		mainLayout.addComponent(verticalLayout, "top:20.0px;left:20.0px;");

		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		configDialog = GuiTemplateHelper.getConfigDialog(template, params, this);
		configDialog.setWidth("400px");
		configDialog.setHeight("400px");
		verticalLayout.addComponent(configDialog);

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSpacing(true);
		horizontalLayout.setHeight(40, UNITS_PIXELS);
		verticalLayout.addComponent(horizontalLayout);
		
		previewButton = new Button();
		previewButton.setWidth("-1px");
		previewButton.setHeight("-1px");
		previewButton.setCaption("Vorschau");
		previewButton.setImmediate(true);
		horizontalLayout.addComponent(previewButton);
		
		htmlPreviewButton = new Button();
		htmlPreviewButton.setWidth("-1px");
		htmlPreviewButton.setHeight("-1px");
		htmlPreviewButton.setCaption("HTML Vorschau");
		htmlPreviewButton.setImmediate(true);
		horizontalLayout.addComponent(htmlPreviewButton);
		
		pdfLink = new Link();
		horizontalLayout.addComponent(pdfLink);
		
		csvLink = new Link();
		horizontalLayout.addComponent(csvLink);
		
		return mainLayout;
	}

	public void paramsChanged() {
		if (GuiTemplateHelper.isComplete(template, params)) {
			pdfLink.setEnabled(true);
			csvLink.setEnabled(true);
		} else {
			pdfLink.setEnabled(false);
			csvLink.setEnabled(false);
		}		
	}

}

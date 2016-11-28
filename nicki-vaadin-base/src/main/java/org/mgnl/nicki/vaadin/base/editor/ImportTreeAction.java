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
package org.mgnl.nicki.vaadin.base.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.util.XMLImporter;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window;

public class ImportTreeAction extends BaseTreeAction implements  Upload.SucceededListener,
		Upload.FailedListener, Upload.Receiver {

	private static final long serialVersionUID = 8567493886374796976L;


	private AbsoluteLayout mainLayout;
	private HorizontalLayout horizontalLayout_1;
	private Upload importComponent;
	private ByteArrayOutputStream outputStream;
	private Window previewWindow;
	private NickiContext context;
	private String i18nBase;
	private TreeData dynamicObject;
	private TreeEditor treeEditor;


	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	public ImportTreeAction(TreeEditor treeEditor, Class<? extends DynamicObject> classDefinition,
			String name, String i18nBase) {
		super(classDefinition, name);
		this.treeEditor = treeEditor;
		this.context = treeEditor.getNickiContext();
		this.i18nBase = i18nBase;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	public void execute(TreeData dynamicObject) {
		this.dynamicObject = dynamicObject;
		
		importComponent.setCaption(I18n.getText(i18nBase + ".import.caption"));
		importComponent.setButtonCaption(I18n.getText(i18nBase + ".import.button.caption"));
		importComponent.addSucceededListener((Upload.SucceededListener) this);
		importComponent.addFailedListener((Upload.FailedListener) this);

		if (null != this.getParent()) {
			this.setParent(null);
		}
		previewWindow = new Window(I18n.getText(i18nBase + ".import.window.title"), this);
		previewWindow.setModal(true);
		previewWindow.setWidth(1024, Unit.PIXELS);
		previewWindow.setHeight(520, Unit.PIXELS);
		UI.getCurrent().addWindow(previewWindow);
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// horizontalLayout_1
		horizontalLayout_1 = buildHorizontalLayout_1();
		mainLayout.addComponent(horizontalLayout_1, "top:0.0px;left:0.0px;");
		
		return mainLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_1() {
		// common part: create layout
		horizontalLayout_1 = new HorizontalLayout();
		horizontalLayout_1.setImmediate(false);
		horizontalLayout_1.setWidth("-1px");
		horizontalLayout_1.setHeight("-1px");
		horizontalLayout_1.setMargin(true);
		horizontalLayout_1.setSpacing(true);
		
		// exportElement
		importComponent = new Upload("Importieren", this);
		importComponent.setCaption("Importieren");
		importComponent.setImmediate(false);
		importComponent.setWidth("-1px");
		importComponent.setHeight("-1px");
		horizontalLayout_1.addComponent(importComponent);
		
		return horizontalLayout_1;
	}

	public void close() {
		UI.getCurrent().removeWindow(previewWindow);
	}

	public OutputStream receiveUpload(String filename, String mimeType) {
		this.outputStream = new ByteArrayOutputStream();
		return this.outputStream;
	}

	public void uploadFailed(FailedEvent event) {
		this.outputStream = null;
		Notification.show("Import failed", "Importing "
                + event.getFilename() + " of type '"
                + event.getMIMEType() + "' failed.", Notification.Type.ERROR_MESSAGE);
	}

	public void uploadSucceeded(SucceededEvent event) {
		try {
			XMLImporter importer = new XMLImporter(context, dynamicObject.getPath(), new ByteArrayInputStream(outputStream.toByteArray()));
			importer.create();
			treeEditor.refresh(dynamicObject);
			Notification.show("Import successful", Notification.Type.HUMANIZED_MESSAGE);
		} catch (Exception e) {
			Notification.show("Import failed", "Importing "
	                + event.getFilename() + " of type '"
	                + event.getMIMEType() + "' failed.", Notification.Type.ERROR_MESSAGE);
		}
	}

}

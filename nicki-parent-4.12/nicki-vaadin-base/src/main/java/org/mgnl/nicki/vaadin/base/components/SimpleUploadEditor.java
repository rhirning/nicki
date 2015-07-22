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
package org.mgnl.nicki.vaadin.base.components;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.LinkResource;
import org.mgnl.nicki.vaadin.base.editor.PropertyStreamSource;

import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

@SuppressWarnings("serial")
public class SimpleUploadEditor extends CustomComponent implements Receiver, SucceededListener, FailedListener {

	private HorizontalLayout fileLayout;
	private Link link;
	private Upload upload;
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();
	private DynamicObject dynamicObject;
	private String attributeName;
	private String i18nBase;


	public SimpleUploadEditor(String i18nBase, DynamicObject dynamicObject, String attributeName, String filename, String mimeType) {
		this.i18nBase = i18nBase;
		this.dynamicObject = dynamicObject;
		this.attributeName = attributeName;
		// editor
		HorizontalLayout layout = buildFileLayout();

		if (dynamicObject.get(attributeName) == null) {
			link.setVisible(false);
		}
		// "Download"
		
		link.setCaption(I18n.getText(i18nBase + ".link.caption"));
		link.setTargetName("_blank");
		StreamSource streamSource = new PropertyStreamSource(this.dynamicObject, this.attributeName);
		link.setResource(new LinkResource(streamSource, filename, mimeType));

		// upload.setButtonCaption(I18n.getText(i18nBase + ".upload.caption"));
		upload.setReceiver(this);
		upload.addSucceededListener(this);
		upload.addFailedListener(this);
		
		setCompositionRoot(layout);
	}
	

	private HorizontalLayout buildFileLayout() {
		// common part: create layout
		fileLayout = new HorizontalLayout();
		fileLayout.setImmediate(false);
		fileLayout.setWidth("-1px");
		fileLayout.setHeight("-1px");
		fileLayout.setMargin(true);
		fileLayout.setSpacing(true);
		
		// link
		link = new Link();
		link.setCaption("Link");
		link.setImmediate(false);
		link.setWidth("-1px");
		link.setHeight("-1px");
		fileLayout.addComponent(link);
		
		// upload_1
		upload = new Upload();
		upload.setImmediate(true);
		upload.setWidth("-1px");
		upload.setHeight("-1px");
		fileLayout.addComponent(upload);
		
		return fileLayout;
	}


	@Override
	public void uploadFailed(FailedEvent event) {
		// "Fehler beim Hochladendes Files"
		Notification.show(I18n.getText(i18nBase + ".upload.fail"), Type.ERROR_MESSAGE);
	}


	@Override
	public void uploadSucceeded(SucceededEvent event) {
		dynamicObject.put(attributeName, bos.toByteArray());
		// "File erfolgreich hochgeladen: " + bos.size() + " Bytes. Bitte Speichern"
		Notification.show(I18n.getText(i18nBase + ".upload.success", "" + bos.size()));
		link.setEnabled(true);
	}


	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		bos.reset();
		return bos;
	}

}

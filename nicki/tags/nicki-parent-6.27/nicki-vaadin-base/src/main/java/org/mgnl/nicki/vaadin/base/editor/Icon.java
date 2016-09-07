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

import java.io.Serializable;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;


@SuppressWarnings("serial")
public final class Icon implements Serializable {
	public static final Icon ARROW_DOWN = new Icon("../runo/icons/16/arrow-down.png");
	public static final Icon ARROW_LEFT = new Icon("../runo/icons/16/arrow-left.png");
	public static final Icon ARROW_RIGHT = new Icon("../runo/icons/16/arrow-right.png");
	public static final Icon ARROW_UP = new Icon("../runo/icons/16/arrow-up.png");
	public static final Icon ATTENTION = new Icon("../runo/icons/16/attention.png");
	public static final Icon CALENDAR = new Icon("../runo/icons/16/calendar.png");
	public static final Icon CANCEL = new Icon("../runo/icons/16/cancel.png");
	public static final Icon DOCUMENT = new Icon("../runo/icons/16/document.png");
	public static final Icon DOCUMENT_ADD = new Icon("../runo/icons/16/document-add.png");
	public static final Icon DOCUMENT_DELETE = new Icon("../runo/icons/16/document-delete.png");
	public static final Icon DOCUMENT_DOC = new Icon("../runo/icons/16/document-doc.png");
	public static final Icon DOCUMENT_IMAGE = new Icon("../runo/icons/16/document-image.png");
	public static final Icon DOCUMENT_PDF = new Icon("../runo/icons/16/document-pdf.png");
	public static final Icon DOCUMENT_PPT = new Icon("../runo/icons/16/document-ppt.png");
	public static final Icon DOCUMENT_TXT = new Icon("../runo/icons/16/document-txt.png");
	public static final Icon DOCUMENT_WEB = new Icon("../runo/icons/16/document-web.png");
	public static final Icon DOCUMENT_XSL = new Icon("../runo/icons/16/document-xsl.png");
	public static final Icon EMAIL = new Icon("../runo/icons/16/email.png");
	public static final Icon EMAIL_REPLY = new Icon("../runo/icons/16/email-reply.png");
	public static final Icon EMAIL_SEND = new Icon("../runo/icons/16/email-send.png");
	public static final Icon FOLDER = new Icon("../runo/icons/16/folder.png");
	public static final Icon FOLDER_ADD = new Icon("../runo/icons/16/folder-add.png");
	public static final Icon FOLDER_DELETE = new Icon("../runo/icons/16/folder-delete.png");
	public static final Icon GLOBE = new Icon("../runo/icons/16/globe.png");
	public static final Icon HELP = new Icon("../runo/icons/16/help.png");
	public static final Icon LOCK = new Icon("../runo/icons/16/lock.png");
	public static final Icon MINUS = new Icon("../icons/16/minus.png");
	public static final Icon MINUS_SELECTED = new Icon("../icons/16/minus_selected.png");
	public static final Icon NOTE = new Icon("../runo/icons/16/note.png");
	public static final Icon OK = new Icon("../runo/icons/16/ok.png");
	public static final Icon PLUS = new Icon("../icons/16/plus.png");
	public static final Icon PLUS_SELECTED = new Icon("../icons/16/plus_selected.png");
	public static final Icon RELOAD = new Icon("../runo/icons/16/reload.png");
	public static final Icon SETTINGS = new Icon("../runo/icons/16/settings.png");
	public static final Icon TRASH = new Icon("../runo/icons/16/trash.png");
	public static final Icon TRASH_FULL = new Icon("../runo/icons/16/trash-full.png");
	public static final Icon USER = new Icon("../runo/icons/16/user.png");
	public static final Icon USERS = new Icon("../runo/icons/16/users.png");
	
	private String resourcePath;
	
	private Icon(String resourcePath) {
		this.setResourcePath(resourcePath);
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public String getResourcePath() {
		return resourcePath;
	}
	
	public Resource getResource() {
		return new ThemeResource(getResourcePath());
	}
}

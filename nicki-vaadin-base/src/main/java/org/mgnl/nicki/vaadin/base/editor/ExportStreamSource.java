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
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.util.XMLBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.StreamResource.StreamSource;


public class ExportStreamSource implements StreamSource{
	private static final Logger LOG = LoggerFactory.getLogger(ExportStreamSource.class);

	private static final long serialVersionUID = -8068031351212191141L;
	private TreeData dynamicObject;
	private NickiContext context;
	private boolean selfOnly;
	
	public ExportStreamSource(TreeData dynamicObject, NickiContext context, boolean selfOnly) {
		this.dynamicObject = dynamicObject;
		this.context = context;
		this.selfOnly = selfOnly;
	}

	public InputStream getStream() {
		try {
			XMLBuilder builder = new XMLBuilder(this.context, this.dynamicObject.getPath(), selfOnly);
			Writer writer = new StringWriter();
			builder.write(writer);
			
			return new ByteArrayInputStream(writer.toString().getBytes("UTF-8"));
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		return null;
	}

}

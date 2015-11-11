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
package org.mgnl.nicki.editor.log4j;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.mgnl.nicki.core.helper.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.data.util.BeanItemContainer;

public class BeanItemContainerRenderer extends Thread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(BeanItemContainerRenderer.class);
	BeanItemContainer<NameValue> container;
	OutputStream out;

	public BeanItemContainerRenderer(BeanItemContainer<NameValue> container, OutputStream out) {
		super();
		this.container = container;
		this.out = out;
	}
	public void run() {
		try {
			for (NameValue entry : container.getItemIds()) {
				IOUtils.write(entry.getValue(), out);
				IOUtils.write("\n", out);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			LOG.error("Error reading container", e);
		}

	}
}

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
package org.mgnl.nicki.jcr.data;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrSearchResult implements ContextSearchResult {
	private static final Logger LOG = LoggerFactory.getLogger(JcrSearchResult.class);
	private Node node;

	public JcrSearchResult(Node node) {
		this.node = node;
	}

	public String getNameInNamespace() {
		try {
			return node.getPath();
		} catch (RepositoryException e) {
			LOG.error("Error", e);
		}
		return null;
	}

	@Override
	public Object getValue(Class<?> clazz, String name) {
		try {
			return node.getProperty(name);
		} catch (PathNotFoundException e) {
			LOG.error("Error", e);
		} catch (RepositoryException e) {
			LOG.error("Error", e);
		}
		return name;
	}

	@Override
	public List<Object> getValues(String name) {
		List<Object> list = new ArrayList<Object>();
		try {
			for (PropertyIterator it = this.node.getProperties(name); it.hasNext();) {
				list.add(it.next());
			}
		} catch (RepositoryException e) {
			LOG.error("Error", e);
		}
		return list;
	}

	@Override
	public boolean hasAttribute(String name) {
		try {
			return node.hasProperty(name);
		} catch (RepositoryException e) {
			return false;
		}
	}

}

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
package org.mgnl.nicki.jcr.query;

import javax.jcr.RepositoryException;
import javax.jcr.Workspace;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.jcr.context.JcrContext;
import org.mgnl.nicki.jcr.context.JcrQueryHandler;

public abstract class BasicJcrHandler implements JcrQueryHandler {
	private NickiContext context;
	private Class<? extends DynamicObject> classDefinition;
	private String statement;
	public void setStatement(String statement) {
		this.statement = statement;
	}

	private LANGUAGE language = LANGUAGE.JCR_JQOM;


	public BasicJcrHandler(NickiContext context) {
		super();
		this.context = context;
	}
	
	@Override
	public Workspace getWorkspace() {
		return ((JcrContext)context).getSession().getWorkspace();
	}

	@Override
	public LANGUAGE getLanguage() {
		return language;
	}
	
	

	@Override
	public String getStatement() {
		return statement;
	}

	@Override
	public Query createQuery(QueryManager queryManager) throws InvalidQueryException, RepositoryException {
		Query query = queryManager.createQuery(getStatement(), language.getValue());
		return query;
	}


	public NickiContext getContext() {
		return context;
	}

	public <T extends DynamicObject> void setClassDefinition(Class<T> classDefinition) {
		this.classDefinition = classDefinition;
	}

	public Class<? extends DynamicObject> getClassDefinition() {
		return classDefinition;
	}

	@Override
	public JcrConstraints getConstraints() {
		return new JcrConstraints();
	}
	
	
}

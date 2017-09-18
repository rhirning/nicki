
package org.mgnl.nicki.jcr.query;

/*-
 * #%L
 * nicki-jcr
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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

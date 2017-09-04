/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.jcr.context;

import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Workspace;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.jcr.query.JcrConstraints;

public interface JcrQueryHandler {
	public static enum SCOPE {OBJECT, ONELEVEL, SUBTREE};
	static enum LANGUAGE {
		 JCR_SQL2("JCR-SQL2"),
		 JCR_JQOM("JCR-JQOM");
		 
		private final String language;

		private LANGUAGE(String language) {
			this.language = language;
		}

		public String getValue() {
			return language;
		}
	}

	Workspace getWorkspace();
	
	LANGUAGE getLanguage();

	String getStatement();

	JcrConstraints getConstraints();
	
	Query createQuery(QueryManager queryManager) throws InvalidQueryException, RepositoryException;

	List<DynamicObject> handle(QueryResult queryResult)
			throws DynamicObjectException;
	
	SCOPE getScope();
}

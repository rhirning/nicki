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

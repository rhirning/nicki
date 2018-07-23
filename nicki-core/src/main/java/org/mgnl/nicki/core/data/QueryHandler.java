
package org.mgnl.nicki.core.data;

/*-
 * #%L
 * nicki-core
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


import java.util.List;

import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObjectException;

public interface QueryHandler {
	public static enum SCOPE {OBJECT, ONELEVEL, SUBTREE};

	String getBaseDN();

	String getFilter();

	Object getConstraints();

	SCOPE getScope();

	void handle(List<ContextSearchResult> results) throws DynamicObjectException;

	int getPageSize();
	
	boolean onePage();
}

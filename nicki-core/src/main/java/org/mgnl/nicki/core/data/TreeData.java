
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

import org.mgnl.nicki.core.objects.DynamicObjectException;

public interface TreeData {

	String getDisplayName();

	boolean childrenAllowed();

	String getName();

	void unLoadChildren();

	List<? extends TreeData> getAllChildren();

	String getSlashPath(TreeData root);
	
	String getSlashPath(String parentPath);

	String getPath();

	boolean isModified();

	void delete() throws DynamicObjectException, InvalidActionException;

	void renameObject(String name) throws DynamicObjectException;

	<T extends TreeData> T createChild(Class<T> classDefinition, String name) throws InvalidActionException;

	String getChildPath(TreeData parent, TreeData child);

	void moveTo(String newPath) throws DynamicObjectException;


}

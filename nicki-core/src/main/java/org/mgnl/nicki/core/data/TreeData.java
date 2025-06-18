
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


/**
 * The Interface TreeData.
 */
public interface TreeData extends TreeObject{

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	String getDisplayName();

	/**
	 * Children allowed.
	 *
	 * @return true, if successful
	 */
	boolean childrenAllowed();

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Un load children.
	 */
	void unLoadChildren();

	/**
	 * Gets the all children.
	 *
	 * @return the all children
	 */
	List<? extends TreeData> getAllChildren();

	/**
	 * Gets the slash path.
	 *
	 * @param root the root
	 * @return the slash path
	 */
	String getSlashPath(TreeData root);
	
	/**
	 * Gets the slash path.
	 *
	 * @param parentPath the parent path
	 * @return the slash path
	 */
	String getSlashPath(String parentPath);

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	String getPath();

	/**
	 * Checks if is modified.
	 *
	 * @return true, if is modified
	 */
	boolean isModified();

	/**
	 * Delete.
	 *
	 * @throws DynamicObjectException the dynamic object exception
	 * @throws InvalidActionException the invalid action exception
	 */
	void delete() throws DynamicObjectException, InvalidActionException;

	/**
	 * Rename object.
	 *
	 * @param name the name
	 * @throws DynamicObjectException the dynamic object exception
	 */
	void renameObject(String name) throws DynamicObjectException;

	/**
	 * Creates the child.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param name the name
	 * @return the t
	 * @throws InvalidActionException the invalid action exception
	 */
	<T extends TreeData> T createChild(Class<T> classDefinition, String name) throws InvalidActionException;

	/**
	 * Gets the child path.
	 *
	 * @param parent the parent
	 * @param child the child
	 * @return the child path
	 */
	String getChildPath(TreeData parent, TreeData child);

	/**
	 * Move to.
	 *
	 * @param newPath the new path
	 * @throws DynamicObjectException the dynamic object exception
	 */
	void moveTo(String newPath) throws DynamicObjectException;


}

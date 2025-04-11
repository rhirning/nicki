
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


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.core.helper.PathHelper;
import org.mgnl.nicki.core.objects.DynamicObjectException;

// TODO: Auto-generated Javadoc
/**
 * The Class FileEntry.
 */
public class FileEntry implements TreeData, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 684918946951446365L;
	
	/** The path. */
	private String path;
	
	/** The file. */
	private File file;

	/**
	 * Instantiates a new file entry.
	 */
	public FileEntry() {
	}

	/**
	 * Instantiates a new file entry.
	 *
	 * @param path the path
	 */
	public FileEntry(String path) {
		this.path = path;
		this.file = new File(path);
	}

	/**
	 * Instantiates a new file entry.
	 *
	 * @param file the file
	 */
	public FileEntry(File file) {
		this.file = file;
		this.path = file.getPath();
	}

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	@Override
	public String getDisplayName() {
		return file.getName();
	}

	/**
	 * Children allowed.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean childrenAllowed() {
		return file.isDirectory();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return file.getName();
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		return getName();
	}

	/**
	 * Un load children.
	 */
	@Override
	public void unLoadChildren() {
	}

	/**
	 * Gets the all children.
	 *
	 * @return the all children
	 */
	@Override
	public List<TreeData> getAllChildren() {
		List<TreeData> children = new ArrayList<>();
		return children;
	}

	/**
	 * Gets the slash path.
	 *
	 * @param root the root
	 * @return the slash path
	 */
	@Override
	public String getSlashPath(TreeData root) {
		return getSlashPath(root.getPath());
	}

	/**
	 * Gets the slash path.
	 *
	 * @param parentPath the parent path
	 * @return the slash path
	 */
	@Override
	public String getSlashPath(String parentPath) {
		return PathHelper.getSlashPath(parentPath, getPath());
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	@Override
	public String getPath() {
		return this.path;
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Checks if is modified.
	 *
	 * @return true, if is modified
	 */
	@Override
	public boolean isModified() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Delete.
	 *
	 * @throws InvalidActionException the invalid action exception
	 */
	@Override
	public void delete() throws InvalidActionException {
		if (file.listFiles() != null && file.listFiles().length > 0) {
			throw new InvalidActionException("Directory is not empty");
		} else {
			file.delete();
		}
	}

	/**
	 * Rename object.
	 *
	 * @param name the name
	 */
	@Override
	public void renameObject(String name) {
		File destFile = new File(file.getParent() + "/" + name);
		file.renameTo(destFile);
	}

	/**
	 * Creates the child.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param name the name
	 * @return the t
	 * @throws InvalidActionException the invalid action exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends TreeData> T createChild(Class<T> classDefinition, String name) throws InvalidActionException {
		File destFile = new File(path + "/" + name);
		destFile.mkdir();
		return (T) new FileEntry(destFile);
	}

	/**
	 * Gets the child path.
	 *
	 * @param parent the parent
	 * @param child the child
	 * @return the child path
	 */
	@Override
	public String getChildPath(TreeData parent, TreeData child) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Move to.
	 *
	 * @param newPath the new path
	 * @throws DynamicObjectException the dynamic object exception
	 */
	@Override
	public void moveTo(String newPath) throws DynamicObjectException {
		File destFile = new File(newPath);
		file.renameTo(destFile);
	}

}

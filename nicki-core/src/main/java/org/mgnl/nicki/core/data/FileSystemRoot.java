
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


import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;



/**
 * The Class FileSystemRoot.
 */
@SuppressWarnings("serial")
public class FileSystemRoot implements DataProvider<TreeData>, Serializable {
	
	/** The root. */
	private String root;
	
	/** The entry filter. */
	private EntryFilter entryFilter;

	/**
	 * Instantiates a new file system root.
	 *
	 * @param root the root
	 * @param entryFilter the entry filter
	 */
	public FileSystemRoot(String root, EntryFilter entryFilter) {
		super();
		this.root = root;
		this.entryFilter = entryFilter;
	}

	/**
	 * Gets the children.
	 *
	 * @param context the context
	 * @return the children
	 */
	public List<? extends TreeData> getChildren(NickiContext context) {
		
		List<? extends TreeData> list = getRoot(context).getAllChildren();
		Collections.sort(list, new Comparator<TreeData>() {

			@Override
			public int compare(TreeData o1, TreeData o2) {
				// TODO Auto-generated method stub
				return StringUtils.lowerCase(o1.getName()).compareTo(StringUtils.lowerCase(o2.getName()));
			}
		});
		return list;
	}

	/**
	 * Gets the root.
	 *
	 * @param context the context
	 * @return the root
	 */
	public TreeData getRoot(NickiContext context) {
		return new DirectoryEntry(this.root);
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return "";
	}

	/**
	 * Gets the entry filter.
	 *
	 * @return the entry filter
	 */
	public EntryFilter getEntryFilter() {
		return this.entryFilter;
	}

	/**
	 * Gets the children.
	 *
	 * @param parent the parent
	 * @return the children
	 */
	@Override
	public Collection<? extends TreeData> getChildren(TreeData parent) {
		return parent.getAllChildren();
	}

}

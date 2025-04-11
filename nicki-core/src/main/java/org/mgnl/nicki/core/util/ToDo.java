
package org.mgnl.nicki.core.util;

// TODO: Auto-generated Javadoc
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


/**
 * The Class ToDo.
 */
public class ToDo {
	
	/** The path. */
	public String path;
	
	/** The attribute name. */
	public String attributeName;
	
	/** The internal link. */
	public String internalLink;
	
	/**
	 * Instantiates a new to do.
	 *
	 * @param path the path
	 * @param attributeName the attribute name
	 * @param internalLink the internal link
	 */
	public ToDo(String path, String attributeName, String internalLink) {
		super();
		this.path = path;
		this.attributeName = attributeName;
		this.internalLink = internalLink;
	}
	
	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Gets the attribute name.
	 *
	 * @return the attribute name
	 */
	public String getAttributeName() {
		return attributeName;
	}
	
	/**
	 * Gets the internal link.
	 *
	 * @return the internal link
	 */
	public String getInternalLink() {
		return internalLink;
	}

}
